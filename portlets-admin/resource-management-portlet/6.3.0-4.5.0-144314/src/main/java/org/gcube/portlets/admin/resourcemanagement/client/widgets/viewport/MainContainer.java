/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: MainContainer.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.widgets.viewport;

import org.gcube.portlets.admin.resourcemanagement.client.widgets.panels.MainPanel;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.registry.WidgetsRegistry;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * @author Daniele Strollo (ISTI-CNR)
 */
public class MainContainer extends LayoutContainer {
	public MainContainer() {
		super();
		//- this.setPagePosition(0, 150);
	}

	public boolean add(String id, MainPanel item) {
		WidgetsRegistry.registerWidget(id, item.getContainer());
		return super.add(item.getContainer(), item.getLayout());
	}

	public boolean addPanel(String id, MainPanel item) {
		WidgetsRegistry.registerPanel(id, item);
		return super.add(item.getContainer(), item.getLayout());
	}

}
