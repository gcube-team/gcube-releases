package org.gcube.datatransfer.portlets.user.server;

import static org.gcube.datatransfer.scheduler.library.proxies.Proxies.transferScheduler;
import static org.gcube.datatransfer.scheduler.library.proxies.Proxies.transferBinder;
import static org.gcube.datatransfer.scheduler.library.proxies.Proxies.transferManagement;
import static org.gcube.resources.discovery.icclient.stubs.CollectorConstants.localname;

import java.net.URL;
import java.net.URLClassLoader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.gcube.common.scan.DefaultScanner;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.api.ServiceMap;
import org.gcube.datatransfer.portlets.user.server.utils.Utils;
import org.gcube.datatransfer.scheduler.library.BinderLibrary;
import org.gcube.datatransfer.scheduler.library.ManagementLibrary;
import org.gcube.datatransfer.scheduler.library.SchedulerLibrary;
import org.gcube.datatransfer.scheduler.library.obj.SchedulerObj;
import org.gcube.datatransfer.scheduler.library.outcome.CallingManagementResult;
import org.gcube.datatransfer.scheduler.library.outcome.CallingSchedulerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;


public class SchedulerProxy {

	SchedulerLibrary schedulerLibrary = null;


	public void retrieveSchedulerLibrary(String scope, String ResourceName) throws Exception{
		ScopeProvider.instance.set(scope);
		//in case of first visit .. 
		//binderLibrary
		BinderLibrary binderLibrary = null;		
		try {		
			binderLibrary = transferBinder().withTimeout(10, TimeUnit.SECONDS).build();		 
		}catch (Exception e) {
			System.err.print("exception when calling transferSchedulerBinder(..)\n"+e);
		}
		//Getting Scheduler EndpointReferenceType
		W3CEndpointReference schedulerEpr = null;
		try{
			schedulerEpr=binderLibrary.bind(ResourceName);
		}catch (Exception e) {
			System.err.print("exception when calling binderLibrary.bind(..).. \n"+e);
		}
		//in case of having visited before we go directly to the stateful by taking the epr from IS

		//schedulerLibrary		
		/*@SuppressWarnings("restriction")
	W3CEndpointReferenceBuilder w3cenpointBuilder = new W3CEndpointReferenceBuilder();
	try{
		w3cenpointBuilder.address(schedulerEpr.getAddress().toString());
		w3cenpointBuilder.referenceParameter((Element)schedulerEpr.getProperties().get(0));
	}catch (Exception e){
		System.err.print("exception when creating the W3CEndpointReferenceBuilder\n"+e);
	}	*/	

		try {
			schedulerLibrary = transferScheduler().at(schedulerEpr).withTimeout(10, TimeUnit.SECONDS).build();
		}catch (Exception e) {
			System.err.print("exception when calling transferScheduler(..).at(..)\n"+e);
		}
	}


	//Cancel
	public CallingSchedulerResult cancel(String transferId, boolean force) throws Exception{
		CallingSchedulerResult callingSchedulerResult=new CallingSchedulerResult();

		try{
			callingSchedulerResult = schedulerLibrary.cancelTransfer(transferId, force);
		}catch (Exception e) {
			System.err.print("exception when calling schedulerLibrary.cancelTransfer(..) \n"+e);
		}
		return callingSchedulerResult;

	}

	//Monitor
	public CallingSchedulerResult monitor(String transferId) throws Exception{
		CallingSchedulerResult callingSchedulerResult=new CallingSchedulerResult();

		try{
			callingSchedulerResult = schedulerLibrary.monitorTransfer(transferId);
		}catch (Exception e) {
			System.err.print("exception when calling schedulerLibrary.monitorTransfer(..) \n"+e);
		}
		return callingSchedulerResult;
	}


	//GetOutcomes
	public CallingSchedulerResult getOutcomes(String transferId) throws Exception{
		CallingSchedulerResult callingSchedulerResult=new CallingSchedulerResult();
		try{
			callingSchedulerResult = schedulerLibrary.getOutcomesOfTransfer(transferId);
		}catch (Exception e) {
			System.err.print("exception when calling schedulerLibrary.getOutcomesOfTransfer(..) \n"+e);
		}
		return callingSchedulerResult;
	}

