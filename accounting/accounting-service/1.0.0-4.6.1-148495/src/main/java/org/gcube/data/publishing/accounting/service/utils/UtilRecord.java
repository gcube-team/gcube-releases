package org.gcube.data.publishing.accounting.service.utils;

import java.util.UUID;

import org.gcube.accounting.datamodel.UsageRecord.OperationResult;
import org.gcube.accounting.datamodel.usagerecords.ServiceUsageRecord;
import org.gcube.documentstore.exception.InvalidValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilRecord {
	
	private static Logger logger = LoggerFactory.getLogger(UtilRecord.class);
	
	public final static String TEST_CONSUMER_ID = "name.surname";
	public final static String TEST_SCOPE = "/infrastructure/vo";
	public final static String TEST_SCOPE_2 = "/infrastructure/vo/vre";
	public final static OperationResult TEST_OPERATION_RESULT = OperationResult.SUCCESS;

	public final static String TEST_SERVICE_CLASS = "TestServiceClass";
	public final static String TEST_SERVICE_NAME = "TestServiceName";
	public final static String TEST_CALLED_METHOD = "TestCalledMethod";
	public final static String TEST_CALLER_QUALIFIER = "TestCallerQualifier";

	public final static String TEST_CALLER_HOST = "remotehost";
	public final static String TEST_HOST = "localhost";

	public final static String TEST_PROPERTY_NAME = "TestPropertyName";
	public final static String TEST_PROPERTY_VALUE = "TestPropertyValue";

	public final static String TEST_JOB_ID = UUID.randomUUID().toString();
	public final static String TEST_JOB_NAME = "TestJobName";
	public final static int TEST_VMS_USED = 2;
	public final static String TEST_JOB_QUALIFIER = "TestJobQualifier";
	public final static long HALF_DURATION = 10 * 60 * 1000; // 10 min

	public final static String TEST_TASK_ID = UUID.randomUUID().toString();
	public final static String TEST_NESTED_MAP = "TestNestedMap";

	public final static String TEST_PORTLET_ID = "TestPortlet";
	public final static String TEST_PORTLET_OPERATION_ID = "TestPortletOperationID";
	public final static String TEST_PORTLET_MESSAGE = "TestPortletMessage";

	private final static long MIN_DURATION = 60; // millisec
	private final static long MAX_DURATION = 1000; // millisec
	/**
	 * Create a valid #ServiceUsageRecord with scope set automatically.
	 * @return the created #ServiceUsageRecord
	 */
	public static ServiceUsageRecord createTestServiceUsageRecord() {
		ServiceUsageRecord usageRecord = new ServiceUsageRecord();
		try {
			usageRecord.setConsumerId(TEST_CONSUMER_ID);
			usageRecord.setOperationResult(TEST_OPERATION_RESULT);

			usageRecord.setCallerHost(TEST_CALLER_HOST);
			usageRecord.setHost(TEST_HOST);
			usageRecord.setCallerQualifier(TEST_CALLER_QUALIFIER);
			usageRecord.setServiceClass(TEST_SERVICE_CLASS);
			usageRecord.setServiceName(TEST_SERVICE_NAME);
			usageRecord.setCalledMethod(TEST_CALLED_METHOD);

			usageRecord.setDuration(generateRandomLong(MIN_DURATION, MAX_DURATION));

		} catch (InvalidValueException e) {
			logger.error(" ------ You SHOULD NOT SEE THIS MESSAGE. Error Creating a test Usage Record", e);
			throw new RuntimeException(e);
		}
		return usageRecord;

	}

	/**
	 * Generate A Random long in a range between min and max.
	 * This function is internally used to set random duration.  
	 * @return the generated random long
	 */
	public static long generateRandomLong(long min, long max){
		return min + (int)(Math.random() * ((max - min) + 1));
	}
}
