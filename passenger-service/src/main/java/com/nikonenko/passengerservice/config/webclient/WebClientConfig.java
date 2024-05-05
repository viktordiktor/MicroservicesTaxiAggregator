package com.nikonenko.passengerservice.config.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@LoadBalancerClients({
        @LoadBalancerClient(name = "RIDE-SERVICE", configuration = LoadBalancerConfiguration.class),
        @LoadBalancerClient(name = "PAYMENT-SERVICE", configuration = LoadBalancerConfiguration.class),
})
public class WebClientConfig {
    @Value("${webclient.ride-service}")
    private String rideBaseUrl;

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientUserService() {
        return WebClient
                .builder()
                .baseUrl(rideBaseUrl);
    }
}
