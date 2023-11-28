package smtp.client;

import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static smtp.client.DataReader.getMessagesFromFile;

public class Client {
    final String SERVER_ADDRESS = "127.0.0.1";
    final int SERVER_PORT = 1025;

    public static void main(String[] args) {
        // Create the client
        Client client = new Client();
        String emailPath = args[0];
        String messagePath = args[1];
        String numberOfGroups = args[2];
        client.run(emailPath, messagePath, Integer.parseInt(numberOfGroups));
    }

    private void run(String emailPath, String messagePath, int numberOfGroups) {
        // Open a connexion to the mail server
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {


            // Create the groups using the DataReader
            ArrayList<String> addresses = DataReader.getAddressesFromFile(emailPath);
            ArrayList<Group> groups = new ArrayList<>();
            int peoplePerGroup = addresses.size() / numberOfGroups;

            if (peoplePerGroup < 2) {
                throw new RuntimeException("Not enough email addresses for this many groups!");
            } else if (peoplePerGroup > 5) {
                // TODO : ???
                throw new RuntimeException("Too many messages for this many groups!");
            }

            // Create the groups and assign them the sender and receivers
            int index = 0;
            for (int i = 0; i < numberOfGroups; ++i) {
                int from = (index + 1); // +1 to skip the sender
                int to = (numberOfGroups + 1) * (i + 1);
                ArrayList<String> receivers = new ArrayList<>(addresses.subList(from, to));
                groups.add(new Group(addresses.get(index), receivers));
                index += peoplePerGroup;
            }

            // Create the messages using the DataReader
            ArrayList<Message> messages = getMessagesFromFile(messagePath);

            if (messages.size() < groups.size()) {
                throw new RuntimeException("Not enough messages for this many groups!");
            }

            for (int i = 0; i < groups.size(); ++i) {
                groups.get(i).setMessage(messages.get(i));
            }

            // Service ready
            readServerResponse(in, "220");

            // Send ehlo
            System.out.println("ehlo heig-vd.ch");
            out.write("ehlo heig-vd.ch\n");
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
        } catch (IOException | RuntimeException e) {
            System.out.println("Error: " + e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

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
