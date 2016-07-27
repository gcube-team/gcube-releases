package org.gcube.application.aquamaps.aquamapsportlet.client;

import java.util.ArrayList;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.Tags;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SpeciesFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientObjectType;
import org.gcube.application.aquamaps.aquamapsportlet.client.details.SubmissionForm;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.Msg;
import org.gcube.application.aquamaps.aquamapsportlet.client.selections.ExtendedLiveGrid;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.UrlParam;
import com.gwtext.client.data.Record;
import com.gwtext.client.widgets.BoxComponent;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.ContainerListenerAdapter;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.form.NumberField;
import com.gwtext.client.widgets.form.event.FieldListenerAdapter;
import com.gwtext.client.widgets.form.event.TextFieldListenerAdapter;
import com.gwtext.client.widgets.grid.GridEditor;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.grid.event.GridCellListenerAdapter;
import com.gwtext.client.widgets.grid.event.RowSelectionListenerAdapter;
import com.gwtext.client.widgets.layout.ColumnLayout;
import com.gwtext.client.widgets.layout.ColumnLayoutData;

public class AquaMapsObjectsSettingsPanel extends TabPanel {

	
	ExtendedLiveGrid distributionGrid = new ExtendedLiveGrid("Species distribution",Stores.toCreateObjectsStore(ClientObjectType.SpeciesDistribution),ColumnDefinitions.toCreateObjectsColumnModel(ClientObjectType.SpeciesDistribution),false);
	ExtendedLiveGrid biodivGrid = new ExtendedLiveGrid("Objects",Stores.toCreateObjectsStore(ClientObjectType.Biodiversity),ColumnDefinitions.toCreateObjectsColumnModel(ClientObjectType.Biodiversity),false);
	ExtendedLiveGrid biodivBasket = new ExtendedLiveGrid("Object Associated Species",Stores.objectBasketStore(""),ColumnDefinitions.selectedSpeciesColumnModel(),false);
	Panel bioDivPanel=new Panel();
	static AquaMapsObjectsSettingsPanel instance=null;
	public static AquaMapsObjectsSettingsPanel get(){return instance;}
	
	AsyncCallback<Msg> updateCallback=new AsyncCallback<Msg>() {
		
		public void onSuccess(Msg result) {
			AquaMapsPortlet.get().hideLoading(instance.getId());
			instance.reload();
		}
		
		public void onFailure(Throwable caught) {
			AquaMapsPortlet.get().hideLoading(instance.getId());
			instance.reload();
		}
	};
	
	SubmissionForm submission= new SubmissionForm();
	ToolbarButton setBioGis=new ToolbarButton("Set gis");
	ToolbarButton setDisGis=new ToolbarButton("Set gis");
	
