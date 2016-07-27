package org.gcube.application.aquamaps.aquamapsportlet.client.selections;



import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsportlet.client.AquaMapsPortlet;
import org.gcube.application.aquamaps.aquamapsportlet.client.ColumnDefinitions;
import org.gcube.application.aquamaps.aquamapsportlet.client.Stores;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.AquaMapsPortletCostants;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SpeciesFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.filters.SpeciesFilter;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.Callbacks;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.Msg;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.SettingsDescriptor;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.event.StoreListenerAdapter;
import com.gwtext.client.util.Format;
import com.gwtext.client.widgets.BoxComponent;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.event.GridRowListenerAdapter;
import com.gwtext.client.widgets.layout.HorizontalLayout;
import com.gwtext.client.widgets.layout.RowLayout;
import com.gwtext.client.widgets.layout.RowLayoutData;

public class SpeciesSelectionPanel extends Panel {

	public ExtendedLiveGrid 	selectedSpecies=new ExtendedLiveGrid("User-selected Species",Stores.selectedSpeciesStore(),ColumnDefinitions.selectedSpeciesColumnModel(),false);

	public ExtendedLiveGrid		toAddSpecies=new ExtendedLiveGrid("Search Result",Stores.availableSpeciesStore(),ColumnDefinitions.availableSpeciesColumnModel(),false);

	public SpeciesFilter filter=new SpeciesFilter();




