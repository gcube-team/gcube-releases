package org.apache.jackrabbit.j2ee.workspacemanager.items;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibary.model.items.type.ContentType;
import org.gcube.common.homelibary.model.items.type.NodeProperty;

import com.thoughtworks.xstream.XStream;

public class JCRPDFFile  extends JCRFile {

	public JCRPDFFile(Node pdf) throws RepositoryException {
		super(pdf);
		
		map.put(NodeProperty.CONTENT, new XStream().toXML(ContentType.PDF));
		
		if (pdf.hasProperty(NodeProperty.NUMBER_OF_PAGES.toString()))
			map.put(NodeProperty.NUMBER_OF_PAGES, String.valueOf(pdf.getProperty(NodeProperty.NUMBER_OF_PAGES.toString()).getLong()));
		if (pdf.hasProperty(NodeProperty.VERSION.toString()))
			map.put(NodeProperty.VERSION, pdf.getProperty(NodeProperty.VERSION.toString()).getString());
		if (pdf.hasProperty(NodeProperty.AUTHOR.toString()))
			map.put(NodeProperty.AUTHOR, pdf.getProperty(NodeProperty.AUTHOR.toString()).getString());
		if (pdf.hasProperty(NodeProperty.PDF_TITLE.toString()))
			map.put(NodeProperty.PDF_TITLE, pdf.getProperty(NodeProperty.PDF_TITLE.toString()).getString());
		if (pdf.hasProperty(NodeProperty.PRODUCER.toString()))
			map.put(NodeProperty.PRODUCER, pdf.getProperty(NodeProperty.PRODUCER.toString()).getString());
	}

}
