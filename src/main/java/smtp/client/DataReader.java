package smtp.client;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public abstract class DataReader {
    /**
     * Regex to validate email addresses
     * Taken from <a href="https://stackoverflow.com/questions/8204680/java-regex-email/13013061#13013061">...</a>
     */
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    /**
     * Validates an email address
     *
     * @param address the address to validate
     * @return true if the address is valid, false otherwise
     */
    private static boolean validateAddress(String address) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(address);
        return matcher.matches();
    }

    /**
     * Reads a file containing email addresses and returns them as an ArrayList
     * @param path the path to the file
     * @return an ArrayList containing the email addresses
     * @throws IOException if the file is not found
     * @throws ParseException if the file is not in the correct format
     * @throws RuntimeException if the file contains invalid addresses
     */
    public static ArrayList<String> getAddressesFromFile(String path) throws IOException, ParseException {
        ArrayList<String> addresses = new ArrayList<>();

        try (FileReader fileReader = new FileReader(path)) {
            Object object = new JSONParser().parse(fileReader);
            JSONObject jo = (JSONObject) object;
            JSONArray ja = (JSONArray) jo.get("addresses");
            if (ja == null) {
                throw new ParseException(0);
            }

            for (Object o : ja) {
                JSONObject message = (JSONObject) o;
                String address = (String) message.get("address");
                if (address == null) {
                    throw new ParseException(0);
                }
                if (!validateAddress(address)) {
                    throw new RuntimeException();
                }
                addresses.add(address);
            }
        } catch (IOException e) {
            System.out.println("Erreur : fichier introuvable");
        } catch (ParseException e) {
            System.out.println("Erreur : le fichier n'est pas au bon format");
        } catch (RuntimeException e) {
            System.out.println("Erreur : le fichier contient des adresses invalides");
        }

        return addresses;
    }

    /**
     * Reads a file containing messages and returns them as an ArrayList
     * @param path the path to the file
     * @return an ArrayList containing the messages
     * @throws IOException if the file is not found
     * @throws ParseException if the file is not in the correct format
     */
    public static ArrayList<Message> getMessagesFromFile(String path) throws IOException, ParseException {
        ArrayList<Message> messages = new ArrayList<>();

        try (FileReader fileReader = new FileReader(path)) {
            Object object = new JSONParser().parse(fileReader);
            JSONObject jo = (JSONObject) object;
            JSONArray ja = (JSONArray) jo.get("messages");
            if (ja == null) {
                throw new ParseException(0);
            }

            for (Object o : ja) {
                JSONObject message = (JSONObject) o;
                String subject = (String) message.get("subject");
                String body = (String) message.get("body");
                if (subject == null || body == null) {
                    throw new RuntimeException();
                }
                messages.add(new Message(subject, body));
            }
        } catch (IOException e) {
            System.out.println("Erreur : le fichier introuvable");
        } catch (ParseException | RuntimeException e) {
            System.out.println("Erreur : le fichier n'est pas au bon format");
        }

        return messages;
    }
}