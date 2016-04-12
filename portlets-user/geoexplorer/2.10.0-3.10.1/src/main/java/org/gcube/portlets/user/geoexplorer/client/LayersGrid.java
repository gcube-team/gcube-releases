/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.geoexplorer.client.beans.LayerItem;
import org.gcube.portlets.user.geoexplorer.client.resources.Images;
import org.gcube.portlets.user.geoexplorer.client.rpc.GeoExplorerServiceAsync;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseFilterPagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;

/**
 * @author ceras
 * @author modified by Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class LayersGrid extends LayoutContainer {
	private static final String REFRESH_BUTTON_TOOLTIP = "Reload all data (hard refresh)";
	private static final String SEARCH_BUTTON_TOOLTIP = "Search for Title";
	private static final String SEARCH_CANCEL_BUTTON_TOOLTIP = "Cancel search filter, Show all data";

	private static final String ALERT_HARD_REFRESH = "Do you want to reload settings and run a hard data refresh on latest search?";
	private static final String OK_HARD_REFRESH = "Data correctly reloaded.";
	private static final String ERROR_HARD_REFRESH = "Error to retrieve data.\nHard refresh not executed.";

	private static final int PAGE_SIZE = 20;
	private static final ImageResource ICON_OPEN_GIS_VIEWER = GeoExplorer.resources.iconGisWorld();

	private GeoExplorerServiceAsync service;
	private Grid<LayerItem> grid;
	private CheckBoxSelectionModel<LayerItem> selectionModel;
	private LayersGridHandler layersGridListener;
	protected GridFilters remoteFilters = new GridFilters();
	protected GridFilters localFilters = new GridFilters();
	private String layerGridtitle;
	private boolean filterActive = false;
	private Button searchCancelButton;
	private Button refreshButton;

//	private List<Workspace> workspaces;
//	protected Workspace currentWorkspace;

	/**
	 * 
	 */
	public LayersGrid(String title, LayersGridHandler layersGridHandler, GeoExplorerServiceAsync service) {
		super();
		this.layersGridListener = layersGridHandler;
		this.service = service;
		this.layerGridtitle = title;
	}

	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);

		setLayout(new FitLayout());

		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setLayout(new FitLayout());
		add(panel);
		layersGridListener.layersGridRendered();
	}

	/**
	 * @return
	 */
	private ToolBar createToolBar() {

		final TextField<String> searchText = new TextField<String>();  
		final Button searchButton = new Button();
		
		searchText.setEmptyText("enter a text");
		searchText.setFieldLabel("Layer Name");
		searchText.setToolTip("Search for title of a GIS layer stored in the D4Science infrastructure.");
		searchText.setAllowBlank(true);
		searchText.addKeyListener(new KeyListener(){
			@Override
			public void componentKeyDown(ComponentEvent event) {
				super.componentKeyDown(event);
				if (event.getKeyCode()==KeyCodes.KEY_ENTER)
					searchButton.fireEvent(Events.Select);
			}
		});

		searchButton.setToolTip(SEARCH_BUTTON_TOOLTIP);
		searchButton.setIcon(Images.iconSearch());
		searchButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				String text = searchText.getValue();
				String field = "title"; //(tb1.isPressed() ? "title" : tb2.isPressed() ? "name" :"geoserverUrl");
				search(field, text);
			}
		});

		searchCancelButton = new Button();
		searchCancelButton.setToolTip(SEARCH_CANCEL_BUTTON_TOOLTIP);
		searchCancelButton.setIcon(Images.iconCancel());
		searchCancelButton.setEnabled(false);
		searchCancelButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				searchText.setValue("");
				search(null, null);
			}
		});
		
		if (Constants.cacheEnabled) {
			refreshButton = new Button();
			refreshButton.setToolTip(REFRESH_BUTTON_TOOLTIP);
			refreshButton.setIcon(Images.iconRefresh());
			refreshButton.setEnabled(true);
			refreshButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
				@Override
				public void componentSelected(ButtonEvent ce) {
					MessageBox.confirm("Warning", ALERT_HARD_REFRESH,
							new Listener<MessageBoxEvent>(){
								@Override
								public void handleEvent(MessageBoxEvent be) {
									if (be.getButtonClicked().getText().contentEquals("Yes"))
										hardRefresh();
								}
							}
						);
				}
			});
		}

		ToolBar toolBar = new ToolBar();  
		toolBar.getAriaSupport().setLabel("Search");
		Label label = new Label("&nbsp;Search for Title&nbsp;&nbsp;");
		label.setToolTip("GeoExplorer ver. "+Constants.VERSION);
		toolBar.add(label);
		toolBar.add(searchText);
		toolBar.add(searchButton);
		toolBar.add(new SeparatorToolItem());
		toolBar.add(searchCancelButton);
		toolBar.add(new SeparatorToolItem());
		return toolBar;
	}
	
	public void hardRefresh(boolean confirm){
		
		if(confirm){
			MessageBox.confirm("Warning", ALERT_HARD_REFRESH,
					new Listener<MessageBoxEvent>(){
						@Override
						public void handleEvent(MessageBoxEvent be) {
							if (be.getButtonClicked().getText().contentEquals("Yes"))
								hardRefresh();
						}
					}
				);
		}else
			hardRefresh();
	}

	
	public List<LayerItem> getSelectedItems() {
		return grid.getSelectionModel().getSelectedItems();
	}

	/**
	 * 
	 */
	public void deselectAll() {
		grid.getSelectionModel().deselectAll();
	}

	/**
	 * 
	 */
	public void selectAll() {
		grid.getSelectionModel().selectAll();
	}

	public void search(String field, String text) {
		
		boolean cancelSearch = (text==null || text.equals(""));
		
		if (cancelSearch && !filterActive)
			return;

		// for prevent automatic reload while change filter 
		remoteFilters.setAutoReload(false);
		remoteFilters.clearFilters();

		if (!cancelSearch) {
			StringFilter filter = new StringFilter(field);
			filter.setValue(text);
			remoteFilters.addFilter(filter);
			
//			//ADDED BY FRANCESCO M. 16/04/2013
//			String isNewSearch = "true";
//			StringFilter isNewSearchFilter = new StringFilter(isNewSearch);
//			filters.addFilter(isNewSearchFilter);
//			//END
			
			remoteFilters.updateColumnHeadings();
		}

		ListLoader<? extends ListLoadResult<LayerItem>> loader = grid.getStore().getLoader();
		loader.load();

		// restore autoreload
		remoteFilters.setAutoReload(true);
		
		searchCancelButton.setEnabled(!cancelSearch);
		filterActive = !cancelSearch;
	}

	/**
	 * @param workspaces 
	 * 
	 */
	public void start() {
	
		// button renderer
		GridCellRenderer<LayerItem> buttonRenderer = new GridCellRenderer<LayerItem>() {
			@Override
			public Object render(final LayerItem l, String property, ColumnData config,
					int rowIndex, int colIndex, ListStore<LayerItem> store, Grid<LayerItem> grid) {
				
				Image imgOpenGisViewer = new Image(ICON_OPEN_GIS_VIEWER);
				imgOpenGisViewer.setTitle("Open this Layer with GisViewer");
				imgOpenGisViewer.setStyleName("imgCursor");
				imgOpenGisViewer.addClickHandler(new ClickHandler(){
					@Override
					public void onClick(ClickEvent event) {
						layersGridListener.openLayer(l);
					}
				});
				
				return imgOpenGisViewer;  
			}
		};

		// loader
		RpcProxy<PagingLoadResult<LayerItem>> proxy = new RpcProxy<PagingLoadResult<LayerItem>>() {
			@Override
			protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<LayerItem>> callback) {
				BaseFilterPagingLoadConfig config = (BaseFilterPagingLoadConfig)loadConfig;
				config.set("workspaceName", "");
				service.getLayers(config, callback);
			}
		};

		final PagingLoader<PagingLoadResult<ModelData>> loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy) {
			/* (non-Javadoc)
			 * @see com.extjs.gxt.ui.client.data.BasePagingLoader#newLoadConfig()
			 */
			@Override
			protected Object newLoadConfig() {
				BasePagingLoadConfig config = new BaseFilterPagingLoadConfig();
				return config;
			}
		};
		loader.setRemoteSort(true);
		
		loader.addLoadListener(new LoadListener(){
			@Override
			public void loaderLoadException(LoadEvent le) {
				Throwable e = le.exception;
				
				if(e.getMessage()!=null && e.getMessage().length()>1)
					MessageBox.alert("Error", e.getMessage(), null);
				else
					MessageBox.alert("Error", "Sorry, an error occurred on contacting the server. Plese refresh the page.", null);
			}
		});

		ListStore<LayerItem> store = new ListStore<LayerItem>(loader);
		

		final PagingToolBar pagingToolBar = new PagingToolBar(PAGE_SIZE){

			 @Override
			  public void last() {
			    int extra = totalLength % pageSize;
			    int lastStart = extra > 0 ? (totalLength - extra) : totalLength - pageSize;
			    doLoadRequest(lastStart, pageSize);
			  }
		};
		pagingToolBar.bind(loader);

		selectionModel = new CheckBoxSelectionModel<LayerItem>();
		selectionModel.setSelectionMode(SelectionMode.MULTI);

		List<ColumnConfig> columnConfigs = new ArrayList<ColumnConfig>();
		columnConfigs.add(selectionModel.getColumn());
		
		ColumnConfig columnButton = new ColumnConfig();  
		columnButton.setId("symbol");  
		columnButton.setHeader("");  
		columnButton.setWidth(25);
		columnButton.setFixed(true);
		columnButton.setSortable(false);
		columnButton.setRenderer(buttonRenderer);  
		columnConfigs.add(columnButton);  

		
	    ColumnConfig title = new ColumnConfig(LayerItem.LAYER_TITLE, "Layer Title", 200);  
	    title.setRenderer(new GridCellRenderer<LayerItem>() {  
	      public Object render(LayerItem model, String property, ColumnData config, int rowIndex, int colIndex,  
	          ListStore<LayerItem> store, Grid<LayerItem> grid) {  
	        return "<b>"
	            + model.getTitle()  +"</b>" +
	            "<br />" +
//	            "Abstract: " +model.getAbstractDescription(true);  
	        	"Publication Date: " +model.getPublicationDate()+
	        	 "<br />" +
	        	"Scope Code: "+model.getScopeCode();
	      }  
	  
	    });  
	    
	    title.setSortable(false);
	    columnConfigs.add(title);
	    ColumnConfig ccAbstract = new ColumnConfig(LayerItem.ABSTRACT_DESCRIPTION, "Abstract", 200);
	    ccAbstract.setSortable(false);
		columnConfigs.add(ccAbstract);

		ColumnConfig keywords = new ColumnConfig(LayerItem.KEYWORDS, "Keywords", 200);  
		keywords.setRenderer(new GridCellRenderer<LayerItem>() {  
		  
		      public Object render(LayerItem model, String property, ColumnData config, int rowIndex, int colIndex,  
		          ListStore<LayerItem> store, Grid<LayerItem> grid) {  
		    	  
		    	  String keysString="";
		    	  for (String key : model.getKeywords()) {
		    		  keysString+=key+", ";
				  }
		    	  
		    	  if(keysString.length()>2)
		    		  return keysString.substring(0, keysString.length()-2);
		    	  return keysString;
		      }  
		  
		});  
		
		keywords.setSortable(false);
		columnConfigs.add(keywords);
		
		
		ColumnConfig ccLayerName = new ColumnConfig(LayerItem.LAYER_NAME, "Layer Name", 150);
		ccLayerName.setSortable(false);
		columnConfigs.add(ccLayerName);
		
		ColumnModel cm = new ColumnModel(columnConfigs);

		grid = new Grid<LayerItem>(store, cm);
		grid.setStateId("pagingGrid");
		grid.setContextMenu(null);
