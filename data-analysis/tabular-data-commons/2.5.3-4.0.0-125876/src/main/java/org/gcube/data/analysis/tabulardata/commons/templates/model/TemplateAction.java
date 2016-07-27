package org.gcube.data.analysis.tabulardata.commons.templates.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.AddColumnAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.ChangeToTimeDimensionAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.DeleteColumnAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.NormalizeTableAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.ReplaceByExpressionAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.TemplateActionColumnReference;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.TimeAggregationAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.ValidateExpressionAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.PlaceholderReplacer;
import org.gcube.data.analysis.tabulardata.expression.leaf.ColumnReferencePlaceholder;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.table.TableId;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({AddColumnAction.class, ChangeToTimeDimensionAction.class, DeleteColumnAction.class, 
	NormalizeTableAction.class, TimeAggregationAction.class, ReplaceByExpressionAction.class, ValidateExpressionAction.class})
public abstract class TemplateAction<T extends Serializable> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public abstract boolean usesExpression();

	public abstract T getIdentifier();	

	private String belongsToGroup = null;

	public abstract List<TemplateColumn<?>> getPostOperationStructure(List<TemplateColumn<?>> columns );

	public abstract Map<String, Object> getParameters();

	protected TemplateAction(){}

	public Map<String,Object> replaceColumnReferences(TableId tableId, Map<String, ColumnDescriptor> columnMapping) throws Exception{
		Map<String,Object> parameters = getParameters();
		if (parameters==null || parameters.isEmpty())
			return parameters;
		return getNewInstanceMap(tableId, columnMapping, getParameters());
	}

	private Map<String,Object> getNewInstanceMap(TableId tableId, Map<String, ColumnDescriptor> columnMapping, Map<String,Object> oldInstances) throws Exception{
		Map<String, Object> returnMap = new HashMap<String, Object>();
		for (Entry<String, Object> entry: oldInstances.entrySet())
			returnMap.put(entry.getKey(), getNewObjectInstance(entry.getValue(), columnMapping, tableId));

		return returnMap;
	}

	@SuppressWarnings("unchecked")
	private Object getNewObjectInstance(Object value, Map<String, ColumnDescriptor> columnMapping, TableId tableId) throws Exception{
		if (value instanceof TemplateActionColumnReference){
			String columnId = ((TemplateActionColumnReference) value).getColumnId();
			if (columnMapping.containsKey(columnId)){
				ColumnLocalId newId = columnMapping.get(columnId).getColumnId();
				ColumnReference colRef = new ColumnReference(tableId, newId);
				return colRef;
			}
			else throw new RuntimeException("unexpected error, id "+columnId+" not found in column mapping");

		} else if (value instanceof ColumnReference){
			ColumnReference ref = (ColumnReference) value;
			if (columnMapping.containsKey(ref.getColumnId().toString())){
				ColumnLocalId newId = columnMapping.get(ref.getColumnId().toString()).getColumnId();
				ref.setColumnId(newId);
				return ref;
			}
		} else if (value instanceof String){
			if (columnMapping.containsKey((String)value))
				return columnMapping.get((String) value).getColumnId().getValue();		

		} else if (value instanceof Map){ 
			Map<String, Object> compParam = (Map<String, Object>) value;
			return getNewInstanceMap(tableId, columnMapping, compParam);
		} else if (value instanceof List){ 
			List<Object> compParam = (List<Object>) value;
			List<Object> newInstances = new ArrayList<>();
			for (Object param: compParam)
				newInstances.add(getNewObjectInstance(param, columnMapping, tableId));
			return newInstances;
		} else if (value instanceof ColumnReferencePlaceholder) {
			ColumnReferencePlaceholder pr=(ColumnReferencePlaceholder) value;
			ColumnReference ref = new ColumnReference(tableId, columnMapping.get(pr.getId()).getColumnId(), columnMapping.get(pr.getId()).getType().newInstance());
			return ref;
		} else if (value instanceof Expression) {
			PlaceholderReplacer pr=new PlaceholderReplacer((Expression) value);
			if (pr.hasPlaceholder()){
				for (Entry<String, ColumnDescriptor> mappingEntry: columnMapping.entrySet()){
					ColumnReference ref = new ColumnReference(tableId, mappingEntry.getValue().getColumnId(), mappingEntry.getValue().getType().newInstance() );
					pr.replaceById(ref, mappingEntry.getKey());
				}
				return pr.getExpression();
			}
		}
		return value;
	}

	public void setBelogsToGroup(String groupId){
		this.belongsToGroup = groupId;
	}

	public String getBelongsToGroup() {
		return belongsToGroup;
	}

}
