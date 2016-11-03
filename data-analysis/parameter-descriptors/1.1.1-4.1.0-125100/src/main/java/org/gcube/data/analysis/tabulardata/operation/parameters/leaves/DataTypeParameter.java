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
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.LeafParameter;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DataTypeParameter extends LeafParameter<DataType>{

	public List<DataType> allowedDataTypes;
	
	@SuppressWarnings("unused")
	private DataTypeParameter() {}

	public DataTypeParameter(String identifier, String name, String description, Cardinality cardinality,
			List<DataType> allowedDataTypes) {
		super(identifier, name, description, cardinality);
		this.allowedDataTypes = allowedDataTypes;
	}

	public DataTypeParameter(String identifier, String name, String description, Cardinality cardinality) {
		super(identifier, name, description, cardinality);
		// TODO-LF Solution with classpath scanning
		allowedDataTypes = new ArrayList<DataType>();
		allowedDataTypes.add(new BooleanType());
		allowedDataTypes.add(new DateType());
		allowedDataTypes.add(new IntegerType());
		allowedDataTypes.add(new NumericType());
		allowedDataTypes.add(new TextType());
		allowedDataTypes.add(new GeometryType());
	}

	public List<DataType> getAllowedDataTypes() {
		return allowedDataTypes;
	}

	@Override
	public Class<DataType> getParameterType() {
		return DataType.class;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((allowedDataTypes == null) ? 0 : allowedDataTypes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataTypeParameter other = (DataTypeParameter) obj;
		if (allowedDataTypes == null) {
			if (other.allowedDataTypes != null)
				return false;
		} else if (!allowedDataTypes.equals(other.allowedDataTypes))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DataTypeParameter [getIdentifier()=");
		builder.append(getIdentifier());
		builder.append(", getName()=");
		builder.append(getName());
		builder.append(", getDescription()=");
		builder.append(getDescription());
		builder.append(", getCardinality()=");
		builder.append(getCardinality());
		builder.append(", getAllowedDataTypes()=");
		builder.append(getAllowedDataTypes());
		builder.append(", getParameterType()=");
		builder.append(getParameterType());
		builder.append("]");
		return builder.toString();
	}

	@Override
	public void validateValue(Object value) throws Exception {
		super.validateValue(value);
		boolean ok=false;
		for(DataType allowed:this.getAllowedDataTypes()){
			if(value.getClass().equals(allowed.getClass())){
				ok=true;
				break;
			}
		}
		//Class not among allowed
		if(!ok) throw new Exception(String.format("Passed argument %s is not among valid ones %s ",value,this.getAllowedDataTypes()));
	}
}
