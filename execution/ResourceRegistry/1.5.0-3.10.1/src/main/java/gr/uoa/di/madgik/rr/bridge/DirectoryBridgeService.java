//package gr.uoa.di.madgik.rr.bridge;
//
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import gr.uoa.di.madgik.rr.ResourceRegistry;
//import gr.uoa.di.madgik.rr.ResourceRegistryException;
//
//public class DirectoryBridgeService
//{
//	private static Logger logger=Logger.getLogger(DirectoryBridgeService.class.getName());
//	
//	private static String provider=null;
//	private static boolean isInit;
//	private static long period=-1;
//	private static IDirectoryBridge instance=null;
//	
//	public static void main(String []args) throws ResourceRegistryException
//	{
//		parseInput(args);
//		logger.log(Level.INFO, "Using directory provider "+provider+" with init "+isInit+" and period "+period);
//		initProvider();
//		while(true)
//		{
//			try
//			{
//				instance.refreshEditables(isInit);
//				instance.setEditableTargets(ResourceRegistry.getContext().getEditableTargets());
//				instance.setReadOnlyTargets(ResourceRegistry.getContext().getReadOnlyTargets());
//				instance.loadTargets();
//				instance.doBridging();
//				isInit=false;
//			}catch(Exception ex)
//			{
//				logger.log(Level.WARNING, "could not complete bridging iteration. continuing...",ex);
//			}
//			finally
//			{
//				try{Thread.sleep(period);}catch(Exception ex){}
//			}
//		}
//	}
//	
//	private static void initProvider() throws ResourceRegistryException
//	{
//		try
//		{
//			Class<?> pobj=Class.forName(provider);
//			Object obj=pobj.newInstance();
//			if(!(obj instanceof IDirectoryBridge))
//			{
//				logger.log(Level.SEVERE, "directory provider not of expected type "+provider);
//				throw new ResourceRegistryException("directory provider not of expected type "+provider);
//			}
//			instance=(IDirectoryBridge)obj;
//		}catch(Exception ex)
//		{
//			logger.log(Level.SEVERE, "could not initialize directory provider "+provider,ex);
//			throw new ResourceRegistryException("could not initialize directory provider "+provider,ex);
//		}
//	}
//	
//	private static void printUsage()
//	{
//		String msg="service usage is "+DirectoryBridgeService.class.getName()+" -p <provider name> -i <purge local and use gCube IS as init repository | true or false> -s <sleep period in milliseconds between bridging>";
//		System.out.println(msg);
//		logger.log(Level.SEVERE, msg);
//	}
//	
//	private static void parseInput(String []args) throws ResourceRegistryException
//	{
//		if(args.length!=6)
//		{
//			logger.log(Level.SEVERE, "directory provider name and mode of operation must be supplied");
//			printUsage();
//			throw new ResourceRegistryException("directory provider name and mode of operation must be supplied");
//		}
//		for(int i=0;i<args.length;i+=1)
//		{
//			if(args[i].equals("-p"))
//			{
//				if(i+1>=args.length)
//				{
//					logger.log(Level.SEVERE, "invalid argument list");
//					printUsage();
//					throw new ResourceRegistryException("invalid argument list");
//				}
//				else
//				{
//					provider=args[i+1];
//				}
//			}
//			else if(args[i].equals("-i"))
//			{
//				if(i+1>=args.length)
//				{
//					logger.log(Level.SEVERE, "invalid argument list");
//					printUsage();
//					throw new ResourceRegistryException("invalid argument list");
//				}
//				else
//				{
//					try
//					{
//						isInit=Boolean.parseBoolean(args[i+1]);
//					}catch(Exception ex)
//					{
//						logger.log(Level.SEVERE, "invalid argument list");
//						printUsage();
//						throw new ResourceRegistryException("invalid argument list");
//					}
//				}
//			}
//			else if(args[i].equals("-s"))
//			{
//				if(i+1>=args.length)
//				{
//					logger.log(Level.SEVERE, "invalid argument list");
//					printUsage();
//					throw new ResourceRegistryException("invalid argument list");
//				}
//				else
//				{
//					try
//					{
//						period=Long.parseLong(args[i+1]);
//					}catch(Exception ex)
//					{
//						logger.log(Level.SEVERE, "invalid argument list");
//						printUsage();
//						throw new ResourceRegistryException("invalid argument list");
//					}
//				}
//			}
//		}
//		if(provider==null || period<=0)
//		{
//			logger.log(Level.SEVERE, "invalid argument list");
//			printUsage();
//			throw new ResourceRegistryException("invalid argument list");
//		}
//	}
//}
