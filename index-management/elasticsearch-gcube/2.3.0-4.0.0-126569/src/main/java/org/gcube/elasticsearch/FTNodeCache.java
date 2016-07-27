package org.gcube.elasticsearch;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.indexmanagement.common.FullTextIndexType;

import com.google.common.collect.Maps;


public class FTNodeCache implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public Map<String, FullTextIndexType> cachedIndexTypes = null;
	public Map<String, Set<String>> indexTypesByCollIDs = null;
	public Map<String, List<String>> presentableFieldsPerIndexType = null;
	public Map<String, List<String>> searchableFieldsPerIndexType = null;
	public Map<String, List<String>> highlightableFieldsPerIndexType = null;
	
	public FTNodeCache() {
		this.init();
	}
	
	void init() {
		this.cachedIndexTypes =  Maps.newConcurrentMap();
		this.indexTypesByCollIDs = Maps.newConcurrentMap();
		this.presentableFieldsPerIndexType = Maps.newConcurrentMap();;
		this.searchableFieldsPerIndexType = Maps.newConcurrentMap();;
		this.highlightableFieldsPerIndexType = Maps.newConcurrentMap();;
	}
	
	public void invalidate() {
		this.init();
	}

	@Override
	public String toString() {
		return "FTNodeCache [cachedIndexTypes=" + this.cachedIndexTypes
				+ ", indexTypesByCollIDs=" + this.indexTypesByCollIDs
				+ ", presentableFieldsPerIndexType="
				+ this.presentableFieldsPerIndexType
				+ ", searchableFieldsPerIndexType="
				+ this.searchableFieldsPerIndexType
				+ ", highlightableFieldsPerIndexType="
				+ this.highlightableFieldsPerIndexType + "]";
	}

	
	
	
}