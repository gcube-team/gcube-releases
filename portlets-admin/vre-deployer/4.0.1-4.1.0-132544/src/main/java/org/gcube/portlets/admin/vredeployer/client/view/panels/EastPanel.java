package org.gcube.portlets.admin.vredeployer.client.view.panels;

import org.gcube.portlets.admin.vredeployer.client.constants.ImagesConstants;
import org.gcube.portlets.admin.vredeployer.client.control.Controller;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;

public class EastPanel  extends ContentPanel {  
	/**
	 * 
	 */
	private Controller controller;

	/**
	 * constructor
	 * @param c -
	 */
	public EastPanel(Controller c) {

		controller = c;
		setWidth("100%");
		setHeight("100%");
	}

	/**
	 * 
	 */
	public void showCloudPanel(boolean selected, int vmSelected) {
		setHeading("deploy VRE Services on Cloud");
		
		
		final Button saveButton = new Button("Commit changes"); 
		saveButton.setEnabled(false);
		
		removeAll();
		unmask();
		LayoutContainer toShow = new LayoutContainer();

		HorizontalPanel hp = new HorizontalPanel();
		hp.setWidth("100%");
		hp.setTableWidth("100%");

		TableData td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.CENTER);

		//toShow.setStyleAttribute("margin", "10px");
		RowLayout rlayout = new RowLayout(Orientation.VERTICAL);
		toShow.setLayout(rlayout);		

		String toAdd = "";
		toAdd = "<div style=\"width:100%; font-size:24px; color: gray;text-align:center;\">Cloud available</div>";
		
		
		hp.add(new Html(toAdd), td);

		HorizontalPanel hp2 = new HorizontalPanel();
		hp2.setWidth("100%");
		hp2.setTableWidth("100%");
		
		hp2.add(new Image(ImagesConstants.CLOUD_IMAGE), td);

		toShow.add(hp);
		toShow.add(hp2);


		FormData formData = new FormData("-20");  

		FormPanel form = new FormPanel();  
		form.setHeaderVisible(false);
		form.setBorders(false);
		form.setBodyBorder(false);
		form.setFrame(false);  
		form.setWidth("100%");  
		

		FieldSet fieldSet = new FieldSet();  
		fieldSet.setHeading("Resources Setup");  
		//fieldSet.setCheckboxToggle(true);  

		FormLayout layout = new FormLayout();  
		layout.setLabelWidth(100);  
		fieldSet.setLayout(layout);  

		
		
		final CheckBox useCloud = new CheckBox();  
		useCloud.addListener(Events.Change, new Listener<FieldEvent>() {
			public void handleEvent(FieldEvent be) {
				boolean checked = useCloud.getValue();
				controller.maskCenterPanel(checked);
				saveButton.setEnabled(checked);
			}
		});


		controller.maskCenterPanel(selected);
		useCloud.setValue(selected);
		
		
		useCloud.setBoxLabel("");  

		CheckBoxGroup checkGroup = new CheckBoxGroup();  
		checkGroup.setFieldLabel("Use Cloud");  
		checkGroup.add(useCloud);  

		final SimpleComboBox<String> combo = new SimpleComboBox<String>();  
		combo.setFieldLabel("Virtual machines");  
		combo.add("2");
		combo.add("3");
		combo.add("4");
		combo.add("5");
		combo.add("10");
		combo.add("20");
		combo.setTriggerAction(TriggerAction.ALL);
		combo.setSimpleValue("2");
		combo.setAllowBlank(false);
		combo.setEditable(false);
		
		if (vmSelected != -1) {
			combo.setSimpleValue(""+vmSelected);
		}

		fieldSet.add(checkGroup, formData);  
		fieldSet.add(combo, formData);  
		form.add(fieldSet, formData); 

		form.addButton(saveButton);		
		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
			@Override  
			public void componentSelected(ButtonEvent ce) { 
				int vMachines = Integer.parseInt(combo.getSimpleValue());
				controller.setCloudSelected(true, vMachines);
				
			}  
		});  
	
		toShow.add(form, new RowData(-1, 165));  


		add(toShow);
		layout();

	}
}
