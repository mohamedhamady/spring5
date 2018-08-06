package api.annotated.controller;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import api.annotated.model.Product;
import api.annotated.repository.ProductRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ProductControllerTest {

  private WebTestClient client;

  @Autowired
  private ProductRepository repository;

  @Autowired
  private ApplicationContext context;

  private List<Product> expectedProducts;

  @BeforeEach
  void beforeEach() {

    this.client = WebTestClient.bindToApplicationContext(context).build();

    this.expectedProducts = this.repository.findAll().collectList().block();
  }

  @Test
  void testGetAllProduct() {

    this.client.get()
        .uri("/products")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(Product.class)
        .isEqualTo(expectedProducts);

  }

}
