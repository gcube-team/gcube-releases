package org.gcube.common.homelibrary.jcr.workspace.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.Validate;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.SearchItemDelegate;
import org.gcube.common.homelibary.model.items.accounting.AccountingDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.servlet.ServletParameter;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.homelibrary.jcr.repository.ServletName;
import org.gcube.common.homelibrary.jcr.workspace.lock.JCRLockManager;
import org.gcube.common.homelibrary.jcr.workspace.util.TokenUtility;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class JCRSession {

	//	private static final String ServletName.GCUBE_TOKEN = "gcube-token";
	private Map<String, Endpoint> servlets;
	private Logger logger = LoggerFactory.getLogger(JCRSession.class);
	private String login;
	private String sessionId;
	private JCRLockManager lockManager;


	public JCRSession(String login, Boolean createSession) throws RepositoryException{
		this.login = login;
		this.servlets = JCRRepository.servlets;
		if (createSession)
			sessionId = getSession();
	}

	//	public JCRSession(String login) throws RepositoryException{
	//		this.login = login;
	//		this.servlets = JCRRepository.servlets;
	//		this.sessionId = getSession();
	//	}

	public String getLogin(){
		return this.login;
	}

	public String getSessionId(){
		return this.sessionId;
	}


	private String loginInfo() {
		StringBuilder loginInfo = new StringBuilder();
		loginInfo.append("?").append(ServletParameter.UUID).append("=").append(sessionId);
		loginInfo.append("&").append(ServletParameter.PORTAL_LOGIN).append("=").append(login);
		return loginInfo.toString();
	}

	/**
	 * Get a new session
	 * @param login
	 * @return
	 * @throws RepositoryException 
	 */
	private String getSession() throws RepositoryException {
		//		logger.debug("Calling servlet getSession by " + login);
		String uuid = null;
		GetMethod getMethod = null;
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		HttpClient httpClient = new HttpClient(); 

		try {   
			getMethod =  new GetMethod(servlets.get(ServletName.CREATE_SESSION).uri().toString() + loginInfo());
			TokenUtility.setHeader(getMethod);
			httpClient.executeMethod(getMethod);	
			uuid = (String) xstream.fromXML(getMethod.getResponseBodyAsStream());
			//			logger.debug("Session " + uuid + " has been created by " + login);
		} catch (Exception e) {
			throw new RepositoryException(e.getMessage());
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}

		return uuid;
	}

	/**
	 * Release session
	 * @throws RepositoryException 
	 */
	public void releaseSession() {
		//		logger.debug("Calling servlet releaseSession " + sessionId +  " by " + login);

		if (sessionId!=null){
			GetMethod getMethod = null;
			HttpClient httpClient = new HttpClient(); 

			try {   
				getMethod =  new GetMethod(servlets.get(ServletName.RELEASE_SESSION).uri().toString() + loginInfo());
				TokenUtility.setHeader(getMethod);
				httpClient.executeMethod(getMethod);	
				//		xstream.fromXML(getMethod.getResponseBodyAsStream());
			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
				if(getMethod != null)
					getMethod.releaseConnection();
			}
		}
	}



	//	/**
	//	 * Release all active sessions
	//	 */
	//	public void releaseAllSessions() {
	//		logger.debug("Calling servlet releaseAllSessions by " + login);
	//
	//		GetMethod getMethod = null;
	//		HttpClient httpClient = new HttpClient(); 
	//
	//		try {   
	//			getMethod =  new GetMethod(JCRRepository.url + "/ReleaseAllSessions?"  );
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
	 * @throws RepositoryException 
	 * @throws InternalErrorException
	 */
	@SuppressWarnings("unchecked")
	public List<ItemDelegate> getChildrenById(String id, Boolean showHidden) throws RepositoryException {
		logger.debug("Calling servlet getChildrenById " + id + " by " + login);
		List<ItemDelegate> items = null;
		GetMethod getMethod = null;
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		HttpClient httpClient = new HttpClient(); 

		try {  
			//			System.out.println(servlets.get(ServletName.GET_CHILDREN_BY_ID).uri().toString() + "?id=" + id +  "&uuid="+sessionId+ "&showHidden="+showHidden);
			getMethod =  new GetMethod(servlets.get(ServletName.GET_CHILDREN_BY_ID).uri().toString() + loginInfo() + "&id=" + id + "&showHidden=" + showHidden );
			TokenUtility.setHeader(getMethod);
			httpClient.executeMethod(getMethod);	
			items = (List<ItemDelegate>) xstream.fromXML(new InputStreamReader(getMethod.getResponseBodyAsStream(),"UTF-8"));
		} catch (Exception e) {
			throw new RepositoryException(e.getMessage());
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
		return items;
	}


	@SuppressWarnings("unchecked")
	public List<ItemDelegate> GetHiddenItemsById(String id) throws RepositoryException {
		logger.debug("Calling servlet GetHiddenItemsById " + id + " by " + login);
		List<ItemDelegate> items = null;
		GetMethod getMethod = null;
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		HttpClient httpClient = new HttpClient(); 

		try {   
			getMethod =  new GetMethod(servlets.get(ServletName.GET_HIDDEN_ITEMS_BY_ID).uri().toString()  + loginInfo() + "&id=" + id);
			TokenUtility.setHeader(getMethod);
			httpClient.executeMethod(getMethod);	
			items= (List<ItemDelegate>) xstream.fromXML(getMethod.getResponseBodyAsStream());
		} catch (Exception e) {
			throw new RepositoryException(e.getMessage());
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
		return items;
	}








	@SuppressWarnings("unchecked")
	public Map<String, String> moveToTrashIds(List<String> ids, String trashId) throws RepositoryException {

		Validate.notNull(trashId, "trashId must be not null");
		Validate.notNull(ids, "ids must be not null");

		logger.debug("Calling Servlet MoveToTrashIds on " + ids.size() + " by " + login);

		Map<String, String> error = null;
		PostMethod post = null;
		HttpClient httpClient = new HttpClient(); 
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		try {
			post =  new PostMethod(servlets.get(ServletName.MOVE_TO_TRASH_IDS).uri().toString()  + loginInfo() + "&trashId=" + trashId);
			TokenUtility.setHeader(post);
			post.setRequestEntity(new StringRequestEntity(xstream.toXML(ids), "application/json", null));

			int response = httpClient.executeMethod(post);
			// Check response code
			if (response != HttpStatus.SC_OK)
				throw new HttpException("Received error status " + response);

			error = (Map<String, String>) xstream.fromXML(post.getResponseBodyAsStream());

		} catch (IOException e) {
			throw new RepositoryException(e.getMessage());
		} finally {
			if(post != null)
				post.releaseConnection();
		}

		return error;
	}



	/**
	 * Retrieve an ItemDelegate object by path using a GET servlet
	 * @param user
	 * @param path
	 * @return an ItemDelegate
	 * @throws ItemNotFoundException 
	 */
	public ItemDelegate getItemByPath(String path) throws ItemNotFoundException {
		//		System.out.println("* " + path);
		logger.debug("*** Calling Servlet GetItemByPath " + path + " by " + login);
		ItemDelegate item = null;
		GetMethod getMethod = null;
		XStream xstream = new XStream(new DomDriver("UTF-8"));

		HttpClient httpClient = new HttpClient();  

		try {
//			System.out.println(servlets.get(ServletName.GET_ITEM_BY_PATH).uri().toString() + loginInfo() + "&path=" + URLEncoder.encode(path, "UTF-8"));
			getMethod =  new GetMethod(servlets.get(ServletName.GET_ITEM_BY_PATH).uri().toString() + loginInfo() + "&path=" + URLEncoder.encode(path, "UTF-8"));
			TokenUtility.setHeader(getMethod);
			httpClient.executeMethod(getMethod);
			item = (ItemDelegate) xstream.fromXML(getMethod.getResponseBodyAsStream());
//			System.out.println(item.toString());
			//		item.getId();
		} catch (Exception e) {
			throw new ItemNotFoundException(e.getMessage());
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
		return item;
	}

//	/Home/valentina.marioli/Workspace/Trash/4e799d62-8068-479d-824d-b30801ae185b/103529784_062ff7b562_b.jpg
//	/Home/valentina.marioli/Workspace/Trash/4e799d62-8068-479d-824d-b30801ae185b/103529784_062ff7b562_b.jpg
	/**
	 * Retrieve and ItemDelegate by id using a GET servlet
	 * @param user
	 * @param id
	 * @return
	 */
	public ItemDelegate getItemById(String id) throws ItemNotFoundException{
		logger.debug("Servlet getItemById " + id);
//		System.out.println("Servlet getItemById " + id);
		ItemDelegate item = null;
		GetMethod getMethod = null;
		HttpClient httpClient = new HttpClient();   
		XStream xstream = new XStream(new DomDriver("UTF-8"));

		try {

//		System.out.println(servlets.get(ServletName.GET_ITEM_BY_ID).uri().toString() + loginInfo() + "&id=" + id);
			getMethod =  new GetMethod(servlets.get(ServletName.GET_ITEM_BY_ID).uri().toString() + loginInfo() + "&id=" + id);
			TokenUtility.setHeader(getMethod);
			httpClient.executeMethod(getMethod);
			item = (ItemDelegate) xstream.fromXML(getMethod.getResponseBodyAsStream());

		} catch (Exception e) {
			throw new ItemNotFoundException(e.getMessage());
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
	public ItemDelegate getParentById(String id) throws ItemNotFoundException{
		logger.debug("Servlet getParentById " + id);
		ItemDelegate item = null;
		GetMethod getMethod = null;
		HttpClient httpClient = new HttpClient();   
		XStream xstream = new XStream(new DomDriver("UTF-8"));

		try {

			getMethod =  new GetMethod(servlets.get(ServletName.GET_PARENT_BY_ID).uri().toString() + loginInfo() + "&id=" + id );
			TokenUtility.setHeader(getMethod);
			httpClient.executeMethod(getMethod);
			item = (ItemDelegate) xstream.fromXML(getMethod.getResponseBodyAsStream());

		} catch (Exception e) {
			throw new ItemNotFoundException(e.getMessage());
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
	 * @throws IOException 
	 * @throws Exception
	 */
	public ItemDelegate saveItem(ItemDelegate item, InputStream is) throws RepositoryException {

		Validate.notNull(item, "item must be not null");

		logger.debug("Calling Servlet SaveItem " + item.getName() + " by " + login);

		ItemDelegate modifiedItem = null;

		PostMethod post = null;
		HttpClient httpClient = new HttpClient(); 
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		try {

			post =  new PostMethod(servlets.get(ServletName.SAVE_ITEM).uri().toString() + loginInfo());
			TokenUtility.setHeader(post);
			post.setRequestEntity(new StringRequestEntity(xstream.toXML(item), "application/json", null));

			// execute the POST
			int response = httpClient.executeMethod(post);
			// Check response code
			if (response != HttpStatus.SC_OK)
				throw new HttpException("Received error status " + response);

			modifiedItem = (ItemDelegate) xstream.fromXML(post.getResponseBodyAsStream());

		} catch (IOException e) {
			throw new RepositoryException(e.getMessage());
		} finally {
			if(post != null)
				post.releaseConnection();
		}

		return modifiedItem;
	}

	
	public ItemDelegate saveItem(ItemDelegate item) throws RepositoryException {

		return saveItem(item, false);
	}

	public ItemDelegate saveItem(ItemDelegate item, boolean createVersion) throws RepositoryException {

		Validate.notNull(item, "item must be not null");

		logger.debug("Calling Servlet SaveItem " + item.getName() + " by " + login);
		
//		System.out.println("Save " + item);

		ItemDelegate modifiedItem = null;

		PostMethod post = null;
		HttpClient httpClient = new HttpClient(); 
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		try {

			post =  new PostMethod(servlets.get(ServletName.SAVE_ITEM).uri().toString() + loginInfo()+ "&flag="+ createVersion);
			TokenUtility.setHeader(post);
			post.setRequestEntity(new StringRequestEntity(xstream.toXML(item), "application/json", null));

			// execute the POST
			int response = httpClient.executeMethod(post);
			// Check response code
			if (response != HttpStatus.SC_OK)
				throw new HttpException("Received error status " + response);

			modifiedItem = (ItemDelegate) xstream.fromXML(post.getResponseBodyAsStream());

		} catch (IOException e) {
			throw new RepositoryException(e.getMessage());
		} finally {
			if(post != null)
				post.releaseConnection();
		}

		return modifiedItem;
	}

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
		logger.debug("Calling Servlet Clone from " + srcAbsPath + " to " +destAbsPath +" by " + login);
		ItemDelegate modifiedItem = null;

		GetMethod getMethod = new GetMethod();
		HttpClient httpClient = new HttpClient(); 
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		try {
//			System.out.println(servlets.get(ServletName.CLONE).uri().toString() + loginInfo() + "&srcAbsPath=" + URLEncoder.encode(srcAbsPath, "UTF-8") + "&destAbsPath=" +URLEncoder.encode(destAbsPath, "UTF-8") + "&removeExisting="+removeExisting);
			getMethod =  new GetMethod(servlets.get(ServletName.CLONE).uri().toString() + loginInfo() + "&srcAbsPath=" + URLEncoder.encode(srcAbsPath, "UTF-8") + "&destAbsPath=" +URLEncoder.encode(destAbsPath, "UTF-8") + "&removeExisting="+removeExisting);
			TokenUtility.setHeader(getMethod);
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
		logger.debug("Calling Servlet Move from " + srcAbsPath + " to " +destAbsPath +" by " + login);
		ItemDelegate modifiedItem = null;

		GetMethod getMethod = null;
		HttpClient httpClient = new HttpClient(); 
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		try {
			getMethod =  new GetMethod(servlets.get(ServletName.MOVE).uri().toString() + loginInfo() + "&srcAbsPath=" + URLEncoder.encode(srcAbsPath, "UTF-8") + "&destAbsPath=" +URLEncoder.encode(destAbsPath, "UTF-8"));
			TokenUtility.setHeader(getMethod);
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

	public void removeItem(String absPath) throws RepositoryException{

		logger.debug("Calling Servlet RemoveItem " + absPath +" by " + login);
		PostMethod post = new PostMethod();
		HttpClient httpClient = new HttpClient(); 
		//		XStream xstream = new XStream(new DomDriver("UTF-8"));
		//		Boolean removedItem = false;
		try {
//			System.out.println(servlets.get(ServletName.REMOVE_ITEM).uri().toString() + loginInfo() + "&absPath=" +  URLEncoder.encode(absPath, "UTF-8"));
			post =  new PostMethod(servlets.get(ServletName.REMOVE_ITEM).uri().toString() + loginInfo() + "&absPath=" +  URLEncoder.encode(absPath, "UTF-8"));
			TokenUtility.setHeader(post);
			// execute the POST
			int response = httpClient.executeMethod(post);
			// Check response code
			if (response != HttpStatus.SC_OK)
				throw new HttpException("Received error status " + response);

			//			removedItem = (Boolean) xstream.fromXML(post.getResponseBodyAsStream());

		} catch (IOException e) {
			//			removedItem = false;
			throw new RepositoryException("Exception removing item " + absPath);
		} finally {
			if(post != null)
				post.releaseConnection();
		}
		//		return removedItem;

	}



	public Boolean changePrimaryType(String id, String primaryType) throws RepositoryException{

		logger.debug("Calling Servlet ChangePrimaryType ID" + id +" primaryType " + primaryType +" by " + login);
		PostMethod post = new PostMethod();
		HttpClient httpClient = new HttpClient(); 
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		Boolean changed = false;
		try {
			post =  new PostMethod(servlets.get(ServletName.CHANGE_PRIMARY_TYPE).uri().toString() + loginInfo() + "&id=" +  id + "&primaryType=" +  primaryType);
			TokenUtility.setHeader(post);
			// execute the POST
			int response = httpClient.executeMethod(post);
			// Check response code
			if (response != HttpStatus.SC_OK)
				throw new HttpException("Received error status " + response);

			changed = (Boolean) xstream.fromXML(post.getResponseBodyAsStream());

		} catch (IOException e) {
			throw new RepositoryException("Exception removing item " + id);
		} finally {
			if(post != null)
				post.releaseConnection();
		}
		return changed;

	}


	public ItemDelegate copy(String srcAbsPath, String destAbsPath, Boolean removeSubgraph) throws IOException {
		logger.debug("Calling Servlet Copy from " + srcAbsPath +" to "+ destAbsPath + " by " + login);

		ItemDelegate item = null;

		GetMethod post = new GetMethod();
		HttpClient httpClient = new HttpClient(); 
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		try {
			//			System.out.println(servlets.get(ServletName.COPY).uri().toString() + loginInfo() + "&srcAbsPath=" +  URLEncoder.encode(srcAbsPath, "UTF-8") + "&destAbsPath=" +  URLEncoder.encode(destAbsPath, "UTF-8") + "&subgraph=" +removeSubgraph);
			post =  new GetMethod(servlets.get(ServletName.COPY).uri().toString() + loginInfo() + "&srcAbsPath=" +  URLEncoder.encode(srcAbsPath, "UTF-8") + "&destAbsPath=" +  URLEncoder.encode(destAbsPath, "UTF-8") + "&subgraph=" +removeSubgraph);
			TokenUtility.setHeader(post);
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

	public ItemDelegate copyContent(String srcId, String destId) throws IOException {
		logger.debug("Calling Servlet CopyContent from id " + srcId +" to id "+ destId + " by " + login);
		ItemDelegate item = null;

		PostMethod post = new PostMethod();
		HttpClient httpClient = new HttpClient(); 
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		try {
			post =  new PostMethod(servlets.get(ServletName.COPY_CONTENT).uri().toString() + loginInfo() + "&srcId=" + srcId + "&destId=" + destId);
			TokenUtility.setHeader(post);
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
	public List<SearchItemDelegate> executeQuery(String query, String lang, int limit) throws HttpException, IOException {
		logger.debug("Calling Servlet ExecuteQuery - query: " + query +" - lang: "+ lang + " - limit: "+ limit +" by " + login);
		GetMethod get = null;
		HttpClient httpClient = new HttpClient(); 
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		List<SearchItemDelegate> list = null;
		try {
			//System.out.println(servlets.get(ServletName.EXECUTE_QUERY).uri().toString() + loginInfo() + "&query=" + query  + "&lang=" + lang + "&limit=" + limit);
			get =  new GetMethod(servlets.get(ServletName.EXECUTE_QUERY).uri().toString() + loginInfo() + "&query=" + URLEncoder.encode(query, "UTF-8")  + "&lang=" + lang + "&limit=" + limit);
			TokenUtility.setHeader(get);
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
	public List<ItemDelegate> searchItems(String query, String lang) throws HttpException, IOException {
		logger.debug("Calling Servlet SearchItems - query: " + query +" - lang: "+ lang + " by " + login);
		GetMethod get = null;
		HttpClient httpClient = new HttpClient(); 
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		List<ItemDelegate> list = null;
		try {
			//			System.out.println(servlets.get(ServletName.SEARCH_ITEMS).uri().toString() + loginInfo() + "&query=" + query + "&lang=" + lang);
			get =  new GetMethod(servlets.get(ServletName.SEARCH_ITEMS).uri().toString() + loginInfo() + "&query=" + URLEncoder.encode(query, "UTF-8")  + "&lang=" + lang);
			TokenUtility.setHeader(get);
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

		logger.debug("Calling Servlet SaveAccountingItem by " + login + " - " + item.getEntryType().toString() + " - " + item.getAccountingProperties().toString());

		PostMethod post = null;
		HttpClient httpClient = new HttpClient(); 
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		try {
			post =  new PostMethod(servlets.get(ServletName.SAVE_ACCOUNTING).uri().toString() + loginInfo());
			TokenUtility.setHeader(post);
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
	public List<AccountingDelegate> getAccountingById(String id) throws RepositoryException {

		logger.debug("Calling Servlet GetAccountingById - id: " + id + " by " + login);

		List<AccountingDelegate> items = null;
		GetMethod getMethod = null;
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		HttpClient httpClient = new HttpClient(); 

		try {   
			//			System.out.println(servlets.get(ServletName.GET_ACCOUNTING_BY_ID).uri().toString() + "?id=" + id + "&uuid="+sessionId);
			getMethod =  new GetMethod(servlets.get(ServletName.GET_ACCOUNTING_BY_ID).uri().toString() + loginInfo() + "&id=" + id);
			TokenUtility.setHeader(getMethod);
			httpClient.executeMethod(getMethod);		
			items= (List<AccountingDelegate>) xstream.fromXML(getMethod.getResponseBodyAsStream());

		} catch (Exception e) {
			throw new RepositoryException(e.getMessage());
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
		return items;

	}

	public ItemDelegate createReference(String itemId, String destinationFolderId) throws HttpException, IOException {
		logger.debug("Calling Servlet CreateReference of Node Id " + itemId + " to destination folder ID" +destinationFolderId +" by " + login);
		ItemDelegate modifiedItem = null;

		PostMethod postMethod = null;
		HttpClient httpClient = new HttpClient(); 
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		try {
			postMethod =  new PostMethod(servlets.get(ServletName.CREATE_REFERENCE).uri().toString() + loginInfo() + "&srcId=" + itemId + "&destId=" +destinationFolderId);
			TokenUtility.setHeader(postMethod);
			int response = httpClient.executeMethod(postMethod);
			// Check response code
			if (response != HttpStatus.SC_OK)
				throw new HttpException("Received error status " + response);

			modifiedItem = (ItemDelegate) xstream.fromXML(postMethod.getResponseBodyAsStream());

		} finally {
			if(postMethod != null)
				postMethod.releaseConnection();
		}

		return modifiedItem;
	}



	@SuppressWarnings("unchecked")
	public List<String> getReferences(String itemId) throws HttpException, IOException {
		Validate.notNull(itemId, "Item id must be not null");
		logger.debug("Calling Servlet CreateReference of Node Id " + itemId +" by " + login);
		List<String> list = null;

		GetMethod getMethod = null;
		HttpClient httpClient = new HttpClient(); 
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		try {

			getMethod =  new GetMethod(servlets.get(ServletName.GET_REFERENCES).uri().toString() + loginInfo() + "&srcId=" + itemId);
			TokenUtility.setHeader(getMethod);
			int response = httpClient.executeMethod(getMethod);
			// Check response code
			if (response != HttpStatus.SC_OK)
				throw new HttpException("Received error status " + response);

			list = (List<String>) xstream.fromXML(getMethod.getResponseBodyAsStream());

		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}

		return list;
	}

	public JCRLockManager getLockManager() {
		if (lockManager==null)
			lockManager = new JCRLockManager(login, sessionId);

		return lockManager;

	}

	@SuppressWarnings("unchecked")
	public List<ItemDelegate> getParentsById(String id) throws HttpException, IOException {
		Validate.notNull(id, "Item id must be not null");

		logger.debug("Calling Servlet get Parents By Id " + id +" by " + login);

		List<ItemDelegate> parents = null;
		GetMethod getMethod = null;
		HttpClient httpClient = new HttpClient(); 
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		try {
			getMethod =  new GetMethod(servlets.get(ServletName.GET_PARENTS_BY_ID).uri().toString() + loginInfo() + "&id=" + id);
			TokenUtility.setHeader(getMethod);

			int response = httpClient.executeMethod(getMethod);
			// Check response code
			if (response != HttpStatus.SC_OK)
				throw new HttpException("Received error status " + response);

			parents = (List<ItemDelegate>) xstream.fromXML(getMethod.getResponseBodyAsStream());

		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}

		return parents;
	}

	public ItemDelegate addNode(ItemDelegate parent, String id) throws HttpException, IOException {
		logger.debug("Calling Servlet add node with id " + id +" to node " + parent.getPath());


		ItemDelegate item = null;

		PostMethod post = new PostMethod();
		HttpClient httpClient = new HttpClient(); 
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		try {
			post =  new PostMethod(servlets.get(ServletName.ADD_NODE).uri().toString() + loginInfo() + "&id=" + id + "&parentId=" + parent.getId());
			TokenUtility.setHeader(post);

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

	public ItemDelegate createItem(ItemDelegate delegate, InputStream is) throws RepositoryException {
		Validate.notNull(delegate, "item must be not null");

		logger.debug("Calling Servlet SaveItem " + delegate.getName() + " by " + login);

		ItemDelegate modifiedItem = null;

//		PostMethod post = null;
//		HttpClient httpClient = new HttpClient(); 
		CloseableHttpClient client = HttpClientBuilder.create().build();
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		try {
			String description = "";
			if (delegate.getDescription()!=null)
				description = delegate.getDescription();

//			System.out.println(delegate.getParentId());
			String parentPath =  null;
			try {
				parentPath = getItemById(delegate.getParentId()).getPath();
			} catch (ItemNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			String uri = servlets.get(ServletName.CREATE_ITEM).uri().toString() + loginInfo()+ "&name=" + URLEncoder.encode(delegate.getName(), "UTF-8")+ "&description=" + URLEncoder.encode(description, "UTF-8") + "&parentPath=" + URLEncoder.encode(parentPath, "UTF-8") ;
//			System.out.println(uri);
			HttpPost post = new HttpPost(uri);
			TokenUtility.setHeader(post);
			
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();         
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			builder.addBinaryBody("upstream", is);
			// 
			HttpEntity entity = builder.build();
			post.setEntity(entity);
			HttpResponse httpResponse = client.execute(post);
			  HttpEntity httpEntity = httpResponse.getEntity();
		        String response = EntityUtils.toString(httpEntity);
			
			
//			post =  new PostMethod(uri);

//			TokenUtility.setHeader(post);
//			post.setRequestEntity(new StringRequestEntity(xstream.toXML(is), "application/json", null));
//
//			// execute the POST
//			int response = httpClient.executeMethod(post);
			// Check response code
//			if (response != HttpStatus.SC_OK)
//				throw new HttpException("Received error status " + response);

			modifiedItem = (ItemDelegate) xstream.fromXML(response);

		} catch (IOException e) {
			throw new RepositoryException(e.getMessage());
		} finally {

//			if(post != null)
//				post.releaseConnection();
		}

		return modifiedItem;
	}

}
