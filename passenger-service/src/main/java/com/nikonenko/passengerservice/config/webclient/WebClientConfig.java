package com.nikonenko.passengerservice.config.webclient;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
    @Value("${webclient.ride-service}")
    private String rideBaseUrl;
    private final LoadBalancerClient loadBalancerClient;

    @Bean
    @LoadBalanced
    public WebClient webClient() {
        ServiceInstance instance = loadBalancerClient.choose(rideBaseUrl);
        if (instance != null) {
            String baseUrl = String.format("http://%s:%s", instance.getHost(), instance.getPort());
            return WebClient.builder()
                    .baseUrl(baseUrl)
                    .build();
        } else {
            throw new RuntimeException("Service RIDE-SERVICE not found");
        }
    }
}
