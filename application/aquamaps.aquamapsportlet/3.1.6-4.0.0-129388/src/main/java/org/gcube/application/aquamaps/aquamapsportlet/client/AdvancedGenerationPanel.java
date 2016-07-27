package org.gcube.application.aquamaps.aquamapsportlet.client;

import java.util.ArrayList;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.AquaMapsPortletCostants;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SpeciesFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientObjectType;
import org.gcube.application.aquamaps.aquamapsportlet.client.details.EnvelopeGrid;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.Msg;
import org.gcube.application.aquamaps.aquamapsportlet.client.selections.ExtendedLiveGrid;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.Record;
import com.gwtext.client.widgets.BoxComponent;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.grid.event.RowSelectionListenerAdapter;
import com.gwtext.client.widgets.layout.AnchorLayout;
import com.gwtext.client.widgets.layout.AnchorLayoutData;
import com.gwtext.client.widgets.layout.ColumnLayout;
import com.gwtext.client.widgets.layout.ColumnLayoutData;
import com.gwtext.client.widgets.menu.BaseItem;
import com.gwtext.client.widgets.menu.Item;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.menu.event.BaseItemListenerAdapter;

public class AdvancedGenerationPanel extends Panel {


	ExtendedLiveGrid selectedSpecies=new ExtendedLiveGrid("Selected Species",Stores.selectedSpeciesStore(),ColumnDefinitions.selectedSpeciesColumnModel(),false);
	EnvelopeGrid envGrid=new EnvelopeGrid("Species environmental tolerance",false,true);
	Panel upperSection=new Panel();

	private AdvancedGenerationPanel instance=this;

	public AquaMapsObjectsSettingsPanel objectPanel=new AquaMapsObjectsSettingsPanel();

	ToolbarButton createMenuButton=new ToolbarButton("Create");
	ToolbarButton addToBioButton=new ToolbarButton("Add to current Biodiversity");


