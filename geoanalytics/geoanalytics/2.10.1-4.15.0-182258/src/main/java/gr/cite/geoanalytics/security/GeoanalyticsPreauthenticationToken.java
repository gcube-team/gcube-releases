package gr.cite.geoanalytics.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class GeoanalyticsPreauthenticationToken extends PreAuthenticatedAuthenticationToken
{
	private static final long serialVersionUID = -268936064465264831L;
	
	List<String> layers = null;
	List<UUID> layersIds = new ArrayList<UUID>();
	String tenant = null;
	private UUID vreUsrUUID = null;
	private UUID tenantId = null;
	
	public GeoanalyticsPreauthenticationToken(Object aPrincipal, Object aCredentials, List<String> layers,
			List<UUID> layersIds, String tenant, UUID vreUsrUUID, UUID tenantId) {
		super(aPrincipal, aCredentials);
		this.layers = layers;
		this.layersIds = layersIds;
		this.tenant = tenant;
		this.vreUsrUUID = vreUsrUUID;
		this.tenantId = tenantId;
	}

	public GeoanalyticsPreauthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, 
			List<String> layers, List<UUID> layersIds, UUID vreUsrUUID, UUID tenantId) {
        super(principal, credentials, authorities);
        this.layers = layers;
        this.layersIds = layersIds;
        this.vreUsrUUID = vreUsrUUID;
		this.tenantId = tenantId;
    }

	public GeoanalyticsPreauthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, 
			List<String> layers, List<UUID> layersIds) {
        super(principal, credentials, authorities);
        this.layers = layers;
        this.layersIds = layersIds;
    }
	
	public List<String> getLayers()
	{
		return new ArrayList<String>(layers);
	}
	
	public void setLayers(List<String> layers)
	{
		this.layers = layers;
	}

	public List<UUID> getLayersIds() {
		return layersIds;
	}

	public void setLayersIds(List<UUID> layersIds) {
		this.layersIds = layersIds;
	}

	public String getTenant() {
		return tenant;
	}

	public void setTenant(String tenant) {
		this.tenant = tenant;
	}

	public UUID getVreUsrUUID() {
		return vreUsrUUID;
	}

	public void setVreUsrUUID(UUID vreUsrUUID) {
		this.vreUsrUUID = vreUsrUUID;
	}

	public UUID getTenantId() {
		return tenantId;
	}

	public void setTenantId(UUID tenantId) {
		this.tenantId = tenantId;
	}
	
}
