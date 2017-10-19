package org.gcube.dataanalysis.geo.wps.client;

import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import net.opengis.wps.x100.CapabilitiesDocument;
import net.opengis.wps.x100.ComplexDataType;
import net.opengis.wps.x100.ExecuteDocument;
import net.opengis.wps.x100.ExecuteResponseDocument;
import net.opengis.wps.x100.ExecuteResponseDocument.ExecuteResponse.ProcessOutputs;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.InputType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.ProcessBriefType;
import net.opengis.wps.x100.ProcessDescriptionType;
import net.opengis.wps.x100.ResponseDocumentType;
import net.opengis.wps.x100.StatusType;
import net.opengis.wps.x100.impl.ExecuteResponseDocumentImpl;

import org.apache.xmlbeans.XmlString;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.geo.wps.mappings.WPS2SM;
import org.n52.wps.client.ExecuteRequestBuilder;
import org.n52.wps.client.WPSClientSession;
import org.w3c.dom.NodeList;

public class WPSClient {

	private ProcessBriefType[] processesList;
	private String wpsServiceURL;
	private InputDescriptionType[] currentInputs;
	private OutputDescriptionType[] currentOutputs;
	public float wpsstatus = 0;

	
	public OutputDescriptionType[] getCurrentOutputs() {
		return currentOutputs;
	}

	public void setCurrentOutputs(OutputDescriptionType[] currentOutputs) {
		this.currentOutputs = currentOutputs;
	}

	private List<StatisticalType> currentInputStatisticalTypes;
	private LinkedHashMap<String, StatisticalType> currentOutputStatisticalTypes;
	private ProcessDescriptionType currentProcessDescription;

	public LinkedHashMap<String, StatisticalType> getCurrentOutputStatisticalTypes() {
		return currentOutputStatisticalTypes;
	}

	public void setCurrentOutputStatisticalTypes(LinkedHashMap<String, StatisticalType> currentOutputStatisticalTypes) {
		this.currentOutputStatisticalTypes = currentOutputStatisticalTypes;
	}

	public String getWpsServiceURL() {
		return wpsServiceURL;
	}

	public void setWpsServiceURL(String wpsServiceURL) {
		this.wpsServiceURL = wpsServiceURL;
	}

	public InputDescriptionType[] getCurrentInputs() {
		return currentInputs;
	}

	public void setCurrentInputs(InputDescriptionType[] currentInputs) {
		this.currentInputs = currentInputs;
	}

	public List<StatisticalType> getCurrentInputStatisticalTypes() {
		return currentInputStatisticalTypes;
	}

	public void setCurrentInputStatisticalTypes(List<StatisticalType> currentStatisticalTypes) {
		this.currentInputStatisticalTypes = currentStatisticalTypes;
	}

	public String getCurrentProcessID() {
		return currentProcessID;
	}

	public void setCurrentProcessID(String currentProcessID) {
		this.currentProcessID = currentProcessID;
	}

	public String getCurrentProcessTitle() {
		return currentProcessTitle;
	}

	public void setCurrentProcessTitle(String currentProcessTitle) {
		this.currentProcessTitle = currentProcessTitle;
	}

	public String getCurrentProcessAbstract() {
		return currentProcessAbstract;
	}

	public void setCurrentProcessAbstract(String currentProcessAbstract) {
		this.currentProcessAbstract = currentProcessAbstract;
	}

	private String currentProcessID;
	private String currentProcessTitle;
	private String currentProcessAbstract;

	// example: http://wps01.i-marine.d4science.org/wps/WebProcessingService
	public WPSClient(String wpsServiceURL) throws Exception {
		this.wpsServiceURL = wpsServiceURL;
	}

	public void describeProcess(String processID) throws Exception {
		describeProcess(processID, null);
	}

