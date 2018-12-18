package org.gcube.datatransfer.resolver.storagehub;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.storagehub.client.StreamDescriptor;
import org.gcube.common.storagehub.client.dsl.StorageHubClient;
import org.gcube.datatransfer.resolver.http.ConstantsHttpResolver;
import org.gcube.datatransfer.resolver.scope.ContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The Class StorageHubResolver.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Oct 24, 2018
 */
public class StorageHubResolver extends HttpServlet {

	/**
	 *
	 */
	private static final long serialVersionUID = -5614200518746652383L;

	public static final String ID = "id";

	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(StorageHubResolver.class);



	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		logger.info(this.getClass().getSimpleName()+" GET starts...");

		String originalScope = ScopeProvider.instance.get();
		String orginalToken = SecurityTokenProvider.instance.get();

		String storageHubId = request.getParameter(ID);

		if (storageHubId == null || storageHubId.equals("")) {
			logger.warn(ID+" not found");
			sendErrorQuietly(response, 400);
			return;
		}

		String version  = request.getParameter("version");

		if (version == null || version.equals("")) {
			logger.warn("version not found");
			version = null;
		}

		//Reading scope from ENV
		String theScope = null;
		try {
			theScope =  ContextUtil.getScopeFromEnvironment();
		}
		catch (ServletException e1) {
			logger.error("Error: ", e1);
			sendErrorQuietly(response, 500);
			return;
		}

		//Reading App Token from ENV
		String theAppToken = null;
		try {
			theAppToken =  ContextUtil.getAppTokenStoragHubEnvironment();
		}
		catch (ServletException e1) {
			logger.error("Error: ", e1);
			sendErrorQuietly(response, 500);
			return;
		}

		ScopeProvider.instance.set(theScope);
		SecurityTokenProvider.instance.set(theAppToken);

		InputStream in = null;
		StreamDescriptor descriptor = null;
		try {

			StorageHubClient shc = new StorageHubClient();

			if(version==null){
				logger.warn("Downloading file with id: "+storageHubId);
				descriptor = shc.open(storageHubId).asFile().download();
			}
			else{
				logger.warn("Downloading versioned file with id: "+storageHubId +" and version: "+version);
				descriptor = shc.open(storageHubId).asFile().downloadSpecificVersion(version);
			}

			in = descriptor.getStream();

			//CASE InputStream NULL
			if(in==null){
				logger.error("Input stream returned from StorageHub is null, sending status error 404");
				sendErrorQuietly(response, 404);
				return;
			}

			response.setHeader(ConstantsHttpResolver.CONTENT_DISPOSITION, "attachment; filename=\"" + descriptor.getFileName() + "\"");
			OutputStream out = response.getOutputStream();
			try {

				IOUtils.copy(in, out);

			} catch (IOException e){
				logger.warn("IOException class name: "+e.getClass().getSimpleName());
				if (e.getClass().getSimpleName().equals("ClientAbortException"))
					logger.warn("Skipping ClientAbortException: "+e.getMessage());
				else
					throw e; //Sending Exceptions

			}catch (NullPointerException e) {
				logger.warn("NullPointerException during copy, skipping printStrackTrace");
				sendErrorQuietly(response, 404);
				return;

			}catch (Exception e) {
				logger.error("Exception: ",e);
				sendErrorQuietly(response, 404);
				return;

			} finally {
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(out);
			}

		} catch (Exception e) {
			logger.error("Exception:", e);
			IOUtils.closeQuietly(in);
			sendErrorQuietly(response, 404);
			return;
		}finally{
			if(originalScope!=null){
				ScopeProvider.instance.set(originalScope);
				logger.info("scope provider set to orginal scope: "+originalScope);
			}else{
				ScopeProvider.instance.reset();
				logger.info("scope provider reset");
			}

			if(orginalToken!=null){
				SecurityTokenProvider.instance.set(orginalToken);
				logger.info("toen provider set to orginal token");
			}else{
				SecurityTokenProvider.instance.reset();
				logger.info("token provider reset");
			}
		}

	}

	/**
	 * Send error quietly.
	 *
	 * @param response the response
	 * @param code the code
	 */
	protected void sendErrorQuietly(HttpServletResponse response, int code){

		if(response!=null){
			try {
				response.sendError(code);
				logger.info("Response sent error: "+code);
			} catch (IOException ioe) {
				 // ignore
			}
		}
	}


}