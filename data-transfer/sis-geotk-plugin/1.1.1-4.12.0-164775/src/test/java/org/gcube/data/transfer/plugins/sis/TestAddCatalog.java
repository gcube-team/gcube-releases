package org.gcube.data.transfer.plugins.sis;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.gcube.data.transfer.plugins.thredds.XMLCatalogHandler;
import org.xml.sax.SAXException;

public class TestAddCatalog {

	public static void main(String[] args) throws SAXException, IOException, TransformerException {
		String mainCatalogPath="/home/fabio/Desktop/catalog.xml";
		XMLCatalogHandler handler=new XMLCatalogHandler(new File(mainCatalogPath));
		System.out.println("Catalogs : "+handler.getCatalogDescriptor());
		handler.registerCatalog(new File("/home/fabio/Desktop/Tuna_Atlas_VRE_catalog.xml"), "testReplace2");
		handler.close();
	}

}
