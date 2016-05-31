package gr.uoa.di.madgik.environment.accounting.record;

import java.util.Date;
import java.util.Map;

/**
 * A task usage record used for accounting purposes, that extends more general
 * execution record
 * 
 * @author jgerbe
 * 
 */
public class TaskUsageRecord extends ExecutionUsageRecord {
	private static final String RESOURCETYPE = "task";

	/**
	 * Create a task usage record
	 * 
	 * @param consumerId
	 * @param startTime
	 * @param endTime
	 * @param scope
	 * @param owner
	 * @param props
	 * @throws InvalidValueException
	 */
	public TaskUsageRecord(String consumerId, Date startTime, Date endTime, String scope, String owner, Map<String, String> props) {
		super(consumerId, startTime, endTime, scope, owner, props);

		resourceType = RESOURCETYPE;
	}
}
