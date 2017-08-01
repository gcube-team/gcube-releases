package org.gcube.data.simulfishgrowthdata.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class CalcKPIAlgorithmExecutor {
	private static final Logger logger = LoggerFactory.getLogger(CalcKPIAlgorithmExecutor.class);

	boolean ignoreWhitespace = false;
	boolean ignoreComments = false;
	boolean putCDATAIntoText = false;
	boolean createEntityRefs = false;
	static final String outputEncoding = "UTF-8";
	static final String formatQueryParam = "&%s=%s";
	static final String formatAlgorithmParam = "%s=%s;";
	static final String RESULT_TYPE = "OutputTable";

	String mIdentification;
	String mToken;
	String mParamDbName;
	String mParamUserName;
	String mParamUserPassword;
	String mParamDbHost;
	String mParamSimulModelId;
	String mEndpointName;

	private Exception exception;

	public CalcKPIAlgorithmExecutor(String endpointName, String identification, String token) {
		mEndpointName = endpointName;
		mIdentification = identification;
		mToken = token;
	}

	public CalcKPIAlgorithmExecutor setConnectionInfo(String dbName, String userName, String password, String host) {
		mParamDbName = dbName;
		mParamUserName = userName;
		mParamUserPassword = password;
		mParamDbHost = host;

		return this;
	}

	public CalcKPIAlgorithmExecutor setModelId(String id) {
		mParamSimulModelId = id;

		return this;
	}

	public void execute(String endPointUrl) throws Exception {
		debugEcho = new DOMEcho(new PrintWriter(new OutputStreamWriter(System.out, outputEncoding), true));

		processOutput(triggerAlgorithm(endPointUrl, mToken));

	}

	private String triggerAlgorithm(String urlEndPoint, String token) throws Exception {

		Map<String, String> algorithmParams = new LinkedHashMap<>();
		algorithmParams.put("KPI.name.db", mParamDbName);
		algorithmParams.put("KPI.user.name", mParamUserName);
		algorithmParams.put("KPI.user.password", mParamUserPassword);
		algorithmParams.put("KPI.user.host", mParamDbHost);
		algorithmParams.put("KPI.simulmodel.id", mParamSimulModelId);

		StringBuilder algoParamsBuilder = new StringBuilder();
		for (Entry<String, String> entry : algorithmParams.entrySet()) {
			algoParamsBuilder.append(String.format(formatAlgorithmParam, entry.getKey(), entry.getValue()));
		}

		String uriString = new StringBuilder().append(urlEndPoint).append("?")
				.append(String.format(formatQueryParam, "request", "Execute"))
				.append(String.format(formatQueryParam, "service", "WPS"))
				.append(String.format(formatQueryParam, "Version", "1.0.0"))
				.append(String.format(formatQueryParam, "gcube-token", token))
				.append(String.format(formatQueryParam, "lang", "en-US"))
				.append(String.format(formatQueryParam, "Identifier", mIdentification))
				.append(String.format(formatQueryParam, "DataInputs",
						URLEncoder.encode(algoParamsBuilder.toString(), "UTF-8")))
				.toString();
		URI uri = URI.create(uriString);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("calling uri [%s]", uri));
		}

		String toRet = "";
		HttpGet request = new HttpGet(uri);
		request.addHeader("Accept", "application/xml");
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse resp = httpclient.execute((HttpUriRequest) request);
		toRet = getContents(resp);

		return toRet;

	}

	private String getContents(final CloseableHttpResponse resp) throws Exception {

		String str = "";
		String exceptionMessage = null;
		try {
			StatusLine statusLine = resp.getStatusLine();
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("status line is [%s]", statusLine));
			}
			if (statusLine.getStatusCode() != HttpURLConnection.HTTP_OK) {
				exceptionMessage = "The server responded: " + statusLine.getReasonPhrase();
			}

			HttpEntity entity = resp.getEntity();
			if (entity != null) {
				InputStream is = entity.getContent();
				try {
					// do something useful
					Scanner s = new Scanner(is, StandardCharsets.UTF_8.name()).useDelimiter("\\A");
					str = s.hasNext() ? s.next() : "";
				} finally {
					is.close();
				}
				if (logger.isTraceEnabled()) {
					logger.trace(String.format("response is [%s]", str));
				}
			}
		} finally {
			resp.close();
		}
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("contentes ~~~%s~~~", str));
		}
		if (exceptionMessage != null) {
			throw new Exception(exceptionMessage, new Exception(str));
		}
		return str;
	}

	private void processOutput(String output)
			throws ParserConfigurationException, SAXException, IOException, Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringComments(ignoreComments);
		dbf.setIgnoringElementContentWhitespace(ignoreWhitespace);
		dbf.setCoalescing(putCDATAIntoText);
		dbf.setExpandEntityReferences(!createEntityRefs);
		//
		DocumentBuilder db = dbf.newDocumentBuilder();
		db.setErrorHandler(
				new MyErrorHandler(new PrintWriter(new OutputStreamWriter(System.err, outputEncoding), true)));

		InputStream asStream = new ByteArrayInputStream(output.getBytes(StandardCharsets.UTF_8));
		Document doc = db.parse(asStream);

		// if (logger.isTraceEnabled()) {
		// System.out.println("processOutput
		// ===========================================");
		// debugEcho.echo(doc);
		// }

		if (logger.isTraceEnabled()) {
			logger.trace(String.format("doc.getFirstChild().getNodeName() [%s]", doc.getFirstChild().getNodeName()));

			for (int i = 0; i < doc.getChildNodes().getLength(); i++) {
				Node node = doc.getChildNodes().item(i);
				logger.trace(String.format("doc child at [%s] is [%s]", i, node.getNodeName()));
				if (node.hasChildNodes()) {
					for (int j = 0; j < node.getChildNodes().getLength(); j++) {
						Node node2 = node.getChildNodes().item(j);
						logger.trace(String.format("child at [%s] is [%s]", j, node2.getNodeName()));
					}
				}
			}

		}
		processResponse(doc);
	}

	private void processResponse(Document doc) throws ParserConfigurationException, SAXException, IOException {
		ResultData resultData = retrieveResultData(doc);
		if (logger.isTraceEnabled())
			logger.trace(String.format("Result data ~~~%s~~~", resultData));

	}

	private ResultData retrieveResultData(Document doc) {
		ResultData resultData = null;

		Node nodeOutput = findSubNode("wps:ProcessOutputs", doc.getFirstChild());
		if (logger.isTraceEnabled())
			logger.trace("wps:ProcessOutputs ===========================================");
		debugEcho.echo(nodeOutput);
		if (nodeOutput != null) {
			Node nodeData = findSubNode("wps:ComplexData", nodeOutput, true);
			if (logger.isTraceEnabled())
				logger.trace("wps:ComplexData ===========================================");
			debugEcho.echo(nodeData);
			if (nodeData != null) {
				resultData = new ResultData(nodeData.getAttributes().getNamedItem("mimeType").getTextContent(),
						nodeData.getTextContent());
			}
		}

		return resultData;
	}

	static class ResultData {
		public String type;
		public String data;

		public ResultData(String type, String data) {
			this.type = type;
			this.data = data;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder().append("ResultData [ ").append(" type=").append(type)
					.append(", data=").append(data).append("]");
			return builder.toString();
		}

	}

	// -----------------------------------------------------------------
	//
	// additional for org dom instead of dom4j
	//
	// -----------------------------------------------------------------
	DOMEcho debugEcho;

	public Node findSubNode(String name, Node node, boolean recurse) {
		if (node.getNodeType() != Node.ELEMENT_NODE) {
			if (logger.isTraceEnabled())
				logger.trace("Error: Search node not of element type");
			return null;
		}

		if (!node.hasChildNodes())
			return null;

		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node subnode = list.item(i);
			if (subnode.getNodeType() == Node.ELEMENT_NODE) {
				if (subnode.getNodeName().equals(name))
					return subnode;
			}
		}
		if (recurse) {
			list = node.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				Node subnode = list.item(i);
				Node found = null;
				if ((found = findSubNode(name, subnode, true)) != null) {
					return found;
				}
			}
		}
		return null;
	}

	public Node findSubNode(String name, Node node) {
		return findSubNode(name, node, false);
	}

	static public String getText(Node node) {
		StringBuffer result = new StringBuffer();
		if (!node.hasChildNodes())
			return "";

		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node subnode = list.item(i);
			if (subnode.getNodeType() == Node.TEXT_NODE) {
				result.append(subnode.getNodeValue());
			} else if (subnode.getNodeType() == Node.CDATA_SECTION_NODE) {
				result.append(subnode.getNodeValue());
			} else if (subnode.getNodeType() == Node.ENTITY_REFERENCE_NODE) {
				// Recurse into the subtree for text
				// (and ignore comments)
				result.append(getText(subnode));
			}
		}

		return result.toString();
	}

	private static class MyErrorHandler implements ErrorHandler {

		private PrintWriter out;

		MyErrorHandler(PrintWriter out) {
			this.out = out;
		}

		private String getParseExceptionInfo(SAXParseException spe) {
			String systemId = spe.getSystemId();
			if (systemId == null) {
				systemId = "null";
			}

			String info = "URI=" + systemId + " Line=" + spe.getLineNumber() + ": " + spe.getMessage();
			return info;
		}

		public void warning(SAXParseException spe) throws SAXException {
			out.println("Warning: " + getParseExceptionInfo(spe));
		}

		public void error(SAXParseException spe) throws SAXException {
			String message = "Error: " + getParseExceptionInfo(spe);
			throw new SAXException(message);
		}

		public void fatalError(SAXParseException spe) throws SAXException {
			String message = "Fatal Error: " + getParseExceptionInfo(spe);
			throw new SAXException(message);
		}
	}

	// debug
	private static class DOMEcho {
		private int indent = 0;
		private final String basicIndent = " ";
		private PrintWriter out;

		public DOMEcho(PrintWriter out) {
			this.out = out;
		}

		private void echo(Node n) {
			if (false) {
				out.print("DOMEcho.echo disabled");
				return;
			}

			if (n == null) {
				out.print("~~~ null node ~~~");
				return;
			}
			outputIndentation();
			int type = n.getNodeType();

			switch (type) {
			case Node.ATTRIBUTE_NODE:
				out.print("ATTR:");
				printlnCommon(n);
				break;

			case Node.CDATA_SECTION_NODE:
				out.print("CDATA:");
				printlnCommon(n);
				break;

			case Node.COMMENT_NODE:
				out.print("COMM:");
				printlnCommon(n);
				break;

			case Node.DOCUMENT_FRAGMENT_NODE:
				out.print("DOC_FRAG:");
				printlnCommon(n);
				break;

			case Node.DOCUMENT_NODE:
				out.print("DOC:");
				printlnCommon(n);
				break;

			case Node.DOCUMENT_TYPE_NODE:
				out.print("DOC_TYPE:");
				printlnCommon(n);
				NamedNodeMap nodeMap = ((DocumentType) n).getEntities();
				indent += 2;
				for (int i = 0; i < nodeMap.getLength(); i++) {
					Entity entity = (Entity) nodeMap.item(i);
					echo(entity);
				}
				indent -= 2;
				break;

			case Node.ELEMENT_NODE:
				out.print("ELEM:");
				printlnCommon(n);

				NamedNodeMap atts = n.getAttributes();
				indent += 2;
				for (int i = 0; i < atts.getLength(); i++) {
					Node att = atts.item(i);
					echo(att);
				}
				indent -= 2;
				break;

			case Node.ENTITY_NODE:
				out.print("ENT:");
				printlnCommon(n);
				break;

			case Node.ENTITY_REFERENCE_NODE:
				out.print("ENT_REF:");
				printlnCommon(n);
				break;

			case Node.NOTATION_NODE:
				out.print("NOTATION:");
				printlnCommon(n);
				break;

			case Node.PROCESSING_INSTRUCTION_NODE:
				out.print("PROC_INST:");
				printlnCommon(n);
				break;

			case Node.TEXT_NODE:
				out.print("TEXT:");
				printlnCommon(n);
				break;

			default:
				out.print("UNSUPPORTED NODE: " + type);
				printlnCommon(n);
				break;
			}

			indent++;
			for (Node child = n.getFirstChild(); child != null; child = child.getNextSibling()) {
				echo(child);
			}
			indent--;
		}

		private void outputIndentation() {
			for (int i = 0; i < indent; i++) {
				out.print(basicIndent);
			}
		}

		private void printlnCommon(Node n) {
			out.print(" nodeName=\"" + n.getNodeName() + "\"");

			String val = n.getNamespaceURI();
			if (val != null) {
				out.print(" uri=\"" + val + "\"");
			}

			val = n.getPrefix();

			if (val != null) {
				out.print(" pre=\"" + val + "\"");
			}

			val = n.getLocalName();
			if (val != null) {
				out.print(" local=\"" + val + "\"");
			}

			val = n.getNodeValue();
			if (val != null) {
				out.print(" nodeValue=");
				if (val.trim().equals("")) {
					// Whitespace
					out.print("[WS]");
				} else {
					out.print("\"" + n.getNodeValue() + "\"");
				}
			}
			out.println();
		}

	}

	public static void main(String[] args) throws Exception {

	}

}
