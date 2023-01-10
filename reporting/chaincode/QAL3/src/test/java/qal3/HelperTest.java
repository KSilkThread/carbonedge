package qal3;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class HelperTest {

    List<BigDecimal> valuesdrift1 = Arrays.asList(0.30, 0.60, 0.90,1.20,1.50,1.80,2.10,2.40,2.70,3.00,3.30,3.60,3.90)
                                          .stream().map(x-> new BigDecimal(String.valueOf(x)))
                                          .collect(Collectors.toList());

    List<BigDecimal> valuesdrift2 = Arrays.asList(-1,1,4,-1.5,3.5,-3,4.5,0.5,5,-4,9,-1.5,6)
                                          .stream().map(x -> new BigDecimal(String.valueOf(x)))
                                          .collect(Collectors.toList());

    List<BigDecimal> valuesprecision1 = Arrays.asList(0.30, 0.60, 0.90, 1.20, 1.50, 1.80, 2.10, 2.40, 2.70, 3.00, 3.30, 3.60, 3.90)
                                              .stream().map(x -> new BigDecimal(String.valueOf(x)))
                                              .collect(Collectors.toList());

    List<BigDecimal> valuesprecision2 = Arrays.asList(-1, 1, 4, -1.5, 3.5, -3, 4.5, 0.5, 5, -4, 9, -1.5, 6)
                                              .stream().map(x -> new BigDecimal(String.valueOf(x)))
                                              .collect(Collectors.toList());   

    
    
    @Test void checkdrift1(){
        QAL3Certificate cert = new QAL3Certificate("sensor", "org", "3", "3", 2);
        for (BigDecimal value: valuesdrift1){
            CusumDrift drift1 = new Helper().calculateDrift(cert.getsAmsdrift(), cert.getDriftzero(), value);
            cert.appendZeroDrift(drift1);
        }
        assertEquals(cert.getDriftzero()[cert.getDriftzero().length-1], new CusumDrift("3.9", "10.776", "0", true, false));
    }

    @Test void checkdrift2(){
        QAL3Certificate cert = new QAL3Certificate("sensor", "org", "3", "3", 2);
        for (BigDecimal value: valuesdrift2){
            CusumDrift drift1 = new Helper().calculateDrift(cert.getsAmsdrift(), cert.getDriftzero(), value);
            cert.appendZeroDrift(drift1);
        }
        assertEquals(cert.getDriftzero()[cert.getDriftzero().length-1], new CusumDrift("6", "8.991", "0", true, false));
    }

    @Test void checkprecision1(){
        QAL3Certificate cert = new QAL3Certificate("sensor", "org", "3", "3", 2);
        for (BigDecimal value: valuesprecision1){
            CusumPrecision drift1 = new Helper().calculatePrecision(cert.getsAmsdrift(), cert.getPrecisionzero(), value);
            cert.appendZeroPrecision(drift1);
        }
        assertEquals(cert.getPrecisionzero()[cert.getPrecisionzero().length-1], new CusumPrecision("3.9", "0.3", "0.045","-16.605", false));
    }

    @Test void checkprecision2(){
        QAL3Certificate cert = new QAL3Certificate("sensor", "org", "3", "3", 2);
        for (BigDecimal value: valuesprecision2){
            CusumPrecision drift1 = new Helper().calculatePrecision(cert.getsAmsdrift(), cert.getPrecisionzero(), value);
            cert.appendZeroPrecision(drift1);
        }
        assertEquals(cert.getPrecisionzero()[cert.getPrecisionzero().length-1], new CusumPrecision("6", "7.5", "28.125","142.425", true));
    }
}


