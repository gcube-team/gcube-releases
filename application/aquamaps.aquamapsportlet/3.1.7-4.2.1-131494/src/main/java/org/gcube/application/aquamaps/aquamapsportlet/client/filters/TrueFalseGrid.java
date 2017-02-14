package org.gcube.application.aquamaps.aquamapsportlet.client.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.application.aquamaps.aquamapsportlet.client.AquaMapsPortlet;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.AquaMapsPortletCostants;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SpeciesFields;
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
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.EditorGridPanel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.event.GridCellListenerAdapter;
import com.gwtext.client.widgets.layout.FitLayout;

public class TrueFalseGrid extends CustomFieldSet {
	
	EditorGridPanel grid; 
	RecordDef recordDef=new RecordDef(
			new FieldDef[]{				
					new BooleanFieldDef("enabled"),
					new StringFieldDef("attribute"),
					new BooleanFieldDef("value"),
					});	
	static Map<String,ClientField> fields=new HashMap<String, ClientField>();
	String summaryFilterType=FilterSummary.additional;
	Store store= new Store(recordDef);
	ColumnModel colModel=new ColumnModel(new BaseColumnConfig[]{
			new ColumnConfig("Enabled","enabled",70,false, new Renderer() {  
				public String render(Object value, CellMetadata cellMetadata, Record record,  
						int rowIndex, int colNum, Store store) {  
					boolean checked = ((Boolean) value).booleanValue();  
					return "<img class=\"checkbox\" src=\""+GWT.getModuleBaseURL() +"../js/ext/resources/images/default/menu/" +  
					(checked ? "checked.gif" : "unchecked.gif") + "\"/>";  
				}  
			},"enabled"),
			new ColumnConfig("Attribute", "attribute", 90, false, null, "attribute"),
			new ColumnConfig("Value", "value", 100, false, null, "value"),
			
	});
	
	
	static{
		ClientField pelagicField=new ClientField();
		pelagicField.setName(SpeciesFields.pelagic+"");
		pelagicField.setType(ClientFieldType.BOOLEAN);
		ClientField deepwaterField=new ClientField();
		deepwaterField.setName(SpeciesFields.deepwater+"");
		deepwaterField.setType(ClientFieldType.BOOLEAN);
		ClientField anglingField=new ClientField();
		anglingField.setName(SpeciesFields.angling+"");
		anglingField.setType(ClientFieldType.BOOLEAN);
		ClientField divingField=new ClientField();
		divingField.setName(SpeciesFields.diving+"");
		divingField.setType(ClientFieldType.BOOLEAN);
		ClientField dangerousField=new ClientField();
		dangerousField.setName(SpeciesFields.dangerous+"");
		dangerousField.setType(ClientFieldType.BOOLEAN);
		ClientField mammalField=new ClientField();
		mammalField.setName(SpeciesFields.m_mammals+"");
		mammalField.setType(ClientFieldType.BOOLEAN);
		ClientField invertebrateField=new ClientField();
		invertebrateField.setName(SpeciesFields.m_invertebrates+"");
		invertebrateField.setType(ClientFieldType.BOOLEAN);
		ClientField algaeField=new ClientField();
		algaeField.setName(SpeciesFields.algae+"");
		algaeField.setType(ClientFieldType.BOOLEAN);
		
		
		fields.put(pelagicField.getName(), pelagicField);
		fields.put(deepwaterField.getName(), deepwaterField);
		fields.put(anglingField.getName(), anglingField);
		fields.put(divingField.getName(), divingField);
		fields.put(dangerousField.getName(), dangerousField);
		fields.put(mammalField.getName(), mammalField);
		fields.put(invertebrateField.getName(), invertebrateField);
		fields.put(algaeField.getName(), algaeField);
	}
	
	public TrueFalseGrid(String title) {
		super();
		this.setTitle(title);		
		this.setWidth(AquaMapsPortletCostants.Filter_Container_Width);
		this.setLayout(new FitLayout());
		this.setCollapsible(true);
		this.collapse();
		
		for(String fieldName:fields.keySet()){			
			store.add(recordDef.createRecord(new Object[]{false,fieldName,false}));			
		}		
			
		colModel.setEditable("attribute", false);		
		colModel.setEditable("enabled",true);
		colModel.setEditable("value",true);
		grid=new EditorGridPanel(store,colModel);
		/*GridView view = new GridView();
		view.setAutoFill(true);
		view.setForceFit(true);	
		grid.setView(view);*/
		grid.setStripeRows(true);
		
		grid.setFrame(true);
		//grid.setWidth(AquaMapsPortletCostants.FILTER_WIDTH);
		grid.addGridCellListener(new GridCellListenerAdapter(){			
			public void onCellDblClick(GridPanel grid, int rowIndex,
					int colIndex, EventObject e) {
				super.onCellDblClick(grid, rowIndex, colIndex, e);
				if(colIndex==2){
					Record record = grid.getStore().getAt(rowIndex);  
					record.set("value", !record.getAsBoolean("value")); 
					updateSummary();
				}				
			}
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
		this.add(grid);
	}
	
	public List<ClientFilter> getFilter(){
		List<ClientFilter> toReturn=new ArrayList<ClientFilter>();
		for(Record record: store.getRecords()){
			if(record.getAsBoolean("enabled")){
			ClientField field =fields.get(record.getAsString("attribute"));
			field.setValue(String.valueOf(record.getAsBoolean("value")));
			field.setType(ClientFieldType.BOOLEAN);
			ClientFilter filter=new ClientFilter();
			filter.setField(field);
			filter.setType(ClientFilterType.is);
			toReturn.add(filter);
			}
		}
		return toReturn;
	}
	
	public void reset(){
		store.removeAll();
		for(String fieldName:fields.keySet()){			
			store.add(recordDef.createRecord(new Object[]{false,fieldName,false}));			
		}
		updateSummary();
	}
	
	public void updateSummary(){
		for(Record record: store.getRecords())
			if(record.getAsBoolean("enabled")){
				AquaMapsPortlet.get().species.filter.filterDetails.addFilter(summaryFilterType, record.getAsString("attribute"), "=" , record.getAsString("value"));					
			}
			else AquaMapsPortlet.get().species.filter.filterDetails.removeFilter(summaryFilterType, record.getAsString("attribute"));
	}
	
}
