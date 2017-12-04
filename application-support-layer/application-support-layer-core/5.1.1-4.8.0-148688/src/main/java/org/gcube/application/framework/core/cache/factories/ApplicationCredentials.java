package org.gcube.application.framework.core.cache.factories;

import java.util.HashMap;

import org.gcube.application.framework.core.security.PortalSecurityManager;
import org.gcube.application.framework.core.security.VOMSAdminManager;
import org.gcube.application.framework.core.util.UserCredential;
//import org.gcube.common.core.scope.GCUBEScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.gcube.vomanagement.vomsapi.VOMSAdmin;
//import org.gcube.vomanagement.vomsapi.impl.VOMSAPIFactory;
//import org.gcube.vomanagement.vomsapi.util.VOMSAttributesReader;
//import org.glite.wsdl.services.org_glite_security_voms.User;
//import org.gridforum.jgss.ExtendedGSSCredential;

/**
 *
 * This class is used for retrieving and updating the portal credential that is needed by the caches in order to retrieve information form gCube services
 * IT'S FOR THE MOMENT DISABLED... IT WILL INTEGRATE THE NEW SECURITY MODEL, WHEN IT'S COMPLETED. 
 * @author Valia Tsagkalidou (NKUA)
 *
 */
public class ApplicationCredentials {
	
	
//	private static ApplicationCredentials applCredentials = new ApplicationCredentials();
//	private HashMap<String, ExtendedGSSCredential> creds;
//	protected static String userDN = "/O=Grid/OU=GlobusTest/OU=simpleCA-gauss.eng.it/OU=eng.it/CN=";
//	protected static String userCA =  "/O=Grid/OU=GlobusTest/OU=simpleCA-gauss.eng.it/CN=Globus Simple CA";
//	
//	/** The logger. */
//	private static final Logger logger = LoggerFactory.getLogger(ApplicationCredentials.class);
//	
//	/**
//	 * The basic constructor
//	 */
//	protected ApplicationCredentials()
//	{
//		creds = new HashMap<String, ExtendedGSSCredential>();
//	}
//	
//	/**
//	 * @return  the sigleton of ApplicationCredentials
//	 */
//	public static ApplicationCredentials getInstance()
//	{
//		return applCredentials;
//	}
//
//	/**
//	 * @param VREname the of the VRE for which you want to get the "portal" credential
//	 * @return the grid credential
//	 */
//	public ExtendedGSSCredential getCredential(String VREname)
//	{
//		PortalSecurityManager secMan = new PortalSecurityManager(VREname);//GCUBEScope.getScope(VREname));
//		if(!secMan.isSecurityEnabled())
//			return null;
//		ExtendedGSSCredential cred = creds.get(VREname);
//		if(cred == null)
//		{
//			// If the credential is not available, it retrieves it from myProxy
//			cred = UserCredential.getCredential("application", VREname);
//			if(cred == null)
//			{
//				//user "application" does not exist on this VRE, so we add him and try to get credential again
//				VOMSAdmin vomsA;
//				VOMSAPIFactory factory;
//				try {
//					factory = VOMSAdminManager.getVOMSFactory();
//					vomsA = factory.getVOMSAdmin();
//				//	String[] roles = vomsA.listRoles();
//				//	vomsA.createUser("application", userDN+"application", userCA, "application@gcube.org");
//				//	vomsA.addMember(VREname, userDN+"application", userCA);
//				//	vomsA.assignRole(VREname, roles[0], userDN+"application", userCA);
//				//	vomsA =  new VOMSAdminImpl();
//					String[] roles = vomsA.listRoles();
//					User myUser = new User();
//					myUser.setCN("application");
//					myUser.setDN(userDN+"application");
//					myUser.setCA(userCA);
//					myUser.setMail("application@gcube.org");
//					//vomsA.getExtendedPortType().createUser("application", userDN+"application", userCA, "application@gcube.org");
//					vomsA.createUser(myUser);
//					//vomsA.getExtendedPortType().addOnlineCAMember(VREname, userDN+"application");
//					//vomsA.getExtendedPortType().assignOnlineCARole(VREname, roles[0], userDN+"application");
//					vomsA.addMember(VREname, userDN + "application", userCA);
//					vomsA.assignRole(VREname, roles[0], userDN + "application", userCA);
//				} 
//				catch (Exception e) {
//					vomsA = null;
//					logger.error("", e);
//				}
//				cred = UserCredential.getCredential("application", VREname);
//			}
//			creds.put(VREname, cred);
//		}
//		else
//		{
//			// credential already available
//			VOMSAttributesReader vomsReader = null;
//			try {
//				vomsReader = new VOMSAttributesReader(cred);
//				//Check if it's gonna expire in the next minute, and refresh it
//				if(vomsReader.getRefreshPeriod() < 60000)
//				{
//					cred = UserCredential.getCredential("application", VREname);
//					creds.put(VREname, cred);
//				}
//			} catch (Exception e1) {
//				logger.error("", e1);
//			}
//		}
//		return cred;
//	}
	
	
}
