package org.gcube.application.datamanagementfacilityportlet.client.forms;

import org.gcube.application.datamanagementfacilityportlet.client.DataManagementFacility;
import org.gcube.application.datamanagementfacilityportlet.client.DataManagementFacilityConstants;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.Tags;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.ClientResource;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.Response;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.ClientResourceType;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.util.Params;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HtmlEditor;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SourceEditingPopup extends Window {

	final TextField<String> titleField = new TextField<String>(); 
	final HtmlEditor htmlDescription = new HtmlEditor();
	final HtmlEditor htmlDisclaimer = new HtmlEditor();
	final HtmlEditor htmlProvenance = new HtmlEditor();
	final CheckBox defaultCheck = new CheckBox();
	ResourcePickerComboBox sourceHSPEN=new ResourcePickerComboBox(ClientResourceType.HSPEN);
//	ResourcePickerComboBox sourceHSPEC=new ResourcePickerComboBox(ClientResourceType.HSPEC);
	ResourcePickerComboBox sourceHCAF=new ResourcePickerComboBox(ClientResourceType.HCAF);
	
	
	private SourceEditingPopup instance =this;
	
	public SourceEditingPopup(final ClientResource toEdit){
		this.setPlain(true);
		this.setPlain(true);  
		this.setSize(600,610);  
		this.setHeading("Resource ID "+toEdit.getSearchId()+" editing");  
		this.setLayout(new FitLayout());
		
		ComponentPlugin plugin = new ComponentPlugin() {  
			public void init(Component component) {  
				component.addListener(Events.Render, new Listener<ComponentEvent>() {  
					public void handleEvent(ComponentEvent be) {  
//						El elem = be.getComponent().el().findParent(".x-form-element", 3);  
//						// should style in external CSS  rather than directly  
//						elem.appendChild(XDOM.create("<div style='color: #615f5f;padding: 1 0 2 0px;'>" + be.getComponent().getData("text") + "</div>"));  
					}  
				});  
			}  
		}; 
		
		
		FormPanel formPanel = new FormPanel();  
		formPanel.setBorders(false);  
		formPanel.setBodyBorder(false);  
		formPanel.setLabelWidth(120);  
		formPanel.setPadding(5);  
		formPanel.setHeaderVisible(false); 
		formPanel.setFrame(true);
		formPanel.setHeight(height);
		
		
		//*************** TITLE
		
		titleField.setFieldLabel("Resource Title");  
		titleField.setAllowBlank(false);
		titleField.setData("text", "Enter a title for the execution.");
		titleField.addPlugin(plugin);
		titleField.setValue(toEdit.getTitle());
		formPanel.add(titleField, new FormData("100%"));
		
		//*************** DEFAULT
		
		defaultCheck.setBoxLabel("True");
		defaultCheck.setValue(toEdit.getIsDefault());
		
		CheckBoxGroup checkGroup = new CheckBoxGroup();  
		checkGroup.setFieldLabel("Use as default");  
		checkGroup.setData("text", "Set this source as default for its type.");
		checkGroup.addPlugin(plugin);
		checkGroup.add(defaultCheck);  
		
		
		formPanel.add(checkGroup, new FormData("100%"));
		

		//******************************** Descriptive Fields
		
		//*************** DESCRIPTION
		
//		htmlDescription.setHideLabel(true);
		htmlDescription.setFieldLabel("Description");
		htmlDescription.setHeight(130);
		htmlDescription.setData("text", "Enter a description for the execution.");
		htmlDescription.addPlugin(plugin);
		htmlDescription.setValue(toEdit.getDescription());
//		formPanel.add(htmlDescription, new FormData("100%"));  
		
		//*************** DISCLAIMER
		
//		htmlDisclaimer.setHideLabel(true);
		htmlDisclaimer.setFieldLabel("Disclaimer");
		htmlDisclaimer.setHeight(130);
		htmlDisclaimer.setData("text", "Enter a Disclaimer for the execution.");
		htmlDisclaimer.addPlugin(plugin);
		htmlDisclaimer.setValue(toEdit.getDisclaimer());
//		formPanel.add(htmlDisclaimer, new FormData("100%"));
		
		//*************** DISCLAIMER
		
//		htmlProvenance.setHideLabel(true);
		htmlProvenance.setFieldLabel("Provenance");
		htmlProvenance.setHeight(130);
		htmlProvenance.setData("text", "Enter a Provenance for the execution.");
		htmlProvenance.addPlugin(plugin);
		htmlProvenance.setValue(toEdit.getProvenance());
//		formPanel.add(htmlProvenance, new FormData("100%"));
		
		

		FieldSet metaFs=new FieldSet();
		FormLayout metaFsLayout = new FormLayout();
		metaFsLayout.setLabelWidth(75);  
		metaFs.setLayout(metaFsLayout);
		metaFs.setHeading("Descriptive information");
		metaFs.setCollapsible(true);
		metaFs.add(htmlDescription,new FormData("100%"));
		metaFs.add(htmlDisclaimer,new FormData("100%"));
		metaFs.add(htmlProvenance,new FormData("100%"));
		formPanel.add(metaFs, new FormData("100%"));
		//*************** Sources
		
				
		
//		sourceHSPEN.setHideLabel(true);
		sourceHSPEN.setFieldLabel(DataManagementFacilityConstants.resourceNames.get(ClientResourceType.HSPEN));
		sourceHSPEN.setData("text", "Select source "+DataManagementFacilityConstants.resourceNames.get(ClientResourceType.HSPEN)+", current ID : "+toEdit.getHspenId());
		sourceHSPEN.addPlugin(plugin);
//		formPanel.add(sourceHSPEN);
		
		
//		sourceHCAF.setHideLabel(true);
		sourceHCAF.setFieldLabel(DataManagementFacilityConstants.resourceNames.get(ClientResourceType.HCAF));
		sourceHCAF.setData("text", "Select source "+DataManagementFacilityConstants.resourceNames.get(ClientResourceType.HCAF)+", current ID : "+toEdit.getHcafId());
		sourceHCAF.addPlugin(plugin);
		
		
//		sourceHSPEC.setHideLabel(true);
//		sourceHSPEC.setFieldLabel(DataManagementFacilityConstants.resourceNames.get(ClientResourceType.HSPEN));
////		sourceHSPEC.setData("text", "Select source "+DataManagementFacilityConstants.resourceNames.get(ClientResourceType.HSPEN)+", current ID : "+toEdit.getHspenId());
//		sourceHSPEC.addPlugin(plugin);
		
		
		FieldSet sourcesFs=new FieldSet();
		sourcesFs.setHeading("Provenance");
		sourcesFs.setCollapsible(true);
		FormLayout sourcesFsLayout = new FormLayout();
		sourcesFsLayout.setLabelWidth(175);  
		
		sourcesFs.setLayout(sourcesFsLayout);
		if(toEdit.getType().equals(ClientResourceType.HSPEC))
			sourcesFs.add(sourceHSPEN,new FormData("100%"));
		if(toEdit.getType().equals(ClientResourceType.HSPEC)||toEdit.getType().equals(ClientResourceType.HSPEN))
			sourcesFs.add(sourceHCAF,new FormData("100%"));
		
		sourcesFs.collapse();
		
		formPanel.add(sourcesFs, new FormData("100%"));
		
		
		
		
		Button ok=new Button("Save",new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				instance.mask("Sending request..");
				toEdit.setTitle(titleField.getValue());
				toEdit.setDescription(htmlDescription.getValue());
				toEdit.setDisclaimer(htmlDisclaimer.getValue());
				toEdit.setProvenance(htmlProvenance.getValue());
				toEdit.setIsDefault(defaultCheck.getValue());
				if(toEdit.getType().equals(ClientResourceType.HSPEC)){
					try{
						ModelData model=sourceHSPEN.getValue();
						Integer toSetid=Integer.parseInt(model.get("searchid").toString());
						String toSetTable=model.get("tablename").toString();
						if(toEdit.getHspenId()==null||!toEdit.getHspenId().equals(toSetid+"")){
							toEdit.setHspenId(toSetid+"");
							toEdit.setHspenName(toSetTable);
						}
					}catch(RuntimeException e){
						Log.debug("Unable to set sourceHSPEN or sourceHSPEN not set ",e);
					}
				}
				
				if(toEdit.getType().equals(ClientResourceType.HSPEC)||toEdit.getType().equals(ClientResourceType.HSPEN)){
					try{
						ModelData model=sourceHCAF.getValue();
						Integer toSetid=Integer.parseInt(model.get("searchid").toString());
						String toSetTable=model.get("tablename").toString();
						if(toEdit.getHcafId()==null||!toEdit.getHcafId().equals(toSetid+"")){
							toEdit.setHcafId(toSetid+"");
							toEdit.setHcafName(toSetTable);
						}
					}catch(RuntimeException e){
						Log.debug("Unable to set sourceHSPEN or sourceHSPEN not set ",e);
					}
				}
				
				
				
				DataManagementFacility.localService.editSource(toEdit, new AsyncCallback<Response>() {

					public void onSuccess(Response arg0) {
						instance.unmask();
						if(arg0.getStatus()){
							Info.display("Edit Resource","Success");
							instance.close();
						}else{
							String message=(String) arg0.getAdditionalObjects().get(Tags.errorMessage);
							String v = Format.ellipse(message, 80);
							Info.display("Edit Resource","Failed to edit : {0}", new Params(v));
						}

					}

					public void onFailure(Throwable arg0) {
						Log.error("Unexpected error while submitting request", arg0);
						String message="Please contact support";
						String v = Format.ellipse(message, 80);
						Info.display("Edit Request","Unable to submit request : {0}", new Params(v));
						instance.unmask();
					}
				});
			}
		});
		formPanel.addButton(ok);
		this.add(formPanel);
	}
	
	
	
	
	
}
