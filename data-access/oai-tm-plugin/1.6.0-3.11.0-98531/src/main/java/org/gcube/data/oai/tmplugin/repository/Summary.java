package org.gcube.data.oai.tmplugin.repository;

import java.util.Calendar;

public class Summary {

	private Calendar lastUpdate;
	private long cardinality;
	
	public Summary(Calendar lastUpdate, long cardinality) {
		super();
		this.lastUpdate = lastUpdate;
		this.cardinality = cardinality;
	}

	public Calendar lastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Calendar lustUpdate) {
		this.lastUpdate = lustUpdate;
	}

	public long cardinality() {
		return cardinality;
	}

	public void setCardinality(long cardinality) {
		this.cardinality = cardinality;
	}

	@Override
	public String toString() {
		return "Summary [cardinality=" + cardinality + ", lastUpdate="
				+ lastUpdate.getTime() + "]";
	}
	
	
	
}
