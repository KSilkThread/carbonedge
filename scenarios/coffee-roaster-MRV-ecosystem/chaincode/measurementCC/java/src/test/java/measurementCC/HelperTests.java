package measurementCC;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import measurementcontract.Helper;
import measurementcontract.MeasurementAsset;

public class HelperTests {

    Instant timestamp = Instant.parse("2022-11-08T13:20:07.546119200Z");

    List<MeasurementAsset> assetListOrg1 = List.of(new MeasurementAsset("sensor", "org1", 100, timestamp.toString()),
                                              new MeasurementAsset("sensor1", "org1", 100, timestamp.toString()),
                                              new MeasurementAsset("sensor3", "org1", 160, timestamp.toString()),
                                              new MeasurementAsset("sensor", "org1", 1000, timestamp.toString()),
                                              new MeasurementAsset("sensor", "org1", 1020, timestamp.toString()),
                                              new MeasurementAsset("sensor1", "org1", 1033, timestamp.toString()),
                                              new MeasurementAsset("sensor", "org1", 1044, timestamp.toString()));

    List<MeasurementAsset> assetListOrg2 = List.of(new MeasurementAsset("sensor2", "org2", 150, timestamp.toString()),
                                              new MeasurementAsset("sensor2", "org2", 150, timestamp.toString()));



    @Test void checkprettySensor(){
        Helper helper = new Helper();

        List<MeasurementAsset> filteredbysensorid = assetListOrg1.stream().filter(sa -> sa.getSensorid() == "sensor")
                                                                     .collect(Collectors.toList());

        JsonObject result = helper.prettySensorJson(filteredbysensorid, "sensor");

        assertNotNull(result);
        assertEquals(result.get("Sensorid").toString(), "\"sensor\"");
        assertNotNull(result.get("Logs"));
        assertEquals(result.get("Logs"), new Gson().toJsonTree(filteredbysensorid));
    }

    @Test void checkprettyOrg(){
        Helper helper = new Helper();

        JsonObject result = helper.prettyOrgJson(assetListOrg1, "org1");

        assertNotNull(result.get("Organisation"));
        assertEquals(result.get("Organisation").toString(), "\"org1\"");
        assertNotNull(result.get("Sensors"));
        assertEquals(result.get("Sensors").getAsJsonArray().getClass(), new JsonArray().getClass());

        try {
            String text = new String(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource("checkprettyOrg.json").toURI())), StandardCharsets.UTF_8);
            JsonObject obj = JsonParser.parseString(text).getAsJsonObject();
            assertEquals(result, obj);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            fail();
        }
    }

    
}
