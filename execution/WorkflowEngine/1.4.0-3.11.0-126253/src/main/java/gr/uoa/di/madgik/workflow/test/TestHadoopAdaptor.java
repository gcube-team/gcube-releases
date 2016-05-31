package gr.uoa.di.madgik.workflow.test;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.engine.ExecutionEngine;
import gr.uoa.di.madgik.workflow.adaptor.WorkflowHadoopAdaptor;
import gr.uoa.di.madgik.workflow.adaptor.utils.IOutputResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.hadoop.AdaptorHadoopResources;
import gr.uoa.di.madgik.workflow.adaptor.utils.hadoop.AttachedHadoopResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.hadoop.AttachedHadoopResource.AttachedResourceType;
import gr.uoa.di.madgik.workflow.adaptor.utils.hadoop.OutputHadoopResource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Observer;
import java.util.Set;
import java.util.UUID;

public class TestHadoopAdaptor extends TestAdaptorBase implements Observer
{
	
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
	
	private static Set<AttachedHadoopResource> ParseResources(String file, FileInfo globalOutputStoreMode) throws Exception
	{
		Set<AttachedHadoopResource> resources=new HashSet<AttachedHadoopResource>();
		File f =new File(file);
		if(!f.exists() || !f.isFile()) throw new Exception("Specified resource file ("+file+") not found");
		BufferedReader r=new BufferedReader(new FileReader(f));
		int order=0;
		while(true)
		{
			order+=1;
			String line=r.readLine();
			if(line==null) break;
			String parts[]=line.trim().split("\\s:\\s");
			AttachedHadoopResource.ResourceType t;
			try
			{
				t=AttachedHadoopResource.ResourceType.valueOf(parts[0].trim());
			}catch(Exception ex)
			{
				continue;
			}
			switch(t)
			{
				case Argument:
				{
					if(parts.length!=3) throw new Exception("different info expected for "+t.toString());
					AttachedHadoopResource res=new AttachedHadoopResource();
					resources.add(res);
					res.Key=parts[2].trim();
					res.IsHDFSPresent=false;
					res.Order=Integer.parseInt(parts[1].trim());
					res.StorageSystemID=null;
					res.TypeOfResource=t;
					res.Value=parts[2].trim();
					break;
				}
				case Jar:
				case Configuration:
				case Lib:
				case Archive:
				case File:
				{
					if(parts.length!=4) throw new Exception("different info expected for "+t.toString());
					AttachedHadoopResource res=new AttachedHadoopResource();
					resources.add(res);
					res.Key=parts[1].trim();
					res.IsHDFSPresent=(parts[2].trim().equalsIgnoreCase("hdfs") ? true : false);
					res.Order=order;
					res.StorageSystemID=null;
					res.TypeOfResource=t;
					res.Value=parts[3].trim();
					break;
				}
				case MainClass:
				case Property:
				{
					if(parts.length!=2) throw new Exception("different info expected for "+t.toString());
					AttachedHadoopResource res=new AttachedHadoopResource();
					resources.add(res);
					res.Key=parts[1].trim();
					res.IsHDFSPresent=false;
					res.Order=order;
					res.StorageSystemID=null;
					res.TypeOfResource=t;
					res.Value=parts[1].trim();
					break;
				}
				case Scope:
				{
					if(parts.length!=2) throw new Exception("different info expected for "+t.toString());
					AttachedHadoopResource res=new AttachedHadoopResource();
					resources.add(res);
					res.Key=parts[1].trim();
					res.IsHDFSPresent=false;
					res.Order=order;
					res.StorageSystemID=null;
					res.TypeOfResource=t;
					res.Value=parts[1].trim();
					break;
				}
				case Input:
				{
					if(parts.length!=5) throw new Exception("different info expected for "+parts[0]);
					AttachedHadoopResource res=new AttachedHadoopResource();
					res.Key=parts[1].trim();
					res.IsHDFSPresent=false;
					res.Order=order;
					res.StorageSystemID=null;
					res.TypeOfResource=t;
					res.Value=parts[4].trim();
					res.CleanUp=(parts[2].trim().equalsIgnoreCase("persist") ? false : true);
					
					switch(FileInfo.LocationType.valueOf(parts[3].trim().toLowerCase()))
					{
						case local:
						{
							res.ResourceLocationType=AttachedResourceType.LocalFile;
							break;
						}
						case ss:
						{
							res.ResourceLocationType=AttachedResourceType.CMSReference;
							break;
						}
						case url:
						{
							res.ResourceLocationType=AttachedResourceType.Reference;
							break;
						}
					}
					break;
				}
				case Output:
				{

					AttachedHadoopResource res=new AttachedHadoopResource();
					resources.add(res);
					res.Key=parts[1].trim();
					res.IsHDFSPresent=true;
					res.Order=order;
					res.StorageSystemID=null;
					res.TypeOfResource=t;
					res.Value=null;
					res.ResourceLocationType=AttachedResourceType.CMSReference;
					res.CleanUp=(parts[2].trim().equalsIgnoreCase("persist") ? false : true);
					
					if(parts.length!=3 && parts.length != 4 && parts.length != 5 && parts.length != 7) throw new Exception("different info expected for "+parts[0]);
					
					if(parts.length == 3 && globalOutputStoreMode != null) 
					{ 
						if(globalOutputStoreMode.TypeOfLocation.equals(FileInfo.LocationType.url))
						{
							res.ResourceLocationType=AttachedResourceType.Reference;
							res.Value=globalOutputStoreMode.Value;
						}else
							res.ResourceLocationType=AttachedResourceType.CMSReference;
					}
					if(parts.length == 4 && parts[3].equals(FileInfo.LocationType.ss)) res.ResourceLocationType=AttachedResourceType.CMSReference;
					else if(parts.length > 4) 
					{ 
						switch(FileInfo.LocationType.valueOf(parts[4].trim()))
						{
						case url:
							res.ResourceLocationType=AttachedResourceType.Reference;
							break;
						default:
							res.ResourceLocationType=AttachedResourceType.CMSReference;
						}
					}
					if(res.ResourceLocationType == AttachedResourceType.CMSReference)
					{
						if(parts.length != 5 && parts.length != 7) continue;
						if(parts.length == 5) res.Value = parts[4].trim();
						else 
						{
							if(parts[4].trim().startsWith("ftp://"))
								res.Value = "ftp://" + parts[5].trim() + ":" + parts[6].trim() + "@" + parts[4].trim().substring("ftp://".length());
						}
					}
					break;
				}
				default:
				{
					break;
				}
			}
		}
		return resources;
	}
	
