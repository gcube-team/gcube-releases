package org.gcube.portlets.user.gisviewer.client.datafeature;


import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.gisviewer.client.commons.beans.DataResult;
import org.gcube.portlets.user.gisviewer.client.commons.beans.ResultColumn;
import org.gcube.portlets.user.gisviewer.client.commons.beans.ResultRow;
import org.gcube.portlets.user.gisviewer.client.commons.beans.WebFeatureTable;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;


public class DataResultPanel extends TabPanel {

	private static final String MESSAGE_NO_DATA = "No data found in this selection for this area.";
	private static final String MESSAGE_WFS_NOT_SUPPORTED = "Wfs requests not supported for this area.";
	private String grid_h;
	private String grid_w;
	
	public DataResultPanel() {
		super();

		this.setMinTabWidth(115);
		this.setAutoWidth(true);
		
		this.setResizeTabs(true);
		this.setTabScroll(true);
		this.setAnimScroll(true);
		this.setCloseContextMenu(true);
	}

	
	public String getGrid_h() {
		return grid_h;
	}

	public void setGrid_h(String gridH) {
		grid_h = gridH;
	}

	public String getGrid_w() {
		return grid_w;
	}

	public void setGrid_w(String gridW) {
		grid_w = gridW;
	}

	public void setDataResult(List<DataResult> dataResults) {
		
		for (DataResult dr : dataResults) {

			List<ResultRow> rows = dr.getTable().getRows();
			List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
			ListStore<BaseModel> listStore = new ListStore<BaseModel>();
			List<String> columnNames = new ArrayList<String>();
			
			int row_n = 0;
			for (ResultRow row : rows) {
				List<ResultColumn> resultCols = row.getColumns();
				if (row_n == 0) {
					// first row, initializing columns
					for (ResultColumn resultColumn : resultCols) {
						ColumnConfig column = new ColumnConfig();
						column.setId(resultColumn.getValue());
						column.setHeader(resultColumn.getValue());
						column.setWidth(65);
						configs.add(column);
						columnNames.add(resultColumn.getValue());
					}
				} else {
					// other rows
					BaseModel bs = new BaseModel();
					int index=0;
					for (ResultColumn column : resultCols) {
						bs.set(columnNames.get(index), column.getValue());
						index++;
					}
					listStore.add(bs);
				}
				row_n++;
			}
			
		    ColumnModel cm = new ColumnModel(configs);
		    Grid<BaseModel> grid = new Grid<BaseModel>(listStore, cm);   
		    grid.setStyleAttribute("borderTop", "none");   
		    grid.setBorders(true);
		    grid.setStripeRows(true);
		    grid.setColumnLines(true);
		    
			
			TabItem item = new TabItem();
			item.setText(dr.getTitle());
			item.setClosable(true);
			
	    
			item.setLayout(new FitLayout());
			item.addStyleName("pad-text");
			item.add(grid);
			this.add(item);
		}
	}

	public boolean isEmpty() {
		return (this.getItems()==null || this.getItems().size()==0);
	}


	/**
	 * @param result
	 */
	public void setDataResultFromWfs(List<WebFeatureTable> result) {
		for (WebFeatureTable table: result) {
			TabItem item = new TabItem();
			item.setText(table.getTitle());
			item.setClosable(true);
	    
			item.addStyleName("pad-text");

			if (table.isError()) {
				item.setLayout(new CenterLayout());
				item.add(new Html(MESSAGE_WFS_NOT_SUPPORTED));
			} 
			else if (table.getRows().size()>0) {
				List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
				ListStore<BaseModel> listStore = new ListStore<BaseModel>();
	
				List<String> columnNames = table.getColumnNames();
				for (String columnName : columnNames) {
					ColumnConfig column = new ColumnConfig();
					column.setId(columnName);
					column.setHeader(columnName);
					column.setWidth(65);
					configs.add(column);
				}
				
				listStore.add(table.getRows());
	
			    ColumnModel cm = new ColumnModel(configs);
			    Grid<BaseModel> grid = new Grid<BaseModel>(listStore, cm);   
			    grid.setStyleAttribute("borderTop", "none");   
			    grid.setBorders(true);
			    grid.setStripeRows(true);
			    grid.setColumnLines(true);
			    
				item.setLayout(new FitLayout());
				item.add(grid);
				
			} else {
				item.setLayout(new CenterLayout());
				item.add(new Html(MESSAGE_NO_DATA));
			}
			this.add(item);
		}
	}
}