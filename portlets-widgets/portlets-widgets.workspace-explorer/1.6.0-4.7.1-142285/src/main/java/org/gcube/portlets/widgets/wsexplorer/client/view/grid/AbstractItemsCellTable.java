package org.gcube.portlets.widgets.wsexplorer.client.view.grid;

import java.util.List;

import org.gcube.portlets.widgets.wsexplorer.client.event.ClickItemEvent;

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
public abstract class AbstractItemsCellTable<T> {

	protected SortedCellTable<T> sortedCellTable;
//	protected ListDataProvider<T> dataProvider = new ListDataProvider<>();
	protected T itemContextMenu = null;
	protected boolean showMoreInfo;
	protected final SingleSelectionModel<T> ssm;
	private final HandlerManager eventBus;

	/**
	 * Inits the table.
	 *
	 * @param pager            the pager
	 * @param pagination            the pagination
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
		sortedCellTable = new SortedCellTable<T>();
		sortedCellTable.addStyleName("table-explorer");
//		cellTable.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		sortedCellTable.addStyleName("table-explorer-vertical-middle");
		sortedCellTable.setStriped(true);
		sortedCellTable.setCondensed(true);
		sortedCellTable.setWidth("100%", true);
//		dataProvider.addDataDisplay(sortedCellTable);
//		initTable(cellTable, null, null);
		sortedCellTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		ssm = new SingleSelectionModel<T>();
		sortedCellTable.setSelectionModel(ssm);
		ssm.addSelectionChangeHandler(new Handler() {
		    @Override
		    public void onSelectionChange(final SelectionChangeEvent event)
		    {
		        final T selectedObject = ssm.getSelectedObject();
		        if(selectedObject!=null){
			        GWT.log("Clicked: "+selectedObject);
//			        selectedItem(selectedObject);
			        if(showMoreInfo)
			        	AbstractItemsCellTable.this.eventBus.fireEvent(new ClickItemEvent<T>(selectedObject));
		        }
		    }
		});

		sortedCellTable.addDomHandler(new DoubleClickHandler() {

	        @Override
	        public void onDoubleClick(final DoubleClickEvent event) {
	        	T selected = ssm.getSelectedObject();
	            if (selected != null) {
	            	GWT.log("Double Click: "+selected);
	            	AbstractItemsCellTable.this.eventBus.fireEvent(new org.gcube.portlets.widgets.wsexplorer.client.event.LoadFolderEvent<T>(selected));
	            }
	        }
	    },
	    DoubleClickEvent.getType());

		MenuBar options = new MenuBar(true);
		ScheduledCommand openCommand = new ScheduledCommand() {

			@Override
			public void execute() {
				AbstractItemsCellTable.this.eventBus.fireEvent(new org.gcube.portlets.widgets.wsexplorer.client.event.LoadFolderEvent<T>(itemContextMenu));
			}
		};

		MenuItem openItem = new MenuItem("Open", openCommand);
		options.addItem(openItem);
	    final DialogBox menuWrapper = new DialogBox(true);
	    menuWrapper.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
	    menuWrapper.getElement().getStyle().setZIndex(10000);
	    menuWrapper.add(options);
		sortedCellTable.sinkEvents(Event.ONCONTEXTMENU);

		sortedCellTable.addHandler(new ContextMenuHandler() {
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
	public void updateItems(List<T> items, boolean removeOldItems) {

		if(removeOldItems){
//			dataProvider.getList().clear();
			ssm.clear();
		}

		sortedCellTable.setList(items);
//		dataProvider.flush();
//		dataProvider.refresh();

		sortedCellTable.setPageSize(items.size()+1);
		sortedCellTable.redraw();
	}

	/**
	 * Gets the cell tables.
	 *
	 * @return the cell tables
	 */
	public CellTable<T> getCellTable() {
		return sortedCellTable;
	}

	/**
	 * Gets the data provider.
	 *
	 * @return the data provider
	 */
	public ListDataProvider<T> getDataProvider() {
		return sortedCellTable.getDataProvider();
	}
}
