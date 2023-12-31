package com.example.Grupp9.config;

import com.example.Grupp9.JwtConfig.JwtAuthenticationFilter;
import com.example.Grupp9.auth.CustomUserDetailsService;
import com.example.Grupp9.exception.DelegatingAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService CustomUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final HandlerExceptionResolver handlerExceptionResolver;

    public SecurityConfig(CustomUserDetailsService CustomUserDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter, HandlerExceptionResolver handlerExceptionResolver) {
        this.CustomUserDetailsService = CustomUserDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }


    @Bean
    public AuthenticationProvider authenticationProvider(@Qualifier("customUserDetailsService") UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(withDefaults())

                .authorizeHttpRequests(configure -> configure
                        .requestMatchers(HttpMethod.POST,"/api/register","/api/login","/register-web","/login-web", "/api/booking","/api/book-appointment","/api/create-booking","/profile","/home").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tyres","/registration","/login","/api/available-tyres", "/api/bookings","/css/**","css/js/**", "/static/css/js/**", "/images/**","/profile","/home").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/tyres").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/bookings","/api/allusers").hasAuthority("ADMIN")
//                        .requestMatchers(HttpMethod.POST, "/api/booking/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/booking/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/booking/**").permitAll()



                        .anyRequest().authenticated())
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider(CustomUserDetailsService, passwordEncoder()))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(authenticationEntryPoint())
                );

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new DelegatingAuthenticationEntryPoint(handlerExceptionResolver);
    }

}
