package com.nikonenko.driverservice.integration;

import org.springframework.test.context.jdbc.Sql;

@Sql(
        scripts = {
                "classpath:sql/delete-data.sql",
                "classpath:sql/insert-data.sql"
        }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public class DriverServiceTest {
}
