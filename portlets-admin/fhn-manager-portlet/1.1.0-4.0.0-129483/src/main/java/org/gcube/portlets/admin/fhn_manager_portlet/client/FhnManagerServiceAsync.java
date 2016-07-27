package org.gcube.portlets.admin.fhn_manager_portlet.client;

import java.util.Map;
import java.util.Set;

import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.communication.OperationTicket;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.communication.ProgressMessage;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.Storable;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface FhnManagerServiceAsync {



	void getProgress(OperationTicket ticket,
			AsyncCallback<ProgressMessage> callback);


	void startNode(String toStartId, AsyncCallback<OperationTicket> callback);

	void stopNode(String toStopId, AsyncCallback<OperationTicket> callback);


	void listResources(ObjectType type, Map<String, String> filters,
			AsyncCallback<Set<Storable>> callback);

	void removeObject(ObjectType type, String toRemoveId,
			Map<String, Boolean> flags, AsyncCallback<OperationTicket> callback);


	void createObject(ObjectType type, Map<String, String> fields,
			AsyncCallback<OperationTicket> callback);



	void getDetails(Storable object, AsyncCallback<OperationTicket> callback);

	

}
