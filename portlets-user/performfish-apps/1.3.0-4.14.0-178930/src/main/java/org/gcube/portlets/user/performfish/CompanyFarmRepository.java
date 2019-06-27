package org.gcube.portlets.user.performfish;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibary.model.versioning.WorkspaceVersion;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.storagehub.client.StreamDescriptor;
import org.gcube.common.storagehub.client.dsl.StorageHubClient;
import org.gcube.common.storagehub.client.plugins.AbstractPlugin;
import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.service.Version;
import org.gcube.portlets.user.performfish.bean.PublishAnonymisedJob;
import org.gcube.portlets.user.performfish.bean.SubmittedForm;
import org.gcube.portlets.user.performfish.util.ImportedBatchChecker;
import org.gcube.portlets.user.performfish.util.PFISHConstants;
import org.gcube.portlets.user.performfish.util.Utils;
import org.gcube.portlets.user.performfish.util.comparators.WSItemComparator;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.model.GCubeTeam;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * 
 * @author M. Assante CNR-ISTI
 */
public class CompanyFarmRepository extends MVCPortlet {
	public static final String PHASE_PREFERENCE_ATTR_NAME = "phase";

	private static com.liferay.portal.kernel.log.Log _log = LogFactoryUtil.getLog(CompanyFarmRepository.class);

	private static RoleManager RM = new LiferayRoleManager();

