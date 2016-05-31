package org.gcube.portlets.admin.sepeditor.client;

import org.gcube.portlets.admin.sepeditor.shared.FilledRuntimeResource;
import org.gcube.portlets.admin.sepeditor.shared.InitInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("serviceendpointEditor")
public interface RuntimeResourceCreatorService extends RemoteService {
	
	 
	 InitInfo getInitialInfo(boolean isEditMode, String idToEdit, String curscope);
	 
	 Boolean createRuntimeResource(String scope, FilledRuntimeResource resource, boolean isUpdate);
}
