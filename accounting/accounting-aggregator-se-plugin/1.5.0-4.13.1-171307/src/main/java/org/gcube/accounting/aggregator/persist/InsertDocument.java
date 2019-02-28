package org.gcube.accounting.aggregator.persist;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.gcube.accounting.aggregator.persistence.CouchBaseConnector;
import org.gcube.accounting.aggregator.status.AggregationState;
import org.gcube.accounting.aggregator.status.AggregationStatus;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.PersistTo;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class InsertDocument extends DocumentElaboration {
	
	public InsertDocument(AggregationStatus aggregationStatus, File file, Bucket bucket){
		super(aggregationStatus, AggregationState.ADDED, file, bucket, aggregationStatus.getAggregatedRecordsNumber());
	}
	
	@Override
	protected void elaborateLine(String line) throws Exception {
		JsonObject jsonObject = JsonObject.fromJson(line);
		String id = jsonObject.getString(ID);
		JsonDocument jsonDocument = JsonDocument.create(id, jsonObject);
		bucket.upsert(jsonDocument, PersistTo.MASTER, CouchBaseConnector.CONNECTION_TIMEOUT_BUCKET, TimeUnit.SECONDS);
	}

	@Override
	protected void afterElaboration() {
		// Nothing to do
	}
	
}
