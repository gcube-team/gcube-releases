package org.gcube.portlets.admin.policydefinition.vaadin.listeners;

import org.gcube.portlets.admin.policydefinition.common.util.PresentationHelper;
import org.gcube.portlets.admin.policydefinition.vaadin.components.PoliciesComponent;
import org.gcube.portlets.admin.policydefinition.vaadin.containers.PoliciesContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

public class ShowPoliciesButtonListener implements Button.ClickListener{

	private static Logger logger = LoggerFactory.getLogger(ShowPoliciesButtonListener.class);
	private static final long serialVersionUID = -3238083351178908749L;
	
	private Table servicesTable;

	public ShowPoliciesButtonListener(Table servicesTable) {
		super();
		this.servicesTable = servicesTable;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		showPopup(event);
	}
	
	private void showPopup(Event event){
		if(servicesTable.getValue() == null) return;
		String selectedId = (String)servicesTable.getContainerDataSource().getItem(servicesTable.getValue()).getItemProperty("id").getValue();
		Window mainWindow = event.getComponent().getApplication().getMainWindow();
		try {
			final Container policiesContainer = PoliciesContainer.getPoliciesContainer(selectedId);					
			PoliciesComponent policiesComponent = new PoliciesComponent(policiesContainer, servicesTable);
			Window policiesPopup = new Window("Policies list for "+PresentationHelper.viewName(selectedId), policiesComponent); 
			policiesPopup.setWidth("60%");
			policiesPopup.setHeight("60%");
			policiesPopup.setModal(true);
			policiesPopup.setCloseShortcut(KeyCode.ESCAPE, null);
			event.getComponent().getWindow().addWindow(policiesPopup);
		} catch (Exception e) {
			logger.error("Error showing policies", e);
			mainWindow.showNotification("Internal server error", Notification.TYPE_ERROR_MESSAGE);	
		}
	}

}
