package org.gcube.data.analysis.tabulardata.operation.parameters.leaves;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.table.TableType;
import org.gcube.data.analysis.tabulardata.model.table.type.CodelistTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetViewTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.HierarchicalCodelistTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.TimeCodelistTableType;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.LeafParameter;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TargetTableParameter extends LeafParameter<TableId> {

	private static final List<TableType> DEFAULT_ALLOWED_TABLE_TYPES=new ArrayList<>();
	
	static{
		DEFAULT_ALLOWED_TABLE_TYPES.add(new CodelistTableType());
		DEFAULT_ALLOWED_TABLE_TYPES.add(new DatasetTableType());
		DEFAULT_ALLOWED_TABLE_TYPES.add(new DatasetViewTableType());
		DEFAULT_ALLOWED_TABLE_TYPES.add(new GenericTableType());
		DEFAULT_ALLOWED_TABLE_TYPES.add(new HierarchicalCodelistTableType());
		DEFAULT_ALLOWED_TABLE_TYPES.add(new TimeCodelistTableType());
	}
	
	private List<TableType> allowedTableTypes = null;
	
	@SuppressWarnings("unused")
	private TargetTableParameter() {	}

	public TargetTableParameter(String identifier, String name, String description, Cardinality cardinality,List<TableType> allowedTableTypes) {
		super(identifier, name, description, cardinality);
		this.allowedTableTypes = allowedTableTypes;
	}

	public TargetTableParameter(String identifier, String name, String description, Cardinality cardinality) {
		this(identifier, name, description, cardinality,DEFAULT_ALLOWED_TABLE_TYPES);
	}
	
	public List<TableType> getAllowedTableTypes() {
		return allowedTableTypes;
	}

	@Override
	public Class<TableId> getParameterType() {
		return TableId.class;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((allowedTableTypes == null) ? 0 : allowedTableTypes.hashCode());
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
		TargetTableParameter other = (TargetTableParameter) obj;
		if (allowedTableTypes == null) {
			if (other.allowedTableTypes != null)
				return false;
		} else if (!allowedTableTypes.equals(other.allowedTableTypes))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TargetTableParameter [getIdentifier()=");
		builder.append(getIdentifier());
		builder.append(", getName()=");
		builder.append(getName());
		builder.append(", getDescription()=");
		builder.append(getDescription());
		builder.append(", getCardinality()=");
		builder.append(getCardinality());
		builder.append(", getAllowedTableTypes()=");
		builder.append(getAllowedTableTypes());
		builder.append(", getParameterType()=");
		builder.append(getParameterType());
		builder.append("]");
		return builder.toString();
	}
	

}
