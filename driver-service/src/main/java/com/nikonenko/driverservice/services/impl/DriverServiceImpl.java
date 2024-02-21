package com.nikonenko.driverservice.services.impl;

import com.nikonenko.driverservice.dto.CarRequest;
import com.nikonenko.driverservice.dto.CarResponse;
import com.nikonenko.driverservice.dto.ChangeRideStatusRequest;
import com.nikonenko.driverservice.dto.DriverRequest;
import com.nikonenko.driverservice.dto.DriverResponse;
import com.nikonenko.driverservice.dto.PageResponse;
import com.nikonenko.driverservice.dto.RatingFromDriverRequest;
import com.nikonenko.driverservice.dto.RatingToDriverRequest;
import com.nikonenko.driverservice.dto.ReviewRequest;
import com.nikonenko.driverservice.dto.feign.rides.RideResponse;
import com.nikonenko.driverservice.exceptions.DriverIsNotAvailableException;
import com.nikonenko.driverservice.exceptions.DriverNoRidesException;
import com.nikonenko.driverservice.exceptions.DriverNotAddedCarException;
import com.nikonenko.driverservice.exceptions.DriverNotFoundException;
import com.nikonenko.driverservice.exceptions.PhoneAlreadyExistsException;
import com.nikonenko.driverservice.exceptions.UsernameAlreadyExistsException;
import com.nikonenko.driverservice.exceptions.WrongPageableParameterException;
import com.nikonenko.driverservice.exceptions.WrongSortFieldException;
import com.nikonenko.driverservice.kafka.producer.DriverReviewRequestProducer;
import com.nikonenko.driverservice.kafka.producer.RideStatusRequestProducer;
import com.nikonenko.driverservice.models.Car;
import com.nikonenko.driverservice.models.Driver;
import com.nikonenko.driverservice.models.RatingDriver;
import com.nikonenko.driverservice.models.RideAction;
import com.nikonenko.driverservice.repositories.DriverRepository;
import com.nikonenko.driverservice.services.CarService;
import com.nikonenko.driverservice.services.DriverService;
import com.nikonenko.driverservice.services.feign.RideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverServiceImpl implements DriverService {
    private final DriverRepository driverRepository;
    private final ModelMapper modelMapper;
    private final CarService carService;
    private final RideStatusRequestProducer rideStatusRequestProducer;
    private final DriverReviewRequestProducer driverReviewRequestProducer;
    private final RideService rideService;

    @Override
    public PageResponse<DriverResponse> getAllDrivers(int pageNumber, int pageSize, String sortField) {
        if (pageNumber < 0 || pageSize < 1) {
            throw new WrongPageableParameterException();
        }
        checkSortField(sortField);
        Page<Driver> page = driverRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(sortField)));
        List<DriverResponse> drivers = page.getContent().stream()
                .map(driver -> modelMapper.map(driver, DriverResponse.class))
                .toList();
        return PageResponse.<DriverResponse>builder()
                .objectList(drivers)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    private void checkSortField(String sortField) {
        if (Stream.of(DriverResponse.class.getDeclaredFields())
                .map(Field::getName)
                .noneMatch(field -> field.equals(sortField))) {
            throw new WrongSortFieldException();
        }
    }

    @Override
    public DriverResponse getDriverById(Long id) {
        return modelMapper.map(getOrThrow(id), DriverResponse.class);
    }

    @Override
    public DriverResponse createDriver(DriverRequest driverRequest) {
        checkDriverExists(driverRequest);
        Driver driver = modelMapper.map(driverRequest, Driver.class);
        Driver savedDriver = driverRepository.save(driver);
        log.info("Driver created with id: {}", savedDriver.getId());
        return modelMapper.map(savedDriver, DriverResponse.class);
    }

    @Override
    public DriverResponse editDriver(Long id, DriverRequest driverRequest) {
        checkDriverExists(driverRequest);
        Driver editingDriver = getOrThrow(id);
        editingDriver = modelMapper.map(driverRequest, Driver.class);
        editingDriver.setId(id);
        driverRepository.save(editingDriver);
        log.info("Driver edited with id: {}", id);
        return modelMapper.map(editingDriver, DriverResponse.class);
    }

    @Override
    public void deleteDriver(Long id) {
        driverRepository.delete(getOrThrow(id));
        log.info("Driver deleted with id: {}", id);
    }

    @Override
    public void acceptRide(String rideId, Long driverId) {
        Driver driver = getAvailableDriver(driverId);
        sendRequest(rideId, driverId, RideAction.ACCEPT);
        driver.setAvailable(false);
        driverRepository.save(driver);
    }

    @Override
    public void rejectRide(String rideId, Long driverId) {
        Driver driver = getNotAvailableDriver(driverId);
        sendRequest(rideId, driverId, RideAction.REJECT);
        driver.setAvailable(true);
        driverRepository.save(driver);
    }

    @Override
    public void startRide(String rideId, Long driverId) {
        Driver driver = getNotAvailableDriver(driverId);
        sendRequest(rideId, driverId, RideAction.START);
        driverRepository.save(driver);
    }

    @Override
    public void finishRide(String rideId, Long driverId) {
        Driver driver = getNotAvailableDriver(driverId);
        sendRequest(rideId, driverId, RideAction.FINISH);
        driver.setAvailable(true);
        driverRepository.save(driver);
    }

    @Override
    public void sendReviewToPassenger(String rideId, RatingFromDriverRequest request) {
        driverReviewRequestProducer.sendRatingPassengerRequest(ReviewRequest.builder()
                .rideId(rideId)
                .rating(request.getRating())
                .comment(request.getComment())
                .build());
    }

    private Driver getNotAvailableDriver(Long driverId) {
        Driver driver = getOrThrow(driverId);
        if (driver.getAvailable()) {
            throw new DriverNoRidesException();
        }
        return driver;
    }

    private Driver getAvailableDriver(Long driverId) {
        Driver driver = getOrThrow(driverId);
        if (!driver.getAvailable()) {
            throw new DriverIsNotAvailableException();
        }
        return driver;
    }

    private void sendRequest(String rideId, Long driverId, RideAction rideAction) {
        rideStatusRequestProducer.sendChangeRideStatusRequest(ChangeRideStatusRequest.builder()
                .rideId(rideId)
                .driverId(driverId)
                .rideAction(rideAction)
                .car(getCarByDriver(driverId))
                .build());
    }

    private CarResponse getCarByDriver(Long driverId) {
        Driver driver = getOrThrow(driverId);
        return Optional.ofNullable(driver.getCar())
                .map(car -> modelMapper.map(car, CarResponse.class))
                .orElseThrow(DriverNotAddedCarException::new);
    }

    @Override
    public void createReview(RatingToDriverRequest ratingRequest) {
        Driver driver = getOrThrow(ratingRequest.getDriverId());

        RatingDriver addingRating = RatingDriver.builder()
                .driverId(driver.getId())
                .rating(ratingRequest.getRating())
                .comment(ratingRequest.getComment())
                .build();

        Set<RatingDriver> modifiedRatingSet = new HashSet<>(driver.getRatingSet());
        modifiedRatingSet.add(addingRating);
        driver.setRatingSet(modifiedRatingSet);

        driverRepository.save(driver);
    }

    @Override
    public DriverResponse addCarToDriver(Long id, CarRequest carRequest) {
        Driver driver = getOrThrow(id);
        carService.createCar(carRequest);
        driver.setCar(modelMapper.map(carRequest, Car.class));
        return modelMapper.map(driverRepository.save(driver), DriverResponse.class);
    }

    @Override
    public PageResponse<RideResponse> getDriverRides(Long driverId, int pageNumber, int pageSize, String sortField) {
        getOrThrow(driverId);
        return rideService.getRidesByDriverId(driverId, pageNumber, pageSize, sortField);
    }

    public Driver getOrThrow(Long id) {
        Optional<Driver> optionalDriver = driverRepository.findById(id);
        return optionalDriver.orElseThrow(DriverNotFoundException::new);
    }

    public void checkDriverExists(DriverRequest driverRequest) {
        if (driverRepository.existsByPhone(driverRequest.getPhone())) {
            log.info("Driver with phone {} already exists!", driverRequest.getPhone());
            throw new PhoneAlreadyExistsException();
        }
        if (driverRepository.existsByUsername(driverRequest.getUsername())) {
            log.info("Driver with username {} already exists!", driverRequest.getUsername());
            throw new UsernameAlreadyExistsException();
        }
    }
}
