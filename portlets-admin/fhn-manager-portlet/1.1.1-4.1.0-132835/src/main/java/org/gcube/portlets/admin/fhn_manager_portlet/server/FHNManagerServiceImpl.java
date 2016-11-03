package org.gcube.portlets.admin.fhn_manager_portlet.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.admin.fhn_manager_portlet.client.FhnManagerService;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.Constants;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.communication.Operation;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.communication.OperationTicket;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.communication.ProgressMessage;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.communication.UnexpectedException;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.Storable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class FHNManagerServiceImpl extends RemoteServiceServlet implements FhnManagerService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3456508651362843402L;
	private static final Logger logger = LoggerFactory.getLogger(FHNManagerServiceImpl.class);

	public static VMManagerServiceInterface getService(){
		return new RemoteServiceImpl();
//		return new MockRemote();
	}


	@Override
	public Set<Storable> listResources(ObjectType resourceType,Map<String, String> filters) throws UnexpectedException {
		try{
			String serviceProfileId=null;
			String vmProviderId=null;
			String vmTemplateId=null;
			if(filters!=null){
				if(filters.containsKey(Constants.SERVICE_PROFILE_ID)) serviceProfileId=filters.get(Constants.SERVICE_PROFILE_ID);
				if(filters.containsKey(Constants.VM_PROVIDER_ID)) vmProviderId=filters.get(Constants.VM_PROVIDER_ID);
				if(filters.containsKey(Constants.VM_TEMPLATE_ID)) vmTemplateId=filters.get(Constants.VM_TEMPLATE_ID);
			}
			logger.info("Gonna execute request to service, scope provider instance value : "+ScopeProvider.instance.get());
			logger.info("ASL "+getASLSession().getScope());
			switch(resourceType){
			
			case REMOTE_NODE : return new HashSet<Storable>(getService().getNodes(serviceProfileId, vmProviderId));
			case SERVICE_PROFILE : return new HashSet<Storable>(getService().getServiceProfiles());
			case VM_PROVIDER : return new HashSet<Storable>(getService().getVMProviders(serviceProfileId, vmTemplateId));
			case VM_TEMPLATES : return new HashSet<Storable> (getService().getVMTemplates(serviceProfileId, vmProviderId)); 
			}
			throw new Exception("Not recognized type "+resourceType);
		}catch(Exception e){
			logger.error("ERROR while listing resources : ",e);
			throw new UnexpectedException(e.getMessage());
		}
	}

	@Override
	public OperationTicket createObject(ObjectType type,Map<String,String> fields) throws UnexpectedException {	
		Map<String,Object> parameters=new HashMap<String,Object>();
		parameters.put(TicketedExecutor.OBJECT_TYPE, type);
		if(fields!=null) 
			for(Entry<String,String> entry:fields.entrySet())
			parameters.put(entry.getKey(), entry.getValue());
		return TicketedExecutor.submitRequest(getASLSession(), parameters,Operation.CREATE_OBJECT);
	}
	
	@Override
	public OperationTicket removeObject(ObjectType type, String toRemoveId,Map<String,Boolean> flags)
			throws UnexpectedException {
		Map<String,Object> parameters=new HashMap<String,Object>();
		parameters.put(TicketedExecutor.OBJECT_TYPE, type);
		parameters.put(TicketedExecutor.OBJECT_ID, toRemoveId);
		if(flags!=null) 
			for(Entry<String,Boolean> entry:flags.entrySet())
			parameters.put(entry.getKey(), entry.getValue());
		return TicketedExecutor.submitRequest(getASLSession(),parameters,Operation.DESTROY_OBJECT);
	}

	//**************************************************** REMOTE NODES


	@Override
	public OperationTicket startNode(String toStartId) {		
		return TicketedExecutor.submitRequest(getASLSession(), 
				Collections.singletonMap(TicketedExecutor.OBJECT_ID, (Object)toStartId), Operation.START_NODE);
	}

	@Override
	public OperationTicket stopNode(String toStopId) {
		return TicketedExecutor.submitRequest(getASLSession(), 
				Collections.singletonMap(TicketedExecutor.OBJECT_ID, (Object)toStopId), Operation.STOP_NODE);
	}


	@Override
	public ProgressMessage getProgress(OperationTicket ticket) throws UnexpectedException {
		return TicketedExecutor.getProgress(ticket.getId());
	}


	private ASLSession getASLSession() {
		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		ASLSession session=null;
		if (user == null) {
			logger.warn("USER IS NULL setting test.user");
			user = "test.user";
			session=SessionManager.getInstance().getASLSession(sessionID, user);
			session.setScope("/gcube/devsec");
		}
		else {
			logger.info("LIFERAY PORTAL DETECTED user=" + user);
			session=SessionManager.getInstance().getASLSession(sessionID, user);
		}
		return session;
	}


//	@Override
//	public String getXMLObject(ObjectType type, String id)throws UnexpectedException {
//		try{
//			return getService().describeResource(type, id);
//		}catch(Exception e){
//			logger.error("Unable to describe resource "+type.getLabel()+", ID "+id,e);
//			throw new UnexpectedException(e.getMessage());
//		}
//	}
	
	
	@Override
	public OperationTicket getDetails(Storable object)
			throws UnexpectedException {
		HashMap<String,Object> params=new HashMap<String,Object>();
		params.put(TicketedExecutor.OBJECT_PARAMETER, object);		
		return TicketedExecutor.submitRequest(getASLSession(),params,Operation.GATHER_INFORMATION); 
	}
}
