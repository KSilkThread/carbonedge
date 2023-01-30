package referencecontract;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import com.google.gson.JsonObject;

@Contract(
        name = "ReferenceContract",
        info = @Info(
                title = "ReferenceContract",
                description = "A smart contract to manage reference models",
                version = "0.0.1-Alpha",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "janbiermann24@gmail.com",
      
                        name = "Jan")))

@Default
public class ReferenceContract implements ContractInterface {

        Helper helper = new Helper();
        private final String keyprefix = "version";

        
        /** 
         * @param context Hyperledger Fabric context
         * @return String
         */
        @Transaction(intent = Transaction.TYPE.SUBMIT)
        public String init(Context context){

                JsonObject json = new JsonObject();
                json.addProperty("status", "200");
                json.addProperty("response", "Init");
                return json.toString();
        }

        
        /** 
         * @param context Hyperledger Fabric context
         * @param version Reference Model id
         * @param benchmark value of benchmark
         * @return String Response
         */
        @Transaction(intent = Transaction.TYPE.SUBMIT)
        public String pushReferenceModel(Context context, int version, double benchmark){
                if(helper.isSuccess(modelExists(context, version))){
                        return helper.createFailureResponse("Moidel already exists");
                }
                ChaincodeStub stub = context.getStub();
                ReferenceModel model = new ReferenceModel(version, benchmark);
                CompositeKey key = new CompositeKey(keyprefix, new String[]{String.valueOf(version)});
                stub.putStringState(key.toString(), model.toJson());
                return helper.createSuccessResponse("Model added succesfully");
        }

        
        /** 
         * @param context Hyperledger Fabric context
         * @param version Reference Model id
         * @return String Response
         */
        @Transaction(intent = Transaction.TYPE.EVALUATE)
        public String modelExists(Context context, int version){
                ChaincodeStub stub = context.getStub();
                CompositeKey key = new CompositeKey(keyprefix, new String[] {String.valueOf(version)});
                String result = stub.getStringState(key.toString());
                if((result != null) && (result.length() > 0)){
                        return helper.createSuccessResponse(true);
                } else {
                        return helper.createFailureResponse(false);
                }
        }

        
        /** 
         * @param context Hyperledger Fabric context
         * @param version Reference Model id
         * @return String Response
         */
        @Transaction(intent = Transaction.TYPE.EVALUATE)
        public String readReferenceAsset(Context context, int version){
                ChaincodeStub stub = context.getStub();

                if(!helper.isSuccess(modelExists(context, version))){
                        return helper.createFailureResponse("The model does not exist");
                }
                CompositeKey key = new CompositeKey(keyprefix, new String[]{String.valueOf(version)});
                return helper.createSuccessResponse(stub.getStringState(key.toString()));
        }

        
        /** 
         * @param context Hyperledger Fabric context
         * @return String Response
         */
        @Transaction(intent = Transaction.TYPE.EVALUATE)
        public String getLatest(Context context){
                ChaincodeStub stub = context.getStub();
                CompositeKey key = new CompositeKey(keyprefix);

                QueryResultsIterator<KeyValue> iterator = stub.getStateByPartialCompositeKey(key.toString());

                ReferenceModel latest = null;
                
                for(KeyValue kv: iterator){
                        ReferenceModel model = ReferenceModel.fromJSON(kv.getStringValue());
                        if(latest == null){
                                latest = model;
                                continue;
                        }

                        if(model.getVersion() > latest.getVersion()){
                                latest = model;
                        }
                }
                
                if(latest == null){
                        return helper.createFailureResponse("Models not found");
                } else {
                        return helper.createSuccessResponse(latest.toJson());
                }
                
        }

}
