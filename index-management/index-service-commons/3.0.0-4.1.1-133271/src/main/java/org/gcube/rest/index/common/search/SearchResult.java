package org.gcube.rest.index.common.search;

import java.util.Map;

/**
 * 
 * @author efthimis
 *
 */
public class SearchResult {
	
	private String recordId;
	private String collectionId;
	private Map<String,Object> source;

	
	public SearchResult(String recordId, String collectionId,Map<String, Object> source) {

		this.recordId = recordId;
		this.collectionId = collectionId;
		this.source = source;
	}
	
	public String getRecordId() {
		return recordId;
	}
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}
	public String getCollectionId() {
		return collectionId;
	}
	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}
	public Map<String, Object> getSource() {
		return source;
	}
	public void setSource(Map<String, Object> source) {
		this.source = source;
	}

	@Override
	public String toString() {
		return "SearchResult [recordId=" + recordId
				+ ", collectionId=" + collectionId + ", source=" + source + "]";
	}
	
	
	
	

}
