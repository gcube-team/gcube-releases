package gr.uoa.di.madgik.environment.gcube;

import gr.uoa.di.madgik.commons.infra.HostingNode;
import gr.uoa.di.madgik.commons.infra.nodeselection.NodeSelector;
import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemSerializationException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.infra.NodeInfo2HostingNodeAdapter;
import gr.uoa.di.madgik.environment.is.IInformationSystemProvider;
import gr.uoa.di.madgik.environment.is.elements.ExtensionPair;
import gr.uoa.di.madgik.environment.is.elements.IInformationSystemElement;
import gr.uoa.di.madgik.environment.is.elements.NodeInfo;
import gr.uoa.di.madgik.environment.is.elements.matching.MatchParser;
import gr.uoa.di.madgik.is.InformationSystem;
import gr.uoa.di.madgik.environment.is.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.QueryParameter;
import org.gcube.common.core.informationsystem.client.XMLResult;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGHNQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.resources.GCUBEGenericResource;
import org.gcube.common.core.resources.GCUBEHostingNode;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.resources.node.Description.FileSystem;
import org.gcube.common.core.resources.node.Description.NetworkAdapter;
import org.gcube.common.core.resources.node.Description.Processor;
import org.gcube.common.core.resources.node.Description.RuntimeEnvironment.Variable;
import org.gcube.common.core.resources.node.Description.StorageDevice;
import org.gcube.common.core.resources.node.Description.StoragePartition;
import org.gcube.common.core.resources.runninginstance.Endpoint;
import org.gcube.common.core.scope.GCUBEScope;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GCubeInformationSystemProvider implements IInformationSystemProvider
{
	private static Logger logger=LoggerFactory.getLogger(GCubeInformationSystemProvider.class);
	
	public enum QualifierAttribute
	{
		ServiceClass,
		ServiceName,
		PortType
	}
	
	private static Random randGen=new Random();
	
	public static final String GCubeActionScopeHintName="GCubeActionScope";
	public static final String RetryOnErrorCountHintName="RetryOnErrorCount";
	public static final String RetryOnErrorIntervalHintName="RetryOnErrorInterval";
	public static final String InformationSystemRIContainerServiceClassHintName="InformationSystemRIContainerServiceClass";
	public static final String InformationSystemRIContainerServiceNameHintName="InformationSystemRIContainerServiceName";
	public static final String ResolveLocalNodeHintName="ResolveLocalNode";
	public static final String NodeSelectorHintName="NodeSelector";
//	private static final String DefaultSecondaryType="ExecutionEngine";
	private static final int DefaultRetryOnError=0;
	private static final String DefaultInformationSystemRIContainerServiceClass="Execution";
	private static final String DefaultInformationSystemRIContainerServiceName="ExecutionEngineService";
	private static final boolean DefaultResolveLocalNode = true;
	
	private static String LocalHostname = null;
	private static Integer LocalPort = null;
	
	public List<String> RetrieveByQualifier(String qualifier,EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		try
		{
			String ServiceClass=GCubeInformationSystemProvider.GetQualifierAttributeValue(QualifierAttribute.ServiceClass, qualifier);
			String ServiceName=GCubeInformationSystemProvider.GetQualifierAttributeValue(QualifierAttribute.ServiceName, qualifier);
			String PortType=GCubeInformationSystemProvider.GetQualifierAttributeValue(QualifierAttribute.PortType, qualifier);
			if(ServiceClass==null || ServiceName==null) throw new EnvironmentInformationSystemException("Insufficient qualifier attributes provided");
			List<String> eps=null;
			Exception rex=null;
			for(int i=0;i<GCubeInformationSystemProvider.GetNumberOfTries(Hints);i+=1)
			{
				try
				{
					int waitPeriod=GCubeInformationSystemProvider.GetSleepBetweenInterval(Hints);
					if(waitPeriod>0 && rex!=null) {try{Thread.sleep(waitPeriod);}catch(Exception eex){}}
					eps=GCubeInformationSystemProvider.CollectInstance(Hints,ServiceClass , ServiceName, PortType);
					rex=null;
					break;
				}catch(Exception ex)
				{
					rex=ex;
					logger.warn("Error while trying to retrieve by qualifier. Check if should try again ("+ex.getMessage()+")");
				}
			}
			if(rex!=null) throw rex;
			return eps;
		}catch(Exception ex)
		{
			throw new EnvironmentInformationSystemException("Could not complete information system interaction",ex);
		}
	}

	public List<String> Query(Query query,EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		//TODO implement
		return new ArrayList<String>();
	}
	
	public List<String> Query(String query,EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		//TODO implement
		return new ArrayList<String>();
	}

	public NodeInfo GetMatchingNode(String RankingExpression, String RequirementsExpression, EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		return GetMatchingNode(RankingExpression, RequirementsExpression, GetNodeSelector(Hints), Hints);
	}
	
	public NodeInfo GetMatchingNode(String RankingExpression, String RequirementsExpression, NodeSelector selector, EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		List<NodeInfo> nodes = GetMatchingNodes(RankingExpression, RequirementsExpression, Hints);
		try
		{
			HostingNode hn = selector.selectNode(new NodeInfo2HostingNodeAdapter().adaptAll(nodes));
			for(NodeInfo ni : nodes)
			{
				if(ni.ID.equals(hn.getId())) return ni;
			}
			return null;
		}catch(Exception ex)
		{
			throw new EnvironmentInformationSystemException("Could not complete information system interaction",ex);
		}
	}

	public List<NodeInfo> GetMatchingNodes(String RankingExpression, String RequirementsExpression, EnvHintCollection Hints) throws EnvironmentInformationSystemException {
		try
		{
			List<NodeInfo> res=null;
			Exception rex=null;
			for(int i=0;i<GCubeInformationSystemProvider.GetNumberOfTries(Hints);i+=1)
			{
				try
				{
					int waitPeriod=GCubeInformationSystemProvider.GetSleepBetweenInterval(Hints);
					if(waitPeriod>0 && rex!=null) {try{Thread.sleep(waitPeriod);}catch(Exception eex){}}
					res=GCubeInformationSystemProvider.CollectNodeInfo(Hints, RequirementsExpression, RankingExpression);
					rex=null;
					break;
				}catch(Exception ex)
				{
					rex=ex;
					logger.warn("Error while trying to retrieve node info. Check if should try again ("+ex.getMessage()+")");
				}
			}
			if(rex!=null) throw rex;
			return res;
		}catch(Exception ex)
		{
			throw new EnvironmentInformationSystemException("Could not complete information system interaction",ex);
		}
	}
		
	public NodeInfo GetNode(String NodeID, EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		try
		{
			NodeInfo res=null;
			Exception rex=null;
			for(int i=0;i<GCubeInformationSystemProvider.GetNumberOfTries(Hints);i+=1)
			{
				try
				{
					int waitPeriod=GCubeInformationSystemProvider.GetSleepBetweenInterval(Hints);
					if(waitPeriod>0 && rex!=null) {try{Thread.sleep(waitPeriod);}catch(Exception eex){}}
					res=GCubeInformationSystemProvider.CollectNodeInfo(Hints, NodeID);
					rex=null;
					break;
				}catch(Exception ex)
				{
					rex=ex;
					logger.warn("Error while trying to retrieve node info. Check if should try again ("+ex.getMessage()+")");
				}
			}
			if(rex!=null) throw rex;
			return res;
		}catch(Exception ex)
		{
			throw new EnvironmentInformationSystemException("Could not complete information system interaction",ex);
		}
	}
	
	public NodeInfo GetNode(String Hostname, String Port, EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		//TODO implement
		throw new EnvironmentInformationSystemException("Operation not supported");
	}

	public String GetGenericByID(String ID, EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		List<String> res=GCubeInformationSystemProvider.GenericResourceQuery("$result/ID/string() eq '" + ID+ "'",Hints);
		if(res.size()==0) return null;
		if(res.size()>1) throw new EnvironmentInformationSystemException("More than one results found");
		return res.get(0);
	}

	public List<String> GetGenericByName(String Name, EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{ 
		return GCubeInformationSystemProvider.GenericResourceQuery("$result/Profile/Name/string() eq '" + Name + "'",Hints);
	}

	public String GetOpenSearchGenericByDescriptionDocumentURI(String URI, EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		return GCubeInformationSystemProvider.GenericResourceQuery("$result/Profile/Body/OpenSearchResource/descriptionDocumentURI/string() eq '" + URI + "'",Hints).get(0);
	}

	public String RegisterNode(NodeInfo info, EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		try
		{
			GCUBEServiceContext cntxs = GHNContext.getContext().getServiceContext(GCubeInformationSystemProvider.GetRIContainerServiceClass(Hints), GCubeInformationSystemProvider.GetRIContainerServiceName(Hints));
			GCUBERunningInstance ri=cntxs.getInstance();
			GCubeInformationSystemProvider.UpdateRISpecificData(ri, info);
			return info.ID;
		}catch(Exception ex)
		{
			throw new EnvironmentInformationSystemException("Could not complete information system interaction",ex);
		}
	}
	
	public void UnregisterNode(String NodeID, EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		throw new EnvironmentInformationSystemException("Operation not supported");
	}
	
	private static List<String> GenericResourceQuery(String queryString, EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		try
		{
			ISClient client = null;
			GCUBEGenericQuery query = null;
			client =  GHNContext.getImplementation(ISClient.class);
			query = client.getQuery("GCUBEResourceQuery");
			
			query.addParameters(new QueryParameter("FILTER", queryString),
					new QueryParameter("TYPE", GCUBEGenericResource.TYPE),
					new QueryParameter("RESULT", "$result/Profile/Body"));
	
			List<XMLResult> res = null;
			
			String scopeID=GCubeInformationSystemProvider.GetActionScope(Hints);
			if(scopeID==null) throw new EnvironmentInformationSystemException("No scope specified");
						
			Exception rex=null;
			for(int i=0;i<GCubeInformationSystemProvider.GetNumberOfTries(Hints);i+=1)
			{
				try
				{
					int waitPeriod=GCubeInformationSystemProvider.GetSleepBetweenInterval(Hints);
					if(waitPeriod>0 && rex!=null) {try{Thread.sleep(waitPeriod);}catch(Exception eex){}}
					res = client.execute(query, GCUBEScope.getScope(scopeID));
					rex=null;
					break;
				}catch(Exception ex)
				{
					rex=ex;
					logger.warn("Error while trying to retrieve generic resource. Check if should try again ("+ex.getMessage()+")");
				}
			}
			if(rex!=null) throw rex;
			
			if(res == null || res.size() == 0)
				throw new EnvironmentInformationSystemException("The generic resource of xpath: " + queryString + " was not found.");
			if(res.size() > 1)
				throw new EnvironmentInformationSystemException("The generic resource of queyr : " + queryString + " was found " + res.size() + " times published.");
			
			List<String> ret = new ArrayList<String>();
			for(XMLResult r : res) ret.add(r.toString());
			return ret;
		}
		catch(Exception ex)
		{
			throw new EnvironmentInformationSystemException("Could not complete information system interaction",ex);
		}
	}
	
	private static void UpdateRISpecificData(GCUBERunningInstance ri,IInformationSystemElement item) throws EnvironmentInformationSystemSerializationException
	{
		try
		{
			StringBuilder buf=new StringBuilder();
			buf.append("<execiolst>");
			buf.append(item.ToXML(false,true));
			buf.append("</execiolst>");
			ri.setSpecificData(buf.toString());
		}catch(Exception ex)
		{
			throw new EnvironmentInformationSystemSerializationException("Could not merge information system elements",ex);
		}
	}
	
	private static NodeInfo CollectRISpecificData(EnvHintCollection Hints,GCUBERunningInstance ri) throws EnvironmentInformationSystemSerializationException
	{
		try
		{
			NodeInfo nfo=null;
			String sd = ri.getSpecificData();
			if(sd==null || sd.trim().length()==0) return nfo;
			Document doc = XMLUtils.Deserialize(sd);
			List<Element> elems=XMLUtils.GetChildElementsWithName(doc.getDocumentElement(), "element");
			for(Element el : elems)
			{
				nfo=new NodeInfo();
				nfo.FromXML(el);
				GCUBEHostingNode hn=GCubeInformationSystemProvider.GetGHN(Hints, ri.getGHNID());
				nfo.StaticExtensions.putAll(GCubeInformationSystemProvider.ParseGHNEntires(hn));
				break;
			}
			return nfo;
		}catch(Exception ex)
		{
			throw new EnvironmentInformationSystemSerializationException("Could not merge information system elements",ex);
		}
	}
	
	private static Map<String,ExtensionPair> ParseGHNEntires(GCUBEHostingNode node) 
	{
		Map<String,ExtensionPair> maps=new HashMap<String, ExtensionPair>();
		if(node==null) return maps;
		try{maps.put("hn.infrastructure", new ExtensionPair("infrastructure", node.getInfrastructure()));}catch(Exception ex){}
		try{maps.put("hn.country", new ExtensionPair("hn.country", node.getSite().getCountry()));}catch(Exception ex){}
		try{maps.put("hn.domain", new ExtensionPair("hn.domain", node.getSite().getDomain()));}catch(Exception ex){}
		try{maps.put("hn.latitude", new ExtensionPair("hn.latitude", node.getSite().getLatitude()));}catch(Exception ex){}
		try{maps.put("hn.longitude", new ExtensionPair("hn.longitude", node.getSite().getLongitude()));}catch(Exception ex){}
		try{maps.put("hn.location", new ExtensionPair("hn.location", node.getSite().getLocation()));}catch(Exception ex){}
		try{maps.put("hn.architecture.platform", new ExtensionPair("hn.architecture.platform", node.getNodeDescription().getArchitecture().getPlatformType()));}catch(Exception ex){}
		try{maps.put("hn.architecture.smp", new ExtensionPair("hn.architecture.smp", Long.toString(node.getNodeDescription().getArchitecture().getSMPSize())));}catch(Exception ex){}
		try{maps.put("hn.architecture.smt", new ExtensionPair("hn.architecture.smt", Long.toString(node.getNodeDescription().getArchitecture().getSMTSize())));}catch(Exception ex){}
		try{maps.put("hn.benchmark.sf00", new ExtensionPair("hn.benchmark.sf00", Long.toString(node.getNodeDescription().getBenchmark().getSF00())));}catch(Exception ex){}
		try{maps.put("hn.benchmark.si00", new ExtensionPair("hn.benchmark.si00", Long.toString(node.getNodeDescription().getBenchmark().getSI00())));}catch(Exception ex){}
		try{maps.put("hn.load.one_day", new ExtensionPair("hn.load.one_day", Double.toString(node.getNodeDescription().getHistoricalLoad().getLast1Day())));}catch(Exception ex){}
		try{maps.put("hn.load.one_hour", new ExtensionPair("hn.load.one_hour", Double.toString(node.getNodeDescription().getHistoricalLoad().getLast1Hour())));}catch(Exception ex){}
		try{maps.put("hn.load.one_week", new ExtensionPair("hn.load.one_week", Double.toString(node.getNodeDescription().getHistoricalLoad().getLast1Week())));}catch(Exception ex){}
		try{maps.put("hn.load.one_min", new ExtensionPair("hn.load.one_min", Double.toString(node.getNodeDescription().getLoad().getLast1min())));}catch(Exception ex){}
		try{maps.put("hn.load.five_min", new ExtensionPair("hn.load.five_min", Double.toString(node.getNodeDescription().getLoad().getLast5min())));}catch(Exception ex){}
		try{maps.put("hn.load.fifteen_min", new ExtensionPair("hn.load.fifteen_min", Double.toString(node.getNodeDescription().getLoad().getLast15min())));}catch(Exception ex){}
		try{maps.put("hn.disk.size", new ExtensionPair("hn.disk.size", Long.toString(node.getNodeDescription().getLocalAvailableSpace())));}catch(Exception ex){}
		try{maps.put("hn.memory.physical.available", new ExtensionPair("hn.memory.physical.available", Long.toString(node.getNodeDescription().getMemory().getAvailable())));}catch(Exception ex){}
		try{maps.put("hn.memory.physical.size", new ExtensionPair("hn.memory.physical.size", Long.toString(node.getNodeDescription().getMemory().getSize())));}catch(Exception ex){}
		try{maps.put("hn.memory.virtual.size", new ExtensionPair("hn.memory.virtual.size", Long.toString(node.getNodeDescription().getMemory().getVirtualSize())));}catch(Exception ex){}
		try{maps.put("hn.memory.virtual.available", new ExtensionPair("hn.memory.virtual.available", Long.toString(node.getNodeDescription().getMemory().getVirtualAvailable())));}catch(Exception ex){}
		try{maps.put("hostname", new ExtensionPair("hostname", node.getNodeDescription().getName().substring(0, node.getNodeDescription().getName().lastIndexOf(':'))));}catch(Exception ex){}
		try{maps.put("hn.port", new ExtensionPair("hn.port", node.getNodeDescription().getName().substring(node.getNodeDescription().getName().lastIndexOf(':')+1)));}catch(Exception ex){}
		try{maps.put("hn.hostname", new ExtensionPair("hn.hostname", node.getNodeDescription().getName()));}catch(Exception ex){}
		int count=0;
		try{
			for(NetworkAdapter nadp : node.getNodeDescription().getNetworkAdapters())
			{
				try{maps.put("hn.network.adapter."+count+".inbound.ip", new ExtensionPair("hn.network.adapter."+count+".inbound.ip", nadp.getInboundIP()));}catch(Exception ex){}
				try{maps.put("hn.network.adapter."+count+".ip.address", new ExtensionPair("hn.network.adapter."+count+".ip.address", nadp.getIPAddress()));}catch(Exception ex){}
				try{maps.put("hn.network.adapter."+count+".mtu", new ExtensionPair("hn.network.adapter."+count+".mtu", Long.toString(nadp.getMTU())));}catch(Exception ex){}
				try{maps.put("hn.network.adapter."+count+".name", new ExtensionPair("hn.network.adapter."+count+".name", nadp.getName()));}catch(Exception ex){}
				try{maps.put("hn.network.adapter."+count+".outbound.ip", new ExtensionPair("hn.network.adapter."+count+".outbound.ip", nadp.getOutboundIP()));}catch(Exception ex){}
				count+=1;
			}
		}catch(Exception ex){}
		try{maps.put("hn.os.name", new ExtensionPair("hn.os.name", node.getNodeDescription().getOS().getName()));}catch(Exception ex){}
		try{maps.put("hn.os.release", new ExtensionPair("hn.os.release", node.getNodeDescription().getOS().getRelease()));}catch(Exception ex){}
		try{maps.put("hn.os.version", new ExtensionPair("hn.os.version", node.getNodeDescription().getOS().getVersion()));}catch(Exception ex){}
		count=0;
		try{
			long totalBogoMips = 0;
			long totalClockSpeed = 0;
			for(Processor pr : node.getNodeDescription().getProcessors())
			{
				try{maps.put("hn.processor."+count+".bogomips", new ExtensionPair("hn.processor."+count+".bogomips", Double.toString(pr.getBogomips()))); totalBogoMips+=pr.getBogomips();}catch(Exception ex){}
				try{maps.put("hn.processor."+count+".cache.l1", new ExtensionPair("hn.processor."+count+".cache.l1", Long.toString(pr.getCacheL1())));}catch(Exception ex){}
				try{maps.put("hn.processor."+count+".cache.l1d", new ExtensionPair("hn.processor."+count+".cache.l1d", Long.toString(pr.getCacheL1D())));}catch(Exception ex){}
				try{maps.put("hn.processor."+count+".cache.l1i", new ExtensionPair("hn.processor."+count+".cache.l1i", Long.toString(pr.getCacheL1I())));}catch(Exception ex){}
				try{maps.put("hn.processor."+count+".cache.l2", new ExtensionPair("hn.processor."+count+".cache.l2", Long.toString(pr.getCacheL2())));}catch(Exception ex){}
				try{maps.put("hn.processor."+count+".clockspeed", new ExtensionPair("hn.processor."+count+".clockspeed", Double.toString(pr.getClockSpeedMHZ()))); totalClockSpeed+=pr.getClockSpeedMHZ();}catch(Exception ex){}
				try{maps.put("hn.processor."+count+".family", new ExtensionPair("hn.processor."+count+".clockspeed", pr.getFamily()));}catch(Exception ex){}
				try{maps.put("hn.processor."+count+".model", new ExtensionPair("hn.processor."+count+".model", pr.getModel()));}catch(Exception ex){}
				try{maps.put("hn.processor."+count+".model_name", new ExtensionPair("hn.processor."+count+".model_name", pr.getModelName()));}catch(Exception ex){}
				try{maps.put("hn.processor."+count+".vendor", new ExtensionPair("hn.processor."+count+".vendor", pr.getVendor()));}catch(Exception ex){}
				count+=1;
			}
			maps.put("hn.processor.count", new ExtensionPair("hn.processor.count", Integer.toString(count)));
			maps.put("hn.processor.total_bogomips", new ExtensionPair("hn.processor.total_bogomips", Long.toString(totalBogoMips)));
			maps.put("hn.processor.total_clockspeed", new ExtensionPair("hn.processor.total_clockspeed", Long.toString(totalClockSpeed)));
		}catch(Exception ex){}
		try{maps.put("hn.status", new ExtensionPair("hn.status", node.getNodeDescription().getStatus().toString()));}catch(Exception ex){}
		count=0;
		try{
			for(StorageDevice sd : node.getNodeDescription().getStorageDevices())
			{
				try{maps.put("hn.disk.device."+count+".name", new ExtensionPair("hn.disk.device."+count+".name", sd.getName()));}catch(Exception ex){}
				try{maps.put("hn.disk.device."+count+".size", new ExtensionPair("hn.disk.device."+count+".size", Long.toString(sd.getSize())));}catch(Exception ex){}
				try{maps.put("hn.disk.device."+count+".transfer_rate", new ExtensionPair("hn.disk.device."+count+".transfer_rate", Long.toString(sd.getTransferRate())));}catch(Exception ex){}
				try{maps.put("hn.disk.device."+count+".type", new ExtensionPair("hn.disk.device."+count+".type", sd.getType()));}catch(Exception ex){}
				int newCount=0;
				try{
					for(StoragePartition sdp : sd.getPartitions().values())
					{
						try{maps.put("hn.disk.device."+count+".partition."+newCount+".name", new ExtensionPair("hn.disk.device."+count+".partition."+newCount+".name", sdp.getName()));}catch(Exception ex){}
						try{maps.put("hn.disk.device."+count+".partition."+newCount+".read_rate", new ExtensionPair("hn.disk.device."+count+".partition."+newCount+".read_rate",Long.toString(sdp.getReadRate())));}catch(Exception ex){}
						try{maps.put("hn.disk.device."+count+".partition."+newCount+".size", new ExtensionPair("hn.disk.device."+count+".partition."+newCount+".size", sdp.getSize()));}catch(Exception ex){}
						try{maps.put("hn.disk.device."+count+".partition."+newCount+".write_rate", new ExtensionPair("hn.disk.device."+count+".partition."+newCount+".write_rate",Long.toString(sdp.getWriteRate())));}catch(Exception ex){}
						int deepCount=0;
						try{
							for(FileSystem sdpfs : sdp.getFileSystems().values())
							{
								try{maps.put("hn.disk.device."+count+".partition."+newCount+".filesystem."+deepCount+".name", new ExtensionPair("hn.disk.device."+count+".partition."+newCount+".filesystem."+deepCount+".name",sdpfs.getName()));}catch(Exception ex){}
								try{maps.put("hn.disk.device."+count+".partition."+newCount+".filesystem."+deepCount+".root", new ExtensionPair("hn.disk.device."+count+".partition."+newCount+".filesystem."+deepCount+".root",sdpfs.getRoot()));}catch(Exception ex){}
								try{maps.put("hn.disk.device."+count+".partition."+newCount+".filesystem."+deepCount+".size", new ExtensionPair("hn.disk.device."+count+".partition."+newCount+".filesystem."+deepCount+".size",Long.toString(sdpfs.getSize())));}catch(Exception ex){}
								try{maps.put("hn.disk.device."+count+".partition."+newCount+".filesystem."+deepCount+".type", new ExtensionPair("hn.disk.device."+count+".partition."+newCount+".filesystem."+deepCount+".type",sdpfs.getType()));}catch(Exception ex){}
								deepCount+=1;
							}
						}catch(Exception ex){}
						newCount+=1;
					}
				}catch(Exception ex){}
				count+=1;
			}
		}catch(Exception ex){}
		try{maps.put("hn.uptime", new ExtensionPair("hn.status", node.getNodeDescription().getUptime()));}catch(Exception ex){}
		try{
			for(GCUBEHostingNode.Package pkg : node.getDeployedPackages())
			{
				String identifier=pkg.getServiceClass()+"."+pkg.getServiceName()+"."+pkg.getPackageName();
				try{maps.put("software."+identifier+".deployed", new ExtensionPair("software."+identifier+".deployed", "true"));}catch(Exception ex){}
				try{maps.put("software."+identifier+".service_version", new ExtensionPair("software."+identifier+".service_version", node.getSite().getLocation()));}catch(Exception ex){}
				try{maps.put("software."+identifier+".package_version", new ExtensionPair("software."+identifier+".package_version", node.getSite().getLocation()));}catch(Exception ex){}
			}
		}catch(Exception ex){}
		try{
			for(Variable var : node.getNodeDescription().getRuntime().getVariables())
			{
				try{maps.put(var.getKey(), new ExtensionPair(var.getKey(), var.getValue()));}catch(Exception ex){}

			}
		}catch(Exception ex){}
		return maps;
	}
	
	private static String GetQualifierAttributeValue(QualifierAttribute attr, String Qualifier)
	{
		String [] attrs = Qualifier.trim().split(",");
		for(String at : attrs)
		{
			String [] parts = at.trim().split(":");
			if(parts.length!=2) continue;
			QualifierAttribute qatr;
			try
			{
				qatr=QualifierAttribute.valueOf(parts[0].trim());
			}catch(Exception ex){continue;}
			if(qatr.equals(attr))return parts[1].trim();
		}
		return null;
	}

	private static List<GCUBERunningInstance> CollectRIProfiles(EnvHintCollection Hints,String ServiceClass, String ServiceName) throws Exception
	{
		String scopeID=GCubeInformationSystemProvider.GetActionScope(Hints);
		if(scopeID==null) throw new EnvironmentInformationSystemException("No scope specified");
		ISClient client =  GHNContext.getImplementation(ISClient.class);
		GCUBERIQuery query = client.getQuery(GCUBERIQuery.class);
		query.addAtomicConditions(new AtomicCondition("/Profile/ServiceClass",ServiceClass));
		query.addAtomicConditions(new AtomicCondition("/Profile/ServiceName",ServiceName));
		List<GCUBERunningInstance> result = client.execute(query, GCUBEScope.getScope(scopeID));
		return result;
	}
	
	private static List<String> CollectInstance(EnvHintCollection Hints,String ServiceClass, String ServiceName,String PortType) throws Exception
	{
		List<GCUBERunningInstance> result = GCubeInformationSystemProvider.CollectRIProfiles(Hints, ServiceClass, ServiceName);
		List<String> instanceeps=new ArrayList<String>();
		for(GCUBERunningInstance res : result)
		{
			List<Endpoint> eps= null;
			eps= res.getAccessPoint().getRunningInstanceInterfaces().getEndpoint();
			for(Endpoint ep : eps)
			{
				if(PortType!=null && PortType.trim().length()!=0 && !ep.getValue().endsWith(PortType)) continue;
				logger.debug("Retrieved epr "+ep.getValue());
				instanceeps.add(ep.getValue());
			}
		}
		return instanceeps;
	}
	
	private static NodeInfo CollectNodeInfo(EnvHintCollection Hints,String ID) throws Exception
	{
		String scopeID=GCubeInformationSystemProvider.GetActionScope(Hints);
		if(scopeID==null) throw new EnvironmentInformationSystemException("No scope specified");
		ISClient client =  GHNContext.getImplementation(ISClient.class);
		GCUBERIQuery query=client.getQuery(GCUBERIQuery.class);
		query.addAtomicConditions(new AtomicCondition("/Profile/ServiceName",GCubeInformationSystemProvider.GetRIContainerServiceName(Hints)));
		query.addAtomicConditions(new AtomicCondition("/Profile/ServiceClass",GCubeInformationSystemProvider.GetRIContainerServiceClass(Hints)));
		query.addGenericCondition("$result/Profile/GHN[@UniqueID = '"+ID+"']");
		List<GCUBERunningInstance> result=client.execute(query, GCUBEScope.getScope(scopeID));
		for(GCUBERunningInstance res : result)
		{
			NodeInfo elem = GCubeInformationSystemProvider.CollectRISpecificData(Hints,res);
			if(elem!=null)
			{
				if(GetResolveLocal(Hints) && elem.getExtension("hostname") != null && elem.getExtension("hn.port") != null)
				{
					if(elem.getExtension("hostname").equals(GetLocalHostname())
							&& Integer.parseInt(elem.getExtension("hn.port")) == GetLocalPort())
						elem.markLocal();
				}
				return elem;
			}
		}
		return null;
	}
	
	private static List<NodeInfo> CollectNodeInfo(EnvHintCollection Hints,String Requirments, String Rank) throws Exception
	{
		List<NodeInfo> nodes=GCubeInformationSystemProvider.GetExecutionEngineInstances(Hints);
		List<NodeInfo> pickedNodes = new ArrayList<NodeInfo>();
		MatchParser parser=new MatchParser(Requirments);
		for(NodeInfo nfo : nodes)
		{
			boolean match=true;
			for(Map.Entry<String, String> entry : parser.requirments.entrySet())
			{
				if(entry.getKey()==null) break;
				if(entry.getValue()==null) break;
				String value=nfo.getExtension(entry.getKey());
				if((value==null) || (!value.trim().equalsIgnoreCase(entry.getValue().trim())))
				{
					match=false;
					break;
				}
			}
			if(match) pickedNodes.add(nfo);
		}
		return pickedNodes;
	}
	
	private static GCUBEHostingNode GetGHN(EnvHintCollection Hints,String id) throws Exception
	{
		String scopeID=GCubeInformationSystemProvider.GetActionScope(Hints);
		if(scopeID==null) throw new EnvironmentInformationSystemException("No scope specified");
		ISClient client =  GHNContext.getImplementation(ISClient.class);
		GCUBEGHNQuery query=client.getQuery(GCUBEGHNQuery.class);
		query.addAtomicConditions(new AtomicCondition("/ID",id));
		List<GCUBEHostingNode> result=client.execute(query, GCUBEScope.getScope(scopeID));
		if(result.size()>0) return result.get(0);
		return null;
	}
	
	private static List<NodeInfo> GetExecutionEngineInstances(EnvHintCollection Hints) throws Exception
	{
		String scopeID=GCubeInformationSystemProvider.GetActionScope(Hints);
		if(scopeID==null) throw new EnvironmentInformationSystemException("No scope specified");
		ISClient client =  GHNContext.getImplementation(ISClient.class);
		GCUBERIQuery query=client.getQuery(GCUBERIQuery.class);
		query.addAtomicConditions(new AtomicCondition("/Profile/ServiceName",GCubeInformationSystemProvider.GetRIContainerServiceName(Hints)));
		query.addAtomicConditions(new AtomicCondition("/Profile/ServiceClass",GCubeInformationSystemProvider.GetRIContainerServiceClass(Hints)));
		List<GCUBERunningInstance> result=client.execute(query, GCUBEScope.getScope(scopeID));
		List<NodeInfo> instances=new ArrayList<NodeInfo>();
		for(GCUBERunningInstance res : result)
		{
			NodeInfo elem = GCubeInformationSystemProvider.CollectRISpecificData(Hints,res);
			if(elem!=null) instances.add(elem);
		}
		return instances;
	}
	
	private static String GetRIContainerServiceClass(EnvHintCollection Hints)
	{
		if(Hints==null) return GCubeInformationSystemProvider.DefaultInformationSystemRIContainerServiceClass;
		if(!Hints.HintExists(GCubeInformationSystemProvider.InformationSystemRIContainerServiceClassHintName)) return GCubeInformationSystemProvider.DefaultInformationSystemRIContainerServiceClass;
		return Hints.GetHint(GCubeInformationSystemProvider.InformationSystemRIContainerServiceClassHintName).Hint.Payload;
	}
	
	private static String GetRIContainerServiceName(EnvHintCollection Hints)
	{
		if(Hints==null) return GCubeInformationSystemProvider.DefaultInformationSystemRIContainerServiceName;
		if(!Hints.HintExists(GCubeInformationSystemProvider.InformationSystemRIContainerServiceNameHintName)) return GCubeInformationSystemProvider.DefaultInformationSystemRIContainerServiceName;
		return Hints.GetHint(GCubeInformationSystemProvider.InformationSystemRIContainerServiceNameHintName).Hint.Payload;
	}
	
	private static String GetActionScope(EnvHintCollection Hints)
	{
		if(Hints==null) return null;
		if(!Hints.HintExists(GCubeInformationSystemProvider.GCubeActionScopeHintName)) return null;
		return Hints.GetHint(GCubeInformationSystemProvider.GCubeActionScopeHintName).Hint.Payload;
	}
	
	private static NodeSelector GetNodeSelector(EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		if(Hints==null) return null;
		if(!Hints.HintExists(GCubeInformationSystemProvider.NodeSelectorHintName)) return InformationSystem.GetDefaultNodeSelector();
		String nodeSelectorClassName =  Hints.GetHint(GCubeInformationSystemProvider.NodeSelectorHintName).Hint.Payload;
		try { return (NodeSelector)Class.forName(nodeSelectorClassName).newInstance(); }
		catch(Exception e) { throw new EnvironmentInformationSystemException("Could not construct node selector", e); }
	}

	private static Boolean GetResolveLocal(EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		if(Hints==null) return null;
		if(!Hints.HintExists(GCubeInformationSystemProvider.ResolveLocalNodeHintName)) return GCubeInformationSystemProvider.DefaultResolveLocalNode;
		return Boolean.parseBoolean(Hints.GetHint(GCubeInformationSystemProvider.ResolveLocalNodeHintName).Hint.Payload);
	}
	
	private static String GetLocalHostname()
	{
		if(GCubeInformationSystemProvider.LocalHostname == null) GCubeInformationSystemProvider.LocalHostname = GHNContext.getContext().getHostname();
		return GCubeInformationSystemProvider.LocalHostname;
	}
	
	private static int GetLocalPort()
	{
		if(GCubeInformationSystemProvider.LocalPort == null) GCubeInformationSystemProvider.LocalPort = GHNContext.getContext().getPort();
		return GCubeInformationSystemProvider.LocalPort;
	}
	private static int GetNumberOfTries(EnvHintCollection Hints)
	{
		if(Hints==null || !Hints.HintExists(GCubeInformationSystemProvider.RetryOnErrorCountHintName))
			return GCubeInformationSystemProvider.DefaultRetryOnError+1;
		try
		{
			return Integer.parseInt(Hints.GetHint(GCubeInformationSystemProvider.RetryOnErrorCountHintName).Hint.Payload)+1;
		}catch(Exception ex)
		{
			logger.warn("provided hint not valid. returning default value");
			return GCubeInformationSystemProvider.DefaultRetryOnError+1;
		}
	}
	
	private static int GetSleepBetweenInterval(EnvHintCollection Hints)
	{
		if(Hints==null || !Hints.HintExists(GCubeInformationSystemProvider.RetryOnErrorIntervalHintName))
			return 0;
		try
		{
			return randGen.nextInt(Integer.parseInt(Hints.GetHint(GCubeInformationSystemProvider.RetryOnErrorIntervalHintName).Hint.Payload));
		}catch(Exception ex)
		{
			logger.warn("provided hint not valid. returning default value");
			return 0;
		}
		
	}

	@Override
	public String CreateGenericResource(String content, gr.uoa.di.madgik.environment.is.Query attributes, EnvHintCollection Hints) throws EnvironmentInformationSystemException 
	{
		// TODO Auto-generated method stub
		throw new EnvironmentInformationSystemException("Operation not supported");
	}
	
	@Override
	public void UpdateGenericResource(String id, String content, gr.uoa.di.madgik.environment.is.Query query, EnvHintCollection Hints) throws EnvironmentInformationSystemException 
	{
		// TODO Auto-generated method stub
		throw new EnvironmentInformationSystemException("Operation not supported");
	}
	
	@Override
	public void DeleteGenericResource(String id, EnvHintCollection Hints)
	{
		//TODO
	}

	@Override
	public String GetLocalNodeHostName() throws EnvironmentInformationSystemException {
		return GHNContext.getContext().getHostname();
	}

	@Override
	public String GetLocalNodePort() throws EnvironmentInformationSystemException {
		return new Integer(GHNContext.getContext().getPort()).toString();
	}

	@Override
	public String GetLocalNodePE2ngPort(EnvHintCollection Hints) throws EnvironmentInformationSystemException {
		if (InformationSystem.GetNode(GHNContext.getContext().getGHNID(), Hints) == null)
			return null;
		return GetNode(GHNContext.getContext().getGHNID(), Hints).getExtension("pe2ng.port");
	}
}
