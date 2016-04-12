package org.gcube.application.datamanagementfacilityportlet.client.forms;

import java.util.List;

import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.Algorithm;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.ClientTinyResource;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.ExecutionEnvironmentModel;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.GroupGenerationRequest;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.ClientLogicType;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.ClientResourceType;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.FormData;

public class HSPECGenerationForm extends SourceGenerationForm {

	final Radio dataRadio = new Radio();
	final Radio imagesRadio = new Radio(); 
	final Radio layersRadio = new Radio();
	
	final Radio combineAll=new Radio();
	final Radio combineMatching=new Radio();
	
	final MultiSourceSelector hspenSelector=new MultiSourceSelector(ClientResourceType.HSPEN, "Select HSPENs");
	final MultiSourceSelector hcafSelector=new MultiSourceSelector(ClientResourceType.HCAF, "Select HCAFs");
	
	final TinyResourceGrid hspenGrid=new TinyResourceGrid();
	final TinyResourceGrid hcafGrid=new TinyResourceGrid();
	
	final CheckBox forceMapGeneration=new CheckBox();
	
	
	public HSPECGenerationForm() {
		
		//Generation Tab 
		
		dataRadio.setBoxLabel("Data only");  
		dataRadio.setValue(true);  


		imagesRadio.setBoxLabel("Data and static images");  

		layersRadio.setBoxLabel("Data, images and GIS layers");

		RadioGroup generateGroup = new RadioGroup();  
		generateGroup.setFieldLabel("Data generation ");  
		generateGroup.add(dataRadio);  
		generateGroup.add(imagesRadio);
		generateGroup.add(layersRadio);
		generateGroup.setData("text", "Choose what you want to generate");
		generateGroup.setOrientation(Orientation.VERTICAL);
		generateGroup.addPlugin(Common.plugin);
		generationSettingsForm.add(generateGroup, new FormData("100%")); 
		
		
		
		
		combineMatching.setBoxLabel("Maching only");
		combineMatching.setValue(true);
		
		combineAll.setBoxLabel("All");
		
		RadioGroup combineGroup = new RadioGroup();  
		combineGroup.setFieldLabel("Combine sources");  
		combineGroup.add(combineMatching);  
		combineGroup.add(combineAll);
		combineGroup.setData("text", "Choose how service should combine selected sources");
		combineGroup.setOrientation(Orientation.HORIZONTAL);
		combineGroup.addPlugin(Common.plugin);
		generationSettingsForm.add(combineGroup, new FormData("100%")); 
		
		forceMapGeneration.setFieldLabel("Whether map existing");
		forceMapGeneration.setBoxLabel("Force regeneration");
		forceMapGeneration.setValue(false);		
		generationSettingsForm.add(forceMapGeneration, new FormData("100%")); 
		
		//Source Selection
		addSourceTab(hcafSelector, DEFAULT_SOURCE_SELECTION_TAB_POSITION);
		
		addSourceTab(hspenSelector, DEFAULT_SOURCE_SELECTION_TAB_POSITION+1);

	}
	
	
	@Override
	protected ClientLogicType getLogic() {
		return ClientLogicType.HSPEC;
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

			toReturn.getAdditionalParameters().put(GroupGenerationRequest.GENERATE_MAPS,!dataRadio.getValue());
			toReturn.getAdditionalParameters().put(GroupGenerationRequest.GIS_ENABLED,layersRadio.getValue());
			toReturn.getAdditionalParameters().put(GroupGenerationRequest.FORCE_MAPS_REGENERATION, forceMapGeneration.getValue());
			toReturn.getAdditionalParameters().put(GroupGenerationRequest.COMBINE_MATCHING,combineMatching.getValue());
			
			
			//************* CHECK SOURCES
			if(combineMatching.getValue()){
				int foundMatching=0;
				for(ClientTinyResource hcaf:hcafSelector.getSelection())
					for(ClientTinyResource hspen:hspenSelector.getSelection()){
						if(hspen.getSourceHcafIds()!=null)
						for(String idString:hspen.getSourceHcafIds().split(","))
							try{
								if(hcaf.getId().equals(Integer.parseInt(idString))) foundMatching++;
							}catch(Exception e){
								Log.warn("Unable to parse id "+idString,e);
							}
					}
				if(foundMatching==0) throw new InvalidSettingsException("Current configuration won't generate any data, please check again selected sources.");
			}
			
			
			List<ClientTinyResource> sources=hspenSelector.getSelection();
			if(sources.size()==0) throw new InvalidSettingsException("You need to select at least one HSPEN source");
			for(ClientTinyResource r: sources)
				toReturn.getSourceIds().add(r.getId());
			
			
			sources=hcafSelector.getSelection();
			if(sources.size()==0) throw new InvalidSettingsException("You need to select at least one HCAF source");
			for(ClientTinyResource r: sources)
				toReturn.getSourceIds().add(r.getId());
			
			
			
			
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
