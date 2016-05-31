package org.gcube.application.aquamaps.aquamapsportlet.client.selections;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsportlet.client.AquaMapsPortlet;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.AquaMapsPortletCostants;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.Callbacks;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextArea;
import com.gwtext.client.widgets.layout.FitLayout;

public class SpeciesImportPopup extends Window {

	private Button cancelButton=new Button("Cancel");
	private Button submitButton=new Button("Import");
	TextArea text = new TextArea();	
	
	private SpeciesImportPopup instance=this;
	
	public SpeciesImportPopup() {
		super("Species Import");
		this.setClosable(false);
		this.setLayout(new FitLayout());
		FormPanel panel=new FormPanel();
		panel.setLayout(new FitLayout());
		panel.setFrame(true);
		
		text.setWidth(AquaMapsPortletCostants.FILTER_WIDTH);
		text.setHeight(AquaMapsPortletCostants.FILTERS_HEIGHT);
		panel.add(text);
		cancelButton.addListener(new ButtonListenerAdapter(){
			@Override
			public void onClick(Button button, EventObject e) {
				instance.close();
			}
		});
		submitButton.addListener(new ButtonListenerAdapter(){
			@Override
			public void onClick(Button button, EventObject e) {
				AquaMapsPortlet.get().showLoading("Parsing species list...", instance.getId());
				List<String> toAdd=new ArrayList<String>();
				for(String s:text.getText().split(","))
					toAdd.add(s);
				AquaMapsPortlet.get().showLoading("Importing "+toAdd.size()+" speciesId(s) into basket..", AquaMapsPortlet.get().species.toAddSpecies.getId());
				AquaMapsPortlet.localService.addToSpeciesSelection(toAdd, Callbacks.speciesSelectionChangeCallback);
				instance.close();
			}
		});
		
		
		panel.setButtons(new Button[]{cancelButton,submitButton});
		this.add(panel);
	}
	
}
