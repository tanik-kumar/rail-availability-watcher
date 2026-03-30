package com.example.railwatcher.config;

import java.util.Map;
import org.h2.server.web.JakartaWebServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("standalone")
public class StandaloneConsoleConfig {

    @Bean
    ServletRegistrationBean<JakartaWebServlet> h2ConsoleServlet() {
        ServletRegistrationBean<JakartaWebServlet> registration =
                new ServletRegistrationBean<>(new JakartaWebServlet(), "/h2-console/*");
        registration.setName("H2Console");
        registration.setLoadOnStartup(1);
        registration.setInitParameters(Map.of(
                "webAllowOthers", "false",
                "trace", "false"
        ));
        return registration;
    }

    @Bean
    WebMvcConfigurer h2ConsoleRedirectConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addRedirectViewController("/h2-console", "/h2-console/");
            }
        };
    }
}
