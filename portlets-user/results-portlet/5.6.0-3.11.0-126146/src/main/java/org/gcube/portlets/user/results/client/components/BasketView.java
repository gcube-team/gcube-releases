package org.gcube.portlets.user.results.client.components;

import org.gcube.portlets.user.results.client.ResultsDisplayer;
import org.gcube.portlets.user.results.client.control.Controller;
import org.gcube.portlets.user.results.client.control.FlexTableRowDropController;
import org.gcube.portlets.user.results.client.model.BasketModel;
import org.gcube.portlets.user.results.client.model.BasketModelItem;
import org.gcube.portlets.user.results.client.panels.LeftPanel;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;
/**
 * <code> Basket </code> 
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version January 2009 (2.0) 
 */
public class BasketView extends Composite {
	/**
	 * 
	 */

	public static final int BASKET_WIDTH = LeftPanel.LEFTPANEL_WIDTH-20;

	private FlexTable table;
	private VerticalPanel mainLayout = new VerticalPanel();

	/**
	 * 
	 */
	private BasketModel myModel;
	
	public static BasketView singleton;
	
	public static BasketView get() {
		return singleton;
	}


	public BasketView(BasketModel myModel, PickupDragController dragc, Controller control) {
		singleton = this;
		this.myModel = myModel;
		int scrollerHeight = ResultsDisplayer.get().getBottomScrollerPanel().getOffsetHeight();

		mainLayout.setPixelSize(BASKET_WIDTH, scrollerHeight);
		//mainLayout.addStyleName("border");
	
		
		table = new FlexTable();
		table.setCellPadding(0);
		table.setCellSpacing(0);
		table.setBorderWidth(0);
		
		int i = 0;
		for (BasketModelItem item : this.myModel.getChildren()) {			
			BasketViewItem toadd = new BasketViewItem(control, item.getName(), table, item);	
			if (! item.isNew())
				toadd.addStyleName("d4sFrame-highlight");
			table.setWidget(i, 0, toadd);
			i++;
		}	
		/**
		 * for dnd
		 */
		FlexTableRowDropController table_dct = new FlexTableRowDropController(control, table);
		dragc.registerDropController(table_dct);
		
		mainLayout.add(table);
		
		initWidget(mainLayout);		
	}

	public void addItem(BasketViewItem item) {
		table.add(item);
	}


	public FlexTable getTable() {
		return table;
	}

}
