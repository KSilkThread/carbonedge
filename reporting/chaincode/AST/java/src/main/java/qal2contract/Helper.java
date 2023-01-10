package qal2contract;

import org.hyperledger.fabric.shim.ledger.CompositeKey;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Helper {

    public String createResponse(String status, String msg){
        JsonObject json = new JsonObject();
        json.addProperty("status", status);
        json.addProperty("response", msg);
        return json.toString();
    }

    public String createSuccessResponse(String msg){
        return createResponse("200", msg);
    }

    public String createFailureResponse(String msg){
        return createResponse("400", msg);
    }

    public String createResponse(String status, boolean msg){
        JsonObject json = new JsonObject();
        json.addProperty("status", status);
        json.addProperty("response", msg);
        return json.toString();
    }

    public String createSuccessResponse(boolean msg){
        return createResponse("200", msg);
    }

    public String createFailureResponse(boolean msg){
        return createResponse("400", msg);
    }

    public JsonObject parseJson(String jsonString){
        return new Gson().fromJson(jsonString, JsonObject.class);
    }

    public CompositeKey createCompositeKey(String keyprefix, String sensorid, String ownerorganisation){
        return new CompositeKey(keyprefix, new String[] {ownerorganisation, sensorid});
    }

    public boolean isSuccess(String response){
        JsonObject object = parseJson(response);
        return object.get("status").getAsString().equals("200");
    }
    
}
