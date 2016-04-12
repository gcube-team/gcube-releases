package org.gcube.application.aquamaps.aquamapsportlet.client.details;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gcube.application.aquamaps.aquamapsportlet.client.AquaMapsPortlet;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.AquaMapsPortletCostants;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SubmittedFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientObjectType;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientObject;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.GISViewerParameters;
import org.gcube.portlets.user.gcubegisviewer.client.GCubeGisViewer;
import org.gcube.portlets.user.gcubegisviewer.client.event.SaveEvent;
import org.gcube.portlets.user.gcubegisviewer.client.event.SaveHandler;
import org.gcube.portlets.user.gisviewer.client.GisViewerParameters;
import org.gcube.portlets.widgets.applicationnews.client.PostAppNewsDialog;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.NameValuePair;
import com.gwtext.client.core.Size;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.ToolTip;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.grid.PropertyGridPanel;
import com.gwtext.client.widgets.grid.event.PropertyGridPanelListener;
import com.gwtext.client.widgets.layout.AnchorLayout;
import com.gwtext.client.widgets.layout.AnchorLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.layout.TableLayout;

public class AquaMapsDetailsPanel extends Panel {

	//TODO supply "generating" image
	//	private static final String unavailableImagePath=GWT.getModuleBaseURL()+"";

	private ClientObject current=null;
	public PropertyGridPanel properties=new PropertyGridPanel();
	Panel imagesContainer= new Panel();	
	Map<String,Image> imageMap=new HashMap<String, Image>();
	ToolbarButton showPerturbation=new ToolbarButton("Show Customizations"); 
	//	ToolbarButton preview=new ToolbarButton("Preview");
	AquaMapsDetailsPanel instance=this;

	ToolbarButton GIS = new ToolbarButton("GIS Viewer");
	ToolbarButton publish=new ToolbarButton("Publish Map");

	public AquaMapsDetailsPanel() {
		try{
			this.setId("AmDetailsPanelID");
			this.setLayout(new AnchorLayout(300,800));
			Toolbar toolBar = new Toolbar();
			toolBar.addButton(showPerturbation);

			properties.setTitle("Map Details");
			properties.setCollapsible(true);
			properties.setAutoHeight(true);
			properties.setFrame(true);
			properties.setHeight(300);
			properties.setSource(new NameValuePair[]{});
			properties.addPropertyGridPanelListener(new PropertyGridPanelListener(){

				public boolean doBeforePropertyChange(PropertyGridPanel source,
						String recordID, Object value, Object oldValue) {
					return false;
				}

				public void onPropertyChange(PropertyGridPanel source,
						String recordID, Object value, Object oldValue) {			
				}

			});
			this.add(properties,new AnchorLayoutData("100% 45%"));


			//Creating images widgets
			imageMap.put("Earth", new Image());
			imageMap.put("Continent View : Africa", new Image());
			imageMap.put("Continent View : Asia", new Image());
			imageMap.put("Continent View : Australia", new Image());
			imageMap.put("Continent View : Europa", new Image());
			imageMap.put("Continent View : North America", new Image());
			imageMap.put("Continent View : South America", new Image());
			imageMap.put("Ocean View : Atlantic", new Image());
			imageMap.put("Ocean View : Indian", new Image());
			imageMap.put("Pole View : Artic", new Image());
			imageMap.put("Ocean View : North Atlantic", new Image());
			imageMap.put("Ocean View : Pacific", new Image());
			imageMap.put("Pole View : Antarctic", new Image());
			imageMap.put("Ocean View : South Atlantic", new Image());

			imagesContainer.setTitle("Generated Images");
			imagesContainer.setLayout(new TableLayout(4));
			imagesContainer.setFrame(true);
			imagesContainer.setSize(300, 300);
			for(final String name: imageMap.keySet()){
				Image img=imageMap.get(name);			
				img.setPixelSize(93, 93);							
				final Panel panel=new Panel();
				panel.setLayout(new FitLayout());
				panel.add(img);
				panel.setPaddings(5);			


				img.addClickHandler(new ClickHandler() {

					public void onClick(ClickEvent arg0) {
						Window window=new Window();
						window.setLayout(new FitLayout());
						window.setTitle(name);
						Image img=new Image(imageMap.get(name).getUrl());					
						img.setPixelSize(300, 300);
						window.add(img);
						window.show(panel.getId());					
					}
				});
				ToolTip tt=new ToolTip();
				tt.setHtml(name);
				tt.applyTo(panel);
				imagesContainer.add(panel);
			}

			imagesContainer.setAutoScroll(true);

			GIS.addListener(new ButtonListenerAdapter(){
				@Override
				public void onClick(final Button button,EventObject e) {
					int jobId=AquaMapsPortlet.get().discoveringPanel.submittedGrid.getSelectionModel().getSelected().getAsInteger(SubmittedFields.searchid+"");
					AquaMapsPortlet.get().showLoading("Retrieving GIS information", instance.getId());
					Log.debug("Requesting GIS information for job Id : "+jobId);
					AquaMapsPortlet.remoteService.checkGIS(jobId, new AsyncCallback<GISViewerParameters>(){

						public void onFailure(Throwable caught) {
							AquaMapsPortlet.get().hideLoading(instance.getId());
							AquaMapsPortlet.get().showMessage("Unable to retrieve information");
							Log.error("[checkGISCallback]", caught);
						}

						public void onSuccess(GISViewerParameters result) {
							if(result.getStatus()){
								AquaMapsPortlet.get().hideLoading(instance.getId());

								GisViewerParameters params=new GisViewerParameters();
								params.setOpeningLayers(result.getLayers());

								GCubeGisViewer gisViewer= new GCubeGisViewer(params);
								gisViewer.addSaveHandler(new SaveHandler() {

									@Override
									public void onSaveSuccess(SaveEvent event) {
										AquaMapsPortlet.get().hideLoading(instance.getId());											
									}

									@Override
									public void onSaveFailure(SaveEvent event) {
										AquaMapsPortlet.get().hideLoading(instance.getId());
										AquaMapsPortlet.get().showMessage("Impossible to save on selected workspace. Please retry");
									}

									@Override
									public void onSave(SaveEvent event) {
										AquaMapsPortlet.get().showLoading("Saving layer to workspace", instance.getId());
									}
								});
								gisViewer.show(); 
							}else {
								AquaMapsPortlet.get().hideLoading(instance.getId());
								AquaMapsPortlet.get().showMessage(result.getMsg());
							}											
							Log.debug("[checkGISCallback] - "+result.getMsg());						
						}

					});



				}
			});
			ToolbarButton details=new ToolbarButton("Additional Details");
			details.addListener(new ButtonListenerAdapter(){
				@Override
				public void onClick(Button button, EventObject e) {
					AdditionalDetailsPopup popup=new AdditionalDetailsPopup(current);
					popup.show(button.getButtonElement());							


				}
			});

			publish.addListener(new ButtonListenerAdapter(){
				@Override
				public void onClick(Button button, EventObject e) {
					//				LinkPreview preview=new LinkPreview();				
					//				preview.setDescription("");
					//				preview.setImageType(ImageType.JPG);
					//				preview.setLinkThumbnailUrl(DataManagementFacilityConstants.getImagePreviewUrl());
					//				preview.setTitle((String) selected.get(ClientResource.TITLE));

					//				String resourceType=selected.get(ClientResource.TYPE);
					//				String resourceId=selected.get(ClientResource.SEARCH_ID);

					new PostAppNewsDialog(
							AquaMapsPortletCostants.getNewsApplicationID(), 
							current.getType()+" map \""+current.getName()+"\" is now available", AquaMapsPortletCostants.getQueryStringParameter()+"="+current.getId());
				}
			});
			imagesContainer.setBottomToolbar(new Button[] {details,
					GIS,
					publish});
			imagesContainer.setId("IMAGESContainer");
			this.add(imagesContainer,new AnchorLayoutData("100% 55%"));			
		}catch(Throwable t){
			Log.debug("Unable to create detail panel", t);
		}
	}


