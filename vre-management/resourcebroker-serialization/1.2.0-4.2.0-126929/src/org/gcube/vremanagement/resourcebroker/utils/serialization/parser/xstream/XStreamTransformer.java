/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: XStreamSerializer.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.utils.serialization.parser.xstream;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PackageElem;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PackageGroup;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanRequest;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanResponse;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.feedback.DeployNode;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.feedback.Feedback;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.requirements.Requirement;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * Provides the factory for transforming both
 * {@link org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanRequest} and {@link PlanResponse} objects.
 *<pre>
 *<b>Usage:</b>
 *
 *	<i>// Instantiates the PlanRequest.</i>
 * 	{@link org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanRequest} planReq = new {@link org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanRequest} ("/gcube/devsec");
 * 	...
 *	{@link XStreamTransformer} transformer = new {@link XStreamTransformer}();
 *	<i>// from PlanRequest -> XML</i>
 *	String xml = transformer.toXML(planReq);
 *	...
 *	<i>// from XML -> PlanRequest.</i>
 *	{@link org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanRequest} tReq = transformer.getRequestfromXML(xml);
 *</pre>
 * @author Daniele Strollo (ISTI-CNR)
 */
public class XStreamTransformer {

	private GCUBELog logger = new GCUBELog(this, Configuration.LOGGING_PREFIX);

	/**
	 * The list of types supported by this serializer.
	 * Keeps association between the class names of supported
	 * types and their relative namespaces.
	 *
	 * @author Daniele Strollo (ISTI-CNR)
	 */
	enum SupportedTypes {
		XML_PLAN_REQUEST("PlanRequest",
				"http://gcube-system.org/namespaces/resourcebroker/broker/xsd/deployRequest",
				"share/schema/" +
				Configuration.PRJ_PACKAGE_NAME +
				"/deployRequest.xsd"),

		XML_PLAN_RESPONSE("PlanResponse",
				"http://gcube-system.org/namespaces/resourcebroker/broker/xsd/deployResponse",
				"share/schema/" +
				Configuration.PRJ_PACKAGE_NAME +
				"/deployResponse.xsd"),

		XML_PLAN_FEEDBACK("Feedback",
				"http://gcube-system.org/namespaces/resourcebroker/broker/xsd/deployFeedback",
				"share/schema/" +
				Configuration.PRJ_PACKAGE_NAME +
				"/deployFeedback.xsd");

		private String value = null;
		private String namespace = null;
		private String validationSchema = null;
		SupportedTypes(final String value, final String namespace, final String validationSchema) {
			this.value = value;
			this.namespace = namespace;
			this.validationSchema = validationSchema;
		}
		public String value() {
			return this.value;
		}
		public String namespace() {
			return this.namespace;
		}
		public String validationSchema() {
			return this.validationSchema;
		}
	}

	// Two different xstream handlers have been defined
	// to support the different namespace mapping
	// and the aliasing that otherwise can clash.
	private XStream xstreamRequestHandler  = null;
	private XStream xstreamResponseHandler  = null;
	private XStream xstreamFeedbackHandler = null;

	// Here the defaults initializations for all the xstream
	// serializers.
	private synchronized void initGlobalParams(XStream xstream) {
		// The implicit constructor for List elems.
		xstream.addDefaultImplementation(Vector.class, List.class);

		// Aliases
		xstream.alias(PackageGroup.NODE_TAG, PackageGroup.class);
		xstream.alias(PackageElem.NODE_TAG, PackageElem.class);
		xstream.alias(Requirement.NODE_TAG, Requirement.class);
		xstream.alias(DeployNode.NODE_TAG, DeployNode.class);

		xstream.addImplicitCollection(PackageGroup.class, "packages");
	}

