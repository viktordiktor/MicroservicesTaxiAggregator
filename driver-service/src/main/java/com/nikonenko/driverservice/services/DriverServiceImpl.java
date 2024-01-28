package com.nikonenko.driverservice.services;

import com.nikonenko.driverservice.dto.DriverRequest;
import com.nikonenko.driverservice.dto.DriverResponse;
import com.nikonenko.driverservice.dto.RatingDriverRequest;
import com.nikonenko.driverservice.exceptions.DriverNotFoundException;
import com.nikonenko.driverservice.exceptions.PhoneAlreadyExistsException;
import com.nikonenko.driverservice.exceptions.UsernameAlreadyExistsException;
import com.nikonenko.driverservice.exceptions.WrongPageableParameterException;
import com.nikonenko.driverservice.models.Driver;
import com.nikonenko.driverservice.models.RatingDriver;
import com.nikonenko.driverservice.repositories.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final ModelMapper modelMapper;
    @Override
    public Page<DriverResponse> getAllDrivers(int pageNumber, int pageSize, String sortField) {
        if (pageNumber < 0 || pageSize < 1) {
            throw new WrongPageableParameterException();
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortField));
        Page<Driver> page = driverRepository.findAll(pageable);
        return modelMapper.map(page, new TypeToken<Page<DriverResponse>>() {}.getType());
    }

    @Override
    public DriverResponse getDriverById(Long id) {
        return modelMapper.map(getDriver(id), DriverResponse.class);
    }

    @Override
    public DriverResponse createDriver(DriverRequest driverRequest) {
        checkDriverExists(driverRequest);
        Driver driver = modelMapper.map(driverRequest, Driver.class);
        Driver savedDriver = driverRepository.save(driver);
        return modelMapper.map(savedDriver, DriverResponse.class);
    }

    @Override
    public DriverResponse editDriver(Long id, DriverRequest driverRequest) {
        checkDriverExists(driverRequest);
        Driver editingDriver = getDriver(id);
        modelMapper.map(driverRequest, editingDriver);
        driverRepository.save(editingDriver);
        return modelMapper.map(editingDriver, DriverResponse.class);
    }

    @Override
    public void deleteDriver(Long id) {
        driverRepository.delete(getDriver(id));
    }

    @Override
    public DriverResponse createReview(Long id, RatingDriverRequest ratingRequest) {
        Driver driver = getDriver(id);

        RatingDriver addingRating = modelMapper.map(ratingRequest, RatingDriver.class);
        addingRating.setDriver(driver);

        Set<RatingDriver> modifiedRatingSet = driver.getRatingSet();
        modifiedRatingSet.add(addingRating);

        driver.setRatingSet(modifiedRatingSet);
        return modelMapper.map(driverRepository.save(driver), DriverResponse.class);
    }

    public Driver getDriver(Long id) {
        Optional<Driver> optionalDriver = driverRepository.findById(id);
        if (optionalDriver.isEmpty()) {
            throw new DriverNotFoundException();
        }
        return optionalDriver.get();
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
