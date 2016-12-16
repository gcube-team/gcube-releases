/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view.external;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;
import org.gcube.portlets.user.tdtemplate.shared.util.CutStringUtil;

import com.google.gwt.core.shared.GWT;


/**
 * The Class ConverterColumnMockupProperty.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 20, 2015
 */
public class ConverterColumnMockupProperty {
	
	private String dataType;
	private ColumnTypeCode columnTypeCode;
	private ColumnDataType columnDataType;
	private String columnType;
	private String columnId;
	
	/**
	 * Instantiates a new converter column mockup property.
	 *
	 * @param columnId the column id
	 * @param columnType the column type
	 * @param dataType the data type
	 * @throws Exception the exception
	 */
	public ConverterColumnMockupProperty(String columnId, String columnType, String dataType) throws Exception {
		GWT.log("ConverterColumnMockupProperty columnType: "+columnType+", dataType: "+dataType);
		
		this.columnId = columnId;
		this.columnType = columnType;
		
		if(this.columnId==null || this.columnId.isEmpty())
			throw new Exception("An error occurred on instancing Expression Dialog, invalid column id parameter");
		
		if(this.columnType==null || this.columnType.isEmpty())
			throw new Exception("An error occurred on instancing Expression Dialog, invalid column id parameter");
		
		//REMOVE SUFFIX "Type" or "type" if exists
		this.dataType = CutStringUtil.stringPurgeSuffix(dataType, "Type");
		this.dataType = CutStringUtil.stringPurgeSuffix(this.dataType, "type");
		

		this.columnTypeCode = ExpressionDialogConvertUtil.toColumnTypeCode(this.columnType);
		this.columnDataType = ExpressionDialogConvertUtil.toColumnDataType(this.dataType);

		if(this.columnTypeCode==null || this.columnDataType==null)
			throw new Exception("An error occurred on instancing Expression Dialog, invalid parameter, Please refresh and try again");
	}

	/**
	 * Gets the data type.
	 *
	 * @return the dataType
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * Gets the column type code.
	 *
	 * @return the columnTypeCode
	 */
	public ColumnTypeCode getColumnTypeCode() {
		return columnTypeCode;
	}

	/**
	 * Gets the column data type.
	 *
	 * @return the columnDataType
	 */
	public ColumnDataType getColumnDataType() {
		return columnDataType;
	}

	/**
	 * Gets the column type.
	 *
	 * @return the columnType
	 */
	public String getColumnType() {
		return columnType;
	}

	/**
	 * Gets the column id.
	 *
	 * @return the columnId
	 */
	public String getColumnId() {
		return columnId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConverterMockupColumn [dataType=");
		builder.append(dataType);
		builder.append(", columnTypeCode=");
		builder.append(columnTypeCode);
		builder.append(", columnDataType=");
		builder.append(columnDataType);
		builder.append(", columnType=");
		builder.append(columnType);
		builder.append(", columnId=");
		builder.append(columnId);
		builder.append("]");
		return builder.toString();
	}
}
