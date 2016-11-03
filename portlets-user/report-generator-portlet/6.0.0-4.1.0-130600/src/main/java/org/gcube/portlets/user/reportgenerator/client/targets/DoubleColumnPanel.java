package org.gcube.portlets.user.reportgenerator.client.targets;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * <code> DoubleColumnPanel </code> class is a Widget that places to widget in the same y
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version October 2009 (1.4) 
 */
public class DoubleColumnPanel extends Composite {
	
	private Widget left;
	private Widget right;
	
	private HorizontalPanel mainPanel = new HorizontalPanel();
	
	
	/**
	 * 
	 * @param left .
	 * @param right .
	 */
	public DoubleColumnPanel(Widget left, Widget right) {
		super();
		
		this.left = left;
		this.right = right;
		
		mainPanel.add(left);
		mainPanel.add(right);
		mainPanel.setWidth("800");
		initWidget(mainPanel);
		
	}
	
	

}
