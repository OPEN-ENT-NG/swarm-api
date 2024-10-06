# User provisioning

If you don't use ENT User via ENT federation, you can simulate one (the one you'll authenticate with)

Choose among ENT platform one user with its `userid`

Assuming `d943571c-1ad1-4c21-9efa-7008e59e0d8d` is your ENT User ID you picked.

Create User

![user-provisioning](/docs/user-provisioning/assets/user-provisioning1.png)

Add "manager" role from "console" client in order to make use of API

Getting user name or its attributes is fetched via JWT 

Dépendance à ajouter
```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-jwt</artifactId>
</dependency>
```


```java
@Inject
JsonWebToken jwt;

jwt.getName());
jwt.getClaim("name")); // <-- attributes from user
```

Exemple
```java
import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.jwt.JsonWebToken;

@GET
@Path("/test")
public Uni<String> test() {
    log.info("testName: " + jwt.getName());
    log.info("testFetchAttributes: " + jwt.getClaim("name"));
    return Uni.createFrom().item("{\"message\":\"OK\"}");
}
```