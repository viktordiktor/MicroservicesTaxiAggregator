package com.nikonenko.passengerservice.component;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "classpath:features",
        glue = "com/nikonenko/passengerservice/component",
        snippets = CucumberOptions.SnippetType.UNDERSCORE
)
public class PassengerServiceTest {
}
