package com.nikonenko.paymentservice.services;

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
import com.nikonenko.paymentservice.services.impl.PaymentCustomerServiceImpl;
import com.nikonenko.paymentservice.utils.PaymentCoefficientUtil;
import com.nikonenko.paymentservice.utils.StripeUtil;
import com.nikonenko.paymentservice.utils.TestUtil;
import com.stripe.model.Coupon;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.CustomerUpdateParams;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class PaymentCustomerServiceTest {
    @Mock
    private CustomerUserRepository customerUserRepository;
    @Mock
    private StripeUtil stripeUtil;
    @Mock
    private PaymentCoefficientUtil paymentCoefficientUtil;
    @InjectMocks
    private PaymentCustomerServiceImpl paymentCustomerService;

    @Test
    void givenNonExistingCustomerRequest_whenCreateCustomer_thenReturnCustomer() {
        CustomerCreationRequest request = TestUtil.getCustomerCreationRequest();
        CustomerCreationResponse response = TestUtil.getCustomerCreationResponse();
        Customer customer = TestUtil.getCustomer();

        doReturn(false)
                .when(customerUserRepository)
                .existsByPassengerId(request.getPassengerId());
        doReturn(customer)
                .when(stripeUtil)
                .stripeCustomerCreation(request);

        CustomerCreationResponse result = paymentCustomerService.createCustomer(request);

        assertEquals(response, result);
        verify(customerUserRepository).existsByPassengerId(request.getPassengerId());
        verify(stripeUtil).stripeCustomerCreation(request);
        verify(stripeUtil).stripePaymentCreating(customer.getId());
        verify(customerUserRepository).save(any(CustomerUser.class));
    }

    @Test
    void givenExistingCustomerRequest_whenCreateCustomer_thenThrowException() {
        CustomerCreationRequest request = TestUtil.getCustomerCreationRequest();

        doReturn(true)
                .when(customerUserRepository)
                .existsByPassengerId(request.getPassengerId());

        assertThrows(
                CustomerAlreadyExistsException.class,
                () ->  paymentCustomerService.createCustomer(request)
        );

        verify(customerUserRepository).existsByPassengerId(request.getPassengerId());
        verifyNoMoreInteractions(customerUserRepository);
    }

    @Test
    void givenChargeRequest_whenCreateCharge_thenReturnCharge() {
        CustomerChargeRequest request = TestUtil.getCustomerChargeRequest();
        CustomerChargeResponse response = TestUtil.getCustomerChargeResponse();
        CustomerUser customerUser = TestUtil.getCustomerUser();
        Customer customer = TestUtil.getCustomer();
        PaymentIntent customerCharge = TestUtil.getPaymentIntent();

        doReturn(Optional.of(customerUser))
                .when(customerUserRepository)
                .findByPassengerId(request.getPassengerId());
        doReturn(customer)
                .when(stripeUtil)
                .stripeCustomerRetrieving(customerUser.getCustomerId());
        doReturn(customerCharge)
                .when(stripeUtil)
                .stripeIntentConfirming(request, customerUser.getCustomerId());

        CustomerChargeResponse result = paymentCustomerService.createCustomerCharge(request);

        assertEquals(response, result);
        verify(customerUserRepository).findByPassengerId(request.getPassengerId());
        verify(stripeUtil, times(2)).stripeCustomerRetrieving(customerUser.getCustomerId());
        verify(stripeUtil).stripeIntentConfirming(request, customerUser.getCustomerId());
        verify(stripeUtil).stripeCustomerUpdating(eq(customer), any(CustomerUpdateParams.class));
    }

    @Test
    void givenChargeRequestInsufficientFunds_whenCreateCharge_thenThrowException() {
        CustomerChargeRequest influentialRequest = TestUtil.getInfluentialChargeRequest();
        CustomerUser customerUser = TestUtil.getCustomerUser();
        Customer customer = TestUtil.getCustomer();

        doReturn(Optional.of(customerUser))
                .when(customerUserRepository)
                .findByPassengerId(influentialRequest.getPassengerId());
        doReturn(customer)
                .when(stripeUtil)
                .stripeCustomerRetrieving(customerUser.getCustomerId());

        assertThrows(
                InsufficientFundsException.class,
                () ->  paymentCustomerService.createCustomerCharge(influentialRequest)
        );

        verify(customerUserRepository).findByPassengerId(influentialRequest.getPassengerId());
        verify(stripeUtil).stripeCustomerRetrieving(customerUser.getCustomerId());
        verifyNoMoreInteractions(customerUserRepository);
        verifyNoMoreInteractions(stripeUtil);
    }

    @Test
    void givenChargeRequestNonExistingCustomer_whenCreateCharge_thenThrowException() {
        CustomerChargeRequest request = TestUtil.getCustomerChargeRequest();

        doReturn(Optional.empty())
                .when(customerUserRepository)
                .findByPassengerId(request.getPassengerId());

        assertThrows(
                CustomerNotFoundException.class,
                () ->  paymentCustomerService.createCustomerCharge(request)
        );

        verify(customerUserRepository).findByPassengerId(request.getPassengerId());
        verifyNoMoreInteractions(customerUserRepository);
        verifyNoMoreInteractions(stripeUtil);
    }

    @Test
    void givenExistCustomerUser_whenGetChargeById_thenReturnCharge() {
        CustomerChargeResponse response = TestUtil.getCustomerChargeResponse();
        PaymentIntent customerCharge = TestUtil.getPaymentIntent();
        CustomerUser customerUser = TestUtil.getCustomerUser();

        doReturn(customerCharge)
                .when(stripeUtil)
                .stripeIntentRetrieving(TestUtil.DEFAULT_CHARGE_ID);
        doReturn(Optional.of(customerUser))
                .when(customerUserRepository)
                .findByCustomerId(customerCharge.getCustomer());

        CustomerChargeResponse result = paymentCustomerService.getCustomerCharge(TestUtil.DEFAULT_CHARGE_ID);

        assertEquals(response, result);
        verify(stripeUtil).stripeIntentRetrieving(TestUtil.DEFAULT_CHARGE_ID);
        verify(customerUserRepository).findByCustomerId(customerCharge.getCustomer());
    }

    @Test
    void givenNonExistCustomerUser_whenGetChargeById_thenThrowException() {
        PaymentIntent customerCharge = TestUtil.getPaymentIntent();

        doReturn(customerCharge)
                .when(stripeUtil)
                .stripeIntentRetrieving(TestUtil.DEFAULT_CHARGE_ID);
        doReturn(Optional.empty())
                .when(customerUserRepository)
                .findByCustomerId(customerCharge.getCustomer());

        assertThrows(
                CustomerNotFoundException.class,
                () ->  paymentCustomerService.getCustomerCharge(TestUtil.DEFAULT_CHARGE_ID)
        );

        verify(stripeUtil).stripeIntentRetrieving(TestUtil.DEFAULT_CHARGE_ID);
        verify(customerUserRepository).findByCustomerId(customerCharge.getCustomer());
    }

    @Test
    void givenRideLengthDateTimeCoupon_whenCalculateRidePrice_thenReturnRidePrice() {
        CustomerCalculateRideResponse response = TestUtil.getCustomerCalculateRideWithCouponResponse();
        Coupon coupon = TestUtil.getCoupon();

        doCallRealMethod().when(paymentCoefficientUtil).getTimeCoefficient(TestUtil.DEFAULT_DATETIME);
        doCallRealMethod().when(paymentCoefficientUtil).getDayCoefficient(TestUtil.DEFAULT_DATETIME);
        doReturn(coupon)
                .when(stripeUtil)
                .retrieveCoupon(TestUtil.DEFAULT_COUPON_ID, TestUtil.DEFAULT_DATETIME);

        CustomerCalculateRideResponse result = paymentCustomerService.calculateRidePrice(TestUtil.DEFAULT_LENGTH,
                TestUtil.DEFAULT_DATETIME, TestUtil.DEFAULT_COUPON_ID);

        assertEquals(response, result);
        verify(paymentCoefficientUtil).getDayCoefficient(TestUtil.DEFAULT_DATETIME);
        verify(paymentCoefficientUtil).getTimeCoefficient(TestUtil.DEFAULT_DATETIME);
        verify(stripeUtil).retrieveCoupon(TestUtil.DEFAULT_COUPON_ID, TestUtil.DEFAULT_DATETIME);
    }

    @Test
    void givenRideLengthDateTimeNoCoupon_whenCalculateRidePrice_thenReturnRidePrice() {
        CustomerCalculateRideResponse response = TestUtil.getCustomerCalculateRideWithoutCouponResponse();

        doCallRealMethod().when(paymentCoefficientUtil).getTimeCoefficient(TestUtil.DEFAULT_DATETIME);
        doCallRealMethod().when(paymentCoefficientUtil).getDayCoefficient(TestUtil.DEFAULT_DATETIME);

        CustomerCalculateRideResponse result = paymentCustomerService.calculateRidePrice(TestUtil.DEFAULT_LENGTH,
                TestUtil.DEFAULT_DATETIME, null);

        assertEquals(response, result);
        verify(paymentCoefficientUtil).getDayCoefficient(TestUtil.DEFAULT_DATETIME);
        verify(paymentCoefficientUtil).getTimeCoefficient(TestUtil.DEFAULT_DATETIME);
        verifyNoInteractions(stripeUtil);
    }

    @Test
    void givenChargeId_whenReturnCustomerCharge_thenReturnRefund() {
        CustomerChargeReturnResponse response = TestUtil.getCustomerChargeReturnResponse();
        Refund refund = TestUtil.getRefund();

        doReturn(refund)
                .when(stripeUtil)
                .stripeRefund(TestUtil.DEFAULT_CHARGE_ID);

        CustomerChargeReturnResponse result = paymentCustomerService.returnCustomerCharge(TestUtil.DEFAULT_CHARGE_ID);

        assertEquals(response, result);
        verify(stripeUtil).stripeRefund(TestUtil.DEFAULT_CHARGE_ID);
    }

    @Test
    void givenExistPassengerId_whenFindCustomer_thenReturnCustomerExistsResponse() {
        CustomerExistsResponse response = TestUtil.getCustomerExistsResponse();

        doReturn(TestUtil.CUSTOMER_EXISTS)
                .when(customerUserRepository)
                .existsByPassengerId(TestUtil.DEFAULT_PASSENGER_ID);

        CustomerExistsResponse result = paymentCustomerService.isCustomerExists(TestUtil.DEFAULT_PASSENGER_ID);

        assertEquals(response, result);
        verify(customerUserRepository).existsByPassengerId(TestUtil.DEFAULT_PASSENGER_ID);
    }

    @Test
    void givenNonExistPassengerId_whenFindCustomer_thenReturnCustomerNonExistsResponse() {
        CustomerExistsResponse response = TestUtil.getCustomerNotExistsResponse();

        doReturn(TestUtil.CUSTOMER_NOT_EXISTS)
                .when(customerUserRepository)
                .existsByPassengerId(TestUtil.DEFAULT_PASSENGER_ID);

        CustomerExistsResponse result = paymentCustomerService.isCustomerExists(TestUtil.DEFAULT_PASSENGER_ID);

        assertEquals(response, result);
        verify(customerUserRepository).existsByPassengerId(TestUtil.DEFAULT_PASSENGER_ID);
    }
}
