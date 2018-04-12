package org.gcube.portlets.admin.fhn_manager_portlet.server.cache;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;

import org.gcube.portlets.admin.fhn_manager_portlet.server.FHNManagerServiceImpl;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.RemoteNode;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.ServiceProfile;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.Storable;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.VMProvider;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.VMTemplate;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.exceptions.ServiceException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CachedCollection <T extends Storable>{

	private long lastUpdate=0l;

	private HashMap<String,T> theCollection=new HashMap<String,T>();

	private long TTL;

	public CachedCollection(long TTL) {
		this.TTL=TTL;
		log.debug("Instantiating {}. TTL {} ",getCollectionName(),TTL);
	}

	public HashSet<T> getElements() throws RemoteException, ServiceException{
		log.debug("Collection {} : returning elements.",getCollectionName());
		checkAndUpdate();
		return new HashSet<T>(theCollection.values());
	}


	public T getElementById(String id)throws RemoteException, ServiceException{
		log.debug("Collection {} : get Element By ID {} ",getCollectionName(),id);
		checkAndUpdate();
		return theCollection.get(id);
	}

	private synchronized void checkAndUpdate()throws RemoteException, ServiceException{
		if(System.currentTimeMillis()-lastUpdate>TTL){
			log.debug("Collection {} is expired TTL {}. Reloading elements",getCollectionName(),TTL);
			theCollection=retrieveElements();
			log.debug("Collection {} is now up to date with {} elements.",getCollectionName(),theCollection.size());
			lastUpdate=System.currentTimeMillis();
		}
	}
	
	protected abstract HashMap<String,T> retrieveElements() throws RemoteException, ServiceException;

	protected abstract String getCollectionName();
	public void invalidate(){
		lastUpdate=0l;
	}


	//************************************ IMPLEMENTATIONS
	public static class RemoteNodesCache extends CachedCollection<RemoteNode>{

		private static final String COLLECTION_NAME="RemoteNodes Cache";

		@Override
		protected String getCollectionName() {
			return COLLECTION_NAME;
		}

		public RemoteNodesCache(long TTL) {
			super(TTL);
		}

		@Override
		protected HashMap<String, RemoteNode> retrieveElements() throws RemoteException, ServiceException {

			HashMap<String,RemoteNode> nodes=new HashMap<>();
			for(RemoteNode node : FHNManagerServiceImpl.getService().getNodes(null, null)){
				nodes.put(node.getId(),node);
			};
			return nodes;
		}
	}

	public static class TemplateCache extends CachedCollection<VMTemplate>{

		private static final String COLLECTION_NAME="Templates Cache";

		@Override
		protected String getCollectionName() {
			return COLLECTION_NAME;
		}

		public TemplateCache(long TTL){
			super(TTL);
		}

		@Override
		protected HashMap<String, VMTemplate> retrieveElements() throws RemoteException, ServiceException {
			HashMap<String,VMTemplate> templates=new HashMap<>();
			for(VMTemplate template : FHNManagerServiceImpl.getService().getVMTemplates(null, null)){
				templates.put(template.getId(),template);
			};
			return templates;
		}
	}	

	public static class ProviderCache extends CachedCollection<VMProvider>{

		private static final String COLLECTION_NAME="Providers Cache";

		@Override
		protected String getCollectionName() {
			return COLLECTION_NAME;
		}

		public ProviderCache(long TTL) {
			super(TTL);			
		}

		@Override
		protected HashMap<String, VMProvider> retrieveElements() throws RemoteException, ServiceException {
			HashMap<String,VMProvider> providers=new HashMap<>();
			for(VMProvider provider : FHNManagerServiceImpl.getService().getVMProviders(null, null)){
				providers.put(provider.getId(),provider);
			};
			return providers;
		}
	}


	public static class ServiceProfileCache extends CachedCollection<ServiceProfile>{

		private static final String COLLECTION_NAME="Service Profiles Cache";

		@Override
		protected String getCollectionName() {
			return COLLECTION_NAME;
		}

		public ServiceProfileCache(long TTL) {
			super(TTL);		
		}

		@Override
		protected HashMap<String, ServiceProfile> retrieveElements() throws RemoteException, ServiceException {
			HashMap<String,ServiceProfile> providers=new HashMap<>();
			for(ServiceProfile profile : FHNManagerServiceImpl.getService().getServiceProfiles()){
				providers.put(profile.getId(),profile);
			};
			return providers;
		}
	}



}
