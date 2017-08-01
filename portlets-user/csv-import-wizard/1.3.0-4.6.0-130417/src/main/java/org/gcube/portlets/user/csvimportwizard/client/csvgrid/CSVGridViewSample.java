/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.client.csvgrid;

import java.util.ArrayList;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class CSVGridViewSample extends GridView {
	
	protected static final GridCellRenderer<ModelData> excludeRenderer = new GridCellRenderer<ModelData>() {

		
		public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<ModelData> store, Grid<ModelData> grid) {
			config.style = "background-color: #e6e0da;color: #bfa698;";
			return model.get(property);
		}
	};

	protected ArrayList<Integer> excludedColumns = new ArrayList<Integer>();

	@Override
	protected Menu createContextMenu(final int colIndex) {
		Menu menu = new Menu();
		
		CheckMenuItem includeMenu = new CheckMenuItem("Include");
		includeMenu.setGroup("include");
		includeMenu.setChecked(!excludedColumns.contains(colIndex));
		menu.add(includeMenu);

		includeMenu.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				excludedColumns.remove(new Integer(colIndex));
			}
		});


		CheckMenuItem excludeMenu = new CheckMenuItem("Exclude");
		excludeMenu.setGroup("include");
		excludeMenu.setChecked(excludedColumns.contains(colIndex));
		menu.add(excludeMenu);
		
		excludeMenu.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				excludedColumns.add(colIndex);
				setColumnExcluded(colIndex);
			}
		});

		
		return menu;
	}
	
	protected void setColumnExcluded(int colIndex)
	{
		grid.getColumnModel().getColumn(colIndex).setRenderer(excludeRenderer);
	}

	/**
	 * Returns the excluded columns by index.
	 * @return an {@link ArrayList} of excluded column index.
	 */
	public ArrayList<Integer> getExcludedColumns() {
		return excludedColumns;
	}

}
