package qal3;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
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
    private final int maintenanceinterval;

    @Property
    private String expirydate;

    @Property
    private CusumDrift[] driftzero = {new CusumDrift("0", "0", "0", false, false)};

    @Property
    private CusumDrift[] driftreference = {new CusumDrift("0", "0", "0", false, false)};

    @Property
    private CusumPrecision[] precisionzero = {new CusumPrecision("0", "0", "0", "0", false)};

    @Property
    private CusumPrecision[] precisionreference = {new CusumPrecision("0", "0", "0", "0", false)};


    public QAL3Certificate(String sensorid, String ownerorg, String sAmsdrift, String sAmsprecision, int maintainanceinterval){
        this.sensorid = sensorid;
        this.ownerorg = ownerorg;
        this.sAmsdrift = new BigDecimal(sAmsdrift);
        this.sAmsprecision = new BigDecimal(sAmsprecision);
        this.maintenanceinterval = maintainanceinterval;
    }

    
    /** 
     * @return String
     */
    public String getSensorid() {
        return sensorid;
    }

    
    /** 
     * @return String
     */
    public String getOwnerorg() {
        return ownerorg;
    }

    
    /** 
     * @return BigDecimal
     */
    public BigDecimal getsAmsdrift() {
        return sAmsdrift;
    }

    
    /** 
     * @return BigDecimal
     */
    public BigDecimal getsAmsprecision() {
        return sAmsprecision;
    }

    
    /** 
     * @return CusumDrift[]
     */
    public CusumDrift[] getDriftzero() {
        return driftzero;
    }

    
    /** 
     * @return CusumDrift[]
     */
    public CusumDrift[] getDriftreference() {
        return driftreference;
    }

    
    /** 
     * @return CusumPrecision[]
     */
    public CusumPrecision[] getPrecisionzero() {
        return precisionzero;
    }

    
    /** 
     * @return CusumPrecision[]
     */
    public CusumPrecision[] getPrecisionreference() {
        return precisionreference;
    }

    public String getExpirydate() {
        return expirydate;
    }

    public int getMaintenanceinterval() {
        return maintenanceinterval;
    }


    public void updateExpirydate(Instant timestamp){
        LocalDateTime time = LocalDateTime.ofInstant(timestamp, ZoneOffset.UTC);
        time = time.plus(getMaintenanceinterval(), ChronoUnit.DAYS);
        Instant newexpiry = time.toInstant(ZoneOffset.UTC);
        this.expirydate = newexpiry.toString();
    }

    
    /** 
     * @param object
     */
    public void appendZeroDrift(CusumDrift object){
        final int n = getDriftzero().length;
        this.driftzero = Arrays.copyOf(getDriftzero(), n + 1);
        this.driftzero[n] = object;
    }

    
    /** 
     * @param object
     */
    public void appendReferenceDrift(CusumDrift object){
        final int n = getDriftreference().length;
        this.driftreference = Arrays.copyOf(getDriftreference(), n + 1);
        this.driftreference[n] = object;
    }

    
    /** 
     * @param object
     */
    public void appendZeroPrecision(CusumPrecision object){
        final int n = getPrecisionzero().length;
        this.precisionzero = Arrays.copyOf(getPrecisionzero(), n + 1);
        this.precisionzero[n] = object;
    }

    
    /** 
     * @param object
     */
    public void appendReferencePrecision(CusumPrecision object){
        final int n = getPrecisionreference().length;
        this.precisionreference = Arrays.copyOf(getPrecisionreference(), n + 1);
        this.precisionreference[n] = object;
    }

    public void reset(){
        appendReferenceDrift(new CusumDrift("0", "0", "0", false, false));
        appendZeroDrift(new CusumDrift("0", "0", "0", false, false));
        appendReferencePrecision(new CusumPrecision("0", "0", "0", "0", false));
        appendZeroPrecision(new CusumPrecision("0", "0", "0", "0", false)); 
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

    
    /** 
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getSensorid(), this.getOwnerorg(), this.getsAmsdrift(), this.getsAmsprecision(),
                            this.getDriftzero(), this.getDriftreference(), this.getPrecisionzero(), this.getPrecisionreference());

    }

    
    /** 
     * @return String
     */
    public String toJSON(){
        return new Gson().toJson(this);
    }

    
    /** 
     * @param json
     * @return QAL3Certificate
     */
    public static QAL3Certificate fromJSON(String json){
        return new Gson().fromJson(json, QAL3Certificate.class);
    }

    
    /** 
     * @return String
     */
    @Override
    public String toString() {
        return toJSON();
    }
    
}





