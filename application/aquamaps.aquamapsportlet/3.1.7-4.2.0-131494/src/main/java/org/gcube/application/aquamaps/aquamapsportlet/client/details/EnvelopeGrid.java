package org.gcube.application.aquamaps.aquamapsportlet.client.details;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsportlet.client.AquaMapsPortlet;
import org.gcube.application.aquamaps.aquamapsportlet.client.ColumnDefinitions;
import org.gcube.application.aquamaps.aquamapsportlet.client.RecordDefinitions;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.EnvelopeFieldsClient;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientEnvelope;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.Store;
import com.gwtext.client.widgets.grid.CheckboxSelectionModel;
import com.gwtext.client.widgets.grid.EditorGridPanel;
import com.gwtext.client.widgets.grid.GridView;

public class EnvelopeGrid extends EditorGridPanel {

	
	public static List<EnvelopeFieldsClient> defaultSelectedEnvelopeRows= new ArrayList<EnvelopeFieldsClient>();
	public List<EnvelopeFieldsClient> customizedSelectedEnvelopeRows=null;
	static {
		defaultSelectedEnvelopeRows.add(EnvelopeFieldsClient.Depth);
		defaultSelectedEnvelopeRows.add(EnvelopeFieldsClient.Temperature);
		defaultSelectedEnvelopeRows.add(EnvelopeFieldsClient.Salinity);
		defaultSelectedEnvelopeRows.add(EnvelopeFieldsClient.PrimaryProduction);
		defaultSelectedEnvelopeRows.add(EnvelopeFieldsClient.IceConcentration);
	}
	
	private EnvelopeGrid instance=this;
	
	public static NumberFormat formatter=NumberFormat.getFormat("#####.##");
	
	public EnvelopeGrid get(){return instance;}
	CheckboxSelectionModel cbm= new CheckboxSelectionModel();

