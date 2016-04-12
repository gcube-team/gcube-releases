package org.gcube.portlets.admin.wfroleseditor.client;

import org.gcube.portlets.admin.wfroleseditor.client.event.AddRoleEvent;
import org.gcube.portlets.admin.wfroleseditor.client.event.AddRoleEventHandler;
import org.gcube.portlets.admin.wfroleseditor.client.event.EditRoleCancelledEvent;
import org.gcube.portlets.admin.wfroleseditor.client.event.EditRoleCancelledEventHandler;
import org.gcube.portlets.admin.wfroleseditor.client.event.EditRoleEvent;
import org.gcube.portlets.admin.wfroleseditor.client.event.EditRoleEventHandler;
import org.gcube.portlets.admin.wfroleseditor.client.event.RoleUpdatedEvent;
import org.gcube.portlets.admin.wfroleseditor.client.event.RoleUpdatedEventHandler;
import org.gcube.portlets.admin.wfroleseditor.client.presenter.EditWfRolePresenter;
import org.gcube.portlets.admin.wfroleseditor.client.presenter.Presenter;
import org.gcube.portlets.admin.wfroleseditor.client.presenter.WfRolesPresenter;
import org.gcube.portlets.admin.wfroleseditor.client.view.EditWfRoleView;
import org.gcube.portlets.admin.wfroleseditor.client.view.WfRolesView;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasWidgets;
/**
 * <code> AppController </code> class is the controller component of this webapp
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version May 2011 (0.1) 
 */
public class AppController implements Presenter, ValueChangeHandler<String> {
  private final HandlerManager eventBus;
  private final WfRolesServiceAsync rpcService; 
  private HasWidgets container;
  
  public AppController(WfRolesServiceAsync rpcService, HandlerManager eventBus) {
    this.eventBus = eventBus;
    this.rpcService = rpcService;
    bind();
  }
  
  private void bind() {
    History.addValueChangeHandler(this);

    eventBus.addHandler(AddRoleEvent.TYPE, new AddRoleEventHandler() {
          public void onAddRole(AddRoleEvent event) {
            doAddNewRole();
          }
        });  

    eventBus.addHandler(EditRoleEvent.TYPE, new EditRoleEventHandler() {
          public void onEditRole(EditRoleEvent event) {
            doEditRole(event.getId());
          }
        });  

    eventBus.addHandler(EditRoleCancelledEvent.TYPE, new EditRoleCancelledEventHandler() {
          public void onEditRoleCancelled(EditRoleCancelledEvent event) {
            doEditRoleCancelled();
          }
        });  

    eventBus.addHandler(RoleUpdatedEvent.TYPE, new RoleUpdatedEventHandler() {
          public void onRoleUpdated(RoleUpdatedEvent event) {
            doRoleUpdated();
          }
        });  
  }
  
  private void doAddNewRole() {
    History.newItem("add");
  }
  
  private void doEditRole(String id) {
    History.newItem("edit", false);
    Presenter presenter = new EditWfRolePresenter(rpcService, eventBus, new EditWfRoleView(), id);
    presenter.go(container);
  }
  
  private void doEditRoleCancelled() {
    History.newItem("list");
  }
  
  private void doRoleUpdated() {
    History.newItem("list");
  }
  
  public void go(final HasWidgets container) {
    this.container = container;
    
    if ("".equals(History.getToken())) {
      History.newItem("list");
    }
    else {
      History.fireCurrentHistoryState();
    }
  }

  public void onValueChange(ValueChangeEvent<String> event) {
    String token = event.getValue();
    
    if (token != null) {
      Presenter presenter = null;

      if (token.equals("list")) {
        presenter = new WfRolesPresenter(rpcService, eventBus, new WfRolesView());
      }
      else if (token.equals("add")) {
        presenter = new EditWfRolePresenter(rpcService, eventBus, new EditWfRoleView());
      }
      else if (token.equals("edit")) {
        presenter = new EditWfRolePresenter(rpcService, eventBus, new EditWfRoleView());
      }
      
      if (presenter != null) {
        presenter.go(container);
      }
    }
  } 
}
