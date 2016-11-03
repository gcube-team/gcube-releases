package org.gcube.execution.workfloworchestrationlayerservice.wrappers;

import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.exception.EnvironmentValidationException;
import gr.uoa.di.madgik.environment.hint.EnvHint;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.hint.NamedEnvHint;
import gr.uoa.di.madgik.is.InformationSystem;
import gr.uoa.di.madgik.ss.StorageSystem;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.execution.workflowengine.service.stubs.WorkflowEngineServicePortType;
import org.gcube.execution.workflowengine.service.stubs.service.WorkflowEngineServiceAddressingLocator;
import org.gcube.execution.workfloworchestrationlayerservice.utils.AccessInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AdaptorBase
{
	private static Logger logger = LoggerFactory.getLogger(AdaptorBase.class);
	
	private static Random randGen=new Random();
	private static final int DefaultInvocationTimeout=1000*60*10;

	protected static void Init() throws EnvironmentValidationException
	{
		EnvHintCollection Hints=new EnvHintCollection();
		Hints.AddHint(new NamedEnvHint("StorageSystemDeleteOnExit",new EnvHint(Boolean.FALSE.toString())));
		Hints.AddHint(new NamedEnvHint("StorageSystemLocalFileSystemBufferPath",new EnvHint("/tmp/")));
		logger.info("Initializing Information System");
		InformationSystem.Init("gr.uoa.di.madgik.environment.gcube.GCubeInformationSystemProvider", Hints);
		logger.info("Initializing Storage System");
		StorageSystem.Init("gr.uoa.di.madgik.environment.gcube.GCubeStorageSystemProvider", Hints);
	}
	
	public static String GetStringFilePayload(String path) throws IOException
	{
		BufferedReader reader=new BufferedReader(new FileReader(new File(path)));
		StringBuilder buf=new StringBuilder();
		while(true)
		{
			String line = reader.readLine();
			if(line==null) break;
			buf.append(line);
			buf.append("\n");
		}
		reader.close();
		return buf.toString();
	}
	
	public static byte[] GetByteFilePayload(String path) throws IOException
	{
		ByteArrayOutputStream bout=new ByteArrayOutputStream();
		BufferedInputStream bin=new BufferedInputStream(new FileInputStream(new File(path)));
		byte []b=new byte[4*1024];
		while(true)
		{
			int read=bin.read(b);
			if(read<0) break;
			bout.write(b, 0, read);
		}
		bin.close();
		bout.flush();
		bout.close();
		return bout.toByteArray();
	}
	
	public static String GetWorkflowEngineEndpoint(String Scope) throws Exception
	{
		return GetWorkflowEngineEndpoint(Scope, null);
	}
	
	public static String GetWorkflowEngineEndpoint(String Scope, String choice) throws Exception
	{
		EnvHintCollection ISHints=new EnvHintCollection();
		ISHints.AddHint(new NamedEnvHint("GCubeActionScope", new EnvHint(Scope)));
		List<String> eps=InformationSystem.RetrieveByQualifier("ServiceClass:Execution,ServiceName:WorkflowEngineService",ISHints);
		if(eps==null || eps.size()==0) throw new EnvironmentInformationSystemException("No usable WorkflowEngine end point found");
		if(choice == null)
			return eps.get(randGen.nextInt(eps.size()));
		String endpoint = null;
		for(String w: eps) {
			if(w.contains(choice)) {
				endpoint = w;
				break;
			}
		}
		if(endpoint==null)
			throw new EnvironmentInformationSystemException("No usable WorkflowEngine end point with given name found");
		logger.info(eps.toString());
		return endpoint;
	}
	
	protected static WorkflowEngineServicePortType GetWorkflowEnginePortType(String Scope, String endpoint) throws Exception
	{
		EndpointReferenceType endpointT = new EndpointReferenceType();
		endpointT.setAddress(new Address(endpoint));
		WorkflowEngineServiceAddressingLocator wflocator=new WorkflowEngineServiceAddressingLocator();
		WorkflowEngineServicePortType wf=wflocator.getWorkflowEngineServicePortTypePort(endpointT);
		wf = GCUBERemotePortTypeContext.getProxy(wf, GCUBEScope.getScope(Scope),AdaptorBase.DefaultInvocationTimeout);
		return wf;
	}
	
	protected static void WriteExecutionID(String path,String ExecutionID,String endpoint,String Scope) throws IOException
	{
		FileWriter w=new FileWriter(new File(path));
		w.write(endpoint);
		w.write("\n");
		w.write(ExecutionID);
		w.write("\n");
		w.write(Scope);
		w.write("\n");
		w.flush();
		w.close();
	}
	
	protected static String WritePlan(String plan, boolean isCompleted) throws IOException
	{
		File tmp=null;
		if(isCompleted)tmp=File.createTempFile(UUID.randomUUID().toString(), ".final.plan.xml");
		else tmp=File.createTempFile(UUID.randomUUID().toString(), ".initial.plan.xml");
		FileWriter w=new FileWriter(tmp);
		w.write(plan);
		w.flush();
		w.close();
		return tmp.toString();
	}
	
	public static String GetWorkflowEngineURL(String path) throws IOException
	{
		BufferedReader r=new BufferedReader(new FileReader(new File(path)));
		String url=r.readLine();
		r.close();
		return url;
	}
	
	public static String GetWorkflowEngineExecutionID(String path) throws IOException
	{
		BufferedReader r=new BufferedReader(new FileReader(new File(path)));
		r.readLine();
		String id=r.readLine();
		r.close();
		return id;
	}
	
	public static String GetWorkflowEngineExecutionScope(String path) throws IOException
	{
		BufferedReader r=new BufferedReader(new FileReader(new File(path)));
		r.readLine();
		r.readLine();
		String id=r.readLine();
		r.close();
		return id;
	}
	
	public static AccessInfo ParseUrlAccessInfo(String url) throws Exception
	{
		URL u = new URL(url);
		int port = u.getPort();
		String userId = null;
		String password = null;
		if(u.getAuthority().indexOf(":") != -1)
		{
			userId = u.getUserInfo().substring(0, u.getUserInfo().indexOf(":"));
			password = u.getUserInfo().substring(u.getUserInfo().indexOf(":")+1);
		}
		AccessInfo ai = new AccessInfo();
		ai.port = port;
		ai.userId = userId;
		ai.password = password;
		return ai;
	}
	
	public static String StripUrlUserInfo(String url) throws Exception
	{
		if(url.lastIndexOf("@")==-1) return url;
		URL u = new URL(url);
		return u.getProtocol() + "://" + url.substring(url.lastIndexOf("@")+1);
	}
	
	public static String StripUrlPort(String url) throws Exception
	{
		URL u = new URL(url);
		if(u.getPort()==-1) return url;
		String newUrl = url.indexOf("@")==-1 ? url.substring(url.indexOf("//")+2) : url.substring(url.indexOf("@")+1);
		return u.getProtocol() + "://" + (u.getUserInfo() == null ? u.getHost() : u.getUserInfo() + "@" + u.getHost()) + (newUrl.indexOf("/")==-1 ? "" : newUrl.substring(newUrl.indexOf("/")));
		//u.get
	}
	
}
