package com.nikonenko.driverservice.unit;

import com.nikonenko.driverservice.dto.CarRequest;
import com.nikonenko.driverservice.dto.CarResponse;
import com.nikonenko.driverservice.dto.DriverRequest;
import com.nikonenko.driverservice.dto.DriverResponse;
import com.nikonenko.driverservice.dto.PageResponse;
import com.nikonenko.driverservice.dto.RatingToDriverRequest;
import com.nikonenko.driverservice.dto.feign.rides.RideResponse;
import com.nikonenko.driverservice.exceptions.DriverIsNotAvailableException;
import com.nikonenko.driverservice.exceptions.DriverNoRidesException;
import com.nikonenko.driverservice.exceptions.DriverNotAddedCarException;
import com.nikonenko.driverservice.exceptions.DriverNotFoundException;
import com.nikonenko.driverservice.exceptions.PhoneAlreadyExistsException;
import com.nikonenko.driverservice.exceptions.UsernameAlreadyExistsException;
import com.nikonenko.driverservice.exceptions.WrongPageableParameterException;
import com.nikonenko.driverservice.exceptions.WrongSortFieldException;
import com.nikonenko.driverservice.kafka.producer.RideStatusRequestProducer;
import com.nikonenko.driverservice.models.Car;
import com.nikonenko.driverservice.models.Driver;
import com.nikonenko.driverservice.repositories.DriverRepository;
import com.nikonenko.driverservice.services.CarService;
import com.nikonenko.driverservice.services.feign.RideService;
import com.nikonenko.driverservice.services.impl.DriverServiceImpl;
import com.nikonenko.driverservice.utils.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private CarService carService;
    @Mock
    private RideService rideService;
    @Mock
    private RideStatusRequestProducer rideStatusRequestProducer;
    @InjectMocks
    private DriverServiceImpl driverService;

    @Test
    void givenValidParams_whenGetDrivers_thenReturnPageResponseDriverResponse() {
        List<Driver> expectedList = TestUtil.getDriverList();
        Page<Driver> driversPage = new PageImpl<>(expectedList);
        List<DriverResponse> expectedResponses = TestUtil.getDriverResponseList(modelMapper, expectedList);

        doReturn(driversPage)
                .when(driverRepository)
                .findAll(PageRequest.of(TestUtil.DEFAULT_PAGE,
                        TestUtil.DEFAULT_PAGE_SIZE, Sort.by(TestUtil.DEFAULT_PAGE_SORT)));

        PageResponse<DriverResponse> result =
                driverService.getAllDrivers(TestUtil.DEFAULT_PAGE,
                        TestUtil.DEFAULT_PAGE_SIZE, TestUtil.DEFAULT_PAGE_SORT);

        verify(driverRepository).findAll(any(Pageable.class));
        assertNotNull(result);
        assertEquals(result.getTotalElements(), expectedList.size());
        assertEquals(result.getTotalPages(), driversPage.getTotalPages());
        assertThat(result.getObjectList()).containsExactlyElementsOf(expectedResponses);
    }

    @Test
    void givenInvalidPageParams_whenGetDrivers_thenThrowException() {
        assertThrows(
                WrongPageableParameterException.class,
                () -> driverService.getAllDrivers(TestUtil.INVALID_PAGE,
                        TestUtil.INVALID_PAGE_SIZE, TestUtil.DEFAULT_PAGE_SORT)
        );
        verifyNoInteractions(driverRepository);
    }

    @Test
    void givenInvalidSortByParam_whenGetDrivers_thenThrowException() {
        assertThrows(
                WrongSortFieldException.class,
                () -> driverService.getAllDrivers(TestUtil.DEFAULT_PAGE, TestUtil.DEFAULT_PAGE_SIZE,
                        TestUtil.INVALID_PAGE_SORT)
        );
        verifyNoInteractions(driverRepository);
    }

    @Test
    void givenExistingDriverId_whenFindById_thenReturnDriverResponse() {
        Driver retrievedDriver = TestUtil.getDefaultDriver();
        DriverResponse response = TestUtil.getDefaultDriverResponse();

        doReturn(Optional.of(retrievedDriver))
                .when(driverRepository)
                .findById(TestUtil.DEFAULT_ID);
        when(modelMapper.map(retrievedDriver, DriverResponse.class))
                .thenReturn(response);

        DriverResponse result = driverService.getDriverById(TestUtil.DEFAULT_ID);

        verify(driverRepository).findById(TestUtil.DEFAULT_ID);
        verify(modelMapper).map(retrievedDriver, DriverResponse.class);
        assertNotNull(result);
        assertEquals(response, result);
    }

    @Test
    void givenNonExistingDriverId_whenFindById_thenThrowException() {
        doReturn(Optional.empty())
                .when(driverRepository)
                .findById(TestUtil.DEFAULT_ID);

        assertThrows(
                DriverNotFoundException.class,
                () -> driverService.getDriverById(TestUtil.DEFAULT_ID)
        );

        verify(driverRepository).findById(TestUtil.DEFAULT_ID);
    }

    @Test
    void givenNonExistingDriver_whenCreateDriver_thenCreateNewDriver() {
        DriverResponse response = TestUtil.getDefaultDriverResponse();
        DriverRequest request = TestUtil.getDriverRequest();
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

        verify(driverRepository).existsByUsername(request.getUsername());
        verify(driverRepository).existsByPhone(request.getPhone());
        verify(modelMapper).map(request, Driver.class);
        verify(driverRepository).save(notSavedDriver);
        verify(modelMapper).map(savedDriver, DriverResponse.class);
        assertEquals(response, result);
    }

    @Test
    void givenDriverWithExistingPhone_whenCreateDriver_thenThrowException() {
        DriverRequest request = TestUtil.getDriverRequest();

        doReturn(true)
                .when(driverRepository)
                .existsByPhone(request.getPhone());
        assertThrows(
                PhoneAlreadyExistsException.class,
                () -> driverService.createDriver(request)
        );

        verify(driverRepository).existsByPhone(request.getPhone());
        verifyNoMoreInteractions(driverRepository);
    }

    @Test
    void givenDriverWithExistingUsername_whenCreateDriver_thenThrowException() {
        DriverRequest request = TestUtil.getDriverRequest();

        doReturn(true)
                .when(driverRepository)
                .existsByUsername(request.getUsername());
        assertThrows(
                UsernameAlreadyExistsException.class,
                () -> driverService.createDriver(request)
        );

        verify(driverRepository).existsByUsername(request.getUsername());
        verify(driverRepository).existsByPhone(request.getPhone());
        verifyNoMoreInteractions(ignoreStubs(driverRepository));
    }

    @Test
    void givenExistingDriver_whenEditDriver_thenUpdateDriver() {
        DriverResponse response = TestUtil.getUpdateDriverResponse();
        DriverRequest request = TestUtil.getUpdateDriverRequest();
        Driver driver = TestUtil.getDefaultDriver();
        Driver editDriver = TestUtil.getSecondDriver();

        doReturn(false)
                .when(driverRepository)
                .existsByUsername(editDriver.getUsername());
        doReturn(false)
                .when(driverRepository)
                .existsByPhone(editDriver.getPhone());
        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findById(TestUtil.DEFAULT_ID);
        doReturn(editDriver)
                .when(modelMapper)
                .map(request, Driver.class);
        doReturn(editDriver)
                .when(driverRepository)
                .save(editDriver);
        doReturn(response)
                .when(modelMapper)
                .map(editDriver, DriverResponse.class);

        DriverResponse result = driverService.editDriver(TestUtil.DEFAULT_ID, request);

        verify(driverRepository).existsByUsername(editDriver.getUsername());
        verify(driverRepository).existsByPhone(editDriver.getPhone());
        verify(driverRepository).findById(TestUtil.DEFAULT_ID);
        verify(modelMapper).map(request, Driver.class);
        verify(driverRepository).save(editDriver);
        verify(modelMapper).map(editDriver, DriverResponse.class);
        assertEquals(response, result);
    }

    @Test
    void givenDriverWithExistingPhone_whenEditDriver_thenThrowException() {
        DriverRequest request = TestUtil.getUpdateDriverRequest();

        doReturn(true)
                .when(driverRepository)
                .existsByPhone(request.getPhone());

        assertThrows(
                PhoneAlreadyExistsException.class,
                () -> driverService.editDriver(TestUtil.DEFAULT_ID, request)
        );

        verify(driverRepository).existsByPhone(request.getPhone());
        verifyNoMoreInteractions(driverRepository);
    }

    @Test
    void givenDriverWithExistingUsername_whenEditDriver_thenThrowException() {
        DriverRequest request = TestUtil.getUpdateDriverRequest();

        doReturn(true)
                .when(driverRepository)
                .existsByUsername(request.getUsername());

        assertThrows(
                UsernameAlreadyExistsException.class,
                () -> driverService.editDriver(TestUtil.DEFAULT_ID, request)
        );

        verify(driverRepository).existsByUsername(request.getUsername());
        verify(driverRepository).existsByPhone(request.getPhone());
        verifyNoMoreInteractions(ignoreStubs(driverRepository));
    }

    @Test
    void givenNonExistingDriver_whenEditDriver_thenThrowException() {
        DriverRequest request = TestUtil.getUpdateDriverRequest();

        doReturn(Optional.empty())
                .when(driverRepository)
                .findById(TestUtil.DEFAULT_ID);

        assertThrows(
                DriverNotFoundException.class,
                () -> driverService.editDriver(TestUtil.DEFAULT_ID, request)
        );

        verify(driverRepository).existsByUsername(request.getUsername());
        verify(driverRepository).existsByPhone(request.getPhone());
        verify(driverRepository).findById(TestUtil.DEFAULT_ID);
        verifyNoMoreInteractions(ignoreStubs(driverRepository));
    }

    @Test
    void givenExistingDriver_whenDeleteDriver_thenDeleteDriver() {
        Driver driver = TestUtil.getDefaultDriver();

        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findById(driver.getId());

        driverService.deleteDriver(driver.getId());

        verify(driverRepository).findById(driver.getId());
        verify(driverRepository).delete(driver);
    }

    @Test
    void givenNonExistingDriver_whenDeleteDriver_thenThrowException() {
        doReturn(Optional.empty())
                .when(driverRepository)
                .findById(TestUtil.DEFAULT_ID);

        assertThrows(
                DriverNotFoundException.class,
                () -> driverService.deleteDriver(TestUtil.DEFAULT_ID)
        );

        verify(driverRepository).findById(TestUtil.DEFAULT_ID);
        verifyNoMoreInteractions(driverRepository);
    }

    @Test
    void givenAvailableDriver_whenAcceptRide_thenAcceptRide() {
        Driver availableDriver = TestUtil.getAvailableDriver();
        Car car = availableDriver.getCar();
        CarResponse carResponse = TestUtil.getDefaultCarResponse();

        doReturn(Optional.of(availableDriver))
                .when(driverRepository)
                .findById(availableDriver.getId());
        doReturn(carResponse)
                .when(modelMapper)
                .map(car, CarResponse.class);

        driverService.acceptRide(TestUtil.DEFAULT_RIDE_ID, availableDriver.getId());

        verify(driverRepository, times(2)).findById(availableDriver.getId());
        verify(modelMapper).map(car, CarResponse.class);
        verify(driverRepository).save(availableDriver);
        assertFalse(availableDriver.isAvailable());
    }

    @Test
    void givenNotAvailableDriver_whenAcceptRide_thenThrowException() {
        Driver notAvailableDriver = TestUtil.getNotAvailableDriver();

        doReturn(Optional.of(notAvailableDriver))
                .when(driverRepository)
                .findById(notAvailableDriver.getId());

        assertThrows(
                DriverIsNotAvailableException.class,
                () -> driverService.acceptRide(TestUtil.DEFAULT_RIDE_ID, notAvailableDriver.getId())
        );

        verify(driverRepository).findById(notAvailableDriver.getId());
        verifyNoMoreInteractions(driverRepository);
        assertFalse(notAvailableDriver.isAvailable());
    }

    @Test
    void givenAvailableDriverWithoutCar_whenAcceptRide_thenThrowException() {
        Driver availableDriver = TestUtil.getAvailableDriverWithoutCar();

        doReturn(Optional.of(availableDriver))
                .when(driverRepository)
                .findById(availableDriver.getId());

        assertThrows(
                DriverNotAddedCarException.class,
                () -> driverService.acceptRide(TestUtil.DEFAULT_RIDE_ID, availableDriver.getId())
        );

        verify(driverRepository, times(2)).findById(availableDriver.getId());
        verifyNoMoreInteractions(driverRepository);
        assertTrue(availableDriver.isAvailable());
    }

    @Test
    void givenNonExistingDriver_whenAcceptRide_thenThrowException() {
        doReturn(Optional.empty())
                .when(driverRepository)
                .findById(TestUtil.DEFAULT_ID);

        assertThrows(
                DriverNotFoundException.class,
                () -> driverService.acceptRide(TestUtil.DEFAULT_RIDE_ID, TestUtil.DEFAULT_ID)
        );

        verify(driverRepository).findById(TestUtil.DEFAULT_ID);
        verifyNoMoreInteractions(driverRepository);
    }

    @Test
    void givenUnavailableDriver_whenRejectRide_thenRejectRide() {
        Driver notAvailableDriver = TestUtil.getNotAvailableDriver();
        Car car = notAvailableDriver.getCar();
        CarResponse carResponse = TestUtil.getDefaultCarResponse();

        doReturn(Optional.of(notAvailableDriver))
                .when(driverRepository)
                .findById(notAvailableDriver.getId());
        doReturn(carResponse)
                .when(modelMapper)
                .map(car, CarResponse.class);

        driverService.rejectRide(TestUtil.DEFAULT_RIDE_ID, notAvailableDriver.getId());

        verify(driverRepository, times(2)).findById(notAvailableDriver.getId());
        verify(modelMapper).map(car, CarResponse.class);
        verify(driverRepository).save(notAvailableDriver);
        assertTrue(notAvailableDriver.isAvailable());
    }

    @Test
    void givenAvailableDriver_whenRejectRide_thenThrowException() {
        Driver availableDriver = TestUtil.getAvailableDriver();

        doReturn(Optional.of(availableDriver))
                .when(driverRepository)
                .findById(availableDriver.getId());

        assertThrows(
                DriverNoRidesException.class,
                () -> driverService.rejectRide(TestUtil.DEFAULT_RIDE_ID, availableDriver.getId())
        );

        verify(driverRepository).findById(availableDriver.getId());
        verifyNoMoreInteractions(driverRepository);
        assertTrue(availableDriver.isAvailable());
    }

    @Test
    void givenNotAvailableDriverWithoutCar_whenAcceptRide_thenThrowException() {
        Driver notAvailableDriver = TestUtil.getNotAvailableDriverWithoutCar();

        doReturn(Optional.of(notAvailableDriver))
                .when(driverRepository)
                .findById(notAvailableDriver.getId());

        assertThrows(
                DriverNotAddedCarException.class,
                () -> driverService.rejectRide(TestUtil.DEFAULT_RIDE_ID, notAvailableDriver.getId())
        );

        verify(driverRepository, times(2)).findById(notAvailableDriver.getId());
        verifyNoMoreInteractions(driverRepository);
        assertFalse(notAvailableDriver.isAvailable());
    }

    @Test
    void givenNonExistingDriver_whenRejectRide_thenThrowException() {
        doReturn(Optional.empty())
                .when(driverRepository)
                .findById(TestUtil.DEFAULT_ID);

        assertThrows(
                DriverNotFoundException.class,
                () -> driverService.rejectRide(TestUtil.DEFAULT_RIDE_ID, TestUtil.DEFAULT_ID)
        );

        verify(driverRepository).findById(TestUtil.DEFAULT_ID);
        verifyNoMoreInteractions(driverRepository);
    }

    @Test
    void givenUnavailableDriver_whenStartRide_thenStartRide() {
        Driver notAvailableDriver = TestUtil.getNotAvailableDriver();
        Car car = notAvailableDriver.getCar();
        CarResponse carResponse = TestUtil.getDefaultCarResponse();

        doReturn(Optional.of(notAvailableDriver))
                .when(driverRepository)
                .findById(notAvailableDriver.getId());
        doReturn(carResponse)
                .when(modelMapper)
                .map(car, CarResponse.class);

        driverService.startRide(TestUtil.DEFAULT_RIDE_ID, notAvailableDriver.getId());

        verify(driverRepository, times(2)).findById(notAvailableDriver.getId());
        verify(modelMapper).map(car, CarResponse.class);
        verify(driverRepository).save(notAvailableDriver);
        assertFalse(notAvailableDriver.isAvailable());
    }

    @Test
    void givenAvailableDriver_whenStartRide_thenThrowException() {
        Driver availableDriver = TestUtil.getAvailableDriver();

        doReturn(Optional.of(availableDriver))
                .when(driverRepository)
                .findById(availableDriver.getId());

        assertThrows(
                DriverNoRidesException.class,
                () -> driverService.finishRide(TestUtil.DEFAULT_RIDE_ID, availableDriver.getId())
        );

        verify(driverRepository).findById(availableDriver.getId());
        verifyNoMoreInteractions(driverRepository);
        assertTrue(availableDriver.isAvailable());
    }

    @Test
    void givenNotAvailableDriverWithoutCar_whenStartRide_thenThrowException() {
        Driver notAvailableDriver = TestUtil.getNotAvailableDriverWithoutCar();

        doReturn(Optional.of(notAvailableDriver))
                .when(driverRepository)
                .findById(notAvailableDriver.getId());

        assertThrows(
                DriverNotAddedCarException.class,
                () -> driverService.startRide(TestUtil.DEFAULT_RIDE_ID, notAvailableDriver.getId())
        );

        verify(driverRepository, times(2)).findById(notAvailableDriver.getId());
        verifyNoMoreInteractions(driverRepository);
        assertFalse(notAvailableDriver.isAvailable());
    }

    @Test
    void givenNonExistingDriver_whenStartRide_thenThrowException() {
        doReturn(Optional.empty())
                .when(driverRepository)
                .findById(TestUtil.DEFAULT_ID);

        assertThrows(
                DriverNotFoundException.class,
                () -> driverService.startRide(TestUtil.DEFAULT_RIDE_ID, TestUtil.DEFAULT_ID)
        );

        verify(driverRepository).findById(TestUtil.DEFAULT_ID);
        verifyNoMoreInteractions(driverRepository);
    }

    @Test
    void givenUnavailableDriver_whenFinishRide_thenFinishRide() {
        Driver notAvailableDriver = TestUtil.getNotAvailableDriver();
        Car car = notAvailableDriver.getCar();
        CarResponse carResponse = TestUtil.getDefaultCarResponse();

        doReturn(Optional.of(notAvailableDriver))
                .when(driverRepository)
                .findById(notAvailableDriver.getId());
        doReturn(carResponse)
                .when(modelMapper)
                .map(car, CarResponse.class);

        driverService.finishRide(TestUtil.DEFAULT_RIDE_ID, notAvailableDriver.getId());

        verify(driverRepository, times(2)).findById(notAvailableDriver.getId());
        verify(modelMapper).map(car, CarResponse.class);
        verify(driverRepository).save(notAvailableDriver);
        assertTrue(notAvailableDriver.isAvailable());
    }

    @Test
    void givenAvailableDriver_whenFinishRide_thenThrowException() {
        Driver availableDriver = TestUtil.getAvailableDriver();

        doReturn(Optional.of(availableDriver))
                .when(driverRepository)
                .findById(availableDriver.getId());

        assertThrows(
                DriverNoRidesException.class,
                () -> driverService.finishRide(TestUtil.DEFAULT_RIDE_ID, availableDriver.getId())
        );

        verify(driverRepository).findById(availableDriver.getId());
        verifyNoMoreInteractions(driverRepository);
        assertTrue(availableDriver.isAvailable());
    }

    @Test
    void givenNotAvailableDriverWithoutCar_whenFinishRide_thenThrowException() {
        Driver notAvailableDriver = TestUtil.getNotAvailableDriverWithoutCar();

        doReturn(Optional.of(notAvailableDriver))
                .when(driverRepository)
                .findById(notAvailableDriver.getId());

        assertThrows(
                DriverNotAddedCarException.class,
                () -> driverService.finishRide(TestUtil.DEFAULT_RIDE_ID, notAvailableDriver.getId())
        );

        verify(driverRepository, times(2)).findById(notAvailableDriver.getId());
        verifyNoMoreInteractions(driverRepository);
        assertFalse(notAvailableDriver.isAvailable());
    }

    @Test
    void givenNonExistingDriver_whenFinishRide_thenThrowException() {
        doReturn(Optional.empty())
                .when(driverRepository)
                .findById(TestUtil.DEFAULT_ID);

        assertThrows(
                DriverNotFoundException.class,
                () -> driverService.finishRide(TestUtil.DEFAULT_RIDE_ID, TestUtil.DEFAULT_ID)
        );

        verify(driverRepository).findById(TestUtil.DEFAULT_ID);
        verifyNoMoreInteractions(driverRepository);
    }

    @Test
    void givenExistingDriver_whenCreateReview_thenCreateReview() {
        Driver driver = TestUtil.getDefaultDriver();
        RatingToDriverRequest request = TestUtil.getRatingToDriverRequest();

        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findById(request.getDriverId());

        driverService.createReview(request);

        verify(driverRepository).findById(request.getDriverId());
        assertEquals(3, driver.getRatingSet().size());
    }

    @Test
    void givenNonExistingDriver_whenCreateReview_thenThrowException() {
        RatingToDriverRequest request = TestUtil.getRatingToDriverRequest();

        doReturn(Optional.empty())
                .when(driverRepository)
                .findById(request.getDriverId());

        assertThrows(
                DriverNotFoundException.class,
                () -> driverService.createReview(request)
        );

        verify(driverRepository).findById(request.getDriverId());
        verifyNoMoreInteractions(driverRepository);
    }

    @Test
    void givenExistingDriver_whenAddCar_thenAddCarToDriver() {
        Driver driver = TestUtil.getDefaultDriver();
        Car car = TestUtil.getDefaultCar();
        CarRequest carRequest = TestUtil.getDefaultCarRequest();
        CarResponse carResponse = TestUtil.getDefaultCarResponse();
        DriverResponse driverResponse = TestUtil.getDefaultDriverResponse();

        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findById(driver.getId());
        doReturn(carResponse)
                .when(carService)
                .createCar(carRequest);
        doReturn(car)
                .when(modelMapper)
                .map(carRequest, Car.class);
        doReturn(driver)
                .when(driverRepository)
                .save(driver);
        doReturn(driverResponse)
                .when(modelMapper)
                .map(driver, DriverResponse.class);

        driverService.addCarToDriver(driver.getId(), carRequest);

        verify(driverRepository).findById(driver.getId());
        verify(carService).createCar(carRequest);
        verify(modelMapper).map(carRequest, Car.class);
        verify(driverRepository).save(driver);
        verify(modelMapper).map(driver, DriverResponse.class);
        assertEquals(driver.getCar(), car);
    }

    @Test
    void givenNonExistingDriver_whenAddCar_thenThrowException() {
        CarRequest carRequest = TestUtil.getDefaultCarRequest();

        doReturn(Optional.empty())
                .when(driverRepository)
                .findById(TestUtil.DEFAULT_ID);

        assertThrows(
                DriverNotFoundException.class,
                () -> driverService.addCarToDriver(TestUtil.DEFAULT_ID, carRequest)
        );

        verify(driverRepository).findById(TestUtil.DEFAULT_ID);
        verifyNoMoreInteractions(driverRepository);
    }

    @Test
    void givenExistingDriver_whenGetDriverRides_thenReturnPageResponseRideResponse() {
        Driver driver = TestUtil.getDefaultDriver();
        List<RideResponse> expectedList = TestUtil.getRideResponseList();
        PageResponse<RideResponse> expectedPageResponse = TestUtil.getPageRideResponse();

        doReturn(expectedPageResponse)
                .when(rideService)
                .getRidesByDriverId(driver.getId(), TestUtil.DEFAULT_PAGE,
                        TestUtil.DEFAULT_PAGE_SIZE, TestUtil.DEFAULT_PAGE_SORT);

        PageResponse<RideResponse> result =
                driverService.getDriverRides(driver.getId(), TestUtil.DEFAULT_PAGE,
                        TestUtil.DEFAULT_PAGE_SIZE, TestUtil.DEFAULT_PAGE_SORT);

        verify(rideService).getRidesByDriverId(driver.getId(), TestUtil.DEFAULT_PAGE,
                TestUtil.DEFAULT_PAGE_SIZE, TestUtil.DEFAULT_PAGE_SORT);
        assertNotNull(result);
        assertEquals(result.getTotalElements(), expectedList.size());
        assertEquals(expectedPageResponse, result);
    }
}
