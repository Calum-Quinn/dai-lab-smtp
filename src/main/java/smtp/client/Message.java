package smtp.client;

public class Message {
    private final String subject;
    private final String body;
    private Group group;

    Message(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }
}
