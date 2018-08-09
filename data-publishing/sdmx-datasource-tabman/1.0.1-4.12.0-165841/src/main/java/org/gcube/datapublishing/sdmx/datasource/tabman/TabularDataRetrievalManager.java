package org.gcube.datapublishing.sdmx.datasource.tabman;


import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.data.analysis.tabulardata.service.impl.TabularDataServiceFactory;
import org.gcube.datapublishing.sdmx.datasource.data.ResultSetExtractor;
import org.gcube.datapublishing.sdmx.datasource.data.utils.SdmxDataWriter;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.TabmanDataRetriever;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.TabmanQuery;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.TabmanResultSetExtractor;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.json.Rows;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.json.exception.SDMXDataException;
import org.sdmxsource.sdmx.api.engine.DataWriterEngine;
import org.sdmxsource.sdmx.api.engine.DataWriterEngine.FooterMessage.SEVERITY;
import org.sdmxsource.sdmx.api.manager.retrieval.data.SdmxDataRetrievalWithWriter;
import org.sdmxsource.sdmx.api.model.data.query.DataQuery;
import org.sdmxsource.sdmx.sdmxbeans.model.beans.base.TextTypeWrapperImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.data.FooterMessageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TabularDataRetrievalManager implements SdmxDataRetrievalWithWriter{
	
	private Logger logger;

	public TabularDataRetrievalManager() {
		this.logger = LoggerFactory.getLogger(this.getClass());
		
	}


	

	@Override
	public void getData(DataQuery dataQuery, DataWriterEngine dataWriter) 
	{
	
		TabularDataService service = TabularDataServiceFactory.getService();
		String dataStructureid = dataQuery.getDataStructure().getId();
		this.logger.debug("Data structure id "+dataStructureid);
		
		try
		{
			TabmanDataRetriever dataRetriever  = new TabmanDataRetriever(dataQuery,service);
			TabmanQuery query = dataRetriever.buildDataQuery();
			this.logger.debug("Query generated");
			this.logger.debug("Adding SDMX elements to rs extractor");
			this.logger.debug("Data Writer "+dataWriter.getClass());
			this.logger.debug("Data Query "+dataQuery);
			ResultSetExtractor resultSetExtractor =  dataRetriever.generateResultSetExtractor();
			Rows rows = executeQuery(service, query);
			((TabmanResultSetExtractor)resultSetExtractor).processRows(rows);
			SdmxDataWriter dataRetrieval = new SdmxDataWriter(dataQuery);
			dataRetrieval.setDataWriter(dataWriter);
			dataRetrieval.writeData(resultSetExtractor.getSeriesList());
		}
		catch (SDMXDataException e)
		{
			this.logger.debug(e.getSDMXMessage(),e);
			FooterMessageImpl noDataMessage = new FooterMessageImpl(e.getCode(), e.getSeverity(), new TextTypeWrapperImpl("en", e.getSDMXMessage(),null));
			dataWriter.close(noDataMessage);
		}
		catch (Exception e)
		{
			logger.error("Unable to get the data", e);
			FooterMessageImpl noDataMessage = new FooterMessageImpl("500", SEVERITY.ERROR, new TextTypeWrapperImpl("en", "Internal error",null));
			dataWriter.close(noDataMessage);
		}
	}
	
	

//	private TabmanDataRetriever getDataRetriever (String dataFlow)
//	{
//		logger.debug("Getting specific data retriever");
//		TabmanDataRetriever response = null;
//		
//		DataRetrieverFactory<TabmanDataRetriever> responseFactory = (DataRetrieverFactory<TabmanDataRetriever>) this.dataRetrieverFactoryMap.get(dataFlow);
//		
//		if (responseFactory == null) responseFactory = (DataRetrieverFactory<TabmanDataRetriever>) this.dataRetrieverFactoryMap.get(DEFAULT_DATA_RETRIEVER);
//		
//		if (responseFactory == null)
//		{
//			logger.warn ("No data retriever factory found: trying with default tabman data retriever implementation");
//			response = new TabmanDataRetrieverImpl();
//		}
//		else
//		{
//			response = responseFactory.generateDataRetriever(dataFlow);
//		}
//		
//		
//		return response;
//		
//	}

	private Rows executeQuery (TabularDataService service,TabmanQuery query) throws Exception
	{

		
		logger.debug("Executing query");
		String jsonResponse = service.queryAsJson(query.getTableId(), null,query.getQueryFilter(),query.getQueryOrder(),query.getRequestedColumnsFilter());
		logger.debug("Query executed");
		logger.debug("JSON response "+jsonResponse);
		Rows response = new Rows(query.getRequestedColumns());
		response.fromJson(jsonResponse.getBytes());
		logger.debug("Bean created");
		return response;
	}




}
