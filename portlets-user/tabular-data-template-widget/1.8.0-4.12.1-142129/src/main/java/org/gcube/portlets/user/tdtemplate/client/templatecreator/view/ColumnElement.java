/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 29, 2014
 *
 */
public interface ColumnElement {
	
	String getColumnId();
	int getColumnIndex();
	String getColumnLabel();
	String getColumnDataType();
	String getColumnType();
}
