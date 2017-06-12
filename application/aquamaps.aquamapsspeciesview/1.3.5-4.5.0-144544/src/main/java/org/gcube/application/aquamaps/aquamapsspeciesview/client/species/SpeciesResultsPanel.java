package org.gcube.application.aquamaps.aquamapsspeciesview.client.species;

import java.util.ArrayList;

import org.gcube.application.aquamaps.aquamapsspeciesview.client.AquaMapsSpeciesView;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.constants.AquaMapsSpeciesViewConstants;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.constants.resources.Resources;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.Tags;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.Response;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.fields.SpeciesFields;

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
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SpeciesResultsPanel extends ContentPanel{

	final SpeciesGrid classicGrid;
	final DescriptiveSpeciesGrid descriptiveGrid;
	final SpeciesThumbsView view;
	
	private SpeciesView toReload;
	
	private static SpeciesResultsPanel instance;

	private static final int PAGE_SIZE=29;
	
	private Button switchToMapsView;
	
	  
	
	
	public static SpeciesResultsPanel getInstance() {
		return instance;
	}
	
	

	public SpeciesResultsPanel() {
		instance=this;

		classicGrid= new SpeciesGrid(getStore());  
		descriptiveGrid=new DescriptiveSpeciesGrid(getStore());
		
		view=new SpeciesThumbsView(getStore(),86);
		
		
		//************************** UPPER TOOLBAR 
		
		ToolBar toolBar = new ToolBar();  
		LabelToolItem labelView=new LabelToolItem("Switch grid view : ");
		toolBar.add(labelView);
		
		String toggleGroup="SPECIES_VIEW";
		
		
		ToggleButton view1 =new ToggleButton("Images",Resources.ICONS.images());		
		view1.setToggleGroup(toggleGroup);
		view1.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				instance.mask("Switching view..");
				classicGrid.hide();
				descriptiveGrid.hide();
				view.show();
				toReload=view;
				instance.unmask();
			}
		});
		view1.setAllowDepress(false);
		view1.toggle(true);
		view1.setToolTip(new ToolTipConfig("Images view", "Shows filtered species images and details on selected."));
		toolBar.add(view1);
		ToggleButton view2 =new ToggleButton("Descriptive",Resources.ICONS.text_list_bullets());
		view2.setToggleGroup(toggleGroup);
		view2.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				instance.mask("Switching view..");
				view.hide();
				descriptiveGrid.show();
				classicGrid.hide();
				toReload=descriptiveGrid;
				instance.unmask();
				
			}
		});
		view2.setAllowDepress(false);
		view2.setToolTip(new ToolTipConfig("Descriptive view", "Shows filtered species as a grid letting users to expand interested rows."));
		toolBar.add(view2);
		ToggleButton view3 =new ToggleButton("Scientific",Resources.ICONS.text_columns());
		view3.setToggleGroup(toggleGroup);
		view3.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				instance.mask("Switching view..");
				view.hide();
				descriptiveGrid.hide();
				classicGrid.show();
				toReload=classicGrid;
				instance.unmask();
			}
		});
		view3.setAllowDepress(false);
		view3.setToolTip(new ToolTipConfig("Scientific view", "Shows filtered species as a basic grid"));
		toolBar.add(view3);

		toolBar.add(new SeparatorToolItem());
		switchToMapsView=new Button("Show related maps",new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {				
				ArrayList<String> selection=new ArrayList<String>();
				final StringBuilder toShowSpeciesName=new StringBuilder();
				for(ModelData selected:toReload.getSelection()){
					selection.add(selected.get(SpeciesFields.genus+"")+"_"+selected.get(SpeciesFields.species+""));
					toShowSpeciesName.append(selected.get(SpeciesFields.genus+"")+" "+selected.get(SpeciesFields.species+""));
				}
				AquaMapsSpeciesView.get().mainPanel.mask("Harvesting data..");
				AquaMapsSpeciesView.localService.retrieveMapPerSpeciesList(selection, new AsyncCallback<Response>() {
					
					@Override
					public void onSuccess(Response result) {
						if(result.getStatus()){
							Integer count=Integer.parseInt(result.getAdditionalObjects().get(Tags.RETRIEVED_MAPS)+"");
							if(count>0){
								AquaMapsSpeciesView.get().switchToMapsView(toShowSpeciesName.toString());
							}else{
								AquaMapsSpeciesView.get().mainPanel.unmask();
								MessageBox.alert("Info", "No map available for selected species ", null);
							}							
						}else{
							AquaMapsSpeciesView.get().mainPanel.unmask();
							MessageBox.alert("Error", "Something went wrong : "+result.getAdditionalObjects().get(Tags.ERROR_MESSAGE), null);							
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {
						AquaMapsSpeciesView.get().mainPanel.unmask();
						MessageBox.alert("Error", "Something went wrong : "+caught.getMessage(), null);
						Log.error("Unexpected error while switching view",caught);
					}
				});
			}
		});
		switchToMapsView.setToolTip(new ToolTipConfig("Maps mode", "Shows current selected species related maps"));
		
		classicGrid.bindToSelection(switchToMapsView);
		descriptiveGrid.bindToSelection(switchToMapsView);
		view.bindToSelection(switchToMapsView);
		
		switchToMapsView.disable();
		toolBar.add(switchToMapsView);


		
		
		//************************ Panel Settings
		
		
		
		this.setFrame(true);  
		this.setCollapsible(false);
		this.setAnimCollapse(false);  
		this.setHeaderVisible(false);
		this.setLayout(new AnchorLayout());
		this.add(view,new AnchorData("100% 100%"));	
		this.add(descriptiveGrid,new AnchorData("100% 100%"));
		this.add(classicGrid,new AnchorData("100% 100%"));
		this.setSize(600, 350);  
		this.setTopComponent(toolBar);
		
		classicGrid.hide();
		descriptiveGrid.hide();
		toReload=view;
	}

	@Override
	protected void onAfterLayout() {
		super.onAfterLayout();
		reload();
	}
	
	
	private static final ListStore<ModelData> getStore(){
		//****************** READER AND STORE Settings
		String url=AquaMapsSpeciesViewConstants.servletUrl.get(Tags.speciesServlet);
		HttpProxy httpProxy = new HttpProxy(new RequestBuilder(RequestBuilder.GET, url));


		ModelType type = new ModelType();  
		type.setRoot(Tags.DATA);  
		type.setTotalName(Tags.TOTAL_COUNT);  
		type.addField(SpeciesFields.genus+"");  
		type.addField(SpeciesFields.species+"");
		type.addField(SpeciesFields.fbname+"");
		type.addField(SpeciesFields.speciesid+"");
		type.addField(SpeciesFields.speccode+"");

		type.addField(SpeciesFields.scientific_name+"");
		type.addField(SpeciesFields.english_name+"");
		type.addField(SpeciesFields.french_name+"");
		type.addField(SpeciesFields.spanish_name+"");
		type.addField(SpeciesFields.kingdom+"");
		type.addField(SpeciesFields.phylum+"");
		type.addField(SpeciesFields.classcolumn+"");
		type.addField(SpeciesFields.ordercolumn+"");
		type.addField(SpeciesFields.familycolumn+"");

		type.addField(SpeciesFields.deepwater+"");
		type.addField(SpeciesFields.m_mammals+"");
		type.addField(SpeciesFields.angling+"");
		type.addField(SpeciesFields.diving+"");
		type.addField(SpeciesFields.dangerous+"");
		type.addField(SpeciesFields.m_invertebrates+"");
		type.addField(SpeciesFields.algae+"");
		type.addField(SpeciesFields.seabirds+"");
		type.addField(SpeciesFields.freshwater+"");
		type.addField(SpeciesFields.pelagic+"");
		type.addField(SpeciesFields.picname+""); 
		type.addField(SpeciesFields.authname+"");
		
		
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
		loader.setSortField(SpeciesFields.genus+"");  
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
