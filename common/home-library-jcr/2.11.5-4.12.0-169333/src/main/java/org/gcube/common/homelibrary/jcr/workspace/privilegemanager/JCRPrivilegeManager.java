package org.gcube.common.homelibrary.jcr.workspace.privilegemanager;

import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.privilegemanager.PrivilegeManager;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class JCRPrivilegeManager implements PrivilegeManager{

	private Logger logger = LoggerFactory.getLogger(JCRPrivilegeManager.class);

	public static Map<String, Endpoint> servlets;

	public JCRPrivilegeManager(){
		super();
		servlets = JCRRepository.servlets;
	}
	
	@Override
	public void createCostumePrivilege(String name, String[] privileges) throws InternalErrorException {

		GetMethod getMethod = null;
		try {

			HttpClient httpClient = new HttpClient();      		

			StringBuilder aggregateName = null;
			if(privileges!=null){
				aggregateName = new StringBuilder();
				for (String privilege: privileges){	
					aggregateName.append("&privilege=" + privilege);
				}
			}

			try{
				String requestUrl = servlets.get("") + "/CreateCostumePrivilegeServlet?"+ "&name=" + name +  aggregateName;
				logger.debug(requestUrl);
				getMethod =  new GetMethod(requestUrl);
				httpClient.executeMethod(getMethod);
				logger.debug("Response " + getMethod.getResponseBodyAsString());

			}catch (Exception e) {
				logger.error("Error executing Costume Privilege", e);
			}

			XStream xstream = new XStream();
			Boolean modified = true;

			try{
				modified = (Boolean) xstream.fromXML(getMethod.getResponseBodyAsString());
			}catch (Exception e) {
				logger.error("Error in create Costume Privilege", e);
			}

			if (modified)
				logger.debug(name + " Privilege created");
			else
				logger.debug(name + " Privilege has not been created");

		} catch (Exception e) {
			logger.error("Error deleting Permissions in AccessManager", e);
			throw new InternalErrorException(e);
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
	}


}
