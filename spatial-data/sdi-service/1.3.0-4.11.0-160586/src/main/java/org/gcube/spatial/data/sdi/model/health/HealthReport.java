package org.gcube.spatial.data.sdi.model.health;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class HealthReport {
	
	private Level overallStatus;
	
	private String context;
	
	private ServiceHealthReport thredds;
	private ServiceHealthReport geonetwork;
	private ServiceHealthReport geoserverCluster;
	
}
