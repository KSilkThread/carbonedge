package certificateCC;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.hyperledger.fabric.contract.ClientIdentity;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import certificatecontract.CertificateAsset;
import certificatecontract.CertificateContract;
import certificatecontract.Helper;

public class OperativeTests {

        @Nested
        class InvokeReadAssetTransaction {

            private final String keyprefix = "org~id";

             X509Certificate certificate;

            
            CertificateContract contract = new CertificateContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            ClientIdentity cID = mock(ClientIdentity.class);

            Helper helper = new Helper();

            CertificateAsset asset1 = new CertificateAsset("sensor1", "org1");
            CertificateAsset asset2 = new CertificateAsset("sensor2", "org2");
            CertificateAsset asset3 = new CertificateAsset("sensor1", "org3");
            
        
            @Test
            public void whenAssetExists() {
                when(ctx.getStub()).thenReturn(stub);
                when(stub.getStringState(helper.createCompositeKey(keyprefix, asset1.getSensorid(), asset1.getOwnerorg()).toString()))
                        .thenReturn(asset1.toString());

                String res = contract.getCertificate(ctx, "sensor1", "org1");
                JsonObject jsonresult = helper.parseJson(res);
                String status = jsonresult.get("status").getAsString();
                assertEquals(status, "200");
                CertificateAsset asset = CertificateAsset.fromJSON(jsonresult.get("response").getAsString());
                assertEquals(asset, asset1);
            }

            @Test void checkCertificateWorkflow() throws CertificateException, IOException{

                CertificateFactory fact = CertificateFactory.getInstance("X.509");

                // create Certificate 
                when(ctx.getStub()).thenReturn(stub);
                when(stub.getMspId()).thenReturn("inspectorOrg");
                contract.createCertificate(ctx, asset1.getSensorid(), asset1.getOwnerorg());
                verify(stub).putStringState(helper.createCompositeKey(keyprefix, asset1.getSensorid(), asset1.getOwnerorg()).toString(), asset1.toString());

                String testX509 = "-----BEGIN CERTIFICATE-----\nMIIB9TCCAZugAwIBAgIUB74ebkJSYz0JGQqgvGnzSxehUFIwCgYIKoZIzj0EAwIw\ncjELMAkGA1UEBhMCVVMxFzAVBgNVBAgMDk5vcnRoIENhcm9saW5hMRAwDgYDVQQH\nDAdSYWxlaWdoMRkwFwYDVQQKDBBvcmcwLmV4YW1wbGUuY29tMR0wGwYDVQQDDBRj\nYTEub3JnMC5leGFtcGxlLmNvbTAeFw0yMjExMTUxNTAwMDBaFw0yMzExMTUxNTA1\nMDBaMCExDzANBgNVBAsTBmNsaWVudDEOMAwGA1UEAxMFYWRtaW4wWTATBgcqhkjO\nPQIBBggqhkjOPQMBBwNCAATseQKUU5PzLHZwqaoi4ObwwqLbg94R6twu976Er91K\nRWp5XCWVP32V2iUJQMouXg+pgFPYdujnxKoiz9O+hUSTo2AwXjAOBgNVHQ8BAf8E\nBAMCB4AwDAYDVR0TAQH/BAIwADAdBgNVHQ4EFgQU08Zo5flczT4P7LEiTWXwtqHM\nST4wHwYDVR0jBBgwFoAUX8HeVPYAR+E9TvCsp4qYfF+TYZswCgYIKoZIzj0EAwID\nSAAwRQIhAJe8tkyiRQ94R9QyXxT7x7ncJ5xOsBgROufO+SXUz4yFAiB+lCNS3N6x\nxRN8j5QWqO3RKFTFBoCsaqs5knOpCk3Rcg==\n-----END CERTIFICATE-----\n";
                InputStream stream = new ByteArrayInputStream(testX509.getBytes());

                // Update Inspector

                when(ctx.getClientIdentity()).thenReturn(cID);
                when(ctx.getClientIdentity().getMSPID()).thenReturn("inspectorOrg");

                try {
                   certificate = (X509Certificate) fact.generateCertificate(stream);
                } catch (CertificateException e) {
                    fail();
                    System.out.println("Failed to generate certificate! Check String!");
                }
                stream.close();


                when(ctx.getClientIdentity().getX509Certificate()).thenReturn(certificate);
                when(stub.getStringState(helper.createCompositeKey(keyprefix, asset1.getSensorid(), asset1.getOwnerorg()).toString())).thenReturn(asset1.toString());


                String salted = certificate.toString().concat("inspectorOrg");
                MessageDigest digest;
                try {
                        digest = MessageDigest.getInstance("SHA-256");
                        byte[] hashedcert = digest.digest(salted.getBytes(StandardCharsets.UTF_8));
                        String cCert = new String(hashedcert, StandardCharsets.UTF_8);
                        asset1.setInspectorcert(cCert);
                        asset1.setInspectororganisation("inspectorOrg");

                } catch (NoSuchAlgorithmException e) {
                        fail();
                }
                contract.updateInspector(ctx, asset1.getSensorid(), asset1.getOwnerorg());
                verify(stub).putStringState(helper.createCompositeKey(keyprefix, asset1.getSensorid(), asset1.getOwnerorg()).toString(), asset1.toString());


                // setSecret

                when(stub.getStringState(helper.createCompositeKey(keyprefix, asset1.getSensorid(), asset1.getOwnerorg()).toString())).thenReturn(asset1.toString());
                String secid = "1234";
                assertNotNull(asset1.getInspectorcert());
                assertNotNull(asset1.getInspectororganisation());
                String secretWithSalt = secid.toString().concat("inspectorOrg");
                MessageDigest digest2;


                try {
                        digest2 = MessageDigest.getInstance("SHA-256");
                        byte[] hashedcert = digest2.digest(secretWithSalt.getBytes(StandardCharsets.UTF_8));
                        asset1.setSecid(new String(hashedcert, StandardCharsets.UTF_8));

                } catch (NoSuchAlgorithmException e) {
                        fail();
                }

                contract.setSecret(ctx, asset1.getSensorid(), asset1.getOwnerorg(), secid);
                verify(stub).putStringState(helper.createCompositeKey(keyprefix, asset1.getSensorid(), asset1.getOwnerorg()).toString(), asset1.toString());
                when(stub.getStringState(helper.createCompositeKey(keyprefix, asset1.getSensorid(), asset1.getOwnerorg()).toString())).thenReturn(asset1.toString());
                when(ctx.getClientIdentity().getMSPID()).thenReturn(asset1.getOwnerorg());
                contract.certify(ctx, asset1.getSensorid(), asset1.getOwnerorg(), secid);
                asset1.setFirstauth(true);
                verify(stub).putStringState(helper.createCompositeKey(keyprefix, asset1.getSensorid(), asset1.getOwnerorg()).toString(), asset1.toString());
                when(stub.getStringState(helper.createCompositeKey(keyprefix, asset1.getSensorid(), asset1.getOwnerorg()).toString())).thenReturn(asset1.toString());
                when(ctx.getClientIdentity().getMSPID()).thenReturn("inspectorOrg");
                verify(stub).putStringState(helper.createCompositeKey(keyprefix, asset1.getSensorid(), asset1.getOwnerorg()).toString(), asset1.toString());
                Instant time = Instant.now();
                when(stub.getTxTimestamp()).thenReturn(time);
                String res = contract.confirmCertification(ctx, asset1.getSensorid(), asset1.getOwnerorg());
                assertEquals(helper.parseJson(res).get("status").getAsString(), "200");
            }

