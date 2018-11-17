package pl.edu.pk.fmi.zjadlbym.co;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "pl.edu.pk.fmi.zjadlbym.co.config")

public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
