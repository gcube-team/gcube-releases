package gr.uoa.di.madgik.environment.accounting.record;

import java.util.Date;
import java.util.Map;

/**
 * A job usage record used for accounting purposes, that extends more general
 * execution record
 * 
 * @author jgerbe
 * 
 */
public class JobUsageRecord extends ExecutionUsageRecord {
	private static final String RESOURCETYPE = "job";

	/**
	 * Create a new job usage record
	 * 
	 * @param consumerId
	 * @param startTime
	 * @param endTime
	 * @param scope
	 * @param owner
	 * @param props
	 * @throws InvalidValueException
	 */
	public JobUsageRecord(String consumerId, Date startTime, Date endTime, String scope, String owner, Map<String, String> props) {
		super(consumerId, startTime, endTime, scope, owner, props);

		resourceType = RESOURCETYPE;
	}
}
