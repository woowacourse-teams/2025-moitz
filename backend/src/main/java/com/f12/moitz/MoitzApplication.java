package com.f12.moitz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
@EnableMongoAuditing
public class MoitzApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoitzApplication.class, args);
	}

}

