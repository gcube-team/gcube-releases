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
import org.gcube.execution.workflowengine.service.stubs.JDLConfig;
import org.gcube.execution.workflowengine.service.stubs.JDLParams;
import org.gcube.execution.workflowengine.service.stubs.JDLResource;
import org.gcube.execution.workflowengine.service.stubs.WorkflowEngineServicePortType;
import org.gcube.execution.workflowengine.service.test.FileInfo.LocationType;

public class TestJDLAdaptor extends TestAdaptorBase
{
	private static HashMap<String,String> ParseResourceFile(String file) throws Exception
	{
		File f =new File(file);
		if(!f.exists() || !f.isFile()) throw new Exception("Specified resource file ("+file+") not found");
		BufferedReader r=new BufferedReader(new FileReader(f));
		HashMap<String,String> res=new HashMap<String, String>();
		while(true)
		{
			String line=r.readLine();
			if(line==null) break;
			String parts[]=line.trim().split("\\s#\\s");
			if(parts.length!=2) continue;
			res.put(parts[0].trim(), parts[1].trim());
		}
		return res;
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
			nfo.TypeOfLocation=FileInfo.LocationType.valueOf(parts[2].trim());
			nfo.Value=parts[3].trim();
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
			else if(parts.length > 3) { nfo = new FileInfo(); nfo.TypeOfLocation=FileInfo.LocationType.valueOf(parts[2].trim()); }
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
	
	private static HashMap<String,FileInfo> ParseFileResourceFile(String file) throws Exception
	{
		File f =new File(file);
		if(!f.exists() || !f.isFile()) throw new Exception("Specified resource file ("+file+") not found");
		BufferedReader r=new BufferedReader(new FileReader(f));
		HashMap<String,FileInfo> res=new HashMap<String, FileInfo>();
		while(true)
		{
			String line=r.readLine();
			if(line==null) break;
			String parts[]=line.trim().split("\\s#\\s");
			if(parts[1].equals("ftp"))
			{
				if(parts.length!=5) continue;
			}else
				if(parts.length!=2) continue;
			if(parts.length!=3) continue;
			FileInfo nfo=new FileInfo();
			nfo.TypeOfLocation=FileInfo.LocationType.valueOf(parts[1].trim().toLowerCase());
			nfo.Value=parts[2];
			res.put(parts[0].trim(), nfo);
		}
		return res;
	}
	
	private static void PrintHelp()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("Usage:\n");
		buf.append("Two arguments are needed\n");
		buf.append("1) the path of the resource file. The syntax of the resource file is the following:\n");
		buf.append("\tscope : <the scope to use>\n");
		buf.append("\tjdl : <path to the jdl file>\n");
		buf.append("\tchokeProgressEvents : <true | false> (depending on whether you want to omit progress reporting)\n");
		buf.append("\tchokePerformanceEvents : <true | false> (depending on whether you want to omit performance reporting)\n");
		buf.append("\tstorePlans : <true | false> (depending on whether you want the plan created and the final one to be stored for inspection)\n");
		buf.append("\t<name of resource as mentioned in jdl> : <local | ss | url depending on where to access the payload from> : <the path / id / url to retrieve the paylaod from>\n");
		buf.append("\t<name of resource as mentioned in jdl> : <local | ss | url depending on where to access the payload from> : <the paath / id / url to retrieve the paylaod from>\n");
		buf.append("\t[...]");
		buf.append("2) the path of the output file that will contain the execution identifier\n");
		System.out.println(buf.toString());
	}

	public static void main(String []args) throws Exception
	{
		if(args.length!=2)
		{
			TestJDLAdaptor.PrintHelp();
			return;
		}
		else
		{
			TestJDLAdaptor.Init();
		}
		System.out.println("resources file used : "+args[0]);
		System.out.println("output execution id : "+args[1]);
		HashMap<String, String> resources=TestJDLAdaptor.ParseResourceFile(args[0]);
		FileInfo globalOutputStoreMode=TestJDLAdaptor.ParseGlobalOutputStoreMode(args[0]);
		HashMap<String, FileInfo> inDataResources=TestJDLAdaptor.ParseInData(args[0]);
		HashMap<String, FileInfo> outDataResources=TestJDLAdaptor.ParseOutData(args[0], globalOutputStoreMode);
		//HashMap<String, FileInfo> fileresources=TestJDLAdaptor.ParseFileResourceFile(args[0]);
		JDLParams params=new JDLParams();

		JDLConfig conf=new JDLConfig();
		if(resources.containsKey("chokePerformanceEvents")) conf.setChokePerformanceEvents(Boolean.parseBoolean(resources.get("chokePerformanceEvents")));
		else conf.setChokePerformanceEvents(false);
		if(resources.containsKey("chokeProgressEvents")) conf.setChokeProgressEvents(Boolean.parseBoolean(resources.get("chokeProgressEvents")));
		else conf.setChokeProgressEvents(false);
		params.setConfig(conf);
		
		params.setJdlDescription(TestAdaptorBase.GetStringFilePayload(resources.get("jdl")));
		
		List<JDLResource> resourceslst=new ArrayList<JDLResource>();
		for(Map.Entry<String, FileInfo> res : inDataResources.entrySet())
		{
			JDLResource r=new JDLResource();
			r.setResourceType("InData");
			r.setResourceKey(res.getKey());
			switch(res.getValue().TypeOfLocation)
			{
				case local:
				{
					r.setInMessageBytePayload(TestAdaptorBase.GetByteFilePayload(res.getValue().Value));
					r.setResourceAccess("InMessageBytes");
					break;
				}
				case ss:
				{
					r.setResourceReference(res.getValue().Value);
					r.setResourceAccess("CMSReference");
					break;
				}
				case url:
				{
					r.setResourceReference(res.getValue().Value);
					r.setResourceAccess("Reference");
					break;
				}
			}
			resourceslst.add(r);
		}
		
		for(Map.Entry<String, FileInfo> res : outDataResources.entrySet())
		{
			JDLResource r=new JDLResource();
			r.setResourceKey(res.getKey());
			r.setResourceType("OutData");
			switch(res.getValue().TypeOfLocation)
			{
				case ss:
				{
					r.setResourceReference(res.getValue().Value);
					r.setResourceAccess("CMSReference");
					break;
				}
				case url:
				{
					r.setResourceReference(res.getValue().Value);
					r.setResourceAccess("Reference");
					AccessInfo ai = new AccessInfo(res.getValue().AccessInfo.password, new Integer(res.getValue().AccessInfo.port).toString(),
							res.getValue().AccessInfo.userId);
					r.setResourceAccessInfo(ai);
					break;
				}
			}
			resourceslst.add(r);
		}
		params.setJdlResources(resourceslst.toArray(new JDLResource[0]));
		
		System.out.println("Locating Workflow Engine");
		String endpoint=TestAdaptorBase.GetWorkflowEngineEndpoint(resources.get("scope"));
		System.out.println("Selected Workflow Engine "+endpoint);
		WorkflowEngineServicePortType wf=TestAdaptorBase.GetWorkflowEnginePortType(resources.get("scope"),endpoint);
		System.out.println("Submiting execution");
		String ExecutionID=wf.adaptJDL(params);
		System.out.println("Execution ID : "+ExecutionID);
		TestAdaptorBase.WriteExecutionID(args[1], ExecutionID, endpoint,resources.get("scope"));
	}
}