	private synchronized void initRequestHandler() {
		logger.debug("[XSTREAM] initializing the request handler");
		if (this.xstreamRequestHandler != null) {
			return;
		}

		QNameMap qmap = new QNameMap();
		// registers the mapping binding between PlanRequest and its
		// namespace.
		qmap.registerMapping(
				new QName(
						SupportedTypes.XML_PLAN_REQUEST.namespace(),
						SupportedTypes.XML_PLAN_REQUEST.value()), PlanRequest.class);

		// NOTE workaround
		// the namespace works only if used the StaxDriver in place of custom one
		// and removing the alias for PlanRequest
		// -- StaxDriver driver = new CustomStaxDriver(qmap);
		StaxDriver driver = new StaxDriver(qmap);
		driver.setRepairingNamespace(false);

		xstreamRequestHandler = new XStream(driver);
		xstreamRequestHandler.processAnnotations(PlanRequest.class);
		// -- xstream.alias("PlanRequest", PlanRequest.class);
		xstreamRequestHandler.addImplicitCollection(PlanRequest.class, "packageGroups");

		this.initGlobalParams(xstreamRequestHandler);
	}

	private synchronized void initResponseHandler() {
		logger.debug("[XSTREAM] initializing the response handler");
		if (this.xstreamResponseHandler != null) {
			return;
		}

		QNameMap qmap = new QNameMap();
		qmap.registerMapping(
				new QName(
						SupportedTypes.XML_PLAN_RESPONSE.namespace(),
						SupportedTypes.XML_PLAN_RESPONSE.value()), PlanResponse.class);

		// NOTE workaround
		// the namespace works only if used the StaxDriver in place of custom one
		// and removing the alias for PlanRequest
		// -- StaxDriver driver = new CustomStaxDriver(qmap);
		StaxDriver driver = new StaxDriver(qmap);
		driver.setRepairingNamespace(false);

		xstreamResponseHandler = new XStream(driver);
		xstreamResponseHandler.processAnnotations(PlanResponse.class);
		xstreamResponseHandler.addImplicitCollection(PlanResponse.class, "packageGroups");
		this.initGlobalParams(xstreamResponseHandler);
	}

	private synchronized void initFeedbackHandler() {
		logger.debug("[XSTREAM] initializing the feedback handler");
		if (this.xstreamFeedbackHandler != null) {
			return;
		}

		QNameMap qmap = new QNameMap();
		// registers the mapping binding between PlanRequest and its
		// namespace.
		qmap.registerMapping(
				new QName(
						SupportedTypes.XML_PLAN_FEEDBACK.namespace(),
						SupportedTypes.XML_PLAN_FEEDBACK.value()), Feedback.class);

		// NOTE workaround
		// the namespace works only if used the StaxDriver in place of custom one
		// and removing the alias for PlanRequest
		// -- StaxDriver driver = new CustomStaxDriver(qmap);
		StaxDriver driver = new StaxDriver(qmap);
		driver.setRepairingNamespace(false);

		xstreamFeedbackHandler = new XStream(driver);
		xstreamFeedbackHandler.processAnnotations(Feedback.class);
		xstreamFeedbackHandler.addImplicitCollection(Feedback.class, "deployNodes");
		this.initGlobalParams(xstreamFeedbackHandler);
	}

	/**
	 * Given an string containing an XML unformatted tree returns
	 * its formatted representation.
	 * For internal use only.
	 * @return the formatted string for the input XML
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	private String formatXML(String param) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(param));
		final Document document = db.parse(is);
		OutputFormat format = new OutputFormat(document);
        format.setLineWidth(65);
        format.setIndenting(true);
        format.setIndent(2);
        Writer out = new StringWriter();
        XMLSerializer serializer = new XMLSerializer(out, format);
        serializer.serialize(document);

        return out.toString();
	}

	/**
	 * Given a {@link org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanRequest} element it is serialized in its XML
	 * representation.
	 *
	 * @param request the object to serialize
	 * @return the XML representation of input {@link org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanRequest}
	 * @throws GCUBEFault
	 */
	public final String toXML(final PlanRequest request)
	throws GCUBEFault {
		// checks parameters
		if (request == null) {
			throw new GCUBEFault("Invalid " + SupportedTypes.XML_PLAN_REQUEST.value() + ": null value not allowed.");
		}
		// starts serialization
		this.initRequestHandler();

		String unformattedXml = this.xstreamRequestHandler.toXML(request);
		try {
			return formatXML(unformattedXml);
		} catch (Exception e) {
			// if the formatting fails return the unformatted one
			return unformattedXml;
		}
	}

