package org.gcube.spatial.data.sdi.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.spatial.data.sdi.model.service.GeoNetworkDescriptor;
import org.gcube.spatial.data.sdi.model.service.GeoServerDescriptor;
import org.gcube.spatial.data.sdi.model.service.ThreddsDescriptor;

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
	private List<GeoNetworkDescriptor> geonetworkConfiguration;
	@NonNull
	private List<GeoServerDescriptor> geoserverClusterConfiguration;
	@NonNull
	private List<ThreddsDescriptor> threddsConfiguration;	
	
}
