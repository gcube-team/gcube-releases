package org.gcube.datapublishing.sdmx.datasource.data;

import org.gcube.datapublishing.sdmx.datasource.data.exceptions.QueryBuilderNotGeneratedException;

public interface DataRetriever<R extends QueryFilterProvider>  {

	public ResultSetExtractor generateResultSetExtractor();
	
	public R buildDataQuery () throws QueryBuilderNotGeneratedException;
	
}