	/**
	 * Given a {@link PlanResponse} element it is serialized in its XML
	 * representation.
	 *
	 * @param response the object to serialize
	 * @return the XML representation of input {@link PlanResponse}
	 * @throws GCUBEFault
	 */
	public final String toXML(final PlanResponse response)
	throws GCUBEFault {
		// checks parameters
		if (response == null) {
			throw new GCUBEFault("Invalid " + SupportedTypes.XML_PLAN_RESPONSE.value() + ": null value not allowed.");
		}
		// starts serialization
		this.initResponseHandler();
		String unformattedXml = this.xstreamResponseHandler.toXML(response);
		try {
			return formatXML(unformattedXml);
		} catch (Exception e) {
			// if the formatting fails return the unformatted one
			return unformattedXml;
		}
	}

	/**
	 * Given a
	 * {@link org.gcube.vremanagement.resourcebroker.utils.serialization.types.feedback.Feedback}
	 * element it is serialized in its XML representation.
	 * 
	 * @param feedback
	 *            the object to serialize
	 * @return the XML representation of input
	 *         {@link org.gcube.vremanagement.resourcebroker.utils.serialization.types.feedback.Feedback}
	 * @throws GCUBEFault
	 */
	public final String toXML(final Feedback feedback)
	throws GCUBEFault {
		// checks parameters
		if (feedback == null) {
			throw new GCUBEFault("Invalid " + SupportedTypes.XML_PLAN_FEEDBACK.value() + ": null value not allowed.");
		}
		// starts serialization
		this.initFeedbackHandler();
		String unformattedXml = this.xstreamFeedbackHandler.toXML(feedback);
		try {
			return formatXML(unformattedXml);
		} catch (Exception e) {
			// if the formatting fails return the unformatted one
			return unformattedXml;
		}
	}


	/**
	 * Given an XML representation of a {@link org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanRequest} instance
	 * it returns its object representation.
	 *
	 * @param xml the serialized {@link org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanRequest} element.
	 * @param validate if the xml input must be validated.
	 * @return the object representation of {@link org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanRequest}.
	 * @throws GCUBEFault if the parameter is null or if validation fails (when required).
	 */
	public final PlanRequest getRequestFromXML(final String xml, final boolean validate)
	throws GCUBEFault {
		if (xml == null) {
			throw new GCUBEFault("Null value received during XML serialization of " + SupportedTypes.XML_PLAN_REQUEST.value());
		}
		// Validate the XML if required
		if (validate) {
			try {
				if (!this.validate(SupportedTypes.XML_PLAN_REQUEST.validationSchema(), xml)) {
					throw new GCUBEFault("The given " + SupportedTypes.XML_PLAN_REQUEST.value() + " is not valid.");
				}
			} catch (IOException e) {
				throw new GCUBEFault("The validation schema " + SupportedTypes.XML_PLAN_REQUEST.validationSchema() + " for " + SupportedTypes.XML_PLAN_REQUEST.value() + " cannot be found.");
			} catch (SAXException e) {
				throw new GCUBEFault("The " + SupportedTypes.XML_PLAN_REQUEST.value() + " given is not valid.");
			}
		}

		this.initRequestHandler();
		PlanRequest retval = (PlanRequest) xstreamRequestHandler.fromXML(xml);
		this.initPackageGroups(retval.getPackageGroups());
		return retval;
	}

	/**
	 * Given an XML representation of a {@link Feedback} instance
	 * it returns its object representation.
	 *
	 * @param xml the serialized {@link Feedback} element.
	 * @param validate if the xml input must be validated.
	 * @return the object representation of {@link Feedback}.
	 * @throws GCUBEFault if the parameter is null or if validation fails (when required).
	 */
	public final Feedback getFeedbackFromXML(final String xml, final boolean validate)
	throws GCUBEFault {
		if (xml == null) {
			throw new GCUBEFault("Null value received during XML serialization of " + SupportedTypes.XML_PLAN_FEEDBACK.value());
		}
		// Validate the XML if required
		if (validate) {
			try {
				if (!this.validate(SupportedTypes.XML_PLAN_FEEDBACK.validationSchema(), xml)) {
					throw new GCUBEFault("The given " + SupportedTypes.XML_PLAN_FEEDBACK.value() + " is not valid.");
				}
			} catch (IOException e) {
				throw new GCUBEFault(e, "The validation schema " + SupportedTypes.XML_PLAN_FEEDBACK.validationSchema() + " for " + SupportedTypes.XML_PLAN_FEEDBACK.value() + " cannot be found.");
			} catch (SAXException e) {
				throw new GCUBEFault(e, "The " + SupportedTypes.XML_PLAN_FEEDBACK.value() + " given is not valid.");
			}
		}

		this.initFeedbackHandler();
		Feedback retval = (Feedback) xstreamFeedbackHandler.fromXML(xml);
		return retval;
	}

