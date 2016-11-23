package org.gcube.application.datamanagementfacilityportlet.client.forms;

import java.sql.Timestamp;

import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.Algorithm;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.ClientResource;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.ExecutionEnvironmentModel;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.GroupGenerationRequest;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.ClientLogicType;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.ClientResourceType;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SpinnerField;
import com.extjs.gxt.ui.client.widget.layout.FormData;

public class HCAFGenerationForm extends SourceGenerationForm {

	final ResourcePickerComboBox firstHcafPicker=new ResourcePickerComboBox(ClientResourceType.HCAF);
	final ResourcePickerComboBox secondHcafPicker=new ResourcePickerComboBox(ClientResourceType.HCAF);
	
	
	final SpinnerField firstHcafYearField = new SpinnerField();  
	final SpinnerField secondHcafYearField = new SpinnerField(); 
	final SpinnerField numInterpolationField = new SpinnerField();  
	
	public HCAFGenerationForm() {
		//Source Selection
		FormPanel sourcesSelectionForm=new FormPanel();
		
		firstHcafPicker.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				String timeString=se.getSelectedItem().get(ClientResource.GENERATION_TIME);
				try{
					Timestamp t=new Timestamp(Long.parseLong(timeString));
					firstHcafYearField.setValue(1900+t.getYear());
				}catch(Throwable t){
					Log.error("Unable to parse Year",t);
				}
			}
		});
		secondHcafPicker.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				String timeString=se.getSelectedItem().get(ClientResource.GENERATION_TIME);
				try{
					Timestamp t=new Timestamp(Long.parseLong(timeString));
					secondHcafYearField.setValue(1900+t.getYear());
				}catch(Throwable t){
					Log.error("Unable to parse Year",t);
				}
			}
		});
		
		firstHcafPicker.setFieldLabel("First HCAF");
		sourcesSelectionForm.add(firstHcafPicker, new FormData("100%"));
		
		firstHcafYearField.setFieldLabel("First Hcaf Year");
		firstHcafYearField.setIncrement(1);  
		firstHcafYearField.getPropertyEditor().setType(Integer.class);  
		firstHcafYearField.setMinValue(1970);  
		firstHcafYearField.setMaxValue(2200);
		firstHcafYearField.setData("text", "Specify first HCAF reference year ");
		firstHcafYearField.addPlugin(Common.plugin);
		sourcesSelectionForm.add(firstHcafYearField);
		
		
		secondHcafPicker.setFieldLabel("Second HCAF");
		sourcesSelectionForm.add(secondHcafPicker,new FormData("100%"));
		
		secondHcafYearField.setFieldLabel("Second Hcaf Year");
		secondHcafYearField.setIncrement(1);  
		secondHcafYearField.getPropertyEditor().setType(Integer.class);  
		secondHcafYearField.setMinValue(1970);  
		secondHcafYearField.setMaxValue(2200);
		secondHcafYearField.setData("text", "Specify second HCAF reference year ");
		secondHcafYearField.addPlugin(Common.plugin);
		sourcesSelectionForm.add(secondHcafYearField);
		
		
		numInterpolationField.setFieldLabel("Interpolations");
		numInterpolationField.setIncrement(1);  
		numInterpolationField.getPropertyEditor().setType(Integer.class);  
		numInterpolationField.setMinValue(1);  
		numInterpolationField.setMaxValue(10);
		numInterpolationField.setData("text", "Specify how many interpolations need to be generated");
		numInterpolationField.addPlugin(Common.plugin);
		numInterpolationField.setValue(1);
		sourcesSelectionForm.add(numInterpolationField);
		
		sourcesSelectionForm.setHeading("Select HCAFs");
		addSourceTab(sourcesSelectionForm, DEFAULT_SOURCE_SELECTION_TAB_POSITION);
	}
	
	@Override
	protected ClientLogicType getLogic() {
		return ClientLogicType.HCAF;
	}

	@Override
	public GroupGenerationRequest getSettings() {
		GroupGenerationRequest toReturn=new GroupGenerationRequest();
		try{
			toReturn.setGenerationname(titleField.getValue());
			if(toReturn.getGenerationname()==null) throw new InvalidSettingsException("Request tile can't be empty");			    		   
			
			toReturn.setDescription(htmlDescription.getValue());
			
			ListStore<Algorithm> toStore=lists.getToList().getStore();
			for(int i=0; i<toStore.getCount();i++)
				toReturn.getAlgorithms().add(toStore.getAt(i).getType());
			if(toReturn.getAlgorithms().size()<1)  throw new InvalidSettingsException("Algorithm selection can't be empty");
			
			toReturn.setLogic(getLogic());
			
			if(firstHcafPicker.getValue()==null) throw new InvalidSettingsException("Please select initial HCAF");
			Integer firstHcafId=Integer.parseInt(firstHcafPicker.getValue().get(ClientResource.SEARCH_ID)+"");
			toReturn.getSourceIds().add(firstHcafId);
			
			
			if(secondHcafPicker.getValue()==null) throw new InvalidSettingsException("Please select final HCAF");
			Integer secondHcafId=Integer.parseInt(secondHcafPicker.getValue().get(ClientResource.SEARCH_ID)+"");
			if(firstHcafId.equals(secondHcafId)) throw new InvalidSettingsException("Final and initial HCAFs must be different");
			toReturn.getSourceIds().add(secondHcafId);
			
			
			
			Integer firstHcafYear=firstHcafYearField.getValue().intValue();
			Integer secondHcafYear=secondHcafYearField.getValue().intValue();
			if(firstHcafYear.equals(secondHcafYear))throw new InvalidSettingsException("Sources cannot refere to the same year");
			if(firstHcafYear.intValue()>secondHcafYear.intValue())throw new InvalidSettingsException("First Hcaf reference year must precede second Hcaf's");
			
			
			Integer numInterpolations=numInterpolationField.getValue().intValue()+2;
			
			toReturn.getAdditionalParameters().put(GroupGenerationRequest.FIRST_HCAF_ID, firstHcafId);
			toReturn.getAdditionalParameters().put(GroupGenerationRequest.FIRST_HCAF_TIME, firstHcafYear);
			toReturn.getAdditionalParameters().put(GroupGenerationRequest.SECOND_HCAF_ID, secondHcafId);
			toReturn.getAdditionalParameters().put(GroupGenerationRequest.SECOND_HCAF_TIME, secondHcafYear);
			toReturn.getAdditionalParameters().put(GroupGenerationRequest.NUM_INTERPOLATIONS, numInterpolations);
			
			
			
			ExecutionEnvironmentModel model= grid.getSelectionModel().getSelectedItem();
			toReturn.setExecutionEnvironment(model);
			
			if(toReturn.getExecutionEnvironment()==null) throw new InvalidSettingsException("Please select an execution environment.");
			
			if(toReturn.getExecutionEnvironment().getDefaultPartitions()>toReturn.getExecutionEnvironment().getMaxPartitions()
					||toReturn.getExecutionEnvironment().getDefaultPartitions()<toReturn.getExecutionEnvironment().getMinPartitions()) 
				throw new InvalidSettingsException("Please specify e valid number of resource for the selected environment");
			
			
			return toReturn;
		}catch(InvalidSettingsException e){
			MessageBox.alert("Invalid request",e.getMessage(),l);
		}catch(Throwable e){
			Log.error("Unable to load configuration ", e);
		}
		return null;
	}

}
