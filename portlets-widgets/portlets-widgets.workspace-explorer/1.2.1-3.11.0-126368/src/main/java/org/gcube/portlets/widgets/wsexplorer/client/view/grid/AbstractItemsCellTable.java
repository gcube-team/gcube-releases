package org.gcube.portlets.widgets.wsexplorer.client.view.grid;

import java.util.List;

import org.gcube.portlets.widgets.wsexplorer.client.event.ClickItemEvent;
import org.gcube.portlets.widgets.wsexplorer.client.resources.CellTableResources;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;

import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.Pagination;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * The Class AbstractItemTable.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Feb 19, 2015
 */
public abstract class AbstractItemsCellTable {

	protected CellTable<Item> cellTable;
	protected ListDataProvider<Item> dataProvider = new ListDataProvider<Item>();
	protected Item itemContextMenu = null;
	protected boolean showMoreInfo;
	protected final SingleSelectionModel<Item> ssm;
	private final HandlerManager eventBus;

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
	public abstract void initTable(AbstractCellTable<Item> ItemTable, final SimplePager pager, final Pagination pagination);


	/**
	 * Instantiates a new abstract items cell table.
	 *
	 * @param eventBus the event bus
	 * @param fireOnClick the fire on click
	 */
	public AbstractItemsCellTable(HandlerManager eventBus, boolean fireOnClick) {
		this.eventBus = eventBus;
		this.showMoreInfo = fireOnClick;
		cellTable = new CellTable<Item>(1, CellTableResources.INSTANCE);
		cellTable.addStyleName("table-overflow");
		cellTable.setStriped(true);
		cellTable.setCondensed(true);
		cellTable.setWidth("100%", true);
		dataProvider.addDataDisplay(cellTable);
//		initTable(cellTable, null, null);
		cellTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		ssm = new SingleSelectionModel<Item>();
		cellTable.setSelectionModel(ssm);
		ssm.addSelectionChangeHandler(new Handler() {
		    @Override
		    public void onSelectionChange(final SelectionChangeEvent event)
		    {
		        final Item selectedObject = ssm.getSelectedObject();
		        if(selectedObject!=null){
			        GWT.log("Clicked: "+selectedObject);
//			        selectedItem(selectedObject);
			        if(showMoreInfo)
			        	AbstractItemsCellTable.this.eventBus.fireEvent(new ClickItemEvent(selectedObject));
		        }
		    }
		});

		cellTable.addDomHandler(new DoubleClickHandler() {

	        @Override
	        public void onDoubleClick(final DoubleClickEvent event) {
	        	Item selected = ssm.getSelectedObject();
	            if (selected != null) {
	            	GWT.log("Double Click: "+selected);
	            	AbstractItemsCellTable.this.eventBus.fireEvent(new org.gcube.portlets.widgets.wsexplorer.client.event.LoadFolderEvent(selected));
	            }
	        }
	    },
	    DoubleClickEvent.getType());

		MenuBar options = new MenuBar(true);
		ScheduledCommand openCommand = new ScheduledCommand() {

			@Override
			public void execute() {
				AbstractItemsCellTable.this.eventBus.fireEvent(new org.gcube.portlets.widgets.wsexplorer.client.event.LoadFolderEvent(itemContextMenu));
			}
		};

		MenuItem openItem = new MenuItem("Open", openCommand);
		options.addItem(openItem);
	    final DialogBox menuWrapper = new DialogBox(true);
	    menuWrapper.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
	    menuWrapper.getElement().getStyle().setZIndex(10000);
	    menuWrapper.add(options);
		cellTable.sinkEvents(Event.ONCONTEXTMENU);

		cellTable.addHandler(new ContextMenuHandler() {
			@Override
			public void onContextMenu(ContextMenuEvent event) {
				/*
				GWT.log("On Context Menu: " + event.getNativeEvent().getEventTarget().toString());
				Item selectedObject = (Item) event.getSource();
				itemContextMenu = selectedObject;
				if (selectedObject != null && selectedObject.isFolder()) {
					event.preventDefault();
					event.stopPropagation();
					menuWrapper.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
					menuWrapper.show();
				}*/
			}
		}, ContextMenuEvent.getType());

//		ssm.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
//
//			@Override
//			public void onSelectionChange(SelectionChangeEvent event) {
//				// changed the context menu selection
//				GWT.log("Selected item is" + ssm.getSelectedObject());
//
//			}
//		});

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
			ssm.clear();
		}

		dataProvider.getList().addAll(items);
		dataProvider.flush();
		dataProvider.refresh();

		cellTable.setPageSize(items.size()+1);
		cellTable.redraw();
	}

	/**
	 * Gets the cell tables.
	 *
	 * @return the cell tables
	 */
	public CellTable<Item> getCellTable() {
		return cellTable;
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
