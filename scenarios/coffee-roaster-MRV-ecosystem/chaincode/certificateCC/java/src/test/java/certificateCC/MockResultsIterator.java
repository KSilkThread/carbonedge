package certificateCC;

import java.util.Iterator;
import java.util.List;

import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

public class MockResultsIterator implements QueryResultsIterator<KeyValue> {

    private final List<KeyValue> assetList;

    public MockResultsIterator(List<KeyValue> assetList){
        super();
        this.assetList = assetList;
    }

    @Override
    public Iterator<KeyValue> iterator() {
        return assetList.iterator();
    }

    @Override
    public void close() throws Exception {
        return;
    }
}

