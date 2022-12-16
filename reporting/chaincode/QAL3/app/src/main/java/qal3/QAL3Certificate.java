package qal3;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.google.gson.Gson;


@DataType
public class QAL3Certificate {

    @Property
    private final BigDecimal sAmsdrift;

    @Property
    private final BigDecimal sAmsprecision;

    @Property
    private final String sensorid;

    @Property
    private final String ownerorg;

    @Property
    private CusumDrift[] driftzero = {};

    @Property
    private CusumDrift[] driftreference = {};

    @Property
    private CusumPrecision[] precisionzero = {};

    @Property
    private CusumPrecision[] precisionreference = {};


    public QAL3Certificate(String sensorid, String ownerorg, String sAmsdrift, String sAmsprecision){
        this.sensorid = sensorid;
        this.ownerorg = ownerorg;
        this.sAmsdrift = new BigDecimal(sAmsdrift);
        this.sAmsprecision = new BigDecimal(sAmsprecision);
    }

    public String getSensorid() {
        return sensorid;
    }

    public String getOwnerorg() {
        return ownerorg;
    }

    public BigDecimal getsAmsdrift() {
        return sAmsdrift;
    }

    public BigDecimal getsAmsprecision() {
        return sAmsprecision;
    }

    public CusumDrift[] getDriftzero() {
        return driftzero;
    }

    public CusumDrift[] getDriftreference() {
        return driftreference;
    }

    public CusumPrecision[] getPrecisionzero() {
        return precisionzero;
    }

    public CusumPrecision[] getPrecisionreference() {
        return precisionreference;
    }

    public void appendZeroDrift(CusumDrift object){
        final int n = getDriftzero().length;
        this.driftzero = Arrays.copyOf(getDriftzero(), n + 1);
        this.driftzero[n] = object;
    }

    public void appendReferenceDrift(CusumDrift object){
        final int n = getDriftreference().length;
        this.driftreference = Arrays.copyOf(getDriftreference(), n + 1);
        this.driftreference[n] = object;
    }

    public void appendZeroPrecision(CusumPrecision object){
        final int n = getPrecisionzero().length;
        this.precisionzero = Arrays.copyOf(getPrecisionzero(), n + 1);
        this.precisionzero[n] = object;
    }

    public void appendReferencePrecision(CusumPrecision object){
        final int n = getPrecisionreference().length;
        this.precisionreference = Arrays.copyOf(getPrecisionreference(), n + 1);
        this.precisionreference[n] = object;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }

        if((obj == null) || (this.getClass() != obj.getClass())){
            return false;
        }

        QAL3Certificate other = (QAL3Certificate) obj;

        return Objects.deepEquals(new String[] {this.getSensorid(), this.getOwnerorg()},
                                  new String[] {other.getSensorid(), other.getOwnerorg()}) &&
                                  Objects.deepEquals(new BigDecimal[] {this.getsAmsdrift(), this.getsAmsprecision()}, 
                                                     new BigDecimal[] {other.getsAmsdrift(), other.getsAmsprecision()}) &&
                                  Objects.deepEquals(this.getDriftzero(), other.getDriftzero()) &&
                                  Objects.deepEquals(this.getDriftreference(), other.getDriftreference()) &&
                                  Objects.deepEquals(this.getPrecisionzero(), other.getPrecisionzero()) &&
                                  Objects.deepEquals(this.getPrecisionreference(), other.getPrecisionreference());

    
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getSensorid(), this.getOwnerorg(), this.getsAmsdrift(), this.getsAmsprecision(),
                            this.getDriftzero(), this.getDriftreference(), this.getPrecisionzero(), this.getPrecisionreference());

    }

    public String toJSON(){
        return new Gson().toJson(this);
    }

    public static QAL3Certificate fromJSON(String json){
        return new Gson().fromJson(json, QAL3Certificate.class);
    }

    @Override
    public String toString() {
        return toJSON();
    }
    
}





