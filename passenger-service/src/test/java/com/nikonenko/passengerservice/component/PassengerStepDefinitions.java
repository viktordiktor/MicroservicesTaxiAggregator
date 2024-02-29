package com.nikonenko.passengerservice.component;

import com.nikonenko.passengerservice.dto.PassengerRequest;
import com.nikonenko.passengerservice.dto.PassengerResponse;
import com.nikonenko.passengerservice.exceptions.PassengerNotFoundException;
import com.nikonenko.passengerservice.exceptions.PhoneAlreadyExistsException;
import com.nikonenko.passengerservice.exceptions.UsernameAlreadyExistsException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@CucumberContextConfiguration
public class PassengerStepDefinitions {
    @Mock
    private PassengerRepository passengerRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private PassengerServiceImpl passengerService;
    private PassengerResponse actualResponse;
    private RuntimeException exception;

    @Given("Passenger with ID {long} exists")
    public void passengerWithIdExists(Long id) {
        Passenger passenger = TestUtil.getDefaultPassenger();
        PassengerResponse expectedResponse = TestUtil.getDefaultPassengerResponse();

        doReturn(Optional.of(passenger))
                .when(passengerRepository)
                .findById(id);
        doReturn(expectedResponse)
                .when(modelMapper)
                .map(passenger, PassengerResponse.class);

        PassengerResponse result = passengerService.getPassengerById(id);
        assertEquals(expectedResponse, result);
    }

    @When("getPassengerById method is called with ID {long}")
    public void findByIdMethodIsCalledWithId(Long id) {
        try {
            actualResponse = passengerService.getPassengerById(id);
        } catch (PassengerNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("PassengerResponse should contains passenger with ID {long}")
    public void passengerResponseShouldContainsPassengerWithId(Long id) {
        Passenger passenger = passengerRepository.findById(id).get();
        PassengerResponse expected = modelMapper.map(passenger, PassengerResponse.class);
        assertEquals(expected, actualResponse);
    }

    @Given("Passenger with ID {long} not exists")
    public void passengerWithIdNotExists(long id) {
        assertTrue(passengerRepository.findById(id).isEmpty());
    }

    @Then("PassengerNotFoundException should thrown for ID {long}")
    public void passengerNotFoundExceptionThrownForId(long id) {
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

        assertEquals(expected, actualResponse);
    }

    @Given("Passenger with existing username {string} and not existing phone {string}")
    public void PassengerWithExistingUsernameAndNotExistingPhone(String existingUsername, String phone) {
        doReturn(true)
                .when(passengerRepository)
                .existsByUsername(existingUsername);

        assertTrue(passengerRepository.existsByUsername(existingUsername));
    }

    @Then("UsernameAlreadyExistsException should thrown for username {string} and phone {string}")
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

    @Then("PhoneAlreadyExistsException should thrown for username {string} and phone {string}")
    public void PhoneAlreadyExistsExceptionShouldThrownForUsernameAndPhone(String existingUsername, String phone) {
        PhoneAlreadyExistsException expected = new PhoneAlreadyExistsException();

        assertEquals(exception.getMessage(), expected.getMessage());
    }

    @Given("Passenger with ID {long} exists and Passenger with username {string} and phone {string} not exists")
    public void passengerWithIdExistsAndPassengerWithUsernameAndPhoneNotExists(Long id, String username, String phone) {
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
                .findById(id);
        doReturn(editPassenger)
                .when(modelMapper)
                .map(request, Passenger.class);
        doReturn(editPassenger)
                .when(passengerRepository)
                .save(editPassenger);
        doReturn(response)
                .when(modelMapper)
                .map(editPassenger, PassengerResponse.class);

        PassengerResponse result = passengerService.editPassenger(id, request);

        assertEquals(response, result);
    }

    @When("editPassenger method is called with ID {long} and PassengerRequest of username {string} and phone {string}")
    public void editPassengerMethodIsCalledWithIdAndPassengerRequestOfUsernameAndPhone(Long id,
                                                                                       String username, String phone) {
        try {
            actualResponse =
                    passengerService.editPassenger(id, TestUtil.getPassengerRequestWithParameters(username, phone));
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @Then("PassengerResponse should contains passenger with ID {long} and username {string} and phone {string}")
    public void PassengerResponseShouldContainsPassengerWithUsernameAndPhoneAndID(Long id,
                                                                                  String username, String phone) {
        PassengerResponse expected = TestUtil.getUpdatePassengerResponse();

        assertEquals(expected, actualResponse);
    }

    @Given("Passenger with ID {long} exists and Passenger with existing username {string} and not existing phone {string}")
    public void passengerWithIdExistsAndPassengerWithExistingUsernameAndNotExistingPhone
                                                                    (Long id, String existingUsername, String phone) {
        doReturn(true)
                .when(passengerRepository)
                .existsByUsername(existingUsername);

        assertTrue(passengerRepository.existsByUsername(existingUsername));
    }

    @Given("Passenger with ID {long} exists and Passenger with not existing username {string} and existing phone {string}")
    public void passengerWithIdExistsAndPassengerWithNotExistingUsernameAndExistingPhone
                                                                (Long id, String username, String existingPhone) {
        doReturn(true)
                .when(passengerRepository)
                .existsByPhone(existingPhone);

        assertTrue(passengerRepository.existsByPhone(existingPhone));
    }

    @Given("Passenger with ID {long} not exists and Passenger with username {string} and phone {string} not exists")
    public void passengerWithIdNotExistsAndPassengerWithUsernameAndPhoneNotExists
                                                                    (Long id, String username, String existingPhone) {
        doReturn(Optional.empty())
                .when(passengerRepository)
                .findById(id);

        assertTrue(passengerRepository.findById(id).isEmpty());
    }

    @When("deletePassenger method is called with ID {long}")
    public void deletePassengerMethodIsCalledWithId(Long id) {
        try {
            passengerService.deletePassenger(id);
        } catch (PassengerNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("Should return No Content for ID {long}")
    public void shouldReturnNoContentForId(Long id) {
        verify(passengerRepository, times(1)).delete(any(Passenger.class));
        assertNull(exception);
    }
}
