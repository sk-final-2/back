package com.backend.recruitAi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class 	RecruitAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecruitAiApplication.class, args);
	}

}