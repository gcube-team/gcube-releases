package org.gcube.datatransfer.scheduler.library.cli;

import static org.gcube.datatransfer.scheduler.library.proxies.Proxies.transferBinder;
import static org.gcube.datatransfer.scheduler.library.proxies.Proxies.transferScheduler;

import java.util.concurrent.TimeUnit;

import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.trees.patterns.Pattern;
import org.gcube.data.trees.patterns.Patterns;
import org.gcube.datatransfer.common.agent.Types.storageType;
import org.gcube.datatransfer.scheduler.library.BinderLibrary;
import org.gcube.datatransfer.scheduler.library.SchedulerLibrary;
import org.gcube.datatransfer.scheduler.library.obj.SchedulerObj;
import org.gcube.datatransfer.scheduler.library.obj.TypeOfSchedule;
import org.gcube.datatransfer.scheduler.library.outcome.CallingSchedulerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class TreeTest {
	static Logger logger = LoggerFactory.getLogger("SchedulerLibraryTest");
	static SchedulerLibrary schedulerLibrary = null;
	static String address = "node18.d.d4science.research-infrastructures.eu";
	static int port = 8081;
	static String sourceID1 = "from_portlet_27_5";
	static String sourceID2 = "from_portlet_27_5";

	static String agent="geoserver-dev.d4science-ii.research-infrastructures.eu";

	public static void main (String [] args){
		String scope="/gcube/devsec";
		String resourceName="testing";	
		ScopeProvider.instance.set(scope);

		//in case of first visit .. 
		//binderLibrary
		BinderLibrary binderLibrary = null;	
		boolean binderException=false;
		try {		
			binderLibrary = transferBinder().at(address, port).withTimeout(10, TimeUnit.SECONDS).build();		 
		}catch (Exception e) {
			binderException=true;
			logger.error("exception when calling transferSchedulerBinder()\n"+e);
		}

		if(binderLibrary!=null){
			if(binderException)logger.debug("SchedulerProxy(retrieveSchedulerLibrary) - built the bunderLibrary by setting the address at pcitgt1012.cern.ch:8080 ...");
			else logger.debug("SchedulerProxy(retrieveSchedulerLibrary) - built the bunderLibrary ...");
		}
		else{
			logger.error("binder library is null");
			return;			
		}

		//Getting Scheduler EndpointReferenceType
		W3CEndpointReference schedulerEpr = null;
		try{
			schedulerEpr=binderLibrary.bind(resourceName);
		}catch (Exception e) {
			logger.error("exception when calling binderLibrary.bind(..).. \n"+e);
			return;
		}
		//schedulerLibrary		
		/*W3CEndpointReferenceBuilder w3cenpointBuilder = new W3CEndpointReferenceBuilder();
		try{
			w3cenpointBuilder.address(schedulerEpr.getAddress().toString());
			w3cenpointBuilder.referenceParameter((Element)schedulerEpr.getProperties().get(0));
		}catch (Exception e){
			logger.error("exception when creating the W3CEndpointReferenceBuilder\n"+e);
			return;
		}	*/	
		try {
			//schedulerLibrary = transferScheduler().at("NickDrakopoulos", "pcitgt1012.cern.ch", 8080).build();
			schedulerLibrary = transferScheduler().at(schedulerEpr).build();
		}catch (Exception e) {
			logger.error("exception when calling transferScheduler(..).at(..)\n"+e);
			return;
		}

	//	String transferId = treeTransfer();
		//cliTest(args);

		String transferId="8bd85830-c762-11e2-8f75-d73b5774a7f0";
		//transferId = periodicallyScheduled1();
		//transferId = periodicallyScheduled2();

		//transferId=manuallyScheduled1();
		//transferId=directScheduled1();

		//transferId=manuallyBigFile();

		if(transferId==null)System.out.println("SchedulerLibraryTest - main: transferId=null");
		else {
	//		monitor(transferId);
			getOutcomes(transferId);
		}

		//cancel(transferId,true);
		//cancel("8b4e9920-edf5-11e1-ae7b-a7ab4a60541f",true);
		//monitor(transferId);

		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			System.out.println("Test -- InterruptedException - Unable to sleep");
			e.printStackTrace();
		}

	}

	static String treeTransfer(){
		SchedulerObj schedulerObj = new SchedulerObj();
		schedulerObj.setTreeSourceID(sourceID1);
		schedulerObj.setTreeStorageID(sourceID2);
	
		Pattern pattern = Patterns.any(); 
		try {
			schedulerObj.setPattern(pattern);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		schedulerObj.setScope("/gcube/devsec");
		schedulerObj.setTypeOfStorage(storageType.DataStorage);

		schedulerObj.setAgentHostname(agent);
		TypeOfSchedule typeSch=new TypeOfSchedule();
		typeSch.setDirectedScheduled(true);
		schedulerObj.setTypeOfSchedule(typeSch);
		schedulerObj.setSyncOp(false);
		schedulerObj.setTypeOfTransfer("TreeBasedTransfer");
		schedulerObj.setOverwrite(true);
		schedulerObj.setUnzipFile(false);

		String transferId=null;
		try{
			transferId = schedulerLibrary.scheduleTransfer(schedulerObj);
		}catch (Exception e) {
			System.err.print("exception when calling schedulerLibrary.localFileBasedTransfer(..) \n"+e);
		}	
		return transferId;
	}

	//Cancel
	static void cancel(String transferId, boolean force){
		CallingSchedulerResult callingSchedulerResult=new CallingSchedulerResult();
		try{
			callingSchedulerResult = schedulerLibrary.cancelTransfer(transferId, force);
		}catch (Exception e) {
			logger.error("exception when calling schedulerLibrary.cancelTransfer(..) \n"+e);
		}

		if(callingSchedulerResult.getErrors()!=null){
			try{		
				int i=0;
				for (String error : callingSchedulerResult.getErrors()){
					i++;
					if(i==1)logger.debug("We've got errors:");
					logger.debug("Error num:"+i+" - "+error);
				}
			}
			catch (Exception e) {
				logger.error("exception when reading the errors from the callingSchedulerResult \n"+e);
			}
		}
		System.out.println("\n-------------------\n" +
				"SchedulerLibraryTest (cancel) - result="+
				callingSchedulerResult.getCancelResult()+"\n-------------------");
	}

	//Monitor
	static void monitor(String transferId){
		CallingSchedulerResult callingSchedulerResult=new CallingSchedulerResult();

		try{
			callingSchedulerResult = schedulerLibrary.monitorTransfer(transferId);
		}catch (Exception e) {
			logger.error("exception when calling schedulerLibrary.monitorTransfer(..) \n"+e);
		}

		if(callingSchedulerResult.getErrors()!=null){
			try{		
				int i=0;
				for (String error : callingSchedulerResult.getErrors()){
					i++;
					if(i==1)logger.debug("We've got errors:");
					logger.debug("Error num:"+i+" - "+error);
				}
			}
			catch (Exception e) {
				logger.error("exception when reading the errors from the callingSchedulerResult \n"+e);
			}
		}
		System.out.println("\n-------------------\n" +
				"SchedulerLibraryTest (monitor) - result="+
				callingSchedulerResult.getMonitorResult()+"\n-------------------");
	}


	//GetOutcomes
	static void getOutcomes(String transferId){
		CallingSchedulerResult callingSchedulerResult=new CallingSchedulerResult();
		try{
			callingSchedulerResult = schedulerLibrary.getOutcomesOfTransfer(transferId);
		}catch (Exception e) {
			logger.error("exception when calling schedulerLibrary.getOutcomesOfTransfer(..) \n"+e);
		}

		if(callingSchedulerResult.getErrors()!=null){
			try{		
				int i=0;
				for (String error : callingSchedulerResult.getErrors()){
					i++;
					if(i==1)logger.debug("We've got errors:");
					logger.debug("Error num:"+i+" - "+error);
				}
			}
			catch (Exception e) {
				logger.error("exception when reading the errors from the callingSchedulerResult \n"+e);
			}
		}
		System.out.println("\n-------------------\n" +
				"SchedulerLibraryTest (getOutcomes) - result="+
				callingSchedulerResult.getSchedulerOutcomes()+"\n-------------------");
	}

}
