package org.gcube.portlets.admin.usersmanagementportlet.gwt.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.gcube.portlets.admin.usersmanagementportlet.gwt.shared.FilterType;
import org.gcube.portlets.admin.usersmanagementportlet.gwt.shared.UserInfo;

import com.google.gwt.user.client.ui.Composite;
import com.gwtext.client.core.Position;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.Checkbox;
import com.gwtext.client.widgets.form.Field;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.event.FieldListenerAdapter;

public class FilterPanel extends Composite {
	
	private Panel mainPanel = new Panel("Filters");
	private FormPanel formPanel = new FormPanel(Position.LEFT);
	private Button filterButton = new Button("Filter");
	private Button resetButton = new Button("Reset");
	
	public FilterPanel(HashMap<String, FilterType> filters) {
		mainPanel.setBodyStyle("background-color:#EEEEEE");  
		mainPanel.setCollapsible(true);
		
		formPanel.setAutoWidth(true);//Width(400);
		formPanel.setBodyStyle("background-color:#EEEEEE");  
		formPanel.setBorder(false);
		formPanel.setAutoHeight(true);
		
		filterButton.setTooltip("Filters the records with the given filters");
		resetButton.setTooltip("Resets the applied filters");
		
		Iterator<Entry<String, FilterType>> it = filters.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, FilterType> entry = it.next();
			String filterName = entry.getKey();
			FilterType filterType = filters.get(filterName);
			if (filterType == FilterType.LITERAL) {
				TextField filterField = new TextField();
				filterField.setLabel(filterName);
				formPanel.add(filterField);
			}
			else {
				Checkbox filterBox = new Checkbox();
				filterBox.setLabel(filterName);
				formPanel.add(filterBox);
			}
		}		
		formPanel.addButton(filterButton);
		formPanel.addButton(resetButton);
		mainPanel.add(formPanel);
		initWidget(mainPanel);
	}
	
	protected void setFilterButtonClickHandler(ButtonListenerAdapter listener) {
		filterButton.addListener(listener);
	}
	
	
	protected void setResetButtonClickHandler(ButtonListenerAdapter listener) {
		resetButton.addListener(listener);
	}
	
	protected void resetFields() {
		Field fields[] = this.formPanel.getFields();
		for (Field f : fields)
			f.reset();
	}
	
	protected ArrayList<UserInfo> filterData(ArrayList<UserInfo> availableUsers) {
		HashMap<String,String> filters = new HashMap<String, String>();
		ArrayList<UserInfo> filteredUsers = new ArrayList<UserInfo>();
		Field fields[] = this.formPanel.getFields();
		for (int i=0; i<fields.length; i++) {
			if (fields[i] instanceof Checkbox) {
				String isSelected = fields[i].getValueAsString();
				// It is checked. This filter should be used
				if (isSelected.equalsIgnoreCase("true")) {
					// this is the name of the field to be used in filtering
					String name = fields[i].getFieldLabel();
					filters.put(name, "true");
				}
			}
			else if (fields[i] instanceof TextField) {
				String textValue = fields[i].getValueAsString();
				if (!textValue.trim().isEmpty()) {
					filters.put(fields[i].getFieldLabel(), textValue);
				}
			}
		}
		// Now we have all the filters that will be used.
		// A hash map with filter name and value, where true is for checked checkBoxes
		for (UserInfo ui : availableUsers) {
			Iterator<String> it = filters.keySet().iterator();
			boolean hasMatch = true;
			while (it.hasNext() && hasMatch) {
				String filterName = it.next();
				String filterValue = filters.get(filterName);
				filterValue = filterValue.replaceAll("\\*", "").replaceAll("\\?", "");
			
				if (filterName.equalsIgnoreCase("username")) {
					//matcher = pattern.matcher(ui.getUsername());
					hasMatch = ui.getUsername().toLowerCase().contains(filterValue.trim().toLowerCase());
				}
				else if (filterName.equalsIgnoreCase("fullName")) {
					//matcher = pattern.matcher(ui.getFullname());
					hasMatch = ui.getFullname().toLowerCase().contains(filterValue.trim().toLowerCase());
				}
				else if (filterName.equalsIgnoreCase("email")) {
					//matcher = pattern.matcher(ui.getEmail());
					hasMatch = ui.getEmail().toLowerCase().contains(filterValue.trim().toLowerCase());
				}
				// This is a filter for role. It can either be true or false
				else {
					ArrayList<String> roles = ui.getAssignedRoles();
					boolean roleFound = false;
					for (String r : roles) {
						if (filterName.equalsIgnoreCase(r)) {
							roleFound = true;
							break;
						}
					}
					if (!roleFound)
						hasMatch = false;
				}
			//	hasMatch = matcher.find();
			}
			if (hasMatch)
				filteredUsers.add(new UserInfo(ui.getUsername(), ui.getFullname(), ui.getEmail(), ui.getAssignedRoles(), null, null));
		}
		
		return filteredUsers;
	}
	
	public void setKeyboardListenerOnFields(FieldListenerAdapter listener) {
		Field fields[] = this.formPanel.getFields();
		for (int i=0; i<fields.length; i++) {
			fields[i].addListener(listener);
		}
	}
}