	public AdvancedGenerationPanel() {
		this.setFrame(true);
		this.setTitle("Maps Generation");
		this.setLayout(new AnchorLayout());
		this.setHeight(AquaMapsPortletCostants.SELECTION_HEIGHT);


		selectedSpecies.addTool(AquaMapsPortlet.get().getHelpTool("https://gcube.wiki.gcube-system.org/gcube/index.php/AquaMaps#AquaMaps_Generation"));
		envGrid.addTool(AquaMapsPortlet.get().getHelpTool("https://gcube.wiki.gcube-system.org/gcube/index.php/AquaMaps#AquaMaps_Generation"));
		objectPanel.addTool(AquaMapsPortlet.get().getHelpTool("https://gcube.wiki.gcube-system.org/gcube/index.php/AquaMaps#AquaMaps_Generation"));
		
		Menu createMenu=new Menu();		
		Item createDistrItem=new Item("Species Distribution");		
		createDistrItem.addListener(new BaseItemListenerAdapter(){
			@Override
			public void onClick(BaseItem item, EventObject e) {
				AquaMapsPortlet.get().showLoading("Updating settings..", instance.getId());
				if(selectedSpecies.useAllButton.isPressed()){
					AquaMapsPortlet.localService.createObjectsBySelection(ClientObjectType.SpeciesDistribution, null, "", objUpdateCallback);
				}else{
					final ArrayList<String> ids=new ArrayList<String>();
					for(Record rec: selectedSpecies.getSelectionModel().getSelections()){
						ids.add(rec.getAsString(SpeciesFields.speciesid+""));					
					}
					AquaMapsPortlet.localService.createObjectsBySelection(ClientObjectType.SpeciesDistribution, ids, "", objUpdateCallback);
				}
			}
		});
		Item createBiodItem=new Item("Biodiversity");
		createBiodItem.addListener(new BaseItemListenerAdapter(){
			@Override
			public void onClick(BaseItem item, EventObject e) {
				MessageBox.prompt("Name", "Please enter new AquaMaps object name:",  
						new MessageBox.PromptCallback() {  
					public void execute(String btnID, String text) { 
						if(btnID.equalsIgnoreCase("OK")){							
							AquaMapsPortlet.get().showLoading("Updating settings..", instance.getId());
							if(selectedSpecies.useAllButton.isPressed()){
								AquaMapsPortlet.localService.createObjectsBySelection(ClientObjectType.Biodiversity, null, text, objUpdateCallback);
							}
							else{
								final ArrayList<String> ids=new ArrayList<String>();
								for(Record rec: selectedSpecies.getSelectionModel().getSelections()){
									ids.add(rec.getAsString(SpeciesFields.speciesid+""));					
								}
								AquaMapsPortlet.localService.createObjectsBySelection(ClientObjectType.Biodiversity, ids, text, objUpdateCallback);
							}
						}
					}
				});
			}
		});
		createMenu.addItem(createBiodItem);
		createMenu.addItem(createDistrItem);

		createMenuButton.setMenu(createMenu);




		//		ToolTip tp=new ToolTip("Adds selected species to current selected Biodiversity AquaMaps object to create"); 
		//		tp.applyTo(addToBioButton.getElement());
		addToBioButton.addListener(new ButtonListenerAdapter(){
			@Override
			public void onClick(Button button, EventObject e) {
				if(objectPanel.biodivGrid.getSelectionModel().hasSelection()){	
					Record rec = objectPanel.biodivGrid.getSelectionModel().getSelected();
					String title=rec.getAsString("title");
					ArrayList<String> ids=null;
					if(selectedSpecies.useAllButton.isPressed()){

					}else{
						AquaMapsPortlet.get().showLoading("Adding to "+title, instance.getId());
						ids=new ArrayList<String>();
						for(Record r:selectedSpecies.getSelectionModel().getSelections()){
							ids.add(r.getAsString(SpeciesFields.speciesid+""));
						}
					}
					AquaMapsPortlet.localService.addToObjectBasket(ids, title, objUpdateCallback);
				}else AquaMapsPortlet.get().showMessage("Please, select a Biodiversity AquaMaps object to add selected species to");
			}
		});
		createMenuButton.disable();
		addToBioButton.disable();		
		selectedSpecies.getBottomToolbar().addButton(createMenuButton);
		selectedSpecies.getBottomToolbar().addButton(addToBioButton);		
		selectedSpecies.setWidth("");
		//		selectedSpecies.setCollapsible(true);


		upperSection.setLayout(new ColumnLayout());
		upperSection.setFrame(true);
		upperSection.add(selectedSpecies,new ColumnLayoutData(.5));
		upperSection.add(envGrid,new ColumnLayoutData(.5));
		upperSection.addListener(new PanelListenerAdapter(){
			@Override
			public void onResize(BoxComponent component, int adjWidth,
					int adjHeight, int rawWidth, int rawHeight) {			
				super.onResize(component, adjWidth, adjHeight, rawWidth, rawHeight);
				Log.debug("Resizing AdvGenPan- Upper..");
				selectedSpecies.setHeight(adjHeight-10);
				envGrid.setHeight(adjHeight-10);

			}
		});

		selectedSpecies.getSelectionModel().addListener(new RowSelectionListenerAdapter(){
			@Override
			public void onSelectionChange(RowSelectionModel sm) {
				if((sm.getCount()>0)||(selectedSpecies.useAllButton.isPressed())){					
					createMenuButton.enable();
					addToBioButton.enable();
				}else{
					createMenuButton.disable();
					addToBioButton.disable();
				}
			}
			@Override
			public void onRowSelect(RowSelectionModel sm, int rowIndex,
					Record record) {								
				super.onRowSelect(sm, rowIndex, record);				
				String speciesId=sm.getSelected().getAsString(SpeciesFields.speciesid+"");
				String scientificName=sm.getSelected().getAsString(SpeciesFields.scientific_name+"");
				Log.debug("Requesting "+speciesId);
				envGrid.setCurrentSpecies(speciesId,scientificName,true);				
			}
		});

		this.add(upperSection, new AnchorLayoutData("100% 50%"));


		this.add(objectPanel, new AnchorLayoutData("100% 50%"));	
		this.addListener(new PanelListenerAdapter(){
			@Override
			public void onActivate(Panel panel) {
				objectPanel.reload();
				selectedSpecies.getStore().reload();
			}
		});
	}

	AsyncCallback<Msg> objUpdateCallback=new AsyncCallback<Msg>() {

		public void onSuccess(Msg result) {
			AquaMapsPortlet.get().hideLoading(instance.getId());
			objectPanel.reload();
		}

		public void onFailure(Throwable caught) {			
			AquaMapsPortlet.get().hideLoading(instance.getId());
			AquaMapsPortlet.get().showMessage("Sorry, an error occurred. Please retry");
			Log.error("[objUpdateCallback]",caught);
			objectPanel.reload();
		}
	};

}
