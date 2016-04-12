package org.gcube.portlets.admin.vredefinition.client.view;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.admin.vredefinition.client.presenter.VREDescriptionPresenter;
import org.gcube.portlets.admin.vredefinition.shared.VREDescriptionBean;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Widget;


public class VREDescriptionView extends Composite implements VREDescriptionPresenter.Display{
	
	
	private TextField<String> vrename;
	private TextField<String> vredesigner;
	private DateField fromDate;
	private DateField toDate;
	private ComboBox<BaseModel> vremanager;
	private TextArea vredescription;
	
	
	public VREDescriptionView()  {
		
		FormData formData = new FormData("-20");  
		
		VerticalPanel panel = new VerticalPanel();
		panel.setSize("100%", "100%");
	
		panel.setStyleAttribute("padding", "10px");
		panel.setTableWidth("100%");
		
		
		FieldSet fieldSet = new FieldSet();
		fieldSet.setWidth("95%");
		fieldSet.setHeading("VRE Information");
		
		FormLayout layout = new FormLayout();  
		layout.setLabelWidth(100);  
		fieldSet.setLayout(layout);
				
		vrename = new TextField<String>();
		vrename.setFieldLabel("Name");  
		vrename.setAllowBlank(false);
		vrename.setValidator(new Validator() {
			public String validate (Field ticketField, String value){
				if (vrename.getRawValue().contains(" "))  
					return "No blanks allowed, a VRE name must be a single word";
				else
					return null;
			}
		});
		fieldSet.add(vrename,formData);  

		vredesigner = new TextField<String>();  
		vredesigner.setFieldLabel("Designer"); 
	
		vredesigner.setAllowBlank(false);
		fieldSet.add(vredesigner,formData); 
		
		
		ListStore<BaseModel> store = new ListStore<BaseModel>();
		
		BaseModel managerModel = new BaseModel();
		managerModel.set("name", "Manager");
		store.add(managerModel);
		
		vremanager = new ComboBox<BaseModel>();
		vremanager.setFieldLabel("VRE Manager");
		vremanager.setDisplayField("name");
		vremanager.setTriggerAction(TriggerAction.ALL);
		vremanager.setStore(store);
		vremanager.setAllowBlank(false);
		vremanager.setEditable(false);
		fieldSet.add(vremanager,formData);
		
		vredescription = new TextArea();  
		vredescription.setFieldLabel("Description");  
		vredescription.setValue("Write the Virtual Research Environment description here");
		vredescription.setAllowBlank(false);
		fieldSet.add(vredescription,formData);  

		
		panel.add(fieldSet);
		
		
		fieldSet = new FieldSet();
		fieldSet.setWidth("95%");
		fieldSet.setHeading("Life time");  
		
		layout = new FormLayout();  
		layout.setLabelWidth(100);  
		fieldSet.setLayout(layout);  

		DateTimeFormat fmt = DateTimeFormat.getFormat("MMMM dd, yyyy");
		
		fromDate = new DateField();  
		fromDate.setName("date");  
		fromDate.setFieldLabel("From");  
		fromDate.getPropertyEditor().setFormat(fmt);
		fromDate.setValue(new Date());
		
		fromDate.setAllowBlank(false);
		fromDate.setAutoValidate(true);
		fieldSet.add(fromDate,formData);

		toDate = new DateField();  
		toDate.setName("date");  
		toDate.setFieldLabel("To"); 
	
		Date date = new Date();
		date.setYear(date.getYear() + 1);
		toDate.setValue(date);
		toDate.getPropertyEditor().setFormat(fmt);
		
		toDate.setAllowBlank(false);
		toDate.setAutoValidate(true);
		fieldSet.add(toDate,formData);  

	
		panel.add(fieldSet); 
		
		initComponent(panel);
		
	}
	

	public void setData(Map<String,Object> result, VREDescriptionBean bean) {
		
		ListStore<BaseModel> store = new ListStore<BaseModel>();
		vremanager.setStore(store);
		
		if (result != null && result.get("Manager")!= null) {
			for(String value : (List<String>)result.get("Manager")){
				BaseModel managerModel = new BaseModel();
				managerModel.set("name", value);
				store.add(managerModel);
				vremanager.setValue(managerModel);
			}
			
		}
		
		if(result != null && result.get("Designer") != null)
			vredesigner.setValue((String)result.get("Designer"));
		
		if(bean == null)
			return;
		
		ListStore<BaseModel> list = vremanager.getStore();
		for(int i = 0; i < list.getCount(); i++) {
			BaseModel manager = list.getAt(i);
			if(manager.get("name").equals(bean.getManager())) {
				vremanager.setValue(manager);
			}
		}
		
		vrename.setValue(bean.getName());
	    vredesigner.setValue(bean.getDesigner());
		vredescription.setValue(bean.getDescription());
		fromDate.setValue(bean.getStartTime());
		toDate.setValue(bean.getEndTime());
	}

	public TextField<String> getVREName() {
		return vrename;
	}
	
	public TextField<String> getVREDesigner() {
		return vredesigner;
	}
	
	public ComboBox<BaseModel> getVREManager() {
		return vremanager;
	}
	
	public TextArea getVREDescription() {
		return vredescription;
	}
	
	public Widget asWidget() {
		return this;
	}

	
	public DateField getFromDate() {

		return fromDate;
	}


	public DateField getToDate() {

		return toDate;
	}

	
}
