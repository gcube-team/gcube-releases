/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.client.csvgrid;

import java.util.ArrayList;

import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.HttpProxy;
import com.extjs.gxt.ui.client.data.JsonLoadResultReader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowNumberer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class CSVGridSample extends ContentPanel {
	
	protected Grid<ModelData> grid;
	protected CSVGridViewSample gridViewSample;

	public CSVGridSample()
	{
		super();
		setHeaderVisible(false);
		setHeight(200);  
		setWidth("100%");
		setBorders(true);
		setLayout(new FitLayout());
		
		grid = new Grid<ModelData>(new ListStore<ModelData>(), new ColumnModel(new ArrayList<ColumnConfig>()));
		grid.setStripeRows(true);
		grid.setLoadMask(true);
		gridViewSample = new CSVGridViewSample();
		grid.setView(gridViewSample);
		
		grid.getView().setEmptyText("No data to show");
		grid.getView().setForceFit(true);
		add(grid);
	}
	
	public void configureColumns(String sessionId,ArrayList<String> columnNames)
	{
		ColumnModel columnModel = createColumnModel(columnNames);
		ListStore<ModelData> store = createStore(sessionId, columnNames);
		grid.reconfigure(store, columnModel);
	}
	
	protected ListStore<ModelData> createStore(String sessionId, ArrayList<String> columnNames)
	{
	    ModelType type = new ModelType();  
	    type.setRoot("records");
	    type.addField("Id","id");
	    
	    for (int i = 0; i<columnNames.size(); i++) {
	    	String columnField = "field"+(i+1);
	    	type.addField(columnField, columnField);
	    }
	    
		String path =  GWT.getModuleBaseURL()+"CSVServlet";  
		  
	    // use a http proxy to get the data  
	    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, path);
	    builder.setHeader("sessionId", sessionId);
	    HttpProxy<String> proxy = new HttpProxy<String>(builder);
	  
	    // need a loader, proxy, and reader  
	    JsonLoadResultReader<ListLoadResult<ModelData>> reader = new JsonLoadResultReader<ListLoadResult<ModelData>>(type);
	    
	    BaseListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(proxy, reader);
	    loader.load();
	  
	   return new ListStore<ModelData>(loader);
	     
	}
	
	protected ColumnModel createColumnModel(ArrayList<String> columnNames)
	{
		ArrayList<ColumnConfig> columns = new ArrayList<ColumnConfig>(); 

		columns.add(new RowNumberer());
		
	    for (int i = 0; i<columnNames.size(); i++) {
	    	String columnField = "field"+(i+1);
	    	String columnName = columnNames.get(i);
	    	ColumnConfig columnConfig = new ColumnConfig(columnField, columnName, 100);
	    	columns.add(columnConfig);
	    }
	    
	    return new ColumnModel(columns);
	}
	
	/**
	 * Returns the import column mask.
	 * @return an array of boolean where the item is <code>true</code> if the column have to be imported, <code>false</code> otherwise.
	 */
	public boolean[] getImportColumnsMask()
	{
		boolean[] columnMask = new boolean[grid.getColumnModel().getColumnCount()];
		ArrayList<Integer> excluded = gridViewSample.getExcludedColumns();
		for (int i = 0; i<columnMask.length; i++) columnMask[i] = !excluded.contains(i);
		return columnMask;
	}

}
