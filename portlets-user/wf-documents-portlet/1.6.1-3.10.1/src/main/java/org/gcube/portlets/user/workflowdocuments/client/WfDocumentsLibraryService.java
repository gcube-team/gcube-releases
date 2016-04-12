package org.gcube.portlets.user.workflowdocuments.client;

import java.util.ArrayList;

import org.gcube.portlets.admin.wfdocslibrary.shared.UserComment;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfTemplate;
import org.gcube.portlets.user.workflowdocuments.shared.LockInfo;
import org.gcube.portlets.user.workflowdocuments.shared.WorkflowDocument;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface WfDocumentsLibraryService extends RemoteService {
	ArrayList<WorkflowDocument> getUserWfDocuments();
	Boolean addUserComment(String workflowdocid, String comment);
	ArrayList<UserComment> getUserComments(String workflowid);

	WfTemplate getWorkflowById(String workflowid);
	Boolean forward(WorkflowDocument wfDoc, String stepForwardedTo);
	
	LockInfo setWorkflowInSession(String documentName, String workflowid, boolean readonly);
}
