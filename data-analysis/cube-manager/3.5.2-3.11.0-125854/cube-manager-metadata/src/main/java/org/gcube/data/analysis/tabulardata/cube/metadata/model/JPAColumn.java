package org.gcube.data.analysis.tabulardata.cube.metadata.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ColumnMetadata;
import org.gcube.data.analysis.tabulardata.model.relationship.ColumnRelationship;

import com.google.common.collect.Lists;

@Entity(name = "TDColumn")
@SequenceGenerator(name = "tdcolumn_seq", sequenceName = "tdcolumn_seq", allocationSize = 1)
public class JPAColumn {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tdcolumn_seq")
	private long id;
	
	private String localId;

	private String name;

	private ColumnType type;

	private DataType dataType;
	
	private int position;
	
	private ColumnRelationship relationship = null;
	
	private ArrayList<ColumnMetadata> metadata = Lists.newArrayList();

	@Column(insertable = true, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date createdTimestamp = new Date();

	@Column(insertable = true, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date updatedTimestamp = new Date();
	
	public JPAColumn() {
	}
		
	public void setColumnId(long id) {
		this.id = id;
	}
		
	public long getColumnId() {
		return id;
	}

	public String getLocalId() {
		return localId;
	}

	public void setLocalId(String localId) {
		this.localId = localId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ColumnType getType() {
		return type;
	}

	public void setType(ColumnType type) {
		this.type = type;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public ColumnRelationship getRelationship() {
		return relationship;
	}

	public void setRelationship(ColumnRelationship relationship) {
		this.relationship = relationship;
	}

	public Collection<ColumnMetadata> getMetadata() {
		return metadata;
	}

	public void setMetadata(Collection<ColumnMetadata> metadata) {
		this.metadata = Lists.newArrayList(metadata);
	}
	
	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JPAColumn [\n\tid=");
		builder.append(id);
		builder.append(",\n\tposition=");
		builder.append(position);
		builder.append(",\n\tlocalId=");
		builder.append(localId);
		builder.append(",\n\tname=");
		builder.append(name);
		builder.append(",\n\ttype=");
		builder.append(type);
		builder.append(",\n\tdataType=");
		builder.append(dataType);
		builder.append(",\n\trelationship=");
		builder.append(relationship);
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
