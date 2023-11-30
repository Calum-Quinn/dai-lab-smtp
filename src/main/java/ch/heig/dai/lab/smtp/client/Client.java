package ch.heig.dai.lab.smtp.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Client {
    final String SERVER_ADDRESS = "127.0.0.1";
    final int SERVER_PORT = 1025;
    private final ArrayList<Group> groups;

    public static void main(String[] args) {
        String emailPath = args[0];
        String messagePath = args[1];
        String numberOfGroups = args[2];

        Client client = new Client(emailPath, messagePath, Integer.parseInt(numberOfGroups));
        client.run();
    }

    /**
     * Prepares the groups and messages to be sent
     * @param emailPath the path to the file containing the email addresses
     * @param messagePath the path to the file containing the messages
     * @param numberOfGroups the number of groups to create
     */
    private Client(String emailPath, String messagePath, int numberOfGroups) {
        ArrayList<String> addresses = DataReader.getAddressesFromFile(emailPath);
        ArrayList<Message> messages = DataReader.getMessagesFromFile(messagePath);
        groups = new ArrayList<>();
        int peoplePerGroup = addresses.size() / numberOfGroups;

        // Check if there are enough addresses and messages
        if (peoplePerGroup < 2) {
            System.out.println("Error : not enough email addresses for this many groups!");
            System.exit(1);
        } else if (peoplePerGroup > 5) {
            // The maximum number of people per group is 5
            peoplePerGroup = 5;
        }

        if (messages.size() < numberOfGroups) {
            System.out.println("Error : not enough messages for this many groups!");
            System.exit(1);
        }

        // Create the groups and assign them the sender and receivers
        int index = 0;
        for (int i = 0; i < numberOfGroups; ++i) {
            int from = index;
            int to = index + peoplePerGroup;
            ArrayList<String> receivers = new ArrayList<>(addresses.subList(from + 1, to));
            groups.add(new Group(addresses.get(index), receivers));
            index = to;
        }

        // Assign the messages to the groups
        for (int i = 0; i < groups.size(); ++i) {
            groups.get(i).setMessage(messages.get(i));
        }
    }

    /**
     * Establishes a connection to the server and sends the emails
     */
    private void run() {
        // Open a connexion to the mail server
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {

            // Service ready
            readServerResponse(in, "220");

            // Send ehlo
            System.out.println("ehlo Client");
            out.write("ehlo Client\n");
            out.flush();

            // Read the response
            readServerResponse(in, "250");

            // Create the emails
            for (Group group : groups) {
                // Send from
                System.out.println("mail from:<" + group.getSenderAddress() + ">");
                out.write("mail from:<" + group.getSenderAddress() + ">\n");
                out.flush();

                // Read the response
                readServerResponse(in, "250");

                // Send to
                for (String address : group.getReceiverAddresses()) {
                    System.out.println("rcpt to:<" + address + ">");
                    out.write("rcpt to:<" + address + ">\n");
                    out.flush();
                    readServerResponse(in, "250");
                }

                // Send data
                System.out.println("data");
                out.write("data\n");
                out.flush();

                // Read the response
                readServerResponse(in, "354");

                // Send the message
                System.out.println(group.getEmailToSend());
                out.write(group.getEmailToSend());
                out.flush();

                // Read the response
                readServerResponse(in, "250");
            }

            // Send quit
            System.out.println("quit");
            out.write("quit\n");
            out.flush();

            // Read connection closing
            readServerResponse(in, "221");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Reads the server response and throws an exception if it is not the expected one
     * @param in the BufferedReader to read from
     * @param successCode the expected success code
     * @throws IOException if an I/O error occurs
     * @throws RuntimeException if the server returns a line starting with 4xx or 5xx
     */
    private void readServerResponse(BufferedReader in, String successCode) throws IOException {
        String line;
        while ((line = in.readLine()) != null && !line.startsWith(successCode + " ")) {
            // If the server returns a line starting with 4xx or 5xx, the message was not sent
            if (!line.startsWith("250")) {
                throw new RuntimeException(line);
            }
            System.out.println("Server: " + line);
        }
        System.out.println("Server: " + line);
    }
}
