package org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.ColumnCreatorAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.TemplateColumnAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.commons.utils.TimeDimensionReference;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;

public class ChangeToTimeDimensionAction extends ColumnCreatorAction implements TemplateColumnAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2204641766604057011L;

	private static final long CHANGE_TO_TIME_DIMENSION_OP = 2007;
	
	private static final String PERIOD_FORMAT_PARAMETER_ID = "periodFormat";
	private static final String FORMAT_PARAMETER_ID ="inputFormatId";
	
	private TemplateColumn<?> columnToChange;	
	
	private TemplateColumn<IntegerType> newColumn;
	
	private PeriodType periodType;
	
	private String formatId;
	
	protected ChangeToTimeDimensionAction(){}
	
	public ChangeToTimeDimensionAction(TemplateColumn<?> columnToChange, TimeDimensionReference timeDimensionReference) {
		super();
		this.columnToChange = columnToChange;
		this.newColumn = new TemplateColumn<>(ColumnCategory.TIMEDIMENSION, IntegerType.class, timeDimensionReference );
		this.periodType = timeDimensionReference.getPeriod();
		this.formatId = timeDimensionReference.getFormatIdentifier();
	}

	@Override
	public String getColumnId() {
		return columnToChange.getId();
	}

	@Override
	public boolean usesExpression() {
		return false;
	}

	@Override
	public Long getIdentifier() {
		return CHANGE_TO_TIME_DIMENSION_OP;
	}

	@Override
	public Map<String, Object> getParameters() {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(PERIOD_FORMAT_PARAMETER_ID, this.periodType.getName() );
		parameters.put(FORMAT_PARAMETER_ID, this.formatId);
		return parameters;
	}

	@Override
	public List<TemplateColumn<?>> getCreatedColumns() {
		List<TemplateColumn<?>> createdCol = new ArrayList<>(1);
		createdCol.add(newColumn);
		return createdCol;
	}

	@Override
	public List<TemplateColumn<?>> getPostOperationStructure(
			List<TemplateColumn<?>> columns) {
		List<TemplateColumn<?>> newStructure = new ArrayList<>();
		for	(TemplateColumn<?> column :columns)	
			if(column.equals(columnToChange))
				newStructure.add(newColumn);
			else newStructure.add(column);
		return newStructure;
	}

}
