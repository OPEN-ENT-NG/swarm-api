package fr.cgi.learninghub.swarm.model;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class MailBodyTest {

    @Test
    public void testMailBodySettersAndGetters() {
        // Création d'un objet MailBody
        MailBody mailBody = new MailBody();

        // Test des setters
        mailBody.setTo("test@example.com")
                .setSubject("Test Subject")
                .setContent("This is a test email content.");

        // Vérification des getters
        assertEquals("test@example.com", mailBody.getTo());
        assertEquals("Test Subject", mailBody.getSubject());
        assertEquals("This is a test email content.", mailBody.getContent());
    }

    @Test
    public void testMailBodyEmptyInitialization() {
        // Création d'un objet MailBody vide
        MailBody mailBody = new MailBody();

        // Vérification que les champs sont initialisés à null
        assertNull(mailBody.getTo());
        assertNull(mailBody.getSubject());
        assertNull(mailBody.getContent());
    }
}
