/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.client.manage.release;

import java.util.Comparator;
import java.util.List;

//import org.gwtbootstrap3.client.ui.Pagination;
//import org.gwtbootstrap3.client.ui.gwt.CellTable;
import org.gcube.portlets.admin.gcubereleases.client.manage.HandlerReleaseOperation;
import org.gcube.portlets.admin.gcubereleases.client.view.DateUtilFormatter;
import org.gcube.portlets.admin.gcubereleases.shared.Release;

import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.Pagination;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * The Class ReleasesTable.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class ReleasesTable {
	
	protected CellTable<Release> cellTables;
	protected ListDataProvider<Release> dataProvider = new ListDataProvider<Release>();
	protected boolean showGroupId;

	//TABLE FIELDS
	private TextColumn<Release> releaseID;
	private TextColumn<Release> releaseName;
	private TextColumn<Release> isOnLine;
	private TextColumn<Release> urlDistribution;
	private HandlerReleaseOperation handlerReleaseOperation;
	
	/**
	 * Instantiates a new releases table.
	 *
	 * @param handlerReleaseOperation the handler release operation
	 */
	public ReleasesTable(HandlerReleaseOperation handlerReleaseOperation) {
		this.handlerReleaseOperation = handlerReleaseOperation;
		cellTables = new CellTable<Release>();
		cellTables.setStriped(true);
		cellTables.setBordered(true);
		cellTables.setWidth("100%", true);
		dataProvider.addDataDisplay(cellTables);
		initTable(cellTables, null, null);
		
	}

	/**
	 * Adds the releases.
	 *
	 * @param releases the releases
	 */
	public void addReleases(List<Release> releases) {
		dataProvider.getList().clear();

		for (Release rls : releases)
			addRelease(rls);

		cellTables.setPageSize(releases.size()+1);
		cellTables.redraw();
	}

	/**
	 * Adds the release.
	 *
	 * @param pckg the pckg
	 */
	private void addRelease(Release pckg) {
		dataProvider.getList().add(pckg);
		dataProvider.flush();
		dataProvider.refresh();
	}


	/**
	 * Inits the table.
	 *
	 * @param releaseTable the release table
	 * @param pager the pager
	 * @param pagination the pagination
	 */
	public void initTable(AbstractCellTable<Release> releaseTable, final SimplePager pager, final Pagination pagination) {
		releaseTable.setEmptyTableWidget(new Label("No data."));


		
		releaseID = new TextColumn<Release>() {
			@Override
			public String getValue(Release object) {
				return object.getId();
			}
		};
		releaseID.setSortable(true);
		releaseTable.addColumn(releaseID, "Release ID");
//		releaseTable.setColumnWidth(releaseID, 20.0, Unit.PCT);
		
		ListHandler<Release> releaseIDHandler = new ListHandler<Release>(dataProvider.getList());
		releaseIDHandler.setComparator(releaseID, new Comparator<Release>() {
			@Override
			public int compare(Release o1, Release o2) {
				return o1.getId().compareTo(o2.getId());
			}
		});
		
		releaseTable.addColumnSortHandler(releaseIDHandler);

		
		releaseName = new TextColumn<Release>() {
			@Override
			public String getValue(Release object) {
				return object.getName() != null ? object.getName() : "";
			}
		};	
		
		releaseName.setSortable(true);
		releaseTable.addColumn(releaseName, "Release Name");
//		releaseTable.setColumnWidth(releaseName, 20.0, Unit.PCT);
		
		ListHandler<Release> releaseNameHandler = new ListHandler<Release>(dataProvider.getList());
		releaseNameHandler.setComparator(releaseName, new Comparator<Release>() {
			@Override
			public int compare(Release o1, Release o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		releaseTable.addColumnSortHandler(releaseNameHandler);

		

		
		/*
		urlDistribution = new TextColumn<Release>() {
			@Override
			public String getValue(Release object) {
				return object.getUrl();
			}
		};	

		urlDistribution.setSortable(true);
		releaseTable.addColumn(urlDistribution, "URL Distribution");
		releaseTable.setColumnWidth(urlDistribution, 20.0, Unit.PCT);

		
		ListHandler<Release> urlDistributionHandler = new ListHandler<Release>(dataProvider.getList());
		isOnLineHandler.setComparator(urlDistribution, new Comparator<Release>() {
			@Override
			public int compare(Release o1, Release o2) {
				return o1.getUrl().compareTo(o2.getUrl());
			}
		});
		
		releaseTable.addColumnSortHandler(urlDistributionHandler);
		*/
		
//		TextColumn<Release> insertDate = new TextColumn<Release>() {
//			@Override
//			public String getValue(Release object) {
//				return DateUtilFormatter.getDateToString(object.getInsertTime());
//			}
//		};	
//		
//		releaseTable.addColumn(insertDate, "Inserted");
//		releaseTable.setColumnWidth(insertDate, 15.0, Unit.PCT);
		
		TextColumn<Release> updateDate = new TextColumn<Release>() {
			@Override
			public String getValue(Release object) {
				return DateUtilFormatter.getDateToString(object.getLatestUpdate());
			}
		};	
		
		releaseTable.addColumn(updateDate, "Latest Update");
		releaseTable.setColumnWidth(updateDate, 12.0, Unit.PCT);
		
		final SingleSelectionModel<Release> selectionModel = new SingleSelectionModel<Release>();
		selectionModel.addSelectionChangeHandler(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
//				Package p = selectionModel.getSelectedObject();
				// CellTables.this.driver.edit(person);
			}
		});
		
		isOnLine = new TextColumn<Release>() {
			@Override
			public String getValue(Release object) {
				return object.isOnLine()? "TRUE" : "FALSE";
			}
		};	
		
		isOnLine.setSortable(true);
		releaseTable.addColumn(isOnLine, "On Line");		
		releaseTable.setColumnWidth(isOnLine, 8.0, Unit.PCT);
		
		ListHandler<Release> isOnLineHandler = new ListHandler<Release>(dataProvider.getList());
		isOnLineHandler.setComparator(isOnLine, new Comparator<Release>() {
			@Override
			public int compare(Release o1, Release o2) {
				return Boolean.compare(o1.isOnLine(), o2.isOnLine());
			}
		});
		
		releaseTable.addColumnSortHandler(isOnLineHandler);
		
		ButtonCell deleteButton = new ButtonCell();
		Column<Release, String> deleteColumn = new Column<Release, String>(deleteButton) {
			@Override
			public String getValue(Release object) {
				return "Delete";
			}
		};

		deleteColumn.setFieldUpdater(new FieldUpdater<Release, String>() {
			public void update(int index, Release object, String value) {
//				Window.alert("You clicked: " + value);
				handlerReleaseOperation.delete(object);
			}
		});
		
		releaseTable.addColumn(deleteColumn, "Delete");
		releaseTable.setColumnWidth(deleteColumn, 10.0, Unit.PCT);
		
		
		ButtonCell updateButton = new ButtonCell();
		Column<Release, String> updateColumn = new Column<Release, String>(updateButton) {
			@Override
			public String getValue(Release object) {
				return "Edit";
			}
		};

		updateColumn.setFieldUpdater(new FieldUpdater<Release, String>() {
			public void update(int index, Release object, String value) {
//				Window.alert("You clicked: " + value);
				handlerReleaseOperation.update(object);
			}
		});
		
		releaseTable.addColumn(updateColumn, "Edit");
		releaseTable.setColumnWidth(updateColumn, 10.0, Unit.PCT);
		
	

//		exampleTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);
		releaseTable.setSelectionModel(selectionModel);
		// pager.setDisplay(exampleTable);
		// pagination.clear();

	}
	
	/**
	 * Gets the data grid.
	 *
	 * @return the data grid
	 */
	public CellTable<Release> getDataGrid() {
		return cellTables;
	}

	/**
	 * Gets the data provider.
	 *
	 * @return the data provider
	 */
	public ListDataProvider<Release> getDataProvider() {
		return dataProvider;
	}
}
