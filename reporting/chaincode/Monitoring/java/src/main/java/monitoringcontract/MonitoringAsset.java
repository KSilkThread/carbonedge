package monitoringcontract;

import java.util.Objects;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.google.gson.Gson;


@DataType
public class MonitoringAsset {

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

    public MonitoringAsset(final String sensorid, final String org, final int data, final String timestamp){
        this.sensorid = sensorid;
        this.organisation = org;
        this.data = data;
        this.timestamp = timestamp;
    }

    public int getData() {
        return data;
    }

    public String getOrganisation() {
        return organisation;
    }

    public String getSensorid() {
        return sensorid;
    }

    public String toJSON(){
        return new Gson().toJson(this);
    }

    public static MonitoringAsset fromJSON(String jsonString){
        return new Gson().fromJson(jsonString, MonitoringAsset.class);
    }
    
    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSensorid(), getOrganisation(), getData(), getTimestamp());
    }

    @Override
    public boolean equals(Object obj){

        if(obj == this){
            return true;
        }

        if ((obj == null) || (this.getClass() != obj.getClass())) {
            return false;
        }

        MonitoringAsset other = (MonitoringAsset) obj;

        return Objects.deepEquals(new String[] {this.getSensorid(), this.getOrganisation()},
                                  new String[] {other.getSensorid(), other.getOrganisation()}) 
                                  && Objects.deepEquals(this.getData(), other.getData())
                                  && Objects.deepEquals(this.getTimestamp(), other.getTimestamp());
    }

    @Override
    public String toString(){
        return toJSON();
    }
}
