package com.nikonenko.driverservice.services;

import com.nikonenko.driverservice.exceptions.WrongPageableParameterException;
import com.nikonenko.driverservice.kafka.producer.DriverReviewRequestProducer;
import com.nikonenko.driverservice.kafka.producer.RideStatusRequestProducer;
import com.nikonenko.driverservice.repositories.DriverRepository;
import com.nikonenko.driverservice.services.impl.DriverServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static com.nikonenko.driverservice.utils.DriverServiceTestUtil.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private RideStatusRequestProducer rideStatusRequestProducer;
    @Mock
    private DriverReviewRequestProducer driverReviewRequestProducer;
    @InjectMocks
    private DriverServiceImpl driverService;

    @Test
    void getDriversWhenPageParamsIsInvalid() {
        assertThrows(
                WrongPageableParameterException.class,
                () -> driverService.getAllDrivers(INVALID_PAGE, INVALID_PAGE_SIZE, INVALID_PAGE_SORT)
        );
    }
}