	public SpeciesSelectionPanel(){
		this.setLayout(new HorizontalLayout(2));
		this.setTitle("Species Selection");
		this.setHeight(AquaMapsPortletCostants.SELECTION_HEIGHT);


		this.setFrame(true);

		filter.setWidth(AquaMapsPortletCostants.FILTERS_SECTION_WIDTH);
		filter.setHeight(AquaMapsPortletCostants.SELECTION_HEIGHT);
		filter.setFrame(true);


		this.add(filter);

		selectedSpecies.addTool(AquaMapsPortlet.get().getHelpTool("https://gcube.wiki.gcube-system.org/gcube/index.php/AquaMaps#Species_Selection")); 
		toAddSpecies.addTool(AquaMapsPortlet.get().getHelpTool("https://gcube.wiki.gcube-system.org/gcube/index.php/AquaMaps#Species_Selection"));
		filter.addTool(AquaMapsPortlet.get().getHelpTool("https://gcube.wiki.gcube-system.org/gcube/index.php/AquaMaps#Species_Selection"));


		//******************* To add species grid--- region center

		final Panel center=new Panel();
		center.setLayout(new RowLayout());
		center.setHeight(AquaMapsPortletCostants.SELECTION_HEIGHT);
		center.setWidth(AquaMapsPortletCostants.WIDTH-AquaMapsPortletCostants.FILTERS_SECTION_WIDTH);
		toAddSpecies.getView().setEmptyText("No species matched the search criteria");

		toAddSpecies.setAdder(new ButtonListenerAdapter(){			
			public void onClick(Button button, EventObject e) {
				if(toAddSpecies.useAllButton.isPressed()){
					Log.debug("Starting loading..");				
					final int totalCount=toAddSpecies.getStore().getTotalCount();
					String sortDir=toAddSpecies.getStore().getSortState().getDirection().getDirection();
					String sortColumn=toAddSpecies.getStore().getSortState().getField();

					//					AquaMapsPortlet.get().showLoading("Importing all filtered species, process could take a while..", toAddSpecies.getId());

					AquaMapsPortlet.localService.addAllFilteredSpecies(totalCount, sortColumn, sortDir, new AsyncCallback<Msg>() {

						@Override
						public void onSuccess(Msg result) {
							MessageBox.show(new MessageBoxConfig() {  
								{  
									setTitle("Please wait...");  
									setMsg("Importing...");  
									setWidth(240);  
									setProgress(true);  
									setClosable(false);  
									setCallback(new MessageBox.PromptCallback() {  
										public void execute(String btnID, String text) {  
											System.out.println("Button Click : " +  
													Format.format("You clicked the {0} button and " +  
															"entered the text {1}", btnID, text));  
										}  
									});  
									//		                        setAnimEl(button.getId());  
								}  
							});  

							Timer timer=new Timer(){

								boolean completedImport=false;

								@Override
								public void run() {
									if(!completedImport)
										AquaMapsPortlet.localService.getImportProgress(new AsyncCallback<Integer>() {

											public void onSuccess(Integer result) {
												if(result>=totalCount) {
													selectedSpecies.getStore().reload();
													completedImport=true;
													MessageBox.hide();
												}
												else{
													Double percent=((new Double(result)/new Double(totalCount))*100d);

													MessageBox.updateProgress(percent.intValue(), "Importing species"  
															+ result + " of "+totalCount+"... ");
												}
											}

											public void onFailure(Throwable caught) {										
												AquaMapsPortlet.get().showMessage("Sorry, an error occurred while trying to load session details. Please retry");
												Log.debug("Submission Form reload : "+caught.getMessage(),	 caught);
											}
										});
								}

							};
							timer.scheduleRepeating(5000);
						}
						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub

						}
					});


				}else{
					Log.debug("Size selected : "+toAddSpecies.getSelectionModel().getSelections().length);				
					AquaMapsPortlet.get().showLoading("Adding species to selection", toAddSpecies.getId());

					List<String> ids=new ArrayList<String>();
					for(Record r: toAddSpecies.getSelectionModel().getSelections())
						ids.add(r.getAsString(SpeciesFields.speciesid+""));

					AquaMapsPortlet.localService.addToSpeciesSelection(ids, Callbacks.speciesSelectionChangeCallback);
				}
			}
		});
		toAddSpecies.setHeight(AquaMapsPortletCostants.SELECTION_HEIGHT-AquaMapsPortletCostants.SELECTED_HEIGHT);		
		toAddSpecies.addGridRowListener(new GridRowListenerAdapter(){			
			public void onRowDblClick(GridPanel grid, int rowIndex,
					EventObject e) {
				//				final Record record=grid.getStore().getAt(rowIndex);
				////				Window.open("http://fishbase.sinica.edu.tw/summary/SpeciesSummary.php?genusname="+record.getAsString(SpeciesFields.genus+"")
				////						+"&speciesname="+record.getAsString(SpeciesFields.species+""), record.getAsString(SpeciesFields.scientific_name+""), "");
				////				
				//				AquaMapsPortlet.get().showLoading("Checking source..", toAddSpecies.getId());
				//				RequestBuilder builder=new RequestBuilder(RequestBuilder.GET, "http://fishbase.sinica.edu.tw/summary/SpeciesSummary.php?genusname="+record.getAsString(SpeciesFields.genus+"")
				//						+"&speciesname="+record.getAsString(SpeciesFields.species+""));
				//				builder.setTimeoutMillis(0);
				//				try {
				//					builder.sendRequest(null, new RequestCallback() {
				//						
				//						@Override
				//						public void onResponseReceived(Request request, Response response) {
				//							Log.debug("Success : "+response.getStatusCode());
				//							AquaMapsPortlet.get().hideLoading(toAddSpecies.getId());
				//							if(response.getStatusCode()==200){
				//								Window.open("http://fishbase.sinica.edu.tw/summary/SpeciesSummary.php?genusname="+record.getAsString(SpeciesFields.genus+"")
				//										+"&speciesname="+record.getAsString(SpeciesFields.species+""), record.getAsString(SpeciesFields.scientific_name+""), "");
				//							}else{
				//								AquaMapsPortlet.get().showMessage("Unable to open external source, FishBase source returned HTTP code "+response.getStatusCode());
				//							}
				//						}
				//						
				//						@Override
				//						public void onError(Request request, Throwable exception) {
				//							Log.debug("Error: "+exception.getMessage());
				//							AquaMapsPortlet.get().showMessage("Unable to open external source, message was :"+exception.getMessage());
				//							AquaMapsPortlet.get().hideLoading(toAddSpecies.getId());
				//						}
				//					});
				//				} catch (RequestException e1) {
				//					Log.error("Unable to send request", e1);
				//					AquaMapsPortlet.get().showMessage("Unable to send request, message was :"+e1.getMessage());
				//					AquaMapsPortlet.get().hideLoading(toAddSpecies.getId());
				//				}

				Record record=grid.getStore().getAt(rowIndex);
				Window.open("http://fishbase.sinica.edu.tw/summary/SpeciesSummary.php?genusname="+record.getAsString(SpeciesFields.genus+"")
						+"&speciesname="+record.getAsString(SpeciesFields.species+""), record.getAsString(SpeciesFields.scientific_name+""), "");
			}
		});

