package org.gcube.portlets.user.gisviewer.client.openlayers;

public class Util {
	public static native String getFormattedLonLat(double coordinate, String axis, String dmsOption)/*-{
		return $wnd.OpenLayers.Util.getFormattedLonLat(coordinate, axis, dmsOption);
	}-*/;
}
