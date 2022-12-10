package qal2contract;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.google.gson.Gson;

@DataType
public class Qal2Certificate {

    @Property
    private final String sensorid;

    @Property
    private final String ownerorg;

    @Property
    private String inspectorcert;

    @Property
    private String inspectororganisation;

    @Property
    private String expirydate;


    public Qal2Certificate(String sensorid, String ownerorg, String inspectorcert, String inspectororganisation, String expirydate){
        this.sensorid = sensorid;
        this.ownerorg = ownerorg;
        this.inspectorcert = inspectorcert;
        this.inspectororganisation = inspectororganisation;
        this.expirydate = expirydate;
    }

    public Qal2Certificate(String sensorid, String ownerorg){
        this(sensorid, ownerorg, null, null, null);
    }

    public Qal2Certificate(String sensorid, String ownerorg, String inspectorcert, String inspectororganisation){
        this(sensorid, ownerorg, inspectorcert, inspectororganisation, null);
    }

    public String getSensorid() {
        return sensorid;
    }

    public String getOwnerorg() {
        return ownerorg;
    }

    public String getInspectororganisation() {
        return inspectororganisation;
    }

    public String getInspectorcert() {
        return inspectorcert;
    }

    public String getExpirydate() {
        return expirydate;
    }

    public void setInspectorcert(String inspectorcert) {
        this.inspectorcert = inspectorcert;
    }

    public void setInspectororganisation(String inspectororganisation) {
        this.inspectororganisation = inspectororganisation;
    }

    public void setExpirydate(String expirydate) {
        this.expirydate = expirydate;
    }

    public String toJson(){
        return new Gson().toJson(this);
    }

    public static Qal2Certificate fromJSON(String jsonstring){
        return new Gson().fromJson(jsonstring, Qal2Certificate.class);
    }

    @Override
    public String toString() {
        return this.toJson();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }

        if((obj == null) || (obj.getClass() != this.getClass())){
            return false;
        }

        Qal2Certificate other = (Qal2Certificate) obj;

        return Objects.deepEquals(new String[] {this.sensorid, this.ownerorg, this.inspectorcert, this.inspectororganisation, this.expirydate}
                                 ,new String[] {other.sensorid, other.ownerorg, other.inspectorcert, other.inspectororganisation, other.expirydate});
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.sensorid, this.ownerorg, this.inspectorcert, this.inspectororganisation, this.expirydate);
    }
    
}
