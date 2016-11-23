/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.tdtemplate.shared.SPECIAL_CATEGORY_TYPE;
import org.gcube.portlets.user.tdtemplate.shared.TdTColumnCategory;
import org.gcube.portlets.user.tdtemplate.shared.TdTDataType;
import org.gcube.portlets.user.tdtemplate.shared.TdTFormatReference;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 29, 2014
 * 
 */
public interface ColumnDefinition {

//	String getColumnId();

	String getColumnName();

	TdTColumnCategory getSelectedColumnCategory();
	
	TdTDataType getSelectedDataType();

	boolean isValid();
	
	void setSpecialCategoryType(SPECIAL_CATEGORY_TYPE category);
	
	SPECIAL_CATEGORY_TYPE getSpecialCategoryType();
	
	ColumnData getReferenceColumnData();

	/**
	 * @return
	 */
	String getSelectedLocale();

	/**
	 * 
	 */
	String getTimePeriod();
	
	TdTFormatReference getTimePeriodFormat();
	
	TdTFormatReference getSelectedDataTypeFormat();

}
