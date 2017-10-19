package org.gcube.portlets.user.td.widgetcommonevent.client.expression;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ExpressionWrapper implements Serializable {

	private static final long serialVersionUID = 3877772253100442685L;
	private ExpressionWrapperType expressionType;
	private TRId trId;
	private ColumnData columnData;
	private C_ExpressionContainer conditionExpressionContainer;
	private boolean replaceByValue;
	private String replaceValue;
	private C_ExpressionContainer replaceExpressionContainer;

	/**
	 * 
	 */
	public ExpressionWrapper() {
		super();
		this.expressionType = ExpressionWrapperType.EXPRESSION_NULL;
	}

	/**
	 * 
	 * @param trId
	 *            TR Id
	 * @param columnData
	 *            Column data
	 * @param conditionExpressionContainer
	 *            Condition expression container
	 */
	public ExpressionWrapper(TRId trId, ColumnData columnData, C_ExpressionContainer conditionExpressionContainer) {
		super();

		this.expressionType = ExpressionWrapperType.CONDITION_COLUMN_EXPRESSION;
		this.trId = trId;
		this.columnData = columnData;
		this.conditionExpressionContainer = conditionExpressionContainer;
		this.replaceByValue = false;
		this.replaceValue = null;
		this.replaceExpressionContainer = null;
	}

	public ExpressionWrapper(String replaceValue, TRId trId, ColumnData columnData) {
		super();
		this.expressionType = ExpressionWrapperType.REPLACE_COLUMN_EXPRESSION;
		this.trId = trId;
		this.columnData = columnData;
		this.conditionExpressionContainer = null;
		this.replaceByValue = true;
		this.replaceValue = replaceValue;
		this.replaceExpressionContainer = null;
	}

	public ExpressionWrapper(C_ExpressionContainer replaceExpressionContainer, TRId trId, ColumnData columnData) {
		super();
		this.expressionType = ExpressionWrapperType.REPLACE_COLUMN_EXPRESSION;
		this.trId = trId;
		this.columnData = columnData;
		this.conditionExpressionContainer = null;
		this.replaceByValue = false;
		this.replaceValue = null;
		this.replaceExpressionContainer = replaceExpressionContainer;
	}

	public ExpressionWrapper(TRId trId, ColumnData columnData, C_ExpressionContainer conditionExpressionContainer,
			C_ExpressionContainer replaceExpressionContainer) {
		super();
		this.expressionType = ExpressionWrapperType.CONDITION_AND_REPLACE_COLUMN_EXPRESSION;
		this.trId = trId;
		this.columnData = columnData;
		this.conditionExpressionContainer = conditionExpressionContainer;
		this.replaceByValue = false;
		this.replaceValue = null;
		this.replaceExpressionContainer = replaceExpressionContainer;
	}

	public ExpressionWrapper(TRId trId, ColumnData columnData, C_ExpressionContainer conditionExpressionContainer,
			String replaceValue) {
		super();
		this.expressionType = ExpressionWrapperType.CONDITION_AND_REPLACE_COLUMN_EXPRESSION;
		this.trId = trId;
		this.columnData = columnData;
		this.conditionExpressionContainer = conditionExpressionContainer;
		this.replaceByValue = true;
		this.replaceValue = replaceValue;
		this.replaceExpressionContainer = null;
	}

	public ExpressionWrapperType getExpressionType() {
		return expressionType;
	}

	public void setExpressionType(ExpressionWrapperType expressionType) {
		this.expressionType = expressionType;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public ColumnData getColumnData() {
		return columnData;
	}

	public void setColumnData(ColumnData columnData) {
		this.columnData = columnData;
	}

	public C_ExpressionContainer getConditionExpressionContainer() {
		return conditionExpressionContainer;
	}

	public void setConditionExpressionContainer(C_ExpressionContainer conditionExpressionContainer) {
		this.conditionExpressionContainer = conditionExpressionContainer;
	}

	public boolean isReplaceByValue() {
		return replaceByValue;
	}

	public void setReplaceByValue(boolean replaceByValue) {
		this.replaceByValue = replaceByValue;
	}

	public String getReplaceValue() {
		return replaceValue;
	}

	public void setReplaceValue(String replaceValue) {
		this.replaceValue = replaceValue;
	}

	public C_ExpressionContainer getReplaceExpressionContainer() {
		return replaceExpressionContainer;
	}

	public void setReplaceExpressionContainer(C_ExpressionContainer replaceExpressionContainer) {
		this.replaceExpressionContainer = replaceExpressionContainer;
	}

	@Override
	public String toString() {
		return "ExpressionWrapper [expressionType=" + expressionType + ", trId=" + trId + ", columnData=" + columnData
				+ ", conditionExpressionContainer=" + conditionExpressionContainer + ", replaceByValue="
				+ replaceByValue + ", replaceValue=" + replaceValue + ", replaceExpressionContainer="
				+ replaceExpressionContainer + "]";
	}

}
