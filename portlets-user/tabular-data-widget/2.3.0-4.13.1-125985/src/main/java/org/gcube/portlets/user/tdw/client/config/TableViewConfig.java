/**
 * 
 */
package org.gcube.portlets.user.tdw.client.config;

/**
 * The table view configuration.
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 */
public class TableViewConfig {
	
	protected RowStyleProvider rowStyleProvider;
	
	public TableViewConfig()
	{
	}

	/**
	 * @return the rowStyleProvider
	 */
	public RowStyleProvider getRowStyleProvider() {
		return rowStyleProvider;
	}

	/**
	 * @param rowStyleProvider the rowStyleProvider to set
	 */
	public void setRowStyleProvider(RowStyleProvider rowStyleProvider) {
		this.rowStyleProvider = rowStyleProvider;
	}

}
