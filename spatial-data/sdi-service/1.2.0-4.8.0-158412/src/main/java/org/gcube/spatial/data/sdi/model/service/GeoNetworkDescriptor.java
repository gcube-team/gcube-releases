package org.gcube.spatial.data.sdi.model.service;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.spatial.data.sdi.model.credentials.Credentials;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@NoArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GeoNetworkDescriptor extends GeoServiceDescriptor{

	
	

	public GeoNetworkDescriptor(Version version, String baseEndpoint, List<Credentials> accessibleCredentials,
			String contextGroup, String defaultGroup, String sharedGroup, String confidentialGroup, String publicGroup,
			Integer priority) {
		super(version, baseEndpoint, accessibleCredentials);
		this.contextGroup = contextGroup;
		this.defaultGroup = defaultGroup;
		this.sharedGroup = sharedGroup;
		this.confidentialGroup = confidentialGroup;
		this.publicGroup = publicGroup;
		this.priority = priority;
	}
	@NonNull
	private String contextGroup;
	@NonNull
	private String defaultGroup;
	@NonNull
	private String sharedGroup;
	@NonNull
	private String confidentialGroup;
	@NonNull
	private String publicGroup;
	@NonNull
	private Integer priority;
	
	
	
}
