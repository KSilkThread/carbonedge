package monitoringcontract;

import java.util.Objects;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.google.gson.Gson;


@DataType
public class MeasurementAsset {

    @Property
    //sensor id
    private final String sensorid;

    @Property
    //org
    private final String organisation;

    @Property
    //Data amount
    private final int data;
    
    @Property
    //timestamp
    private final String timestamp;

    public MeasurementAsset(final String sensorid, final String org, final int data, final String timestamp){
        this.sensorid = sensorid;
        this.organisation = org;
        this.data = data;
        this.timestamp = timestamp;
    }

    
    /** 
     * @return int
     */
    public int getData() {
        return data;
    }

    
    /** 
     * @return String
     */
    public String getOrganisation() {
        return organisation;
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
    public String toJSON(){
        return new Gson().toJson(this);
    }

    
    /** 
     * @param jsonString
     * @return MonitoringAsset
     */
    public static MeasurementAsset fromJSON(String jsonString){
        return new Gson().fromJson(jsonString, MeasurementAsset.class);
    }
    
    
    /** 
     * @return String
     */
    public String getTimestamp() {
        return timestamp;
    }

    
    /** 
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(getSensorid(), getOrganisation(), getData(), getTimestamp());
    }

    
    /** 
     * @param obj
     * @return boolean
     */
    @Override
    public boolean equals(Object obj){

        if(obj == this){
            return true;
        }

        if ((obj == null) || (this.getClass() != obj.getClass())) {
            return false;
        }

        MeasurementAsset other = (MeasurementAsset) obj;

        return Objects.deepEquals(new String[] {this.getSensorid(), this.getOrganisation()},
                                  new String[] {other.getSensorid(), other.getOrganisation()}) 
                                  && Objects.deepEquals(this.getData(), other.getData())
                                  && Objects.deepEquals(this.getTimestamp(), other.getTimestamp());
    }

    
    /** 
     * @return String
     */
    @Override
    public String toString(){
        return toJSON();
    }
}
