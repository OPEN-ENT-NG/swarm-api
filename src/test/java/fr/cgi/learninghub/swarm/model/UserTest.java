package fr.cgi.learninghub.swarm.model;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

@QuarkusTest
class UserTest {
/*    @Test
    void testGetClassIds() {
        User user = new User().setClasses(Arrays.asList("42$1TES 2", "42$1TES 1"));
        assertEquals(Arrays.asList("42$1TES 2", "42$1TES 1"), user.getClassIds());
        assertEquals(Arrays.asList("1TES 2", "1TES 1"), user.getClasses().stream().map(ClassInfos::getName).toList());
    }*/
    
    // @Test
    // void testGetGroupIds() {
    //     StudentGroup group1 = new StudentGroup().setId("e1ef8cb4-d7dc-4c9d-9b98-8c0270cfac0b");
    //     StudentGroup group2 = new StudentGroup().setId("4265605f-3352-4f42-8cef-18e150bbf6bf");
    //     List<StudentGroup> groups = Arrays.asList(group1, group2);

    //     User user = new User().setGroups(groups);
    //     assertEquals(Arrays.asList("e1ef8cb4-d7dc-4c9d-9b98-8c0270cfac0b","4265605f-3352-4f42-8cef-18e150bbf6bf"), user.getGroupIds());
    // }
}
