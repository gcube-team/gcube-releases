/**
 * 
 */
package org.gcube.portlets.user.tdw.server.session;

import org.gcube.portlets.user.tdw.server.datasource.DataSource;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class TDSession {
	
	protected int id;
	protected DataSource dataSource;
	
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
	public DataSource getDataSource() {
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
	public void setDataSource(DataSource dataSource) {
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
