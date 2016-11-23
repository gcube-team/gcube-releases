package org.gcube.portlets.admin.sepeditor.client.forms;

import java.util.ArrayList;

import org.gcube.portlets.admin.sepeditor.shared.Property;
import org.gcube.portlets.admin.sepeditor.shared.RRAccessPoint;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

public class AccesPointPanel extends ContentPanel {
	private ArrayList<PropertyPanel> properties = new ArrayList<PropertyPanel>();

	private FormPanel form = null;

	private TextField<String> desc = new TextField<String>();  
	private TextField<String> interfaceEntryNameAttr = new TextField<String>();  
	private TextField<String> interfaceEndPoint = new TextField<String>();  
	private TextField<String> username = new TextField<String>();  
	private TextField<String> password1 = new TextField<String>();  
	private TextField<String> password2 = new TextField<String>();

	private Button addPropertyButton;

	private RuntimeResourceForm caller;

	public AccesPointPanel(RuntimeResourceForm caller, RRAccessPoint toEdit) {
		this(caller, true, toEdit);
		desc.setValue(toEdit.getDesc());
		interfaceEntryNameAttr.setValue(toEdit.getInterfaceEntryNameAttr());
		interfaceEndPoint.setValue(toEdit.getInterfaceEndPoint());
		username.setValue(toEdit.getUsername());
		password1.setValue(toEdit.getPassword());
		password2.setValue(toEdit.getPassword());
	}

	public AccesPointPanel(RuntimeResourceForm caller, 	boolean	isEditMode, RRAccessPoint toEdit) {
		this.caller = caller;
		setHeading("Access Point");
		form = new FormPanel();
		form.setFrame(true);
		form.setAutoWidth(true);

		form.setLabelWidth(150);

		form.setHeaderVisible(false);
		form.getHeader().setStyleName("x-hide-panel-header");

		desc.setFieldLabel("Description");  
		desc.setAllowBlank(false);  

		interfaceEntryNameAttr.setFieldLabel("Name"); 
		interfaceEntryNameAttr.setAllowBlank(false);

		interfaceEndPoint.setFieldLabel("Address");  
		interfaceEndPoint.setAllowBlank(false);

		username.setFieldLabel("Username");
		username.setAllowBlank(true);  

		password1.setFieldLabel("Password");
		password1.setAllowBlank(true); 
		password1.setPassword(true);
		//password1.setAutoValidate(true);

		password2.setFieldLabel("Repeat Password");
		password2.setAllowBlank(true); 
		password2.setPassword(true);


		password2.addListener(Events.Blur, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				if (password1.getRawValue().compareTo(password2.getRawValue()) != 0) {
					Info.display("Password Mismatch", "Your entered password and password confirm looks different!");
					password2.focus();
					password1.markInvalid("Password Mismatch");
				}		
				else
					password1.validate();
			}

		});


		Timer t = new Timer() {
			@Override
			public void run() {
				password1.setValidator(new Validator() {
					public String validate (Field ticketField, String value){
						if (password1.getRawValue().compareTo(password2.getRawValue()) != 0) 
							return "Password Mismatch";
						else
							return null;
					}
				});
			}
		};

		t.schedule(500); //need to use a timer to defer the check password wehn editing first time
		//		
		form.add(desc, new FormData("-20"));
		form.add(interfaceEntryNameAttr, new FormData("-20"));
		form.add(interfaceEndPoint, new FormData("-20"));
		form.add(username, new FormData("-20"));
		form.add(password1, new FormData("-20"));
		form.add(password2, new FormData("-20"));

		if (toEdit != null) {
			for(Property prop: toEdit.getProperties()) 
				addProperty2Edit(prop);
		}

		addPropertyButton = getAddPropertyButton();
		form.add(addPropertyButton);
		form.add(new Label()); //spacer
		setBorders(true);
		getHeader().addTool(new ToolButton("x-tool-close", new SelectionListener<IconButtonEvent>() {
			@Override  
			public void componentSelected(IconButtonEvent ce) {
				remove();
			}
		}));
		add(form);

	}

	private void addProperty2Edit(Property prop) {
		PropertyPanel pp = new PropertyPanel(this, prop);
		form.add(pp, new FormData("-20"));
		form.add(new Label()); //spacer
		properties.add(pp);
		form.layout();	
	}

	private Button getAddPropertyButton() {
		Button addProperty = new Button("Add New Property"){
			@Override
			protected void onClick(final ComponentEvent ce) {
				PropertyPanel pp =  getPropertyPanel();
				form.remove(addPropertyButton);
				form.add(pp, new FormData("-20"));
				form.add(new Label());
				form.add(addPropertyButton);
				form.layout();	
				properties.add(pp);
			}
		};
		return addProperty;
	}

	private PropertyPanel getPropertyPanel() {
		return new PropertyPanel(this);
	}

	protected void removeProperty(PropertyPanel toRemove) {
		this.properties.remove(toRemove);
		form.remove(toRemove);
	}

	private void remove() {
		caller.removeAccessPoint(this);
	}
	public TextField<String> getDesc() {
		return desc;
	}

	public TextField<String> getEntryNameAttr() {
		return interfaceEntryNameAttr;
	}

	public TextField<String> getEndPoint() {
		return interfaceEndPoint;
	}


	public TextField<String> getUsername() {
		return username;
	}

	public TextField<String> getPassword() {
		return password1;
	}

	public ArrayList<PropertyPanel> getProperties() {
		return properties;
	}


}
