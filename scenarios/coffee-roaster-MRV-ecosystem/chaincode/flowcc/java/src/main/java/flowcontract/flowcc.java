package flowcontract;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.hyperledger.fabric.Logger;
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
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import com.google.gson.JsonObject;

import flowcontract.models.InspectorOrganisations;


@Contract(
        name = "FlowContract",
        info = @Info(
                title = "FlowContract",
                description = "This contract saves, checks and manages the flows",
                version = "0.0.1-Alpha",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "janbiermann24@gmail.com",
                        name = "Jan")))


@Default
public class flowcc implements ContractInterface {

    final Logger logger = Logger.getLogger(flowcc.class);


    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String init(final Context context){

        JsonObject json = new JsonObject();
        json.addProperty("status", "200");
        json.addProperty("response", "Init");
        return json.toString();

    }



    //insert
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String insert(final Context context, final String organisation, final String flow){

        ChaincodeStub stub = context.getStub();
        String mspid = context.getClientIdentity().getMSPID();


        if(!InspectorOrganisations.contains(mspid)){
            logger.error("The organisation " + mspid + " is not allowed to insert or change a flow");
            throw new ChaincodeException("You are not allowed to insert or change a flow!");
        }

        CompositeKey key = new CompositeKey(organisation);

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedflow = digest.digest(flow.getBytes(StandardCharsets.UTF_8));

            String hashedflowstring = new String(hashedflow, StandardCharsets.UTF_8);

            stub.putStringState(key.toString(), hashedflowstring);

            logger.info(hashedflowstring+ " added");

            return "Flow of organisation" + organisation + "was successfully added hash: " + hashedflowstring;

        } catch (NoSuchAlgorithmException e) {
            return e.getMessage();
        }

    }	
    //check
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean check(final Context context, final String flow){
        ChaincodeStub stub = context.getStub();
        String mspid = context.getClientIdentity().getMSPID();

        CompositeKey key = new CompositeKey(mspid);
        String hashedflowledger = stub.getStringState(key.toString());

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedflow = digest.digest(flow.getBytes(StandardCharsets.UTF_8));
            String hashedflowstring = new String(hashedflow, StandardCharsets.UTF_8);

            return hashedflowledger.equals(hashedflowstring);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
            return false;
        }

    }

    // get
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String get(final Context context, final String organisation){
        ChaincodeStub stub = context.getStub();
        String mspid = context.getClientIdentity().getMSPID();

        if((!InspectorOrganisations.contains(mspid)) && (!organisation.equals(mspid))){
            logger.error("The organisation " + mspid + " is not allowed to get a flow from a foreign organisation");
            throw new ChaincodeException("You are not allowed to get a flow from a foreign organisation!");
        }
        CompositeKey key = new CompositeKey(organisation);
        return stub.getStringState(key.toString());

    }
    //delete
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String delete(final Context context, final String organisation){
        ChaincodeStub stub = context.getStub();
        String mspid = context.getClientIdentity().getMSPID();

        if((!InspectorOrganisations.contains(mspid))){
            logger.error("The organisation " + mspid + " is not allowed to delete a flow ");
            throw new ChaincodeException("You are not allowed to delete a flow!");
        }
        CompositeKey key = new CompositeKey(organisation);
        stub.delState(key.toString());
        return "Flow successfully deleted";        
    }



    
}
