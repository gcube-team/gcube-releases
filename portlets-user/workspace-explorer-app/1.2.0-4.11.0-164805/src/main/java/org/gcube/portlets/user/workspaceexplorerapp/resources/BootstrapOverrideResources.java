package org.gcube.portlets.user.workspaceexplorerapp.resources;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jul 28, 2014
 *
 */
import com.github.gwtbootstrap.client.ui.resources.Resources;
import com.google.gwt.resources.client.TextResource;

public interface BootstrapOverrideResources extends Resources {
	@Source("css/bootstrap.min.css")
	TextResource bootstrapCss();

	@Source("css/gwt-bootstrap.css")
	TextResource gwtBootstrapCss();
}