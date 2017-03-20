/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templateactions;

import java.util.List;

import org.gcube.portlets.user.tdtemplate.client.TdTemplateConstants;
import org.gcube.portlets.user.tdtemplate.client.event.operation.AggregateByTimeOperationEvent;
import org.gcube.portlets.user.tdtemplate.client.event.operation.DeleteColumnOperationEvent;
import org.gcube.portlets.user.tdtemplate.shared.TdColumnDefinition;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.SimpleEventBus;


/**
 * The Class MenuActionUpdater.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 9, 2015
 */
public class MenuActionBuilder {
	

	/**
	 * Instantiates a new menu action builder.
	 */
	public MenuActionBuilder() {
	}
	
	/**
	 * Reset button menu.
	 *
	 * @param button the button
	 */
	private void resetButtonMenu(Button button){
		button.setTitle("");
		if(button.getMenu()!=null)
			button.getMenu().removeAll();
		
		button.setMenu(null);
	}
	/**
	 * Gets the menu item for colum.
	 *
	 * @param col the col
	 * @return the menu item for colum
	 */
	private MenuItem getMenuItemForColumn(TdColumnDefinition col){
		String columnName = col.getColumnName();
		if(columnName==null ||columnName.isEmpty())
			columnName = "Column "+(col.getIndex()+1);
		
		return new MenuItem(columnName);
	}
	
	
	/**
	 * Creates the menu for remove colum.
	 *
	 * @param actionBus the action bus
	 * @param button the button
	 * @param columns the columns
	 */
	public void createMenuForRemoveColum(final SimpleEventBus actionBus, Button button, List<TdColumnDefinition> columns){
		
		resetButtonMenu(button);
		Menu menu = new Menu();
		for (final TdColumnDefinition col : columns) {	
	
			MenuItem mi = getMenuItemForColumn(col);
			mi.addSelectionListener(new SelectionListener<MenuEvent>() {

				@Override
				public void componentSelected(MenuEvent ce) {
					MessageBox.confirm(TdTemplateConstants.ACTION_REMOVE_COLUMN, "Removing "+col.getColumnName()+", confirm?", null).addCallback(new Listener<MessageBoxEvent>() {
					
					@Override
					public void handleEvent(MessageBoxEvent be) {
						String clickedButton = be.getButtonClicked().getItemId();
						if(clickedButton.equals(Dialog.YES)){
							actionBus.fireEvent(new DeleteColumnOperationEvent(col));
						}
					}
				});
				}
			});
			menu.add(mi);
		}
		button.setMenu(menu);
	}
	
	
	/**
	 * Creates the menu for time aggregation.
	 *
	 * @param button the button
	 * @param timeColumns the time columns
	 * @param otherColumns the other columns
	 */
	public void createMenuForTimeAggregation(final SimpleEventBus actionBus, Button button, List<TdColumnDefinition> timeColumns,  final List<TdColumnDefinition> otherColumns){
		
		resetButtonMenu(button);
		if(timeColumns.size()>0){
			button.setEnabled(true);
			Menu menu = new Menu();
			for (final TdColumnDefinition col : timeColumns) {
				GWT.log("UpdateOperationsAvailableTime for TimeDimensionColumn: "+col);
			
				MenuItem mi = getMenuItemForColumn(col);
				mi.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						actionBus.fireEvent(new AggregateByTimeOperationEvent(col, otherColumns));
					}
				});
				
				menu.add(mi);
			}
			button.setMenu(menu);
		}else{
			button.setEnabled(false);
			button.setTitle("Operation not available TIME DIMENSION column not found!");
		}
	}

}
