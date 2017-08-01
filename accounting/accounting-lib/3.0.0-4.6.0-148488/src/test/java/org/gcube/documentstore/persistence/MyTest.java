package org.gcube.documentstore.persistence;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.gcube.accounting.datamodel.BasicUsageRecord;
import org.gcube.accounting.datamodel.UsageRecord;
import org.gcube.accounting.datamodel.basetypes.AbstractServiceUsageRecord;
import org.gcube.accounting.datamodel.usagerecords.ServiceUsageRecord;
import org.gcube.documentstore.records.DSMapper;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.RecordUtility;
import org.gcube.documentstore.records.implementation.AbstractRecord;
import org.gcube.testutility.ScopedTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyTest extends ScopedTest {
	
	private static Logger logger = LoggerFactory.getLogger(MyTest.class);
	
	@Test
	public void test() throws Exception{
		File f = new File("test.log");
		
		DSMapper.registerSubtypes(Record.class);
		DSMapper.registerSubtypes(AbstractRecord.class);
		DSMapper.registerSubtypes(UsageRecord.class);
		DSMapper.registerSubtypes(BasicUsageRecord.class);
		DSMapper.registerSubtypes(AbstractServiceUsageRecord.class);
		DSMapper.registerSubtypes(ServiceUsageRecord.class);
		
		/*
		Record record = TestUsageRecord.createTestServiceUsageRecord();
		
		FallbackPersistenceBackend fallbackPersistenceBackend = new FallbackPersistenceBackend(f);
		
		fallbackPersistenceBackend.reallyAccount(record);
		*/
		/*
		try(BufferedReader br = new BufferedReader(new FileReader(f))) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	try {
		    		Record record = RecordUtility.getRecord(ServiceUsageRecord.class, line);
		    		logger.debug(record.toString());
		    	} catch(Exception e){
		    		logger.error("Was not possible parse line {} to obtain a valid Record. Going to writing back this line as string fallback file.", line, e);
		    		
		    	}
		    }
		} catch (FileNotFoundException e) {
			logger.error("File non trovato", e);
		} catch (IOException e) {
			logger.error("IOException", e);
		}
		*/
	}
	
	
}
