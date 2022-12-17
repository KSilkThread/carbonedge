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
    private boolean posdrift;
    @Property
    private boolean negdrift;

    public CusumDrift(String value, String sumpos, String sumneg, boolean posdrift,boolean negdrift){
        this.value = new BigDecimal(value);
        this.sumpos = new BigDecimal(sumpos);
        this.sumneg = new BigDecimal(sumneg);
        this.negdrift = negdrift;
        this.posdrift = posdrift;
    }

    
    public CusumDrift(BigDecimal measurement, BigDecimal sumpos, BigDecimal sumneg, boolean posdrift, boolean negdrift) {
        this.value = measurement;
        this.sumpos = sumpos;
        this.sumneg = sumneg;
        this.negdrift = negdrift;
        this.posdrift = posdrift;
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

    public boolean isPosdrift() {
        return posdrift;
    }

    public boolean isNegdrift() {
        return negdrift;
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
        return Objects.hash(this.getValue(), this.getSumpos(), this.getSumneg(), this.isPosdrift(), this.isNegdrift());
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
                                  && Objects.deepEquals(this.isPosdrift(), other.isPosdrift())
                                  && Objects.deepEquals(this.isNegdrift(), other.isNegdrift());
    }
                                 

    
    /** 
     * @return String
     */
    @Override
    public String toString() {
        return toJSON();
    }

    
}