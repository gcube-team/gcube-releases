/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.client;

import java.util.ArrayList;

import org.gcube.portlets.user.csvimportwizard.client.data.CSVRowError;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class CSVErrorWindow extends Window {
	
	protected Grid<CSVRowError> grid;
	protected ListStore<CSVRowError> store;
	
	public CSVErrorWindow()
	{
		setHeading("CSV error details");
		setModal(true);
		setBlinkModal(true);
		setWidth(600);
		setHeight(350);
		
		setLayout(new FitLayout());
		
		createGrid();
		add(grid);
		
		Button close = new Button("Close");
		close.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				hide();
			}
		});
		
		addButton(close);
		setButtonAlign(HorizontalAlignment.CENTER);
	}
	
	protected void createGrid()
	{
		ArrayList<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		
		columns.add(new ColumnConfig(CSVRowError.LINE_NUMBER, "# line", 30));
		columns.add(new ColumnConfig(CSVRowError.LINE_VALUE, "Line", 60));
		columns.add(new ColumnConfig(CSVRowError.ERROR_DESCRIPTION, "Error", 160));
		
		ColumnModel columnModel = new ColumnModel(columns);
		
		store = new ListStore<CSVRowError>();
		
		grid = new Grid<CSVRowError>(store, columnModel);
		grid.getView().setForceFit(true);
	}
	
	public void updateGrid(ArrayList<CSVRowError> errors)
	{
		store.removeAll();
		store.add(errors);
	}

}
