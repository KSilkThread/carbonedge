package validationcontract;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.google.gson.Gson;

@DataType
public class ValidationAsset {

    @Property
    private final String sensorid;

    @Property
    private final String org;

    @Property
    private final String[] requiredcerts;

    public ValidationAsset(String sensorid, String organisation, String[] certificates){
        this.sensorid = sensorid;
        this.org = organisation;
        this.requiredcerts = certificates;
    }

    
    /** 
     * @return String
     */
    public String getOrg() {
        return org;
    }

    
    /** 
     * @return String[]
     */
    public String[] getRequiredcerts() {
        return requiredcerts;
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
    @Override
    public String toString() {
        return this.toJSON();
    }

    
    /** 
     * @return String
     */
    public String toJSON(){
        return new Gson().toJson(this);
    }

    
    /** 
     * @param json
     * @return ValidationAsset
     */
    public static ValidationAsset fromJSON(String json){
        return new Gson().fromJson(json, ValidationAsset.class);
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

        if((obj.getClass() != this.getClass()) || (obj == null)){
            return false;
        }

        ValidationAsset other = (ValidationAsset) obj;

        return Objects.deepEquals(new String[] {this.sensorid, this.org}, 
                                  new String[] {other.sensorid, other.org}) 
                                  && Objects.deepEquals(this.requiredcerts, other.requiredcerts);
    }

    
    /** 
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.sensorid, this.org, this.requiredcerts);
    }
    
}
