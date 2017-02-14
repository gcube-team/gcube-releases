package org.gcube.application.framework.oaipmh.objectmappers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.gcube.application.framework.oaipmh.constants.MetadataConstants;
import org.gcube.application.framework.oaipmh.tools.ElementGenerator;
import org.gcube.application.framework.oaipmh.tools.Toolbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OAIDCMetadataXSD {
	
	
	/////////////////////////////////////////////////////////////
	////  IN DC, THE XSD PARAMS ARE AVAILABLE FROM THE WEB   ////
	/////////////////////////////////////////////////////////////
	
	private static final Logger logger = LoggerFactory.getLogger(OAIDCMetadataXSD.class);
	
	private String pathXSD;
	private String xmlnsPlusName;
	private String name;
	
	public OAIDCMetadataXSD(){
		this.name = "oai_dc:dc";
		this.pathXSD = MetadataConstants.OAIDC_SCHEMA;
		this.xmlnsPlusName = MetadataConstants.OAIDC_NAMESPACE;
	}

	public String getPathXSD() {
		return pathXSD;
	}

	public String getXmlnsPlusName() {
		return xmlnsPlusName;
	}
	
	public String getName(){
		return name;
	}
	
	
	
	
	
	
}
