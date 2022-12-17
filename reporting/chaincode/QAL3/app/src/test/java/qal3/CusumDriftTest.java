package qal3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class CusumDriftTest {

    CusumDrift drift = new CusumDrift("2", "0", "0", false, true);
    CusumDrift drift2 = new CusumDrift("2", "1", "0", true, false);


    @Test void isEquals(){
        assertEquals(drift, drift);
    }

    @Test void isnotEquals(){
        assertNotEquals(drift, drift2);
    }

    @Test void testJSON(){
        String json = drift.toJSON();
        CusumDrift driftcopy = CusumDrift.fromJSON(json);
        assertEquals(driftcopy, drift);
    }
    
}
