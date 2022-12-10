package monitoringcontract;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Helper {

    public Helper(){

    }

    public JsonObject prettyConsortiumJson(List<MonitoringAsset> assetList, String channelname){
        JsonObject json = new JsonObject();

        List<JsonObject> sortedObj = new ArrayList<>();
        List<String> usedorgs = new ArrayList<>();

        json.addProperty("Channel", channelname);

        for (MonitoringAsset asset: assetList){
            if(usedorgs.contains(asset.getOrganisation())) continue;

            List<MonitoringAsset> currentData = assetList.stream().filter(sa -> sa.getOrganisation().equals( asset.getOrganisation()))
                                                              .collect(Collectors.toList());


            sortedObj.add(prettyOrgJson(currentData, asset.getOrganisation()));
            usedorgs.add(asset.getOrganisation());
        }
        
        json.add("Organisations", new Gson().toJsonTree(sortedObj));

        return json;
        
    }

    public JsonObject prettyOrgJson(List<MonitoringAsset> assetList, String org){

        JsonObject json = new JsonObject();

        List<String> usedsensorids = new ArrayList<>();
        List<JsonObject> sortedObj = new ArrayList<>();

        json.addProperty("Organisation", org);

        for (MonitoringAsset asset: assetList){
            if(usedsensorids.contains(asset.getSensorid())) continue;

            List<MonitoringAsset> currentSensorData = assetList.stream().filter(sa -> sa.getSensorid().equals(asset.getSensorid()))
                                                                    .collect(Collectors.toList());

            sortedObj.add(prettySensorJson(currentSensorData, asset.getSensorid()));
            usedsensorids.add(asset.getSensorid());
        }

        json.add("Sensors", new Gson().toJsonTree(sortedObj));

        return json;
    }


    public JsonObject prettySensorJson(List<MonitoringAsset> assetList, String senosrid){
        JsonObject json = new JsonObject();
        json.addProperty("Sensorid", senosrid);
        json.add("Logs", new Gson().toJsonTree(assetList));
        return json;
    }
}
