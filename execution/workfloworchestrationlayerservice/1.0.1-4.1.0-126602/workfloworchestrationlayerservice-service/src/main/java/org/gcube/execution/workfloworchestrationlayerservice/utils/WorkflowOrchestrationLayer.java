package org.gcube.execution.workfloworchestrationlayerservice.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.apache.xml.security.utils.Base64;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.execution.workflowengine.service.stubs.CONDORResource;
import org.gcube.execution.workflowengine.service.stubs.HADOOPArchiveResource;
import org.gcube.execution.workflowengine.service.stubs.HADOOPArgumentResource;
import org.gcube.execution.workflowengine.service.stubs.HADOOPConfigurationResource;
import org.gcube.execution.workflowengine.service.stubs.HADOOPFileResource;
import org.gcube.execution.workflowengine.service.stubs.HADOOPInputResource;
import org.gcube.execution.workflowengine.service.stubs.HADOOPJarResource;
import org.gcube.execution.workflowengine.service.stubs.HADOOPLibResource;
import org.gcube.execution.workflowengine.service.stubs.HADOOPMainResource;
import org.gcube.execution.workflowengine.service.stubs.HADOOPOutputResource;
import org.gcube.execution.workflowengine.service.stubs.HADOOPPropertyResource;
import org.gcube.execution.workfloworchestrationlayerservice.WOLServiceContext;
import org.gcube.execution.workfloworchestrationlayerservice.WorkflowOrchestrationLayerService;
import org.gcube.execution.workfloworchestrationlayerservice.stubs.WOLConfig;
import org.gcube.execution.workfloworchestrationlayerservice.wrappers.AdaptorBase;
import org.gcube.execution.workfloworchestrationlayerservice.wrappers.CondorAdaptor;
import org.gcube.execution.workfloworchestrationlayerservice.wrappers.GridAdaptor;
import org.gcube.execution.workfloworchestrationlayerservice.wrappers.HadoopAdaptor;
import org.gcube.execution.workfloworchestrationlayerservice.wrappers.JDLAdaptor;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import static org.gcube.resources.discovery.icclient.ICFactory.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowOrchestrationLayer {

	private static Logger logger = LoggerFactory.getLogger(WorkflowOrchestrationLayer.class);

	private static final String GRID = "\"GRID\"";
	private static final String PE2NG = "\"PE2NG\"";
	private static final String CONDOR = "\"CONDOR\"";
	private static final String HADOOP = "\"HADOOP\"";

	public enum Type {
		GRID, PE2NG, CONDOR, HADOOP
	}

	public static String turnBytesToFile(byte[] bytes) throws IOException {
		File temp = File.createTempFile("wol-temp-", ".txt", WorkflowOrchestrationLayerService.tempDir);
		// TODO maybe force deletion before exit?
		temp.deleteOnExit();
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(temp));
		bos.write(bytes);
		bos.flush();
		bos.close();
		return temp.getAbsolutePath();
	}

	// Used to convert a file to a base64 string representation
	public static String encodeFileToBase64Binary(String fileName) throws IOException {
		File file = new File(fileName);
		byte[] bytes = loadFile(file);
		String encoded = Base64.encode(bytes);
		return encoded;
	}

	public static byte[] loadFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		long length = file.length();
		if (length > Integer.MAX_VALUE) {
			// File is too large
		}
		byte[] bytes = new byte[(int) length];

		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		if (offset < bytes.length) {
			throw new IOException("Could not completely read file " + file.getName());
		}
		is.close();
		return bytes;
	}

	public static String transform(String serialization, HashMap<String, byte[]> wrs, HashMap<String, String> jdlrs, WOLConfig wc, String scope)
			throws FileNotFoundException {
		logger.info("Transforming...");
		Map<String, Map<String, String>> maps = new HashMap<String, Map<String, String>>();
		try {
			String jdlSer = JDLParsingUtils.Trim(serialization);
			jdlSer = JDLParsingUtils.StripComments(jdlSer);
			String jdlBlock = JDLParsingUtils.GetDefinitionBlock(jdlSer);
			Map<String, String> keyValues = JDLParsingUtils.GetKeyValues(jdlBlock);
			String nodesSerialization = keyValues.get(JDLParsingUtils.NODES);
			nodesSerialization = JDLParsingUtils.removeBrackets(nodesSerialization);
			Map<String, String> nodes = JDLParsingUtils.GetKeyValues(nodesSerialization);

			for (String node : nodes.keySet()) {
				String nodeSerialization = nodes.get(node);
				nodeSerialization = JDLParsingUtils.removeBrackets(nodeSerialization);

				String description = JDLParsingUtils.GetKeyValues(nodeSerialization).get(JDLParsingUtils.DESCRIPTION);
				description = JDLParsingUtils.removeBrackets(description);
				Map<String, String> map = JDLParsingUtils.GetKeyValues(description);
				maps.put(node, map);
			}
			logger.info("Maps: " + maps.toString());
			String resourceFile = rewriteAsWebServices(maps, keyValues, wrs, jdlrs, wc, scope);
			return resourceFile;
		} catch (IOException e) {
			logger.error("Exception", e);
		}
		return null;
	}

	private static String getGRIDResources(String file, HashMap<String, byte[]> wrs) throws Exception {
		StringBuilder sb = new StringBuilder();
		String remoteValue = GridAdaptor.ParseRemoteValue(file, "jdl");
		String localValue = GridAdaptor.ParseLocalKeyValue(file, "jdl");
		sb.append("<gridResources xmlns=\"\">");
		sb.append("<resourceType>JDL</resourceType>");
		sb.append("<resourceKey>" + remoteValue + "</resourceKey>");
		sb.append("<resourceAccess>InMessageString</resourceAccess>");
		sb.append("<inMessageStringPayload>" + AdaptorBase.GetStringFilePayload(turnBytesToFile(wrs.get(localValue))).replaceAll("\"", "&quot;")
				+ "</inMessageStringPayload>");
		sb.append("</gridResources>");
		remoteValue = GridAdaptor.ParseRemoteValue(file, "config");
		localValue = GridAdaptor.ParseLocalKeyValue(file, "config");
		if (!(remoteValue == null || remoteValue.trim().length() == 0 || localValue == null || localValue.trim().length() == 0)) {
			sb.append("<gridResources xmlns=\"\">");
			sb.append("<resourceType>Config</resourceType>");
			sb.append("<resourceKey>" + remoteValue + "</resourceKey>");
			sb.append("<resourceAccess>InMessageString</resourceAccess>");
			sb.append("<inMessageStringPayload>" + AdaptorBase.GetStringFilePayload(turnBytesToFile(wrs.get(localValue))).replaceAll("\"", "&quot;")
					+ "</inMessageStringPayload>");
			sb.append("</gridResources>");
		}
		sb.append("<gridResources xmlns=\"\">");
		remoteValue = GridAdaptor.ParseRemoteValue(file, "userProxy");
		localValue = GridAdaptor.ParseLocalKeyValue(file, "userProxy");
		sb.append("<resourceType>UserProxy</resourceType>");
		sb.append("<resourceKey>" + remoteValue + "</resourceKey>");
		sb.append("<resourceAccess>InMessageBytes</resourceAccess>");
		sb.append("<inMessageBytePayload>" + Base64.encode(wrs.get(localValue)) + "</inMessageBytePayload>");
		sb.append("</gridResources>");

		FileInfo globalOutputStoreMode = GridAdaptor.ParseGlobalOutputStoreMode(file);

		HashMap<String, FileInfo> data;
		try {
			data = GridAdaptor.ParseInData(file);
		} catch (Exception e) {
			throw new RuntimeException("Failed to parse In Data", e);
		}
		for (Map.Entry<String, FileInfo> entry : data.entrySet()) {
			sb.append("<gridResources xmlns=\"\">");
			sb.append("<resourceType>InData</resourceType>");
			sb.append("<resourceKey>" + entry.getKey() + "</resourceKey>");
			switch (entry.getValue().TypeOfLocation) {
			case local: {
				sb.append("<resourceAccess>InMessageBytes</resourceAccess>");
				sb.append("<inMessageBytePayload>" + Base64.encode(wrs.get(localValue)) + "</inMessageBytePayload>");
				break;
			}
			case ss: {
				sb.append("<resourceAccess>CMSReference</resourceAccess>");
				sb.append("<resourceReference>" + entry.getValue().Value + "</resourceReference>");
				break;
			}
			case url: {
				sb.append("<resourceAccess>Reference</resourceAccess>");
				sb.append("<resourceReference>" + entry.getValue().Value + "</resourceReference>");
				sb.append("<resourceAccessInfo>");
				sb.append("<password>" + entry.getValue().AccessInfo.password + "</password>");
				sb.append("<port>" + new Integer(entry.getValue().AccessInfo.port).toString() + "</port>");
				sb.append("<userId>" + entry.getValue().AccessInfo.userId + "</userId>");
				sb.append("</resourceAccessInfo>");
				break;
			}
			}
			sb.append("</gridResources>");
		}
		HashMap<String, FileInfo> data2 = GridAdaptor.ParseOutData(file, globalOutputStoreMode);
		for (Map.Entry<String, FileInfo> entry : data2.entrySet()) {
			sb.append("<gridResources xmlns=\"\">");
			sb.append("<resourceType>OutData</resourceType>");
			sb.append("<resourceKey>" + entry.getKey() + "</resourceKey>");
			switch (entry.getValue().TypeOfLocation) {
			case ss: {
				sb.append("<resourceAccess>CMSReference</resourceAccess>");
				sb.append("<resourceReference>" + entry.getValue().Value + "</resourceReference>");
				break;
			}
			case url: {
				sb.append("<resourceAccess>CMSReference</resourceAccess>");
				sb.append("<resourceReference>" + entry.getValue().Value + "</resourceReference>");
				break;
			}
			}
			sb.append("</gridResources>");
		}

		sb.append("<config xmlns=\"\">");
		sb.append("<chokePerformanceEvents>" + GridAdaptor.ParseBooleanProperty(file, "chokePerformanceEvents") + "</chokePerformanceEvents>");
		sb.append("<chokeProgressEvents>" + GridAdaptor.ParseBooleanProperty(file, "chokeProgressEvents") + "</chokeProgressEvents>");
		sb.append("<retryOnErrorPeriod>" + GridAdaptor.ParseLongProperty(file, "retryOnErrorPeriod") + "</retryOnErrorPeriod>");
		sb.append("<retryOnErrorTimes>" + GridAdaptor.ParseLongProperty(file, "retryOnErrorTimes") + "</retryOnErrorTimes>");
		sb.append("<timeout>" + GridAdaptor.ParseLongProperty(file, "timeout") + "</timeout>");
		sb.append("<pollPeriod>" + GridAdaptor.ParseLongProperty(file, "pollPeriod") + "</pollPeriod>");
		sb.append("</config>");

		return sb.toString();
	}

	private static String getPE2NGResources(String file, HashMap<String, byte[]> wrs, HashMap<String, String> jdlrs) throws Exception {
		StringBuilder sb = new StringBuilder();

		HashMap<String, String> resources = null;
		HashMap<String, FileInfo> inDataResources = null;
		HashMap<String, FileInfo> outDataResources = null;
		try {
			resources = JDLAdaptor.ParseResourceFile(file);
			FileInfo globalOutputStoreMode = JDLAdaptor.ParseGlobalOutputStoreMode(file);
			inDataResources = JDLAdaptor.ParseInData(file);
			outDataResources = JDLAdaptor.ParseOutData(file, globalOutputStoreMode);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		sb.append("<jdlDescription xmlns=\"\">");
		logger.info("Going to add " + resources.get("jdl") + " " + jdlrs.get(resources.get("jdl")));
		sb.append(jdlrs.get(resources.get("jdl")).replaceAll("\"", "&quot;").replaceAll("\t", ""));
		sb.append("</jdlDescription>");

		for (Map.Entry<String, FileInfo> res : inDataResources.entrySet()) {
			sb.append("<jdlResources xmlns=\"\">");
			sb.append("<resourceKey>" + res.getKey() + "</resourceKey>");
			sb.append("<resourceType>InData</resourceType>");

			switch (res.getValue().TypeOfLocation) {
			case local: {
				sb.append("<resourceAccess>InMessageBytes</resourceAccess>");
				logger.info("This is the file " + res.getKey() + " " + wrs.get(res.getKey()));
				sb.append("<inMessageBytePayload>" + Base64.encode(wrs.get(res.getKey())) + "</inMessageBytePayload>");
				break;
			}
			case ss: {
				sb.append("<resourceReference>" + res.getValue().Value + "</resourceReference>");
				sb.append("<resourceAccess>CMSReference</resourceAccess>");
				break;
			}
			case url: {
				sb.append("<resourceReference>" + res.getValue().Value + "</resourceReference>");
				sb.append("<resourceAccess>Reference</resourceAccess>");
				break;
			}
			}
			sb.append("</jdlResources>");
		}

		for (Map.Entry<String, FileInfo> res : outDataResources.entrySet()) {

			sb.append("<jdlResources xmlns=\"\">");
			sb.append("<resourceType>OutData</resourceType>");
			sb.append("<resourceKey>" + res.getKey() + "</resourceKey>");
			switch (res.getValue().TypeOfLocation) {
			case ss: {
				sb.append("<resourceReference>" + res.getValue().Value + "</resourceReference>");
				sb.append("<resourceAccess>CMSReference</resourceAccess>");
				break;
			}
			case url: {
				sb.append("<resourceAccess>Reference</resourceAccess>");
				sb.append("<resourceReference>" + res.getValue().Value + "</resourceReference>");
				sb.append("<resourceAccessInfo>");
				sb.append("<password>" + res.getValue().AccessInfo.password + "</password>");
				sb.append("<port>" + new Integer(res.getValue().AccessInfo.port).toString() + "</port>");
				sb.append("<userId>" + res.getValue().AccessInfo.userId + "</userId>");
				sb.append("</resourceAccessInfo>");
				break;
			}
			}
			sb.append("</jdlResources>");
		}

		sb.append("<config xmlns=\"\">");
		if (resources.containsKey("chokePerformanceEvents"))
			sb.append("<chokePerformanceEvents>" + Boolean.parseBoolean(resources.get("chokePerformanceEvents")) + "</chokePerformanceEvents>");
		if (resources.containsKey("chokeProgressEvents"))
			sb.append("<chokeProgressEvents>" + Boolean.parseBoolean(resources.get("chokeProgressEvents")) + "</chokeProgressEvents>");
		if (resources.containsKey("queueSupport"))
			sb.append("<queueSupport>" + Boolean.parseBoolean(resources.get("queueSupport")) + "</queueSupport>");
		else
			sb.append("<queueSupport>" + false + "</queueSupport>");
		if (resources.containsKey("utiliaztion"))
			sb.append("<utilization>" + Float.parseFloat(resources.get("utiliaztion")) + "</utilization>");
		else
			sb.append("<utilization>" + new Float(0.1f) + "</utilization>");
		if (resources.containsKey("passedBy"))
			sb.append("<passedBy>" + Integer.parseInt(resources.get("passedBy")) + "</passedBy>");
		else
			sb.append("<passedBy>" + new Integer(1) + "</passedBy>");
		sb.append("</config>");

		return sb.toString();
	}

	private static String getCONDORResources(String file, HashMap<String, byte[]> wrs) throws Exception {
		StringBuilder sb = new StringBuilder();

		CONDORResource[] resources = null;
		resources = CondorAdaptor.GetResources(file, wrs);

		for (CONDORResource res : resources) {
			sb.append("<condorResources xmlns=\"\">");
			sb.append("<resourceKey>" + res.getResourceKey() + "</resourceKey>");
			sb.append("<resourceType>" + res.getResourceType() + "</resourceType>");

			sb.append("<resourceAccess>" + res.getResourceAccess() + "</resourceAccess>");
			if (res.getInMessageStringPayload() != null)
				sb.append("<inMessageStringPayload>" + res.getInMessageStringPayload() + "</inMessageStringPayload>");
			if (res.getInMessageBytePayload() != null)
				sb.append("<inMessageBytePayload>" + Base64.encode(res.getInMessageBytePayload()) + "</inMessageBytePayload>");
			if (res.getResourceReference() != null)
				sb.append("<resourceReference>" + res.getResourceReference() + "</resourceReference>");

			sb.append("</condorResources>");
		}

		sb.append("<config xmlns=\"\">");
		// if (resources.containsKey("chokePerformanceEvents"))
		sb.append("<chokePerformanceEvents>" + CondorAdaptor.ParseBooleanProperty(file, "chokePerformanceEvents") + "</chokePerformanceEvents>");
		sb.append("<chokeProgressEvents>" + CondorAdaptor.ParseBooleanProperty(file, "chokeProgressEvents") + "</chokeProgressEvents>");
		sb.append("<retrieveJobClassAd>" + CondorAdaptor.ParseBooleanProperty(file, "retrieveJobClassAd") + "</retrieveJobClassAd>");
		sb.append("<pollPeriod>" + CondorAdaptor.ParseLongProperty(file, "pollPeriod") + "</pollPeriod>");
		sb.append("<timeout>" + CondorAdaptor.ParseLongProperty(file, "timeout") + "</timeout>");
		sb.append("<isDag>" + CondorAdaptor.ParseBooleanProperty(file, "isDag") + "</isDag>");

		sb.append("</config>");

		return sb.toString();
	}

	private static String getHADOOPResources(String file, HashMap<String, byte[]> wrs) throws Exception {

		StringBuilder sb = new StringBuilder();

		HADOOPArchiveResource[] archives = HadoopAdaptor.GetArchives(file, wrs);
		for (HADOOPArchiveResource res : archives) {
			sb.append("<archives xmlns=\"\">");
			sb.append("<resourceKey>" + res.getResourceKey() + "</resourceKey>");
			sb.append("<resourceAccess>" + res.getResourceAccess() + "</resourceAccess>");
			sb.append("<hdfsPresent>" + res.isHdfsPresent() + "</hdfsPresent>");
			if (res.getInMessageStringPayload() != null)
				sb.append("<inMessageStringPayload>" + res.getInMessageStringPayload() + "</inMessageStringPayload>");
			if (res.getInMessageBytePayload() != null)
				sb.append("<inMessageBytePayload>" + Base64.encode(res.getInMessageBytePayload()) + "</inMessageBytePayload>");
			if (res.getResourceReference() != null)
				sb.append("<resourceReference>" + res.getResourceReference() + "</resourceReference>");
			sb.append("</archives>");
		}

		HADOOPArgumentResource[] arguments = HadoopAdaptor.GetArguments(file);
		for (HADOOPArgumentResource res : arguments) {
			sb.append("<arguments xmlns=\"\">");
			sb.append("<resourceValue>" + res.getResourceValue() + "</resourceValue>");
			sb.append("<order>" + res.getOrder() + "</order>");
			sb.append("</arguments>");
		}

		HADOOPConfigurationResource configuration = HadoopAdaptor.GetConfiguration(file, wrs);
		sb.append("<configuration xmlns=\"\">");
		sb.append("<resourceKey>" + configuration.getResourceKey() + "</resourceKey>");
		sb.append("<resourceAccess>" + configuration.getResourceAccess() + "</resourceAccess>");
		sb.append("<hdfsPresent>" + configuration.isHdfsPresent() + "</hdfsPresent>");
		if (configuration.getInMessageStringPayload() != null)
			sb.append("<inMessageStringPayload>" + configuration.getInMessageStringPayload() + "</inMessageStringPayload>");
		if (configuration.getInMessageBytePayload() != null)
			sb.append("<inMessageBytePayload>" + Base64.encode(configuration.getInMessageBytePayload()) + "</inMessageBytePayload>");
		if (configuration.getResourceReference() != null)
			sb.append("<resourceReference>" + configuration.getResourceReference() + "</resourceReference>");
		sb.append("</configuration>");

		HADOOPFileResource[] files = HadoopAdaptor.GetFiles(file, wrs);
		for (HADOOPFileResource res : files) {
			sb.append("<files xmlns=\"\">");
			sb.append("<resourceKey>" + res.getResourceKey() + "</resourceKey>");
			sb.append("<resourceAccess>" + res.getResourceAccess() + "</resourceAccess>");
			sb.append("<hdfsPresent>" + res.isHdfsPresent() + "</hdfsPresent>");
			if (res.getInMessageStringPayload() != null)
				sb.append("<inMessageStringPayload>" + res.getInMessageStringPayload() + "</inMessageStringPayload>");
			if (res.getInMessageBytePayload() != null)
				sb.append("<inMessageBytePayload>" + Base64.encode(res.getInMessageBytePayload()) + "</inMessageBytePayload>");
			if (res.getResourceReference() != null)
				sb.append("<resourceReference>" + res.getResourceReference() + "</resourceReference>");
			sb.append("</files>");
		}

		HADOOPInputResource[] inputs = HadoopAdaptor.GetInputs(file, wrs);
		for (HADOOPInputResource res : inputs) {
			sb.append("<inputs xmlns=\"\">");
			sb.append("<resourceKey>" + res.getResourceKey() + "</resourceKey>");
			sb.append("<resourceAccess>" + res.getResourceAccess() + "</resourceAccess>");
			sb.append("<cleanup>" + res.isCleanup() + "</cleanup>");
			if (res.getInMessageStringPayload() != null)
				sb.append("<inMessageStringPayload>" + res.getInMessageStringPayload() + "</inMessageStringPayload>");
			if (res.getInMessageBytePayload() != null)
				sb.append("<inMessageBytePayload>" + Base64.encode(res.getInMessageBytePayload()) + "</inMessageBytePayload>");
			if (res.getResourceReference() != null)
				sb.append("<resourceReference>" + res.getResourceReference() + "</resourceReference>");
			sb.append("</inputs>");
		}

		HADOOPJarResource jar = HadoopAdaptor.GetJar(file, wrs);
		sb.append("<jar xmlns=\"\">");
		sb.append("<resourceKey>" + jar.getResourceKey() + "</resourceKey>");
		sb.append("<resourceAccess>" + jar.getResourceAccess() + "</resourceAccess>");
		sb.append("<hdfsPresent>" + jar.isHdfsPresent() + "</hdfsPresent>");
		if (jar.getInMessageStringPayload() != null)
			sb.append("<inMessageStringPayload>" + jar.getInMessageStringPayload() + "</inMessageStringPayload>");
		if (jar.getInMessageBytePayload() != null)
			sb.append("<inMessageBytePayload>" + Base64.encode(jar.getInMessageBytePayload()) + "</inMessageBytePayload>");
		if (jar.getResourceReference() != null)
			sb.append("<resourceReference>" + jar.getResourceReference() + "</resourceReference>");
		sb.append("</jar>");

		HADOOPLibResource[] libs = HadoopAdaptor.GetLibs(file, wrs);
		for (HADOOPLibResource res : libs) {
			sb.append("<libs xmlns=\"\">");
			sb.append("<resourceKey>" + res.getResourceKey() + "</resourceKey>");
			sb.append("<resourceAccess>" + res.getResourceAccess() + "</resourceAccess>");
			sb.append("<hdfsPresent>" + res.isHdfsPresent() + "</hdfsPresent>");
			if (res.getInMessageStringPayload() != null)
				sb.append("<inMessageStringPayload>" + res.getInMessageStringPayload() + "</inMessageStringPayload>");
			if (res.getInMessageBytePayload() != null)
				sb.append("<inMessageBytePayload>" + Base64.encode(res.getInMessageBytePayload()) + "</inMessageBytePayload>");
			if (res.getResourceReference() != null)
				sb.append("<resourceReference>" + res.getResourceReference() + "</resourceReference>");
			sb.append("</libs>");
		}

		HADOOPMainResource main = HadoopAdaptor.GetMain(file);
		sb.append("<main xmlns=\"\">");
		sb.append("<resourceValue>" + main.getResourceValue() + "</resourceValue>");
		sb.append("</main>");

		HADOOPPropertyResource[] properties = HadoopAdaptor.GetProperties(file);
		for (HADOOPPropertyResource res : properties) {
			sb.append("<properties xmlns=\"\">");
			sb.append("<resourceValue>" + res.getResourceValue() + "</resourceValue>");
			sb.append("</properties>");
		}

		FileInfo globalOutputStoreMode = HadoopAdaptor.ParseGlobalOutputStoreMode(file);
		HADOOPOutputResource[] outputs = HadoopAdaptor.GetOutputs(file, globalOutputStoreMode);
		for (HADOOPOutputResource res : outputs) {
			sb.append("<outputs xmlns=\"\">");
			sb.append("<resourceKey>" + res.getResourceKey() + "</resourceKey>");
			sb.append("<resourceAccess>" + res.getResourceAccess() + "</resourceAccess>");
			sb.append("<cleanup>" + res.isCleanup() + "</cleanup>");
			if (res.getResourceReference() != null)
				sb.append("<resourceReference>" + res.getResourceReference() + "</resourceReference>");
			sb.append("</outputs>");
		}

		sb.append("<config xmlns=\"\">");
		sb.append("<chokePerformanceEvents>" + HadoopAdaptor.ParseBooleanProperty(file, "chokePerformanceEvents") + "</chokePerformanceEvents>");
		sb.append("<chokeProgressEvents>" + HadoopAdaptor.ParseBooleanProperty(file, "chokeProgressEvents") + "</chokeProgressEvents>");
		sb.append("</config>");

		return sb.toString();
	}

	private static String rewriteAsWebServices(Map<String, Map<String, String>> maps, Map<String, String> keyValues, HashMap<String, byte[]> wrs,
			HashMap<String, String> jdlrs, WOLConfig wc, String scope) throws IOException {

		Set<String> files = new HashSet<String>();

		StringBuilder sb = new StringBuilder();

		sb.append("[\n");
		for (Entry<String, String> entry : keyValues.entrySet()) {
			String key = entry.getKey();
			if (key.equals(JDLParsingUtils.NODES))
				continue;
			else {
				sb.append(key + " = " + entry.getValue() + ";\n");
			}
		}

		sb.append("Nodes =\n[");
		for (String node : maps.keySet()) {

			String tempfile = "wol-" + UUID.randomUUID().toString() + ".txt";
			String tempfilePath = WorkflowOrchestrationLayerService.tempDir + File.separator + tempfile;
			files.add(tempfilePath);
			
			
			sb.append("\n" + node + " =\n[\n\t" + JDLParsingUtils.DESCRIPTION + " =\n\t[");

			sb.append("\n\t\tJobType = \"WS\";");
			sb.append("\n\t\tExecutable = \"WEBSERVICE\";");
			sb.append("\n\t\tArguments = \"\";");
			sb.append("\n\t\tStdOutput = \"std.out\";");
			sb.append("\n\t\tInputSandbox = {");
			sb.append("\n\t\t\t\"" + tempfile + "\"");
			sb.append("\n\t\t};");
			sb.append("\n\t\tOutputSandbox = {\n\t\t\t\"std.out\"\n\t\t};");
			sb.append("\n\t\tRetryCount = " + maps.get(node).get("RetryCount") + ";");
			sb.append("\n\t\tRetryInterval = " + maps.get(node).get("RetryInterval") + ";");

			sb.append("\n\t];\n];");

			String type = maps.get(node).get("Executable");
			String resourceFile = maps.get(node).get("Arguments").replaceAll("\"", "");
			byte[] resource = wrs.get(resourceFile);
			logger.info("Resource: " + resource);
			String resourcePath = null;
			try {
				resourcePath = turnBytesToFile(resource);
			} catch (IOException e) {
				logger.error("Exception creating file from bytes", e);
				return null;
			}
			boolean retVal = false;
			if (type.equals(GRID))
				retVal = createGRIDFile(resourcePath, tempfilePath, wrs, scope);
			else if (type.equals(PE2NG))
				retVal = createPE2NGFile(resourcePath, tempfilePath, wrs, jdlrs, scope);
			else if (type.equals(CONDOR))
				retVal = createCONDORFile(resourcePath, tempfilePath, wrs, scope);
			else if (type.equals(HADOOP))
				retVal = createHADOOPFile(resourcePath, tempfilePath, wrs, scope);
			if (!retVal)
				return null;
		}
		sb.append("\n\n];\n];");

		File jdl = File.createTempFile("wol-", ".txt", WorkflowOrchestrationLayerService.tempDir);
		jdl.deleteOnExit();
		PrintWriter writer = new PrintWriter(jdl.getAbsolutePath());
		writer.println(sb.toString());
		writer.close();

		File resource = File.createTempFile("wol-", ".txt", WorkflowOrchestrationLayerService.tempDir);
		resource.deleteOnExit();
		writer = new PrintWriter(resource.getAbsolutePath());
		writer.println("scope" + " # " + scope);
		writer.println("chokePerformanceEvents" + " # " + wc.isChokePerformanceEvents());
		writer.println("chokeProgressEvents" + " # " + wc.isChokeProgressEvents());
		writer.println("queueSupport" + " # " + wc.isQueueSupport());
		writer.println("passedBy" + " # " + wc.getPassedBy());
		writer.println("utilization" + " # " + wc.getUtilization());
		writer.println("jdl" + " # " + jdl.getAbsolutePath());
		for (String indata : files) {
			File file = new File(indata);
			logger.info("Writing the following: " + "inData # " + file.getName() + " # local # " + file.getAbsolutePath());
			writer.println("inData # " + file.getName() + " # local # " + file.getAbsolutePath());
		}
		writer.flush();
		writer.close();

		return resource.getAbsolutePath();
	}

	private static boolean createGRIDFile(String resourcePath, String tempfile, HashMap<String, byte[]> wrs, String scope) {
		Writer writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempfile)));
			String endpoint = null;
			try {
				endpoint = getEndpoint(scope, Type.GRID);
			} catch (Exception e) {
				logger.error("Failed to find endpoint...");
				return false;
			}
			if(endpoint==null)
			{
				logger.error("Null endpoint");
				return false;
			}
			writer.write("serviceClass=Execution\nserviceName=WorkflowEngineService\nTo=" + endpoint
					+ "\noperation=adaptGRID\nOutputLocatorExtractionExpression=//*[local-name()='grid']\nbody=");
			writer.write("<adaptGRID xmlns=\"http://gcube.org/execution/workflowengine\"><executionLease xmlns=\"\">0</executionLease>");
			try {
				writer.write(getGRIDResources(resourcePath, wrs).replace("\n", ""));
			} catch (Exception e) {
				throw new RuntimeException("Exception while retrieving the GRID resources");
			}
			writer.write("</adaptGRID>");
		} catch (IOException ex) {
			logger.info("Error writing GRID file", ex);
			ex.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
				logger.error("Failed to close writer", ex);
				ex.printStackTrace();
			}
		}
		return true;
	}

	private static boolean createPE2NGFile(String resourcePath, String tempfile, HashMap<String, byte[]> wrs, HashMap<String, String> jdlrs, String scope) {
		Writer writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempfile)));
			String endpoint = null;
			try {
				endpoint = getEndpoint(scope, Type.PE2NG);
				if(endpoint==null)
				{
					logger.error("Null endpoint");
					return false;
				}
			} catch (Exception e) {
				logger.error("Failed to find endpoint...");
				return false;
			}
			writer.write("serviceClass=Execution\nserviceName=WorkflowEngineService\nTo=" + endpoint
					+ "\noperation=adaptJDL\nOutputLocatorExtractionExpression=//*[local-name()='jdl']\nbody=");
			writer.write("<adaptJDL xmlns=\"http://gcube.org/execution/workflowengine\"><executionLease xmlns=\"\">0</executionLease>");
			try {
				logger.info("Getting PE2NGResources from " + resourcePath);
				writer.write(getPE2NGResources(resourcePath, wrs, jdlrs).replace("\n", ""));
			} catch (Exception e) {
				throw new RuntimeException("Exception while retrieving the PE2NG resources");
			}
			writer.write("</adaptJDL>");
		} catch (IOException ex) {
			logger.info("Error writing PE2NG file", ex);
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
				logger.error("Failed to close writer", ex);
			}
		}
		return true;
	}

	private static boolean createCONDORFile(String resourcePath, String tempfile, HashMap<String, byte[]> wrs, String scope) {
		Writer writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempfile)));
			String endpoint = null;
			try {
				endpoint = getEndpoint(scope, Type.CONDOR);
			} catch (Exception e) {
				logger.error("Failed to find endpoint...");
				return false;
			}
			if(endpoint==null)
			{
				logger.error("Null endpoint");
				return false;
			}
			writer.write("serviceClass=Execution\nserviceName=WorkflowEngineService\nTo=" + endpoint
					+ "\noperation=adaptCONDOR\nOutputLocatorExtractionExpression=//*[local-name()='condor']\nbody=");
			writer.write("<adaptCONDOR xmlns=\"http://gcube.org/execution/workflowengine\"><executionLease xmlns=\"\">0</executionLease>");
			try {
				writer.write(getCONDORResources(resourcePath, wrs).replace("\n", ""));
			} catch (Exception e) {
				throw new RuntimeException("Exception while retrieving the GRID resources");
			}
			writer.write("</adaptCONDOR>");
		} catch (IOException ex) {
			logger.info("Error writing GRID file", ex);
			ex.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
				logger.error("Failed to close writer", ex);
				ex.printStackTrace();
			}
		}
		return true;
	}

	private static boolean createHADOOPFile(String resourcePath, String tempfile, HashMap<String, byte[]> wrs, String scope) {
		Writer writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempfile)));
			String endpoint = null;
			try {
				endpoint = getEndpoint(scope, Type.HADOOP);
			} catch (Exception e) {
				logger.error("Failed to find endpoint...");
				return false;
			}
			if(endpoint==null)
			{
				logger.error("Null endpoint");
				return false;
			}
			writer.write("serviceClass=Execution\nserviceName=WorkflowEngineService\nTo=" + endpoint
					+ "\noperation=adaptHADOOP\nOutputLocatorExtractionExpression=//*[local-name()='hadoop']\nbody=");
			writer.write("<adaptHADOOP xmlns=\"http://gcube.org/execution/workflowengine\"><executionLease xmlns=\"\">0</executionLease>");
			try {
				writer.write(getHADOOPResources(resourcePath, wrs).replace("\n", ""));
			} catch (Exception e) {
				throw new RuntimeException("Exception while retrieving the GRID resources");
			}
			writer.write("</adaptHADOOP>");
		} catch (IOException ex) {
			logger.info("Error writing GRID file", ex);
			ex.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
				logger.error("Failed to close writer", ex);
				ex.printStackTrace();
			}
		}
		return true;
	}

	public static String getEndpoint(String scope, Type type) throws Exception {
		String choice = null;
		logger.info("Finding endpoint for: "+type.toString());
		switch (type) {
		case GRID:
			choice = ((String) WOLServiceContext.getContext().getProperty(WOLServiceContext.GRID_NODE, false));
			break;
		case PE2NG:
			choice = ((String) WOLServiceContext.getContext().getProperty(WOLServiceContext.PE2NG_NODE, false));
			break;
		case CONDOR:
			choice = ((String) WOLServiceContext.getContext().getProperty(WOLServiceContext.CONDOR_NODE, false));
			break;
		case HADOOP:
			choice = ((String) WOLServiceContext.getContext().getProperty(WOLServiceContext.HADOOP_NODE, false));
			break;
		}
		logger.info("Endpoint retrieved from jndi is: " + choice);

		List<String> endpoints = new ArrayList<String>();
		
		ScopeProvider.instance.set(scope);

		SimpleQuery query = queryFor(GCoreEndpoint.class);

		query.addCondition("$resource/Profile/ServiceClass/text() eq 'Execution'").addCondition(
				"$resource/Profile/ServiceName/text() eq 'WorkflowEngineService'");

		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);

		List<GCoreEndpoint> resources = client.submit(query);

		for (GCoreEndpoint resource : resources) {
			for (Endpoint endpoint : resource.profile().endpoints()) {
				if(choice!=null && endpoint.uri().toString().equals(choice))
				{
					logger.info("Chose endpoint found: "+endpoint.uri().toString());
					return endpoint.uri().toString();
				}
				endpoints.add(endpoint.uri().toString());
			}
		}
		if(choice==null)
		{
			String endpoint = endpoints.get(new Random().nextInt(endpoints.size()));
			logger.info("Picked endpoint: "+endpoint);
			return null;
		}
			
		else
			return null;
	}

	public static void main(String[] args) // throws MissingArgumentException,
											// IOException
	{

		// String resouceFile = transform(args[0]);
		// System.out.println("RESOURCE FILE: "+resouceFile);
		// AdaptorBase adaptor = AdaptorFactory.createAdaptor("JDL");
		// String[] newArgs = {resouceFile,"/tmp/something.txt"};
		// adaptor.execute(newArgs);

		// SimpleQuery query = queryFor(GCoreEndpoint.class);
		//
		// query.setResult("$resource/Profile/AccessPoint");
		//
		// DiscoveryClient<AccessPoint> client = clientFor(AccessPoint.class);
		//
		// List<AccessPoint> accesspoints = client.submit(query);
		//
		// for (AccessPoint point : accesspoints) {
		// System.out.println(point.description()+"  "+point.address()+" "+point.name());
		// // ...point.name()....point.address()....
		// }
	}

}
