package org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author "Giancarlo Panichi"
 * 
 */
public class ColumnMockUp implements Serializable {

	private static final long serialVersionUID = 7467523779864189492L;

	private String id;// For grid and combo only;
	private String columnId;//ColumnId for template only;
	private ColumnTypeCode columnType;
	private ColumnDataType columnDataType;
	private String defaultValue;
	private String label;
	private String localeName;
	private boolean hasExpression;
	private C_Expression expression;
	
	protected PeriodDataType timeDimensionType;
	protected ColumnData codelistColumnReference;

	public ColumnMockUp() {

	}

	/**
	 * Generic simple column with only ColumnDataType set
	 * 
	 * 
	 * @param id
	 * @param columnId
	 * @param columnDataType
	 * @param label
	 */
	public ColumnMockUp(String id, String columnId,
			ColumnDataType columnDataType, String label) {
		super();
		this.id = id;
		this.columnId = columnId;
		this.columnType = null;
		this.columnDataType = columnDataType;
		this.defaultValue = null;
		this.label = label;
		this.localeName = null;
		this.hasExpression = false;
		this.expression = null;
		this.timeDimensionType = null;
		this.codelistColumnReference = null;
	}
	
	
	/**
	 * ColumnMockUp
	 * 
	 * 
	 * @param id
	 * @param columnId
	 * @param columnType
	 * @param columnDataType
	 * @param label
	 */
	public ColumnMockUp(String id, String columnId, ColumnTypeCode columnType,
			ColumnDataType columnDataType, String label) {
		super();
		this.id = id;
		this.columnId = columnId;
		this.columnType = columnType;
		this.columnDataType = columnDataType;
		this.defaultValue = null;
		this.label = label;
		this.localeName = null;
		this.hasExpression = false;
		this.expression = null;
		this.timeDimensionType = null;
		this.codelistColumnReference = null;
	}



	/**
	 * For Code, CodeDescription and Annotation Column
	 * @param id TODO
	 * @param columnId TODO
	 * @param label
	 * @param columnType
	 * @param defaultValue
	 * @param localeName
	 */
	public ColumnMockUp(String id,String columnId,
			String label, ColumnTypeCode columnType, String defaultValue) {
		this.id = id;
		this.columnId=columnId;
		this.label = label;
		this.columnType = columnType;
		this.columnDataType = ColumnDataType.Text;
		this.defaultValue = defaultValue;
		hasExpression = false;
		expression = null;
	}

	/**
	 * For Code, CodeDescription and Annotation Column
	 * @param id TODO
	 * @param columnId TODO
	 * @param label
	 * @param columnType
	 * @param expressionWrapper
	 */
	public ColumnMockUp(String id, String columnId,
			String label, ColumnTypeCode columnType, C_Expression expression) {
		this.id = null;
		this.columnId=null;
		this.label = label;
		this.columnType = columnType;
		this.columnDataType = ColumnDataType.Text;
		this.defaultValue = null;
		hasExpression = true;
		this.expression = expression;
	}

	/**
	 * For Attribute and Measure Column
	 * @param id TODO
	 * @param columnId TODO
	 * @param label
	 * @param columnType
	 * @param defaultValue
	 * @param localeName
	 */
	public ColumnMockUp(String id, String columnId,
			String label, ColumnTypeCode columnType, ColumnDataType columnDataType, String defaultValue) {
		this.id = id;
		this.columnId=columnId;
		this.label = label;
		this.columnType = columnType;
		this.columnDataType = columnDataType;
		this.defaultValue = defaultValue;
		hasExpression = false;
		expression = null;
	}

	/**
	 * For Attribute and Measure Column
	 * @param id TODO
	 * @param columnId TODO
	 * @param label
	 * @param columnType
	 * @param columnDataType
	 * @param expressionContainer
	 */
	public ColumnMockUp(String id, String columnId,
			String label, ColumnTypeCode columnType, ColumnDataType columnDataType, C_Expression expression) {
		this.id = id;
		this.columnId=columnId;
		this.label = label;
		this.columnType = columnType;
		this.columnDataType = columnDataType;
		this.defaultValue = null;
		hasExpression = true;
		this.expression = expression;
	}

