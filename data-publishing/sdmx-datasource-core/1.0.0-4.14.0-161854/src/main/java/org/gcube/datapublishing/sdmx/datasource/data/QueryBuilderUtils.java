package org.gcube.datapublishing.sdmx.datasource.data;

import java.util.Date;
import java.util.List;

import org.sdmxsource.sdmx.api.constants.ATTRIBUTE_ATTACHMENT_LEVEL;
import org.sdmxsource.sdmx.api.model.base.SdmxDate;
import org.sdmxsource.sdmx.api.model.beans.base.ComponentBean;
import org.sdmxsource.sdmx.api.model.beans.datastructure.AttributeBean;
import org.sdmxsource.sdmx.api.model.beans.datastructure.DimensionBean;
import org.sdmxsource.sdmx.api.model.beans.datastructure.PrimaryMeasureBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryBuilderUtils {

	private static Logger logger = LoggerFactory.getLogger(QueryBuilderUtils.class);
	
	public static void setDimensions (List<DimensionBean> dimensions, BasicQuery rawQuery)
	{
		logger.debug("Adding attribute columns");
		
		for (DimensionBean dimension: dimensions)
		{
			String [] columnData = getColumnData(dimension);
			rawQuery.addDimension(columnData [0], columnData [1], dimension.isTimeDimension(), dimension.isMeasureDimension());
		}
		
		logger.debug("Operation completed");
	}
	
	public static void setAttributes (List<AttributeBean> attributes, BasicQuery rawQuery)
	{
		logger.debug("Adding attribute columns");
		
		for (AttributeBean attribute: attributes)
		{
			ATTRIBUTE_ATTACHMENT_LEVEL attachmentLevel = attribute.getAttachmentLevel();
			String [] columnData = getColumnData(attribute);
			logger.debug("Adding attribute "+columnData[0]);
			logger.debug("Attachment level "+attachmentLevel);
			rawQuery.addAttribute(columnData [0], columnData[1],((attachmentLevel ==ATTRIBUTE_ATTACHMENT_LEVEL.OBSERVATION)|| (attachmentLevel== ATTRIBUTE_ATTACHMENT_LEVEL.GROUP)));
		}
		
		logger.debug("Operation completed");
	}
	
	public static void setTimeDimension (DimensionBean timeDimensionBean, BasicQuery rawQuery)
	{
		logger.debug("Adding time dimension column");
		String [] timeDimensionColumnData = getColumnData(timeDimensionBean);	
		rawQuery.setTimeDimension(timeDimensionColumnData [0],timeDimensionColumnData[1]);
		logger.debug("Operation completed");
	}
	
	
	public static void setPrimaryMeasure (PrimaryMeasureBean primaryMeasureBean, BasicQuery rawQuery)
	{
		logger.debug("Adding primary dimension column");
		String [] primaryMeasureColumnData = getColumnData(primaryMeasureBean);	
		rawQuery.setPrimaryMeasure(primaryMeasureColumnData[0],primaryMeasureColumnData[1]);
		logger.debug("Operation completed");
	}
	

	
	private static String [] getColumnData (ComponentBean component)
	{
		String [] response = new String [2];
		response [0] = component.getId();
		String [] conceptIds = component.getConceptRef().getIdentifiableIds();
		response [1] = conceptIds [0];
		return response;
	}
	
	public static Date getDate (SdmxDate sdmxDate)
	{
		Date result = null;
		
		if (sdmxDate != null)
		{
			logger.debug("Found date from "+sdmxDate.getDateInSdmxFormat());
			logger.debug("Date in canonical format "+sdmxDate.getDate());
			result = sdmxDate.getDate();
		}
		
		return result;
	}
}
