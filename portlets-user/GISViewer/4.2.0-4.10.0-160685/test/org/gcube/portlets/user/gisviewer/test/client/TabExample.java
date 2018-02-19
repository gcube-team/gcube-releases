package org.gcube.portlets.user.gisviewer.test.client;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.google.gwt.user.client.ui.HTML;

public class TabExample extends LayoutContainer {

	public TabExample() {
		TabPanel advanced = new TabPanel();
		advanced.setSize(600, 250);
		advanced.setMinTabWidth(115);
		advanced.setResizeTabs(true);
		advanced.setAnimScroll(true);
		advanced.setTabScroll(true);
		advanced.setCloseContextMenu(true);

		int index = 0;
		while (index < 7) {
			TabItem item = new TabItem();
			item.setText("New Tab " + ++index);
			item.setClosable(index != 1);
			item.add(new HTML("<b>Tab Body " + index+"</b>"));
			item.addStyleName("pad-text");
			advanced.add(item);
		}

		advanced.setSelection(advanced.getItem(6));
		add(advanced);
	}

}