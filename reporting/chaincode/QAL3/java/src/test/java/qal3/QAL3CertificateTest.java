package qal3;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;



public class QAL3CertificateTest {

    QAL3Certificate cert = new QAL3Certificate("sesnor", "org1", "3", "3", 1);

    @Test void testJson(){
        JsonObject json = new JsonObject();
        json.addProperty("status", 200);
        json.addProperty("response", "Init");

        JsonObject object = new Gson().fromJson(json.toString(), JsonObject.class);
        assertEquals(object, json);
        System.out.println(object.get("status").getAsString());

    }
    
}
