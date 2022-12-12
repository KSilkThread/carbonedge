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
import org.hyperledger.fabric.shim.ChaincodeException;
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
                        name = "Jan")))


@Default
public class MonitoringContract implements ContractInterface {

    String keyPrefixString = "mspid~sensorid~timestamp";

    Helper helper = new Helper();

    //init
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String init(final Context context){

        JsonObject json = new JsonObject();
        json.addProperty("status", "200");
        json.addProperty("response", "Init");
        return json.toString();

    }

    //push
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void pushData(final Context context, final String sensorid, final int data){

        final ChaincodeStub stub = context.getStub();
        final ClientIdentity clientIdentity = context.getClientIdentity();
        final String timestamp = stub.getTxTimestamp().toString();
        final String cmspID = clientIdentity.getMSPID();

        Response response = stub.invokeChaincodeWithStringArgs("ValidationContract", List.of("checkCertificates", sensorid, cmspID), stub.getChannelId());  
            if(response.getStatus() != Status.SUCCESS){
                throw new ChaincodeException("Invalid Certificate");
            }

        //final String txid = stub.getTxId();
        
        CompositeKey key = stub.createCompositeKey(keyPrefixString, new String[] {cmspID, sensorid, timestamp});
        
        MonitoringAsset asset = new MonitoringAsset(sensorid, cmspID, data, timestamp);
        stub.putStringState(key.toString(), asset.toJSON());
    }

    //Necessary ???
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void deleteData(final Context context){
        
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String queryDataByOrg(final Context context, final String mspid){
        ChaincodeStub stub = context.getStub();
        CompositeKey key = new CompositeKey(keyPrefixString, new String[] {mspid});

        int result = 0;

        QueryResultsIterator<KeyValue> iterator = stub.getStateByPartialCompositeKey(key.toString());

        for (KeyValue kv: iterator){
            result += MonitoringAsset.fromJSON(kv.getStringValue()).getData();
        }

    
        return String.valueOf(result);    
    }


    //TODO querySensor
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public void querySensor(final Context context){

    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean dataExists(final Context context, final String mspid, final String sensorid, final String timestamp){
        ChaincodeStub stub = context.getStub(); 
        CompositeKey key = stub.createCompositeKey(keyPrefixString, new String[] {mspid, sensorid, timestamp});
        String result = stub.getStringState(key.toString());
        return (result != null && result.length() > 0);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String readMonitoringAsset(Context context, final String mspid, final String sensorid, final String timestamp){

        if(!dataExists(context, mspid, sensorid, timestamp)){
                throw new ChaincodeException("Asset does not exists!");
        }
        ChaincodeStub stub = context.getStub();
        CompositeKey key = stub.createCompositeKey(keyPrefixString , new String[] {mspid, sensorid, timestamp});
        return stub.getStringState(key.toString());
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String queryOrgEntries(Context context, final String mspid){

        ChaincodeStub stub = context.getStub();
        CompositeKey key = new CompositeKey(keyPrefixString, new String[] {mspid});
        List<MonitoringAsset> assetList = new ArrayList<>();

        QueryResultsIterator<KeyValue> iterator = stub.getStateByPartialCompositeKey(key.toString());

        for(KeyValue result: iterator){
            assetList.add(MonitoringAsset.fromJSON(result.getStringValue()));
        }

        return helper.prettyOrgJson(assetList, mspid).toString();        
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String querySensorEntries(final Context context, final String mspid, final String sensorid){

        ChaincodeStub stub = context.getStub();
        CompositeKey key = new CompositeKey(keyPrefixString, new String[] {mspid, sensorid});
        List<MonitoringAsset> assetList = new ArrayList<>();

        QueryResultsIterator<KeyValue> iterator = stub.getStateByPartialCompositeKey(key.toString());

        for(KeyValue kv: iterator){
            assetList.add(MonitoringAsset.fromJSON(kv.getStringValue()));
        }

        return helper.prettySensorJson(assetList, sensorid).toString();
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String queryConsortiumEntries(final Context context){

        ChaincodeStub stub = context.getStub();
        String channel = stub.getChannelId();
        CompositeKey key = new CompositeKey(keyPrefixString);
        List<MonitoringAsset> assetList = new ArrayList<>();

        QueryResultsIterator<KeyValue> iterator = stub.getStateByPartialCompositeKey(key.toString());

       
        
        for(KeyValue kv: iterator){
            assetList.add(MonitoringAsset.fromJSON(kv.getStringValue()));
        } 
        

        return helper.prettyConsortiumJson(assetList, channel).toString();
    }
    
}