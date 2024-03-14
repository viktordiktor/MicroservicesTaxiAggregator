package com.nikonenko.passengerservice.component;

import com.nikonenko.passengerservice.dto.PassengerRequest;
import com.nikonenko.passengerservice.dto.PassengerResponse;
import com.nikonenko.passengerservice.dto.RatingToPassengerRequest;
import com.nikonenko.passengerservice.exceptions.PassengerNotFoundException;
import com.nikonenko.passengerservice.exceptions.PhoneAlreadyExistsException;
import com.nikonenko.passengerservice.exceptions.UsernameAlreadyExistsException;
import com.nikonenko.passengerservice.kafka.producer.CustomerCreationRequestProducer;
import com.nikonenko.passengerservice.models.Passenger;
import com.nikonenko.passengerservice.repositories.PassengerRepository;
import com.nikonenko.passengerservice.services.impl.PassengerServiceImpl;
import com.nikonenko.passengerservice.utils.TestUtil;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@CucumberContextConfiguration
public class PassengerStepDefinitions {
    @Mock
    private PassengerRepository passengerRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private CustomerCreationRequestProducer customerCreationRequestProducer;
    @InjectMocks
    private PassengerServiceImpl passengerService;
    private PassengerResponse actualResponse;
    private RuntimeException exception;

    @Given("Passenger with ID {string} exists")
    public void passengerWithIdExists(String id) {
        Passenger passenger = TestUtil.getDefaultPassenger();
        PassengerResponse expectedResponse = TestUtil.getDefaultPassengerResponse();

        doReturn(Optional.of(passenger))
                .when(passengerRepository)
                .findById(UUID.fromString(id));
        doReturn(expectedResponse)
                .when(modelMapper)
                .map(passenger, PassengerResponse.class);

        PassengerResponse result = passengerService.getPassengerById(UUID.fromString(id));
        assertEquals(expectedResponse, result);
    }

