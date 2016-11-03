package org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.templates.model.TemplateAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.TemplateColumnAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;

public class DeleteColumnAction extends TemplateAction<Long> implements TemplateColumnAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4885387040085000829L;

	private static final long DELETE_COLUMN_OP_ID=1004;
	
	private TemplateColumn<?> columnToRemove;
		
	protected DeleteColumnAction(){}
	
	public DeleteColumnAction(TemplateColumn<?> columnToRemove) {
		super();
		this.columnToRemove = columnToRemove;
	}

	@Override
	public boolean usesExpression() {
		return false;
	}

	@Override
	public Long getIdentifier() {
		return DELETE_COLUMN_OP_ID;
	}

	@Override
	public Map<String, Object> getParameters() {
		return Collections.emptyMap();
	}

	@Override
	public String getColumnId() {
		return columnToRemove.getId();
	}

	@Override
	public List<TemplateColumn<?>> getPostOperationStructure(
			List<TemplateColumn<?>> columns) {
		List<TemplateColumn<?>> newStructure = new ArrayList<>();
		for (TemplateColumn<?> column: columns)
			if (!column.equals(this.columnToRemove))
				newStructure.add(column);
		return newStructure;
	}

	
	
}
