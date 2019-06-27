package org.gcube.application.perform.service;

import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.gcube.application.perform.service.engine.dm.DMException;
import org.gcube.application.perform.service.engine.dm.DMUtils;
import org.gcube.data.analysis.dataminermanagercl.server.monitor.DMMonitorListener;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;

public class DMTests {

	public static void main(String[] args) throws DMException, MalformedURLException {
		Map<String,String> params=new HashMap<String,String>();
		TokenSetter.set("/gcube/preprod/preVRE");
		LocalConfiguration.init(Paths.get("src/main/webapp/WEB-INF/config.properties").toUri().toURL());
	
		
		
		ComputationId compId=DMUtils.submitJob(LocalConfiguration.getProperty(LocalConfiguration.IMPORTER_COMPUTATION_ID), params);		
		
		final Semaphore sem=new Semaphore(0);
		
		DMUtils.monitor(compId, new DMMonitorListener() {
			
			@Override
			public void running(double percentage) {
				System.out.println("RUN");
				
			}
			
			@Override
			public void failed(String message, Exception exception) {
				System.out.println("FAIL");
				sem.release();
			}
			
			@Override
			public void complete(double percentage) {
				System.out.println("DONE");
				sem.release();
			}
			
			@Override
			public void cancelled() {
				System.out.println("CANC");
				sem.release();
			}
			
			@Override
			public void accepted() {
				System.out.println("ACCEPTED");
			}
		});
		
		
		System.out.println("WAITING FOR MONITOR");
		try {
			sem.acquire();
		} catch (InterruptedException e) {		
		}
		System.out.println("WOKE UP");
	}
	
}
