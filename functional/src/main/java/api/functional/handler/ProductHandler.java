package api.functional.handler;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import api.functional.model.Product;
import api.functional.repository.ProductRepository;
import reactor.core.publisher.Mono;

@Component
public class ProductHandler {

  private ProductRepository repository;

  public ProductHandler(ProductRepository repository) {
    this.repository = repository;
  }

  public Mono<ServerResponse> findAllProducts(ServerRequest request) {
    return ServerResponse
        .ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(this.repository.findAll(), Product.class);
  }

  public Mono<ServerResponse> findProductById(ServerRequest request) {

    String id = request.pathVariable("id");

    return this.repository.findById(id)
        .flatMap(product -> ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(fromObject(product)))
        .switchIfEmpty(ServerResponse.notFound().build());
  }


  public Mono<ServerResponse> saveProduct(ServerRequest request) {

    Mono<Product> productMono = request.bodyToMono(Product.class);

    return productMono.flatMap(product -> ServerResponse
        .status(HttpStatus.CREATED)
        .contentType(MediaType.APPLICATION_JSON)
        .body(this.repository.save(product), Product.class));
  }


  public Mono<ServerResponse> updateProduct(ServerRequest request) {

    String id = request.pathVariable("id");
    Mono<Product> productMono = request.bodyToMono(Product.class);
    Mono<Product> dbProductMono = this.repository.findById(id);

    return productMono
        .zipWith(dbProductMono, (product, dbProduct) -> {
          dbProduct.setName(product.getName());
          dbProduct.setPrice(product.getPrice());
          return dbProduct;
        })
        .flatMap(product -> ServerResponse
            .status(HttpStatus.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(this.repository.save(product), Product.class))
        .switchIfEmpty(ServerResponse.notFound().build());
  }

  public Mono<ServerResponse> deleteProduct(ServerRequest request) {

    String id = request.pathVariable("id");
    Mono<Product> dbProductMono = this.repository.findById(id);

    return dbProductMono
        .flatMap(product -> ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .build(this.repository.delete(product)))
        .switchIfEmpty(ServerResponse.notFound().build());
  }

}
