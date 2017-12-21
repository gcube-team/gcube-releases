package org.gcube.application.aquamaps.aquamapsportlet.client.details;

import org.gcube.application.aquamaps.aquamapsportlet.client.AquaMapsPortlet;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.AquaMapsPortletCostants;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientResourceType;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.Msg;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.SettingsDescriptor;
import org.gcube.application.aquamaps.aquamapsportlet.client.selections.ResourcePickerPopUp;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.MultiFieldPanel;
import com.gwtext.client.widgets.form.NumberField;
import com.gwtext.client.widgets.form.TextField;

public class SubmissionForm extends FormPanel {

	TextField titleField=new TextField("Title of job");
	
	NumberField biodNumber=new NumberField("Biodiversity AquaMaps objects to generate");  
	NumberField distNumber=new NumberField("Species Distribution AquaMaps objects to generate");
//	NumberField speciesNumber = new NumberField("Total selected species");
	Button submit=new Button("Submit");
	
	TextField HSPECField=new TextField("Selected HSPEC");
	
	
	private SubmissionForm instance=this;
	
	public SubmissionForm() {		
		this.setTitle("Submission");
		this.setFrame(true);
//		this.setLayout(new TableLayout(3));
		this.setMonitorValid(true);
		
		titleField.setAllowBlank(false);
		titleField.setBlankText("Choose a name for the job");
		titleField.setValue(null);
//		titleField.addListener(new TextFieldListenerAdapter(){
//			@Override
//			public void onValid(Field field) {
//				submit.enable();
//			}
//			@Override
//			public void onInvalid(Field field, String msg) {
//				submit.disable();
//			}
//		});
//		speciesNumber.setDisabled(true);
//		speciesNumber.setAllowDecimals(false);
		MultiFieldPanel firstRow=new MultiFieldPanel();
		firstRow.addToRow(titleField,AquaMapsPortletCostants.FILTER_WIDTH);
//		firstRow.addToRow(speciesNumber,AquaMapsPortletCostants.FILTER_WIDTH);
		biodNumber.setDisabled(true);
		biodNumber.setAllowDecimals(false);
		firstRow.addToRow(biodNumber,AquaMapsPortletCostants.FILTER_WIDTH);		
		distNumber.setDisabled(true);
		distNumber.setAllowDecimals(false);
		firstRow.addToRow(distNumber,AquaMapsPortletCostants.FILTER_WIDTH);
		this.add(firstRow);
		
		MultiFieldPanel secondRow=new MultiFieldPanel();		
		
		HSPECField.setAutoWidth(true);
		HSPECField.setDisabled(true);
		secondRow.addToRow(HSPECField, AquaMapsPortletCostants.FILTER_WIDTH);
		Button HCAFButton =new Button("Change");
		HCAFButton.addListener(new ButtonListenerAdapter(){
			@Override
			public void onClick(Button button, EventObject e) {
				ResourcePickerPopUp popup=new ResourcePickerPopUp(ClientResourceType.HSPEC);
				popup.show(button.getElement());
			}
		});
		secondRow.addToRow(HCAFButton, AquaMapsPortletCostants.FILTER_WIDTH);
		
		
		this.add(secondRow);
		
		
		
		this.setButtons(new Button[]{submit});
		submit.addListener(new ButtonListenerAdapter(){
			@Override
			public void onClick(Button button, EventObject e) {
				String title=titleField.getValueAsString();
				if((title==null)||(title.equals(""))){
					AquaMapsPortlet.get().showMessage("Please, insert a name for the submitting Job");
				}else {
					AquaMapsPortlet.get().showLoading("Submitting "+title, AquaMapsPortlet.get().mainPanel.getId());
					AquaMapsPortlet.remoteService.submitJob(title,new AsyncCallback<Msg>(){

						public void onFailure(Throwable caught) {
							AquaMapsPortlet.get().hideLoading(AquaMapsPortlet.get().mainPanel.getId());
							AquaMapsPortlet.get().showMessage("Unable to submit current Job");
							Log.error("[checkForMapGenerationNeedsCallback]", caught);

						}

						public void onSuccess(Msg result) {
							AquaMapsPortlet.get().hideLoading(AquaMapsPortlet.get().mainPanel.getId());
							AquaMapsPortlet.get().showMessage(result.getMsg());
							instance.reload();
						}

					});
				}
			}
		});
		
		
		this.addListener(new PanelListenerAdapter(){
			@Override
			public void onActivate(Panel panel) {
				instance.reload();
			}
		});
	}
	public void reload(){
		AquaMapsPortlet.get().showLoading("Loading stats", instance.getId());
		AquaMapsPortlet.localService.getStats(new AsyncCallback<SettingsDescriptor>(){
			public void onSuccess(SettingsDescriptor result) {
				AquaMapsPortlet.get().hideLoading(instance.getId());
				Log.debug(result+"");
//				speciesNumber.setValue(result.getSpeciesInBasket());
				biodNumber.setValue(result.getBiodiversityObjectsCount());
				distNumber.setValue(result.getSpeciesDistributionObjectCount());
				HSPECField.setValue(result.getHspecTitle());
				titleField.setValue(result.getToSubmitName());
//				if(result.getSubmittable().getStatus()){
//					submit.enable();
//				}else{
//					submit.disable();
//				}
			}
			
			public void onFailure(Throwable caught) {
				AquaMapsPortlet.get().hideLoading(instance.getId());
				AquaMapsPortlet.get().showMessage("Sorry, an error occurred while trying to load session details. Please retry");
				Log.debug("Submission Form reload : "+caught.getMessage(),	 caught);
			}
		});
	}
	
}
