package org.gcube.accounting.aggregator.file;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.gcube.accounting.aggregator.aggregation.AggregationType;
import org.gcube.accounting.aggregator.directory.WorkSpaceDirectoryStructure;
import org.gcube.accounting.aggregator.plugin.ScopedTest;
import org.gcube.accounting.aggregator.utility.Constant;
import org.gcube.accounting.aggregator.utility.Utility;
import org.gcube.accounting.aggregator.workspace.WorkSpaceManagement;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkSpaceDirectoryStructureTest extends ScopedTest {
	
	public static Logger logger = LoggerFactory.getLogger(WorkSpaceDirectoryStructureTest.class);
	
	@Test
	public void test() throws Exception{
		
		AggregationType aggregationType = AggregationType.YEARLY;
		Date date = Utility.getAggregationStartCalendar(2015, Calendar.JANUARY, 1).getTime();
		
		WorkSpaceDirectoryStructure workSpaceDirectoryStructure = new WorkSpaceDirectoryStructure();
		String targetFolder = workSpaceDirectoryStructure.getTargetFolder(aggregationType, date);
		
		File file = new File(Constant.ROOT_DIRECTORY, "aux.txt");
		
		WorkSpaceManagement.zipAndBackupFiles(targetFolder, "Test", file);
	}
}
