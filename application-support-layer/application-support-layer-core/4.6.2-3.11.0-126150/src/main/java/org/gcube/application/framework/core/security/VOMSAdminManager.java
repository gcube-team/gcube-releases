package org.gcube.application.framework.core.security;


import java.io.File;


import org.gcube.application.framework.core.util.Settings;
//import org.gcube.vomanagement.vomsapi.ExtendedVOMSAdmin;
//import org.gcube.vomanagement.vomsapi.VOMSAdmin;
//import org.gcube.vomanagement.vomsapi.impl.VOMSAPIConfiguration;
//import org.gcube.vomanagement.vomsapi.impl.VOMSAPIConfigurationException;
//import org.gcube.vomanagement.vomsapi.impl.VOMSAPIFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Valia Tsagkalidou (NKUA)
 *
 */
public class VOMSAdminManager {
	

	private static final Logger logger = LoggerFactory.getLogger(VOMSAdminManager.class);


	/*
	protected static VOMSAPIFactory factory = null;
	protected static VOMSAdmin vomsAdmin = null;
	protected static ExtendedVOMSAdmin extendedVomsAdmin = null;
	

	public static VOMSAPIFactory getVOMSFactory()
	{
		if(factory == null)
		{
			try {
				logger.info("The location is: " + Settings.getInstance().getProperty("sharedDir") + File.separator + "vomsAPI.properties");
				// must be uncomented!!!!!
				//vomsAdmin = new VOMSAdminImpl(Settings.getInstance().getProperty("sharedDir")+ File.separator +  "vomsAPI.properties");
				factory = new VOMSAPIFactory(new VOMSAPIConfiguration(new File(Settings.getInstance().getProperty("sharedDir")+ File.separator +  "vomsAPI.properties")));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception:", e);
			}
		}
		return factory; 
	}
	
	
	public static VOMSAdmin getVOMSAdmin() {
		if (vomsAdmin == null) {
			if (factory == null) {
				try {
					logger.info("I am in ASL and I am creating vomsAdmin - location should be printed");
					logger.info("The location is: " + Settings.getInstance().getProperty("sharedDir") + File.separator + "vomsAPI.properties");
					// must be uncomented!!!!!
					//vomsAdmin = new VOMSAdminImpl(Settings.getInstance().getProperty("sharedDir")+ File.separator +  "vomsAPI.properties");
					factory = new VOMSAPIFactory(new VOMSAPIConfiguration(new File(Settings.getInstance().getProperty("sharedDir")+ File.separator +  "vomsAPI.properties")));
	
					vomsAdmin = factory.getVOMSAdmin();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception:", e);
				}
			}
			else {
				try {
					vomsAdmin = factory.getVOMSAdmin();
				} catch (VOMSAPIConfigurationException e) {
					// TODO Auto-generated catch block
					logger.error("Exception:", e);
				} catch (ServiceException e) {
					// TODO Auto-generated catch block
					logger.error("Exception:", e);
				}
			}
		}
		return vomsAdmin; 
	}
	
	
	public static ExtendedVOMSAdmin getExtendedVomsAdmin() {
		if (extendedVomsAdmin == null) {
			if (factory == null) {
				try {
					logger.info("I am in ASL and I am creating vomsAdmin - location should be printed");
					logger.info("The location is: " + Settings.getInstance().getProperty("sharedDir") + File.separator + "vomsAPI.properties");
					// must be uncomented!!!!!
					//vomsAdmin = new VOMSAdminImpl(Settings.getInstance().getProperty("sharedDir")+ File.separator +  "vomsAPI.properties");
					factory = new VOMSAPIFactory(new VOMSAPIConfiguration(new File(Settings.getInstance().getProperty("sharedDir")+ File.separator +  "vomsAPI.properties")));
	
					extendedVomsAdmin = factory.getExtendedVOMSAdmin();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception:", e);
				}
			}
			else {
				try {
					extendedVomsAdmin = factory.getExtendedVOMSAdmin();
				} catch (VOMSAPIConfigurationException e) {
					// TODO Auto-generated catch block
					logger.error("Exception:", e);
				} catch (ServiceException e) {
					// TODO Auto-generated catch block
					logger.error("Exception:", e);
				}
			}
		}
		return extendedVomsAdmin; 
	}
	*/
	
}
