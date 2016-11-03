package org.gcube.data.spd.model.products;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class TaxonomyStatus implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum Status{
		ACCEPTED, 
		SYNONYM,
		VALID, 
		INVALID,
		MISAPPLIED,
		PROVISIONAL,
		UNKNOWN
	} 
	
	protected TaxonomyStatus(){	}
	
	public TaxonomyStatus(String statusAsString, Status status){
		this.status = status;
		this.statusAsString = statusAsString;
		this.refId = null;
	}
	
	public TaxonomyStatus(Status status, String refId){
		this.status = status;
		this.refId = refId;
	}
	
	public TaxonomyStatus(Status status, String refId, String statusAsString){
		this.status = status;
		this.refId = refId;
		this.statusAsString = statusAsString;
	}
	
	@XmlAttribute
	private String refId;

	@XmlAttribute
	private Status status;

	@XmlAttribute
	private String statusAsString="";
	
	
	public String getRefId() {
		return refId;
	}

	public Status getStatus() {
		return status;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}
	
	public String getStatusAsString() {
		return statusAsString;
	}

	public void setStatusAsString(String statusAsString) {
		this.statusAsString = statusAsString;
	}

	@Override
	public String toString(){
		StringBuilder toReturn= new StringBuilder();
		toReturn.append("[value: "+status.toString()+"]");
		if(refId!=null) toReturn.append("[refId :"+refId+"]");
		return toReturn.toString();
	}
}
