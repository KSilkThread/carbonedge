package qal2contract;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

import com.google.gson.JsonObject;

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
public class Qal2Contract implements ContractInterface {

        private final String keyprefix = "org~id";

        Helper helper = new Helper();

        private static Log _logger = LogFactory.getLog(Qal2Certificate.class);


        
        /** 
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
         * Creates a QAL2Certificate
         * @param ctx Hyperledger fabric context
         * @param sensorid Unique sensorid
         * @param ownerorg Owner organisation of the sensor
         * @return String Response
         */
        @Transaction(intent = Transaction.TYPE.SUBMIT)
        public String createCertificate(Context ctx, String sensorid, String ownerorg){
                ChaincodeStub stub = ctx.getStub();

                if(ownerorg == stub.getMspId()){
                       return helper.createFailureResponse("You are not allowed to create a certificate for yourself");
                }

                if(helper.isSuccess(certificateExists(ctx, sensorid, ownerorg))){
                        helper.createFailureResponse("Certificate already exists");
                }

                Qal2Certificate certificate = new Qal2Certificate(sensorid, ownerorg);
                CompositeKey key = new CompositeKey(keyprefix, new String[] {ownerorg, sensorid});
                stub.putStringState(key.toString(), certificate.toJson());
                return helper.createSuccessResponse("Certificate created successfully");
        }

        
        /** 
         * Updates the inspector section of the Qal2Cerificate
         * 
         * @param ctx Hyperledger fabric context
         * @param sensorid Unique sensor id
         * @param ownerorg Owner of the sensor
         * @param inspectorcert Inspection secret
         * @param inspectororganisation Organisation of the inspector
         * @return String response
         */
        @Transaction(intent = Transaction.TYPE.SUBMIT)
        public String updateInspector(Context ctx, String sensorid, String ownerorg, String inspectorcert, String inspectororganisation) {
                
                ChaincodeStub stub = ctx.getStub();

                if(ownerorg == stub.getMspId()){
                        return helper.createFailureResponse("You are not allowed to update the inspector");
                }

                if(!helper.isSuccess(certificateExists(ctx, sensorid, ownerorg))){
                        return helper.createFailureResponse("Certificate does not exist");
                }

                Qal2Certificate currentcertificate = Qal2Certificate.fromJSON(getCertificate(ctx, sensorid, ownerorg));

                String salted = inspectorcert.concat(inspectororganisation);
                MessageDigest digest;
                try {
                        digest = MessageDigest.getInstance("SHA-256");
                        byte[] hashedcert = digest.digest(salted.getBytes(StandardCharsets.UTF_8));
                        Qal2Certificate certificate = new Qal2Certificate(sensorid, ownerorg, new String(hashedcert, StandardCharsets.UTF_8), inspectororganisation, currentcertificate.getExpirydate());
                        stub.putStringState(helper.createCompositeKey(keyprefix, sensorid, ownerorg).toString(), certificate.toJson());
                } catch (NoSuchAlgorithmException e) {
                        return helper.createFailureResponse(e.getMessage());
                }

                return helper.createSuccessResponse("Inspector successfully updated");
                
        }

        
        /**
         * Certifies the sensor
         *  
         * @param ctx Hyperledger fabric context
         * @param sensorid Unique sensor id
         * @param ownerorg Organisation of the sensor
         * @param inspectorcert Inspection secret
         * return String response
         */
        @Transaction(intent = Transaction.TYPE.SUBMIT)
        public String certify(Context ctx, String sensorid, String ownerorg, String inspectorcert) throws NoSuchAlgorithmException{

                ChaincodeStub stub = ctx.getStub();
                final ClientIdentity clientIdentity = ctx.getClientIdentity();
                final String cmspID = clientIdentity.getMSPID();
                final String checkid = clientIdentity.getX509Certificate().getSubjectDN().getName();
                final String isid = "CN="+sensorid+", OU=client";

                if(!helper.isSuccess(certificateExists(ctx, sensorid, ownerorg))){
                        return helper.createFailureResponse("The certificate does not exists");
                }

                if(!cmspID.equals(ownerorg)){
                        return helper.createFailureResponse("Your organisation is not allowed to certify this device");
                }

                if(!isid.equals(checkid)){
                        return helper.createFailureResponse("You are not allowed to certify this device! You are not the device!");
                }

                LocalDateTime time = LocalDateTime.ofInstant(stub.getTxTimestamp(), ZoneOffset.UTC);
                time = time.plus(3, ChronoUnit.YEARS);
                Instant expiryDate = time.toInstant(ZoneOffset.UTC);

                Qal2Certificate currentcertificate = Qal2Certificate.fromJSON(getCertificate(ctx, sensorid, ownerorg));

                
                String salted = inspectorcert.concat(currentcertificate.getInspectororganisation());
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                String hashedcert = new String(digest.digest(salted.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);

                if(hashedcert.equals(currentcertificate.getInspectorcert())){
                        currentcertificate.setExpirydate(expiryDate.toString());
                        stub.putStringState(helper.createCompositeKey(keyprefix, sensorid, ownerorg).toString(), currentcertificate.toJson());
                        return helper.createSuccessResponse("Sensor was certified successfully");
                } else {
                        return helper.createFailureResponse("Invalid secret");
                }

        }

        
        /** 
         * Deletes a specific Qal2Certificate
         * @param ctx Hyperledger fabric context
         * @param sensorid Unique sensor id
         * @param ownerorg Organisation of the sensor
         */
        @Transaction(intent = Transaction.TYPE.SUBMIT)
        public String deleteCertificate(Context ctx, String sensorid, String ownerorg){

                if(!helper.isSuccess(certificateExists(ctx, sensorid, ownerorg))){
                        return helper.createFailureResponse("Certificate does not exist");
                }
                ChaincodeStub stub = ctx.getStub();
                stub.delState(helper.createCompositeKey(keyprefix, sensorid, ownerorg).toString());
                return helper.createSuccessResponse("Certificate deleted successfully");

        }

        
        /**
         * Reads a specific Qal2Certificate 
         * @param ctx Hyperledger fabric context
         * @param sensorid Unique sensor id
         * @param ownerorg Organisation of the sensor
         * @return String response
         */
        @Transaction(intent = Transaction.TYPE.EVALUATE)
        public String getCertificate(Context ctx, String sensorid, String ownerorg){

                if(!helper.isSuccess(certificateExists(ctx, sensorid, ownerorg))){
                        return helper.createFailureResponse("Certificate does not exist");
                }

                CompositeKey key = new CompositeKey(keyprefix, new String[] {ownerorg, sensorid});
                ChaincodeStub stub = ctx.getStub();
                return helper.createSuccessResponse(stub.getStringState(key.toString()));
        }

        
        /**
         * Checks, if a specific Qal2Certificate exists
         * @param ctx Hyperledger fabric context
         * @param sensorid Unique sensor id
         * @param ownerorg Organisation of the sensor
         * @return String response
         */
        @Transaction(intent = Transaction.TYPE.EVALUATE)
        public String certificateExists(Context ctx, String sensorid, String ownerorg){
                ChaincodeStub stub = ctx.getStub();
                CompositeKey key = new CompositeKey(keyprefix, new String[] {ownerorg, sensorid});
                String cert = stub.getStringState(key.toString());
                if(cert != null && cert.length() > 0){
                        return helper.createSuccessResponse("Certificate exist");
                }
                return helper.createFailureResponse("Certificate does not exist");
        }

        
        /**
         * Checks, if a specific Qal2Certificate is valid 
         * @param ctx Hyperledger fabric context
         * @param sensorid Unique sensor id
         * @param ownerorg Organisation of the sensor
         * @return String response
         */
        @Transaction(intent = Transaction.TYPE.EVALUATE)
        public String isValid(Context ctx, String sensorid, String ownerorg){
                String response = isExpired(ctx, sensorid, ownerorg);
                if(helper.isSuccess(response)){
                        return helper.createSuccessResponse("valid");
                } else {
                        return helper.createFailureResponse(helper.parseJson(response).get("response").getAsString());
                }
        }

        
        /**
         * Checks, if a specific Qal2Certificate is expired 
         * @param ctx Hyperledger fabric context
         * @param sensorid Unique sensor id
         * @param ownerorg Organisation of the sensor
         * @return String response
         */
        @Transaction(intent = Transaction.TYPE.EVALUATE)
        public String isExpired(Context ctx, String sensorid, String ownerorg){

                ChaincodeStub stub = ctx.getStub();
                Instant time = stub.getTxTimestamp();
                Qal2Certificate certificate = Qal2Certificate.fromJSON(getCertificate(ctx, sensorid, ownerorg));

                _logger.info("Certificate loaded");

                if(certificate.getExpirydate() == null){
                        _logger.info("Certificate time null");
                        return helper.createFailureResponse("Certificate time null");
                }

                try {
                        Instant expire = Instant.parse(certificate.getExpirydate());
                        _logger.info("Certificate time loaded");
                        if(time.isBefore(expire)){
                                return helper.createSuccessResponse("not expired");
                        } else {
                                return helper.createFailureResponse("expired");
                        }

                } catch (DateTimeParseException e) {
                        _logger.info("Error format incorrect");
                        return helper.createFailureResponse("Date format error");
                }


        }


        






    
}