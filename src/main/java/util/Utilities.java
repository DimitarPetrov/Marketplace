package util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Utilities {

    public static String encrypt(String pass){
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(pass.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }

    public static JsonObject parseCloudFoundryDatabaseCredentials(String env){
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(env);
        JsonArray array = element.getAsJsonObject().getAsJsonArray("postgresql");
        return array.iterator().next().getAsJsonObject().get("credentials").getAsJsonObject();
    }
}
