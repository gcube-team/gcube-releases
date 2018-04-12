package org.gcube.accounting.aggregator.persist;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.gcube.accounting.aggregator.persistence.CouchBaseConnector;
import org.gcube.accounting.aggregator.status.AggregationState;
import org.gcube.accounting.aggregator.status.AggregationStatus;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.PersistTo;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.error.DocumentDoesNotExistException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class DeleteDocument extends DocumentElaboration {
	
	public DeleteDocument(AggregationStatus aggregationStatus, File file, Bucket bucket){
		super(aggregationStatus, AggregationState.DELETED, file, bucket, aggregationStatus.getOriginalRecordsNumber());
	}
	
	@Override
	protected void elaborateLine(String line) throws Exception {
		JsonObject jsonObject = JsonObject.fromJson(line);
		String id = jsonObject.getString(ID);
		try {
			bucket.remove(id, PersistTo.MASTER, CouchBaseConnector.CONNECTION_TIMEOUT_BUCKET, TimeUnit.SECONDS);
		}catch (DocumentDoesNotExistException e) {
			// OK it can happen when the delete procedure were started but was interrupted
		}
	}

	@Override
	protected void afterElaboration() {
		// Nothing to do
	}
	
}
