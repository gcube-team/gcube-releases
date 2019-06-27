package org.gcube.portlets.user.workspaceexplorerapp.client.grid;

import java.util.List;
import java.util.Set;

import org.gcube.portlets.user.workspaceexplorerapp.client.download.DownloadType;
import org.gcube.portlets.user.workspaceexplorerapp.client.event.ClickItemEvent;
import org.gcube.portlets.user.workspaceexplorerapp.client.event.DownloadItemEvent;
import org.gcube.portlets.user.workspaceexplorerapp.client.event.LoadFolderEvent;
import org.gcube.portlets.user.workspaceexplorerapp.client.event.RightClickItemEvent;
import org.gcube.portlets.user.workspaceexplorerapp.shared.Item;

import com.github.gwtbootstrap.client.ui.Pagination;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Event;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

/**
 * The Class AbstractItemTable.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Feb 19, 2015
 */
public abstract class AbstractItemsCellTable {

	protected DataGrid<Item> dataGrid;
	protected ListDataProvider<Item> dataProvider = new ListDataProvider<Item>();
	protected Item itemContextMenu = null;
	protected boolean showMoreInfo;
	protected final MultiSelectionModel<Item> msm;
	protected final HandlerManager eventBus;

	/**
	 * Inits the table.
	 *
	 * @param ItemTable
	 *            the Item table
	 * @param pager
	 *            the pager
	 * @param pagination
	 *            the pagination
	 */
	public abstract void initTable(final SimplePager pager, final Pagination pagination);


	/**
	 * Instantiates a new abstract items cell table.
	 *
	 * @param eventBus the event bus
	 * @param fireOnClick the fire on click
	 */
	public AbstractItemsCellTable(HandlerManager eventBus, boolean fireOnClick) {
		this.eventBus = eventBus;
		this.showMoreInfo = fireOnClick;
		dataGrid = new DataGrid<Item>(1);
		dataGrid.getElement().setId("data_grid_explorer");
		dataGrid.getElement().setAttribute("id", "data_grid_explorer");
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataProvider.addDataDisplay(dataGrid);
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		msm = new MultiSelectionModel<Item>();
		dataGrid.setSelectionModel(msm);
		msm.addSelectionChangeHandler(new Handler() {
		    @Override
		    public void onSelectionChange(final SelectionChangeEvent event)
		    {
		        final Set<Item> selectedObject = msm.getSelectedSet();
		        GWT.log("Clicked: "+selectedObject);
//		        dataGrid.getRowContainer().removeClassName("dataGridSelectedRow");
		        if(selectedObject!=null && !selectedObject.isEmpty()){
//			        GWT.log("Current Selected Row : "+ dataGrid.getKeyboardSelectedRow());
//			        dataGrid.getRowElement(dataGrid.getKeyboardSelectedRow()).addClassName("dataGridSelectedRow");
//			        selectedItem(selectedObject);
			        if(showMoreInfo)
			        	AbstractItemsCellTable.this.eventBus.fireEvent(new ClickItemEvent(selectedObject));
		        }
		    }
		});

		dataGrid.addDomHandler(new DoubleClickHandler() {

	        @Override
	        public void onDoubleClick(final DoubleClickEvent event) {
	        	Set<Item> selected = msm.getSelectedSet();
	        	GWT.log("Double Click: "+selected);
	            if (selected != null && !selected.isEmpty()) {
	            	Item item = selected.iterator().next();
	            	if(item.isFolder() || item.isRoot() || item.isSharedFolder() || item.isSpecialFolder())
	            		//IN CASE OF FOLDER OPEN IT
	            		AbstractItemsCellTable.this.eventBus.fireEvent(new LoadFolderEvent(selected.iterator().next()));
	            	else //IN CASE OF FILE DOWNLOAD IT...
	            		AbstractItemsCellTable.this.eventBus.fireEvent(new DownloadItemEvent(item, DownloadType.DOWNLOAD));
	            }
	        }
	    },
	    DoubleClickEvent.getType());

		dataGrid.addDomHandler(new ContextMenuHandler() {

			@Override
			public void onContextMenu(ContextMenuEvent event) {
				event.preventDefault();
				event.stopPropagation();
			}
		}, ContextMenuEvent.getType());


		dataGrid.addCellPreviewHandler(new CellPreviewEvent.Handler<Item>() {

		    @Override
		    public void onCellPreview(CellPreviewEvent<Item> event) {
//		    	 GWT.log("CellPreview click");
		        if (event.getNativeEvent().getButton() == NativeEvent.BUTTON_RIGHT && event.getNativeEvent().getType().equals("contextmenu")) {
		        	event.getNativeEvent().preventDefault();
		        	event.getNativeEvent().stopPropagation();
//		            int clickedRow = event.getIndex();
		            Item item = event.getValue();
		            GWT.log("Right click on "+ item);
		            AbstractItemsCellTable.this.eventBus.fireEvent(new RightClickItemEvent(event.getNativeEvent().getClientX(),  event.getNativeEvent().getClientY(), item));
		            event.setCanceled(true);
		        }
		    }
		});

		/*MenuBar options = new MenuBar(true);
		ScheduledCommand openCommand = new ScheduledCommand() {

			@Override
			public void execute() {
				AbstractItemsCellTable.this.eventBus.fireEvent(new LoadFolderEvent(itemContextMenu));
			}
		};

		MenuItem openItem = new MenuItem("Open", openCommand);
		options.addItem(openItem);
	    final DialogBox menuWrapper = new DialogBox(true);
	    menuWrapper.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
	    menuWrapper.getElement().getStyle().setZIndex(10000);
	    menuWrapper.add(options);*/
		dataGrid.sinkEvents(Event.ONCONTEXTMENU);
	}


	/**
	 * Update items.
	 *
	 * @param items the items
	 * @param removeOldItems the remove old items
	 */
	public void updateItems(List<Item> items, boolean removeOldItems) {

		if(removeOldItems){
			dataProvider.getList().clear();
			msm.clear();
		}

		dataProvider.getList().addAll(items);
		dataGrid.setPageSize(items.size()+1);
		dataGrid.redraw();
	}

	/**
	 * Gets the cell tables.
	 *
	 * @return the cell tables
	 */
	public DataGrid<Item> getCellTable() {
		return dataGrid;
	}

	/**
	 * Gets the data provider.
	 *
	 * @return the data provider
	 */
	public ListDataProvider<Item> getDataProvider() {
		return dataProvider;
	}

	/**
	 * Sets the data provider.
	 *
	 * @param dataProvider
	 *            the new data provider
	 */
	public void setDataProvider(ListDataProvider<Item> dataProvider) {
		this.dataProvider = dataProvider;
	}
}
