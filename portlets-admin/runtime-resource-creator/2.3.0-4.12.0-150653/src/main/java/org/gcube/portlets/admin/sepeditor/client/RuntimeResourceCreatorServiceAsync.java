package org.gcube.portlets.admin.sepeditor.client;

import org.gcube.portlets.admin.sepeditor.shared.FilledRuntimeResource;
import org.gcube.portlets.admin.sepeditor.shared.InitInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>RuntimeResourceCreatorService</code>.
 */
public interface RuntimeResourceCreatorServiceAsync {

	void createRuntimeResource(String scope, FilledRuntimeResource resource,  boolean isUpdate,
			AsyncCallback<Boolean> callback);

	void getInitialInfo(boolean isEditMode, String idToEdit,
			String curscope, AsyncCallback<InitInfo> callback);
}
