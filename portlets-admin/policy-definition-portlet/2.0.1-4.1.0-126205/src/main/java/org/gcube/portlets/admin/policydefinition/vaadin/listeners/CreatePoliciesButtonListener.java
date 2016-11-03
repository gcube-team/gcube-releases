package org.gcube.portlets.admin.policydefinition.vaadin.listeners;

import org.gcube.portlets.admin.policydefinition.common.util.PresentationHelper;
import org.gcube.portlets.admin.policydefinition.vaadin.components.CreatePolicyComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

public class CreatePoliciesButtonListener implements Button.ClickListener {
	
	private static Logger logger = LoggerFactory.getLogger(CreatePoliciesButtonListener.class);
	private static final long serialVersionUID = -1108919293654366874L;
	private Table servicesTable;
	
	public CreatePoliciesButtonListener(Table servicesTable) {
		super();
		this.servicesTable = servicesTable;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		Window mainWindow = event.getComponent().getApplication().getMainWindow();
		try {	
			Object value = servicesTable.getValue();
			if(value == null) return;
			Property servProp = servicesTable.getContainerDataSource().getItem(value).getItemProperty("id");
			String service = (String)servProp.getValue();
			CreatePolicyComponent policyComponent = new CreatePolicyComponent(service);
			Window createPolicyPopup = new Window("Create Policies for "+PresentationHelper.viewName(service), policyComponent); 
			createPolicyPopup.setWidth((policyComponent.getWidth()+15)+"px");
			createPolicyPopup.setHeight((policyComponent.getHeight()+60)+"px");
			createPolicyPopup.setModal(true);
			createPolicyPopup.setCloseShortcut(KeyCode.ESCAPE, null);
			mainWindow.addWindow(createPolicyPopup);
		} catch (Exception e) {
			logger.error("Error showing create policies popup", e);
			mainWindow.showNotification("Internal server error", Notification.TYPE_ERROR_MESSAGE);	
		}
		mainWindow.removeWindow(event.getComponent().getWindow());
	}

}
