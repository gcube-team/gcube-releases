/**
 * 
 */
package org.gcube.portlets.user.tdwx.server.session;

import org.gcube.portlets.user.tdwx.server.datasource.DataSourceX;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TDSession {
	
	private int id;
	private DataSourceX dataSource;
	
	/**
	 * @param id
	 */
	public TDSession(int id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the dataSource
	 */
	public DataSourceX getDataSource() {
		return dataSource;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSourceX dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TDSession [id=");
		builder.append(id);
		builder.append(", dataSource=");
		builder.append(dataSource);
		builder.append("]");
		return builder.toString();
	}
	
	

}
