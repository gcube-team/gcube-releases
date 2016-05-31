package org.gcube.dataanalysis.ecoengine.connectors;

import org.gcube.contentmanagement.graphtools.utils.HttpRequest;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

public class RemoteGenerationManager {

	private final String submissionMethod = "submit";
	private final String statusMethod = "status/";
	private String submissionID;
	private String username;
	private String endpoint;
	
	public RemoteGenerationManager(String generatorEndPoint){
		if (generatorEndPoint.charAt(generatorEndPoint.length()-1)=='/')
			endpoint = generatorEndPoint;
		else
			endpoint = generatorEndPoint+"/"; 
	}
	
	public void submitJob(RemoteHspecInputObject rhio) throws Exception{
		
		AnalysisLogger.getLogger().warn("RemoteGenerationManager: retrieving job information");
		RemoteHspecOutputObject rhoo = null;
		username = rhio.userName;
		try{	
			rhoo = (RemoteHspecOutputObject)HttpRequest.postJSonData(endpoint+submissionMethod, rhio, RemoteHspecOutputObject.class);
			AnalysisLogger.getLogger().trace("RemoteGenerationManager: job information retrieved");	
		}catch(Exception e){
			e.printStackTrace();
			AnalysisLogger.getLogger().trace("RemoteGenerationManager: ERROR - job information NOT retrieved");
			throw e;
		}
		if ((rhoo!=null) && (rhoo.id!=null)){
			AnalysisLogger.getLogger().warn("RemoteGenerationManager: job ID retrieved ");
			submissionID = rhoo.id;
		}
		else{
			AnalysisLogger.getLogger().warn("RemoteGenerationManager: ERROR - job ID NOT retrieved "+rhoo.error);
			throw new Exception();
		}
	}
	
	public double retrieveCompletion(){
		RemoteHspecOutputObject rhoo = retrieveCompleteStatus();
		
		try{
			double completion = Double.parseDouble(rhoo.completion);
			return completion;
		}catch(Exception e){
			e.printStackTrace();
			AnalysisLogger.getLogger().warn("RemoteGenerationManager: ERROR - cannot retrieve information from remote site ",e);
		}
		return 0;
	}
	
	public RemoteHspecOutputObject retrieveCompleteStatus(){
		RemoteHspecOutputObject rhoo = null;

		try{
			rhoo = (RemoteHspecOutputObject)HttpRequest.getJSonData(endpoint+statusMethod+submissionID, null ,RemoteHspecOutputObject.class);
		}catch(Exception e){
			e.printStackTrace();
			AnalysisLogger.getLogger().warn("RemoteGenerationManager: ERROR - cannot retrieve information from remote site ",e);
		}
		
		return rhoo;
	}
	
	
}
