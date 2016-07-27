package org.gcube.execution.workfloworchestrationlayerservice.wrappers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.execution.workflowengine.service.stubs.CONDORResource;
import org.gcube.execution.workfloworchestrationlayerservice.utils.FileInfo;
import org.gcube.execution.workfloworchestrationlayerservice.utils.WorkflowOrchestrationLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CondorAdaptor extends AdaptorBase
{
	
	private static Logger logger = LoggerFactory.getLogger(CondorAdaptor.class);
	
	public static long ParseLongProperty(String file, String key) throws Exception
	{
		File f =new File(file);
		if(!f.exists() || !f.isFile()) throw new Exception("Specified resource file ("+file+") not found");
		BufferedReader r=new BufferedReader(new FileReader(f));
		while(true)
		{
			String line=r.readLine();
			if(line==null) break;
			String parts[]=line.trim().split("\\s#\\s");
			if(parts.length!=2) continue;
			if(parts[0].trim().equalsIgnoreCase(key)) return Long.parseLong(parts[1].trim());
		}
		return -1;
	}
	
//	private static String ParseScopeProperty(String file) throws Exception
//	{
//		File f =new File(file);
//		if(!f.exists() || !f.isFile()) throw new Exception("Specified resource file ("+file+") not found");
//		BufferedReader r=new BufferedReader(new FileReader(f));
//		while(true)
//		{
//			String line=r.readLine();
//			if(line==null) break;
//			String parts[]=line.trim().split("\\s#\\s");
//			if(parts.length!=2) continue;
//			if(parts[0].trim().equalsIgnoreCase("scope")) return parts[1].trim();
//		}
//		return null;
//	}
	
	public static boolean ParseBooleanProperty(String file,String key) throws Exception
	{
		File f =new File(file);
		if(!f.exists() || !f.isFile()) throw new Exception("Specified resource file ("+file+") not found");
		BufferedReader r=new BufferedReader(new FileReader(f));
		while(true)
		{
			String line=r.readLine();
			if(line==null) break;
			String parts[]=line.trim().split("\\s#\\s");
			if(parts.length!=2) continue;
			if(parts[0].trim().equalsIgnoreCase(key)) return Boolean.parseBoolean(parts[1].trim());
		}
		throw new Exception("resource file not complete");
	}
	
	private static String ParseLocalValue(String file,String key) throws Exception
	{
		File f =new File(file);
		if(!f.exists() || !f.isFile()) throw new Exception("Specified resource file ("+file+") not found");
		BufferedReader r=new BufferedReader(new FileReader(f));
		while(true)
		{
			String line=r.readLine();
			if(line==null) break;
			String parts[]=line.trim().split("\\s#\\s");
			if(parts.length!=3) continue;
			if(parts[0].trim().equalsIgnoreCase(key)) return parts[2].trim();
		}
		return null;
	}
	
	private static String ParseRemoteValue(String file,String key) throws Exception
	{
		File f =new File(file);
		if(!f.exists() || !f.isFile()) throw new Exception("Specified resource file ("+file+") not found");
		BufferedReader r=new BufferedReader(new FileReader(f));
		while(true)
		{
			String line=r.readLine();
			if(line==null) break;
			String parts[]=line.trim().split("\\s#\\s");
			if(parts.length!=3) continue;
			if(parts[0].trim().equalsIgnoreCase(key)) return parts[1].trim();
		}
		return null;
	}
	
	private static HashMap<String, FileInfo> ParseInData(String file) throws Exception
	{
		File f =new File(file);
		if(!f.exists() || !f.isFile()) throw new Exception("Specified resource file ("+file+") not found");
		HashMap<String, FileInfo> resource=new HashMap<String, FileInfo>();
		BufferedReader r=new BufferedReader(new FileReader(f));
		while(true)
		{
			String line=r.readLine();
			if(line==null) break;
			String parts[]=line.trim().split("\\s#\\s");
			if(parts.length!=4) continue;
			FileInfo nfo=new FileInfo();
			nfo.TypeOfLocation=FileInfo.LocationType.valueOf(parts[3].trim());
			nfo.Value=parts[2].trim();
			if(parts[0].trim().equalsIgnoreCase("inData")) resource.put(parts[1].trim(), nfo);
		}
		return resource;
	}
	
	private static HashMap<String, FileInfo> ParseExecutable(String file) throws Exception
	{
		File f =new File(file);
		if(!f.exists() || !f.isFile()) throw new Exception("Specified resource file ("+file+") not found");
		HashMap<String, FileInfo> resource=new HashMap<String, FileInfo>();
		BufferedReader r=new BufferedReader(new FileReader(f));
		while(true)
		{
			String line=r.readLine();
			if(line==null) break;
			String parts[]=line.trim().split("\\s#\\s");
			if(parts.length!=4) continue;
			FileInfo nfo=new FileInfo();
			nfo.TypeOfLocation=FileInfo.LocationType.valueOf(parts[3].trim());
			nfo.Value=parts[2].trim();
			if(parts[0].trim().equalsIgnoreCase("executable")) resource.put(parts[1].trim(), nfo);
		}
		return resource;
	}
	
	public static CONDORResource[] GetResources(String file, HashMap<String, byte[]> wrs) throws Exception
	{
		List<CONDORResource> resources=new ArrayList<CONDORResource>();
		String remoteValue=CondorAdaptor.ParseRemoteValue(file, "submit");
		String localValue=CondorAdaptor.ParseLocalValue(file, "submit");
		CONDORResource res= new CONDORResource();
		res.setResourceKey(remoteValue);
		res.setResourceType("Submit");
		res.setResourceAccess("InMessageString");
		res.setInMessageStringPayload(AdaptorBase.GetStringFilePayload(WorkflowOrchestrationLayer.turnBytesToFile(wrs.get(localValue))));
		resources.add(res);
		HashMap<String, FileInfo> data1 =  CondorAdaptor.ParseInData(file);
		for(Map.Entry<String, FileInfo> entry : data1.entrySet())
		{
			res= new CONDORResource();
			res.setResourceKey(entry.getKey());
			res.setResourceType("InData");
			switch(entry.getValue().TypeOfLocation)
			{
				case local:
				{
					res.setInMessageBytePayload(wrs.get(entry.getValue().Value));
					res.setResourceAccess("InMessageBytes");
					break;
				}
				case ss:
				{
					res.setResourceReference(entry.getValue().Value);
					res.setResourceAccess("CMSReference");
					break;
				}
				case url:
				{
					res.setResourceReference(entry.getValue().Value);
					res.setResourceAccess("Reference");
					break;
				}
			}
			resources.add(res);
		}
		HashMap<String, FileInfo> data2 =  CondorAdaptor.ParseExecutable(file);
		for(Map.Entry<String, FileInfo> entry : data2.entrySet())
		{
			res= new CONDORResource();
			res.setResourceKey(entry.getKey());
			res.setResourceType("Executable");
			switch(entry.getValue().TypeOfLocation)
			{
				case local:
				{
					res.setInMessageBytePayload(wrs.get(entry.getValue().Value));
					res.setResourceAccess("InMessageBytes");
					break;
				}
				case ss:
				{
					res.setResourceReference(entry.getValue().Value);
					res.setResourceAccess("CMSReference");
					break;
				}
				case url:
				{
					res.setResourceReference(entry.getValue().Value);
					res.setResourceAccess("Reference");
					break;
				}
			}
			resources.add(res);
		}
		return resources.toArray(new CONDORResource[0]);
	}
	
//	private static void PrintHelp()
//	{
//		StringBuilder buf=new StringBuilder();
//		buf.append("Usage:\n");
//		buf.append("Two arguments are needed\n");
//		buf.append("1) the path of the resource file. The syntax of the resource file is the following:\n");
//		buf.append("\tscope : <the scope to use in case of a gcube environment> (optional)\n");
//		buf.append("\tchokeProgressEvents : <true | false> (depending on whether you want to omit progress reporting)\n");
//		buf.append("\tchokePerformanceEvents : <true | false> (depending on whether you want to omit performance reporting)\n");
//		buf.append("\tisDAG : <true | false> (depending on whether the submited job is a dag or not)\n");
//		buf.append("\tretrieveJobClassAd : <true | false> (depending on whether the job class ad should be retrieved upon status check or not)\n");
//		buf.append("\ttimeout : <The time in milliseconds to wait for the job before candceling it or negative for no timeout> (optional)\n");
//		buf.append("\tpollPeriod : <The period in milliseconds to wait before checking the job status> (optional)\n");
//		buf.append("\tstorePlans : <true | false> (depending on whether you want the plan created and the final one to be stored for inspection)\n");
//		buf.append("\tsubmit : <the name that this file should have once moved to the ui node> : <path to the submit file>\n");
//		buf.append("{ these brackets indicate that the following element can be repeated as many times as needed and they neeed not be in the properties file\n");
//		buf.append("\tinData : <the name of the corresponding resource as it apperas in the jdl> : <the path where the data that are to be moved to the ui is stored> : <local | ss | url where to retrieve the data from> (This property is optional)\n");
//		buf.append("\tinData : <the name of the corresponding resource as it apperas in the jdl> : <the path where the data that are to be moved to the ui is stored> : <local | ss | url where to retrieve the data from> (This property is optional)\n");
//		buf.append("\t[...]\n");
//		buf.append("}\n");
//		buf.append("2) the path of the output file that will contain the execution identifier\n");
//		logger.info(buf.toString());
//	}

//	@Override
//	public void execute(String[] args) {
//		logger.error("Not implemented yet...");
//	}

//	@Override
//	public void execute(String[] args) {
//		if(args.length!=2)
//		{
//			CondorAdaptor.PrintHelp();
//			return;
//		}
//		else
//		{
//			try {
//				CondorAdaptor.Init();
//			} catch (EnvironmentValidationException e) {
//				logger.error("Exception",e);
//			}
//		}
//		logger.info("resources file used : "+args[0]);
//		logger.info("output execution id : "+args[1]);
//		
//		CONDORParams params=new CONDORParams();
//		
//		CONDORConfig conf=new CONDORConfig();
//		try {
//			conf.setChokePerformanceEvents(CondorAdaptor.ParseBooleanProperty(args[0], "chokePerformanceEvents"));
//			conf.setChokeProgressEvents(CondorAdaptor.ParseBooleanProperty(args[0], "chokeProgressEvents"));
//			conf.setRetrieveJobClassAd(CondorAdaptor.ParseBooleanProperty(args[0], "retrieveJobClassAd"));
//			conf.setWaitPeriod(CondorAdaptor.ParseLongProperty(args[0], "pollPeriod"));
//			conf.setTimeout(CondorAdaptor.ParseLongProperty(args[0], "timeout"));
//			conf.setIsDag(CondorAdaptor.ParseBooleanProperty(args[0], "isDag"));
//			params.setConfig(conf);
//			params.setCondorResources(CondorAdaptor.GetResources(args[0]));
//			
//			String scope=CondorAdaptor.ParseScopeProperty(args[0]);
//			
//			logger.info("Locating Workflow Engine");
//			String endpoint=AdaptorBase.GetWorkflowEngineEndpoint(scope);
//			logger.info("Selected Workflow Engine "+endpoint);
//			WorkflowEngineServicePortType wf=AdaptorBase.GetWorkflowEnginePortType(scope,endpoint);
//			logger.info("Submiting execution");
//			String ExecutionID=wf.adaptCONDOR(params);
//			logger.info("Execution ID : "+ExecutionID);
//			AdaptorBase.WriteExecutionID(args[1], ExecutionID, endpoint,scope);
//		} catch (Exception e) {
//			logger.error("Exception",e);
//		}
//		
//	}
}
