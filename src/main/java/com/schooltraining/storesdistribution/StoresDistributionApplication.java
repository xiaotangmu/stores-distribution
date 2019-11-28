package com.schooltraining.storesdistribution;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan(value = "com.schooltraining.storesdistribution.mapper")
@SpringBootApplication
public class StoresDistributionApplication {

	public static void main(String[] args) {
		SpringApplication.run(StoresDistributionApplication.class, args);
	}

}
