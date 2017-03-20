package org.gcube.data.analysis.tabulardata.model.column;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.gcube.data.analysis.tabulardata.metadata.ArrayListMetadataHolder;
import org.gcube.data.analysis.tabulardata.metadata.MetadataHolder;
import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.gcube.data.analysis.tabulardata.model.column.type.AnnotationColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeDescriptionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeNameColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.MeasureColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.DateType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ColumnMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataLocaleMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataValidationMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.column.PeriodTypeMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ValidationReferencesMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ViewColumnMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.DescriptionsMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ValidationsMetadata;
import org.gcube.data.analysis.tabulardata.model.relationship.ColumnRelationship;
import org.gcube.data.analysis.tabulardata.model.relationship.ImmutableColumnRelationship;

@XmlRootElement(name = "Column")
@XmlType(name = "Column")
@XmlSeeAlso({ Object.class, BooleanType.class, DateType.class, IntegerType.class, GeometryType.class, TextType.class,
		NamesMetadata.class, ViewColumnMetadata.class, DataLocaleMetadata.class, AnnotationColumnType.class,
		AttributeColumnType.class, CodeColumnType.class, CodeDescriptionColumnType.class, CodeNameColumnType.class,
		DimensionColumnType.class, IdColumnType.class, MeasureColumnType.class, TimeDimensionColumnType.class,
		ValidationColumnType.class, ValidationReferencesMetadata.class, DataValidationMetadata.class,PeriodTypeMetadata.class })
@XmlAccessorType(XmlAccessType.NONE)
public class Column implements MetadataHolder<ColumnMetadata>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 222621862202261888L;

	private transient TDTypeValue creationDefaultValue = null;
	
	@XmlAttribute
	private ColumnLocalId localId;

	@XmlAttribute
	private String name = null;

	@XmlElementRef
	private DataType dataType;

	@XmlElement(type = ImmutableColumnRelationship.class, name = "Relationship")
	private ColumnRelationship relationship = null;

	@XmlElementRef
	private ColumnType columnType;

	private ArrayListMetadataHolder<ColumnMetadata> metadataDelegate = new ArrayListMetadataHolder<ColumnMetadata>();
	
	@XmlElementRefs({
		@XmlElementRef(type=DataLocaleMetadata.class),
		@XmlElementRef(type=DataValidationMetadata.class),
		@XmlElementRef(type=DescriptionsMetadata.class),
		@XmlElementRef(type=NamesMetadata.class),
		@XmlElementRef(type=ValidationReferencesMetadata.class),
		@XmlElementRef(type=ValidationsMetadata.class),
		@XmlElementRef(type=ViewColumnMetadata.class),
		@XmlElementRef(type=PeriodTypeMetadata.class),
	})
	private List<ColumnMetadata> getMetadata(){
		return metadataDelegate.metadata;
	}

	@SuppressWarnings("unused")
	private Column() {
		// Serialization only
	}

	public Column(ColumnLocalId columnId, DataType dataType, ColumnType columnType) {
		setLocalId(columnId);
		setDataType(dataType);
		setColumnType(columnType);
	}

	public ColumnLocalId getLocalId() {
		return localId;
	}

	public void setLocalId(ColumnLocalId localId) {
		this.localId = localId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean hasName() {
		return name != null ? true : false;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		if (dataType == null)
			throw new IllegalArgumentException("DataType cannot be null");
		this.dataType = dataType;
	}

	public ColumnRelationship getRelationship() {
		return relationship;
	}

	public void setRelationship(ColumnRelationship columnRelationship) {
		relationship = columnRelationship;
	}

	public boolean hasRelationship() {
		return relationship != null ? true : false;
	}

	public ColumnType getColumnType() {
		return columnType;
	}

	public void setColumnType(ColumnType columnType) {
		if (columnType == null)
			throw new IllegalArgumentException("ColumnType cannot be null");
		this.columnType = columnType;
	}

	

	public <C extends ColumnMetadata> C getMetadata(Class<C> metadataType){
		C meta = metadataDelegate.getMetadata(metadataType);
		if (meta== null) throw new NoSuchMetadataException(metadataType);
		return meta;
	}

	public void removeMetadata(Class<? extends ColumnMetadata> metadataType) {
		metadataDelegate.removeMetadata(metadataType);
	}

	public void setMetadata(ColumnMetadata metadata) {
		metadataDelegate.setMetadata(metadata);
	}

	public Collection<ColumnMetadata> getAllMetadata() {
		return metadataDelegate.getAllMetadata();
	}

	public void setAllMetadata(Collection<ColumnMetadata> metadata) {
		metadataDelegate.setAllMetadata(metadata);
	}

	public void removeAllMetadata() {
		metadataDelegate.removeAllMetadata();
	}
		
	public TDTypeValue getCreationDefaultValue() {
		return creationDefaultValue;
	}

	public void setCreationDefaultValue(TDTypeValue creationDefaultValue) {
		this.creationDefaultValue = creationDefaultValue;
	}

	@Override
	public boolean contains(Class<? extends ColumnMetadata> metadataType) {
		return metadataDelegate.contains(metadataType);
	}
	
	
	public boolean sameStructureAs(Column column) {
		/*if (!this.name.equals(column.name))
			return false;*/
		if (!this.dataType.equals(column.dataType))
			return false;
		// if ((this.relationship == null && column.relationship != null)
		// || (this.relationship != null && column.relationship == null))
		// return false;
		if (this.relationship != null && !this.relationship.equals(column.relationship))
			return false;
		if (!this.columnType.equals(column.columnType))
			return false;
		return true;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((localId == null) ? 0 : localId.hashCode());
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
		Column other = (Column) obj;
		if (localId == null) {
			if (other.localId != null)
				return false;
		} else if (!localId.equals(other.localId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Column [localId=");
		if (localId!=null) builder.append(localId.getValue());
		else builder.append("null");
		builder.append(", name=");
		builder.append(name);
		builder.append(", dataType=");
		builder.append(dataType);
		builder.append(", relationship=");
		builder.append(relationship);
		builder.append(", columnType=");
		builder.append(columnType);
		builder.append(", metadata=");
		builder.append(getAllMetadata());
		builder.append("]");
		return builder.toString();
	}

	

}