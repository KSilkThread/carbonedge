package qal3contract;

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


@Contract(
        name = "Qal2Contract",
        info = @Info(
                title = "Qal2 chaincode",
                description = "A smart contract to save the QAL2 Certificate",
                version = "0.0.1-Alpha",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "janbiermann24@gmail.com",
      
                        name = "Jan")))
@Default
public class QAL3Contract implements ContractInterface {

    private final String keyprefix = "org~id";

    public void createCertificate(Context ctx, String sensorid, String ownerorg, float derivationprecision, float derivationdrift){
        if(certificateExists(ctx, sensorid, ownerorg)){
            throw new ChaincodeException("Certificate already exists");
        }

        ChaincodeStub stub = ctx.getStub();
        CompositeKey key = new CompositeKey(keyprefix, new String[] {ownerorg, sensorid});
        QAL3Certificate certificate = new QAL3Certificate(sensorid, ownerorg, derivationprecision, derivationdrift);
        stub.putStringState(key.toString(), certificate.toJSON());
    }

    public void deleteCertificate(Context ctx, String sensorid, String ownerorg){
        if(!certificateExists(ctx, sensorid, ownerorg)){
            throw new ChaincodeException("Certificate already exists");
        }

        ChaincodeStub stub = ctx.getStub();
        CompositeKey key = new CompositeKey(keyprefix, new String[] {ownerorg, sensorid});
        stub.delState(key.toString());
    }

    public void certify(){

    }

    public String getCertificate(Context ctx, String sensorid, String ownerorg){
        if(!certificateExists(ctx, sensorid, ownerorg)){
            throw new ChaincodeException("Certificate already exists");
        }

        ChaincodeStub stub = ctx.getStub();
        CompositeKey key = new CompositeKey(keyprefix, new String[] {ownerorg, sensorid});
        return stub.getStringState(key.toString());

    }
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean certificateExists(Context ctx, String sensorid, String ownerorg){
        ChaincodeStub stub = ctx.getStub();
        CompositeKey key = new CompositeKey(keyprefix, new String[] {ownerorg, sensorid});
        String result = stub.getStringState(key.toString());
        return (result != null && result.length() > 0);
    }
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean isValid(){
        return !isExpired() && checkCusum();
        
    }

    public boolean isExpired(){
        return false;

    }

    public boolean checkCusum(){
        return false;

    }

    public boolean checkDrift(){
        return false;

    }

    public boolean checkPrecision(){
        return false;

    }
    
}
