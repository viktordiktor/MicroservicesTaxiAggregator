package com.nikonenko.paymentservice.services.impl;

import com.nikonenko.paymentservice.dto.customers.CustomerCalculateRideRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerCalculateRideResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerChargeRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerChargeResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerCreationRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerCreationResponse;
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
import com.stripe.param.CustomerUpdateParams;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentCustomerServiceImpl implements PaymentCustomerService {
    private final CustomerUserRepository customerUserRepository;
    private final StripeUtil stripeUtil;
    private final ModelMapper modelMapper;
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
    public CustomerChargeResponse customerCharge(CustomerChargeRequest customerChargeRequest) {
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
    public void returnCustomerCharge(String chargeId) {
        PaymentIntent paymentIntent = stripeUtil.stripeIntentRetrieving(chargeId);
        updateBalance(paymentIntent.getCustomer(), (double) -paymentIntent.getAmount() / 100);
    }

    @Override
    public CustomerCalculateRideResponse calculateRidePrice(CustomerCalculateRideRequest customerCalculateRideRequest) {
        CustomerCalculateRideResponse customerCalculateRideResponse = modelMapper.map(customerCalculateRideRequest, CustomerCalculateRideResponse.class);
        customerCalculateRideResponse.setPrice(calculatePrice(customerCalculateRideRequest));
        return customerCalculateRideResponse;
    }

    private Double calculatePrice(CustomerCalculateRideRequest customerCalculateRideRequest) {
        LocalDateTime dateTime = customerCalculateRideRequest.getRideDateTime();
        Double length = customerCalculateRideRequest.getRideLength();
        double price = 1.0;
        price *= calculateWithRideLength(length) * coefficientUtil.getDayCoefficient(dateTime) *
                coefficientUtil.getTimeCoefficient(dateTime);
        if (customerCalculateRideRequest.getCoupon() != null) {
            Coupon coupon = stripeUtil.retrieveCoupon(customerCalculateRideRequest.getCoupon(), dateTime);
            price -= price * coupon.getPercentOff().doubleValue() / 100;
        }
        return price;
    }

    /**
     * The method returns ride-cost based on the ride length
     *
     * @param length Ride Length
     * @return Price without coefficients and discounts
     */
    private Double calculateWithRideLength(Double length) {
        return length / 2;
    }
}