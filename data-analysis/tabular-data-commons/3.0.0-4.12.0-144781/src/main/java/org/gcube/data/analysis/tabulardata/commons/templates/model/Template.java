package org.gcube.data.analysis.tabulardata.commons.templates.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.validator.annotations.NotEmpty;
import org.gcube.common.validator.annotations.NotNull;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.GroupAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.finals.AddToFlowAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnDescription;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.OnRowErrorAction;



@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Template implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@NotEmpty
	private List<TemplateColumn<?>> columns;
	
	@NotNull
	private OnRowErrorAction onRowErrorAction = OnRowErrorAction.ASK;
	
	@NotNull
	private List<TemplateAction<Long>> actions = new ArrayList<TemplateAction<Long>>(); 
	
	private AddToFlowAction addToFlowAction;
	
	private TemplateCategory category;
	
	public static Template create(TemplateCategory templateType, TemplateColumn<?> ... columns ){
		return new Template(templateType, columns);
	}
	
	private Template(){}
	
	private Template(TemplateCategory templateType ,TemplateColumn<?>[] columns){
		for (TemplateColumn<?> column: columns)
			if (!containsColumnCategory(templateType.getAllowedColumn(), column.getColumnType()))
				throw new IllegalArgumentException(String.format("column type %s not allowed for template type %s ",column.getColumnType().name(), templateType.name()));
		this.columns = Arrays.asList(columns);		
		this.category = templateType;
	}

	private static boolean containsColumnCategory(List<ColumnDescription> allowedColumns, ColumnCategory columnType){
		for (ColumnDescription desc: allowedColumns)
			if (desc.getColumnCategory()==columnType) return true;
		return false;
	}
	
	/**
	 * @return the columns
	 */
	public List<TemplateColumn<?>> getColumns() {
		return Collections.unmodifiableList(columns);
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(List<TemplateColumn<?>> columns) {
		this.columns = columns;
	}

	public List<TemplateColumn<?>> getActualStructure(){
		if (actions.isEmpty()) return this.getColumns();
		List<TemplateColumn<?>> actualStructure = this.getColumns();
		for (TemplateAction<?> action: actions)
			actualStructure = action.getPostOperationStructure(actualStructure);
		
		return actualStructure;
	}
	
	/**
	 * @return the onErrorAction
	 */
	public OnRowErrorAction getOnRowErrorAction() {
		return onRowErrorAction;
	}

	/**
	 * @param onErrorAction the onErrorAction to set
	 */
	public void setOnErrorAction(OnRowErrorAction onErrorAction) {
		this.onRowErrorAction = onErrorAction;
	}

	/**
	 * @return the operations
	 */
	public List<TemplateAction<Long>> getActions() {
		return  Collections.unmodifiableList(actions);
	}

	/**
	 * @param operations the operations to set
	 */
	//TODO: add action consistency control
	public void addAction(TemplateAction<Long> action) {
		this.actions.add(action);
	}

	//TODO: add actions consistency control
	public void addAction(GroupAction groupAction) {
		this.actions.addAll(groupAction.getActions());
	}
	
	public void removeLastAction(){
		TemplateAction<Long> lastAction = actions.remove(actions.size()-1);
		String groupId = lastAction.getBelongsToGroup();
		
		while (groupId !=null && 
				actions.size()>0){
			if (groupId.equals(actions.get(actions.size()-1).getBelongsToGroup()))
				actions.remove(actions.size()-1);
			else break;
		}
	}
	
	/**
	 * @return the finalAction
	 */
	public AddToFlowAction getAddToFlow() {
		return addToFlowAction;
	}

	/**
	 * @param finalAction the finalAction to set
	 */
	public void setAddToFlow(AddToFlowAction addToFlow) {
		this.addToFlowAction = addToFlow;
	}
	
	
	/**
	 * @return the category
	 */
	public TemplateCategory getCategory() {
		return category;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Template [columns=" + columns + ", onErrorAction=" + onRowErrorAction + ", actions="
				+ actions + ", addToFlowAction=" + addToFlowAction + "]";
	}
		
	
}
