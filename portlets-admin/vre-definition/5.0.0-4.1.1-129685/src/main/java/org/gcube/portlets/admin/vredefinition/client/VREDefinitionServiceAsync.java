package org.gcube.portlets.admin.vredefinition.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import org.gcube.portlets.admin.vredefinition.shared.Functionality;
import org.gcube.portlets.admin.vredefinition.shared.VREDescriptionBean;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface VREDefinitionServiceAsync {

	void getVRE(AsyncCallback<Map<String, Serializable>> callback);

	void getFunctionality(boolean isEdit, AsyncCallback<ArrayList<Functionality>> callback);

	void setVRE(VREDescriptionBean bean,
			ArrayList<Functionality> functionalities,
			AsyncCallback<Boolean> callback);
}
