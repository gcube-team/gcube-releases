package org.gcube.spatial.data.sdi.model.service;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.spatial.data.sdi.model.credentials.Credentials;

import lombok.NoArgsConstructor;
import lombok.NonNull;


@NoArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GeoNetworkConfiguration extends GeoService{

	
	
	public GeoNetworkConfiguration(Version version, String baseEndpoint, List<Credentials> accessibleCredentials,
			String contextGroup, String sharedGroup, String publicGroup) {
		super(version, baseEndpoint, accessibleCredentials);
		this.contextGroup = contextGroup;
		this.sharedGroup = sharedGroup;
		this.publicGroup = publicGroup;
	}
	@NonNull
	private String contextGroup;
	@NonNull
	private String sharedGroup;
	@NonNull
	private String publicGroup;
	
}
