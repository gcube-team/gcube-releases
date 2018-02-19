package org.gcube.data.analysis.sdmx.datasource.tabman.querymanager.factory.impl;

import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.sdmx.datasource.data.DataRetrieverFactory;
import org.gcube.data.analysis.sdmx.datasource.tabman.querymanager.TabmanDataRetriever;
import org.gcube.data.analysis.sdmx.datasource.tabman.querymanager.impl.TabmanDataRetrieverImpl;
import org.sdmxsource.sdmx.api.model.data.query.DataQuery;

public class TabmanStaticDataRetrieverFactoryImpl implements DataRetrieverFactory<TabmanDataRetriever> {

	private long tableId;
	
	private String 	observationTimeColumn,
					observationValueColumn,
					seriesKeyColumn;
	
	private Map<String, String> dimensionsColumnMap,
								attributesColumnMap;
	
	public TabmanStaticDataRetrieverFactoryImpl() {
		this.dimensionsColumnMap = new HashMap<String, String>();
		this.attributesColumnMap = new HashMap<String, String>();
	}
	
	@Override
	public TabmanDataRetriever generateDataRetriever(DataQuery dataQuery) {
		TabmanDataRetrieverImpl response = new TabmanDataRetrieverImpl();
		response.setAttributesColumnMap(attributesColumnMap);
		response.setDimensionsColumnMap(dimensionsColumnMap);
		response.setObservationTimeColumn(observationTimeColumn);
		response.setObservationValueColumn(observationValueColumn);
		response.setTableId(tableId);
		response.setSeriesKeyColumn(seriesKeyColumn);
		return response;
	}


	public void setTableId(long tableId) {
		this.tableId = tableId;
	}


	public void setObservationTimeColumn(String observationTimeColumn) {
		this.observationTimeColumn = observationTimeColumn;
	}


	public void setObservationValueColumn(String observationValueColumn) {
		this.observationValueColumn = observationValueColumn;
	}


	public void setSeriesKeyColumn(String seriesKeyColumn) {
		this.seriesKeyColumn = seriesKeyColumn;
	}


	public void setDimensionsColumnMap(Map<String, String> dimensionsColumnMap) {
		this.dimensionsColumnMap = dimensionsColumnMap;
	}


	public void setAttributesColumnMap(Map<String, String> attributesColumnMap) {
		this.attributesColumnMap = attributesColumnMap;
	}

	
	
}
