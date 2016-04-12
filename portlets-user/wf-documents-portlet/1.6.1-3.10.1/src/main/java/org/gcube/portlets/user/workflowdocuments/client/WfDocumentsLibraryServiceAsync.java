package org.gcube.portlets.user.workflowdocuments.client;

import java.util.ArrayList;

import org.gcube.portlets.admin.wfdocslibrary.shared.UserComment;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfTemplate;
import org.gcube.portlets.user.workflowdocuments.shared.LockInfo;
import org.gcube.portlets.user.workflowdocuments.shared.WorkflowDocument;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface WfDocumentsLibraryServiceAsync {

	void getUserWfDocuments(AsyncCallback<ArrayList<WorkflowDocument>> callback);

	void addUserComment(String workflowdocid, String comment,
			AsyncCallback<Boolean> callback);

	void getUserComments(String workflowid,
			AsyncCallback<ArrayList<UserComment>> callback);

	void getWorkflowById(String workflowid, AsyncCallback<WfTemplate> callback);

	void forward(WorkflowDocument wfDoc, String stepForwardedTo,
			AsyncCallback<Boolean> callback);

	void setWorkflowInSession(String documentName, String workflowid, boolean readonly,
			AsyncCallback<LockInfo> callback);
}
