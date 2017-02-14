package gr.uoa.di.madgik.environment.accounting.record;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * A usage record in order to account execution resources
 * 
 * @author jgerbe
 * 
 */
public abstract class ExecutionUsageRecord {
	private String id;
	private String consumerId;
	private Date createTime;
	private Date startTime;
	private Date endTime;
	protected String resourceType;
	private String resourceScope;
	private String resourceOwner;
	
	private Map<String, String> resourceProperties; 
	
	
	/**
	 * Initialize common raw usage record fields
	 * 
	 * @param id
	 * @param createTime
	 * @param startTime
	 * @param endTime
	 * @param scope
	 * @param owner
	 * @param consumerId
	 * @param props
	 * @throws InvalidValueException
	 */
	public ExecutionUsageRecord(String consumerId, Date startTime, Date endTime, String scope, String owner, Map<String, String> props) {
		this.id = UUID.randomUUID().toString();
		this.consumerId = consumerId;
		this.createTime = new Date();
		this.startTime = startTime;
		this.endTime = endTime;
		this.resourceScope = scope;
		this.resourceOwner = owner;
		
		this.resourceProperties = props;
	}
	
	@Override
	public String toString() {
		return "ExecutionUsageRecord [id=" + id + ", consumerId=" + consumerId + ", createTime=" + createTime + ", startTime=" + startTime + ", endTime="
				+ endTime + ", resourceType=" + resourceType + ", recourceScope=" + resourceScope + ", resourceOwner=" + resourceOwner
				+ ", resourceProperties=" + resourceProperties + "]";
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the consumerId
	 */
	public String getConsumerId() {
		return consumerId;
	}

	/**
	 * @return the createTime
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * @return the startTime
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * @return the resourceType
	 */
	public String getResourceType() {
		return resourceType;
	}

	/**
	 * @return the recourceScope
	 */
	public String getResourceScope() {
		return resourceScope;
	}

	/**
	 * @return the resourceOwner
	 */
	public String getResourceOwner() {
		return resourceOwner;
	}

	/**
	 * @return the resourceProperties
	 */
	public Map<String, String> getResourceProperties() {
		return resourceProperties;
	}
}
