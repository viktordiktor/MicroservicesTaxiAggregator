package com.nikonenko.passengerservice.utils;

import com.nikonenko.passengerservice.exceptions.AccessDeniedByPassengerException;
import com.nikonenko.passengerservice.exceptions.BadRequestByPassengerException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SecurityUtil {
    public String ID = "id";
    public String USERNAME = "username";
    public String EMAIL = "email";
    public String PHONE = "phone";
    public String ACTUATOR_PATH = "/actuator/**";
    public String RESOURCE_ROLES = "roles";
    public String RESOURCE_ACCESS = "resource_access";
    public String ROLE_PREFIX = "ROLE_";
    public String SUB_UUID = "sub";
    public String PREFERRED_USERNAME = "preferred_username";
    public String ACCESS_DENIED = "Access Denied";

    public void checkException(String exceptionMessage) {
        if (exceptionMessage != null) {
            throw exceptionMessage.contains(ACCESS_DENIED) ?
                    new AccessDeniedByPassengerException(exceptionMessage) :
                    new BadRequestByPassengerException(exceptionMessage);
        }
    }
}
