package com.nonestack.apiclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@RestController
public class ProductResourceConsumer {

    private final RestClient restClient = RestClient.builder()
        .baseUrl("http://localhost:8081/api")
        .build();

    @GetMapping("/findAllProducts")
    public List<Product> findAllProducts() {
        ResponseEntity<List<Product>> response = restClient.get()
            .uri("/products")
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                throw new ResponseStatusException(res.getStatusCode(), res.getStatusText());
            })
            .toEntity(new ParameterizedTypeReference<>() {
            });

        return response.getBody();
    }

    @GetMapping("/findProduct/{productId}")
    public Product findProductById(@PathVariable Long productId) {
        ObjectMapper mapper = new ObjectMapper();
        return restClient.get()
            .uri("/products/{productId}", productId)
            .exchange((req,res) -> {
                if (res.getStatusCode().is2xxSuccessful()) {
                    return  mapper.readValue(res.getBody(), Product.class);
                }
                if (res.getStatusCode().is4xxClientError()) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "the product does not exist");
                }
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "something went wrong");
            });
    }

    @PostMapping("/storeProducts")
    public Product createProduct(@RequestBody Product product) {

        ResponseEntity<Product> response = restClient.post()
            .uri("/products")
            .contentType(APPLICATION_JSON)
            .body(product)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                throw new ResponseStatusException(res.getStatusCode(), res.getStatusText());
            })
            .toEntity(Product.class);

        return response.getBody();
    }

    @DeleteMapping("/deleteProduct/{productId}")
    public Product deleteProductById(@PathVariable Long productId) {
        ResponseEntity<Product> response = restClient.delete()
            .uri("/products/{productId}", productId)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                throw new ResponseStatusException(res.getStatusCode(), res.getStatusText());
            })
            .toEntity(Product.class);

        return response.getBody();
    }

}
