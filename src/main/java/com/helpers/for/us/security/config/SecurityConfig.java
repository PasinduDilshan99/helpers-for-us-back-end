package com.you.and.me.security.config;

import com.you.and.me.filter.JwtFilter;
import com.you.and.me.util.AuthorizeEndPoints;
import com.you.and.me.util.PublicEndpoints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtFilter jwtFilter;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService,JwtFilter jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/actuator/**").permitAll()
                        // DESTINATIONS
                        .requestMatchers("/v0/api/destination/add-destination").hasAuthority("DESTINATION_CREATE")
                        .requestMatchers("/v0/api/destination/update-destination").hasAuthority("DESTINATION_UPDATE")
                        .requestMatchers("/v0/api/destination/terminate-destination").hasAuthority("DESTINATION_TERMINATE")
                        // ACTIVITIES
                        .requestMatchers("/api/v0/activities/add-activity").hasAuthority("ACTIVITY_CREATE")
                        .requestMatchers("/api/v0/activities/update-activity").hasAuthority("ACTIVITY_UPDATE")
                        .requestMatchers("/api/v0/activities/terminate-activity").hasAuthority("ACTIVITY_TERMINATE")
                        // TOURS
                        .requestMatchers("/v0/api/tour/add-tour").hasAuthority("TOUR_CREATE")
                        .requestMatchers("/v0/api/tour/update-tour").hasAuthority("TOUR_UPDATE")
                        .requestMatchers("/v0/api/tour/terminate-tour").hasAuthority("TOUR_TERMINATE")
                        // PACKAGES
                        .requestMatchers("/v0/api/package/add-package").hasAuthority("PACKAGE_CREATE")
                        .requestMatchers("/v0/api/package/update-package").hasAuthority("PACKAGE_UPDATE")
                        .requestMatchers("/v0/api/package/terminate-package").hasAuthority("PACKAGE_TERMINATE")

                                .requestMatchers(PublicEndpoints.ENDPOINTS).permitAll()
                        .requestMatchers(AuthorizeEndPoints.ENDPOINTS).permitAll()
                        .anyRequest().permitAll()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        authenticationProvider.setUserDetailsService(userDetailsService);
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3400","http://localhost:3401"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type","Cookie"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
