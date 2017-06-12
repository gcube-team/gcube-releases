package org.gcube.data.analysis.tabulardata.model.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.gcube.data.analysis.tabulardata.metadata.ArrayListMetadataHolder;
import org.gcube.data.analysis.tabulardata.metadata.MetadataHolder;
import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.exceptions.NoSuchColumnException;
import org.gcube.data.analysis.tabulardata.model.metadata.column.PeriodTypeMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.DescriptionsMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.TableDescriptorMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ValidationsMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.CountMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.DatasetViewTableMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.ExportMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.GcubeServiceReferenceMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.GenericMapMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.GlobalDataValidationReportMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.HarmonizationRuleTable;
import org.gcube.data.analysis.tabulardata.model.metadata.table.ImportMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.TableMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.TimePeriodTypeMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.VersionMetadata;
import org.gcube.data.analysis.tabulardata.model.relationship.TableRelationship;
import org.gcube.data.analysis.tabulardata.model.relationship.TableRelationshipImpl;
import org.gcube.data.analysis.tabulardata.model.table.type.CodelistTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetViewTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.HierarchicalCodelistTableType;

@XmlRootElement(name = "Table")
@XmlType(name = "Table")
@XmlSeeAlso(value = { ImportMetadata.class, CodelistTableType.class, DatasetTableType.class,
		HierarchicalCodelistTableType.class, DatasetViewTableType.class, GenericTableType.class })
