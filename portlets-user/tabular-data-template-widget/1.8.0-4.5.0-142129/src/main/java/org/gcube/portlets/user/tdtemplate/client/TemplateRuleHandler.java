/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client;

import org.gcube.portlets.user.tdtemplate.client.event.ExpressionDialogOpenedEvent.ExpressionDialogType;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 5, 2014
 *
 */
public class TemplateRuleHandler {
	
	TemplateIndexes indexer;
	ExpressionDialogType type;
	/**
	 * @param indexer
	 * @param type
	 */
	public TemplateRuleHandler(TemplateIndexes indexer, ExpressionDialogType type) {
		this.indexer = indexer;
		this.type = type;
	}
	
	public TemplateIndexes getIndexer() {
		return indexer;
	}
	public ExpressionDialogType getType() {
		return type;
	}
	public void setIndexer(TemplateIndexes indexer) {
		this.indexer = indexer;
	}
	public void setType(ExpressionDialogType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TemplateRuleUpdater [indexer=");
		builder.append(indexer);
		builder.append(", type=");
		builder.append(type);
		builder.append("]");
		return builder.toString();
	}
}
