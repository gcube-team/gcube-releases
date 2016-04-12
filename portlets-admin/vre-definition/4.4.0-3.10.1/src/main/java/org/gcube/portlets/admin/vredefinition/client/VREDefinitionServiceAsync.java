package org.gcube.portlets.admin.vredefinition.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.admin.vredefinition.client.model.VREFunctionalityModel;
import org.gcube.portlets.admin.vredefinition.shared.ExternalResourceModel;
import org.gcube.portlets.admin.vredefinition.shared.VREDescriptionBean;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface VREDefinitionServiceAsync {

	void getExistingNames(AsyncCallback<String[]> callback);

	void setVRE(
			VREDescriptionBean bean,
			String[] functionalityIDs,
			HashMap<String, List<ExternalResourceModel>> funcToExternalResources,
			AsyncCallback<String> callback);
	
	void getFunctionality(boolean isEdit,
			AsyncCallback<VREFunctionalityModel> callback);
	
	void getVRE(AsyncCallback<Map<String, Object>> callback);

	void isEditMode(AsyncCallback<Map<String, Object>> callback);

	void getResourceCategoryByFunctionality(String funcId,
			AsyncCallback<ArrayList<ExternalResourceModel>> callback);

}
