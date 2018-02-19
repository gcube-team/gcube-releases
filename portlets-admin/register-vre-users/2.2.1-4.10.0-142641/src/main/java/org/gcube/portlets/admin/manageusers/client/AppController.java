package org.gcube.portlets.admin.manageusers.client;

import org.gcube.portlets.admin.manageusers.client.presenter.Presenter;
import org.gcube.portlets.admin.manageusers.client.presenter.VREDeploymentPresenter;
import org.gcube.portlets.admin.manageusers.client.view.VREDeploymentView;

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
	private final ManageUsersServiceAsync rpcService;
	private HasWidgets container;
	Presenter presenter;

	public AppController(ManageUsersServiceAsync rpcService, HandlerManager eventBus) {
		this.eventBus = eventBus;
		this.rpcService = rpcService;
		bind();
	}

	private void bind() {
		History.addValueChangeHandler(this);
	}

	@Override
	public void go(HasWidgets container) {
		this.container = container;
		History.fireCurrentHistoryState();
	}
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		presenter = new VREDeploymentPresenter(rpcService, eventBus, new VREDeploymentView());
		if (presenter != null) {
			GWT.log("Container=null?"+(container==null));
			presenter.go(this.container);
		}
	}

}
