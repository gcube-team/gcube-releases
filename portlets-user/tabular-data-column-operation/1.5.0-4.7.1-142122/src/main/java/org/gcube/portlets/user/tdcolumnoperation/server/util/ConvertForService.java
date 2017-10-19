/**
 * 
 */
package org.gcube.portlets.user.tdcolumnoperation.server.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.expression.composite.CompositeExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Avg;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Count;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Max;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Min;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.ST_Extent;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Sum;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Concat;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Length;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringByRegex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringPosition;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.tdcolumnoperation.server.TdTabularDataService;
import org.gcube.portlets.user.tdcolumnoperation.shared.AggregatePair;
import org.gcube.portlets.user.tdcolumnoperation.shared.TdAggregateFunction;
import org.gcube.portlets.user.tdcolumnoperation.shared.operation.TdIndexValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 19, 2014
 *
 */
public class ConvertForService {
	
	public static Logger logger = LoggerFactory.getLogger(ConvertForService.class);
	
	/**
	 * 
	 * @param tdOperatorDescriptionId
	 * @return
	 * @throws Exception
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
	 * 
	 * @param periodType
	 * @return
	 * @throws Exception
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
	 * 
	 * @param sourceRef
	 * @param regexValue
	 * @return
	 */
	public static Expression substringByRegex(ColumnReference sourceRef, String regexValue){
		return new SubstringByRegex(sourceRef, new TDText(regexValue));
	}
	
	/**
	 * 
	 * @param indexValue
	 * @param sourceRef
	 * @return
	 * @throws Exception
	 */
	public static Expression convertExpressionByTdIndexValue(TdIndexValue indexValue, ColumnReference sourceRef) throws Exception{
		
		Expression expression = null;
		
		if(indexValue.getIndex()!=TdIndexValue.INDEX_UNDEFINED_DEFAULT_VALUE){
			int value = indexValue.getIndex();
			
			if(value==TdIndexValue.INDEX_MIN_DEFAULT_VALUE){
				expression = new TDInteger(0);
			}else if(value==TdIndexValue.INDEX_MAX_DEFAULT_VALUE){
				expression = new Length(sourceRef);
			}else {
				expression = new TDInteger(value);
			}
		}else if(indexValue.getSubString()!=TdIndexValue.SUBSTRING_UNDEFINED_DEFAULT_VALUE){
			expression = new SubstringPosition(sourceRef, new TDText(indexValue.getSubString()));
		}
		
		if(expression==null){
			throw new Exception("Error on converting expression to TdIndexValue "+indexValue);
		}
		
		return expression;
	}
	
	/**
	 * 
	 * @param firstRef
	 * @param secondRef
	 * @param value
	 * @return
	 */
	public static Expression concatByReferences(ColumnReference firstRef, ColumnReference secondRef, String value){
		return new Concat(firstRef, new Concat(new TDText(value), secondRef));
	}
	
	/**
	 * 
	 * @param aggregationId
	 * @return
	 * @throws Exception
	 */
	public static Class<? extends Expression> convertAggregationFunctionForId(String aggregationId) {
		

			if(Avg.class.getName().compareTo(aggregationId)==0){
				return Avg.class;
			}else if(Count.class.getName().compareTo(aggregationId)==0){
				return Count.class;
//			}else if(First.class.getName().compareTo(aggregationId)==0){
//				return First.class;
//			}else if(Last.class.getName().compareTo(aggregationId)==0){
//				return Last.class;
			}else if(Max.class.getName().compareTo(aggregationId)==0){
				return Max.class;
			}else if(Min.class.getName().compareTo(aggregationId)==0){
				return Min.class;
			}else if(Sum.class.getName().compareTo(aggregationId)==0){
				return Sum.class;
			}else if (ST_Extent.class.getName().compareTo(aggregationId)==0){
				return ST_Extent.class;
			}
			
			return null;
	}
	
	
	public static Operator getOperatorForAggregationFunctionId(String aggregateFunId) throws Exception{
		
		Class<? extends Expression> function = convertAggregationFunctionForId(aggregateFunId);
		
		if(function==null)
			throw new Exception("The aggregationId: " + aggregateFunId + " is not a a class of " + Avg.class +" or "+Count.class +" or "+Max.class +" or "+Min.class +" or "+ST_Extent.class);
		
		Operator op = Operator.getByExpressionClass((Class<? extends CompositeExpression>) function);
		
		return op;
	}
	
