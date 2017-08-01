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
public class GeoServerConfiguration extends GeoService {

	
	
	
	public GeoServerConfiguration(Version version, String baseEndpoint, List<Credentials> accessibleCredentials,
			String confidentialWorkspace, String contextVisibilityWorkspace, String sharedWorkspace,
			String publicWorkspace) {
		super(version, baseEndpoint, accessibleCredentials);
		this.confidentialWorkspace = confidentialWorkspace;
		this.contextVisibilityWorkspace = contextVisibilityWorkspace;
		this.sharedWorkspace = sharedWorkspace;
		this.publicWorkspace = publicWorkspace;
	}
	@NonNull
	private String confidentialWorkspace;
	@NonNull
	private String contextVisibilityWorkspace;
	@NonNull
	private String sharedWorkspace;
	@NonNull
	private String publicWorkspace;
	
	
}
