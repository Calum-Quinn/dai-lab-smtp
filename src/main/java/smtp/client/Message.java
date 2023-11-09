package smtp.client;

public class Message {
    private final String subject;
    private final String body;

    private final Group group;

    Message(String subject, String body, Group group) {
        this.subject = subject;
        this.body = body;
        this.group = group;
    }
}
