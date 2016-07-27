/**
 *
 */
package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.CkanConnectorAccessPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class CkanLogout.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 27, 2016
 */
public class CkanLogout extends HttpServlet {

	/**
	 *
	 */
	private static final long serialVersionUID = 2793892309831716065L;

	private static Logger logger = LoggerFactory.getLogger(CkanLogout.class);

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession httpSession = req.getSession();
		ASLSession session = getASLSession(httpSession);
		String scope = session.getScope();
		String username = session.getUsername();
		logger.info("CkanLogout performing...");
		CkanConnectorAccessPoint ckanAP = SessionUtil.getCkanAccessPoint(req.getSession(), scope);
		//		String token = getGcubeSecurityToken();
		logger.info("Logout from CKAN for: "+username +" by token: "+ckanAP.getGcubeTokenValue() +", the scope is: "+scope);

		String ckanConnectorLogut = getServletContext().getInitParameter(GcubeCkanDataCatalogServiceImpl.CKANCONNECTORLOGOUT);
		logger.debug(GcubeCkanDataCatalogServiceImpl.CKANCONNECTORLOGOUT + " is: "+ckanConnectorLogut);

		CkanConnectorAccessPoint ckan = new CkanConnectorAccessPoint(ckanAP.getBaseUrl(), ckanConnectorLogut);
		ckan.addGubeToken(ckanAP.getGcubeTokenValue());

		String deleteURI = ckan.buildURI();
		logger.debug(GcubeCkanDataCatalogServiceImpl.CKANCONNECTORLOGOUT + " calling: "+deleteURI);
		resp.sendRedirect(deleteURI);

		/*logger.debug(GcubeCkanDataCatalogServiceImpl.CKANCONNECTORLOGOUT + " is: "+ckanConnectorLogut);
		CloseableHttpResponse httpResponse = null;
		try {

			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(deleteURI);
			httpResponse = httpclient.execute(httpget);

			if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				resp.setStatus(HttpStatus.SC_OK);

				Header[] headers = httpResponse.getAllHeaders();

				for (Header header : headers) {
					logger.trace("header key: "+header.getName() +", value: "+header.getValue());
					if(header.getName()==null){
						logger.trace("skip key: "+header.getName() +", value: "+header.getValue());
					}else
						resp.setHeader(header.getName(),header.getValue());
				}

				try {
					HttpEntity entity = httpResponse.getEntity();
					if (entity != null) {
						resp.setHeader("Content-Length", String.valueOf(entity.getContentLength()));
						//				    	resp.setContentLength(entity.getContentLength());
						Header encoding = entity.getContentEncoding();
						logger.trace("Encoding: "+encoding.getName() + ", "+encoding.getValue());
						//				    	String encod = encoding == null ? GcubeCkanDataCatalogServiceImpl.UTF_8 : encoding.getName();
						//				    	resp.setCharacterEncoding(encoding);
						resp.setStatus(HttpStatus.SC_OK);
						resp.setContentType(resp.getContentType());
						InputStream in = entity.getContent();
						try {
							ServletOutputStream out = resp.getOutputStream();
							IOUtils.copy(in, out);
							logger.info("Logout Completed, response code: "+HttpStatus.SC_OK);
						} finally {
							in.close();
						}
					}
				}catch(Exception e){
					logger.warn("An error occurred during copying CKAN logout response",e);
				}

			}else{
				logger.warn("An error occurred during perfoming CKAN logout, Response status is: "+httpResponse.getStatusLine().getStatusCode());
				HttpEntity entity = httpResponse.getEntity();
				if (entity != null) {
					InputStream in = entity.getContent();
					if(in!=null){
						logger.error("Response error: "+IOUtils.toString(in));
					}
				}
			}
		}catch(Exception e){
			logger.warn("An error occurred during perfoming CKAN logout", e);
		}finally {
			if(httpResponse!=null)
				httpResponse.close();
		}*/

		/*try {

			logger.debug("Perfoming HTTP delete to URI: "+deleteURI);
			url = new URL(deleteURI);
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();

			req.getCookies();
//			httpCon.setDoOutput(true);
//			httpCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded" );
//			httpCon.setRequestProperty(key, value);
			httpCon.setRequestMethod("GET");
//			httpCon.setRequestMethod("DELETE");
			httpCon.connect();

			HttpServletResponse response = resp;

			if(httpCon.getResponseCode()==HttpStatus.SC_OK){
				response.setContentLength(httpCon.getContentLength());
				Map<String, List<String>> map = httpCon.getHeaderFields();
				for (String  key : map.keySet()) {
					String hf = httpCon.getHeaderField(key);
					logger.trace("key: "+key +", value: "+hf);
					if(key==null){
						logger.trace("skip key: "+key +", value: "+hf);
					}else
						response.setHeader(key,hf);
				}

				response.setContentLength(httpCon.getContentLength());
				String encoding = httpCon.getContentEncoding();
				encoding = encoding == null ? GcubeCkanDataCatalogServiceImpl.UTF_8 : encoding;
				response.setCharacterEncoding(encoding);
				response.setStatus(HttpStatus.SC_OK);
				response.setContentType(httpCon.getContentType());

//				Cookie cookie = new Cookie("user", null); // Not necessary, but saves bandwidth.
//				cookie.setPath("/MyApplication");
//				cookie.setHttpOnly(true);
//				cookie.setMaxAge(0); // Don't set to -1 or it will become a session cookie!
//				response.addCookie(cookie);

				InputStream in = httpCon.getInputStream();
				ServletOutputStream out = response.getOutputStream();
				IOUtils.copy(in, out);
				logger.info("Logout Completed, response code: "+HttpStatus.SC_OK);

			}else{
				logger.warn("An error occurred during perfoming CKAN logout, Response status is: "+httpCon.getResponseCode());
				logger.warn(IOUtils.toString(httpCon.getErrorStream()));
			}
		}
		catch (IOException e) {
			logger.error("An error occured during performing Logout from CKAN for: "+username +" by token: "+ckanAP.getGcubeTokenValue(), e);
		}*/
	}

	/**
	 * Gets the ASL session.
	 *
	 * @param httpSession the http session
	 * @return the ASL session
	 */
	protected ASLSession getASLSession(HttpSession httpSession)
	{
		String sessionID = httpSession.getId();
		String user = (String) httpSession.getAttribute(GcubeCkanDataCatalogServiceImpl.USERNAME_ATTRIBUTE);

		if (user == null) {

			logger.warn("****** STARTING IN TEST MODE - NO USER FOUND *******");
			//for test only
			user = GcubeCkanDataCatalogServiceImpl.TEST_USER;
			httpSession.setAttribute(GcubeCkanDataCatalogServiceImpl.USERNAME_ATTRIBUTE, user);
			ASLSession session = SessionManager.getInstance().getASLSession(sessionID, user);
			session.setScope(GcubeCkanDataCatalogServiceImpl.TEST_SCOPE);
			session.setUserEmailAddress(GcubeCkanDataCatalogServiceImpl.TEST_MAIL);
			//session.setScope("/gcube/devsec/devVRE");

			return session;
		} else logger.trace("user found in session "+user);
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}


}
