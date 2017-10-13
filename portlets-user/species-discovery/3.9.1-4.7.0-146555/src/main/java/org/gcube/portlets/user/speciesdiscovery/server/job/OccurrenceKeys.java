package org.gcube.portlets.user.speciesdiscovery.server.job;

import java.util.ArrayList;
import java.util.List;

public class OccurrenceKeys {
	
	private List<String> listKey = new ArrayList<String>();
	private int totalOccurrence = 0;

	public OccurrenceKeys() {
	}

	public OccurrenceKeys(List<String> listKey, int totalOccurrence) {
		this.listKey = listKey;
		this.totalOccurrence = totalOccurrence;
	}
	
	
	public List<String> getListKey() {
		return listKey;
	}
	public void setListKey(List<String> listKey) {
		this.listKey = listKey;
	}
	public int getTotalOccurrence() {
		return totalOccurrence;
	}
	public void setTotalOccurrence(int totalOccurrence) {
		this.totalOccurrence = totalOccurrence;
	}

	
}
