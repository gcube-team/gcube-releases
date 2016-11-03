package gr.cite.geoanalytics.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class GeoanalyticsPreauthenticationToken extends PreAuthenticatedAuthenticationToken
{
	private static final long serialVersionUID = -268936064465264831L;
	
	List<String> layers = null;
	
	public GeoanalyticsPreauthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, 
			List<String> layers) {
        super(principal, credentials, authorities);
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
