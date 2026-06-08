package cn.jason;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "cn.jason.repository")
public class FaqApplication {
    public static void main(String[] args) {
        SpringApplication.run(FaqApplication.class, args);
    }
}
