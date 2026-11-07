package com.erp;

import java.util.TimeZone;

import org.egovframe.boot.access.EgovAccessConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
// Spring Boot 실행 진입점
@SpringBootApplication
@Import(EgovAccessConfiguration.class)
public class EgovBootApplication {
	public static void main(String[] args) {
		// 한국 표준시 초기화
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		SpringApplication.run(EgovBootApplication.class, args);
	}

}
