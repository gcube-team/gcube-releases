/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.form;

import org.gcube.portlets.user.statisticalmanager.client.StatisticalManager;
import org.gcube.portlets.user.statisticalmanager.client.bean.TableItemSimple;
import org.gcube.portlets.user.statisticalmanager.client.resources.Images;
import org.gcube.portlets.user.tdw.client.TabularData;
import org.gcube.portlets.user.tdw.client.TabularDataGridPanel;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

/**
 * @author ceras
 *
 */
public abstract class OpenTableButton extends Button {
	
	TableItemSimple tableItem=null;
	boolean added = false;
	
	public abstract void onOpenTable();
	
	public abstract void onHideTable();
	
	public OpenTableButton() {
		super("Open Data Set", Images.table());
		this.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				TabularData tabularData = StatisticalManager.getTabularData();
				TabularDataGridPanel gridPanel = tabularData.getGridPanel();

				Dialog dialog = new Dialog();
				dialog.setMaximizable(true);
				dialog.setBodyBorder(false);
				dialog.setExpanded(true);
//				dialog.setButtons(Dialog.OKCANCEL);
//				dialog.setIcon(Images.table());  
				dialog.setHeadingText("Data Set "+tableItem.getName());  
				dialog.setWidth(700);  
				dialog.setHeight(500);  
				dialog.setHideOnButtonClick(true);  
				dialog.setModal(true);
				dialog.add(gridPanel);
				dialog.show();
				
				dialog.addHideHandler(new HideHandler() {
					@Override
					public void onHide(HideEvent event) {
						onHideTable();
					}
				});

				onOpenTable();

				tabularData.openTable(tableItem.getId());				
				//gridPanel.setSize("320", "200");
				gridPanel.setHeaderVisible(false);
			}
		});
	}
	
	public void setTable(TableItemSimple tableItem) {
		this.tableItem = tableItem;
	}
	

	/**
	 * @return the added
	 */
	public boolean isAdded() {
		return added;
	}
	
	/**
	 * @param added the added to set
	 */
	public void setAdded(boolean added) {
		this.added = added;
	}

}
