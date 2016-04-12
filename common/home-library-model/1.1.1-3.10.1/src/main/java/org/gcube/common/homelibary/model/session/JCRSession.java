package org.gcube.common.homelibary.model.session;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.Validate;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.SearchItemDelegate;
import org.gcube.common.homelibary.model.items.accounting.AccountingDelegate;
import org.gcube.common.homelibrary.model.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.model.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.homelibrary.model.lock.JCRLockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class JCRSession {
	
	private Logger logger = LoggerFactory.getLogger(JCRSession.class);
	public String urlRepository;
	public String credential;
	
	private String login;
	private String sessionId;
	private JCRLockManager lockManager;


	public JCRSession(String login, String repository, String credential){
		this.login = login;
		this.urlRepository = repository;
		this.credential = credential;
		sessionId = getSession();
	}

	public JCRSession( String repository, String credential){
		this.login = null;
		this.urlRepository = repository;
		this.credential = credential;
		sessionId = getSession();
	}

	/**
	 * Get a new session
	 * @param login
	 * @return
	 */
	private String getSession() {
		logger.info("Calling servlet getSession by " + login);
		String uuid = null;
		GetMethod getMethod = null;
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		HttpClient httpClient = new HttpClient(); 

		try {   
			getMethod =  new GetMethod(urlRepository + "/CreateSession?" + credential + "&login=" + login);
			httpClient.executeMethod(getMethod);	
			uuid = (String) xstream.fromXML(getMethod.getResponseBodyAsStream());
		} catch (Exception e) {
			e.getStackTrace();
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
		return uuid;
	}

	/**
	 * Release session
	 */
	public void releaseSession() {
		logger.info("Calling servlet releaseSession by " + login);

		GetMethod getMethod = null;
		HttpClient httpClient = new HttpClient(); 

		try {   
			getMethod =  new GetMethod(urlRepository + "/ReleaseSession?" + credential + "&login=" + login + "&uuid="+ sessionId);
			httpClient.executeMethod(getMethod);	
			//		xstream.fromXML(getMethod.getResponseBodyAsStream());
		} catch (Exception e) {
			e.getStackTrace();
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
	}

//	/**
//	 * Release all active sessions
//	 */
//	public void releaseAllSessions() {
//		logger.info("Calling servlet releaseAllSessions by " + login);
//
//		GetMethod getMethod = null;
//		HttpClient httpClient = new HttpClient(); 
//
//		try {   
//			getMethod =  new GetMethod(JCRRepository.url + "/ReleaseAllSessions?" + credential + "&login=" + login);
//			httpClient.executeMethod(getMethod);	
//			//		xstream.fromXML(getMethod.getResponseBodyAsStream());
//		} catch (Exception e) {
//			e.getStackTrace();
//		} finally {
//			if(getMethod != null)
//				getMethod.releaseConnection();
//		}
//	}

	/**
	 * Get children by id using a GET servlet
	 * @param user
	 * @return
	 * @throws InternalErrorException
	 */
	@SuppressWarnings("unchecked")
	public List<ItemDelegate> getChildrenById(String id, String login) {
		logger.info("Calling servlet getChildrenById " + id + " by " + login);
		List<ItemDelegate> items = null;
		GetMethod getMethod = null;
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		HttpClient httpClient = new HttpClient(); 

		try {   
			getMethod =  new GetMethod(urlRepository + "/get/GetChildrenById?" + credential + "&id=" + id + "&login=" + login + "&uuid="+sessionId);
			httpClient.executeMethod(getMethod);	
			items= (List<ItemDelegate>) xstream.fromXML(getMethod.getResponseBodyAsStream());
		} catch (Exception e) {
			e.getStackTrace();
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
		return items;
	}

	/**
	 * Retrieve an ItemDelegate object by path using a GET servlet
	 * @param user
	 * @param path
	 * @return an ItemDelegate
	 * @throws ItemNotFoundException 
	 */
	public ItemDelegate getItemByPath(String path, String login) throws ItemNotFoundException {
		logger.info("Calling Servlet GetItemByPath " + path + " by " + login);
		ItemDelegate item = null;
		GetMethod getMethod = null;
		XStream xstream = new XStream(new DomDriver("UTF-8"));

		HttpClient httpClient = new HttpClient();  

		try {
			getMethod =  new GetMethod(urlRepository + "/get/GetItemByPath?" + credential + "&path=" + URLEncoder.encode(path, "UTF-8")+ "&login=" + login + "&uuid="+sessionId);
			httpClient.executeMethod(getMethod);
			item = (ItemDelegate) xstream.fromXML(getMethod.getResponseBodyAsStream());
			//		item.getId();
		} catch (Exception e) {
			throw new ItemNotFoundException(e.toString());
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
		return item;
	}


	/**
	 * Retrieve and ItemDelegate by id using a GET servlet
	 * @param user
	 * @param id
	 * @return
	 */
	public ItemDelegate getItemById(String id) throws ItemNotFoundException {
		//		logger.info("Servlet getItemById " + id);
		ItemDelegate item = null;
		GetMethod getMethod = null;
		HttpClient httpClient = new HttpClient();   
		XStream xstream = new XStream(new DomDriver("UTF-8"));

		try {
			getMethod =  new GetMethod(urlRepository + "/get/GetItemById?" + credential + "&id=" + id );
			httpClient.executeMethod(getMethod);
			item = (ItemDelegate) xstream.fromXML(getMethod.getResponseBodyAsStream());

		} catch (Exception e) {
			throw new ItemNotFoundException(e.toString());
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
		return item;
	}

	/**
	 * Retrieve and ItemDelegate by id using a GET servlet
	 * @param user
	 * @param id
	 * @return
	 */
	public ItemDelegate getParentById(String id) {

		ItemDelegate item = null;
		GetMethod getMethod = null;
		HttpClient httpClient = new HttpClient();   
		XStream xstream = new XStream(new DomDriver("UTF-8"));

		try {

			getMethod =  new GetMethod(urlRepository + "/get/GetParentById?" + credential + "&id=" + id + "&uuid="+sessionId);
			httpClient.executeMethod(getMethod);
			item = (ItemDelegate) xstream.fromXML(getMethod.getResponseBodyAsStream());

		} catch (Exception e) {
			e.getStackTrace();
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
		return item;
	}

	/**
	 * Save item: if it does not exist, create it, otherwise modify it
	 * @param item
	 * @return
	 * @throws Exception
	 */
	public ItemDelegate saveItem(ItemDelegate item) throws Exception {

		Validate.notNull(item, "item must be not null");

		logger.info("Calling Servlet SaveItem " + item.getName() + " by " + login);

		ItemDelegate modifiedItem = null;

		PostMethod post = null;
		HttpClient httpClient = new HttpClient(); 
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		try {

			//			System.out.println(urlRepository + "/SaveItem");
			post =  new PostMethod(urlRepository + "/post/SaveItem?" + credential + "&uuid="+sessionId);
			post.setRequestEntity(new StringRequestEntity(xstream.toXML(item), "application/json", null));

			// execute the POST
			int response = httpClient.executeMethod(post);
			// Check response code
			if (response != HttpStatus.SC_OK)
				throw new HttpException("Received error status " + response);

			modifiedItem = (ItemDelegate) xstream.fromXML(post.getResponseBodyAsStream());

		} finally {
			if(post != null)
				post.releaseConnection();
		}

		return modifiedItem;
	}

	/**
	 * Copy node, change name and set as owner the current user
	 * @param path
	 * @param pathDestination
	 * @param name
	 * @param owner 
	 * @return
	 */
	//	public ItemDelegate copy(String srcAbsPath, String destAbsPath, String name, String owner) {
	//	
	//		System.out.println("Call servlet copy");
	//		ItemDelegate item = null;
	//
	//		PostMethod post = new PostMethod();
	//		HttpClient httpClient = new HttpClient(); 
	//		XStream xstream = new XStream();
	//		try {
	////			srcAbsPath, String destAbsPath, boolean removeExisting)
	//			System.out.println(urlRepository + "/Copy?srcAbsPath=" + srcAbsPath + "&destAbsPath=" +destAbsPath + "&removeExisting="+removeExisting);
	//			post =  new PostMethod(urlRepository + "/Copy?srcAbsPath=" + srcAbsPath + "&destAbsPath=" +destAbsPath + "&removeExisting="+removeExisting);
	//
	//			// execute the POST
	//			int response = httpClient.executeMethod(post);
	//			System.out.println("status: " + response);
	//			// Check response code
	//			if (response != HttpStatus.SC_OK)
	//				throw new HttpException("Received error status " + response);
	//
	//			item = (ItemDelegate) xstream.fromXML(post.getResponseBodyAsStream());
	//
	//
	//		} finally {
	//			if(post != null)
	//				post.releaseConnection();
	//		}
	//		return item;
	//		
	//		//		workspace.copyRemoteContent(itemSaved,itemSaved);
	//	
	//	}


	/**
	 * Clone item
	 * @param srcAbsPath
	 * @param destAbsPath
	 * @param b
	 * @return
	 * @throws IOException 
	 * @throws HttpException 
	 */
	public ItemDelegate clone(String srcAbsPath, String destAbsPath, boolean removeExisting) throws HttpException, IOException {
		logger.info("Calling Servlet Clone from " + srcAbsPath + " to " +destAbsPath +" by " + login);
		ItemDelegate modifiedItem = null;

		GetMethod getMethod = new GetMethod();
		HttpClient httpClient = new HttpClient(); 
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		try {
			getMethod =  new GetMethod(urlRepository + "/get/Clone?" + credential + "&srcAbsPath=" + URLEncoder.encode(srcAbsPath, "UTF-8") + "&destAbsPath=" +URLEncoder.encode(destAbsPath, "UTF-8") + "&removeExisting="+removeExisting + "&uuid="+sessionId);

			// execute the POST
			httpClient.executeMethod(getMethod);

			modifiedItem = (ItemDelegate) xstream.fromXML(getMethod.getResponseBodyAsStream());

		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
		return modifiedItem;

	}



	/**
	 * Moves the node at srcAbsPath (and its entire subtree) to the new location at destAbsPath. 
	 * @param srcAbsPath is an absolute path to the original location 
	 * @param destAbsPath is an absolute path to the parent node of the new location, appended with the new name desired for the moved node
	 * @return the item moved
	 * @throws HttpException
	 * @throws IOException
	 */
	public ItemDelegate move(String srcAbsPath, String destAbsPath) throws HttpException, IOException {
		logger.info("Calling Servlet Move from " + srcAbsPath + " to " +destAbsPath +" by " + login);
		ItemDelegate modifiedItem = null;

		GetMethod getMethod = null;
		HttpClient httpClient = new HttpClient(); 
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		try {
			getMethod =  new GetMethod(urlRepository + "/get/Move?" + credential + "&srcAbsPath=" + URLEncoder.encode(srcAbsPath, "UTF-8") + "&destAbsPath=" +URLEncoder.encode(destAbsPath, "UTF-8")+ "&uuid="+sessionId);

			// execute the POST
			int response = httpClient.executeMethod(getMethod);
			// Check response code
			if (response != HttpStatus.SC_OK)
				throw new HttpException("Received error status " + response);

			modifiedItem = (ItemDelegate) xstream.fromXML(getMethod.getResponseBodyAsStream());

		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}

		return modifiedItem;

	}

	public void removeItem(String absPath) throws IOException {

		logger.info("Calling Servlet RemoveItem " + absPath +" by " + login);
		PostMethod post = new PostMethod();
		HttpClient httpClient = new HttpClient(); 
		try {
			post =  new PostMethod(urlRepository + "/post/RemoveItem?" + credential + "&absPath=" +  URLEncoder.encode(absPath, "UTF-8") + "&uuid="+sessionId);

			// execute the POST
			int response = httpClient.executeMethod(post);
			// Check response code
			if (response != HttpStatus.SC_OK)
				throw new HttpException("Received error status " + response);


		} finally {
			if(post != null)
				post.releaseConnection();
		}

	}

	public ItemDelegate copy(String srcAbsPath, String destAbsPath) throws HttpException, IOException {
		ItemDelegate item = null;

		GetMethod post = new GetMethod();
		HttpClient httpClient = new HttpClient(); 
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		try {
			post =  new GetMethod(urlRepository + "/get/Copy?" + credential + "&srcAbsPath=" +  URLEncoder.encode(srcAbsPath, "UTF-8") + "&destAbsPath=" +  URLEncoder.encode(destAbsPath, "UTF-8") + "&uuid="+sessionId);

			// execute the POST
			int response = httpClient.executeMethod(post);
			// Check response code
			if (response != HttpStatus.SC_OK)
				throw new HttpException("Received error status " + response);

			item = (ItemDelegate) xstream.fromXML(post.getResponseBodyAsStream());


		} finally {
			if(post != null)
				post.releaseConnection();
		}
		return item;

	}

	public ItemDelegate copyContent(String srcId, String destId) throws HttpException, IOException {

		ItemDelegate item = null;

		PostMethod post = new PostMethod();
		HttpClient httpClient = new HttpClient(); 
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		try {
			post =  new PostMethod(urlRepository + "/post/CopyContent?" + credential + "&srcId=" + srcId + "&destId=" +destId+ "&uuid="+sessionId);

			// execute the POST
			int response = httpClient.executeMethod(post);
			// Check response code
			if (response != HttpStatus.SC_OK)
				throw new HttpException("Received error status " + response);

			item = (ItemDelegate) xstream.fromXML(post.getResponseBodyAsStream());


		} finally {
			if(post != null)
				post.releaseConnection();
		}
		return item;
	}


	@SuppressWarnings("unchecked")
	public List<SearchItemDelegate> executeQuery(String query, String lang, String login, int limit) throws HttpException, IOException {

		GetMethod get = null;
		HttpClient httpClient = new HttpClient(); 
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		List<SearchItemDelegate> list = null;
		try {

			get =  new GetMethod(urlRepository + "/get/ExecuteQuery?" + credential + "&query=" + URLEncoder.encode(query, "UTF-8")  + "&lang=" + lang + "&login="+ login + "&limit="+ limit+ "&uuid="+sessionId);
			// execute the POST
			int response = httpClient.executeMethod(get);
			// Check response code
			if (response != HttpStatus.SC_OK)
				throw new HttpException("Received error status " + response);

			list = (List<SearchItemDelegate>) xstream.fromXML(get.getResponseBodyAsStream());


		} finally {
			if(get != null)
				get.releaseConnection();
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<ItemDelegate> searchItems(String query, String lang, String login) throws HttpException, IOException {

		GetMethod get = null;
		HttpClient httpClient = new HttpClient(); 
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		List<ItemDelegate> list = null;
		try {
			get =  new GetMethod(urlRepository + "/get/SearchItems?" + credential + "&query=" + URLEncoder.encode(query, "UTF-8")  + "&lang=" + lang + "&login="+ login+ "&uuid="+sessionId);
			// execute the POST
			int response = httpClient.executeMethod(get);
			// Check response code
			if (response != HttpStatus.SC_OK)
				throw new HttpException("Received error status " + response);

			list = (List<ItemDelegate>) xstream.fromXML(get.getResponseBodyAsStream());


		} finally {
			if(get != null)
				get.releaseConnection();
		}
		return list;
	}


	public void saveAccountingItem(AccountingDelegate item) throws RepositoryException {
		Validate.notNull(item, "item must be not null");

		PostMethod post = null;
		HttpClient httpClient = new HttpClient(); 
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		try {
			post =  new PostMethod(urlRepository + "/SaveAccountingItem?" + credential+ "&uuid="+sessionId);
			post.setRequestEntity(new StringRequestEntity(xstream.toXML(item), "application/json", null));

			// execute the POST
			int response = httpClient.executeMethod(post);
			// Check response code
			if (response != HttpStatus.SC_OK)
				throw new HttpException("Received error status " + response);

		} catch (IOException e) {
			throw new RepositoryException(e.getMessage());
		} finally {
			if(post != null)
				post.releaseConnection();
		}


	}

	@SuppressWarnings("unchecked")
	public List<AccountingDelegate> getAccountingById(String id) {
		List<AccountingDelegate> items = null;
		GetMethod getMethod = null;
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		HttpClient httpClient = new HttpClient(); 

		try {   
			getMethod =  new GetMethod(urlRepository + "/GetAccountingById?" + credential + "&id=" + id + "&uuid="+sessionId);
			httpClient.executeMethod(getMethod);		
			items= (List<AccountingDelegate>) xstream.fromXML(getMethod.getResponseBodyAsStream());

		} catch (Exception e) {
			e.getStackTrace();
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
		return items;

	}

	public ItemDelegate createReference(String itemId, String destinationFolderId, String login) throws HttpException, IOException {
		logger.info("Calling Servlet createReference of Node Id " + itemId + " to destination folder ID" +destinationFolderId +" by " + login);
		ItemDelegate modifiedItem = null;

		GetMethod getMethod = null;
		HttpClient httpClient = new HttpClient(); 
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		try {

			getMethod =  new GetMethod(urlRepository + "/get/CreateReference?" + credential + "&srcId=" + itemId + "&destId=" +destinationFolderId + "&login="+ login+ "&uuid="+sessionId);

			// execute the POST
			int response = httpClient.executeMethod(getMethod);
			// Check response code
			if (response != HttpStatus.SC_OK)
				throw new HttpException("Received error status " + response);

			modifiedItem = (ItemDelegate) xstream.fromXML(getMethod.getResponseBodyAsStream());

		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}

		return modifiedItem;
	}

	public JCRLockManager getLockManager() {
		if (lockManager==null)
			lockManager = new JCRLockManager(login, sessionId, urlRepository, credential);

		return lockManager;

	}

}
