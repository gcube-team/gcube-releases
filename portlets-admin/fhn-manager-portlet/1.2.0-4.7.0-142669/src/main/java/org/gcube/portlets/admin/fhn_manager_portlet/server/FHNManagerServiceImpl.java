package org.gcube.portlets.admin.fhn_manager_portlet.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.gcube.common.portal.PortalContext;
import org.gcube.portlets.admin.fhn_manager_portlet.client.FhnManagerService;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.communication.Operation;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.communication.OperationTicket;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.communication.ProgressMessage;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.communication.UnexpectedException;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.Storable;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class FHNManagerServiceImpl extends RemoteServiceServlet implements FhnManagerService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3456508651362843402L;

	public static VMManagerServiceInterface getService(){
		return new RemoteServiceImpl();
//		return new MockRemote();
	}

	
	
	

	
	
	@Override
	public OperationTicket listResources(ObjectType resourceType,Map<String, String> filters) throws UnexpectedException {
		Map<String,Object> parameters=new HashMap<String,Object>();
		if(filters!=null) parameters.putAll(filters);
		parameters.put(TicketedExecutor.OBJECT_TYPE, resourceType);
		return TicketedExecutor.submitRequest(getUserInfo(), parameters, Operation.ACCESS_CACHE);		
	}

	@Override
	public OperationTicket createObject(ObjectType type,Map<String,String> fields) throws UnexpectedException {	
		Map<String,Object> parameters=new HashMap<String,Object>();
		parameters.put(TicketedExecutor.OBJECT_TYPE, type);
		if(fields!=null) 
			for(Entry<String,String> entry:fields.entrySet())
			parameters.put(entry.getKey(), entry.getValue());
		return TicketedExecutor.submitRequest(getUserInfo(), parameters,Operation.CREATE_OBJECT);
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
		return TicketedExecutor.submitRequest(getUserInfo(),parameters,Operation.DESTROY_OBJECT);
	}

	//**************************************************** REMOTE NODES


	@Override
	public OperationTicket startNode(String toStartId) {		
		return TicketedExecutor.submitRequest(getUserInfo(), 
				Collections.singletonMap(TicketedExecutor.OBJECT_ID, (Object)toStartId), Operation.START_NODE);
	}

	@Override
	public OperationTicket stopNode(String toStopId) {
		return TicketedExecutor.submitRequest(getUserInfo(), 
				Collections.singletonMap(TicketedExecutor.OBJECT_ID, (Object)toStopId), Operation.STOP_NODE);
	}


	@Override
	public ProgressMessage getProgress(OperationTicket ticket) throws UnexpectedException {
		return TicketedExecutor.getProgress(ticket.getId());
	}


	// fires load context
	private UserInformation getUserInfo() {
		//ensure context is loaded 
		Context.load(getServletContext());
		
		
		// get user info
		HttpServletRequest httpServletRequest=this.getThreadLocalRequest();
		PortalContext pContext = PortalContext.getConfiguration();
		String username = pContext.getCurrentUser(httpServletRequest).getUsername();
		String currentScope = pContext.getCurrentScope(httpServletRequest);
		String userToken = pContext.getCurrentUserToken(httpServletRequest);
		return new UserInformation(username, currentScope, userToken);
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
		return TicketedExecutor.submitRequest(getUserInfo(),params,Operation.GATHER_INFORMATION); 
	}
}
