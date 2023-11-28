package ch.heig.dai.lab.smtp.client;

public class Message {
    private final String subject;
    private final String body;

    public Message(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

    public String toString() {
        return "Subject: " + subject + "\n\n" + body + "\n";
    }
}
