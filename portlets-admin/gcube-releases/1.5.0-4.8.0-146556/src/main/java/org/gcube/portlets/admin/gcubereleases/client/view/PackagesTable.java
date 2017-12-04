/**
 *
 */
package org.gcube.portlets.admin.gcubereleases.client.view;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import org.gwtbootstrap3.client.ui.Pagination;
//import org.gwtbootstrap3.client.ui.gwt.CellTable;
import org.gcube.portlets.admin.gcubereleases.client.GcubeReleasesAppController;
import org.gcube.portlets.admin.gcubereleases.client.event.PackageClickEvent;
import org.gcube.portlets.admin.gcubereleases.client.resources.Icons;
import org.gcube.portlets.admin.gcubereleases.shared.AccoutingReference;
import org.gcube.portlets.admin.gcubereleases.shared.Package;

import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.Pagination;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
/**
 * The Class PackagesTable.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class PackagesTable extends AbstractPackageTable{

	//TABLE FIELDS
	private TextColumn<Package> groupId;
	private TextColumn<Package> artifactID;
	private TextColumn<Package> packageVersion;
	private Column<Package, ImageResource> download;
	private Column<Package, ImageResource> javadoc;
	private Column<Package, ImageResource> wikidoc;
	private Column<Package, ImageResource> github;
	private Column<Package, ImageResource> mvnCoordinates;

	/**
	 * Instantiates a new packages table.
	 *
	 * @param showGroupId the show group id
	 */
	public PackagesTable(boolean showGroupId) {
		super(showGroupId);

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.client.view.AbstractPackageTable#addPackages(java.util.List)
	 */
	public void addPackages(List<Package> packages) {
		dataProvider.getList().clear();

		for (Package pckg : packages)
			addPackage(pckg);

		cellTables.setPageSize(packages.size()+1);
		cellTables.redraw();
	}

	/**
	 * Adds the package.
	 *
	 * @param pckg the pckg
	 */
	private void addPackage(Package pckg) {
		dataProvider.getList().add(pckg);
		dataProvider.flush();
		dataProvider.refresh();
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

		packageVersion = new TextColumn<Package>() {
			@Override
			public String getValue(Package object) {
				return object.getVersion() != null ? object.getVersion() : "";
			}
		};

		packageVersion.setCellStyleNames("img-centered-clickable");
		packageVersion.setSortable(true);
		packageTable.addColumn(packageVersion, "Version");

		packageTable.setColumnWidth(packageVersion, 10.0, Unit.PCT);

		ListHandler<Package> packageColHandler = new ListHandler<Package>(dataProvider.getList());
		packageColHandler.setComparator(packageVersion, new Comparator<Package>() {
			@Override
			public int compare(Package o1, Package o2) {
				return o1.getArtifactID().compareTo(o2.getArtifactID());
			}
		});

		packageTable.addColumnSortHandler(packageColHandler);

		//DOWNLOAD
	    ImageResourceCell myImgDownloadCell = new ImageResourceCell() {
	        public Set<String> getConsumedEvents() {
	            HashSet<String> events = new HashSet<String>();
	            events.add("click");
	            return events;
	        }

	    };

	    download = new Column<Package, ImageResource>(myImgDownloadCell) {
	        @Override
	        public ImageResource getValue(Package dataObj) {

	        	if(dataObj.getURL()!=null && !dataObj.getURL().isEmpty())
					return Icons.ICONS.download();

	        	return null;
	        }

	        @Override
	        public void onBrowserEvent(Context context, Element elem, Package object, NativeEvent event) {
	            super.onBrowserEvent(context, elem, object, event);
	            if ("click".equals(event.getType())) {
	            	  Window.open(object.getURL(), "_blank", "");
	            	  GcubeReleasesAppController.eventBus.fireEvent(new PackageClickEvent(object, AccoutingReference.DOWNLOAD));
	            }
	        }

	        /* (non-Javadoc)
	         * @see com.google.gwt.user.cellview.client.Column#render(com.google.gwt.cell.client.Cell.Context, java.lang.Object, com.google.gwt.safehtml.shared.SafeHtmlBuilder)
	         */
	        @Override
	        public void render(Context context, Package object, SafeHtmlBuilder sb) {
	        	super.render(context, object, sb);

	        }
	    };

	    download.setCellStyleNames("img-centered-clickable");
	    download.setSortable(false);
	    packageTable.addColumn(download, "Download");
		packageTable.setColumnWidth(download, 10.0, Unit.PCT);

		//MAVEN COORDS
	    ImageResourceCell myImgMavenCell = new ImageResourceCell() {
	        public Set<String> getConsumedEvents() {
	            HashSet<String> events = new HashSet<String>();
	            events.add("click");
	            return events;
	        }
	    };

	    mvnCoordinates = new Column<Package, ImageResource>(myImgMavenCell) {
	        @Override
	        public ImageResource getValue(Package dataObj) {
				return Icons.ICONS.maven();
	        }

	        @Override
	        public void onBrowserEvent(Context context, Element elem, Package object, NativeEvent event) {
	            super.onBrowserEvent(context, elem, object, event);
	            if ("click".equals(event.getType())) {
	            	String mavenURI = "http://maven.research-infrastructures.eu/nexus/index.html#nexus-search;quick~"+object.getArtifactID();
    	    		Window.open(mavenURI, "_blank", "");
    	    		GcubeReleasesAppController.eventBus.fireEvent(new PackageClickEvent(object, AccoutingReference.MAVEN_REPO));
	    	    }
	       }
	    };

	    mvnCoordinates.setCellStyleNames("img-centered-clickable");
	    mvnCoordinates.setSortable(false);
		packageTable.addColumn(mvnCoordinates, "Maven Repo");
		packageTable.setColumnWidth(mvnCoordinates, 10.0, Unit.PCT);

		//JAVADOC
	    ImageResourceCell myImgJavadocCell = new ImageResourceCell() {
	        public Set<String> getConsumedEvents() {
	            HashSet<String> events = new HashSet<String>();
	            events.add("click");
	            return events;
	        }
	    };

	    javadoc = new Column<Package, ImageResource>(myImgJavadocCell) {
	        @Override
	        public ImageResource getValue(Package dataObj) {

	        	if(dataObj.getJavadoc()!=null && !dataObj.getJavadoc().isEmpty() && dataObj.getJavadoc().compareTo("null")!=0)
					return Icons.ICONS.javadoc();

	        	return null;
	        }

	        @Override
	        public void onBrowserEvent(Context context, Element elem, Package object, NativeEvent event) {
	            super.onBrowserEvent(context, elem, object, event);
	            if ("click".equals(event.getType())) {
    	    		String javadocResolver = GcubeReleasesAppController.JAVADOC_RESOLVER_SERVLET+"?"
    		            		+"groupID="+com.google.gwt.http.client.URL.encode(object.getGroupID())+"&"
    		            		+"artifactID="+object.getArtifactID()+"&"
    		              		+"releaseID="+object.getReleaseIdRef() +"&"
    		              		+"version="+com.google.gwt.http.client.URL.encode(object.getVersion());

    	    		 Window.open(javadocResolver, "_blank", "");
    	    		 GcubeReleasesAppController.eventBus.fireEvent(new PackageClickEvent(object, AccoutingReference.JAVADOC));
	    	    }
	       }
	    };

	    javadoc.setCellStyleNames("img-centered-clickable");
	    javadoc.setSortable(false);
	    packageTable.addColumn(javadoc, "Javadoc");
		packageTable.setColumnWidth(javadoc, 10.0, Unit.PCT);


		//gitHubCell
	    ImageResourceCell gitHubCell = new ImageResourceCell() {
	        public Set<String> getConsumedEvents() {
	            HashSet<String> events = new HashSet<String>();
	            events.add("click");
	            return events;
	        }
	    };

	    github = new Column<Package, ImageResource>(gitHubCell) {
	        @Override
	        public ImageResource getValue(Package dataObj) {
	        	if(dataObj.getGitHubPath()!=null && !dataObj.getGitHubPath().isEmpty())
					return Icons.ICONS.github();

	        	return null;
	        }

	        @Override
	        public void onBrowserEvent(Context context, Element elem, Package object, NativeEvent event) {
	            super.onBrowserEvent(context, elem, object, event);
	            if ("click".equals(event.getType())) {
    	    		 Window.open(object.getGitHubPath(), "_blank", "");
    	    		 GcubeReleasesAppController.eventBus.fireEvent(new PackageClickEvent(object, AccoutingReference.GITHUB));
	    	    }
	       }
	    };

	    github.setCellStyleNames("img-centered-clickable");
	    github.setSortable(false);
		packageTable.addColumn(github, "GitHub");
		packageTable.setColumnWidth(github, 10.0, Unit.PCT);


		//wikidoc
	    ImageResourceCell myImgWikiCell = new ImageResourceCell() {
	        public Set<String> getConsumedEvents() {
	            HashSet<String> events = new HashSet<String>();
	            events.add("click");
	            return events;
	        }
	    };

	    wikidoc = new Column<Package, ImageResource>(myImgWikiCell) {
	        @Override
	        public ImageResource getValue(Package dataObj) {

	        	if(dataObj.getWikidoc()!=null && !dataObj.getWikidoc().isEmpty())
					return Icons.ICONS.wiki();

	        	return null;
	        }

	        @Override
	        public void onBrowserEvent(Context context, Element elem, Package object, NativeEvent event) {
	            super.onBrowserEvent(context, elem, object, event);
	            if ("click".equals(event.getType())) {
    	    		 Window.open(object.getWikidoc(), "_blank", "");
    	    		 GcubeReleasesAppController.eventBus.fireEvent(new PackageClickEvent(object, AccoutingReference.WIKI));
	    	    }
	       }
	    };

	    wikidoc.setCellStyleNames("img-centered-clickable");
	    wikidoc.setSortable(false);
		packageTable.addColumn(wikidoc, "Doc");
		packageTable.setColumnWidth(wikidoc, 10.0, Unit.PCT);


	    /*
		//AS LINK
	    final SafeHtmlRenderer<String> mvnCoordinatesRenderer = new AbstractSafeHtmlRenderer<String>() {
	      @Override
	      public SafeHtml render(String object) {
	        SafeHtmlBuilder sb = new SafeHtmlBuilder();
	        sb.appendHtmlConstant("<a target=\"_blank\" href=\""+object+"\">").appendEscaped("Maven Repo").appendHtmlConstant("</a>");
	        return sb.toSafeHtml();
	      }
	    };

	    mvnCoordinates = new Column<Package, String>(new ClickableTextCell(mvnCoordinatesRenderer)) {
	      @Override
	      public String getValue(Package object) {
	    	 return "http://maven.research-infrastructures.eu/nexus/index.html#nexus-search;quick~"+object.getArtifactID();
	      }
	    };
	    */
//		mvn.setSortable(false);

		final SingleSelectionModel<Package> selectionModel = new SingleSelectionModel<Package>();
		selectionModel.addSelectionChangeHandler(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
//				Package p = selectionModel.getSelectedObject();
				// CellTables.this.driver.edit(person);
			}
		});

