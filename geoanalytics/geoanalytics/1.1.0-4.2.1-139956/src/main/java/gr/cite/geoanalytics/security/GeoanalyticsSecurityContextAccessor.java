package gr.cite.geoanalytics.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.inject.Inject;

import gr.cite.geoanalytics.manager.PrincipalManager;
import gr.cite.geoanalytics.manager.TenantManager;
import gr.cite.geoanalytics.manager.UserManager;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.geoanalytics.dataaccess.entities.document.Document;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDao;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeDocument;
import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeDocumentDao;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.dao.TaxonomyTermDao;

import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("securityContextAccessor")
public class GeoanalyticsSecurityContextAccessor implements SecurityContextAccessor
{
	private AuthenticationTrustResolver authenticationTrustResolver = null;
	private UserManager userManager = null;
	private TenantManager tenantManager = null;
	private PrincipalDao principalDao = null;
	private PrincipalManager principalManager = null;
	
	private SecurityManager securityManager;

	private ShapeDocumentDao shapeDocumentDao;
	
	@Inject
	public GeoanalyticsSecurityContextAccessor(AuthenticationTrustResolver authenticationTrustResolver, 
			UserManager userManager, TenantManager tenantManager) {
		this.authenticationTrustResolver = authenticationTrustResolver;
		this.userManager = userManager;
		this.tenantManager = tenantManager;
	}

	@Inject
	public void setSecurityManager(SecurityManager securityManager) {
		this.securityManager = securityManager;
	}
	
	@Inject
	public void setShapeDocumentDao(ShapeDocumentDao shapeDocumentDao) {
		this.shapeDocumentDao = shapeDocumentDao;
	}
	
	@Inject
	public void setPrincipalDao(PrincipalDao principalDao) {
		this.principalDao = principalDao;
	}
	
	@Override
	public boolean isAnonymous() {
		return authenticationTrustResolver.isAnonymous(
				SecurityContextHolder.getContext().getAuthentication());
	}

	@Override
	public boolean isRememberMe() {
		return authenticationTrustResolver.isRememberMe(
				SecurityContextHolder.getContext().getAuthentication());
	}

	@Override
	public boolean isFullyAuthenticated() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return !authenticationTrustResolver.isAnonymous(auth) &&
				!authenticationTrustResolver.isAnonymous(auth);
	}
	
	@Override
	public boolean isAdministrator() throws Exception {
		if(getRoles().stream().map(p -> p.getName()).collect(Collectors.toList()).contains(SecurityManager.GROUP_APPADMIN)) return true;
		return false;
	}
	
	@Override
	public boolean isUser() throws Exception {
		List<String> roles = getRoles().stream().map(p -> p.getName()).collect(Collectors.toList());
		if(roles.contains(SecurityManager.GROUP_USER) && !roles.contains(SecurityManager.GROUP_APPADMIN)) return true;
		return false;
	}

	@Override
	@Transactional(readOnly = true)
	public Principal getPrincipal() throws Exception {
		if(isAnonymous())
			return null;
		org.springframework.security.core.userdetails.User authUser = 
				(org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Principal principal = this.principalDao.findActivePrincipalByName(authUser.getUsername());
		if(principal == null) throw new Exception("User " + authUser.getUsername() + " not found");
		return principal;
	}

	@Override
	public List<Principal> getRoles() throws Exception {
		List<Principal> principals = new ArrayList<Principal>();
		if(isAnonymous())
			return Collections.singletonList(securityManager.findPrincipalByName(SecurityManager.GROUP_GUEST, true));

		return securityManager.findRolesByPrincipal(getPrincipal());
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Principal> getAdministrators() throws Exception {
		//return userManager.findByRole("ROLE_admin");//TODO
		List<Principal> principals = new ArrayList<>();
		principals.add(principalManager.getSystemPrincipal());
		return principals;
	}
	
	@Override
	public List<String> getLayers() throws Exception
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth instanceof GeoanalyticsAuthenticationToken)
			return new ArrayList<String>(((GeoanalyticsAuthenticationToken)auth).getLayers());
		else if(auth instanceof GeoanalyticsRememberMeAuthenticationToken)
		{
			return new ArrayList<String>(((GeoanalyticsRememberMeAuthenticationToken)auth).getLayers());
		}
		else if(auth instanceof GeoanalyticsPreauthenticationToken) {
			return new ArrayList<String>(((GeoanalyticsPreauthenticationToken)auth).getLayers());
		}
		throw new Exception("Invalid authentication token");
	}
	
	@Override
	public boolean canAccessLayer(String layer) throws Exception
	{
		return getLayers().contains(layer);
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean canAccessDocument(Document document) throws Exception {
		if(isAdministrator() && isFullyAuthenticated()){
			return true;
		}
		
		Principal principal = this.getPrincipal();
		List<ShapeDocument> shapeDocs = shapeDocumentDao.findByDocument(document);
		if(shapeDocs == null || shapeDocs.isEmpty()) {
			if(!principal.getId().equals(document.getCreator().getId())){
				return false;
			}
		}/*else
		{
			if(!customerManager.isActiveForAll(principal.getCustomer()))
			{
				List<Shape> grantedShapes = customerManager.getGrantedShapes(principal.getCustomer());
				List<ShapeDocument> filteredDocs = new ArrayList<ShapeDocument>();
				for(ShapeDocument sd : shapeDocs)
				{
					for(Shape s : grantedShapes)
					{
						if(sd.getTaxonomyTermShape().getShape().getId().equals(s.getId()))
							return true;
					}
				}
				return false;
			}
			return true;
		}*/
		return true;
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean canAccessShape(Shape s) throws Exception
	{
		if(isAdministrator() && isFullyAuthenticated()){
			return true;
		}
		
		Principal principal = this.getPrincipal();
		
		/*if(!customerManager.isActiveForAll(principal.getCustomer())) {
			List<Shape> grantedShapes = customerManager.getGrantedShapes(principal.getCustomer());
			
			for(Shape gs : grantedShapes) {
				if(gs.getId().equals(s.getId()))
					return true;
			}
			return false;
		}*/
		return true;	
	}
	
	@Override
	public void updateLayers() throws Exception
	{
		//List<String> layers = userManager.getAccessibleLayers(getPrincipal());//TODO
		List<String> layers = new ArrayList<>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth instanceof GeoanalyticsAuthenticationToken)
			((GeoanalyticsAuthenticationToken)auth).setLayers(layers);
		else if(auth instanceof GeoanalyticsRememberMeAuthenticationToken)
			((GeoanalyticsRememberMeAuthenticationToken)auth).setLayers(layers);
		else if(auth instanceof PreAuthenticatedAuthenticationToken)
			((GeoanalyticsPreauthenticationToken)auth).setLayers(layers);
	}

}
