package org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.ColumnCreatorAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AddColumnAction extends ColumnCreatorAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4829645296356074095L;

	public static final String DATA_TYPE="dataType";
	public static final String COLUMN_TYPE="columnType";
	public static final String LABEL= "label";
	public static final String VALUE_PARAMETER="value";

	private static final long ADDCOLUMN_OP_ID =1005;
	
	private TemplateColumn<?> column;
	
	private Expression initialValue;
	
	public static final List<ColumnCategory> allowedColumn = Arrays.asList(ColumnCategory.ATTRIBUTE, ColumnCategory.ANNOTATION, ColumnCategory.CODE,
			ColumnCategory.CODEDESCRIPTION, ColumnCategory.CODENAME, ColumnCategory.MEASURE);
	
	protected AddColumnAction(){}
	
	public AddColumnAction(TemplateColumn<?> column, Expression initialValue){
		if (!allowedColumn.contains(column.getColumnType()))
			throw new IllegalArgumentException("column category not allowed");
		this.column = column;
		this.initialValue = initialValue;
	}
	
	@Override
	public Long getIdentifier() {
		return ADDCOLUMN_OP_ID;
	}

	@Override
	public Map<String, Object> getParameters() {
		
		Map<String, Object> parameters = new HashMap<>();
		TemplateColumn<?> templateColumn = this.column;
		try {
			parameters.put(DATA_TYPE, (Object) templateColumn.getValueType().newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalArgumentException("unexpected exception",e);
		}
		parameters.put(COLUMN_TYPE, templateColumn.getColumnType().getModelType());
		parameters.put(VALUE_PARAMETER, this.initialValue );
		if (templateColumn.getLabel()!=null)
			parameters.put(LABEL, new ImmutableLocalizedText(templateColumn.getLabel()));
		return parameters;
	}

	public static List<ColumnCategory> getAllowedcolumn() {
		return allowedColumn;
	}

	public TemplateColumn<?> getColumn() {
		return column;
	}
	

	@Override
	public List<TemplateColumn<?>> getPostOperationStructure(
			List<TemplateColumn<?>> columns) {
		List<TemplateColumn<?>> newStructure = new ArrayList<>(columns);
		newStructure.add(column);
		return newStructure;
	}

	@Override
	public boolean usesExpression() {
		return true;
	}

	@Override
	public List<TemplateColumn<?>> getCreatedColumns() {
		List<TemplateColumn<?>> returnList = new ArrayList<>(1);
		returnList.add(column);
		return returnList;
	}

	public Expression getInitialValue() {
		return initialValue;
	}
	
}
