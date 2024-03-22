package com.nikonenko.rideservice.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SecurityList {
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
}