	public void loadObject(ClientObject obj){
		try{
			current=obj;
			Log.debug("Loading AquaMapsObject...");
			Log.debug("Title "+obj.getName());
			Log.debug("Type "+obj.getType());
			Log.debug("Author "+obj.getAuthor());
			Log.debug("Id "+obj.getId());
			Log.debug("spec count "+obj.getSelectedSpecies());
			Log.debug("BB "+obj.getBoundingBox());
			Log.debug("threshold "+obj.getThreshold());
			Log.debug("number of imgs "+obj.getImages().size());
			Log.debug("gis "+obj.getGis());

			ArrayList<NameValuePair> source=new ArrayList<NameValuePair>();
			source.add(new NameValuePair("Name of Map",obj.getName()));
			source.add(new NameValuePair("Type of Map", obj.getType().toString()));
			source.add(new NameValuePair("Algorithm", obj.getAlgorithmType()));
			source.add(new NameValuePair("Author", obj.getAuthor()));
			source.add(new NameValuePair("Id", obj.getId()));
			source.add(new NameValuePair(obj.getType().equals(ClientObjectType.Biodiversity)?"Number of species":"Selected Species",obj.getSelectedSpecies().getValue()));
			source.add(new NameValuePair("Bounding Box (N,S,E,W)",obj.getBoundingBox().toString()));
			if(obj.getType().equals(ClientObjectType.Biodiversity))source.add(new NameValuePair("PSO threshold",String.valueOf(obj.getThreshold())));
			source.add(new NameValuePair("Number of generated image(s)",obj.getImages().size()));
			source.add(new NameValuePair("GIS enabled ",String.valueOf(obj.getGis())));
			if(obj.getGis()){
				source.add(new NameValuePair("Layer Title",obj.getLayerName()));
				source.add(new NameValuePair("Layer Url",obj.getLayerUrl()));
			}
			if(obj.getImages().size()>0)source.add(new NameValuePair("Images base path",obj.getLocalBasePath()));

			properties.setSource(source.toArray(new NameValuePair[source.size()]));
			Log.debug("loaded Properties");
			if(obj.getImages().size()>0)
				for(String name:imageMap.keySet()){
					imageMap.get(name).setUrl("");
					imageMap.get(name).setVisible(false);
					for(String fName:obj.getImages().keySet())
						if(fName.matches("(.)*"+name)){
							imageMap.get(name).setUrl(obj.getImages().get(fName));	
							imageMap.get(name).setVisible(true);
							break;}			
				}
			else for(Image img:imageMap.values()){
				img.setUrl("");
				img.setVisible(false);
			}


			//			preview.enable();			
			Log.debug("Object Loaded into details panel");

			if(obj.getGis()) GIS.enable();
			else GIS.disable();			
		}
		catch (Exception e){
			MessageBox.alert("Sorry,some information may not be available");
			Log.error("Exception while loading object in detail panel ",e);
		}
	}

	
	public void resize(int width, int height){
		Log.debug("Resizing details w: "+width+" h: "+height);
		properties.setSize(width, height);
		imagesContainer.setSize(width, height/2);
	}
}
