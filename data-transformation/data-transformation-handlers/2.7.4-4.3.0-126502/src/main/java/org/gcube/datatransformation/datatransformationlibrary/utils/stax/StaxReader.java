package org.gcube.datatransformation.datatransformationlibrary.utils.stax;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;

import org.gcube.datatransformation.datatransformationlibrary.DTSScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jgerbe
 * 
 *         <p>
 *         Reader class used for reading xml with Stax
 *         </p>
 */
public class StaxReader {
	private static Logger log = LoggerFactory.getLogger(StaxReader.class);

	private XMLStreamReader streamReader;

	private String currPath;

	private Set<String> pathsSet;

	private Set<String> allPathsSet;

	/**
	 * Constructor class that parses xml with stax through http url
	 * 
	 * @param pathsSet
	 *            Paths to match
	 * @param url
	 *            http url to get content from
	 * @throws XMLStreamException
	 *             in case of bad xml
	 * @throws IOException
	 *             in case of bad url
	 */
	public StaxReader(Set<String> pathsSet, URL url) throws XMLStreamException, IOException {
		// First, create a new XMLInputFactory
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();

		URLConnection urlConnection = url.openConnection();
		urlConnection.addRequestProperty("gcube-scope", DTSScope.getScope());
		
		// read input stream from url and create an xml event reader
		streamReader = inputFactory.createXMLStreamReader(urlConnection.getInputStream());

		currPath = "/";

		this.allPathsSet = new HashSet<String>();
		for (String path : pathsSet) {
			allPathsSet.add(path);
			for (; !path.isEmpty(); path = path.substring(0, path.lastIndexOf("/")))
				allPathsSet.add(path);
		}

		this.pathsSet = pathsSet;
	}

	/**
	 * Check if reader has next
	 * 
	 * @return true if reader still open
	 */
	public boolean hasNext() {
		try {
			return streamReader.hasNext();
		} catch (XMLStreamException e) {
			return false;
		}
	}

	/**
	 * get next element
	 * 
	 * @return next element
	 */
	public StaxResponse next() {
		try {

			// Read collection description
			while (streamReader.hasNext()) {
				streamReader.next();

				if (streamReader.isStartElement()) {
					String startElement = streamReader.getLocalName();

					currPath += (currPath.endsWith("/") ? "" : "/") + startElement;

					if (allPathsSet.contains(currPath)) {
						if (!pathsSet.contains(currPath))
							continue;

						String text = null;

						try {
							text = streamReader.getElementText();
						} catch (Exception e) {
							text = getValue(streamReader);
						}
						StaxResponse resp = new StaxResponse(text, currPath);

						if (currPath.endsWith(startElement))
							currPath = currPath.substring(0, currPath.lastIndexOf("/"));

						if (currPath.isEmpty())
							currPath = "/";

						return resp;
					}
				}

				if (streamReader.isEndElement()) {
					String endElement = streamReader.getLocalName();

					if (currPath.endsWith(endElement))
						currPath = currPath.substring(0, currPath.lastIndexOf("/"));

					if (currPath.isEmpty())
						currPath = "/";
				}
			}
		} catch (XMLStreamException e) {
			log.warn("will return null", e);
			return null;
		}
		return null;
	}
	
	public void close() {
		try {
			streamReader.close();
		} catch (XMLStreamException e) {
			log.warn("could not close inputstream", e);
		}
	}

	private static String getValue(XMLStreamReader xsr) {
		StringWriter sw = new StringWriter();
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(new StAXSource(xsr), new StreamResult(sw));
		} catch (TransformerException e) {
			e.printStackTrace();
			return null;
		}
		return sw.toString();
	}
}
