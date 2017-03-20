package org.gcube.datatransformation.adaptors.common.db.xmlobjects;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Table implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@XmlElement(name="name")
	String name;
	@XmlElement(name="sql")
	String sql;
	
	public String getName(){
		return name;
	}
	
	
	public String getSql(){
		return sql;
	}
	
	public void setSql(String newSql){
		this.sql = newSql;
	}
	
}
