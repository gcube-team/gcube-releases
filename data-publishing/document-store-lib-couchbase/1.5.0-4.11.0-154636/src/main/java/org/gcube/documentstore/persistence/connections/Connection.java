package org.gcube.documentstore.persistence.connections;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;

public class Connection {

	Map<String, Bucket> bucketsMap = Collections.synchronizedMap(new HashMap<String, Bucket>());
	Cluster cluster;
	
	public Connection(Cluster cluster) {
		super();
		this.cluster = cluster;
	}

	public Map<String, Bucket> getBucketsMap() {
		return bucketsMap;
	}
	
	public Cluster getCluster(){
		return this.cluster;
	}

	@Override
	public String toString() {
		return "Connection [bucketsMap=" + bucketsMap + ", cluster=" + cluster
				+ "]";
	}
	
	
}
