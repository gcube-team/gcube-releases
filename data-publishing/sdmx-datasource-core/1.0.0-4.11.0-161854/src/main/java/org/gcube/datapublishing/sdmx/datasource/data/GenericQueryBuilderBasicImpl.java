package org.gcube.datapublishing.sdmx.datasource.data;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sdmxsource.sdmx.api.builder.DataQueryBuilder;
import org.sdmxsource.sdmx.api.model.base.SdmxDate;
import org.sdmxsource.sdmx.api.model.beans.datastructure.DataStructureBean;
import org.sdmxsource.sdmx.api.model.beans.datastructure.DataflowBean;
import org.sdmxsource.sdmx.api.model.data.query.DataQuery;
import org.sdmxsource.sdmx.api.model.data.query.DataQuerySelection;
import org.sdmxsource.sdmx.api.model.data.query.DataQuerySelectionGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class GenericQueryBuilderBasicImpl<T extends BasicQuery>  extends GenericQueryBuilder<T> implements DataQueryBuilder<T> 
{
	
	private Logger logger;

	
//	protected class SelectionGroupStructure
//	{
//				public int 	dateFrom = -1,
//								dateTo = -1;
//	}
	

	

	
	public GenericQueryBuilderBasicImpl(T rawQuery) 
	{
		super (rawQuery);
		this.logger = LoggerFactory.getLogger(GenericQueryBuilderBasicImpl.class);
	}
	



	@Override
	protected void setDataFlow (BasicQuery rawQuery,DataflowBean dataFlow)
	{
		this.logger.debug("Adding data flow information");
		String dataFlowAgency = dataFlow.getAgencyId();
		String dataFlowId = dataFlow.getId();
		String dataFlowVersion = dataFlow.getVersion();
		this.logger.debug("Data flow agency "+dataFlowAgency);
		this.logger.debug("Data flow id "+dataFlowId);
		this.logger.debug("Data flow version "+dataFlowVersion);
		rawQuery.setDataFlow(dataFlowAgency, dataFlowId, dataFlowVersion);
	}

	@Override
	protected void addDimensionAttributesColumns (BasicQuery rawQuery,DataStructureBean dsd)
	{
		logger.debug("Getting ids of dimensions and attributes");
		logger.debug("Adding attributes list");
		QueryBuilderUtils.setAttributes (dsd.getAttributes(), rawQuery);
		logger.debug("Adding dimensions list");
		QueryBuilderUtils.setDimensions (dsd.getDimensionList().getDimensions(),rawQuery);
		logger.debug("Adding time dimension");
		QueryBuilderUtils.setTimeDimension(dsd.getTimeDimension(), rawQuery);
		logger.debug("Adding primary measure");
		QueryBuilderUtils.setPrimaryMeasure(dsd.getPrimaryMeasure(), rawQuery);

	}
	
	
	@Override
	protected void parseSelectionGroup (BasicQuery rawQuery,DataQuery buildFrom)
	{
		this.logger.debug("Loading data query selection group");
		List<DataQuerySelectionGroup> selectionGroups = buildFrom.getSelectionGroups();
		Iterator<DataQuerySelectionGroup> selectionGroupIterator = selectionGroups.iterator();
		
		
		while (selectionGroupIterator.hasNext())
		{
			DataQuerySelectionGroup selectionGroup = selectionGroupIterator.next();
			logger.debug("Getting selection group "+selectionGroup);
			SdmxDate dateFrom = selectionGroup.getDateFrom();
			SdmxDate dateTo = selectionGroup.getDateTo();
			rawQuery.setTimeInterval(QueryBuilderUtils.getDate(dateFrom), QueryBuilderUtils.getDate(dateTo));
			Set<DataQuerySelection> dataQuerySelections = selectionGroup.getSelections();
			
			if (dataQuerySelections != null && !dataQuerySelections.isEmpty())
			{
				for (DataQuerySelection selection : dataQuerySelections)
				{
					this.logger.debug("Adding selection parameters for column "+selection.getComponentId());
					rawQuery.addParameters(selection.getComponentId(), selection.getValues());
				}
			}
			
		}
		

	}
	

	
}
