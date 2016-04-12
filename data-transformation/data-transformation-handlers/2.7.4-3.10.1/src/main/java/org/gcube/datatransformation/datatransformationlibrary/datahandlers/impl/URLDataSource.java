package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.URLDataElement;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.ContentTypeDataSource;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.model.XMLDefinitions;

/**
 * @author Dimitris Katris, NKUA
 *
 * <p>
 * This <tt>DataSource</tt> fetches <tt>DataElements</tt> from a file containing urls.
 * </p>
 */
public class URLDataSource implements DataSource, ContentTypeDataSource {

	private Logger log = LoggerFactory.getLogger(URLDataSource.class);
	
	private REFDataBridge objectsinfs = new REFDataBridge();
	
	/**
	 * @param input The input value of the <tt>DataSource</tt>.
	 * @param inputParameters The input parameters of the <tt>DataSource</tt>.
	 * @throws Exception If the <tt>DataSource</tt> could not be initialized.
	 */
	public URLDataSource(String input, Parameter[] inputParameters) throws Exception {
		URL confURL = new URL(input);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(confURL.openStream());
		
		int i=0;
		Element ObjectElement = null;
		while((ObjectElement = (Element)doc.getElementsByTagName("DataElement").item(i++))!=null){
			String id = ((Element)ObjectElement.getElementsByTagName(XMLDefinitions.ELEMENT_id).item(0)).getTextContent();
			ContentType contentFormat = new ContentType();
			Element contentFormatElement = (Element)ObjectElement.getElementsByTagName(XMLDefinitions.ELEMENT_contenttype).item(0);
			String mimetype = ((Element)contentFormatElement.getElementsByTagName(XMLDefinitions.ELEMENT_mimetype).item(0)).getTextContent();
			contentFormat.setMimeType(mimetype);
			Element formatparameters;
			if((formatparameters=(Element)contentFormatElement.getElementsByTagName(XMLDefinitions.ELEMENT_contenttypeparameters).item(0))!=null){
				ArrayList <Parameter> contentTypeParametersList = new ArrayList<Parameter>();
				Element parameter;int cnt=0;
				while((parameter=(Element)formatparameters.getElementsByTagName(XMLDefinitions.ELEMENT_parameter).item(cnt++))!=null){
					Parameter param = new Parameter();
					param.setName(parameter.getAttribute(XMLDefinitions.ATTRIBUTE_parameterName));
					param.setValue(parameter.getAttribute(XMLDefinitions.ATTRIBUTE_parameterValue));
					String isOptional = parameter.getAttribute(XMLDefinitions.ATTRIBUTE_parameterIsOptional);
					if(isOptional==null || isOptional.equalsIgnoreCase("true"))
						param.setOptional(true);
					contentTypeParametersList.add(param);
				}
				contentFormat.setContentTypeParameters(contentTypeParametersList);
			}

			String url = ((Element)ObjectElement.getElementsByTagName("Location").item(0)).getTextContent();
			log.debug("Got URL object "+url+" with type "+contentFormat.toString());
			URLDataElement newURLObject = URLDataElement.getSourceDataElement();
			newURLObject.setId(id);
			newURLObject.setContentType(contentFormat);
			newURLObject.setContent(new URL(url));
			objectsinfs.append(newURLObject);
		}
	}
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource#hasNext()
	 * @return true if the <tt>DataSource</tt> has more elements.
	 */
	public boolean hasNext() {
		return objectsinfs.hasNext();
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource#next()
	 * @return the next element of the <tt>DataSource</tt>.
	 */
	public DataElement next() {
		return objectsinfs.next();
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#close()
	 */
	public void close() {
		objectsinfs.close();
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#isClosed()
	 * @return true if the <tt>DataHandler</tt> has been closed.
	 */
	public boolean isClosed() {
		return objectsinfs.isClosed();
	}
	
	public ContentType nextContentType() {
		DataElement de = objectsinfs.next();
		
		return de == null? null : de.getContentType();
	}
}
