package org.gcube.portlets.widgets.wsexplorer.resources;


import com.github.gwtbootstrap.client.ui.config.Configurator;
import com.github.gwtbootstrap.client.ui.resources.Resources;
import com.google.gwt.core.client.GWT;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jul 30, 2014
 *
 */
public class BootstrapConfigurator implements Configurator {

	public Resources getResources() {
		return GWT.create(BootstrapOverrideResources.class);
	}

	public boolean hasResponsiveDesign() {
		return false;
	}
}
