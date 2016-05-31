package org.gcube.portlets.user.joinnew.client.commons;

import com.google.gwt.user.client.ui.FlexTable;

public class ODLFlexTable extends FlexTable {
	
	public ODLFlexTable() {
		super();
		this.setBorderWidth(0);
		this.setCellPadding(0);
		this.setCellSpacing(0);
	}

	public void removeAllRows() {
		while (this.getRowCount() > 0) {
			this.removeRow(0);
		}
	}
	
}
