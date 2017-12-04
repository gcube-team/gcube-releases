package org.gcube.application.framework.core.util;

import java.io.File;

//import org.gcube.vomanagement.vomsapi.CredentialsManager;
//import org.gcube.vomanagement.vomsapi.VOMSAdmin;
//import org.gcube.vomanagement.vomsapi.impl.VOMSAPIConfiguration;
//import org.gcube.vomanagement.vomsapi.impl.VOMSAPIFactory;
//import org.gridforum.jgss.ExtendedGSSCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Valia Tsagkalidou (NKUA)
 *
 */
public class UserCredential {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(UserCredential.class);
	
	/**
	 * Retrieves credential for users
	 * @param username the user name for which it will retrieve credential
	 * @param DLname DLname
	 * @return the GSS Credential
	 */
	
	//UNCOMMENT ABOVE CODE WHEN THE SECURITY BECOMES AVAILABLE.
/*
	public static ExtendedGSSCredential getCredential(String username, String DLname)
	{	
		CredentialsManager man = null;
		try {
			String sharedDir = Settings.getInstance().getProperty("sharedDir");
			logger.info("file " + sharedDir + "/vomsAPI.properties exists: "+ new File(sharedDir + "/vomsAPI.properties").exists());
		//	man = new CredentialsManagerImpl(sharedDir + "/vomsAPI.properties");
			
			VOMSAPIFactory factory = new VOMSAPIFactory(new VOMSAPIConfiguration(new File(sharedDir + "/vomsAPI.properties")));
			VOMSAdmin vomsAdm = factory.getVOMSAdmin();
			man = factory.getCredentialsManager();
		} catch (Exception e1) {
			logger.error("Exception:", e1);
		}
		ExtendedGSSCredential cred = null;
		try {
			//TODO: put a real password there...
			cred = man.getAttributedCredentials(username, "", DLname);
		} catch (Exception e) {
			logger.error("Exception:", e);
		}
		return cred;
	}
	
	public static ExtendedGSSCredential getPlainCredential(String username, String password)
	{	
		CredentialsManager man = null;
		try {
			String sharedDir = Settings.getInstance().getProperty("sharedDir");
			logger.info("file " + sharedDir + "/vomsAPI.properties exists: "+ new File(sharedDir + "/vomsAPI.properties").exists());
		//	man = new CredentialsManagerImpl(sharedDir + "/vomsAPI.properties");
			
			VOMSAPIFactory factory = new VOMSAPIFactory(new VOMSAPIConfiguration(new File(sharedDir + "/vomsAPI.properties")));
			VOMSAdmin vomsAdm = factory.getVOMSAdmin();
			man = factory.getCredentialsManager();
		} catch (Exception e1) {
			logger.error("Exception:", e1);
		}
		ExtendedGSSCredential cred = null;
		try {
			//TODO: put a real password there...
			cred = man.getPlainCredentials(username, password);
		} catch (Exception e) {
			logger.error("Exception:", e);
		}
		return cred;
	}
	
	*/
	

}
