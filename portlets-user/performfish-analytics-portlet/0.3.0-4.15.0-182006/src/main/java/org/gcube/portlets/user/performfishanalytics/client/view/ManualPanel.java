package org.gcube.portlets.user.performfishanalytics.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;


/**
 * The Class ManualPanel.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Sep 5, 2019
 */
public class ManualPanel extends Composite {

	private static ManualPanelUiBinder uiBinder = GWT.create(ManualPanelUiBinder.class);

	/**
	 * The Interface ManualPanelUiBinder.
	 *
	 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
	 * 
	 * Sep 5, 2019
	 */
	interface ManualPanelUiBinder extends UiBinder<Widget, ManualPanel> {
	}

	/**
	 * Instantiates a new manual panel.
	 *
	 * @param parent the parent
	 */
	public ManualPanel(Panel parent) {
		initWidget(uiBinder.createAndBindUi(this));
		
		//parent.add(this);
	}
}
