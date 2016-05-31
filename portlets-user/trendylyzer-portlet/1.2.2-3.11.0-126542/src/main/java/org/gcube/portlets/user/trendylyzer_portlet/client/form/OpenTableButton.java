/**
 * 
 */
package org.gcube.portlets.user.trendylyzer_portlet.client.form;





import org.gcube.portlets.user.trendylyzer_portlet.client.TrendyLyzer_portlet;
import org.gcube.portlets.user.trendylyzer_portlet.client.resources.Images;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;


public abstract class OpenTableButton extends Button {
	
	TableItemSimple tableItem=null;
	boolean added = false;
	
	public abstract void onOpenTable();
	
	public abstract void onHideTable();
	
	public OpenTableButton() {
		super("Open Data Set", Images.table());
//		this.addSelectionListener(new SelectionListener<ButtonEvent>() {
//			public void componentSelected(ButtonEvent ce) {
//				TabularData tabularData = TrendyLyzer_portlet.getTabularData();
//				TabularDataGridPanel gridPanel = tabularData.getGridPanel();

				Dialog dialog = new Dialog();
				dialog.setMaximizable(true);
				dialog.setBodyBorder(false);
				dialog.setExpanded(true);
//				
				//dialog.setHeadingText("Data Set "+tableItem.getName());  
				dialog.setWidth(700);  
				dialog.setHeight(500);  
				dialog.setHideOnButtonClick(true);  
				dialog.setModal(true);
			//	dialog.add(gridPanel);
				dialog.show();
				
//				dialog.addHideHandler(new HideHandler() {
//					public void onHide(HideEvent event) {
//						onHideTable();
//					}
//				});
//
//				onOpenTable();
//
//				tabularData.openTable(tableItem.getId());				
//				gridPanel.setHeaderVisible(false);
//			}
//		});
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
