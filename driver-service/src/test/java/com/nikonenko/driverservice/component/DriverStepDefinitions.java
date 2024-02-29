package com.nikonenko.driverservice.component;

import com.nikonenko.driverservice.dto.DriverRequest;
import com.nikonenko.driverservice.dto.DriverResponse;
import com.nikonenko.driverservice.exceptions.DriverNotFoundException;
import com.nikonenko.driverservice.exceptions.PhoneAlreadyExistsException;
import com.nikonenko.driverservice.exceptions.UsernameAlreadyExistsException;
import com.nikonenko.driverservice.models.Driver;
import com.nikonenko.driverservice.repositories.DriverRepository;
import com.nikonenko.driverservice.services.impl.DriverServiceImpl;
import com.nikonenko.driverservice.utils.TestUtil;
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
import static org.mockito.Mockito.verify;

@CucumberContextConfiguration
public class DriverStepDefinitions {
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private DriverServiceImpl driverService;
    private DriverResponse actualResponse;
    private RuntimeException exception;

    @Given("Driver with ID {long} exists")
    public void driverWithIdExists(Long id) {
        Driver driver = TestUtil.getDefaultDriver();
        DriverResponse expectedResponse = TestUtil.getDefaultDriverResponse();

        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findById(id);
        doReturn(expectedResponse)
                .when(modelMapper)
                .map(driver, DriverResponse.class);

        DriverResponse result = driverService.getDriverById(id);
        assertEquals(expectedResponse, result);
    }

