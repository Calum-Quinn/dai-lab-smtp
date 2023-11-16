package smtp.client;

import java.io.*;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public abstract class DataReader {
    public static ArrayList<String> getAddressesFromFile(String path) throws IOException, ParseException {
        Object object = new JSONParser().parse(new FileReader(path));
        JSONObject jo = (JSONObject) object;
        JSONArray ja = (JSONArray) jo.get("addresses");
        ArrayList<String> addresses = new ArrayList<>();

        for (Object o : ja) {
            JSONObject message = (JSONObject) o;
            addresses.add((String) message.get("address"));
        }

        return addresses;
    }

    public static ArrayList<Message> getMessagesFromFile(String path) throws IOException, ParseException {
        Object object = new JSONParser().parse(new FileReader(path));
        JSONObject jo = (JSONObject) object;
        JSONArray ja = (JSONArray) jo.get("messages");
        ArrayList<Message> messages = new ArrayList<>();

        for (Object o : ja) {
            JSONObject message = (JSONObject) o;
            String subject = (String) message.get("subject");
            String body = (String) message.get("body");
            messages.add(new Message(subject, body));
        }

        return messages;
    }
}