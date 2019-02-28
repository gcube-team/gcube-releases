/**
 * 
 */
package org.gcube.portlets.user.tdw.datasource.jdbc;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class JDBCCredential {
	protected String id;
	protected String url;
	protected String tableName;
	
	/**
	 * @param id
	 * @param url
	 * @param tableName
	 */
	public JDBCCredential(String id, String url, String tableName) {
		this.id = id;
		this.url = url;
		this.tableName = tableName;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JDBCCredential [id=");
		builder.append(id);
		builder.append(", url=");
		builder.append(url);
		builder.append(", tableName=");
		builder.append(tableName);
		builder.append("]");
		return builder.toString();
	}
}