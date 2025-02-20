package com.example.demo.config;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.models.Customer;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import reactor.core.publisher.Mono;

@Configuration
public class ApplicationConfiguration {
	
	@Autowired
    private WebClient.Builder webClientBuilder;
	
	@Autowired 
	private CircuitBreaker circuitBreaker;

	@Bean
	public UserDetailsService userDetailsService() {
		WebClient webClient = webClientBuilder.baseUrl("http://customer-service").build();
		return username -> {
            return Mono.defer(() -> 
                webClient.get()
                    .uri("/customers/username/" + username)
                    .retrieve()
                    .bodyToMono(Customer.class)
            )
            .transform(CircuitBreakerOperator.of(circuitBreaker))
            .onErrorResume(throwable -> {
                System.err.println("error circuit breaker: " + throwable.toString());
                return Mono.empty();
            })
            .block();
        };
	}


    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
}
