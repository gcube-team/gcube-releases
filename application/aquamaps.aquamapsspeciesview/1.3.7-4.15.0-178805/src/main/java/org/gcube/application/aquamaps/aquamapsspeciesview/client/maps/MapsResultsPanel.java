package org.gcube.application.aquamaps.aquamapsspeciesview.client.maps;

import org.gcube.application.aquamaps.aquamapsspeciesview.client.AquaMapsSpeciesView;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.Reloadable;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.constants.AquaMapsSpeciesViewConstants;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.constants.resources.Resources;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.Tags;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.CompoundMapItem;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.HttpProxy;
import com.extjs.gxt.ui.client.data.JsonPagingLoadResultReader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.layout.AnchorData;
import com.extjs.gxt.ui.client.widget.layout.AnchorLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.http.client.RequestBuilder;

public class MapsResultsPanel extends ContentPanel{
	final MapsGrid classicGrid;
	final MapsThumbsView view;
	
	private Reloadable toReload;
	
	private static MapsResultsPanel instance;

	private static final int PAGE_SIZE=29;
	
	
	private Button switchToMaps;
	
	
	public static MapsResultsPanel getInstance() {
		return instance;
	}
	
	

	public MapsResultsPanel() {
		instance=this;

		classicGrid= new MapsGrid(getStore());  
		
		view=new MapsThumbsView(getStore(),86);
		
		
		//************************** UPPER TOOLBAR 
		
		ToolBar toolBar = new ToolBar();  
		LabelToolItem labelView=new LabelToolItem("Switch grid view : ");
		toolBar.add(labelView);
		
		String toggleGroup="MAPS_VIEW";
		
		
		ToggleButton view1 =new ToggleButton("Images",Resources.ICONS.images());		
		view1.setToggleGroup(toggleGroup);
		view1.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				instance.mask("Switching view..");
				classicGrid.hide();
				view.show();
				toReload=view;
				instance.unmask();
			}
		});
		view1.setAllowDepress(false);
		view1.toggle(true);
		view1.setToolTip(new ToolTipConfig("Images view", "Shows maps images and details on selected."));
		toolBar.add(view1);
		ToggleButton view3 =new ToggleButton("Scientific",Resources.ICONS.text_columns());
		view3.setToggleGroup(toggleGroup);
		view3.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				instance.mask("Switching view..");
				view.hide();
				classicGrid.show();
				toReload=classicGrid;
				instance.unmask();
			}
		});
		view3.setAllowDepress(false);
		view3.setToolTip(new ToolTipConfig("Scientific view", "Shows maps as a basic grid"));
		toolBar.add(view3);

		
		toolBar.add(new SeparatorToolItem());
		switchToMaps=new Button("Return to Species Mode",new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				AquaMapsSpeciesView.get().switchToSpeciesView();
			}
		});
		switchToMaps.setToolTip(new ToolTipConfig("Species Mode", "Returns to species searching mode"));
		toolBar.add(switchToMaps);

		
		
		//************************ Panel Settings
		
		
		
		this.setFrame(true);  
		this.setCollapsible(false);
		this.setAnimCollapse(false);  
		this.setHeaderVisible(false);
		this.setLayout(new AnchorLayout());
		this.add(view,new AnchorData("100% 100%"));	
		this.add(classicGrid,new AnchorData("100% 100%"));
		this.setSize(600, 350);  
		this.setTopComponent(toolBar);
		
		classicGrid.hide();
		toReload=view;
	}

	@Override
	protected void onAfterLayout() {
		super.onAfterLayout();
		reload();
	}
	
	
	private static final ListStore<ModelData> getStore(){
		//****************** READER AND STORE Settings
		String url=AquaMapsSpeciesViewConstants.servletUrl.get(Tags.mapServlet);
		HttpProxy httpProxy = new HttpProxy(new RequestBuilder(RequestBuilder.GET, url));


		ModelType type = new ModelType();  
		type.setRoot(Tags.DATA);  
		type.setTotalName(Tags.TOTAL_COUNT);  
		type.addField(CompoundMapItem.ALGORITHM);
		type.addField(CompoundMapItem.AUTHOR);
		type.addField(CompoundMapItem.COVERAGE);
		type.addField(CompoundMapItem.CREATION_DATE);
		type.addField(CompoundMapItem.DATA_GENERATION_TIME);
		type.addField(CompoundMapItem.FILESET_ID);
		type.addField(CompoundMapItem.GIS);
		type.addField(CompoundMapItem.IMAGE_COUNT);
		type.addField(CompoundMapItem.IMAGE_LIST);
		type.addField(CompoundMapItem.LAYER_ID);
		type.addField(CompoundMapItem.LAYER_PREVIEW);
		type.addField(CompoundMapItem.LAYER_URL);
		type.addField(CompoundMapItem.RESOURCE_ID);
		type.addField(CompoundMapItem.SPECIES_LIST);
		type.addField(CompoundMapItem.THUMBNAIL);
		type.addField(CompoundMapItem.TITLE);
		type.addField(CompoundMapItem.TYPE);
		type.addField(CompoundMapItem.CUSTOM);
		
		
		JsonPagingLoadResultReader<PagingLoadResult<ModelData>> reader = new JsonPagingLoadResultReader<PagingLoadResult<ModelData>>(type);  

		PagingLoader<PagingLoadResult<ModelData>> loader;
		
		loader = new BasePagingLoader<PagingLoadResult<ModelData>>(httpProxy,reader){
			protected void onLoadFailure(Object loadConfig, Throwable t) {
				super.onLoadFailure(loadConfig, t);
				Log.debug("THE LOAD EVENT HAS BEEN CANCELLED");			
				MessageBox.alert("Alert", "Your session seems to be expired, please refresh page.", null);  
			};
		};

		loader.addListener(Loader.BeforeLoad, new Listener<LoadEvent>() { 

			public void handleEvent(LoadEvent be) {				
				BasePagingLoadConfig m = be.<BasePagingLoadConfig> getConfig();  
				m.set(Tags.START, m.get(Tags.OFFSET));
				m.set("ext", "js");  
				m.set("lightWeight", true);
				m.set(Tags.sort, (m.get("sortField") == null) ? "" : m.get("sortField"));
				m.set(Tags.dir, (m.get("sortDir") == null || (m.get("sortDir") != null && m.<SortDir> get("sortDir").equals(  SortDir.NONE))) ? "" : m.get("sortDir"));
				
				Log.debug("beforeLoad event.. Offset "+m.getOffset()+", Limit "+m.getLimit()+" SORT "+m.getSortField());
				
			}
		});  
		
		loader.setSortDir(SortDir.ASC);  
		loader.setSortField(CompoundMapItem.TITLE);  
		loader.setRemoteSort(true);
		final ListStore<ModelData> toReturn=new ListStore<ModelData>(loader);
		
		Listener<BaseEvent> l= new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				Log.debug("Store event : "+be.getType().getEventCode()+", Store contains "+toReturn.getModels().size()+" elements");
			}
		};
		toReturn.addListener(ListStore.Clear, l);
		toReturn.addListener(ListStore.Add, l);
		toReturn.addListener(ListStore.Remove, l);
		toReturn.addListener(ListStore.Update, l);
		return toReturn;
	}



	public void reload() {
		toReload.reload();
	}
}
