package fr.cgi.learninghub.swarm.service;

import fr.cgi.learninghub.swarm.model.MailBody;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;


@ApplicationScoped
public class MailService {

    @Inject
    ReactiveMailer mailer;

    private static final Logger log = Logger.getLogger(MailService.class);

    public Uni<Void> send(MailBody mailBody) {
        return mailer.send(
                Mail.withHtml(mailBody.getTo(), mailBody.getSubject(), mailBody.getContent())
        ).onFailure()
                .invoke(e -> log.error(String.format("[SwarmApi@%s::send] Failed send mail : %s", this.getClass().getSimpleName(), e.getMessage())));
    }
}
