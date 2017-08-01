package org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.templates.model.Utils;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.ColumnCreatorAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.expression.functions.Cast;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;

public class NormalizeTableAction extends ColumnCreatorAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3577530532365235156L;

	private static final long NORMALIZATION_OP_ID = 3008;

	private static final String TO_NORMALIZE_COLUMN_PARAMETER = "to_normalize";

	private List<TemplateColumn<?>> columnsToNormalize;

	private TemplateColumn<?> normColumn;
	private TemplateColumn<?> valueColumn;

	protected NormalizeTableAction(){}
	
	public NormalizeTableAction(List<TemplateColumn<?>> columnsToNormalize, String normColumnLabel, String valueColumnLabel) {
		super();
		this.columnsToNormalize = columnsToNormalize;
		this.normColumn = new TemplateColumn<>(ColumnCategory.ATTRIBUTE, TextType.class, Utils.TEXT_FORMAT_REFERENCE);
		this.normColumn.setLabel(normColumnLabel);

		Class<? extends DataType> valueClass = retrieveValueType().getClass(); 

		this.valueColumn = new TemplateColumn<>(ColumnCategory.ATTRIBUTE, valueClass, Utils.getDefaultFormatReferenceForType(valueClass));
		this.valueColumn.setLabel(valueColumnLabel);
	}

	private DataType retrieveValueType(){
		DataType quantityType=null;
		try{
			for(TemplateColumn<?> col:columnsToNormalize){

				if(quantityType==null) quantityType=col.getValueType().newInstance();
				else if (!Cast.isCastSupported(col.getValueType().newInstance(), quantityType)){ 
					if(Cast.isCastSupported(quantityType, col.getValueType().newInstance()))quantityType=col.getValueType().newInstance();
					else quantityType=new TextType();
				}
				if(quantityType instanceof TextType) break;
			}
		}catch(Exception e){
			throw new RuntimeException("normalize action cannot be created",e);
		}
		return quantityType;
	}
	
	@Override
	public List<TemplateColumn<?>> getCreatedColumns() {
		return Arrays.asList(normColumn, valueColumn);
	}

	@Override
	public boolean usesExpression() {
		return false;
	}

	@Override
	public Long getIdentifier() {
		return NORMALIZATION_OP_ID;
	}

	@Override
	public Map<String, Object> getParameters() {
		List<TemplateActionColumnReference> columnsToNormalize = new ArrayList<>();
		for (TemplateColumn<?> column: this.columnsToNormalize)
			columnsToNormalize.add(new TemplateActionColumnReference(column.getId()));
		return Collections.singletonMap(TO_NORMALIZE_COLUMN_PARAMETER, (Object)columnsToNormalize);
	}

	@Override
	public List<TemplateColumn<?>> getPostOperationStructure(
			List<TemplateColumn<?>> columns) {
		List<TemplateColumn<?>> newStructure = new ArrayList<>();
		for (TemplateColumn<?> column: columns)
			if (!this.columnsToNormalize.contains(column))
				newStructure.add(column);
		newStructure.add(this.normColumn);
		newStructure.add(this.valueColumn);
		return newStructure;
	}

}
