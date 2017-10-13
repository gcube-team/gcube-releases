/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 5, 2014
 *
 */
public class TemplateIndexes {
	
	int columnIndex;
	int expressionIndex;
	
	
	/**
	 * @param columnIndex
	 * @param expressionIndex
	 */
	public TemplateIndexes(int columnIndex, int expressionIndex) {
		this.columnIndex = columnIndex;
		this.expressionIndex = expressionIndex;
	}
	
	public int getColumnIndex() {
		return columnIndex;
	}
	public int getExpressionIndex() {
		return expressionIndex;
	}
	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}
	public void setExpressionIndex(int expressionIndex) {
		this.expressionIndex = expressionIndex;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TemplateIndexs [columnIndex=");
		builder.append(columnIndex);
		builder.append(", expressionIndex=");
		builder.append(expressionIndex);
		builder.append("]");
		return builder.toString();
	}

}
