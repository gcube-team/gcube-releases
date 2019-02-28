package org.gcube.data.analysis.tabulardata.operation.parameters.leaves;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AnnotationColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeDescriptionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeNameColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.MeasureColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.DateType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
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
public class TargetColumnParameter extends LeafParameter<ColumnReference>  {

	private static final List<ColumnType> DEFAULT_ALLOWED_COLUMN_TYPES=new ArrayList<>();
	private static final List<TableType> DEFAULT_ALLOWED_TABLE_TYPES=new ArrayList<>();
	private static final List<DataType> DEFAULT_ALLOWED_DATA_TYPES=new ArrayList<DataType>();
	
	
	static{
		DEFAULT_ALLOWED_COLUMN_TYPES.add(new AnnotationColumnType());
		DEFAULT_ALLOWED_COLUMN_TYPES.add(new AttributeColumnType());
		DEFAULT_ALLOWED_COLUMN_TYPES.add(new CodeColumnType());
		DEFAULT_ALLOWED_COLUMN_TYPES.add(new CodeDescriptionColumnType());
		DEFAULT_ALLOWED_COLUMN_TYPES.add(new CodeNameColumnType());
		DEFAULT_ALLOWED_COLUMN_TYPES.add(new DimensionColumnType());
		DEFAULT_ALLOWED_COLUMN_TYPES.add(new MeasureColumnType());
		DEFAULT_ALLOWED_COLUMN_TYPES.add(new TimeDimensionColumnType());
		
		DEFAULT_ALLOWED_TABLE_TYPES.add(new CodelistTableType());
		DEFAULT_ALLOWED_TABLE_TYPES.add(new DatasetTableType());
		DEFAULT_ALLOWED_TABLE_TYPES.add(new DatasetViewTableType());
		DEFAULT_ALLOWED_TABLE_TYPES.add(new GenericTableType());
		DEFAULT_ALLOWED_TABLE_TYPES.add(new HierarchicalCodelistTableType());
		DEFAULT_ALLOWED_TABLE_TYPES.add(new TimeCodelistTableType());
		
		DEFAULT_ALLOWED_DATA_TYPES.add(new BooleanType());
		DEFAULT_ALLOWED_DATA_TYPES.add(new DateType());
		DEFAULT_ALLOWED_DATA_TYPES.add(new GeometryType());
		DEFAULT_ALLOWED_DATA_TYPES.add(new IntegerType());
		DEFAULT_ALLOWED_DATA_TYPES.add(new NumericType());
		DEFAULT_ALLOWED_DATA_TYPES.add(new TextType());
	}
	
	
	
	
	private List<TableType> allowedTableTypes = DEFAULT_ALLOWED_TABLE_TYPES;

	private List<ColumnType> allowedColumnTypes = DEFAULT_ALLOWED_COLUMN_TYPES;

	private List<DataType> allowedDataTypes=DEFAULT_ALLOWED_DATA_TYPES;
	
	
	@SuppressWarnings("unused")
	private TargetColumnParameter() {}

	public TargetColumnParameter(String identifier, String name,
			String description, Cardinality cardinality,
			List<TableType> allowedTableTypes,
			List<ColumnType> allowedColumnTypes, List<DataType> allowedDataTypes) {
		super(identifier, name, description, cardinality);
		this.allowedTableTypes = allowedTableTypes;
		this.allowedColumnTypes = allowedColumnTypes;
		this.allowedDataTypes = allowedDataTypes;
	}

	
	
	public TargetColumnParameter(String identifier, String name, String description, Cardinality cardinality,
			List<TableType> allowedTableTypes, List<ColumnType> allowedColumnTypes) {
		this(identifier, name, description, cardinality,allowedTableTypes,allowedColumnTypes,DEFAULT_ALLOWED_DATA_TYPES);		
	}

	public TargetColumnParameter(String identifier, String name, String description, Cardinality cardinality,
			List<TableType> allowedTableTypes) {
		this(identifier, name, description, cardinality,allowedTableTypes,DEFAULT_ALLOWED_COLUMN_TYPES);
	}
	
	public TargetColumnParameter(String identifier, String name, String description, Cardinality cardinality) {
		this(identifier, name, description, cardinality,DEFAULT_ALLOWED_TABLE_TYPES,DEFAULT_ALLOWED_COLUMN_TYPES);
	}
	
	
	
	
	@Override
	public Class<ColumnReference> getParameterType() {
		return ColumnReference.class;
	}

	public List<TableType> getAllowedTableTypes() {
		return allowedTableTypes;
	}

	public List<ColumnType> getAllowedColumnTypes() {
		return allowedColumnTypes;
	}
	
	public List<DataType> getAllowedDataTypes() {
		return allowedDataTypes;
	}
	
	public void setAllowedColumnTypes(List<ColumnType> allowedColumnTypes) {
		this.allowedColumnTypes = allowedColumnTypes;
	}

	/**
	 * @param allowedTableTypes the allowedTableTypes to set
	 */
	public void setAllowedTableTypes(List<TableType> allowedTableTypes) {
		this.allowedTableTypes = allowedTableTypes;
	}

	/**
	 * @param allowedDataTypes the allowedDataTypes to set
	 */
	public void setAllowedDataTypes(List<DataType> allowedDataTypes) {
		this.allowedDataTypes = allowedDataTypes;
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
		result = prime
				* result
				+ ((allowedDataTypes == null) ? 0 : allowedDataTypes.hashCode());
		result = prime
				* result
				+ ((allowedTableTypes == null) ? 0 : allowedTableTypes
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
		TargetColumnParameter other = (TargetColumnParameter) obj;
		if (allowedColumnTypes == null) {
			if (other.allowedColumnTypes != null)
				return false;
		} else if (!allowedColumnTypes.equals(other.allowedColumnTypes))
			return false;
		if (allowedDataTypes == null) {
			if (other.allowedDataTypes != null)
				return false;
		} else if (!allowedDataTypes.equals(other.allowedDataTypes))
			return false;
		if (allowedTableTypes == null) {
			if (other.allowedTableTypes != null)
				return false;
		} else if (!allowedTableTypes.equals(other.allowedTableTypes))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TargetColumnParameter [allowedTableTypes=");
		builder.append(allowedTableTypes);
		builder.append(", allowedColumnTypes=");
		builder.append(allowedColumnTypes);
		builder.append(", allowedDataTypes=");
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
	

	
}
