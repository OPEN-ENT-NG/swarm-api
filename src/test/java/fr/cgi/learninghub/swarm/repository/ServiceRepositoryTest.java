package fr.cgi.learninghub.swarm.repository;

import fr.cgi.learning.hub.swarm.common.entities.Service;
import fr.cgi.learning.hub.swarm.common.enums.State;
import fr.cgi.learning.hub.swarm.common.enums.Type;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class ServiceRepositoryTest {

   /* @Inject
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

        // List<String> userIds = Arrays.asList("user2-id", "user3-id");
        // List<Service> results = serviceRepository.listByUsersIds(userIds).await().indefinitely();

        // assertEquals(3, results.size());
        // assertFalse(results.stream().allMatch(s -> s.getUserId().equals("user1-id")));
        // assertTrue(results.stream().anyMatch(s -> s.getUserId().equals("user2-id") || s.getUserId().equals("user3-id")));
    }

    @Test
    @DisplayName("Test ServiceRepository.listByIds(List<String> ids)")
    void testListByIds() {
        List<String> serviceIds = Arrays.asList("service01", "service02");
        Uni<List<Service>> resultsUni = serviceRepository.listByIds(serviceIds);
        List<Service> results = resultsUni.await().indefinitely();

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(s -> serviceIds.contains(s.getServiceName())));
    }

    @Test
    @DisplayName("Test ServiceRepository.patchState(List<String> serviceIds, State state)")
    void testPatchState() {
        List<String> serviceIds = Arrays.asList("service01", "service02");
        State newState = State.RESET_IN_ERROR;
        Uni<Integer> updateResult = serviceRepository.patchState(serviceIds, newState);

        Integer rowsAffected = updateResult.await().indefinitely();
        assertEquals(2, rowsAffected);

        Uni<List<Service>> servicesUni = serviceRepository.listByIds(serviceIds);
        List<Service> updatedServices = servicesUni.await().indefinitely();

        assertTrue(updatedServices.stream().allMatch(s -> s.getState().equals(newState)));
    }

    @Test
    @DisplayName("Test ServiceRepository.update(List<String> serviceIds, Date deletionDate)")
    void testUpdateDeletionDate() {
        List<String> serviceIds = Arrays.asList("service03", "service04");
        Date newDeletionDate = new Date();
        Uni<Integer> updateResult = serviceRepository.update(serviceIds, newDeletionDate);

        Integer rowsAffected = updateResult.await().indefinitely();
        assertEquals(2, rowsAffected);

        Uni<List<Service>> servicesUni = serviceRepository.listByIds(serviceIds);
        List<Service> updatedServices = servicesUni.await().indefinitely();

        assertTrue(updatedServices.stream().allMatch(s -> s.getDeletionDate().equals(newDeletionDate)));
    }

    @Test
    @DisplayName("Test ServiceRepository.delete(List<String> serviceIds)")
    void testDeleteServices() {
        List<String> serviceIds = Arrays.asList("service01", "service03");
        Uni<Integer> deleteResult = serviceRepository.delete(serviceIds);

        Integer rowsAffected = deleteResult.await().indefinitely();
        assertEquals(2, rowsAffected);

        Uni<List<Service>> servicesUni = serviceRepository.listByIds(serviceIds);
        List<Service> deletedServices = servicesUni.await().indefinitely();

        assertTrue(deletedServices.stream().allMatch(s -> s.getState().equals(State.DELETION_SCHEDULED)));
    }

    @Test
    @DisplayName("Test ServiceRepository.reset(List<String> serviceIds, Date deletionDate)")
    void testResetServices() {
        List<String> serviceIds = Arrays.asList("service02", "service04");
        Date newDeletionDate = new Date();
        Uni<Integer> resetResult = serviceRepository.reset(serviceIds, newDeletionDate);

        Integer rowsAffected = resetResult.await().indefinitely();
        assertEquals(2, rowsAffected);

        Uni<List<Service>> servicesUni = serviceRepository.listByIds(serviceIds);
        List<Service> resetServices = servicesUni.await().indefinitely();

        assertTrue(resetServices.stream().allMatch(s -> s.getState().equals(State.RESET_IN_PROGRESS)));
        assertTrue(resetServices.stream().allMatch(s -> s.getDeletionDate().equals(newDeletionDate)));
    }*/
}
