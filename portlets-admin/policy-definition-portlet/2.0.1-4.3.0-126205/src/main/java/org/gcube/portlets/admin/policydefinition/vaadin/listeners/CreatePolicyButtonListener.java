package org.gcube.portlets.admin.policydefinition.vaadin.listeners;

import it.eng.rdlab.soa3.pm.connector.javaapi.beans.RuleBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gcube.portlets.admin.policydefinition.common.util.TimeFormatHelper;
import org.gcube.portlets.admin.policydefinition.services.restful.RestfulClient;
import org.gcube.portlets.admin.policydefinition.services.restful.RestfulResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

public class CreatePolicyButtonListener implements Button.ClickListener{
	
	private static final long serialVersionUID = -7501318601865820327L;
	
	private String service;
	private ListSelect roleslistSelect;
	private ListSelect categorySelect;
	private ListSelect hostListSelect;
	private TextField startTimeTextField;
	private TextField endTimeTextField;
	private PopupDateField startPopupDateField;
	private PopupDateField endPopupDateField;
	private CheckBox permit;
	
	private static Logger logger = LoggerFactory.getLogger(CreatePolicyButtonListener.class);

	public CreatePolicyButtonListener(
			String service,
			ListSelect roleslistSelect, ListSelect categorySelect, ListSelect hostListSelect,
			TextField startTimeTextField, TextField endTimeTextField,
			PopupDateField startPopupDateField, PopupDateField endPopupDateField,
			CheckBox permit) {
		super();
		this.service = service;
		this.roleslistSelect = roleslistSelect;
		this.categorySelect = categorySelect;
		this.hostListSelect = hostListSelect;
		this.startTimeTextField = startTimeTextField;
		this.endTimeTextField = endTimeTextField;
		this.startPopupDateField = startPopupDateField;
		this.endPopupDateField = endPopupDateField;
		this.permit = permit;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		if(!startTimeTextField.isValid() || !endTimeTextField.isValid() || !startPopupDateField.isValid() || !endPopupDateField.isValid()) return;
		Window mainWindow = event.getComponent().getApplication().getMainWindow();
		Collection<Item> roles = new ArrayList<Item>();
		if(roleslistSelect.getValue() instanceof Collection<?>){
			Collection<?> values = (Collection<?>)roleslistSelect.getValue();
			for (Object value : values) {
				roles.add(roleslistSelect.getContainerDataSource().getItem(value));
			}
		}
		if(categorySelect.getValue() instanceof Collection<?>){
			Collection<?> values = (Collection<?>)categorySelect.getValue();
			for (Object value : values) {
				roles.add(categorySelect.getContainerDataSource().getItem(value));
			}
		}
		Collection<?> hosts = null;
		if(hostListSelect.getValue() instanceof Collection<?>)
			hosts = (Collection<?>)hostListSelect.getValue();
		Boolean permitValue = (Boolean)permit.getValue();
		
		RestfulClient client = RestfulClient.getInstance();
		try {
			List<RuleBean> result = client.createRules(
					service, roles, hosts, permitValue, 
					TimeFormatHelper.getTimeRange(startTimeTextField, endTimeTextField), 
					TimeFormatHelper.getDateRange(startPopupDateField, endPopupDateField));
			mainWindow.showNotification(result.size()+" policies created succesfully", Notification.POSITION_CENTERED);
		} catch (RestfulResponseException e){
			logger.error("Error creating policies", e);
			mainWindow.showNotification("Internal server error creating policies", Notification.TYPE_ERROR_MESSAGE);	
		}
		mainWindow.removeWindow(event.getComponent().getWindow());
		
	}

}
