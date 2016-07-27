package org.gcube.execution.workflowengine.service.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import org.gcube.execution.workflowengine.service.stubs.HADOOPArchiveResource;
import org.gcube.execution.workflowengine.service.stubs.HADOOPArgumentResource;
import org.gcube.execution.workflowengine.service.stubs.HADOOPConfig;
import org.gcube.execution.workflowengine.service.stubs.HADOOPConfigurationResource;
import org.gcube.execution.workflowengine.service.stubs.HADOOPFileResource;
import org.gcube.execution.workflowengine.service.stubs.HADOOPInputResource;
import org.gcube.execution.workflowengine.service.stubs.HADOOPJarResource;
import org.gcube.execution.workflowengine.service.stubs.HADOOPLibResource;
import org.gcube.execution.workflowengine.service.stubs.HADOOPMainResource;
import org.gcube.execution.workflowengine.service.stubs.HADOOPOutputResource;
import org.gcube.execution.workflowengine.service.stubs.HADOOPParams;
import org.gcube.execution.workflowengine.service.stubs.HADOOPPropertyResource;
import org.gcube.execution.workflowengine.service.stubs.HADOOPResource;
import org.gcube.execution.workflowengine.service.stubs.WorkflowEngineServicePortType;
import org.gcube.execution.workflowengine.service.test.FileInfo.LocationType;

