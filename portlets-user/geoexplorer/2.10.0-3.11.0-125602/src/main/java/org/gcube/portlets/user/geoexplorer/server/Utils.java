/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.server;


/**
 * @author ceras
 *
 */
public class Utils {

	// transform layer obtained from csw into GeoExplorer LayerItem
//	public static List<LayerItem> getLayerItemsFromLayersCsw(List<LayerCsw> layersCsw) {
//		List<LayerItem> layerItems = new ArrayList<LayerItem>();
//		for (LayerCsw l : layersCsw) {
//			String layer = l.getName();
//			layer = (layer==null)?"Unknown":layer;
//
//			String[] splitName = layer.split(":");
//			String name = (splitName.length==2 ? splitName[1] : layer);
//			String geoserverUrl = l.getGeoserverUrl();
//
//			// remove each string after "?"
//			int index = geoserverUrl.indexOf("?");
//			if (index!=-1)
//				geoserverUrl = geoserverUrl.substring(0, geoserverUrl.indexOf("?"));
//			// remove suffix "/wms" or "/wms/"
//			geoserverUrl = geoserverUrl.replaceFirst("(/wms)$", "").replaceFirst("(/wms/)$", "");
//
//			layerItems.add(new LayerItem(l.getUuid(), name, layer, l.getTitle(), l.getDescription(), geoserverUrl));
//		}
//		return layerItems;
//	}

}
