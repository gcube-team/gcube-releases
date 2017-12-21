package org.gcube.data.spd.model;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class PointInfo {

	PointInfo(){};
		
	public PointInfo(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}


	@XmlAttribute(required=true)
	double x;
	@XmlAttribute(required=true)
	double y;
	
	@XmlElement(required=true, nillable=true)
	List<KeyValue> propertiesList;


	public double getY() {
		return y;
	}

	public double getX() {
		return x;
	}

	public List<KeyValue> getPropertiesList() {
		return propertiesList;
	}

	public void setPropertiesList(List<KeyValue> propertiesList) {
		this.propertiesList = propertiesList;
	}
	

	
	
	
}