	public AquaMapsObjectsSettingsPanel() {
		instance=this;
		this.setFrame(true);
		
		
		bioDivPanel.setTitle("Biodiversity");
		bioDivPanel.setLayout(new ColumnLayout());
		bioDivPanel.add(biodivGrid,new ColumnLayoutData(.5));
		bioDivPanel.add(biodivBasket,new ColumnLayoutData(.5));
		bioDivPanel.addListener(new ContainerListenerAdapter(){
			@Override
			public void onResize(BoxComponent component, int adjWidth,
					int adjHeight, int rawWidth, int rawHeight) {				
//				super.onResize(component, adjWidth, adjHeight, rawWidth, rawHeight);
				biodivGrid.setSize(adjWidth, adjHeight);
				biodivBasket.setSize(adjWidth, adjHeight);
			}
		});
		
		
//		ToolbarButton addDist=new ToolbarButton("create", listener) 
		
		this.addListener(new ContainerListenerAdapter(){
			@Override
			public void onResize(BoxComponent component, int adjWidth,
					int adjHeight, int rawWidth, int rawHeight) {				
//				super.onResize(component, adjWidth, adjHeight, rawWidth, rawHeight);
				distributionGrid.setSize(adjWidth, adjHeight-80);
				bioDivPanel.setSize(adjWidth, adjHeight-40);
				
//				biodivGrid.setSize(adjWidth, adjHeight-80);
//				biodivBasket.setSize(adjWidth, adjHeight-80);
			}
		});
		biodivGrid.getSelectionModel().addListener(new RowSelectionListenerAdapter(){
			@Override
			public void onRowSelect(RowSelectionModel sm, int rowIndex,
					Record record) {
				AquaMapsPortlet.get().showLoading("Refreshing object basket..", instance.getId());
				UrlParam[] pars=biodivBasket.getStore().getBaseParams();
				for(int i=0;i<pars.length;i++){
					if(pars[i].getName().equalsIgnoreCase(Tags.AQUAMAPS_TITLE))
						pars[i]=new UrlParam(Tags.AQUAMAPS_TITLE,record.getAsString("title"));
				}
				biodivBasket.setTitle("Object Associated Species , related object : "+record.getAsString("title"));
				biodivBasket.getStore().setBaseParams(pars);
				biodivBasket.getStore().reload();
				AquaMapsPortlet.get().hideLoading(instance.getId());
			}
			@Override
			public void onSelectionChange(RowSelectionModel sm) {
				if(!sm.hasSelection()){
					AquaMapsPortlet.get().showLoading("Refreshing object basket..", instance.getId());
					UrlParam[] pars=biodivBasket.getStore().getBaseParams();
					for(int i=0;i<pars.length;i++){
						if(pars[i].getName().equalsIgnoreCase(Tags.AQUAMAPS_TITLE))
							pars[i]=new UrlParam(Tags.AQUAMAPS_TITLE,"");
					}
					biodivBasket.setTitle("Associated Species , no Biodiversity object selected");
					biodivBasket.getStore().setBaseParams(pars);
					biodivBasket.getStore().reload();
					AquaMapsPortlet.get().hideLoading(instance.getId());
					setBioGis.disable();					
					AquaMapsPortlet.get().advGeneration.addToBioButton.disable();
				}else{
					AquaMapsPortlet.get().advGeneration.addToBioButton.enable();
					setBioGis.enable();
				}
			}
		});
		
		distributionGrid.getSelectionModel().addListener(new RowSelectionListenerAdapter(){
			@Override
			public void onSelectionChange(RowSelectionModel sm) {
				if(sm.hasSelection()){
					setDisGis.enable();					
				}else{
					setDisGis.disable();
				}
			}
		});
		
		distributionGrid.setRemover(new ButtonListenerAdapter(){
			@Override
			public void onClick(Button button, EventObject e) {				
				AquaMapsPortlet.get().showLoading("Deleting selected..", instance.getId());
				String title=null;
				if(!distributionGrid.useAllButton.isPressed())
					title=distributionGrid.getSelectionModel().getSelected().getAsString("title");				
				AquaMapsPortlet.localService.removeObject(title, ClientObjectType.SpeciesDistribution,updateCallback);
			}
		});
		biodivGrid.setRemover(new ButtonListenerAdapter(){			
			public void onClick(Button button, EventObject e) {
				AquaMapsPortlet.get().showLoading("Deleting selected..", instance.getId());
				String title=null;
				if(!biodivGrid.useAllButton.isPressed())
					title=biodivGrid.getSelectionModel().getSelected().getAsString("title");				
				AquaMapsPortlet.localService.removeObject(title, ClientObjectType.Biodiversity,updateCallback);
			}
		});
		biodivBasket.setRemover(new ButtonListenerAdapter(){
			@Override
			public void onClick(Button button, EventObject e) {
				AquaMapsPortlet.get().showLoading("Removing selected..", instance.getId());
				String title=biodivGrid.getSelectionModel().getSelected().getAsString("title");
				if(biodivBasket.useAllButton.isPressed()){					
					AquaMapsPortlet.localService.removeSelectionFromBasket(title,null, updateCallback);
				}else{
					ArrayList<String> ids= new ArrayList<String>();
					for(Record rec: biodivBasket.getSelectionModel().getSelections()){
						ids.add(rec.getAsString(SpeciesFields.speciesid+""));
					}					
					AquaMapsPortlet.localService.removeSelectionFromBasket(title,ids, updateCallback);
				}
			}
		});
		
		
		bioDivPanel.addListener(new PanelListenerAdapter(){
			@Override
			public void onActivate(Panel panel) {
				biodivGrid.getStore().reload();				
			}
			@Override
			public void onShow(Component component) {
				biodivGrid.getStore().reload();
			}
		});		
		distributionGrid.addListener(new PanelListenerAdapter(){
			@Override
			public void onActivate(Panel panel) {
				distributionGrid.getStore().reload();
				AquaMapsPortlet.get().advGeneration.addToBioButton.disable();
			}
		});
		
		
		final NumberField thresholdField=new NumberField();
		thresholdField.addListener(new TextFieldListenerAdapter(){
			@Override
			public void onStateSave(Component component, JavaScriptObject state) {
				Record rec = biodivGrid.getSelectionModel().getSelected();
				editObject(rec.getAsString("title"), rec.getAsString("title"), ClientObjectType.Biodiversity, rec.getAsString("bbox"), rec.getAsFloat("threshold"), rec.getAsBoolean("gis"));
			}
		});
		thresholdField.setMaxValue(1);
		thresholdField.setMinValue(0);		
		thresholdField.setAllowBlank(false);
		GridEditor thresholdEditor=new GridEditor(thresholdField);
		//Editors
		biodivGrid.getColumnModel().setEditor("threshold", thresholdEditor);
		biodivGrid.getColumnModel().setEditable("threshold", true);
		
		
		distributionGrid.addGridCellListener(new GridCellListenerAdapter(){			
			public void onCellClick(GridPanel grid, int rowIndex, int colindex,
					EventObject e) {				
				if (grid.getColumnModel().getDataIndex(colindex).equals("gis") &&  
						e.getTarget(".checkbox", 1) != null) {  
					Record rec = distributionGrid.getSelectionModel().getSelected();
					editObject(rec.getAsString("title"), rec.getAsString("title"), ClientObjectType.SpeciesDistribution, rec.getAsString("bbox"), 0.5f, !rec.getAsBoolean("gis"));
				}  
			}  
			@Override
			public void onCellDblClick(GridPanel grid, int rowIndex,
					int colIndex, EventObject e) {
				Record rec = distributionGrid.getSelectionModel().getSelected();
				ObjectEditing popup= new ObjectEditing(rec.getAsString("title"), rec.getAsString("type"), rec.getAsString("bbox"), rec.getAsFloat("threshold"), rec.getAsBoolean("gis"));
//				AquaMapsPortlet.get().showLoading("Close edit form to unmask",instance.getId());
				popup.show();
			}
		});
		biodivGrid.addGridCellListener(new GridCellListenerAdapter(){			
			public void onCellClick(GridPanel grid, int rowIndex, int colindex,
					EventObject e) {				
				if (grid.getColumnModel().getDataIndex(colindex).equals("gis") &&  
						e.getTarget(".checkbox", 1) != null) {  
					Record rec = biodivGrid.getSelectionModel().getSelected();
					editObject(rec.getAsString("title"), rec.getAsString("title"), ClientObjectType.Biodiversity, rec.getAsString("bbox"), rec.getAsFloat("threshold"), !rec.getAsBoolean("gis"));
				}
			}
			@Override
			public void onCellDblClick(GridPanel grid, int rowIndex,
					int colIndex, EventObject e) {
				Record rec = biodivGrid.getSelectionModel().getSelected();
				ObjectEditing popup= new ObjectEditing(rec.getAsString("title"), rec.getAsString("type"), rec.getAsString("bbox"), rec.getAsFloat("threshold"), rec.getAsBoolean("gis"));
//				AquaMapsPortlet.get().showLoading("Close edit form to unmask",instance.getId());
				popup.show();
			}
		});
		
		 
		biodivGrid.getBottomToolbar().addButton(setBioGis);
		setBioGis.addListener(new ButtonListenerAdapter(){
			@Override
			public void onClick(Button button, EventObject e) {
				AquaMapsPortlet.get().showLoading("Updating", instance.getId());
				if(biodivGrid.useAllButton.isPressed()){					
					AquaMapsPortlet.localService.changeGisByType(ClientObjectType.Biodiversity.toString(), updateCallback);
				}else{
					ArrayList<String> ids= new ArrayList<String>();
					for(Record r: biodivGrid.getSelectionModel().getSelections()) ids.add(r.getAsString("title"));
					AquaMapsPortlet.localService.changeGisById(ids, updateCallback);
				}
			}
		});
		setBioGis.disable();
		
		distributionGrid.getBottomToolbar().addButton(setDisGis);
		setDisGis.addListener(new ButtonListenerAdapter(){
			@Override
			public void onClick(Button button, EventObject e) {
				AquaMapsPortlet.get().showLoading("Updating", instance.getId());
				if(distributionGrid.useAllButton.isPressed()){					
					AquaMapsPortlet.localService.changeGisByType(ClientObjectType.SpeciesDistribution.toString(), updateCallback);
				}else{
					ArrayList<String> ids= new ArrayList<String>();
					for(Record r: distributionGrid.getSelectionModel().getSelections()) ids.add(r.getAsString("title"));
					AquaMapsPortlet.localService.changeGisById(ids, updateCallback);
				}
			}
		});
		setDisGis.disable();
		
		this.add(bioDivPanel);
		this.add(distributionGrid);
		this.add(submission);
	}	
	
	public void editObject(String oldTitle, String title, ClientObjectType type, String bbox,Float threshold,boolean gis){		
		AquaMapsPortlet.get().showLoading("Changing settings...", instance.getId());
		AquaMapsPortlet.localService.updateObject(oldTitle, title, type, bbox, threshold, gis,updateCallback);
	}
	
	FieldListenerAdapter distributiontextSaveing=new FieldListenerAdapter(){
		public void onStateSave(Component component, JavaScriptObject state) {
//			if(((TextField)component)){}
			Record rec = distributionGrid.getSelectionModel().getSelected();
			editObject(rec.getAsString("title"), rec.getAsString("title"), ClientObjectType.SpeciesDistribution, rec.getAsString("bbox"), 0.5f, rec.getAsBoolean("gis"));
		};
	};
	
	public void reload(){
		if(instance.getActiveTab().getId().equalsIgnoreCase(distributionGrid.getId())){
			distributionGrid.getStore().reload();
			}else if(instance.getActiveTab().getId().equalsIgnoreCase(bioDivPanel.getId())){
			biodivGrid.getStore().reload();
			}else{
				submission.reload();
			}
	}
}
