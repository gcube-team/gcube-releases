package org.gcube.portlets.admin.wfdocviewer.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.admin.wfdocslibrary.shared.WfGraph;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfRoleDetails;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfTemplate;
import org.gcube.portlets.admin.wfdocviewer.shared.ActionLogBean;
import org.gcube.portlets.admin.wfdocviewer.shared.UserBean;
import org.gcube.portlets.admin.wfdocviewer.shared.WfDocumentBean;
import org.gcube.portlets.admin.wfdocviewer.shared.WfTemplateBean;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>WorkflowDocService</code>.
 */
public interface WorkflowDocServiceAsync {

	void getAllWfDocuments(AsyncCallback<ArrayList<WfDocumentBean>> callback);

	void getAllTemplates(AsyncCallback<ArrayList<WfTemplateBean>> callback);

	void getWfTemplate(String id, AsyncCallback<WfTemplate> callback);

	void getRoleDetails(AsyncCallback<ArrayList<WfRoleDetails>> callback);

	void getVREUsers(AsyncCallback<ArrayList<UserBean>> callback);

	void saveWorkflow(String selectedReportid, String selectedReportName,
			WfGraph toSave,
			HashMap<String, List<UserBean>> rolesAndUsersToCreate,
			AsyncCallback<Boolean> callback);

	void getWfReport(String id, AsyncCallback<WfTemplate> callback);

	void fetchActionsByWorkflowId(String workflowid,
			AsyncCallback<ArrayList<ActionLogBean>> callback);

	void deleteWorkflowDocument(WfDocumentBean docBean,
			AsyncCallback<Boolean> callback);
}
