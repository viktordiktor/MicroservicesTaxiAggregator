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
import com.stripe.net.RequestOptions;
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

    @Override
    public StripeCustomerResponse createCustomer(StripeCustomerRequest customerRequest){
        checkCustomerExists(customerRequest.getPassengerId());
        Customer customer = createStripeCustomer(customerRequest);

        createPayment(customer.getId());
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

    private Customer createStripeCustomer(StripeCustomerRequest customerRequest) {
        try{
            CustomerCreateParams customerCreateParams = CustomerCreateParams.builder()
                    .setPhone(customerRequest.getPhone())
                    .setName(customerRequest.getUsername())
                    .setBalance(customerRequest.getAmount())
                    .build();
            return Customer.create(customerCreateParams, getRequestOptions(secretKey));
        } catch (StripeException ex) {
            throw new CreateCustomerException(ex.getMessage());
        }
    }

    private void createPayment(String customerId)  {
        try{
            Map<String, Object> paymentParams = Map.of(
                    "type", "card",
                    "card", Map.of("token", "tok_visa")
            );
            PaymentMethod paymentMethod = PaymentMethod.create(paymentParams, getRequestOptions(secretKey));
            paymentMethod.attach(Map.of("customer", customerId));
        } catch (StripeException ex){
            throw new CreatePaymentFailedException(ex.getMessage());
        }
    }

    private void saveCustomerUserToDatabase(Long passengerId, String customerId){
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
        PaymentIntent intent = confirmIntent(customerChargeRequest, customerId);
        updateBalance(customerId, customerChargeRequest.getAmount());
        return StripeCustomerChargeResponse.builder().id(intent.getId())
                .passengerId(customerChargeRequest.getPassengerId())
                .amount(intent.getAmount() / 100)
                .currency(intent.getCurrency()).build();
    }

    private CustomerUser getCustomerUser(Long id){
        return customerUserRepository.findById(id)
                .orElseThrow(CustomerNotFoundException::new);
    }

    private void checkBalanceEnough(String customerId, long amount) {
        Customer customer = retrieveCustomer(customerId);
        if (customer.getBalance() < amount) {
            throw new InsufficientFundsException();
        }
    }

    private Customer retrieveCustomer(String customerId) {
        try {
            return Customer.retrieve(customerId, getRequestOptions(secretKey));
        } catch (StripeException ex) {
            throw new RetrieveCustomerFailedException(ex.getMessage());
        }
    }

    private PaymentIntent confirmIntent(StripeCustomerChargeRequest request, String customerId) {
        PaymentIntent intent = createIntent(request, customerId);
        PaymentIntentConfirmParams params = PaymentIntentConfirmParams.builder()
                        .setPaymentMethod("pm_card_visa")
                        .build();
        try {
            RequestOptions requestOptions = RequestOptions.builder()
                    .setApiKey(secretKey)
                    .build();
            return intent.confirm(params, requestOptions);
        } catch (StripeException ex) {
            throw new ConfirmIntentFailedException(ex.getMessage());
        }
    }

    private PaymentIntent createIntent(StripeCustomerChargeRequest request, String customerId) {
        try {
            PaymentIntent intent = PaymentIntent.create(Map.of("amount", request.getAmount() * 100,
                    "currency", request.getCurrency(),
                    "customer", customerId,
                    "automatic_payment_methods", createAutomaticPaymentMethods()));
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

    private void updateBalance(String customerId, long amount) {
        Customer customer = retrieveCustomer(customerId);
        CustomerUpdateParams params =
                CustomerUpdateParams.builder()
                        .setBalance(customer.getBalance() - amount * 100)
                        .build();
        updateCustomer(customer, params);
    }

    private void updateCustomer(Customer customer, CustomerUpdateParams params) {
        try {
            customer.update(params, getRequestOptions(secretKey));
        } catch (StripeException ex) {
            throw new UpdateCustomerFailedException(ex.getMessage());
        }
    }

    private RequestOptions getRequestOptions(String key){
        return RequestOptions.builder()
                .setApiKey(key)
                .build();
    }
}
