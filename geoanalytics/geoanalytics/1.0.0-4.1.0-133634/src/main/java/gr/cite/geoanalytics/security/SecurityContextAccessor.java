package gr.cite.geoanalytics.security;

import java.util.List;

import gr.cite.geoanalytics.dataaccess.entities.document.Document;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;

public interface SecurityContextAccessor
{
	public boolean isAnonymous();
	public boolean isRememberMe();
	public boolean isFullyAuthenticated();
	public boolean isAdministrator() throws Exception;
	public boolean isUser() throws Exception;
	public Principal getPrincipal() throws Exception;
	public List<Principal> getRoles() throws Exception;
	public List<String> getLayers() throws Exception;
	
	public boolean canAccessLayer(String layer) throws Exception;
	public boolean canAccessDocument(Document d) throws Exception;
	public boolean canAccessShape(Shape s) throws Exception;
	
	public void updateLayers() throws Exception;
	public List<Principal> getAdministrators() throws Exception;
}
