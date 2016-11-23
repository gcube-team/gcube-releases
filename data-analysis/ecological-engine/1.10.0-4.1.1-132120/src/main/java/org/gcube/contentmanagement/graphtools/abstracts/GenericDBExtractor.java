package org.gcube.contentmanagement.graphtools.abstracts;

import java.util.Map;


public interface GenericDBExtractor {

	public SamplesTable getMonoDimTable (String table, String column);
	
	public Map<String, SamplesTable> getMultiDimTemporalTables(String table, String xDimension, String groupDimension, String yValue, String speciesColumn, String... yFilters);
	
}
