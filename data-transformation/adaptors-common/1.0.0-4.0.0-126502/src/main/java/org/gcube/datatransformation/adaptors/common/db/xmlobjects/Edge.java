package org.gcube.datatransformation.adaptors.common.db.xmlobjects;

import javax.xml.bind.annotation.XmlElement;

public class Edge {

	@XmlElement(name="parent")
	String parent;
	@XmlElement(name="pkeys")
	String pkeys;
	@XmlElement(name="child")
	String child;
	@XmlElement(name="ckeys")
	String ckeys;
	
	
	public String getParent(){
		return parent;
	}
	public String getPKeys(){
		return pkeys;
	}
	public String getChild(){
		return child;
	}
	public String getCKeys(){
		return ckeys;
	}
	
	
}
