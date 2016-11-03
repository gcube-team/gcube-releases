package org.gcube.execution.workflowengine.service.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.gcube.execution.workflowengine.service.stubs.AccessInfo;
import org.gcube.execution.workflowengine.service.stubs.GRIDConfig;
import org.gcube.execution.workflowengine.service.stubs.GRIDParams;
import org.gcube.execution.workflowengine.service.stubs.GRIDResource;
import org.gcube.execution.workflowengine.service.stubs.JDLResource;
import org.gcube.execution.workflowengine.service.stubs.WorkflowEngineServicePortType;
import org.gcube.execution.workflowengine.service.test.FileInfo.LocationType;

public class TestGridAdaptor extends TestAdaptorBase
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
	
	private static FileInfo ParseGlobalOutputStoreMode(String file) throws Exception
	{
		File f = new File(file);
		if(!f.exists() || !f.isFile()) throw new Exception("Specified resource file ("+file+") not found");
		FileInfo nfo = null;
		BufferedReader r = new BufferedReader(new FileReader(f));
		while(true)
		{
			String line=r.readLine();
			if(line==null) break;
			String parts[]=line.trim().split("\\s#\\s");
			if(parts.length != 2 && parts.length != 3 && parts.length != 5) continue;
			if(parts[0].trim().equalsIgnoreCase("outputStoreMode"))
			{
				nfo = new FileInfo();
				if(parts.length == 2 && parts[1].trim().equals(LocationType.ss.toString()))
					nfo.TypeOfLocation = LocationType.ss;
				else
				{
					nfo.TypeOfLocation=FileInfo.LocationType.valueOf(parts[1].trim());
					if(nfo.TypeOfLocation.equals(LocationType.url))
					{
						if(parts.length == 3) 
						{
							nfo.Value = TestAdaptorBase.StripUrlPort(TestAdaptorBase.StripUrlUserInfo(parts[2].trim()));
							nfo.AccessInfo = TestAdaptorBase.ParseUrlAccessInfo(parts[2].trim());
						}
						else 
						{
							nfo.Value = TestAdaptorBase.StripUrlPort(parts[2].trim());
							nfo.AccessInfo = TestAdaptorBase.ParseUrlAccessInfo(parts[2].trim());
							nfo.AccessInfo.userId = parts[3].trim();
							nfo.AccessInfo.password = parts[4].trim();
						}
					}
				}
			}
		}
		if(nfo == null)
		{
			nfo = new FileInfo();
			nfo.TypeOfLocation = LocationType.ss;
		}
		return nfo;
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
			if(!parts[0].trim().equalsIgnoreCase("inData")) continue;
			FileInfo nfo=new FileInfo();
			nfo.TypeOfLocation=FileInfo.LocationType.valueOf(parts[3].trim());
			nfo.Value=parts[2].trim();
			if(parts[0].trim().equalsIgnoreCase("inData")) resource.put(parts[1].trim(), nfo);
		}
		return resource;
	}
	
	private static HashMap<String, FileInfo> ParseOutData(String file, FileInfo globalOutputStoreMode) throws Exception
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
			if(parts.length<2) continue;
			if(!parts[0].trim().equalsIgnoreCase("outData")) continue;
			FileInfo nfo = null;
			if(parts.length == 2 && globalOutputStoreMode != null) 
			{ 
				nfo = new FileInfo(); 
				nfo.TypeOfLocation = globalOutputStoreMode.TypeOfLocation;
				nfo.Value = globalOutputStoreMode.Value;
			}
			if(parts.length == 3 && parts[2].equals(LocationType.ss.toString())) { nfo = new FileInfo(); nfo.TypeOfLocation = LocationType.ss; }
			else if(parts.length > 3) { nfo = new FileInfo(); nfo.TypeOfLocation=FileInfo.LocationType.valueOf(parts[3].trim()); }
			if(nfo.TypeOfLocation.equals(LocationType.url))
			{
				if(parts.length != 4 && parts.length != 6) continue;
				if(parts.length == 4)
				{
					nfo.Value = TestAdaptorBase.StripUrlPort(TestAdaptorBase.StripUrlUserInfo(parts[3].trim()));
					nfo.AccessInfo = TestAdaptorBase.ParseUrlAccessInfo(parts[3].trim());
				}
				else 
				{
					nfo.Value = TestAdaptorBase.StripUrlPort(parts[3].trim());
					nfo.AccessInfo = TestAdaptorBase.ParseUrlAccessInfo(parts[3].trim());
					nfo.AccessInfo.userId = parts[4].trim();
					nfo.AccessInfo.password = parts[5].trim();
				}
			}
			if(parts[0].trim().equalsIgnoreCase("outData")) resource.put(parts[1].trim(), nfo);
		}
		List<String> defaultInfo = new ArrayList<String>();
		for(Map.Entry<String, FileInfo> outData : resource.entrySet())
		{
			if(outData.getValue() == null) defaultInfo.add(outData.getKey());
		}
		for(String d : defaultInfo)
		{
			FileInfo nfo = new FileInfo();
			nfo.TypeOfLocation = LocationType.ss;
			resource.put(d, nfo);
		}
			
		return resource;
	}
	
	private static GRIDResource[] GetResources(String file) throws Exception
	{
		List<GRIDResource> resources=new ArrayList<GRIDResource>();
		String remoteValue=TestGridAdaptor.ParseRemoteValue(file, "jdl");
		String localValue=TestGridAdaptor.ParseLocalValue(file, "jdl");
		GRIDResource res= new GRIDResource();
		res.setResourceKey(remoteValue);
		res.setResourceType("JDL");
		res.setResourceAccess("InMessageString");
		res.setInMessageStringPayload(TestAdaptorBase.GetStringFilePayload(localValue));
		resources.add(res);
		remoteValue=TestGridAdaptor.ParseRemoteValue(file, "config");
		localValue=TestGridAdaptor.ParseLocalValue(file, "config");
		if(!(remoteValue==null || remoteValue.trim().length()==0 || localValue==null || localValue.trim().length()==0))
		{
			res= new GRIDResource();
			res.setResourceKey(remoteValue);
			res.setResourceType("Config");
			res.setResourceAccess("InMessageString");
			res.setInMessageStringPayload(TestAdaptorBase.GetStringFilePayload(localValue));
			resources.add(res);
		}
		remoteValue=TestGridAdaptor.ParseRemoteValue(file, "userProxy");
		localValue=TestGridAdaptor.ParseLocalValue(file, "userProxy");
		res= new GRIDResource();
		res.setResourceKey(remoteValue);
		res.setResourceType("UserProxy");
		res.setResourceAccess("InMessageBytes");
		res.setInMessageBytePayload(TestAdaptorBase.GetByteFilePayload(localValue));
		resources.add(res);
		FileInfo globalOutputStoreMode=TestGridAdaptor.ParseGlobalOutputStoreMode(file);
		HashMap<String, FileInfo> data1 =  TestGridAdaptor.ParseInData(file);
		for(Map.Entry<String, FileInfo> entry : data1.entrySet())
		{
			res= new GRIDResource();
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
					AccessInfo ai = new AccessInfo(entry.getValue().AccessInfo.password, new Integer(entry.getValue().AccessInfo.port).toString(),
							entry.getValue().AccessInfo.userId);
					res.setResourceAccessInfo(ai);
					break;
				}
			}
			resources.add(res);
		}
		HashMap<String, FileInfo> data2 =  TestGridAdaptor.ParseOutData(file, globalOutputStoreMode);
		for(Map.Entry<String, FileInfo> entry : data2.entrySet())
		{
			GRIDResource r=new GRIDResource();
			r.setResourceKey(entry.getKey());
			r.setResourceType("OutData");
			switch(entry.getValue().TypeOfLocation)
			{
				case ss:
				{
					r.setResourceReference(entry.getValue().Value);
					r.setResourceAccess("CMSReference");
					break;
				}
				case url:
				{
					r.setResourceReference(entry.getValue().Value);
					r.setResourceAccess("Reference");
					break;
				}
			}
			resources.add(r);
		}
		return resources.toArray(new GRIDResource[0]);
	}
	
	private static void PrintHelp()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("Usage:\n");
		buf.append("Two arguments are needed\n");
		buf.append("1) the path of the resource file. The syntax of the resource file is the following:\n");
		buf.append("\tscope : <the scope to use in case of a gcube environment> (optional)\n");
		buf.append("\ttimeout : <The time in milliseconds to wait for the job before candceliong it or negative for no timeout> (optional)\n");
		buf.append("\tpollPeriod : <The period in milliseconds to wait before checking the job status> (optional)\n");
		buf.append("\tchokeProgressEvents : <true | false> (depending on whether you want to omit progress reporting)\n");
		buf.append("\tchokePerformanceEvents : <true | false> (depending on whether you want to omit performance reporting)\n");
		buf.append("\tstorePlans : <true | false> (depending on whether you want the plan created and the final one to be stored for inspection)\n");
		buf.append("\tretryOnErrorPeriod : <The period in milliseconds to wait before rechecking the status of a job after an error occured> (optional)\n");
		buf.append("\tretryOnErrorTimes : <The times to retry rechecking the status of a job after an error occured> (optional)\n");
		buf.append("\tjdl : <the name that this file should have once moved to the ui node> : <path to the jdl file>\n");
		buf.append("\tuserProxy : <the name that this file should have once moved to the ui node> : <the path where the user proxy is stored in the local file system>\n");
		buf.append("\tconfig : <the name that this file should have once moved to the ui node> : <the path where the overriding config file is stored in the local file system> (This property is optional)\n");
		buf.append("{ these brackets indicate that the following element can be repeated as many times as needed and they neeed not be in the properties file\n");
		buf.append("\tinData : <the name of the corresponding resource as it apperas in the jdl> : <the path where the data that are to be moved to the ui is stored> : <local | ss | url where to retrieve the data from> (This property is optional)\n");
		buf.append("\tinData : <the name of the corresponding resource as it apperas in the jdl> : <the path where the data that are to be moved to the ui is stored> : <local | ss | url where to retrieve the data from> (This property is optional)\n");
		buf.append("\t[...]\n");
		buf.append("}\n");
		buf.append("{ these brackets indicate that the following element can be repeated as many times as needed and they neeed not be in the properties file\n");
		buf.append("\toutData : <the name of the data that are to be retrieved from the grid output as defined in the jdl> (This property is optional)\n");
		buf.append("\toutData : <the name of the data that are to be retrieved from the grid output as defined in the jdl> (This property is optional)\n");
		buf.append("\t[...]\n");
		buf.append("}\n");
		buf.append("2) the path of the output file that will contain the execution identifier\n");
		System.out.println(buf.toString());
	}

	public static void main(String []args) throws Exception
	{
		if(args.length!=2)
		{
			TestGridAdaptor.PrintHelp();
			return;
		}
		else
		{
			TestGridAdaptor.Init();
		}
		System.out.println("resources file used : "+args[0]);
		System.out.println("output execution id : "+args[1]);
		
		GRIDParams params=new GRIDParams();
		
		GRIDConfig conf=new GRIDConfig();
		conf.setChokePerformanceEvents(TestGridAdaptor.ParseBooleanProperty(args[0], "chokePerformanceEvents"));
		conf.setChokeProgressEvents(TestGridAdaptor.ParseBooleanProperty(args[0], "chokeProgressEvents"));
		conf.setRetryOnErrorPeriod(TestGridAdaptor.ParseLongProperty(args[0], "retryOnErrorPeriod"));
		conf.setRetryOnErrorTimes((int)TestGridAdaptor.ParseLongProperty(args[0], "retryOnErrorTimes"));
		conf.setTimeout(TestGridAdaptor.ParseLongProperty(args[0], "timeout"));
		conf.setWaitPeriod(TestGridAdaptor.ParseLongProperty(args[0], "pollPeriod"));
		params.setConfig(conf);
		params.setGridResources(TestGridAdaptor.GetResources(args[0]));
		
		String scope=TestGridAdaptor.ParseScopeProperty(args[0]);
		
		System.out.println("Locating Workflow Engine");
		String endpoint=TestAdaptorBase.GetWorkflowEngineEndpoint(scope);
		System.out.println("Selected Workflow Engine "+endpoint);
		WorkflowEngineServicePortType wf=TestAdaptorBase.GetWorkflowEnginePortType(scope,endpoint);
		System.out.println("Submiting execution");
		String ExecutionID=wf.adaptGRID(params);
		System.out.println("Execution ID : "+ExecutionID);
		TestAdaptorBase.WriteExecutionID(args[1], ExecutionID, endpoint,scope);
	}
}
