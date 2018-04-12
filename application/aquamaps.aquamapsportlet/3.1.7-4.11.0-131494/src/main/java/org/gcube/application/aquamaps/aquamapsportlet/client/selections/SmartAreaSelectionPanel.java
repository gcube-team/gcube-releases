package org.gcube.application.aquamaps.aquamapsportlet.client.selections;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsportlet.client.AquaMapsPortlet;
import org.gcube.application.aquamaps.aquamapsportlet.client.ColumnDefinitions;
import org.gcube.application.aquamaps.aquamapsportlet.client.Stores;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.AquaMapsPortletCostants;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.AreaFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.AreaType;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.Callbacks;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientArea;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.Msg;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.GroupingStore;
import com.gwtext.client.data.Record;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.layout.RowLayout;

public class SmartAreaSelectionPanel extends Panel {

	
	ToolbarButton enableFAO=new ToolbarButton("Hide Fao Areas");
	ToolbarButton enableEEZ=new ToolbarButton("Show EEZ Areas");
	ToolbarButton enableLME=new ToolbarButton("Show LME Areas");
	
	//ExtendedLiveGrid grid;
	
	public ExtendedLiveGrid toAddAreasPanel;
	
	GroupingStore store;
	
	
	public ExtendedLiveGrid 	selectedAreas=new ExtendedLiveGrid("Selected Areas",Stores.selectedAreasStore(),ColumnDefinitions.areasColumnModel(),false);
	
	public SmartAreaSelectionPanel() {
		this.setTitle("Area Selection");
		this.setFrame(true);
		//this.setLayout(new AnchorLayout());
		this.setLayout(new RowLayout());
		this.setHeight(AquaMapsPortletCostants.SELECTION_HEIGHT);
		
		this.setTopToolbar(new Button[]{enableFAO,enableEEZ,enableLME});
		
		selectedAreas.addTool(AquaMapsPortlet.get().getHelpTool("https://gcube.wiki.gcube-system.org/gcube/index.php/AquaMaps#Area_Selection"));
		toAddAreasPanel.addTool(AquaMapsPortlet.get().getHelpTool("https://gcube.wiki.gcube-system.org/gcube/index.php/AquaMaps#Area_Selection"));
	
		
		toAddAreasPanel=new ExtendedLiveGrid("Area List",Stores.smartAvailableAreasStore(),ColumnDefinitions.areasColumnModel(),false);
		
		toAddAreasPanel.setAdder(new ButtonListenerAdapter(){
			
			public void onClick(Button button, EventObject e) {
				List<ClientArea> areas=new ArrayList<ClientArea>();
				if(!toAddAreasPanel.useAllButton.isPressed())
					for(Record r:toAddAreasPanel.getSelectionModel().getSelections())
						 areas.add(new ClientArea(
								 AreaType.valueOf(r.getAsString(AreaFields.type+"")),
								 r.getAsString(AreaFields.code+""),
								 r.getAsString(AreaFields.name+"")));
				
				AquaMapsPortlet.get().showLoading("Adding areas to selection", toAddAreasPanel.getId());
				AquaMapsPortlet.localService.addToAreaSelection(areas, Callbacks.areaSelectionChangeCallback);
			}
		});
		toAddAreasPanel.setCollapsible(false);			
		//toAddAreasPanel.setHeight(this.getHeight()-20);
		toAddAreasPanel.setHeight("");
		selectedAreas.setHeight("");
		
		ButtonListenerAdapter toggleListener= new ButtonListenerAdapter(){
			public void onToggle(Button button, boolean pressed) {
				if(pressed)
					button.setText(button.getText().replaceFirst("Show", "Hide"));
				else 
					button.setText(button.getText().replaceFirst("Hide", "Show"));
				
				
				AquaMapsPortlet.get().showLoading("Changing preferences..", toAddAreasPanel.getId());
				AquaMapsPortlet.localService.filterAreas(enableFAO.isPressed(), enableLME.isPressed(), enableEEZ.isPressed(), new AsyncCallback<Msg>(){

					public void onFailure(Throwable caught) {
						AquaMapsPortlet.get().hideLoading(AquaMapsPortlet.get().area.toAddAreasPanel.getId());
						AquaMapsPortlet.get().showMessage("Unable change preferences");
						Log.error("[areaFilterChangeCallback]", caught);
					}

					public void onSuccess(Msg result) {
						toAddAreasPanel.getStore().reload();
						AquaMapsPortlet.get().hideLoading(AquaMapsPortlet.get().area.toAddAreasPanel.getId());
						Log.debug("[areaFilterChangeCallback] - "+result.getMsg());		
					}
					
				});
			}
		};
		
		
		
		enableFAO.setEnableToggle(true);
		enableFAO.setPressed(true);
		enableFAO.addListener(toggleListener);
		enableLME.setEnableToggle(true);
		enableLME.setPressed(false);
		enableLME.addListener(toggleListener);
		enableEEZ.setEnableToggle(true);
		enableEEZ.setPressed(false);
		enableEEZ.addListener(toggleListener);
		//this.add(toAddAreasPanel,new AnchorLayoutData("100% 50%"));
		this.add(toAddAreasPanel);
		this.addListener(new PanelListenerAdapter(){
			@Override
			public void onActivate(Panel panel) {
				toAddAreasPanel.getStore().reload();
				selectedAreas.getStore().reload();
			}
		});
		selectedAreas.setRemover(new ButtonListenerAdapter(){			
			
			public void onClick(Button button, EventObject e) {
				List<ClientArea> areas=new ArrayList<ClientArea>();
				if(!selectedAreas.useAllButton.isPressed()){
					for(Record r:selectedAreas.getSelectionModel().getSelections())
						 areas.add(new ClientArea(
								 AreaType.valueOf(r.getAsString(AreaFields.type+"")),
								 r.getAsString(AreaFields.code+""),
								 r.getAsString(AreaFields.name+"")));
				}
				AquaMapsPortlet.get().showLoading("Removing selected areas", toAddAreasPanel.getId());
				AquaMapsPortlet.localService.removeFromAreaSelection(areas, Callbacks.areaSelectionChangeCallback);
			}
		
	});
	this.add(selectedAreas);
	selectedAreas.setCollapsible(true);
	
	this.addListener(new PanelListenerAdapter(){
		@Override
		public void onActivate(Panel panel) {
			Log.debug("On Activate - Area Panel");
			toAddAreasPanel.getStore().reload();
			selectedAreas.getStore().reload();
		}
	});
	
	
	}
	
	
	
	
	
	
	
}
