package org.gcube.execution.workflowengine.service.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gcube.execution.workflowengine.service.stubs.CONDORConfig;
import org.gcube.execution.workflowengine.service.stubs.CONDORParams;
import org.gcube.execution.workflowengine.service.stubs.CONDORResource;
import org.gcube.execution.workflowengine.service.stubs.WorkflowEngineServicePortType;

public class TestCondorAdaptor extends TestAdaptorBase
{
	private static long ParseLongProperty(String file, String key) throws Exception
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
	
	private static String ParseScopeProperty(String file) throws Exception
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
			if(parts[0].trim().equalsIgnoreCase("scope")) return parts[1].trim();
		}
		return null;
	}
	
	private static boolean ParseBooleanProperty(String file,String key) throws Exception
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
	
	private static CONDORResource[] GetResources(String file) throws Exception
	{
		List<CONDORResource> resources=new ArrayList<CONDORResource>();
		String remoteValue=TestCondorAdaptor.ParseRemoteValue(file, "submit");
		String localValue=TestCondorAdaptor.ParseLocalValue(file, "submit");
		CONDORResource res= new CONDORResource();
		res.setResourceKey(remoteValue);
		res.setResourceType("Submit");
		res.setResourceAccess("InMessageString");
		res.setInMessageStringPayload(TestAdaptorBase.GetStringFilePayload(localValue));
		resources.add(res);
		HashMap<String, FileInfo> data1 =  TestCondorAdaptor.ParseInData(file);
		for(Map.Entry<String, FileInfo> entry : data1.entrySet())
		{
			res= new CONDORResource();
			res.setResourceKey(entry.getKey());
			res.setResourceType("InData");
			switch(entry.getValue().TypeOfLocation)
			{
				case local:
				{
					res.setInMessageBytePayload(TestAdaptorBase.GetByteFilePayload(entry.getValue().Value));
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
		HashMap<String, FileInfo> data2 =  TestCondorAdaptor.ParseExecutable(file);
		for(Map.Entry<String, FileInfo> entry : data2.entrySet())
		{
			res= new CONDORResource();
			res.setResourceKey(entry.getKey());
			res.setResourceType("Executable");
			switch(entry.getValue().TypeOfLocation)
			{
				case local:
				{
					res.setInMessageBytePayload(TestAdaptorBase.GetByteFilePayload(entry.getValue().Value));
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
	
	private static void PrintHelp()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("Usage:\n");
		buf.append("Two arguments are needed\n");
		buf.append("1) the path of the resource file. The syntax of the resource file is the following:\n");
		buf.append("\tscope : <the scope to use in case of a gcube environment> (optional)\n");
		buf.append("\tchokeProgressEvents : <true | false> (depending on whether you want to omit progress reporting)\n");
		buf.append("\tchokePerformanceEvents : <true | false> (depending on whether you want to omit performance reporting)\n");
		buf.append("\tisDAG : <true | false> (depending on whether the submited job is a dag or not)\n");
		buf.append("\tretrieveJobClassAd : <true | false> (depending on whether the job class ad should be retrieved upon status check or not)\n");
		buf.append("\ttimeout : <The time in milliseconds to wait for the job before candceling it or negative for no timeout> (optional)\n");
		buf.append("\tpollPeriod : <The period in milliseconds to wait before checking the job status> (optional)\n");
		buf.append("\tstorePlans : <true | false> (depending on whether you want the plan created and the final one to be stored for inspection)\n");
		buf.append("\tsubmit : <the name that this file should have once moved to the ui node> : <path to the submit file>\n");
		buf.append("{ these brackets indicate that the following element can be repeated as many times as needed and they neeed not be in the properties file\n");
		buf.append("\tinData : <the name of the corresponding resource as it apperas in the jdl> : <the path where the data that are to be moved to the ui is stored> : <local | ss | url where to retrieve the data from> (This property is optional)\n");
		buf.append("\tinData : <the name of the corresponding resource as it apperas in the jdl> : <the path where the data that are to be moved to the ui is stored> : <local | ss | url where to retrieve the data from> (This property is optional)\n");
		buf.append("\t[...]\n");
		buf.append("}\n");
		buf.append("2) the path of the output file that will contain the execution identifier\n");
		System.out.println(buf.toString());
	}

	public static void main(String []args) throws Exception
	{
		if(args.length!=2)
		{
			TestCondorAdaptor.PrintHelp();
			return;
		}
		else
		{
			TestCondorAdaptor.Init();
		}
		System.out.println("resources file used : "+args[0]);
		System.out.println("output execution id : "+args[1]);
		
		CONDORParams params=new CONDORParams();
		
		CONDORConfig conf=new CONDORConfig();
		conf.setChokePerformanceEvents(TestCondorAdaptor.ParseBooleanProperty(args[0], "chokePerformanceEvents"));
		conf.setChokeProgressEvents(TestCondorAdaptor.ParseBooleanProperty(args[0], "chokeProgressEvents"));
		conf.setRetrieveJobClassAd(TestCondorAdaptor.ParseBooleanProperty(args[0], "retrieveJobClassAd"));
		conf.setWaitPeriod(TestCondorAdaptor.ParseLongProperty(args[0], "pollPeriod"));
		conf.setTimeout(TestCondorAdaptor.ParseLongProperty(args[0], "timeout"));
		conf.setIsDag(TestCondorAdaptor.ParseBooleanProperty(args[0], "isDag"));
		params.setConfig(conf);
		params.setCondorResources(TestCondorAdaptor.GetResources(args[0]));
		
		String scope=TestCondorAdaptor.ParseScopeProperty(args[0]);
		
		System.out.println("Locating Workflow Engine");
		String endpoint=TestAdaptorBase.GetWorkflowEngineEndpoint(scope);
		System.out.println("Selected Workflow Engine "+endpoint);
		WorkflowEngineServicePortType wf=TestAdaptorBase.GetWorkflowEnginePortType(scope,endpoint);
		System.out.println("Submiting execution");
		String ExecutionID=wf.adaptCONDOR(params);
		System.out.println("Execution ID : "+ExecutionID);
		TestAdaptorBase.WriteExecutionID(args[1], ExecutionID, endpoint,scope);
	}
}
