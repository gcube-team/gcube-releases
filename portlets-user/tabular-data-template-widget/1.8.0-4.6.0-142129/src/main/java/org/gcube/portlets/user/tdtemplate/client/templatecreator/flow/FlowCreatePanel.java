/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.flow;

import java.util.Date;
import java.util.List;

import org.gcube.portlets.user.tdtemplate.client.TdTemplateConstants;
import org.gcube.portlets.user.tdtemplate.client.TdTemplateController;
import org.gcube.portlets.user.tdtemplate.shared.TdBehaviourModel;
import org.gcube.portlets.user.tdtemplate.shared.TdLicenceModel;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Oct 16, 2014
 *
 */
public class FlowCreatePanel extends LayoutContainer {

	protected SimpleComboBox<String> comboLicence = new SimpleComboBox<String>();
	protected SimpleComboBox<String> comboDuplicateBehaviour = new SimpleComboBox<String>();
	protected TextField<String> name = new TextField<String>();
	protected TextArea description = new TextArea();
	protected TextField<String> agency = new TextField<String>();
	protected TextField<String> rights = new TextField<String>();
	
	private FormData formData = new FormData("-20");  
	protected Button buttonCreateFlow = new Button("Create Flow");
	
	private HorizontalPanel hp;
	private FormPanel formPanel = new FormPanel();
	private FlowCreatePanel instance = this;

	private DateField toDate;
	private DateField fromDate;
	
	private boolean flowCreated = false;
	private boolean isReadOnly = false;
	
	  @Override  
	  protected void onRender(Element parent, int index) {  
	    super.onRender(parent, index);  
//	    formData = new FormData("-10");  
	    hp = new HorizontalPanel();
//	    vp.setLayout(new FitLayout());
		int width = 390;
		int height = 370;
		hp.setSize(width-10, height);
		formPanel.setWidth(width);
		hp.add(formPanel);
	    hp.setStyleAttribute("padding", "10px");
	    add(hp);  
	  }  
	  
	  
	
