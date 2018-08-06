package api.annotated.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import api.annotated.model.Product;
import api.annotated.repository.ProductRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/products")
public class ProductController {

  private ProductRepository repository;

  public ProductController(ProductRepository repository) {
    this.repository = repository;
  }

  @GetMapping
  public Flux<Product> findAllProducts() {
    return this.repository.findAll();
  }

  @GetMapping("/{id}")
  public Mono<ResponseEntity<Product>> findProductById(@PathVariable("id") String id) {
    return this.repository.findById(id)
        .map(product -> ResponseEntity.ok().body(product))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Product> saveProduct(@RequestBody Product product) {
    return this.repository.save(product);
  }

  @PutMapping("/{id}")
  public Mono<ResponseEntity<Product>> updateProduct(@PathVariable("id") String id,
      @RequestBody Product product) {

    return this.repository.findById(id)
        .flatMap(dbProduct -> {
          dbProduct.setName(product.getName());
          dbProduct.setPrice(product.getPrice());
          return this.repository.save(dbProduct);
        })
        .map(dbProduct -> ResponseEntity.ok().body(dbProduct))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable("id") String id) {
    return this.repository.findById(id)
        .flatMap(dbProduct -> this.repository.delete(dbProduct)
            .then(Mono.just(ResponseEntity.ok().<Void>build())))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

}
