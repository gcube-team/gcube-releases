package org.gcube.application.aquamaps.ecomodelling.generators.processing;

import org.gcube.application.aquamaps.ecomodelling.generators.connectors.RemoteHspecInputObject;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.RemoteHspecOutputObject;
import org.gcube.contentmanagement.graphtools.utils.HttpRequest;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

import com.google.gson.Gson;

public class RemoteGenerationManager {

	private final String submissionMethod = "submit";
	private final String statusMethod = "status/";
	private String submissionID;
	private String username;
	private String endpoint;
	private boolean started;
	
	public RemoteGenerationManager(String generatorEndPoint){
		if (generatorEndPoint.charAt(generatorEndPoint.length()-1)=='/')
			endpoint = generatorEndPoint;
		else
			endpoint = generatorEndPoint+"/";
		
		started = false;
	}
	
	public void submitJob(RemoteHspecInputObject rhio) throws Exception{
		
		AnalysisLogger.getLogger().warn("RemoteGenerationManager: retrieving job information");
		RemoteHspecOutputObject rhoo = null;
		username = rhio.userName;
		try{	
//			AnalysisLogger.getLogger().trace("IN: "+new Gson().toJson(rhio));
			rhoo = (RemoteHspecOutputObject)HttpRequest.postJSonData(endpoint+submissionMethod, rhio, RemoteHspecOutputObject.class);
			AnalysisLogger.getLogger().trace("OUT: "+new Gson().toJson(rhoo));
			AnalysisLogger.getLogger().trace("RemoteGenerationManager: job information retrieved");	
			started = true;
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
		if (started){
		try{
			rhoo = (RemoteHspecOutputObject)HttpRequest.getJSonData(endpoint+statusMethod+submissionID, null ,RemoteHspecOutputObject.class);
			if ((rhoo.id == null) || rhoo.id.equals("null")) rhoo = getDefaultOutput();
//			AnalysisLogger.getLogger().trace("OUT: "+new Gson().toJson(rhoo));
		}catch(Exception e){
			e.printStackTrace();
			AnalysisLogger.getLogger().warn("RemoteGenerationManager: ERROR - cannot retrieve information from remote site ",e);
		}
		
		}
		else {
			rhoo = getDefaultOutput();
		}
		return rhoo;
	}
	
	private RemoteHspecOutputObject getDefaultOutput(){
		
		RemoteHspecOutputObject rhoo = new RemoteHspecOutputObject();
		rhoo.status = "INITIALIZING";
		rhoo.completion = "0.0";
		return rhoo;
	}
	
}
