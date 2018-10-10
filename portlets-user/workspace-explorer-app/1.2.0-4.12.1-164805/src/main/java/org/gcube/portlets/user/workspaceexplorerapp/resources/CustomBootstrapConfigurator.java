package org.gcube.portlets.user.workspaceexplorerapp.resources;


import com.github.gwtbootstrap.client.ui.config.Configurator;
import com.github.gwtbootstrap.client.ui.resources.Resources;
import com.google.gwt.core.client.GWT;


/**
 * The Class BootstrapConfigurator.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 10, 2016
 */
public class CustomBootstrapConfigurator implements Configurator {

	/* (non-Javadoc)
	 * @see com.github.gwtbootstrap.client.ui.config.Configurator#getResources()
	 */
	public Resources getResources() {
		return GWT.create(BootstrapOverrideResources.class);
	}

	/* (non-Javadoc)
	 * @see com.github.gwtbootstrap.client.ui.config.Configurator#hasResponsiveDesign()
	 */
	public boolean hasResponsiveDesign() {
		return false;
	}
}
