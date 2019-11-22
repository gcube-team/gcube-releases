package org.gcube.data.analysis.tabulardata.operation.parameters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.BooleanParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.ColumnMetadataParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.ColumnTypeParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.DataTypeParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.ExpressionParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.IntegerParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.LocaleParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.LocalizedTextChoiceParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.LocalizedTextParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.MapParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.MultivaluedStringParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.RegexpStringParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.SimpleStringParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TDTypeValueParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TargetColumnParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TargetTableParameter;

@XmlSeeAlso({BooleanParameter.class, 
	DataTypeParameter.class, 
	ExpressionParameter.class, 
	LocaleParameter.class, 
	LocalizedTextChoiceParameter.class, 
	LocalizedTextParameter.class, 
	MultivaluedStringParameter.class, 
	RegexpStringParameter.class, 
	SimpleStringParameter.class, 
	TargetColumnParameter.class, 
	TargetTableParameter.class,
	TDTypeValueParameter.class,
	CompositeParameter.class,
	IntegerParameter.class,
	MapParameter.class,
	ColumnTypeParameter.class,
	LeafParameter.class,
	ColumnMetadataParameter.class
	})
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Parameter {

	private String identifier;

	private String name;

	private String description;

	private Cardinality cardinality;

	protected Parameter() {
	};

	public Parameter(String identifier, String name, String description, Cardinality cardinality) {
		this.identifier = identifier;
		this.name = name;
		this.description = description;
		this.cardinality = cardinality;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Cardinality getCardinality() {
		return cardinality;
	}

	public void setCardinality(Cardinality cardinality) {
		this.cardinality = cardinality;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cardinality == null) ? 0 : cardinality.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Parameter other = (Parameter) obj;
		if (cardinality == null) {
			if (other.cardinality != null)
				return false;
		} else if (!cardinality.equals(other.cardinality))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
