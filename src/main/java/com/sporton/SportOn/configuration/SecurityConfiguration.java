package com.sporton.SportOn.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.sporton.SportOn.entity.Permission.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

  private final JWTAuthenticationFilter jwtAuthFilter;
  private final AuthenticationProvider authenticationProvider;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf()
        .disable()
            .cors().disable()
        .authorizeHttpRequests()
        .requestMatchers(
                "/api/v1/auth/**",
                "/demo/**",
                "/api/v1/authenticate/**",
                "/api/v1/authenticate/requestForgetPasswordOTP",
                "/api/v1/venue/get",
                "/api/v1/venue/getSingleVenue/**",
                "/api/v1/venue/get",
                "/api/v1/venue/nearByVenues",
                "/api/v1/venue/popularVenues",
                "/api/v1/venue/getSingleVenue/**",
                "/api/v1/court/getCourtsByVenueId/**",
                "/api/v1/venue/search",
                "/api/v1/venue/saveSearchedVenue",
                "/api/v1/venue/getSavedSearchVenues"

        )
          .permitAll()


        .requestMatchers(HttpMethod.POST, "/api/v1/region/create").hasAnyAuthority(ADMIN_CREATE.getPermission())
        .requestMatchers(HttpMethod.POST, "/api/v1/venue/create").hasAnyAuthority(PROVIDER_CREATE.getPermission(), ADMIN_CREATE.getPermission())
        .requestMatchers(HttpMethod.PUT, "/api/v1/venue/update/**").hasAnyAuthority(PROVIDER_UPDATE.getPermission(), ADMIN_UPDATE.getPermission())
        .requestMatchers(HttpMethod.GET, "/api/v1/venue/isVenueFavoritedByUser/**").hasAnyAuthority(PROVIDER_READ.getPermission(), ADMIN_READ.getPermission(), USER_READ.getPermission())


//            .requestMatchers(HttpMethod.GET, "/api/v1/venue/search").hasAnyAuthority(PROVIDER_READ.getPermission(), ADMIN_READ.getPermission(), USER_READ.getPermission())
            .requestMatchers(HttpMethod.DELETE, "/api/v1/venue/delete/**").hasAnyAuthority(PROVIDER_DELETE.getPermission(), ADMIN_DELETE.getPermission())
        .requestMatchers(HttpMethod.POST, "/api/v1/court/create").hasAnyAuthority(PROVIDER_CREATE.getPermission(), ADMIN_CREATE.getPermission())
        .requestMatchers(HttpMethod.PUT, "/api/v1/court/update/**").hasAnyAuthority(PROVIDER_UPDATE.getPermission(), ADMIN_UPDATE.getPermission())
        .requestMatchers(HttpMethod.DELETE, "/api/v1/court/delete/**").hasAnyAuthority(PROVIDER_DELETE.getPermission(), ADMIN_DELETE.getPermission())
        .requestMatchers(HttpMethod.POST, "/api/v1/timeslot/create").hasAnyAuthority(PROVIDER_CREATE.getPermission(), ADMIN_CREATE.getPermission())
        .requestMatchers(HttpMethod.PUT, "/api/v1/timeslot/update/**").hasAnyAuthority(PROVIDER_UPDATE.getPermission(), ADMIN_UPDATE.getPermission())
        .requestMatchers(HttpMethod.DELETE, "/api/v1/timeslot/delete/**").hasAnyAuthority(PROVIDER_DELETE.getPermission(), ADMIN_DELETE.getPermission())
        .requestMatchers(HttpMethod.GET, "/api/v1/timeslot/getTimeSlotByCourtId/**").hasAnyAuthority(PROVIDER_READ.getPermission(), ADMIN_DELETE.getPermission(), USER_READ.getPermission())
//        .requestMatchers(HttpMethod.GET, "/api/v1/booking/get").hasAnyAuthority(PROVIDER_READ.getPermission(), ADMIN_READ.getPermission())
        .requestMatchers(HttpMethod.PUT, "/api/v1/booking/update/**").hasAnyAuthority(PROVIDER_UPDATE.getPermission(), ADMIN_UPDATE.getPermission())
        .requestMatchers(HttpMethod.PUT, "/api/v1/booking/accept/**").hasAnyAuthority(PROVIDER_UPDATE.getPermission(), ADMIN_UPDATE.getPermission())
        .requestMatchers(HttpMethod.GET, "/api/v1/booking/getBookingByProviderId").hasAnyAuthority(PROVIDER_READ.getPermission(), ADMIN_READ.getPermission())
        .requestMatchers(HttpMethod.GET, "/api/v1/booking/getBookingByCustomerId").hasAnyAuthority(PROVIDER_READ.getPermission(), ADMIN_READ.getPermission(), USER_READ.getPermission())
        .requestMatchers(HttpMethod.POST, "/api/v1/facility/create").hasAnyAuthority(PROVIDER_CREATE.getPermission(), ADMIN_CREATE.getPermission())
        .requestMatchers(HttpMethod.PUT, "/api/v1/facility/update").hasAnyAuthority(PROVIDER_UPDATE.getPermission(), ADMIN_UPDATE.getPermission())
        .requestMatchers(HttpMethod.DELETE, "/api/v1/facility/delete").hasAnyAuthority(PROVIDER_DELETE.getPermission(), ADMIN_DELETE.getPermission())

        .requestMatchers(HttpMethod.PUT, "/api/v1/profile/addOrRemoveFavoriteVenueToUser/**").hasAnyAuthority(PROVIDER_UPDATE.getPermission(), ADMIN_UPDATE.getPermission(), USER_UPDATE.getPermission())



            .anyRequest()
        .authenticated()
        .and()
          .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
    ;
    return http.build();
  }
}
