package org.gcube.portlets.user.performfish;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.portlets.user.performfish.util.Utils;
import org.gcube.portlets.user.performfish.util.ValidationResult;
import org.gcube.portlets.user.performfish.util.db.DBUtil;
import org.gcube.portlets.user.performfish.util.db.DatabaseConnection;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.model.GCubeTeam;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.service.TeamLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class SubmittedFormValidation
 */
public class SubmittedFormValidation extends MVCPortlet {
	private static com.liferay.portal.kernel.log.Log _log = LogFactoryUtil.getLog(SubmittedFormValidation.class);

	private final static String XSLX_MIME = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	private static final int RETRY_NO = 3;
	private static String XML_RESULT_ROOT_EXCEPTION = "ExceptionReport";
	private static final String USER_AGENT = "Mozilla/5.0";

	public static final String VALIDATOR_METHOD_ID = "org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.PERFORMFISH_DATA_VALIDATOR_V2";

	public void render(RenderRequest request, RenderResponse response) {
		GCubeTeam theCompany;
		try {
			HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));
			String[] farmIds = ParamUtil.getParameterValues(httpReq, Utils.ENCODED_FARM_PARAM);
		
			GCubeTeam theFarm = null;
			theCompany = Utils.checkBelongingToOneCompanyOnly(request, response, this);
			if (farmIds == null || farmIds.length == 0) {
				System.out.println("farmIds == null || farmIds.length == 0");
				if (Utils.getUserFarmsNumber(request, response, this) < 2) {
					theFarm = Utils.checkBelongingToOneFarmOnly(request, response, this);
					request.setAttribute("theFarm", theFarm); //pass to the JSP
				}					
			}
			else { //the farmId is passed via param on the query string
				long selectedFarmId = Utils.unmaskId(farmIds[0]);
				theFarm = new LiferayRoleManager().getTeam(selectedFarmId);
				if (Utils.checkBelongsToTeam(PortalUtil.getUserId(request), theFarm.getTeamId(), PortalUtil.getScopeGroupId(request)) ) {//check that the user belong ot the farm
					request.setAttribute("theFarm", theFarm); //pass to the JSP
				}
				else {
					PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(Utils.NOT_BELONGING_ANY_FARM_PAGE_PATH);
					dispatcher.include(request, response);	
				}					
			}
			if (theFarm != null) {
				request.setAttribute("theCompany", TeamLocalServiceUtil.getTeam(theCompany.getTeamId()));
				request.setAttribute("theFarm", theFarm);
				super.render(request, response);
			} else {
				_log.warn("theFarm is null");
				super.render(request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(Utils.NOT_BELONGING_ANY_FARM_PAGE_PATH);
			try {
				dispatcher.include(request, response);
			} catch (PortletException | IOException e1) {
				e1.printStackTrace();
			}	
		}
	}


	public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws IOException, PortletException {
		long companyId = ParamUtil.getLong(resourceRequest, "companyId");
		long farmId = ParamUtil.getLong(resourceRequest, "farmId");
		long userId = ParamUtil.getLong(resourceRequest, "userId");
		long groupId = ParamUtil.getLong(resourceRequest, "groupId");
		String encodedURI = ParamUtil.getString(resourceRequest, "encodedURI", null);
		String fileName = ParamUtil.getString(resourceRequest, "fileName", null);
		String username = Utils.getCurrentUser(userId).getUsername();
		String phase = ParamUtil.getString(resourceRequest, "phase", null);
		_log.debug("companyId: " + companyId);
		_log.debug("farmId: " + farmId);
		_log.debug("userId: " + userId);
		_log.debug("groupId: " + groupId);
		_log.debug("encodedURI: " + encodedURI);
		_log.debug("phase: " + phase);
		_log.debug("fileName: " + fileName);


		String inputFileLink = encodedURI;
		String template = Utils.getBatchTypeName(phase, fileName);
		String context = Utils.getCurrentContext(groupId);
		String token = Utils.getCurrentUserToken(context, username);

		_log.debug("context: " + context);
		_log.debug("token: " + token);
		_log.debug("template: " + template);

		ValidationResult result = doWPSCallToDataMiner(inputFileLink, context, token, template);
		_log.debug("RESULT:\n"+result);

		if (result.isSuccess()) {
			try {
				String decodedURL = new java.net.URI(inputFileLink).getPath();
				uploadToCompanyRepository(username, context, companyId, farmId, fileName,  new URL(decodedURL).openConnection().getInputStream());
				Connection conn = DatabaseConnection.getInstance(Utils.getCurrentContext(groupId)).getConnection();
				java.sql.Date lastActivity = new java.sql.Date(new Date().getTime());
				DBUtil.updateFarmLastSubmissionActivity(conn, farmId, lastActivity);
			} catch (Exception e) {
				result.setComment("The file is valid but we could not upload it in the company repository, please report this issue at support.d4science.org");
				e.printStackTrace();
			}
		}
		JSONObject fileObject = JSONFactoryUtil.createJSONObject();
		fileObject.put("success", result.isSuccess());
		fileObject.put("comment", result.getComment());
		resourceResponse.getWriter().println(fileObject);	
	}

