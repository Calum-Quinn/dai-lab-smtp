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

            String line;
            int messageCount = 0;
            String code = "0";
            boolean success = true;
            // Send messages to groups
            for (Group group :groups) {
                while ((line = in.readLine()) != null && !line.equals("220 a547a1b16b1a ESMTP")) {
                    System.out.println(line);
                }

                out.write("ehlo heig-vd.ch\n");
                out.flush();


                while ((line = in.readLine()) != null && !line.equals("250 SMTPUTF8")) {
                    System.out.println(line);
                }
                System.out.println(line);

                out.write("mail from:<" + group.getSenderAddress() + ">\n");
                out.flush();

                while ((line = in.readLine()) != null && !line.equals("250 Accepted")) {
                    System.out.println(line);
                }

                for (String address : group.getReceiverAddresses()) {
                    out.write("rcpt to:<" + address + ">\n");
                    out.flush();
                    while ((line = in.readLine()) != null && !line.equals("250 Accepted")) {
                        System.out.println(line);
                    }
                }

                out.write("data\n");
                out.flush();
                while ((line = in.readLine()) != null && !line.equals("354 End data with <CR><LF>.<CR><LF>")) {
                    System.out.println(line);
                }

                // Only move on if command was successful
                while(success) {
                    out.write(
                            "From: <chuck.norris@hotmail.ch>\n" +
                                    "To:\n" +
                                    "Date: 30 novembre 2023\n" +
                                    "Subject: Hello\n" +
                                    "\n" +
                                    messages.get(messageCount++) +
                                    ".\n"
                    );
                    out.flush();

                    line = in.readLine();

                    System.out.println(line);
                    if (line != null) {
                        code = line.substring(0, 3);
                    }

                    success = code.equals("250");
                }

                out.write("quit\n");
                out.flush();

                while((line = in.readLine()) != null && !line.equals("221 Bye")) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
