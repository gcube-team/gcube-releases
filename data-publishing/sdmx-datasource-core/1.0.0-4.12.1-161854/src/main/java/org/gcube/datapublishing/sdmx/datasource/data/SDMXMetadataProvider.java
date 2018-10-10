package org.gcube.datapublishing.sdmx.datasource.data;

import java.util.List;

import org.gcube.datapublishing.sdmx.datasource.data.beans.AttributeColumnBean;
import org.gcube.datapublishing.sdmx.datasource.data.beans.DimensionColumnBean;
import org.gcube.datapublishing.sdmx.datasource.data.beans.MeasureColumnBean;

public interface SDMXMetadataProvider {

	public String 	getDataFlowId ();
	
	public String 	getDataFlowAgency ();
	
	public String 	getDataFlowVersion ();

	public  List<AttributeColumnBean> getDimensionAttributes ();
	
	public  List<AttributeColumnBean> getObservationAttributes ();
	
	public  List<DimensionColumnBean> getDimensions ();
	
	public MeasureColumnBean getPrimaryMeasure ();
	
	public short getDataPosition (String columnId);
	
	public DimensionColumnBean getObservationDimension ();
	
	public int getNObservations ();

	public boolean timeBasedObservations ();
	
}
