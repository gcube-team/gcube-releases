/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElements;

import java.util.LinkedList;

import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.UILogEntry;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.UILogEntry.UILogEntryLevel;

import com.gwtext.client.core.TextAlign;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.GridView;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.RowNumberingColumnConfig;
import com.gwtext.client.widgets.layout.AnchorLayout;
import com.gwtext.client.widgets.layout.AnchorLayoutData;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class ExecutionLogWindow extends Window {

	private GridPanel grid;
	private RecordDef recordDef;
	private ArrayReader reader;
	private Store store;
	
	public ExecutionLogWindow() {
		/* Create the record definitions */
		this.recordDef = new RecordDef(  
				new FieldDef[]{  
						new StringFieldDef("type"),
						new StringFieldDef("message")  
				}  
		);
		this.reader = new ArrayReader(recordDef);

		/* create the column model*/
		BaseColumnConfig[] columns = new BaseColumnConfig[]{  
				new RowNumberingColumnConfig(),  
				new ColumnConfig(
						" ", 
						"type", 
						20, 
						false, 
						new Renderer() {
							public String render(Object value, CellMetadata cellMetadata, Record record, int rowIndex, int colNum, Store store) {
								String level = (String) value;
								if (level.equals(UILogEntryLevel.TYPE_ERROR.toString()))
									cellMetadata.setCssClass("error-icon");
								else if (level.equals(UILogEntryLevel.TYPE_WARNING.toString()))
									cellMetadata.setCssClass("warning-icon");
								else if (level.equals(UILogEntryLevel.TYPE_INFORMATION.toString()))
									cellMetadata.setCssClass("information-icon");
								return "";
							}}),  
				new ColumnConfig("Message", "message", 500)
		};
		((ColumnConfig) columns[1]).setResizable(false);
		((ColumnConfig) columns[1]).setAlign(TextAlign.CENTER);
		((ColumnConfig) columns[2]).setResizable(true);
		ColumnModel columnModel = new ColumnModel(columns);
		
		/* create the grid */
		this.grid = new GridPanel();
		this.grid.setWidth(550);
		this.grid.setHeight(300);
		this.grid.setColumnModel(columnModel);

		/* create the data store */
		store = new Store(reader);
		this.grid.setStore(store);
		
		/* create the grid view */
		GridView view = new GridView();
		this.grid.setView(view); 
		
		this.setModal(true);
		this.setTitle("Execution log");
		this.setWidth(600);
		this.setHeight(400);
		this.setLayout(new AnchorLayout());
		this.add(grid, new AnchorLayoutData("100% 100%"));
	}
	
	public void setLogData(LinkedList<UILogEntry> logData) {
		Object[][] data = new Object[logData.size()][2];
		for (int i=0; i<logData.size(); i++) {
			UILogEntry logEntry = logData.get(i);
			data[i] = new Object[] { logEntry.getLevel().toString(), logEntry.getMessage() };
		}
		MemoryProxy proxy = new MemoryProxy(data);
		store.removeAll();
		store.setDataProxy(proxy);
		store.load();
	}
}
