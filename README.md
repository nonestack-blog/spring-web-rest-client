![display](https://repository-images.githubusercontent.com/711627154/862262a3-28a6-4111-b272-22ef96f6298f)

Spring Framework **6.1** introduces `RestClient` a new **Fluent API** to make **synchronous HTTP requests**

## <a name="what-you-will-build" aria-label="what-you-will-build" id="what-you-will-build" href="#what-you-will-build"></a>What You Will build
You will build a Spring Boot web application that consumes and external api with RestClient.

## <a name="what-you-need" aria-label="what-you-need" id="what-you-need" href="#what-you-need"></a>What You Need
- A favorite text editor or IDE
- JDK 1.8 or later
- Gradle 4+ or Maven 3.2+

## <a name="setup-project-with-spring-initializr" aria-label="setup-project-with-spring-initializr" id="setup-project-with-spring-initializr" href="#setup-project-with-spring-initializr"></a>Setup Project With Spring Initializr

- Navigate to [Spring initializr](https://start.spring.io).
- Define the project name example: `spring-web-rest-client`
- Choose Project **Maven** and the language  **Java**.
- Choose Your **Java** version ex: **17**
- Click add dependencies and select:
    - Spring Web
- Make sure the Spring Version at least is : **3.2**
- Click Generate.

Unzip the Downloaded Zip and open the Project using your favorite text editor or IDE

## <a name="overview" aria-label="overview" id="overview" href="#overview"></a>Overview

In this tutorial, we’re going to demonstrate a range of operations where and How the `RestClient` can be used.


## <a name="instantiate-the-rest-client" aria-label="instantiate-the-rest-client" id="instantiate-the-rest-client" href="#instantiate-the-rest-client"></a>Instantiate the RestClient

In order to do that you can simply use the static method `create()`

```java
private final RestClient restClient = RestClient.create();
```
Or you can use the `builder` for more configuration, eg: set the base url , set default headers...

```java
private final RestClient restClient = RestClient.builder()
    .baseUrl("http://localhost:8081/api")
    .build();
```

## <a name="retrieve-resource-s-" aria-label="retrieve-resource-s-" id="retrieve-resource-s-" href="#retrieve-resource-s-"></a>Retrieve Resource(s)

- Using the `toEntity` to return `ResponseEntity` with the body of a given type
- Once you get the `ResponseEntity` object you can check the response `statusCode` and the `headers`

```java
// retrieve a list of products
ResponseEntity<List<Product>> response = restClient.get()
    .uri("/products")
    .retrieve()
    .toEntity(new ParameterizedTypeReference<>() {});
```

```java
// retrieve a product by id
ResponseEntity<Product> response = restClient.get()
    .uri("/products")
    .retrieve()
    .toEntity(Product.class);
```

## <a name="post-resource" aria-label="post-resource" id="post-resource" href="#post-resource"></a>Post Resource

To perform a `POST` request you just need to use the `post()` and specify the `contentType` and the `body`
```java
ResponseEntity<Product> response = restClient.post()
    .uri("/products")
    .contentType(APPLICATION_JSON)
    .body(product)
    .retrieve()
    .toEntity(Product.class);
```

## <a name="delete-resource" aria-label="delete-resource" id="delete-resource" href="#delete-resource"></a>Delete Resource

To perform a `DELETE` request you just you need to use the `delete()`

```java
 ResponseEntity<Product> response = restClient.delete()
            .uri("/products/{productId}", productId)
            .retrieve()
            .toEntity(Product.class);
```

## <a name="error-handling" aria-label="error-handling" id="error-handling" href="#error-handling"></a>Error handling

`RestClient` throws `RestClientResponseException` when receiving a 4xx or 5xx status code. You can catch the exception and throw a customized exception instead:

```java
ResponseEntity<Product> response = restClient.get()
            .uri("/products")
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                throw new ResponseStatusException(response.getStatusCode(), response.getHeaders())
             })
            .toEntity(Product.class);
```
For more flexibility of how you want to handle the response, the `exchange` would be the option to go with, 
also you need to provide an explicit mapping for your desire Type, eg : `ObjectMapper`

```java
ObjectMapper mapper = new ObjectMapper();
Product product = restClient.get()
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
```

## <a name="summary" aria-label="summary" id="summary" href="#summary"></a>Summary

Congratulations 🎉 ! You've covered the main features of the new api `RestClient`, You can notice that the new API is more straightforward (fluent) to handle Http Requests compared to the old `RestTemplate`

## <a name="github" aria-label="github" id="github" href="#github"></a>Github
The tutorial can be found here on [GitHub](https://github.com/nonestack-blog/spring-web-rest-client) 👋

## <a name="blog" aria-label="blog" id="blog" href="#blog"></a>Blog

Check new tutorials on [nonestack](https://www.nonestack.com) 👋
