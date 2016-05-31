package gr.uoa.di.madgik.workflow.test;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.engine.ExecutionEngine;
import gr.uoa.di.madgik.workflow.adaptor.WorkflowGridAdaptor;
import gr.uoa.di.madgik.workflow.adaptor.utils.IOutputResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.grid.AdaptorGridResources;
import gr.uoa.di.madgik.workflow.adaptor.utils.grid.AttachedGridResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.grid.AttachedGridResource.AttachedResourceType;
import gr.uoa.di.madgik.workflow.adaptor.utils.grid.OutputSandboxGridResource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.UUID;

public class TestGridAdaptor extends TestAdaptorBase implements Observer
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
			String parts[]=line.trim().split("\\s:\\s");
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
			String parts[]=line.trim().split("\\s:\\s");
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
			String parts[]=line.trim().split("\\s:\\s");
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
			String parts[]=line.trim().split("\\s:\\s");
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
			String parts[]=line.trim().split("\\s:\\s");
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
		buf.append("\tinData : <the name of the corresponding resource as it apperas in the jdl> : <the path where the data that are to be moved to the ui is stored in the local file system> (This property is optional)\n");
		buf.append("\tinData : <the name of the corresponding resource as it apperas in the jdl> : <the path where the data that are to be moved to the ui is stored in the local file system> (This property is optional)\n");
		buf.append("\t[...]\n");
		buf.append("}\n");
		buf.append("{ these brackets indicate that the following element can be repeated as many times as needed and they neeed not be in the properties file\n");
		buf.append("\toutData : <the name of the data that are to be retrieved from the grid output as defined in the jdl> (This property is optional)\n");
		buf.append("\toutData : <the name of the data that are to be retrieved from the grid output as defined in the jdl> (This property is optional)\n");
		buf.append("\t[...]\n");
		buf.append("}\n");
		buf.append("4) <ftp | gcube> depending on the targeted infrastructure\n");
		System.out.println(buf.toString());
	}

	public static void main(String []args) throws Exception
	{
		if(args.length!=4)
		{
			TestGridAdaptor.PrintHelp();
			return;
		}
		else
		{
			String hostName=args[0];
			int port=Integer.parseInt(args[1]);
			TestGridAdaptor.Init(hostName, port,args[3]);
		}
		AdaptorGridResources attachedResources=new AdaptorGridResources();
		String remoteValue=TestGridAdaptor.ParseRemoteValue(args[2], "jdl");
		String localValue=TestGridAdaptor.ParseLocalValue(args[2], "jdl");
		if(remoteValue==null || remoteValue.trim().length()==0 || localValue==null || localValue.trim().length()==0) throw new Exception("Resource file incomplete");
		attachedResources.Resources.add(new AttachedGridResource(remoteValue,localValue,AttachedGridResource.ResourceType.JDL));
		remoteValue=TestGridAdaptor.ParseRemoteValue(args[2], "config");
		localValue=TestGridAdaptor.ParseLocalValue(args[2], "config");
		if(!(remoteValue==null || remoteValue.trim().length()==0 || localValue==null || localValue.trim().length()==0))
		{
			attachedResources.Resources.add(new AttachedGridResource(remoteValue,localValue,AttachedGridResource.ResourceType.Config));
		}
		remoteValue=TestGridAdaptor.ParseRemoteValue(args[2], "userProxy");
		localValue=TestGridAdaptor.ParseLocalValue(args[2], "userProxy");
		if(remoteValue==null || remoteValue.trim().length()==0 || localValue==null || localValue.trim().length()==0) throw new Exception("Resource file incomplete");
		attachedResources.Resources.add(new AttachedGridResource(remoteValue,localValue,AttachedGridResource.ResourceType.UserProxy));
		HashMap<String, FileInfo> data1 =  TestGridAdaptor.ParseInData(args[2]);
		for(Map.Entry<String, FileInfo> entry : data1.entrySet())
		{
			AttachedResourceType type = null;
			switch(entry.getValue().TypeOfLocation)
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
			attachedResources.Resources.add(new AttachedGridResource(entry.getKey(),entry.getValue().Value,AttachedGridResource.ResourceType.InData,type));
		}
		FileInfo globalOutputStoreMode = TestGridAdaptor.ParseGlobalOutputStoreMode(args[2]);
		HashMap<String, FileInfo> data2 = TestGridAdaptor.ParseOutData(args[2], globalOutputStoreMode);
		for(Map.Entry<String, FileInfo> entry: data2.entrySet())
		{
			AttachedResourceType type = null;
			switch(entry.getValue().TypeOfLocation)
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
			attachedResources.Resources.add(new AttachedGridResource(entry.getKey(),entry.getValue().Value,AttachedGridResource.ResourceType.OutData, type));
		}
		String scopeProp=TestGridAdaptor.ParseScopeProperty(args[2]);
		if(scopeProp!=null)attachedResources.Resources.add(new AttachedGridResource(scopeProp,scopeProp,AttachedGridResource.ResourceType.Scope));

		WorkflowGridAdaptor adaptor=new WorkflowGridAdaptor();
		if(TestGridAdaptor.ParseLongProperty(args[2], "timeout")>0) adaptor.Timeout=TestGridAdaptor.ParseLongProperty(args[2], "timeout");
		if(TestGridAdaptor.ParseLongProperty(args[2], "pollPeriod")>0) adaptor.WaitPeriod=TestGridAdaptor.ParseLongProperty(args[2], "pollPeriod");
		if(TestGridAdaptor.ParseLongProperty(args[2], "retryOnErrorPeriod")>0) adaptor.RetryOnErrorPeriod=TestGridAdaptor.ParseLongProperty(args[2], "retryOnErrorPeriod");
		if(TestGridAdaptor.ParseLongProperty(args[2], "retryOnErrorTimes")>0) adaptor.RetryOnErrorTimes=(int)TestGridAdaptor.ParseLongProperty(args[2], "retryOnErrorTimes");
		
		
		adaptor.SetAdaptorResources(attachedResources);
		adaptor.CreatePlan();
		adaptor.GetCreatedPlan().Config.ChokeProgressReporting=TestGridAdaptor.ParseBooleanProperty(args[2], "chokeProgressEvents");
		adaptor.GetCreatedPlan().Config.ChokePerformanceReporting=TestGridAdaptor.ParseBooleanProperty(args[2], "chokePerformanceEvents");
		adaptor.Timeout=TestGridAdaptor.ParseLongProperty(args[2], "timout");
		adaptor.WaitPeriod=TestGridAdaptor.ParseLongProperty(args[2], "pollPeriod");
		
		if(TestGridAdaptor.ParseBooleanProperty(args[2], "storePlans"))
		{
			File tmp=File.createTempFile(UUID.randomUUID().toString(), ".test.grid.adaptor.original.plan.xml");
			XMLUtils.Serialize(tmp.toString(), adaptor.GetCreatedPlan().Serialize());
			logger.info("Initial plan is stored at "+tmp.toString());
		}
		
		Handle= ExecutionEngine.Submit(adaptor.GetCreatedPlan());
		TestGridAdaptor test=new TestGridAdaptor();
		Handle.RegisterObserver(test);
		synchronized (synchCompletion)
		{
			ExecutionEngine.Execute(Handle);
			try {
				synchCompletion.wait();
			}
			catch(Exception ex){}
		}
		
		if(TestGridAdaptor.ParseBooleanProperty(args[2], "storePlans"))
		{
			File tmp=File.createTempFile(UUID.randomUUID().toString(), ".test.grid.adaptor.final.plan.xml");
			XMLUtils.Serialize(tmp.toString(), adaptor.GetCreatedPlan().Serialize());
			logger.info("Final plan is stored at "+tmp.toString());
		}
		
		if(TestGridAdaptor.EvaluateResult())
		{
			for(IOutputResource res : adaptor.GetOutput())
			{
				if(!(res instanceof OutputSandboxGridResource)) throw new Exception("Different type found");
				logger.info("Output file "+((OutputSandboxGridResource)res).Key+" is stored at "+TestGridAdaptor.GetStoredFilePayload(((OutputSandboxGridResource)res).VariableID,"grid"));
			}
		}
	}
}
