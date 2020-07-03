package org.dell.kube.pages;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class PageApplication {

	@Bean
	public IPageRepository iPageRepository(DataSource dataSource){
		return new MySqlPageRepository(dataSource);
	}
	public static void main(String[] args) {
		SpringApplication.run(PageApplication.class, args);
	}
}
