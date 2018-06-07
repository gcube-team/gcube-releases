package org.gcube.spatial.data.sdi.model.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GeoNetworkServiceDefinition extends ServiceDefinition {
	
	@XmlElement(name = "priority")	
	private int priority;
	
	@Override
	public Type getType() {
		return Type.GEONETWORK;
	}
	
	
	
}
