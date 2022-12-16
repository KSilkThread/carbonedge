package qal3;

import java.math.BigDecimal;
import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.google.gson.Gson;

/**
 * QAL3Drift
 */
@DataType
public class CusumDrift {

    @Property
    private BigDecimal value;
    @Property
    private BigDecimal sumpos;
    @Property
    private BigDecimal sumneg;
    @Property
    private boolean valid;
    @Property
    private BigDecimal adjustment;

    public CusumDrift(BigDecimal value, BigDecimal sumpos, BigDecimal sumneg, boolean valid, BigDecimal adjustment){
        this.value = value;
        this.sumpos = sumpos;
        this.sumneg = sumneg;
        this.valid = valid;
        this.adjustment = adjustment;
    }

    public BigDecimal getValue() {
        return value;
    }

    public BigDecimal getAdjustment() {
        return adjustment;
    }

    public BigDecimal getSumneg() {
        return sumneg;
    }

    public BigDecimal getSumpos() {
        return sumpos;
    }

    public boolean isValid() {
        return valid;
    }

    public String toJSON(){
        return new Gson().toJson(this);
    }

    public static CusumDrift fromJSON(String json){
        return new Gson().fromJson(json, CusumDrift.class);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getValue(), this.getSumpos(), this.getSumneg(), this.isValid(), this.getAdjustment());
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }

        if((obj == null) || (this.getClass() != obj.getClass())){
            return false;
        }

        CusumDrift other = (CusumDrift) obj;

        return Objects.deepEquals(new BigDecimal[] {this.getValue(), this.getSumpos(), this.getSumneg(), this.getAdjustment()}, 
                                  new BigDecimal[] {other.getValue(), other.getSumpos(), other.getSumneg(), other.getAdjustment()})
                                  && Objects.deepEquals(this.isValid(), other.isValid());
    }

    @Override
    public String toString() {
        return toJSON();
    }

    
}