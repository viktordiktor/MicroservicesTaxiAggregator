package com.nikonenko.driverservice.component;

import com.nikonenko.driverservice.dto.CarRequest;
import com.nikonenko.driverservice.dto.CarResponse;
import com.nikonenko.driverservice.dto.ChangeRideStatusRequest;
import com.nikonenko.driverservice.dto.DriverRequest;
import com.nikonenko.driverservice.dto.DriverResponse;
import com.nikonenko.driverservice.dto.RatingToDriverRequest;
import com.nikonenko.driverservice.exceptions.CarNotFoundException;
import com.nikonenko.driverservice.exceptions.CarNumberAlreadyExistsException;
import com.nikonenko.driverservice.exceptions.DriverIsNotAvailableException;
import com.nikonenko.driverservice.exceptions.DriverNoRidesException;
import com.nikonenko.driverservice.exceptions.DriverNotFoundException;
import com.nikonenko.driverservice.exceptions.PhoneAlreadyExistsException;
import com.nikonenko.driverservice.exceptions.UsernameAlreadyExistsException;
import com.nikonenko.driverservice.kafka.producer.RideStatusRequestProducer;
import com.nikonenko.driverservice.models.Car;
import com.nikonenko.driverservice.models.Driver;
import com.nikonenko.driverservice.repositories.CarRepository;
import com.nikonenko.driverservice.repositories.DriverRepository;
import com.nikonenko.driverservice.services.CarService;
import com.nikonenko.driverservice.services.impl.CarServiceImpl;
import com.nikonenko.driverservice.services.impl.DriverServiceImpl;
import com.nikonenko.driverservice.utils.SecurityList;
import com.nikonenko.driverservice.utils.TestUtil;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    private CarRepository carRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private RideStatusRequestProducer rideStatusRequestProducer;
    @Mock
    private CarService carServiceMock;
    @InjectMocks
    private DriverServiceImpl driverService;
    @InjectMocks
    private CarServiceImpl carService;
    private Driver driver;
    private DriverResponse actualDriverResponse;
    private CarResponse actualCarResponse;
    private RuntimeException exception;

    @Given("Driver with ID {string} exists")
    public void driverWithIdExists(String id) {
        Driver driver = TestUtil.getDefaultDriver();
        DriverResponse expectedResponse = TestUtil.getDefaultDriverResponse();

        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findById(UUID.fromString(id));
        doReturn(expectedResponse)
                .when(modelMapper)
                .map(driver, DriverResponse.class);

        DriverResponse result = driverService.getDriverById(UUID.fromString(id));
        assertEquals(expectedResponse, result);
    }

    @When("getDriverById method is called with ID {string}")
    public void getDriverByIdMethodIsCalledWithId(String id) {
        try {
            actualDriverResponse = driverService.getDriverById(UUID.fromString(id));
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @Then("DriverResponse should contains driver with ID {string}")
    public void driverResponseShouldContainsDriverWithId(String id) {
        Driver driver = driverRepository.findById(UUID.fromString(id)).get();
        DriverResponse expected = modelMapper.map(driver, DriverResponse.class);

        assertNull(exception);
        assertEquals(expected, actualDriverResponse);
    }

    @Given("Driver with ID {string} not exists")
    public void driverWithIdNotExists(String id) {
        assertTrue(driverRepository.findById(UUID.fromString(id)).isEmpty());
    }

    @Then("DriverNotFoundException should be thrown for ID {string}")
    public void driverNotFoundExceptionThrownForId(String id) {
        DriverNotFoundException expected = new DriverNotFoundException();

        assertEquals(exception.getMessage(), expected.getMessage());
    }

    @Given("Driver with username {string} and phone {string} not exists")
    public void driverWithUsernameAndPhoneNotExists(String username, String phone) {
        DriverResponse response = TestUtil.getCreationDriverResponse();
        DriverRequest request = TestUtil.getDriverRequestWithParameters(username, phone);
        OAuth2User oAuth2User = TestUtil.getOAuth2UserWithParameters(username, phone);
        Driver notSavedDriver = TestUtil.getNotSavedDriver();
        Driver savedDriver = TestUtil.getDefaultDriver();

        doReturn(false)
                .when(driverRepository)
                .existsByUsername(oAuth2User.getAttribute(SecurityList.USERNAME));
        doReturn(false)
                .when(driverRepository)
                .existsByPhone(oAuth2User.getAttribute(SecurityList.PHONE));
        doReturn(notSavedDriver)
                .when(modelMapper)
                .map(request, Driver.class);
        doReturn(savedDriver)
                .when(driverRepository)
                .save(notSavedDriver);
        doReturn(response)
                .when(modelMapper)
                .map(savedDriver, DriverResponse.class);

        DriverResponse result = driverService.createDriver(oAuth2User);

        assertEquals(response, result);
    }

    @When("createDriver method is called with DriverRequest of username {string} and phone {string}")
    public void createDriverMethodIsCalledWithDriverRequestOfUsernameAndPhone(String username, String phone) {
        try {
            actualDriverResponse = driverService.createDriver(TestUtil.getOAuth2UserWithParameters(username, phone));
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @Then("DriverResponse should contains driver with username {string} and phone {string}")
    public void driverResponseShouldContainsDriverWithUsernameAndPhone(String username, String phone) {
        DriverResponse expected = TestUtil.getCreationDriverResponse();

        assertNull(exception);
        assertEquals(expected, actualDriverResponse);
    }

    @Given("Driver with existing username {string} and not existing phone {string}")
    public void driverWithExistingUsernameAndNotExistingPhone(String existingUsername, String phone) {
        doReturn(true)
                .when(driverRepository)
                .existsByUsername(existingUsername);

        assertTrue(driverRepository.existsByUsername(existingUsername));
    }

    @Then("UsernameAlreadyExistsException should be thrown for username {string} and phone {string}")
    public void usernameAlreadyExistsExceptionShouldThrownForUsernameAndPhone(String existingUsername, String phone) {
        UsernameAlreadyExistsException expected = new UsernameAlreadyExistsException();

        assertEquals(exception.getMessage(), expected.getMessage());
    }

    @Given("Driver with not existing username {string} and existing phone {string}")
    public void driverWithNotExistingUsernameAndExistingPhone(String username, String existingPhone) {
        doReturn(true)
                .when(driverRepository)
                .existsByPhone(existingPhone);

        assertTrue(driverRepository.existsByPhone(existingPhone));
    }

    @Then("PhoneAlreadyExistsException should be thrown for username {string} and phone {string}")
    public void phoneAlreadyExistsExceptionShouldThrownForUsernameAndPhone(String existingUsername, String phone) {
        PhoneAlreadyExistsException expected = new PhoneAlreadyExistsException();

        assertEquals(exception.getMessage(), expected.getMessage());
    }

    @Given("Driver with ID {string} exists and Driver with username {string} and phone {string} not exists")
    public void driverWithIdExistsAndDriverWithUsernameAndPhoneNotExists(String id, String username, String phone) {
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
                .findById(UUID.fromString(id));
        doReturn(editDriver)
                .when(modelMapper)
                .map(request, Driver.class);
        doReturn(editDriver)
                .when(driverRepository)
                .save(editDriver);
        doReturn(response)
                .when(modelMapper)
                .map(editDriver, DriverResponse.class);

        DriverResponse result = driverService.editDriver(UUID.fromString(id), request);

        assertEquals(response, result);
    }

    @When("editDriver method is called with ID {string} and DriverRequest of username {string} and phone {string}")
    public void editDriverMethodIsCalledWithIdAndDriverRequestOfUsernameAndPhone(String id,
                                                                                 String username, String phone) {
        try {
            actualDriverResponse = driverService
                    .editDriver(UUID.fromString(id), TestUtil.getDriverRequestWithParameters(username, phone));
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @Then("DriverResponse should contains driver with ID {string} and username {string} and phone {string}")
    public void DriverResponseShouldContainsDriverWithUsernameAndPhoneAndID(String id,
                                                                            String username, String phone) {
        DriverResponse expected = TestUtil.getUpdateDriverResponse();

        assertNull(exception);
        assertEquals(expected, actualDriverResponse);
    }

    @Given("Driver with ID {string} exists and Driver with existing username {string} and not existing phone {string}")
    public void driverWithIdExistsAndDriverWithExistingUsernameAndNotExistingPhone
            (String id, String existingUsername, String phone) {
        doReturn(true)
                .when(driverRepository)
                .existsByUsername(existingUsername);

        assertTrue(driverRepository.existsByUsername(existingUsername));
    }

    @Given("Driver with ID {string} exists and Driver with not existing username {string} and existing phone {string}")
    public void driverWithIdExistsAndDriverWithNotExistingUsernameAndExistingPhone
            (String id, String username, String existingPhone) {
        doReturn(true)
                .when(driverRepository)
                .existsByPhone(existingPhone);

        assertTrue(driverRepository.existsByPhone(existingPhone));
    }

    @Given("Driver with ID {string} not exists and Driver with username {string} and phone {string} not exists")
    public void driverWithIdNotExistsAndDriverWithUsernameAndPhoneNotExists
            (String id, String username, String existingPhone) {
        doReturn(Optional.empty())
                .when(driverRepository)
                .findById(UUID.fromString(id));

        assertTrue(driverRepository.findById(UUID.fromString(id)).isEmpty());
    }

    @When("deleteDriver method is called with ID {string}")
    public void deleteDriverMethodIsCalledWithId(String id) {
        try {
            driverService.deleteDriver(UUID.fromString(id));
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @Then("Should return No Content for ID {string} and delete Driver")
    public void shouldReturnNoContentForIdAndDeleteDriver(String id) {
        verify(driverRepository).delete(any(Driver.class));
        assertNull(exception);
    }

    @Given("Driver with ID {string} exists and available and Ride ID {string}")
    public void driverWithIdExistsAndAvailableAndRideId(String driverId, String rideId) {
        driver = TestUtil.getAvailableDriver();
        Car car = driver.getCar();
        CarResponse carResponse = TestUtil.getDefaultCarResponse();

        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findById(UUID.fromString(driverId));
        doReturn(carResponse)
                .when(modelMapper)
                .map(car, CarResponse.class);

        assertTrue(driver.isAvailable());
    }

    @When("acceptRide method is called with Driver ID {string} and Ride ID {string}")
    public void acceptRideMethodIsCalledWithDriverIdAndRideId(String driverId, String rideId) {
        try {
            driverService.acceptRide(rideId, UUID.fromString(driverId));
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @Then("Should return No Content And send request to Ride Service for Driver ID {string} and Ride ID {string}")
    public void shouldReturnNoContentAndSendRequestToRideServiceForDriverIdAndRideId(String driverId, String rideId) {
        assertNull(exception);
        verify(rideStatusRequestProducer).sendChangeRideStatusRequest(any(ChangeRideStatusRequest.class));
    }

    @And("Driver should become not available")
    public void driverShouldBecomeNotAvailable() {
        assertFalse(driver.isAvailable());
    }

    @Given("Driver with ID {string} exists and not available and Ride ID {string}")
    public void driverWithIdExistsAndNotAvailableAndRideId(String driverId, String rideId) {
        driver = TestUtil.getNotAvailableDriver();
        Car car = driver.getCar();
        CarResponse carResponse = TestUtil.getDefaultCarResponse();

        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findById(UUID.fromString(driverId));
        doReturn(carResponse)
                .when(modelMapper)
                .map(car, CarResponse.class);

        assertFalse(driver.isAvailable());
    }

    @Then("DriverIsNotAvailableException should be thrown for ID {string}")
    public void driverIsNotAvailableExceptionShouldBeThrownForId(String id) {
        DriverIsNotAvailableException expected = new DriverIsNotAvailableException();

        assertEquals(exception.getMessage(), expected.getMessage());
    }

    @When("rejectRide method is called with Driver ID {string} and Ride ID {string}")
    public void rejectRideMethodIsCalledWithDriverIdAndRideId(String driverId, String rideId) {
        try {
            driverService.rejectRide(rideId, UUID.fromString(driverId));
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @And("Driver should become available")
    public void driverShouldBecomeAvailable() {
        assertTrue(driver.isAvailable());
    }

    @Then("DriverNoRidesException should be thrown for ID {string}")
    public void driverNoRidesExceptionShouldBeThrownForId(String id) {
        DriverNoRidesException expected = new DriverNoRidesException();

        assertEquals(exception.getMessage(), expected.getMessage());
    }

    @When("startRide method is called with Driver ID {string} and Ride ID {string}")
    public void startRideMethodIsCalledWithDriverIdAndRideId(String driverId, String rideId) {
        try {
            driverService.startRide(rideId, UUID.fromString(driverId));
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @When("finishRide method is called with Driver ID {string} and Ride ID {string}")
    public void finishRideMethodIsCalledWithDriverIdAndRideId(String driverId, String rideId) {
        try {
            driverService.finishRide(rideId, UUID.fromString(driverId));
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @Given("Driver with ID {string} exists and rating {int} and comment {string}")
    public void driverWithIdExistsAndRatingAndComment(String id, int rating, String comment) {
        Driver driver = TestUtil.getDefaultDriver();
        RatingToDriverRequest request = TestUtil
                .getRatingToDriverRequestWithParameters(UUID.fromString(id), rating, comment);

        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findById(request.getDriverId());
    }

    @When("createReview method is called with RatingToDriverRequest of driver ID {string} and rating {int} and comment {string}")
    public void createReviewMethodIsCalledWithRatingToDriverRequestOfDriverIdAndRatingAndComment
            (String id, int rating, String comment) {
        try {
            driverService.createReview(TestUtil
                    .getRatingToDriverRequestWithParameters(UUID.fromString(id), rating, comment));
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @Then("Should return No Content for ID {string} and rating {int} and comment {string}")
    public void shouldReturnNoContentForIdAndRatingAndComment(String id, int rating, String comment) {
        verify(driverRepository).save(any(Driver.class));
        verify(driverRepository).findById(UUID.fromString(id));
        assertNull(exception);
    }

    @Given("Driver with ID {string} exists and CarRequest of number {string} and model {string} and color {string}")
    public void driverWithIdExistsAndCarRequestOfNumberAndModelAndColor(String driverId,
                                                                        String number, String model, String color) {
        Driver driver = TestUtil.getDefaultDriver();
        Car car = TestUtil.getSavedCarWithParameters(number, model, color);
        CarRequest carRequest = TestUtil.getCarRequestWithParameters(number, model, color);
        CarResponse carResponse = TestUtil.getCarResponseWithParameters(number, model, color);
        DriverResponse driverResponse = TestUtil.getDriverResponseWithCarParameters(number, model, color);

        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findById(UUID.fromString(driverId));
        doReturn(carResponse)
                .when(carServiceMock)
                .createCar(carRequest);
        doReturn(car)
                .when(modelMapper)
                .map(carResponse, Car.class);
        doReturn(driver)
                .when(driverRepository)
                .save(driver);
        doReturn(driverResponse)
                .when(modelMapper)
                .map(driver, DriverResponse.class);
    }

    @When("addCarToDriver method is called with Driver ID {string} and CarRequest of number {string} and model {string} and color {string}")
    public void addCarToDriverMethodIsCalledWithDriverIdAndCarRequestOfNumberAndModelAndColor
            (String driverId, String number, String model, String color) {
        try {
            actualDriverResponse = driverService.addCarToDriver(UUID.fromString(driverId),
                    TestUtil.getCarRequestWithParameters(number, model, color));
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @Then("DriverResponse should contains driver with ID {string} and CarResponse of number {string} and model {string} and color {string}")
    public void driverResponseShouldContainsCarResponseOfNumberAndModelAndColor(String driverId, String number,
                                                                                String model, String color) {
        DriverResponse expected = TestUtil.getDriverResponseWithCarParameters(number, model, color);

        assertNull(exception);
        assertEquals(expected, actualDriverResponse);
    }

    @Given("Car with ID {long} exists")
    public void carWithIdExists(Long id) {
        Car car = TestUtil.getDefaultCar();
        CarResponse expectedResponse = TestUtil.getDefaultCarResponse();

        doReturn(Optional.of(car))
                .when(carRepository)
                .findById(id);
        doReturn(expectedResponse)
                .when(modelMapper)
                .map(car, CarResponse.class);

        CarResponse result = carService.getCarById(id);
        assertEquals(expectedResponse, result);
    }

    @When("getCarById method is called with ID {long}")
    public void getCarByIdMethodIsCalledWithId(Long id) {
        try {
            actualCarResponse = carService.getCarById(id);
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @Then("CarResponse should contains car with ID {long}")
    public void carResponseShouldContainsCarWithId(Long id) {
        Car car = carRepository.findById(id).get();
        CarResponse expected = modelMapper.map(car, CarResponse.class);

        assertNull(exception);
        assertEquals(expected, actualCarResponse);
    }

    @Given("Car with ID {long} not exists")
    public void carWithIdNotExists(Long id) {
        assertTrue(carRepository.findById(id).isEmpty());
    }

    @Then("CarNotFoundException should be thrown for ID {long}")
    public void carNotFoundExceptionThrownForId(Long id) {
        CarNotFoundException expected = new CarNotFoundException();

        assertEquals(exception.getMessage(), expected.getMessage());
    }

    @Given("Car with ID {long} exists and Car with number {string} not exists")
    public void carWithIdExistsAndCarWithUsernameAndPhoneNotExists(Long id, String number) {
        CarResponse response = TestUtil.getUpdateCarResponse();
        CarRequest request = TestUtil.getCarRequestWithNumber(number);
        Car car = TestUtil.getDefaultCar();
        Car editCar = TestUtil.getSecondCar();

        doReturn(false)
                .when(carRepository)
                .existsByNumber(request.getNumber());
        doReturn(Optional.of(car))
                .when(carRepository)
                .findById(id);
        doReturn(editCar)
                .when(modelMapper)
                .map(request, Car.class);
        doReturn(editCar)
                .when(carRepository)
                .save(editCar);
        doReturn(response)
                .when(modelMapper)
                .map(editCar, CarResponse.class);

        CarResponse result = carService.editCar(id, request);

        assertEquals(response, result);
    }

    @When("editCar method is called with ID {long} and CarRequest of number {string}")
    public void editCarMethodIsCalledWithIdAndCarRequestOfUsernameAndPhone(Long id, String number) {
        try {
            actualCarResponse =
                    carService.editCar(id, TestUtil.getCarRequestWithNumber(number));
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @Then("CarResponse should contains car with ID {long} and number {string}")
    public void CarResponseShouldContainsCarWithUsernameAndPhoneAndID(Long id, String number) {
        CarResponse expected = TestUtil.getUpdateCarResponse();

        assertNull(exception);
        assertEquals(expected, actualCarResponse);
    }

    @Given("Car with ID {long} exists and Car with existing number {string}")
    public void carWithIdExistsAndCarWithExistingNumber(Long id, String existingNumber) {
        doReturn(true)
                .when(carRepository)
                .existsByNumber(existingNumber);

        assertTrue(carRepository.existsByNumber(existingNumber));
    }

    @Then("CarNumberAlreadyExistsException should be thrown for number {string}")
    public void carNumberAlreadyExistsExceptionShouldThrownForUsernameAndPhone(String existingNumber) {
        CarNumberAlreadyExistsException expected = new CarNumberAlreadyExistsException();

        assertEquals(exception.getMessage(), expected.getMessage());
    }

    @Given("Car with ID {long} not exists and Ride with number {string} not exists")
    public void carWithIdNotExistsAndRideWithNumberNotExists(Long id, String number) {
        doReturn(Optional.empty())
                .when(carRepository)
                .findById(id);

        assertTrue(carRepository.findById(id).isEmpty());
    }
}