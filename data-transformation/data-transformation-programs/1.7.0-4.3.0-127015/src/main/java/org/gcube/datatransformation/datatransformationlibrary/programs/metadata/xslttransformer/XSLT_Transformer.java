package org.gcube.datatransformation.datatransformationlibrary.programs.metadata.xslttransformer;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.gcube.datatransformation.datatransformationlibrary.DTSScope;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.StrDataElement;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.programs.Program;
import org.gcube.datatransformation.datatransformationlibrary.programs.metadata.util.XSLTRetriever;
import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Status;
import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Type;
import org.gcube.datatransformation.datatransformationlibrary.reports.ReportManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dimitris Katris, NKUA
 *         <p>
 *         Uses an xslt in order to transform metadata.
 *         </p>
 */
public class XSLT_Transformer implements Program {

	private static Logger log = LoggerFactory.getLogger(XSLT_Transformer.class);

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.programs.Program#transform(java.util.List, java.util.List,
	 *      org.gcube.datatransformation.datatransformationlibrary.model.ContentType,
	 *      org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink)
	 * @param sources
	 *            The <tt>DataSources</tt> from which the <tt>Program</tt> will get the <tt>DataElements</tt>.
	 * @param programParameters
	 *            The parameters of the <tt>Program</tt> which are primarily set by the <tt>TransformationUnit</tt>.
	 * @param targetContentType
	 *            The <tt>ContentType</tt> in which the source data will be transformed.
	 * @param sink
	 *            The <tt>DataSink</tt> in which the <tt>Program</tt> will append the transformed <tt>DataElements</tt>.
	 * @throws Exception
	 *             If the program is not capable to transform <tt>DataElements</tt>.
	 */
	public void transform(List<DataSource> sources, List<Parameter> programParameters, ContentType targetContentType, DataSink sink) throws Exception {
		List<String> xsltIDs = new ArrayList<String>();

		if (programParameters == null || programParameters.size() == 0) {
			log.error("Program parameters do not contain xslt");
			throw new Exception("Program parameters do not contain xslt");
		}

		String finalftsxslt = null;
		for (Parameter param : programParameters) {
			if (param.getName().toLowerCase().startsWith("xslt")) {
				if (!param.getValue().trim().endsWith("-"))
					xsltIDs.add(param.getValue());
			}
			if (param.getName().toLowerCase().equals("finalftsxslt"))
				if (!param.getValue().trim().endsWith("-"))
					finalftsxslt = param.getValue();
		}
		if (finalftsxslt != null)
			xsltIDs.add(finalftsxslt);
		
		List<String> xslts = new ArrayList<String>();
		for (String xsltID : xsltIDs)
		if (xsltID != null && xsltID.trim().length() > 0) {
			log.debug("Got XSLT ID: " + xsltIDs);
			try {
				xslts.add(XSLTRetriever.getXSLTFromIS(xsltID, DTSScope.getScope()));
			} catch (Exception e) {
				log.error("Did not manage to retrieve the XSLT with ID " + xsltIDs + ", aborting transformation...");
				throw new Exception("Did not manage to retrieve the XSLT with ID " + xsltIDs);
			}
		} else {
			log.error("Program parameters do not contain xslt");
			throw new Exception("Program parameters do not contain xslt");
		}

		
		List<Transformer> xsltTransformers = new ArrayList<Transformer>();
		for (String xslt : xslts) {
			try {
				TransformerFactory factory = TransformerFactory.newInstance();
				Transformer tr = factory.newTransformer(new StreamSource(new StringReader(xslt)));
				tr.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	
				for (Parameter param : programParameters) {
					if (!param.getName().toLowerCase().equals("xslt")) {
						tr.setParameter(param.getName(), param.getValue());
					}
				}
				xsltTransformers.add(tr);
			} catch (Exception e) {
				log.error("Failed to compile the XSLT: " + xslt, e);
				throw new Exception("Failed to compile the XSLT");
			}
		}
		transformByXSLT(sources, xsltTransformers, targetContentType, sink);
	}

	private void transformByXSLT(List<DataSource> sources, List<Transformer> xsltTransformer, ContentType targetContentType, DataSink sink) throws Exception {
		if (sources.size() != 1) {
			throw new Exception("Elm2ElmProgram is only applicable for programs with one Input");
		}
		DataSource source = sources.get(0);
		while (source.hasNext() && !sink.isClosed()) {
			log.debug("Source has next...");
			DataElement object = source.next();
			if (object != null) {
				DataElement transformedObject;
				try {
					log.debug("Got next object with id " + object.getId());
					transformedObject = transformDataElementByXSLT(object, xsltTransformer, targetContentType);
					if (transformedObject == null) {
						log.warn("Got null transformed object");
						throw new NullPointerException();
					}
					transformedObject.setId(object.getId());
					log.debug("Got transformed object with id: " + transformedObject.getId() + " and content format: "
							+ transformedObject.getContentType().toString() + ", appending into the sink");
					ReportManager.manageRecord(object.getId(), "Data element with id " + object.getId() + " and content format "
							+ object.getContentType().toString() + " " + "was transformed successfully to " + transformedObject.getContentType().toString(),
							Status.SUCCESSFUL, Type.TRANSFORMATION);
				} catch (Exception e) {
					log.error("Could not transform Data Element, continuing to next...", e);
					ReportManager.manageRecord(object.getId(), "Data element with id " + object.getId() + " and content format "
							+ object.getContentType().toString() + " " + "could not be transformed to " + targetContentType.toString(), Status.FAILED,
							Type.TRANSFORMATION);
					continue;
				}
				sink.append(transformedObject);
				log.debug("Transformed object with id: " + transformedObject.getId() + ", was appended successfully");
			} else {
				log.warn("Got null object from the data source");
			}

		}
		if (!source.hasNext())
			log.debug("Source does not have any objects left, closing the sink...");
		else
			log.debug("Sink was closed unexpectedly...");
		sink.close();
	}

	private DataElement transformDataElementByXSLT(DataElement sourceDataElement, List<Transformer> xsltTransformer, ContentType targetContentType) throws Exception {
		StrDataElement transformedElement = StrDataElement.getSinkDataElement(sourceDataElement);
		transformedElement.setContentType(targetContentType);
		transformedElement.setId(sourceDataElement.getId());

		StringWriter output = null;
		StreamSource source;
		try {
			if (sourceDataElement instanceof StrDataElement) {
				source = new StreamSource(new StringReader(((StrDataElement) sourceDataElement).getStringContent()));
			} else {
				source = new StreamSource(sourceDataElement.getContent());
			}
			
			for (Transformer tr : xsltTransformer) {
				output = new StringWriter();
				tr.transform(source, new StreamResult(output));
				source = new StreamSource(new StringReader(output.toString()));
			}
		} catch (Exception e) {
			log.error("Failed to transform element with ID = " + sourceDataElement.getId());
			throw new Exception("Failed to transform element with ID = " + sourceDataElement.getId());
		}

		transformedElement.setContent(output.toString());
		return transformedElement;
	}

	// XXX add delimiter parameter to xslt