//		grid.setStateful(true);
		
		
		//COMMENTED FRANCESCO M. 15/11/2013
//		grid.addListener(Events.Attach, new Listener<GridEvent<LayerItem>>() {
//			public void handleEvent(GridEvent<LayerItem> be) {
//				loader.load(0, PAGE_SIZE);
//			}
//		});

		grid.addListener(Events.RowClick, new Listener<GridEvent<LayerItem>>() {
			public void handleEvent(GridEvent<LayerItem> be) {
				LayerItem layerItem = be.getModel();
				layersGridListener.clickLayer(layerItem);
			}
		});
		grid.setLoadMask(true);
		grid.setBorders(true);
		grid.setAutoExpandColumn(LayerItem.LAYER_TITLE);
		grid.setAutoWidth(true);
		grid.getView().setForceFit(true);
		
		
		//FRANCESCO M.
		grid.getView().setEmptyText("No layer metadata is currently displayed. Search for title of a GIS layer stored in the D4Science Infrastructure");
		
		
		grid.setSelectionModel(selectionModel);
		grid.addPlugin(selectionModel);
		grid.addPlugin(remoteFilters);

		ContentPanel panel = new ContentPanel();
		panel.setHeading(layerGridtitle);
		panel.setHeaderVisible(false);
		panel.setLayout(new FitLayout());
		add(panel);
		panel.add(grid);

		ToolBar toolBar = createToolBar();

		panel.setTopComponent(toolBar);
		panel.setBottomComponent(pagingToolBar);
		grid.getAriaSupport().setLabelledBy(panel.getId());

		this.removeAll();
		this.add(panel);
		this.layout();
	}

	private void hardRefresh() {
		setWaitMessage(true);
		
		service.hardRefresh(new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				setWaitMessage(false);
				if (OK_HARD_REFRESH!=null && !OK_HARD_REFRESH.equals(""))
					MessageBox.info("Data reloaded.", OK_HARD_REFRESH, null);
				grid.getStore().getLoader().load();
			}
			@Override
			public void onFailure(Throwable caught) {
				setWaitMessage(false);
				MessageBox.alert("Error", ERROR_HARD_REFRESH+"<hr>"+"Cause:"+caught.getCause()+"Message:"+caught.getMessage()+"<br>StackTrace:"+caught.getStackTrace().toString(), null);
			}
		});
	}

	private MessageBox waitMessageBox;

	public void setWaitMessage(boolean wait) {
		if (wait)
			waitMessageBox = MessageBox.wait("", "Hard refresh, please wait...", "Loading...");
		else
			waitMessageBox.close();
	}
}
