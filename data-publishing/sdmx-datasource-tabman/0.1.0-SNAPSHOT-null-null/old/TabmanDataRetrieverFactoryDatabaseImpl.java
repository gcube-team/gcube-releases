package org.gcube.data.analysis.sdmx.datasource.tabman.querymanager.factory.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.sdmx.datasource.data.DataRetrieverFactory;
import org.gcube.data.analysis.sdmx.datasource.tabman.querymanager.TabmanDataRetriever;
import org.gcube.data.analysis.sdmx.datasource.tabman.querymanager.impl.TabmanDataRetrieverImpl;
import org.sdmxsource.sdmx.api.model.data.query.DataQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class TabmanDataRetrieverFactoryDatabaseImpl extends JdbcDaoSupport implements DataRetrieverFactory<TabmanDataRetriever> {

	private class ConfigurationDataRetriever implements RowCallbackHandler
	{
		
		private final String 	SERIES_KEY = "SERIES_KEY",
								OBSERVATION_TIME = "OBSERVATION_TIME",
								OBSERVATION_VALUE = "OBSERVATION_VALUE",
								TABLE_ID = "TABLE_ID",
								DIMENSIONS_MAP ="DIMENSIONS",
								ATTRIBUTES_MAP = "ATTRIBUTES";
				
		private Logger logger;
		
		protected String 	seriesKey,
							observationTime,
							observationValue;
		protected long	tableId;
		
		protected Map<String, String> 	dimensionsMap,
										attributesMap;
		
		public ConfigurationDataRetriever() {
			this.logger = LoggerFactory.getLogger(this.getClass());
		}

		@Override
		public void processRow(ResultSet rs) throws SQLException 
		{
			this.logger.debug("Configuration");
			this.observationTime = rs.getString(OBSERVATION_TIME);
			logger.debug("Observation time "+this.observationTime);
			this.seriesKey = rs.getString(SERIES_KEY);
			logger.debug("Series key "+this.seriesKey);
			this.observationValue = rs.getString(OBSERVATION_VALUE);
			logger.debug("Observation value "+this.observationValue);
			this.tableId = rs.getLong(TABLE_ID);
			logger.debug("Table ID "+this.tableId);
			this.dimensionsMap = generateColumnMap(rs.getString(DIMENSIONS_MAP));
			this.attributesMap = generateColumnMap(rs.getString(ATTRIBUTES_MAP));
		}
		
		private Map<String, String> generateColumnMap (String valueString)
		{
			this.logger.debug("Value String "+valueString);
			Map<String, String> response = new HashMap<>();
			
			if (valueString != null && valueString.trim().length()>0)
			{
				String [] elements = valueString.split(";");
				
				for (String element : elements)
				{
					logger.debug("Element "+element);
					String [] conceptColumn = element.split("=");
					
					if (conceptColumn.length == 2)
					{
						logger.debug("Concept "+conceptColumn[0]);
						logger.debug("Column "+conceptColumn[1]);
						response.put(conceptColumn[0], conceptColumn[1]);

					}
				}
			}
		
			return response;
		}
		
	}
	
	private Logger logger;
	
	private final String 	CONFIGURATION_TABLE ="TABMAN_DATA_STRUCTURE",
							KEY_NAME = "DSD_ID";
	
	public TabmanDataRetrieverFactoryDatabaseImpl() {
		this.logger = LoggerFactory.getLogger(this.getClass());
	}
	
	@Override
	public TabmanDataRetriever generateDataRetriever(DataQuery dataQuery) {
		TabmanDataRetrieverImpl tabmanDataRetriever = new TabmanDataRetrieverImpl();
		initDataRetriever(tabmanDataRetriever, dataQuery.getDataStructure().getId());
		return tabmanDataRetriever;
	}
	
	public void initDataRetriever(TabmanDataRetrieverImpl dataRetriever ,String dsdId) 
	{
		this.logger.debug("Loading configuration data");
		StringBuilder queryBuilder = new StringBuilder("select * from ");
		queryBuilder.append("\"").append(CONFIGURATION_TABLE).append("\"").append(" where ").append("\"").append(KEY_NAME).append("\"").append(" = ?");
		String query = queryBuilder.toString();
		logger.debug("Configuration query "+query);
		ConfigurationDataRetriever configurationDataRetriever = new ConfigurationDataRetriever();
		super.getJdbcTemplate().query(query,new Object [] {dsdId}, configurationDataRetriever);
		String seriesKey = configurationDataRetriever.seriesKey;
		
		if (seriesKey != null && seriesKey.trim().length()>0) dataRetriever.setSeriesKeyColumn(seriesKey);
		
		dataRetriever.setObservationTimeColumn(configurationDataRetriever.observationTime);
		dataRetriever.setObservationValueColumn(configurationDataRetriever.observationValue);
		dataRetriever.setTableId(configurationDataRetriever.tableId);
		dataRetriever.setAttributesColumnMap(configurationDataRetriever.attributesMap);
		dataRetriever.setDimensionsColumnMap(configurationDataRetriever.dimensionsMap);
		
	}


}
