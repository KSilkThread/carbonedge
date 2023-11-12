package certificateCC;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import certificatecontract.CertificateAsset;

public class AssetTests {

    private String sensorid = "sensor1";
    private String org = "Organisation 1";
    private String inspectorcert = "mockCert";
    private String inspectorOrg = "inspectorOrg";
    private String secID = "1234";
    private String expirydateString = Instant.now().toString();
    

    CertificateAsset asset = new CertificateAsset(sensorid, org, inspectorcert, inspectorOrg, secID, expirydateString, false);


    @Test void isequalSensorAsset() {
        assertEquals(asset, asset);
    }

    @Test void isequalsecond(){
        CertificateAsset asset2 = new CertificateAsset(sensorid, org, inspectorcert, inspectorOrg, secID, expirydateString, false);
        assertEquals(asset, asset2);
        System.out.println(asset.toJson());
    }


    @Test void checkJsontoString(){
        String jsonString = "{\"sensorid\":\"sensor1\",\"ownerorg\":\"Organisation 1\",\"inspectorcert\":\"mockCert\",\"inspectororganisation\":\"inspectorOrg\",\"expirydate\":\"1234\",\"firstauth\":false}";
        assertEquals(asset.toJson(), jsonString);
    }

    @Test void checkJsontoObject(){
        String jsonString = "{\"sensorid\":\"sensor1\",\"ownerorg\":\"Organisation 1\",\"inspectorcert\":\"mockCert\",\"inspectororganisation\":\"inspectorOrg\",\"expirydate\":\"1234\",\"firstauth\":false}";
        CertificateAsset asset2 = CertificateAsset.fromJSON(jsonString);
        assertEquals(asset, asset2);
    }

    @Test void isequalJSON(){
        String json = asset.toJson();
        CertificateAsset asset2 = CertificateAsset.fromJSON(json);
        assertEquals(asset, asset2);
    }

    @Test void isNotEqualTo(){
        CertificateAsset as = new CertificateAsset(sensorid, org, inspectorcert, inspectorOrg, secID, expirydateString, false);
        as.setFirstauth(true);
        assertFalse(asset.equals(as));
    }
    
}
