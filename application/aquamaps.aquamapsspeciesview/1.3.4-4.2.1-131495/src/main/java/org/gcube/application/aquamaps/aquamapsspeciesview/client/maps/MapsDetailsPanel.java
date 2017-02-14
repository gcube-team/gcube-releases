package org.gcube.application.aquamaps.aquamapsspeciesview.client.maps;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsspeciesview.client.PortletCommon;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.constants.AquaMapsSpeciesViewConstants;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.constants.resources.Resources;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.CompoundMapItem;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.DetailsParameter;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.ImageItem;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.save.SaveCompoundMapRequest;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.save.SaveOperationType;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSaveNotification.WorskpaceExplorerSaveNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.save.WorkspaceExplorerSaveDialog;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LoadListener;
import com.google.gwt.user.client.ui.Widget;

public class MapsDetailsPanel extends ContentPanel{



	private ContentPanel imageContainer=new ContentPanel(new FitLayout());
	private ListStore<ImageItem> imageStore = new ListStore<ImageItem>();


	private GroupingStore<DetailsParameter> store=new GroupingStore<DetailsParameter>();
	private Image image=new Image();
	private Image layerPreview=new Image();
	private Button save;
	private Button gis;
	
	
	
	private Image loading=new Image();

	private static final String GENERIC_GROUP="Generic Information";
	private static final String GIS_GROUP="GIS Information";
	private static final String GENERATION_GROUP="Generation Information";

	private CompoundMapItem current=null;
	
