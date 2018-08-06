package api.annotated.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import api.annotated.model.Product;

@Repository
public interface ProductRepository extends ReactiveMongoRepository<Product, String> {

}