	/**
	 * 
	 * @param table
	 * @param columnName
	 * @return
	 * @throws Exception
	 */
	public static ColumnReference getColumnReference(Table table, String columnName) throws Exception{
		logger.info("Get Column Reference to column name: "+columnName);
		
		if(table==null){
			logger.error("Get Column Reference , Table is null");
			throw new Exception("Table is null");
		}
		
		logger.info("Table name is: "+table.getName());
		
		Column source = null;
		try{
			
			source = table.getColumnByName(columnName);
		
		}catch (Exception e) {
			logger.error("Error on recovering column by name",e);
			throw new Exception("Error on recovering column by name");
		}
		
		return table.getColumnReference(source);
	}
	
	/**
	 * 
	 * @param trID
	 * @param service
	 * @return
	 * @throws Exception
	 */
	public static Table getTable(TRId trID, TdTabularDataService service) throws Exception{
		logger.info("Get Table - TRId is: "+trID);
		
		if(trID==null)
			throw new Exception("TRId is null");
		
		
		String tableId;
		
		if(trID.isViewTable()){
			logger.info("TRid is view table, reading reference table id");
			tableId = trID.getReferenceTargetTableId();
		}else{
			logger.info("TRid is not a view table, reading table id");
			tableId = trID.getTableId();
		}
		
		logger.info("Table Id is: "+tableId);
		
		if(tableId==null || tableId.isEmpty())
			throw new Exception("Table id is null or empty");
		
		long tableIdLong = 0;
		
		try{
			tableIdLong = Long.parseLong(tableId);
		}catch (Exception e) {
			logger.error("Error on parsing "+tableId+" as long",e);
			throw new Exception("Error on parsing "+tableId+" as long");
		}
		
		return service.getTable(new TableId(tableIdLong));
	}
	
	/**
	 * Convert List<AggregatePair> into ArrayList<Map<String,Object>> composites readable by service
	 * @param table
	 * @param aggregatePairs
	 * @return
	 * @throws Exception 
	 */
	public static List<Map<String, Object>> buildCompositesByAggregatePairs(Table table, List<AggregatePair> aggregatePairs) throws Exception{
		
		List<Map<String,Object>> composites =new ArrayList<Map<String,Object>>(aggregatePairs.size());
		
		if(aggregatePairs==null || table==null)
			throw new Exception("Table or List<AggregatePair> is null");
		
		for (AggregatePair aggregatePair : aggregatePairs) {
			
			try{
				
				logger.info("Converting: "+aggregatePair);
				TdAggregateFunction aggregateFunId = aggregatePair.getAggegrateFunction();
				ColumnData columnData = aggregatePair.getColumnData();
				
				Operator operator = getOperatorForAggregationFunctionId(aggregateFunId.getId());
				ColumnReference colRef = getColumnReference(table, columnData.getName());

				HashMap<String,Object> cmp =new HashMap<String,Object>();
				cmp.put(GroupByOperationIdentifier.FUNCTION_PARAMETER, new ImmutableLocalizedText(operator.toString()));
				cmp.put(GroupByOperationIdentifier.TO_AGGREGATE_COLUMNS, colRef);
				
				composites.add(cmp);
				logger.info("Added: "+aggregatePair +" to composite "+cmp);
				
			}catch (Exception e) {
				logger.error("Error on converting  "+aggregatePair+", skipping",e);
			}
		}
		
		return composites;

	}
	
	public static void main(String[] args) {
		
		try {
			Operator op = getOperatorForAggregationFunctionId("org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Avg");
			
			System.out.println(op.getLabel());
			System.out.println(op.getSymbol());
			System.out.println(op.getClass());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}
