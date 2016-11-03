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
 * Filename: MainPanel.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.widgets.panels;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.ui.Widget;

public abstract class MainPanel {
	private BorderLayoutData layout = null;
	private ContentPanel container = null;

	public MainPanel(final LayoutRegion position) {
		this.layout = new BorderLayoutData(position);
		this.container = new ContentPanel() {
			protected void onRender(final com.google.gwt.user.client.Element parent, final int pos) {
				super.onRender(parent, pos);
				init();
			};
		};
		this.setSplit(true);
		container.setHeaderVisible(false);
	}

	public MainPanel(final int size, final LayoutRegion position) {
		this(position);
		this.layout.setSize(size);
	}

	public MainPanel(final int size, final String title, final LayoutRegion position) {
		this(size, position);
		if (title != null) {
			this.container.setHeading(title);
		}
	}

	public final BorderLayoutData getLayout() {
		return this.layout;
	}

	public final void showHeader(final boolean showHeader) {
		this.container.setHeaderVisible(showHeader);
	}

	public final LayoutContainer getContainer() {
		return this.container;
	}

	public final void setMargins(final Margins margins) {
		this.layout.setMargins(margins);
	}

	public final void setCollapsible(final boolean collapsible) {
		this.layout.setCollapsible(collapsible);
	}

	public final void setFloatable(final boolean floatable) {
		this.layout.setFloatable(floatable);
	}

	public final void setSplit(final boolean split) {
		this.layout.setSplit(split);
	}
	

	public final void add(final Widget w, final boolean removeOthers) {
		if (removeOthers) {
			this.container.removeAll();
		}
		this.container.add(w);
		this.container.layout();
	}

	public final void setTopComponent(final Component component) {
		this.container.setTopComponent(component);
	}

	public final void setBottomComponent(final Component component) {
		this.container.setBottomComponent(component);
	}


	public final void removeAll() {
		this.container.removeAll();
	}

	/**
	 * Requires the following lines in the .css
	 * <pre>
	 * .x-hide-panel-header {
	 * 		display:none !important;
	 * }
	 * </pre>
	 */
	public final void hideHeader() {
		this.container.setHeaderVisible(false);
		this.container.getHeader().setStyleName("x-hide-panel-header");
	}

	public abstract void init();
}
