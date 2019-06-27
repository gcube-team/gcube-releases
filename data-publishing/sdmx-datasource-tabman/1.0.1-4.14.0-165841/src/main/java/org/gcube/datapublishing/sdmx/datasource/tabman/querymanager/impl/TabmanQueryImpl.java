package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.data.analysis.sdmx.DataInformationProvider;
import org.gcube.data.analysis.sdmx.model.TableIdentificators;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.Equals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.GreaterOrEquals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.LessOrEquals;
import org.gcube.data.analysis.tabulardata.expression.logical.And;
import org.gcube.data.analysis.tabulardata.expression.logical.Or;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryFilter;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryOrder;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryOrderDirection;
import org.gcube.data.analysis.tabulardata.query.parameters.select.QueryColumn;
import org.gcube.data.analysis.tabulardata.query.parameters.select.QuerySelect;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;
import org.gcube.datapublishing.sdmx.datasource.data.BasicQuery;
import org.gcube.datapublishing.sdmx.datasource.data.SDMXMetadataProvider;
import org.gcube.datapublishing.sdmx.datasource.data.SDMXMetadataProviderBuilder;
import org.gcube.datapublishing.sdmx.datasource.data.SDMXMetadataProviderGenerator;
import org.gcube.datapublishing.sdmx.datasource.data.beans.ColumnBean;
import org.gcube.datapublishing.sdmx.datasource.tabman.config.TokenBasedDatasourceConfigurationManager;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.exceptions.InvalidInformationSystemDataException;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.data.DataFactoryMap;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.data.DateConverterMap;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.json.exception.InvalidFilterParameterException;
import org.sdmxsource.sdmx.api.model.beans.datastructure.DimensionBean;
import org.sdmxsource.sdmx.api.model.beans.datastructure.PrimaryMeasureBean;
import org.sdmxsource.sdmx.api.model.format.DataQueryFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;


@Configurable("tabmanQuery")
public class TabmanQueryImpl extends BasicQuery implements DataQueryFormat<TabmanQueryBuilder>, TabmanQuery, SDMXMetadataProviderGenerator
{
	private Logger logger;
	private QueryFilter tabmanQueryfilter;
	private QuerySelect tabmanRequestedColumnsFilter;
	private TableId tabmanTableId;
	private TableIdentificators tableIdentificators;
	private List<String> tabmanRequestedColumnIds;
	private Map<String, Column> tabmanColumnsMap;
	//private final HashMap<String, DataFactory> dataFactoryMap;

	private DataFactoryMap dataFactoryMap;
	private DateConverterMap dateConverterMap;
	private QueryOrder queryOrder;
	private TokenBasedDatasourceConfigurationManager configurationManager;
	
	
	public TabmanQueryImpl() {
		super ();
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.tabmanQueryfilter = null;
		this.tabmanRequestedColumnsFilter = null;
//		this.dataFactoryMap = new HashMap<>();
//		this.dataFactoryMap.put("Date", new DateDataFactory());
//		this.dataFactoryMap.put("Integer", new IntegerDataFactory());
	}


	public void initQuery (TabularDataService service, boolean lastTable) throws NoSuchTabularResourceException, NoSuchTableException, InvalidInformationSystemDataException, InvalidFilterParameterException 
	{
		
		if (this.tableIdentificators == null)
		{
			this.logger.error("Tabular resource not found for the data flow");
			throw new InvalidInformationSystemDataException("Tabular resource not found for the data flow");

		}

		else
		{
			Table table = null;
			
			
			if (lastTable)
			{
				table = service.getLastTable(new TabularResourceId(this.tableIdentificators.getTabularResourceID()));
				
				if (table == null)
				{
					this.logger.error("Tablular resource not found "+this.tableIdentificators.getTabularResourceIDString());
					throw new NoSuchTabularResourceException(this.tableIdentificators.getTabularResourceID());
				}
			}
			else
			{
				table = service.getTable(new TableId(this.tableIdentificators.getTableID()));
				
				if (table == null)
				{
					this.logger.error("Table not found "+this.tableIdentificators.getTableIDString());
					throw new NoSuchTableException(new TableId(this.tableIdentificators.getTableID()));
				}
			}
			
			this.tabmanTableId = table.getId();
			this.logger.debug("Table  found: id "+this.tabmanTableId.getValue());
			this.logger.debug("Defining requested columns according to SDMX table columns");
			defineRequestedColumns(table.getColumns());
			this.logger.debug("Generating query filter");
			buildTabmanQueryFilter();
			buildQueryOrder();
		}

		
		logger.debug("Query generated");
	}
	
