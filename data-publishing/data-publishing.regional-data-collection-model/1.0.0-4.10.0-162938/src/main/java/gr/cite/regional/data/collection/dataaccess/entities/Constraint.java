package gr.cite.regional.data.collection.dataaccess.entities;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import gr.cite.regional.data.collection.dataaccess.constraints.ConstraintDefinition;
import gr.cite.regional.data.collection.dataaccess.types.JsonUserType;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import java.sql.Types;

@javax.persistence.Entity
@Table(name="\"Constraint\"")
//@TypeDef(name = "JSONType", typeClass = JSONType.class)
@TypeDef(name = "JsonUserType", typeClass = JsonUserType.class)
public class Constraint implements Entity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="constraint_generator")
	@SequenceGenerator(name="constraint_generator", sequenceName="constraint_id_seq", allocationSize = 1)
	@Column(name="\"ID\"", updatable = false, nullable = false)
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="\"DataModel\"", nullable = false)
	private DataModel dataModel;
	
	@Column(name="\"ConstraintType\"", nullable = false)
	private String constraintType;
	
	//@Type(type = "gr.cite.regional.data.collection.dataaccess.types.JSONType")
	//@Column(name="\"Constraint\"", nullable = false, columnDefinition = "json")
	@Type(type = "JsonUserType", parameters = {
			@Parameter(name = "classType", value = "gr.cite.regional.data.collection.dataaccess.constraints.ConstraintDefinition")
	})
	@Column(name="\"Constraint\"", nullable = false, columnDefinition = "json")
	private ConstraintDefinition constraint;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public DataModel getDataModel() {
		return dataModel;
	}

	public void setDataModel(DataModel dataModel) {
		this.dataModel = dataModel;
	}

	public String getConstraintType() {
		return constraintType;
	}

	public void setConstraintType(String constraintType) {
		this.constraintType = constraintType;
	}

	public ConstraintDefinition getConstraint() {
		return constraint;
	}

	public void setConstraint(ConstraintDefinition constraint) {
		this.constraint = constraint;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		Constraint other = (Constraint) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