            @Test void validTest(){
                Instant time = Instant.now();
                LocalDateTime tDateTime = LocalDateTime.ofInstant(time, ZoneOffset.UTC);
                asset1.setExpirydate(tDateTime.plus(3, ChronoUnit.YEARS).toInstant(ZoneOffset.UTC).toString());
                asset2.setExpirydate(tDateTime.minus(2, ChronoUnit.SECONDS).toInstant(ZoneOffset.UTC).toString());
                
                when(ctx.getStub()).thenReturn(stub);
                when(stub.getStringState(helper.createCompositeKey(keyprefix, asset1.getSensorid(), asset1.getOwnerorg()).toString()))
                        .thenReturn(asset1.toString());

                when(stub.getStringState(helper.createCompositeKey(keyprefix, asset2.getSensorid(), asset2.getOwnerorg()).toString()))
                        .thenReturn(asset2.toString());

                when(stub.getMspId()).thenReturn("inspectorOrg");
                when(stub.getTxTimestamp()).thenReturn(time);

                String respAsset1 = contract.isValid(ctx, asset1.getSensorid(), asset1.getOwnerorg());
                String respAsset2 = contract.isValid(ctx, asset2.getSensorid(), asset2.getOwnerorg());

                assertEquals(helper.parseJson(respAsset1).get("status").getAsString(), "200");
                assertEquals(helper.parseJson(respAsset2).get("status").getAsString(), "400");


            }
    
        }

}