package org.gcube.application.aquamaps.aquamapsportlet.client.selections;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsportlet.client.AquaMapsPortlet;
import org.gcube.application.aquamaps.aquamapsportlet.client.ColumnDefinitions;
import org.gcube.application.aquamaps.aquamapsportlet.client.Stores;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.CellFields;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.Record;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.WindowListenerAdapter;
import com.gwtext.client.widgets.layout.FitLayout;

public class OccurrenceCellsPopup extends Window {

	ToolbarButton reCalculate=new ToolbarButton("Re-Calculate Envelope");
	ExtendedLiveGrid grid =null;

	String showingSpeciesId="";

	public OccurrenceCellsPopup(String speciesId) {
		//		String url=AquaMapsPortletCostants.servletUrl.get("occurrenceCells")+"?"+Species.Tags.ID+"="+speciesId;
		//		grid=new ExtendedLiveGrid("Occurrence Cells",Stores.initStore(url, RecordDefinitions.cellRecordDef),ColumnDefinitions.goodCellsColumnModel(),false);
		grid=new ExtendedLiveGrid("Occurrence Cells",Stores.occurrenceCellsStore(speciesId),ColumnDefinitions.goodCellsColumnModel(),false);
		this.setTitle("Occurrence Cells for selected speciesID "+speciesId);
		this.setFrame(true);
		this.setLayout(new FitLayout());
		this.add(grid);
		this.setWidth(950);
		this.addListener(new WindowListenerAdapter(){			
			public void onShow(Component component) {
				super.onShow(component);
				grid.getStore().reload();
			}
		});
		grid.getBottomToolbar().addButton(reCalculate);	
		showingSpeciesId=speciesId;
		reCalculate.addListener(new ButtonListenerAdapter(){			
			public void onClick(Button button, EventObject e) {
				Record[] selection=grid.getSelectionModel().getSelections();
				List<String> cellIds=new ArrayList<String>();
				if(grid.useAllButton.isPressed()){
					AquaMapsPortlet.get().showLoading("Re-calculating envelope",AquaMapsPortlet.get().envelopeCustomization.actualEnvelope.getId());
					for(int i=0;i<grid.getStore().getTotalCount();i++)
						cellIds.add(grid.getStore().getAt(i).getAsString(CellFields.csquarecode+""));										
					AquaMapsPortlet.remoteService.reCalculateEnvelopeFromCellIds(cellIds,showingSpeciesId, AquaMapsPortlet.get().envelopeCustomization.envelopeRecalculationCallback);
				}else
					if(selection.length<10) AquaMapsPortlet.get().showMessage("You must select at least 10 cells");
					else{
						//boolean useBottom =AquaMapsPortlet.get().envelopeCustomization.form.getEnvelope().isUseBottomSeaTempAndSalinity();
						for(int i=0;i<selection.length;i++)
							cellIds.add(selection[i].getAsString(CellFields.csquarecode+""));
						AquaMapsPortlet.get().showLoading("Re-calculating envelope",AquaMapsPortlet.get().envelopeCustomization.actualEnvelope.getId());					
						AquaMapsPortlet.remoteService.reCalculateEnvelopeFromCellIds(cellIds,showingSpeciesId, AquaMapsPortlet.get().envelopeCustomization.envelopeRecalculationCallback);
					}
			}
		});
	}

	



}
