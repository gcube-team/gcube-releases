package gr.cite.commons.util.datarepository.config;

import javax.validation.constraints.NotNull;

import gr.cite.repo.auth.app.config.SamlSecurityConfiguration;

public class DataRepositoryApplicationConfiguration extends SamlSecurityConfiguration {

	@NotNull
	private String publicUrl;
	
	public String getPublicUrl() {
		return publicUrl;
	}
	
	public void setPublicUrl(String publicUrl) {
		this.publicUrl = publicUrl;
	}

}