	public FlowCreatePanel(WindowFlowCreate windowFlowCreate) {
		formPanel.setHeaderVisible(false);
	    formPanel.setFrame(false);  
//	    formPanel.setWidth(350); 

	    initComboLicence(false);
	    initComboBehaviour(false);
		initListeners();

		name.setFieldLabel("Name");
		name.setAllowBlank(false);
		formPanel.add(name, formData);
		
		agency.setFieldLabel("Agency");
		agency.setAllowBlank(true);
		formPanel.add(agency,formData);

		rights.setFieldLabel("Rights");
		rights.setAllowBlank(true);
		formPanel.add(rights,formData);
		
		description.setFieldLabel("Description");
		description.setAllowBlank(true);
		formPanel.add(description,formData);
		
		ComponentPlugin plugin = new ComponentPlugin() {  
	      public void init(Component component) {  
	        component.addListener(Events.Render, new Listener<ComponentEvent>() {  
	          public void handleEvent(ComponentEvent be) {  
	            El elem = be.getComponent().el().findParent(".x-form-element", 3);  
	            // should style in external CSS  rather than directly  
	            elem.appendChild(XDOM.create("<div style='color: #615f5f;padding: 1 0 2 0px;'>" + be.getComponent().getData("text") + "</div>"));  
	          }  
	        });  
	      }  
	    };  
		
		fromDate = new DateField();  
		fromDate.setFieldLabel("Valid From");  
		fromDate.addPlugin(plugin);  
		fromDate.setData("text", "Enter valid from");  
		formPanel.add(fromDate, formData);  
	    
	    toDate = new DateField();
	    toDate.setFieldLabel("Valid Until To");  
	    toDate.addPlugin(plugin);  
	    toDate.setData("text", "Enter valid until to");  
	    formPanel.add(toDate, formData);  
	    
		fromDate.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {

				if(fromDate.isValid())
					toDate.setMinValue(fromDate.getValue());
				else{
//					toDate.reset();
					toDate.getDatePicker().clearState();
				}
			}
        });
		
		toDate.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {

				if(toDate.isValid()){
					fromDate.setMaxValue(toDate.getValue());
				}
				else{
//					fromDate.reset();
					fromDate.getDatePicker().clearState();
				}
			}
        });

		comboLicence.setAllowBlank(false);
	    formPanel.add(comboLicence, formData);
	    
		comboDuplicateBehaviour.setAllowBlank(false);
	    formPanel.add(comboDuplicateBehaviour, formData);
	    
	    formPanel.add(buttonCreateFlow);
	    formPanel.setButtonAlign(HorizontalAlignment.CENTER);
	    
	    FormButtonBinding binding = new FormButtonBinding(formPanel);  
	    binding.addButton(buttonCreateFlow);  
	}

	/**
	 * 
	 */
	private void initComboLicence(boolean enabled) {
		
		comboLicence.setFieldLabel("Licence");
		comboLicence.setEditable(false);
		comboLicence.setTriggerAction(TriggerAction.ALL);
		comboLicence.setEmptyText("Choose Licence");
		comboLicence.setAllowBlank(false);
		comboLicence.setEnabled(enabled);
		
		
		TdTemplateController.tdTemplateServiceAsync.getLicences(new AsyncCallback<List<TdLicenceModel>>() {

			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert(TdTemplateConstants.TEMPLATE_ERROR, "Get License error", null).show();
				
			}

			@Override
			public void onSuccess(List<TdLicenceModel> result) {
				
				for (TdLicenceModel licenceData : result) {
					comboLicence.add(licenceData.getLabel());
					comboLicence.setData(licenceData.getLabel(), licenceData);
				}
				
				comboLicence.setEnabled(true);
				
			}
		});
	}
	
	/**
	 * 
	 */
	private void initComboBehaviour(boolean enabled) {
		
		comboDuplicateBehaviour.setFieldLabel("Behaviour");
		comboDuplicateBehaviour.setEditable(false);
		comboDuplicateBehaviour.setTriggerAction(TriggerAction.ALL);
		comboDuplicateBehaviour.setEmptyText("On duplicate behaviour");
		comboDuplicateBehaviour.setAllowBlank(false);
		comboDuplicateBehaviour.setEnabled(enabled);
		
		
		TdTemplateController.tdTemplateServiceAsync.getBehaviours(new AsyncCallback<List<TdBehaviourModel>>() {

			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert(TdTemplateConstants.TEMPLATE_ERROR, "Get Behaviour error", null).show();
				
			}

			@Override
			public void onSuccess(List<TdBehaviourModel> result) {
				
				for (TdBehaviourModel behaviour : result) {
					comboDuplicateBehaviour.add(behaviour.getLabel());
					comboDuplicateBehaviour.setData(behaviour.getLabel(), behaviour);
				}
				
				comboDuplicateBehaviour.setEnabled(true);
				
			}
		});
	}
	
	

	protected void initListeners() {
		
		buttonCreateFlow.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				
				if(isValidForm()){
					flowCreated = true;
					buttonCreateFlow.setText("Update Flow");
					WindowFlowCreate.geInstance().hide();
				}else{
					flowCreated = false;
				}
			}
		});
		
	}

	public boolean isValidForm(){
		
		if(name.isValid() && comboLicence.isValid()){
			return true;
		}
		return false;
	}
	
	public String getLicenceId(){
		
		if(comboLicence.getSelection().size()>0){
			
			SimpleComboValue<String> selected = comboLicence.getSelection().get(0);
//			GWT.log("Selected "+selected);
//			GWT.log("Selected getValue "+selected.getValue());
			
			TdLicenceModel model = (TdLicenceModel) comboLicence.getData(selected.getValue());
			
//			GWT.log("model "+model);
			return model.getId();
		}
		return null;
	}
	
	public String getBehaviourId(){
		
		if(comboDuplicateBehaviour.getSelection().size()>0){
			
			SimpleComboValue<String> selected = comboDuplicateBehaviour.getSelection().get(0);
//			GWT.log("Selected "+selected);
//			GWT.log("Selected getValue "+selected.getValue());
			
			TdBehaviourModel model = (TdBehaviourModel) comboDuplicateBehaviour.getData(selected.getValue());

//			GWT.log("model "+model);
			return model.getId();
		}
		return null;
	}
	


	public String getName(){
		return name.getValue();
	}
	

	public String getDescription(){
		return description.getValue();
	}
	
	public String getAgency() {
		return agency.getValue();
	}

	public boolean isFlowCreated() {
		return flowCreated;
	}

	public boolean isFlowReadOnly() {
		return isReadOnly;
	}

	public String getRights() {
		return rights.getValue();
	}

	public Date getToDate() {
		return toDate.getValue();
	}

	public Date getFromDate() {
		return fromDate.getValue();
	}



	protected void setBehaviour(String duplicateBehaviourValue) {
		this.comboDuplicateBehaviour.add(duplicateBehaviourValue);
//		this.comboDuplicateBehaviour.getView().getSelectionModel().select(0, true);
		TdBehaviourModel tdBehaviourModel = new TdBehaviourModel(duplicateBehaviourValue, duplicateBehaviourValue);
		this.comboDuplicateBehaviour.setData(duplicateBehaviourValue, tdBehaviourModel);
		this.comboDuplicateBehaviour.select(0);
		this.comboDuplicateBehaviour.setSimpleValue(duplicateBehaviourValue);
		
	}

	protected void setLicence(String licenceValue) {
		this.comboLicence.add(licenceValue);
//		this.comboLicence.getView().getSelectionModel().select(0, true);
		TdLicenceModel tdLicenceModel = new TdLicenceModel(licenceValue, licenceValue);
		this.comboLicence.setData(licenceValue, tdLicenceModel);
		this.comboLicence.select(0);
		this.comboLicence.setSimpleValue(licenceValue);
	}

	protected void setName(String name) {
		this.name.setValue(name);
	}


	protected void setDescription(String description) {
		this.description.setValue(description);
	}


	protected void setAgency(String agency) {
		this.agency.setValue(agency);
	}


	protected void setRights(String rights) {
		this.rights.setValue(rights);
	}

	protected void setToDate(Date toDate) {
		this.toDate.setValue(toDate);
	}


	protected void setFromDate(Date fromDate) {
		this.fromDate.setValue(fromDate);
	}

	/**
	 * @param bool
	 */
	public void setAsReadOnly(boolean bool) {
	
		formPanel.setReadOnly(bool);
		buttonCreateFlow.setVisible(!bool);
		isReadOnly = bool;
	}
}
