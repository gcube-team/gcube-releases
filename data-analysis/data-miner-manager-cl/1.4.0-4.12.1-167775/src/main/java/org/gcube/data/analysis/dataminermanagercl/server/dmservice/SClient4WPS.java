package org.gcube.data.analysis.dataminermanagercl.server.dmservice;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.opengis.ows.x11.OperationDocument.Operation;
import net.opengis.wps.x100.CapabilitiesDocument;
import net.opengis.wps.x100.ComplexDataType;
import net.opengis.wps.x100.ExecuteDocument;
import net.opengis.wps.x100.ExecuteResponseDocument;
import net.opengis.wps.x100.ExecuteResponseDocument.ExecuteResponse.ProcessOutputs;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.InputType;
import net.opengis.wps.x100.OutputDataType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.ProcessBriefType;
import net.opengis.wps.x100.ProcessDescriptionType;
import net.opengis.wps.x100.ResponseDocumentType;
import net.opengis.wps.x100.StatusType;
import net.opengis.wps.x100.SupportedComplexDataInputType;
import net.opengis.wps.x100.impl.ExecuteResponseDocumentImpl;

import org.apache.xmlbeans.XmlString;
import org.gcube.data.analysis.dataminermanagercl.server.dmservice.wps.ProcessInformations;
import org.gcube.data.analysis.dataminermanagercl.server.dmservice.wps.ResponseWPS;
import org.gcube.data.analysis.dataminermanagercl.server.dmservice.wps.SClient4WPSSession;
import org.gcube.data.analysis.dataminermanagercl.server.dmservice.wps.WPS2DM;
import org.gcube.data.analysis.dataminermanagercl.server.dmservice.wps.computationsvalue.ComputationValueBuilder;
import org.gcube.data.analysis.dataminermanagercl.server.is.InformationSystemUtils;
import org.gcube.data.analysis.dataminermanagercl.server.storage.StorageUtil;
import org.gcube.data.analysis.dataminermanagercl.server.util.ServiceCredentials;
import org.gcube.data.analysis.dataminermanagercl.shared.Constants;
import org.gcube.data.analysis.dataminermanagercl.shared.data.OutputData;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationData;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationItemPropertiesValue;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationValue;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.FileResource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.ImageResource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.MapResource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.ObjectResource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.Resource;
import org.gcube.data.analysis.dataminermanagercl.shared.exception.ServiceException;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.FileParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.ObjectParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.TabularListParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.perspective.PerspectiveType;
import org.gcube.data.analysis.dataminermanagercl.shared.process.ComputationStatus;
import org.gcube.data.analysis.dataminermanagercl.shared.process.ComputationStatus.Status;
import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;
import org.gcube.data.analysis.dataminermanagercl.shared.process.OperatorCategory;
import org.gcube.data.analysis.dataminermanagercl.shared.process.OperatorsClassification;
import org.n52.wps.client.ExecuteRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Client 4 WPS Service
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class SClient4WPS extends SClient {

	private static final long serialVersionUID = 1909871837115147159L;
	private static Logger logger = LoggerFactory.getLogger(SClient4WPS.class);
	// private static final int OPERATOR_BRIEF_DESCRIPTION_MAX_LENGHT = 170;
	private static final String OTHERS = "OTHERS";

	private String wpsToken;
	private String wpsUser;
	// private String wpsServiceURL;
	private String wpsProcessingServlet;
	private String wpsCancelComputationServlet;

	private ProcessDescriptionType[] processesDescriptionType;
	private ProcessBriefType[] processesBriefs;

	private HashMap<String, ProcessInformations> process;
	private HashMap<ComputationId, ProcessInformations> runningProcess;

	private SClient4WPSSession wpsClient;

	/**
	 * 
	 * @param serviceCredentials
	 *            sevice credentials
	 * @throws ServiceException
	 *             ServiceException
	 */
	public SClient4WPS(ServiceCredentials serviceCredentials) throws ServiceException {
		super();
		process = new HashMap<>();
		runningProcess = new HashMap<>();
		if (serviceCredentials == null) {
			logger.error("Error credetials are null!");
			throw new ServiceException("Error credetials are null!");
		} else {
			String token = serviceCredentials.getToken();
			if (token == null || token.isEmpty()) {
				logger.error("Error authorization token invalid: " + token);
				throw new ServiceException("Error authorization token invalid: " + token);
			} else {
				wpsToken = token;
			}

			String userName = serviceCredentials.getUserName();
			if (userName == null || userName.isEmpty()) {
				logger.error("Error invalid user name: " + userName);
				throw new ServiceException("Error invalid user name: " + userName);
			} else {
				wpsUser = userName;
			}
			List<String> serviceAddress;
			try {
				serviceAddress = InformationSystemUtils.retrieveServiceAddress(Constants.DATAMINER_SERVICE_CATEGORY,
						Constants.DATA_MINER_SERVICE_NAME, serviceCredentials.getScope());
			} catch (Exception e) {
				logger.error("Error retrieving service address: " + e.getLocalizedMessage());
				e.printStackTrace();
				throw new ServiceException(e.getLocalizedMessage(), e);
			}
			logger.debug("Service Address retrieved:" + serviceAddress);
			if (serviceAddress == null || serviceAddress.size() < 1) {
				logger.error("No DataMiner service address available!");
				throw new ServiceException("No DataMiner service address available!");
			} else {
				logger.info("DataMiner service address found: " + serviceAddress.get(0));
				wpsProcessingServlet = serviceAddress.get(0);

				int wpsWebProcessingServiceIndex = wpsProcessingServlet.indexOf(Constants.WPSWebProcessingService);
				if (wpsWebProcessingServiceIndex > 0) {
					String wpsServiceUrl = wpsProcessingServlet.substring(0, wpsWebProcessingServiceIndex);
					wpsCancelComputationServlet = wpsServiceUrl + Constants.WPSCancelComputationServlet;
					logger.debug("Cancel computation servlet: " + wpsCancelComputationServlet);

				} else {
					logger.error("Cancel computation servlet not available!");
					throw new ServiceException("Cancel computation servlet not available!");
				}

			}

		}

	}

	/**
	 * 
	 * @param serviceCredentials
	 *            sevice credentials
	 * @param serviceAddressUrl
	 *            valid url for example:
	 *            <span>http://dataminer1-devnext.d4science.org/wps/</span>
	 * @throws ServiceException
	 *             ServiceException
	 */
	public SClient4WPS(ServiceCredentials serviceCredentials, String serviceAddressUrl) throws ServiceException {
		super();
		process = new HashMap<>();
		runningProcess = new HashMap<>();
		if (serviceCredentials == null) {
			logger.error("Error credetials are null!");
			throw new ServiceException("Error credetials are null!");
		} else {
			String token = serviceCredentials.getToken();
			if (token == null || token.isEmpty()) {
				logger.error("Error authorization token invalid: " + token);
				throw new ServiceException("Error authorization token invalid: " + token);
			} else {
				wpsToken = token;
			}

			String userName = serviceCredentials.getUserName();
			if (userName == null || userName.isEmpty()) {
				logger.error("Error invalid user name: " + userName);
				throw new ServiceException("Error invalid user name: " + userName);
			} else {
				wpsUser = userName;
			}

			logger.debug("Service Address:" + serviceAddressUrl);
			if (serviceAddressUrl == null || serviceAddressUrl.isEmpty()) {
				logger.error("Invalid DataMiner service address: " + serviceAddressUrl);
				throw new ServiceException("Invalid DataMiner service address: " + serviceAddressUrl);
			} else {
				logger.debug("DataMiner service address: " + serviceAddressUrl);
				// http://dataminer1-devnext.d4science.org/wps/WebProcessingService
				wpsProcessingServlet = serviceAddressUrl + Constants.WPSWebProcessingService;
				wpsCancelComputationServlet = serviceAddressUrl + Constants.WPSCancelComputationServlet;

			}

		}

	}

	private SClient4WPSSession createWPSClientSession() {
		if (wpsClient == null) {
			wpsClient = new SClient4WPSSession(wpsUser, wpsToken);
			logger.debug("Created StatWPSClientSession");
			return wpsClient;
		} else {
			logger.debug("Use already created StatWPSClientSession");
			return wpsClient;
		}
	}

	@Override
	public Operator getOperatorById(String id) throws Exception {
		if (id == null || id.isEmpty()) {
			String error = "Invalid operator request, id=" + id;
			logger.error(error);
			throw new ServiceException(error);
		}

		List<OperatorsClassification> operatorsClassifications = getOperatorsClassifications();

		if (operatorsClassifications != null && operatorsClassifications.size() > 0) {
			Operator operator = null;
			for (OperatorsClassification oc : operatorsClassifications) {
				for (Operator op : oc.getOperators()) {
					if (op.getId().compareToIgnoreCase(id) == 0) {
						operator = op;
						break;
					}

				}
				if (operator != null) {
					break;
				}

			}
			if (operator == null) {
				String error = "Operator not found (" + id + ")";
				logger.error(error);
				throw new ServiceException(error);
			} else {
				logger.debug("Operator Name: " + operator.getName() + " (" + operator.getId() + ")");
				logger.debug("Operator: " + operator);
				return operator;
			}

		} else {
			logger.debug("OperatorsClassification void");
			String error = "Operator not found (" + id + ")";
			logger.error(error);
			throw new ServiceException(error);
		}

	}

	@Override
	public List<OperatorsClassification> getOperatorsClassifications() throws ServiceException {
		return getOperatorsClassifications(false);
	}
	
	@Override
	public List<OperatorsClassification> getOperatorsClassifications(boolean refresh) throws ServiceException{
		logger.debug("getOperatorsClassifications: "+refresh);
		LinkedHashMap<String, String> operatorsToCategoriesMap = new LinkedHashMap<>();
		LinkedHashMap<String, List<Operator>> categoriesToOperatorsMap = new LinkedHashMap<>();
		
		if(refresh){
			logger.debug("Clear Processes Descriptions: Refresh");
			process.clear();
		}
		
		requestCapability(refresh);
		if (processesBriefs == null || processesDescriptionType == null) {
			throw new ServiceException("Algorithms WPS not available!");
		}

		for (ProcessBriefType processBrief : processesBriefs) {
			String categoryTitle = processBrief.getTitle().getStringValue();
			String categoryName;
			if (categoryTitle == null || categoryTitle.isEmpty()) {
				categoryName = OTHERS;
			} else {
				String[] categorySplitted = categoryTitle.split(":");
				if (categorySplitted.length < 1) {
					categoryName = OTHERS;
				} else {
					categoryName = categorySplitted[0];
				}
			}
			operatorsToCategoriesMap.put(processBrief.getIdentifier().getStringValue(), categoryName);
		}

		String briefDescription;
		for (ProcessBriefType processDescriptionType : processesDescriptionType) {
			briefDescription = processDescriptionType.getAbstract().getStringValue();

			String categoryName = operatorsToCategoriesMap.get(processDescriptionType.getIdentifier().getStringValue());
			if (categoryName == null || categoryName.isEmpty()) {
				categoryName = OTHERS;
			}

			List<Operator> listOperators = categoriesToOperatorsMap.get(categoryName);
			if (listOperators == null) {
				listOperators = new ArrayList<>();
			}

			listOperators.add(new Operator(processDescriptionType.getIdentifier().getStringValue(),
					processDescriptionType.getTitle().getStringValue(), briefDescription,
					processDescriptionType.getAbstract().getStringValue(), null));

			categoriesToOperatorsMap.put(categoryName, listOperators);

		}

		List<OperatorCategory> categories = new ArrayList<>();
		List<Operator> operators = new ArrayList<>();

		Comparator<OperatorCategory> categoriesComparator = new Comparator<OperatorCategory>() {
			public int compare(OperatorCategory c1, OperatorCategory c2) {
				return c1.getName().compareTo(c2.getName()); // use your logic
			}
		};

		Comparator<Operator> operatorsComparator = new Comparator<Operator>() {
			public int compare(Operator c1, Operator c2) {
				return c1.getName().compareTo(c2.getName()); // use your logic
			}
		};

		for (String categoryName : categoriesToOperatorsMap.keySet()) {
			OperatorCategory category = new OperatorCategory(categoryName, categoryName, categoryName);
			List<Operator> listOperators = categoriesToOperatorsMap.get(categoryName);
			for (Operator operator : listOperators) {
				operator.setCategory(category);
			}
			Collections.sort(listOperators, operatorsComparator);
			category.setOperators(listOperators);
			operators.addAll(listOperators);
			categories.add(category);
		}

		Collections.sort(operators, operatorsComparator);
		Collections.sort(categories, categoriesComparator);

		List<OperatorsClassification> operatorsClass = new ArrayList<>();

		OperatorsClassification op = new OperatorsClassification(PerspectiveType.User.getPerspective(), categories,
				operators);

		operatorsClass.add(op);

		logger.debug("OperatorClass: " + operatorsClass);
		return operatorsClass;
	}
	

	private ProcessInformations describeProcess(String processId) throws ServiceException {
		return describeProcess(processId, null);
	}

	private ProcessInformations describeProcess(String processId, URL processDescriptionURL) throws ServiceException {
		if (process.containsKey(processId)) {
			return process.get(processId);
		}

		SClient4WPSSession wpsClient = null;
		try {
			wpsClient = createWPSClientSession();

			logger.debug("Describe Process WPS URL: " + wpsProcessingServlet);
			ProcessDescriptionType processDescription = null;
			for (int k = 0; k <= 3; k++) {
				try {
					processDescription = wpsClient.getProcessDescription(wpsProcessingServlet, processId);
				} catch (Throwable e) {
					logger.error("Error getProcessDescription for process " + processId + " with WPS URL: "
							+ wpsProcessingServlet);
					if (k == 3)
						throw e;
				}
				if (processDescription != null)
					break;
			}

			ProcessInformations processInformations = new ProcessInformations(processDescription);

			if (processDescriptionURL != null)
				processDescription.set(XmlString.Factory.parse(processDescriptionURL));

			logger.debug("ProcessDescription: " + processDescription);

			InputDescriptionType[] inputList = processDescription.getDataInputs().getInputArray();
			logger.debug("WPSClient->Fetching Inputs");
			for (InputDescriptionType input : inputList) {
				logger.debug("WPSClient->Input: " + input);
			}

			OutputDescriptionType[] outputList = processDescription.getProcessOutputs().getOutputArray();
			logger.debug("WPSClient->Fetching Outputs");
			for (OutputDescriptionType output : outputList) {
				logger.debug("WPSClient->Output: " + output);
			}

			processInformations.setInputs(inputList);
			processInformations.setOutputs(outputList);

			process.put(processId, processInformations);

			return processInformations;
		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage(),e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		} finally {
			wpsClient.disconnect(wpsProcessingServlet);
		}
	}

	private void requestCapability(boolean refresh) throws ServiceException {
		SClient4WPSSession wpsClient = null;
		processesDescriptionType = null;
		processesBriefs = null;
		
		logger.debug("Request Capability: refresh="+refresh);
		
		try {
			wpsClient = createWPSClientSession();

			wpsClient.connect(wpsProcessingServlet,refresh);

			processesDescriptionType = wpsClient.getAllProcessDescriptions(wpsProcessingServlet);

			CapabilitiesDocument capabilitiesDocument = wpsClient.getWPSCaps(wpsProcessingServlet);

			Operation[] operations = capabilitiesDocument.getCapabilities().getOperationsMetadata().getOperationArray();
			for (Operation operation : operations) {
				operation.getDCPArray()[0].getHTTP().getPostArray()[0].setHref(wpsProcessingServlet + "?");

			}

			processesBriefs = capabilitiesDocument.getCapabilities().getProcessOfferings().getProcessArray();
			return;

		} catch (Throwable e) {
			logger.error("RequestCapability(): " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		} finally {
			if (wpsClient != null) {
				wpsClient.disconnect(wpsProcessingServlet);
			}
		}

	}

	private String executeProcessAsync(ExecuteRequestBuilder executeBuilder, ProcessDescriptionType processDescription)
			throws ServiceException {
		SClient4WPSSession wpsClient = null;
		try {
			try {

				OutputDescriptionType[] odts = processDescription.getProcessOutputs().getOutputArray();
				for (OutputDescriptionType odt : odts) {
					// executeBuilder.setMimeTypeForOutput("text/xml",
					// "result");
					if (odt.isSetComplexOutput())
						executeBuilder.setMimeTypeForOutput("text/xml", odt.getIdentifier().getStringValue());
				}
			} catch (Exception e) {
				logger.debug("Execute Process-> Warning, no xml structured objects will be provided: "
						+ e.getLocalizedMessage());
				e.printStackTrace();
			}
			// executeBuilder.setSchemaForOutput("http://schemas.opengis.net/gml/3.1.1/base/feature.xsd",
			// "result");

			ExecuteDocument execute = executeBuilder.getExecute();
			execute.getExecute().setService("WPS");
			// System.out.println("RESPONSE
			// FORM:"+execute.getExecute().getResponseForm());
			wpsClient = createWPSClientSession();
			wpsClient.connect(wpsProcessingServlet);
			logger.debug("Sending: " + execute);
			if (execute.getExecute().getResponseForm() != null) {
				ResponseDocumentType documentType = execute.getExecute().getResponseForm().getResponseDocument();
				documentType.setStoreExecuteResponse(true);
				documentType.setStatus(true);
				documentType.setLineage(false);
				execute.getExecute().getResponseForm().setResponseDocument(documentType);
			}
			Object responseObject = wpsClient.execute(wpsProcessingServlet, execute);
			String processLocation = null;
			Date creationData = null;
			if (responseObject != null) {
				if (responseObject instanceof ExecuteResponseDocumentImpl) {
					ExecuteResponseDocumentImpl executeResponseDocumentImpl = ((ExecuteResponseDocumentImpl) responseObject);
					processLocation = executeResponseDocumentImpl.getExecuteResponse().getStatusLocation();
					creationData = executeResponseDocumentImpl.getExecuteResponse().getStatus().getCreationTime()
							.getTime();
				} else {
					throw new ServiceException("Invalid response from service, "
							+ "response isn't instance of ExecuteResponseDocumentImpl, class is "
							+ responseObject.getClass());
				}
			} else {
				throw new ServiceException("Invalid Response from service, " + responseObject);
			}
			logger.debug("Retrieved: [ProcessLocation=" + processLocation + ", CreationDate=" + creationData + "]");
			return processLocation;

		} catch (Throwable e) {
			logger.error("ExecuteProcessAsync: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		} finally {
			try {
				if (wpsClient != null)
					wpsClient.disconnect(wpsProcessingServlet);
			} catch (Exception e) {
				logger.debug("Problems in wps disconnect! " + e.getLocalizedMessage());
			}
		}
	}

	private ProcessOutputs retrieveProcessResult(String processLocation) throws ServiceException {
		SClient4WPSSession wpsClient = null;
		try {
			logger.debug("RetrieveProcessResult: " + processLocation);
			wpsClient = createWPSClientSession();
			// wpsClient.connectForMonitoring(webProcessingService);
			// wpsClient.connect(url)

			Object responseObject = null;
			if (processLocation != null && processLocation.length() > 0)
				responseObject = wpsClient.executeViaGET(processLocation, "");
			else
				throw new ServiceException("Process Location is null!");

			logger.debug("Response:\n" + responseObject);
			return ((ExecuteResponseDocument) responseObject).getExecuteResponse().getProcessOutputs();

		} catch (Throwable e) {
			logger.debug("RetrieveProcessResult: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		} finally {
			wpsClient.disconnect(wpsProcessingServlet);
		}
	}

	private static int calculateBBDimensions(String bbstring) {
		String[] bbinput = bbstring.split(",");
		int dimcounter = 0;
		try {
			for (int i = 0; i < bbinput.length; i++) {
				Double.parseDouble(bbinput[i]);
				dimcounter++;
			}
		} catch (Exception e) {
			logger.debug("Dimensions Count: " + dimcounter);
		}
		return dimcounter;
	}

	private static void addBoundingBoxInput(org.n52.wps.client.ExecuteRequestBuilder executeBuilder, String identifier,
			String BBstring) {

		ExecuteDocument executor = executeBuilder.getExecute();
		InputType input1 = executor.getExecute().getDataInputs().addNewInput();
		input1.addNewIdentifier().setStringValue(identifier);

		net.opengis.ows.x11.BoundingBoxType bbtype = input1.addNewData().addNewBoundingBoxData();

		// bboxInput=46,102,47,103,urn:ogc:def:crs:EPSG:6.6:4326,2
		String[] bbinput = BBstring.split(",");
		int dimensions = calculateBBDimensions(BBstring);
		List<String> lc = new ArrayList<String>();
		for (int i = 0; i < dimensions / 2; i++) {
			lc.add(bbinput[i]);
		}
		List<String> uc = new ArrayList<String>();
		for (int i = dimensions / 2; i < dimensions; i++) {
			uc.add(bbinput[i]);
		}

		bbtype.setLowerCorner(lc);
		bbtype.setUpperCorner(uc);

		// int crsidx = bbinput[dimensions].indexOf("crs:");
		String crs = bbinput[dimensions];
		/*
		 * if (crsidx>=0) crs = bbinput[dimensions].substring(crsidx+4);
		 */
		bbtype.setCrs(crs);
		bbtype.setDimensions(new BigInteger("" + dimensions / 2));

	}

	private static LinkedHashMap<String, ResponseWPS> retrieveURLsFromWPSResponse(ComplexDataType cdt) {
		org.w3c.dom.Node node = cdt.getDomNode();
		LinkedHashMap<String, ResponseWPS> urls = new LinkedHashMap<>();
		getURLFromXML(node, urls);
		for (String key : urls.keySet()) {
			logger.debug("ResponseWPS Map: " + key + "-->" + urls.get(key));
		}
		return urls;
	}

	/*
	 * private static String getExceptionText(String exceptionText) { try {
	 * String excText = "ExceptionText>"; int idx =
	 * exceptionText.indexOf(excText); if (idx >= 0) { String exception =
	 * exceptionText.substring(idx + excText.length()); exception =
	 * exception.substring(0, exception.indexOf("</")); exception = exception
	 * .replace("<", "") .replace(">", "") .replace("/", " ") .replace("\\",
	 * " ") .replaceAll( "[\\]\\[!\"#$%&'\\(\\)*+/:;<=>?@\\^_`{\\|}~-]",
	 * "_").trim(); exception = exception.replaceAll("[ ]+", " "); if
	 * (exception.length() > 200) exception = exception.substring(0, 200) +
	 * "..."; return exception; } else return "Process error in WPS Execution";
	 * } catch (Exception e) { e.printStackTrace(); return
	 * "Backend error in WPS Execution"; } }
	 */

	private static void getURLFromXML(org.w3c.dom.Node node, ResponseWPS responseWPS) {
		if (node == null)
			return;

		logger.debug("Node Name: " + node.getNodeName());
		if (node.getNodeName() == null) {
			return;
		}

		if (node.getFirstChild() != null) {
			logger.debug("Node Value: " + node.getFirstChild().getNodeValue());
		} else {
			logger.debug("FirstChild is NULL");
			return;
		}

		String text;
		switch (node.getNodeName()) {
		case "d4science:Data":
			text = node.getFirstChild().getNodeValue();
			responseWPS.setData(text);
			break;
		case "d4science:Description":
			text = node.getFirstChild().getNodeValue();
			responseWPS.setDescription(text);
			break;
		case "d4science:MimeType":
			text = node.getFirstChild().getNodeValue();
			responseWPS.setMimeType(text);
			break;
		default:
			break;
		}
		/* logger.debug("ResponseWPS:"+responseWPS); */

	}

	private static void getURLFromXML(org.w3c.dom.Node node, LinkedHashMap<String, ResponseWPS> urls) {
		if (node == null)
			return;

		logger.debug("Node Name: " + node.getNodeName());

		if (node.getNodeName() == null) {
			return;
		}

		ResponseWPS responseWPS = null;
		NodeList listnodes = node.getChildNodes();
		int nChildren = listnodes.getLength();

		switch (node.getNodeName()) {
		case "ogr:Result":
			NamedNodeMap attrs = node.getAttributes();
			Node n = attrs.getNamedItem("fid");
			String key = n.getNodeValue();
			responseWPS = new ResponseWPS();
			urls.put(key, responseWPS);
			if (nChildren == 0) {

			} else {
				for (int i = 0; i < nChildren; i++) {
					getURLFromXML(listnodes.item(i), responseWPS);

				}
			}
			break;
		default:
			if (nChildren == 0) {

			} else {
				for (int i = 0; i < nChildren; i++) {
					getURLFromXML(listnodes.item(i), urls);

				}
			}
			break;

		}

		/*
		 * 
		 * NodeList listnodes = node.getChildNodes(); int nChildren =
		 * listnodes.getLength();
		 * 
		 * if (nChildren == 0) { String text = node.getNodeValue(); if (text !=
		 * null && (text.startsWith("https:") || text.startsWith("http:") ||
		 * text.startsWith("ftp:") || text.startsWith("smp:") || text
		 * .startsWith("file:"))) urls.add(text.trim()); else if (text != null
		 * && text.trim().length() > 0) urls.add(text.trim()); } else { for (int
		 * i = 0; i < nChildren; i++) { List<String> childrenurls =
		 * getURLFromXML(listnodes.item(i)); urls.addAll(childrenurls);
		 * 
		 * } } return urls;
		 */
	}

	// TODO
	@Override
	public List<Parameter> getInputParameters(Operator operator) throws ServiceException {
		try {
			logger.debug("Input Parameters of algorithm " + operator.getId());

			ProcessInformations processInformations;
			try {
				processInformations = describeProcess(operator.getId());
			} catch (Throwable e) {
				logger.error("GetParameters: " + e.getLocalizedMessage());
				e.printStackTrace();
				throw new ServiceException(e.getLocalizedMessage());
			}

			logger.debug("ProcessInformation: " + processInformations);

			List<Parameter> parameters = new ArrayList<>();

			Parameter inputParameter;
			for (InputDescriptionType inputDesc : processInformations.getInputs()) {
				inputParameter = WPS2DM.convert2DMType(inputDesc);
				logger.debug("InputParameter: " + inputParameter);
				parameters.add(inputParameter);
			}

			logger.debug("Parameters: " + parameters);
			return parameters;

		} catch (Throwable e) {
			logger.error("Error in getInputParameters: " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage());
		}

	}

	@Override
	public List<Parameter> getOutputParameters(Operator operator) throws Exception {
		try {
			logger.debug("Output Parameters of algorithm " + operator.getId());

			ProcessInformations processInformations;
			try {
				processInformations = describeProcess(operator.getId());
			} catch (Throwable e) {
				logger.error("GetParameters: " + e.getLocalizedMessage());
				e.printStackTrace();
				throw new ServiceException(e.getLocalizedMessage());
			}

			logger.debug("ProcessInformation: " + processInformations);

			List<Parameter> parameters = new ArrayList<>();

			Parameter outputParameter;
			for (OutputDescriptionType outputDesc : processInformations.getOutputs()) {
				outputParameter = WPS2DM.convert2DMType(outputDesc);
				logger.debug("OutputParameter: " + outputParameter);
				parameters.add(outputParameter);
			}

			logger.debug("Parameters: " + parameters);
			return parameters;

		} catch (Throwable e) {
			logger.error("Error in getOutputParameters: " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage());
		}

	}

	@Override
	public ComputationId startComputation(Operator operator) throws ServiceException {
		ProcessInformations processInformations;
		try {
			processInformations = describeProcess(operator.getId());
		} catch (Throwable e) {
			logger.error("GetParameters: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

		LinkedHashMap<String, Parameter> inputParameters = new LinkedHashMap<>();

		Parameter inputParameter;
		for (InputDescriptionType inputDesc : processInformations.getInputs()) {
			inputParameter = WPS2DM.convert2DMType(inputDesc);
			logger.debug("InputParameter: " + inputParameter);
			inputParameters.put(inputParameter.getName(), inputParameter);
		}

		List<Parameter> params = operator.getOperatorParameters();
		Map<String, String> userInputs = new LinkedHashMap<>();
		for (Parameter parm : params) {
			userInputs.put(parm.getName(), parm.getValue());
			logger.debug("UserInputs[key=" + parm.getName() + ", value=" + parm.getValue() + "]");
		}

		LinkedHashMap<String, String> equivalentRequestMap = new LinkedHashMap<>();
		String processUrl = compute(processInformations, userInputs, inputParameters, equivalentRequestMap);
		logger.debug("Stated Computation ProcessLocation:" + processUrl);

		int idIndex = processUrl.lastIndexOf("?id=");
		String id;
		if (idIndex > -1) {
			id = processUrl.substring(idIndex + 4, processUrl.length());
		} else {
			logger.error("Invalid processLocation: " + processUrl);
			throw new ServiceException("Invalid processLocation: " + processUrl);
		}

		String equivalentRequest = extractEquivalentRequestForComputation(operator, equivalentRequestMap);

		ComputationId computationId = new ComputationId(id, processUrl, operator.getId(), operator.getName(),
				equivalentRequest);
		logger.debug("ComputationId: " + computationId);

		runningProcess.put(computationId, processInformations);

		return computationId;
	}

	private String extractEquivalentRequestForComputation(Operator operator,
			LinkedHashMap<String, String> equivalentRequestMap) {
		String equivalentRequest = wpsProcessingServlet + "?" + "request=Execute&service=WPS&Version=1.0.0&gcube-token="
				+ wpsToken + "&lang=en-US&Identifier=" + operator.getId() + "&DataInputs=";

		for (String key : equivalentRequestMap.keySet()) {
			String keyEncoded = "";
			try {
				keyEncoded = URLEncoder.encode(key, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.error("Error in equivalent request creation: Unsupported Encoding for parameter=" + key, e);
				break;
			}

			String valueEncoded = "";
			try {
				valueEncoded = URLEncoder.encode(equivalentRequestMap.get(key), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.error("Error in equivalent request creation: Unsupported Encoding for value="
						+ equivalentRequestMap.get(key) + " of parameter=" + key, e);
				break;
			}
			equivalentRequest = equivalentRequest + keyEncoded + "=" + valueEncoded + ";";
		}

		return equivalentRequest;
	}

	private String compute(ProcessInformations processInformations, Map<String, String> userInputs,
			Map<String, Parameter> inputParameters, LinkedHashMap<String, String> equivalentRequestMap)
			throws ServiceException {
		try {
			// setup the inputs
			org.n52.wps.client.ExecuteRequestBuilder executeBuilder = new org.n52.wps.client.ExecuteRequestBuilder(
					processInformations.getProcessDescription());
			for (InputDescriptionType input : processInformations.getInputs()) {
				String value = userInputs.get(input.getIdentifier().getStringValue());
				if (value != null && value.trim().length() > 0) {
					if (input.isSetLiteralData()) {
						logger.debug(
								"Configuring Literal: " + input.getIdentifier().getStringValue() + " to: " + value);
						equivalentRequestMap.put(input.getIdentifier().getStringValue(), value);
						executeBuilder.addLiteralData(input.getIdentifier().getStringValue(), value);

					} else if (input.isSetBoundingBoxData()) {
						logger.debug("Configuring Bounding Box: " + input.getIdentifier().getStringValue() + " to: "
								+ value);
						equivalentRequestMap.put(input.getIdentifier().getStringValue(), value);
						addBoundingBoxInput(executeBuilder, input.getIdentifier().getStringValue(), value);

					} else {
						if (input.isSetComplexData()) {
							logger.debug(
									"Configuring Complex: " + input.getIdentifier().getStringValue() + " to: " + value);
							SupportedComplexDataInputType complex = input.getComplexData();
							Parameter par = inputParameters.get(input.getIdentifier().getStringValue());

							String publicLink;
							if (par instanceof TabularListParameter) {
								// TabularListParameter tabularListParameter =
								// ((TabularListParameter) par);
								InputStream tablesStream = new ByteArrayInputStream(value.getBytes());
								publicLink = StorageUtil.saveOnStorageInTemporalFile(tablesStream);
							} else {
								publicLink = value;
							}
							equivalentRequestMap.put(input.getIdentifier().getStringValue(), value);
							executeBuilder.addComplexDataReference(input.getIdentifier().getStringValue(), publicLink,
									complex.getDefault().getFormat().getSchema(),
									complex.getDefault().getFormat().getEncoding(),
									complex.getDefault().getFormat().getMimeType());

						} else {
							logger.error("This input parameter type is not supported by client library: " + input);
							throw new ServiceException(
									"This input parameter is not supported by client library: " + input);
						}

					}
				}
			}

			// Submit the execution
			String statusLocation = executeProcessAsync(executeBuilder, processInformations.getProcessDescription());
			logger.debug("Starting Process: " + statusLocation);
			return statusLocation;

		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage(),e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public ComputationStatus getComputationStatus(ComputationId computationId) throws ServiceException {
		SClient4WPSSession wpsClient = null;
		try {
			logger.debug("GetComputationStatus(): ComputationId=" + computationId);
			wpsClient = createWPSClientSession();
			wpsClient.connectForMonitoring(wpsProcessingServlet);

			Object responseObject = null;
			if (computationId == null || computationId.getUrlId() == null || computationId.getUrlId().isEmpty()) {
				throw new ServiceException("Process Location is null!");
			} else {
				ComputationStatus computationStatus = null;
				try {

					responseObject = wpsClient.executeViaGET(computationId.getUrlId(), "");

					logger.debug("ComputationStatus ResponseObject: " + responseObject);

					if (responseObject instanceof ExecuteResponseDocumentImpl) {
						if (((ExecuteResponseDocumentImpl) responseObject).getExecuteResponse() == null) {
							logger.debug("WPS FAILURE: ExecuteResponse is null");

							computationStatus = new ComputationStatus(Status.FAILED, 100f);

						} else {
							StatusType statusType = ((ExecuteResponseDocumentImpl) responseObject).getExecuteResponse()
									.getStatus();
							if (statusType == null) {
								logger.debug("WPS FAILURE: Status Type is null");
								computationStatus = null;
							} else {

								String failure = statusType.getProcessFailed() == null ? null
										: statusType.getProcessFailed().getExceptionReport().toString();
								if ((failure != null && !failure.isEmpty())) {
									logger.debug("WPS FAILURE: " + failure);
									computationStatus = new ComputationStatus(new ServiceException(failure));
								} else {
									String paused = statusType.getProcessPaused() == null ? null
											: statusType.getProcessPaused().getStringValue();
									if (paused != null && !paused.isEmpty()) {
										logger.debug("WPS PAUSED: " + paused);
										computationStatus = new ComputationStatus(new ServiceException(paused));
									} else {
										String success = statusType.getProcessSucceeded() == null ? null
												: statusType.getProcessSucceeded();

										if (success != null && !success.isEmpty()) {
											logger.debug("WPS SUCCESS");
											computationStatus = new ComputationStatus(Status.COMPLETE, 100f);
										} else {
											String accepted = statusType.getProcessAccepted() == null ? null
													: statusType.getProcessAccepted();
											if (accepted != null && !accepted.isEmpty()) {
												logger.debug("WPS ACCEPTED");
												computationStatus = new ComputationStatus(Status.ACCEPTED, 0f);
											} else {
												int status = statusType.getProcessStarted() == null ? -1
														: statusType.getProcessStarted().getPercentCompleted();

												if (status >= 0) {
													Float statusd = (float) status;
													try {
														statusd = Float.parseFloat(
																statusType.getProcessStarted().getStringValue());
													} catch (Exception e) {
														logger.debug(e.getLocalizedMessage());
													}
													logger.debug("WPS STATUS:" + statusd);
													computationStatus = new ComputationStatus(Status.RUNNING, statusd);
												} else {
													if (status == -1) {
														logger.debug("WPS STATUS: Computation cancelled, "
																+ statusType.getProcessStarted());
														computationStatus = new ComputationStatus(Status.CANCELLED, -1);

													} else {

														logger.debug("WPS STATUS: Not Started, "
																+ statusType.getProcessStarted());
													}
												}
											}
										}
									}
								}
							}
						}
						logger.debug("ComputationStatus: " + computationStatus);
						return computationStatus;
					} else {
						logger.error("Error in ResponceObject: " + responseObject);
						logger.error("WPS FAILURE: ");
						computationStatus = new ComputationStatus(Status.FAILED, 100f);
						return computationStatus;
					}
				} catch (Throwable e) {
					logger.error("WPS FAILURE: " + e.getLocalizedMessage());
					e.printStackTrace();
					computationStatus = new ComputationStatus(Status.FAILED, 100f);
					return computationStatus;

				}
			}

		} catch (Throwable e) {
			logger.error("MonitorProcess: " + e.getLocalizedMessage(),e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		} finally {
			wpsClient.disconnect(wpsProcessingServlet);
		}

	}

	@Override
	public OutputData getOutputDataByComputationId(ComputationId computationId) throws ServiceException {
		LinkedHashMap<String, Resource> resources = retrieveOutput(computationId);
		MapResource mapResource = new MapResource("mapResource", "Resources", "Resources", resources);
		OutputData outputData = new OutputData(computationId, mapResource);

		return outputData;
	}

	private LinkedHashMap<String, Resource> retrieveOutput(ComputationId computationId) throws ServiceException {
		LinkedHashMap<String, Resource> outputResource = new LinkedHashMap<>();
		LinkedHashMap<String, Parameter> outputParameters = new LinkedHashMap<>();
		ProcessInformations processInformations = runningProcess.get(computationId);

		Parameter outputParameter;
		if (processInformations != null && processInformations.getOutputs() != null) {
			for (OutputDescriptionType outputDesc : processInformations.getOutputs()) {
				outputParameter = WPS2DM.convert2DMType(outputDesc);
				logger.debug("OutputParameter: " + outputParameter);
				outputParameters.put(outputParameter.getName(), outputParameter);
			}
		}

		retrieveProcessOutput(computationId.getUrlId(), outputParameters, outputResource);

		return outputResource;

	}

	private void retrieveProcessOutput(String processLocation, Map<String, Parameter> outputParameters,
			Map<String, Resource> outputResource) throws ServiceException {
		ProcessOutputs outs = retrieveProcessResult(processLocation);
		logger.debug("Process Executed");
		// retrieve the output objs
		if (outs == null)
			throw new ServiceException("Error during the execution of the WPS process: returned an empty document");
		else {
			OutputDataType[] outputData = outs.getOutputArray();

			for (OutputDataType out : outputData) {
				String outputID = out.getIdentifier().getStringValue();
				String value = "";
				if (out.getData().isSetLiteralData()) {
					value = out.getData().getLiteralData().getStringValue();
					Parameter paramLiteral = outputParameters.get(outputID);
					if (paramLiteral != null) {
						paramLiteral.setValue(value);
						logger.debug("Assigning value: " + value + " to output named: " + outputID);
						Resource resource = new ObjectResource(outputID, paramLiteral.getName(),
								paramLiteral.getDescription(), paramLiteral.getValue());
						outputResource.put(outputID, resource);
					}
				} else {
					if (out.getData().isSetComplexData()) {
						if (out.getReference() != null) {
							value = out.getReference().getHref();
							Parameter paramComplexData = outputParameters.get(outputID);
							if (paramComplexData != null) {
								paramComplexData.setValue(value);
								logger.debug("Assigning value: " + value + " to output named: " + outputID);
								Resource resource = new ObjectResource(outputID, paramComplexData.getName(),
										paramComplexData.getDescription(), paramComplexData.getValue());
								outputResource.put(outputID, resource);
							}
						} else
							// remove the element name, which is not useful
							outputParameters.remove(outputID);

						ComplexDataType cdt = out.getData().getComplexData();
						LinkedHashMap<String, ResponseWPS> urls = retrieveURLsFromWPSResponse(cdt);

						for (String key : urls.keySet()) {
							logger.debug("Adding OBJ:" + key);
							ResponseWPS responseWPS = urls.get(key);
							ObjectParameter objP = new ObjectParameter(key, responseWPS.getDescription(),
									String.class.getName(), " ");
							objP.setValue(responseWPS.getData());
							logger.debug("ObjectParameter: " + objP);
							outputParameters.put(key, objP);
							if (responseWPS != null && responseWPS.getMimeType() != null) {
								Resource resource;
								switch (responseWPS.getMimeType()) {
								case "image/bmp":
								case "image/gif":
								case "image/jpeg":
								case "image/png":
									resource = new ImageResource(key, responseWPS.getDescription(),
											responseWPS.getDescription(), responseWPS.getData(),
											responseWPS.getMimeType());
									outputResource.put(key, resource);
									break;
								case "text/csv":
									if (responseWPS.getData() != null && !responseWPS.getData().isEmpty()) {
										if (responseWPS.getData().startsWith("http:")
												|| responseWPS.getData().startsWith("https:")
												|| responseWPS.getData().startsWith("smp:")) {

											Parameter complexParameter = outputParameters.get(outputID);
											if (complexParameter instanceof FileParameter) {
												FileParameter fileParameter = (FileParameter) complexParameter;
												if (fileParameter.isNetcdf()) {
													resource = new FileResource(key, responseWPS.getDescription(),
															responseWPS.getDescription(), responseWPS.getData(),
															responseWPS.getMimeType(), true);
												} else {
													resource = new FileResource(key, responseWPS.getDescription(),
															responseWPS.getDescription(), responseWPS.getData(),
															responseWPS.getMimeType(), false);
												}
											} else {
												resource = new FileResource(key, responseWPS.getDescription(),
														responseWPS.getDescription(), responseWPS.getData(),
														responseWPS.getMimeType(), false);
											}
											outputResource.put(key, resource);
										} else {
											resource = new ObjectResource(key, responseWPS.getDescription(),
													responseWPS.getDescription(), responseWPS.getData());
											outputResource.put(key, resource);
										}
									} else {
										resource = new ObjectResource(key, responseWPS.getDescription(),
												responseWPS.getDescription(), responseWPS.getData());
										outputResource.put(key, resource);
									}

									break;
								default:
									if (responseWPS.getData() != null && !responseWPS.getData().isEmpty()) {
										if (responseWPS.getData().startsWith("http:")
												|| responseWPS.getData().startsWith("https:")
												|| responseWPS.getData().startsWith("smp:")) {
											Parameter complexParameter = outputParameters.get(outputID);
											if (complexParameter instanceof FileParameter) {
												FileParameter fileParameter = (FileParameter) complexParameter;
												if (fileParameter.isNetcdf()) {
													resource = new FileResource(key, responseWPS.getDescription(),
															responseWPS.getDescription(), responseWPS.getData(),
															responseWPS.getMimeType(), true);
												} else {
													resource = new FileResource(key, responseWPS.getDescription(),
															responseWPS.getDescription(), responseWPS.getData(),
															responseWPS.getMimeType(), false);
												}
											} else {
												resource = new FileResource(key, responseWPS.getDescription(),
														responseWPS.getDescription(), responseWPS.getData(),
														responseWPS.getMimeType(), false);
											}
											outputResource.put(key, resource);
										} else {
											resource = new ObjectResource(key, responseWPS.getDescription(),
													responseWPS.getDescription(), responseWPS.getData());
											outputResource.put(key, resource);
										}
									} else {
										resource = new ObjectResource(key, responseWPS.getDescription(),
												responseWPS.getDescription(), responseWPS.getData());
										outputResource.put(key, resource);
									}
									break;
								}

							}
						}
					} else {
						value = out.getData().getLiteralData().getStringValue();

					}
				}
			}
		}
	}

	@Override
	public String cancelComputation(ComputationId computationId) throws ServiceException {
		SClient4WPSSession wpsClient = null;
		try {
			wpsClient = createWPSClientSession();

			String result = wpsClient.cancelComputation(wpsCancelComputationServlet, computationId.getId());

			return result;

		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		} finally {
			if (wpsClient != null) {
				wpsClient.disconnect(wpsProcessingServlet);
			}
		}

	}

	@Override
	public ComputationId resubmitComputation(Map<String, String> computationProperties) throws ServiceException {
		ProcessInformations processInformations;
		if (computationProperties == null || computationProperties.isEmpty()) {
			throw new ServiceException("Invalid computation properties: " + computationProperties);
		}
		try {
			processInformations = describeProcess(computationProperties.get("operator_id"));
		} catch (Throwable e) {
			logger.error("GetParameters: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

		LinkedHashMap<String, Parameter> inputParameters = new LinkedHashMap<>();

		Parameter inputParameter;
		for (InputDescriptionType inputDesc : processInformations.getInputs()) {
			inputParameter = WPS2DM.convert2DMType(inputDesc);
			logger.debug("InputParameter: " + inputParameter);
			inputParameters.put(inputParameter.getName(), inputParameter);
		}

		Map<String, String> userInputs = new LinkedHashMap<>();
		for (String key : computationProperties.keySet()) {
			if (key.startsWith("input")) {
				int inputSeparatorIndex = key.indexOf("_");
				String inputKey = key.substring(inputSeparatorIndex + 1);
				if (inputKey.compareToIgnoreCase("user.name") != 0 && inputKey.compareToIgnoreCase("scope") != 0) {
					userInputs.put(inputKey, computationProperties.get(key));
					logger.debug("UserInputs[key=" + inputKey + ", value=" + computationProperties.get(key) + "]");
				}

			}
		}

		if (userInputs.isEmpty()) {
			logger.error("Attention no inputs parameters retrieved for this computation: " + computationProperties);
			throw new ServiceException(
					"Attention no inputs parameters retrieved for this computation: " + computationProperties);
		}

		LinkedHashMap<String, String> equivalentRequestMap = new LinkedHashMap<>();
		String processUrl = compute(processInformations, userInputs, inputParameters, equivalentRequestMap);
		logger.debug("Stated Computation ProcessLocation:" + processUrl);

		int idIndex = processUrl.lastIndexOf("?id=");
		String id;
		if (idIndex > -1) {
			id = processUrl.substring(idIndex + 4, processUrl.length());
		} else {
			logger.error("Invalid processLocation: " + processUrl);
			throw new ServiceException("Invalid processLocation: " + processUrl);
		}

		String equivalentRequest = extractEquivalentRequestForResubmit(computationProperties, equivalentRequestMap);

		ComputationId computationId = new ComputationId(id, processUrl, computationProperties.get("operator_id"),
				computationProperties.get("operator_name"), equivalentRequest);
		logger.debug("ComputationId: " + computationId);

		runningProcess.put(computationId, processInformations);

		return computationId;
	}

	private String extractEquivalentRequestForResubmit(Map<String, String> computationProperties,
			LinkedHashMap<String, String> equivalentRequestMap) {
		String equivalentRequest = wpsProcessingServlet + "?" + "request=Execute&service=WPS&Version=1.0.0&gcube-token="
				+ wpsToken + "&lang=en-US&Identifier=" + computationProperties.get("operator_id") + "&DataInputs=";

		for (String key : equivalentRequestMap.keySet()) {
			String keyEncoded = "";
			try {
				keyEncoded = URLEncoder.encode(key, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.error("Error in equivalent request creation: Unsupported Encoding for parameter=" + key, e);
				break;
			}

			String valueEncoded = "";
			try {
				valueEncoded = URLEncoder.encode(equivalentRequestMap.get(key), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.error("Error in equivalent request creation: Unsupported Encoding for value="
						+ equivalentRequestMap.get(key) + " of parameter=" + key, e);
				break;
			}
			equivalentRequest = equivalentRequest + keyEncoded + "=" + valueEncoded + ";";
		}

		return equivalentRequest;
	}

	@Override
	public ComputationData getComputationDataByComputationProperties(Map<String, String> computationProperties)
			throws ServiceException {
		try {
			if (computationProperties == null || computationProperties.isEmpty()) {
				throw new Exception("Invalid computation properties: " + computationProperties);
			}

			String compId = computationProperties.get("computation_id");

			String operatorId = computationProperties.get("operator_id");
			String operatorName = computationProperties.get("operator_name");
			String operatorDescritpion = computationProperties.get("operator_description");
			String vre = computationProperties.get("VRE");
			String startDate = computationProperties.get("start_date");
			String endDate = computationProperties.get("end_date");
			String status = computationProperties.get("status");
			String executionType = computationProperties.get("execution_type");

			ComputationId computationId = new ComputationId();
			computationId.setId(compId);
			computationId.setOperatorId(operatorId);
			computationId.setOperatorName(operatorName);

			ArrayList<ComputationItemPropertiesValue> inputParametersAsProperties = new ArrayList<>();
			ArrayList<ComputationItemPropertiesValue> outputParametersAsProperties = new ArrayList<>();

			for (String key : computationProperties.keySet()) {
				if (key != null) {
					if (key.startsWith("input")) {
						int inputSeparatorIndex = key.indexOf("_");
						String inputKey = key.substring(inputSeparatorIndex + 1);
						if (inputKey.compareToIgnoreCase("user.name") != 0
								&& inputKey.compareToIgnoreCase("scope") != 0) {
							String inputOrder = key.substring(5, inputSeparatorIndex);
							Integer order;
							try {
								order = Integer.parseInt(inputOrder);
							} catch (NumberFormatException e) {
								order = 0;
							}
							ComputationItemPropertiesValue inputProps = new ComputationItemPropertiesValue(order,
									inputKey, computationProperties.get(key));
							inputParametersAsProperties.add(inputProps);

						}

					} else {
						if (key.startsWith("output")) {
							int outputSeparatorIndex = key.indexOf("_");
							String outputKey = key.substring(outputSeparatorIndex + 1);

							String outputOrder = key.substring(6, outputSeparatorIndex);
							Integer order;
							try {
								order = Integer.parseInt(outputOrder);
							} catch (NumberFormatException e) {
								order = 0;
							}
							ComputationItemPropertiesValue outputProps = new ComputationItemPropertiesValue(order,
									outputKey, computationProperties.get(key));

							outputParametersAsProperties.add(outputProps);

						}

					}

				}
			}

			Collections.sort(inputParametersAsProperties);
			Collections.sort(outputParametersAsProperties);

			ComputationValueBuilder computationValueBuilder = new ComputationValueBuilder(inputParametersAsProperties);
			LinkedHashMap<String, ComputationValue> inputParameters = computationValueBuilder.create();
			computationValueBuilder = new ComputationValueBuilder(outputParametersAsProperties);
			LinkedHashMap<String, ComputationValue> outputParameters = computationValueBuilder.create();

			logger.debug("inputParameters: " + inputParameters);
			logger.debug("outputParameters: " + outputParameters);

			ComputationData computationData = new ComputationData(computationId, inputParameters, outputParameters,
					operatorDescritpion, startDate, endDate, status, executionType, vre);

			logger.debug("ComputationData: " + computationData);
			return computationData;

		} catch (ServiceException e) {
			logger.error("Error in getComutationDataByComputationProperties: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("Error in getComutationDataByComputationProperties: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);

		}

	}

	@Override
	public String toString() {
		return "DataMiner WPS Client";
	}

}