	/**
	 * For CodeName Column
	 * @param id TODO
	 * @param columnId TODO
	 * @param label
	 * @param columnType
	 * @param localeName
	 * @param defaultValue
	 */
	public ColumnMockUp(String id, String columnId,
			String label, ColumnTypeCode columnType, String localeName, String defaultValue) {
		this.id = null;
		this.columnId=null;
		this.label = label;
		this.columnType = columnType;
		this.columnDataType = ColumnDataType.Text;
		this.localeName = localeName;
		this.defaultValue = defaultValue;
		hasExpression = false;
		expression = null;
	}

	/**
	 * 
	 * @param id TODO
	 * @param columnId TODO
	 * @param label
	 * @param columnType
	 * @param localeName
	 * @param expressionContainer
	 */
	public ColumnMockUp(String id, String columnId,
			String label, ColumnTypeCode columnType, String localeName, C_Expression expression) {
		this.id = id;
		this.columnId=columnId;
		this.label = label;
		this.columnType = columnType;
		this.columnDataType = ColumnDataType.Text;
		this.localeName = localeName;
		this.defaultValue = null;
		hasExpression = true;
		this.expression = expression;
	}

	/**
	 * For Dimension
	 * @param id TODO
	 * @param columnId TODO
	 * @param label
	 * @param columnType
	 * @param defaultValue
	 * @param timeDimensionType
	 */
	public ColumnMockUp(String id, String columnId,
			String label, ColumnTypeCode columnType, ColumnData codelistColumnReference, String defaultValue) {
		this.id = id;
		this.columnId=columnId;
		this.label = label;
		this.columnType = columnType;
		this.columnDataType = ColumnDataType.Integer;
		this.codelistColumnReference = codelistColumnReference;
		this.defaultValue = defaultValue;
		hasExpression = false;
		expression = null;

	}

	/**
	 * For TimeDimension
	 * @param id TODO
	 * @param columnId TODO
	 * @param label
	 * @param columnType
	 * @param timeDimensionType
	 * @param defaultValue
	 */
	public ColumnMockUp(String id, String columnId,
			String label, ColumnTypeCode columnType, PeriodDataType timeDimensionType, String defaultValue) {
		this.id = id;
		this.columnId=columnId;
		this.label = label;
		this.columnType = columnType;
		this.columnDataType = ColumnDataType.Integer;
		this.timeDimensionType = timeDimensionType;
		this.defaultValue = defaultValue;
		hasExpression = false;
		expression = null;

	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public ColumnTypeCode getColumnType() {
		return columnType;
	}

	public void setColumnType(ColumnTypeCode columnType) {
		this.columnType = columnType;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getLocaleName() {
		return localeName;
	}

	public void setLocaleName(String localeName) {
		this.localeName = localeName;
	}

	public ColumnDataType getColumnDataType() {
		return columnDataType;
	}

	public void setColumnDataType(ColumnDataType columnDataType) {
		this.columnDataType = columnDataType;
	}

	public PeriodDataType getTimeDimensionType() {
		return timeDimensionType;
	}

	public void setTimeDimensionType(PeriodDataType timeDimensionType) {
		this.timeDimensionType = timeDimensionType;
	}

	public ColumnData getCodelistColumnReference() {
		return codelistColumnReference;
	}

	public void setCodelistColumnReference(ColumnData codelistColumnReference) {
		this.codelistColumnReference = codelistColumnReference;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean hasExpression() {
		return hasExpression;
	}

	public void setHasExpression(boolean hasExpression) {
		this.hasExpression = hasExpression;
	}

	public C_Expression getExpression() {
		return expression;
	}

	public void setExpression(C_Expression expression) {
		this.expression = expression;
	}

	public String getColumnId() {
		return columnId;
	}

	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	@Override
	public String toString() {
		return "ColumnMockUp [id=" + id + ", columnId=" + columnId
				+ ", columnType=" + columnType + ", columnDataType="
				+ columnDataType + ", defaultValue=" + defaultValue
				+ ", label=" + label + ", localeName=" + localeName
				+ ", hasExpression=" + hasExpression + ", expression="
				+ expression + ", timeDimensionType=" + timeDimensionType
				+ ", codelistColumnReference=" + codelistColumnReference + "]";
	}

	

	
	

}