    @When("getPassengerById method is called with ID {string}")
    public void findByIdMethodIsCalledWithId(String id) {
        try {
            actualResponse = passengerService.getPassengerById(UUID.fromString(id));
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @Then("PassengerResponse should contains passenger with ID {string}")
    public void passengerResponseShouldContainsPassengerWithId(String id) {
        Passenger passenger = passengerRepository.findById(UUID.fromString(id)).get();
        PassengerResponse expected = modelMapper.map(passenger, PassengerResponse.class);

        assertNull(exception);
        assertEquals(expected, actualResponse);
    }

    @Given("Passenger with ID {string} not exists")
    public void passengerWithIdNotExists(String id) {
        assertTrue(passengerRepository.findById(UUID.fromString(id)).isEmpty());
    }

    @Then("PassengerNotFoundException should be thrown for ID {string}")
    public void passengerNotFoundExceptionThrownForId(String id) {
        PassengerNotFoundException expected = new PassengerNotFoundException();

        assertEquals(exception.getMessage(), expected.getMessage());
    }

    @Given("Passenger with username {string} and phone {string} not exists")
    public void passengerWithUsernameAndPhoneNotExists(String username, String phone) {
        PassengerResponse response = TestUtil.getCreationPassengerResponse();
        PassengerRequest request = TestUtil.getPassengerRequestWithParameters(username, phone);
        Passenger notSavedPassenger = TestUtil.getNotSavedCreationPassenger();
        Passenger savedPassenger = TestUtil.getSavedCreationPassenger();

        doReturn(false)
                .when(passengerRepository)
                .existsByUsername(request.getUsername());
        doReturn(false)
                .when(passengerRepository)
                .existsByPhone(request.getPhone());
        doReturn(notSavedPassenger)
                .when(modelMapper)
                .map(request, Passenger.class);
        doReturn(savedPassenger)
                .when(passengerRepository)
                .save(notSavedPassenger);
        doReturn(response)
                .when(modelMapper)
                .map(savedPassenger, PassengerResponse.class);

        PassengerResponse result = passengerService.createPassenger(request);

        assertEquals(response, result);
    }

    @When("createPassenger method is called with PassengerRequest of username {string} and phone {string}")
    public void createPassengerMethodIsCalledWithPassengerRequestOfUsernameAndPhone(String username, String phone) {
        try {
            actualResponse = passengerService.createPassenger(TestUtil.getPassengerRequestWithParameters(username, phone));
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @Then("PassengerResponse should contains passenger with username {string} and phone {string}")
    public void passengerResponseShouldContainsPassengerWithUsernameAndPhone(String username, String phone) {
        PassengerResponse expected = TestUtil.getCreationPassengerResponse();

        assertNull(exception);
        assertEquals(expected, actualResponse);
    }

    @Given("Passenger with existing username {string} and not existing phone {string}")
    public void PassengerWithExistingUsernameAndNotExistingPhone(String existingUsername, String phone) {
        doReturn(true)
                .when(passengerRepository)
                .existsByUsername(existingUsername);

        assertTrue(passengerRepository.existsByUsername(existingUsername));
    }

    @Then("UsernameAlreadyExistsException should be thrown for username {string} and phone {string}")
    public void UsernameAlreadyExistsExceptionShouldThrownForUsernameAndPhone(String existingUsername, String phone) {
        UsernameAlreadyExistsException expected = new UsernameAlreadyExistsException();

        assertEquals(exception.getMessage(), expected.getMessage());
    }

    @Given("Passenger with not existing username {string} and existing phone {string}")
    public void PassengerWithNotExistingUsernameAndExistingPhone(String username, String existingPhone) {
        doReturn(true)
                .when(passengerRepository)
                .existsByPhone(existingPhone);

        assertTrue(passengerRepository.existsByPhone(existingPhone));
    }

    @Then("PhoneAlreadyExistsException should be thrown for username {string} and phone {string}")
    public void PhoneAlreadyExistsExceptionShouldThrownForUsernameAndPhone(String username, String existingPhone) {
        PhoneAlreadyExistsException expected = new PhoneAlreadyExistsException();

        assertEquals(exception.getMessage(), expected.getMessage());
    }

    @Given("Passenger with ID {string} exists and Passenger with username {string} and phone {string} not exists")
    public void passengerWithIdExistsAndPassengerWithUsernameAndPhoneNotExists(String id, String username, String phone) {
        PassengerResponse response = TestUtil.getUpdatePassengerResponse();
        PassengerRequest request = TestUtil.getPassengerRequestWithParameters(username, phone);
        Passenger passenger = TestUtil.getDefaultPassenger();
        Passenger editPassenger = TestUtil.getSecondPassenger();

        doReturn(false)
                .when(passengerRepository)
                .existsByUsername(request.getUsername());
        doReturn(false)
                .when(passengerRepository)
                .existsByPhone(request.getPhone());
        doReturn(Optional.of(passenger))
                .when(passengerRepository)
                .findById(UUID.fromString(id));
        doReturn(editPassenger)
                .when(modelMapper)
                .map(request, Passenger.class);
        doReturn(editPassenger)
                .when(passengerRepository)
                .save(editPassenger);
        doReturn(response)
                .when(modelMapper)
                .map(editPassenger, PassengerResponse.class);

        PassengerResponse result = passengerService.editPassenger(UUID.fromString(id), request);

        assertEquals(response, result);
    }

    @When("editPassenger method is called with ID {string} and PassengerRequest of username {string} and phone {string}")
    public void editPassengerMethodIsCalledWithIdAndPassengerRequestOfUsernameAndPhone(String id,
                                                                                       String username, String phone) {
        try {
            actualResponse =
                    passengerService.editPassenger(UUID.fromString(id),
                            TestUtil.getPassengerRequestWithParameters(username, phone));
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @Then("PassengerResponse should contains passenger with ID {string} and username {string} and phone {string}")
    public void PassengerResponseShouldContainsPassengerWithUsernameAndPhoneAndID(String id,
                                                                                  String username, String phone) {
        PassengerResponse expected = TestUtil.getUpdatePassengerResponse();

        assertNull(exception);
        assertEquals(expected, actualResponse);
    }

    @Given("Passenger with ID {string} exists and Passenger with existing username {string} and not existing phone {string}")
    public void passengerWithIdExistsAndPassengerWithExistingUsernameAndNotExistingPhone
            (String id, String existingUsername, String phone) {
        doReturn(true)
                .when(passengerRepository)
                .existsByUsername(existingUsername);

        assertTrue(passengerRepository.existsByUsername(existingUsername));
    }

    @Given("Passenger with ID {string} exists and Passenger with not existing username {string} and existing phone {string}")
    public void passengerWithIdExistsAndPassengerWithNotExistingUsernameAndExistingPhone
            (String id, String username, String existingPhone) {
        doReturn(true)
                .when(passengerRepository)
                .existsByPhone(existingPhone);

        assertTrue(passengerRepository.existsByPhone(existingPhone));
    }

    @Given("Passenger with ID {string} not exists and Passenger with username {string} and phone {string} not exists")
    public void passengerWithIdNotExistsAndPassengerWithUsernameAndPhoneNotExists
            (String id, String username, String existingPhone) {
        doReturn(Optional.empty())
                .when(passengerRepository)
                .findById(UUID.fromString(id));

        assertTrue(passengerRepository.findById(UUID.fromString(id)).isEmpty());
    }

    @When("deletePassenger method is called with ID {string}")
    public void deletePassengerMethodIsCalledWithId(String id) {
        try {
            passengerService.deletePassenger(UUID.fromString(id));
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @Then("Should return No Content for ID {string} and delete Passenger")
    public void shouldReturnNoContentForIdAndDeletePassenger(String id) {
        verify(passengerRepository).delete(any(Passenger.class));
        assertNull(exception);
    }

    @Given("Passenger with ID {string} exists and rating {int} and comment {string}")
    public void passengerWithIdExistsAndRatingAndComment(String id, int rating, String comment) {
        Passenger passenger = TestUtil.getDefaultPassenger();
        RatingToPassengerRequest request = TestUtil.getRatingToPassengerRequestWithParameters(id, rating, comment);

        doReturn(Optional.of(passenger))
                .when(passengerRepository)
                .findById(request.getPassengerId());
    }

    @When("createReview method is called with RatingToPassengerRequest of passenger ID {string} and rating {int} and comment {string}")
    public void createReviewMethodIsCalledWithRatingToPassengerRequestOfPassengerIdAndRatingAndComment
            (String id, int rating, String comment) {
        try {
            passengerService.createReview(TestUtil.getRatingToPassengerRequestWithParameters(id, rating, comment));
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @Then("Should return No Content for ID {string} and rating {int} and comment {string}")
    public void shouldReturnNoContentForIdAndRatingAndComment(String id, int rating, String comment) {
        verify(passengerRepository).save(any(Passenger.class));
        verify(passengerRepository).findById(UUID.fromString(id));
        assertNull(exception);
    }

    @Given("Passenger with ID {string} not exists and rating {int} and comment {string}")
    public void passengerWithIdNotExistsAndRatingAndComment(String id, int rating, String comment) {
        RatingToPassengerRequest request = TestUtil.getRatingToPassengerRequestWithParameters(id, rating, comment);

        doReturn(Optional.empty())
                .when(passengerRepository)
                .findById(request.getPassengerId());
    }

    @Given("Passenger with ID {string} exists and CustomerDataRequest of username {string} and phone {string} and amount {string}")
    public void passengerWithIdExistsAndCustomerDataRequestOfUsernameAndPhoneAndAmount
            (String id, String username, String phone, String amount) {
        Passenger passenger = TestUtil.getPassengerWithParameters(id, username, phone);

        doReturn(Optional.of(passenger))
                .when(passengerRepository)
                .findById(passenger.getId());
    }

    @When("createCustomerByPassenger method is called with Passenger ID {string} and CustomerDataRequest of username {string} and phone {string} and amount {string}")
    public void createCustomerByPassengerMethodIsCalledWithPassengerIdAndCustomerDataRequestOfUsernameAndPhoneAndAmount
            (String id, String username, String phone, String amount) {
        try {
            passengerService.createCustomerByPassenger(UUID.fromString(id),
                    TestUtil.getCustomerDataRequestWithParameters(username, phone, amount));
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @Then("Should return No Content and send Customer Creation Request for Passenger ID {string} and username {string} and phone {string} and amount {string}")
    public void shouldReturnNoContentAndSendCustomerCreationRequest(String id,
                                                                    String username, String phone, String amount) {
        verify(passengerRepository).findById(UUID.fromString(id));
        verify(customerCreationRequestProducer).sendCustomerCreationRequest(TestUtil
                .getCustomerCreationRequestWithParameters(id, username, phone, amount));
        assertNull(exception);
    }

    @Given("Passenger with ID {string} not exists and CustomerDataRequest of username {string} and phone {string} and amount {string}")
    public void passengerWithIdNotExistsAndCustomerDataRequestOfUsernameAndPhoneAndAmount
            (String id, String username, String phone, String amount) {
        doReturn(Optional.empty())
                .when(passengerRepository)
                .findById(UUID.fromString(id));
    }
}
