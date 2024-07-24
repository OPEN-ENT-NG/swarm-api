package fr.cgi.learninghub.swarm.resources;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.oidc.OidcSecurity;
import io.quarkus.test.security.oidc.UserInfo;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class UserResourceTest {

    @Test
    @TestSecurity(user = "John DOE")
    @OidcSecurity(
            userinfo = {
                    @UserInfo(key = "name", value = "John DOE"),
            }
    )
    void testHelloEndpoint() {
        given()
                .when().get("/users/me")
                .then()
                .statusCode(200)
                .body("name", is("John DOE"));
    }
}
