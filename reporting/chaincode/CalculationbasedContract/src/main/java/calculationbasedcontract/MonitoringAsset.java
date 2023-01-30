package calculationbasedcontract;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.google.gson.Gson;

@DataType
public class MonitoringAsset {

    @Property
    //org
    private final String organisation;

    @Property
    //Data amount
    private final double data;
    
    @Property
    //timestamp
    private final String timestamp;

    public MonitoringAsset(final String org, final double data, final String timestamp){
        this.organisation = org;
        this.data = data;
        this.timestamp = timestamp;
    }

    
    /** 
     * @return int
     */
    public double getData() {
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
    public String toJSON(){
        return new Gson().toJson(this);
    }

    
    /** 
     * @param jsonString
     * @return MonitoringAsset
     */
    public static MonitoringAsset fromJSON(String jsonString){
        return new Gson().fromJson(jsonString, MonitoringAsset.class);
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
        return Objects.hash(getOrganisation(), getData(), getTimestamp());
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

        MonitoringAsset other = (MonitoringAsset) obj;

        return Objects.deepEquals(new String[] {this.getOrganisation()},
                                  new String[] {other.getOrganisation()}) 
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
