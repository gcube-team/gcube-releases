/**
 *
 */
package org.gcube.portlets.admin.gcubereleases.client.manage.statistics;

import java.util.Comparator;

import org.gcube.portlets.admin.gcubereleases.client.view.AbstractPackageTable;
import org.gcube.portlets.admin.gcubereleases.shared.Package;

import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.Pagination;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * The Class StatisticsTableManager.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 4, 2015
 */
public class StatisticsTableManager extends AbstractPackageTable{


	//TABLE FIELDS
	private TextColumn<Package> packageVersion;
	private TextColumn<Package> groupId;
	private TextColumn<Package> artifactID;
	private TextColumn<Package> packageDownloadNmb;
	private TextColumn<Package> packageJavodocNmb;
	private TextColumn<Package> packageMavenRepoNmb;
	private TextColumn<Package> packageWikiNmb;
	private TextColumn<Package> packageGitHubNmb;

	/**
	 * Instantiates a new package table mng.
	 *
	 * @param showGroupId the show group id
	 */
	public StatisticsTableManager(boolean showGroupId) {
		super(showGroupId);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.client.view.AbstractPackageTable#initTable(com.google.gwt.user.cellview.client.AbstractCellTable, com.google.gwt.user.cellview.client.SimplePager, com.github.gwtbootstrap.client.ui.Pagination)
	 */
	@Override
	public void initTable(AbstractCellTable<Package> packageTable, final SimplePager pager, final Pagination pagination) {
		packageTable.setEmptyTableWidget(new Label("No data."));

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

		packageDownloadNmb = new TextColumn<Package>() {
			@Override
			public String getValue(Package object) {
				return object.getAccouting()!=null?""+object.getAccouting().getDownloadNmb():"";
			}
		};
		packageTable.addColumn(packageDownloadNmb, "#Download");
		packageTable.setColumnWidth(packageDownloadNmb, 10.0, Unit.PCT);
		packageDownloadNmb.setSortable(true);

		ListHandler<Package> packageDownloadHandler = new ListHandler<Package>(dataProvider.getList());
		packageVersionColHandler.setComparator(packageDownloadNmb, new Comparator<Package>() {
			@Override
			public int compare(Package o1, Package o2) {

				if (o1.getAccouting() == null) {
					return -1;
				} else if (o2.getAccouting() == null) {
					return 1;
				}

				if(o1.getAccouting().getDownloadNmb()<o2.getAccouting().getDownloadNmb())
					return -1;
				else
					return 1;
			}
		});

		packageTable.addColumnSortHandler(packageDownloadHandler);

		packageJavodocNmb = new TextColumn<Package>() {
			@Override
			public String getValue(Package object) {
				return object.getAccouting()!=null?""+object.getAccouting().getJavadocNmb():"";
			}
		};

		packageTable.addColumn(packageJavodocNmb, "#Javadoc");
		packageTable.setColumnWidth(packageJavodocNmb, 10.0, Unit.PCT);
		packageJavodocNmb.setSortable(true);

		ListHandler<Package> packageJavadocHandler = new ListHandler<Package>(dataProvider.getList());
		packageVersionColHandler.setComparator(packageJavodocNmb, new Comparator<Package>() {
			@Override
			public int compare(Package o1, Package o2) {

				if (o1.getAccouting() == null) {
					return -1;
				} else if (o2.getAccouting() == null) {
					return 1;
				}

				if(o1.getAccouting().getJavadocNmb()<o2.getAccouting().getJavadocNmb())
					return -1;
				else
					return 1;
			}
		});

		packageTable.addColumnSortHandler(packageJavadocHandler);


		packageWikiNmb = new TextColumn<Package>() {
			@Override
			public String getValue(Package object) {
				return object.getAccouting()!=null?""+object.getAccouting().getWikiNmb():"";
			}
		};

		packageTable.addColumn(packageWikiNmb, "#Wiki");
		packageTable.setColumnWidth(packageWikiNmb, 10.0, Unit.PCT);
		packageWikiNmb.setSortable(true);

		ListHandler<Package> packageWikiHandler = new ListHandler<Package>(dataProvider.getList());
		packageVersionColHandler.setComparator(packageWikiNmb, new Comparator<Package>() {
			@Override
			public int compare(Package o1, Package o2) {

				if (o1.getAccouting() == null) {
					return -1;
				} else if (o2.getAccouting() == null) {
					return 1;
				}

				if(o1.getAccouting().getWikiNmb()<o2.getAccouting().getWikiNmb())
					return -1;
				else
					return 1;
			}
		});

		packageTable.addColumnSortHandler(packageWikiHandler);

		packageMavenRepoNmb = new TextColumn<Package>() {
			@Override
			public String getValue(Package object) {
				return object.getAccouting()!=null?""+object.getAccouting().getMavenRepoNmb():"";
			}
		};

		packageTable.addColumn(packageMavenRepoNmb, "#Maven Repo");
		packageTable.setColumnWidth(packageMavenRepoNmb, 10.0, Unit.PCT);
		packageMavenRepoNmb.setSortable(true);

		ListHandler<Package> packageMavenRepoHandler = new ListHandler<Package>(dataProvider.getList());
		packageVersionColHandler.setComparator(packageMavenRepoNmb, new Comparator<Package>() {
			@Override
			public int compare(Package o1, Package o2) {

				if (o1.getAccouting() == null) {
					return -1;
				} else if (o2.getAccouting() == null) {
					return 1;
				}

				if(o1.getAccouting().getMavenRepoNmb()<o2.getAccouting().getMavenRepoNmb())
					return -1;
				else
					return 1;
			}
		});

		packageTable.addColumnSortHandler(packageMavenRepoHandler);

//*******************

		packageGitHubNmb = new TextColumn<Package>() {
			@Override
			public String getValue(Package object) {
				return object.getAccouting()!=null?""+object.getAccouting().getGitHubNmb():"";
			}
		};

		packageTable.addColumn(packageGitHubNmb, "#GitHub");
		packageTable.setColumnWidth(packageGitHubNmb, 10.0, Unit.PCT);
		packageGitHubNmb.setSortable(true);

		ListHandler<Package> packageMavenGitHubHandler = new ListHandler<Package>(dataProvider.getList());
		packageVersionColHandler.setComparator(packageGitHubNmb, new Comparator<Package>() {
			@Override
			public int compare(Package o1, Package o2) {

				if (o1.getAccouting() == null) {
					return -1;
				} else if (o2.getAccouting() == null) {
					return 1;
				}

				if(o1.getAccouting().getGitHubNmb()<o2.getAccouting().getGitHubNmb())
					return -1;
				else
					return 1;
			}
		});

		packageTable.addColumnSortHandler(packageMavenGitHubHandler);

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
}