	public MapsDetailsPanel() {
		setFrame(true);  
		setSize(700, 450); 
		setLayout(new FitLayout());  
		setHeading("Map Details");	   
		image.setTitle("Map Preview");
		image.setAltText("");


		//************************ GRID
		ContentPanel gridPanel=new ContentPanel();
		gridPanel.setLayout(new FitLayout());
		gridPanel.setHeading("Meta Information");
		gridPanel.setCollapsible(true);
		gridPanel.collapse();


		store.groupBy(DetailsParameter.PARAMETER_GROUP);
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();  

		ColumnConfig name = new ColumnConfig(DetailsParameter.PARAMETER_NAME, "Parameter", 100);
		columns.add(name);
		ColumnConfig value = new ColumnConfig(DetailsParameter.PARAMETER_VALUE, "Value", 100);
		columns.add(value);
		value.setEditor(new CellEditor(new TextField<Object>()){
			
		});
		ColumnModel cm = new ColumnModel(columns); 




		GroupingView view=new GroupingView(){
			
		};
		view.setShowGroupedColumn(false);  
		view.setForceFit(true);
		view.setAutoFill(true);
		view.setSortingEnabled(false);
		view.setStartCollapsed(true);
		view.setGroupRenderer(new GridGroupRenderer() {  
			public String render(GroupColumnData data) { 

				String l = data.models.size() == 1 ? "Item" : "Items";  
				return data.group + " (" + data.models.size() + " " + l + ")";  
			}  
		});
		
		EditorGrid<DetailsParameter> grid=new EditorGrid<DetailsParameter>(store, cm);
		grid.addListener(Events.AfterEdit, new Listener<GridEvent<DetailsParameter>>() {
            public void handleEvent(GridEvent<DetailsParameter> be) {
            		be.getRecord().reject(false);
            		be.getRecord().cancelEdit();
             }
         });
		grid.addListener(Events.BeforeEdit, new Listener<GridEvent<DetailsParameter>>() {
            public void handleEvent(GridEvent<DetailsParameter> be) {
            	
            		be.getRecord().beginEdit();
            	
             }
         });
		grid.setHideHeaders(true);
		grid.setView(view);
		grid.setBorders(true);
		grid.setHeight(300);
		grid.setWidth(340);
		grid.setStripeRows(true);
		grid.removeStyleName("x-unselectable");
		gridPanel.add(grid);

		

		//*********************** IMAGE CONTAINER

		imageContainer.setHeading("Static Images");
		imageContainer.setCollapsible(true);
		imageContainer.collapse();
		imageContainer.setFrame(true);
		imageContainer.setHeight(500);



		ListView<ImageItem> imageView = new ListView<ImageItem>() {  
			@Override  
			protected ImageItem prepareData(ImageItem model) {  
				String s = model.get(ImageItem.LABEL);  
				model.set("shortName", Format.ellipse(s, 15));  
				return model;  
			}  

		};  

		imageView.setTemplate(getTemplate());  
		imageView.setStore(imageStore);  
		imageView.setItemSelector("div.thumb-wrap");  
		imageView.getSelectionModel().addListener(Events.SelectionChange,  
				new Listener<SelectionChangedEvent<ImageItem>>() {  

			public void handleEvent(SelectionChangedEvent<ImageItem> be) { 
				if(be.getSelection().size()>0)
					imageContainer.setHeading("Static Images (" + be.getSelectedItem().get(ImageItem.LABEL) 
							+ " selected)");
				else imageContainer.setHeading("Static Images ("+imageStore.getCount()+ " total)");
			}  

		});  

		imageView.setId("img-details-view");
		imageView.setWidth(340);
		imageContainer.add(imageView);

		//********************** Layer preview
		ContentPanel layerPanel=new ContentPanel();
		layerPanel.setLayout(new FitLayout());
		layerPanel.setCollapsible(true);		
		layerPanel.setHeading("Layer Preview");
		layerPanel.add(layerPreview);
		layerPreview.setTitle("Layer Preview");
		layerPreview.setAltText("");
		layerPreview.addLoadListener(new LoadListener() {
			
			@Override
			public void onLoad(Widget sender) {
				layerPreview.setVisible(true);
				loading.setVisible(false);
			}
			
			@Override
			public void onError(Widget sender) {
				
			}
		});
		
		
		loading.setPixelSize(340, 240);
		loading.setUrl(GWT.getModuleBaseURL()+"img/loading.gif");
		

		add(image);
		add(gridPanel);
		add(imageContainer);


		add(layerPanel);		
		setScrollMode(Scroll.AUTOY);

		
		save=new Button("Save", Resources.ICONS.disk(), new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {				
				String toExportName=current.getTitle();
				
				final WorkspaceExplorerSaveDialog navigator = new WorkspaceExplorerSaveDialog(toExportName, true);
				
				
		        
		        
		        WorskpaceExplorerSaveNotificationListener listener = new WorskpaceExplorerSaveNotificationListener(){
		        	 
		    		@Override
		    		public void onSaving(Item parent, String fileName) {
		    			PortletCommon.sendSaveRequest(
								new SaveCompoundMapRequest(SaveOperationType.COMPOUND_MAP, current, parent.getId(), fileName));
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
				
				
//				WorkspaceLightTreeSavePopup popup=new WorkspaceLightTreeSavePopup("Save map data ", true, toExportName);
//				popup.addStyleName("z_index_1200");
//				//only the basket item can be selected
//				popup.setSelectableTypes(ItemType.FOLDER, ItemType.ROOT);
//				popup.center();
//				
//				popup.addPopupHandler(new PopupHandler() {	
//				
//				public void onPopup(PopupEvent event) {
//					if (!event.isCanceled()){						
//						org.gcube.portlets.widgets.lighttree.client.Item item = event.getSelectedItem();
//						final String name = event.getName();
//						PortletCommon.sendSaveRequest(
//								new SaveCompoundMapRequest(SaveOperationType.COMPOUND_MAP, current, item.getId(), name));
//
//				}}});
//				
//				
//				
//				popup.addDataLoadHandler(new DataLoadHandler(){
//					public void onDataLoad(DataLoadEvent event) {
//						if (event.isFailed()){
//							Log.error("LoadingFailure: "+event.getCaught());
//						}
//					}});
//				
//				popup.setText(toExportName);
//				popup.show();
			}
		});
		save.disable();
		save.setToolTip(new ToolTipConfig("Save", "Save current map to workspace"));
		gis=new Button("GIS Viewer", Resources.ICONS.world(), new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				com.google.gwt.user.client.Window.open(current.getLayerUrl(),current.getTitle(),"");
				
			}
		});
		gis.setToolTip(new ToolTipConfig("GIS Viewer", "Open current map in GIS Viewer"));
		gis.disable();
		
