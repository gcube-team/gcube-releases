package org.gcube.portlets.admin.fhn_manager_portlet.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.RemoteNode;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.RemoteNodeStatus;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.ServiceProfile;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.Storable;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.VMProvider;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.VMTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelTranslation {

	private static SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-YYYY HH:mm:ss");
	
	
	private static final Logger logger = LoggerFactory.getLogger(ModelTranslation.class);
	
	
	public static Storable toClient(Object toTranslate) throws ParseException{
		if(toTranslate==null) throw new RuntimeException("Object is null.");
		if(toTranslate instanceof org.gcube.resources.federation.fhnmanager.api.type.ServiceProfile) return toClient((org.gcube.resources.federation.fhnmanager.api.type.ServiceProfile)toTranslate);
		if(toTranslate instanceof org.gcube.resources.federation.fhnmanager.api.type.ResourceTemplate) return toClient((org.gcube.resources.federation.fhnmanager.api.type.ResourceTemplate)toTranslate);
		if(toTranslate instanceof org.gcube.resources.federation.fhnmanager.api.type.VMProvider) return toClient((org.gcube.resources.federation.fhnmanager.api.type.VMProvider)toTranslate);
		if(toTranslate instanceof org.gcube.resources.federation.fhnmanager.api.type.Node) return toClient((org.gcube.resources.federation.fhnmanager.api.type.Node)toTranslate);
		throw new RuntimeException("Unable to translate type "+toTranslate.getClass());
	}
	
	
	
	
	
	
	// Entity
	
	public static ServiceProfile toClient(org.gcube.resources.federation.fhnmanager.api.type.ServiceProfile toTranslate) throws ParseException{
	
		return new ServiceProfile(toTranslate.getId(),toTranslate.getVersion(),toTranslate.getDescription(),formatter.parse(toTranslate.getCreationDate())); 
	}
	
	public static VMTemplate toClient(org.gcube.resources.federation.fhnmanager.api.type.ResourceTemplate toTranslate){
		//public VMTemplate(String id, String name, String cores, String memory,String providerId)
		
		return new VMTemplate(toTranslate.getId(),
				toTranslate.getName(),toTranslate.getCores(),toTranslate.getMemory(),toTranslate.getVmProvider().getRefId());
	}
	public static VMProvider toClient(org.gcube.resources.federation.fhnmanager.api.type.VMProvider toTranslate){
//		public VMProvider(String name, String url,String id)
		return new VMProvider(toTranslate.getName(), toTranslate.getEndpoint(), toTranslate.getId());
	}
	
	public static RemoteNode toClient(org.gcube.resources.federation.fhnmanager.api.type.Node toTranslate){
		
		RemoteNode toReturn=new RemoteNode();
		toReturn.setId(toTranslate.getId());
		toReturn.setHost(toTranslate.getHostname());
		
		if(toTranslate.getWorkload()!=null){
			toReturn.setAllTimeAverageWorkload(toTranslate.getWorkload().getAllTimeAverageWorkload());
			toReturn.setLastDayWorkload(toTranslate.getWorkload().getLastDayWorkload());
			toReturn.setLastHourWorkload(toTranslate.getWorkload().getLastHourWorkload());
			toReturn.setNowWorkload(toTranslate.getWorkload().getNowWorkload());
		}
		toReturn.setServiceProfileId(toTranslate.getServiceProfile().getRefId());
		toReturn.setStatus(RemoteNodeStatus.valueOf(toTranslate.getStatus()));
		toReturn.setVmProviderId(toTranslate.getVmProvider().getRefId());
		toReturn.setVmTemplate(toTranslate.getResourceTemplate().getRefId());
		return toReturn;		
		
	}
	
	
	// Collections
	
	
	public static List<ServiceProfile> toServiceProfiles(Collection<org.gcube.resources.federation.fhnmanager.api.type.ServiceProfile> toTranslate){
		ArrayList<ServiceProfile> toReturn=new ArrayList<ServiceProfile>();
		for(org.gcube.resources.federation.fhnmanager.api.type.ServiceProfile profile:toTranslate)
			if(profile!=null)
			try{
				toReturn.add(toClient(profile));
			}catch(Exception e){
				logger.warn("Skipped service profile "+profile,e);
			}
		return toReturn;
	}
	
	
	public static List<VMTemplate> toVMTemplates(Collection<org.gcube.resources.federation.fhnmanager.api.type.ResourceTemplate> toTranslate){
		ArrayList<VMTemplate> toReturn=new ArrayList<VMTemplate>();
		for(org.gcube.resources.federation.fhnmanager.api.type.ResourceTemplate profile:toTranslate)
			if(profile!=null)
				try{
					toReturn.add(toClient(profile));
				}catch(Exception e){
					logger.warn("Skipped template "+profile,e);
				}
			return toReturn;
	}
	
	
	public static List<VMProvider> toVMProviders(Collection<org.gcube.resources.federation.fhnmanager.api.type.VMProvider> toTranslate){
		ArrayList<VMProvider> toReturn=new ArrayList<VMProvider>();
		for(org.gcube.resources.federation.fhnmanager.api.type.VMProvider profile:toTranslate)
			if(profile!=null)
				try{
					toReturn.add(toClient(profile));
				}catch(Exception e){
					logger.warn("Skipped provider "+profile,e);
				}
			return toReturn;
	}
	
	
	
	public static List<RemoteNode> toRemoteNodes(Collection<org.gcube.resources.federation.fhnmanager.api.type.Node> toTranslate){
		ArrayList<RemoteNode> toReturn=new ArrayList<RemoteNode>();
		for(org.gcube.resources.federation.fhnmanager.api.type.Node profile:toTranslate)
			if(profile!=null)
				try{
					toReturn.add(toClient(profile));
				}catch(Exception e){
					logger.warn("Skipped service profile "+profile,e);
				}
			return toReturn;
	}
}