//	public static void main(String[] args) throws Exception {
//		TCPConnectionManager.Init(new TCPConnectionManagerConfig("meteora.di.uoa.gr", new ArrayList<PortRange>(), true));
//
//		TCPConnectionManager.RegisterEntry(new ChannelTCPConnManagerEntry());
//		TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler());
//		TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
//		String input = "http://meteora.di.uoa.gr/sru.xml";
//		DTSScope.setScope("/gcube/devNext");
//		XSLT_Transformer transformer = new XSLT_Transformer();
//
//		
//		List<DataSource> sources = Arrays.asList(new DataSource[] { new SRUDataSource(input, null) });
//		List<Parameter> programParameters = Arrays.asList(new Parameter[] { new Parameter("xslt", "$BrokerXSLT_XML_to_flattenJSON"), new Parameter("delimiter", ",,,") });
//		ContentType targetContentType = new ContentType("application/json", null);
//		DataSink sink = new GRS2DataSink(null, null);
//		transformer.transform(sources, programParameters, targetContentType, sink);
//
//		System.out.println(sink.getOutput());
//
//		GRS2DataSource source = new GRS2DataSource(sink.getOutput(), null);
//		int i = 0;
//		while (source.hasNext()) {
//			DataElement de = source.next();
//			System.out.println(++i);
//			System.out.println(de.getId());
//			System.out.println(de.getAllAttributes());
//			System.out.println(de.getContentType());
//			System.out.println(readFile(((LocalFileDataElement) de).getFileContent()));
//		}
//
//	}
//	
//	private static String readFile(File file) throws Exception {
//		BufferedReader reader = new BufferedReader(new FileReader(file));
//		String line = null;
//		StringBuilder stringBuilder = new StringBuilder();
//		String ls = System.getProperty("line.separator");
//
//		while ((line = reader.readLine()) != null) {
//			stringBuilder.append(line);
//			stringBuilder.append(ls);
//		}
//
//		return stringBuilder.toString();
//	}
}
