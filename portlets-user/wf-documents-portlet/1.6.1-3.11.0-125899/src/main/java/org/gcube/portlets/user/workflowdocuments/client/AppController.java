package org.gcube.portlets.user.workflowdocuments.client;

import org.gcube.portlets.user.workflowdocuments.client.event.AddCommentEvent;
import org.gcube.portlets.user.workflowdocuments.client.event.AddCommentEventHandler;
import org.gcube.portlets.user.workflowdocuments.client.event.ForwardEvent;
import org.gcube.portlets.user.workflowdocuments.client.event.ForwardEventHandler;
import org.gcube.portlets.user.workflowdocuments.client.presenter.Presenter;
import org.gcube.portlets.user.workflowdocuments.client.presenter.WorkflowDocumentsPresenter;
import org.gcube.portlets.user.workflowdocuments.client.view.WfDocLibraryView;
import org.gcube.portlets.user.workflowdocuments.shared.WorkflowDocument;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasWidgets;

/**
 * 
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version May 2011 (0.1) 
 * 
 * This <class>AppController</class> contains the view transition logic, 
 * to handle logic that is not specific to any presenter and instead resides at the application layer
 */
public class AppController implements Presenter, ValueChangeHandler<String> {
	private final HandlerManager eventBus;
	private final WfDocumentsLibraryServiceAsync rpcService;
	private HasWidgets container;
	Presenter presenter;

	public AppController(WfDocumentsLibraryServiceAsync rpcService, HandlerManager eventBus) {
		this.eventBus = eventBus;
		this.rpcService = rpcService;
		bind();
	}

	private void bind() {
		eventBus.addHandler(AddCommentEvent.TYPE, new AddCommentEventHandler() {
			public void onAddComent(AddCommentEvent event) {
				doAddComment(event.getComment(), event.getWorkflowid());
			}
		});
		eventBus.addHandler(ForwardEvent.TYPE, new ForwardEventHandler() {
			public void onHasForwarded(ForwardEvent event) {
				doForward(event.getWorkflow(), event.getToStepLabel());
			}
		});
		History.addValueChangeHandler(this);
	}

	@Override
	public void go(HasWidgets container) {
		this.container = container;
		History.fireCurrentHistoryState();
	}
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {

		//presenter = new WorkflowDocsPresenter(rpcService, eventBus, new WorkflowDocsView());
		presenter = new WorkflowDocumentsPresenter(rpcService, eventBus, new WfDocLibraryView());
		if (presenter != null) {
			GWT.log("Container=null?"+(container==null));
			presenter.go(this.container);
		}
	}

	@Override
	public void doAddComment(String comment, String workflowid) {
		presenter.doAddComment(comment, workflowid);		
	}

	@Override
	public void doForward(WorkflowDocument wfDoc, String toStepLabel) {
		presenter.doForward(wfDoc, toStepLabel);		
	}
}
