package smtp.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Client {

    private DataReader dataReader;
    private ArrayList<Group> groups;
    private ArrayList<Message> messages;

    final String SERVER_ADDRESS = "127.0.0.1";
    final int SERVER_PORT = 1025;

//    final String fileName = "Emails.txt";

    public static void main(String[] args) {
        // Create the client
        Client client = new Client();
        client.run();
    }

    private void run() {
        // Open a connexion to the mail server
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {

            // Create the groups using the DataReader


            // Create the messages using the DataReader


            // Send messages to groups



//            // Read the welcome message
//            String line;
//            while ((line = in.readLine()) != null && !line.equals("END")) {
//                System.out.println(line);
//            }
//
//            // Read user input
//            Scanner scanner = new Scanner(System.in);
//            String msg;
//            while (!Objects.equals(msg = scanner.nextLine(), "EXIT")) {
//                // Send the message to the server
//                out.write(msg + "\n");
//                out.flush();
//                // Read the response from the server
//                line = in.readLine();
//                System.out.println(line);
//            }
        } catch (IOException e) {
//            System.out.println("Client: exception while using client socket: " + e);
        }
    }
}
