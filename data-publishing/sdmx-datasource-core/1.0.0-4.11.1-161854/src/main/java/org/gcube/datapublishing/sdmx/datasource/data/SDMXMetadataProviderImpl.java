package org.gcube.datapublishing.sdmx.datasource.data;

import java.util.List;
import java.util.Map;

import org.gcube.datapublishing.sdmx.datasource.data.beans.AttributeColumnBean;
import org.gcube.datapublishing.sdmx.datasource.data.beans.DimensionColumnBean;
import org.gcube.datapublishing.sdmx.datasource.data.beans.MeasureColumnBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SDMXMetadataProviderImpl implements SDMXMetadataProvider {

	String 	dataFlowId,
			dataFlowAgency,
			dataFlowVersion;

	List<AttributeColumnBean> 	dimensionAttributes,
								observationAttributes;
				
	List<DimensionColumnBean> 	dimensions;
	
	MeasureColumnBean 			primaryMeasure;
	Map<String, Short> positions;
	
	DimensionColumnBean observationDimension;
	
	boolean timeBasedObservation;
	
	int nObservations;
	
	private Logger logger;
	
	public SDMXMetadataProviderImpl() {
		this.logger = LoggerFactory.getLogger(SDMXMetadataProviderImpl.class);
	}
	

	
	@Override
	public String getDataFlowId() 
	{
		return this.dataFlowId;
	}

	@Override
	public String getDataFlowAgency() 
	{
		return this.dataFlowAgency;
	}

	@Override
	public String getDataFlowVersion() {
		return this.dataFlowVersion;
	}

	@Override
	public List<AttributeColumnBean> getDimensionAttributes() 
	{
		return this.dimensionAttributes;
	}

	@Override
	public List<AttributeColumnBean> getObservationAttributes() {
		return this.observationAttributes;
	}

	@Override
	public List<DimensionColumnBean> getDimensions() {
		return this.dimensions;
	}



	@Override
	public MeasureColumnBean getPrimaryMeasure() {
		return this.primaryMeasure;
	}

	@Override
	public short getDataPosition(String columnId) 
	{
		this.logger.debug("Getting data position for column id "+columnId);
		Short position = this.positions.get(columnId);
		this.logger.debug("Position "+position);
		
		if (position == null) return -1;
		else return position;
	}



	@Override
	public DimensionColumnBean getObservationDimension() {
		return observationDimension;
	}



	@Override
	public int getNObservations() {

		return this.nObservations;
	}



	@Override
	public boolean timeBasedObservations() {
		return this.timeBasedObservation;
	}




}
