/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.shared;

import java.io.Serializable;
import java.util.List;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * The Class TdColumnDefinition.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 5, 2014
 */
public class TdColumnDefinition implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 257447065800064772L;
	
	private int index;
	private String columnName;
	private TdTColumnCategory category;
	private TdTDataType dataType;
	
	/**
	 * User client-side to handle special behavior
	 */
	private SPECIAL_CATEGORY_TYPE specialCategoryType;

	/**
	 * Used to set column reference. Used to DIMENSION
	 */
	private ColumnData columnDataReference;

	private String locale;
	
	private TdTTimePeriod timePeriod;

	/**
	 * Used to assign rules
	 */
	private List<TemplateExpression> listExpressionExtend;

	private boolean isBaseColumn = true;

	private String serverId;

	/**
	 * Instantiates a new td column definition.
	 */
	public TdColumnDefinition() {
	}

	/**
	 * Base constructor.
	 *
	 * @param index the index
	 * @param serverId the server id
	 * @param columnName the column name
	 * @param category the category
	 * @param dataType the data type
	 * @param type the type
	 */
	public TdColumnDefinition(int index, String serverId, String columnName, TdTColumnCategory category, TdTDataType dataType, SPECIAL_CATEGORY_TYPE type) {
		this.index = index;
		this.serverId = serverId;
		this.columnName = columnName;
		this.category = category;
		this.dataType = dataType;
		this.specialCategoryType = type;
	}
	

	/**
	 * Gets the column name.
	 *
	 * @return the columnName
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * Sets the column name.
	 *
	 * @param columnName the columnName to set
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	/**
	 * Gets the index.
	 *
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Gets the category.
	 *
	 * @return the category
	 */
	public TdTColumnCategory getCategory() {
		return category;
	}

	/**
	 * Gets the data type.
	 *
	 * @return the data type
	 */
	public TdTDataType getDataType() {
		return dataType;
	}

	/**
	 * Sets the index.
	 *
	 * @param index the new index
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * Sets the category.
	 *
	 * @param category the new category
	 */
	public void setCategory(TdTColumnCategory category) {
		this.category = category;
	}

	/**
	 * Sets the data type.
	 *
	 * @param dataType the new data type
	 */
	public void setDataType(TdTDataType dataType) {
		this.dataType = dataType;
	}

	/**
	 * Gets the special category type.
	 *
	 * @return the special category type
	 */
	public SPECIAL_CATEGORY_TYPE getSpecialCategoryType() {
		return specialCategoryType;
	}

	/**
	 * Gets the column data reference.
	 *
	 * @return the column data reference
	 */
	public ColumnData getColumnDataReference() {
		return columnDataReference;
	}
	
	/**
	 * Sets the special category type.
	 *
	 * @param specialCategoryType the new special category type
	 */
	public void setSpecialCategoryType(SPECIAL_CATEGORY_TYPE specialCategoryType) {
		this.specialCategoryType = specialCategoryType;
	}

	/**
	 * Sets the column data. Used to DIMENSION
	 *
	 * @param columnDataReference the new column data reference
	 */
	public void setColumnDataReference(ColumnData columnDataReference) {
		this.columnDataReference = columnDataReference;
	}


	/**
	 * Sets the locale.
	 *
	 * @param selectedLocale the new locale
	 */
	public void setLocale(String selectedLocale) {
		this.locale = selectedLocale;
	}
	
	/**
	 * Gets the locale.
	 *
	 * @return the locale
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * Sets the time period.
	 *
	 * @param timePeriod the new time period
	 */
	public void setTimePeriod(TdTTimePeriod timePeriod) {
//		this.timePeriod = timePeriod;
		this.timePeriod = timePeriod;
	}


	/**
	 * Gets the time period.
	 *
	 * @return the time period
	 */
	public TdTTimePeriod getTimePeriod() {
		return timePeriod;
	}

	/**
	 * Sets the rules extends.
	 *
	 * @param listExpressionExtend the new rules extends
	 */
	public void setRulesExtends(List<TemplateExpression> listExpressionExtend) {
		this.listExpressionExtend = listExpressionExtend;
	}


	/**
	 * Gets the rules extends.
	 *
	 * @return the rules extends
	 */
	public List<TemplateExpression> getRulesExtends() {
		return listExpressionExtend;
	}
	
	/**
	 * Sets the checks if is base column.
	 *
	 * @param bool the new checks if is base column
	 */
	public void setIsBaseColumn(boolean bool) {
		this.isBaseColumn = bool;
	}
	
	/**
	 * @return the isBaseColumn
	 */
	public boolean isBaseColumn() {
		return isBaseColumn;
	}
	
	/**
	 * @return the serverId
	 */
	public String getServerId() {
		return serverId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TdColumnDefinition [index=");
		builder.append(index);
		builder.append(", columnName=");
		builder.append(columnName);
		builder.append(", category=");
		builder.append(category);
		builder.append(", dataType=");
		builder.append(dataType);
		builder.append(", specialCategoryType=");
		builder.append(specialCategoryType);
		builder.append(", columnDataReference=");
		builder.append(columnDataReference);
		builder.append(", locale=");
		builder.append(locale);
		builder.append(", timePeriod=");
		builder.append(timePeriod);
		builder.append(", listExpressionExtend=");
		builder.append(listExpressionExtend);
		builder.append(", isBaseColumn=");
		builder.append(isBaseColumn);
		builder.append(", serverId=");
		builder.append(serverId);
		builder.append("]");
		return builder.toString();
	}

}
