/**
 *
 */
package org.gcube.portlets.widgets.wsexplorer.client.explore;

import java.util.List;

import org.gcube.portlets.widgets.wsexplorer.client.Util;
import org.gcube.portlets.widgets.wsexplorer.client.event.BreadcrumbClickEvent;
import org.gcube.portlets.widgets.wsexplorer.client.event.BreadcrumbClickEventHandler;
import org.gcube.portlets.widgets.wsexplorer.client.event.ClickItemEvent;
import org.gcube.portlets.widgets.wsexplorer.client.event.ClickItemEventHandler;
import org.gcube.portlets.widgets.wsexplorer.client.event.LoadFolderEvent;
import org.gcube.portlets.widgets.wsexplorer.client.event.LoadFolderEventHandler;
import org.gcube.portlets.widgets.wsexplorer.client.view.WorkspaceExplorerPaginated;
import org.gcube.portlets.widgets.wsexplorer.client.view.grid.ItemsTable.DISPLAY_FIELD;
import org.gcube.portlets.widgets.wsexplorer.shared.FilterCriteria;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;


/**
 * The Class WorkspaceResourcesExplorerPanelPaginated.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 5, 2017
 */
public class WorkspaceResourcesExplorerPanelPaginated extends WorkspaceResourcesExplorerPanel {

	protected WorkspaceExplorerPaginated wsExplorerPaginated;
	protected FlowPanel centerPanel = null;
	private FlowPanel centerDock;
	private int southPanelSize = 40;
	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wsexplorer.client.explore.WorkspaceResourcesExplorerPanel#onLoad()
	 */
	@Override
	protected void onLoad() {
		//centerPanel = new FlowPanel();
		// TODO Auto-generated method stub
		super.onLoad();
		Util.console("WorkspaceResourcesExplorerPanelPaginated set new height to centerDock panel: "+parentHeight);
		if(parentHeight>0){
			setHeightToInternalScroll(parentHeight);
		}
	}


	/**
	 * Instantiates a new workspace resources explorer panel paginated.
	 *
	 * @param folderId the folder id
	 * @param showOnlyFolders the show only folders
	 * @throws Exception the exception
	 */
	public WorkspaceResourcesExplorerPanelPaginated(String folderId, boolean showOnlyFolders) throws Exception {
		super(folderId, showOnlyFolders);
	}


	/**
	 * Instantiates a new workspace resources explorer panel paginated.
	 *
	 * @param folderId the folder id
	 * @param folderName the folder name
	 * @throws Exception the exception
	 */
	public WorkspaceResourcesExplorerPanelPaginated(String folderId, String folderName) throws Exception {
		super(folderId, folderName);
	}


	/**
	 * Instantiates a new workspace resources explorer panel paginated.
	 *
	 * @param folderId the folder id
	 * @param showOnlyFolders the show only folders
	 * @param showProperties the show properties
	 * @param filter the filter
	 * @throws Exception the exception
	 */
	public WorkspaceResourcesExplorerPanelPaginated(String folderId, boolean showOnlyFolders, List<String> showProperties, FilterCriteria filter) throws Exception {
		super(folderId, showOnlyFolders, showProperties, filter);
	}



	/**
	 * Instantiates a new workspace resources explorer panel paginated.
	 *
	 * @param folderId the folder id
	 * @param showOnlyFolders the show only folders
	 * @param showProperties the show properties
	 * @param filter the filter
	 * @param showGcubeInfo the show gcube info
	 * @param sortByColumn the sort by column
	 * @throws Exception
	 */
	public WorkspaceResourcesExplorerPanelPaginated(String folderId, boolean showOnlyFolders, List<String> showProperties, FilterCriteria filter, boolean showGcubeInfo, DISPLAY_FIELD sortByColumn) throws Exception {
		super(folderId,showOnlyFolders,showProperties,filter,showGcubeInfo,sortByColumn);
	}

