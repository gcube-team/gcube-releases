package gr.cite.geoanalytics.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class GeoanalyticsAuthenticationToken extends UsernamePasswordAuthenticationToken {
	private static final long serialVersionUID = -268936064465264831L;
	
	List<String> layers = null;
	List<UUID> layersIds = new ArrayList<UUID>();

	public GeoanalyticsAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, 
			List<String> layers, List<UUID> layersIds) {
        super(principal, credentials, authorities);
        this.layers = layers;
        this.layersIds = layersIds;
    }
	
	public List<String> getLayers() {
		return new ArrayList<String>(layers);
	}
	
	public void setLayers(List<String> layers) {
		this.layers = layers;
	}
	
	public List<UUID> getLayersIds() {
		return layersIds;
	}

	public void setLayersIds(List<UUID> layersIds) {
		this.layersIds = layersIds;
	}
	
}
