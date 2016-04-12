/**
 * 
 */
package org.gcube.portlets.user.td.csvimportwidget.client.csvgrid;

import java.util.ArrayList;

import org.gcube.portlets.user.td.csvimportwidget.client.data.CSVRow;
import org.gcube.portlets.user.td.csvimportwidget.client.data.CSVRowKeyProvider;
import org.gcube.portlets.user.td.csvimportwidget.client.data.CSVRowValueProvider;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.wizardwidget.client.util.UtilsGXT3;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.data.client.loader.HttpProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.DataReader;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.RowNumberer;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class CSVGrid extends Grid<CSVRow> {

	private static final String csvImportFileServlet = "CSVImportFileServlet";
	private CSVGridView gridViewSample;
	private CSVGridMessages msgs;
	private CommonMessages msgsCommon;

	
	
	public CSVGrid() {
		super(new ListStore<CSVRow>(new CSVRowKeyProvider()),
				new ColumnModel<CSVRow>(
						new ArrayList<ColumnConfig<CSVRow, ?>>()));
		initMessages();
		setHeight(200);
		setBorders(true);

		getView().setStripeRows(true);
		setLoadMask(true);

		gridViewSample = new CSVGridView();
		setView(gridViewSample);

		getView().setEmptyText(msgs.noData());
		setBorders(true);
	}

	
	protected void initMessages() {
		msgs = GWT.create(CSVGridMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
	}
	
	
	public void configureColumns(ArrayList<String> columnNames) {
		ColumnModel<CSVRow> columnModel = createColumnModel(columnNames);
		ListStore<CSVRow> store = createStore(columnNames);
		reconfigure(store, columnModel);
		getView().refresh(true);
	}

	protected ListStore<CSVRow> createStore(ArrayList<String> columnNames) {
		ListStore<CSVRow> store=null;
		
		try {
			String path = GWT.getModuleBaseURL() + csvImportFileServlet;
			Log.info("CSVImportFileServlet path:" + path);
			// use a http proxy to get the data
			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
					path);

			HttpProxy<ListLoadConfig> proxy = new HttpProxy<ListLoadConfig>(
					builder);

			// need a loader, proxy, and reader
			DataReader<ListLoadResult<CSVRow>, String> reader = new CSVJsonReader();

			final ListLoader<ListLoadConfig, ListLoadResult<CSVRow>> loader = new ListLoader<ListLoadConfig, ListLoadResult<CSVRow>>(
					proxy, reader);

			store = new ListStore<CSVRow>(
					new CSVRowKeyProvider());
			loader.addLoadHandler(new LoadResultListStoreBinding<ListLoadConfig, CSVRow, ListLoadResult<CSVRow>>(
					store));

			loader.load();

			
		} catch (Exception e) {
			UtilsGXT3.alert(msgsCommon.error(),msgs.errorCreatingTheStore(e.getLocalizedMessage()));
			Log.error("Error in creating the store: "+e.getLocalizedMessage());
			e.printStackTrace();
			
		}
		
		return store;
	}

	protected ColumnModel<CSVRow> createColumnModel(
			ArrayList<String> columnNames) {
		ArrayList<ColumnConfig<CSVRow, ?>> columns = new ArrayList<ColumnConfig<CSVRow, ?>>();

		columns.add(new RowNumberer<CSVRow>(new IdentityValueProvider<CSVRow>()));

		for (int i = 0; i < columnNames.size(); i++) {
			String columnField = "field" + (i + 1);
			String columnName = columnNames.get(i);
			ColumnConfig<CSVRow, String> columnConfig = new ColumnConfig<CSVRow, String>(
					new CSVRowValueProvider(columnField), 100, columnName);
			columns.add(columnConfig);
		}

		return new ColumnModel<CSVRow>(columns);
	}

	/**
	 * Returns the import column mask.
	 * 
	 * @return an array of boolean where the item is <code>true</code> if the
	 *         column have to be imported, <code>false</code> otherwise.
	 */
	public ArrayList<Boolean> getImportColumnsMask() {
		// boolean[] columnMask = new
		// boolean[getColumnModel().getColumnCount()];
		int limit = getColumnModel().getColumnCount();
		ArrayList<Boolean> columnMask = new ArrayList<Boolean>();
		ArrayList<Integer> excluded = gridViewSample.getExcludedColumns();
		for (int i = 1; i < limit; i++) {
			// columnMask[i] = !excluded.contains(i);
			Boolean b = !excluded.contains(i);
			columnMask.add(b);
		}
		Log.debug("Column Mask: " + columnMask);
		return columnMask;
	}

}
