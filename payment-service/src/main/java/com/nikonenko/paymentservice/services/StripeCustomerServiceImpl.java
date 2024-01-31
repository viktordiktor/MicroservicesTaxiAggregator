package com.nikonenko.paymentservice.services;

import com.nikonenko.paymentservice.dto.customers.StripeCustomerChargeRequest;
import com.nikonenko.paymentservice.dto.customers.StripeCustomerChargeResponse;
import com.nikonenko.paymentservice.dto.customers.StripeCustomerRequest;
import com.nikonenko.paymentservice.dto.customers.StripeCustomerResponse;
import com.nikonenko.paymentservice.exceptions.ConfirmIntentFailedException;
import com.nikonenko.paymentservice.exceptions.CreateCustomerException;
import com.nikonenko.paymentservice.exceptions.CreateIntentFailedException;
import com.nikonenko.paymentservice.exceptions.CreatePaymentFailedException;
import com.nikonenko.paymentservice.exceptions.CustomerAlreadyExistsException;
import com.nikonenko.paymentservice.exceptions.CustomerNotFoundException;
import com.nikonenko.paymentservice.exceptions.InsufficientFundsException;
import com.nikonenko.paymentservice.exceptions.RetrieveCustomerFailedException;
import com.nikonenko.paymentservice.exceptions.UpdateCustomerFailedException;
import com.nikonenko.paymentservice.models.CustomerUser;
import com.nikonenko.paymentservice.repositories.CustomerUserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.PaymentIntentConfirmParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class StripeCustomerServiceImpl implements StripeCustomerService{
    @Value("${api.stripe.secret-key}")
    private String secretKey;
    private final CustomerUserRepository customerUserRepository;
    private final StripeUtilityService utilityService;

    @Override
    public StripeCustomerResponse createCustomer(StripeCustomerRequest customerRequest){
        checkCustomerExists(customerRequest.getPassengerId());
        Customer customer = stripeCustomerCreation(customerRequest);

        stripePaymentCreating(customer.getId());
        saveCustomerUserToDatabase(customerRequest.getPassengerId(), customer.getId());

        return StripeCustomerResponse.builder()
                .id(customer.getId())
                .phone(customer.getPhone())
                .username(customer.getName())
                .build();
    }

    private void checkCustomerExists(Long passengerId){
        if (customerUserRepository.existsByPassengerId(passengerId)) {
            throw new CustomerAlreadyExistsException();
        }
    }

    private Customer stripeCustomerCreation(StripeCustomerRequest customerRequest) {
        try{
            CustomerCreateParams customerCreateParams = CustomerCreateParams.builder()
                    .setPhone(customerRequest.getPhone())
                    .setName(customerRequest.getUsername())
                    .setBalance(customerRequest.getAmount())
                    .build();
            return Customer.create(customerCreateParams, utilityService.getRequestOptions(secretKey));
        } catch (StripeException ex) {
            throw new CreateCustomerException(ex.getMessage());
        }
    }

    private void stripePaymentCreating(String customerId)  {
        try{
            Map<String, Object> paymentParams = Map.of(
                    "type", "card",
                    "card", Map.of("token", "tok_visa")
            );
            PaymentMethod paymentMethod = PaymentMethod.create(paymentParams,
                    utilityService.getRequestOptions(secretKey));
            paymentMethod.attach(Map.of("customer", customerId), utilityService.getRequestOptions(secretKey));
        } catch (StripeException ex){
            throw new CreatePaymentFailedException(ex.getMessage());
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
    public StripeCustomerChargeResponse customerCharge(StripeCustomerChargeRequest customerChargeRequest) {
        Long passengerId = customerChargeRequest.getPassengerId();
        CustomerUser user = getCustomerUser(passengerId);
        String customerId = user.getCustomerId();
        checkBalanceEnough(customerId, customerChargeRequest.getAmount());
        PaymentIntent intent = stripeIntentConfirming(customerChargeRequest, customerId);
        updateBalance(customerId, customerChargeRequest.getAmount());
        return StripeCustomerChargeResponse.builder().id(intent.getId())
                .passengerId(customerChargeRequest.getPassengerId())
                .amount(customerChargeRequest.getAmount())
                .currency(customerChargeRequest.getCurrency())
                .success(true)
                .build();
    }

    private CustomerUser getCustomerUser(Long id){
        return customerUserRepository.findById(id)
                .orElseThrow(CustomerNotFoundException::new);
    }

    private void checkBalanceEnough(String customerId, Double amount) {
        Customer customer = stripeCustomerRetrieving(customerId);
        if (customer.getBalance() < amount) {
            throw new InsufficientFundsException();
        }
    }

    private Customer stripeCustomerRetrieving(String customerId) {
        try {
            return Customer.retrieve(customerId, utilityService.getRequestOptions(secretKey));
        } catch (StripeException ex) {
            throw new RetrieveCustomerFailedException(ex.getMessage());
        }
    }

    private PaymentIntent stripeIntentConfirming(StripeCustomerChargeRequest request, String customerId) {
        PaymentIntent intent = stripeIntentCreation(request, customerId);
        PaymentIntentConfirmParams params = PaymentIntentConfirmParams.builder()
                        .setPaymentMethod("pm_card_visa")
                        .build();
        try {
            return intent.confirm(params, utilityService.getRequestOptions(secretKey));
        } catch (StripeException ex) {
            throw new ConfirmIntentFailedException(ex.getMessage());
        }
    }

    private PaymentIntent stripeIntentCreation(StripeCustomerChargeRequest request, String customerId) {
        try {
            PaymentIntent intent = PaymentIntent.create(Map.of("amount", (int)(request.getAmount() * 100),
                    "currency", request.getCurrency(),
                    "customer", customerId,
                    "automatic_payment_methods", createAutomaticPaymentMethods()),
                    utilityService.getRequestOptions(secretKey));
            intent.setPaymentMethod(customerId);
            return intent;
        } catch (StripeException ex) {
            throw new CreateIntentFailedException(ex.getMessage());
        }
    }

    private Map<String, Object> createAutomaticPaymentMethods() {
        Map<String, Object> automaticPaymentMethods = new HashMap<>();
        automaticPaymentMethods.put("enabled", true);
        automaticPaymentMethods.put("allow_redirects", "never");
        return automaticPaymentMethods;
    }

    private void updateBalance(String customerId, Double amount) {
        Customer customer = stripeCustomerRetrieving(customerId);
        CustomerUpdateParams params =
                CustomerUpdateParams.builder()
                        .setBalance((long)(customer.getBalance() - amount))
                        .build();
        stripeCustomerUpdating(customer, params);
    }

    private void stripeCustomerUpdating(Customer customer, CustomerUpdateParams params) {
        try {
            customer.update(params, utilityService.getRequestOptions(secretKey));
        } catch (StripeException ex) {
            throw new UpdateCustomerFailedException(ex.getMessage());
        }
    }
}
