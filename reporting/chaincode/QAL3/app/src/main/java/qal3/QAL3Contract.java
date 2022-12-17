package qal3;

import java.math.BigDecimal;
import java.time.Instant;

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
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Contract(
        name = "QAL3 Contract",
        info = @Info(
                title = "Qal3 Contract",
                description = "This contract handles the QAL3 process",
                version = "0.0.1-Alpha",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "janbiermann24@gmail.com",
                        name = "Jan")))

@Default
public class QAL3Contract implements ContractInterface {


    private final String kexprefix = "org~id";
    Helper helper = new Helper();

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String init(final Context context){

        JsonObject json = new JsonObject();
        json.addProperty("status", "200");
        json.addProperty("response", "Init");
        return json.toString();

    }

    
    /** 
     * @param ctx
     * @param sensorid
     * @param ownerorg
     * @param sPrecision
     * @param sDrift
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void createCertificate(Context ctx, String sensorid, String ownerorg, String sPrecision, String sDrift, String maintainanceintervall){
        ChaincodeStub stub = ctx.getStub();
        ClientIdentity clientid = ctx.getClientIdentity();

        if(clientid.getMSPID().equals(ownerorg)){
            throw new ChaincodeException("You are not Allowed to create a Certificate for yourself");
        }

        if(certificateExists(ctx, sensorid, ownerorg)){
            throw new ChaincodeException("Certificate already exists");
        }

        QAL3Certificate certificate = new QAL3Certificate(sensorid, ownerorg, sDrift, sPrecision, Integer.parseInt(maintainanceintervall));
        CompositeKey key = new CompositeKey(kexprefix, new String[] {ownerorg, sensorid});
        stub.putStringState(key.toString(), certificate.toJSON());
    }

    
    /** 
     * @param ctx
     * @param sensorid
     * @param ownerorg
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void deleteCertificate(Context ctx, String sensorid, String ownerorg){
        ChaincodeStub stub = ctx.getStub();
        ClientIdentity clientid = ctx.getClientIdentity();

        if(clientid.getMSPID().equals(ownerorg)){
            throw new ChaincodeException("You can not delete this certificate");
        }

        if(!certificateExists(ctx, sensorid, ownerorg)){
            throw new ChaincodeException("The certificate does not exist");
        }

        CompositeKey key = new CompositeKey(kexprefix, new String[] {ownerorg, sensorid});
        stub.delState(key.toString());
    }

    
    
    
    /** 
     * @param ctx
     * @param sensorid
     * @param ownerorg
     * @return boolean
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean certificateExists(Context ctx, String sensorid, String ownerorg){
        ChaincodeStub stub = ctx.getStub();
        CompositeKey key = new CompositeKey(kexprefix, new String[] {ownerorg, sensorid});
        String result = stub.getStringState(key.toString());
        return ((result != null) && (result.length() > 0));
    }

    
    
    
    /** 
     * @param ctx
     * @param sensorid
     * @param ownerorg
     * @return String
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String getCertificate(Context ctx, String sensorid, String ownerorg){
        ChaincodeStub stub = ctx.getStub();

        if(!certificateExists(ctx, sensorid, ownerorg)){
            throw new ChaincodeException("The certificate does not exist");
        }

        CompositeKey key = new CompositeKey(kexprefix, new String[] {ownerorg, sensorid});
        return stub.getStringState(key.toString());
    }
    
    /** 
     * @param ctx
     * @param sensorid
     * @param ownerorg
     * @return String
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String getDriftZero(Context ctx, String sensorid, String ownerorg){
        String result = getCertificate(ctx, sensorid, ownerorg);
        QAL3Certificate certificate = QAL3Certificate.fromJSON(result);
        return new Gson().toJson(certificate.getDriftzero());
    }
    
    /** 
     * @param ctx
     * @param sensorid
     * @param ownerorg
     * @return String
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String getDriftReference(Context ctx, String sensorid, String ownerorg){
        String result = getCertificate(ctx, sensorid, ownerorg);
        QAL3Certificate certificate = QAL3Certificate.fromJSON(result);
        return new Gson().toJson(certificate.getDriftreference());
    }
    
    /** 
     * @param ctx
     * @param sensorid
     * @param ownerorg
     * @return String
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String getPrecisionZero(Context ctx, String sensorid, String ownerorg){
        String result = getCertificate(ctx, sensorid, ownerorg);
        QAL3Certificate certificate = QAL3Certificate.fromJSON(result);
        return new Gson().toJson(certificate.getPrecisionzero());
    }
    
    /** 
     * @param ctx
     * @param sensorid
     * @param ownerorg
     * @return String
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String getPrecisionReference(Context ctx, String sensorid, String ownerorg){
        String result = getCertificate(ctx, sensorid, ownerorg);
        QAL3Certificate certificate = QAL3Certificate.fromJSON(result);
        return new Gson().toJson(certificate.getPrecisionreference());
    }

    
    /** 
     * @param ctx
     * @param sensorid
     * @param ownerorg
     * @param dz
     * @param dr
     * @param pz
     * @param pr
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void commitValues(Context ctx, String sensorid, String ownerorg, String dz, String dr, String pz, String pr){
        ChaincodeStub stub = ctx.getStub();

        String certjson = getCertificate(ctx, sensorid, ownerorg);
        QAL3Certificate certificate = QAL3Certificate.fromJSON(certjson);
        certificate.appendZeroDrift(helper.calculateDrift(certificate.getsAmsdrift(), certificate.getDriftzero(), new BigDecimal(dz)));
        certificate.appendReferenceDrift(helper.calculateDrift(certificate.getsAmsdrift(), certificate.getDriftreference(), new BigDecimal(dr)));
        certificate.appendZeroPrecision(helper.calculatePrecision(certificate.getsAmsprecision(), certificate.getPrecisionzero(), new BigDecimal(pz)));
        certificate.appendReferencePrecision(helper.calculatePrecision(certificate.getsAmsprecision(), certificate.getPrecisionreference(), new BigDecimal(pr)));

        certificate.updateExpirydate(stub.getTxTimestamp());

        CompositeKey key = new CompositeKey(kexprefix, new String[] {ownerorg, sensorid});
        stub.putStringState(key.toString(), certificate.toJSON());
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean isValid(Context ctx, String sensorid, String ownerorg){
        return !isExpired(ctx, sensorid, ownerorg) && !hasdecreasedPrecision(ctx, sensorid, ownerorg) && !hasDrift(ctx, sensorid, ownerorg);
    }


    public boolean hasDrift(Context ctx, String sensorid, String ownerorg) {
        QAL3Certificate cert = QAL3Certificate.fromJSON(getCertificate(ctx, sensorid, ownerorg));
        CusumDrift lastzeropoint = cert.getDriftzero()[cert.getDriftzero().length-1];
        CusumDrift lastreferencepoint = cert.getDriftreference()[cert.getDriftreference().length-1];
        return lastzeropoint.isPosdrift() || lastzeropoint.isNegdrift() || lastreferencepoint.isPosdrift() || lastreferencepoint.isNegdrift();
    }


    public boolean hasdecreasedPrecision(Context ctx, String sensorid, String ownerorg) {
        QAL3Certificate cert = QAL3Certificate.fromJSON(getCertificate(ctx, sensorid, ownerorg));
        CusumPrecision lastzeropoint = cert.getPrecisionzero()[cert.getPrecisionzero().length-1];
        CusumPrecision lastreferencepoint = cert.getPrecisionreference()[cert.getPrecisionreference().length-1];
        return lastzeropoint.isPrecisiondecreased() || lastreferencepoint.isPrecisiondecreased();
    }


    public boolean isExpired(Context ctx, String sensorid, String ownerorg) {
        QAL3Certificate cert = QAL3Certificate.fromJSON(getCertificate(ctx, sensorid, ownerorg));
        ChaincodeStub stub = ctx.getStub();

        if(cert.getExpirydate() == null){
            return true;
        }
        Instant expire = Instant.parse(cert.getExpirydate());
        return stub.getTxTimestamp().isAfter(expire);
    }
    
}
