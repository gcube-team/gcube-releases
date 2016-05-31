package org.gcube.application.aquamaps.aquamapsportlet.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.Tags;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SubmittedFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.details.AquaMapsDetailsPanel;
import org.gcube.application.aquamaps.aquamapsportlet.client.filters.SubmittedFiltersMenu;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientObject;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.Msg;
import org.gcube.application.aquamaps.aquamapsportlet.client.selections.ExtendedLiveGrid;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSaveNotification.WorskpaceExplorerSaveNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.save.WorkspaceExplorerSaveDialog;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.core.XDOM;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.Record;
import com.gwtext.client.widgets.BoxComponent;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.ContainerListenerAdapter;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.grid.event.RowSelectionListenerAdapter;
import com.gwtext.client.widgets.layout.ColumnLayout;
import com.gwtext.client.widgets.layout.ColumnLayoutData;

public class DiscoveringPanel extends Panel {



	DiscoveringPanel instance = this;
	AquaMapsDetailsPanel aquamapsDetails=new AquaMapsDetailsPanel();
	public 	ExtendedLiveGrid submittedGrid=new ExtendedLiveGrid("Submitted AquaMaps Objects",Stores.submittedStore(),ColumnDefinitions.submittedColumnModel(),false);



	ToolbarButton delete=new ToolbarButton("Delete");
	ToolbarButton save=new ToolbarButton("Save");
	ToolbarButton refresh=new ToolbarButton("Enable Auto Refresh");

	private boolean loadObject=false;

	ToolbarButton filterMenuButton=new ToolbarButton("Filter by");

	final public AsyncCallback<Msg> jobFilterUpdate=new AsyncCallback<Msg>(){

		public void onFailure(Throwable caught) {
			Log.error("Unable to update job filter",caught);
			AquaMapsPortlet.get().hideLoading(AquaMapsPortlet.get().discoveringPanel.getId());
			AquaMapsPortlet.get().showMessage("Sorry, an error occurred while trying to update job filter");
		}

		public void onSuccess(Msg result) {
			Log.debug("Filter updated, msg : "+result.getMsg());
			AquaMapsPortlet.get().hideLoading(AquaMapsPortlet.get().discoveringPanel.getId());
			AquaMapsPortlet.get().discoveringPanel.submittedGrid.getStore().reload();
		}

	};	




