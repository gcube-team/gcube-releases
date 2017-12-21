/**
 * 
 */
package org.gcube.portlets.user.tdtemplateoperation.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.AggregationPair.AggregationFunction;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdAggregateFunction;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdPeriodType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ConvertOperationForGwtModule.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 9, 2014
 */
public class ConvertOperationForGwtModule {
	
	public static Logger logger = LoggerFactory.getLogger(ConvertOperationForGwtModule.class);
	
	/**
	 * Gets the aggregation function ids for template.
	 *
	 * @return the aggregation function ids for template
	 */
	public static List<TdAggregateFunction> getAggregationFunctionIdsForTemplate() {

		List<TdAggregateFunction> aggFuncts = new ArrayList<TdAggregateFunction>(AggregationFunction.values().length);
		
		for (AggregationFunction af : AggregationFunction.values()) {
			aggFuncts.add(getAggegateFunction(af));
		}
		return aggFuncts;
	}
	
	/**
	 * 
	 * @param af
	 * @return
	 */
	public static TdAggregateFunction getAggegateFunction(AggregationFunction af){
		
		if(af == null)
			return null;
		
		TdAggregateFunction aggreFunction = new TdAggregateFunction(af.name(), af.name());
		List<String> alloweds = new ArrayList<String>();
		for (Class<? extends DataType> dt : af.getAllowedTypes()) {
			alloweds.add(dt.getSimpleName());
		}
		aggreFunction.setAllowedDataTypesForName(alloweds);
		
		return aggreFunction;
	}
	
	/**
	 * Convert instance of object.
	 *
	 * @param <T> the generic type
	 * @param o the o
	 * @param clazz the clazz
	 * @return the t
	 */
	public static <T> T convertInstanceOfObject(Object o, Class<T> clazz) {
	    try {
	        return clazz.cast(o);
	    } catch(ClassCastException e) {
	    	logger.error("ClassCastException: ",e);
	        return null;
	    }
	}

	/**
	 * Gets the time types.
	 *
	 * @return the time types
	 */
	public static List<TdPeriodType> getTimeTypes() {
		List<TdPeriodType> list = new ArrayList<TdPeriodType>(PeriodType.values().length);
		
		for (PeriodType pt : PeriodType.values()) {
			list.add(getTimeType(pt));
		}
		return list;
	}
	
	/**
	 * Gets the time type.
	 *
	 * @param type the type
	 * @return the time type
	 */
	public static TdPeriodType getTimeType(PeriodType type) {
		return new TdPeriodType(type.name(), type.getName());
	}

	/**
	 * Gets the superior time period.
	 *
	 * @param timePeriod the time period
	 * @return the superior time period
	 */
	public static List<PeriodType> getSuperiorTimePeriod(PeriodType timePeriod) {
		
		Map<PeriodType, List<PeriodType>> hierarchicalRelation = PeriodType.getHierarchicalRelation();
		return hierarchicalRelation.get(timePeriod);
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		logger.trace("Start..");
		List<TdAggregateFunction> list = getAggregationFunctionIdsForTemplate();
		
		for (TdAggregateFunction tdAggregateFunction : list) {
			logger.trace(tdAggregateFunction.toString());
		}
		
		logger.trace("End..");
	}

}
