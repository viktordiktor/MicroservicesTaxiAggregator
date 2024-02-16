package com.nikonenko.paymentservice.services.impl;

import com.nikonenko.paymentservice.dto.customers.CustomerCalculateRideResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerChargeRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerChargeResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerChargeReturnResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerCreationRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerCreationResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerExistsResponse;
import com.nikonenko.paymentservice.exceptions.CustomerAlreadyExistsException;
import com.nikonenko.paymentservice.exceptions.CustomerNotFoundException;
import com.nikonenko.paymentservice.exceptions.InsufficientFundsException;
import com.nikonenko.paymentservice.models.CustomerUser;
import com.nikonenko.paymentservice.repositories.CustomerUserRepository;
import com.nikonenko.paymentservice.services.PaymentCustomerService;
import com.nikonenko.paymentservice.utils.PaymentCoefficientUtil;
import com.nikonenko.paymentservice.utils.StripeUtil;
import com.stripe.model.Coupon;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.CustomerUpdateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentCustomerServiceImpl implements PaymentCustomerService {
    private final CustomerUserRepository customerUserRepository;
    private final StripeUtil stripeUtil;
    private final PaymentCoefficientUtil coefficientUtil;

    @Override
    public CustomerCreationResponse createCustomer(CustomerCreationRequest customerRequest) {
        checkCustomerExists(customerRequest.getPassengerId());
        Customer customer = stripeUtil.stripeCustomerCreation(customerRequest);

        stripeUtil.stripePaymentCreating(customer.getId());
        saveCustomerUserToDatabase(customerRequest.getPassengerId(), customer.getId());

        return CustomerCreationResponse.builder()
                .id(customer.getId())
                .phone(customer.getPhone())
                .username(customer.getName())
                .build();
    }

    private void checkCustomerExists(Long passengerId) {
        if (customerUserRepository.existsByPassengerId(passengerId)) {
            throw new CustomerAlreadyExistsException();
        }
    }

    private void saveCustomerUserToDatabase(Long passengerId, String customerId) {
        CustomerUser user = CustomerUser.builder()
                .customerId(customerId)
                .passengerId(passengerId)
                .build();
        customerUserRepository.save(user);
    }

    @Override
    public CustomerChargeResponse createCustomerCharge(CustomerChargeRequest customerChargeRequest) {
        Long passengerId = customerChargeRequest.getPassengerId();
        CustomerUser user = getCustomerUser(passengerId);
        String customerId = user.getCustomerId();
        checkBalanceEnough(customerId, customerChargeRequest.getAmount());
        PaymentIntent intent = stripeUtil.stripeIntentConfirming(customerChargeRequest, customerId);
        updateBalance(customerId, customerChargeRequest.getAmount().doubleValue());
        return CustomerChargeResponse.builder().id(intent.getId())
                .passengerId(customerChargeRequest.getPassengerId())
                .amount(customerChargeRequest.getAmount())
                .currency(customerChargeRequest.getCurrency())
                .success(true)
                .build();
    }

    private CustomerUser getCustomerUser(Long id) {
        return customerUserRepository.findByPassengerId(id)
                .orElseThrow(CustomerNotFoundException::new);
    }

    private void checkBalanceEnough(String customerId, BigDecimal amount) {
        Customer customer = stripeUtil.stripeCustomerRetrieving(customerId);
        if (customer.getBalance() < amount.doubleValue() * 100) {
            throw new InsufficientFundsException();
        }
    }

    private void updateBalance(String customerId, Double amount) {
        Customer customer = stripeUtil.stripeCustomerRetrieving(customerId);
        CustomerUpdateParams params =
                CustomerUpdateParams.builder()
                        .setBalance((long) (customer.getBalance() - amount * 100))
                        .build();
        stripeUtil.stripeCustomerUpdating(customer, params);
    }

    @Override
    public CustomerChargeResponse getCustomerCharge(String chargeId) {
        PaymentIntent paymentIntent = stripeUtil.stripeIntentRetrieving(chargeId);
        CustomerUser customerUser = customerUserRepository.findByCustomerId(paymentIntent.getCustomer())
                .orElseThrow(CustomerNotFoundException::new);

        MathContext mc = new MathContext(6, RoundingMode.HALF_UP);
        return CustomerChargeResponse.builder()
                .id(paymentIntent.getId())
                .success(paymentIntent.getStatus().equals("succeeded"))
                .amount(BigDecimal.valueOf(paymentIntent.getAmount()).divide(BigDecimal.valueOf(100), mc))
                .passengerId(customerUser.getPassengerId())
                .build();
    }

    @Override
    public CustomerChargeReturnResponse returnCustomerCharge(String chargeId) {
        Refund refund = stripeUtil.stripeRefund(chargeId);
        MathContext mc = new MathContext(6, RoundingMode.HALF_UP);
        return CustomerChargeReturnResponse.builder()
                .id(refund.getId())
                .amount(BigDecimal.valueOf(refund.getAmount()).divide(BigDecimal.valueOf(100), mc))
                .currency(refund.getCurrency())
                .paymentId(refund.getPaymentIntent())
                .build();
    }

    @Override
    public CustomerExistsResponse isCustomerExists(Long passengerId) {
        return new CustomerExistsResponse(customerUserRepository.existsByPassengerId(passengerId));
    }

    @Override
    public CustomerCalculateRideResponse calculateRidePrice(Double rideLength,
                                                            LocalDateTime rideDateTime, String coupon) {
        CustomerCalculateRideResponse customerCalculateRideResponse = CustomerCalculateRideResponse.builder()
                .rideLength(rideLength)
                .rideDateTime(rideDateTime)
                .coupon(coupon)
                .price(calculatePrice(rideLength, rideDateTime, coupon))
                .build();
        customerCalculateRideResponse.setPrice(calculatePrice(rideLength, rideDateTime, coupon));
        return customerCalculateRideResponse;
    }

    private BigDecimal calculatePrice(Double rideLength,
                                      LocalDateTime rideDateTime, String coupon) {
        BigDecimal length = BigDecimal.valueOf(rideLength);
        BigDecimal price = BigDecimal.valueOf(1.0);

        price = price.multiply(calculateWithRideLength(length))
                .multiply(coefficientUtil.getDayCoefficient(rideDateTime))
                .multiply(coefficientUtil.getTimeCoefficient(rideDateTime));

        if (coupon != null) {
            Coupon stripeCoupon = stripeUtil.retrieveCoupon(coupon, rideDateTime);
            BigDecimal percentOff = stripeCoupon.getPercentOff();
            MathContext mc = new MathContext(6, RoundingMode.HALF_UP);
            price = price.subtract(price.multiply(percentOff).divide(BigDecimal.valueOf(100), mc));
        }
        return price;
    }

    /**
     * The method returns ride-cost based on the ride length
     *
     * @param length Ride Length
     * @return Price without coefficients and discounts
     */
    private BigDecimal calculateWithRideLength(BigDecimal length) {
        MathContext mc = new MathContext(6, RoundingMode.HALF_UP);
        return length.divide(BigDecimal.valueOf(2), mc);
    }
}