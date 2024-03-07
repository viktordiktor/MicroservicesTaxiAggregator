package com.nikonenko.driverservice.component;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "classpath:features",
        glue = "com/nikonenko/driverservice/component",
        snippets = CucumberOptions.SnippetType.UNDERSCORE
)
public class DriverServiceTest {
}
