package com.nikonenko.paymentservice.utils;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class PaymentCoefficientUtil {
    private static final BigDecimal DEFAULT_COEFFICIENT = BigDecimal.valueOf(1);
    private static final BigDecimal FRIDAY_COEFFICIENT = BigDecimal.valueOf(1.6);
    private static final BigDecimal SATURDAY_COEFFICIENT = BigDecimal.valueOf(1.4);
    private static final BigDecimal SUNDAY_COEFFICIENT = BigDecimal.valueOf(1.2);
    private static final BigDecimal MORNING_COEFFICIENT = BigDecimal.valueOf(1.4);
    private static final BigDecimal EVENING_COEFFICIENT = BigDecimal.valueOf(1.5);
    private static final BigDecimal NIGHT_COEFFICIENT = BigDecimal.valueOf(1.3);

    /**
     * The method returns a coefficient, depending on the time of ordering
     * MONDAY-THURSDAY -- 1
     * FRIDAY -- 1.6
     * SATURDAY -- 1.4
     * SUNDAY -- 1.2
     *
     * @param localDateTime DateTime of ordering
     * @return Day Coefficient
     */
    public BigDecimal getDayCoefficient(LocalDateTime localDateTime) {
        DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();
        return switch (dayOfWeek) {
            case MONDAY, TUESDAY, WEDNESDAY, THURSDAY -> DEFAULT_COEFFICIENT;
            case FRIDAY -> FRIDAY_COEFFICIENT;
            case SATURDAY -> SATURDAY_COEFFICIENT;
            case SUNDAY -> SUNDAY_COEFFICIENT;
        };
    }

    /**
     * The method returns a coefficient, depending on the time of ordering
     * 07:00-10:00 -- 1.4
     * 10:00-16:00 -- 1.0
     * 16:00-21:00 -- 1.5
     * 21:00-07:00 -- 1.3
     *
     * @param localDateTime DateTime of ordering
     * @return Time Coefficient
     */
    public BigDecimal getTimeCoefficient(LocalDateTime localDateTime) {
        LocalTime time = localDateTime.toLocalTime();
        if (time.isAfter(LocalTime.of(7, 0)) && time.isBefore(LocalTime.of(10, 0))) {
            return MORNING_COEFFICIENT;
        }
        if (time.isAfter(LocalTime.of(10, 0)) && time.isBefore(LocalTime.of(16, 0))) {
            return DEFAULT_COEFFICIENT;
        }
        if (time.isAfter(LocalTime.of(16, 0)) && time.isBefore(LocalTime.of(21, 0))) {
            return EVENING_COEFFICIENT;
        } else {
            return NIGHT_COEFFICIENT;
        }
    }
}
