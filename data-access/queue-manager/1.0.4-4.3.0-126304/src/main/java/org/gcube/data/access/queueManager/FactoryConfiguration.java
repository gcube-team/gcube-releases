package org.gcube.data.access.queueManager;

public class FactoryConfiguration {

	//********************* Default Values
	
	public static final int DEFAULT_MAXIMUM_REDELIVERIES=6;
	public static final boolean DEFAULT_USE_EXPONENTIAL_BACKOFF=true;
	public static final long DEFAULT_INITIAL_REDELIVERY_DELAY=500;
	
	
	private String serviceClass;
	private String serviceName;
	private String brokerEndpoint;
	private String user;
	private String password;
	
	//***** Redelivery options
	
	
	
	
	private int maximumRedeliveries=DEFAULT_MAXIMUM_REDELIVERIES;
	private boolean useExponentialRedelivery=DEFAULT_USE_EXPONENTIAL_BACKOFF;
	private long initialRedeliveryDelay=DEFAULT_MAXIMUM_REDELIVERIES;
	
	
	public FactoryConfiguration(String serviceClass, String serviceName,
			String brokerEndpoint, String user, String password) {
		super();
		this.serviceClass = serviceClass;
		this.serviceName = serviceName;
		this.brokerEndpoint = brokerEndpoint;
		this.user = user;
		this.password = password;
	}
	/**
	 * @return the serviceClass
	 */
	public String getServiceClass() {
		return serviceClass;
	}
	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}
	/**
	 * @return the brokerEndpoint
	 */
	public String getBrokerEndpoint() {
		return brokerEndpoint;
	}
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @return the maximumRedeliveries
	 */
	public int getMaximumRedeliveries() {
		return maximumRedeliveries;
	}
	/**
	 * @param maximumRedeliveries the maximumRedeliveries to set
	 */
	public void setMaximumRedeliveries(int maximumRedeliveries) {
		this.maximumRedeliveries = maximumRedeliveries;
	}
	/**
	 * @return the useExponentialRedelivery
	 */
	public boolean isUseExponentialRedelivery() {
		return useExponentialRedelivery;
	}
	/**
	 * @param useExponentialRedelivery the useExponentialRedelivery to set
	 */
	public void setUseExponentialRedelivery(boolean useExponentialRedelivery) {
		this.useExponentialRedelivery = useExponentialRedelivery;
	}
	/**
	 * @return the initialRedeliveryDelay
	 */
	public long getInitialRedeliveryDelay() {
		return initialRedeliveryDelay;
	}
	/**
	 * @param initialRedeliveryDelay the initialRedeliveryDelay to set
	 */
	public void setInitialRedeliveryDelay(long initialRedeliveryDelay) {
		this.initialRedeliveryDelay = initialRedeliveryDelay;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FactoryConfiguration [serviceClass=");
		builder.append(serviceClass);
		builder.append(", serviceName=");
		builder.append(serviceName);
		builder.append(", brokerEndpoint=");
		builder.append(brokerEndpoint);
		builder.append(", user=");
		builder.append(user);
		builder.append(", password=");
		builder.append(password);
		builder.append(", maximumRedeliveries=");
		builder.append(maximumRedeliveries);
		builder.append(", useExponentialRedelivery=");
		builder.append(useExponentialRedelivery);
		builder.append(", initialRedeliveryDelay=");
		builder.append(initialRedeliveryDelay);
		builder.append("]");
		return builder.toString();
	}

	
	
}