public class TestHadoopAdaptor extends TestAdaptorBase
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
			String parts[]=line.trim().split("\\s#\\s");
			if(parts.length!=2) continue;
			if(parts[0].trim().equalsIgnoreCase(key)) return Boolean.parseBoolean(parts[1].trim());
		}
		throw new Exception("resource file not complete");
	}
	
	private static HADOOPArchiveResource[] GetArchives(String file) throws Exception
	{
		List<HADOOPArchiveResource> res=new ArrayList<HADOOPArchiveResource>();
		File f =new File(file);
		if(!f.exists() || !f.isFile()) throw new Exception("Specified resource file ("+file+") not found");
		BufferedReader reader=new BufferedReader(new FileReader(f));
		while(true)
		{
			String line=reader.readLine();
			if(line==null) break;
			String parts[]=line.trim().split("\\s#\\s");
			if(!parts[0].trim().equalsIgnoreCase("Archive")) continue;
			if(parts.length!=4) throw new Exception("different info expected for "+parts[0]);
			HADOOPArchiveResource r=new HADOOPArchiveResource();
			r.setHdfsPresent((parts[2].trim().equalsIgnoreCase("hdfs") ? true : false));
			r.setResourceKey(parts[1].trim());
			if(r.isHdfsPresent())
			{
				r.setResourceAccess("Reference");
				r.setInMessageStringPayload(parts[3].trim());
			}
			else
			{
				switch(FileInfo.LocationType.valueOf(parts[2].trim().toLowerCase()))
				{
					case local:
					{
						r.setResourceAccess("InMessageBytes");
						r.setInMessageBytePayload(TestAdaptorBase.GetByteFilePayload(parts[3].trim()));
						break;
					}
					case ss:
					{
						r.setResourceReference(parts[3].trim());
						r.setResourceAccess("CMSReference");
						break;
					}
					case url:
					{
						r.setResourceReference(parts[3].trim());
						r.setResourceAccess("Reference");
						break;
					}
				}
			}
			res.add(r);
		}
		reader.close();
		return res.toArray(new HADOOPArchiveResource[0]);
	}
	
	private static HADOOPFileResource[] GetFiles(String file) throws Exception
	{
		List<HADOOPFileResource> res=new ArrayList<HADOOPFileResource>();
		File f =new File(file);
		if(!f.exists() || !f.isFile()) throw new Exception("Specified resource file ("+file+") not found");
		BufferedReader reader=new BufferedReader(new FileReader(f));
		while(true)
		{
			String line=reader.readLine();
			if(line==null) break;
			String parts[]=line.trim().split("\\s#\\s");
			if(!parts[0].trim().equalsIgnoreCase("File")) continue;
			if(parts.length!=4) throw new Exception("different info expected for "+parts[0]);
			HADOOPFileResource r=new HADOOPFileResource();
			r.setHdfsPresent((parts[2].trim().equalsIgnoreCase("hdfs") ? true : false));
			r.setResourceKey(parts[1].trim());
			if(r.isHdfsPresent())
			{
				r.setResourceAccess("Reference");
				r.setInMessageStringPayload(parts[3].trim());
			}
			else
			{
				switch(FileInfo.LocationType.valueOf(parts[2].trim().toLowerCase()))
				{
					case local:
					{
						r.setResourceAccess("InMessageBytes");
						r.setInMessageBytePayload(TestAdaptorBase.GetByteFilePayload(parts[3].trim()));
						break;
					}
					case ss:
					{
						r.setResourceReference(parts[3].trim());
						r.setResourceAccess("CMSReference");
						break;
					}
					case url:
					{
						r.setResourceReference(parts[3].trim());
						r.setResourceAccess("Reference");
						break;
					}
				}
			}
			res.add(r);
		}
		reader.close();
		return res.toArray(new HADOOPFileResource[0]);
	}
	
	private static HADOOPLibResource[] GetLibs(String file) throws Exception
	{
		List<HADOOPLibResource> res=new ArrayList<HADOOPLibResource>();
		File f =new File(file);
		if(!f.exists() || !f.isFile()) throw new Exception("Specified resource file ("+file+") not found");
		BufferedReader reader=new BufferedReader(new FileReader(f));
		while(true)
		{
			String line=reader.readLine();
			if(line==null) break;
			String parts[]=line.trim().split("\\s#\\s");
			if(!parts[0].trim().equalsIgnoreCase("Lib")) continue;
			if(parts.length!=4) throw new Exception("different info expected for "+parts[0]);
			HADOOPLibResource r=new HADOOPLibResource();
			r.setHdfsPresent((parts[2].trim().equalsIgnoreCase("hdfs") ? true : false));
			r.setResourceKey(parts[1].trim());
			if(r.isHdfsPresent())
			{
				r.setResourceAccess("Reference");
				r.setInMessageStringPayload(parts[3].trim());
			}
			else
			{
				switch(FileInfo.LocationType.valueOf(parts[2].trim().toLowerCase()))
				{
					case local:
					{
						r.setResourceAccess("InMessageBytes");
						r.setInMessageBytePayload(TestAdaptorBase.GetByteFilePayload(parts[3].trim()));
						break;
					}
					case ss:
					{
						r.setResourceReference(parts[3].trim());
						r.setResourceAccess("CMSReference");
						break;
					}
					case url:
					{
						r.setResourceReference(parts[3].trim());
						r.setResourceAccess("Reference");
						break;
					}
				}
			}
			res.add(r);
		}
		reader.close();
		return res.toArray(new HADOOPLibResource[0]);
	}
	
	private static HADOOPInputResource[] GetInputs(String file) throws Exception
	{
		List<HADOOPInputResource> res=new ArrayList<HADOOPInputResource>();
		File f =new File(file);
		if(!f.exists() || !f.isFile()) throw new Exception("Specified resource file ("+file+") not found");
		BufferedReader reader=new BufferedReader(new FileReader(f));
		while(true)
		{
			String line=reader.readLine();
			if(line==null) break;
			String parts[]=line.trim().split("\\s#\\s");
			if(!parts[0].trim().equalsIgnoreCase("Input")) continue;
			if(parts.length!=5) throw new Exception("different info expected for "+parts[0]);
			HADOOPInputResource r=new HADOOPInputResource();
			r.setResourceKey(parts[1].trim());
			switch(FileInfo.LocationType.valueOf(parts[3].trim().toLowerCase()))
			{
				case local:
				{
					r.setResourceAccess("InMessageBytes");
					r.setInMessageBytePayload(TestAdaptorBase.GetByteFilePayload(parts[4].trim()));
					break;
				}
				case ss:
				{
					r.setResourceReference(parts[4].trim());
					r.setResourceAccess("CMSReference");
					break;
				}
				case url:
				{
					r.setResourceReference(parts[4].trim());
					r.setResourceAccess("Reference");
					break;
				}
			}
			r.setCleanup(parts[2].trim().equalsIgnoreCase("persist") ? false : true);
			res.add(r);
		}
		reader.close();
		return res.toArray(new HADOOPInputResource[0]);
	}
	
	private static HADOOPConfigurationResource GetConfiguration(String file) throws Exception
	{
		File f =new File(file);
		if(!f.exists() || !f.isFile()) throw new Exception("Specified resource file ("+file+") not found");
		BufferedReader reader=new BufferedReader(new FileReader(f));
		while(true)
		{
			String line=reader.readLine();
			if(line==null) break;
			String parts[]=line.trim().split("\\s#\\s");
			if(!parts[0].trim().equalsIgnoreCase("Configuration")) continue;
			if(parts.length!=4) throw new Exception("different info expected for "+parts[0]);
			HADOOPConfigurationResource r=new HADOOPConfigurationResource();
			r.setHdfsPresent((parts[2].trim().equalsIgnoreCase("hdfs") ? true : false));
			r.setResourceKey(parts[1].trim());
			if(r.isHdfsPresent())
			{
				r.setResourceAccess("Reference");
				r.setInMessageStringPayload(parts[3].trim());
			}
			else
			{
				switch(FileInfo.LocationType.valueOf(parts[2].trim().toLowerCase()))
				{
					case local:
					{
						r.setResourceAccess("InMessageBytes");
						r.setInMessageBytePayload(TestAdaptorBase.GetByteFilePayload(parts[3].trim()));
						break;
					}
					case ss:
					{
						r.setResourceReference(parts[3].trim());
						r.setResourceAccess("CMSReference");
						break;
					}
					case url:
					{
						r.setResourceReference(parts[3].trim());
						r.setResourceAccess("Reference");
						break;
					}
				}
			}
			reader.close();
			return r;
		}
		reader.close();
		return null;
	}
	
	private static HADOOPJarResource GetJar(String file) throws Exception
	{
		File f =new File(file);
		if(!f.exists() || !f.isFile()) throw new Exception("Specified resource file ("+file+") not found");
		BufferedReader reader=new BufferedReader(new FileReader(f));
		while(true)
		{
			String line=reader.readLine();
			if(line==null) break;
			String parts[]=line.trim().split("\\s#\\s");
			if(!parts[0].trim().equalsIgnoreCase("Jar")) continue;
			if(parts.length!=4) throw new Exception("different info expected for "+parts[0]);
			HADOOPJarResource r=new HADOOPJarResource();
			r.setHdfsPresent((parts[2].trim().equalsIgnoreCase("hdfs") ? true : false));
			r.setResourceKey(parts[1].trim());
			if(r.isHdfsPresent())
			{
				r.setResourceAccess("Reference");
				r.setInMessageStringPayload(parts[3].trim());
			}
			else
			{
				switch(FileInfo.LocationType.valueOf(parts[2].trim().toLowerCase()))
				{
					case local:
					{
						r.setResourceAccess("InMessageBytes");
						r.setInMessageBytePayload(TestAdaptorBase.GetByteFilePayload(parts[3].trim()));
						break;
					}
					case ss:
					{
						r.setResourceReference(parts[3].trim());
						r.setResourceAccess("CMSReference");
						break;
					}
					case url:
					{
						r.setResourceReference(parts[3].trim());
						r.setResourceAccess("Reference");
						break;
					}
				}
			}
			reader.close();
			return r;
		}
		reader.close();
		return null;
	}
	
	private static HADOOPArgumentResource[] GetArguments(String file) throws Exception
	{
		List<HADOOPArgumentResource> res=new ArrayList<HADOOPArgumentResource>();
		File f =new File(file);
		if(!f.exists() || !f.isFile()) throw new Exception("Specified resource file ("+file+") not found");
		BufferedReader reader=new BufferedReader(new FileReader(f));
		while(true)
		{
			String line=reader.readLine();
			if(line==null) break;
			String parts[]=line.trim().split("\\s#\\s");
			if(!parts[0].trim().equalsIgnoreCase("Argument")) continue;
			if(parts.length!=3) throw new Exception("different info expected for "+parts[0]);
			HADOOPArgumentResource r=new HADOOPArgumentResource();
			r.setOrder(Integer.parseInt(parts[1].trim()));
			r.setResourceValue(parts[2].trim());
			res.add(r);
		}
		reader.close();
		return res.toArray(new HADOOPArgumentResource[0]);
	}
	
	private static HADOOPMainResource GetMain(String file) throws Exception
	{
		File f =new File(file);
		if(!f.exists() || !f.isFile()) throw new Exception("Specified resource file ("+file+") not found");
		BufferedReader reader=new BufferedReader(new FileReader(f));
		while(true)
		{
			String line=reader.readLine();
			if(line==null) break;
			String parts[]=line.trim().split("\\s#\\s");
			if(!parts[0].trim().equalsIgnoreCase("MainClass")) continue;
			if(parts.length!=2) throw new Exception("different info expected for "+parts[0]);
			HADOOPMainResource r=new HADOOPMainResource();
			r.setResourceValue(parts[1].trim());
			reader.close();
			return r;
		}
		reader.close();
		return null;
	}
	
	private static HADOOPPropertyResource []GetProperties(String file) throws Exception
	{
		List<HADOOPPropertyResource> res=new ArrayList<HADOOPPropertyResource>();
		File f =new File(file);
		if(!f.exists() || !f.isFile()) throw new Exception("Specified resource file ("+file+") not found");
		BufferedReader reader=new BufferedReader(new FileReader(f));
		while(true)
		{
			String line=reader.readLine();
			if(line==null) break;
			String parts[]=line.trim().split("\\s#\\s");
			if(!parts[0].trim().equalsIgnoreCase("Property")) continue;
			if(parts.length!=2) throw new Exception("different info expected for "+parts[0]);
			HADOOPPropertyResource r=new HADOOPPropertyResource();
			r.setResourceValue(parts[1].trim());
			res.add(r);
		}
		reader.close();
		return res.toArray(new HADOOPPropertyResource[0]);
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
	
	private static HADOOPOutputResource []GetOutputs(String file, FileInfo globalOutputStoreMode) throws Exception
	{
		List<HADOOPOutputResource> res=new ArrayList<HADOOPOutputResource>();
		File f =new File(file);
		if(!f.exists() || !f.isFile()) throw new Exception("Specified resource file ("+file+") not found");
		BufferedReader reader=new BufferedReader(new FileReader(f));
		while(true)
		{
			String line=reader.readLine();
			if(line==null) break;
			String parts[]=line.trim().split("\\s#\\s");
			if(!parts[0].trim().equalsIgnoreCase("Output")) continue;
			if(parts.length!=3 && parts.length != 4 && parts.length != 5 && parts.length != 7) throw new Exception("different info expected for "+parts[0]);
			HADOOPOutputResource r=new HADOOPOutputResource();
			r.setCleanup((parts[2].trim().equalsIgnoreCase("persist") ? false : true));
			r.setResourceKey(parts[1].trim());
			FileInfo nfo = null;
			if(parts.length == 3 && globalOutputStoreMode != null) 
			{ 
				if(globalOutputStoreMode.TypeOfLocation.equals(LocationType.url))
				{
					r.setResourceAccess("Reference");
					r.setResourceReference(globalOutputStoreMode.Value);
				}else
					r.setResourceAccess("CMSReference");
				nfo = new FileInfo(); 
				nfo.TypeOfLocation = globalOutputStoreMode.TypeOfLocation;
				nfo.Value = globalOutputStoreMode.Value;
			}
			if(parts.length == 4 && parts[3].equals(LocationType.ss.toString())) r.setResourceAccess("CMSReference");
			else if(parts.length > 4) 
			{ 
				switch(FileInfo.LocationType.valueOf(parts[4].trim()))
				{
				case url:
					r.setResourceAccess("Reference");
					break;
				default:
					r.setResourceAccess("CMSReference");
				}
			}
			if(r.getResourceAccess().equals("Reference"))
			{
				if(parts.length != 5 && parts.length != 7) continue;
				if(parts.length == 5) 
				{
					
					 r.setResourceReference(TestAdaptorBase.StripUrlPort(TestAdaptorBase.StripUrlUserInfo(parts[4].trim())));
					 AccessInfo ai = TestAdaptorBase.ParseUrlAccessInfo(parts[4].trim());
					 org.gcube.execution.workflowengine.service.stubs.AccessInfo accessInfo = 
						 new org.gcube.execution.workflowengine.service.stubs.AccessInfo(ai.password, new Integer(ai.port).toString(), ai.userId);
					 r.setResourceAccessInfo(accessInfo);
					 
				}
				else 
				{
					if(parts[4].trim().startsWith("ftp://"))
						r.setResourceReference("ftp://" + parts[5].trim() + ":" + parts[6].trim() + "@" + parts[4].trim().substring("ftp://".length()));
				}
			}
			res.add(r);
		}
		reader.close();
		return res.toArray(new HADOOPOutputResource[0]);
	}
	
	private static String GetScope(String file) throws Exception
	{
		File f =new File(file);
		if(!f.exists() || !f.isFile()) throw new Exception("Specified resource file ("+file+") not found");
		BufferedReader reader=new BufferedReader(new FileReader(f));
		while(true)
		{
			String line=reader.readLine();
			if(line==null) break;
			String parts[]=line.trim().split("\\s#\\s");
			if(!parts[0].trim().equalsIgnoreCase("Scope")) continue;
			if(parts.length!=2) throw new Exception("different info expected for "+parts[0]);
			reader.close();
			return parts[1].trim();
		}
		reader.close();
		return null;
	}
	
	private static void PrintHelp()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("Usage:\n");
		buf.append("Two arguments are needed\n");
		buf.append("1) the path of the resource file. The syntax of the resource file is the following:\n");
		buf.append("\tScope : <The scope value to use in case of gCube environment>\n");
		buf.append("\tchokeProgressEvents : <true | false> (depending on whether you want to omit progress reporting)\n");
		buf.append("\tchokePerformanceEvents : <true | false> (depending on whether you want to omit performance reporting)\n");
		buf.append("\tstorePlans : <true | false> (depending on whether you want the plan created and the final one to be stored for inspection)\n");
		buf.append("\tJar : <the name that this file should have once moved to the ui node> : <local | ss | url | hdfs> (depending on where the file is stored and should be retrieved from) : <path to retrieve the file from>\n");
		buf.append("\tMainClass : <the name of the class containing the main method to run in the jar file>\n");
		buf.append("{ these brackets indicate that the following element can be repeated as many times as needed and they neeed not be in the properties file\n");
		buf.append("\tArgument : <the order of the argument in the call> : <the argument to pass>\n");
		buf.append("\t[...]\n");
		buf.append("}\n");
		buf.append("\tConfiguration : <the name that this file should have once moved to the ui node> : <local | ss | url | hdfs> (depending on where the file is stored and should be retrieved from) : <path to retrieve the file from>\n");
		buf.append("\tProperty : <the property value in the form of key=value>\n");
		buf.append("{ these brackets indicate that the following element can be repeated as many times as needed and they neeed not be in the properties file\n");
		buf.append("\tFile : <the name that this file should have once moved to the ui node> : <local | ss | url | hdfs> (depending on where the file is stored and should be retrieved from) : <path to retrieve the file from>\n");
		buf.append("\t[...]\n");
		buf.append("}\n");
		buf.append("{ these brackets indicate that the following element can be repeated as many times as needed and they neeed not be in the properties file\n");
		buf.append("\tLib : <the name that this file should have once moved to the ui node> : <local | ss | url | hdfs> (depending on where the file is stored and should be retrieved from) : <path to retrieve the file from>\n");
		buf.append("\t[...]\n");
		buf.append("}\n");
		buf.append("{ these brackets indicate that the following element can be repeated as many times as needed and they neeed not be in the properties file\n");
		buf.append("\tArchive : <the name that this file should have once moved to the ui node> : <local | ss | url | hdfs> (depending on where the file is stored and should be retrieved from) : <path to retrieve the file from>\n");
		buf.append("\t[...]\n");
		buf.append("}\n");
		buf.append("{ these brackets indicate that the following element can be repeated as many times as needed and they neeed not be in the properties file\n");
		buf.append("\tInput : <the name that this file should have once moved to the HDFS system> : <tmp | persist> (depending on whether the file should be removed or left in HDFS) : <local | ss | url> (depending on where the file is stored and should be retrieved from) : <path to retrieve the file from>\n");
		buf.append("\t[...]\n");
		buf.append("{ these brackets indicate that the following element can be repeated as many times as needed and they neeed not be in the properties file\n");
		buf.append("\tOutput : <the name that the output directory in hdfs has> : <tmp | persist> (depending on whether the file should be removed or left in HDFS)\n");
		buf.append("\t[...]\n");
		buf.append("2) the path of the output file that will contain the execution identifier\n");
		System.out.println(buf.toString());
	}

	public static void main(String []args) throws Exception
	{
		if(args.length!=2)
		{
			TestHadoopAdaptor.PrintHelp();
			return;
		}
		else
		{
			TestHadoopAdaptor.Init();
		}
		System.out.println("resources file used : "+args[0]);
		System.out.println("outut execution id : "+args[1]);
		
		HADOOPParams params=new HADOOPParams();

		HADOOPConfig conf=new HADOOPConfig();
		conf.setChokeProgressEvents(TestHadoopAdaptor.ParseBooleanProperty(args[0], "chokeProgressEvents"));
		conf.setChokePerformanceEvents(TestHadoopAdaptor.ParseBooleanProperty(args[0], "chokePerformanceEvents"));
		FileInfo globalOutputStoreMode = TestHadoopAdaptor.ParseGlobalOutputStoreMode(args[0]);
		params.setConfig(conf);
		
		HADOOPResource res=new HADOOPResource();
		res.setArchives(TestHadoopAdaptor.GetArchives(args[0]));
		res.setArguments(TestHadoopAdaptor.GetArguments(args[0]));
		res.setConfiguration(TestHadoopAdaptor.GetConfiguration(args[0]));
		res.setFiles(TestHadoopAdaptor.GetFiles(args[0]));
		res.setInputs(TestHadoopAdaptor.GetInputs(args[0]));
		res.setJar(TestHadoopAdaptor.GetJar(args[0]));
		res.setLibs(TestHadoopAdaptor.GetLibs(args[0]));
		res.setMain(TestHadoopAdaptor.GetMain(args[0]));
		res.setProperties(TestHadoopAdaptor.GetProperties(args[0]));
		res.setOutputs(TestHadoopAdaptor.GetOutputs(args[0], globalOutputStoreMode));
		params.setHadoopResources(res);
		
		String Scope=TestHadoopAdaptor.GetScope(args[0]);
				
		System.out.println("Locating Workflow Engine");
		String endpoint=TestAdaptorBase.GetWorkflowEngineEndpoint(Scope);
		System.out.println("Selected Workflow Engine "+endpoint);
		WorkflowEngineServicePortType wf=TestAdaptorBase.GetWorkflowEnginePortType(Scope,endpoint);
		System.out.println("Submiting execution");
		String ExecutionID=wf.adaptHADOOP(params);
		System.out.println("Execution ID : "+ExecutionID);
		TestAdaptorBase.WriteExecutionID(args[1], ExecutionID, endpoint,Scope);
	}
}
