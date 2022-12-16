package qal3;

import java.math.BigDecimal;
import java.math.MathContext;

public class Helper {
    //Static drift constants
    final BigDecimal constxdrift = new BigDecimal("0.501");
    final BigDecimal constydrift = new BigDecimal("2.85");

    //Static precision constants
    final BigDecimal constxprecision = new BigDecimal("1.85");
    final BigDecimal constyprecision = new BigDecimal("6.90");
    final BigDecimal constzprecision = new BigDecimal("2");

    //TODO Alarm
    public CusumDrift calculateDrift(BigDecimal sAmsdrift, CusumDrift[] values, BigDecimal measurement){
        BigDecimal threshold = constxdrift.multiply(sAmsdrift);
        BigDecimal testvalue = constydrift.multiply(sAmsdrift);
        BigDecimal alarm = threshold.add(testvalue);

        CusumDrift lastobject = values[values.length-1];
        BigDecimal sumpos = lastobject.getSumpos().add(measurement).subtract(threshold).compareTo(BigDecimal.ZERO) == 1 ? lastobject.getSumpos().add(measurement).subtract(threshold) : BigDecimal.ZERO;
        BigDecimal sumneg = lastobject.getSumneg().subtract(measurement).subtract(threshold).compareTo(BigDecimal.ZERO) == 1 ? lastobject.getSumneg().subtract(measurement).subtract(threshold) : BigDecimal.ZERO;
        return new CusumDrift(measurement, sumpos, sumneg, ((sumpos.compareTo(testvalue) == -1)&&(sumneg.compareTo(testvalue) == -1)));
    }

    //TODO Alarm
    public CusumPrecision calculatePrecision(BigDecimal sAmsPrecision, CusumPrecision[] values, BigDecimal measurement){

        BigDecimal thresholdsquared = constxprecision.multiply(sAmsPrecision).multiply(sAmsPrecision);
        BigDecimal testvalue = constyprecision.multiply(sAmsPrecision).multiply(sAmsPrecision);
        BigDecimal thresholdsimple = constxprecision.add(constzprecision).sqrt(new MathContext(5)).multiply(sAmsPrecision);
        BigDecimal alarm = constxprecision.add(constyprecision).multiply(constzprecision).multiply(sAmsPrecision).multiply(sAmsPrecision).sqrt(new MathContext(5));

        CusumPrecision lastobject = values[values.length-1];

        BigDecimal delta = measurement.subtract(lastobject.getValue());
        BigDecimal dpowhalf = delta.multiply(delta).divide(constzprecision);
        BigDecimal s = lastobject.getS().compareTo(BigDecimal.ZERO) == 1 ? lastobject.getS().add(dpowhalf).subtract(thresholdsquared) : BigDecimal.ZERO.add(dpowhalf).subtract(thresholdsquared);

        return new CusumPrecision(measurement, delta, dpowhalf, s, s.compareTo(testvalue) == -1);
    }
    
}
