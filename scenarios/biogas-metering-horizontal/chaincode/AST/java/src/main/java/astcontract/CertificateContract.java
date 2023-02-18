package astcontract;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

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

import com.google.gson.JsonObject;

@Contract(
        name = "ASTContract",
        info = @Info(
                title = "AST Contract",
                description = "A smart contract to manage AST Certificates",
                version = "0.0.1-Alpha",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "janbiermann24@gmail.com",
                        name = "Jan")))

@Default
public class CertificateContract implements ContractInterface {

        private final String keyprefix = "org~id";

        Helper helper = new Helper();

        /** 
         * @param context Hyperledger Fabric context
         * @return String Response
         */
        @Transaction(intent = Transaction.TYPE.SUBMIT)
        public String init(final Context context){

                JsonObject json = new JsonObject();
                json.addProperty("status", "200");
                json.addProperty("response", "Init");
                return json.toString();

        }


        
        /** 
         * Creates a CertificateAsset
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

                CertificateAsset certificate = new CertificateAsset(sensorid, ownerorg);
                stub.putStringState(helper.createCompositeKey(keyprefix, sensorid, ownerorg).toString(), certificate.toJson());
                return helper.createSuccessResponse("Certificate created successfully");
        }

        
        /** 
         * Updates the inspector section of the CertificateAsset
         * 
         * @param ctx Hyperledger fabric context
         * @param sensorid Unique sensor id
         * @param ownerorg Owner of the sensor
         * @param inspectorcert Inspection secret
         * @param inspectororganisation Organisation of the inspector
         * @return String response
         */
        @Transaction(intent = Transaction.TYPE.SUBMIT)
        public String updateInspector(Context ctx, String sensorid, String ownerorg) {
                
                ChaincodeStub stub = ctx.getStub();
                String mspid = ctx.getClientIdentity().getMSPID();
                X509Certificate cert = ctx.getClientIdentity().getX509Certificate();

                if(ownerorg == stub.getMspId()){
                        return helper.createFailureResponse("You are not allowed to update the inspector");
                }

                if(!helper.isSuccess(certificateExists(ctx, sensorid, ownerorg))){
                        return helper.createFailureResponse("Certificate does not exist");
                }

                CertificateAsset currentcertificate = CertificateAsset.fromJSON(helper.parseJson(getCertificate(ctx, sensorid, ownerorg))
                                                                              .get("response")
                                                                              .getAsString());

                String salted = cert.toString().concat(mspid);
                MessageDigest digest;
                try {
                        digest = MessageDigest.getInstance("SHA-256");
                        byte[] hashedcert = digest.digest(salted.getBytes(StandardCharsets.UTF_8));
                        String cCert = new String(hashedcert, StandardCharsets.UTF_8);
                        currentcertificate.setInspectorcert(cCert);
                        currentcertificate.setInspectororganisation(mspid);
                        stub.putStringState(helper.createCompositeKey(keyprefix, sensorid, ownerorg).toString(), currentcertificate.toJson());
                        return helper.createSuccessResponse("Inspector successfully updated");

                } catch (NoSuchAlgorithmException e) {
                        return helper.createFailureResponse(e.getMessage());
                }

                
        }

        
        /** 
         * @param ctx Hyperledger fabric context
         * @param sensorid Unique sensor id
         * @param ownerorg Organisation of the sensor
         * @param secid the Security-Id of the transponder
         * @return String Response
         */
        @Transaction(intent = Transaction.TYPE.SUBMIT)
        public String setSecret(Context ctx, String sensorid, String ownerorg, String secid){
                ChaincodeStub stub = ctx.getStub();
                String mspid = ctx.getClientIdentity().getMSPID();
                X509Certificate cert = ctx.getClientIdentity().getX509Certificate();

                if(ownerorg == stub.getMspId()){
                        return helper.createFailureResponse("You are not allowed to update the inspector");
                }

                if(!helper.isSuccess(certificateExists(ctx, sensorid, ownerorg))){
                        return helper.createFailureResponse("Certificate does not exist");
                }

                CertificateAsset currentcertificate = CertificateAsset.fromJSON(helper.parseJson(getCertificate(ctx, sensorid, ownerorg))
                                                                              .get("response")
                                                                              .getAsString());


                String saltedcert = cert.toString().concat(mspid);
                String saltedsecid = secid.concat(mspid);
                MessageDigest digest;
                try {
                        digest = MessageDigest.getInstance("SHA-256");
                        byte[] hashedcert = digest.digest(saltedcert.getBytes(StandardCharsets.UTF_8));
                        String clientcert = new String(hashedcert, StandardCharsets.UTF_8);


                        if(clientcert.equals(currentcertificate.getInspectorcert())){
                                byte[] hashedsecret = digest.digest(saltedsecid.getBytes(StandardCharsets.UTF_8));
                                currentcertificate.setSecid(new String(hashedsecret, StandardCharsets.UTF_8));
                                stub.putStringState(helper.createCompositeKey(keyprefix, sensorid, ownerorg).toString(), currentcertificate.toJson());
                        } else {
                                return helper.createFailureResponse("You are not allowed to set the secret!");
                        }

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
        public String certify(Context ctx, String sensorid, String ownerorg, String secid){

                ChaincodeStub stub = ctx.getStub();
                final ClientIdentity clientIdentity = ctx.getClientIdentity();
                final String cmspID = clientIdentity.getMSPID();

                if(!helper.isSuccess(certificateExists(ctx, sensorid, ownerorg))){
                        return helper.createFailureResponse("The certificate does not exists");
                }

                if(!cmspID.equals(ownerorg)){
                        return helper.createFailureResponse("Your organisation is not allowed to certify this device");
                }
                
                
                CertificateAsset currentcertificate = CertificateAsset.fromJSON(helper.parseJson(getCertificate(ctx, sensorid, ownerorg))
                                                                              .get("response")
                                                                              .getAsString());

                
                String salted = secid.concat(currentcertificate.getInspectororganisation());
                MessageDigest digest;
                try {
                        digest = MessageDigest.getInstance("SHA-256");
                        byte[] hashedsecid = digest.digest(salted.getBytes(StandardCharsets.UTF_8));

                        if(new String(hashedsecid, StandardCharsets.UTF_8).equals(currentcertificate.getSecid())){
                        
                                currentcertificate.setFirstauth(true);
                                stub.putStringState(helper.createCompositeKey(keyprefix, sensorid, ownerorg).toString(), currentcertificate.toJson());
                                return helper.createSuccessResponse("Sensor was certified successfully");
                        } else {
                                return helper.createFailureResponse("Invalid secret");
                        }
                } catch (NoSuchAlgorithmException e) {
                        return helper.createFailureResponse(e.getMessage());
                }
                

        }

        
        /** 
         *@param ctx Hyperledger fabric context
         * @param sensorid Unique sensor id
         * @param ownerorg Organisation of the sensor
         * @return String Response
         */
        @Transaction(intent = Transaction.TYPE.SUBMIT)
        public String confirmCertification(Context ctx, String sensorid, String ownerorg){
                
                ChaincodeStub stub = ctx.getStub();
                String mspid = ctx.getClientIdentity().getMSPID();
                X509Certificate cert = ctx.getClientIdentity().getX509Certificate();

                if(ownerorg == stub.getMspId()){
                        return helper.createFailureResponse("You are not allowed to update the inspector");
                }

                if(!helper.isSuccess(certificateExists(ctx, sensorid, ownerorg))){
                        return helper.createFailureResponse("Certificate does not exist");
                }

                CertificateAsset currentcertificate = CertificateAsset.fromJSON(helper.parseJson(getCertificate(ctx, sensorid, ownerorg))
                                                                              .get("response")
                                                                              .getAsString());

                String saltedcert = cert.toString().concat(mspid);
                MessageDigest digest;
                try {
                        digest = MessageDigest.getInstance("SHA-256");
                        byte[] hashedcert = digest.digest(saltedcert.getBytes(StandardCharsets.UTF_8));

                        if(new String(hashedcert, StandardCharsets.UTF_8).equals(currentcertificate.getInspectorcert())){

                                // Calculating expiry date
                                LocalDateTime time = LocalDateTime.ofInstant(stub.getTxTimestamp(), ZoneOffset.UTC);
                                time = time.plus(1, ChronoUnit.YEARS);
                                Instant expiryDate = time.toInstant(ZoneOffset.UTC);

                                // finish certification and reset data;
                                currentcertificate.setExpirydate(expiryDate.toString());
                                currentcertificate.setFirstauth(false);
                                currentcertificate.setSecid(null);
                                currentcertificate.setInspectorcert(null);
                                stub.putStringState(helper.createCompositeKey(keyprefix, sensorid, ownerorg).toString(), currentcertificate.toJson());
                                return helper.createSuccessResponse("Certification completed!");
                        } else {
                                return helper.createFailureResponse("You do not have the permission to do this");
                        }

                } catch (NoSuchAlgorithmException e){
                        return helper.createFailureResponse(e.getMessage());
                }
        }


        /** 
         * Deletes a specific CertificateAsset
         * @param ctx Hyperledger fabric context
         * @param sensorid Unique sensor id
         * @param ownerorg Organisation of the sensor
         * @return String response
         */
        @Transaction(intent = Transaction.TYPE.SUBMIT)
        public String deleteCertificate(Context ctx, String sensorid, String ownerorg){
                ChaincodeStub stub = ctx.getStub();
                if(!helper.isSuccess(certificateExists(ctx, sensorid, ownerorg))){
                        return helper.createFailureResponse("Certificate does not exist");
                }
                if(ownerorg == stub.getMspId()){
                        return helper.createFailureResponse("You are not allowed to update the inspector");
                }
                
                stub.delState(helper.createCompositeKey(keyprefix, sensorid, ownerorg).toString());
                return helper.createSuccessResponse("Certificate deleted successfully");

        }

        
        /**
         * Reads a specific CertificateAsset 
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

                ChaincodeStub stub = ctx.getStub();
                return helper.createSuccessResponse(stub.getStringState(helper.createCompositeKey(keyprefix, sensorid, ownerorg).toString()));
        }

        
        /**
         * Checks, if a specific CertificateAsset exists
         * @param ctx Hyperledger fabric context
         * @param sensorid Unique sensor id
         * @param ownerorg Organisation of the sensor
         * @return String response
         */
        @Transaction(intent = Transaction.TYPE.EVALUATE)
        public String certificateExists(Context ctx, String sensorid, String ownerorg){
                ChaincodeStub stub = ctx.getStub();
                String cert = stub.getStringState(helper.createCompositeKey(keyprefix, sensorid, ownerorg).toString());
                if(cert != null && cert.length() > 0){
                        return helper.createSuccessResponse("Certificate exist");
                }
                return helper.createFailureResponse("Certificate does not exist");
        }

        
        /**
         * Checks, if a specific CertificateAsset is valid 
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
         * Checks, if a specific CertificateAsset is expired 
         * @param ctx Hyperledger fabric context
         * @param sensorid Unique sensor id
         * @param ownerorg Organisation of the sensor
         * @return String response
         */
        @Transaction(intent = Transaction.TYPE.EVALUATE)
        public String isExpired(Context ctx, String sensorid, String ownerorg){

                ChaincodeStub stub = ctx.getStub();
                Instant time = stub.getTxTimestamp();
                CertificateAsset certificate = CertificateAsset.fromJSON(helper.parseJson(getCertificate(ctx, sensorid, ownerorg))
                                                                      .get("response")
                                                                      .getAsString());

                if(certificate.getExpirydate() == null){
                        return helper.createFailureResponse("Certificate time null");
                }

                try {
                        Instant expire = Instant.parse(certificate.getExpirydate());
                        if(time.isBefore(expire)){
                                return helper.createSuccessResponse("not expired");
                        } else {
                                return helper.createFailureResponse("expired");
                        }

                } catch (DateTimeParseException e) {
                        return helper.createFailureResponse("Date format error");
                }


        }
}