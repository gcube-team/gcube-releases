/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.server.beans;


/**
 * @author ceras
 *
 */
public class WorkspaceCache {
	
//	private Workspace workspace;
//	private int counter = 0;
//	private List<LayerItem> layerItems = new ArrayList<LayerItem>();
//	private GeonetworkCache geonetworkCache;
//	private LogPrinter logPrinter;
//	boolean loaded = false;
//
//	/**
//	 * 
//	 */
//	public WorkspaceCache(Workspace workspace, GeonetworkCache geonetworkCache, LogPrinter logPrinter) {
//		this.workspace = workspace;
//		this.geonetworkCache = geonetworkCache;
//		this.logPrinter = logPrinter;
//	}
//	
//	public void incrementCounter() {
//		counter++;	
//	}
//	
//	public void decrementCounter() {
//		counter--;
//	}
//	
//	/**
//	 * @return the counter
//	 */
//	public int getCounter() {
//		return counter;
//	}
//	
//	/**
//	 * @return the workspace
//	 */
//	public Workspace getWorkspace() {
//		return workspace;
//	}
//	
//	/**
//	 * @return the layerItems
//	 */
//	public List<LayerItem> getLayerItems() {
//		return layerItems;
//	}
//	
//	/**
//	 * @return the loaded
//	 */
//	public boolean isLoaded() {
//		return loaded;
//	}
//	
//	/**
//	 * @param loaded the loaded to set
//	 */
//	public void setLoaded(boolean loaded) {
//		this.loaded = loaded;
//	}
//	
//	/**
//	 * @param layerItems the layerItems to set
//	 */
//	public void setLayerItems(List<LayerItem> layerItems) {
//		this.layerItems = layerItems;
//	}
//
//	/**
//	 * 
//	 */
//	public void update() {
//		try {
//			String workspaceName = this.getWorkspace().getName();
//			GeonetworkInstance geonetworkInstance = this.geonetworkCache.getGeonetworkInstance();
//			
//			String refer = "geonetwork:" + geonetworkInstance.getGeoNetworkUrl().substring(6, 23) + "..., ws:"+workspaceName ;
//			
//			logPrinter.printLogInfo("Load layers from csw (" + refer + ")...");
//			
//			long start = new Date().getTime();
//
//			// this csw query loads all layers and require much time
//			GeoCaller geoCaller = geonetworkCache.getGeoCaller();
//			CswLayersResult result = geoCaller.getLayersFromCsw(workspaceName);
//
//			List<LayerItem> layerItems = getLayerItemsFromLayersCsw(result.getLayers());
//
//			double time = new Date().getTime() - start;
//			logPrinter.printLogInfo("...Loading layers from csw (" + refer + ") terminated in "+(time/1000)+" seconds.");
//
//			this.setLayerItems(layerItems);
//			this.setLoaded(true);
//		} catch (Exception e) {
//			this.setLoaded(false); // TODO check
//			e.printStackTrace();
//		}
//	}
//
}
