package com.wesabe.grendel;

import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import static com.wesabe.grendel.util.Banner.writeBanner;

/**
 * Created by netmask on 3/30/14.
 */
@Configuration
@ComponentScan
@PropertySource("classpath:application.properties")
@EnableAutoConfiguration
@EnableTransactionManagement
public class Application  {

    public static void main(String[] args) {
        writeBanner(System.out);
        SpringApplication application = new SpringApplication(Application.class);
        application.setShowBanner(false);
        application.run(args);
    }

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Bean
    public ServletRegistrationBean jerseyServlet() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new ServletContainer(), "/*");
        registration.addInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS, JerseyConfig.class.getName());
        return registration;
    }

    @Bean
    public JpaTransactionManager transactionManager(){
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);;
        return transactionManager;
    }
}


