package org.gcube.spatial.data.sdi.engine.impl.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.UUID;

import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.spatial.data.sdi.engine.impl.metadata.templates.AbstractMetadataTemplate.InsertionPoint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MetadataHandler {

	private Document document=null;
	private String metaUUID=null;
	private XPathHelper helper;
	public MetadataHandler(File xmlFile){
		//		Get document owner
		Element documentNode=null;
		try{
			InputStream inputStream= new FileInputStream(xmlFile);
			Reader reader = new InputStreamReader(inputStream,"UTF-8");

			InputSource is = new InputSource(reader);
			documentNode = MetadataUtils.docBuilder.parse(is).getDocumentElement();
			document=documentNode.getOwnerDocument();

			helper=MetadataUtils.getHelper(document);
			
			//			document = (Document)xpath.evaluate("/", inputSource, XPathConstants.NODE);
		}catch(Exception e){
			//			throw e;
			throw new RuntimeException("Unable to fix : unable to get Document",e);
		}


	}

	public String getUUID() throws SAXException, IOException{
		//Set | get meta UUID
		if(metaUUID==null){
			log.debug("Managing metadata ID.. ");
			

			List<String> metaUUIDList=helper.evaluate("//gmd:fileIdentifier/gco:CharacterString/text()");
			if(metaUUIDList.isEmpty()){				
				metaUUID=UUID.randomUUID().toString();
				log.debug("Stting uuid {} ",metaUUID);
				MetadataUtils.addContent("gmd:MD_Metadata",document,String.format(CommonMetadataPieces.fileIdentifier, metaUUID),helper,MetadataUtils.Position.first_child);
			}else {
				metaUUID=metaUUIDList.get(0);
				log.debug("Found meta UUID {} ",metaUUID);				
			}
		}
		return metaUUID;
	}
	
	
	public void addContent(String content, InsertionPoint insertion) throws SAXException, IOException{
		MetadataUtils.addContent(insertion.getElementReference(), document, content, helper, insertion.getPosition());
	}
	
	public File writeOut() throws IOException, TransformerException{
		DOMSource source = new DOMSource(document);
		File output=File.createTempFile("meta_", ".xml");
		output.createNewFile();
		StreamResult result = new StreamResult(output);
		MetadataUtils.transformer.transform(source, result);
		return output;
	}
}