package com.zg.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
//@ServletComponentScan({"com.github.zhou6ang.mvc.servlet","com.zg.testing"})
@SpringBootApplication
public class BootStrapApp {
	private static final Logger log = LogManager.getLogger(BootStrapApp.class);

	public static void main(String[] args) {
		log.info("Starting BootStrapApp ...");
		SpringApplication.run(BootStrapApp.class, args);
		log.info("Starting BootStrapApp Done.");
	}

	
}
