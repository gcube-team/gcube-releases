package org.gcube.application.aquamaps.aquamapsportlet.client.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.application.aquamaps.aquamapsportlet.client.AquaMapsPortlet;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.AquaMapsPortletCostants;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.EnvelopeFieldsClient;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientFieldType;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientFilterType;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientEnvelope;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientField;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientFilter;

import com.google.gwt.core.client.GWT;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.BooleanFieldDef;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.EditorGridPanel;
import com.gwtext.client.widgets.grid.GridEditor;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.event.EditorGridListenerAdapter;
import com.gwtext.client.widgets.grid.event.GridCellListenerAdapter;
import com.gwtext.client.widgets.layout.FitLayout;

public class MinMaxGrid extends CustomFieldSet {

	EditorGridPanel grid; 
	RecordDef recordDef=new RecordDef(
			new FieldDef[]{
					new StringFieldDef("attribute"),
					new IntegerFieldDef("min"),
					new IntegerFieldDef("max"),
					new BooleanFieldDef("enabled") 
			});

	static Map<String,ClientField> fields=new HashMap<String, ClientField>();	
	Store store= new Store(recordDef);
	String summaryFilterType=FilterSummary.MinMax;
	ColumnModel colModel=new ColumnModel(new BaseColumnConfig[]{
			new ColumnConfig("Enabled","enabled",50,false, new Renderer() {  
				public String render(Object value, CellMetadata cellMetadata, Record record,  
						int rowIndex, int colNum, Store store) {  
					boolean checked = ((Boolean) value).booleanValue();  
					return "<img class=\"checkbox\" src=\""+GWT.getModuleBaseURL() +"../js/ext/resources/images/default/menu/" +  
					(checked ? "checked.gif" : "unchecked.gif") + "\"/>";   
				}  
			},"enabled"),
			new ColumnConfig("Attribute", "attribute", 90, false, null, "attribute"),
			new ColumnConfig("Min", "min", 70, false, null, "min"),
			new ColumnConfig("Max", "max", 70, false, null, "max")			
	});



	static{
		ClientField depthField=new ClientField();
		depthField.setName(EnvelopeFieldsClient.Depth+"");
		depthField.setType(ClientFieldType.DOUBLE);
		ClientField tempField=new ClientField();
		tempField.setName(EnvelopeFieldsClient.Temperature+"");
		tempField.setType(ClientFieldType.DOUBLE);
		ClientField salinityField=new ClientField();
		salinityField.setName(EnvelopeFieldsClient.Salinity+"");
		salinityField.setType(ClientFieldType.DOUBLE);
		ClientField iceConField=new ClientField();
		iceConField.setName(EnvelopeFieldsClient.IceConcentration+"");
		iceConField.setType(ClientFieldType.DOUBLE);
		ClientField primProdField=new ClientField();
		primProdField.setName(EnvelopeFieldsClient.PrimaryProduction+"");
		primProdField.setType(ClientFieldType.DOUBLE);
		fields.put(depthField.getName(), depthField);
		fields.put(tempField.getName(), tempField);
		fields.put(salinityField.getName(), salinityField);
		fields.put(iceConField.getName(), iceConField);
		fields.put(primProdField.getName(), primProdField);

	}

