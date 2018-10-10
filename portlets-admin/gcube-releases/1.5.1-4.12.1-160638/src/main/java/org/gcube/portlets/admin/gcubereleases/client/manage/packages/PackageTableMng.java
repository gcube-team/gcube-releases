/**
 *
 */
package org.gcube.portlets.admin.gcubereleases.client.manage.packages;

import java.util.Comparator;

import org.gcube.portlets.admin.gcubereleases.client.manage.HandlerPackageDeletable;
import org.gcube.portlets.admin.gcubereleases.client.view.AbstractPackageTable;
import org.gcube.portlets.admin.gcubereleases.shared.Package;

import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.Pagination;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * The Class PackageTableMng.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class PackageTableMng extends AbstractPackageTable{

	//TABLE FIELDS
	private TextColumn<Package> packageVersion;
	private TextColumn<Package> packageStatus;
	private TextColumn<Package> groupId;
	private TextColumn<Package> artifactID;
//	private Column<Package, String> deleteColumn;
//	private Column<Package, String> editColumn;
	private Column<Package, Boolean> checkColumn;
	private HandlerPackageDeletable handlerPackageDeletableInterface;

	/**
	 * Instantiates a new package table mng.
	 *
	 * @param showGroupId the show group id
	 * @param handlerPackageDeletableInterface the handler package deletable interface
	 */
	public PackageTableMng(boolean showGroupId, HandlerPackageDeletable handlerPackageDeletableInterface) {
		super(showGroupId);
		this.handlerPackageDeletableInterface = handlerPackageDeletableInterface;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.client.view.AbstractPackageTable#initTable(com.google.gwt.user.cellview.client.AbstractCellTable, com.google.gwt.user.cellview.client.SimplePager, com.github.gwtbootstrap.client.ui.Pagination)
	 */
	@Override
	public void initTable(AbstractCellTable<Package> packageTable, final SimplePager pager, final Pagination pagination) {
		packageTable.setEmptyTableWidget(new Label("No data."));

		CheckboxCell checkCell = new CheckboxCell(true, true);

		checkColumn = new Column<Package, Boolean>(checkCell){

			@Override
			public Boolean getValue(Package object) {
				return null;
			}

		};

		checkColumn.setFieldUpdater(new FieldUpdater<Package, Boolean>() {

			@Override
			public void update(int index, Package object, Boolean value) {
				if (value.booleanValue())
					handlerPackageDeletableInterface.delete(object);
				 else
					handlerPackageDeletableInterface.undelete(object);
			}

		});

		packageTable.addColumn(checkColumn, "Delete?");
		packageTable.setColumnWidth(checkColumn, 7.0, Unit.PCT);

		double artifactIDWidht = 40.0;

		if(showGroupId){

			groupId = new TextColumn<Package>() {
				@Override
				public String getValue(Package object) {
					return object!=null && object.getGroupID()!=null? object.getGroupID():"";
				}
			};

			groupId.setSortable(true);
			packageTable.addColumn(groupId, "Subsystem");
			packageTable.setColumnWidth(groupId, 30.0, Unit.PCT);
			artifactIDWidht = 20.0;

			ListHandler<Package> groupIdHandler = new ListHandler<Package>(dataProvider.getList());
			groupIdHandler.setComparator(groupId, new Comparator<Package>() {
				@Override
				public int compare(Package o1, Package o2) {
//					if (o1 == null) {
//				        return -1;
//				    } else if (o2 == null) {
//				        return 1;
//				    }
				    return o1.getGroupID().compareTo(o2.getGroupID());

				}
			});

			packageTable.addColumnSortHandler(groupIdHandler);
		}



		//ARTIFACT ID
		artifactID = new TextColumn<Package>() {
			@Override
			public String getValue(Package object) {
				return object.getArtifactID();
			}
		};
		artifactID.setSortable(true);
		packageTable.addColumn(artifactID, "Artifact ID");

		packageTable.setColumnWidth(artifactID, artifactIDWidht, Unit.PCT);

		ListHandler<Package> artifactIDHandler = new ListHandler<Package>(dataProvider.getList());
		artifactIDHandler.setComparator(artifactID, new Comparator<Package>() {
			@Override
			public int compare(Package o1, Package o2) {
				return o1.getArtifactID().compareTo(o2.getArtifactID());
			}
		});

		packageTable.addColumnSortHandler(artifactIDHandler);

		//VERSION
		packageVersion = new TextColumn<Package>() {
			@Override
			public String getValue(Package object) {
				return object.getVersion() != null ? object.getVersion() : "";
			}
		};

		packageVersion.setSortable(true);
		packageTable.addColumn(packageVersion, "Version");

		packageTable.setColumnWidth(packageVersion, 10.0, Unit.PCT);

		ListHandler<Package> packageVersionColHandler = new ListHandler<Package>(dataProvider.getList());
		packageVersionColHandler.setComparator(packageVersion, new Comparator<Package>() {
			@Override
			public int compare(Package o1, Package o2) {
				return o1.getArtifactID().compareTo(o2.getArtifactID());
			}
		});

		packageTable.addColumnSortHandler(packageVersionColHandler);

		//STATUS
		packageStatus = new TextColumn<Package>() {
			@Override
			public String getValue(Package object) {
				return object.getStatus() != null ? object.getStatus() : "";
			}
		};
		packageTable.addColumn(packageStatus, "Status");
		packageTable.setColumnWidth(packageStatus, 10.0, Unit.PCT);

		ListHandler<Package> packageStatusHandler = new ListHandler<Package>(dataProvider.getList());
		packageVersionColHandler.setComparator(packageStatus, new Comparator<Package>() {
			@Override
			public int compare(Package o1, Package o2) {

				if (o1 == null) {
					return -1;
				} else if (o2 == null) {
					return 1;
				}

				return o1.getStatus().compareTo(o2.getStatus());
			}
		});

		packageTable.addColumnSortHandler(packageStatusHandler);

		final SingleSelectionModel<Package> selectionModel = new SingleSelectionModel<Package>();
		selectionModel.addSelectionChangeHandler(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
//
			}
		});

		packageTable.setSelectionModel(selectionModel);

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.client.view.AbstractPackageTable#getCellTables()
	 */
	public CellTable<Package> getCellTables() {
		return cellTables;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.client.view.AbstractPackageTable#getDataProvider()
	 */
	public ListDataProvider<Package> getDataProvider() {
		return dataProvider;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.client.view.AbstractPackageTable#isShowGroupId()
	 */
	public boolean isShowGroupId() {
		return showGroupId;
	}

	/**
	 * Gets the group id.
	 *
	 * @return the group id
	 */
	public TextColumn<Package> getGroupId() {
		return groupId;
	}

	/**
	 * Gets the artifact id.
	 *
	 * @return the artifact id
	 */
	public TextColumn<Package> getArtifactID() {
		return artifactID;
	}

	/**
	 * Gets the package version.
	 *
	 * @return the package version
	 */
	public TextColumn<Package> getPackageVersion() {
		return packageVersion;
	}

	/**
	 * Gets the package status.
	 *
	 * @return the package status
	 */
	public TextColumn<Package> getPackageStatus() {
		return packageStatus;
	}

	/**
	 * Sets the cell tables.
	 *
	 * @param cellTables the new cell tables
	 */
	public void setCellTables(CellTable<Package> cellTables) {
		this.cellTables = cellTables;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.client.view.AbstractPackageTable#setDataProvider(com.google.gwt.view.client.ListDataProvider)
	 */
	public void setDataProvider(ListDataProvider<Package> dataProvider) {
		this.dataProvider = dataProvider;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.client.view.AbstractPackageTable#setShowGroupId(boolean)
	 */
	public void setShowGroupId(boolean showGroupId) {
		this.showGroupId = showGroupId;
	}

	/**
	 * Sets the group id.
	 *
	 * @param groupId the new group id
	 */
	public void setGroupId(TextColumn<Package> groupId) {
		this.groupId = groupId;
	}

	/**
	 * Sets the artifact id.
	 *
	 * @param artifactID the new artifact id
	 */
	public void setArtifactID(TextColumn<Package> artifactID) {
		this.artifactID = artifactID;
	}

	/**
	 * Sets the package version.
	 *
	 * @param packageVersion the new package version
	 */
	public void setPackageVersion(TextColumn<Package> packageVersion) {
		this.packageVersion = packageVersion;
	}

	/**
	 * Sets the package status.
	 *
	 * @param packageStatus the new package status
	 */
	public void setPackageStatus(TextColumn<Package> packageStatus) {
		this.packageStatus = packageStatus;
	}
}
