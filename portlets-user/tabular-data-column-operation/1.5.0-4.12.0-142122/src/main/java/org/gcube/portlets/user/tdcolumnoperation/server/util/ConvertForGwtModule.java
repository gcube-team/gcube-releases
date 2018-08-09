/**
 * 
 */
package org.gcube.portlets.user.tdcolumnoperation.server.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.UnaryExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.AggregationExpression;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.portlets.user.tdcolumnoperation.shared.TdAggregateFunction;
import org.gcube.portlets.user.tdcolumnoperation.shared.TdPeriodType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 9, 2014
 * 
 */
public class ConvertForGwtModule {
	
	public static Logger logger = LoggerFactory.getLogger(ConvertForGwtModule.class);
	
	public static List<TdAggregateFunction> getAggregationFunctionIds() {

		List<Class<? extends Expression>> expressions = Expression.getExpressionsByCategory(AggregationExpression.class);

//		List<UnaryExpression> unaryExpressions = (List<UnaryExpression>) (List<?>) expressions;

		List<TdAggregateFunction> aggFuncts = new ArrayList<TdAggregateFunction>(expressions.size());
		
		for (Class<? extends Expression> agg : expressions){
			TdAggregateFunction aggreFunction = new TdAggregateFunction(agg.getName(), agg.getSimpleName());
			UnaryExpression unExpression = null;
			try {
				
				unExpression = (UnaryExpression) agg.getConstructor(Expression.class).newInstance((Expression)null);
				
			} catch (Exception e) {
				logger.error("ClassCastException: ",e);
			}	
			
//			UnaryExpression unExpression = (UnaryExpression) convertInstanceOfObject(agg, UnaryExpression.class);
			
			if(unExpression!=null){
				List<String> alloweds = new ArrayList<String>();
				for (Class<? extends DataType> dt : unExpression.allowedDataTypes()) {
					logger.trace("Adding allowed type: "+dt.getSimpleName() +" to "+agg.getName());
					alloweds.add(dt.getSimpleName());
				}
			
				aggreFunction.setAllowedDataTypesForName(alloweds);
			}
//			UnaryExpression unary=(UnaryExpression) clazz.getConstructor(Expression.class).newInstance((Expression)null);	
//			if(unary.allowedDataTypes().contains(type.getClass()))
			aggFuncts.add(aggreFunction);

		}

		return aggFuncts;
		
		/*
		List<Class<? extends Expression>> expressions = Expression.getExpressionsByCategory(AggregationExpression.class);

		List<TdAggregateFunction> aggFuncts = new ArrayList<TdAggregateFunction>(expressions.size());
		
		for (Class<? extends Expression> agg : expressions) 
			aggFuncts.add(new TdAggregateFunction(agg.getName(), agg.getSimpleName()));

		return aggFuncts;
		*/
	}
	
	public static <T> T convertInstanceOfObject(Object o, Class<T> clazz) {
	    try {
	        return clazz.cast(o);
	    } catch(ClassCastException e) {
	    	logger.error("ClassCastException: ",e);
	        return null;
	    }
	}
	

	public static void main(String[] args) {
		List<TdAggregateFunction> list = getAggregationFunctionIds();
		
		for (TdAggregateFunction tdAggregateFunction : list) {
			System.out.println(tdAggregateFunction);
		}
	}

	/**
	 * 
	 * @return
	 */
	public static List<TdPeriodType> getTimeTypes() {
		List<TdPeriodType> list = new ArrayList<TdPeriodType>(PeriodType.values().length);
		
		for (PeriodType pt : PeriodType.values()) {
			list.add(getTimeType(pt));
		}
		return list;
	}
	
	/**
	 * 
	 * @return
	 */
	public static TdPeriodType getTimeType(PeriodType type) {
		return new TdPeriodType(type.name(), type.getName());
	}

	/**
	 * @param timePeriod
	 * @return 
	 */
	public static List<PeriodType> getSuperiorTimePeriod(PeriodType timePeriod) {
		
		Map<PeriodType, List<PeriodType>> hierarchicalRelation = PeriodType.getHierarchicalRelation();
		return hierarchicalRelation.get(timePeriod);
	}

}
