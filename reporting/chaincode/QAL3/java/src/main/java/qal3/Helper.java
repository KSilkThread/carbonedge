package qal3;

import java.math.BigDecimal;

import org.hyperledger.fabric.shim.ledger.CompositeKey;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Helper {
    //Static drift constants
    final BigDecimal constxdrift = new BigDecimal("0.501");
    final BigDecimal constydrift = new BigDecimal("2.85");

    //Static precision constants
    final BigDecimal constxprecision = new BigDecimal("1.85");
    final BigDecimal constyprecision = new BigDecimal("6.90");
    final BigDecimal constzprecision = new BigDecimal("2");

    
    public CusumDrift calculateDrift(BigDecimal sAmsdrift, CusumDrift[] values, BigDecimal measurement){
        BigDecimal threshold = constxdrift.multiply(sAmsdrift);
        BigDecimal testvalue = constydrift.multiply(sAmsdrift);

        CusumDrift lastobject = values[values.length-1];
        BigDecimal sumpos = lastobject.getSumpos().add(measurement).subtract(threshold).compareTo(BigDecimal.ZERO) == 1 ? lastobject.getSumpos().add(measurement).subtract(threshold) : BigDecimal.ZERO;
        BigDecimal sumneg = lastobject.getSumneg().subtract(measurement).subtract(threshold).compareTo(BigDecimal.ZERO) == 1 ? lastobject.getSumneg().subtract(measurement).subtract(threshold) : BigDecimal.ZERO;
        return new CusumDrift(measurement, sumpos, sumneg, (sumpos.compareTo(testvalue) == 1), (sumneg.compareTo(testvalue) == 1) );
    }

    public CusumPrecision calculatePrecision(BigDecimal sAmsPrecision, CusumPrecision[] values, BigDecimal measurement){

        BigDecimal thresholdsquared = constxprecision.multiply(sAmsPrecision).multiply(sAmsPrecision);
        BigDecimal testvalue = constyprecision.multiply(sAmsPrecision).multiply(sAmsPrecision);
    
        CusumPrecision lastobject = values[values.length-1];

        BigDecimal delta = measurement.subtract(lastobject.getValue());
        BigDecimal dpowhalf = delta.multiply(delta).divide(constzprecision);
        BigDecimal s = lastobject.getS().compareTo(BigDecimal.ZERO) == 1 ? lastobject.getS().add(dpowhalf).subtract(thresholdsquared) : BigDecimal.ZERO.add(dpowhalf).subtract(thresholdsquared);

        return new CusumPrecision(measurement, delta, dpowhalf, s, s.compareTo(testvalue) == 1);
    }

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
    

