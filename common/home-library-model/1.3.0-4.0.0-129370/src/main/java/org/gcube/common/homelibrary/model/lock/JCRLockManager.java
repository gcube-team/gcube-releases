package org.gcube.common.homelibrary.model.lock;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.gcube.common.homelibrary.model.exceptions.ItemNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class JCRLockManager {


	private String urlRepository;
	private Logger logger = LoggerFactory.getLogger(JCRLockManager.class);
	private String login;
	private String sessionId;
	private String credential;


	public JCRLockManager(String login, String sessionId, String repository, String credential){
		this.urlRepository = repository;
		this.credential = credential;
		this.login = login;
		this.sessionId = sessionId;
	}


	/**
	 *  Lock a node by id
	 * @param id
	 * @param uuid
	 * @param login
	 * @throws ItemNotFoundException
	 */
	public void lockItem(String id) throws ItemNotFoundException {
		logger.info("Calling Servlet Lock Node with id " + id + " by " + login);
		GetMethod getMethod = null;
		XStream xstream = new XStream(new DomDriver("UTF-8"));

		HttpClient httpClient = new HttpClient();  

		try {
			getMethod =  new GetMethod(urlRepository + "/rest/LockSession?" + credential + "&login=" + login + "&id=" + id + "&uuid=" + sessionId);
			httpClient.executeMethod(getMethod);
		} catch (Exception e) {
			throw new ItemNotFoundException(e.toString());
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
	}

	/**
	 * Unlock a node by id
	 * @param id
	 * @param uuid
	 * @param login
	 * @throws ItemNotFoundException
	 */
	public void unlockItem(String id) throws ItemNotFoundException {
		logger.info("Calling Servlet Lock Node with id " + id + " by " + login);
		GetMethod getMethod = null;
		XStream xstream = new XStream(new DomDriver("UTF-8"));

		HttpClient httpClient = new HttpClient();  

		try {
			getMethod =  new GetMethod(urlRepository + "/rest/UnlockSession?" + credential + "&login=" + login + "&id=" + id + "&uuid=" + sessionId);
			httpClient.executeMethod(getMethod);
		} catch (Exception e) {
			throw new ItemNotFoundException(e.toString());
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}

	}


	/**
	 * Test for locked status
	 * @param id
	 * @param uuid
	 * @param login
	 * @return true if the node with id is locked; otherwise returns false
	 * @throws ItemNotFoundException
	 */
	public boolean isLocked(String id) throws ItemNotFoundException {
		logger.info("Calling Servlet Lock Node with id " + id + " by " + login);
		GetMethod getMethod = null;
		XStream xstream = new XStream(new DomDriver("UTF-8"));

		HttpClient httpClient = new HttpClient();  

		try {
			getMethod =  new GetMethod(urlRepository + "/rest/IsLocked?" + credential + "&login=" + login + "&id=" + id);
			httpClient.executeMethod(getMethod);
		} catch (Exception e) {
			throw new ItemNotFoundException(e.toString());
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
		return false;

	}

}
