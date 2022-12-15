package validationcontract;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ValidationAssetTest {

    String ids1 = "sensor1";
    String orgs1 = "org1";
    String[] certs1 =  {"QAL2", "QAL3", "AST"};

    String ids2 = "sensor2";
    String orgs2 = "org2";
    String[] certs2 ={ "QAL2", "QAL3", "dummy"};

    @Test void equalsSelf(){
        ValidationAsset asset = new ValidationAsset(ids1, orgs1, certs1);
        assertEquals(asset, asset);
    }
    
    @Test void notEquals(){
        ValidationAsset asset = new ValidationAsset(ids1, orgs1, certs1);
        ValidationAsset asset2 = new ValidationAsset(ids1, orgs1, certs2);
        assertNotEquals(asset, asset2);
    }

    @Test void json(){
        ValidationAsset asset = new ValidationAsset(ids1, orgs1, certs1);
        String jsonAsset = asset.toJSON();
        ValidationAsset asset2 = ValidationAsset.fromJSON(jsonAsset);
        assertEquals(asset, asset2);
    }

    @Test void checkJsonString(){
        ValidationAsset asset = new ValidationAsset(ids1, orgs1, certs1);
        String jsonAsset = asset.toJSON();

        try {
            String text = new String(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource("TestJSON.json").toURI())), StandardCharsets.UTF_8);
            JsonObject obj = JsonParser.parseString(text).getAsJsonObject();
            assertEquals(jsonAsset, obj.toString());
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            fail();
        }
    }
}
