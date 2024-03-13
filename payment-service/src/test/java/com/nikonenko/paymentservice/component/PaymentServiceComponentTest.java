package com.nikonenko.paymentservice.component;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "classpath:features",
        glue = "com/nikonenko/paymentservice/component",
        snippets = CucumberOptions.SnippetType.UNDERSCORE
)
public class PaymentServiceComponentTest {
}
