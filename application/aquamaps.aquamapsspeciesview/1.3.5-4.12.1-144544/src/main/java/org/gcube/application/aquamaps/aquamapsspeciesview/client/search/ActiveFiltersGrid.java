package org.gcube.application.aquamaps.aquamapsspeciesview.client.search;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsspeciesview.client.AquaMapsSpeciesView;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.PortletCommon;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.constants.AquaMapsSpeciesViewConstants;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.ClientResource;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.SpeciesFilter;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.SpeciesSearchDescriptor;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.fields.FilterCategory;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.fields.SpeciesFields;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.types.ClientFieldType;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.types.ClientFilterOperator;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.types.ClientResourceType;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BeanModelReader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.RowEditorEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.grid.RowEditor;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;



public class ActiveFiltersGrid extends ContentPanel {

	private static final String TITLE="Advanced Search Options";
	
	private static ListStore<SpeciesFilter> filterStore=getFilterStore();
	private ColumnModel columnModel=getFilterColumnModel();
	
	private Grid<SpeciesFilter> grid=new Grid<SpeciesFilter>(filterStore,columnModel);
	
	SimpleComboBox<String> operatorCombo = new SimpleComboBox<String>();  
	ParameteredValidator valueValidator=new ParameteredValidator();
	ResourcePickerComboBox resourcePicker=new ResourcePickerComboBox(ClientResourceType.HSPEN);
	
	
	
	private static ActiveFiltersGrid instance=null;
	
