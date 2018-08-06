package api.functional;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import api.functional.handler.ProductHandler;
import api.functional.model.Product;
import api.functional.repository.ProductRepository;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class FunctionalEndpointsApplication {

  public static void main(String[] args) {
    SpringApplication.run(FunctionalEndpointsApplication.class, args);
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

  @Bean
  RouterFunction<ServerResponse> routes(ProductHandler productHandler) {
    return RouterFunctions.nest(

        RequestPredicates.path("/products")
            .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)
                .or(RequestPredicates.contentType(MediaType.APPLICATION_JSON))),

        RouterFunctions.route(RequestPredicates.GET("/"), productHandler::findAllProducts)
            .andRoute(RequestPredicates.method(HttpMethod.POST), productHandler::saveProduct)
            .andNest(
              RequestPredicates.path("/{id}"),
              RouterFunctions.route(RequestPredicates.method(HttpMethod.GET), productHandler::findProductById)
                .andRoute(RequestPredicates.method(HttpMethod.PUT), productHandler::updateProduct)
                .andRoute(RequestPredicates.method(HttpMethod.DELETE), productHandler::deleteProduct))
    );
  }
}
