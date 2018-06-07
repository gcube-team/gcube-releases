package org.gcube.spatial.data.sdi.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement
@Data
@XmlAccessorType(XmlAccessType.NONE)
public class StringEntry {
	
	@XmlElement(name = "name")
	private String name;
	@XmlElement(name = "value")
	private String value;
	
}
