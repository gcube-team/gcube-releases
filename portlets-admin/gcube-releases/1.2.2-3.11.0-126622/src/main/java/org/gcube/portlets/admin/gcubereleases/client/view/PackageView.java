/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.client.view;

import java.util.List;

import org.gcube.portlets.admin.gcubereleases.client.GcubeReleasesAppController;
import org.gcube.portlets.admin.gcubereleases.client.resources.Icons;
import org.gcube.portlets.admin.gcubereleases.shared.Package;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

/**
 * The Class PackageView.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class PackageView extends FlowPanel{
	
	private PackagesTable tables = new PackagesTable(true);
//	private HTML htmlTitle = new HTML();
	private FlowPanel basePanel = new FlowPanel();
//	private PanelHeader panelHeader = new PanelHeader();
	private Image imgTopLink = new Image(Icons.ICONS.top());
	private HTML topLink = new HTML("<div><a href=\"#"+GcubeReleasesAppController.HEADER_ID+"\"/>"+imgTopLink+"</div>");

	/**
	 * Instantiates a new package view.
	 *
	 * @param packages the packages
	 * @param addTopLink the add top link
	 */
	public PackageView(List<Package> packages, boolean addTopLink) {
		addStyleName("margin-FlowPanel");
//		basePanel.add(panelHeader);
//		basePanel.setType(PanelType.INFO);
		addPackages(packages);
		add(basePanel);
		if(addTopLink){
			imgTopLink.setTitle("Top");
			add(topLink);
		}
	}
	
	/**
	 * Adds the packages.
	 *
	 * @param packages the packages
	 */
	private void addPackages(List<Package> packages) {
		tables.addPackages(packages);
		FlowPanel panel = new FlowPanel();
//		panel.setMarginBottom(10);
//		panel.setMarginRight(10);
//		panel.setMarginLeft(10);
//		panel.setMarginTop(10);
		panel.add(tables.getDataGrid());
//		panel.add("<div><a href=\"#"+HeaderPage.HEADER_ID+"\"/></div>");
		basePanel.add(panel);
		
	}
}
