package org.gcube.datapublishing.sdmx.datasource.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.gcube.datapublishing.sdmx.datasource.data.beans.AttributeColumnBean;
import org.gcube.datapublishing.sdmx.datasource.data.beans.DimensionColumnBean;
import org.gcube.datapublishing.sdmx.datasource.data.beans.MeasureColumnBean;
import org.gcube.datapublishing.sdmx.datasource.data.beans.TimeDimensionColumnBean;
import org.sdmxsource.sdmx.api.model.beans.datastructure.DimensionBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SDMXMetadataProviderBuilder {

	private String dataFlowAgency;
	private String dataFlowId;
	private String dataFlowVersion;
	private String observationDimensionParameter;
	private List<AttributeColumnBean> dimensionAttributes;
	private List<AttributeColumnBean> observationAttributes;
	private List<DimensionColumnBean> dimensions;
	private TimeDimensionColumnBean 	timeDimension;
	private MeasureColumnBean 		primaryMeasure;
	private List<String> columnIds;
	private  int 	firstNObservations,
					lastNObservations;
	private Logger logger;
	
	public SDMXMetadataProviderBuilder() {
		this.logger = LoggerFactory.getLogger(SDMXMetadataProviderBuilder.class);
	}
	
	public void setDataFlowAgency(String dataFlowAgency) {
		this.dataFlowAgency = dataFlowAgency;
	}
	public void setDataFlowId(String dataFlowId) {
		this.dataFlowId = dataFlowId;
	}
	public void setDataFlowVersion(String dataFlowVersion) {
		this.dataFlowVersion = dataFlowVersion;
	}
	public void setDimensionAttributes(List<AttributeColumnBean> dimensionAttributes) {
		
		this.dimensionAttributes =  new LinkedList<>(dimensionAttributes);
	}
	public void setObservationAttributes(List<AttributeColumnBean> observationAttributes) {
		this.observationAttributes = new LinkedList<>(observationAttributes);
	}
	public void setDimensions(List<DimensionColumnBean> dimensions) {
		this.dimensions = new LinkedList<>(dimensions);
	}
	public void setTimeDimension(TimeDimensionColumnBean timeDimension) {
		this.timeDimension = timeDimension;
	}
	public void setPrimaryMeasure(MeasureColumnBean primaryMeasure) {
		this.primaryMeasure = primaryMeasure;
	}
	
	public void setObservationDimensionParameter (String observationDimensionParameter)
	{
		this.observationDimensionParameter = observationDimensionParameter;
	}
	
	
	
	public void setFirstNObservations(int firstNObservations) {
		this.firstNObservations = firstNObservations;
	}

	public void setLastNObservations(int lastNObservations) {
		this.lastNObservations = lastNObservations;
	}

	public void setAttributes (List<AttributeColumnBean> attributes)
	{
		this.dimensionAttributes =  new LinkedList<>();
		this.observationAttributes = new LinkedList<>();
		
		for (AttributeColumnBean attribute : attributes)
		{
			if (attribute.isDimension()) this.dimensionAttributes.add(attribute);
			else this.observationAttributes.add(attribute);
		}
	}
	public void setColumnIds(List<String> columnIds) {
		this.columnIds = new LinkedList<>(columnIds);
	}

	private	void parseDataPosition (SDMXMetadataProviderImpl response,List<String> columnIDs)
	{
		this.logger.debug("Parsing data positions");
		response.positions = new HashMap<>();
		
		for (short i =0; i<columnIDs.size(); i++)
		{
			this.logger.debug("Adding column id "+columnIDs.get(i)+" for position "+i);
			response.positions.put(columnIDs.get(i), i);
		}
	}
	
	public SDMXMetadataProvider generate ()
	{
		SDMXMetadataProviderImpl response = new SDMXMetadataProviderImpl();
		response.dataFlowAgency = this.dataFlowAgency;
		response.dataFlowId = this.dataFlowId;
		response.dataFlowVersion = this.dataFlowVersion;
		response.dimensionAttributes = this.dimensionAttributes;
		response.observationAttributes = this.observationAttributes;
		response.primaryMeasure = this.primaryMeasure;
		response.observationDimension = this.timeDimension;
		response.timeBasedObservation = true;
		this.logger.debug("Dimension at observation parameter "+this.observationDimensionParameter);
		
		if (this.firstNObservations != -1) response.nObservations = this.firstNObservations;
		else if (this.lastNObservations != -1) response.nObservations = this.lastNObservations;	
		
		
		if (this.observationDimensionParameter != null && !this.observationDimensionParameter.equals(DimensionBean.TIME_DIMENSION_FIXED_ID))
		{
			this.logger.debug("Looking for dimension "+this.observationDimensionParameter);
			Iterator<DimensionColumnBean> dimensions = this.dimensions.iterator();
			DimensionColumnBean found = null;
			
			while (dimensions.hasNext() && found == null)
			{
				DimensionColumnBean currentDimension = dimensions.next();
				
				if (currentDimension.getId().equals(this.observationDimensionParameter)) 
				{
					found = currentDimension;
					this.logger.debug("Dimension found");
					this.dimensions.remove(currentDimension);
					this.dimensions.add(this.timeDimension);
				}
			}
			
			if (found != null)
			{
				response.observationDimension = found;
				response.timeBasedObservation = false;
			}
		}
		
		
		response.dimensions = this.dimensions;
		parseDataPosition(response,this.columnIds);
		return response;
	}
	
	
	
	
}