	private void buildQueryOrder ()
	{
		this.logger.debug("Looking for the max N results");
		String tabmandimensionColumnId = DataInformationProvider.getInstance().getColumnConverter().registry2Local(this.getTimeDimension().getId());
		
		
		
		if (this.getLastNObservations()>0)
		{
			this.logger.debug("Last n observation "+this.getLastNObservations()+" setting descending order");
			this.queryOrder = new QueryOrder(new ColumnLocalId(tabmandimensionColumnId), QueryOrderDirection.DESCENDING);
			
		}
		else
		{
			this.logger.debug("First n observation "+this.getFirstNObservations()+ " setting the default ascending order");
			this.queryOrder = new QueryOrder(new ColumnLocalId(tabmandimensionColumnId), QueryOrderDirection.ASCENDING);
			
		}
	
	}
	
	
	public void setDataFactoryMap(DataFactoryMap dataFactoryMap) {
		this.dataFactoryMap = dataFactoryMap;
	}

	

	public void setDateConverterMap(DateConverterMap dateConverterMap) {
		this.dateConverterMap = dateConverterMap;
	}


	@Override
	public void setDataFlow(String agency, String id, String version) {
		super.setDataFlow(agency, id, version);
		this.logger.debug("Loading table identificators from the Information System");
		this.tableIdentificators = getTableIdentificators(this.getDataFlowAgency(), this.getDataFlowId(), this.getDataFlowVersion());

	}
	

	private TableIdentificators getTableIdentificators (String dataFlowAgency, String dataFlowId, String dataFlowVersion)
	{
		this.logger.debug("Getting table identificators for agency "+dataFlowAgency+ " data flow "+dataFlowId+ " version "+dataFlowVersion);
		String dataFlowKey = DataInformationProvider.getDataFlowKey(dataFlowAgency, dataFlowId, dataFlowVersion);
		this.logger.debug("Data flow key "+dataFlowKey);
		TableIdentificators identificators = DataInformationProvider.getInstance().getTableId(this.configurationManager.getName(), dataFlowKey);
		this.logger.debug("Operation completed with result "+identificators);
		return identificators;
	}
	
	private void defineRequestedColumns (List<Column> tableColumns)
	{
		logger.debug("Generating the list of requested columns");
		this.tabmanRequestedColumnIds = new LinkedList<>();
		this.tabmanColumnsMap = new HashMap<>();
		List<QueryColumn> tabmanQueryColumns = new ArrayList<>();
		List<String> registryColumnIds = getColumnIds();
		
		
		for (Column column : tableColumns)
		{
			ColumnLocalId tabmanLocalId = column.getLocalId();
			String tabmanLocalIdString = tabmanLocalId.getValue();
			String registryColumnIdString = DataInformationProvider.getInstance().getColumnConverter().local2Registry(tabmanLocalIdString);
			logger.debug("Registry column id "+registryColumnIdString);
			
			if (registryColumnIds.remove(registryColumnIdString))
			{
				logger.debug("Element found");
				tabmanQueryColumns.add(new QueryColumn (tabmanLocalId));
				this.tabmanRequestedColumnIds.add(tabmanLocalIdString);
				this.tabmanColumnsMap.put(tabmanLocalIdString, column);
			}
			else logger.warn("Element "+registryColumnIdString+" not found on the registry: extra column?");
			
		}
		
		if (!registryColumnIds.isEmpty())
		{
			this.logger.warn("At least one column on the registry has not a corresponding data column!");
			this.logger.debug("Removing all existing extra dimension/attributes from the query");
			this.logger.debug(registryColumnIds.toString());
			
			for (String toBeRemovedRegistryId : registryColumnIds)
			{
				this.logger.debug("Id to be removed "+toBeRemovedRegistryId);
				
				if (!removeRegistryColumn(this.getAttributes(), toBeRemovedRegistryId))
				{
					this.logger.debug("column not found among attributes");
					
					if (!removeRegistryColumn(this.getDimensions(), toBeRemovedRegistryId))
					{
						this.logger.warn("Column not found among dimensions: possible problems");
					}
					else this.logger.debug("Dimension column removed");
				}
				else this.logger.debug("Attribute column removed");
			}
			
		}
		
		if (tabmanQueryColumns.size() > 0) 
		{
			logger.debug("Generating query select filter");
			this.tabmanRequestedColumnsFilter = new QuerySelect(tabmanQueryColumns);
		}
		else logger.debug("No query select filter generated");
		
	}
	

