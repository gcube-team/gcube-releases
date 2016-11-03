package org.gcube.common.homelibrary.jcr.workspace.lock;

import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.homelibrary.jcr.repository.ServletName;
import org.gcube.common.homelibrary.jcr.workspace.util.TokenUtility;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class JCRLockManager {

	private Logger logger = LoggerFactory.getLogger(JCRLockManager.class);
	private String login;
	private String sessionId;
	private Map<String, Endpoint> servlets;


	public JCRLockManager(String login, String sessionId){
		this.servlets = JCRRepository.servlets;
		this.login = login;
		this.sessionId = sessionId;
	}

	/**
	 *  Lock a node by id
	 * @param id
	 * @param uuid
	 * @param login
	 * @return 
	 * @throws ItemNotFoundException
	 */
	public boolean lockItem(String id) throws InternalErrorException {
		logger.info("Calling Servlet Lock Node with id " + id + " by " + login);
		
		GetMethod getMethod = null;
		boolean locked = false;
		
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		HttpClient httpClient = new HttpClient();  

		try {
			getMethod =  new GetMethod(servlets.get(ServletName.LOCK_SESSION).uri().toString() + "?login=" + login + "&id=" + id + "&uuid=" + sessionId);
			TokenUtility.setHeader(getMethod);
			int response = httpClient.executeMethod(getMethod);
			
			// Check response code
			if (response != HttpStatus.SC_OK)
				throw new HttpException("Received error status " + response);
			
			locked = (boolean) xstream.fromXML(getMethod.getResponseBodyAsStream());
			
		} catch (Exception e) {
			throw new InternalErrorException(e.toString());
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
		return locked;
	}

	/**
	 * Unlock a node by id
	 * @param id
	 * @param uuid
	 * @param login
	 * @throws ItemNotFoundException
	 */
	public void unlockItem(String id) throws InternalErrorException {
		logger.info("Calling Servlet UnLock Node with id " + id + " by " + login);
		
		GetMethod getMethod = null;
//		boolean unLocked = false;
		
//		XStream xstream = new XStream(new DomDriver("UTF-8"));
		HttpClient httpClient = new HttpClient();  

		try {
			getMethod =  new GetMethod(servlets.get(ServletName.UNLOCK_SESSION).uri().toString() + "?login=" + login + "&id=" + id + "&uuid=" + sessionId);
			TokenUtility.setHeader(getMethod);
			int response = httpClient.executeMethod(getMethod);
			
			// Check response code
			if (response != HttpStatus.SC_OK)
				throw new HttpException("Received error status " + response);
			
//			unLocked = (boolean) xstream.fromXML(getMethod.getResponseBodyAsStream());
			
		} catch (Exception e) {
			throw new InternalErrorException(e);
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
//		return unLocked;

	}

//	90:E6:BA:C9:F3:8E
//	146.48.87.214
//	255.255.248.0
//	146.48.80.1
//	146.48.80.3, 146.48.80.4, 146.48.127.2
//	isti.cnr.it, research-infrastructures.eu, d4science.org
	/**
	 * Test for locked status
	 * @param id
	 * @param uuid
	 * @param login
	 * @return true if the node with id is locked; otherwise returns false
	 * @throws ItemNotFoundException
	 */
	public boolean isLocked(String id) throws InternalErrorException {
		logger.info("Calling Servlet isLock Node with id " + id + " by " + login);
		GetMethod getMethod = null;
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		HttpClient httpClient = new HttpClient();  
		boolean isLocked = false;

		try {
			
			getMethod =  new GetMethod(servlets.get(ServletName.IS_LOCKED).uri().toString() + "?login=" + login + "&id=" + id);
			TokenUtility.setHeader(getMethod);
			int response = httpClient.executeMethod(getMethod);
			
			// Check response code
			if (response != HttpStatus.SC_OK)
				throw new HttpException("Received error status " + response);
			
			isLocked = (boolean) xstream.fromXML(getMethod.getResponseBodyAsStream());
			
		} catch (Exception e) {
			throw new InternalErrorException(e.toString());
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
		return isLocked;

	}

}
