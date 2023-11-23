package smtp.client;

import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static smtp.client.DataReader.getMessagesFromFile;

public class Client {

    private DataReader dataReader;
    private ArrayList<Group> groups;
    private ArrayList<Message> messages;

    final String SERVER_ADDRESS = "127.0.0.1";
    final int SERVER_PORT = 1025;

    public static void main(String[] args) {
        // Create the client
        Client client = new Client();
        String emailPath = args[0];
        String messagePath = args[1];
        String numberOfGroups = args[2];
        client.run(emailPath,messagePath,Integer.parseInt(numberOfGroups));
    }

    private void run(String emailPath, String messagePath,int numberOfGroups) {
        // Open a connexion to the mail server
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {


            // Create the groups using the DataReader
            ArrayList<String> addresses = DataReader.getAddressesFromFile(emailPath);
            ArrayList<Group> groups = new ArrayList<>();
            int peoplePerGroup = addresses.size() / numberOfGroups;

            if(peoplePerGroup < 2) {
                throw new RuntimeException("Not enough email addresses for this many groups!");
            } else if (peoplePerGroup > 5) {
                throw new RuntimeException("Too many messages for this many groups!");
            }
            // For each group provide a few addresses and remove them from the list so as not to use them multiple times
            for (int i = 0; i < numberOfGroups; i++) {
                groups.add(new Group(addresses.subList(0, peoplePerGroup).toArray(new String[0])));
                addresses.subList(0, peoplePerGroup).clear();
            }

            // Create the messages using the DataReader
            ArrayList<Message> messages = getMessagesFromFile(messagePath);

            // Send messages to groups
            for (Group group :groups) {
                out.write(
                        "ehlo heig-vd.ch\n" +
                        "mail from:<senderEmail>\n"
                );
                out.flush();
                for (String address : group.getReceiverAddresses()) {
                    out.write("rcpt to:<" + address + ">\n");
                }
                out.flush();
                out.write(
                        "data\n" +
                        "From: <chuck.norris@hotmail.ch>\n" +
                        "To:\n" +
                        "Date: November 30th, 2023\n" +
                        "Subject: Hello\n" +
                        "Hi, this is spoof.\n" +
                        "\n" +
                        ".\n"
                );
                out.flush();
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
