package org.gcube.application.aquamaps.aquamapsportlet.client.perturbation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.application.aquamaps.aquamapsportlet.client.AquaMapsPortlet;
import org.gcube.application.aquamaps.aquamapsportlet.client.ColumnDefinitions;
import org.gcube.application.aquamaps.aquamapsportlet.client.RecordDefinitions;
import org.gcube.application.aquamaps.aquamapsportlet.client.Stores;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.AquaMapsPortletCostants;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.EnvelopeFieldsClient;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SpeciesFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.details.EnvelopeGrid;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientEnvelope;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.Msg;
import org.gcube.application.aquamaps.aquamapsportlet.client.selections.ExtendedLiveGrid;
import org.gcube.application.aquamaps.aquamapsportlet.client.selections.OccurrenceCellsPopup;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.Store;
import com.gwtext.client.widgets.BoxComponent;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.grid.EditorGridPanel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.GridView;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.grid.event.EditorGridListenerAdapter;
import com.gwtext.client.widgets.grid.event.RowSelectionListenerAdapter;
import com.gwtext.client.widgets.layout.AnchorLayout;
import com.gwtext.client.widgets.layout.AnchorLayoutData;
import com.gwtext.client.widgets.layout.ColumnLayout;
import com.gwtext.client.widgets.layout.ColumnLayoutData;

