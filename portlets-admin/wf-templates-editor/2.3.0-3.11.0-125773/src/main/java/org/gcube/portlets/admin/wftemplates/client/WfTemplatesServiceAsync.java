package org.gcube.portlets.admin.wftemplates.client;

import java.util.ArrayList;

import org.gcube.portlets.admin.wfdocslibrary.shared.WfGraph;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfRoleDetails;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfTemplate;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>WfTemplatesService</code>.
 */
public interface WfTemplatesServiceAsync {
	
	void getTemplates(AsyncCallback<ArrayList<WfTemplate>> callback);

	void getRoleDetails(AsyncCallback<ArrayList<WfRoleDetails>> callback);

	void saveTemplate(String wfName, WfGraph toSave, AsyncCallback<Boolean> callback);

	void deleteTemplate(WfTemplate toDelete, AsyncCallback<Boolean> callback);
}
