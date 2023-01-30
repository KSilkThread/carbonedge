package calculationbasedcontract;

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
        name = "Calculationbased Contract",
        info = @Info(
                title = "CalculationbasedContract",
                description = "A smart contract to manage Monitoring Assets",
                version = "0.0.1-Alpha",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "janbiermann24@gmail.com",
                        name = "Jan")))

@Default
public class MonitoringContract implements ContractInterface {

    String keyPrefixString = "mspid~timestamp";

    Helper helper = new Helper();
    
    /** 
     * @param context Hyperledger fabric context
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
     * @param ctx Hyperledger Fabric Context
     * @param mspid Mspid of the organisation
     * @return String Response
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String pushMonitoringAsset(Context ctx, int amount){
        ChaincodeStub stub = ctx.getStub();
        ClientIdentity clientIdentity = ctx.getClientIdentity();
        String timestamp = stub.getTxTimestamp().toString();
        String cmspID = clientIdentity.getMSPID();

        Response response = stub.invokeChaincodeWithStringArgs("ReferenceContract", List.of("getlatest"), stub.getChannelId());
        String result = new String(response.getPayload(), StandardCharsets.UTF_8);
        JsonObject jsonresult = helper.parseResponse(result);  
        if(!jsonresult.get("status").getAsString().equals("200")){
            return helper.createFailureResponse("Model not found!");
        }
        CompositeKey key = stub.createCompositeKey(keyPrefixString, new String[] {cmspID, timestamp});
        double benchmark = jsonresult.get("response").getAsJsonObject().get("benchmark").getAsDouble();
        MonitoringAsset asset = new MonitoringAsset(cmspID, benchmark * amount, timestamp);
        stub.putStringState(key.toString(), asset.toJSON());
        return helper.createSuccessResponse("Asset added successfully");
    }

    
    /** 
     * @param ctx Hyperledger Fabric Context
     * @param mspid Mspid of the organisation
     * @param timestamp Time and date of the transaction
     * @return String Response
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String monitoringExists(Context ctx,String mspid, String timestamp){
        ChaincodeStub stub = ctx.getStub(); 
        CompositeKey key = stub.createCompositeKey(keyPrefixString, new String[] {mspid, timestamp});
        String result = stub.getStringState(key.toString());
        if(result != null && result.length() > 0){
            return helper.createSuccessResponse(true);
        }
        return helper.createFailureResponse(false);
    }

    
    /** 
     * @param ctx Hyperledger Fabric Context
     * @param mspid Mspid of the organisation
     * @param timestamp Time and date of the transaction
     * @return String
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String readMonitoringAsset(Context ctx, String mspid, String timestamp){
        if(!helper.parseResponse(monitoringExists(ctx, mspid, timestamp)).get("status").getAsString().equals("200")){
            return helper.createFailureResponse("Asset does not exists");
        }
        ChaincodeStub stub = ctx.getStub();
        CompositeKey key = stub.createCompositeKey(keyPrefixString , new String[] {mspid, timestamp});
        return helper.createSuccessResponse(stub.getStringState(key.toString()));
    }

    
    /** 
     * @param ctx Hyperledger Fabric Context
     * @param mspid Mspid of the organisation
     * @return String Response
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String queryOrgEntries(Context ctx,String mspid){
        ChaincodeStub stub = ctx.getStub();
        CompositeKey key = new CompositeKey(keyPrefixString, new String[] {mspid});
        List<MonitoringAsset> assetList = new ArrayList<>();
        QueryResultsIterator<KeyValue> iterator = stub.getStateByPartialCompositeKey(key.toString());
        for(KeyValue result: iterator){
            assetList.add(MonitoringAsset.fromJSON(result.getStringValue()));
        }

        return helper.createSuccessResponse(new Gson().toJsonTree(assetList).toString());
    }
    
}
