package top.hunfan.mail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages="top.hunfan.mail.**")
public class MailMicroServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MailMicroServiceApplication.class, args);
    }

}
