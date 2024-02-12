package com.nikonenko.driverservice.services.impl;

import com.nikonenko.driverservice.dto.CarRequest;
import com.nikonenko.driverservice.dto.CarResponse;
import com.nikonenko.driverservice.dto.PageResponse;
import com.nikonenko.driverservice.exceptions.CarNotFoundException;
import com.nikonenko.driverservice.exceptions.PhoneAlreadyExistsException;
import com.nikonenko.driverservice.exceptions.WrongPageableParameterException;
import com.nikonenko.driverservice.models.Car;
import com.nikonenko.driverservice.repositories.CarRepository;
import com.nikonenko.driverservice.services.CarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final ModelMapper modelMapper;

    @Override
    public PageResponse<CarResponse> getAllCars(int pageNumber, int pageSize, String sortField) {
        if (pageNumber < 0 || pageSize < 1) {
            throw new WrongPageableParameterException();
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortField));
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
        Car car = modelMapper.map(carRequest, Car.class);
        Car savedCar = carRepository.save(car);
        log.info("Car created with id: {}", savedCar.getId());
        return modelMapper.map(savedCar, CarResponse.class);
    }

    @Override
    public CarResponse getCarById(Long id) {
        return modelMapper.map(getCar(id), CarResponse.class);
    }

    @Override
    public CarResponse editCar(Long id, CarRequest carRequest) {
        checkCarExists(carRequest);
        Car editingCar = getCar(id);
        modelMapper.map(carRequest, editingCar);
        carRepository.save(editingCar);
        log.info("Car edited with id: {}", id);
        return modelMapper.map(editingCar, CarResponse.class);
    }

    @Override
    public void deleteCar(Long id) {
        carRepository.delete(getCar(id));
        log.info("Car deleted with id: {}", id);
    }

    public Car getCar(Long id) {
        Optional<Car> optionalCar = carRepository.findById(id);
        return optionalCar.orElseThrow(CarNotFoundException::new);
    }

    public void checkCarExists(CarRequest carRequest) {
        if (carRepository.existsByNumber(carRequest.getNumber())) {
            log.info("Car with number {} already exists!", carRequest.getNumber());
            throw new PhoneAlreadyExistsException();
        }
    }
}
