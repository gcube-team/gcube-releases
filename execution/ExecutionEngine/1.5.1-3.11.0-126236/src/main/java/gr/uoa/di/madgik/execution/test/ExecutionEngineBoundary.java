//package gr.uoa.di.madgik.execution.test;
//
//import gr.uoa.di.madgik.commons.channel.proxy.tcp.ChannelTCPConnManagerEntry;
//import gr.uoa.di.madgik.commons.server.PortRange;
//import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
//import gr.uoa.di.madgik.commons.server.TCPConnectionManagerConfig;
//import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
//import gr.uoa.di.madgik.environment.exception.EnvironmentValidationException;
//import gr.uoa.di.madgik.environment.hint.EnvHint;
//import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
//import gr.uoa.di.madgik.environment.hint.NamedEnvHint;
//import gr.uoa.di.madgik.environment.is.elements.BoundaryListenerInfo;
//import gr.uoa.di.madgik.environment.is.elements.ExtensionPair;
//import gr.uoa.di.madgik.environment.is.elements.NodeInfo;
//import gr.uoa.di.madgik.execution.engine.ExecutionEngine;
//import gr.uoa.di.madgik.execution.engine.ExecutionEngineConfig;
//import gr.uoa.di.madgik.execution.plan.element.invocable.tcpserver.ExecEngTCPConnManagerEntry;
//import gr.uoa.di.madgik.is.InformationSystem;
//import gr.uoa.di.madgik.ss.StorageSystem;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//public class ExecutionEngineBoundary
//{
//	private static Logger logger=LoggerFactory.getLogger(HelloWorld.class);
//	private static EnvHintCollection Hints=new EnvHintCollection();
//	
//	private static void Init(String HostName,int Port,String EnvProvider) throws EnvironmentValidationException
//	{
//		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO,"Initializing Connection Manager");
//		ArrayList<PortRange> ports=new ArrayList<PortRange>();
//		boolean useRandom=true;
//		if(Port<=0) useRandom=true;
//		else
//		{
//			ports.add(new PortRange(Port, Port));
//			useRandom=false;
//		}
//		TCPConnectionManager.Init(new TCPConnectionManagerConfig(HostName,ports,useRandom));
//		TCPConnectionManager.RegisterEntry(new ExecEngTCPConnManagerEntry());
//		TCPConnectionManager.RegisterEntry(new ChannelTCPConnManagerEntry());
//		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO,"Initializing Execution Engine");
//		ExecutionEngine.Init(new ExecutionEngineConfig(ExecutionEngineConfig.InfinitePlans));
//		String providerInformationName=null;
//		String providerStorageName=null;
//		if(EnvProvider.equalsIgnoreCase("gcube"))
//		{
//			ExecutionEngineBoundary.Hints.AddHint(new NamedEnvHint("StorageSystemDeleteOnExit",new EnvHint(Boolean.TRUE.toString())));
//			ExecutionEngineBoundary.Hints.AddHint(new NamedEnvHint("StorageSystemLocalFileSystemBufferPath",new EnvHint("/tmp/")));
//			ExecutionEngineBoundary.Hints.AddHint(new NamedEnvHint("RetryOnErrorCount",new EnvHint("5")));
//			ExecutionEngineBoundary.Hints.AddHint(new NamedEnvHint("RetryOnErrorInterval",new EnvHint(Integer.toString((1000*60*2)))));
//			providerInformationName="gr.uoa.di.madgik.environment.gcube.GCubeInformationSystemProvider";
//			providerStorageName="gr.uoa.di.madgik.environment.gcube.GCubeStorageSystemProvider";
//		}
//		else
//		{
//			ExecutionEngineBoundary.Hints.AddHint(new NamedEnvHint("InformationSystemFTPURL",new EnvHint("ftp://ftpuser:za73ba97ra@dl13.di.uoa.gr/d5s/is/")));
//			ExecutionEngineBoundary.Hints.AddHint(new NamedEnvHint("StorageSystemFTPURL",new EnvHint("ftp://ftpuser:za73ba97ra@dl13.di.uoa.gr/d5s/ss/")));
//			ExecutionEngineBoundary.Hints.AddHint(new NamedEnvHint("StorageSystemDeleteOnExit",new EnvHint(Boolean.TRUE.toString())));
//			ExecutionEngineBoundary.Hints.AddHint(new NamedEnvHint("StorageSystemLocalFileSystemBufferPath",new EnvHint("/tmp/")));
//			providerInformationName="gr.uoa.di.madgik.environment.ftp.FTPInformationSystemProvider";
//			providerStorageName="gr.uoa.di.madgik.environment.ftp.FTPStorageSystemProvider";
//		}
//		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO,"Initializing Information System");
//		InformationSystem.Init(providerInformationName, ExecutionEngineBoundary.Hints);
//		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO,"Initializing Storage System");
//		StorageSystem.Init(providerStorageName, ExecutionEngineBoundary.Hints);
//	}
//	
//	private static void PrintHelp()
//	{
//		StringBuilder buf=new StringBuilder();
//		buf.append("Usage:\n");
//		buf.append("Four arguments are needed\n");
//		buf.append("1) first the hostname of the machine that is running this code through which it can be reached\n");
//		buf.append("2) a port that can be used by the tcp server or <=0 to use a random one\n");
//		buf.append("3) <GCube | Grid | Hadoop> (the type of node)\n");
//		buf.append("3) <ftp | gcube> (the type of environment provider)\n");
//		buf.append("After Initialized typing \"exit\" to the std in will cause the lister to exit\n");
//		System.out.println(buf.toString());
//	}
//	
//	public static void main(String []args) throws IOException, EnvironmentInformationSystemException, EnvironmentValidationException
//	{
//		if(args.length!=4)
//		{
//			ExecutionEngineBoundary.PrintHelp();
//			return;
//		}
//		else
//		{
//			String hostName=args[0];
//			int port=Integer.parseInt(args[1]);
//			ExecutionEngineBoundary.Init(hostName, port,args[3]);
//		}
//		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO,"Engine listens to "+TCPConnectionManager.GetConnectionManagerHostName()+":"+TCPConnectionManager.GetConnectionManagerPort());
//		System.out.println("Registering Node");
//		String nodeID=null;
//		NodeInfo.NodeType nt=NodeInfo.NodeType.valueOf(args[2]);
//		switch(nt)
//		{
//			case GCube:
//			{
//				nodeID=InformationSystem.RegisterNode(new NodeInfo(args[0]),ExecutionEngineBoundary.Hints);
//				break;
//			}
//			case Grid:
//			{
//				List<ExtensionPair> exts=new ArrayList<ExtensionPair>();
//				exts.add(new ExtensionPair(ExtensionPair.ORIGINAL_GLOBUS_LOCATION,System.getenv("ORIGINAL_GLOBUS_LOCATION")));
//				exts.add(new ExtensionPair(ExtensionPair.GLITE_LOCATION,System.getenv("GLITE_LOCATION")));
//				nodeID=InformationSystem.RegisterNode(new NodeInfo(args[0], NodeInfo.NodeType.valueOf(args[2]),exts),ExecutionEngineBoundary.Hints);
//				break;
//			}
//			case Hadoop:
//			{
//				List<ExtensionPair> exts=new ArrayList<ExtensionPair>();
//				exts.add(new ExtensionPair(ExtensionPair.HADOOP_LOCATION,System.getenv("HADOOP_HOME")));
//				nodeID=InformationSystem.RegisterNode(new NodeInfo(args[0], NodeInfo.NodeType.valueOf(args[2]),exts),ExecutionEngineBoundary.Hints);
//				break;
//			}
//			case Condor:
//			{
//				List<ExtensionPair> exts=new ArrayList<ExtensionPair>();
//				exts.add(new ExtensionPair(ExtensionPair.CONDOR_LOCATION,System.getenv("CONDOR_LOCATION")));
//				nodeID=InformationSystem.RegisterNode(new NodeInfo(args[0], NodeInfo.NodeType.valueOf(args[2]),exts),ExecutionEngineBoundary.Hints);
//				break;
//			}
//			default:
//			{
//				throw new EnvironmentInformationSystemException("Unrecognized node type");
//			}
//		}
//		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO,"Registering boundary listener");
//		InformationSystem.RegisterBoundaryListener(new BoundaryListenerInfo(nodeID, TCPConnectionManager.GetConnectionManagerPort()),ExecutionEngineBoundary.Hints);
//		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO,"Kill me to shutdown");
//		while(true)
//		{
//			try{Thread.sleep(60*60*1000);}catch(Exception ex){}
//		}
//	}
//}