	public void describeProcess(String processID, URL processDescriptionURL) throws Exception {
		WPSClientSession wpsClient = WPSClientSession.getInstance();
		try {
			AnalysisLogger.getLogger().debug("Describe Process WPS URL: " + wpsServiceURL);
			ProcessDescriptionType processDescription = null;
			for (int k = 0; k <= 3; k++) {
				try {
					processDescription = wpsClient.getProcessDescription(wpsServiceURL, processID);
				} catch (Exception e) {
					AnalysisLogger.getLogger().debug("Retrying with WPS URL: " + wpsServiceURL);
					if (k == 3)
						throw e;
				}
				if (processDescription != null)
					break;
			}
			this.currentProcessDescription = processDescription;
			// processDescription.set(XmlString.Factory.parse(new URL("http://schemas.opengis.net/wps/1.0.0/examples/40_wpsDescribeProcess_response.xml")));
			if (processDescriptionURL != null)
				processDescription.set(XmlString.Factory.parse(processDescriptionURL));

			AnalysisLogger.getLogger().debug(processDescription.toString());
			currentProcessID = processDescription.getIdentifier().getStringValue();
			currentProcessTitle = processDescription.getTitle().getStringValue();
			currentProcessAbstract = processDescription.getAbstract() != null ? processDescription.getAbstract().getStringValue() : "";
			AnalysisLogger.getLogger().debug("WPSClient->Process ID:" + currentProcessID);
			AnalysisLogger.getLogger().debug("WPSClient->Process Title:" + currentProcessTitle);
			AnalysisLogger.getLogger().debug("WPSClient->Process Abstract:" + currentProcessAbstract);

			InputDescriptionType[] inputList = processDescription.getDataInputs().getInputArray();
			AnalysisLogger.getLogger().debug("WPSClient->Fetching Inputs");
			currentInputStatisticalTypes = new ArrayList<StatisticalType>();
			for (InputDescriptionType input : inputList) {
				StatisticalType stype = WPS2SM.convert2SMType(input);
				currentInputStatisticalTypes.add(stype);
				AnalysisLogger.getLogger().debug("WPSClient->Converted Into a Statistical Type: " + stype);
			}

			AnalysisLogger.getLogger().debug("WPSClient->Fetching Outputs");
			OutputDescriptionType[] outputList = processDescription.getProcessOutputs().getOutputArray();
			currentOutputStatisticalTypes = new LinkedHashMap<String, StatisticalType>();
			currentOutputs = outputList;
			for (OutputDescriptionType output : outputList) {
				AnalysisLogger.getLogger().debug("WPSClient->Output id:" + output.getIdentifier().getStringValue());
				if (output.getAbstract() != null)
					AnalysisLogger.getLogger().debug("WPSClient->Abstract:" + output.getAbstract().getStringValue());
				AnalysisLogger.getLogger().debug("WPSClient->Name:" + output.getTitle().getStringValue());
				StatisticalType stype = WPS2SM.convert2SMType(output);
				currentOutputStatisticalTypes.put(output.getIdentifier().getStringValue(), stype);
				AnalysisLogger.getLogger().debug("WPSClient->Converted Into a Statistical Type: " + stype);
			}

			currentInputs = inputList;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			wpsClient.disconnect(wpsServiceURL);
		}
	}

	public void requestGetCapabilities() throws Exception {

		WPSClientSession wpsClient = WPSClientSession.getInstance();
		wpsClient.connect(wpsServiceURL);
		try {
			CapabilitiesDocument capabilities = wpsClient.getWPSCaps(wpsServiceURL);

			ProcessBriefType[] processList = capabilities.getCapabilities().getProcessOfferings().getProcessArray();

			for (ProcessBriefType process : processList) {
				AnalysisLogger.getLogger().debug("WPSClient->Process id:" + process.getIdentifier().getStringValue());
				AnalysisLogger.getLogger().debug("WPSClient->title:" + process.getTitle().getStringValue());
				AnalysisLogger.getLogger().debug("WPSClient->abstract:" + process.getAbstract());
			}

			this.setProcessesList(processList);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			wpsClient.disconnect(wpsServiceURL);
		}
	}

	public ProcessBriefType[] getProcessesList() {
		return processesList;
	}

	public void setProcessesList(ProcessBriefType[] processesList) {
		this.processesList = processesList;
	}

	public ProcessDescriptionType getProcessDescription() {
		return currentProcessDescription;
	}

	public void setProcessDescription(ProcessDescriptionType processDescription) {
		this.currentProcessDescription = processDescription;
	}

