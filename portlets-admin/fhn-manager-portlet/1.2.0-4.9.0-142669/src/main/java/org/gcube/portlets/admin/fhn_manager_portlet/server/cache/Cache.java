package org.gcube.portlets.admin.fhn_manager_portlet.server.cache;

import java.rmi.RemoteException;
import java.util.HashSet;

import org.gcube.portlets.admin.fhn_manager_portlet.server.Context;
import org.gcube.portlets.admin.fhn_manager_portlet.server.cache.CachedCollection.ProviderCache;
import org.gcube.portlets.admin.fhn_manager_portlet.server.cache.CachedCollection.RemoteNodesCache;
import org.gcube.portlets.admin.fhn_manager_portlet.server.cache.CachedCollection.ServiceProfileCache;
import org.gcube.portlets.admin.fhn_manager_portlet.server.cache.CachedCollection.TemplateCache;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.RemoteNode;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.ServiceProfile;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.VMProvider;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.VMTemplate;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.exceptions.ServiceException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Cache {

	private ProviderCache providers;
	private RemoteNodesCache remoteNodes;
	private TemplateCache templates;
	private ServiceProfileCache profiles;
	
	private long nodesTTL=0l;
	private long providersTTL=0l;
	private long templatesTTL=0l;
	private long profilesTTL;
	
	public Cache(Context context) {
		super();
		log.debug("Creating new cache with {} ",context);
		this.nodesTTL = context.getNodesCacheTTL();
		this.providersTTL = context.getProvidersCacheTTL();
		this.templatesTTL = context.getTemplatesCacheTTL();
		this.profilesTTL = context.getServiceProfilesCacheTTL();
		providers=new ProviderCache(providersTTL);
		remoteNodes=new RemoteNodesCache(nodesTTL);
		templates=new TemplateCache(templatesTTL);
		profiles=new ServiceProfileCache(profilesTTL);
	}

	
	public HashSet<RemoteNode> getNodes(String serviceProfileId,String vmProviderId) throws RemoteException, ServiceException{		
		HashSet<RemoteNode> toReturn= new HashSet<RemoteNode>();
		for(RemoteNode node:remoteNodes.getElements()){				
			if((serviceProfileId==null||(node.getVmTemplate().equals(serviceProfileId))) // no filter or matching
					&&
			  (vmProviderId==null||(node.getVmProviderId().equals(vmProviderId)))	// no filter or matching
					)toReturn.add(enhance(node));
		}
		return toReturn;
	}
	
	public HashSet<VMProvider> getProviders(String serviceProfileId,String vmTemplateId) throws RemoteException, ServiceException{
		
		//TODO implement filters 
		
		
//		if(byServiceProfile){
//			profiles.getElementById(serviceProfileId).get	
//		}
		
		HashSet<VMProvider> toReturn= providers.getElements();
		
		for(VMProvider provider:toReturn){
			enhance(provider);
		}
		return toReturn;
	}
	
	public HashSet<VMTemplate> getTemplates(String serviceProfileId,String vmProviderId) throws RemoteException, ServiceException{		
		
		
		//TODO implment filters
		
		HashSet<VMTemplate> toReturn= templates.getElements();
		for(VMTemplate template:toReturn){
			enhance(template);
		}
		return toReturn;
	}
	
	
	public HashSet<ServiceProfile> getProfiles() throws RemoteException, ServiceException{
		HashSet<ServiceProfile> toReturn= profiles.getElements();
		for(ServiceProfile profile:toReturn){
			enhance(profile);
		}
		return toReturn;
	}
	
	
	
	public void invalidateNodesCache(){
		remoteNodes.invalidate();
	}
	public void invalidateProvidersCache(){
		providers.invalidate();
	}
	public void invalidateTemplatesCache(){
		templates.invalidate();
	}
	
	public void invlidateProfilesCache(){
		profiles.invalidate();
	}
	public RemoteNode getNodeById(String id) throws RemoteException, ServiceException{
		return enhance(remoteNodes.getElementById(id));
	}
	
	public VMTemplate getTemplateById(String id) throws RemoteException, ServiceException{
		return enhance(templates.getElementById(id));
	}
	
	public VMProvider getProviderById(String id) throws RemoteException, ServiceException{
		return enhance(providers.getElementById(id));
	}
	
	public ServiceProfile getProfileById(String id) throws RemoteException, ServiceException{
		return enhance(profiles.getElementById(id));
	}
	
	
	
	
	
	//**** ENHANCED INFO LOAD
	
	
	private RemoteNode enhance(RemoteNode toEnhance){
		try{
			log.debug("loading info for node {} ",toEnhance);			
			toEnhance.setServiceProfile(profiles.getElementById(toEnhance.getServiceProfileId()));
			toEnhance.setVmTemplate(templates.getElementById(toEnhance.getVmTemplateId()));
			toEnhance.setVmProvider(providers.getElementById(toEnhance.getVmProviderId()));
		}catch(Exception e){
			log.warn("Unable to enhance info on node {}.",toEnhance,e);
		}
		return toEnhance;
	}
	
	private VMTemplate enhance(VMTemplate toEnhance){
		try{
			log.debug("loading info for template {} ",toEnhance);
			toEnhance.setProvider(providers.getElementById(toEnhance.getProviderId()));
		}catch(Exception e){
			log.warn("Unable to enhance info on node {}.",toEnhance,e);
		}
		return toEnhance;
	}
	
	private VMProvider enhance(VMProvider toEnhance){
		return toEnhance;
	}
	
	private ServiceProfile enhance(ServiceProfile toEnhance){
		return toEnhance;
	}
}
