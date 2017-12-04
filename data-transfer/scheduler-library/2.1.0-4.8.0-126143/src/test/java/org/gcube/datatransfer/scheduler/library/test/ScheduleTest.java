package org.gcube.datatransfer.scheduler.library.test;

import static org.gcube.datatransfer.scheduler.library.proxies.Proxies.transferBinder;
import static org.gcube.datatransfer.scheduler.library.proxies.Proxies.transferScheduler;

import java.util.concurrent.TimeUnit;

import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.common.agent.Types.storageType;
import org.gcube.datatransfer.scheduler.library.BinderLibrary;
import org.gcube.datatransfer.scheduler.library.SchedulerLibrary;
import org.gcube.datatransfer.scheduler.library.obj.SchedulerObj;
import org.gcube.datatransfer.scheduler.library.obj.TypeOfSchedule;
import org.gcube.datatransfer.scheduler.library.outcome.CallingSchedulerResult;


public class ScheduleTest {
	static SchedulerLibrary schedulerLibrary = null;


	public static void retriveSchedulerLibrary(String scope, String name){
		//binderLibrary
		ScopeProvider.instance.set(scope);

		BinderLibrary binderLibrary = null;	
		boolean binderException=false;
		try {		
			binderLibrary = transferBinder().at("node18.d.d4science.research-infrastructures.eu", 8081).withTimeout(10, TimeUnit.SECONDS).build();		 
		}catch (Exception e) {
			binderException=true;
			System.err.print("exception when calling transferSchedulerBinder()\n"+e);
		}

		if(binderLibrary!=null){
			if(binderException)System.out.println("SchedulerProxy(retrieveSchedulerLibrary) - built the bunderLibrary by setting the address at pcitgt1012.cern.ch:8080 ...");
			else System.out.println("SchedulerProxy(retrieveSchedulerLibrary) - built the binderLibrary ...");
		}
		else{
			System.err.print("binder library is null");
			return;			
		}

		//Getting Scheduler EndpointReferenceType
		W3CEndpointReference schedulerEpr = null;
		try{
			schedulerEpr=binderLibrary.bind(name);
		}catch (Exception e) {
			System.out.print("exception when calling binderLibrary.bind(..).. \n"+e);
			return;
		}

		if(schedulerEpr==null){
			System.out.print("schedulerEpr==null\n");
			return;
		}
		/*//schedulerLibrary		
	W3CEndpointReferenceBuilder w3cenpointBuilder = new W3CEndpointReferenceBuilder();
	try{
		w3cenpointBuilder.address(schedulerEpr.getAddress().toString());
		w3cenpointBuilder.referenceParameter((Element)schedulerEpr.getProperties().get(0));
	}catch (Exception e){
		System.out.print("exception when creating the W3CEndpointReferenceBuilder\n"+e);
		return;
	}	*/	

		try {
			//	schedulerLibrary = transferScheduler().at("testing", "node18.d.d4science.research-infrastructures.eu", 8081).build();
			schedulerLibrary = transferScheduler().at(schedulerEpr).build();
		}catch (Exception e) {
			System.out.print("exception when calling transferScheduler(..).at(..)\n"+e);
			return;
		}
	}


	public static void main(String[] args) {
		SchedulerObj schedulerObj = new SchedulerObj();
		String[] inputUrl=new String[]{"http://upload.wikimedia.org/wikipedia/commons/6/6e/Wikipedia_logo_silver.png"};

		schedulerObj.setDestinationFolder("/nickTest/test");
		schedulerObj.setScope("/gcube/devsec");
		schedulerObj.setTypeOfStorage(storageType.LocalGHN);
		schedulerObj.setAgentHostname("geoserver-dev.d4science-ii.research-infrastructures.eu");
		TypeOfSchedule typeSch=new TypeOfSchedule();
		typeSch.setDirectedScheduled(true);
		schedulerObj.setTypeOfSchedule(typeSch);
		schedulerObj.setSyncOp(false);

		schedulerObj.setTypeOfTransfer("FileBasedTransfer");
		schedulerObj.setInputUrls(inputUrl);
		schedulerObj.setOverwrite(true);
		schedulerObj.setUnzipFile(false);

		retriveSchedulerLibrary("/gcube/devsec", "testing");
		String transferId=null;
		if(schedulerLibrary==null){
			System.out.print("schedulerLibrary=null\n");
			return;
		}
		//schedule
		try{
			transferId = schedulerLibrary.scheduleTransfer(schedulerObj);
		}catch (Exception e) {
			System.err.print("exception when calling schedulerLibrary(schedule)\n"+e);
		}
		System.out.println("transferId="+transferId);

		//monitor
		if(transferId==null)return;
		CallingSchedulerResult res=null;
		String monitorResult="";
		int n=0;
		while((monitorResult.compareTo("STANDBY")==0 ||
				monitorResult.compareTo("ONGOING")==0||
						monitorResult.compareTo("")==0 ) && n<=10){
			sleepFiveSec();
			n++;
			try{
				res = schedulerLibrary.monitorTransfer(transferId);
			}catch (Exception e) {
				System.err.println("exception when calling schedulerLibrary(monitor)\n"+e);
			}
			System.out.println("monitor res="+res.getMonitorResult());

			if(res!=null)monitorResult=res.getMonitorResult();
		}
		if(monitorResult.compareTo("STANDBY")==0)return;
		
		//getoutcomes
		try{
			res = schedulerLibrary.getOutcomesOfTransfer(transferId);
		}catch (Exception e) {
			System.err.println("exception when calling schedulerLibrary(getOutcomesOfTransfer)\n"+e);
		}
		System.out.println("getoutcomes res="+res.getSchedulerOutcomes());

		//cancel 
		try{
			res = schedulerLibrary.cancelTransfer(transferId,true);
		}catch (Exception e) {
			System.err.println("exception when calling schedulerLibrary(cancelTransfer)\n"+e);
		}
		System.out.println("cancel res="+res.getCancelResult());

	}

	public static void sleepFiveSec(){
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
