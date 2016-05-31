package gr.uoa.di.madgik.workflow.test;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.engine.ExecutionEngine;
import gr.uoa.di.madgik.workflow.adaptor.WorkflowJDLAdaptor;
import gr.uoa.di.madgik.workflow.adaptor.utils.IOutputResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.AdaptorJDLResources;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.AttachedJDLResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.AttachedJDLResource.AttachedResourceType;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.AttachedJDLResource.ResourceType;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.OutputSandboxJDLResource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.UUID;

public class TestJDLAdaptor extends TestAdaptorBase implements Observer
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
			String parts[]=line.trim().split("\\s:\\s");
			if(parts.length!=2) continue;
			res.put(parts[0].trim(), parts[1].trim());
		}
		return res;
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
			String parts[]=line.trim().split("\\s:\\s");
			if(parts.length!=3) continue;
			FileInfo nfo=new FileInfo();
			nfo.TypeOfLocation=FileInfo.LocationType.valueOf(parts[1].trim().toLowerCase());
			nfo.Value=parts[2];
			res.put(parts[0].trim(), nfo);
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
			String parts[]=line.trim().split("\\s:\\s");
			if(parts.length != 2 && parts.length != 5) continue;
			if(parts[0].trim().equalsIgnoreCase("outputStoreMode"))
			{
				nfo = new FileInfo();
				if(parts.length == 2 && parts[1].trim().equals(FileInfo.LocationType.ss))
					nfo.TypeOfLocation = FileInfo.LocationType.ss;
				else
				{
					nfo.TypeOfLocation=FileInfo.LocationType.valueOf(parts[3].trim());
					if(nfo.TypeOfLocation.equals(FileInfo.LocationType.url))
					{
						if(parts.length == 4) nfo.Value = parts[3].trim();
						else 
						{
							if(parts[3].trim().startsWith("ftp://"))
								nfo.Value = "ftp://" + parts[4].trim() + ":" + parts[5].trim() + "@" + parts[3].trim();
						}
					}
				}
			}
		}
		if(nfo == null)
		{
			nfo = new FileInfo();
			nfo.TypeOfLocation = FileInfo.LocationType.ss;
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
			String parts[]=line.trim().split("\\s:\\s");
			if(parts.length!=4) continue;
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
			String parts[]=line.trim().split("\\s:\\s");
			if(parts.length<2) continue;
			FileInfo nfo = null;
			if(parts.length == 2 && globalOutputStoreMode != null) 
			{ 
				nfo = new FileInfo(); 
				nfo.TypeOfLocation = globalOutputStoreMode.TypeOfLocation;
				nfo.Value = globalOutputStoreMode.Value;
			}
			if(parts.length == 3 && parts[2].equals(FileInfo.LocationType.ss)) { nfo = new FileInfo(); nfo.TypeOfLocation = FileInfo.LocationType.ss; }
			else if(parts.length > 3) { nfo = new FileInfo(); nfo.TypeOfLocation=FileInfo.LocationType.valueOf(parts[3].trim()); }
			if(nfo.TypeOfLocation.equals(FileInfo.LocationType.url))
			{
				if(parts.length != 4 && parts.length != 6) continue;
				if(parts.length == 4) nfo.Value = parts[3].trim();
				else 
				{
					if(parts[3].trim().startsWith("ftp://"))
						nfo.Value = "ftp://" + parts[4].trim() + ":" + parts[5].trim() + "@" + parts[3].trim().substring("ftp://".length());
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
			nfo.TypeOfLocation = FileInfo.LocationType.ss;
			resource.put(d, nfo);
		}
			
		return resource;
	}
	
	private static void PrintHelp()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("Usage:\n");
		buf.append("Four arguments are needed\n");
		buf.append("1) first the hostname of the machine that is running this code through which it can be reached\n");
		buf.append("2) a port that can be used by the tcp server or <=0 to use a random one\n");
		buf.append("3) the path of the resource file. The syntax of the resource file is the following:\n");
		buf.append("\tscope : <the scope to use in case of a gcube environment>\n");
		buf.append("\tjdl : <path to the jdl file>\n");
		buf.append("\tchokeProgressEvents : <true | false> (depending on whether you want to omit progress reporting)\n");
		buf.append("\tchokePerformanceEvents : <true | false> (depending on whether you want to omit performance reporting)\n");
		buf.append("\tstorePlans : <true | false> (depending on whether you want the plan created and the final one to be stored for inspection)\n");
		buf.append("\t<name of resource as mentioned in jdl> : <the path where this resource is stored in the local filesystem>\n");
		buf.append("\t<name of resource as mentioned in jdl> : <the path where this resource is stored in the local filesystem>\n");
		buf.append("\t[...]");
		buf.append("4) <ftp | gcube> depending on the targeted infrastructure\n");
		System.out.println(buf.toString());
	}

	public static void main(String []args) throws Exception
	{
		if(args.length!=4)
		{
			TestJDLAdaptor.PrintHelp();
			return;
		}
		else
		{
			String hostName=args[0];
			int port=Integer.parseInt(args[1]);
			TestJDLAdaptor.Init(hostName, port,args[3]);
		}
		HashMap<String, String> resources=TestJDLAdaptor.ParseResourceFile(args[2]);
		FileInfo globalOutputStoreMode=TestJDLAdaptor.ParseGlobalOutputStoreMode(args[2]);
		HashMap<String, FileInfo> inDataResources=TestJDLAdaptor.ParseInData(args[2]);
		HashMap<String, FileInfo> outDataResources=TestJDLAdaptor.ParseOutData(args[2], globalOutputStoreMode);
		
		AdaptorJDLResources attachedResources=new AdaptorJDLResources();
		for(Map.Entry<String, String> res : resources.entrySet())
		{
			if(res.getKey().equals("jdl")) continue;
			else if(res.getKey().equals("chokeProgressEvents")) continue;
			else if(res.getKey().equals("chokePerformanceEvents")) continue;
			else if(res.getKey().equals("storePlans")) continue;
			else if(res.getKey().equals("scope")) continue;
		}
		
		if(!resources.containsKey("jdl")) throw new Exception("no jdl attribute specified");
		if(!resources.containsKey("chokeProgressEvents")) throw new Exception("no chokeProgressEvents attribute specified");
		if(!resources.containsKey("chokePerformanceEvents")) throw new Exception("no chokePerformanceEvents attribute specified");
		if(!resources.containsKey("storePlans")) throw new Exception("no chokePerformanceEvents attribute specified");
		
		for(Map.Entry<String, FileInfo> res : inDataResources.entrySet())
		{
			AttachedResourceType type = null;
			switch(res.getValue().TypeOfLocation)
			{
				case local:
				{
					type = AttachedResourceType.LocalFile;
					break;
				}
				case ss:
				{
					type = AttachedResourceType.CMSReference;
					break;
				}
				case url:
				{
					type = AttachedResourceType.Reference;
					break;
				}
			}
			attachedResources.Resources.add(new AttachedJDLResource(res.getKey(), ResourceType.InData, res.getValue().Value, type));
		}
		
		for(Map.Entry<String, FileInfo> res : outDataResources.entrySet())
		{
			AttachedResourceType type = null;
			switch(res.getValue().TypeOfLocation)
			{
				case ss:
				{
					type = AttachedResourceType.CMSReference;
					break;
				}
				case url:
				{
					type = AttachedResourceType.Reference;
					break;
				}
			}
			attachedResources.Resources.add(new AttachedJDLResource(res.getKey(), ResourceType.OutData, res.getValue().Value, type));
		}
		
		WorkflowJDLAdaptor adaptor=new WorkflowJDLAdaptor();
		if(resources.containsKey("scope")) adaptor.ConstructEnvironmentHints(resources.get("scope"));
		adaptor.SetAdaptorResources(attachedResources);
		adaptor.SetJDL(new File(resources.get("jdl")));
		adaptor.CreatePlan();
		adaptor.GetCreatedPlan().Config.ChokeProgressReporting=Boolean.parseBoolean(resources.get("chokeProgressEvents"));
		adaptor.GetCreatedPlan().Config.ChokePerformanceReporting=Boolean.parseBoolean(resources.get("chokePerformanceEvents"));
		
		if(Boolean.parseBoolean(resources.get("storePlans")))
		{
			File tmp=File.createTempFile(UUID.randomUUID().toString(), ".test.jdl.adaptor.original.plan.xml");
			XMLUtils.Serialize(tmp.toString(), adaptor.GetCreatedPlan().Serialize());
			logger.info("Initial plan is stored at "+tmp.toString());
		}
		
		Handle= ExecutionEngine.Submit(adaptor.GetCreatedPlan());
		TestJDLAdaptor test=new TestJDLAdaptor();
		Handle.RegisterObserver(test);
		synchronized (synchCompletion)
		{
			ExecutionEngine.Execute(Handle);
			try{synchCompletion.wait();}catch(Exception ex){}
		}
		
		if(Boolean.parseBoolean(resources.get("storePlans")))
		{
			File tmp=File.createTempFile(UUID.randomUUID().toString(), ".test.jdl.adaptor.final.plan.xml");
			XMLUtils.Serialize(tmp.toString(), adaptor.GetCreatedPlan().Serialize());
			logger.info("Final plan is stored at "+tmp.toString());
		}
		
		TestJDLAdaptor.EvaluateResult();

		for(IOutputResource res : adaptor.GetOutput())
		{
			if(!(res instanceof OutputSandboxJDLResource)) throw new Exception("Different type found");
			logger.info("Output file of node "+((OutputSandboxJDLResource)res).NodeName+" with jdl name : "+((OutputSandboxJDLResource)res).SandboxName+" is stored at "+TestJDLAdaptor.GetStoredFilePayload(((OutputSandboxJDLResource)res).VariableID,"jdl"));
		}
	}
}
