package measurementCC;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.hyperledger.fabric.contract.ClientIdentity;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.Chaincode.Response;
import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import measurementcontract.Helper;
import measurementcontract.MeasurementAsset;
import measurementcontract.MeasurementContract;

public class MeasurementContractTests {
     
    @Nested
    class LedgerTests {

        Helper helper = new Helper();

        MeasurementContract contract = new MeasurementContract();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class); 
        ClientIdentity cID = mock(ClientIdentity.class);
        
        String sensorid = "sensor1";
        String mspid = "org1";
        int data = 1000;
        Instant time = Instant.now();
        String timestamp = time.toString();
        
        String keyPrefixString = "mspid~sensorid~timestamp";
        CompositeKey key = new CompositeKey(keyPrefixString, new String[] {mspid, sensorid, timestamp});

        MeasurementAsset asset = new MeasurementAsset(sensorid, mspid, data, timestamp);


        @Test void noProperAsset() {

            when(ctx.getStub()).thenReturn(stub);
            when(stub.createCompositeKey(keyPrefixString, new String[] {mspid, sensorid, timestamp})).thenReturn(key);
            when(stub.getStringState(key.toString())).thenReturn(null);

            String response = contract.monitoringExists(ctx, mspid, sensorid, timestamp);
            assertNotEquals(helper.parseResponse(response).get("status").getAsString(), "200");
        }

        
        @Test void pushDataTest(){
            
            when(ctx.getStub()).thenReturn(stub);
            when(ctx.getClientIdentity()).thenReturn(cID);
            when(stub.getTxTimestamp()).thenReturn(time);
            when(stub.createCompositeKey(keyPrefixString, new String[] {mspid, sensorid, timestamp})).thenReturn(key);
            when(ctx.getClientIdentity().getMSPID()).thenReturn(mspid);
            when(stub.getChannelId()).thenReturn("mychannel");
            when(stub.invokeChaincodeWithStringArgs("certificateCC", List.of("isValid", sensorid, mspid), stub.getChannelId())).thenReturn(new Response(200, helper.createSuccessResponse(true), helper.createSuccessResponse(true).getBytes()));

            String jsonString = "{\"sensorid\":\"sensor1\",\"organisation\":\"org1\",\"data\":1000,\"timestamp\":\"" + timestamp + "\"}";

            contract.pushMeasurementAsset(ctx, sensorid, data);
            verify(stub).putStringState(key.toString(), jsonString);
        }

        @Test void MeasurementAssetExistsTest(){

            System.out.println(timestamp);
    
            when(ctx.getStub()).thenReturn(stub);
            when(stub.createCompositeKey(keyPrefixString, new String[] {mspid, sensorid, timestamp})).thenReturn(key);
            when(stub.getStringState(key.toString())).thenReturn(asset.toJSON());

            String result = contract.monitoringExists(ctx, mspid, sensorid, timestamp);
            assertEquals(helper.parseResponse(result).get("status").getAsString(), "200");

        }

        @Test void getMeasurementAssetTest(){

            when(ctx.getStub()).thenReturn(stub);
            when(stub.createCompositeKey(keyPrefixString, new String[] {mspid, sensorid, timestamp})).thenReturn(key);
            when(stub.getStringState(key.toString())).thenReturn(asset.toJSON());

            String result = contract.readMeasurementAsset(ctx, mspid, sensorid, timestamp);
            assertEquals(helper.parseResponse(result).get("status").getAsString(), "200");
        }

    }

    @Nested
    class QueryTests {

        MeasurementContract contract = new MeasurementContract();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);

        Instant timestamp = Instant.now();

        Helper helper = new Helper();

        String keyPrefixString = "mspid~sensorid~timestamp";
        CompositeKey orgkey1 = new CompositeKey(keyPrefixString, new String[] {"org1"});
        CompositeKey orgkey2 = new CompositeKey(keyPrefixString, new String[] {"org2"});


        List<MeasurementAsset> assetListOrg1 = List.of(new MeasurementAsset("sensor", "org1", 100, timestamp.toString()),
                                                  new MeasurementAsset("sensor1", "org1", 100, timestamp.toString()),
                                                  new MeasurementAsset("sensor3", "org1", 160, timestamp.toString()));

        List<MeasurementAsset> assetListOrg2 = List.of(new MeasurementAsset("sensor2", "org2", 150, timestamp.toString()),
                                                  new MeasurementAsset("sensor2", "org2", 150, timestamp.toString()));

        MockResultsIterator iterator1 = new MockResultsIterator(assetListOrg1.stream().map(sa -> new MockKeyValue(sa, keyPrefixString))
                                                                                      .collect(Collectors.toList()));

        MockResultsIterator iterator2 = new MockResultsIterator(assetListOrg2.stream().map(sa -> new MockKeyValue(sa, keyPrefixString))
                                                                                      .collect(Collectors.toList()));

        @Test void checkQueryOrg(){

            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStateByPartialCompositeKey(orgkey1.toString())).thenReturn(iterator1);

            String response = contract.queryOrgEntries(ctx, "org1");

            assertEquals(helper.prettyOrgJson(assetListOrg1, "org1").toString(), helper.parseResponse(response).get("response").getAsString());

        }

        @Test void checkquerySensor(){
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStateByPartialCompositeKey(new CompositeKey(keyPrefixString, new String[] {"org2", "sensor2"}).toString()))
                                                    .thenReturn(iterator2);
            
            String response = contract.querySensorEntries(ctx, "org2", "sensor2");

            assertEquals(helper.prettySensorJson(assetListOrg2, "sensor2").toString(), helper.parseResponse(response).get("response").getAsString());
        }

    }

    
}
