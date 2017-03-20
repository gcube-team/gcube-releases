/**
 *
 */
package org.gcube.portlets.user.geoexplorerportlet.client.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author ceras
 *
 */
public interface Resources extends ClientBundle {

	@Source("layers.png")
	ImageResource iconLayers();

	@Source("truemarble.png")
	ImageResource iconTrueMarble();

	@Source("folderOpen.png")
	ImageResource iconOpen();

	@Source("remove.png")
	ImageResource iconRemove();

	@Source("addWms.png")
	ImageResource iconAddWms();

	@Source("geoexplorer.png")
	ImageResource logoGeoExplorer();

	@Source("loading.gif")
	ImageResource loading();

	@Source("settings-refresh.png")
	ImageResource settingsRefresh();

	@Source("closewindow.jpg")
	ImageResource closeWindow();
}