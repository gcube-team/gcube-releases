/**
 * 
 */
package org.gcube.documentstore.records.aggregation;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.gcube.documentstore.exception.NotAggregatableRecordsExceptions;
import org.gcube.documentstore.records.AggregatedRecord;
import org.gcube.documentstore.records.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class AggregationUtility<T extends AggregatedRecord<T,?>> {
	
	private static final Logger logger = LoggerFactory.getLogger(AggregationUtility.class);
	
	protected T t;
	protected Set<String> aggregationFields;
	protected Set<String> neededFields;
	
	
	protected void setDefaultAggregationFields(){
		this.aggregationFields = new HashSet<String>(t.getRequiredFields());
		this.aggregationFields.removeAll(t.getAggregatedFields());
		this.aggregationFields.remove(Record.ID);
		this.aggregationFields.remove(Record.CREATION_TIME);
		this.aggregationFields.remove(AggregatedRecord.OPERATION_COUNT);
		this.aggregationFields.remove(AggregatedRecord.AGGREGATED);
		this.aggregationFields.remove(AggregatedRecord.START_TIME);
		this.aggregationFields.remove(AggregatedRecord.END_TIME);
	}
	
	protected void setDefaultNeededFields(){
		this.neededFields = new HashSet<String>(t.getRequiredFields());
		this.neededFields.addAll(t.getAggregatedFields());
		this.neededFields.add(AggregatedRecord.OPERATION_COUNT);
		this.neededFields.add(AggregatedRecord.AGGREGATED);
		this.neededFields.add(AggregatedRecord.START_TIME);
		this.neededFields.add(AggregatedRecord.END_TIME);
	}
	
	public AggregationUtility(T t){
		this.t = t;
		setDefaultAggregationFields();
		setDefaultNeededFields();
	}
	
	/**
	 * This function is used to set the Set of Aggregation Fields.
	 * By default this Set if composed by Required Fields for lossless
	 * aggregation. If you want perform lossy aggregation set this Set
	 * consistently with NeededFields using {@link #setNeededFields}
	 * @param aggregationFields
	 */
	public void setAggregationFields(Set<String> aggregationFields){
		this.aggregationFields = aggregationFields;
	}
	
	/**
	 * This function is used to set the Set of Needed Fields to keep after 
	 * aggregation. All other fields are removed.
	 * By default this Set if composed by Required Fields and AggregationField 
	 * for lossless aggregation. If you want perform lossy aggregation set 
	 * this Set consistently with AggregationFields using 
	 * {@link #setAggregationFields}
	 * @param neededFields
	 */
	public void setNeededFields(Set<String> neededFields){
		this.neededFields = neededFields;
	}
	
	/**
	 * Check if the record provided as argument is aggregable with the one 
	 * provided to the Constructor.
	 * This is done comparing the value of each AggregationFields
	 * @param record to check
	 * @return true if the record provided as argument is aggregable with the 
	 * one provided to the Constructor. False otherwise.
	 */
	@SuppressWarnings("unchecked")
	public boolean isAggregable(T record) {
		for(String field : aggregationFields){
			Serializable recordValue = record.getResourceProperty(field);
			Serializable thisValue = t.getResourceProperty(field);
			if(recordValue instanceof Comparable && thisValue instanceof Comparable){
				@SuppressWarnings("rawtypes")
				Comparable recordValueComparable = (Comparable) recordValue;
				@SuppressWarnings("rawtypes")
				Comparable thisValueComparable = (Comparable) thisValue;
				if(recordValueComparable.compareTo(thisValueComparable)!=0){
					logger.trace("{} != {}", recordValueComparable, thisValueComparable);
					return false;
				}
			}else{
				if(recordValue.hashCode()!=this.hashCode()){
					logger.trace("{} != {}", recordValue, thisValue);
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Remove all fields which are not in AggregationFields nor in
	 * AggregatedFields Sets
	 */
	protected void cleanExtraFields(){
		Set<String> propertyKeys = t.getResourceProperties().keySet();
		for(String propertyName : propertyKeys){
			if(!neededFields.contains(propertyName)){
				t.getResourceProperties().remove(propertyName);
			}
		}
	}
	
	public synchronized T aggregate(T record) throws NotAggregatableRecordsExceptions {
		try{
			if(!isAggregable(record)){
				throw new NotAggregatableRecordsExceptions("The Record provided as argument has different values for field wich must be common to be aggregatable");
			}
			
			Calendar recordStartTime = record.getStartTime();
			Calendar actualStartTime = t.getStartTime();
			if(recordStartTime.before(actualStartTime)){
				t.setStartTime(recordStartTime);
			}
			
			Calendar recordEndTime = record.getEndTime();
			Calendar actualEndTime = t.getEndTime();
			if(recordEndTime.after(actualEndTime)){
				t.setEndTime(recordEndTime);
			}
			
			Calendar newCreationTime = Calendar.getInstance();
			t.setCreationTime(newCreationTime);
			
			t.setOperationCount(t.getOperationCount() + record.getOperationCount());
			
			cleanExtraFields();
			
			return t;
		}catch(NotAggregatableRecordsExceptions e){
			throw e;
		}catch(Exception ex){
			throw new NotAggregatableRecordsExceptions(ex.getCause());
		}
	}

}
