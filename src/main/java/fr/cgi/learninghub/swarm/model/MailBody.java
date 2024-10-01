package fr.cgi.learninghub.swarm.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "MailBody object representing mail body")
public class MailBody {
    private String to;
    private String subject;
    private String content;

    public String getTo() {
        return to;
    }

    public MailBody setTo(String to) {
        this.to = to;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public MailBody setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getContent() {
        return content;
    }

    public MailBody setContent(String content) {
        this.content = content;
        return this;
    }
    
}