	private boolean removeRegistryColumn (List<? extends ColumnBean> columns, String id)
	{
		boolean removed = false;
		Iterator<? extends ColumnBean> columnsIterator = columns.iterator();
		
		while (columnsIterator.hasNext() && ! removed)
		{
			ColumnBean column = columnsIterator.next();
			
			if (column.getId().equals(id))
			{
				columns.remove(column);
				removed = true;
			}
		}
		
		return removed;
	}
	
	
	private Expression buildSingleTabmanColumnExpression (String tabmanColumnId, Set<String> values) throws InvalidFilterParameterException
	{
		if (values.size() == 1)
		{
			this.logger.debug("Generating single value expression");
			return generateEqualExpression(tabmanColumnId, values.iterator().next());
		}
		else if (values.size()>1)
		{
			this.logger.debug("Generating or expression");
			List<Expression> equalsList = new LinkedList<>();
			Iterator<String> valuesIterator = values.iterator();
			
			while (valuesIterator.hasNext())
			{
				equalsList.add(generateEqualExpression(tabmanColumnId, valuesIterator.next()));
			}
			return new Or (equalsList);
		}
		else return null;
		
	}
	
	private void buildTabmanQueryFilter () throws InvalidFilterParameterException
	{
		this.logger.debug("Generating query filter");
		List<Expression> andExpressionsList = new ArrayList<>();
		
		
		if (!this.getParametersMap().isEmpty())
		{

			Iterator<String> registryColumnIdsIterator = this.getParametersMap().keySet().iterator();
			
			while (registryColumnIdsIterator.hasNext())
			{
				String registryColumnId = registryColumnIdsIterator.next();
				this.logger.debug("Registry query filter column id "+registryColumnId);
				Set<String> values = this.getParametersMap().get(registryColumnId);
					String tabmanColumnId = DataInformationProvider.getInstance().getColumnConverter().registry2Local(registryColumnId);
				this.logger.debug("Tabman filter column id "+tabmanColumnId);
				andExpressionsList.add(buildSingleTabmanColumnExpression(tabmanColumnId, values));
			}
		}
		
		String tabmanTimedimensionId = DataInformationProvider.getInstance().getColumnConverter().registry2Local(this.getTimeDimension().getId ());
		this.logger.debug("Tabman time dimension column id "+tabmanTimedimensionId);
		ColumnReference leftArgument = new ColumnReference(this.tabmanTableId, new ColumnLocalId(tabmanTimedimensionId));
		parseTimeIntervalMaxFilter(andExpressionsList, this.getTimeIntervalMax(), leftArgument, tabmanTimedimensionId);
		parseTimeIntervalMinFilter(andExpressionsList, this.getTimeIntervalMin(), leftArgument, tabmanTimedimensionId);

		if (andExpressionsList.size()==1) this.tabmanQueryfilter = new QueryFilter(andExpressionsList.get(0));
		else if (andExpressionsList.size() >1) this.tabmanQueryfilter= new QueryFilter(new And(andExpressionsList));
		
		this.logger.debug("Query filter built");
		
	}
	
	
	private TDTypeValue getDateTDValue (String tabmanTimeDimensionColumnId, Date date) throws InvalidFilterParameterException
	{
		Column tabmanTimeDimensionColumn = this.tabmanColumnsMap.get(tabmanTimeDimensionColumnId);
		//return this.dataFactoryMap.get(tabmanTimeDimensionColumn.getDataType().getName()).getTypeValue(date, tabmanTimeDimensionColumn);
		DataType timedimensionDataType = tabmanTimeDimensionColumn.getDataType();
		Object dateObject = this.dateConverterMap.getDateConverter(timedimensionDataType).convertDate(date, tabmanTimeDimensionColumn);
		this.logger.debug("Date object type "+dateObject.getClass().toString());
		return this.dataFactoryMap.getDataFactory(timedimensionDataType).getTypeValue(dateObject, tabmanTimeDimensionColumn);
	}
	
	private void parseTimeIntervalMinFilter (List<Expression> expressionList, Date timeIntervalMin, ColumnReference leftArgument, String tabmanTimeDimensionColumnId) throws InvalidFilterParameterException
	{
		if (timeIntervalMin != null)
		{
			this.logger.debug("Parsing min time filter");
			expressionList.add(new GreaterOrEquals(leftArgument, getDateTDValue (tabmanTimeDimensionColumnId,timeIntervalMin)));
		}
	}
	
