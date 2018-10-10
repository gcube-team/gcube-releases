/**
 *
 */
package org.gcube.portlets.user.workspaceexplorerapp.client;

import gwt.material.design.client.ui.MaterialFooter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 18, 2016
 */
public class WorkspaceExplorerFoooterPanel extends Composite {

	private static WorkspaceExplorerFoooterPanelUiBinder uiBinder =
		GWT.create(WorkspaceExplorerFoooterPanelUiBinder.class);

	interface WorkspaceExplorerFoooterPanelUiBinder
		extends UiBinder<Widget, WorkspaceExplorerFoooterPanel> {
	}

	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 *   xmlns:g="urn:import:**user's package**">
	 *  <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder>
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 */
	public WorkspaceExplorerFoooterPanel() {

		initWidget(uiBinder.createAndBindUi(this));
	}
	@UiField
	MaterialFooter we_footer_bar;

	public WorkspaceExplorerFoooterPanel(String firstName) {

		initWidget(uiBinder.createAndBindUi(this));
	}
}