	/**
	 * Initializes the {@link PackageGroup} elements inside a request
	 * or response once it has been deserialized from the XML.
	 * This ensures that an unique ID is assigned to each {@link PackageGroup}
	 * before returning them.
	 * For internal use only invoked inside getRequestfromXML getResponsefromXML.
	 */
	private void initPackageGroups(List<PackageGroup> pgs) {
		if (pgs == null || pgs.size() == 0) {
			return;
		}
		int i = 1;
		for (PackageGroup pg : pgs) {
			if (pg.getID() == null) {
				// a new random ID will be assigned
				// FIXME assigns a random string
				// pg.setID(null);
				pg.setID(String.valueOf(i++));
			}
		}
	}

	/**
	 * Given an XML representation of a {@link PlanResponse} instance
	 * it returns its object representation.
	 *
	 * @param xml the serialized {@link PlanResponse} element.
	 * @param validate if the xml input must be validated.
	 * @return the object representation of {@link PlanResponse}.
	 * @throws GCUBEFault if the parameter is null or if validation fails (when required).
	 */
	public final PlanResponse getResponseFromXML(final String xml, final boolean validate)
	throws GCUBEFault {
		if (xml == null) {
			throw new GCUBEFault("Null value received during XML serialization of " + SupportedTypes.XML_PLAN_RESPONSE.value());
		}
		// Validate the XML if required
		if (validate) {
			try {
				if (!this.validate(SupportedTypes.XML_PLAN_RESPONSE.validationSchema(), xml)) {
					throw new GCUBEFault("The given " + SupportedTypes.XML_PLAN_RESPONSE.value() + " is not valid.");
				}
			} catch (IOException e) {
				throw new GCUBEFault("The validation schema for " + SupportedTypes.XML_PLAN_RESPONSE.value() + " cannot be found.");
			} catch (SAXException e) {
				throw new GCUBEFault("The " + SupportedTypes.XML_PLAN_RESPONSE.value() + " given is not valid.");
			}
		}
		this.initResponseHandler();
		PlanResponse retval = (PlanResponse) xstreamResponseHandler.fromXML(xml);
		this.initPackageGroups(retval.getPackageGroups());
		return retval;
	}


	/**
	 * For internal use only.
	 * Validates the input xml string against the given XSD schema file.
	 *
	 * @param schemaFilePath the location of XSD schema file to use.
	 * @param xml a string consisting of an XML.
	 * @return true if the file is valid for the given schema.
	 * @throws SAXException if schema is not valid.
	 * @throws IOException if the schema file cannot be found.
	 */
	private boolean validate(final String schemaFilePath, final String xml)
			throws SAXException, IOException {

		if (schemaFilePath == null) {
			throw new IOException("The Schema file has not been specified.");
		}

		// 1. Lookup a factory for the W3C XML Schema language
		SchemaFactory factory = SchemaFactory
				.newInstance("http://www.w3.org/2001/XMLSchema");
		// 2. Compile the schema.
		// Here the schema is loaded from a java.io.File, but you could use
		// a java.net.URL or a javax.xml.transform.Source instead.
		File schemaLocation = new File(schemaFilePath);
		if (!schemaLocation.exists()) {
			System.err.println("*** The Schema file has not been specified. " + schemaFilePath);
			throw new IOException("The Schema file has not been specified.");
		}

		Schema schema = factory.newSchema(schemaLocation);
		// 3. Get a validator from the schema.
		Validator validator = schema.newValidator();
		// 4. Parse the document you want to check.
		Source source = new StreamSource(new StringReader(xml));
		// 5. Check the document
		try {
			validator.validate(source);
		} catch (SAXException ex) {
			throw ex;
		}
		return true;
	}
}
