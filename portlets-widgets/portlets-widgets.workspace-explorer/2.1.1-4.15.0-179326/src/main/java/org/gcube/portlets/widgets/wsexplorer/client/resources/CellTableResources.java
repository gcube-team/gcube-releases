/**
 * 
 */
package org.gcube.portlets.widgets.wsexplorer.client.resources;

import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.CellTable.Resources;
import com.google.gwt.core.shared.GWT;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 23, 2015
 */
public interface CellTableResources extends Resources {

    public CellTableResources INSTANCE = GWT.create(CellTableResources.class);

	//  The styles used in this widget.
    @Override
    @Source("CellTable.css")
    CellTable.Style cellTableStyle();
}
