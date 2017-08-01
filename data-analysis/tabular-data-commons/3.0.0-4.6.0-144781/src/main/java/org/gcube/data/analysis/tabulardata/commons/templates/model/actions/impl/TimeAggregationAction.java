package org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.commons.templates.model.TemplateAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.TemplateColumnAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.commons.utils.TimeDimensionReference;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TimeAggregationAction  extends TemplateAction<Long> implements TemplateColumnAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5255728286366763755L;

	private static final long TIMEAGGREGATION_OP_ID =3009;

	private static final String FUNCTION_PARAMETER_ID = "functionParameter";
	private static final String TO_AGGREGATE_COLUMN_ID ="functionMember";
	private static final String COMPOSITE_AGGREGATION_ID ="aggregationFunctions";
	private static final String GROUP_COLUMN_PARAMETER_ID = "keyColumns";
	private static final String PERIOD_AGGREGGATION_PARAMETER_ID ="timeDimensionAggr";


	private TemplateColumn<?> column;

	private PeriodType periodType;

	private List<AggregationPair> aggregationPairs = new ArrayList<>();

	private List<TemplateColumn<?>> groupColumns = new ArrayList<>();

	protected TimeAggregationAction(){}
	
	public TimeAggregationAction(TemplateColumn<?> column, PeriodType periodType, List<TemplateColumn<?>> groupColumns, AggregationPair ... aggregationPairs) {
		super();
		this.column = column;
		if (!column.getColumnType().equals(ColumnCategory.TIMEDIMENSION))
			throw new IllegalArgumentException("timeAggregationAction is apllicable only to TimeDimension columns");
		if (groupColumns==null || groupColumns.isEmpty())
			throw new IllegalArgumentException("at least one column for grouping has to be selected");

		PeriodType columnPeriod = ((TimeDimensionReference)column.getReference()).getPeriod();
		if (!PeriodType.getHierarchicalRelation().get(columnPeriod).contains(periodType))
			throw new IllegalArgumentException(String.format("%s period type cannot be aggregated for %s", columnPeriod, periodType));
		this.periodType = periodType;
		if (aggregationPairs!=null && aggregationPairs.length>0)
			this.aggregationPairs = Arrays.asList(aggregationPairs);
		this.groupColumns = groupColumns;
	}

	@Override
	public boolean usesExpression() {
		return false;
	}

	@Override
	public Long getIdentifier() {
		return TIMEAGGREGATION_OP_ID;
	}
	
	@Override
	public String getColumnId() {
		return column.getId();
	}

	@Override
	public Map<String, Object> getParameters() {
		try{
			Map<String, Object> parameters = new HashMap<>();
			
			if (!aggregationPairs.isEmpty()){
				List<HashMap<String,Object>> compositeList = new ArrayList<>();

				for (AggregationPair pair :this.aggregationPairs){

					HashMap<String,Object> composite=new HashMap<String,Object>();
					composite.put(FUNCTION_PARAMETER_ID, new ImmutableLocalizedText(pair.getFunction().name()));
					composite.put(TO_AGGREGATE_COLUMN_ID, new TemplateActionColumnReference(pair.getColumn().getId()));
					compositeList.add(composite);
				}
				parameters.put(COMPOSITE_AGGREGATION_ID, compositeList);
			}

			List<TemplateActionColumnReference> groupColumnReference= new ArrayList<>();
			for (TemplateColumn<?> groupColumn : groupColumns)
				groupColumnReference.add(new TemplateActionColumnReference(groupColumn.getId()));
			parameters.put(GROUP_COLUMN_PARAMETER_ID, groupColumnReference);

			parameters.put(PERIOD_AGGREGGATION_PARAMETER_ID, periodType.name());


			return parameters;
		}catch(Exception e){
			throw new RuntimeException("unexpected exception", e);
		}
	}

	public TemplateColumn<?> getColumn() {
		return column;
	}

	public PeriodType getPeriodType() {
		return periodType;
	}

	public List<AggregationPair> getAggregationPairs() {
		return aggregationPairs;
	}

	public List<TemplateColumn<?>> getGroupColumns() {
		return groupColumns;
	}

	@Override
	public List<TemplateColumn<?>> getPostOperationStructure(
			List<TemplateColumn<?>> columns) {
		List<TemplateColumn<?>> newStructureColumn = new ArrayList<>();
		for (TemplateColumn<?> templateColumn : columns){
			if (templateColumn.equals(this.column)){
				TemplateColumn<IntegerType> col = new TemplateColumn<>(ColumnCategory.TIMEDIMENSION, IntegerType.class, new TimeDimensionReference(periodType, periodType.getAcceptedFormats().get(0).getId()));
				col.setId(templateColumn.getId());
				newStructureColumn.add(col);
			}else if (this.groupColumns.contains(templateColumn) || containsColumn(templateColumn, this.aggregationPairs))
				newStructureColumn.add(templateColumn);
		}
		return newStructureColumn;
	}

	private boolean containsColumn(TemplateColumn<?> templateColumn, List<AggregationPair> pairs){
		for (AggregationPair pair: pairs)
			if (pair.getColumn().equals(templateColumn))
				return true;
		return false;
	}
	
}
