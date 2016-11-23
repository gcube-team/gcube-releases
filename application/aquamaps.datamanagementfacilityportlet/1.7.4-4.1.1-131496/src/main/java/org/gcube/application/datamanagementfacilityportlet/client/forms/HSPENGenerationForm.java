package org.gcube.application.datamanagementfacilityportlet.client.forms;

import java.util.List;

import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.Algorithm;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.ClientResource;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.ClientTinyResource;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.ExecutionEnvironmentModel;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.GroupGenerationRequest;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.ClientLogicType;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.ClientResourceType;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.FormData;

public class HSPENGenerationForm extends SourceGenerationForm {

	
	final MultiSourceSelector hcafPicker=new MultiSourceSelector(ClientResourceType.HCAF,"Select HCAFs");
	final MultiSourceSelector hspenPicker=new MultiSourceSelector(ClientResourceType.HSPEN,"Select HSPENs");
	final MultiSourceSelector occurPicker=new MultiSourceSelector(ClientResourceType.OCCURRENCECELLS,"Select OCCURRENCEs");
	
	
	public HSPENGenerationForm() {
		//Sources Selection
		
		addSourceTab(hcafPicker, DEFAULT_SOURCE_SELECTION_TAB_POSITION);
		addSourceTab(hspenPicker, DEFAULT_SOURCE_SELECTION_TAB_POSITION+1);
		addSourceTab(occurPicker, DEFAULT_SOURCE_SELECTION_TAB_POSITION+2);
//		getParent().setWidth("700");
//		Log.debug("HSPEN Form parent is "+getParent().getClass());
	}
	
	@Override
	protected ClientLogicType getLogic() {
		return ClientLogicType.HSPEN;
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
			
			List<ClientTinyResource> sources=hspenPicker.getSelection();
			if(sources.size()==0) throw new InvalidSettingsException("You need to select at least one HSPEN source");
			for(ClientTinyResource r: sources)
				toReturn.getSourceIds().add(r.getId());
			
			sources=hcafPicker.getSelection();
			if(sources.size()==0) throw new InvalidSettingsException("You need to select at least one HCAF source");
			for(ClientTinyResource r: sources)
				toReturn.getSourceIds().add(r.getId());
			
			sources=occurPicker.getSelection();
			if(sources.size()==0) throw new InvalidSettingsException("You need to select at least one OCCURRENCE source");
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
