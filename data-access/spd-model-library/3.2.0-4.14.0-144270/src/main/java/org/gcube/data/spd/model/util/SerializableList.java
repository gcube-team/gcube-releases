package org.gcube.data.spd.model.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class SerializableList<T> {

	@XmlElement
	private List<T> valuesList = new ArrayList<T>();

	protected SerializableList(){}
	
	public SerializableList(List<T> valuesList) {
		super();
		this.valuesList = valuesList;
	}

	public List<T> getValuesList() {
		return valuesList;
	}

	@Override
	public String toString() {
		return valuesList.toString();
	}

	
}
