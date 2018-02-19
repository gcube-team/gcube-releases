package gr.cite.regional.data.collection.dataaccess.entities;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Map;
import java.util.UUID;

//@javax.persistence.Entity
//@Table(name = "\"CDT_37\"")
public class Cdt /*implements Entity*/ {
	//@Id
	//@GenericGenerator(name = "uuid-gen", strategy = "uuid2")
	//@GeneratedValue(generator = "uuid-gen")
	//@Type(type="org.hibernate.type.PostgresUUIDType")
	//@Column(name="\"ID\"", nullable = false)
	private UUID id;
	
	//@ManyToOne(fetch = FetchType.EAGER)
	//@JoinColumn(name = "\"DataSubmission\"", nullable = false)
	private DataSubmission dataSubmission;
	
	//@Column(name = "\"Ordinal\"", nullable = false)
	private Integer ordinal;
	
	//@Column(name = "\"Status\"", nullable = false)
	private Integer status;
	
	private Map<String, Object> data;
	
	public UUID getId() {
		return id;
	}
	
	public void setId(UUID id) {
		this.id = id;
	}
	
	public DataSubmission getDataSubmission() {
		return dataSubmission;
	}
	
	public void setDataSubmission(DataSubmission dataSubmission) {
		this.dataSubmission = dataSubmission;
	}
	
	public Integer getOrdinal() {
		return ordinal;
	}
	
	public void setOrdinal(Integer ordinal) {
		this.ordinal = ordinal;
	}
	
	public Integer getStatus() {
		return status;
	}
	
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public Map<String, Object> getData() {
		return data;
	}
	
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
}
