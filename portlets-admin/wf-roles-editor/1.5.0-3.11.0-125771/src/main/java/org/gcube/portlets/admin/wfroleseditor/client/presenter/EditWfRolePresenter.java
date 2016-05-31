package org.gcube.portlets.admin.wfroleseditor.client.presenter;

import org.gcube.portlets.admin.wfdocslibrary.shared.WfRole;
import org.gcube.portlets.admin.wfroleseditor.client.WfRolesServiceAsync;
import org.gcube.portlets.admin.wfroleseditor.client.event.EditRoleCancelledEvent;
import org.gcube.portlets.admin.wfroleseditor.client.event.RoleUpdatedEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class EditWfRolePresenter implements Presenter{  
	public interface Display {
		HasClickHandlers getSaveButton();
		HasClickHandlers getCancelButton();
		HasValue<String> getRoleName();
		HasValue<String> getDescription();
		void updateSize();
		Widget asWidget();
	}

	private WfRole wfRole;
	private final WfRolesServiceAsync rpcService;
	private final HandlerManager eventBus;
	private final Display display;

	/**
	 * called in case of add role
	 */
	public EditWfRolePresenter(WfRolesServiceAsync rpcService, HandlerManager eventBus, Display display) {
		this.rpcService = rpcService;
		this.eventBus = eventBus;
		this.wfRole = new WfRole();
		this.display = display;
		bind();
	}
	/**
	 * called in case of edit role
	 */
	public EditWfRolePresenter(WfRolesServiceAsync rpcService, HandlerManager eventBus, Display display, String id) {
		this.rpcService = rpcService;
		this.eventBus = eventBus;
		this.display = display;
		bind();

		rpcService.getRole(id, new AsyncCallback<WfRole>() {
			public void onSuccess(WfRole result) {
				wfRole = result;
				EditWfRolePresenter.this.display.getRoleName().setValue(wfRole.getRolename());
				EditWfRolePresenter.this.display.getDescription().setValue(wfRole.getRoledescription());
			}

			public void onFailure(Throwable caught) {
				Window.alert("Error retrieving wfRole");
			}
		});

	}

	public void bind() {
		this.display.getSaveButton().addClickHandler(new ClickHandler() {   
			public void onClick(ClickEvent event) {
				doSave();
			}
		});

		this.display.getCancelButton().addClickHandler(new ClickHandler() {   
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new EditRoleCancelledEvent());
			}
		});
	}

	public void go(final HasWidgets container) {
		container.clear();
		container.add(display.asWidget());
	}

	private void doSave() {
		if (! fieldsEmpty()) {
			wfRole.setRolename(display.getRoleName().getValue());
			wfRole.setRoledescription(display.getDescription().getValue());
			//creating a new role
			if (wfRole.getRoleid() == null) {
				rpcService.addRole(wfRole, new AsyncCallback<WfRole>() {
					public void onSuccess(WfRole result) {
						eventBus.fireEvent(new RoleUpdatedEvent(result));
					}
					public void onFailure(Throwable caught) {
						Window.alert("Error adding wfRole");
					}
				});
			}
			else { //editing a role
				rpcService.updateRole(wfRole, new AsyncCallback<WfRole>() {
					public void onSuccess(WfRole result) {
						eventBus.fireEvent(new RoleUpdatedEvent(result));
					}
					public void onFailure(Throwable caught) {
						Window.alert("Error updating wfRole");
					}
				});
			}
		}
		else {
			Window.alert("All fileds must be filled");
		}
	}

	private boolean fieldsEmpty() {
		return (display.getRoleName().getValue().equals("") || display.getDescription().getValue().equals(""));
	}

}
