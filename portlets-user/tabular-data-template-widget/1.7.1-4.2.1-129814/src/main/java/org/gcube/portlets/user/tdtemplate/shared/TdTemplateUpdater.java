/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.shared;

import java.io.Serializable;
import java.util.List;

import org.gcube.portlets.user.tdtemplateoperation.shared.action.TabularDataActionDescription;


/**
 * The Class TdTemplateUpdater.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 1, 2015
 */
public class TdTemplateUpdater implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5877411129479020087L;

	private TdTemplateDefinition templateDefinition;
	private List<TdColumnDefinition> listColumns;
	private List<TabularDataActionDescription> tabularDataActionDescription;

	/**
	 * Instantiates a new td template updater.
	 */
	public TdTemplateUpdater() {
	}

	/**
	 * Instantiates a new td template updater.
	 *
	 * @param templateDefinition the template definition
	 * @param listColumns the list columns
	 */
	public TdTemplateUpdater(TdTemplateDefinition templateDefinition,
			List<TdColumnDefinition> listColumns) {
		this.templateDefinition = templateDefinition;
		this.listColumns = listColumns;
	}

	/**
	 * Gets the tabular data action description.
	 *
	 * @return the tabularDataActionDescription
	 */
	public List<TabularDataActionDescription> getTabularDataActionDescription() {
		return tabularDataActionDescription;
	}

	/**
	 * Sets the tabular data action description.
	 *
	 * @param tabularDataActionDescription            the tabularDataActionDescription to set
	 */
	public void setTabularDataActionDescription(
			List<TabularDataActionDescription> tabularDataActionDescription) {
		this.tabularDataActionDescription = tabularDataActionDescription;
	}

	/**
	 * Gets the template definition.
	 *
	 * @return the template definition
	 */
	public TdTemplateDefinition getTemplateDefinition() {
		return templateDefinition;
	}

	/**
	 * Gets the list columns.
	 *
	 * @return the list columns
	 */
	public List<TdColumnDefinition> getListColumns() {
		return listColumns;
	}

	/**
	 * Sets the template definition.
	 *
	 * @param templateDefinition the new template definition
	 */
	public void setTemplateDefinition(TdTemplateDefinition templateDefinition) {
		this.templateDefinition = templateDefinition;
	}

	/**
	 * Sets the list columns.
	 *
	 * @param listColumns the new list columns
	 */
	public void setListColumns(List<TdColumnDefinition> listColumns) {
		this.listColumns = listColumns;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TdTemplateUpdater [templateDefinition=");
		builder.append(templateDefinition);
		builder.append(", listColumns=");
		builder.append(listColumns);
		builder.append(", tabularDataActionDescription=");
		builder.append(tabularDataActionDescription);
		builder.append("]");
		return builder.toString();
	}

}
