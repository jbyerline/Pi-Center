package com.jbyerline.stats

import com.jbyerline.stats.repositories.CredentialsRepository
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = CredentialsRepository)
class StatsApplication {

	static void main(String[] args) {
		SpringApplication.run(StatsApplication, args)
	}

}