    @When("getDriverById method is called with ID {long}")
    public void findByIdMethodIsCalledWithId(Long id) {
        try {
            actualResponse = driverService.getDriverById(id);
        } catch (DriverNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("DriverResponse should contains driver with ID {long}")
    public void driverResponseShouldContainsDriverWithId(Long id) {
        Driver driver = driverRepository.findById(id).get();
        DriverResponse expected = modelMapper.map(driver, DriverResponse.class);
        assertEquals(expected, actualResponse);
    }

    @Given("Driver with ID {long} not exists")
    public void driverWithIdNotExists(long id) {
        assertTrue(driverRepository.findById(id).isEmpty());
    }

    @Then("DriverNotFoundException should thrown for ID {long}")
    public void driverNotFoundExceptionThrownForId(long id) {
        DriverNotFoundException expected = new DriverNotFoundException();

        assertEquals(exception.getMessage(), expected.getMessage());
    }

    @Given("Driver with username {string} and phone {string} not exists")
    public void driverWithUsernameAndPhoneNotExists(String username, String phone) {
        DriverResponse response = TestUtil.getCreationDriverResponse();
        DriverRequest request = TestUtil.getDriverRequestWithParameters(username, phone);
        Driver notSavedDriver = TestUtil.getNotSavedDriver();
        Driver savedDriver = TestUtil.getDefaultDriver();

        doReturn(false)
                .when(driverRepository)
                .existsByUsername(request.getUsername());
        doReturn(false)
                .when(driverRepository)
                .existsByPhone(request.getPhone());
        doReturn(notSavedDriver)
                .when(modelMapper)
                .map(request, Driver.class);
        doReturn(savedDriver)
                .when(driverRepository)
                .save(notSavedDriver);
        doReturn(response)
                .when(modelMapper)
                .map(savedDriver, DriverResponse.class);

        DriverResponse result = driverService.createDriver(request);

        assertEquals(response, result);
    }

    @When("createDriver method is called with DriverRequest of username {string} and phone {string}")
    public void createDriverMethodIsCalledWithDriverRequestOfUsernameAndPhone(String username, String phone) {
        try {
            actualResponse = driverService.createDriver(TestUtil.getDriverRequestWithParameters(username, phone));
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @Then("DriverResponse should contains driver with username {string} and phone {string}")
    public void driverResponseShouldContainsDriverWithUsernameAndPhone(String username, String phone) {
        DriverResponse expected = TestUtil.getCreationDriverResponse();

        assertEquals(expected, actualResponse);
    }

    @Given("Driver with existing username {string} and not existing phone {string}")
    public void DriverWithExistingUsernameAndNotExistingPhone(String existingUsername, String phone) {
        doReturn(true)
                .when(driverRepository)
                .existsByUsername(existingUsername);

        assertTrue(driverRepository.existsByUsername(existingUsername));
    }

    @Then("UsernameAlreadyExistsException should thrown for username {string} and phone {string}")
    public void UsernameAlreadyExistsExceptionShouldThrownForUsernameAndPhone(String existingUsername, String phone) {
        UsernameAlreadyExistsException expected = new UsernameAlreadyExistsException();

        assertEquals(exception.getMessage(), expected.getMessage());
    }

    @Given("Driver with not existing username {string} and existing phone {string}")
    public void DriverWithNotExistingUsernameAndExistingPhone(String username, String existingPhone) {
        doReturn(true)
                .when(driverRepository)
                .existsByPhone(existingPhone);

        assertTrue(driverRepository.existsByPhone(existingPhone));
    }

    @Then("PhoneAlreadyExistsException should thrown for username {string} and phone {string}")
    public void PhoneAlreadyExistsExceptionShouldThrownForUsernameAndPhone(String existingUsername, String phone) {
        PhoneAlreadyExistsException expected = new PhoneAlreadyExistsException();

        assertEquals(exception.getMessage(), expected.getMessage());
    }

    @Given("Driver with ID {long} exists and Driver with username {string} and phone {string} not exists")
    public void driverWithIdExistsAndDriverWithUsernameAndPhoneNotExists(Long id, String username, String phone) {
        DriverResponse response = TestUtil.getUpdateDriverResponse();
        DriverRequest request = TestUtil.getDriverRequestWithParameters(username, phone);
        Driver driver = TestUtil.getDefaultDriver();
        Driver editDriver = TestUtil.getSecondDriver();

        doReturn(false)
                .when(driverRepository)
                .existsByUsername(request.getUsername());
        doReturn(false)
                .when(driverRepository)
                .existsByPhone(request.getPhone());
        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findById(id);
        doReturn(editDriver)
                .when(modelMapper)
                .map(request, Driver.class);
        doReturn(editDriver)
                .when(driverRepository)
                .save(editDriver);
        doReturn(response)
                .when(modelMapper)
                .map(editDriver, DriverResponse.class);

        DriverResponse result = driverService.editDriver(id, request);

        assertEquals(response, result);
    }

    @When("editDriver method is called with ID {long} and DriverRequest of username {string} and phone {string}")
    public void editDriverMethodIsCalledWithIdAndDriverRequestOfUsernameAndPhone(Long id,
                                                                                       String username, String phone) {
        try {
            actualResponse =
                    driverService.editDriver(id, TestUtil.getDriverRequestWithParameters(username, phone));
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @Then("DriverResponse should contains driver with ID {long} and username {string} and phone {string}")
    public void DriverResponseShouldContainsDriverWithUsernameAndPhoneAndID(Long id,
                                                                                  String username, String phone) {
        DriverResponse expected = TestUtil.getUpdateDriverResponse();

        assertEquals(expected, actualResponse);
    }

    @Given("Driver with ID {long} exists and Driver with existing username {string} and not existing phone {string}")
    public void driverWithIdExistsAndDriverWithExistingUsernameAndNotExistingPhone
            (Long id, String existingUsername, String phone) {
        doReturn(true)
                .when(driverRepository)
                .existsByUsername(existingUsername);

        assertTrue(driverRepository.existsByUsername(existingUsername));
    }

    @Given("Driver with ID {long} exists and Driver with not existing username {string} and existing phone {string}")
    public void driverWithIdExistsAndDriverWithNotExistingUsernameAndExistingPhone
            (Long id, String username, String existingPhone) {
        doReturn(true)
                .when(driverRepository)
                .existsByPhone(existingPhone);

        assertTrue(driverRepository.existsByPhone(existingPhone));
    }

    @Given("Driver with ID {long} not exists and Driver with username {string} and phone {string} not exists")
    public void driverWithIdNotExistsAndDriverWithUsernameAndPhoneNotExists
            (Long id, String username, String existingPhone) {
        doReturn(Optional.empty())
                .when(driverRepository)
                .findById(id);

        assertTrue(driverRepository.findById(id).isEmpty());
    }

    @When("deleteDriver method is called with ID {long}")
    public void deleteDriverMethodIsCalledWithId(Long id) {
        try {
            driverService.deleteDriver(id);
        } catch (DriverNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("Should return No Content for ID {long} and delete Driver")
    public void shouldReturnNoContentForIdAndDeleteDriver(Long id) {
        verify(driverRepository).delete(any(Driver.class));
        assertNull(exception);
    }
}