	/**
	 * Inits the explorer.
	 *
	 * @param folderId the folder id
	 * @param folderName the folder name
	 * @param selectableTypes the selectable types
	 * @param showableTypes the showable types
	 * @param showProperties the show properties
	 * @param filter the filter
	 * @param showGcubeInfo the show gcube info - shows all the properties associated to a file (or a gcube item) stored into related gcube item by opening a popup window when clicking on the item
	 * @param sortByColumn the sort by column
	 * @throws Exception the exception
	 */
	@Override
	protected void initExplorer(String folderId, String folderName, ItemType[] selectableTypes, ItemType[] showableTypes, List<String> showProperties, FilterCriteria filter, boolean showGcubeInfo, DISPLAY_FIELD sortByColumn,  DISPLAY_FIELD... displayFields) throws Exception{
		GWT.log("Using paginated init");
		setParameters(folderId, folderName, showProperties, filter);
		bindEvents();
		wsExplorerPaginated = new WorkspaceExplorerPaginated(eventBus, filter, showableTypes, selectableTypes, showProperties, showGcubeInfo, sortByColumn, displayFields);
		wsExplorerPaginated.initPagination(ITEMS_PER_PAGE);
		//wsExplorer.initTable(dataProvider);
		Item item = new Item(folderId, folderName, true);
		wsExplorerPaginated.loadFolder(item, true, ITEM_START_INDEX, ITEMS_PER_PAGE, true);
		initPanel("");
		super.wsExplorer = wsExplorerPaginated;
	}


	/**
	 * Purge cache. It performs cache purge.
	 */
	public void purgeCache(){
		wsExplorerPaginated.purgeCache();
	}

	/**
	 * Hard refresh. It performs a purge of cached page and reload the folder passed in input
	 *
	 * @throws Exception the exception
	 */
	public void hardRefresh() throws Exception{
		wsExplorerPaginated.purgeCache();
		super.refreshRootFolderView();
	}

	/**
	 * Bind events.
	 */
	private void bindEvents(){

		eventBus.addHandler(ClickItemEvent.TYPE, new ClickItemEventHandler() {

			@Override
			public <T> void onClick(final ClickItemEvent<T> clickItemEvent) {
				isSelect = false;
				Item item = wsExplorerPaginated.getItemSelected();
				//Return if item is not selectable
				if(!itemIsSelectable(item)){
					notifyNotValidSelection();
					return;
				}

				notifySelectedItem(wsExplorerPaginated.getItemSelected());
				isSelect = true;
			}
		});

		eventBus.addHandler(LoadFolderEvent.TYPE, new LoadFolderEventHandler() {

			@Override
			public <T> void onLoadFolder(LoadFolderEvent<T> loadFolderEvent) {

				if(loadFolderEvent.getTargetItem()!=null){

					if(loadFolderEvent.getTargetItem() instanceof Item){
						Item item = (Item) loadFolderEvent.getTargetItem();
						if(item.isFolder()){
							try {
								wsExplorerPaginated.loadFolder(item, true, ITEM_START_INDEX, ITEMS_PER_PAGE, true);
								loadParentBreadcrumbByItemId(item.getId(), true);
								clearMoreInfo();
							} catch (Exception e) {
								GWT.log(e.getMessage());
							}
						}
					}
				}
			}
		});


		eventBus.addHandler(BreadcrumbClickEvent.TYPE, new BreadcrumbClickEventHandler() {

			@Override
			public void onBreadcrumbClick(BreadcrumbClickEvent breadcrumbClickEvent) {
				eventBus.fireEvent(new LoadFolderEvent<Item>(breadcrumbClickEvent.getTargetItem()));
			}
		});
	}

	/**
	 * Inits the panel.
	 *
	 * @param captionTxt the caption txt is the tool-tip
	 */
	protected void initPanel(String captionTxt) {
		super.captionTxt = captionTxt;
		if(super.captionTxt!=null && !super.captionTxt.isEmpty())
			setTitle(super.captionTxt);
	    add(breadcrumbs, DockPanel.NORTH);
	    setCellHeight(breadcrumbs, offsetBreadcrumb+"px");
	    centerDock = new FlowPanel();
	    centerDock.addStyleName("we-dock-center-panel");
//	    centerDock.getElement().getStyle().setOverflowY(Overflow.AUTO);
//	    centerDock.getElement().getStyle().setBorderWidth(1.0, Unit.PX);
//	    centerDock.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
//	    centerDock.getElement().getStyle().setBorderColor("#DDD");

	    centerDock.add(wsExplorerPaginated.getCellPanel());
	    add(centerDock, DockPanel.CENTER);
	    add(wsExplorerPaginated.getPagerPanel(), DockPanel.SOUTH);
	}

	/**
	 * Sets the height to internal scroll.
	 *
	 * @param height the new height to internal scroll
	 */
	public void setHeightToInternalScroll(int height){

		super.setHeightToInternalScroll(height-southPanelSize);
		int nh = super.getNewHeightForContainer(height-southPanelSize);
		if(nh>0){
			Util.console("Set new height to center Dock panel: "+nh);
			centerDock.setHeight(nh+"px");
		}
	}

}
