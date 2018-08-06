package api.annotated;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import api.annotated.model.Product;
import api.annotated.repository.ProductRepository;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class AnnotatedEndpointsApplication {

  public static void main(String[] args) {
    SpringApplication.run(AnnotatedEndpointsApplication.class, args);
  }


  @Bean
  CommandLineRunner init(ProductRepository repository) {
    return args -> {
      Flux<Product> productFlux = Flux.just(
          new Product("Product 1", BigDecimal.TEN),
          new Product("Product 2", BigDecimal.valueOf(20)),
          new Product("Product 3", BigDecimal.TEN)
      ).flatMap(repository::save);

      productFlux.thenMany(repository.findAll()).subscribe(System.out::println);

    };
  }
}
