package org.gcube.portlets.admin.sepeditor.client.forms;

import org.gcube.portlets.admin.sepeditor.shared.Property;

import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;

public class PropertyPanel  extends ContentPanel {

	private FormPanel form = null;
	private TextField<String> key = new TextField<String>();  
	private TextField<String> value = new TextField<String>();  
	private CheckBox crypted = new CheckBox();
	AccesPointPanel owner;

	public PropertyPanel(AccesPointPanel caller, Property source) {
		this(caller);
		key.setValue(source.getKey());
		crypted.setValue(source.isCrypted());
		value.setValue(source.getValue());
	}
	
	public PropertyPanel(AccesPointPanel caller) {
		owner = caller;
		setHeading("Property");
		form = new FormPanel();
		form.setFrame(true);
		form.setAutoWidth(true);
		

		form.setHeaderVisible(false);
		form.getHeader().setStyleName("x-hide-panel-header");

		crypted.setFieldLabel("Encrypt");
		
		key.setFieldLabel("Key");  
		key.setAllowBlank(false);  

		value.setFieldLabel("Value"); 
		value.setAllowBlank(false);
		
		form.add(crypted, new FormData(20, 20));
		form.add(key, new FormData("-20"));
		form.add(value, new FormData("-20"));

		getHeader().addTool(new ToolButton("x-tool-close", new SelectionListener<IconButtonEvent>() {
			@Override  
			public void componentSelected(IconButtonEvent ce) {
				remove();
			}
		}));
		add(form);
	}
	private void remove() {
		owner.removeProperty(this);

	}
	public TextField<String> getKey() {
		return key;
	}
	public TextField<String> getValue() {
		return value;
	}
	public boolean isCrypted() {
		return crypted.getValue();
	}	
}

