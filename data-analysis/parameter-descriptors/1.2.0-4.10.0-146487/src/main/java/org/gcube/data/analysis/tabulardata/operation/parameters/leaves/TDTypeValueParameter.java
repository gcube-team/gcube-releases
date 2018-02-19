package org.gcube.data.analysis.tabulardata.operation.parameters.leaves;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.DateType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.LeafParameter;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TDTypeValueParameter extends LeafParameter<TDTypeValue>{

	private static final List<DataType> DEFAULT_ALLOWED_DATA_TYPES=new ArrayList<DataType>();
	
	static{
		DEFAULT_ALLOWED_DATA_TYPES.add(new BooleanType());
		DEFAULT_ALLOWED_DATA_TYPES.add(new DateType());
		DEFAULT_ALLOWED_DATA_TYPES.add(new GeometryType());
		DEFAULT_ALLOWED_DATA_TYPES.add(new IntegerType());
		DEFAULT_ALLOWED_DATA_TYPES.add(new NumericType());
		DEFAULT_ALLOWED_DATA_TYPES.add(new TextType());
	}
	
	private List<DataType> allowedDataTypes=DEFAULT_ALLOWED_DATA_TYPES;
	
	
	public TDTypeValueParameter() {
		super();
	}

	public TDTypeValueParameter(String identifier, String name,
			String description, Cardinality cardinality) {
		super(identifier, name, description, cardinality);
	}

	public TDTypeValueParameter(String identifier, String name,
			String description, Cardinality cardinality,List<DataType> allowedDataTypes) {
		super(identifier, name, description, cardinality);
		this.allowedDataTypes=allowedDataTypes;
	}
	
	public List<DataType> getAllowedDataTypes() {
		return allowedDataTypes;
	}
	
	public void setAllowedDataTypes(List<DataType> allowedDataTypes) {
		this.allowedDataTypes = allowedDataTypes;
	}
	
	
	@Override
	public Class<TDTypeValue> getParameterType() {		
		return TDTypeValue.class;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TDTypeValueParameter [allowedDataTypes=");
		builder.append(allowedDataTypes);
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((allowedDataTypes == null) ? 0 : allowedDataTypes.hashCode());
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
		TDTypeValueParameter other = (TDTypeValueParameter) obj;
		if (allowedDataTypes == null) {
			if (other.allowedDataTypes != null)
				return false;
		} else if (!allowedDataTypes.equals(other.allowedDataTypes))
			return false;
		return true;
	}


	@Override
	public void validateValue(Object valueObject) throws Exception {
		super.validateValue(valueObject);
		TDTypeValue value=(TDTypeValue) valueObject;
		boolean ok=false;
		for(DataType type:allowedDataTypes)
			if(type.getClass().isInstance(value.getReturnedDataType())) ok=true;
		if(!ok)throw new Exception(String.format("Invalid data type %s, allowed types are : %s.", value.getReturnedDataType(),allowedDataTypes));
	}
	
}