	@Override
	public void render(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		_log.info(" This is render method of CompanyFarmRepository");

		try {
			GCubeTeam theFarm = null;
			HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));
			String[] farmIds = ParamUtil.getParameterValues(httpReq, Utils.ENCODED_FARM_PARAM);
			String selectedItemId = (String) request.getAttribute("itemId");
			if (selectedItemId == null) {//this handles when show version is clicked
				if (farmIds == null || farmIds.length == 0) {
					List<GCubeTeam> theFarms = Utils.getUserFarms(request, response, this); //if pass here only one farm is returned otherswise ir renders Utils.SELECT_FARM_PAGE_PATH
					if (theFarms != null && ! theFarms.isEmpty()) {
						theFarm = theFarms.get(0);
						request.setAttribute("theFarm", theFarms.get(0)); //pass to the JSP
					}
				}
				else { //the farmId is passed via param on the query string
					long selectedFarmId = Utils.unmaskId(farmIds[0]);
					theFarm = RM.getTeam(selectedFarmId);
					if ( Utils.checkBelongsToTeam(PortalUtil.getUserId(request), theFarm.getTeamId(), PortalUtil.getScopeGroupId(request)) ) //check that the user belong ot the farm
						request.setAttribute("theFarm", theFarm); //pass to the JSP
					else {
						PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(Utils.NOT_BELONGING_ANY_FARM_PAGE_PATH);
						dispatcher.include(request, response);	
					}					
				}	
				_log.info(" initialise of CompanyFarmRepository Page done");
				long groupId = PortalUtil.getScopeGroupId(request);
				GCubeTeam theCompany = Utils.checkBelongingToOneCompanyOnly(request, response, this);
				request.setAttribute("theCompany", theCompany);

				String context = Utils.getCurrentContext(request);
				String username = Utils.getCurrentUser(request).getUsername();
				if (theFarm != null ) {
					//check folder exists
					WorkspaceFolder sharedFolder = Utils.getWSFarmFolder(
							username, 
							context, 
							theCompany, theFarm);

					List<WorkspaceItem> companyFiles = sharedFolder.getChildren(true);
					Collections.sort(companyFiles, new WSItemComparator());
					List<ExternalFile> filteredFiles = new ArrayList<>();
					PortletPreferences portletPreferences = request.getPreferences();
					String selectedPhase = GetterUtil.getString(portletPreferences.getValue(PHASE_PREFERENCE_ATTR_NAME, StringPool.BLANK));
					_log.debug("selectedPhase: "+selectedPhase);
					String adjustedPhaseName = selectedPhase;
					if (selectedPhase.compareTo("Pre") == 0)
						adjustedPhaseName = "Pre-grow";
					if (selectedPhase.compareTo(PFISHConstants.SHOW_ALL_PHASES) != 0) {
						for (WorkspaceItem workspaceItem : companyFiles) {
							if (workspaceItem.getName().contains(adjustedPhaseName) ||
									((selectedPhase.compareTo("Grow") == 0) && workspaceItem.getName().contains("Farm Data")) ) {
								if (workspaceItem instanceof ExternalFile) {
									ExternalFile file = (ExternalFile) workspaceItem;
									filteredFiles.add(file);
								}
							} 
						}
					}
					else {
						for (WorkspaceItem workspaceItem : companyFiles) {
							if (workspaceItem instanceof ExternalFile) {
								ExternalFile file = (ExternalFile) workspaceItem;
								filteredFiles.add(file);
							}
						}
					}

					//TODO
					List<SubmittedForm> submittedFormsWithPublishStatus= getSubmittedFormPublishAndAnonimStatus(request, context, theFarm.getTeamId(), filteredFiles);
					request.setAttribute("submittedFormsWithPublishStatus", submittedFormsWithPublishStatus); //pass to the JSP
					String imageUrl = Utils.getCompanyLogoURL(theCompany.getTeamName(), groupId, request);
					request.setAttribute("companyLogoURL", imageUrl); //pass to logo URL to the JSP
				} 
			} 
			super.render(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<SubmittedForm> getSubmittedFormPublishAndAnonimStatus(RenderRequest renderRequest, String context, long farmId, List<ExternalFile> filteredFiles) throws Exception {
		List<SubmittedForm> toReturn = new ArrayList<>();

		String authorizationToken = "";
		try {
			authorizationToken = Utils.getCurrentUserToken(context, Utils.getCurrentUser(renderRequest).getUsername());
		} catch (Exception e) {
			_log.error("Cannot get user token");
		}
		String analyticalToolkitServiceURL = Utils.getAnalyticalToolkitEndpoint(context) ;
		SecurityTokenProvider.instance.set(authorizationToken);
		StringBuilder sb = new StringBuilder(analyticalToolkitServiceURL)
				.append("/import/")
				.append(farmId).append("?gcube-token="+authorizationToken);
		String request = sb.toString();
		fixUntrustCertificate() ;
		URL url= new URL(request);
		HttpsURLConnection conn= (HttpsURLConnection) url.openConnection();           
		conn.setDoOutput( true );
		conn.setInstanceFollowRedirects( false );
		conn.setRequestMethod( "GET" );
		conn.setRequestProperty( "Content-Type", "application/json"); 
		conn.setRequestProperty( "charset", "utf-8");
		conn.setUseCaches( false );

		InputStream in = conn.getInputStream();
		String encoding = conn.getContentEncoding();
		encoding = encoding == null ? "UTF-8" : encoding;
		String body = IOUtils.toString(in, encoding);

		JSONArray jsArray = JSONFactoryUtil.createJSONArray(body);
		Map<String, PublishAnonymisedJob> jobs = new HashMap<>();
		System.out.println("*** Processing list, no of items = "+jsArray.length());
		for (int i = 0; i < jsArray.length(); i++) {
			JSONObject cData = jsArray.getJSONObject(i);
			PublishAnonymisedJob toPut = new PublishAnonymisedJob(cData);
			String sourceUrl = toPut.getSourceUrl(); //theKey
			if (jobs.containsKey(sourceUrl)) {
				PublishAnonymisedJob toCompare = jobs.get(sourceUrl);
				if (toPut.getEndTimeEpochSecond() > toCompare.getEndTimeEpochSecond()) { //if the current (toPut) is more recent than the one in the map (toCompare) we replace it
					jobs.put(sourceUrl, toPut);
				}
			} 
			else
				jobs.put(sourceUrl, new PublishAnonymisedJob(cData));
		}
		/*here we have the list of the most recent jobs per type (GROW_OUT_INDIVIDUAL, PRE_ONGROWING etc) 
		 * with their STATUS (COMPLETE, FAILED, IN PROGRESS)
		 *
		 */
		_log.debug("*** List Processed, no of items = "+jobs.keySet().size());
		for (String key : jobs.keySet()) {
			System.out.println(jobs.get(key));
		}
		//		
		StorageHubClient shub = new StorageHubClient();		  
		for (ExternalFile externalFile : filteredFiles) {
			String publicURL = shub.open(externalFile.getId()).asFile().getPublicLink().toString();
			PublishAnonymisedJob job = jobs.get(publicURL);
			if (job != null) {
				long endTimeInMillis = job.getEndTimeEpochSecond()*1000;
				toReturn.add(new SubmittedForm(externalFile, job.getStatus(), job.getSubmitterIdentity(), endTimeInMillis));		
			} else
				toReturn.add(new SubmittedForm(externalFile));		
		}
		return toReturn;
	}

	public void displayVersions(ActionRequest request, ActionResponse response) throws Exception {
		System.out.println("**** displayVersionsdisplayVersionsdisplayVersionsdisplayVersions");
		String itemId = ParamUtil.getString(request, "fileItem");
		long farmId = ParamUtil.getLong(request, "farmId");

		if (itemId == null || itemId.compareTo("")==0) {
			SessionErrors.add(request.getPortletSession(),"form-error");
			return;
		}
		HttpServletRequest httpReq = PortalUtil.getHttpServletRequest(request);
		GCubeUser currentUser = Utils.getCurrentUser(httpReq);
		String context = Utils.getCurrentContext(httpReq);
		Workspace ws = Utils.getWS(currentUser.getUsername(), context) ;
		WorkspaceItem theItem = ws.getItem(itemId);
		if (theItem instanceof ExternalFile) {
			ExternalFile file = (ExternalFile) theItem;	
			List<WorkspaceVersion> versions = file.getVersionHistory();
			request.setAttribute("versions", versions); //pass to the JSP
		} else { 
			response.setRenderParameter("jspPage", PFISHConstants.OPERATION_ERROR_PATH);
		}
		request.setAttribute("farmId", farmId);
		request.setAttribute("itemId", itemId);
		request.setAttribute("itemName", theItem.getName());
		response.setWindowState(WindowState.MAXIMIZED);
		response.setRenderParameter("jspPage", "/html/farmrepository/show_all_versions.jsp");
	}


	public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws IOException, PortletException {
		//if download file
		String fileToDownloadId = ParamUtil.getString(resourceRequest, "fileToDownloadId", null);
		//if download version
		String versionDownloadItemId = ParamUtil.getString(resourceRequest, "versionDownloadItemId", null);
		String versionDownloadName = ParamUtil.getString(resourceRequest, "versionDownloadName", null);
		
		String fileToPublishId = ParamUtil.getString(resourceRequest, "fileToPublishId", null);
		String farmIdString = ParamUtil.getString(resourceRequest, "farmId", null);

		String authToken = setAuthorizationToken(resourceRequest);
		//check if is a file download
		if (fileToDownloadId != null) { 	
			String selectedItemId =  fileToDownloadId;
			try {
				ItemManagerClient client = AbstractPlugin.item().build();
				StreamDescriptor streamDescr = client.download(selectedItemId);
				HttpServletResponse httpRes = PortalUtil.getHttpServletResponse(resourceResponse);
				HttpServletRequest httpReq = PortalUtil.getHttpServletRequest(resourceRequest);
				ServletResponseUtil.sendFile(httpReq,httpRes, streamDescr.getFileName(), streamDescr.getStream(), "application/download");
				streamDescr.getStream().close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (versionDownloadItemId != null && versionDownloadName != null) {	//check if download version
			GCubeUser currentUser = Utils.getCurrentUser(resourceRequest);
			String context = Utils.getCurrentContext(resourceRequest);
			Workspace ws = Utils.getWS(currentUser.getUsername(), context) ;
			WorkspaceItem theItem;
			try {
				theItem = ws.getItem(versionDownloadItemId);
				if (theItem instanceof ExternalFile) {
					ExternalFile file = (ExternalFile) theItem;	
					InputStream is = file.downloadVersion(versionDownloadName);
					HttpServletResponse httpRes = PortalUtil.getHttpServletResponse(resourceResponse);
					HttpServletRequest httpReq = PortalUtil.getHttpServletRequest(resourceRequest);
					ServletResponseUtil.sendFile(httpReq,httpRes, versionDownloadName+"_"+file.getName(), is, "application/download");
				} 
			} catch (ItemNotFoundException | InternalErrorException e) {
				e.printStackTrace();
			}
		}
		else if (fileToPublishId != null && farmIdString != null) {	//check if is publish and anonimyse
			long farmId = Long.parseLong(farmIdString);
			try {
				ItemManagerClient client = AbstractPlugin.item().build();
				Item selectedForm = client.get(fileToPublishId, "hl:accounting");

				String publicLink = client.getPublickLink(fileToPublishId).toString();
				List<Version> versions = client.getFileVersions(fileToPublishId);
				String theVersion = "1.0";
				for (Version version : versions) {
					if (version.isCurrent())
						theVersion = version.getName();
				}
				_log.debug("Publish and Anonymise " + selectedForm.getName());
				String batchType = Utils.getBatchTypeName(Utils.getPhaseByFileName(selectedForm.getName()), selectedForm.getName());

				String result = submitPublishAndAnonymise(resourceRequest, farmId, publicLink, theVersion, batchType, authToken);
				JSONObject fileObject = JSONFactoryUtil.createJSONObject();
				fileObject.put("success", result);
				resourceResponse.getWriter().println(fileObject);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			String batchType = ParamUtil.getString(resourceRequest, "batchType", null);
			String farmIdStr = ParamUtil.getString(resourceRequest, "farmId", null);
			String context = Utils.getCurrentContext(resourceRequest);
			long farmId = Long.parseLong(farmIdStr);
			boolean analyisisEnabled = false;
			try {
				analyisisEnabled = ImportedBatchChecker.checkAnalysisAvailability(farmId, batchType, context);
			}
			catch (Exception e) {
				JSONObject fileObject = JSONFactoryUtil.createJSONObject();
				fileObject.put("success", "NOK");
				fileObject.put("message", " There was an error on the server, please report this issue");
				resourceResponse.getWriter().println(fileObject);
			}
			if (analyisisEnabled) {
				JSONObject fileObject = JSONFactoryUtil.createJSONObject();
				fileObject.put("success", "OK");
				resourceResponse.getWriter().println(fileObject);
			} else {
				JSONObject fileObject = JSONFactoryUtil.createJSONObject();
				fileObject.put("success", "NOK");
				fileObject.put("message", " Low number of batches");
				resourceResponse.getWriter().println(fileObject);
			}
			
		}

	}

	/**
	 * set the authorization token in Thread local and return the current {@link AuthorizedUser} instance
	 * @param request
	 * @return
	 */
	private String setAuthorizationToken(ResourceRequest request) {
		GCubeUser currentUser = Utils.getCurrentUser(request);
		String scope = Utils.getCurrentContext(request);
		String authorizationToken = Utils.getCurrentUserToken(scope,  currentUser.getUsername());
		SecurityTokenProvider.instance.set(authorizationToken);
		return authorizationToken;
	}

	private static String submitPublishAndAnonymise(ResourceRequest request, long farmId, String publicLink, String version, String batchType, String token)  {
		String encodedURL = publicLink;
		try {
			encodedURL = URLEncoder.encode(publicLink, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		_log.debug("farmId " + farmId);
		_log.debug("item URL encoded " + encodedURL);
		_log.debug("version " + version);
		_log.debug("batchType " + batchType);
		Map<String, String> params = new HashMap<>();
		params.put("batch_type", batchType);
		params.put("farmid", ""+farmId);
		params.put("source", encodedURL);
		params.put("source_version", version);
		params.put("gcube-token", token);

		String urlParameters  = "batch_type="+batchType+"&farmid="+farmId+"&source="+encodedURL+"&source_version="+version+"&gcube-token="+token;

		String context = Utils.getCurrentContext(request);
		String analyticalToolkitServiceURL = Utils.getAnalyticalToolkitEndpoint(context) ;

		return doPOSTRequest(analyticalToolkitServiceURL, params, urlParameters);
	}

	private static String doPOSTRequest(String analyticalToolkitServiceURL, Map<String, String> params, String urlParameters)  {

		StringBuilder sb = new StringBuilder(analyticalToolkitServiceURL)
				.append("/import?")
				.append(urlParameters);
		String request = sb.toString();
		_log.info("Performing POST to: " + request);
		int status = -1;
		try {
			fixUntrustCertificate() ;
			URL url= new URL(request);
			HttpsURLConnection conn= (HttpsURLConnection) url.openConnection();           
			conn.setDoOutput( true );
			conn.setInstanceFollowRedirects( false );
			conn.setRequestMethod( "POST" );
			conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
			conn.setRequestProperty( "charset", "utf-8");
			conn.setUseCaches( false );
			System.out.println("Posted with code "+ conn.getResponseCode() +" Response: " + conn.getResponseMessage());
			System.out.println("Parameters:" + params.toString());
			//responseMessage = conn.getResponseMessage();
			status = conn.getResponseCode();

			InputStream in = conn.getInputStream();
			String encoding = conn.getContentEncoding();
			encoding = encoding == null ? "UTF-8" : encoding;
			String body = IOUtils.toString(in, encoding);
			System.out.println("Response Body:");
			System.out.println(body);

			if (status >= 200 && status <=  300) {
				return "OK";
			}		
			else
				return "KO";
		}
		catch (Exception e) {
			e.printStackTrace();		
			return "KO";
		}
	}

	

	private static void fixUntrustCertificate() throws KeyManagementException, NoSuchAlgorithmException{
		TrustManager[] trustAllCerts = new TrustManager[]{
				new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					@Override
					public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
							throws CertificateException {
					}

					@Override
					public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
							throws CertificateException {			
					}

				}
		};

		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		// set the  allTrusting verifier
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	}


}