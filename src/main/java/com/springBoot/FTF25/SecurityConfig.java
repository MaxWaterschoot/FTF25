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
            // REST-API kan CSRF negeren
            .csrf(csrf -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/api/**")))
            .authorizeHttpRequests(auth -> auth
                // Publieke pagina's & statics
                .requestMatchers(
                        "/", "/home", "/login",
                        "/festivals", "/festivals/**",
                        "/css/**", "/js/**", "/images/**", "/webjars/**", "/i18n/**"
                ).permitAll()
                // Admin-only
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // REST publiek (indien je endpoints hebt die geen auth vereisen)
                .requestMatchers("/api/public/**").permitAll()
                // De rest: ingelogd
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/", true)
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
            )
            // Handige basic auth voor snelle REST-tests
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
