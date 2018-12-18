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
 * Filename: ResourceProfilePanel.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.views.profile;


import org.gcube.portlets.admin.resourcemanagement.client.widgets.panels.GenericTreePanel;
import org.gcube.resourcemanagement.support.client.views.ResourceTypeDecorator;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class ResourceProfilePanel extends TabPanel {
	private String htmlProfile = null;
	private String xmlProfile = null;
	private static int counter = 0;
	private ResourceTypeDecorator type = null;

	public ResourceProfilePanel(final ResourceTypeDecorator type, final String xmlProfile, final String htmlProfile) {
		super();
		this.xmlProfile = xmlProfile;
		this.htmlProfile = htmlProfile;
		this.type = type;
		this.init();
	}

	private void init() {
		this.setId("resource-profile-panel-" + counter++);

		this.setPlain(true);
		this.setHeight("100%");

		TabItem xmlProfilePanel = new TabItem("XML Profile");
		//xmlProfilePanel.setScrollMode(Scroll.AUTO);

		TreePanel<ModelData> treepanel = null;
		// depending on the resource to show the root node will be declared
		if (this.type == ResourceTypeDecorator.WSResource) {
			treepanel = new GenericTreePanel(xmlProfile, "Document").getWidget();
		} 
		else if (this.type == ResourceTypeDecorator.DeployReport) {
			treepanel = new GenericTreePanel(xmlProfile, "ResourceReport").getWidget();
		}
		else if (this.type == ResourceTypeDecorator.AddScopeReport) {
			treepanel = new GenericTreePanel(xmlProfile, "ResourceReport").getWidget();
		}
		else {
			treepanel = new GenericTreePanel(xmlProfile, "Resource").getWidget();
		}
		xmlProfilePanel.add(treepanel);
		xmlProfilePanel.layout(true);

		xmlProfilePanel.setScrollMode(Scroll.AUTO);

		this.add(xmlProfilePanel);

		TabItem htmlProfilePanel = new TabItem("Source");
		//xmlProfilePanel.setScrollMode(Scroll.AUTO);

		Html htmlContainer = new Html();
		htmlContainer.setId("xml-container-" + counter);
		htmlContainer.setHtml(htmlProfile);
		htmlContainer.setAutoHeight(true);

		htmlProfilePanel.add(htmlContainer);
		htmlProfilePanel.layout(true);
		htmlProfilePanel.setScrollMode(Scroll.AUTO);

		this.add(htmlProfilePanel);
	}

	public final Component getWidget() {
		return this;
	}
}
