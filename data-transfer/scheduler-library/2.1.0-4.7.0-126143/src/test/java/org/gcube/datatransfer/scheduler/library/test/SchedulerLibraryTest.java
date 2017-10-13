package org.gcube.datatransfer.scheduler.library.test;


import static org.gcube.datatransfer.scheduler.library.proxies.Proxies.transferBinder;
import static org.gcube.datatransfer.scheduler.library.proxies.Proxies.transferScheduler;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.common.agent.Types.StorageManagerDetails;
import org.gcube.datatransfer.common.agent.Types.storageType;
import org.gcube.datatransfer.common.scheduler.Types.FrequencyType;
import org.gcube.datatransfer.scheduler.library.BinderLibrary;
import org.gcube.datatransfer.scheduler.library.SchedulerLibrary;
import org.gcube.datatransfer.scheduler.library.obj.ManuallyScheduled;
import org.gcube.datatransfer.scheduler.library.obj.PeriodicallyScheduled;
import org.gcube.datatransfer.scheduler.library.obj.SchedulerObj;
import org.gcube.datatransfer.scheduler.library.obj.TypeOfSchedule;
import org.gcube.datatransfer.scheduler.library.outcome.CallingSchedulerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerLibraryTest {

	static Logger logger = LoggerFactory.getLogger("SchedulerLibraryTest");
	static SchedulerLibrary schedulerLibrary = null;

	//@Test
	//public void Test1(){
	public static void main (String [] args){
		String scope="/gcube/devsec";
		String resourceName="testing";	
		ScopeProvider.instance.set(scope);

		//in case of first visit .. 
		//binderLibrary
		BinderLibrary binderLibrary = null;	
		boolean binderException=false;
		try {		
	//		binderLibrary = transferSchedulerBinder().at("node18.d.d4science.research-infrastructures.eu", 8081).withTimeout(10, TimeUnit.SECONDS).build();		 
	//		binderLibrary = transferBinder().at("pcitgt1012.cern.ch", 8080).withTimeout(10, TimeUnit.SECONDS).build();		 
	binderLibrary = transferBinder().at("node18.d.d4science.research-infrastructures.eu", 8081).withTimeout(10, TimeUnit.SECONDS).build();		 
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

		//String transferId = test1();
		String transferId = test2();
		//cliTest(args);

		//String transferId="090f06f0-2f38-11e2-bf41-fa57d5197d4f";
		//transferId = periodicallyScheduled1();
		//transferId = periodicallyScheduled2();

		//transferId=manuallyScheduled1();
		//transferId=directScheduled1();

		//transferId=manuallyBigFile();

		if(transferId==null)System.out.println("SchedulerLibraryTest - main: transferId=null");
		else {
			//monitor(transferId);
			//getOutcomes(transferId);
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

	
	static String test1(){
		SchedulerObj schedulerObj = new SchedulerObj();
		String[] inputUrl=new String[]{"http://upload.wikimedia.org/wikipedia/commons/6/6e/Wikipedia_logo_silver.png"};
		
		schedulerObj.setDestinationFolder("/home/nick/Downloads/test");
		schedulerObj.setScope("/gcube/devsec");

		schedulerObj.setTypeOfStorage(storageType.LocalGHN);
		schedulerObj.setAgentHostname("geoserver-dev.d4science-ii.research-infrastructures.eu");
	//	schedulerObj.setAgentHostname("pcitgt1012.cern.ch");
	//	schedulerObj.setAgentHostname("thredds.research-infrastructures.eu");
		TypeOfSchedule typeSch=new TypeOfSchedule();
		typeSch.setDirectedScheduled(true);
		schedulerObj.setTypeOfSchedule(typeSch);
		schedulerObj.setSyncOp(false);

		schedulerObj.setTypeOfTransfer("FileBasedTransfer");
		schedulerObj.setInputUrls(inputUrl);
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
	
	static String test2(){
		SchedulerObj schedulerObj = new SchedulerObj();
		String[] inputUrl=new String[]{"http://upload.wikimedia.org/wikipedia/commons/6/6e/Wikipedia_logo_silver.png"};
		//String[] outputUrl=new String[]{"ftp://andrea:bilico1980@pcd4science3.cern.ch/testandrea/Wikipedia_logo_silver.png"};
		String[] outputUrl=new String[]{"ftp://d4science:fourD_314@ftp.d4science.org/nickTest/Wikipedia_logo_silver.png"};
		
		
		
		schedulerObj.setInputUrls(inputUrl);
		schedulerObj.setOutputUrls(outputUrl);
	//	schedulerObj.setDestinationFolder("/home/nick/Downloads/test");
		schedulerObj.setScope("/gcube/devsec");
		schedulerObj.setTypeOfStorage(storageType.DataStorage);
		
		schedulerObj.setAgentHostname("geoserver-dev.d4science-ii.research-infrastructures.eu");
	//	schedulerObj.setAgentHostname("pcitgt1012.cern.ch");
	//	schedulerObj.setAgentHostname("thredds.research-infrastructures.eu");
		TypeOfSchedule typeSch=new TypeOfSchedule();
		typeSch.setDirectedScheduled(true);
		schedulerObj.setTypeOfSchedule(typeSch);
		schedulerObj.setSyncOp(false);

		schedulerObj.setTypeOfTransfer("FileBasedTransfer");
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
	
	//LocalFileBased
	static String periodicallyScheduled1(){
		String file = new String ("file:/home/nick/Downloads/testTransferred.txt");
		String file2 = new String ("file:/home/nick/Downloads/testTransferred2.txt");
		String file3 = new String ("file:/home/nick/Downloads/testTransferred3.txt");
		String[] files = new String[]{file,file2,file3};
		String outUri = new String("/home/nick/Downloads/test");
		boolean overwrite=true;
		boolean unzipFile = false;

		String agenthost="pcitgt1012.cern.ch";

		TypeOfSchedule typeOfSchedule= new TypeOfSchedule();
		PeriodicallyScheduled periodicallyScheduled=new PeriodicallyScheduled();
		periodicallyScheduled.setFrequency(FrequencyType.perMinute);
		Calendar startInstance=Calendar.getInstance();
		periodicallyScheduled.setStartInstance(startInstance);
		typeOfSchedule.setPeriodicallyScheduled(periodicallyScheduled);

		String transferId=null;
		String scope = new String ("/gcube/devsec/");

		SchedulerObj schedulerObj = new SchedulerObj();

		schedulerObj.setInputUrls(files);
		schedulerObj.setDestinationFolder(outUri);
		schedulerObj.setOverwrite(overwrite);
		schedulerObj.setUnzipFile(unzipFile);
		schedulerObj.setAgentHostname(agenthost);
		schedulerObj.setTypeOfSchedule(typeOfSchedule);
		schedulerObj.setScope(scope);
		schedulerObj.setSyncOp(false);
		schedulerObj.setTypeOfTransfer("LocalFileBasedTransfer");

		try{
			transferId = schedulerLibrary.scheduleTransfer(schedulerObj);
		}catch (Exception e) {
			logger.error("exception when calling schedulerLibrary.localFileBasedTransfer(..) \n"+e);
		}
		return transferId;
	}

	//FileBased
	static String periodicallyScheduled2(){
		String url1 = new String ("http://upload.wikimedia.org/wikipedia/commons/thumb/e/e6/Views_of_Geneva.jpg/480px-Views_of_Geneva.jpg");
		String url2 = new String ("http://upload.wikimedia.org/wikipedia/commons/thumb/2/28/Switzerland_location_map.svg/800px-Switzerland_location_map.svg.png");
		String url3 = new String ("http://upload.wikimedia.org/wikipedia/commons/5/54/Geneva_Steel_Mill_1942_by_Andreas_Feininger.jpg");
		String[] inputUrls = new String[]{url1,url2,url3};
		boolean overwrite=true;
		boolean unzipFile = false;
		String agenthost="pcitgt1012.cern.ch";		
		String destinationFolder = new String("/home/nick/Downloads/test");

		
		//-----------------------------------------------------------------
		// if you need to make a transfer to a DataStorage you need to fill 
		// info in the StorageManagerDetails about:
		// ServiceClass, ServiceName, Owner, AccessType and to fill
		// the GCUBEScope in the source
		//-------------------------------------------------------------
		// StorageType storageType = StorageType.StorageManager;
		// StorageManagerDetails storageManagerDetails= new StorageManagerDetails();
		// StorageAccessType storageAccessType = StorageAccessType.PUBLIC;
		// storageManagerDetails.setAccessType(storageAccessType);
		// storageManagerDetails.setOwner("EXAMPLE");
		// storageManagerDetails.setServiceClass("EXAMPLE");
		// storageManagerDetails.setServiceName("EXAMPLE");

		StorageManagerDetails storageManagerDetails= null;
		TypeOfSchedule typeOfSchedule= new TypeOfSchedule();

		PeriodicallyScheduled periodicallyScheduled=new PeriodicallyScheduled();
		periodicallyScheduled.setFrequency(FrequencyType.perMinute);
		Calendar startInstance=Calendar.getInstance();
		periodicallyScheduled.setStartInstance(startInstance);

		typeOfSchedule.setPeriodicallyScheduled(periodicallyScheduled);

		String transferId=null;
		String scope = new String ("/gcube/devsec/");

		SchedulerObj schedulerObj = new SchedulerObj();

		schedulerObj.setInputUrls(inputUrls);
		schedulerObj.setDestinationFolder(destinationFolder);
		schedulerObj.setOverwrite(overwrite);
		schedulerObj.setUnzipFile(unzipFile);
		schedulerObj.setAgentHostname(agenthost);
		schedulerObj.setTypeOfTransfer("FileBasedTransfer");
		schedulerObj.setTypeOfStorage(storageType.LocalGHN);
		schedulerObj.setSmDetails(storageManagerDetails);
		schedulerObj.setTypeOfSchedule(typeOfSchedule);
		schedulerObj.setScope(scope);
		schedulerObj.setSyncOp(false);

		System.out.println("xml:\n"+schedulerObj.toXML()+"\n\n");


		try{
			transferId = schedulerLibrary.scheduleTransfer(schedulerObj);
		}catch (Exception e) {
			logger.error("exception when calling schedulerLibrary.localFileBasedTransfer(..) \n"+e);
		}
		return transferId;
	}

	//LocalFileBased
	static String manuallyScheduled1(){
		String file = new String ("file:/home/nick/Downloads/testTransferred.txt");
		String file2 = new String ("file:/home/nick/Downloads/testTransferred2.txt");
		String file3 = new String ("file:/home/nick/Downloads/testTransferred3.txt");
		String[] files = new String[]{file,file2,file3};
		String outUri = new String("/home/nick/Downloads/test");
		boolean overwrite=true;
		boolean unzipFile = false;
		String agenthost="pcitgt1012.cern.ch";

		TypeOfSchedule typeOfSchedule= new TypeOfSchedule();
		ManuallyScheduled manuallyScheduled = new ManuallyScheduled();
		Calendar tmpCalendar = Calendar.getInstance();
		tmpCalendar.set(Calendar.MINUTE,Calendar.getInstance().get(Calendar.MINUTE)+1);

		manuallyScheduled.setCalendar(tmpCalendar);
		typeOfSchedule.setManuallyScheduled(manuallyScheduled);
		System.out.println("manuallyScheduled.getCalendar()="+typeOfSchedule.getManuallyScheduled().getCalendar());

		String transferId= null; 
		String scope = new String ("/gcube/devsec/");

		SchedulerObj schedulerObj = new SchedulerObj();

		schedulerObj.setInputUrls(files);
		schedulerObj.setDestinationFolder(outUri);
		schedulerObj.setOverwrite(overwrite);
		schedulerObj.setUnzipFile(unzipFile);
		schedulerObj.setAgentHostname(agenthost);
		schedulerObj.setTypeOfSchedule(typeOfSchedule);
		schedulerObj.setScope(scope);
		schedulerObj.setSyncOp(false);
		schedulerObj.setTypeOfTransfer("LocalFileBasedTransfer");

		try{
			transferId = schedulerLibrary.scheduleTransfer(schedulerObj);
		}catch (Exception e) {
			logger.error("exception when calling schedulerLibrary.localFileBasedTransfer(..) \n"+e);
		}
		return transferId;
	}

	//manuallyBigFile
	static String manuallyBigFile(){
		String url1 = new String ("http://ftp.lip6.fr/pub/linux/distributions/scientific/6.0/x86_64/iso/SL-60-x86_64-2011-03-03-Everything-DVD2.iso");
		String[] inputUrls = new String[]{url1};
		String agenthost="pcitgt1012.cern.ch";

		String destinationFolder = new String("/home/nick/Downloads/test");
		boolean overwrite=true;
		boolean unzipFile = false;

		StorageManagerDetails storageManagerDetails= null;
		TypeOfSchedule typeOfSchedule= new TypeOfSchedule();

		ManuallyScheduled manuallyScheduled = new ManuallyScheduled();
		Calendar tmpCalendar = Calendar.getInstance();
		tmpCalendar.set(Calendar.MINUTE,Calendar.getInstance().get(Calendar.MINUTE)+1);

		manuallyScheduled.setCalendar(tmpCalendar);
		typeOfSchedule.setManuallyScheduled(manuallyScheduled);
		System.out.println("manuallyScheduled.getCalendar()="+typeOfSchedule.getManuallyScheduled().getCalendar());

		String transferId=null;
		String scope = new String ("/gcube/devsec/");

		SchedulerObj schedulerObj = new SchedulerObj();

		schedulerObj.setInputUrls(inputUrls);
		schedulerObj.setDestinationFolder(destinationFolder);
		schedulerObj.setOverwrite(overwrite);
		schedulerObj.setUnzipFile(unzipFile);
		schedulerObj.setAgentHostname(agenthost);
		schedulerObj.setTypeOfTransfer("FileBasedTransfer");
		schedulerObj.setTypeOfStorage(storageType.LocalGHN);
		schedulerObj.setSmDetails(storageManagerDetails);
		schedulerObj.setTypeOfSchedule(typeOfSchedule);
		schedulerObj.setScope(scope);
		schedulerObj.setSyncOp(false);

		try{
			transferId = schedulerLibrary.scheduleTransfer(schedulerObj);
		}catch (Exception e) {
			logger.error("exception when calling schedulerLibrary.localFileBasedTransfer(..) \n"+e);
		}
		return transferId;
	}


	//LocalFileBased
	static String directScheduled1(){
		String file = new String ("file:/home/nick/Downloads/testTransferred.txt");
		String file2 = new String ("file:/home/nick/Downloads/testTransferred2.txt");
		String file3 = new String ("file:/home/nick/Downloads/testTransferred3.txt");
		String[] files = new String[]{file,file2,file3};
		String outUri = new String("/home/nick/Downloads/test");
		boolean overwrite=true;
		boolean unzipFile = false;
		String agenthost="pcitgt1012.cern.ch";

		TypeOfSchedule typeOfSchedule= new TypeOfSchedule();
		typeOfSchedule.setDirectedScheduled(true);

		String transferId=null; 
		String scope = new String ("/gcube/devsec/");

		SchedulerObj schedulerObj = new SchedulerObj();

		schedulerObj.setInputUrls(files);
		schedulerObj.setDestinationFolder(outUri);
		schedulerObj.setOverwrite(overwrite);
		schedulerObj.setUnzipFile(unzipFile);
		schedulerObj.setAgentHostname(agenthost);
		schedulerObj.setTypeOfSchedule(typeOfSchedule);
		schedulerObj.setScope(scope);
		schedulerObj.setSyncOp(false);
		schedulerObj.setTypeOfTransfer("LocalFileBasedTransfer");

		try{
			transferId = schedulerLibrary.scheduleTransfer(schedulerObj);
		}catch (Exception e) {
			logger.error("exception when calling schedulerLibrary.localFileBasedTransfer(..) \n"+e);
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

	static void cliTest(String [] args){
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String inputValue = null;
		boolean flagExit=false;
		do{
			do{
				System.out.println("\n--------------------------------\n" +
						"choose one of the following options: " +
						"\nschedule \ncancel \nmonitor \ngetoutcomes \nexit \n----------------------");
				try {
					inputValue = br.readLine();
				} catch (IOException ioe) {
					System.out.println("IO error trying to read your inputValue!");
					System.exit(1);
				}
			}while(inputValue.compareTo("schedule")!=0 &&
					inputValue.compareTo("cancel")!=0 &&
					inputValue.compareTo("monitor")!=0 &&
					inputValue.compareTo("getoutcomes")!=0 &&
					inputValue.compareTo("exit")!=0 );	


			if (inputValue.compareTo("exit")==0){
				flagExit=true;
			}
			else if(inputValue.compareTo("schedule")==0){
				FileInputStream fstream = null;
				do{
					String fileName = null;
					System.out.println("\ngive fileName of schedule");
					try {
						fileName = br.readLine();
					} catch (IOException ioe) {
						System.out.println("IO error trying to read your inputValue!");
						System.exit(1);
					}
					try{
						// Open the given file 
						fstream = new FileInputStream(fileName);
					}catch(FileNotFoundException e){
						System.out.println("File Not Found");
						continue;
					}
					break;
				}while(true);

				try{
					// Get the object of DataInputStream
					DataInputStream in = new DataInputStream(fstream);
					BufferedReader brFile = new BufferedReader(new InputStreamReader(in));
					String strLine;
					//Read File Line By Line
					while ((strLine = brFile.readLine()) != null)   {
						// Print the content on the console
						System.out.println (strLine);
						// .... 
					}
					//Close the input stream
					in.close();
				}catch(FileNotFoundException e){
					System.out.println("File Not Found");
				}catch (Exception e){//Catch exception if any
					System.err.println("Error: " + e.getMessage());
				}
			}
			else {
				System.out.println("\ngive transfer id");
				String transferId = null;
				try {
					transferId = br.readLine();
				} catch (IOException ioe) {
					System.out.println("IO error trying to read the transfer id!");
					System.exit(1);
				}

				if(inputValue.compareTo("cancel")==0){
					boolean forceCancel;
					String force =null;
					do{
						try {
							System.out.println("\nforce cancel? (true, false)");
							force = br.readLine();
						} catch (IOException ioe) {
							System.out.println("IO error trying to read the transfer id!");
							System.exit(1);
						}
					}while(force.compareTo("false")!=0 && force.compareTo("true")!=0);

					if(force.compareTo("true")==0)forceCancel=false;
					else forceCancel=true;

					cancel(transferId,forceCancel);				
				}
				else if(inputValue.compareTo("monitor")==0){
					monitor(transferId);
				}
				else if(inputValue.compareTo("getoutcomes")==0){
					getOutcomes(transferId);
				}
			}
		}while(!flagExit);
	}




}


