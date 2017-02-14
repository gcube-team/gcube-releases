package org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl;

import static org.gcube.data.analysis.tabulardata.expression.dsl.Casts.toText;
import static org.gcube.data.analysis.tabulardata.expression.dsl.Texts.concat;
import static org.gcube.data.analysis.tabulardata.expression.dsl.Texts.regexprReplace;
import static org.gcube.data.analysis.tabulardata.expression.dsl.Types.text;
import static org.gcube.data.analysis.tabulardata.expression.dsl.Types.typedPlaceholder;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.gcube.data.analysis.tabulardata.commons.templates.model.TemplateAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.Utils;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.GroupAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.commons.utils.TimeDimensionReference;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.model.time.TimeConstants;

public class GenerateTimeDimensionGroup implements GroupAction {

	private TemplateColumn<?> year;
	private TemplateColumn<?> month;
	private TemplateColumn<?> quarter;
	private TemplateColumn<?> day;
	
	private String id = UUID.randomUUID().toString();
	
	private PeriodType periodType;
	private String formatId ;
	private String label;
	
	
	public GenerateTimeDimensionGroup(TemplateColumn<?> year, String label) {
		super();
		this.year = year;
		this.periodType = PeriodType.YEAR;
		this.formatId = TimeConstants.ISO_YEAR.getId();
		this.label = label;
	}
	
	public GenerateTimeDimensionGroup(TemplateColumn<?> year,
			TemplateColumn<?> secondParam, boolean isMonth, String label) {
		super();
		this.year = year;
		if (isMonth){
			this.month = secondParam;
			this.periodType = PeriodType.MONTH;
			this.formatId = TimeConstants.ISO_MONTH.getId();
		}else{
			this.quarter = secondParam;
			this.periodType = PeriodType.QUARTER;
			this.formatId = TimeConstants.ISO_QUARTER.getId();
		}
		this.label = label;
	}
	
	public GenerateTimeDimensionGroup(TemplateColumn<?> year,
			TemplateColumn<?> month, TemplateColumn<?> day, String label) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.periodType = PeriodType.DAY;
		this.formatId = TimeConstants.ISO_DATE_ANY_SEP.getId();
		this.label = label;
	}

	@Override
	public List<TemplateAction<Long>> getActions() {
		Expression expr;
						
		if (this.day!=null)
			expr = concat(concat(concat(concat(getYearExpression(), text("-")),getMonthExpression()),text("-")),getDayExpression());
		else if (this.quarter!=null) expr = concat(concat(getYearExpression(), text("-Q")),getQuarterExpression());
		else if (this.month!=null) expr = concat(concat(getYearExpression(), text("-")),getMonthExpression());
		else expr = getYearExpression();
		TemplateColumn<TextType> newColumn = new TemplateColumn<TextType>(ColumnCategory.ATTRIBUTE, TextType.class, Utils.TEXT_FORMAT_REFERENCE );
		newColumn.setLabel(label);
		AddColumnAction addColumn = new AddColumnAction(newColumn, expr);
		addColumn.setBelogsToGroup(getGroupId());
		
		ChangeToTimeDimensionAction changeCol = new ChangeToTimeDimensionAction(addColumn.getCreatedColumns().get(0), new TimeDimensionReference(periodType, formatId));
		changeCol.setBelogsToGroup(getGroupId());
		
		return Arrays.asList((TemplateAction<Long>)addColumn, changeCol);
	}

	@Override
	public String getGroupId() {
		return id;
	}
		
	private Expression getMonthExpression(){
		return regexprReplace(toText(typedPlaceholder(month.getId(), month.getValueType())), "^[^\\d]*(\\d)[^\\d]*$", "0\\1");
	}
	
	private Expression getDayExpression(){
		Expression expr = regexprReplace(toText(typedPlaceholder(day.getId(), day.getValueType())), "^[^\\d]*(\\d)[^\\d]*$", "0\\1");
		System.out.println(expr);
		return regexprReplace(toText(typedPlaceholder(day.getId(), day.getValueType())), "^[^\\d]*(\\d)[^\\d]*$", "0\\1");
	}
	
	private Expression getYearExpression(){
		Expression expr = regexprReplace(toText(typedPlaceholder(year.getId(), year.getValueType())), "^[^\\d]*(\\d\\d\\d\\d)[^\\d]*$", "\\1");
		System.out.println("expr "+expr);
		return expr;
	}
	
	private Expression getQuarterExpression(){
		Expression expr =  regexprReplace(toText(typedPlaceholder(quarter.getId(), quarter.getValueType())), "^[^\\d]*(\\d)[^\\d]*$", "\\1");
		System.out.println("expr "+expr);
		return expr;
	}
}
