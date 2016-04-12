package org.gcube.portlets.user.workflowdocuments.client.presenter;

import org.gcube.portlets.user.workflowdocuments.shared.WorkflowDocument;

import com.google.gwt.user.client.ui.HasWidgets;

public interface Presenter {
	public void go(HasWidgets container);
	public void doAddComment(String comment, String workflowid);
	public void doForward(WorkflowDocument wfDoc, String toStepLabel);
}
