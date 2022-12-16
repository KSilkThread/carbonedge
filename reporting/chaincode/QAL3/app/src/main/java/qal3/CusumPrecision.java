package qal3;

import java.math.BigDecimal;
import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.google.gson.Gson;

@DataType
public class CusumPrecision {

    @Property
    private BigDecimal value;
    @Property
    private BigDecimal delta;
    @Property
    private BigDecimal deltapowtwohalf;
    @Property
    private BigDecimal s;
    @Property
    private boolean valid;
    @Property
    private BigDecimal correction;

    public CusumPrecision(BigDecimal value, BigDecimal delta, BigDecimal deltapowtwohalf, BigDecimal s, boolean valid, BigDecimal correction){
        this.value = value;
        this.delta = delta;
        this.deltapowtwohalf = deltapowtwohalf;
        this.s = s;
        this.valid = valid;
        this.correction = correction;
    }

    public BigDecimal getValue() {
        return value;
    }

    public BigDecimal getDelta() {
        return delta;
    }

    public BigDecimal getDeltapowtwohalf() {
        return deltapowtwohalf;
    }

    public BigDecimal getS() {
        return s;
    }
    
    public boolean isValid() {
        return valid;
    }

    public BigDecimal getCorrection() {
        return correction;
    }

    public void setCorrection(BigDecimal correction) {
        this.correction = correction;
    }

    public void setDelta(BigDecimal delta) {
        this.delta = delta;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public void setDeltapowtwohalf(BigDecimal deltapowtwohalf) {
        this.deltapowtwohalf = deltapowtwohalf;
    }

    public void setS(BigDecimal s) {
        this.s = s;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String toJSON(){
        return new Gson().toJson(this);
    }

    public static CusumPrecision fromJSON(String json){
        return new Gson().fromJson(json, CusumPrecision.class);
    }
    @Override
    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }

        if((obj == null) || (this.getClass() != obj.getClass())){
            return false;
        }

        CusumPrecision other = (CusumPrecision) obj;

        return Objects.deepEquals(new BigDecimal[] {this.getValue(), this.getDelta(), this.getDeltapowtwohalf(), this.getS(), this.getCorrection()}, 
                                  new BigDecimal[] {other.getValue(), other.getDelta(), other.getDeltapowtwohalf(), other.getS(), other.getCorrection()})
                                  && Objects.deepEquals(this.isValid(), other.isValid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getValue(), this.getDelta(), this.getDeltapowtwohalf(), this.getS(), this.getCorrection(), this.isValid());
    }

    @Override
    public String toString() {
        return toJSON();
    }
    
}
