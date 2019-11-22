package gr.cite.geoanalytics.logicallayer;

import java.net.URL;

public interface NodeAwareLayerOperations extends LayerOperations{
	public void setNode(URL url);
	public URL getNode();
	public String getNodeFromURL();
}
