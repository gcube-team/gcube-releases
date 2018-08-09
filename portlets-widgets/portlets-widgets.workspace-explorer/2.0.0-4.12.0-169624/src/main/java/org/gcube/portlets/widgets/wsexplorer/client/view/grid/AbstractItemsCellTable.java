package org.gcube.portlets.widgets.wsexplorer.client.view.grid;

import java.util.List;

import org.gcube.portlets.widgets.wsexplorer.client.event.ClickItemEvent;

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
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;


/**
 * The Class AbstractItemsCellTable.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 11, 2017
 * @param <T> the generic type
 */
public abstract class AbstractItemsCellTable<T> {

	protected SortedCellTable<T> sortedCellTable;
//	protected ListDataProvider<T> dataProvider = new ListDataProvider<>();
	protected T itemContextMenu = null;
	protected boolean fireEventOnClick = true;
	protected SingleSelectionModel<T> ssm;
	protected HandlerManager eventBus;


	/**
	 * Inits the table.
	 *
	 * @param pager the pager
	 * @param pagination the pagination
	 * @param dataProvider the data provider
	 */
	public abstract void initTable(final SimplePager pager, final Pagination pagination, AbstractDataProvider<T> dataProvider);


	/**
	 * Inits the abstract table.
	 *
	 * @param eventBus the event bus
	 * @param fireOnClick the fire on click
	 * @param dataProvider the data provider
	 * @param pageSize the page size
	 */
	protected void initAbstractTable(HandlerManager eventBus, boolean fireOnClick, AbstractDataProvider<T> dataProvider, int pageSize){
		this.eventBus = eventBus;
		this.fireEventOnClick = fireOnClick;
		sortedCellTable = new SortedCellTable<T>(pageSize, dataProvider);
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
			        if(fireEventOnClick)
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
			}
		}, ContextMenuEvent.getType());

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
	public SortedCellTable<T> getCellTable() {
		return sortedCellTable;
	}


	/**
	 * Checks if is fire event on click.
	 *
	 * @return the fireEventOnClick
	 */
	public boolean isFireEventOnClick() {

		return fireEventOnClick;
	}



	/**
	 * @param fireEventOnClick the fireEventOnClick to set
	 */
	public void setFireEventOnClick(boolean fireEventOnClick) {

		this.fireEventOnClick = fireEventOnClick;
	}


	/**
	 * Adds the items.
	 *
	 * @param items the items
	 */
	public void addItems(List<T> items){

		AbstractDataProvider<T> dataProvider = sortedCellTable.getDataProvider();

		if(dataProvider instanceof ListDataProvider){
			List<T> ldp = ((ListDataProvider<T>) dataProvider).getList();
			for (int i=0; i<items.size(); i++) {
				ldp.add(i, items.get(i));
			}

			sortedCellTable.setPageSize(items.size()+1);
			sortedCellTable.redraw();
		}else if (dataProvider instanceof AsyncDataProvider){

			//TODO ???

		}
	}
}
