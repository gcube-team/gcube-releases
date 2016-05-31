package org.gcube.portlets.user.templates.client.components;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * <code> DoubleColumnPanel </code> class is a Widget that places to widget in the same y
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version October 2009 (1.4) 
 */
public class DoubleColumnPanel extends HorizontalPanel {
	
	Widget left;
	Widget right;
	/**
	 * 
	 * @param left .
	 * @param right .
	 */
	public DoubleColumnPanel(Widget left, Widget right) {
		super();
		this.left = left;
		this.right = right;
		
		add(left);
		HTML spacer = new HTML();
		spacer.setPixelSize(25, 50);
		add(spacer);
		add(right);				
	}
	
	/**
	 * return the widget which is different from clicked, useful when the user remove the widgets
	 * @param clicked
	 * @return
	 */
	public Widget getTheOtherOne(Widget clicked) {
		if (this.left.equals(clicked))
			return this.right;
		else 
			return this.left;
	}
	
	/**
	 * if a static image is uploaded
	 * @param imgToPlace
	 */
	public void insertImage(Widget toSubstitute, ImageArea imgToPlace ) {
		HTML spacer = new HTML();
		spacer.setPixelSize(25, 50);
		if (left.equals(toSubstitute)) {
			clear();
			this.left = imgToPlace;
			add(imgToPlace);			
			add(spacer);
			add(right);		
		} else {
			clear();
			this.right = imgToPlace;
			add(left);
			add(spacer);
			add(imgToPlace);		
		}
			
	}

}
