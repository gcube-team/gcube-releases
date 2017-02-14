package gr.cite.geoanalytics.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class GeoanalyticsRememberMeAuthenticationToken extends RememberMeAuthenticationToken
{
	private static final long serialVersionUID = -268936064465264831L;
	
	List<String> layers = null;
	
	public GeoanalyticsRememberMeAuthenticationToken(String key, Object principal, Collection<? extends GrantedAuthority> authorities, 
			List<String> layers) {
        super(key, principal, authorities);
        this.layers = layers;
    }
	
	public List<String> getLayers()
	{
		return new ArrayList<String>(layers);
	}
	
	public void setLayers(List<String> layers)
	{
		this.layers = layers;
	}
	
}
