package org.gcube.spatial.data.sdi.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.spatial.data.sdi.model.service.GeoNetworkConfiguration;
import org.gcube.spatial.data.sdi.model.service.GeoServerClusterConfiguration;
import org.gcube.spatial.data.sdi.model.service.ThreddsConfiguration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ScopeConfiguration {
	
	
	
	@NonNull
	private String contextName;
	
	@NonNull
	private GeoNetworkConfiguration geonetworkConfiguration;
	@NonNull
	private GeoServerClusterConfiguration geoserverClusterConfiguration;
	@NonNull
	private ThreddsConfiguration threddsConfiguration;	
	
}
