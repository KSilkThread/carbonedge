package measurementCC;

import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.hyperledger.fabric.shim.ledger.KeyValue;

import measurementcontract.MeasurementAsset;

public class MockKeyValue implements KeyValue {

    private final CompositeKey key;
    private final String value;

    public MockKeyValue(final MeasurementAsset asset, final String keyPrefixString){
        super();
        this.key = new CompositeKey(keyPrefixString, new String[] {asset.getOrganisation(), asset.getSensorid(), asset.getTimestamp()});
        this.value = asset.toJSON();
    }

    @Override
    public String getKey() {
        return this.key.toString();
    }

    @Override
    public byte[] getValue() {
        return this.value.getBytes();
    }

    @Override
    public String getStringValue() {
        return this.value;
    }
    
}
