package org.gcube.data.analysis.tabulardata.cube.metadata.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.metadata.table.TableMetadata;
import org.gcube.data.analysis.tabulardata.model.table.TableType;

import com.google.common.collect.Lists;

@Entity(name = "TDTable")
@NamedQueries({ @NamedQuery(name = "Table.findAll", query = "SELECT t FROM TDTable AS t"),
		@NamedQuery(name = "Table.findAllByType", query = "SELECT t FROM TDTable AS t WHERE t.tableType = :TableType"),
		@NamedQuery(name = "Table.findById", query = "SELECT t FROM TDTable AS t WHERE t.id = :Id") ,
		@NamedQuery(name = "Table.findByName", query = "SELECT t FROM TDTable AS t WHERE t.name = :Name") })
@SequenceGenerator(name = "tdtable_seq", sequenceName = "tdtable_seq", allocationSize = 1)
public class JPATable {

	@Id
	@Column(name = "ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tdtable_seq")
	private long id;

	private String name;
	
	private TableType tableType;

	@OneToMany(cascade = CascadeType.ALL)
	@OrderBy(value="position")
	private Collection<JPAColumn> columns = Lists.newArrayList();

	private ArrayList<TableMetadata> metadata = Lists.newArrayList();

	@Column(insertable = true, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date createdTimestamp = new Date();

	@Column(insertable = true, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date updatedTimestamp = new Date();
	
	
	public JPATable() {
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TableType getTableType() {
		return tableType;
	}

	public void setTableType(TableType tableType) {
		this.tableType = tableType;
	}

	public Collection<JPAColumn> getColumns() {
		return columns;
	}

	public void setColumns(Collection<JPAColumn> columns) {
		this.columns = columns;
	}

	public Collection<TableMetadata> getMetadata() {
		return metadata;
	}

	public void setMetadata(Collection<TableMetadata> metadata) {
		this.metadata = Lists.newArrayList(metadata);
	}

	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public Date getUpdatedTimestamp() {
		return updatedTimestamp;
	}

	public JPAColumn getColumn(ColumnLocalId localId){
		for (JPAColumn col : columns)
			if (col.getLocalId()== localId.getValue())
				return col;
		return null;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JPATable [\n\tid=");
		builder.append(id);
		builder.append(",\n\tname=");
		builder.append(name);
		builder.append(",\n\ttableType=");
		builder.append(tableType);
		builder.append(",\n\tcolumns=");
		builder.append(columns);
		builder.append(",\n\tmetadata=");
		builder.append(metadata);
		builder.append(",\n\tcreatedTimestamp=");
		builder.append(createdTimestamp);
		builder.append(",\n\tupdatedTimestamp=");
		builder.append(updatedTimestamp);
		builder.append("\n]");
		return builder.toString();
	}
	
	

}
