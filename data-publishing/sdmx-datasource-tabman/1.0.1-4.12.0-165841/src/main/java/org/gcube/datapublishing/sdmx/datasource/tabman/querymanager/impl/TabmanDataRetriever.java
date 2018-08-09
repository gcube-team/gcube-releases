package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.datapublishing.sdmx.datasource.data.DataRetriever;
import org.gcube.datapublishing.sdmx.datasource.data.ResultSetExtractor;
import org.gcube.datapublishing.sdmx.datasource.data.exceptions.QueryBuilderNotGeneratedException;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.exceptions.InvalidInformationSystemDataException;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.json.exception.InvalidFilterParameterException;
import org.sdmxsource.sdmx.api.model.data.query.DataQuery;


public class TabmanDataRetriever  implements DataRetriever<TabmanQuery>{
	
	private TabmanQueryImpl tabmanQuery;

	
	public TabmanDataRetriever(DataQuery dataQuery,TabularDataService service) throws NoSuchTabularResourceException, NoSuchTableException, InvalidInformationSystemDataException, InvalidFilterParameterException {
		TabmanQueryBuilder queryBuilder = new TabmanQueryBuilder();
		this.tabmanQuery =  queryBuilder.buildDataQuery(dataQuery);
		this.tabmanQuery.initQuery(service, true);
	}
	

	
	@Override
	public ResultSetExtractor generateResultSetExtractor() {
		TabmanResultSetExtractor resultSetExtractor = new TabmanResultSetExtractor();
		resultSetExtractor.setMetadataProvider(this.tabmanQuery.getMetadataProvider());
		return resultSetExtractor;
	}

	@Override
	public TabmanQuery buildDataQuery() throws QueryBuilderNotGeneratedException {
		
		return this.tabmanQuery;
		
	}

}
