package validationcontract;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.hyperledger.fabric.contract.ClientIdentity;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.Chaincode.Response;
import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Contract(
        name = "ValidationContract",
        info = @Info(
                title = "Validation Contract",
                description = "This smart contract checks the necessasry certificates",
                version = "0.0.1-Alpha",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "janbiermann24@gmail.com",
                        name = "Jan")))

@Default
public class ValidationContract implements ContractInterface {

    String keyPrefixString = "ownerorg~sensorid";

    Helper helper = new Helper();

    /**
     *  
     * @param context Hyperledger Fabric context
     * @return String 
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String init(final Context context){

        JsonObject json = new JsonObject();
        json.addProperty("status", "200");
        json.addProperty("response", "Init");
        return json.toString();

    }

    
    /**
     * Creates a ValidationAsset
     *   
     * @param ctx Hyperledger fabric context
     * @param sensorid Organisation wide unique id
     * @param ownerorganisation Organisation which owns the sensor
     * @param requiredCertificates Array of required certificates
     * 
     * @return Response
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String createValidationAsset(Context ctx, String sensorid, String ownerorganisation, String... requiredCertificates){
        if(helper.isSuccess(assetExists(ctx, sensorid, ownerorganisation))){
            return helper.createFailureResponse("ValidationAsset already exists");
        }

        final ClientIdentity clientIdentity = ctx.getClientIdentity();
        final String cmspID = clientIdentity.getMSPID();

        if(cmspID.equals(ownerorganisation)){
            helper.createFailureResponse("You are not allowed to create this asset");
        }

        ChaincodeStub stub = ctx.getStub();
        ValidationAsset asset = new ValidationAsset(sensorid, ownerorganisation, requiredCertificates);
        stub.putStringState(helper.createCompositeKey(keyPrefixString, sensorid, ownerorganisation).toString(), asset.toJSON());
        return helper.createSuccessResponse("ValidationAsset successfully added!");
    }

    
    /**
     * Evaluates, if a specefic MonitoringAsset exists on the ledger  
     * @param ctx Hyperledger fabric context
     * @param sensorid Organisation wide unique id
     * @param ownerorganisation Organisation which owns the sensor
     * @return String Response
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String assetExists(Context ctx, String sensorid, String ownerorganisation){
        ChaincodeStub stub = ctx.getStub();
        String result = stub.getStringState(helper.createCompositeKey(keyPrefixString, sensorid, ownerorganisation).toString());
        if(result != null && result.length() > 0){
            return helper.createSuccessResponse(true);
        }
        return helper.createFailureResponse(false);
    }

    
    /**
     * Queries a specific ValidationAsset 
     * @param ctx Hyperledger fabric context
     * @param sensorid Organisation wide unique id
     * @param ownerorganisation Organisation which owns the sensor
     * @return String Response
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String getValidationAsset(Context ctx, String sensorid, String ownerorganisation){
        if(!helper.isSuccess(assetExists(ctx, sensorid, ownerorganisation))){
            return helper.createFailureResponse("ValidationAsset does not exist");
        }
        ChaincodeStub stub = ctx.getStub();
        return stub.getStringState(helper.createCompositeKey(keyPrefixString, sensorid, ownerorganisation).toString());
    }

    
    /**
     * Updates a specific ValidationAsset 
     * @param ctx Hyperledger fabric context
     * @param sensorid Organisation wide unique id
     * @param org Organisation which owns the sensor
     * @param certs Array of required certificates
     * @return String Response
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String updateValidationAsset(Context ctx, String sensorid, String ownerorganisation, String... certs){
        if(!helper.isSuccess(assetExists(ctx, sensorid, ownerorganisation))){
            return helper.createFailureResponse("Asset does not exist");
        }

        final ClientIdentity clientIdentity = ctx.getClientIdentity();
        final String cmspID = clientIdentity.getMSPID();
        
        if(cmspID.equals(ownerorganisation)){
            return helper.createFailureResponse("You are not allowed to update this asset");
        }

        ChaincodeStub stub = ctx.getStub();
        ValidationAsset asset = new ValidationAsset(sensorid, ownerorganisation, certs);
        stub.putStringState(helper.createCompositeKey(keyPrefixString, sensorid, ownerorganisation).toString(), asset.toJSON());
        return helper.createSuccessResponse("Asset successfully updated");
    }

    
    /**
     * Checks if all certificates of a specific ValidationAsset are valid
     * @param ctx Hyperledger fabric context
     * @param sensorid Organisation wide unique id
     * @param org Organisation which owns the sensor
     * @return String Response
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String checkCertificates(Context ctx, String sensorid, String ownerorganisation){

        if(!helper.isSuccess(assetExists(ctx, sensorid, ownerorganisation))){
            return helper.createFailureResponse("Asset does not exist");
        }

        ChaincodeStub stub = ctx.getStub();
        ValidationAsset asset = ValidationAsset.fromJSON(getValidationAsset(ctx, sensorid, ownerorganisation));

        for(String chaincode: asset.getRequiredcerts()){
            Response response = stub.invokeChaincodeWithStringArgs(chaincode, List.of("isValid", sensorid, ownerorganisation), stub.getChannelId());
            String payload = new String(response.getPayload(), StandardCharsets.UTF_8);
            if(!helper.isSuccess(payload)){
                return helper.createFailureResponse(helper.parseJson(payload).get("response").getAsString() + " " + chaincode);
            }
        }
        
        return helper.createSuccessResponse("All certificates are valid");
    }

    
    /**
     * Deletes a specific ValidationAsset 
     * @param ctx Hyperledger fabric context
     * @param sensorid Organisation wide unique id
     * @param org Organisation which owns the sensor
     * @return String Response
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String deleteValidationAsset(Context ctx, String sensorid, String ownerorganisation){

        if(!helper.isSuccess(assetExists(ctx, sensorid, ownerorganisation))){
            return helper.createFailureResponse("Asset does not exist");
        }

        ChaincodeStub stub = ctx.getStub();
        stub.delState(helper.createCompositeKey(keyPrefixString, sensorid, ownerorganisation).toString());
        return helper.createSuccessResponse("Asset deleted successfully");
    }

    
    /**
     * Queries all existing ValidationAsset
     * @param ctx Hyperledger fabric context
     * @return String JSON Array of ValidationAssets
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String queryAll(Context ctx){

        ChaincodeStub stub = ctx.getStub();
        CompositeKey key = new CompositeKey(keyPrefixString);
        List<ValidationAsset> assetList = new ArrayList<>();

        QueryResultsIterator<KeyValue> iterator = stub.getStateByPartialCompositeKey(key.toString());

        for(KeyValue kv: iterator){
            assetList.add(ValidationAsset.fromJSON(kv.getStringValue()));
        } 
        return new Gson().toJson(assetList);
    }        


}