	public DiscoveringPanel() {
		try{
			aquamapsDetails=new AquaMapsDetailsPanel();
			Log.debug("Created details panel");
			this.setTitle("View AquaMaps Results");
			this.setFrame(true);

			this.setLayout(new ColumnLayout());
			submittedGrid.addTool(AquaMapsPortlet.get().getHelpTool("https://gcube.wiki.gcube-system.org/gcube/index.php/AquaMaps#AquaMaps_Products_Browsing_and_Management"));
			aquamapsDetails.addTool(AquaMapsPortlet.get().getHelpTool("https://gcube.wiki.gcube-system.org/gcube/index.php/AquaMaps#AquaMaps_Products_Browsing_and_Management"));

			submittedGrid.getSelectionModel().addListener(new RowSelectionListenerAdapter(){
				@Override
				public void onRowSelect(RowSelectionModel sm, int rowIndex,
						Record record) {
					delete.enable();
					save.enable();
					if((sm.getCount()==1)&&(loadObject)){
						AquaMapsPortlet.get().showLoading("Loading details..", aquamapsDetails.getId());
						final Integer id=submittedGrid.getStore().getAt(rowIndex).getAsInteger(SubmittedFields.searchid+"");
						AquaMapsPortlet.remoteService.getAquaMapsObject(id,false, new AsyncCallback<ClientObject>(){

							public void onFailure(Throwable caught) {
								AquaMapsPortlet.get().hideLoading(aquamapsDetails.getId());
								AquaMapsPortlet.get().showMessage("Work in progress, please retry later");
								Log.error("[getAquaMapsObjectCallback]", caught);
							}

							public void onSuccess(ClientObject result) {
								Log.debug("[getAquaMapsObjectCallback] - success");
								aquamapsDetails.loadObject(result);						
								AquaMapsPortlet.get().hideLoading(aquamapsDetails.getId());

							}				
						});
					}
				}
				@Override
				public void onRowDeselect(RowSelectionModel sm, int rowIndex,
						Record record) {
					delete.disable();
					save.disable();
				}
			});


			submittedGrid.useAllButton.hide();

			delete.addListener(new ButtonListenerAdapter(){

				public void onClick(Button button, EventObject e) {
					Record[] selection=submittedGrid.getSelectionModel().getSelections();
					final List<Integer> aquamapsIds=new ArrayList<Integer>();
					for(int i=0;i<selection.length;i++)
						aquamapsIds.add(selection[i].getAsInteger(SubmittedFields.searchid+""));
					MessageBox.confirm("Delete "+aquamapsIds.size()+" object(s)", 
							"Are you sure?",  
							new MessageBox.ConfirmCallback() {  
						public void execute(String btnID) {  
							if(btnID.equalsIgnoreCase("yes")){
								AquaMapsPortlet.get().showLoading("Sending request..", instance.getId());							
								AquaMapsPortlet.remoteService.deleteSubmittedById(aquamapsIds, new AsyncCallback<Integer>(){

									public void onFailure(Throwable caught) {
										AquaMapsPortlet.get().hideLoading(instance.getId());
										Log.error("[DeleteSubmittedByIdCallback] Exception cause : "+caught.getMessage());
										AquaMapsPortlet.get().showMessage("Sorry, an error occurred while performing opoeration");
									}

									public void onSuccess(Integer result) {					
										AquaMapsPortlet.get().hideLoading(instance.getId());
										submittedGrid.getStore().reload();
										AquaMapsPortlet.get().showMessage("Deleted "+result+" items");
									}

								});
							}
						}
					}); 
				}
			});
			save.addListener(new ButtonListenerAdapter(){
				public void onClick(Button button, EventObject e) {

					final Record[] selection=submittedGrid.getSelectionModel().getSelections();
					
					String caption="Select where to save selected AquaMaps item"+(selection.length>1?"s":"");
					String suggestedFileName=selection[0].getAsString("title");
					
					final WorkspaceExplorerSaveDialog navigator = new WorkspaceExplorerSaveDialog(caption,suggestedFileName, true);
					
					
			        
			        
			        WorskpaceExplorerSaveNotificationListener listener = new WorskpaceExplorerSaveNotificationListener(){
			        	 
			    		@Override
			    		public void onSaving(Item parent, String fileName) {
			    			final List<Integer> aquamapsIds=new ArrayList<Integer>();
							for(int i=0;i<selection.length;i++)
								aquamapsIds.add(selection[i].getAsInteger(SubmittedFields.searchid+""));				
							
							
							AquaMapsPortlet.get().showLoading("Saving AquaMaps object to workspace", instance.getId());
							AquaMapsPortlet.remoteService.saveAquaMapsItem(aquamapsIds, fileName, parent.getId(), new AsyncCallback<Integer>() {

								public void onSuccess(Integer result) {
									AquaMapsPortlet.get().hideLoading(instance.getId());
									AquaMapsPortlet.get().showMessage("Saved "+result+" item"+((result>1)?"s":""));
								}

								public void onFailure(Throwable caught) {
									AquaMapsPortlet.get().hideLoading(instance.getId());
									AquaMapsPortlet.get().showMessage("Impossible to save on selected workspace. Please retry");									
								}
							});
			    			navigator.hide();
			    		}
			     
			    		@Override
			    		public void onAborted() {
			    			GWT.log("onAborted");
			    		}
			     
			    		@Override
			    		public void onFailed(Throwable throwable) {
			    			GWT.log("onFailed");
			    		}
			     
			           
			    	};
			    	navigator.addWorkspaceExplorerSaveNotificationListener(listener);
			    	
			    	navigator.setZIndex(XDOM.getTopZIndex());
			        navigator.show();
			    	
			    	
//					WorkspaceLightTreeSavePopup popup;
//					if (selection.length>1)
//						popup = new WorkspaceLightTreeSavePopup("Select where to save the AquaMaps item(s)", true);
//					else {
//						String title=selection[0].getAsString("title");
//						popup = new WorkspaceLightTreeSavePopup("Select where to save the AquaMaps item(s)", true,title);
//					}
//					popup.addStyleName("z_index_1200");
//					//only the basket item can be selected
//					popup.setSelectableTypes(ItemType.FOLDER, ItemType.ROOT);
//					popup.center();
//
//					popup.addPopupHandler(new PopupHandler() {					
//
//
//						//					public void save(org.gcube.portlets.user.workspace.lighttree.client.Item item, String name){}
//
//						public void onPopup(PopupEvent event) {
//							if (!event.isCanceled()){
//
//								final List<Integer> aquamapsIds=new ArrayList<Integer>();
//								for(int i=0;i<selection.length;i++)
//									aquamapsIds.add(selection[i].getAsInteger(SubmittedFields.searchid+""));				
//								org.gcube.portlets.widgets.lighttree.client.Item item = event.getSelectedItem();
//								String name = event.getName();
//								AquaMapsPortlet.get().showLoading("Saving AquaMaps object to workspace", instance.getId());
//								AquaMapsPortlet.remoteService.saveAquaMapsItem(aquamapsIds, name, item.getId(), new AsyncCallback<Integer>() {
//
//									public void onSuccess(Integer result) {
//										AquaMapsPortlet.get().hideLoading(instance.getId());
//										AquaMapsPortlet.get().showMessage("Saved "+result+" item"+((result>1)?"s":""));
//									}
//
//									public void onFailure(Throwable caught) {
//										AquaMapsPortlet.get().hideLoading(instance.getId());
//										AquaMapsPortlet.get().showMessage("Impossible to save on selected workspace. Please retry");									
//									}
//								});			
//							}
//							else {
//								//AquaMapsPortlet.get().hideLoading(AquaMapsPortlet.get().mainPanel.getId());
//							}
//
//						}});
//
//
//
//					popup.addDataLoadHandler(new DataLoadHandler(){
//						public void onDataLoad(DataLoadEvent event) {
//							if (event.isFailed()){
//								System.err.println("LoadingFailure: "+event.getCaught());
//							}
//						}});
//					popup.setText(submittedGrid.getSelectionModel().getSelected().getAsString(SubmittedFields.title+""));
//					popup.show();
//

				}
			});
			refresh.setEnableToggle(true);

			refresh.addListener(new ButtonListenerAdapter(){
				public void onToggle(Button button, boolean pressed) {
					if(pressed)
						button.setText(button.getText().replaceFirst("Enable", "Disable"));
					else 
						button.setText(button.getText().replaceFirst("Disable", "Enable"));
				}
			});

			submittedGrid.getBottomToolbar().addButton(refresh);
			submittedGrid.getBottomToolbar().addSeparator();
			submittedGrid.getBottomToolbar().addButton(save);
			submittedGrid.getBottomToolbar().addButton(delete);
			submittedGrid.getBottomToolbar().addSeparator();


			filterMenuButton.setMenu(new SubmittedFiltersMenu(){

				@Override
				public void deleteParameter(String paramName) {

					AquaMapsPortlet.get().showLoading("Updating filters settings..", instance.getId());
					AquaMapsPortlet.localService.filterSubmitted(paramName, null, jobFilterUpdate);
				}

				@Override
				public void setParameter(String paramName, String paramValue) {
					AquaMapsPortlet.get().showLoading("Updating filters settings..", instance.getId());
					if(paramName.equals(Tags.submittedShowAquaMaps))
						setDetailsEnable(Boolean.parseBoolean(paramValue));
					AquaMapsPortlet.localService.filterSubmitted(paramName, paramValue,jobFilterUpdate);
				}

			});
			submittedGrid.getBottomToolbar().addButton(filterMenuButton);


			this.add(submittedGrid,new ColumnLayoutData(.55));		
			this.add(aquamapsDetails,new ColumnLayoutData(.45));

			this.addListener(new ContainerListenerAdapter(){
				@Override
				public void onResize(BoxComponent component, int adjWidth,
						int adjHeight, int rawWidth, int rawHeight) {
					Log.debug("Resizing Discovering panel, adjW:"+adjWidth+" adjH:"+adjHeight);
					submittedGrid.setSize(adjWidth,adjHeight-10);
					aquamapsDetails.resize(adjWidth,adjHeight-10);
				}			
			});
			this.addListener(new PanelListenerAdapter(){
				@Override
				public void onActivate(Panel panel) {
					submittedGrid.getStore().reload();				
				}
			});

			Timer t = new Timer(){

				@Override
				public void run() {
					if(AquaMapsPortlet.get().mainPanel.getActiveTab().getId().equals(instance.getId()))
						if(refresh.isPressed())submittedGrid.getStore().reload();
				}

			};
			t.scheduleRepeating(10*1000);
			this.doLayout();
		}catch(Throwable t){
			Log.debug("Unable to create discovering panel", t);
		}
	}

	protected void setDetailsEnable(boolean enable){
		loadObject=enable;
		if(enable)
			AquaMapsPortlet.get().hideLoading(aquamapsDetails.getId());			
		else
			AquaMapsPortlet.get().showLoading("Job preview not supported. Please select an AquaMaps Object", aquamapsDetails.getId());

	}

}
