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
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;

import org.hyperledger.fabric.shim.ChaincodeStub;


import com.google.gson.JsonObject;

@Contract(
        name = "ASTContract",
        info = @Info(
                title = "AST chaincode",
                description = "A smart contract to save the AST Certificate",
                version = "0.0.1-Alpha",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "janbiermann24@gmail.com",
                        name = "Jan")))

@Default
public class ASTContract implements ContractInterface {

        private final String keyprefix = "org~id";

        Helper helper = new Helper();

        private static Log _logger = LogFactory.getLog(ASTCertificate.class);


        
        /** 
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
         * Creates an ASTCertificate
         * @param ctx Hyperledger fabric context
         * @param sensorid Unique sensorid
         * @param ownerorg Organisation which owns the sensor
         */
        @Transaction(intent = Transaction.TYPE.SUBMIT)
        public String createCertificate(Context ctx, String sensorid, String ownerorg){

                if(ctx.getClientIdentity().getMSPID().equals(ownerorg)){
                        return helper.createFailureResponse("You do not have the permission to create a certificate for yourself");
                }
                
                if(helper.isSuccess(certificateExists(ctx, sensorid, ownerorg))){
                        return helper.createFailureResponse("Certificate already exists");
                }

                ChaincodeStub stub = ctx.getStub();
                ASTCertificate certificate = new ASTCertificate(sensorid, ownerorg);
                stub.putStringState(helper.createCompositeKey(keyprefix, sensorid, ownerorg).toString(), certificate.toJson());
                return helper.createSuccessResponse("Certificate successfully created");
        }

        
        /**
         * Updates the inspector section 
         * @param ctx Hyperledger fabric context
         * @param sensorid Unique sensorid
         * @param ownerorg Organisation which owns the sensor
         * @param inspectorcert inspector secret
         * @param inspectororganisation organisation of the inspector
         * @return String response
         */
        @Transaction(intent = Transaction.TYPE.SUBMIT)
        public String updateInspector(Context ctx, String sensorid, String ownerorg, String inspectorcert, String inspectororganisation) throws NoSuchAlgorithmException{

                if(ctx.getClientIdentity().getMSPID().equals(ownerorg)){
                        return helper.createFailureResponse("You do not have the permission to update a certificate");
                }

                

                if(!helper.isSuccess(certificateExists(ctx, sensorid, ownerorg))){
                        return helper.createFailureResponse("Certificate does not exist");
                }

                ASTCertificate currentcertificate = ASTCertificate.fromJSON(getCertificate(ctx, sensorid, ownerorg));

                
                ChaincodeStub stub = ctx.getStub();

                
                String salted = inspectorcert.concat(inspectororganisation);
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hashedcert = digest.digest(salted.getBytes(StandardCharsets.UTF_8));
                ASTCertificate certificate = new ASTCertificate(sensorid, ownerorg, new String(hashedcert, StandardCharsets.UTF_8), inspectororganisation, currentcertificate.getExpirydate());
                stub.putStringState(helper.createCompositeKey(keyprefix, sensorid, ownerorg).toString(), certificate.toJson());
                return helper.createSuccessResponse("Certificate successfully updated");

        }

        
        /**
         * Certifies the sensor 
         * @param ctx Hyperledger fabric context
         * @param sensorid Unique sensorid
         * @param ownerorg Organisation which owns the sensor
         * @param inspectorcert
         * @throws NoSuchAlgorithmException
         */
        @Transaction(intent = Transaction.TYPE.SUBMIT)
        public String certify(Context ctx, String sensorid, String ownerorg, String inspectorcert) throws NoSuchAlgorithmException{

                String subject = ctx.getClientIdentity().getX509Certificate().getSubjectDN().getName();

                if(!ctx.getClientIdentity().getMSPID().equals(ownerorg)){
                        return helper.createFailureResponse("You do not have the permission to certify a sensor of " + ownerorg);
                }

                ChaincodeStub stub = ctx.getStub();

                LocalDateTime time = LocalDateTime.ofInstant(stub.getTxTimestamp(), ZoneOffset.UTC);
                time = time.plus(1, ChronoUnit.YEARS);
                Instant expiryDate = time.toInstant(ZoneOffset.UTC);

                ASTCertificate currentcertificate = ASTCertificate.fromJSON(getCertificate(ctx, sensorid, ownerorg));
                

                String salted = inspectorcert.concat(currentcertificate.getInspectororganisation());
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                String hashedcert = new String(digest.digest(salted.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);

                if(hashedcert.equals(currentcertificate.getInspectorcert())){
                        currentcertificate.setExpirydate(expiryDate.toString());
                        stub.putStringState(helper.createCompositeKey(keyprefix, sensorid, ownerorg).toString(), currentcertificate.toJson());
                        return helper.createSuccessResponse("Certification completed successfully");
                } else {
                        return helper.createFailureResponse("Invalid secret");
                }

        }

        
        /**
         * Deletes an ASTCertificate
         * @param ctx Hyperledger fabric context
         * @param sensorid Unique sensorid
         * @param ownerorg Organisation which owns the sensor
         */
        @Transaction(intent = Transaction.TYPE.SUBMIT)
        public String deleteCertificate(Context ctx, String sensorid, String ownerorg){

                if(!helper.isSuccess(certificateExists(ctx, sensorid, ownerorg))){
                        return helper.createFailureResponse("Certificate does not exist");
                }

                helper.createCompositeKey(keyprefix, sensorid, ownerorg);
                ChaincodeStub stub = ctx.getStub();
                stub.delState(helper.createCompositeKey(keyprefix, sensorid, ownerorg).toString());
                return helper.createSuccessResponse("Certificate successfully deleted");

        }

        
        /**
         * Reads an ASTCertificate 
         * @param ctx Hyperledger fabric context
         * @param sensorid Unique sensorid
         * @param ownerorg Organisation which owns the sensor
         * @return String
         */
        @Transaction(intent = Transaction.TYPE.EVALUATE)
        public String getCertificate(Context ctx, String sensorid, String ownerorg){

                if(!helper.isSuccess(certificateExists(ctx, sensorid, ownerorg))){
                        return helper.createFailureResponse("Certificate does not exist");
                }
                ChaincodeStub stub = ctx.getStub();
                return helper.createSuccessResponse(stub.getStringState(helper.createCompositeKey(keyprefix, sensorid, ownerorg).toString()));
        }

        
        /**
         * Checks, if an ASTCertificate exists 
         * @param ctx Hyperledger fabric context
         * @param sensorid Unique sensorid
         * @param ownerorg Organisation which owns the sensor
         * @return String response
         */
        @Transaction(intent = Transaction.TYPE.EVALUATE)
        public String certificateExists(Context ctx, String sensorid, String ownerorg){
                ChaincodeStub stub = ctx.getStub();
                String cert = stub.getStringState(helper.createCompositeKey(keyprefix, sensorid, ownerorg).toString());

                if(cert != null && cert.length() > 0){
                        return helper.createSuccessResponse(true);
                }
                return helper.createFailureResponse(false);
        }

        
        /**
         * Checks if an ASTCertificate is valid 
         * @param ctx Hyperledger fabric context
         * @param sensorid Unique sensorid
         * @param ownerorg Organisation which owns the sensor
         * @return String response
         */
        @Transaction(intent = Transaction.TYPE.EVALUATE)
        public String isValid(Context ctx, String sensorid, String ownerorg){
                String response = isExpired(ctx, sensorid, ownerorg);
                if(helper.isSuccess(response)){
                        return helper.createSuccessResponse("is valid");
                } else {
                        return helper.createFailureResponse(helper.parseJson(response).get("response").getAsString());
                }
        }

        
        /**
         * Checks if an ASTCertificate is expired 
         * @param ctx Hyperledger fabric context
         * @param sensorid Unique sensorid
         * @param ownerorg Organisation which owns the sensor
         * @return String response
         */
        @Transaction(intent = Transaction.TYPE.EVALUATE)
        public String isExpired(Context ctx, String sensorid, String ownerorg){

                ChaincodeStub stub = ctx.getStub();
                Instant time = stub.getTxTimestamp();
                ASTCertificate certificate = ASTCertificate.fromJSON(getCertificate(ctx, sensorid, ownerorg));

                _logger.info("Certificate loaded");

                if(certificate.getExpirydate() == null){
                        _logger.info("Certificate time null");
                        return helper.createFailureResponse("Certificate time null");
                }

                _logger.info("Certificate time not null");

                try {
                        Instant expire = Instant.parse(certificate.getExpirydate());
                        _logger.info("Certificate time loaded");
                        if(time.isBefore(expire)){
                                return helper.createSuccessResponse("not expired");
                        } else {
                                return helper.createFailureResponse("AST certificate expired");
                        }

                } catch (DateTimeParseException e) {
                        _logger.info("Error format incorrect");
                        return helper.createFailureResponse(e.getMessage());
                }


        }


        






    
}