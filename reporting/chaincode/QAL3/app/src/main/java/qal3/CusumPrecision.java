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

    public CusumPrecision(String value, String delta, String deltapowtwohalf, String s, boolean valid){
        this.value = new BigDecimal(value);
        this.delta = new BigDecimal(delta);
        this.deltapowtwohalf = new BigDecimal(deltapowtwohalf);
        this.s = new BigDecimal(s);
        this.valid = valid;
    }

    
    public CusumPrecision(BigDecimal measurement, BigDecimal delta, BigDecimal dpowhalf, BigDecimal s, boolean b) {
        this.value = measurement;
        this.delta = delta;
        this.deltapowtwohalf = dpowhalf;
        this.s = s;
        this.valid = b;
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
    public BigDecimal getDelta() {
        return delta;
    }

    
    /** 
     * @return BigDecimal
     */
    public BigDecimal getDeltapowtwohalf() {
        return deltapowtwohalf;
    }

    
    /** 
     * @return BigDecimal
     */
    public BigDecimal getS() {
        return s;
    }
    
    
    /** 
     * @return boolean
     */
    public boolean isValid() {
        return valid;
    }

    
    /** 
     * @param delta
     */
    public void setDelta(BigDecimal delta) {
        this.delta = delta;
    }

    
    /** 
     * @param value
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    
    /** 
     * @param deltapowtwohalf
     */
    public void setDeltapowtwohalf(BigDecimal deltapowtwohalf) {
        this.deltapowtwohalf = deltapowtwohalf;
    }

    
    /** 
     * @param s
     */
    public void setS(BigDecimal s) {
        this.s = s;
    }

    
    /** 
     * @param valid
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    
    /** 
     * @return String
     */
    public String toJSON(){
        return new Gson().toJson(this);
    }

    
    /** 
     * @param json
     * @return CusumPrecision
     */
    public static CusumPrecision fromJSON(String json){
        return new Gson().fromJson(json, CusumPrecision.class);
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

        CusumPrecision other = (CusumPrecision) obj;

        return Objects.deepEquals(new BigDecimal[] {this.getValue(), this.getDelta(), this.getDeltapowtwohalf(), this.getS()}, 
                                  new BigDecimal[] {other.getValue(), other.getDelta(), other.getDeltapowtwohalf(), other.getS()})
                                  && Objects.deepEquals(this.isValid(), other.isValid());
    }

    
    /** 
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getValue(), this.getDelta(), this.getDeltapowtwohalf(), this.getS(), this.isValid());
    }

    
    /** 
     * @return String
     */
    @Override
    public String toString() {
        return toJSON();
    }
    
}
