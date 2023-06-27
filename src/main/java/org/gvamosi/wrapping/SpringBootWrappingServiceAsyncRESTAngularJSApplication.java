package org.gvamosi.wrapping;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
//@EnableAutoConfiguration(exclude = { HibernateJpaAutoConfiguration.class })
@EnableAsync
public class SpringBootWrappingServiceAsyncRESTAngularJSApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWrappingServiceAsyncRESTAngularJSApplication.class, args)/*.close()*/;
	}

	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(2);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("WrapText-");
		executor.initialize();
		return executor;
	}
}