	/*
	 * schedule
	 * input: SchedulerObj, String with the scope, String with the resourceName and String
	 * with the pass in case of having workspace
	 * returns: transferId or null in case of having an error
	 */
	public String schedule(SchedulerObj schedulerObj, String scope, String resourceName, String workspacePass,String sourceType, String destinationFolder) throws Exception{
		//we check if there is a need for adding the credentials in the URIS
		//it is not a safe way for now ... !!
		// ALTHOUGH WE DO HAVE safety between client - portlet.. 

		//first case when having datasource ... 
		if(schedulerObj.getDataSourceId()!=null && schedulerObj.getTypeOfTransfer().compareTo("TreeBasedTransfer")!=0){
			String nameDSOURCE=null;
			String descriptionDSOURCE=null;
			String hostDSOURCE=null;
			String userDSOURCE=null;
			String passDSOURCE=null;

			String result=getObjectsFromIS("DataSource", resourceName,scope);
			if (result==null) {
				System.out.println("SchedulerProxy - schedule - getObjectsFromIS(DataSources) - result=null");
				return null;
			}
			String[] sourcesArray=result.split("\n");
			for(String tmp:sourcesArray){
				//tmp contains: resultIdOfIS--name--description--endpoint--username--password--propertyFolders
				String[] partsOfInfo=tmp.split("--");
				if(partsOfInfo[0].compareTo(schedulerObj.getDataSourceId())==0){
					nameDSOURCE=partsOfInfo[1];
					descriptionDSOURCE = partsOfInfo[2];
					hostDSOURCE=partsOfInfo[3];
					userDSOURCE=partsOfInfo[4];
					passDSOURCE=partsOfInfo[5];
					break;
				}
			}
			if(hostDSOURCE==null|| nameDSOURCE==null){
				System.out.println("SchedulerProxy - schedule - cannot find the datasource in IS");
				return null;
			}

			String[] inputURIS = schedulerObj.getInputUrls();
			String header="";
			if(nameDSOURCE.startsWith("FTP")){
				if(userDSOURCE==null || passDSOURCE==null){
					System.out.println("GET FILE LIST OF DATASOURCE - FTP datasource does not have 'username' or/and 'password' in IS");
					return null;
				}
				String[] partsOfEndpoint=hostDSOURCE.split("//");
				if(partsOfEndpoint.length<2){
					System.out.println("SchedulerProxy - schedule - endpoint does not contain '//' and it is not a proper hostname");
					return null;
				}
				//header = "ftp://user:pass@pcd4science3.cern.ch/";
				header=partsOfEndpoint[0]+"//"+userDSOURCE+":"+passDSOURCE+"@"+partsOfEndpoint[1];
				if(!header.endsWith("/"))header=header+"/";
			}
			else if(nameDSOURCE.startsWith("HTTP")||nameDSOURCE.startsWith("HTTPS")){
				if(userDSOURCE==null || passDSOURCE==null){
					header=hostDSOURCE;
					if(!header.endsWith("/"))header=header+"/";
				}
				else {
					String[] partsOfEndpoint=hostDSOURCE.split("//");
					if(partsOfEndpoint.length<2){
						System.out.println("SchedulerProxy - schedule - endpoint does not contain '//' and it is not a proper hostname");
						return null;
					}
					//header = "http://username:password@hostname/";
					header=partsOfEndpoint[0]+"//"+userDSOURCE+":"+passDSOURCE+"@"+partsOfEndpoint[1];
					if(!header.endsWith("/"))header=header+"/";
				}		
			}

			int num=0;
			for(String tmp:inputURIS){				
				inputURIS[num]=header+tmp+"/";
				num++;
			}
			schedulerObj.setInputUrls(inputURIS);
		}
		else if(workspacePass.compareTo("")!=0){ // we have workspace items
			String user=resourceName;
			String[] inputURIS=schedulerObj.getInputUrls();
			int num=0;
			for(String tmp:inputURIS){		
				String[] partsOfLink=tmp.split("//");
				if(partsOfLink.length<2){
					System.out.println("SchedulerProxy - schedule - endpoint does not contain '//' and it is not a proper hostname");
					return null;
				}

				//authenticatedLink = "http://username:password@hostname/fileName";
				String authenticatedLink="webdav://"+user+":"+workspacePass+"@"+partsOfLink[1];

				inputURIS[num]=authenticatedLink;
				num++;
			}
			schedulerObj.setInputUrls(inputURIS);
		}

		//encode the input urls 
		if(schedulerObj.getInputUrls()!=null){
			//.....TO DO
			List<String> encodedUris = new ArrayList<String>();
			for(String tmp: schedulerObj.getInputUrls()){
				encodedUris.add(tmp.replaceAll(" ","%20"));
			}
			String[] encodedArray=new String[encodedUris.size()];
			for(int i =0;i<encodedUris.size();i++)encodedArray[i]=encodedUris.get(i);
			schedulerObj.setInputUrls(encodedArray);
		}

		//when having a datastorage, there is a need for modifying the output urls
		if(schedulerObj.getDataStorageId()!=null && schedulerObj.getTypeOfTransfer().compareTo("TreeBasedTransfer")!=0){
			String nameDSTORAGE=null;
			String hostDSTORAGE=null;
			String userDSTORAGE=null;
			String passDSTORAGE=null;

			String result=getObjectsFromIS("DataStorage", resourceName,scope);
			if (result==null) {
				System.out.println("SchedulerProxy - schedule - getObjectsFromIS(DataStorages) - result=null");
				return null;
			}
			String[] sourcesArray=result.split("\n");
			for(String tmp:sourcesArray){
				//tmp contains: resultIdOfIS--name--description--endpoint--username--password--
				String[] partsOfInfo=tmp.split("--");
				if(partsOfInfo[0].compareTo(schedulerObj.getDataStorageId())==0){
					nameDSTORAGE=partsOfInfo[1];
					hostDSTORAGE=partsOfInfo[3];
					userDSTORAGE=partsOfInfo[4];
					passDSTORAGE=partsOfInfo[5];
					break;
				}
			}
			if(hostDSTORAGE==null|| nameDSTORAGE==null){
				System.out.println("SchedulerProxy - schedule - cannot find the datastorage in IS");
				return null;
			}

			String[] outputURIS = schedulerObj.getOutputUrls();
			String header="";
			if(nameDSTORAGE.startsWith("FTP")){
				if(userDSTORAGE==null || passDSTORAGE==null){
					System.out.println("GET FILE LIST OF DATASTORAGE - FTP datastorage does not have 'username' or/and 'password' in IS");
					return null;
				}
				String[] partsOfEndpoint=hostDSTORAGE.split("//");
				if(partsOfEndpoint.length<2){
					System.out.println("SchedulerProxy - schedule - endpoint does not contain '//' and it is not a proper hostname");
					return null;
				}
				//header = "ftp://USER:PASS@pcd4science3.cern.ch/";
				header=partsOfEndpoint[0]+"//"+userDSTORAGE+":"+passDSTORAGE+"@"+partsOfEndpoint[1];
				if(!header.endsWith("/"))header=header+"/";
			}
			else if(nameDSTORAGE.startsWith("HTTP")||nameDSTORAGE.startsWith("HTTPS")){
				if(userDSTORAGE==null || passDSTORAGE==null){
					header=hostDSTORAGE;
					if(!header.endsWith("/"))header=header+"/";
				}
				else {
					String[] partsOfEndpoint=hostDSTORAGE.split("//");
					if(partsOfEndpoint.length<2){
						System.out.println("SchedulerProxy - schedule - endpoint does not contain '//' and it is not a proper hostname");
						return null;
					}
					//header = "http://username:password@hostname/";
					header=partsOfEndpoint[0]+"//"+userDSTORAGE+":"+passDSTORAGE+"@"+partsOfEndpoint[1];
					if(!header.endsWith("/"))header=header+"/";
				}		
			}

			String[] newOutPutUris=outputURIS.clone();
			//			if(sourceType.compareTo("URI")==0 || sourceType.compareTo("MongoDB")==0
			//					|| sourceType.compareTo("AgentSource")==0
			//					|| sourceType.compareTo("DataSource")==0){
			//				newOutPutUris = keepOnlyTheLastPart(newOutPutUris);
			//			}
			//CHANGED in any case (MongoDB,AgentSource,DataSource,Workspace) 
			//we keep the last part only
			newOutPutUris = keepOnlyTheLastPart(newOutPutUris);
			int num=0;
			if(!destinationFolder.endsWith("/"))destinationFolder=destinationFolder+"/";
			for(String tmp:newOutPutUris){				
				outputURIS[num]=header+destinationFolder+tmp+"/";
				num++;
			}
			schedulerObj.setOutputUrls(outputURIS);
		}
		//encode the output urls 
		if(schedulerObj.getOutputUrls()!=null){
			//.....TO DO
		}

		if(schedulerObj.getTypeOfSchedule().getManuallyScheduled()!=null){
			DateFormat formatter = new SimpleDateFormat("dd.MM.yy-HH.mm");
			Date tmpDate=null;
			String instanceString= schedulerObj.getTypeOfSchedule().getManuallyScheduled().getInstanceString();
			//System.out.println("given string:\n"+instanceString);
			try {
				tmpDate = formatter.parse(instanceString);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Calendar tmpCalendar = Calendar.getInstance();
			tmpCalendar.setTime(tmpDate);
			//System.out.println("given date:\nYEAR="+tmpCalendar.get(Calendar.YEAR)+"\nMONTH="+tmpCalendar.get(Calendar.MONTH)+"\nDAY_OF_MONTH="+tmpCalendar.get(Calendar.DAY_OF_MONTH)+"\nHOUR="+tmpCalendar.get(Calendar.HOUR_OF_DAY)+"\nMINUTE="+tmpCalendar.get(Calendar.MINUTE));

			schedulerObj.getTypeOfSchedule().getManuallyScheduled().setCalendar(tmpCalendar);
		}
		else if(schedulerObj.getTypeOfSchedule().getPeriodicallyScheduled()!=null){
			DateFormat formatter = new SimpleDateFormat("dd.MM.yy-HH.mm");
			Date tmpDate=null;
			String startInstanceString= schedulerObj.getTypeOfSchedule().getPeriodicallyScheduled().getStartInstanceString();
			//System.out.println("given startInstanceString:\n"+startInstanceString);

			Calendar tmpCalendar = Calendar.getInstance();
			if(startInstanceString.compareToIgnoreCase("now")!=0){
				try {
					tmpDate = formatter.parse(startInstanceString);
				} catch (Exception e) {
					e.printStackTrace();
				}
				tmpCalendar.setTime(tmpDate);
				//System.out.println("given startInstance:\nYEAR="+tmpCalendar.get(Calendar.YEAR)+"\nMONTH="+tmpCalendar.get(Calendar.MONTH)+"\nDAY_OF_MONTH="+tmpCalendar.get(Calendar.DAY_OF_MONTH)+"\nHOUR="+tmpCalendar.get(Calendar.HOUR_OF_DAY)+"\nMINUTE="+tmpCalendar.get(Calendar.MINUTE));
			}
			else{
				//System.out.println("given startInstance:\nYEAR="+tmpCalendar.get(Calendar.YEAR)+"\nMONTH="+tmpCalendar.get(Calendar.MONTH)+"\nDAY_OF_MONTH="+tmpCalendar.get(Calendar.DAY_OF_MONTH)+"\nHOUR="+tmpCalendar.get(Calendar.HOUR_OF_DAY)+"\nMINUTE="+tmpCalendar.get(Calendar.MINUTE));
			}
			schedulerObj.getTypeOfSchedule().getPeriodicallyScheduled().setStartInstance(tmpCalendar);
		}

		//if(schedulerObj.getInputUrls()!=null){
		//	System.out.println("SchedulerProxy - schedule - input URLS:");
		//	for(String tmp:schedulerObj.getInputUrls()){				
		//		System.out.println("'"+tmp+"'");
		//	}
		//}
		//if(schedulerObj.getOutputUrls()!=null){
		//	System.out.println("SchedulerProxy - schedule - output URLS:");
		//	for(String tmp:schedulerObj.getOutputUrls()){				
		//		System.out.println("'"+tmp+"'");
		//	}
		//}
		String transferId=null;
		try{
			transferId = schedulerLibrary.scheduleTransfer(schedulerObj);
		}catch (Exception e) {
			System.err.print("exception when calling schedulerLibrary.localFileBasedTransfer(..) \n"+e);
		}

		return transferId;
	}

	//optional print
	//		void hierarchy() {
	//	        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	//	        while (classLoader != null) {
	//	            System.out.println(classLoader+"->"+classLoader.getParent());
	//	            classLoader = classLoader.getParent();
	//	        }       
	//	    }
	//		void hierarchy2() {
	//	        ClassLoader classLoader = (SchedulerProxy.class).getClassLoader();
	//	        while (classLoader != null) {
	//	            System.out.println(classLoader+"->"+classLoader.getParent());
	//	            classLoader = classLoader.getParent();
	//	        }
	//	    }

	public CallingManagementResult getTransfers(String resourceName, String scope) throws Exception{
	//	System.out.println("SchedulerProxy (getTransfers) - resourceName="+resourceName+" - scope="+scope);
		ScopeProvider.instance.set(scope);
	//	System.out.println("SchedulerProxy (getTransfers) - set scope is:"+ScopeProvider.instance.get());


		//				Logger logger = LoggerFactory.getLogger("getTransfers");		
		//				String address = ServiceMap.instance.endpoint(localname);
		//				logger.debug("address="+address);
		//				System.out.println(" **************** calling hierarchy() **************** ");
		//				hierarchy();
		//				System.out.println(" **************** calling hierarchy2() **************** c");
		//				hierarchy2();
		//				System.out.println(" **************** calling commonConfiguration Default Scanner() **************** c");
		//				DefaultScanner.main(null);

		//Management Library
		ManagementLibrary managementLibrary = null;		
		try {		
			managementLibrary = transferManagement().withTimeout(10, TimeUnit.SECONDS).build();		 
		}catch (Exception e) {
			System.err.print("exception when calling transferSchedulerManagement().build()\n"+e);
		}
		CallingManagementResult callingManagementResult= null;
		try{
			callingManagementResult=managementLibrary.getAllTransfersInfo(resourceName); 
		}catch (Exception e) {
			System.err.print("exception when calling managementLibrary.getAllTransfersInfo(..).. \n"+e);
		}
		return callingManagementResult;
	}	

	public String getObjectsFromIS(String type , String resourceName, String scope) throws Exception{
	//	System.out.println("SchedulerProxy (getObjectsFromIS) - type="+type+" - resourceName="+resourceName+" - scope="+scope);
		ScopeProvider.instance.set(scope);
	//	System.out.println("SchedulerProxy (getObjectsFromIS) - set scope is:"+ScopeProvider.instance.get());
		//Management Library
		ManagementLibrary managementLibrary = null;		
		try {		
			managementLibrary = transferManagement().withTimeout(10, TimeUnit.SECONDS).build();		 
		}catch (Exception e) {
			System.err.print("exception when calling transferSchedulerManagement().build()\n"+e);
		}

		final String typeOfObj=type;
		String result= null;
		try{
			result=managementLibrary.getObjectsFromIS(typeOfObj); 
		}catch (Exception e) {
			System.err.print("exception when calling managementLibrary.getAllTransfersInfo(..).. \n"+e);
		}
		
		result = filterObjs(result, type); 
		return result;
	}
	
	// filter only datasources and datastorages by protocol: 
	// We keep only those whose name start with http or ftp
	public String filterObjs(String result, String type){
		if(type==null)return null;
		else if (type.compareTo("DataSource")==0 || type.compareTo("DataStorage")==0){
		//tmpDataSource structure: 
		// resultIdOfIS--name--description--endpoint--username--password--folder
			String[] objs=result.split("\n");
			String newResult="";
			for(String obj:objs){
				String[] parts=obj.split("--");
				if(parts==null || parts.length<4)continue;
				else if(parts[1].toUpperCase().startsWith("HTTP") || parts[1].toUpperCase().startsWith("FTP")){
					newResult=newResult+obj+"\n";
				}
			}
			return newResult;
		}
		else return result; // in case of Agent or whatever we do not filter...
		
	}
	
	public String getAgentStatistics(String scope) throws Exception{
		ScopeProvider.instance.set(scope);
		//Management Library
		ManagementLibrary managementLibrary = null;		
		try {		
			managementLibrary = transferManagement().withTimeout(10, TimeUnit.SECONDS).build();		 
		}catch (Exception e) {
			System.err.print("exception when calling transferSchedulerManagement().build()\n"+e);
		}		
		String result= null;
		try{
			result=managementLibrary.getAgentStatistics(); 
		}catch (Exception e) {
			System.err.print("exception when calling managementLibrary.getAgentStatistics().. \n"+e);
		}
		return result;
	}



	public String[] keepOnlyTheLastPart(String[] uris) throws Exception{
		List<String> changedUris=new ArrayList<String>();
		for(String uri:uris){
			String tmp;
			if(uri.startsWith("smp")){
				String[] uriParts = uri.split("\\?");
				tmp = uriParts[0].replaceFirst("smp://", "");
				//changedUris.add(tmp.replaceFirst("smp://", ""));
				//continue;
			}
			else tmp=uri;

			String[] parts=tmp.split("/");
			String lastPart=parts[parts.length-1];
			changedUris.add(lastPart);
		}
		String[] returnArray=new String[changedUris.size()];
		int i =0;
		for(String tmp:changedUris){returnArray[i]=tmp;i++;}

		return returnArray;
	}
}
