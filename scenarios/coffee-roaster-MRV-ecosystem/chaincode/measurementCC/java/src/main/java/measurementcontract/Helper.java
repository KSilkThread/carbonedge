package measurementcontract;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Helper {

    public Helper(){

    }
    
    /**
     * Returns all MeasurementAssets owned by one organisation as a sorted json
     * 
     * @param assetList List of MeasurementAssets
     * @param org Organisation
     * @return JsonObject
     */
    public JsonObject prettyOrgJson(List<MeasurementAsset> assetList, String org){

        JsonObject json = new JsonObject();

        List<String> usedsensorids = new ArrayList<>();
        List<JsonObject> sortedObj = new ArrayList<>();

        json.addProperty("Organisation", org);

        for (MeasurementAsset asset: assetList){
            if(usedsensorids.contains(asset.getSensorid())) continue;

            List<MeasurementAsset> currentSensorData = assetList.stream().filter(sa -> sa.getSensorid().equals(asset.getSensorid()))
                                                                    .collect(Collectors.toList());

            sortedObj.add(prettySensorJson(currentSensorData, asset.getSensorid()));
            usedsensorids.add(asset.getSensorid());
        }

        json.add("Sensors", new Gson().toJsonTree(sortedObj));

        return json;
    }


    
    /** 
     * Returns all MeasurementAssets invoked by a specific sensor as a sorted json
     * @param assetList List of MeasurementAssets
     * @param sensorid Id of a specific sensor 
     * @return JsonObject
     */
    public JsonObject prettySensorJson(List<MeasurementAsset> assetList, String sensorid){
        JsonObject json = new JsonObject();
        json.addProperty("Sensorid", sensorid);
        json.add("Logs", new Gson().toJsonTree(assetList));
        return json;
    }

    
    /** 
     * @param status Statuscode
     * @param msg Message
     * @return String as JSON
     */
    public String createResponse(String status, String msg){
        JsonObject json = new JsonObject();
        json.addProperty("status", status);
        json.addProperty("response", msg);
        return json.toString();
    }

    
    /** 
     * @param msg Message
     * @return String as JSON
     */
    public String createSuccessResponse(String msg){
        return createResponse("200", msg);
    }

    
    /** 
     * @param msg Message 
     * @return String as JSON
     */
    public String createFailResponse(String msg){
        return createResponse("400", msg);
    }

    
    /** 
     * @param b
     * @return String
     */
    public String createFailResponse(boolean b){
        JsonObject json = new JsonObject();
        json.addProperty("status", "400");
        json.addProperty("response", b);
        return json.toString();
    }

    
    /** 
     * @param b
     * @return String
     */
    public String createSuccessResponse(boolean b){
        JsonObject json = new JsonObject();
        json.addProperty("status", "200");
        json.addProperty("response", b);
        return json.toString();
    }


    public JsonObject parseResponse(String response){
        return new Gson().fromJson(response, JsonObject.class);
    }

    /** 
     * @param context Hyperledger fabric context
     * @param mspid Mspid of the organisation, which owns the sensor
     * @param sensorid Organisation wide unique sensor id
     * @param timestamp Timestamp of the transaction
     * @return boolean
     */
    public boolean assetExists(Context context, String mspid, String sensorid, String timestamp) {
        ChaincodeStub stub = context.getStub(); 
        CompositeKey key = stub.createCompositeKey("mspid~sensorid~timestamp", new String[] {mspid, sensorid, timestamp});
        String result = stub.getStringState(key.toString());
        return (result != null && result.length() > 0);
    }
}
