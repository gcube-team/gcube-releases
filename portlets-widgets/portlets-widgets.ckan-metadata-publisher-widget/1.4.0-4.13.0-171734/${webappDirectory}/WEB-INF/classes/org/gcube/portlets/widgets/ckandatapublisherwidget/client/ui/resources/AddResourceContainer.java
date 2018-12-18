package org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.resources;

import com.github.gwtbootstrap.client.ui.TabPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Container for the third phase (add resource to dataset)
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class AddResourceContainer extends Composite{

	private static AddResourceContainerUiBinder uiBinder = GWT
			.create(AddResourceContainerUiBinder.class);

	interface AddResourceContainerUiBinder extends
			UiBinder<Widget, AddResourceContainer> {
	}
	
	@UiField VerticalPanel resourcesPanel;
	
	public AddResourceContainer(final String datasetUrl) {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	/**
	 * Add the form to this panel
	 * @param w
	 */
	public void add(TabPanel w){
		resourcesPanel.add(w);
	}
}
