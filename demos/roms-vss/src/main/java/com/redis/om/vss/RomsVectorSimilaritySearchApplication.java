package com.redis.om.vss;

import com.google.common.io.Files;
import com.redis.om.spring.annotations.EnableRedisEnhancedRepositories;
import com.redis.om.vss.domain.Product;
import com.redis.om.vss.repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

@SpringBootApplication
@EnableRedisEnhancedRepositories(basePackages = "com.redis.om.vss.*")
public class RomsVectorSimilaritySearchApplication {
  Logger logger = LoggerFactory.getLogger(RomsVectorSimilaritySearchApplication.class);

  @Value("${com.redis.om.vss.useLocalImages}")
  private boolean useLocalImages;

  @Value("${com.redis.om.vss.maxLines}")
  private long maxLines;

  @Bean
  CommandLineRunner loadAndVectorizeProductData(ProductRepository repository, @Value("classpath:/data/styles.csv") File dataFile) {
    return args -> {
      if (repository.count() == 0) {
        logger.info("⚙️ Loading products...");
        List<Product> data = Files //
            .readLines(dataFile, StandardCharsets.UTF_8) //
            .stream() //
            .limit(maxLines) //
            .map(line -> Product.fromCSV(line, useLocalImages)) //
            .toList();
        repository.saveAll(data);
      }
    };
  }

	public static void main(String[] args) {
		SpringApplication.run(RomsVectorSimilaritySearchApplication.class, args);
	}

}