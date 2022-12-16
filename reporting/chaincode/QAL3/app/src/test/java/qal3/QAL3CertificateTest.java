package qal3;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

public class QAL3CertificateTest {

    QAL3Certificate cert = new QAL3Certificate("sesnor", "org1", "3", "3");

    @Test void testJson(){
        System.out.println(new Gson().toJson(cert.getDriftzero()));
    }
    
}
