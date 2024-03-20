package com.nikonenko.driverservice.services.impl;

import com.nikonenko.driverservice.dto.CarRequest;
import com.nikonenko.driverservice.dto.CarResponse;
import com.nikonenko.driverservice.dto.PageResponse;
import com.nikonenko.driverservice.exceptions.CarNotFoundException;
import com.nikonenko.driverservice.exceptions.CarNumberAlreadyExistsException;
import com.nikonenko.driverservice.models.Car;
import com.nikonenko.driverservice.repositories.CarRepository;
import com.nikonenko.driverservice.services.CarService;
import com.nikonenko.driverservice.utils.LogList;
import com.nikonenko.driverservice.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final ModelMapper modelMapper;

    @Override
    public PageResponse<CarResponse> getAllCars(int pageNumber, int pageSize, String sortField) {
        Pageable pageable = PageUtil.createPageable(pageNumber, pageSize, sortField, CarResponse.class);
        Page<Car> page = carRepository.findAll(pageable);
        List<CarResponse> cars = page.getContent().stream()
                .map(car -> modelMapper.map(car, CarResponse.class))
                .toList();
        return PageResponse.<CarResponse>builder()
                .objectList(cars)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    public CarResponse createCar(CarRequest carRequest) {
        checkCarExists(carRequest);
        Car savedCar = carRepository.save(modelMapper.map(carRequest, Car.class));
        log.info(LogList.LOG_CREATE_CAR, savedCar.getId());
        return modelMapper.map(savedCar, CarResponse.class);
    }

    @Override
    public CarResponse getCarById(Long id) {
        Car car = getOrThrow(id);
        log.info(LogList.LOG_GET_CAR, id);
        return modelMapper.map(car, CarResponse.class);
    }

    @Override
    public CarResponse editCar(Long id, CarRequest carRequest) {
        checkCarExists(carRequest);
        Car editingCar = getOrThrow(id);
        editingCar = modelMapper.map(carRequest, Car.class);
        editingCar.setId(id);
        carRepository.save(editingCar);
        log.info(LogList.LOG_EDIT_CAR, id);
        return modelMapper.map(editingCar, CarResponse.class);
    }

    @Override
    public void deleteCar(Long id) {
        carRepository.delete(getOrThrow(id));
        log.info(LogList.LOG_DELETE_CAR, id);
    }

    public Car getOrThrow(Long id) {
        return carRepository.findById(id).orElseThrow(CarNotFoundException::new);
    }

    public void checkCarExists(CarRequest carRequest) {
        if (carRepository.existsByNumber(carRequest.getNumber())) {
            throw new CarNumberAlreadyExistsException();
        }
    }
}