	private static void PrintHelp()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("Usage:\n");
		buf.append("Four arguments are needed\n");
		buf.append("1) first the hostname of the machine that is running this code through which it can be reached\n");
		buf.append("2) a port that can be used by the tcp server or <=0 to use a random one\n");
		buf.append("3) the path of the resource file. The syntax of the resource file is the following:\n");
		buf.append("\tScope : <The scope value to use in case of gCube environment>\n");
		buf.append("\tchokeProgressEvents : <true | false> (depending on whether you want to omit progress reporting)\n");
		buf.append("\tchokePerformanceEvents : <true | false> (depending on whether you want to omit performance reporting)\n");
		buf.append("\tstorePlans : <true | false> (depending on whether you want the plan created and the final one to be stored for inspection)\n");
		buf.append("\tJar : <the name that this file should have once moved to the ui node> : <local | hdfs> (depending on where the file is stored and should be retrieved from) : <path to retrieve the file from>\n");
		buf.append("\tMainClass : <the name of the class containing the main method to run in the jar file>\n");
		buf.append("{ these brackets indicate that the following element can be repeated as many times as needed and they neeed not be in the properties file\n");
		buf.append("\tArgument : <the order of the argument in the call> : <the argument to pass>\n");
		buf.append("\t[...]\n");
		buf.append("}\n");
		buf.append("\tConfiguration : <the name that this file should have once moved to the ui node> : <local | hdfs> (depending on where the file is stored and should be retrieved from) : <path to retrieve the file from>\n");
		buf.append("\tProperty : <the property value in the form of key=value>\n");
		buf.append("{ these brackets indicate that the following element can be repeated as many times as needed and they neeed not be in the properties file\n");
		buf.append("\tFile : <the name that this file should have once moved to the ui node> : <local | hdfs> (depending on where the file is stored and should be retrieved from) : <path to retrieve the file from>\n");
		buf.append("\t[...]\n");
		buf.append("}\n");
		buf.append("{ these brackets indicate that the following element can be repeated as many times as needed and they neeed not be in the properties file\n");
		buf.append("\tLib : <the name that this file should have once moved to the ui node> : <local | hdfs> (depending on where the file is stored and should be retrieved from) : <path to retrieve the file from>\n");
		buf.append("\t[...]\n");
		buf.append("}\n");
		buf.append("{ these brackets indicate that the following element can be repeated as many times as needed and they neeed not be in the properties file\n");
		buf.append("\tArchive : <the name that this file should have once moved to the ui node> : <local | hdfs> (depending on where the file is stored and should be retrieved from) : <path to retrieve the file from>\n");
		buf.append("\t[...]\n");
		buf.append("}\n");
		buf.append("{ these brackets indicate that the following element can be repeated as many times as needed and they neeed not be in the properties file\n");
		buf.append("\tInput : <the name that this file should have once moved to the HDFS system> : <tmp | persist> (depending on whether the file should be removed or left in HDFS) : <path to retrieve the file from>\n");
		buf.append("\t[...]\n");
		buf.append("{ these brackets indicate that the following element can be repeated as many times as needed and they neeed not be in the properties file\n");
		buf.append("\tOutput : <the name that the output directory in hdfs has> : <tmp | persist> (depending on whether the file should be removed or left in HDFS)\n");
		buf.append("\t[...]\n");
		buf.append("4) <ftp | gcube> depending on the targeted infrastructure\n");
		System.out.println(buf.toString());
	}

	public static void main(String []args) throws Exception
	{
		if(args.length!=4)
		{
			TestHadoopAdaptor.PrintHelp();
			return;
		}
		else
		{
			String hostName=args[0];
			int port=Integer.parseInt(args[1]);
			TestHadoopAdaptor.Init(hostName, port,args[3]);
		}
		FileInfo globalOutputStoreMode = TestHadoopAdaptor.ParseGlobalOutputStoreMode(args[2]);
		AdaptorHadoopResources attachedResources=new AdaptorHadoopResources();
		attachedResources.Resources.addAll(TestHadoopAdaptor.ParseResources(args[2], globalOutputStoreMode));
		
		WorkflowHadoopAdaptor adaptor=new WorkflowHadoopAdaptor();
		adaptor.SetAdaptorResources(attachedResources);
		adaptor.CreatePlan();
		adaptor.GetCreatedPlan().Config.ChokeProgressReporting=TestHadoopAdaptor.ParseBooleanProperty(args[2], "chokeProgressEvents");
		adaptor.GetCreatedPlan().Config.ChokePerformanceReporting=TestHadoopAdaptor.ParseBooleanProperty(args[2], "chokePerformanceEvents");
		
		if(TestHadoopAdaptor.ParseBooleanProperty(args[2], "storePlans"))
		{
			File tmp=File.createTempFile(UUID.randomUUID().toString(), ".test.hadoop.adaptor.original.plan.xml");
			XMLUtils.Serialize(tmp.toString(), adaptor.GetCreatedPlan().Serialize());
			logger.info("Initial plan is stored at "+tmp.toString());
		}
		
		Handle= ExecutionEngine.Submit(adaptor.GetCreatedPlan());
		TestHadoopAdaptor test=new TestHadoopAdaptor();
		Handle.RegisterObserver(test);
		synchronized (synchCompletion)
		{
			ExecutionEngine.Execute(Handle);
			try{synchCompletion.wait();}catch(Exception ex){}
		}
		
		if(TestHadoopAdaptor.ParseBooleanProperty(args[2], "storePlans"))
		{
			File tmp=File.createTempFile(UUID.randomUUID().toString(), ".test.hadoop.adaptor.final.plan.xml");
			XMLUtils.Serialize(tmp.toString(), adaptor.GetCreatedPlan().Serialize());
			logger.info("Final plan is stored at "+tmp.toString());
		}
		
		if(TestHadoopAdaptor.EvaluateResult())
		{
			for(IOutputResource res : adaptor.GetOutput())
			{
				if(!(res instanceof OutputHadoopResource)) throw new Exception("Different type found");
				File tmp= new File(TestGridAdaptor.GetStoredFilePayload(((OutputHadoopResource)res).VariableID,"hadoop"));
				if(((OutputHadoopResource)res).TypeOfOutput.equals(OutputHadoopResource.OutputType.OutputArchive))
				{
					File f=new File(tmp.toString()+".tar.gz");
					tmp.renameTo(f);
					logger.info("Output file "+((OutputHadoopResource)res).Key+" of type "+((OutputHadoopResource)res).TypeOfOutput+" is stored at "+f.toString());
				}
				else
				{
					logger.info("Output file "+((OutputHadoopResource)res).Key+" of type "+((OutputHadoopResource)res).TypeOfOutput+" is stored at "+tmp.toString());
				}
			}
		}
	}
}
