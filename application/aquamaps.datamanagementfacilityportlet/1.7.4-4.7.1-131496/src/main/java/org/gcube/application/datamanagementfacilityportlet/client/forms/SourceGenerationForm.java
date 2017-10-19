package org.gcube.application.datamanagementfacilityportlet.client.forms;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.datamanagementfacilityportlet.client.DataManagementFacility;
import org.gcube.application.datamanagementfacilityportlet.client.DataManagementFacilityConstants;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.Algorithm;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.ExecutionEnvironmentModel;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.GroupGenerationRequest;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.ClientLogicType;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.form.DualListField;
import com.extjs.gxt.ui.client.widget.form.DualListField.Mode;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HtmlEditor;
import com.extjs.gxt.ui.client.widget.form.ListField;
import com.extjs.gxt.ui.client.widget.form.SpinnerField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class SourceGenerationForm extends TabPanel{

	final TextField<String> titleField = new TextField<String>(); 
	final HtmlEditor htmlDescription = new HtmlEditor();  
	final DualListField<Algorithm> lists = new DualListField<Algorithm>();
	
	protected static int DEFAULT_SOURCE_SELECTION_TAB_POSITION=2;
	
	EditorGrid<ExecutionEnvironmentModel> grid ;
	
	final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {  
		public void handleEvent(MessageBoxEvent ce) {  
			instance.unmask();  
		}  
	};
	
	
	protected SourceGenerationForm instance =this;
	
	
	//FORMS
	FormPanel generationSettingsForm=new FormPanel();
	FormPanel generalDetailsForm = new FormPanel();  

	FormPanel executionEnvironmentForm= new FormPanel();
	
	public SourceGenerationForm() {
		//*************** TAB 1

		TabItem generalDetailsTab=new TabItem("General details");  

		
		generalDetailsForm.setBorders(false);  
		generalDetailsForm.setBodyBorder(false);  
		generalDetailsForm.setLabelWidth(100);  
		generalDetailsForm.setPadding(5);  
		generalDetailsForm.setHeaderVisible(false); 
		generalDetailsForm.setFrame(true);
		generalDetailsForm.setHeight(height);

		titleField.setFieldLabel("Request Title");  
		titleField.setAllowBlank(false);
		titleField.setData("text", "Enter a title for the execution.");
		titleField.addPlugin(Common.plugin);
		generalDetailsForm.add(titleField, new FormData("100%"));


		htmlDescription.setHideLabel(true);
		htmlDescription.setFieldLabel("Description");
		htmlDescription.setHeight(200);
		htmlDescription.setData("text", "Enter a description for the execution.");
		htmlDescription.addPlugin(Common.plugin);
		generalDetailsForm.add(htmlDescription, new FormData("100%"));  

		generalDetailsTab.add(generalDetailsForm);
		add(generalDetailsTab);
		
		
		//*************** TAB 2


		TabItem algorithmSelectionTab=new TabItem("Generation Settings");
		
		generationSettingsForm.setBorders(false);  
		generationSettingsForm.setBodyBorder(false);  
		generationSettingsForm.setLabelWidth(100);  
		generationSettingsForm.setPadding(5);  
		generationSettingsForm.setHeaderVisible(false); 
		generationSettingsForm.setFrame(true);
		generationSettingsForm.setHeight(height);
		lists.setMode(Mode.INSERT);  
		lists.setFieldLabel("Algorithms");
		lists.setData("text", "Select which algorithm(s) to use.");
		lists.addPlugin(Common.plugin);
		ListField<Algorithm> from = lists.getFromList();
		from.setDisplayField(Algorithm.LABEL);  
		ListStore<Algorithm> store = new ListStore<Algorithm>();  
		store.add(DataManagementFacilityConstants.logicAlgorithmMap.get(getLogic()));  
		from.setStore(store);  
		ListField<Algorithm> to = lists.getToList();  
		to.setDisplayField(Algorithm.LABEL);
		store = new ListStore<Algorithm>();  
		to.setStore(store);

		generationSettingsForm.add(lists,new FormData("100%"));
		
		
		algorithmSelectionTab.add(generationSettingsForm);
		add(algorithmSelectionTab);
		
		
		///******* TAB 3
		// SUPPRESSED AND ADDED Facility to add various tabs

//		TabItem sourcesSelectionTab = new TabItem("Sources Selection");
//		
//		sourcesSelectionForm.setBorders(false);  
//		sourcesSelectionForm.setBodyBorder(false);  
//		sourcesSelectionForm.setLabelWidth(100);  
//		sourcesSelectionForm.setPadding(5);  
//		sourcesSelectionForm.setHeaderVisible(false); 
//		sourcesSelectionForm.setFrame(true);
//		sourcesSelectionForm.setHeight(height);
//
//		
//		sourcesSelectionTab.add(sourcesSelectionForm);
//		add(sourcesSelectionTab);
		
		
		//************ TAB 4

		TabItem executionEnvironmentTab = new TabItem("Execution Environment");
		
		executionEnvironmentForm.setBorders(false);  
		executionEnvironmentForm.setBodyBorder(false);  
		executionEnvironmentForm.setLabelWidth(100);  
		executionEnvironmentForm.setPadding(5);  
		executionEnvironmentForm.setHeaderVisible(false); 
		executionEnvironmentForm.setFrame(true);
		executionEnvironmentForm.setHeight(450);


		final GroupingStore<ExecutionEnvironmentModel> gridStore = new GroupingStore<ExecutionEnvironmentModel>();  

		gridStore.groupBy(ExecutionEnvironmentModel.INFRASTRUCTURE);  

		XTemplate tpl = XTemplate.create("<p><b>Description:</b> {"+ExecutionEnvironmentModel.DESCRIPTION+"}</p><br><p><b>Disclaimer:</b> {"+ExecutionEnvironmentModel.DISCLAIMER+"}</p>");  
		final RowExpander expander = new RowExpander();  
		expander.setTemplate(tpl);  


		final CheckBoxSelectionModel<ExecutionEnvironmentModel> sm = new CheckBoxSelectionModel<ExecutionEnvironmentModel>();  
		sm.setSelectionMode(SelectionMode.SINGLE);




		ColumnConfig backend = new ColumnConfig(ExecutionEnvironmentModel.ENVIRONMENT_NAME+"",100);  
		ColumnConfig minPartitionsColumn = new ColumnConfig(ExecutionEnvironmentModel.MIN_PARTITIONS+"","Min Resources",80);
		
		ColumnConfig actualPartitionsColumn = new ColumnConfig(ExecutionEnvironmentModel.DEFAULT_PARTITIONS+"","Resources", 80);
		
		ColumnConfig maxPartitionsColumn = new ColumnConfig(ExecutionEnvironmentModel.MAX_PARTITIONS+"","Max Resources",80);  


		final SpinnerField spinnerField = new SpinnerField();  
		spinnerField.setIncrement(1);  
		spinnerField.getPropertyEditor().setType(Integer.class);  
		spinnerField.setMinValue(1);  
		spinnerField.setMaxValue(20);  

		CellEditor resourceEditor= new CellEditor(spinnerField){};

		actualPartitionsColumn.setEditor(resourceEditor);
		resourceEditor.setCancelOnEsc(true);
		
		GridCellRenderer<ExecutionEnvironmentModel> cellRenderer=new GridCellRenderer<ExecutionEnvironmentModel>() {  
			public String render(ExecutionEnvironmentModel model, String property, ColumnData config, int rowIndex, int colIndex,  
					ListStore<ExecutionEnvironmentModel> store, Grid<ExecutionEnvironmentModel> grid) {  
				int val = model.getDefaultPartitions();  
				String style = (val > model.getMaxPartitions())||(val<model.getMinPartitions()) ? "red" : "green";  
				return "<span style='color:" + style + "'>" + val + "</span>";  
			}  
		}; 

		actualPartitionsColumn.setRenderer(cellRenderer);
		
		List<ColumnConfig> config = new ArrayList<ColumnConfig>();  
		config.add(sm.getColumn());
		config.add(expander);
		config.add(backend); 
		config.add(minPartitionsColumn);
		config.add(actualPartitionsColumn);  
		config.add(maxPartitionsColumn);





		final ColumnModel cm = new ColumnModel(config);  

		GroupingView view = new GroupingView();  
		view.setShowGroupedColumn(false);  
		view.setForceFit(true);  
		

		grid= new EditorGrid<ExecutionEnvironmentModel>(gridStore, cm);
		grid.setSelectionModel(sm);
		grid.setView(view);  
		grid.setBorders(true);  
		grid.setAutoExpandColumn(ExecutionEnvironmentModel.ENVIRONMENT_NAME+"");
		grid.setHeight(200);
		grid.addPlugin(expander);
		grid.addPlugin(sm);
		grid.setStripeRows(true);
		


		executionEnvironmentTab.addListener(Events.Select, new Listener<TabPanelEvent>(){

			public void handleEvent(TabPanelEvent be) {
				instance.mask("Loading..");
				DataManagementFacility.localService.getEnvironments(getLogic(),new AsyncCallback<ArrayList<ExecutionEnvironmentModel>>() {

					public void onFailure(Throwable arg0) {
						MessageBox.alert("Execution Environments", arg0.getMessage(), l);
						Log.error("Unable to retrieve environments",arg0);
					}

					public void onSuccess(ArrayList<ExecutionEnvironmentModel> arg0) {
						try{
							Log.debug("Cleaning environemnts..");
							gridStore.removeAll();
							Log.debug("Found "+arg0.size()+" environments");
							gridStore.add(arg0);
							instance.unmask();
						}catch(Exception e){
							Log.error("Unable to load environemnts into grid",e);
							MessageBox.alert("Execution Environments", "Sorry, an error occurred. Please reload grid", l);
						}
					}

				});
			}

		});



		executionEnvironmentForm.add(grid,new FormData("100%"));
		executionEnvironmentTab.add(executionEnvironmentForm);
		add(executionEnvironmentTab);
	}
	
	
	protected abstract ClientLogicType getLogic();
	
	
	public abstract GroupGenerationRequest getSettings();
	
	protected void addSourceTab(ContentPanel content, int index){
		content.setBorders(false);  
		content.setBodyBorder(false);  
		if(content instanceof FormPanel){
			Log.debug("Passed FormPanel");
			((FormPanel)content).setLabelWidth(100);
			((FormPanel)content).setPadding(5);  
		}
		content.setHeaderVisible(false); 
		content.setFrame(true);
		content.setHeight(height);
		TabItem newTab = new TabItem(content.getHeading());
		newTab.add(content);
		insert(newTab,index);
//		newTab.setTabIndex(index);
//		this.adjustIndex(newTab, beforeIndex);
	}
}
