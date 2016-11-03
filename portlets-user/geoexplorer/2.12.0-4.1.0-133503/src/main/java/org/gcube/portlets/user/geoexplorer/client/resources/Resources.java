/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.client.resources;

/**
 * @author ceras
 *
 */
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Resources extends ClientBundle {
	@Source("search.png")
	ImageResource iconSearch();

	@Source("preload.gif")
	ImageResource iconPreload();

	@Source("gisplus.png")
	ImageResource iconGisWorld();

	@Source("cancel.png")
	ImageResource iconCancel();

	@Source("refresh.png")
	ImageResource iconRefresh();
	
	@Source("expand.gif")
	ImageResource expand();
	
	@Source("view_fullscreen.png")
	ImageResource view_full();
	
	@Source("source-view-icon.png")
	ImageResource view_source();
	
	@Source("expand_new_window.png")
	ImageResource expandNewWindow();
	
	@Source("table-icon.png")
	ImageResource table();
	
	@Source("summary.png")
	ImageResource summary();
	
	@Source("geonetworkico.jpeg")
	ImageResource geonetworkico();
	
	@Source("geonetworkicosimple.jpeg")
	ImageResource geonetworkicosimple();
	
	@Source("geonetworkicoinspire.jpeg")
	ImageResource geonetworkicoinspire();
	
	@Source("geonetworkicoiso.jpeg")
	ImageResource geonetworkicoisocore();
	
	@Source("loading.gif")
	ImageResource loading();
	
	@Source("maplink.png")
	ImageResource maplink();
}
