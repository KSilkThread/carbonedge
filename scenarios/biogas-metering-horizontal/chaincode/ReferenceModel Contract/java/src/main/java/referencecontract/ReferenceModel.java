package referencecontract;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.google.gson.Gson;

@DataType
public class ReferenceModel {
    @Property
    private final int version;
    @Property
    private final double benchmark;



    public ReferenceModel(int version, double benchmark){
        this.version = version;
        this.benchmark = benchmark;
    }

    public double getBenchmark() {
        return benchmark;
    }

    public int getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVersion(), getBenchmark());
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }

        if((obj == null) || (this.getClass() != obj.getClass())){
            return false;
        }

        ReferenceModel other = (ReferenceModel) obj;
        return this.getVersion() == other.getVersion() && this.getBenchmark() == other.getBenchmark();
    }

    public String toJson(){
        return new Gson().toJson(this);
    }

    public static ReferenceModel fromJSON(String json){
        return new Gson().fromJson(json, ReferenceModel.class);
    }

    @Override
    public String toString() {
        return toJson();
    }
    
}
