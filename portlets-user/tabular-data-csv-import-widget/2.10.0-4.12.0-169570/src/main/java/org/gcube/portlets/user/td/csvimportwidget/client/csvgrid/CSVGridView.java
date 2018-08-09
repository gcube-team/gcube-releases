/**
 * 
 */
package org.gcube.portlets.user.td.csvimportwidget.client.csvgrid;

import java.util.ArrayList;

import org.gcube.portlets.user.td.csvimportwidget.client.data.CSVRow;
import org.gcube.portlets.user.td.csvimportwidget.client.dataresource.ResourceBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.grid.GridViewConfig;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;

/**
 * 
 * @author "Giancarlo Panichi" 
 *  
 *
 */
public class CSVGridView extends GridView<CSVRow> {
	
	private ArrayList<Integer> excludedColumns = new ArrayList<Integer>();
	private CSVGridMessages msgs;
	
	public CSVGridView()
	{
		initMessages();
		setViewConfig(new GridViewConfig<CSVRow>() {
			
		
			public String getRowStyle(CSVRow model, int rowIndex) {
				return "";
			}
			
			public String getColStyle(CSVRow model, ValueProvider<? super CSVRow, ?> valueProvider, int rowIndex, int colIndex) {
				return excludedColumns.contains(colIndex)?ResourceBundle.INSTANCE.importCss().getColumnExcluded():"";
			}
		});
	}
	
	protected void initMessages() {
		msgs = GWT.create(CSVGridMessages.class);
		
	}
	
	@Override
	protected Menu createContextMenu(final int colIndex) {
		Menu menu = new Menu();
		
		CheckMenuItem itmInclude = new CheckMenuItem(msgs.itmInclude());
		itmInclude.setGroup("include");
		itmInclude.setChecked(!excludedColumns.contains(colIndex));
		menu.add(itmInclude);

		itmInclude.addSelectionHandler(new SelectionHandler<Item>() {
			
		
			public void onSelection(SelectionEvent<Item> event) {
				excludedColumns.remove(new Integer(colIndex));
				//refresh(false);
			}
		});


		CheckMenuItem itmExclude = new CheckMenuItem(msgs.itmExclude());
		itmExclude.setGroup("include");
		itmExclude.setChecked(excludedColumns.contains(colIndex));
		menu.add(itmExclude);
		
		itmExclude.addSelectionHandler(new SelectionHandler<Item>() {
			
			public void onSelection(SelectionEvent<Item> event) {
				excludedColumns.add(colIndex);
				//refresh(false);
			}
		});

		
		return menu;
	}

	/**
	 * Returns the excluded columns by index.
	 * @return an {@link ArrayList} of excluded column index.
	 */
	public ArrayList<Integer> getExcludedColumns() {
		return excludedColumns;
	}

}
