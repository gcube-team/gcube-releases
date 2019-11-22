package org.gcube.portlets.user.speciesdiscovery.client.advancedsearch;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class ColumnContainer {
	
	protected LayoutContainer columnContainer = new LayoutContainer();
	protected LayoutContainer left = new LayoutContainer();
	protected LayoutContainer right = new LayoutContainer();
	
	
	public ColumnContainer() {
		
		columnContainer.setLayout(new ColumnLayout());
		columnContainer.setStyleAttribute("marginLeft", "10px");
		columnContainer.setStyleAttribute("marginRight", "10px");
		columnContainer.setStyleAttribute("padding", "5px");
		

		left.setStyleAttribute("paddingRight", "10px");
		FormLayout layout = new FormLayout();
		left.setLayout(layout);
		
		
		right.setStyleAttribute("paddingLeft", "10px");
		layout = new FormLayout();
		right.setLayout(layout);
		
		
		columnContainer.add(left, new ColumnData(.5));  
		columnContainer.add(right, new ColumnData(.5));  
	}

}