@XmlAccessorType(XmlAccessType.NONE)
public class Table implements MetadataHolder<TableMetadata>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8626095063616457984L;

	@XmlAttribute
	private TableId id = null;

	@XmlAttribute
	private String name = null;

	@XmlElementWrapper(name = "Columns")
	@XmlElement(name = "Column")
	private List<Column> columns = new ArrayList<Column>();

	private ArrayListMetadataHolder<TableMetadata> metadataDelegate = new ArrayListMetadataHolder<TableMetadata>();

	@XmlElementRefs({
		@XmlElementRef(type = CountMetadata.class),
		@XmlElementRef(type = DescriptionsMetadata.class),
		@XmlElementRef(type = DatasetViewTableMetadata.class),
		@XmlElementRef(type = ExportMetadata.class), 
		@XmlElementRef(type = GenericMapMetadata.class), 
		@XmlElementRef(type = GlobalDataValidationReportMetadata.class),
		@XmlElementRef(type = ImportMetadata.class),
		@XmlElementRef(type = NamesMetadata.class),
		@XmlElementRef(type = TimePeriodTypeMetadata.class),
		@XmlElementRef(type = ValidationsMetadata.class),
		@XmlElementRef(type = VersionMetadata.class), 
		@XmlElementRef(type = TableDescriptorMetadata.class),
		@XmlElementRef(type = HarmonizationRuleTable.class),
		@XmlElementRef(type= GcubeServiceReferenceMetadata.class),
	})
	private List<TableMetadata> getMetadata() {
		return metadataDelegate.metadata;
	}

	@XmlElementRef
	private TableType tableType;

	@SuppressWarnings("unused")
	private Table() {
		// Serialization only
	}

	public Table(TableType tableType) {
		setTableType(tableType);
	}

	public TableId getId() {
		return id;
	}

	public void setId(TableId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name == null || name.isEmpty())
			throw new IllegalArgumentException("Table name cannot be null or empty");
		this.name = name;
	}

	public boolean hasName() {
		return name != null ? true : false;
	}

	public TableType getTableType() {
		return tableType;
	}

	public void setTableType(TableType tableType) {
		this.tableType = tableType;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public Column getColumnByName(String columnName) throws NoSuchColumnException {

		Collection<Column> result = new ArrayList<Column>();
		for (Column column : getColumns()) {
			if (column.getName().equals(columnName))
				result.add(column);
		}

		if (result.size() > 1)
			throw new RuntimeException("Found multiple column with the same name '"+columnName+"'.");

		if (result.size() == 0)
			throw new NoSuchColumnException(columnName, this);

		return result.iterator().next();
	}

	
	public Column getColumnByLabel(String label) throws NoSuchColumnException{
		for(Column col:this.getColumns()){
			if(col.getName().equals(label)) return col;
			try{
				for(LocalizedText localizedLabel:col.getMetadata(NamesMetadata.class).getTexts())
					if(localizedLabel.getValue().equals(label)) return col;
			}catch(NoSuchMetadataException e){
				//skip column
			}
		}
		throw new NoSuchColumnException(label,this);
	}
	
	public Column getColumnById(ColumnLocalId columnId) throws NoSuchColumnException {
		Collection<Column> columns = new ArrayList<Column>();

		for (Column column : getColumns()) {
			if (column.getLocalId().equals(columnId))
				columns.add(column);
		}

		if (columns.size() > 1)
			throw new RuntimeException("Found multiple column with the same id '"+id+"'.");
		if (columns.size() == 0)
			throw new NoSuchColumnException(columnId, this);
		return columns.iterator().next();
	}

	public List<Column> getColumnsByType(ColumnType... columnTypes) {
		List<Column> result = new ArrayList<Column>();
		for (Column column : getColumns()) {
			for (int i = 0; i < columnTypes.length; i++) {
				if (column.getColumnType().equals(columnTypes[i]))
					result.add(column);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Column> getColumnsByType(Class<? extends ColumnType>... columnTypes) {
		List<Column> result = new ArrayList<Column>();
		for (Column column : getColumns()) {
			for (int i = 0; i < columnTypes.length; i++) {
				if (column.getColumnType().getClass().equals(columnTypes[i]))
					result.add(column);
			}
		}
		return result;
	}

	public List<Column> getColumnsExceptTypes(ColumnType... columnTypes) {
		List<Column> result = new ArrayList<Column>();
		List<ColumnType> columnTypesList = new ArrayList<ColumnType>();
		Collections.addAll(columnTypesList, columnTypes);

		for (Column column : getColumns()) {
			if (!columnTypesList.contains(column.getColumnType()))
				result.add(column);
		}
		return result;
	}

	public List<Column> getColumnsExceptTypes(@SuppressWarnings("unchecked") Class<? extends ColumnType>... columnTypes) {
		List<Class<? extends ColumnType>> columnTypesList = new ArrayList<Class<? extends ColumnType>>();
		Collections.addAll(columnTypesList, columnTypes);
		List<Column> result = new ArrayList<Column>();
		for (Column column : getColumns()) {
			if (!columnTypesList.contains(column.getColumnType().getClass()))
				result.add(column);
		}
		return result;
	}

	public boolean hasRelationships() {
		for (Column column : getColumns()) {
			if (column.getRelationship() != null)
				return true;
		}
		return false;
	}

	public List<TableRelationship> getCodelistRelationships() {
		List<TableRelationship> result = new ArrayList<TableRelationship>();
		for (Column column : getForeignKeyColumns()) {
			result.add(new TableRelationshipImpl(column.getRelationship(), getId(), column.getLocalId()));
		}
		return result;
	}

	public List<Column> getForeignKeyColumns() {
		List<Column> fkColumns = new ArrayList<Column>();
		for (Column column : getColumns()) {
			if (column.getRelationship() != null && !column.contains(PeriodTypeMetadata.class))
				fkColumns.add(column);
		}
		return fkColumns;
	}

	public ColumnReference getColumnReference(Column column) {
		if (!columns.contains(column))
			throw new IllegalArgumentException("Table does not contain given column");
		return new ColumnReference(id, column.getLocalId(),column.getDataType());
	}

	public <C extends TableMetadata> C getMetadata(Class<C> metadataType) {
		C meta = metadataDelegate.getMetadata(metadataType);
		if (meta== null) throw new NoSuchMetadataException(metadataType);
		return meta;
	}

	public void removeMetadata(Class<? extends TableMetadata> metadataType) {
		metadataDelegate.removeMetadata(metadataType);
	}

	public void setMetadata(TableMetadata metadata) {
		metadataDelegate.setMetadata(metadata);
	}

	public Collection<TableMetadata> getAllMetadata() {
		return metadataDelegate.getAllMetadata();
	}

	public void setAllMetadata(Collection<TableMetadata> metadata) {
		metadataDelegate.setAllMetadata(metadata);
	}

	public void removeAllMetadata() {
		metadataDelegate.removeAllMetadata();
	}

	@Override
	public boolean contains(Class<? extends TableMetadata> metadataType) {
		return metadataDelegate.contains(metadataType);
	}
	
	@SuppressWarnings("unchecked")
	public boolean sameStructureAs(Table table) {
		if (!this.getTableType().equals(table.getTableType()))
			return false;
		if (this.getColumnsExceptTypes(ValidationColumnType.class).size()!=table.getColumnsExceptTypes(ValidationColumnType.class).size())
			return false;
		for (Column column : this.getColumnsExceptTypes(ValidationColumnType.class)) {
			boolean columnWithSameStructurePresent = false;
			for (Column otherTableColumn : table.getColumns())
				if (column.sameStructureAs(otherTableColumn))
					columnWithSameStructurePresent = true;
			if (columnWithSameStructurePresent == false)
				return false;
		}
		/*for (Column column : table.getColumns()) {
			boolean columnWithSameStructurePresent = false;
			for (Column thisTableColumn : this.getColumns())
				if (column.sameStructureAs(thisTableColumn))
					columnWithSameStructurePresent = true;
			if (columnWithSameStructurePresent == false)
				return false;
		}*/

		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((columns == null) ? 0 : columns.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((metadataDelegate == null) ? 0 : metadataDelegate.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((tableType == null) ? 0 : tableType.hashCode());
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
		Table other = (Table) obj;
		if (columns == null) {
			if (other.columns != null)
				return false;
		} else if (!columns.equals(other.columns))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (metadataDelegate == null) {
			if (other.metadataDelegate != null)
				return false;
		} else if (!metadataDelegate.equals(other.metadataDelegate))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (tableType == null) {
			if (other.tableType != null)
				return false;
		} else if (!tableType.equals(other.tableType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Table [\n\tid=");
		if (id != null)
			builder.append(id.getValue());
		else
			builder.append("null");
		builder.append(",\n\tname=");
		builder.append(name);
		builder.append(",\n\tcolumns=\n");
		for (int i = 0; i < columns.size(); i++)
			builder.append(i + ":" + columns.get(i) + "\n");
		builder.append(",\n\ttableType=");
		builder.append(tableType);
		builder.append(",\n\tgetAllMetadata()=");
		builder.append(getAllMetadata());
		builder.append("\n]");
		return builder.toString();
	}

}