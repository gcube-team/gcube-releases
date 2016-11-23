package org.gcube.data.analysis.tabulardata.operation.parameters.leaves;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AnnotationColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeDescriptionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeNameColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.MeasureColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.LeafParameter;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ColumnTypeParameter extends LeafParameter<ColumnType> {

	private static final List<ColumnType> DEFAULT_ALLOWED_COLUMN_TYPES=new ArrayList<>();
	
	static{
		DEFAULT_ALLOWED_COLUMN_TYPES.add(new AnnotationColumnType());
		DEFAULT_ALLOWED_COLUMN_TYPES.add(new AttributeColumnType());
		DEFAULT_ALLOWED_COLUMN_TYPES.add(new CodeColumnType());
		DEFAULT_ALLOWED_COLUMN_TYPES.add(new CodeDescriptionColumnType());
		DEFAULT_ALLOWED_COLUMN_TYPES.add(new CodeNameColumnType());
		DEFAULT_ALLOWED_COLUMN_TYPES.add(new DimensionColumnType());
		DEFAULT_ALLOWED_COLUMN_TYPES.add(new MeasureColumnType());
		DEFAULT_ALLOWED_COLUMN_TYPES.add(new TimeDimensionColumnType());
	}
	
	@SuppressWarnings("unused")
	private ColumnTypeParameter() {}

	private List<ColumnType> allowedColumnTypes;

	public ColumnTypeParameter(String identifier, String name,
			String description, Cardinality cardinality,
			List<ColumnType> allowedColumnTypes) {
		super(identifier, name, description, cardinality);
		this.allowedColumnTypes = allowedColumnTypes;
	}
	
	
	public ColumnTypeParameter(String identifier, String name,
			String description, Cardinality cardinality) {
		this(identifier,name,description,cardinality,DEFAULT_ALLOWED_COLUMN_TYPES);
	}
	
	
	public List<ColumnType> getAllowedColumnTypes() {
		return allowedColumnTypes;
	}
	
	@Override
	public Class<ColumnType> getParameterType() {		
		return ColumnType.class;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((allowedColumnTypes == null) ? 0 : allowedColumnTypes
						.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ColumnTypeParameter other = (ColumnTypeParameter) obj;
		if (allowedColumnTypes == null) {
			if (other.allowedColumnTypes != null)
				return false;
		} else if (!allowedColumnTypes.equals(other.allowedColumnTypes))
			return false;
		return true;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ColumnTypeParameter [getAllowedColumnTypes()=");
		builder.append(getAllowedColumnTypes());
		builder.append(", getParameterType()=");
		builder.append(getParameterType());
		builder.append(", getIdentifier()=");
		builder.append(getIdentifier());
		builder.append(", getName()=");
		builder.append(getName());
		builder.append(", getDescription()=");
		builder.append(getDescription());
		builder.append(", getCardinality()=");
		builder.append(getCardinality());
		builder.append("]");
		return builder.toString();
	}

	@Override
	public void validateValue(Object value) throws Exception {
		super.validateValue(value);
		boolean ok=false;
		for(ColumnType allowed:allowedColumnTypes)
			if(allowed.getClass().equals(value.getClass())) ok=true;
		if(!ok) throw new Exception(String.format("Passed argument %s is not among valid ones %s ",value,this.getAllowedColumnTypes()));
	}
}