	public MinMaxGrid(String title) {
		super();
		this.setTitle(title);		
		this.setWidth(AquaMapsPortletCostants.Filter_Container_Width);
		this.setLayout(new FitLayout());
		this.setCollapsible(true);
		this.collapse();


		for(String fieldName:fields.keySet()){			
			store.add(recordDef.createRecord(new Object[]{fieldName,0,0,false}));			
		}
		TextField minValueField=new TextField();
		minValueField.setAllowBlank(false);
		minValueField.setBlankText("0");

		TextField maxValueField=new TextField();
		maxValueField.setAllowBlank(false);
		maxValueField.setBlankText("0");

		colModel.setEditor("min",new GridEditor( minValueField));
		colModel.setEditor("max",new GridEditor( maxValueField));
			
		colModel.setEditable(1, false);
		grid=new EditorGridPanel(store,colModel);
		//grid.setWidth(AquaMapsPortletCostants.FILTER_WIDTH);
		/*GridView view = new GridView();
		view.setAutoFill(true);
		view.setForceFit(true);	
		grid.setView(view);*/
		grid.setStripeRows(true);
		grid.addGridCellListener(new GridCellListenerAdapter(){			
			public void onCellClick(GridPanel grid, int rowIndex, int colindex,
					EventObject e) {				
				if (grid.getColumnModel().getDataIndex(colindex).equals("enabled") &&  
						e.getTarget(".checkbox", 1) != null) {  
					Record record = grid.getStore().getAt(rowIndex);  
					record.set("enabled", !record.getAsBoolean("enabled"));  
					updateSummary();
				}  
			}  
		});
		grid.setClicksToEdit(1);
		grid.setFrame(true);		
		grid.addEditorGridListener(new EditorGridListenerAdapter(){			
			public boolean doValidateEdit(GridPanel grid, Record record,
					String field, Object value, Object originalValue,
					int rowIndex, int colIndex) {	
				try{
					Integer.parseInt(value.toString());
					return true;
				}catch(NumberFormatException e){
					AquaMapsPortlet.get().showMessage("You must insert a valid number");
					return false;
				}				
			}
			
			public void onAfterEdit(GridPanel grid, Record record,
					String field, Object newValue, Object oldValue,
					int rowIndex, int colIndex) {			
				super.onAfterEdit(grid, record, field, newValue, oldValue, rowIndex, colIndex);
				updateSummary();
			}
		});

		this.add(grid);
	}

	public List<ClientFilter> getFilter()throws Exception{
		List<ClientFilter> toReturn=new ArrayList<ClientFilter>();
		for(Record record: store.getRecords()){			
			if(record.getAsBoolean("enabled")){				
				ClientField field =fields.get(record.getAsString("attribute"));
				ClientField min=new ClientField();
				min.setName(ClientEnvelope.getMinName(EnvelopeFieldsClient.valueOf(field.getName()))+"");
				min.setType(field.getType());
				min.setValue(String.valueOf(record.getAsInteger("min")));
				ClientFilter minFilter=new ClientFilter();
				minFilter.setField(min);
				minFilter.setType(ClientFilterType.greater_then);
				toReturn.add(minFilter);
				ClientField max=new ClientField();
				max.setName(ClientEnvelope.getMaxName(EnvelopeFieldsClient.valueOf(field.getName()))+"");
				max.setType(field.getType());
				max.setValue(String.valueOf(record.getAsInteger("max")));
				ClientFilter maxFilter=new ClientFilter();
				maxFilter.setField(min);
				maxFilter.setType(ClientFilterType.smaller_then);
				toReturn.add(maxFilter);
				if(Integer.parseInt(min.getValue())>Integer.parseInt(max.getValue())) throw new Exception("Incorrect range bounds for attribute "+field.getName());		
			}
		}		
		return toReturn;
	}

	public void reset(){
		store.removeAll();
		for(String fieldName:fields.keySet()){			
			store.add(recordDef.createRecord(new Object[]{fieldName,0,0,false}));			
		}
		updateSummary();
	}
	
	public void updateSummary(){
		for(Record record: store.getRecords())
			if(record.getAsBoolean("enabled")){
				AquaMapsPortlet.get().species.filter.filterDetails.addFilter(summaryFilterType, record.getAsString("attribute")+"Min", "=>", record.getAsString("min"));
				AquaMapsPortlet.get().species.filter.filterDetails.addFilter(summaryFilterType, record.getAsString("attribute")+"Max", "=<", record.getAsString("max"));
			}
			else {
				AquaMapsPortlet.get().species.filter.filterDetails.removeFilter(summaryFilterType, record.getAsString("attribute")+"Min");
				AquaMapsPortlet.get().species.filter.filterDetails.removeFilter(summaryFilterType, record.getAsString("attribute")+"Max");
			}
	}
}
