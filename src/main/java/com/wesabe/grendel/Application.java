package com.wesabe.grendel;

import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.ansi.AnsiElement;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.PrintStream;

/**
 * Created by netmask on 3/30/14.
 */
@Configuration
@ComponentScan
@PropertySource("classpath:application.properties")
@EnableAutoConfiguration
public class Application  {
    public static String[] BANNER = {
                    "╔═╗┬─┐┌─┐┌┐┌┌┬┐┌─┐┬  ",
                    "║ ╦├┬┘├┤ │││ ││├┤ │  ",
                    "╚═╝┴└─└─┘┘└┘─┴┘└─┘┴─┘",
                    "                     ",
                    "Spring Boot, Spring DI container"
    };


    public static void main(String[] args) {

        writeBanner(System.out);

        SpringApplication application = new SpringApplication(Application.class);
        application.setShowBanner(false);
        application.run(args);
    }

    @Bean
    public ServletRegistrationBean jerseyServlet() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new ServletContainer(), "/*");
        registration.addInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS, JerseyConfig.class.getName());
        return registration;
    }


    public static void writeBanner(PrintStream printStream) {
        for (String line : BANNER) {
            printStream.println(line);
        }
        printStream.println(AnsiOutput.toString(AnsiElement.GREEN, AnsiElement.FAINT, "0.4.1-netmask-SNAPSHOT"));

        printStream.println();
    }
}


