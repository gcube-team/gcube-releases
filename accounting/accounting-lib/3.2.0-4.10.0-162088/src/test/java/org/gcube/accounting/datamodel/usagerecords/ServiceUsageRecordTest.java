/**
 * 
 */
package org.gcube.accounting.datamodel.usagerecords;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.accounting.datamodel.UsageRecord;
import org.gcube.accounting.datamodel.aggregation.AggregatedServiceUsageRecord;
import org.gcube.accounting.datamodel.basetypes.AbstractServiceUsageRecord;
import org.gcube.accounting.datamodel.validations.validators.CalledMethodRegexReplaceValidator;
import org.gcube.accounting.datamodel.validations.validators.RegexReplace;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.DSMapper;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.RecordUtility;
import org.gcube.documentstore.records.aggregation.AggregationScheduler;
import org.gcube.testutility.ScopedTest;
import org.gcube.testutility.TestUsageRecord;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ServiceUsageRecordTest extends ScopedTest {

	private static Logger logger = LoggerFactory.getLogger(ServiceUsageRecordTest.class);
	
	public static Set<String> getExpectedRequiredFields(){
		Set<String> expectedRequiredFields = new HashSet<String>();
		expectedRequiredFields.add(Record.ID);
		expectedRequiredFields.add(UsageRecord.CONSUMER_ID);
		expectedRequiredFields.add(UsageRecord.CREATION_TIME);
		expectedRequiredFields.add(UsageRecord.SCOPE);
		expectedRequiredFields.add(UsageRecord.OPERATION_RESULT);
		expectedRequiredFields.add(AbstractServiceUsageRecord.CALLER_HOST);
		expectedRequiredFields.add(AbstractServiceUsageRecord.HOST);
		expectedRequiredFields.add(AbstractServiceUsageRecord.SERVICE_CLASS);
		expectedRequiredFields.add(AbstractServiceUsageRecord.SERVICE_NAME);
		expectedRequiredFields.add(AbstractServiceUsageRecord.CALLED_METHOD);
		expectedRequiredFields.add(AbstractServiceUsageRecord.DURATION);
		expectedRequiredFields.add(AbstractServiceUsageRecord.CALLER_QUALIFIER);
		return expectedRequiredFields;
	}
	
	@Test(expected=InvalidValueException.class)
	public void scopeNotSetValidationError() throws InvalidValueException {
		SecurityTokenProvider.instance.reset();
		ScopeProvider.instance.reset();
		ServiceUsageRecord usageRecord = TestUsageRecord.createTestServiceUsageRecord();
		usageRecord.validate();
		logger.debug("{}", usageRecord);
	}
	
	@Test
	public void testRequiredFields() throws InvalidValueException{
		SecurityTokenProvider.instance.reset();
		ScopeProvider.instance.reset();
		ServiceUsageRecord usageRecord = TestUsageRecord.createTestServiceUsageRecord();
		Assert.assertTrue(usageRecord.getScope()==null);
		usageRecord.setScope(TestUsageRecord.TEST_SCOPE);
		
		Set<String> expectedRequiredFields = getExpectedRequiredFields();
		logger.debug("Expected Required Fields : {}", expectedRequiredFields);
		
		Set<String> gotRequiredFields = usageRecord.getRequiredFields();
		logger.debug("Got Required Fields : {}", gotRequiredFields);
		
		Assert.assertTrue(expectedRequiredFields.containsAll(gotRequiredFields));
		Assert.assertTrue(gotRequiredFields.containsAll(expectedRequiredFields));
		
		usageRecord.validate();
		logger.debug("{}", usageRecord);
	}

	@Test
	public void testMarshalling() throws Exception {
		ServiceUsageRecord usageRecord = TestUsageRecord.createTestServiceUsageRecord();
		AggregatedServiceUsageRecord aggregatedUsageRecord = (AggregatedServiceUsageRecord) AggregationScheduler.getAggregatedRecord(usageRecord);
		logger.debug(DSMapper.marshal(aggregatedUsageRecord));
	}

	@Test
	public void testListMarshalling() throws Exception {
		ServiceUsageRecord usageRecord = TestUsageRecord.createTestServiceUsageRecord();
		AggregatedServiceUsageRecord aggregatedUsageRecord = (AggregatedServiceUsageRecord) AggregationScheduler.getAggregatedRecord(usageRecord);
		List<UsageRecord> list = new ArrayList<>();
		list.add(aggregatedUsageRecord);
		logger.debug(DSMapper.marshal(list));
	}
	
	
	@Test
	public void testDeserialization() throws Exception{
		RecordUtility.addRecordPackage(ServiceUsageRecord.class.getPackage());
		RecordUtility.addRecordPackage(AggregatedServiceUsageRecord.class.getPackage());
		
		String singleUsageRecordString = "{"
				+ "\"creationTime\":1498081970156,"
				+ "\"serviceClass\":\"InformationSystem\","
				+ "\"callerHost\":\"146.48.123.84\","
				+ "\"callerQualifier\":\"TOKEN\","
				+ "\"recordType\":\"ServiceUsageRecord\","
				+ "\"consumerId\":\"luca.frosini\","
				+ "\"aggregated\":true,"
				+ "\"serviceName\":\"resource-registry\","
				+ "\"duration\":692,"
				+ "\"scope\":\"/gcube/devNext/NextNext\","
				+ "\"host\":\"resourceregistry1-d-d4s.d4science.org:80\","
				+ "\"id\":\"1ca35f57-75b7-48f4-84b1-ac3dae2f58ab\","
				+ "\"calledMethod\":\"/er/resource/957d40f4-44b5-4e7e-be7d-76bba44e751f\","
				+ "\"operationResult\":\"SUCCESS\""
			+ "}";
		UsageRecord usageRecord = DSMapper.unmarshal(UsageRecord.class, singleUsageRecordString);
		AggregatedServiceUsageRecord aggregatedServiceUsageRecord = new AggregatedServiceUsageRecord(usageRecord.getResourceProperties());
		logger.debug("{}", usageRecord);
		logger.debug("{}", aggregatedServiceUsageRecord);
		
	}
	
	@Test
	public void testRegexReplace() throws Exception{
		RecordUtility.addRecordPackage(ServiceUsageRecord.class.getPackage());
		RecordUtility.addRecordPackage(AggregatedServiceUsageRecord.class.getPackage());
		
		String usageRecordString = "{"
				+ "\"startTime\": 1507334430481,"
				+ "\"endTime\": 1507334430481,"
				+ "\"creationTime\":1507334430724,"
				+ "\"serviceClass\":\"Index\","
				+ "\"callerHost\":\"88.197.53.47\","
				+ "\"callerQualifier\":\"UNKNOWN\","
				+ "\"recordType\":\"ServiceUsageRecord\","
				+ "\"consumerId\":\"UNKNOWN\","
				+ "\"aggregated\":true,"
				+ "\"serviceName\":\"FullTextIndexNode\","
				+ "\"duration\":3,"
				+ "\"maxInvocationTime\": 58,"
				+ "\"minInvocationTime\": 1,"
				+ "\"scope\":\"/d4science.research-infrastructures.eu/gCubeApps/iSearch\","
				+ "\"host\":\"dewn10.madgik.di.uoa.gr:8080\","
				+ "\"id\":\"12a631da-1541-4771-8961-8d7e4a694bc0\","
				+ "\"calledMethod\":\"/delete/3121eccdcbf9c6e982dcc5a2164b8b4029f51d8b1c448ddaf46316178d755c6f/oai:oai.datacite.org:352019\","
				+ "\"operationResult\":\"SUCCESS\""
			+ "}";
		
		
		String replace = "/delete/COLLECTION_ID/ITEM_ID";
		RegexReplace regexReplace = new RegexReplace("Index", "FullTextIndexNode", 
				"/delete/[^\\t\\n\\r\\f\\v]+/[^\\t\\n\\r\\f\\v]+", replace);
		CalledMethodRegexReplaceValidator.addRegexReplace(regexReplace);
		
		
		UsageRecord usageRecord = DSMapper.unmarshal(AggregatedServiceUsageRecord.class, usageRecordString);
		logger.debug("{}", usageRecord);
		AggregatedServiceUsageRecord aggregatedServiceUsageRecord = new AggregatedServiceUsageRecord(usageRecord.getResourceProperties());
		logger.debug("{}", aggregatedServiceUsageRecord);
		
		Assert.assertTrue(aggregatedServiceUsageRecord.getCalledMethod().compareTo(replace)==0);
		
	}
	
}
