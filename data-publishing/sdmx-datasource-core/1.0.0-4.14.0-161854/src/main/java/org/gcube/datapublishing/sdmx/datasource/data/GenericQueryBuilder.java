package org.gcube.datapublishing.sdmx.datasource.data;

import java.util.Set;

import org.sdmxsource.sdmx.api.builder.DataQueryBuilder;
import org.sdmxsource.sdmx.api.model.beans.base.DataProviderBean;
import org.sdmxsource.sdmx.api.model.beans.datastructure.DataStructureBean;
import org.sdmxsource.sdmx.api.model.beans.datastructure.DataflowBean;
import org.sdmxsource.sdmx.api.model.data.query.DataQuery;
import org.sdmxsource.util.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class GenericQueryBuilder<T extends BasicQuery>  implements DataQueryBuilder<T> 
{
	
	private Logger logger;
	private T rawQuery;
	
//	protected class SelectionGroupStructure
//	{
//				public int 	dateFrom = -1,
//								dateTo = -1;
//	}
	

	
	public GenericQueryBuilder(T rawQuery) 
	{
		this.logger = LoggerFactory.getLogger(GenericQueryBuilder.class);
		this.rawQuery = rawQuery;
	}
	


	
	@Override
	public T buildDataQuery(DataQuery buildFrom) {
		this.logger.debug("Execute Query Request : " + buildFrom.toString());
		//2. EXTRACT AND STORE HIGH LEVEL PARAMETERS FROM QUERY
		setDataFlow(this.rawQuery,buildFrom.getDataflow());
		
		Integer lastNObs = buildFrom.getLastNObservations(); //Optional
		
		if(lastNObs != null && lastNObs>0) this.rawQuery.setLastNObservations(lastNObs);
		
		Integer firstNObs = buildFrom.getFirstNObservations();                //Optional

		if(firstNObs != null && firstNObs>0) this.rawQuery.setFirstNObservations(firstNObs);

		
		String dimensionAtObs = buildFrom.dimensionAtObservation();         //Optional
		
		if (dimensionAtObs != null && dimensionAtObs.trim().length()>0) this.rawQuery.setObservationDimension(dimensionAtObs);
		
		rawQuery.setDataQueryDetail(buildFrom.getDataQueryDetail());
		

		//Mandatory
		addDimensionAttributesColumns(this.rawQuery,buildFrom.getDataStructure());
		
		Set<DataProviderBean> dataProviders = buildFrom.getDataProvider();  //Optional
		//3. LOOP THROUGH DATA PROVIDERS ON QUERY - CAN IGNORE IF NO CONCEPT OF THIS IN DATABASE
		if(ObjectUtil.validCollection(dataProviders)) {          
			boolean hasMultipleDataProviders = buildFrom.getDataProvider().size() > 1;
			if(hasMultipleDataProviders) {
				for(DataProviderBean currentDataProvider : buildFrom.getDataProvider()) {
					//DO A FILTER ON DATA PROVIDERS e.g WHERE PROVIDER IN(IMF,ECB,FAO)
				}
			} else {
				DataProviderBean provider = (DataProviderBean)buildFrom.getDataProvider().toArray()[0];
				//DO A FILTER ON DATA PROVIDERS e.g WHERE PROVIDER=IMF
			}
		}
		
		parseSelectionGroup(this.rawQuery,buildFrom);
		
//		//4. Loop through the selection groups
//		for(DataQuerySelectionGroup selectionGroup : buildFrom.getSelectionGroups()) {
//			//5. ADD FILTER ON CODE SELECTIONS FOR DIMENSIONS THAT HAVE SELECTIONS - OF WHICH THERE MAY NOT BE ANY
//			for(DataQuerySelection selection : selectionGroup.getSelections()) {
//				String dimensionId = selection.getComponentId();
//				if(selection.hasMultipleValues()) {
//					sqlQuery.appendSql(" and "+dimensionId+" IN (");
//					//DO A FILTER ON THE VALUES SELECTED IN THIS DIMENSION  e.g. WHERE COUNTRY IN(UK,FR,NZ)
//					for(String currentSelectionValue : selection.getValues()) {
//						sqlQuery.appendSql("?, ", currentSelectionValue);
//					}
//				} else {
//					//DO A FILTER ON THE VALUE SELECTED IN THIS DIMENSION  e.g. WHERE COUNTRY=UK
//					sqlQuery.appendSql(" and "+dimensionId+" = ?", selection.getValue());
//				}
//			}
//			//6. ADD DATE FILTERS (BOTH OPTIONAL)
//			if(selectionGroup.getDateFrom() != null) {
//				
//			}
//			if(selectionGroup.getDateTo() != null) {
//				
//			}
//		}
		
		//7. OPTIONALLY THERE IS SOME ADDITIONAL DETAIL THAT MAY BE NEEDED TO FILTER THE QUERY RESULT
		switch(buildFrom.getDataQueryDetail()) {
		case DATA_ONLY :
			break;
		case FULL :
			break;
		case NO_DATA : 
			break;
		case SERIES_KEYS_ONLY :
			break;
		}
		return this.rawQuery;
	}

	protected abstract void setDataFlow (BasicQuery rawQuery,DataflowBean dataFlow);
	
	protected abstract void addDimensionAttributesColumns (BasicQuery rawQuery,DataStructureBean dsd);	
	
	protected abstract void parseSelectionGroup (BasicQuery rawQuery,DataQuery buildFrom);
	

	
}
