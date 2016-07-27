package org.gcube.portlets.admin.fhn_manager_portlet.client;

import java.util.Map;
import java.util.Set;

import org.gcube.portlets.admin.fhn_manager_portlet.shared.Constants;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.communication.OperationTicket;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.communication.ProgressMessage;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.communication.UnexpectedException;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.Storable;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath(Constants.SERVICE_RELATIVE_PATH)
public interface FhnManagerService extends RemoteService{

	/**
	 * Methods return a String ticketId to be used for asynchronous status update
	 * 
	 * @return
	 * @throws UnexpectedException 
	 */
	
	public Set<Storable> listResources(ObjectType type,Map<String,String> filters)throws UnexpectedException;
	
	public OperationTicket removeObject(ObjectType type, String toRemoveId,Map<String,Boolean> flags)throws UnexpectedException;
	public OperationTicket createObject(ObjectType type,Map<String,String> fields) throws UnexpectedException;
	
	
	public OperationTicket getDetails(Storable object) throws UnexpectedException;
	
	
	//Node Management
	public OperationTicket startNode(String toStartId);
	public OperationTicket stopNode(String toStopId);	
	
	public ProgressMessage getProgress(OperationTicket ticket) throws UnexpectedException;
}