	public ActiveFiltersGrid() {
		instance=this;
		setLayout(new FitLayout());
		setHeading(TITLE);
		
		
		operatorCombo.setForceSelection(false);  
        operatorCombo.setTriggerAction(TriggerAction.ALL);  
        operatorCombo.setEditable(false);
        operatorCombo.setAllowBlank(false);
        operatorCombo.setInEditor(true);
        CellEditor editor = new CellEditor(operatorCombo) {  
          @Override  
          public Object preProcessValue(Object value) { 
            if (value == null) {  
              return value;  
            }  
            try{
            	SimpleComboValue<String> found=instance.operatorCombo.findModel(AquaMapsSpeciesViewConstants.getOperatorLabel(ClientFilterOperator.valueOf(value+"")));
            	Log.debug("Found "+found.getValue());
            	return found;
            }catch(Exception e){
            	Log.error("",e);
            	return null;
            }
          }  
      
          @Override  
          public Object postProcessValue(Object value) {
            if (value == null) {  
              return value;  
            }else  
            return AquaMapsSpeciesViewConstants.getOperatorFromString((String) ((ModelData) value).get("value"))+"";  
          }  
        };  
        grid.getColumnModel().getColumnById(SpeciesFilter.OPERATOR).setEditor(editor);
		
        
        TextField<String> text = new TextField<String>();  
			text.setAllowBlank(false);
			text.setEmptyText("Type filter value here");
			text.setValidator(valueValidator);
			grid.getColumnModel().getColumnById(SpeciesFilter.FIELD_VALUE).setEditor(new CellEditor(text));
        
        
		grid.setStripeRows(true);
		
		
		GridView view=new GridView();
		view.setAutoFill(true);
		view.setEmptyText("No active filters");
		view.setForceFit(true);
		
		
		final RowEditor<SpeciesFilter> re = new RowEditor<SpeciesFilter>(){
			@Override
			public void stopEditing(boolean saveChanges) {
				Log.debug("Stop editing, save changes is "+saveChanges+", row is "+rowIndex);
				super.stopEditing(saveChanges);
				SpeciesFilter toEdit=grid.getStore().getAt(rowIndex);
				if(toEdit!=null){
				if(!saveChanges){
					if((toEdit.getType().equals(ClientFieldType.STRING))&&((String)toEdit.getValue()).equals("")){
						Log.debug("Removing dirty row..");
						grid.getStore().remove(rowIndex);
					}
				}else{
					try{
					Log.debug("Committing filters..");
					grid.getStore().commitChanges();
					AquaMapsSpeciesView.get().mainPanel.mask("Updating filters..");
					ArrayList<SpeciesFilter> filters=new ArrayList<SpeciesFilter>();
					filters.addAll(grid.getStore().getModels());
					AquaMapsSpeciesView.localService.setAdvancedSpeciesFilter(filters,PortletCommon.refreshSpeciesCallback);
					instance.updateSummary();
					}catch(Throwable t){
						Log.debug("Error!!!");
					}
				}				
				}
			}
			
			@Override
			protected void createButtons() {
				try{
				 btns = new ContentPanel() {
				      protected void createStyles(String baseStyle) {
				        baseStyle = "x-plain";
				        headerStyle = baseStyle + "-header";
				        headerTextStyle = baseStyle + "-header-text";
				        bwrapStyle = baseStyle + "-bwrap";
				        tbarStyle = baseStyle + "-tbar";
				        bodStyle = baseStyle + "-body";
				        bbarStyle = baseStyle + "-bbar";
				        footerStyle = baseStyle + "-footer";
				        collapseStyle = baseStyle + "-collapsed";
				      }
				    };

				    btns.setHeaderVisible(false);
				    btns.addStyleName("x-btns");
				    btns.setLayout(new TableLayout(2));

				    cancelBtn = new Button("Remove", new SelectionListener<ButtonEvent>() {
				      @Override
				      public void componentSelected(ButtonEvent ce) {
				    	  Log.debug("Delete row "+rowIndex);
				    		grid.getStore().remove(rowIndex);
				    		stopEditing(false);
				    		AquaMapsSpeciesView.get().mainPanel.mask("Updating filters..");
							ArrayList<SpeciesFilter> filters=new ArrayList<SpeciesFilter>();
							filters.addAll(grid.getStore().getModels());
							AquaMapsSpeciesView.localService.setAdvancedSpeciesFilter(filters,PortletCommon.refreshSpeciesCallback);
							instance.updateSummary();
				      }
				    });
				    cancelBtn.setMinWidth(getMinButtonWidth());
				    btns.add(cancelBtn);

				    saveBtn = new Button(getMessages().getSaveText(), new SelectionListener<ButtonEvent>() {
				      @Override
				      public void componentSelected(ButtonEvent ce) {
				        stopEditing(true);
				      }
				    });
				    saveBtn.setMinWidth(getMinButtonWidth());
				    btns.add(saveBtn);
				    
//				    Button deleteBtn=new Button("Remove",new SelectionListener<ButtonEvent>() {
//				    	public void componentSelected(ButtonEvent ce) {
//				    		Log.debug("Delete row "+rowIndex);
//				    		stopEditing(false);
//				    		grid.getStore().remove(rowIndex);
//				    	}
//				    });
//				    deleteBtn.setMinWidth(getMinButtonWidth());
//				    btns.add(deleteBtn);
				    
				    cancelBtn.getFocusSupport().setNextId(saveBtn.getId());
				    saveBtn.getFocusSupport().setPreviousId(cancelBtn.getId());

				    btns.render(getElement("bwrap"));
				    btns.layout();

				    btns.getElement().removeAttribute("tabindex");
				    btns.getFocusSupport().setIgnore(true);
				}catch(Throwable t){
					Log.error("Unexpected Error while creating buttons",t);
				}
			}
			
		};
		re.setErrorSummary(false);
		re.setClicksToEdit(EditorGrid.ClicksToEdit.ONE);
		re.addListener(Events.BeforeEdit, new Listener<RowEditorEvent>(){
			@Override
			public void handleEvent(RowEditorEvent be) {
				Log.debug("BeforeEdit, editing is "+re.isEditing());
				SpeciesFilter toEdit=grid.getStore().getAt(be.getRowIndex());
				Log.debug("TO EDIT : "+toEdit.getLabel()+", "+toEdit.getName()+", "+toEdit.getOperator()+", "+toEdit.getType()+", "+toEdit.getValue());
				   instance.operatorCombo.removeAll();
				   for(ClientFilterOperator op:AquaMapsSpeciesViewConstants.operatorsPerFieldType.get(toEdit.getType()))
					   instance.operatorCombo.add(AquaMapsSpeciesViewConstants.getOperatorLabel(op));
				   
				   valueValidator.setType(toEdit.getType());
				    
			}
		});
//		re.addListener(Events.AfterEdit, new Listener<RowEditorEvent>(){
//			@Override
//			public void handleEvent(RowEditorEvent be) {
////				Log.debug("AfterEdit");
//			}
//		});
//		filterStore.addStoreListener(new StoreListener<SpeciesFilter>(){
//
//			@Override
//			public void storeUpdate(StoreEvent<SpeciesFilter> se) {				
//				super.storeUpdate(se);
//				
//			}
//		});
		
//		filterStore.getLoader().addLoadListener(new LoadListener(){
//		@Override
//		public void loaderLoad(LoadEvent le) {
//			// TODO Auto-generated method stub
//			super.loaderLoad(le);
//			
//		}
//		});
		
		grid.setBorders(true);  
	    grid.addPlugin(re);  
		
		grid.setView(view);
		

		
		add(grid, new FitData(new Margins(0)));
		
		//************************* MENUStore
		
		SelectionListener<MenuEvent> addFilterListener=new SelectionListener<MenuEvent>() {  
			  
		      @Override  
		      public void componentSelected(MenuEvent ce) {
		    	  SpeciesFilterItem item=(SpeciesFilterItem) ce.getItem();
		    	  Log.debug("Clicked "+item.getText()+", "+item.getField()+", "+item.getCategory());
		    	  ClientFieldType type=AquaMapsSpeciesViewConstants.fieldTypes.get(item.getField());
		    	  ClientFilterOperator operator=AquaMapsSpeciesViewConstants.operatorsPerFieldType.get(type).get(0);
		    	  SpeciesFilter toAdd=new SpeciesFilter(item.getField()+"", item.getText(), AquaMapsSpeciesViewConstants.defaultFilterValues.get(type), operator, type);
		    	  re.stopEditing(false);  
		    	  filterStore.insert(toAdd, 0);  
		          re.startEditing(filterStore.indexOf(toAdd), true); 
		      }  
		  
		    };
		
		Menu addMenu=new Menu();
		Menu namesMenu=new Menu();
		for(SpeciesFields f: AquaMapsSpeciesViewConstants.nameSpeciesFields){
			SpeciesFilterItem item=new SpeciesFilterItem(AquaMapsSpeciesViewConstants.speciesFieldsNames.get(f), f, FilterCategory.NAME);
			item.addSelectionListener(addFilterListener);
			namesMenu.add(item);
		}
		MenuItem namesItem=new MenuItem("Name Filter");
		namesItem.setSubMenu(namesMenu);
		addMenu.add(namesItem);
		
		Menu codesMenu=new Menu();
		for(SpeciesFields f: AquaMapsSpeciesViewConstants.codesSpeciesFields){
			SpeciesFilterItem item=new SpeciesFilterItem(AquaMapsSpeciesViewConstants.speciesFieldsNames.get(f), f, FilterCategory.CODES);
			item.addSelectionListener(addFilterListener);
			codesMenu.add(item);
		}
		MenuItem codesItem=new MenuItem("Code Filter");
		codesItem.setSubMenu(codesMenu);
		addMenu.add(codesItem);
		
		Menu taxoMenu=new Menu();
		for(SpeciesFields f: AquaMapsSpeciesViewConstants.taxonomySpeciesFields){
			SpeciesFilterItem item=new SpeciesFilterItem(AquaMapsSpeciesViewConstants.speciesFieldsNames.get(f), f, FilterCategory.TAXONOMY);
			item.addSelectionListener(addFilterListener);
			taxoMenu.add(item);
		}
		MenuItem taxoItem=new MenuItem("Taxonomy Filter");
		taxoItem.setSubMenu(taxoMenu);
		addMenu.add(taxoItem);
		
		Menu charsMenu=new Menu();
		for(SpeciesFields f: AquaMapsSpeciesViewConstants.characteristicsSpeciesFields){
			SpeciesFilterItem item=new SpeciesFilterItem(AquaMapsSpeciesViewConstants.speciesFieldsNames.get(f), f, FilterCategory.CHARACTERISTICS);
			item.addSelectionListener(addFilterListener);
			charsMenu.add(item);
		}
		MenuItem charsItem=new MenuItem("Characteristic Filter");
		charsItem.setSubMenu(charsMenu);
		addMenu.add(charsItem);
		
		ToolBar toolBar = new ToolBar();  
		Button add = new Button("Add Filter");
		add.setMenu(addMenu);
		  
		
		toolBar.add(add); 
		
		toolBar.add(new SeparatorToolItem());  
		  
	    toolBar.add(new FillToolItem()); 
	    toolBar.add(new LabelField("Current "+AquaMapsSpeciesViewConstants.resourceNames.get(ClientResourceType.HSPEN)));
	    resourcePicker.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				AquaMapsSpeciesView.get().mainPanel.mask();
				ClientResource resource =new ClientResource(se.getSelectedItem().getProperties());					
				AquaMapsSpeciesView.localService.setSource(resource, PortletCommon.refreshSpeciesCallback);
			}
		});
	    
	    toolBar.add(resourcePicker); 
		
		
		setTopComponent(toolBar); 
	}
	
	public void setSelectedResource(ClientResource res){
		resourcePicker.setRawValue(res.getTitle());
	}
	public void setFilters(List<SpeciesFilter> filters){
		grid.getStore().removeAll();
		grid.getStore().add(filters);
	}
	
	private void updateSummary(){
		if(filterStore.getCount()>0){
			StringBuilder summaryBuilder=new StringBuilder();

			for(SpeciesFilter filter:filterStore.getModels()){
				summaryBuilder.append(filter.getLabel()+" "+AquaMapsSpeciesViewConstants.getOperatorLabel(filter.getOperator())+" "+filter.getValue()+";");
			}
			instance.setHeading(TITLE+" - current settings : "+summaryBuilder.toString());
		}else instance.setHeading(TITLE);
	}
	
	
	//******************************STATIC
	
	
	private static ListStore<SpeciesFilter> getFilterStore(){
		RpcProxy<List<SpeciesFilter>> proxy = new RpcProxy<List<SpeciesFilter>>() {  
		      @Override  
		      public void load(Object loadConfig, final AsyncCallback<List<SpeciesFilter>> callback) {  
		        AquaMapsSpeciesView.localService.getFilterSettings(new AsyncCallback<SpeciesSearchDescriptor>() {
		        	@Override
		        	public void onFailure(Throwable caught) {
		        		callback.onFailure(caught);
		        	}
		        	@Override
		        	public void onSuccess(SpeciesSearchDescriptor result) {
		        		callback.onSuccess(result.getAdvancedFilterList());
		        	};
				});
		      }  
		    };  
		    BeanModelReader reader = new BeanModelReader();  
		    
		    // loader and store  
		    ListLoader<ListLoadResult<SpeciesFilter>> loader = new BaseListLoader<ListLoadResult<SpeciesFilter>>(proxy, reader);  
		    ListStore<SpeciesFilter> store = new ListStore<SpeciesFilter>(loader);
		    
		   
		    return store;
	}
	
	private static ColumnModel getFilterColumnModel(){
		List<ColumnConfig> cols = new ArrayList<ColumnConfig>();  

		ColumnConfig fieldCol = new ColumnConfig(SpeciesFilter.FIELD_LABEL, "Field", 100);  
		cols.add(fieldCol);
		ColumnConfig operatorCol = new ColumnConfig(SpeciesFilter.OPERATOR, "Operator", 60);  
		cols.add(operatorCol);
		operatorCol.setRenderer(new GridCellRenderer<SpeciesFilter>() {
			@Override
			public Object render(SpeciesFilter model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<SpeciesFilter> store, Grid<SpeciesFilter> grid) {
				return AquaMapsSpeciesViewConstants.getOperatorLabel(model.getOperator());
			}
		});
		
		ColumnConfig valueCol = new ColumnConfig(SpeciesFilter.FIELD_VALUE, "Value", 100);  
		cols.add(valueCol);
		return new ColumnModel(cols);
	}
	
	
}
