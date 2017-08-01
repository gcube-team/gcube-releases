package org.gcube.testutility;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.gcube.accounting.persistence.AccountingPersistence;
import org.gcube.accounting.persistence.AccountingPersistenceFactory;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.documentstore.records.DSMapper;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.RecordUtility;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class MyTest {

	private static final Logger logger = LoggerFactory.getLogger(MyTest.class);
	
	@Before
	public void beforeClass() throws Exception{
		SecurityTokenProvider.instance.set("36501a0d-a205-4bf1-87ad-4c7185faa0d6-98187548");
		ScopeProvider.instance.set("/gcube/devNext");

	}
	
	@Test
	public void testFull() throws Exception{

		
		AccountingPersistence apq= AccountingPersistenceFactory.getPersistence();
		
		//Record record=TestUsageRecord.createTestServiceUsageRecord();//ok verificato
		//Record record=TestUsageRecord.createTestStorageUsageRecord();//ok verificato		
		//Record record=TestUsageRecord.createTestStorageVolumeUsageRecord();//ok verificato 
		//Record record=TestUsageRecord.createTestPortletUsageRecord();//ok verificato		
		Record record=TestUsageRecord.createTestTaskUsageRecord();//ok verificato
		//Record record=TestUsageRecord.createTestJobUsageRecord();//ok
		logger.debug("----record:{}",record);
		apq.account(record);
		Thread.sleep(1500);
		apq.flush(1500, TimeUnit.MILLISECONDS);
		logger.debug("end flush");
		File elaborationFile = new File("/home/pieve/_gcube_devNext.fallback.log");
		
		try(BufferedReader br = new BufferedReader(new FileReader(elaborationFile))) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	try {
		    		//Record r =DSMapper.unmarshal(Record.class, line);
		    		
		    		Record r = RecordUtility.getRecord( line);	
		    		logger.debug("--record:{}", r);
		    		
		    	} catch(Exception e){
		    		logger.error("Was not possible parse line {} to obtain a valid Record. Going to writing back this line as string fallback file.", line, e);
		    		
		    	}
		    }
		} catch (FileNotFoundException e) {
			logger.error("File non trovato", e);
		} catch (IOException ei) {
			logger.error("IOException", ei);
		}
		
		
	}
	
	
	
	@Test
	public void testSingle() throws Exception{

		Record record=TestUsageRecord.createTestServiceUsageRecord();
		logger.debug("----init record:{}",record.toString());
		String test=DSMapper.marshal(record);
		logger.debug("----marshal single record:{}",test);
	
		Record r =DSMapper.unmarshal(Record.class, test);
		logger.debug("----unmarshal single record:{}",r.toString());
		
		
		
	}
	
	
	
	
}
