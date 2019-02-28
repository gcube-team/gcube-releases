package org.gcube.portlets.admin.createusers.client.ui;

import com.github.gwtbootstrap.client.ui.CellTable;

public interface TableResources extends CellTable.Resources{
	
	@Source({CellTable.Style.DEFAULT_CSS, "org/gcube/portlets/admin/createusers/client/ui/Table.css"})
	TableStyle cellTableStyle();
	 
	interface TableStyle extends CellTable.Style {}
}
