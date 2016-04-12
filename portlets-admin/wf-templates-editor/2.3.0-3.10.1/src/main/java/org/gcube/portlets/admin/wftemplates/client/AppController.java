package org.gcube.portlets.admin.wftemplates.client;

import java.util.ArrayList;

import org.gcube.portlets.admin.wfdocslibrary.shared.WfRoleDetails;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfTemplate;
import org.gcube.portlets.admin.wftemplates.client.event.AddStepEvent;
import org.gcube.portlets.admin.wftemplates.client.event.AddStepEventHandler;
import org.gcube.portlets.admin.wftemplates.client.event.AddTemplateEvent;
import org.gcube.portlets.admin.wftemplates.client.event.AddTemplateEventHandler;
import org.gcube.portlets.admin.wftemplates.client.event.ConnectionRemovedEvent;
import org.gcube.portlets.admin.wftemplates.client.event.ConnectionRemovedEventHandler;
import org.gcube.portlets.admin.wftemplates.client.event.DeleteTemplateEvent;
import org.gcube.portlets.admin.wftemplates.client.event.DeleteTemplateEventHandler;
import org.gcube.portlets.admin.wftemplates.client.event.RemoveConnectionEvent;
import org.gcube.portlets.admin.wftemplates.client.event.RemoveConnectionEventHandler;
import org.gcube.portlets.admin.wftemplates.client.event.RolesAddedEvent;
import org.gcube.portlets.admin.wftemplates.client.event.RolesAddedEventHandler;
import org.gcube.portlets.admin.wftemplates.client.event.StepRemovedEvent;
import org.gcube.portlets.admin.wftemplates.client.event.StepRemovedEventHandler;
import org.gcube.portlets.admin.wftemplates.client.presenter.Presenter;
import org.gcube.portlets.admin.wftemplates.client.presenter.WfTemplatesPresenter;
import org.gcube.portlets.admin.wftemplates.client.view.WfStep;
import org.gcube.portlets.admin.wftemplates.client.view.WfTemplatesView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasWidgets;
import com.orange.links.client.connection.Connection;

/**
 * <code> AppController </code> class is the controller component of this webapp
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version May 2011 (0.1) 
 */
public class AppController implements Presenter, ValueChangeHandler<String> {
	private final HandlerManager eventBus;
	private final WfTemplatesServiceAsync rpcService; 
	private HasWidgets container;
	Presenter presenter;

	public AppController(WfTemplatesServiceAsync rpcService, HandlerManager eventBus) {
		this.eventBus = eventBus;
		this.rpcService = rpcService;
		bind();
	}
	/*
	 * used to add handlers to the eventbus
	 */
	private void bind() {
		History.addValueChangeHandler(this);

		eventBus.addHandler(AddStepEvent.TYPE, new AddStepEventHandler() {
			@Override
			public void onAddStep(AddStepEvent event) {
				addNewStep(event.getName(), event.getDescription());
			}
		});

		eventBus.addHandler(AddTemplateEvent.TYPE, new AddTemplateEventHandler() {
			@Override
			public void onAddTemplates(AddTemplateEvent templateAddEvent) {
				doSaveTemplate(templateAddEvent.getTemplateName());				
			}
		});

		eventBus.addHandler(RolesAddedEvent.TYPE, new RolesAddedEventHandler() {			
			@Override
			public void onAddRoles(RolesAddedEvent rolesAddedEvent) {
				addRolesOnConnection(rolesAddedEvent.getSelectedEdge(), rolesAddedEvent.getRoles());
			}
		});

		eventBus.addHandler(ConnectionRemovedEvent.TYPE, new ConnectionRemovedEventHandler() {
			@Override
			public void onRemovedConnection(ConnectionRemovedEvent connectionRemovedEvent) {
				doRemoveConnectionFromModel(connectionRemovedEvent.getSelected());
			}
		});

		eventBus.addHandler(RemoveConnectionEvent.TYPE, new RemoveConnectionEventHandler() {
			@Override
			public void onConnectionRemoval(RemoveConnectionEvent connectionEvent) {
				doRemoveConnectionFromView(connectionEvent.getSelected());
			}
		});

		eventBus.addHandler(StepRemovedEvent.TYPE, new StepRemovedEventHandler() {			
			@Override
			public void onStepRemoved(StepRemovedEvent stepRemoved) {
				doRemoveStep(stepRemoved.getRemovedStep());
			}
		});
		
		eventBus.addHandler(DeleteTemplateEvent.TYPE, new DeleteTemplateEventHandler() {
			@Override
			public void onDeleteTemplate(DeleteTemplateEvent deletedTemplateEvent) {
				doDeleteTemplate(deletedTemplateEvent.getDeleted());
			}
		});
	}

	@Override
	public void go(HasWidgets container) {
		this.container = container;
		History.fireCurrentHistoryState();
	}
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {

		presenter = new WfTemplatesPresenter(rpcService, eventBus, new WfTemplatesView());

		if (presenter != null) {
			GWT.log("Container=null?"+(container==null));
			presenter.go(this.container);
		}
	}

	@Override
	public void addNewStep(String label, String description) {
		presenter.addNewStep(label, description);
	}
	public void doSaveTemplate(String templateName) {
		presenter.doSaveTemplate(templateName);
	}
	@Override
	public void addRolesOnConnection(Connection selectedEdge, ArrayList<WfRoleDetails> roles) {
		presenter.addRolesOnConnection(selectedEdge, roles);
	}
	@Override
	public void doRemoveConnectionFromModel(Connection selected) {
		presenter.doRemoveConnectionFromModel(selected);		
	}
	@Override
	public void doRemoveStep(WfStep step) {
		presenter.doRemoveStep(step);		
	}
	@Override
	public void doRemoveConnectionFromView(Connection selected) {
		presenter.doRemoveConnectionFromView(selected);
	}
	@Override
	public void doDeleteTemplate(WfTemplate toDelete) {
		presenter.doDeleteTemplate(toDelete);
	}

}
