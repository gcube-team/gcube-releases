package org.gcube.documentstore.records;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
//@XmlAccessorType(XmlAccessType.NONE)
public class SerializableList<String> {
 
	@XmlElement
	private List<String> valuesList = new ArrayList<String>();
 
	protected SerializableList(){}
 
	public SerializableList(List<String> valuesList) {
		super();
		this.valuesList = valuesList;
	}
 
	public List<String> getValuesList() {
		return valuesList;
	}
 }
 