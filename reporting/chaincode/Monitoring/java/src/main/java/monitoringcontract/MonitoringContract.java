package monitoringcontract;

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
import org.hyperledger.fabric.shim.Chaincode.Response.Status;
import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import com.google.gson.JsonObject;

@Contract(
        name = "MonitoringContract",
        info = @Info(
                title = "Monitoring Contract",
                description = "A simple chaincode with main focus on high throughput",
                version = "0.0.1-Alpha",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "janbiermann24@gmail.com",
                        name = "janzumbier")))

@Default
public class MonitoringContract implements ContractInterface {

    String keyPrefixString = "mspid~sensorid~timestamp";

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
     * Submits a MonitoringAsset to the ledger 
     * 
     * @param context Hyperledger fabric context
     * @param sensorid Id of the sensor which must be equal with the SubjectDN CN at the X.509 certificate
     * @param data Data which should be invoked to the ledger
     * @return String Response
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String pushMonitoringAsset(final Context context, final String sensorid, final int data){

        final ChaincodeStub stub = context.getStub();
        final ClientIdentity clientIdentity = context.getClientIdentity();
        final String timestamp = stub.getTxTimestamp().toString();
        final String cmspID = clientIdentity.getMSPID();
        final String checkid = clientIdentity.getX509Certificate().getSubjectDN().getName();
        final String isid = "CN="+sensorid+", OU=client";

        if(!isid.equals(checkid)){
            return helper.createFailResponse("You do not have the permission to do that");
        }

        Response response = stub.invokeChaincodeWithStringArgs("ValidationContract", List.of("checkCertificates", sensorid, cmspID), stub.getChannelId());  
            if(response.getStatus() != Status.SUCCESS){
                return helper.createFailResponse("Invalid Certificate");
            }
        
        CompositeKey key = stub.createCompositeKey(keyPrefixString, new String[] {cmspID, sensorid, timestamp});
        
        MonitoringAsset asset = new MonitoringAsset(sensorid, cmspID, data, timestamp);
        stub.putStringState(key.toString(), asset.toJSON());
        return helper.createSuccessResponse("MonitoringAsset successfully added!");
    }

    
    /** 
     * Checks, if a specic MonitoringAsset exists on the ledger
     * 
     * @param context Hyperledger fabric context
     * @param mspid Mspid of the organisation, which owns the sensor
     * @param sensorid Organisation wide unique sensor id
     * @param timestamp Timestamp of the transaction
     * @return String Response 
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String monitoringExists(final Context context, final String mspid, final String sensorid, final String timestamp){
        ChaincodeStub stub = context.getStub(); 
        CompositeKey key = stub.createCompositeKey(keyPrefixString, new String[] {mspid, sensorid, timestamp});
        String result = stub.getStringState(key.toString());
        if(result != null && result.length() > 0){
            return helper.createSuccessResponse(true);
        }
        return helper.createFailResponse(false);
    }
        

    
    /**
     * Queries a specific MonitoringAsset
     * 
     * @param context Hyperledger fabric context
     * @param mspid Mspid of the organisation, which owns the sensor
     * @param sensorid Organisation wide unique sensor id
     * @param timestamp Timestamp of the transaction
     * @return String Response
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String readMonitoringAsset(Context context, final String mspid, final String sensorid, final String timestamp){

        if(!helper.assetExists(context, mspid, sensorid, timestamp)){
                helper.createFailResponse("Asset does not exists");
        }
        ChaincodeStub stub = context.getStub();
        CompositeKey key = stub.createCompositeKey(keyPrefixString , new String[] {mspid, sensorid, timestamp});
        return helper.createSuccessResponse(stub.getStringState(key.toString()));
    }

    
    /**
     * Gets all MonitoringAssets, which belong to one specific organisation
     *  
     * @param context Hyperledger fabric context
     * @param mspid Mspid of the organisation, which owns the sensor
     * @return String Response
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String queryOrgEntries(Context context, final String mspid){

        ChaincodeStub stub = context.getStub();
        CompositeKey key = new CompositeKey(keyPrefixString, new String[] {mspid});
        List<MonitoringAsset> assetList = new ArrayList<>();

        QueryResultsIterator<KeyValue> iterator = stub.getStateByPartialCompositeKey(key.toString());

        for(KeyValue result: iterator){
            assetList.add(MonitoringAsset.fromJSON(result.getStringValue()));
        }

        return helper.createSuccessResponse(helper.prettyOrgJson(assetList, mspid).toString());        
    }

    
    /**
     * Queries all MonitoringAssets, which belong to one specific organisation and were invokey by a specific sensor 
     * 
     * @param context Hyperledger fabric context
     * @param mspid Mspid of the organisation, which owns the sensor
     * @param sensorid Organisation wide unique sensor id
     * @return String Response
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String querySensorEntries(final Context context, final String mspid, final String sensorid){

        ChaincodeStub stub = context.getStub();
        CompositeKey key = new CompositeKey(keyPrefixString, new String[] {mspid, sensorid});
        List<MonitoringAsset> assetList = new ArrayList<>();

        QueryResultsIterator<KeyValue> iterator = stub.getStateByPartialCompositeKey(key.toString());

        for(KeyValue kv: iterator){
            assetList.add(MonitoringAsset.fromJSON(kv.getStringValue()));
        }

        return helper.createSuccessResponse(helper.prettySensorJson(assetList, sensorid).toString());
    }
    
}