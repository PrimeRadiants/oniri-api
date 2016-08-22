package com.primeradiants.oniri.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.primeradiants.oniri.config.security.ApplicationSecurityConfig;

@EnableWebMvc
@Configuration
@ComponentScan(basePackages = "com.primeradiants.oniri.*")
@Import({ ApplicationSecurityConfig.class })
public class ApplicationConfig {

	@Bean
    public MultipartResolver multipartResolver() {
		return new CommonsMultipartResolver();
    }
	
}