//		exampleTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);
		packageTable.setSelectionModel(selectionModel);
		// pager.setDisplay(exampleTable);
		// pagination.clear();

	}

	/**
	 * Gets the data grid.
	 *
	 * @return the data grid
	 */
	public CellTable<Package> getDataGrid() {
		return cellTables;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.client.view.AbstractPackageTable#getDataProvider()
	 */
	public ListDataProvider<Package> getDataProvider() {
		return dataProvider;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.client.view.AbstractPackageTable#getCellTables()
	 */
	public CellTable<Package> getCellTables() {
		return cellTables;
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
	 * Gets the download.
	 *
	 * @return the download
	 */
	public Column<Package, ImageResource> getDownload() {
		return download;
	}

	/**
	 * Gets the javadoc.
	 *
	 * @return the javadoc
	 */
	public Column<Package, ImageResource> getJavadoc() {
		return javadoc;
	}

	/**
	 * Gets the wikidoc.
	 *
	 * @return the wikidoc
	 */
	public Column<Package, ImageResource> getWikidoc() {
		return wikidoc;
	}

	/**
	 * Gets the mvn coordinates.
	 *
	 * @return the mvn coordinates
	 */
	public Column<Package, ImageResource> getMvnCoordinates() {
		return mvnCoordinates;
	}
}
