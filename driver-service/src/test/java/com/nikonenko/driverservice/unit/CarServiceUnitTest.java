package com.nikonenko.driverservice.unit;

import com.nikonenko.driverservice.dto.CarRequest;
import com.nikonenko.driverservice.dto.CarResponse;
import com.nikonenko.driverservice.dto.PageResponse;
import com.nikonenko.driverservice.exceptions.CarNotFoundException;
import com.nikonenko.driverservice.exceptions.CarNumberAlreadyExistsException;
import com.nikonenko.driverservice.exceptions.WrongPageableParameterException;
import com.nikonenko.driverservice.exceptions.WrongSortFieldException;
import com.nikonenko.driverservice.models.Car;
import com.nikonenko.driverservice.repositories.CarRepository;
import com.nikonenko.driverservice.services.impl.CarServiceImpl;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CarServiceUnitTest {
    @Mock
    private CarRepository carRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private CarServiceImpl carService;

    @Test
    void givenValidParams_whenGetCars_thenReturnPageResponseCarResponse() {
        List<Car> expectedList = TestUtil.getDefaultCarList();
        Page<Car> carsPage = new PageImpl<>(expectedList);
        List<CarResponse> expectedResponses = TestUtil.getCarResponseList(modelMapper, expectedList);

        doReturn(carsPage)
                .when(carRepository)
                .findAll(PageRequest.of(TestUtil.DEFAULT_PAGE,
                        TestUtil.DEFAULT_PAGE_SIZE, Sort.by(TestUtil.DEFAULT_PAGE_SORT)));

        PageResponse<CarResponse> result =
                carService.getAllCars(TestUtil.DEFAULT_PAGE,
                        TestUtil.DEFAULT_PAGE_SIZE, TestUtil.DEFAULT_PAGE_SORT);

        verify(carRepository).findAll(any(Pageable.class));
        assertNotNull(result);
        assertEquals(result.getTotalElements(), expectedList.size());
        assertEquals(result.getTotalPages(), carsPage.getTotalPages());
        assertThat(result.getObjectList()).containsExactlyElementsOf(expectedResponses);
    }

    @Test
    void givenInvalidPageParams_whenGetCars_thenThrowException() {
        assertThrows(
                WrongPageableParameterException.class,
                () -> carService.getAllCars(TestUtil.INVALID_PAGE,
                        TestUtil.INVALID_PAGE_SIZE, TestUtil.DEFAULT_PAGE_SORT)
        );
        verifyNoInteractions(carRepository);
    }

    @Test
    void givenInvalidSortByParam_whenGetCars_thenThrowException() {
        assertThrows(
                WrongSortFieldException.class,
                () -> carService.getAllCars(TestUtil.DEFAULT_PAGE,
                        TestUtil.DEFAULT_PAGE_SIZE, TestUtil.INVALID_PAGE_SORT)
        );
        verifyNoInteractions(carRepository);
    }

    @Test
    void givenExistingCarId_whenFindById_thenReturnCarResponse() {
        Car retrievedCar = TestUtil.getDefaultCar();
        CarResponse response = TestUtil.getDefaultCarResponse();

        doReturn(Optional.of(retrievedCar))
                .when(carRepository)
                .findById(TestUtil.DEFAULT_CAR_ID);
        when(modelMapper.map(retrievedCar, CarResponse.class))
                .thenReturn(response);

        CarResponse result = carService.getCarById(TestUtil.DEFAULT_CAR_ID);

        verify(carRepository).findById(TestUtil.DEFAULT_CAR_ID);
        verify(modelMapper).map(retrievedCar, CarResponse.class);
        assertNotNull(result);
        assertEquals(response, result);
    }

    @Test
    void givenNonExistingCarId_whenFindById_thenThrowException() {
        doReturn(Optional.empty())
                .when(carRepository)
                .findById(TestUtil.DEFAULT_CAR_ID);

        assertThrows(
                CarNotFoundException.class,
                () -> carService.getCarById(TestUtil.DEFAULT_CAR_ID)
        );

        verify(carRepository).findById(TestUtil.DEFAULT_CAR_ID);
    }

    @Test
    void givenNonExistingCar_whenCreateCar_thenCreateNewCar() {
        CarResponse response = TestUtil.getDefaultCarResponse();
        CarRequest request = TestUtil.getDefaultCarRequest();
        Car notSavedCar = TestUtil.getNotSavedCar();
        Car savedCar = TestUtil.getDefaultCar();

        doReturn(false)
                .when(carRepository)
                .existsByNumber(request.getNumber());
        doReturn(notSavedCar)
                .when(modelMapper)
                .map(request, Car.class);
        doReturn(savedCar)
                .when(carRepository)
                .save(notSavedCar);
        doReturn(response)
                .when(modelMapper)
                .map(savedCar, CarResponse.class);

        CarResponse result = carService.createCar(request);

        verify(carRepository).existsByNumber(request.getNumber());
        verify(modelMapper).map(request, Car.class);
        verify(carRepository).save(notSavedCar);
        verify(modelMapper).map(savedCar, CarResponse.class);
        assertEquals(response, result);
    }

    @Test
    void givenCarWithExistingNumber_whenCreateCar_thenThrowException() {
        CarRequest request = TestUtil.getDefaultCarRequest();

        doReturn(true)
                .when(carRepository)
                .existsByNumber(request.getNumber());
        assertThrows(
                CarNumberAlreadyExistsException.class,
                () -> carService.createCar(request)
        );

        verify(carRepository).existsByNumber(request.getNumber());
        verifyNoMoreInteractions(carRepository);
    }

    @Test
    void givenExistingCar_whenEditCar_thenUpdateCar() {
        CarResponse response = TestUtil.getUpdateCarResponse();
        CarRequest request = TestUtil.getUpdateCarRequest();
        Car car = TestUtil.getDefaultCar();
        Car editCar = TestUtil.getSecondCar();

        doReturn(false)
                .when(carRepository)
                .existsByNumber(editCar.getNumber());
        doReturn(Optional.of(car))
                .when(carRepository)
                .findById(TestUtil.DEFAULT_CAR_ID);
        doReturn(editCar)
                .when(modelMapper)
                .map(request, Car.class);
        doReturn(editCar)
                .when(carRepository)
                .save(editCar);
        doReturn(response)
                .when(modelMapper)
                .map(editCar, CarResponse.class);

        CarResponse result = carService.editCar(TestUtil.DEFAULT_CAR_ID, request);

        verify(carRepository).existsByNumber(editCar.getNumber());
        verify(carRepository).findById(TestUtil.DEFAULT_CAR_ID);
        verify(modelMapper).map(request, Car.class);
        verify(carRepository).save(editCar);
        verify(modelMapper).map(editCar, CarResponse.class);
        assertEquals(response, result);
    }

    @Test
    void givenCarWithExistingNumber_whenEditCar_thenThrowException() {
        CarRequest request = TestUtil.getUpdateCarRequest();

        doReturn(true)
                .when(carRepository)
                .existsByNumber(request.getNumber());

        assertThrows(
                CarNumberAlreadyExistsException.class,
                () -> carService.editCar(TestUtil.DEFAULT_CAR_ID, request)
        );

        verify(carRepository).existsByNumber(request.getNumber());
        verifyNoMoreInteractions(carRepository);
    }

    @Test
    void givenNonExistingCar_whenEditCar_thenThrowException() {
        CarRequest request = TestUtil.getDefaultCarRequest();

        doReturn(Optional.empty())
                .when(carRepository)
                .findById(TestUtil.DEFAULT_CAR_ID);

        assertThrows(
                CarNotFoundException.class,
                () -> carService.editCar(TestUtil.DEFAULT_CAR_ID, request)
        );

        verify(carRepository).existsByNumber(request.getNumber());
        verify(carRepository).findById(TestUtil.DEFAULT_CAR_ID);
        verifyNoMoreInteractions(ignoreStubs(carRepository));
    }

    @Test
    void givenExistingCar_whenDeleteCar_thenDeleteCar() {
        Car car = TestUtil.getDefaultCar();

        doReturn(Optional.of(car))
                .when(carRepository)
                .findById(car.getId());

        carService.deleteCar(car.getId());

        verify(carRepository).findById(car.getId());
        verify(carRepository).delete(car);
    }

    @Test
    void givenNonExistingCar_whenDeleteCar_thenThrowException() {
        doReturn(Optional.empty())
                .when(carRepository)
                .findById(TestUtil.DEFAULT_CAR_ID);

        assertThrows(
                CarNotFoundException.class,
                () -> carService.deleteCar(TestUtil.DEFAULT_CAR_ID)
        );

        verify(carRepository).findById(TestUtil.DEFAULT_CAR_ID);
        verifyNoMoreInteractions(carRepository);
    }
}
