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
	      .csrf(csrf -> csrf.disable()) // of laten aan + CSRF hidden inputs in formulieren
	      .authorizeHttpRequests(auth -> auth
	        .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**").permitAll()
	        .requestMatchers("/admin/**").hasRole("ADMIN")
	        // Tickets alleen voor USER en expliciet NIET voor ADMIN
	        .requestMatchers("/tickets/**").hasRole("USER")
	        .anyRequest().authenticated()
	      )
	      .formLogin(login -> login
	        .loginPage("/login")
	        .defaultSuccessUrl("/", true)
	        .failureUrl("/login?error=Ongeldige+inloggegevens")
	        .permitAll()
	      )
	      .logout(logout -> logout
	        .logoutUrl("/logout")
	        .logoutSuccessUrl("/?success=Je+bent+afgemeld")
	        .invalidateHttpSession(true)
	        .deleteCookies("JSESSIONID")
	      );

	    return http.build();
	  }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
