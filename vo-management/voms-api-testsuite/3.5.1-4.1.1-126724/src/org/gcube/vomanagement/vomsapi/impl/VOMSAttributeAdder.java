package org.gcube.vomanagement.vomsapi.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.gcube.vomanagement.vomsapi.ExtendedVOMSAdmin;
import org.gcube.vomanagement.vomsapi.impl.utils.VOMSServerBean;
import org.gcube.vomanagement.vomsapi.util.CredentialsUtil;
import org.gcube.vomanagement.vomsapi.util.InMemoryVOMSProxyFactory;
import org.gcube.vomanagement.vomsapi.util.VOMSInfo;
import org.gcube.vomanagement.vomsapi.util.VOMSRole;
import org.gridforum.jgss.ExtendedGSSCredential;

public abstract class VOMSAttributeAdder
{
	private Logger logger;
	
	VOMSAPIConfiguration config = null;
	ExtendedVOMSAdmin extendedVOMSAdmin = null;
	List<VOMSServerBean> serverList;
	
	VOMSAttributeAdder (VOMSAPIConfiguration config,ExtendedVOMSAdmin extendedVOMSAdmin)
	{
		this.logger =  Logger.getLogger(this.getClass());
		this.config = config;
		this.extendedVOMSAdmin = extendedVOMSAdmin;
		this.serverList = new ArrayList<VOMSServerBean>();
	}
	
	public void addServer (VOMSServerBean serverBean)
	{
		this.serverList.add(serverBean);
	}
	
	public void setServerList (List<VOMSServerBean> serverList)
	{
		if (serverList != null) this.serverList = serverList;
	}
	
	// add VOMS roles of the given credentials in the given group
	ExtendedGSSCredential addVOMSRoles(ExtendedGSSCredential credentials,String... groupNames) throws VOMSAdminException 
	{
		logger.debug("Generating and adding VO roles");
		// get user DN and CA
		String userDN = CredentialsUtil.getIdentityDN(credentials);
		String userCA = CredentialsUtil.getIssuerDN(credentials);
		logger.debug("User DN "+userDN);
		logger.debug("User CA "+userCA);
		InMemoryVOMSProxyFactory factory = generateInMemoryProxyFactorty ();
		
		if (groupNames != null && groupNames.length > 0) addRoles(factory, userDN, userCA, groupNames, CredentialsUtil.stringCredentials(credentials));
		else
		{
			logger.debug("No role defined: adding only VO info");
			String voName = config.getProperty(VOMSAPIConfigurationProperty.VO_NAME);
			logger.debug("VO name "+voName);
			factory.addVomsFQANInfo(new VOMSInfo(voName));
		}

		// create proxy with roles
		try {
			ExtendedGSSCredential attributedCredentials = factory
					.createInMemoryProxy(credentials);
			logger.debug("Created VOMS proxy "
					+ CredentialsUtil.stringCredentials(attributedCredentials));
			return attributedCredentials;
		} catch (Exception e) {
			logger.error("Cannot create the VOMS proxy for "
					+ CredentialsUtil.stringCredentials(credentials), e);
			throw new VOMSAdminException("Cannot create the VOMS proxy for "
					+ CredentialsUtil.stringCredentials(credentials), e);
		}
	}
	
	private InMemoryVOMSProxyFactory generateInMemoryProxyFactorty () throws VOMSAdminException
	{
		InMemoryVOMSProxyFactory factory;
		// intialize the VOMS proxy factory
		String proxiesDir = config
				.getProperty(VOMSAPIConfigurationProperty.PROXIES_DIR);
		
		try {
			factory = new InMemoryVOMSProxyFactory(proxiesDir);
			
			for (VOMSServerBean server : this.serverList)
			{
				factory.addVomsServer(server.getHostName(), server.getHostDN(), server.getHostPort(), server.getVoName());
			}
			
			logger.debug("Intialized factory for VOMS proxies with directory "
					+ proxiesDir);
		} catch (IOException e) {
			logger.error("Cannot create the factory for VOMS proxies", e);
			throw new VOMSAdminException(
					"Cannot create the factory for VOMS proxies", e);
		}
		
		return factory;
	}
	
	private void addRoles (InMemoryVOMSProxyFactory factory,String userDN, String userCA,String [] groupNames, String credentialsLogString) throws VOMSAdminException 
	{
		for (String groupName : groupNames)
		{
			logger.debug("Adding VOMS roles in group " + groupName + " to "+ credentialsLogString);

			// get roles of the user in VOMS
			String[] roles;
			try {
				roles = extendedVOMSAdmin.listRoles(groupName, userDN, userCA);
			} catch (Exception e) {
				logger.error("Cannot get roles for "
						+ credentialsLogString
						+ " in group " + groupName, e);
				throw new VOMSAdminException("Cannot get roles for "
						+ credentialsLogString
						+ " in group " + groupName, e);
			}
	
			// log roles found
			String strRoles;
			if (roles.length > 0) {
				strRoles = roles.length + " roles found in group " + groupName
						+ " for " + credentialsLogString
						+ ":";
				for (String role : roles) {
					strRoles += "\n\t" + role;
				}
			} else {
				strRoles = "No roles found in group " + groupName + " for "
						+ credentialsLogString;
			}
			logger.debug(strRoles);
			String voName = config
					.getProperty(VOMSAPIConfigurationProperty.VO_NAME);
			for (String role : roles) {
				VOMSRole vomsRole = new VOMSRole(groupName, voName, role);
				factory.addVomsFQANInfo(vomsRole);
				logger
						.debug("Configured factory for VOMS proxies with VOMS role [group="
								+ groupName + ", vo=" + voName + ", role=" + role);
			}

		}
	}
}
