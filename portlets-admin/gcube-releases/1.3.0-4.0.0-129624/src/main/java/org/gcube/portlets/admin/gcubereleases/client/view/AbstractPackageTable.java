/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.client.view;

import java.util.List;

import org.gcube.portlets.admin.gcubereleases.shared.Package;

import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.Pagination;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.view.client.ListDataProvider;

/**
 * The Class AbstractPackageTable.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public abstract class AbstractPackageTable {
	
	protected CellTable<Package> cellTables;
	protected ListDataProvider<Package> dataProvider = new ListDataProvider<Package>();
	protected boolean showGroupId;

	/**
	 * Inits the table.
	 *
	 * @param packageTable the package table
	 * @param pager the pager
	 * @param pagination the pagination
	 */
	public abstract void initTable(AbstractCellTable<Package> packageTable, final SimplePager pager, final Pagination pagination);
	
	/**
	 * Instantiates a new abstract package table.
	 *
	 * @param showGroupId the show group id
	 */
	public AbstractPackageTable(boolean showGroupId) {
		this.showGroupId = showGroupId;
		/*cellTables = new CellTable<Package>(){
			
		  @Override
          protected void doSetHeaderVisible(boolean isFooter, boolean isVisible) {
            super.doSetHeaderVisible(isFooter, isVisible);

            NodeList<Node> nodelist = getTableHeadElement().getChild(0).getChildNodes();
            for (int i = 0; i < nodelist.getLength(); i++) {
              Node el = nodelist.getItem(i);
              if (el instanceof Element) {
            	  String element = ((Element) el).toString();
            	  GWT.log(element);
//                if (COLUMN_STYLES[i] != null) {
//                  ((Element) el).addClassName(COLUMN_STYLES[i]);
//                }
              }
            }
          }
		};*/
		cellTables = new CellTable<Package>();
		cellTables.addStyleName("table-overflow");
		cellTables.setStriped(true);
		cellTables.setBordered(true);
//		cellTables.setCondensed(true);
		cellTables.setWidth("100%", true);
		dataProvider.addDataDisplay(cellTables);
		initTable(cellTables, null, null);
	}

	/**
	 * Adds the packages.
	 *
	 * @param packages the packages
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
	
	/**
	 * Gets the cell tables.
	 *
	 * @return the cell tables
	 */
	public CellTable<Package> getCellTables() {
		return cellTables;
	}

	/**
	 * Gets the data provider.
	 *
	 * @return the data provider
	 */
	public ListDataProvider<Package> getDataProvider() {
		return dataProvider;
	}

	/**
	 * Checks if is show group id.
	 *
	 * @return true, if is show group id
	 */
	public boolean isShowGroupId() {
		return showGroupId;
	}

	/**
	 * Sets the data provider.
	 *
	 * @param dataProvider the new data provider
	 */
	public void setDataProvider(ListDataProvider<Package> dataProvider) {
		this.dataProvider = dataProvider;
	}

	/**
	 * Sets the show group id.
	 *
	 * @param showGroupId the new show group id
	 */
	public void setShowGroupId(boolean showGroupId) {
		this.showGroupId = showGroupId;
	}
}
