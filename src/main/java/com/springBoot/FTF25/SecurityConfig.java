package com.springBoot.FTF25;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {
	@Autowired
    private UserDetailsService userDetailsService;
	
	@Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	  http
	    .authorizeHttpRequests(auth -> auth
	      .requestMatchers("/", "/festivals/**", "/css/**", "/js/**", "/images/**", "/login", "/error").permitAll()
	      .requestMatchers("/admin/**").hasRole("ADMIN")
	      // User-only functionaliteit
	      .requestMatchers("/tickets/**", "/reviews/**").hasRole("USER")
	      .anyRequest().authenticated()
	    )
	    .formLogin(form -> form
	      .loginPage("/login").permitAll()
	      .defaultSuccessUrl("/", true)
	      .failureUrl("/login?error")
	    )
	    .logout(logout -> logout
	      .logoutUrl("/logout")
	      .logoutSuccessUrl("/?logout")
	      .invalidateHttpSession(true)
	      .deleteCookies("JSESSIONID")
	    )
	    .exceptionHandling(e -> e.accessDeniedPage("/access-denied"))
	    .csrf(Customizer.withDefaults());
	  return http.build();
	}

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
