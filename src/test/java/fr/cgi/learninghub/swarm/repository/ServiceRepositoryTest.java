package fr.cgi.learninghub.swarm.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.cgi.learning.hub.swarm.common.entities.Service;
import fr.cgi.learning.hub.swarm.common.enums.State;
import fr.cgi.learning.hub.swarm.common.enums.Type;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class ServiceRepositoryTest {
    @Inject
    ServiceRepository serviceRepository;

    List<Service> services;

    ServiceRepositoryTest() {
        Service service1 = new Service()
            .setUserId("user1-id")
            .setFirstName("First")
            .setLastName("USER")
            .setServiceName("service01")
            .setStructureId("structure-id")
            .setType(Type.WORDPRESS)
            .setDeletionDate(new Date())
            .setState(State.DEPLOYED);

        Service service2 = new Service()
            .setUserId("user2-id")
            .setFirstName("Second")
            .setLastName("USER")
            .setServiceName("service02")
            .setStructureId("structure-id")
            .setType(Type.WORDPRESS)
            .setDeletionDate(new Date())
            .setState(State.DEPLOYED);

        Service service3 = new Service()
            .setUserId("user2-id")
            .setFirstName("Second")
            .setLastName("USER")
            .setServiceName("service03")
            .setStructureId("structure-id")
            .setType(Type.PRESTASHOP)
            .setDeletionDate(new Date())
            .setState(State.DEPLOYED);

        Service service4 = new Service()
            .setUserId("user3-id")
            .setFirstName("Third")
            .setLastName("USER")
            .setServiceName("service04")
            .setStructureId("structure-id")
            .setType(Type.WORDPRESS)
            .setDeletionDate(new Date())
            .setState(State.DEPLOYED);

        services = Arrays.asList(service1, service2, service3, service4);
    }

    @Test
    @DisplayName("Test ServiceRepository.listByUsersIds(List<String> userIds)")
    void testListByUsersIds() {
        // Insère des données dans la base de test
        // services.stream().forEach(service -> serviceRepository.persist(service));

        // List<String> userIds = Arrays.asList("user2-id","user3-id");
        // List<Service> results = serviceRepository.listByUsersIds(userIds).await().indefinitely();

        // assertEquals(3, results.size());
        // assertFalse(results.stream().allMatch(s -> s.getUserId().equals("user1-id")));
        // assertTrue(results.stream().anyMatch(s -> s.getUserId().equals("user2-id") || s.getUserId().equals("user3-id")));
    }

}
