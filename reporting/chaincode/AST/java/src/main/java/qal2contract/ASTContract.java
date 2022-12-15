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
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

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

        private static Log _logger = LogFactory.getLog(ASTCertificate.class);


        @Transaction(intent = Transaction.TYPE.SUBMIT)
        public String init(final Context context){

                JsonObject json = new JsonObject();
                json.addProperty("status", "200");
                json.addProperty("response", "Init");
                return json.toString();

        }


        @Transaction(intent = Transaction.TYPE.SUBMIT)
        public void createCertificate(Context ctx, String sensorid, String ownerorg){

                if(ctx.getClientIdentity().getMSPID().equals(ownerorg)){
                        throw new ChaincodeException("You do not have the permission to create a certificate for yourself");
                }
                
                if(certificateExists(ctx, sensorid, ownerorg)){
                        throw new ChaincodeException("Certificate already exists");
                }

                ChaincodeStub stub = ctx.getStub();

                

                ASTCertificate certificate = new ASTCertificate(sensorid, ownerorg);
                CompositeKey key = new CompositeKey(keyprefix, new String[] {ownerorg, sensorid});
                stub.putStringState(key.toString(), certificate.toJson());
        }

        @Transaction(intent = Transaction.TYPE.SUBMIT)
        public void updateInspector(Context ctx, String sensorid, String ownerorg, String inspectorcert, String inspectororganisation) throws NoSuchAlgorithmException{

                if(ctx.getClientIdentity().getMSPID().equals(ownerorg)){
                        throw new ChaincodeException("You do not have the permission to update a certificate");
                }

                if(!certificateExists(ctx, sensorid, ownerorg)){
                        throw new ChaincodeException("Certificate does not exist");
                }

                ASTCertificate currentcertificate = ASTCertificate.fromJSON(getCertificate(ctx, sensorid, ownerorg));

                CompositeKey key = new CompositeKey(keyprefix, new String[] {ownerorg, sensorid});
                ChaincodeStub stub = ctx.getStub();

                
                String salted = inspectorcert.concat(inspectororganisation);
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hashedcert = digest.digest(salted.getBytes(StandardCharsets.UTF_8));
                ASTCertificate certificate = new ASTCertificate(sensorid, ownerorg, new String(hashedcert, StandardCharsets.UTF_8), inspectororganisation, currentcertificate.getExpirydate());
                stub.putStringState(key.toString(), certificate.toJson());

        }

        @Transaction(intent = Transaction.TYPE.SUBMIT)
        public void certify(Context ctx, String sensorid, String ownerorg, String inspectorcert) throws NoSuchAlgorithmException{

                String subject = ctx.getClientIdentity().getX509Certificate().getSubjectDN().getName();

                if(!ctx.getClientIdentity().getMSPID().equals(ownerorg)){
                        throw new ChaincodeException("You do not have the permission to certify a sensor of " + ownerorg);
                }

                ChaincodeStub stub = ctx.getStub();

                LocalDateTime time = LocalDateTime.ofInstant(stub.getTxTimestamp(), ZoneOffset.UTC);
                time = time.plus(1, ChronoUnit.YEARS);
                Instant expiryDate = time.toInstant(ZoneOffset.UTC);

                ASTCertificate currentcertificate = ASTCertificate.fromJSON(getCertificate(ctx, sensorid, ownerorg));
                CompositeKey key = new CompositeKey(keyprefix, new String[] {ownerorg, sensorid});

                String salted = inspectorcert.concat(currentcertificate.getInspectororganisation());
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                String hashedcert = new String(digest.digest(salted.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);

                if(hashedcert.equals(currentcertificate.getInspectorcert())){
                        currentcertificate.setExpirydate(expiryDate.toString());
                        stub.putStringState(key.toString(), currentcertificate.toJson());
                } else {
                        throw new ChaincodeException("Invalid Certificate");
                }

        }

        @Transaction(intent = Transaction.TYPE.SUBMIT)
        public void deleteCertificate(Context ctx, String sensorid, String ownerorg){

                if(!certificateExists(ctx, sensorid, ownerorg)){
                        throw new ChaincodeException("Certificate does not exist");
                }

                CompositeKey key = new CompositeKey(keyprefix, new String[] {ownerorg, sensorid});
                ChaincodeStub stub = ctx.getStub();
                stub.delState(key.toString());

        }

        @Transaction(intent = Transaction.TYPE.EVALUATE)
        public String getCertificate(Context ctx, String sensorid, String ownerorg){

                if(!certificateExists(ctx, sensorid, ownerorg)){
                        throw new ChaincodeException("Certificate does not exist");
                }

                CompositeKey key = new CompositeKey(keyprefix, new String[] {ownerorg, sensorid});
                ChaincodeStub stub = ctx.getStub();
                return stub.getStringState(key.toString());
        }

        @Transaction(intent = Transaction.TYPE.EVALUATE)
        public boolean certificateExists(Context ctx, String sensorid, String ownerorg){
                ChaincodeStub stub = ctx.getStub();
                CompositeKey key = new CompositeKey(keyprefix, new String[] {ownerorg, sensorid});
                String cert = stub.getStringState(key.toString());
                return (cert != null && cert.length() > 0);
        }

        @Transaction(intent = Transaction.TYPE.EVALUATE)
        public boolean isValid(Context ctx, String sensorid, String ownerorg){
                return isExpired(ctx, sensorid, ownerorg);
        }

        @Transaction(intent = Transaction.TYPE.EVALUATE)
        public boolean isExpired(Context ctx, String sensorid, String ownerorg){

                ChaincodeStub stub = ctx.getStub();
                Instant time = stub.getTxTimestamp();
                ASTCertificate certificate = ASTCertificate.fromJSON(getCertificate(ctx, sensorid, ownerorg));

                _logger.info("Certificate loaded");

                if(certificate.getExpirydate() == null){
                        _logger.info("Certificate time null");
                        return false;
                }

                _logger.info("Certificate time not null");

                try {
                        Instant expire = Instant.parse(certificate.getExpirydate());
                        _logger.info("Certificate time loaded");
                        return time.isBefore(expire);

                } catch (DateTimeParseException e) {
                        _logger.info("Error format incorrect");
                        return false;
                }


        }


        






    
}