public class EnvelopeGridsPanel extends Panel {

	
	public List<EnvelopeFieldsClient> customizedSelectedEnvelopeRows=null;
	
	
	
	
	public ExtendedLiveGrid grid=new ExtendedLiveGrid("Selected Species",Stores.selectedSpeciesStore(),ColumnDefinitions.selectedSpeciesColumnModel(),true);
	//OccurrenceCellsPopup occurrenceCells=null;
	public EditorGridPanel actualEnvelope;
	public EnvelopeCalulationForm form=new EnvelopeCalulationForm(); 
	EnvelopeGrid envGrid=new EnvelopeGrid("Species environmental tolerance",false,true);
	Panel upperSection=new Panel();
	ToolbarButton commit=new ToolbarButton("Commit envelope");
	ToolbarButton undo=new ToolbarButton("Undo Changes");
	ToolbarButton cells=new ToolbarButton("Show Occurrence Cells");
	ToolbarButton restore=new ToolbarButton("Restore HSPEN Settings");
//	ToolbarButton weightsButton=new ToolbarButton("Commit parameter evaluation");
	
//	private EnvelopeGridsPanel instance=this;
	
	
//	CheckboxSelectionModel cbm= new CheckboxSelectionModel();
	public EnvelopeGridsPanel() {
		this.setTitle("Species envelope customization");
		this.setLayout(new AnchorLayout());
		this.setHeight(AquaMapsPortletCostants.HEIGHT);
		actualEnvelope=new EditorGridPanel();
		actualEnvelope.setTitle("Species environmental tolerance");		
		actualEnvelope.setBottomToolbar(new ToolbarButton[]{commit,undo,cells});		
		Store actualStore=new Store(RecordDefinitions.envRecordDef);
		actualEnvelope.setStore(actualStore);		
		actualEnvelope.setColumnModel(ColumnDefinitions.envColumnModel(true));
		actualEnvelope.setAutoScroll(true);
		actualEnvelope.setStripeRows(true);
		GridView gridView = new GridView();		
		gridView.setAutoFill(true);
		gridView.setForceFit(true);
		actualEnvelope.setView(gridView);	
		actualEnvelope.setFrame(true);
		
		
		
		commit.disable();	
		undo.disable();
		cells.disable();
		restore.disable();
		
		cells.addListener(new ButtonListenerAdapter(){			
			public void onClick(Button button, EventObject e) {
				String speciesId=grid.getSelectionModel().getSelected().getAsString(SpeciesFields.speciesid+"");
				OccurrenceCellsPopup popup=new OccurrenceCellsPopup(speciesId);
				//occurrenceCells.setSpeciesId(speciesId);
				popup.show();
			}
		});
		
		restore.addListener(new ButtonListenerAdapter(){			
			public void onClick(Button button, EventObject e) {
				AquaMapsPortlet.get().showLoading("Restoring HSPEN Values", actualEnvelope.getId());
				final String speciesId=grid.getSelectionModel().getSelected().getAsString(SpeciesFields.speciesid+"");
				AquaMapsPortlet.localService.clearEnvelopeCustomization(speciesId,  new AsyncCallback<Msg>(){

					public void onFailure(Throwable caught) {
						AquaMapsPortlet.get().hideLoading(actualEnvelope.getId());
						AquaMapsPortlet.get().showMessage("Unable to restore HSPEN Values");
						Log.error("[setSpeciesCustomizationCallback]", caught);
					}

					public void onSuccess(Msg result) {
						AquaMapsPortlet.get().showMessage(result.getMsg());			
						AquaMapsPortlet.remoteService.getEnvelope(speciesId,true,envelopeRetrievalCallback);
						restore.disable();
						Log.debug("[setSpeciesCustomizationCallback] - "+result.getMsg());
						
					}	

				});
			}
		});
		
		
		
		//************************* Sends the chosen perturbation to the Servlet
		commit.addListener(new ButtonListenerAdapter(){			
			public void onClick(Button button, EventObject e) {
				//Setting modified values
				Map<SpeciesFields,Float> toSet=new HashMap<SpeciesFields, Float>();
				for(Record record : actualEnvelope.getStore().getRecords()){
					EnvelopeFieldsClient parameter=EnvelopeGrid.labelToParameter(record.getAsString("parameter"));
					if(record.isModified("min"))
						toSet.put(ClientEnvelope.getMinName(parameter),record.getAsFloat("min"));
					if(record.isModified("max"))
						toSet.put(ClientEnvelope.getMaxName(parameter), record.getAsFloat("max"));
					if(record.isModified("prefMin"))
						toSet.put(ClientEnvelope.getPrefMinName(parameter), record.getAsFloat("prefMin"));
					if(record.isModified("prefMax"))
						toSet.put(ClientEnvelope.getPrefMaxName(parameter), record.getAsFloat("prefMax"));
				}			
				ArrayList<String> ids=new ArrayList<String>();
				for(Record rec: grid.getSelectionModel().getSelections())
					ids.add(rec.getAsString(SpeciesFields.speciesid+""));
				AquaMapsPortlet.get().showLoading("Sending customization to server", actualEnvelope.getId());
				AquaMapsPortlet.localService.setEnvelopeCustomization(grid.getSelectionModel().getSelected().getAsString(SpeciesFields.speciesid+""), toSet, new AsyncCallback<Msg>(){

					public void onFailure(Throwable caught) {
						AquaMapsPortlet.get().hideLoading(actualEnvelope.getId());
						AquaMapsPortlet.get().showMessage("Unable to set species Customization");
						Log.error("[setSpeciesCustomizationCallback]", caught);
					}

					public void onSuccess(Msg result) {							
						AquaMapsPortlet.get().hideLoading(actualEnvelope.getId());
						commit.disable();
						AquaMapsPortlet.get().showMessage(result.getMsg());						
						Log.debug("[setSpeciesCustomizationCallback] - "+result.getMsg());
						
					}	

				});
				//Setting weights if any change
				
				commit.disable();
				undo.disable();
				actualEnvelope.getStore().commitChanges();
				restore.enable();
			}		
		});
		

		undo.addListener(new ButtonListenerAdapter(){
			
			public void onClick(Button button, EventObject e) {
				actualEnvelope.getStore().rejectChanges();
//				envelopeRowSelection(grid.getSelectionModel().getSelected().getAsString(SpeciesFields.speciesid+""));
				undo.disable();
				commit.disable();
			}
		});
		
		/* ************************ On Selection Change
		 * *
		 * *----enabling /disabling buttons
		 * *
		 * *----retrieves actual envelop and customizations 
		 */
		
		
		grid.getSelectionModel().addListener(new RowSelectionListenerAdapter(){
			
			public void onRowSelect(RowSelectionModel sm, int rowIndex,
					Record record) {				
				String speciesId=record.getAsString(SpeciesFields.speciesid+"");
				String scientificName=sm.getSelected().getAsString(SpeciesFields.scientific_name+"");
				AquaMapsPortlet.get().showLoading("Retrieving Envelope", actualEnvelope.getId());
				AquaMapsPortlet.remoteService.getEnvelope(speciesId,true,envelopeRetrievalCallback);
				envGrid.setCurrentSpecies(speciesId,scientificName,false);
			}
			
			
			public void onRowDeselect(RowSelectionModel sm, int rowIndex,
					Record record) {
				commit.disable();
				undo.disable();
				actualEnvelope.getStore().removeAll();
				cells.disable();
//				weightsButton.disable();
			}
			
		
		});
		
		actualEnvelope.addEditorGridListener(new EditorGridListenerAdapter(){			
			public void onAfterEdit(GridPanel grid, Record record,
					String field, Object newValue, Object oldValue,
					int rowIndex, int colIndex) {
				commit.enable();
				undo.enable();
			}
		});
		actualEnvelope.getSelectionModel().addListener(new RowSelectionListenerAdapter(){			
			public void onSelectionChange(RowSelectionModel sm) {
				boolean changedWeights = false;
				List<EnvelopeFieldsClient> currentSetting=customizedSelectedEnvelopeRows;
				if((currentSetting==null)||(currentSetting.size()==0)) currentSetting=EnvelopeGrid.defaultSelectedEnvelopeRows;
				for(Record record : actualEnvelope.getStore().getRecords()){										
					if((currentSetting.contains(EnvelopeFieldsClient.valueOf(record.getId())))^
							(actualEnvelope.getSelectionModel().isSelected(record))){ // previously selected XOR selected
						changedWeights=true;
						break;
					}
				}
				Log.debug("changed Weights : "+changedWeights);
//				if(changedWeights){					
////					weightsButton.enable();
//				}
//				else weightsButton.disable();
			}
		});
	
		final Panel wrapper=new Panel();		
		
		upperSection.setLayout(new ColumnLayout());
		upperSection.setFrame(true);
		upperSection.add(grid,new ColumnLayoutData(.5));
		upperSection.add(envGrid,new ColumnLayoutData(.5));
		upperSection.addListener(new PanelListenerAdapter(){
			@Override
			public void onResize(BoxComponent component, int adjWidth,
					int adjHeight, int rawWidth, int rawHeight) {			
				super.onResize(component, adjWidth, adjHeight, rawWidth, rawHeight);
				Log.debug("Resizing AdvGenPan- Upper..");
				grid.setHeight(adjHeight-10);
				envGrid.setHeight(adjHeight-10);

			}
		});
		
		
		this.add(upperSection,new AnchorLayoutData("100% 50%"));
		this.add(wrapper,new AnchorLayoutData("100% 50%"));
		this.addListener(new PanelListenerAdapter(){			
			public void onResize(BoxComponent component, int adjWidth,
					int adjHeight, int rawWidth, int rawHeight) {
				super.onResize(component, adjWidth, adjHeight, rawWidth, rawHeight);
				Log.debug("Envelope Grids : resizing main");
				wrapper.setHeight(adjHeight/2-5);
				grid.setHeight(adjHeight/2-5);
			}
			@Override
			public void onActivate(Panel panel) {
				Log.debug("Envelope Grids : reload post activation");
				grid.getStore().reload();
			}
		});
		wrapper.setLayout(new ColumnLayout());
		wrapper.add(form,new ColumnLayoutData(.3));
		wrapper.add(actualEnvelope,new ColumnLayoutData(.7));
		wrapper.addListener(new PanelListenerAdapter(){			
			public void onResize(BoxComponent component, int adjWidth,
					int adjHeight, int rawWidth, int rawHeight) {
				int finalHeight=(adjHeight>AquaMapsPortletCostants.EnvelopeGridsHeight)?adjHeight:AquaMapsPortletCostants.EnvelopeGridsHeight;
				form.setHeight(finalHeight);
				actualEnvelope.setHeight(finalHeight);
			}
		});
		grid.setCollapsible(true);
	}

	
	
