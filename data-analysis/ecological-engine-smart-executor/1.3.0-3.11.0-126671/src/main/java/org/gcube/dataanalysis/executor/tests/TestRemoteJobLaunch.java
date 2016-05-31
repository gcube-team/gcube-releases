package org.gcube.dataanalysis.executor.tests;

import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.executor.job.management.RemoteJobManager;

public class TestRemoteJobLaunch {

	
	public static void main(String [] args) throws Exception{
		String scope = "/gcube";
		String serviceClass = "TestGP";
		String serviceName = "TestGPHome";
		String owner = "GP"; 
		String directory = "./shipping/";
		String remotedirectory = "/shipping/";
		String tempDir = "./";
		String scriptName = "execute.sh";
		int numberOfNodes = 1;
		List<String> argums = new ArrayList<String>();
		argums.add("0_178204_0_3_./");
		AnalysisLogger.setLogger("./cfg/ALog.properties");
		RemoteJobManager job = new RemoteJobManager(scope,numberOfNodes);
//		job.uploadAndExecute(serviceClass, serviceName, owner, directory, remotedirectory, tempDir, scriptName, argums);
	}
	
}
