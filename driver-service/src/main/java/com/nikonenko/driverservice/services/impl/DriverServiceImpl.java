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
import com.nikonenko.driverservice.exceptions.CarNotFoundException;
import com.nikonenko.driverservice.exceptions.DriverIsNotAvailableException;
import com.nikonenko.driverservice.exceptions.DriverNoRidesException;
import com.nikonenko.driverservice.exceptions.DriverNotAddedCarException;
import com.nikonenko.driverservice.exceptions.DriverNotFoundException;
import com.nikonenko.driverservice.exceptions.KeycloakUserIsNotValid;
import com.nikonenko.driverservice.exceptions.PhoneAlreadyExistsException;
import com.nikonenko.driverservice.exceptions.UsernameAlreadyExistsException;
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
import com.nikonenko.driverservice.utils.LogList;
import com.nikonenko.driverservice.utils.PageUtil;
import com.nikonenko.driverservice.utils.PatternList;
import com.nikonenko.driverservice.utils.SecurityList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
        Pageable pageable = PageUtil.createPageable(pageNumber, pageSize, sortField, DriverResponse.class);
        Page<Driver> page = driverRepository.findAll(pageable);
        List<DriverResponse> drivers = page.getContent().stream()
                .map(driver -> modelMapper.map(driver, DriverResponse.class))
                .toList();
        return PageResponse.<DriverResponse>builder()
                .objectList(drivers)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    public DriverResponse getDriverById(UUID id) {
        Driver driver = getOrThrow(id);
        log.info(LogList.LOG_GET_DRIVER, id);
        return modelMapper.map(driver, DriverResponse.class);
    }

    @Override
    public DriverResponse createDriver(OAuth2User principal) {
        DriverRequest driverRequest = createRequestFromPrincipal(principal);
        checkDriverExists(driverRequest);
        Driver driver = modelMapper.map(driverRequest, Driver.class);
        driver.setAvailable(true);
        driver.setId(principal.getAttribute(SecurityList.ID));
        Driver savedDriver = driverRepository.save(driver);
        log.info(LogList.LOG_CREATE_DRIVER, savedDriver.getId());
        return modelMapper.map(savedDriver, DriverResponse.class);
    }

    private DriverRequest createRequestFromPrincipal(OAuth2User principal) {
        String phone = principal.getAttribute(SecurityList.PHONE);
        String username = principal.getAttribute(SecurityList.USERNAME);
        if (phone == null || username == null || !phone.matches(PatternList.PHONE_PATTERN)) {
            throw new KeycloakUserIsNotValid();
        }
        return DriverRequest.builder()
                .username(username)
                .phone(phone)
                .build();
    }

    @Override
    public DriverResponse editDriver(UUID id, DriverRequest driverRequest) {
        checkDriverExists(driverRequest);
        Driver editingDriver = getOrThrow(id);
        Set<RatingDriver> driverRating = editingDriver.getRatingSet();
        editingDriver = modelMapper.map(driverRequest, Driver.class);
        editingDriver.setId(id);
        editingDriver.setRatingSet(driverRating);
        driverRepository.save(editingDriver);
        log.info(LogList.LOG_EDIT_DRIVER, id);
        return modelMapper.map(editingDriver, DriverResponse.class);
    }

    @Override
    public void deleteDriver(UUID id) {
        driverRepository.delete(getOrThrow(id));
        log.info(LogList.LOG_DELETE_DRIVER, id);
    }

    @Override
    public void acceptRide(String rideId, UUID driverId) {
        Driver driver = getAvailableDriver(driverId);
        sendRequest(rideId, driverId, RideAction.ACCEPT);
        driver.setAvailable(false);
        driverRepository.save(driver);
    }

    @Override
    public void rejectRide(String rideId, UUID driverId) {
        Driver driver = getNotAvailableDriver(driverId);
        sendRequest(rideId, driverId, RideAction.REJECT);
        driver.setAvailable(true);
        driverRepository.save(driver);
    }

    @Override
    public void startRide(String rideId, UUID driverId) {
        Driver driver = getNotAvailableDriver(driverId);
        sendRequest(rideId, driverId, RideAction.START);
        driverRepository.save(driver);
    }

    @Override
    public void finishRide(String rideId, UUID driverId) {
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

    private Driver getNotAvailableDriver(UUID driverId) {
        Driver driver = getOrThrow(driverId);
        if (driver.isAvailable()) {
            throw new DriverNoRidesException();
        }
        return driver;
    }

    private Driver getAvailableDriver(UUID driverId) {
        Driver driver = getOrThrow(driverId);
        if (!driver.isAvailable()) {
            throw new DriverIsNotAvailableException();
        }
        return driver;
    }

    private void sendRequest(String rideId, UUID driverId, RideAction rideAction) {
        rideStatusRequestProducer.sendChangeRideStatusRequest(ChangeRideStatusRequest.builder()
                .rideId(rideId)
                .driverId(driverId)
                .rideAction(rideAction)
                .car(getCarByDriver(driverId))
                .build());
    }

    private CarResponse getCarByDriver(UUID driverId) {
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
        log.info(LogList.LOG_ADD_RATING, driver.getId());
    }

    @Override
    public DriverResponse addCarToDriver(UUID id, CarRequest carRequest) {
        Driver driver = getOrThrow(id);
        CarResponse carResponse = carService.createCar(carRequest);
        driver.setCar(modelMapper.map(carResponse, Car.class));
        log.info(LogList.LOG_ADD_CAR_FOR_DRIVER, id);
        return modelMapper.map(driverRepository.save(driver), DriverResponse.class);
    }

    @Override
    public PageResponse<RideResponse> getDriverRides(UUID driverId, int pageNumber, int pageSize, String sortField) {
        return rideService.getRidesByDriverId(driverId, pageNumber, pageSize, sortField);
    }

    @Override
    public void deleteCar(UUID driverId) {
        Driver driver = getOrThrow(driverId);
        if (driver.getCar() == null) {
            throw new CarNotFoundException();
        }
        Long carId = driver.getCar().getId();
        driver.setCar(null);
        carService.deleteCar(carId);
        driverRepository.save(driver);
        log.info(LogList.LOG_DELETE_CAR_FOR_DRIVER, driverId);
    }

    public Driver getOrThrow(UUID id) {
        Optional<Driver> optionalDriver = driverRepository.findById(id);
        return optionalDriver.orElseThrow(DriverNotFoundException::new);
    }

    public void checkDriverExists(DriverRequest driverRequest) {
        if (driverRepository.existsByPhone(driverRequest.getPhone())) {
            throw new PhoneAlreadyExistsException();
        }
        if (driverRepository.existsByUsername(driverRequest.getUsername())) {
            throw new UsernameAlreadyExistsException();
        }
    }
}
