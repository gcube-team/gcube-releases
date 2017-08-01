/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.client.view;

import java.util.List;

import org.gcube.portlets.admin.gcubereleases.client.GcubeReleasesAppController;
import org.gcube.portlets.admin.gcubereleases.client.resources.Icons;
import org.gcube.portlets.admin.gcubereleases.shared.Package;

import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.LabelType;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;


/**
 * The Class SubsystemView.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class SubsystemView extends FlowPanel{
	
	private PackagesTable tables = new PackagesTable(false);
//	private HTML htmlTitle = new HTML();
	private FlowPanel basePanel = new FlowPanel();
	private FlowPanel panelHeader = new FlowPanel();
	private Image imgTopLink = new Image(Icons.ICONS.top());
	private HTML topLink = new HTML("<div><a href=\"#"+GcubeReleasesAppController.HEADER_ID+"\"/>"+imgTopLink+"</div>");

	/**
	 * Instantiates a new subsystem view.
	 *
	 * @param title the title
	 * @param packages the packages
	 * @param addTopLink the add top link
	 */
	public SubsystemView(String title, List<Package> packages, boolean addTopLink) {
		addStyleName("margin-FlowPanel");
		basePanel.add(panelHeader);
//		basePanel.setType(PanelType.INFO);
		addTitle(title);
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

	/**
	 * Adds the title.
	 *
	 * @param header the header
	 */
	private void addTitle(String header){
//		Heading heading = new Heading(4, header);
//		heading.getElement().setId(header);
//		panelHeader.add(heading);
		
		FlowPanel flowPanel = new FlowPanel();
		HTML anchor = new HTML("<div id="+header+" style=\"padding-bottom:10px\"></div>");
		Label labelTitle = new Label(header);
		labelTitle.addStyleName("label-gcube-principal");
		labelTitle.setType(LabelType.INFO);
		flowPanel.add(anchor);
		flowPanel.add(labelTitle);
		panelHeader.add(flowPanel);
//		panelSubsystem.add(labelSubsytem);
//		Heading heading = new Heading(4, header);
//		heading.getElement().setId(header);
	

	}
}
