package gr.cite.repo.auth.app.config;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Security {
	
	@NotNull
	@NotEmpty
	@JsonProperty
	private String unauthorizedLocation;
	
	@NotNull
	@JsonProperty
	private Boolean includeTarget;
	
	
	@NotNull
	@NotEmpty
	@JsonProperty
	private String spHost;
	
	@NotNull
	@NotEmpty
	@JsonProperty
	private String idpMetadataLocation;
	
	@NotNull
	@NotEmpty
	@JsonProperty
	private String certificateFilename;
	
	@NotNull
	@NotEmpty
	@JsonProperty
	private String privateKeyFilename;
	
	@NotNull
	@NotEmpty
	@JsonProperty
	private List<String> protectedUrls;
	
	@NotNull
	@JsonProperty
	private Boolean invalidateLocalSessionOnSamlError;
	
	@NotNull
	@JsonProperty
	private Boolean tryRenewSessionOnLogout;
	
	@NotNull
	@JsonProperty
	private Boolean bulkLogout;

	public String getUnauthorizedLocation() {
		return unauthorizedLocation;
	}

	public void setUnauthorizedLocation(String unauthorizedLocation) {
		this.unauthorizedLocation = unauthorizedLocation;
	}

	public Boolean getIncludeTarget() {
		return includeTarget;
	}

	public void setIncludeTarget(Boolean includeTarget) {
		this.includeTarget = includeTarget;
	}

	public String getSpHost() {
		return spHost;
	}

	public void setSpHost(String spHost) {
		this.spHost = spHost;
	}

	public String getIdpMetadataLocation() {
		return idpMetadataLocation;
	}

	public void setIdpMetadataLocation(String idpMetadataLocation) {
		this.idpMetadataLocation = idpMetadataLocation;
	}

	public String getCertificateFilename() {
		return certificateFilename;
	}

	public void setCertificateFilename(String certificateFilename) {
		this.certificateFilename = certificateFilename;
	}

	public String getPrivateKeyFilename() {
		return privateKeyFilename;
	}

	public void setPrivateKeyFilename(String privateKeyFilename) {
		this.privateKeyFilename = privateKeyFilename;
	}

	public List<String> getProtectedUrls() {
		return protectedUrls;
	}

	public void setProtectedUrls(List<String> protectedUrls) {
		this.protectedUrls = protectedUrls;
	}

	public Boolean getInvalidateLocalSessionOnSamlError() {
		return invalidateLocalSessionOnSamlError;
	}

	public void setInvalidateLocalSessionOnSamlError(
			Boolean invalidateLocalSessionOnSamlError) {
		this.invalidateLocalSessionOnSamlError = invalidateLocalSessionOnSamlError;
	}

	public Boolean getTryRenewSessionOnLogout() {
		return tryRenewSessionOnLogout;
	}

	public void setTryRenewSessionOnLogout(Boolean tryRenewSessionOnLogout) {
		this.tryRenewSessionOnLogout = tryRenewSessionOnLogout;
	}

	public Boolean getBulkLogout() {
		return bulkLogout;
	}

	public void setBulkLogout(Boolean bulkLogout) {
		this.bulkLogout = bulkLogout;
	}
	
	
	
}