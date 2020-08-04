package com.slj.util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class SljUtilApplication {
	private static Logger logger = LoggerFactory.getLogger(SljUtilApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(SljUtilApplication.class, args);
		logger.warn("log4j2");
	}

}
