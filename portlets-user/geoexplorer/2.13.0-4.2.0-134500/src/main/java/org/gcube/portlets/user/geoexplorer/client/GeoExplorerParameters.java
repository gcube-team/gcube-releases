/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.client;

/**
 * @author ceras
 *
 */
public class GeoExplorerParameters {
	
	private boolean closeWindowWhenSelectItems = true;
	private boolean showGroups = false;
	private int itemsForPage = 20;
	private GeoExplorerHandler geoExplorerHandler = null;
	private int windowWidth=Constants.windowWidth, windowHeight=Constants.windowHeight;
	private int windowMinWidth=Constants.windowMinWidth, windowMinHeight=Constants.windowMinHeight;
	private boolean displaySelectorsPanel = true;
	private String referredWorkspace = null;
	
	/**
	 * 
	 */
	public GeoExplorerParameters() {
		super();
	}

	/**
	 * @return the closeWindowWhenSelectItems
	 */
	public boolean isCloseWindowWhenSelectItems() {
		return closeWindowWhenSelectItems;
	}

	/**
	 * @param closeWindowWhenSelectItems the closeWindowWhenSelectItems to set
	 */
	public void setCloseWindowWhenSelectItems(boolean closeWindowWhenSelectItems) {
		this.closeWindowWhenSelectItems = closeWindowWhenSelectItems;
	}

	/**
	 * @return the showGroups
	 */
	public boolean isShowGroups() {
		return showGroups;
	}

	/**
	 * @param showGroups the showGroups to set
	 */
	public void setShowGroups(boolean showGroups) {
		this.showGroups = showGroups;
	}

	/**
	 * @return the itemsForPage
	 */
	public int getItemsForPage() {
		return itemsForPage;
	}

	/**
	 * @param itemsForPage the itemsForPage to set
	 */
	public void setItemsForPage(int itemsForPage) {
		this.itemsForPage = itemsForPage;
	}
	
	public void setGeoExplorerHandler(GeoExplorerHandler handler) {
		this.geoExplorerHandler = handler;
	}

	/**
	 * @return the geoExplorerHandler
	 */
	public GeoExplorerHandler getGeoExplorerHandler() {
		return geoExplorerHandler;
	}
	
	public void setWindowSize(int w, int h) {
		windowWidth = w;
		windowHeight = h;
	}

	public void setWindowMinSize(int w, int h) {
		windowMinWidth = w;
		windowMinHeight = h;
	}
	
	/**
	 * @return the windowWidth
	 */
	public int getWindowWidth() {
		return windowWidth;
	}
	
	/**
	 * @return the windowHeight
	 */
	public int getWindowHeight() {
		return windowHeight;
	}
	
	/**
	 * @return the windowMinWidth
	 */
	public int getWindowMinWidth() {
		return windowMinWidth;
	}
	
	/**
	 * @return the windowMinHeight
	 */
	public int getWindowMinHeight() {
		return windowMinHeight;
	}
	
	/**
	 * @return the displaySelectorsPanel
	 */
	public boolean isDisplaySelectorsPanel() {
		return displaySelectorsPanel;
	}
	
	/**
	 * @param displaySelectorsPanel the displaySelectorsPanel to set
	 */
	public void setDisplaySelectorsPanel(boolean displaySelectorsPanel) {
		this.displaySelectorsPanel = displaySelectorsPanel;
	}
	
	/**
	 * @return the referredWorkspace
	 */
	public String getReferredWorkspace() {
		return referredWorkspace;
	}
	
	/**
	 * @param referredWorkspace the referredWorkspace to set
	 */
	public void setReferredWorkspace(String referredWorkspace) {
		this.referredWorkspace = referredWorkspace;
	}
}
