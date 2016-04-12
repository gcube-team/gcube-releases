package org.gcube.portlets.admin.wfdocviewer.client.view.dialog;
import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.admin.wfdocviewer.shared.ActionLogBean;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.i18n.client.DateTimeFormat;


/**
 * <code> AddCommentDialog </code> class is is the Dialog for input the new comment from user
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version June 2011 (0.1) 
 */


public class ShowUserActionsDialog extends Dialog {
	private ListStore<ActionLogBean> store;
	private Grid<ActionLogBean> grid;
	/**
	 * 
	 * @param controller
	 */
	public ShowUserActionsDialog(ArrayList<ActionLogBean> actions ) {
		super.setWidth(450);
		super.setHeight(300);
		setHeading("User Actions");
		setButtons(Dialog.CLOSE);
		
		store = new ListStore<ActionLogBean>();
		store.add(actions);
		store.setDefaultSort("date", SortDir.DESC);
		store.sort("date", SortDir.DESC);
		grid = new Grid<ActionLogBean>(store, getWfDocsListColumnModel()); 
	
		grid.setAutoExpandColumn("date"); 		
		grid.setBorders(true); 
		grid.setStripeRows(true);
		grid.getView().setForceFit(true);	
		grid.setSize(450, 300);

		ContentPanel gridPanel = new ContentPanel(new FitLayout());
		gridPanel.setHeaderVisible(false);
		gridPanel.add(grid); 		
		gridPanel.layout();
		add(gridPanel);		
		super.layout();
	}
	/**
	 * 
	 * @return the Column Model for the table
	 */
	private ColumnModel getWfDocsListColumnModel() {

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  

		ColumnConfig column = new ColumnConfig();  

		
		column = new ColumnConfig("date", "Action time", 80);  
		column.setHidden(false);
		column.setAlignment(HorizontalAlignment.CENTER);
	    column.setDateTimeFormat(DateTimeFormat.getFormat("dd/MM/yyyy 'at' HH:mm"));  
		column.setRowHeader(true);  
		configs.add(column);  

				
		column = new ColumnConfig("actiontype", "Action", 75);  
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setHidden(false);
		column.setRowHeader(true);  
		configs.add(column); 
		
		column = new ColumnConfig("author", "User", 75);  
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setHidden(false);
		column.setRowHeader(true);  
		configs.add(column); 
			
		return new ColumnModel(configs); 
	}

}