	private String currentSpeciesId="";
	private String currentScientificName="";
	private String titleTemplate="";
	
	
	public EnvelopeGrid(String title,boolean showToolbar,boolean readOnly) {
		this.setTitle(title);
		Store actualStore=new Store(RecordDefinitions.envRecordDef);
		this.setStore(actualStore);		
		this.setColumnModel(ColumnDefinitions.envColumnModel(!readOnly));
		this.setAutoScroll(true);
		this.setStripeRows(true);
		GridView gridView = new GridView();		
		gridView.setAutoFill(true);
		gridView.setForceFit(true);
		this.setView(gridView);	
		this.setFrame(true);		
		if(readOnly) this.stopEditing();
		cbm.lock();
		titleTemplate=title;
	}
	
//	public void envelopeRowSelection(){
//		AquaMapsPortlet.get().showLoading("Checking parameter evaluation customization..", instance.getId());
//		Log.debug("Requesting weights for "+currentSpeciesId);
//		
//		AquaMapsPortlet.localService.getEnvelopeWeights(currentSpeciesId, new AsyncCallback<Map<String, Boolean>>(){
//			public void onFailure(Throwable caught) {
//				AquaMapsPortlet.get().hideLoading( instance.getId());
//				AquaMapsPortlet.get().showMessage("Unable to retrieve parameters evaluation");
//				Log.error("[getEnvelopeWeightsCallback]", caught);
//			}
//			
//			public void onSuccess(Map<String, Boolean> result) {
//				cbm.unlock();
//				List<Record> recs= new ArrayList<Record>();
//				if((result==null)||result.size()==0){
//					customizedSelectedEnvelopeRows=null;					
//					for(EnvelopeFieldsClient id : defaultSelectedEnvelopeRows)
//						recs.add(instance.getStore().getById(id.toString()));					
//				}else{
//					customizedSelectedEnvelopeRows= new ArrayList<EnvelopeFieldsClient>();
//					for(Entry<String, Boolean> entry: result.entrySet())
//						if(entry.getValue()){
//							EnvelopeFieldsClient f =EnvelopeFieldsClient.valueOf(entry.getKey());
//							customizedSelectedEnvelopeRows.add(f);
//							recs.add(instance.getStore().getById(f.toString()));
//						}
//				}				
//				instance.getSelectionModel().selectRecords(recs.toArray(new Record[recs.size()]));
//				cbm.lock();
//			}
//		});
//	}
	
	
	public AsyncCallback<ClientEnvelope> envelopeRetrievalCallback=new AsyncCallback<ClientEnvelope>(){

		public void onFailure(Throwable caught) {
			AquaMapsPortlet.get().hideLoading( instance.getId());
			AquaMapsPortlet.get().showMessage("Unable to retrieve species envelope, "+caught.getMessage());
			Log.error("[getEnvelopeCallback]", caught);
		}
		public void onSuccess(ClientEnvelope result) {
			Store store=instance.getStore();
			store.removeAll();
			Log.debug("SpeciesID returned is "+result.getSpeciesId());
			Log.debug("Stored speciesId is "+currentSpeciesId);
			for(EnvelopeFieldsClient param:envelopeFieldsOrder){							
					Record record=RecordDefinitions.envRecordDef.createRecord(param.toString(), new Object[]{
							getParameterLabel(param),
							((param.equals(EnvelopeFieldsClient.IceConcentration))&&(result.getMinValue(param)<0)?0:formatter.format(result.getMinValue(param))), //Depth min =>0
							//result.getMinValue(param),
							formatter.format(result.getMaxValue(param)),
							formatter.format(result.getPrefMinValue(param)),
							formatter.format(result.getPrefMaxValue(param)),
					});
					store.add(record);
			}
//			envelopeRowSelection();
			instance.setTitle(titleTemplate+" ("+currentScientificName+")");
			AquaMapsPortlet.get().hideLoading( instance.getId());						
		}
	};
	public static String getParameterLabel(EnvelopeFieldsClient parameter){
		switch(parameter){
		case Depth : return "Depth (m)";
		case IceConcentration : return "Ice Concentration (% cover)";
		case LandDistance: return "Distance to Land (km)";
		case PrimaryProduction: return "Primary Production";
		case Salinity : return "Salinity (psu)";
		case Temperature: return "Temperature ("+'\u00B0'+"C)";
		default : return "";
		}
	}
	
	public static EnvelopeFieldsClient labelToParameter(String label){
		if(label.equals("Depth (m)")) return EnvelopeFieldsClient.Depth;
		if(label.equals("Ice Concentration (% cover)")) return EnvelopeFieldsClient.IceConcentration;
		if(label.equals("Distance to Land (km)")) return EnvelopeFieldsClient.LandDistance;
		if(label.equals("Primary Production")) return EnvelopeFieldsClient.PrimaryProduction;
		if(label.equals("Salinity (psu)")) return EnvelopeFieldsClient.Salinity;
		if(label.equals("Temperature ("+'\u00B0'+"C)")) return EnvelopeFieldsClient.Temperature;
		return null;		
	}
	public static EnvelopeFieldsClient[] envelopeFieldsOrder = new EnvelopeFieldsClient[]{
		EnvelopeFieldsClient.Depth,
		EnvelopeFieldsClient.Temperature,
		EnvelopeFieldsClient.Salinity,
		EnvelopeFieldsClient.PrimaryProduction,
		EnvelopeFieldsClient.LandDistance,
		EnvelopeFieldsClient.IceConcentration,
	};

	public String getCurrentSpeciesId() {
		return currentSpeciesId;
	}

	public void setCurrentSpecies(String currentSpeciesId, String scientificName,boolean loadCustomizations) {
		AquaMapsPortlet.get().showLoading("Loading Envelope..", instance.getId());
		this.currentSpeciesId = currentSpeciesId;
		this.currentScientificName=scientificName;
		AquaMapsPortlet.remoteService.getEnvelope(this.currentSpeciesId, loadCustomizations,instance.envelopeRetrievalCallback);
	}
}
