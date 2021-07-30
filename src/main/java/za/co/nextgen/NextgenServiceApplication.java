package za.co.nextgen;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@EnableProcessApplication
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class NextgenServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NextgenServiceApplication.class, args);
    }
}
