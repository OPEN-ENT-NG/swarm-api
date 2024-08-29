package fr.cgi.learninghub.swarm.resources;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.oidc.OidcSecurity;
import io.quarkus.test.security.oidc.UserInfo;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class ServiceResourceTest {

    @Test
    @TestSecurity(user = "John DOE")
    @OidcSecurity(
        userinfo = {
                @UserInfo(key = "name", value = "John DOE"),
        }
    )
    public void testList() {
        given()
                .when().get("/services")
                .then()
                .statusCode(200);
    }
}
