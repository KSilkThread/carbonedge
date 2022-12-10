package qal3contract;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;

import com.google.gson.Gson;
import com.google.j2objc.annotations.Property;

@DataType
public class QAL3Certificate {

    @Property
    private final String sensorid;

    @Property
    private final String ownerorg;

    @Property
    private String expirydate;

    @Property
    private final float derivationprecision;

    @Property
    private final float derivationdrift;

    @Property
    private float[] datadrift;

    @Property
    private float[] dataprecision;

    public QAL3Certificate(String sensorid, String ownerorg, float derivationprecision, float derivationdrift, String expirydate, float[] datadrift, float[] dataprecision){
        this.sensorid = sensorid;
        this.ownerorg = ownerorg;
        this.derivationprecision = derivationprecision;
        this.derivationdrift = derivationdrift;
        this.expirydate = expirydate;
        this.dataprecision = dataprecision;
        this.datadrift = datadrift;
    }

    public QAL3Certificate(String sensorid, String ownerorg, float derivationprecision, float derivationdrift){
        this(sensorid, ownerorg, derivationprecision, derivationdrift, null, null, null);
    }

    public String getSensorid() {
        return sensorid;
    }

    public String getOwnerorg() {
        return ownerorg;
    }

    public float[] getDataprecision() {
        return dataprecision;
    }

    public float getDerivationprecision() {
        return derivationprecision;
    }

    public float[] getDatadrift() {
        return datadrift;
    }

    public float getDerivationdrift() {
        return derivationdrift;
    }

    public String getExpirydate() {
        return expirydate;
    }

    public String toJSON(){
        return new Gson().toJson(this);
    }

    public static QAL3Certificate fromJSON(String jsonString){
        return new Gson().fromJson(jsonString, QAL3Certificate.class);
    }

    @Override
    public String toString() {
        return toJSON();
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

        return Objects.deepEquals(new String[] {this.getSensorid(), this.getOwnerorg(), this.getExpirydate()}, 
                                  new String[] {other.getSensorid(), other.getOwnerorg(), other.getExpirydate()})
                                  && Objects.deepEquals(new float[]{this.getDerivationdrift(), this.getDerivationprecision()}, 
                                                        new float[]{other.getDerivationdrift(), other.getDerivationprecision()})
                                                        && Objects.deepEquals(this.getDatadrift(), other.getDatadrift())
                                                        && Objects.deepEquals(this.getDataprecision(), other.getDataprecision());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSensorid(), getOwnerorg(), getDerivationdrift(), getDerivationprecision(), getExpirydate(), getDatadrift(), getDataprecision());
    }



}