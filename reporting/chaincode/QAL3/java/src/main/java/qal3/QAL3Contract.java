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

    
    /** 
     * Initializes the contract
     * @param context Hyperledger fabric context
     * @return String response
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String init(final Context context){

        JsonObject json = new JsonObject();
        json.addProperty("status", "200");
        json.addProperty("response", "Init");
        return json.toString();

    }

    
    /**
     * Initialises the certificate  
     * @param ctx Hyperledger fabric context
     * @param sensorid Organisation wide unique sensor id 
     * @param ownerorg Organisation which owns the sensor
     * @param sPrecision Standard derivation of the precision of the sensor measured in the QAL1 inspection
     * @param sDrift Standard derivation of the drift of the sensor measured in the QAL1 inspection
     * @return String response
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String createCertificate(Context ctx, String sensorid, String ownerorg, String sPrecision, String sDrift, String maintainanceintervall){
        ChaincodeStub stub = ctx.getStub();
        ClientIdentity clientid = ctx.getClientIdentity();

        if(clientid.getMSPID().equals(ownerorg)){
            return helper.createFailureResponse("You are not allowed to create a Certificate for yourself");
        }

        if(helper.isSuccess(certificateExists(ctx, sensorid, ownerorg))){
            return helper.createFailureResponse("Certificate already exists");
        }

        try {
            QAL3Certificate certificate = new QAL3Certificate(sensorid, ownerorg, sDrift, sPrecision, Integer.parseInt(maintainanceintervall));
            stub.putStringState(helper.createCompositeKey(kexprefix, sensorid, ownerorg).toString(), certificate.toJSON());
            return helper.createSuccessResponse("Certificate created successfully");
        } catch (NumberFormatException e){
            return helper.createFailureResponse(e.getMessage());
        }
       
    }

    
    /**
     * Deletes a certificate  
     * @param ctx Hyperledger fabric context
     * @param sensorid Organisation wide unique sensor id 
     * @param ownerorg Organisation which owns the sensor
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void deleteCertificate(Context ctx, String sensorid, String ownerorg){
        ChaincodeStub stub = ctx.getStub();
        ClientIdentity clientid = ctx.getClientIdentity();

        if(clientid.getMSPID().equals(ownerorg)){
            helper.createFailureResponse("You can not delete this certificate");
        }

        if(!helper.isSuccess(certificateExists(ctx, sensorid, ownerorg))){
            helper.createFailureResponse("The certificate does not exist");
        }

        CompositeKey key = new CompositeKey(kexprefix, new String[] {ownerorg, sensorid});
        stub.delState(key.toString());
    }

    
    
    
    /**
     * Checks, if a certificate exists 
     * @param ctx Hyperledger fabric context
     * @param sensorid Organisation wide unique sensor id 
     * @param ownerorg Organisation which owns the sensor
     * @return String response
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String certificateExists(Context ctx, String sensorid, String ownerorg){
        ChaincodeStub stub = ctx.getStub();
        CompositeKey key = new CompositeKey(kexprefix, new String[] {ownerorg, sensorid});
        String result = stub.getStringState(key.toString());
        if((result != null) && (result.length() > 0)){
            return helper.createSuccessResponse(true);
        } else {
            return helper.createFailureResponse(false);
        }
    }

    
    
    
    /**
     * Reads a specific certificate from the ledger  
     * @param ctx Hyperledger fabric context
     * @param sensorid Organisation wide unique sensor id 
     * @param ownerorg Organisation which owns the sensor
     * @return String Response
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String getCertificate(Context ctx, String sensorid, String ownerorg){
        ChaincodeStub stub = ctx.getStub();

        if(!helper.isSuccess(certificateExists(ctx, sensorid, ownerorg))){
            return helper.createFailureResponse("The certificate does not exist");
        }

        return helper.createSuccessResponse(stub.getStringState(helper.createCompositeKey(kexprefix, sensorid, ownerorg).toString()));
    }
    
    /**
     * Returns all CUSUM values of the drift zero point measurement 
     * @param ctx Hyperledger fabric context
     * @param sensorid Organisation wide unique sensor id 
     * @param ownerorg Organisation which owns the sensor
     * @return String response
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String getDriftZero(Context ctx, String sensorid, String ownerorg){
        String result = getCertificate(ctx, sensorid, ownerorg);
        QAL3Certificate certificate = QAL3Certificate.fromJSON(result);
        return helper.createSuccessResponse(new Gson().toJson(certificate.getDriftzero()));
    }
    
    /**
     * Returns all CUSUM values of the drift reference point measurement 
     * @param ctx Hyperledger fabric context
     * @param sensorid Organisation wide unique sensor id 
     * @param ownerorg Organisation which owns the sensor
     * @return String response
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String getDriftReference(Context ctx, String sensorid, String ownerorg){
        String result = getCertificate(ctx, sensorid, ownerorg);
        QAL3Certificate certificate = QAL3Certificate.fromJSON(result);
        return helper.createSuccessResponse(new Gson().toJson(certificate.getDriftreference()));
    }
    
    /**
     * Returns all CUSUM values of the drift reference point measurement 
     * @param ctx Hyperledger fabric context
     * @param sensorid Organisation wide unique sensor id 
     * @param ownerorg Organisation which owns the sensor
     * @return String response
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String getPrecisionZero(Context ctx, String sensorid, String ownerorg){
        String result = getCertificate(ctx, sensorid, ownerorg);
        QAL3Certificate certificate = QAL3Certificate.fromJSON(result);
        return helper.createSuccessResponse(new Gson().toJson(certificate.getPrecisionzero()));
    }
    
    /**
     * Returns all CUSUM values of the drift reference point measurement 
     * @param ctx Hyperledger fabric context
     * @param sensorid Organisation wide unique sensor id 
     * @param ownerorg Organisation which owns the sensor
     * @return String response
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String getPrecisionReference(Context ctx, String sensorid, String ownerorg){
        String result = getCertificate(ctx, sensorid, ownerorg);
        QAL3Certificate certificate = QAL3Certificate.fromJSON(result);
        return helper.createSuccessResponse(new Gson().toJson(certificate.getPrecisionreference()));
    }

    /** 
     *@param ctx Hyperledger fabric context
     * @param sensorid Organisation wide unique sensor id 
     * @param ownerorg Organisation which owns the sensor
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
        try {
             certificate.appendZeroDrift(helper.calculateDrift(certificate.getsAmsdrift(), certificate.getDriftzero(), new BigDecimal(dz)));
            certificate.appendReferenceDrift(helper.calculateDrift(certificate.getsAmsdrift(), certificate.getDriftreference(), new BigDecimal(dr)));
            certificate.appendZeroPrecision(helper.calculatePrecision(certificate.getsAmsprecision(), certificate.getPrecisionzero(), new BigDecimal(pz)));
            certificate.appendReferencePrecision(helper.calculatePrecision(certificate.getsAmsprecision(), certificate.getPrecisionreference(), new BigDecimal(pr)));
            certificate.updateExpirydate(stub.getTxTimestamp());
            stub.putStringState(helper.createCompositeKey(kexprefix, sensorid, ownerorg).toString(), certificate.toJSON());
            helper.createSuccessResponse("Values commited successfully");
        } catch (NumberFormatException e) {
            helper.createFailureResponse(e.getMessage());
        }
       
    }

    
    /**
     * Checks, if a specific qal3 certificate is valid 
     * @param ctx Hyperledger fabric context
     * @param sensorid Organisation wide unique sensor id 
     * @param ownerorg Organisation which owns the sensor
     * @return String response
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String isValid(Context ctx, String sensorid, String ownerorg){
        if(!helper.isSuccess(isExpired(ctx, sensorid, ownerorg)) 
            && !helper.isSuccess(hasdecreasedPrecision(ctx, sensorid, ownerorg)) 
            && !helper.isSuccess(hasDrift(ctx, sensorid, ownerorg))){

            return helper.createSuccessResponse("QAL3 is valid");
        } else {
            return helper.createFailureResponse("QAL3 is invalid");
        }
    }


    
    /**
     * Checks, if the sensor has drift 
     * @param ctx Hyperledger fabric context
     * @param sensorid Organisation wide unique sensor id 
     * @param ownerorg Organisation which owns the sensor
     * @return String response
     */
    public String hasDrift(Context ctx, String sensorid, String ownerorg) {
        QAL3Certificate cert = QAL3Certificate.fromJSON(getCertificate(ctx, sensorid, ownerorg));
        CusumDrift lastzeropoint = cert.getDriftzero()[cert.getDriftzero().length-1];
        CusumDrift lastreferencepoint = cert.getDriftreference()[cert.getDriftreference().length-1];
        if (lastzeropoint.isPosdrift() || lastzeropoint.isNegdrift() || lastreferencepoint.isPosdrift() || lastreferencepoint.isNegdrift()){
            return helper.createSuccessResponse(true);
        } else {
            return helper.createFailureResponse(false);
        }
    }


    
    /**
     * Checks, if the sensor has precision 
     * @param ctx Hyperledger fabric context
     * @param sensorid Organisation wide unique sensor id 
     * @param ownerorg Organisation which owns the sensor
     * @return String response
     */
    public String hasdecreasedPrecision(Context ctx, String sensorid, String ownerorg) {
        QAL3Certificate cert = QAL3Certificate.fromJSON(getCertificate(ctx, sensorid, ownerorg));
        CusumPrecision lastzeropoint = cert.getPrecisionzero()[cert.getPrecisionzero().length-1];
        CusumPrecision lastreferencepoint = cert.getPrecisionreference()[cert.getPrecisionreference().length-1];
        if(lastzeropoint.isPrecisiondecreased() || lastreferencepoint.isPrecisiondecreased()){
            return helper.createSuccessResponse(true);
        } else {
            return helper.createFailureResponse(false);
        }
    }


    
    /**
     * Checks, if the certificate is expired 
     * @param ctx Hyperledger fabric context
     * @param sensorid Organisation wide unique sensor id 
     * @param ownerorg Organisation which owns the sensor   
     * @return String response
     */
    public String isExpired(Context ctx, String sensorid, String ownerorg) {
        QAL3Certificate cert = QAL3Certificate.fromJSON(getCertificate(ctx, sensorid, ownerorg));
        ChaincodeStub stub = ctx.getStub();

        if(cert.getExpirydate() == null){
            helper.createSuccessResponse(true);
        }
        Instant expire = Instant.parse(cert.getExpirydate());
        if(stub.getTxTimestamp().isAfter(expire)){
            return helper.createSuccessResponse(true);
        } else {
            return helper.createFailureResponse(false);
        }
    }
    
}
