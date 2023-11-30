package ch.heig.dai.lab.smtp.client;

import java.util.ArrayList;

public class Group {
    private final String senderAddress;
    private final ArrayList<String> receiverAddresses;
    private Message message;

    public Group(String senderAddress, ArrayList<String> receiverAddresses) {
        this.senderAddress = senderAddress;
        this.receiverAddresses = receiverAddresses;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public ArrayList<String> getReceiverAddresses() {
        return receiverAddresses;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getEmailToSend() {
        StringBuilder email = new StringBuilder();
        email.append("From: ").append(senderAddress).append("\n");
        // Keep To empty so recipients don't see other recipient email addresses
        email.append("To: ");
        email.deleteCharAt(email.length() - 1);
        email.append("\n");
        email.append("Date: ").append(System.currentTimeMillis()).append("\n");
        email.append(message.toString());
        email.append("\r\n.\r\n");

        return email.toString();
    }
}
