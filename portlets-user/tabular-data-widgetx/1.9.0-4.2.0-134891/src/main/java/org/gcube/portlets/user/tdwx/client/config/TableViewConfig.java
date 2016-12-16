/**
 * 
 */
package org.gcube.portlets.user.tdwx.client.config;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
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
