package fr.cgi.learninghub.swarm.model;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import fr.cgi.learninghub.swarm.model.StudentClass;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

@QuarkusTest
class UserTest {
    @Test
    void testGetClassIds() {
        StudentClass class1 = new StudentClass().setId("e1ef8cb4-d7dc-4c9d-9b98-8c0270cfac0b");
        StudentClass class2 = new StudentClass().setId("4265605f-3352-4f42-8cef-18e150bbf6bf");
        List<StudentClass> classes = Arrays.asList(class1, class2);

        User user = new User().setClasses(classes);
        assertEquals(Arrays.asList("e1ef8cb4-d7dc-4c9d-9b98-8c0270cfac0b","4265605f-3352-4f42-8cef-18e150bbf6bf"), user.getClassIds());
    }
    
    @Test
    void testGetGroupIds() {
        Group group1 = new Group().setId("e1ef8cb4-d7dc-4c9d-9b98-8c0270cfac0b");
        Group group2 = new Group().setId("4265605f-3352-4f42-8cef-18e150bbf6bf");
        List<Group> groups = Arrays.asList(group1, group2);

        User user = new User().setGroups(groups);
        assertEquals(Arrays.asList("e1ef8cb4-d7dc-4c9d-9b98-8c0270cfac0b","4265605f-3352-4f42-8cef-18e150bbf6bf"), user.getGroupIds());
    }
}
