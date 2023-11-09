package smtp.client;

import java.util.ArrayList;

public class Group {
    private final String senderAddress;
    private final String[] receiverAddresses;
    private ArrayList<Message> messages;

    public String getSenderAddress() {
        return senderAddress;
    }

    public String[] getReceiverAddresses() {
        return receiverAddresses;
    }

    Group(String[] emailAddresses) {
        senderAddress = emailAddresses[0];
        receiverAddresses = new String[emailAddresses.length - 1];
        System.arraycopy(emailAddresses, 1, receiverAddresses, 0, receiverAddresses.length);
    }

    public void sendMessage(Message message) {
        messages.add(message);

        // Send the message
    }
}