		ToolBar bar=new ToolBar();
		bar.add(save);
		bar.add(gis);
		setBottomComponent(bar);
	}


	public void setData(CompoundMapItem data){
		mask("Loading details..");	
		this.current=data;
		save.enable();
		if(data.isGis())gis.enable();
		else gis.disable();
		setHeading(data.getTitle()+" details.");
		image.setUrl(data.getImageThumbNail());
		image.setAltText("Static image for "+data);
		image.setTitle(data.getTitle());
		image.setPixelSize(340,240);


		List<DetailsParameter> toAdd=new ArrayList<DetailsParameter>();

		toAdd.add(new DetailsParameter(AquaMapsSpeciesViewConstants.mapFieldsNames.get(CompoundMapItem.TITLE), data.getTitle(), GENERIC_GROUP));
		toAdd.add(new DetailsParameter(AquaMapsSpeciesViewConstants.mapFieldsNames.get(CompoundMapItem.AUTHOR), data.getAuthor(), GENERIC_GROUP));
		toAdd.add(new DetailsParameter(AquaMapsSpeciesViewConstants.mapFieldsNames.get(CompoundMapItem.TYPE), data.getType(), GENERIC_GROUP));
		toAdd.add(new DetailsParameter(AquaMapsSpeciesViewConstants.mapFieldsNames.get(CompoundMapItem.IMAGE_COUNT), data.getImageCount()+"", GENERIC_GROUP));
		toAdd.add(new DetailsParameter(AquaMapsSpeciesViewConstants.mapFieldsNames.get(CompoundMapItem.GIS), data.isGis()+"", GENERIC_GROUP));
		toAdd.add(new DetailsParameter(AquaMapsSpeciesViewConstants.mapFieldsNames.get(CompoundMapItem.CUSTOM), data.isCustom()+"", GENERIC_GROUP));

		toAdd.add(new DetailsParameter(AquaMapsSpeciesViewConstants.mapFieldsNames.get(CompoundMapItem.CREATION_DATE), AquaMapsSpeciesViewConstants.timeFormat.format(new Date(data.getCreationDate())), GENERATION_GROUP));
		toAdd.add(new DetailsParameter(AquaMapsSpeciesViewConstants.mapFieldsNames.get(CompoundMapItem.RESOURCE_ID), data.getResourceId()+"", GENERATION_GROUP));
		toAdd.add(new DetailsParameter(AquaMapsSpeciesViewConstants.mapFieldsNames.get(CompoundMapItem.ALGORITHM), data.getAlgorithm(), GENERATION_GROUP));
		toAdd.add(new DetailsParameter(AquaMapsSpeciesViewConstants.mapFieldsNames.get(CompoundMapItem.DATA_GENERATION_TIME), AquaMapsSpeciesViewConstants.timeFormat.format(new Date(data.getDataGenerationTime())), GENERATION_GROUP));


		toAdd.add(new DetailsParameter(AquaMapsSpeciesViewConstants.mapFieldsNames.get(CompoundMapItem.LAYER_ID), data.getLayerId(), GIS_GROUP));
		toAdd.add(new DetailsParameter(AquaMapsSpeciesViewConstants.mapFieldsNames.get(CompoundMapItem.LAYER_URL), data.getLayerUrl(), GIS_GROUP));

		store.removeAll();
		store.add(toAdd);



		List<ImageItem> toAddImages=new ArrayList<ImageItem>();

		for(String uri:data.getImageList().split(",")) toAddImages.add(new ImageItem(uri));


		imageStore.removeAll();
		imageStore.add(toAddImages);




		layerPreview.setUrl(data.getLayerPreview());
		layerPreview.setPixelSize(340,240);
		layerPreview.setVisible(false);
		
		loading.setVisible(true);
		unmask();
		Log.debug("store contains : "+store.getModels().size());
	}

	private native String getTemplate() /*-{ 
     return ['<tpl for=".">', 
     '<div class="thumb-wrap" id="{PATH}" style="border: 1px solid white">', 
     '<div class="thumb"><img src="{PATH}" title="{LABEL}"></div>', 
     '<span class="x-editable">{shortName}</span></div>', 
     '</tpl>', 
     '<div class="x-clear"></div>'].join(""); 

     }-*/;
}
