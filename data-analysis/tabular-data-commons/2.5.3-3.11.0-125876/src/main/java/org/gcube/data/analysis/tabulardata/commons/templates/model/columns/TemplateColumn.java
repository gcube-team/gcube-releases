package org.gcube.data.analysis.tabulardata.commons.templates.model.columns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.commons.templates.model.ReferenceObject;
import org.gcube.data.analysis.tabulardata.commons.templates.model.Representable;
import org.gcube.data.analysis.tabulardata.commons.utils.DimensionReference;
import org.gcube.data.analysis.tabulardata.commons.utils.FormatReference;
import org.gcube.data.analysis.tabulardata.commons.utils.LocaleReference;
import org.gcube.data.analysis.tabulardata.commons.utils.TimeDimensionReference;
import org.gcube.data.analysis.tabulardata.expression.Expression;
//import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.TemplateColumnAction;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TemplateColumn<T extends DataType> implements Serializable, Representable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id = UUID.randomUUID().toString();

	private ColumnCategory columnType;

	private String label;
	
	@XmlElementRefs({
		@XmlElementRef(type=DimensionReference.class),
		@XmlElementRef(type=TimeDimensionReference.class),
		@XmlElementRef(type=LocaleReference.class),
		@XmlElementRef(type=FormatReference.class)
	})
	private ReferenceObject reference;

	private List<Long> ruleIds = new ArrayList<Long>();

	private List<Expression> expressions = new ArrayList<Expression>();

	private Class<T> valueType;

	@SuppressWarnings("unused")
	private TemplateColumn(){}

	public TemplateColumn(ColumnCategory columnType, Class<T> valueType){
		if (!columnType.getAllowedClasses().contains(valueType))
			throw new IllegalArgumentException(
					String.format("valueType %s not allowed for column %s", valueType.getName(), columnType));
		if (columnType.isReferenceRequired())
			throw new IllegalArgumentException(
					String.format("a valid reference is required"));
		this.columnType = columnType;
		this.valueType = valueType;
	}

	public TemplateColumn(ColumnCategory columnType, Class<T> valueType, ReferenceObject reference){
		if (!columnType.getAllowedClasses().contains(valueType))
			throw new IllegalArgumentException(
					String.format("valueType %s not allowed for column %s", valueType.getName(), this.getClass().getSimpleName()));
		if (!columnType.isReferenceRequired() || reference==null 
				|| !reference.getClass().equals(columnType.getReferenceClass()) || !reference.check(valueType) )
			throw new IllegalArgumentException(
					String.format("a valid reference is required"));
		this.columnType = columnType;
		this.valueType = valueType;
		this.reference = reference;
	}

	/**
	 * @return the columnType
	 */
	public ColumnCategory getColumnType() {
		return columnType;
	}

	/**
	 * @return the valueType
	 */
	public Class<? extends DataType> getValueType() {
		return valueType;
	}

	/*	public List<TemplateColumnAction> getActions() {
		return actions;
	}*/

	/*	public void setActions(List<TemplateColumnAction> actions) {
		this.actions = actions;
	}*/

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	public List<Long> getRules() {
		return Collections.unmodifiableList(ruleIds);
	}


	public void addRule(Long ruleId){
		ruleIds.add(ruleId);
	}

	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the expressions
	 */
	public List<Expression> getExpressions() {
		return Collections.unmodifiableList(expressions);
	}

	/**
	 * @param expressions the expressions to set
	 */
	public void addExpression(Expression expression) {
		this.expressions.add(expression);
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the reference
	 */
	
	public ReferenceObject getReference() {
		return reference;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TemplateColumn [id=" + id + ", columnType=" + columnType
				+ ", rules=" + expressions + ", valueType="
				+ valueType.getName() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TemplateColumn<?> other = (TemplateColumn<?>) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String getRepresentation() {
		return this.label+" ("+columnType+")";
	}
	
}