	private void parseTimeIntervalMaxFilter (List<Expression> expressionList, Date timeIntervalMax, ColumnReference leftArgument, String tabmanTimeDimensionColumnId) throws InvalidFilterParameterException
	{
		if (timeIntervalMax != null)
		{
			this.logger.debug("Parsing max time filter");
			expressionList.add(new LessOrEquals(leftArgument, getDateTDValue (tabmanTimeDimensionColumnId,timeIntervalMax)));
		}
	}
	

	

	
	private Expression generateEqualExpression (String tabmanColumnId, String queryParameter) throws InvalidFilterParameterException
	{
		Column tabmanColumn = this.tabmanColumnsMap.get(tabmanColumnId);
		logger.debug("Generating expression for "+tabmanColumnId+ " with parameter "+queryParameter);
		ColumnReference leftArgument = new ColumnReference(this.tabmanTableId, new ColumnLocalId(tabmanColumnId));
		//TDTypeValue rightArgument = new TDText((String) queryParameter);
		TDTypeValue rightArgument = this.dataFactoryMap.getDataFactory(tabmanColumn.getDataType()).getTypeValue(queryParameter, tabmanColumn);
		return new Equals(leftArgument, rightArgument);
	}
	
//	private Expression generateNotEqualExpression (String columnId, long queryParameter, boolean greaterOrEqual)
//	{
//		logger.debug("Generating expression for "+columnId+ " with parameter "+queryParameter+" greater or equal flag "+greaterOrEqual);
//		ColumnReference leftArgument = new ColumnReference(this.tableId, new ColumnLocalId(columnId));
//		TDDate
//		TDTypeValue rightArgument = new TDInteger(queryParameter);
//		return greaterOrEqual ? new GreaterOrEquals(leftArgument, rightArgument): new LessOrEquals (leftArgument, rightArgument); 
//	}
	

	
//	private TDTypeValue generateValue (Object queryParameter)
//	{
//		TDTypeValue response = null;
//		
//		if (queryParameter instanceof String)
//		{
//			logger.debug("Query parameter string format");
//			response = new TDText((String) queryParameter);
//		}
//		else if (queryParameter instanceof Integer)
//		{
//			logger.debug("Query parameter integer format");
//			response = new TDInteger((Integer) queryParameter);
//		}
//		else if (queryParameter instanceof Number)
//		{
//			logger.debug("Query parameter integer format");
//			response = new TDNumeric((Double) queryParameter);
//		}
//		else if (queryParameter instanceof Date)
//		{
//			logger.debug("Query parameter integer format");
//			response = new TDDate((Date) queryParameter);
//		}
//		
//		return response;
//	}

	@Override
	public void addParameters (String columnId, Set<String> values)
	{
		
		if (columnId.equals(DimensionBean.TIME_DIMENSION_FIXED_ID)) columnId = this.tableIdentificators.getTimeDimension();
		else if (columnId.equals(PrimaryMeasureBean.FIXED_ID)) columnId = this.tableIdentificators.getPrimaryMeasure();
		
		super.addParameters(columnId, values);
	}

	@Override
	public void setTimeDimension(String columnId, String columnConcept) {
	
		this.logger.debug("Setting time dimension: modifying SDMX column ID "+columnId);
		String timeDimensionId = this.tableIdentificators.getTimeDimension();
		this.logger.debug("Time dimension id "+timeDimensionId);
	
		super.setTimeDimension(DataInformationProvider.getInstance().getColumnConverter().local2Registry(timeDimensionId), columnConcept);
	}

	@Override
	public void setPrimaryMeasure(String columnId, String columnConcept) {
		this.logger.debug("Setting primary measure: modifying SDMX column ID "+columnId);
		String primaryMeasureId = this.tableIdentificators.getPrimaryMeasure();
		this.logger.debug("Primary measure id "+primaryMeasureId);
		super.setPrimaryMeasure(DataInformationProvider.getInstance().getColumnConverter().local2Registry(primaryMeasureId), columnConcept);
	}


	
	
	@Override
	public QueryFilter getQueryFilter() {
		return this.tabmanQueryfilter;
	}


	@Override
	public QuerySelect getRequestedColumnsFilter() {
		return this.tabmanRequestedColumnsFilter;
	}



	@Override
	public TableId getTableId ()
	{
		return this.tabmanTableId;
	}


	@Override
	public List<String> getRequestedColumns() 
	{
		return this.tabmanRequestedColumnIds;
	}
	

	
	@Override
	public SDMXMetadataProvider getMetadataProvider ()
	{
		SDMXMetadataProviderBuilder builder =  getMetadataProviderBuilder ();

		List<String> registryRequestedColumnIds = new LinkedList<>();
		
		for (String tabmanColumnID : this.tabmanRequestedColumnIds)
		{
			registryRequestedColumnIds.add(DataInformationProvider.getInstance().getColumnConverter().local2Registry(tabmanColumnID));
		}
		
		builder.setColumnIds(registryRequestedColumnIds);

		return builder.generate();
	}


	@Override
	public QueryOrder getQueryOrder() 
	{
		return this.queryOrder;
	}


	public void setConfigurationManager(TokenBasedDatasourceConfigurationManager configurationManager) {
		this.configurationManager = configurationManager;
	}


	

	
}
