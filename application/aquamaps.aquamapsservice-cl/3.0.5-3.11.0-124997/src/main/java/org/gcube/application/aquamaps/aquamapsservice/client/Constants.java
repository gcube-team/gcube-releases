package org.gcube.application.aquamaps.aquamapsservice.client;

import java.util.concurrent.TimeUnit;

public class Constants {

	/** Service name. */
	public static final String SERVICE_NAME = "AquaMapsService";

	/** Service class. */
	public static final String SERVICE_CLASS = "Application";
	
	public static final int DEFAULT_TIMEOUT= (int) TimeUnit.SECONDS.toMillis(10);

	public static final String NAMESPACE = "gcube/application/aquamaps/aquamapsservice";
	
	
	
	
	public static final long POLL_WAIT_TIME=1500;
	public static final int MAX_POLL_MULTIPLIER=10;
}
