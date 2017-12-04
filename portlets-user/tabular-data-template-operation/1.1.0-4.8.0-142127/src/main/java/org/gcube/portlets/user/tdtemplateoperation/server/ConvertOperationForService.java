/**
 * 
 */
package org.gcube.portlets.user.tdtemplateoperation.server;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.AggregationPair;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.AggregationPair.AggregationFunction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.portlets.user.tdtemplateoperation.shared.AggregatePair;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdAggregateFunction;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdColumnData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ConvertOperationForService.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 19, 2014
 */
public class ConvertOperationForService {
	
	public static Logger logger = LoggerFactory.getLogger(ConvertOperationForService.class);
	
	/**
	 * Operator from td operator id.
	 *
	 * @param tdOperatorDescriptionId the td operator description id
	 * @return the operator
	 * @throws Exception the exception
	 */
	public static Operator operatorFromTdOperatorId(String tdOperatorDescriptionId) throws Exception {

		try {
			return Operator.valueOf(tdOperatorDescriptionId);

		} catch (Exception e) {
			throw new Exception("The td operator " + tdOperatorDescriptionId
					+ " is not a value of " + Operator.class);
		}
	}

	/**
	 * Period type.
	 *
	 * @param periodType the period type
	 * @return the period type
	 * @throws Exception the exception
	 */
	public static PeriodType periodType(String periodType) throws Exception {

		try {
			return PeriodType.valueOf(periodType);

		} catch (Exception e) {
			throw new Exception("The periodType " + periodType
					+ " is not a value of " + PeriodType.class);
		}
	}
	
	/**
	 * Aggregation function from id.
	 *
	 * @param aggregateFunId is the name of the enum
	 * @return the aggregation function
	 * @throws Exception the exception
	 */
	public static AggregationFunction aggregationFunctionFromId(String aggregateFunId) throws Exception{
		
		try {
			return AggregationFunction.valueOf(aggregateFunId);

		} catch (Exception e) {
			throw new Exception("The td operator " + aggregateFunId
					+ " is not a value of " + AggregationFunction.class);
		}
	}
	
	
	/**
	 * Gets the column from td column data.
	 *
	 * @param template the template
	 * @param column the column
	 * @return the column from td column data
	 * @throws Exception the exception
	 */
	public static TemplateColumn<?> getColumnFromTdColumnData(Template template, TdColumnData column) throws Exception{
		
		if(column==null)
			throw new Exception("Error, the input column is null");
		
		if(column.getServerId()==null)
			throw new Exception("Error, the server Id is null");
		
		Integer serverColumnIndex = column.getServerId().getColumnIndex();
		
		if(serverColumnIndex==null || serverColumnIndex.intValue()<0 || serverColumnIndex.intValue() > template.getActualStructure().size())
			throw new Exception("Error, the server column index "+serverColumnIndex+", is null or not valid");

		return template.getActualStructure().get(serverColumnIndex);
	}
	
	/**
	 * Builds the aggregation pair for template.
	 *
	 * @param aggregatePairs the aggregate pairs
	 * @param template the template
	 * @return the list of AggregationPair with columna as null
	 * @throws Exception the exception
	 */
	public static List<AggregationPair> aggregationPairListForTemplate(List<AggregatePair> aggregatePairs, Template template) throws Exception{
		
		if(aggregatePairs==null)
			throw new Exception("List<AggregatePair> is null");
		
		List<AggregationPair> listAggPair = new ArrayList<AggregationPair>(aggregatePairs.size());
		
		for (AggregatePair aggregatePair : aggregatePairs) {
			
			try{
				
				logger.info("Converting: "+aggregatePair);
				TdAggregateFunction aggregateFun = aggregatePair.getAggegrateFunction();
				AggregationFunction function = ConvertOperationForService.aggregationFunctionFromId(aggregateFun.getId());
				TdColumnData column = aggregatePair.getColumnData();
				
				if(column==null){
					throw new Exception("Error on converting  "+aggregatePair+", column data is null");
				}
				
				int serverColumnIndex = column.getServerId().getColumnIndex();
				logger.info("Server columnIndex is : "+serverColumnIndex);
				TemplateColumn<?> col = template.getActualStructure().get(serverColumnIndex);
				logger.trace("Retrieved server column : "+col);
				listAggPair.add(new AggregationPair(col, function));
				logger.info("Added: "+aggregatePair +" to list AggregationPair ");
				
			}catch (Exception e) {
				logger.error("Error on converting  "+aggregatePair+", skipping",e);
			}
		}
		
		return listAggPair;

	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		
		try {
//			Operator op = aggregationFunctionFromId("org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Avg");
//			
//			System.out.println(op.getLabel());
//			System.out.println(op.getSymbol());
//			System.out.println(op.getClass());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}
