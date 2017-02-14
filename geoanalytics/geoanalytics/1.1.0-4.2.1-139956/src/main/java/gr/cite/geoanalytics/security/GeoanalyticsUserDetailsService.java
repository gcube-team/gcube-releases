package gr.cite.geoanalytics.security;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import gr.cite.geoanalytics.manager.admin.AdministrationManager;
import gr.cite.gaap.utilities.HtmlUtils;
import gr.cite.geoanalytics.dataaccess.dao.UUIDGenerator;
import gr.cite.geoanalytics.dataaccess.entities.ActiveStatus;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalClass;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalData;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDao;
import gr.cite.geoanalytics.dataaccess.entities.user.UserRights;
import gr.cite.geoanalytics.dataaccess.entities.user.dao.UserDaoOld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

@Service("userDetailsService")
@Transactional(readOnly = true)
public class GeoanalyticsUserDetailsService implements UserDetailsService {
	
	private static final Logger log = LoggerFactory.getLogger(GeoanalyticsUserDetailsService.class);
	
	private AdministrationManager administrationManager;
	private SecurityManager securityManager;
	
	private UserDaoOld userDaoOld;
	private PrincipalDao principalDao;
	
	private JAXBContext ctx = null;
	
	public GeoanalyticsUserDetailsService() throws Exception {
		ctx = JAXBContext.newInstance(UserRights.class);
	}
	
	@Inject
	public void setAdministrationManager(AdministrationManager administrationManager) {
		this.administrationManager = administrationManager;
	}
	
	@Inject
	public void setSecurityManager(SecurityManager securityManager) {
		this.securityManager = securityManager;
	}
	
	@Inject
	public void setUserDao(UserDaoOld userDaoOld) {
		this.userDaoOld = userDaoOld;
	}
	
	@Inject
	public void setPrincipalDao(PrincipalDao principalDao) {
		this.principalDao = principalDao;
	}
	
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

		try {
			Principal principal = principalDao.findPrincipalByNameAndActivityStatus(userName, null);
			if (principal == null || principal.getPrincipalData() == null || principal.getPrincipalData().getCredential() == null){
				principal = principalDao.read(UUIDGenerator.systemUserUUID());
			}
//				throw new UsernameNotFoundException("User " + userName + " not found");

			Unmarshaller um = ctx.createUnmarshaller();
			
			List<GrantedAuthority> grantedAuthorities = securityManager.findRolesByPrincipal(principal).stream().
					map(p -> new SimpleGrantedAuthority("ROLE_" + p.getName())).
					collect(Collectors.toList());
			

			return new org.springframework.security.core.userdetails.User(principal.getName(), principal.getPrincipalData().getCredential(),
					principal.getIsActive() == ActiveStatus.ACTIVE, new Date().getTime() < principal.getPrincipalData().getExpirationDate().getTime(), true, principal.getIsActive() != ActiveStatus.LOCKED,
					grantedAuthorities);

		} catch (UsernameNotFoundException ue) {
			log.error("Exception while authenticating user " + userName, ue);
			System.out.println("Exception while authenticating user " + userName);
			throw ue;
		} catch (Exception e) {
			log.error("Exception while authenticating user " + userName, e);
			System.out.println("Exception while authenticating user " + userName);
		}
		return null;
    }
}