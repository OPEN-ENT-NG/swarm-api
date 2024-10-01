package fr.cgi.learninghub.swarm.resources;

import fr.cgi.learning.hub.swarm.common.enums.State;
import fr.cgi.learninghub.swarm.model.*;
import io.quarkus.security.Authenticated;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

@QuarkusTest
class ServiceResourceTest {
/*
    @Test
    @DisplayName("Test DELETE /services")
    @Authenticated
    void testDeleteService() {
        DeleteServiceBody deleteServiceBody = new DeleteServiceBody();
        deleteServiceBody.setServicesIds(Collections.singletonList("service-id-to-delete"));

        // Exécute la requête DELETE
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(deleteServiceBody)
                .when()
                .delete("/services")
                .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("Test PUT /services")
    @Authenticated
    void testUpdateService() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1); // Ajouter un jour
        Date tomorrow = calendar.getTime();

        UpdateServiceBody updateServiceBody = new UpdateServiceBody();
        updateServiceBody.setServicesIds(Collections.singletonList("service-id-to-update"))
                .setDeletionDate(tomorrow);

        // Exécute la requête PUT
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Collections.singletonList(updateServiceBody))
                .when()
                .put("/services")
                .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("Test PATCH /services/reset")
    @Authenticated
    void testResetService() {
        // Prépare un body de requête
        ResetServiceBody resetServiceBody = new ResetServiceBody();
        resetServiceBody.setServicesIds(Collections.singletonList("service-id-to-reset"));

        // Exécute la requête PATCH
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(resetServiceBody)
                .when()
                .patch("/services/reset")
                .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("Test PATCH /services")
    @Authenticated
    void testPatchStatus() {
        // Prépare un body de requête
        PatchStateServiceBody patchStateServiceBody = new PatchStateServiceBody();
        patchStateServiceBody.setServicesIds(Collections.singletonList("service-id-to-patch"))
                        .setState(State.RESET_IN_ERROR);

        // Exécute la requête PATCH
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Collections.singletonList(patchStateServiceBody))
                .when()
                .patch("/services")
                .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("Test POST /services/emails")
    @Authenticated
    void testDistributeMails() {
        // Prépare un body de requête
        DistributeServiceBody distributeServiceBody = new DistributeServiceBody();
        distributeServiceBody.setServicesIds(Arrays.asList("service-id1-to-distribute", "service-id2-to-distribute"));

        // Exécute la requête POST
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(distributeServiceBody)
                .when()
                .post("/services/emails")
                .then()
                .statusCode(204);
    }*/
}
