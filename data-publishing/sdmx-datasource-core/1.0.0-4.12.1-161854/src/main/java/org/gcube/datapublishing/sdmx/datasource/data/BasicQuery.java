package org.gcube.datapublishing.sdmx.datasource.data;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.datapublishing.sdmx.datasource.data.beans.AttributeColumnBean;
import org.gcube.datapublishing.sdmx.datasource.data.beans.DimensionAttributeColumnBean;
import org.gcube.datapublishing.sdmx.datasource.data.beans.DimensionColumnBean;
import org.gcube.datapublishing.sdmx.datasource.data.beans.MeasureColumnBean;
import org.gcube.datapublishing.sdmx.datasource.data.beans.ObservationAttributeColumnBean;
import org.gcube.datapublishing.sdmx.datasource.data.beans.TimeDimensionColumnBean;
import org.sdmxsource.sdmx.api.constants.DATA_QUERY_DETAIL;



public abstract class BasicQuery implements SDMXMetadataProviderGenerator
{
	public enum DATA_DETAIL {
		SERIES_ATTRIBUTES ((short)0b10000),
		SERIES_GROUP ((short)0b01000),
		ANNOTATIONS ((short)0b00100),
		OBSERVATIONS ((short)0b00010),
		OBSERVATION_ATTRIBUTES ((byte)0b00001);
		
		private short maskValue;
		
		private DATA_DETAIL (short maskValue)
		{
			this.maskValue = maskValue;
		}
		

	}
	
	private enum DATA_DETAILS_TYPE {
		FULL ((short) 0b11111,DATA_QUERY_DETAIL.FULL),
		SERIES_KEYS_ONLY ((short)0b00000,DATA_QUERY_DETAIL.SERIES_KEYS_ONLY),
		DATA_ONLY ((short) 0b00010,DATA_QUERY_DETAIL.DATA_ONLY),
		NO_DATA ((short) 0b11000,DATA_QUERY_DETAIL.NO_DATA);
		
		private short detailValue;
		private DATA_QUERY_DETAIL dataQueryDetail;
		
		private DATA_DETAILS_TYPE(short detailValue,DATA_QUERY_DETAIL dataQueryDetail) {
			this.detailValue = detailValue;
			this.dataQueryDetail = dataQueryDetail;
		}
		
		private boolean check (DATA_DETAIL mask)
		{
			return (mask.maskValue & detailValue) != 0 ;
		}
		
		private static DATA_DETAILS_TYPE fromDataQueryDetail (DATA_QUERY_DETAIL dataQueryDetail)
		{
			try
			{
				for (DATA_DETAILS_TYPE response : DATA_DETAILS_TYPE.values())
				{
					if (response.dataQueryDetail == dataQueryDetail) return response;
				}
			} catch (Exception e)
			{
				
			}
		
			return DATA_DETAILS_TYPE.FULL;

		
			
		}
	}

	
	
	private String 	dataFlowId,
						dataFlowAgency,
						dataFlowVersion;
	
	private List<AttributeColumnBean> 	attributes;
								
	private List<DimensionColumnBean> 	dimensions;
	
	private TimeDimensionColumnBean 	timeDimension;
	private MeasureColumnBean 		primaryMeasure;
	
	private Date		timeIntervalMin,
						timeIntervalMax;
	
	private List<String> 	columnIds;
	private Map<String, Set<String>> parametersMap;
	
	private String observationDimension;
	
	private int 	firstNObservations,
					lastNObservations;

	private DATA_DETAILS_TYPE dataDetailType;
	
//	protected ArrayList<Object> queryParameters;
//	protected ArrayList<String> columnParameters;
//	protected ArrayList<OperationType> operations;
//	protected Set<String> columns;
//	
	public enum OperationType {
		EQUAL ("="),
		LESSER_THAN("<"),
		GREATER_THAN(">"),
		LESSER_EQUAL_THAN("<="),
		GREATER_EQUAL_THAN(">=");
		
		String name;
		
		private OperationType(String name) {
			this.name = name;
		}
		
		public String toString ()
		{
			return name;
		}
	}
	
	protected BasicQuery ()
	{
		this.attributes = new LinkedList<>();
		this.dimensions = new LinkedList<>();
		this.columnIds = new LinkedList<>();
		this.timeIntervalMin = null;
		this.timeIntervalMax = null;
		this.parametersMap = new HashMap<>();
		this.firstNObservations = -1;
		this.lastNObservations = -1;
//		this.queryParameters = new ArrayList<Object>();
//		this.columnParameters = new ArrayList<String>();
//		this.operations = new ArrayList<BasicQuery.OperationType>();
//		this.columns = new HashSet<>();
	}

	public void setDataFlow (String agency, String id, String version)
	{
		this.dataFlowAgency = agency;
		this.dataFlowId = id;
		this.dataFlowVersion = version;
	}
	
	public void setTimeInterval (Date min, Date max)
	{
		this.timeIntervalMin = min;
		this.timeIntervalMax = max;
		
	}
	
//	public void addQueryParameter (String columnName,Object parameter,OperationType operation)
//	{
//		this.queryParameters.add(parameter);
//		this.columnParameters.add(columnName);
//		this.operations.add(operation);
//	}
	
	public void addAttribute (String columnId, String columnConcept,boolean observation)
	{
		if (observation && this.checkDataDetail(DATA_DETAIL.OBSERVATION_ATTRIBUTES))
		{
			this.attributes.add(new ObservationAttributeColumnBean(columnId, columnConcept));
			this.columnIds.add(columnId);

		}
		else if (!observation && checkDataDetail(DATA_DETAIL.SERIES_ATTRIBUTES))
		{
			this.attributes.add(new DimensionAttributeColumnBean(columnId, columnConcept));
			this.columnIds.add(columnId);
	
		}
	}
	
