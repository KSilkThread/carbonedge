package certificateCC;



import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.hyperledger.fabric.shim.ledger.KeyValue;

import certificatecontract.CertificateAsset;

public class MockKeyValue implements KeyValue {

    private final CompositeKey key;
    private final String value;

    public MockKeyValue(final CertificateAsset asset, final String keyPrefixString){
        super();
        this.key = new CompositeKey(keyPrefixString, new String[] {asset.getOwnerorg(), asset.getSensorid()});
        this.value = asset.toJson();
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
