package org.gcube.portlets.admin.policydefinition.vaadin.listeners;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.gcube.portlets.admin.policydefinition.common.util.PresentationHelper;
import org.gcube.portlets.admin.policydefinition.common.util.RoleHelper;
import org.gcube.portlets.admin.policydefinition.common.util.TimeFormatHelper;
import org.gcube.portlets.admin.policydefinition.services.restful.RestfulClient;
import org.gcube.portlets.admin.policydefinition.services.restful.RestfulResponseException;
import org.gcube.portlets.admin.policydefinition.vaadin.containers.PoliciesContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

public class UpdatePolicyButtonListener implements Button.ClickListener {
	
	private static final long serialVersionUID = -2893092244054498271L;
	private String service;
	private Table policyTable;
	private TextField startTimeTextField;
	private TextField endTimeTextField;
	private PopupDateField startPopupDateField;
	private PopupDateField endPopupDateField;
	private CheckBox permitCheckBox;
	private CheckBox overridePermit;
	private CheckBox overrideDate;
	private CheckBox overrideTime;
	private Button cancel;
	
	private static Logger logger = LoggerFactory.getLogger(UpdatePolicyButtonListener.class);

	public UpdatePolicyButtonListener(String service, Table policyTable, TextField startTimeTextField,
			TextField endTimeTextField, PopupDateField startPopupDateField,
			PopupDateField endPopupDateField, CheckBox permitCheckBox, 
			CheckBox overridePermit, CheckBox overrideDate, CheckBox overrideTime,
			Button cancel) {
		super();
		this.service = service;
		this.policyTable = policyTable;
		this.startTimeTextField = startTimeTextField;
		this.endTimeTextField = endTimeTextField;
		this.startPopupDateField = startPopupDateField;
		this.endPopupDateField = endPopupDateField;
		this.permitCheckBox = permitCheckBox;
		this.overrideDate = overrideDate;
		this.overridePermit = overridePermit;
		this.overrideTime = overrideTime;
		this.cancel = cancel;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		Window mainWindow = event.getComponent().getApplication().getMainWindow();
		Boolean dateCB = (Boolean)this.overrideDate.getValue();
		Boolean timeCB = (Boolean)this.overrideTime.getValue();
		Boolean permCB = (Boolean)this.overridePermit.getValue();
		if((timeCB && (!startTimeTextField.isValid() || !endTimeTextField.isValid())) 
				|| (dateCB && (!startPopupDateField.isValid() || !endPopupDateField.isValid()))) return;
		int size = 0;
		try {
			if(policyTable.getContainerDataSource().getItemIds() instanceof Collection<?>){
				Collection<?> policies = (Collection<?>)policyTable.getContainerDataSource().getItemIds();
				for (Object policy : policies) {
					String role = (String)policyTable.getContainerDataSource().getItem(policy).getItemProperty(PoliciesContainer.ROLE).getValue();
					if(role == null)
						role = RoleHelper.SERVICE_PREFIX+policyTable.getContainerDataSource().getItem(policy).getItemProperty(PoliciesContainer.SERVICE_CATEGORY).getValue();
					String timeRange = (String)policyTable.getContainerDataSource().getItem(policy).getItemProperty(PoliciesContainer.TIME_RANGE).getValue();
					if(timeCB)
						timeRange = TimeFormatHelper.getTimeRange(startTimeTextField, endTimeTextField);
					String dateRange = (String)policyTable.getContainerDataSource().getItem(policy).getItemProperty(PoliciesContainer.DATE_RANGE).getValue();
					if(dateCB)
						dateRange = TimeFormatHelper.getDateRange(startPopupDateField, endPopupDateField);
					Boolean permit = (Boolean)policyTable.getContainerDataSource().getItem(policy).getItemProperty(PoliciesContainer.PERMITTED).getValue();
					if(permCB)
						permit = (Boolean)permitCheckBox.getValue();
					if(timeCB || dateCB || permCB)
						RestfulClient.getInstance().updateRule(
								(String)policy, 
								service, 
								role, 
								(String)policyTable.getContainerDataSource().getItem(policy).getItemProperty(PoliciesContainer.HOST).getValue(), 
								permit, 
								timeRange, 
								dateRange); 
				}
				if(timeCB || dateCB || permCB)
					size = policies.size();
			}
			mainWindow.showNotification("Updated "+size+" "+PresentationHelper.getPolicyNoun(policyTable)+" succesfully", Notification.POSITION_CENTERED);
			((Window) event.getComponent().getWindow().getParent()).removeWindow(event.getComponent().getWindow());
			Map<String, Object> vars = new HashMap<String, Object>();
			vars.put("state",true);
			cancel.changeVariables(this, vars);
		} catch (RestfulResponseException e) {
			logger.error("Error updating policy", e);
			mainWindow.showNotification("Internal server error updating policy", Notification.TYPE_ERROR_MESSAGE);	
		}
	}

}