	public AsyncCallback<ClientEnvelope> envelopeRetrievalCallback=new AsyncCallback<ClientEnvelope>(){

		public void onFailure(Throwable caught) {
			AquaMapsPortlet.get().hideLoading( actualEnvelope.getId());
			AquaMapsPortlet.get().showMessage("Unable to retrieve species envelope, "+caught.getMessage());
			Log.error("[getEnvelopeCallback]", caught);
		}
		public void onSuccess(ClientEnvelope result) {
			form.loadEnvelope(result);
			Store store=actualEnvelope.getStore();
			store.removeAll();
			for(EnvelopeFieldsClient param:EnvelopeGrid.envelopeFieldsOrder){							
					Record record=RecordDefinitions.envRecordDef.createRecord(param.toString(), new Object[]{
							EnvelopeGrid.getParameterLabel(param),							
							((param.equals(EnvelopeFieldsClient.IceConcentration))&&(result.getMinValue(param)<0)?0:EnvelopeGrid.formatter.format(result.getMinValue(param))), //Depth min =>0
							//result.getMinValue(param),
							EnvelopeGrid.formatter.format(result.getMaxValue(param)),
							EnvelopeGrid.formatter.format(result.getPrefMinValue(param)),
							EnvelopeGrid.formatter.format(result.getPrefMaxValue(param)),
					});
					store.add(record);
					//Log.debug("Loaded "+getParameterLabel(param)+" "+result.getMinValue(param)+" "+result.getMaxValue(param)+" "+result.getPrefMinValue(param)+" "+result.getPrefMaxValue(param)+" ");
			}
//			envelopeRowSelection(grid.getSelectionModel().getSelected().getAsString(SpeciesFields.speciesid+""));
			cells.enable();
			AquaMapsPortlet.get().hideLoading( actualEnvelope.getId());						
		}
	};
	