	public void addDimension (String columnId, String columnConcept, boolean timeDimension, boolean measure)
	{
		if (timeDimension) setTimeDimension(columnId,columnConcept);
		else if (measure && (this.primaryMeasure == null || !this.primaryMeasure.getId().equals(columnConcept)))
		{
			this.dimensions.add(new MeasureColumnBean(columnId, columnConcept));
			this.columnIds.add(columnId);
		}
		else if (!measure)
		{
			this.dimensions.add(new DimensionColumnBean(columnId, columnConcept));
			this.columnIds.add(columnId);
		}
		


	}

	
	public void setDataQueryDetail(DATA_QUERY_DETAIL dataQueryDetail) {
		
		this.dataDetailType = DATA_DETAILS_TYPE.fromDataQueryDetail(dataQueryDetail);
	}

	public boolean checkDataDetail (DATA_DETAIL dataDetail)
	{
		return this.dataDetailType.check(dataDetail);
	}
	
	public void setFirstNObservations(int firstNObservations) {
		this.firstNObservations = firstNObservations;
	}

	public void setLastNObservations(int lastNObservations) {
		this.lastNObservations = lastNObservations;
	}

	public void setObservationDimension(String observationDimension) {
		this.observationDimension = observationDimension;
	}

//	public void addAttributes (List<AttributeColumnBean> attributes)
//	{
//		this.attributes.addAll(attributes);
//
//	}
//	
//	public void addDimensions (List<DimensionColumnBean> dimensions)
//	{
//
//		this.dimensions.addAll(dimensions);
//		
//		if (this.timeDimension != null) this.dimensions.remove(this.timeDimension);
//		
//		if (this.primaryMeasure != null) this.dimensions.remove(this.primaryMeasure);
//
//	}
	
	public void setTimeDimension (TimeDimensionColumnBean timeDimensionColumn)
	{
		this.timeDimension = timeDimensionColumn;
		this.columnIds.add(timeDimensionColumn.getId());
		
		if (this.dimensions.size()>0) this.dimensions.remove(this.timeDimension);
	}
	
	public void setTimeDimension (String columnId, String columnConcept)
	{
		this.setTimeDimension(new TimeDimensionColumnBean(columnId, columnConcept));
	}
	
	public void setPrimaryMeasure (MeasureColumnBean primaryMeasureColumn)
	{
		if (checkDataDetail(DATA_DETAIL.OBSERVATIONS))
		{
			this.primaryMeasure = primaryMeasureColumn;
			this.columnIds.add(primaryMeasureColumn.getId());
			
			if (this.dimensions.size()>0) this.dimensions.remove(this.primaryMeasure);
		}

	}
	
	public void setPrimaryMeasure (String columnId, String columnConcept)
	{
		this.setPrimaryMeasure(new MeasureColumnBean(columnId, columnConcept));

	}

	public void addParameter (String columnId, String value)
	{
		Set<String> values = this.parametersMap.get(columnId);
		
		if (values == null)
		{
			values = new HashSet<>();
			this.parametersMap.put(columnId, values);
		}
		
		values.add(value);
	}
	
	public void addParameters (String columnId, Set<String> values)
	{
		Set<String> valuesSet = this.parametersMap.get(columnId);
		
		if (valuesSet == null)
		{
			valuesSet = new HashSet<>();
			this.parametersMap.put(columnId, values);
		}
		
		valuesSet.addAll(values);
	}
	
	
	public List<String> getColumnIds ()
	{
		return new LinkedList<>(this.columnIds);
	}
	

	
	protected SDMXMetadataProviderBuilder getMetadataProviderBuilder ()
	{
		SDMXMetadataProviderBuilder builder = new SDMXMetadataProviderBuilder();
		builder.setDataFlowAgency(this.dataFlowAgency);
		builder.setDataFlowId(this.dataFlowId);
		builder.setDataFlowVersion(this.dataFlowVersion);
		builder.setAttributes(this.attributes);
		builder.setDimensions(this.dimensions);
		builder.setPrimaryMeasure(this.primaryMeasure);
		builder.setTimeDimension(this.timeDimension);
		builder.setColumnIds(this.columnIds);
		builder.setObservationDimensionParameter(this.observationDimension);
		builder.setFirstNObservations(this.firstNObservations);
		builder.setLastNObservations(this.lastNObservations);
		return builder;
	}
	
	@Override
	public SDMXMetadataProvider getMetadataProvider ()
	{

		return getMetadataProviderBuilder ().generate();
		
	}
	
	protected List<AttributeColumnBean> getAttributes ()
	{
		return this.attributes;
	}

	protected List<DimensionColumnBean> getDimensions() {
		return dimensions;
	}

	protected TimeDimensionColumnBean getTimeDimension() {
		return timeDimension;
	}

	protected int getFirstNObservations() {
		return firstNObservations;
	}

	protected int getLastNObservations() {
		return lastNObservations;
	}

	protected String getDataFlowId() {
		return dataFlowId;
	}

	protected String getDataFlowAgency() {
		return dataFlowAgency;
	}

	protected String getDataFlowVersion() {
		return dataFlowVersion;
	}

	protected Map<String, Set<String>> getParametersMap() {
		return parametersMap;
	}

	protected Date getTimeIntervalMin() {
		return timeIntervalMin;
	}

	protected Date getTimeIntervalMax() {
		return timeIntervalMax;
	}
	

	
	
}