	public ProcessOutputs executeProcess(ExecuteRequestBuilder executeBuilder, ProcessDescriptionType processDescription) throws Exception {

		try {

			OutputDescriptionType[] odts = processDescription.getProcessOutputs().getOutputArray();
			for (OutputDescriptionType odt : odts) {
				// executeBuilder.setMimeTypeForOutput("text/xml", "result");
				if (odt.isSetComplexOutput())
					executeBuilder.setMimeTypeForOutput("text/xml", odt.getIdentifier().getStringValue());
			}
		} catch (Exception e) {
			AnalysisLogger.getLogger().debug(e);
			AnalysisLogger.getLogger().debug("Execute Process-> Warning, no xml structured objects will be provided: " + e.getLocalizedMessage());
		}
		// executeBuilder.setSchemaForOutput("http://schemas.opengis.net/gml/3.1.1/base/feature.xsd", "result");

		ExecuteDocument execute = executeBuilder.getExecute();
		execute.getExecute().setService("WPS");
		// System.out.println("RESPONSE FORM:"+execute.getExecute().getResponseForm());
		WPSClientSession wpsClient = WPSClientSession.getInstance();
		try {
			wpsClient.connect(wpsServiceURL);
			AnalysisLogger.getLogger().debug("Sending:\n" + execute);
			if (execute.getExecute().getResponseForm() != null) {
				ResponseDocumentType documentType = execute.getExecute().getResponseForm().getResponseDocument();
				documentType.setStoreExecuteResponse(true);
				documentType.setStatus(true);
				documentType.setLineage(false);
				execute.getExecute().getResponseForm().setResponseDocument(documentType);
			}
			boolean end = false;
			Object responseObject = wpsClient.execute(wpsServiceURL, execute);
			String statusLocation = null;
			if (responseObject != null)
				statusLocation = ((ExecuteResponseDocumentImpl) responseObject).getExecuteResponse().getStatusLocation();
			else
				throw new Exception("" + responseObject);

			while (!end) {
				// AnalysisLogger.getLogger().debug("Response:\n" + responseObject);

				if (responseObject instanceof ExecuteResponseDocumentImpl) {
					// AnalysisLogger.getLogger().debug("ResponseImpl:\n" + responseObject);
					StatusType statusType = ((ExecuteResponseDocumentImpl) responseObject).getExecuteResponse().getStatus();
					int status = statusType.getProcessStarted() == null ? -1 : statusType.getProcessStarted().getPercentCompleted();
					String failure = statusType.getProcessFailed() == null ? null : statusType.getProcessFailed().getExceptionReport().toString();
					String accepted = statusType.getProcessAccepted() == null ? null : statusType.getProcessAccepted();
					String success = statusType.getProcessSucceeded() == null ? null : statusType.getProcessSucceeded();
					String paused = statusType.getProcessPaused() == null ? null : statusType.getProcessPaused().getStringValue();

					if ((failure != null && failure.length() > 0) || (paused != null && paused.length() > 0)) {
						AnalysisLogger.getLogger().debug("WPS FAILURE: " + failure + " OR PAUSED: " + paused);
						wpsstatus = 100f;
						throw new Exception(failure);
					} else if (accepted != null && accepted.length() > 0) {
						AnalysisLogger.getLogger().debug("WPS ACCEPTED");
						wpsstatus = 0f;
					} else if (success != null && success.length() > 0) {
						AnalysisLogger.getLogger().debug("WPS SUCCESS");
						wpsstatus = 100f;
						end = true;
					} else if (status >= 0) {
						Float statusd = (float) status;
						try {
							statusd = Float.parseFloat(statusType.getProcessStarted().getStringValue());
						} catch (Exception e) {
						}

						AnalysisLogger.getLogger().debug("WPS STATUS:" + statusd);

						wpsstatus = statusd;
					}

					Thread.sleep(2000);
					if (statusLocation != null && statusLocation.length() > 0)
						responseObject = wpsClient.executeViaGET(statusLocation, "");
					else if (wpsstatus != 100)
						throw new Exception("Cannot retrieve process status");

					// AnalysisLogger.getLogger().debug("ResponseOBJ:\n" + responseObject);
				} else
					throw new Exception("" + responseObject);

			}

			AnalysisLogger.getLogger().debug("Response:\n" + responseObject);
			wpsstatus = 100f;
			return ((ExecuteResponseDocument) responseObject).getExecuteResponse().getProcessOutputs();
		} catch (Exception e) {
			AnalysisLogger.getLogger().debug(e);
			AnalysisLogger.getLogger().debug("WPSClient->Caught a WPS exception: " + e.getLocalizedMessage() + " Returning the exception");
			throw e;
		} finally {
			wpsClient.disconnect(wpsServiceURL);
		}
	}

