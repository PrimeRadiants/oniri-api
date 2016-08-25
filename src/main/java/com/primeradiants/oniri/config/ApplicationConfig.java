package com.primeradiants.oniri.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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
    public JavaMailSender mailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("SSL0.OVH.NET");
		mailSender.setPort(465);
		mailSender.setProtocol("smtps");
		mailSender.setUsername("noreply-test@prime-radiants.com");
		mailSender.setPassword("A12345678");
		
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.quitwait", "true");
		mailSender.setJavaMailProperties(properties);
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
