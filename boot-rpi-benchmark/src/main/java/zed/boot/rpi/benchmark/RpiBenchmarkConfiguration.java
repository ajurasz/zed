package zed.boot.rpi.benchmark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RpiBenchmarkConfiguration {
    public static void main(String[] args) {
        new SpringApplication(RpiBenchmarkConfiguration.class).run(args);
    }
}
