package com.nikonenko.paymentservice.services;

import com.nikonenko.paymentservice.dto.customers.CustomerRideRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerRideResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerChargeRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerChargeResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerCreationRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerCreationResponse;
import com.nikonenko.paymentservice.exceptions.CustomerAlreadyExistsException;
import com.nikonenko.paymentservice.exceptions.CustomerNotFoundException;
import com.nikonenko.paymentservice.exceptions.InsufficientFundsException;
import com.nikonenko.paymentservice.models.CustomerUser;
import com.nikonenko.paymentservice.repositories.CustomerUserRepository;
import com.stripe.model.Coupon;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.param.CustomerUpdateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentCustomerServiceImpl implements PaymentCustomerService {
    private final CustomerUserRepository customerUserRepository;
    private final StripeUtilityService utilityService;
    private final ModelMapper modelMapper;

    @Override
    public CustomerCreationResponse createCustomer(CustomerCreationRequest customerRequest) {
        checkCustomerExists(customerRequest.getPassengerId());
        Customer customer = utilityService.stripeCustomerCreation(customerRequest);

        utilityService.stripePaymentCreating(customer.getId());
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
        PaymentIntent intent = utilityService.stripeIntentConfirming(customerChargeRequest, customerId);
        updateBalance(customerId, customerChargeRequest.getAmount());
        return CustomerChargeResponse.builder().id(intent.getId())
                .passengerId(customerChargeRequest.getPassengerId())
                .amount(customerChargeRequest.getAmount())
                .currency(customerChargeRequest.getCurrency())
                .success(true)
                .build();
    }

    private CustomerUser getCustomerUser(Long id) {
        return customerUserRepository.findById(id)
                .orElseThrow(CustomerNotFoundException::new);
    }

    private void checkBalanceEnough(String customerId, Double amount) {
        Customer customer = utilityService.stripeCustomerRetrieving(customerId);
        if (customer.getBalance() < amount) {
            throw new InsufficientFundsException();
        }
    }

    private void updateBalance(String customerId, Double amount) {
        Customer customer = utilityService.stripeCustomerRetrieving(customerId);
        CustomerUpdateParams params =
                CustomerUpdateParams.builder()
                        .setBalance((long) (customer.getBalance() - amount))
                        .build();
        utilityService.stripeCustomerUpdating(customer, params);
    }

    @Override
    public CustomerRideResponse calculateRidePrice(CustomerRideRequest customerRideRequest) {
        CustomerRideResponse customerRideResponse = modelMapper.map(customerRideRequest, CustomerRideResponse.class);
        customerRideResponse.setPrice(calculatePrice(customerRideRequest));
        return customerRideResponse;
    }

    private Double calculatePrice(CustomerRideRequest customerRideRequest) {
        LocalDateTime dateTime = customerRideRequest.getRideDateTime();
        Double length = customerRideRequest.getRideLength();
        double price = 1.0;
        price *= calculateWithRideLength(length) * getDayCoefficient(dateTime) * getTimeCoefficient(dateTime);
        if (customerRideRequest.getCoupon() != null) {
            Coupon coupon = utilityService.retrieveCoupon(customerRideRequest.getCoupon(), dateTime);
            price -= price * coupon.getPercentOff().doubleValue() / 100;
        }
        return price;
    }

    /**
     * The method returns ride-cost based on the ride length
     *
     * @param length Ride Length
     * @return Price without coefficients
     */
    private Double calculateWithRideLength(Double length) {
        return length / 2;
    }

    /**
     * The method returns a coefficient, depending on the time of ordering
     * MONDAY-THURSDAY -- 1
     * FRIDAY -- 1.6
     * SATURDAY -- 1.4
     * SUNDAY -- 1.2
     *
     * @param localDateTime DateTime of ordering
     * @return Day Coefficient
     */
    private static double getDayCoefficient(LocalDateTime localDateTime) {
        DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();
        switch (dayOfWeek) {
            case MONDAY, TUESDAY, WEDNESDAY, THURSDAY -> {
                return 1;
            }
            case FRIDAY -> {
                return 1.6;
            }
            case SATURDAY -> {
                return 1.4;
            }
            case SUNDAY -> {
                return 1.2;
            }
        }
        return 1;
    }

    /**
     * The method returns a coefficient, depending on the time of ordering
     * 07:00-10:00 -- 1.4
     * 10:00-16:00 -- 1.0
     * 16:00-19:00 -- 1.5
     * 19:00-22:00 -- 1.2
     * 22:00-07:00 -- 1.3
     *
     * @param localDateTime DateTime of ordering
     * @return Time Coefficient
     */
    private static Double getTimeCoefficient(LocalDateTime localDateTime) {
        LocalTime time = localDateTime.toLocalTime();
        if (time.isAfter(LocalTime.of(7, 0)) && time.isBefore(LocalTime.of(10, 0))) {
            return 1.4;
        } else if (time.isAfter(LocalTime.of(10, 0)) && time.isBefore(LocalTime.of(16, 0))) {
            return 1.0;
        } else if (time.isAfter(LocalTime.of(16, 0)) && time.isBefore(LocalTime.of(19, 0))) {
            return 1.5;
        } else if (time.isAfter(LocalTime.of(19, 0)) && time.isBefore(LocalTime.of(22, 0))) {
            return 1.2;
        } else {
            return 1.3;
        }
    }
}