package org.gcube.application.aquamaps.aquamapsportlet.client.perturbation;

import org.gcube.application.aquamaps.aquamapsportlet.client.AquaMapsPortlet;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SpeciesFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientEnvelope;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.Label;
import com.gwtext.client.widgets.form.TextField;

public class EnvelopeCalulationForm extends FormPanel {

	private final String[] surfaceBottomStrings= new String[]{"surface values","bottom values"};
	private final String[] areaRestrictionStrings=new String[]{"Fao Areas","Bounding Box","Both (intersection)"};
	
	
	TextField faoAreas = new TextField("FAO Areas");
	CustomComboBox pelagic = new CustomComboBox("Pelagic",new String[]{"true","false"});
	CustomComboBox useMean = new CustomComboBox("Use Mean Depth",new String[]{"true","false"});
	CustomComboBox surfaceBottomSelector = new CustomComboBox("For Sea temp. and Salinity use",surfaceBottomStrings);
	TextField boundingBox=new TextField("Bounding Box (N,S,W,E)");
	Button calculate = new Button("Re-calculate Envelope and Good Cells");
	CustomComboBox restriction=new CustomComboBox("Use area restriction",areaRestrictionStrings);
	
	
	public EnvelopeCalulationForm() {
		this.add(new Label("Area Restrictions"));
		this.setFrame(true);
		this.add(faoAreas);
		this.add(boundingBox);
		this.add(pelagic);
		this.add(useMean);
		this.add(surfaceBottomSelector);
		this.add(restriction);
		this.setButtons(new Button[]{calculate});
		calculate.addListener(new ButtonListenerAdapter(){			
			public void onClick(Button button, EventObject e) {
				
				String speciesId=AquaMapsPortlet.get().envelopeCustomization.grid.getSelectionModel().getSelected().getAsString(SpeciesFields.speciesid+"");
				AquaMapsPortlet.get().showLoading("Re - calculating goodCells and envelope...", AquaMapsPortlet.get().envelopeCustomization.actualEnvelope.getId());
				AquaMapsPortlet.remoteService.reCalculateGoodCells(
						boundingBox.getValueAsString(),
						faoAreas.getValueAsString(),
						speciesId,
						surfaceBottomSelector.getValueAsString().equals(surfaceBottomStrings[1]),
						restriction.getValueAsString().equals(areaRestrictionStrings[2])||restriction.getValueAsString().equals(areaRestrictionStrings[1]),
						restriction.getValueAsString().equals(areaRestrictionStrings[2])||restriction.getValueAsString().equals(areaRestrictionStrings[0]),
						AquaMapsPortlet.get().envelopeCustomization.envelopeRecalculationCallback);
			}
		});
	}
	
	public void loadEnvelope(ClientEnvelope toLoad){
		AquaMapsPortlet.get().showLoading("Loading envelope", this.getId());
		faoAreas.setValue(toLoad.getFaoAreas());
		pelagic.setValue(String.valueOf(toLoad.isPelagic()));
		useMean.setValue(String.valueOf(toLoad.isUseMeanDepth()));
		boundingBox.setValue(toLoad.getBoundingBox().toString());
		if(toLoad.isUseBottomSeaTempAndSalinity()) surfaceBottomSelector.setValue("bottom values");
		else surfaceBottomSelector.setValue("surface values");
		String areaRestrictionValue=null;
		if((toLoad.isUseBoundingBox())&&(toLoad.isUseFaoAreas()))areaRestrictionValue= areaRestrictionStrings[2];
			else if(toLoad.isUseBoundingBox()) areaRestrictionValue=areaRestrictionStrings[1];
			else areaRestrictionValue=areaRestrictionStrings[0];
		restriction.setValue(areaRestrictionValue);
		AquaMapsPortlet.get().hideLoading(this.getId());
	}
}
