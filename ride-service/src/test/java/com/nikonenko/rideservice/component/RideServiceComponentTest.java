package com.nikonenko.rideservice.component;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "classpath:features",
        glue = "com/nikonenko/rideservice/component",
        snippets = CucumberOptions.SnippetType.UNDERSCORE
)
public class RideServiceComponentTest {
}