	private String uploadToCompanyRepository(String username, String context, long companyId, long farmId, String fileName, InputStream fileData) throws Exception {
		GCubeTeam theCompany = new LiferayRoleManager().getTeam(companyId);
		GCubeTeam theFarm = new LiferayRoleManager().getTeam(farmId);
		Workspace ws = Utils.getWS(username, context);
		WorkspaceFolder companyRepoFolder = Utils.getWSFarmFolder(username, context, theCompany, theFarm);
		WorkspaceItem overwriteItem = ws.find(fileName, companyRepoFolder.getId());

		String toReturn = "";
		if (overwriteItem == null) {
			ExternalFile item = companyRepoFolder.createExternalFileItem(fileName, "User form submission on " + new Date(), XSLX_MIME, fileData);
			toReturn = item.getId();
		}
		else {
			ws.updateItem(overwriteItem.getId(), fileData);
			toReturn = overwriteItem.getId();
			_log.debug("updateItem with id: "+overwriteItem.getId()+ ", is completed");
		}
		_log.info("Uploaded " + fileName + " - Returned Workspace id=" +toReturn);

		return toReturn;
	}

	/**
	 * performs tha actual call to the service
	 * @param peer session 
	 * @param entry authEntry	
	 * @param token token 
	 * @param username username
	 * @param inputText
	 * @param options
	 * @return
	 */
	private ValidationResult doWPSCallToDataMiner(String inputFileLink, String context, String token, String template) {
		_log.info("doWPSCallToDataMiner");
		ValidationResult toReturn = null;
		List<ServiceEndpoint> dms = null;
		try {
			dms = Utils.getDataMinerInstance(context);
			if (dms == null || dms.isEmpty()) {
				return new ValidationResult(false, "Error, no DataMiner cluster in this VRE,  please report this issue at www.d4science.org/contact-us");
			}				
			ServiceEndpoint se = dms.get(0);
			ServiceEndpoint.AccessPoint ap = se.profile().accessPoints().asCollection().iterator().next();
			_log.info("got DataMiner instance="+ap.address());

			String apAddress = ap.address(); 
			_log.info("inputFileLink at: " + inputFileLink);
			String wpsParams = getWPSCallURLParameters(token, inputFileLink, template);
			String responseXML = sendGet(apAddress, wpsParams, token);
			String resultFileURL = parseResult(responseXML);
			_log.info("PARSED OK resultFileURL: " + resultFileURL);

			InputStream is = new URL(resultFileURL).openConnection().getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line;
			List<String> resultLines = new ArrayList<>();
			while ((line = reader.readLine()) != null)	{
				if (line.split("=").length > 1) 				
 					resultLines.add(line.substring(line.indexOf("=")+1));
				else
					resultLines.add("Validation is successful");
			}
			reader.close();
			for (int i = 0; i < resultLines.size(); i++) {
				_log.debug("Line " + i + ": " + resultLines.get(i));
			}
			toReturn = new ValidationResult(resultLines.get(0).equalsIgnoreCase("OK"), resultLines.get(1));
		} catch (Exception e) {
			e.printStackTrace();
			return new ValidationResult(false,  "There was a problem contacting the DataMiner cluster in this VRE (" 
					+ context+ ")" 
					+ "), please report this issue at http://support.d4science.org");
		}
		return toReturn;
	}