		toAddSpecies.setWidth(AquaMapsPortletCostants.WIDTH-AquaMapsPortletCostants.FILTER_WIDTH);
		center.add(toAddSpecies,new RowLayoutData("70%"));

		//		ToolbarButton importListPopup=new ToolbarButton("Import User List");
		//		importListPopup.addListener(new ButtonListenerAdapter(){
		//			@Override
		//			public void onClick(Button button, EventObject e) {
		//				SpeciesImportPopup popup= new SpeciesImportPopup();
		//				popup.show(button.getButtonElement());
		//			}
		//		});
		//		toAddSpecies.getBottomToolbar().addButton(importListPopup);

		//****************************** Selected Species

		selectedSpecies.setWidth(AquaMapsPortletCostants.WIDTH-AquaMapsPortletCostants.FILTER_WIDTH-5);
		selectedSpecies.setHeight(AquaMapsPortletCostants.SELECTED_HEIGHT);
		selectedSpecies.setRemover(new ButtonListenerAdapter(){

			public void onClick(Button button, EventObject e) {
				if(selectedSpecies.useAllButton.isPressed()){
					AquaMapsPortlet.get().showLoading("Clearing basket, process could take a while", toAddSpecies.getId());
					AquaMapsPortlet.localService.removeSelectionFromBasket(null,null,Callbacks.speciesSelectionChangeCallback);
				}else{				
					AquaMapsPortlet.get().showLoading("Removing selected species", toAddSpecies.getId());

					List<String> ids=new ArrayList<String>();
					for(Record r: selectedSpecies.getSelectionModel().getSelections())
						ids.add(r.getAsString(SpeciesFields.speciesid+""));

					AquaMapsPortlet.localService.removeSelectionFromBasket(null,ids, Callbacks.speciesSelectionChangeCallback);
				}
			}
		});



		final ToolbarButton customizeEnvelope=new ToolbarButton("Customize species envelope");
		customizeEnvelope.disable();
		customizeEnvelope.setEnableToggle(true);
		customizeEnvelope.addListener(new ButtonListenerAdapter(){			
			public void onToggle(Button button, boolean pressed) {
				String id=AquaMapsPortlet.get().envelopeCustomization.getId();
				if(pressed){
					Log.debug("Unhide Tab Panel envelope customization");
					AquaMapsPortlet.get().mainPanel.unhideTabStripItem(id);
					AquaMapsPortlet.get().mainPanel.activate(id);
					Log.debug("Reload envelope customization");
					AquaMapsPortlet.get().envelopeCustomization.grid.getStore().reload();
				}else AquaMapsPortlet.get().mainPanel.hideTabStripItem(id);
			}			
		});
		selectedSpecies.getStore().addStoreListener(new StoreListenerAdapter(){			

			public void onDataChanged(Store store) {
				if(store.getCount()==0)customizeEnvelope.disable();
				else customizeEnvelope.enable();
			}
		});
		selectedSpecies.getBottomToolbar().addButton(customizeEnvelope);		
		center.add(selectedSpecies,new RowLayoutData("30%"));

		this.add(center);
		this.addListener(new PanelListenerAdapter(){			
			public void onShow(Component component) {
				AquaMapsPortlet.get().showLoading("Retrieving information", component.getId());
				toAddSpecies.getStore().reload();
				selectedSpecies.getStore().reload();
				AquaMapsPortlet.get().hideLoading(component.getId());
			}

			public void onResize(BoxComponent component, int adjWidth,
					int adjHeight, int rawWidth, int rawHeight) {			
				filter.setHeight(adjHeight-20);
				center.setHeight(adjHeight-20);
				center.setWidth(adjWidth-filter.getWidth()-10);
			}
		});

	}



}