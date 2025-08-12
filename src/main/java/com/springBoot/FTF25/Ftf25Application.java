package com.springBoot.FTF25;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import java.util.Locale;

@SpringBootApplication(
        scanBasePackages = {
                "com.springBoot.FTF25", // MVC & REST controllers
                "service",              // @Service-implementaties
                "utils"                 // o.a. SystemTimeProvider
        }
)
@EnableJpaRepositories(basePackages = { "repository" })
@EntityScan(basePackages = { "domain" })
public class Ftf25Application implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(Ftf25Application.class, args);
    }

    /** View shortcuts (geen controller nodig voor /login & /error). */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Als je liever / naar de home laat gaan, laat dit weg.
        // registry.addRedirectViewController("/", "/festivals");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/error").setViewName("error");
    }

    /** Locale via cookie (zoals in je voorbeeld), default NL. */
    @Bean
    LocaleResolver localeResolver() {
        CookieLocaleResolver r = new CookieLocaleResolver();
        r.setDefaultLocale(new Locale("nl"));
        return r;
    }
}
