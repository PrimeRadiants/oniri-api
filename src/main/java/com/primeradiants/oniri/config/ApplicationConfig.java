package com.primeradiants.oniri.config;

import java.io.IOException;
import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
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
	
	@Bean
    public JavaMailSender mailSender() throws IOException {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
				
		Resource resource = new ClassPathResource("/mailserver.properties");
		Properties properties = PropertiesLoaderUtils.loadProperties(resource);
		mailSender.setJavaMailProperties(properties);
		
		mailSender.setHost(properties.getProperty("mail.smtp.host"));
		mailSender.setPort(Integer.parseInt(properties.getProperty("mail.smtp.port")));
		mailSender.setProtocol(properties.getProperty("mail.smtp.protocol"));
		mailSender.setUsername(properties.getProperty("mail.smtp.username"));
		mailSender.setPassword(properties.getProperty("mail.smtp.password"));
		
		return mailSender;
    }
	
	@Bean
	public FreeMarkerConfigurationFactoryBean freeMarkerConfiguration() {
		FreeMarkerConfigurationFactoryBean configuration = new FreeMarkerConfigurationFactoryBean();
		
		configuration.setTemplateLoaderPath("/WEB-INF/templates");
		Resource resource = new ClassPathResource("/freemarker.properties");
		configuration.setConfigLocation(resource);;
		
		return configuration;
	}
}
