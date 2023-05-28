package com.example.webshoporder.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http.authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/orders/buy").hasRole("ADMIN")
                        .requestMatchers("/orders","/orders/","/ordersitems", "/orders/**", "/ordersitems/**").permitAll())
                .httpBasic(withDefaults())
                .csrf().disable();
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("admin")
                .password("{bcrypt}$2a$10$kZDM73N77UesrsyAcrvBlOr4ZNbN/xUBX8lguK0h57YygDlePw.qG")
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}