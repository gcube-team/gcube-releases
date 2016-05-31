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

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface WorkflowDocService extends RemoteService {
	
	ArrayList<WfTemplateBean> getAllTemplates();
	
	ArrayList<WfDocumentBean> getAllWfDocuments();
	
	WfTemplate getWfTemplate(String id);
	
	WfTemplate getWfReport(String id);
	
	ArrayList<WfRoleDetails> getRoleDetails();
	
	ArrayList<UserBean> getVREUsers();
	
	Boolean saveWorkflow(String selectedReportid, String selectedReportName, WfGraph toSave, HashMap<String, List<UserBean>> rolesAndUsersToCreate);

	ArrayList<ActionLogBean> fetchActionsByWorkflowId(String workflowid);
	
	Boolean deleteWorkflowDocument(WfDocumentBean docBean);
}
