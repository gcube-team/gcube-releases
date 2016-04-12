package org.gcube.application.aquamaps.aquamapsportlet.client.filters;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsportlet.client.AquaMapsPortlet;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.AquaMapsPortletCostants;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientFieldType;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientFilterType;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientField;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientFilter;

import com.google.gwt.core.client.GWT;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.BooleanFieldDef;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.SimpleStore;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.event.FieldListenerAdapter;
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

public class SearchByKeywordsGrid extends EditorGridPanel {


	RecordDef recordDef=new RecordDef(
			new FieldDef[]{
					new StringFieldDef("attribute"),
					new StringFieldDef("type"),
					new StringFieldDef("value"),
					new BooleanFieldDef("enabled")
			});


	Store store= new Store(recordDef);
	
	String summaryFilterType=null;
	
	
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
			new ColumnConfig("Method", "type", 50, false, null, "method"),
			new ColumnConfig("Value", "value", 95, false, null, "value"),

	});



	public SearchByKeywordsGrid(String title,String [] fields,final boolean isCodeFilter) {
		super();
		//this.setTitle(title);		
		this.setWidth(AquaMapsPortletCostants.FILTER_WIDTH);	
		this.setFrame(true);
		summaryFilterType=(isCodeFilter)?FilterSummary.CodeType:FilterSummary.NameType;
		for(String field:fields){			
			store.add(recordDef.createRecord(new Object[]{
					field,
					ClientFilterType.is.toString(),
					AquaMapsPortletCostants.EMPTY_TEXT,
					false}));			
		}
		SimpleStore cbStore = new SimpleStore("method", new String[]{  
				ClientFilterType.is.toString(),  
				ClientFilterType.contains.toString(),  
				ClientFilterType.begins.toString(),  
				ClientFilterType.ends.toString(),
		});  
		cbStore.load();  

		final ComboBox cb = new ComboBox();  
		cb.setDisplayField("method");  
		cb.setStore(cbStore);  
		cb.setAllowBlank(false);
		cb.setEditable(false);
		colModel.setEditor("method",new GridEditor( cb));	

		TextField valueField=new TextField();
		valueField.setAllowBlank(false);
		valueField.setBlankText(AquaMapsPortletCostants.EMPTY_TEXT);
		valueField.addListener(new FieldListenerAdapter(){			
			public void onFocus(com.gwtext.client.widgets.form.Field field) {
				field.setValue("");
			}
		});


		colModel.setEditor("value",new GridEditor( valueField));

			
		colModel.setEditable("attribute", false);


		
		this.setStore(store);
		this.setColumnModel(colModel);	
		/*GridView view = new GridView();
		view.setAutoFill(true);
		view.setForceFit(true);	
		this.setView(view);*/
		this.setStripeRows(true);
		this.addGridCellListener(new GridCellListenerAdapter(){			
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
		this.setClicksToEdit(1);
		this.addEditorGridListener(new EditorGridListenerAdapter(){
			
			public void onAfterEdit(GridPanel grid, Record record,
					String field, Object newValue, Object oldValue,
					int rowIndex, int colIndex) {				
				super.onAfterEdit(grid, record, field, newValue, oldValue, rowIndex, colIndex);
				updateSummary();
			}
		});
	}

	public List<ClientFilter> getFilter(){
		List<ClientFilter> toReturn=new ArrayList<ClientFilter>();
		for(Record record: store.getRecords())
			if(record.getAsBoolean("enabled")){
				ClientField field =new ClientField();
				field.setName(record.getAsString("attribute"));
				field.setType(ClientFieldType.STRING);
				field.setValue(record.getAsString("value"));
				ClientFilter filter=new ClientFilter();
				filter.setField(field);
				filter.setType(ClientFilterType.valueOf(record.getAsString("type")));
				toReturn.add(filter);
			}		
		return toReturn;
	}

	public void reset(){
		for(Record record : store.getRecords()){
			record.set("enabled", false);
			record.set("value", AquaMapsPortletCostants.EMPTY_TEXT);
			updateSummary();
		}
	}

	public void updateSummary(){
		for(Record record: store.getRecords())
			if(record.getAsBoolean("enabled")){
				AquaMapsPortlet.get().species.filter.filterDetails.addFilter(summaryFilterType, record.getAsString("attribute"), record.getAsString("type"), record.getAsString("value"));					
			}
			else AquaMapsPortlet.get().species.filter.filterDetails.removeFilter(summaryFilterType, record.getAsString("attribute"));
	}

}
