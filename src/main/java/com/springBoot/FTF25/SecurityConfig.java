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
	    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	        http
	            .csrf(csrf -> csrf.ignoringRequestMatchers(
	                // REST endpoints die forms aanroepen mogen CSRF negeren als je pure fetch gebruikt;
	                // gebruik je normale <form>, laat CSRF dan AAN en stuur de token mee in het formulier.
	                "/api/**"
	            ))
	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/webjars/**", "/login", "/logout", "/festivals", "/api/festivals/**", "/api/reviews/**").permitAll()
	                .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
	                .requestMatchers("/my/**").hasRole("USER")
	                .requestMatchers("/festivals/*/reviews/**").hasRole("USER")
	                .anyRequest().authenticated()
	            )
	            .formLogin(form -> form
	                .loginPage("/login").permitAll()
	                .defaultSuccessUrl("/", true)
	                .failureUrl("/login?error")
	            )
	            .logout(l -> l
	                .logoutUrl("/logout")
	                .logoutSuccessUrl("/?logout")
	                .invalidateHttpSession(true)
	                .deleteCookies("JSESSIONID")
	                .permitAll()
	            );
	        return http.build();
	    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