	private String parseResult(String xml) throws Exception {
		String elem = removeXmlStringNamespaceAndPreamble(xml);
		DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Node node = docBuilder.parse(new InputSource(new StringReader(elem))).getDocumentElement();
		String rootElement =  node.getNodeName();
		XPathHelper helper = new XPathHelper(node);
		List<String> currValue = null;
		if (rootElement.compareTo(XML_RESULT_ROOT_EXCEPTION) == 0) {
			currValue = helper.evaluate("/ExceptionReport");
			if (currValue != null && currValue.size() > 0) 
				return currValue.get(0);
		}
		else {
			currValue = helper.evaluate("//ProcessOutputs/Output/Data/ComplexData/FeatureCollection/featureMember/Result/Data/text()");
			if (currValue != null && currValue.size() > 1) 
				return currValue.get(1);
		}
		return null;
	}

	//this remove all the namespaces causing parsing errors as i don't have time to deal with this.
	private static String removeXmlStringNamespaceAndPreamble(String xmlString) {
		return xmlString.replaceAll("(<\\?[^<]*\\?>)?", "") /* remove preamble */
				.replaceAll("xmlns.*?(\"|\').*?(\"|\')", "") /* remove xmlns declaration */
				.replaceAll("(<)(\\w+:)(.*?>)", "$1$3") /* remove opening tag prefix */
				.replaceAll("(</)(\\w+:)(.*?>)", "$1$3")
				.replaceAll("wps:", "")
				.replaceAll("xsi:", "")
				.replaceAll("ogr:",""); /* remove closing tags prefix */
	}

	// HTTP  request
	private String sendGet(String url, String urlParameters, String token) {
		StringBuilder toReturn = new StringBuilder();
		try {
			int i = 1;
			while (i <= RETRY_NO) {
				CloseableHttpClient client = HttpClients.createDefault();
				HttpGet request = new HttpGet(url+"?"+urlParameters);
				_log.info("request to="+request.toString());
				// add header
				request.setHeader("User-Agent", USER_AGENT);
				request.setHeader("Accept-Encoding","UTF-8");
				request.setHeader("Accept-Charset","UTF-8");
				request.setHeader("Content-Encoding","UTF-8");

				HttpResponse response = client.execute(request);

				// Get the response
				BufferedReader rd = new BufferedReader
						(new InputStreamReader(
								response.getEntity().getContent()));

				String line = "";
				while ((line = rd.readLine()) != null) {
					toReturn.append(line);
				}
				i++;
				if (toReturn.toString().compareTo("") == 0 || toReturn.toString().startsWith("Error")) {
					_log.warn("response from Dataminer is empty or an error occurred, retry tentative: " + i + " of " + RETRY_NO);
					_log.error("here is the faulty response from Dataminer="+toReturn.toString());
				} else {
					_log.debug("response from Dataminer="+toReturn.toString());
					break;
				}
			} 
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return toReturn.toString();
	}
	/**
	 * 
	 * @param token the token
	 * @param inputFileLink the http  of the input file
	 * @param options 
	 * @return the url paramters for WPS Call to perform
	 */
	private String getWPSCallURLParameters(String token, String inputFileLink, String template) {
		StringBuilder sb = new StringBuilder("request=Execute&service=WPS&Version=1.0.0&gcube-token=")
				.append(token)
				.append("&lang=en-US&Identifier=")
				.append(VALIDATOR_METHOD_ID)
				.append("&DataInputs=CompanyData=")
				.append(inputFileLink)
				.append(";Template=")
				.append(template)
				.append(";")
				.append("Overwrite=false;LatestCompanyData=")
				.append(inputFileLink)
				.append(";");
		return sb.toString();
	}


	
}