	public static int calculateBBDimensions(String bbstring) {
		String[] bbinput = bbstring.split(",");
		int dimcounter = 0;
		try {
			for (int i = 0; i < bbinput.length; i++) {
				Double.parseDouble(bbinput[i]);
				dimcounter++;
			}
		} catch (Exception e) {
			AnalysisLogger.getLogger().debug("Dimensions Count: " + dimcounter);
		}
		return dimcounter;
	}

	public static void addBoundingBoxInput(org.n52.wps.client.ExecuteRequestBuilder executeBuilder, String identifier, String BBstring) {

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

	public static List<String> retrieveURLsFromWPSResponse(ComplexDataType cdt) {
		org.w3c.dom.Node node = cdt.getDomNode();
		List<String> urls = getURLFromXML(node);
		return urls;
	}

	public static String getExceptionText(String exceptionText) {
		try {
			String excText = "ExceptionText>";
			int idx = exceptionText.indexOf(excText);
			if (idx >= 0) {
				String exception = exceptionText.substring(idx + excText.length());
				exception = exception.substring(0, exception.indexOf("</"));
				exception = exception.replace("<", "").replace(">", "").replace("/", " ").replace("\\", " ").replaceAll("[\\]\\[!\"#$%&'\\(\\)*+/:;<=>?@\\^_`{\\|}~-]", "_").trim();
				exception = exception.replaceAll("[ ]+"," ");
				if (exception.length()>200)
					exception = exception.substring(0, 200)+"...";
				return exception;
			} else
				return "Process error in WPS Execution";
		} catch (Exception e) {
			e.printStackTrace();
			return "Backend error in WPS Execution";
		}
	}

	private static List<String> getURLFromXML(org.w3c.dom.Node node) {

		List<String> urls = new ArrayList<String>();
		if (node == null)
			return urls;

		NodeList listnodes = node.getChildNodes();
		int nChildren = listnodes.getLength();

		if (nChildren == 0) {
			String text = node.getNodeValue();
			if (text != null && (text.startsWith("https:") || text.startsWith("http:") || text.startsWith("ftp:") || text.startsWith("smp:") || text.startsWith("file:")))
				urls.add(text.trim());
			else if (text != null && text.trim().length() > 0)
				urls.add(text.trim());
		} else {
			for (int i = 0; i < nChildren; i++) {
				List<String> childrenurls = getURLFromXML(listnodes.item(i));
				urls.addAll(childrenurls);

			}
		}
		return urls;
	}

	public static void main1(String[] args) throws Exception {
		AnalysisLogger.setLogger("./cfg/ALog.properties");
		// WPSClient client = new WPSClient("http://wps01.i-marine.d4science.org/wps/WebProcessingService");
		WPSClient client = new WPSClient("http://geoprocessing.demo.52north.org:8080/wps/WebProcessingService");
		client.requestGetCapabilities();
		// client.describeProcess("com.terradue.wps_hadoop.processes.examples.async.Async", new URL("file:///C:/Users/coro/Desktop/WorkFolder/Workspace/EcologicalEngineWPSExtension/cfg/test.xml"));
		// client.describeProcess("org.n52.wps.extension.GetFuelPriceProcess");
		// client.describeProcess("org.n52.wps.server.algorithm.test.DummyTestClass");
		// client.describeProcess("org.n52.wps.server.algorithm.coordinatetransform.CoordinateTransformAlgorithm");
		// client.describeProcess("org.n52.wps.extension.GetFuelPriceProcess");

		// client.describeProcess("com.terradue.wps_hadoop.processes.examples.async.Async");
		client.describeProcess("org.n52.wps.server.algorithm.SimpleBufferAlgorithm");
	}

	public static void main(String[] args) {
		String exception = "<xml-fragment xmlns:ns1=\"http://www.opengis.net/ows/1.1\" xmlns:ns=\"http://www.opengis.net/wps/1.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">  <ns1:Exception>    <ns1:ExceptionText>org.n52.wps.server.ExceptionReport: Statistical Manager Computation Failed:java.io.FileNotFoundException: /home/gcube2/gCore/etc/statistical-manager-service-full-1.4.0-SNAPSHOT/cfg/PARALLEL_PROCESSING/matcherOutput0e2f68f72b5e48f7a39dfc5bf999c168.csv (No such file or directory)</ns1:ExceptionText>  </ns1:Exception>";
		System.out.println(getExceptionText(exception));
	}

}
