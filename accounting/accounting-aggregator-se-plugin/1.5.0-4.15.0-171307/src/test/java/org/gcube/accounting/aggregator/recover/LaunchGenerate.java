package org.gcube.accounting.aggregator.recover;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* 
 * This is used in case of multiple concurrent run launched accidentally 
 * which generated duplicated entries in original.json file and 
 * unpredictable records in aggregated
 * To recover the situation. the file must be copied in src/test/resources
 */
public class LaunchGenerate {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(LaunchGenerate.class);
	
	//@Test
	public void run() throws Exception {
		File src = new File("src");
		File test = new File(src, "test");
		File resources = new File(test, "resources");
		File year = new File(resources, "2017");
		
		List<File> files = new ArrayList<>();
		
		File month07 = new File(year, "07");
		File file26 = new File(month07, "26-ServiceUsageRecord.original.json.bad");
		Assert.assertTrue(file26.exists());
		files.add(file26);
		
		for(File file : files){
			RecoverOriginalRecords g = new RecoverOriginalRecords(file);
			g.elaborate();
		}
		
	}
	
}
