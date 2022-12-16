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

    public CusumDrift(String value, String sumpos, String sumneg, boolean valid){
        this.value = new BigDecimal(value);
        this.sumpos = new BigDecimal(sumpos);
        this.sumneg = new BigDecimal(sumneg);
        this.valid = valid;
    }

    
    public CusumDrift(BigDecimal measurement, BigDecimal sumpos, BigDecimal sumneg, boolean valid) {
        this.value = measurement;
        this.sumpos = sumpos;
        this.sumneg = sumneg;
        this.valid = valid;
    }


    /** 
     * @return BigDecimal
     */
    public BigDecimal getValue() {
        return value;
    }

    
    /** 
     * @return BigDecimal
     */
    public BigDecimal getSumneg() {
        return sumneg;
    }

    
    /** 
     * @return BigDecimal
     */
    public BigDecimal getSumpos() {
        return sumpos;
    }

    
    /** 
     * @return boolean
     */
    public boolean isValid() {
        return valid;
    }

    
    /** 
     * @return String
     */
    public String toJSON(){
        return new Gson().toJson(this);
    }

    
    /** 
     * @param json
     * @return CusumDrift
     */
    public static CusumDrift fromJSON(String json){
        return new Gson().fromJson(json, CusumDrift.class);
    }

    
    /** 
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getValue(), this.getSumpos(), this.getSumneg(), this.isValid());
    }

    
    /** 
     * @param obj
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }

        if((obj == null) || (this.getClass() != obj.getClass())){
            return false;
        }

        CusumDrift other = (CusumDrift) obj;

        return Objects.deepEquals(new BigDecimal[] {this.getValue(), this.getSumpos(), this.getSumneg()}, 
                                  new BigDecimal[] {other.getValue(), other.getSumpos(), other.getSumneg()})
                                  && Objects.deepEquals(this.isValid(), other.isValid());
    }

    
    /** 
     * @return String
     */
    @Override
    public String toString() {
        return toJSON();
    }

    
}