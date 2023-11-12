package certificateCC;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

public class HashTest {

    @Test void hashTest() throws NoSuchAlgorithmException{
        String adminX509 = "-----BEGIN CERTIFICATE-----\nMIIB9TCCAZugAwIBAgIUB74ebkJSYz0JGQqgvGnzSxehUFIwCgYIKoZIzj0EAwIw\ncjELMAkGA1UEBhMCVVMxFzAVBgNVBAgMDk5vcnRoIENhcm9saW5hMRAwDgYDVQQH\nDAdSYWxlaWdoMRkwFwYDVQQKDBBvcmcwLmV4YW1wbGUuY29tMR0wGwYDVQQDDBRj\nYTEub3JnMC5leGFtcGxlLmNvbTAeFw0yMjExMTUxNTAwMDBaFw0yMzExMTUxNTA1\nMDBaMCExDzANBgNVBAsTBmNsaWVudDEOMAwGA1UEAxMFYWRtaW4wWTATBgcqhkjO\nPQIBBggqhkjOPQMBBwNCAATseQKUU5PzLHZwqaoi4ObwwqLbg94R6twu976Er91K\nRWp5XCWVP32V2iUJQMouXg+pgFPYdujnxKoiz9O+hUSTo2AwXjAOBgNVHQ8BAf8E\nBAMCB4AwDAYDVR0TAQH/BAIwADAdBgNVHQ4EFgQU08Zo5flczT4P7LEiTWXwtqHM\nST4wHwYDVR0jBBgwFoAUX8HeVPYAR+E9TvCsp4qYfF+TYZswCgYIKoZIzj0EAwID\nSAAwRQIhAJe8tkyiRQ94R9QyXxT7x7ncJ5xOsBgROufO+SXUz4yFAiB+lCNS3N6x\nxRN8j5QWqO3RKFTFBoCsaqs5knOpCk3Rcg==\n-----END CERTIFICATE-----\n";
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashedString = digest.digest(adminX509.getBytes(StandardCharsets.UTF_8));
        System.out.println(new String(hashedString, StandardCharsets.UTF_8));
    }

    @Test void parseInstantTest(){
        Instant instant = Instant.now();
        String s = instant.toString();
        Instant instant2 = Instant.parse(s);
        assertEquals(instant, instant2); 
    }

    @Test void addthreeYears(){
        Instant instant = Instant.now();
        LocalDateTime time = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        time = time.plus(3, ChronoUnit.YEARS);
        Instant instant2 = time.toInstant(ZoneOffset.UTC);
        //System.out.println(instant2.toString());
        //instant = (Instant) Period.ofYears(3).addTo(instant);

        System.out.println(instant2.toString());
    }
    
}