	public AsyncCallback<ClientEnvelope> envelopeRecalculationCallback=new AsyncCallback<ClientEnvelope>(){

		public void onFailure(Throwable caught) {
			AquaMapsPortlet.get().hideLoading( actualEnvelope.getId());
			AquaMapsPortlet.get().showMessage("Unable to retrieve species envelope, "+caught.getMessage());
			Log.error("[getEnvelopeCallback]", caught);
		}
		public void onSuccess(ClientEnvelope result) {			
			Store store=actualEnvelope.getStore();			
			for(EnvelopeFieldsClient param:EnvelopeGrid.envelopeFieldsOrder){	
				Record rec=store.getById(param.toString());
				rec.beginEdit();
				if(rec.getAsFloat("min")!=result.getMinValue(param)) rec.set("min", result.getMinValue(param));
				if(rec.getAsFloat("prefMin")!=result.getPrefMinValue(param)) rec.set("prefMin", result.getPrefMinValue(param));
				if(rec.getAsFloat("max")!=result.getMaxValue(param)) rec.set("max", result.getMaxValue(param));
				if(rec.getAsFloat("prefMax")!=result.getPrefMaxValue(param)) rec.set("prefMax", result.getPrefMaxValue(param));
				rec.endEdit();
			}
			Record [] modified=actualEnvelope.getStore().getModifiedRecords();
			if((modified!=null)&&(modified.length>0)) commit.enable(); 
//			envelopeRowSelection(grid.getSelectionModel().getSelected().getAsString(SpeciesFields.speciesid+""));
			AquaMapsPortlet.get().hideLoading( actualEnvelope.getId());						
		}
	};
	
//	public void envelopeRowSelection(String speciesId){
//		AquaMapsPortlet.get().showLoading("Checking parameter evaluation customization..", actualEnvelope.getId());
//		AquaMapsPortlet.localService.getEnvelopeWeights(speciesId, new AsyncCallback<Map<String,Boolean>>(){
//			public void onFailure(Throwable caught) {
//				AquaMapsPortlet.get().hideLoading( actualEnvelope.getId());
//				AquaMapsPortlet.get().showMessage("Unable to retrieve parameters evaluation");
//				Log.error("[getEnvelopeWeightsCallback]", caught);
//			}
//			
//			public void onSuccess(Map<String,Boolean> result) {
//				List<Record> recs= new ArrayList<Record>();
//				if((result==null)||result.size()==0){
//					Log.debug("Weights not customized");
//					customizedSelectedEnvelopeRows=null;					
//					for(EnvelopeFieldsClient id : EnvelopeGrid.defaultSelectedEnvelopeRows)
//						recs.add(actualEnvelope.getStore().getById(id.toString()));					
//				}else{
//					Log.debug("Found weights customization");
//					customizedSelectedEnvelopeRows= new ArrayList<EnvelopeFieldsClient>();
//					for(String k:result.keySet())
//						if(result.get(k)){
//							EnvelopeFieldsClient f =EnvelopeFieldsClient.valueOf(k);
//							customizedSelectedEnvelopeRows.add(f);
//							recs.add(actualEnvelope.getStore().getById(f.toString()));
//						}
//				}
//				actualEnvelope.getSelectionModel().selectRecords(recs.toArray(new Record[recs.size()]));
//			}
//		});
//	}
	
}
