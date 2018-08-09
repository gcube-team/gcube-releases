package org.gcube.data.transfer.plugins.thredds;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import javax.xml.transform.TransformerException;

import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.data.transfer.model.plugins.thredds.CatalogCollection;
import org.gcube.data.transfer.model.plugins.thredds.DataSetRoot;
import org.gcube.data.transfer.model.plugins.thredds.DataSetScan;
import org.gcube.data.transfer.model.plugins.thredds.ThreddsCatalog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XMLCatalogHandler {

	/*
	 * NB XML STRUCTURE IS AS FOLLOW
	 * 	catalog
	 * 		|-datasetRoot?
	 * 		|-datasetScan*
	 * 		|-dataset [dedicated_catalogs, only in main catalog file]
	 * 			|-catalogRef
	 * 
	 */
	
	

	private static final String DEDICATED_CATALOGS_DATASET_ID="VRE_Catalogs";
	
	private static final String LINKED_CATALOGS_XPATH="//*[local-name()='catalogRef' and parent::node()[local-name()='dataset']]";
	private static final String DECLARED_DATASETSCANS="//*[local-name()='datasetScan']";
	private static final String DECLARED_DATASETROOT="//*[local-name()='datasetRoot']";
//	private static final String ROOT_CATALOG_XPATH="/[local-name()='catalog']";
	private static final String CATALOG_COLLECTION_XPATH="//*[local-name()='dataset'][child::node()[local-name()='catalogRef']]|//*[@ID='"+DEDICATED_CATALOGS_DATASET_ID+"']";
	
	//PARAMETRIC	
	private static final String CATALOG_REFERENCE_BY_FILE="//*[local-name()='catalogRef'][@xlink:href='%s']";
	private static final String ELEMENT_BY_ID="//*[@ID='%s']";
	
	
	
	
	/*
	 * 1 - to expose reference 
	 * 2 - path to catalog file
	 * 3 - ID
	 */
	private static final String catalogReferenceXMLPiece="<catalogRef "
			+"xmlns=\"http://www.unidata.ucar.edu/namespaces/thredds/InvCatalog/v1.0\"\n" + 
			"         xmlns:xlink=\"http://www.w3.org/1999/xlink\" "
			+ "xlink:title=\"%1$s\" xlink:href=\"%2$s\" ID=\"%3$s\" name=\"%1$s\"/>"; 
	
	
	

	

	private XPathHelper helper;
	private Document document;
//	private Element documentNode;
	private File currentCatalogFile;
	
	
	public XMLCatalogHandler(File catalog) throws SAXException, IOException {
		this.currentCatalogFile=catalog;		
		document=CommonXML.getDocument(catalog);
		helper=CommonXML.getHelper(document);
	}

	public void registerCatalog(File toRegisterCatalogFile,String toRegisterReference) throws SAXException, IOException {
		String filename=toRegisterCatalogFile.getName();
		String ID=filename.contains(".")?filename.substring(0, filename.lastIndexOf('.')):filename;
		
		String toSetString=String.format(catalogReferenceXMLPiece, toRegisterReference,filename,ID);
		log.info("Checking if file is already referenced..");		

		log.debug("Checking by filname {} ",filename);											//Check filename presence 
		String referenceByFilenameXPATH=String.format(CATALOG_REFERENCE_BY_FILE,filename);
		NodeList filenameReferencesNodelist=helper.evaluateForNodes(referenceByFilenameXPATH);		
		if(filenameReferencesNodelist.getLength()>0) {
			log.info("Filename {} is already declared. Updateing reference..",filename);
			CommonXML.addContent(referenceByFilenameXPATH,document,toSetString,helper,CommonXML.Position.replace);
		}else {
			
			log.debug("Checking by ID {} ",ID);													//else check by ID
			String referenceByIDXPATH=String.format(ELEMENT_BY_ID, ID);
			NodeList IDReferencesNodelist=helper.evaluateForNodes(referenceByIDXPATH);
			if(IDReferencesNodelist.getLength()>0) {
				log.info("ID {} found. Updateing reference..",ID);
				CommonXML.addContent(referenceByIDXPATH,document,toSetString,helper,CommonXML.Position.replace);
			}else {
				
				log.info("No similar entries found. Adding reference..");						//else add
				String catalogCollectionXPATH=String.format(ELEMENT_BY_ID, DEDICATED_CATALOGS_DATASET_ID);
				CommonXML.addContent(catalogCollectionXPATH,document,toSetString,helper,CommonXML.Position.last_child);
			}
		}		
	}


	public void close() throws IOException, TransformerException {
		CommonXML.writeOut(document, currentCatalogFile);
	}

	
	
	

	/**
	 * Parses the current catalog file and linked ones in order to 
	 * gather information on declared datasets
	 *   
	 * 
	 * @return
	 */
	public ThreddsCatalog getCatalogDescriptor() {
		log.debug("loading catalogs from {} ",currentCatalogFile.getAbsolutePath());
		ThreddsCatalog toReturn=new ThreddsCatalog();
		toReturn.setCatalogFile(currentCatalogFile.getName());
		log.debug("Checking declared datasets in {} ",currentCatalogFile.getAbsolutePath());
		//get dataset root
		NodeList datasetRootNodes=helper.evaluateForNodes(DECLARED_DATASETROOT);
		if(datasetRootNodes.getLength()>0) {
			Element rootNode=(Element) datasetRootNodes.item(0);
			DataSetRoot root=new DataSetRoot();
			root.setLocation(rootNode.getAttribute("location"));
			root.setPath(rootNode.getAttribute("path"));
			toReturn.setDeclaredDataSetRoot(root);
		}
		//get dataset Scans
		NodeList datasetScans=helper.evaluateForNodes(DECLARED_DATASETSCANS);
		toReturn.setDeclaredDataSetScan(new HashSet<DataSetScan>());
		for(int i=0;i<datasetScans.getLength();i++) {
			Element scanNode=(Element) datasetScans.item(i);
			DataSetScan scan=new DataSetScan();
			scan.setID(scanNode.getAttribute("ID"));
			scan.setLocation(scanNode.getAttribute("location"));
			scan.setName(scanNode.getAttribute("name"));
			scan.setPath(scanNode.getAttribute("path"));
			toReturn.getDeclaredDataSetScan().add(scan);
		}		
		
		//Check for declared catalog collections		
		NodeList catalogCollectionNodes=helper.evaluateForNodes(CATALOG_COLLECTION_XPATH);
		if(catalogCollectionNodes.getLength()>0) {
			Element catalogCollectionNode=(Element) catalogCollectionNodes.item(0);
			CatalogCollection collection=new CatalogCollection();
			
			collection.setID(catalogCollectionNode.getAttribute("ID"));
			collection.setName(catalogCollectionNode.getAttribute("name"));
			log.debug("Found catalog collection ID {} , NAME {} ",collection.getID(),collection.getName());
			
			collection.setLinkedCatalogs(new HashSet<ThreddsCatalog>());
			NodeList linkedCatalogsNodelist=helper.evaluateForNodes(LINKED_CATALOGS_XPATH);
			//load linked catalogs
			for(int i=0;i<linkedCatalogsNodelist.getLength();i++) {
				Element linkedCatalogElement=(Element) linkedCatalogsNodelist.item(i);
				String linkedCatalogFile=linkedCatalogElement.getAttribute("xlink:href");
				String linkedCatalogName=linkedCatalogElement.getAttribute("name");
				String linkedCatalogTitle=linkedCatalogElement.getAttribute("xlink:title");
				String linkedCatalogID=linkedCatalogElement.getAttribute("ID");
				try{
					ThreddsCatalog linkedCatalog=new XMLCatalogHandler(new File(currentCatalogFile.getParentFile(),linkedCatalogFile)).getCatalogDescriptor();
					linkedCatalog.setCatalogFile(linkedCatalogFile);
					linkedCatalog.setName(linkedCatalogName);
					linkedCatalog.setTitle(linkedCatalogTitle);
					linkedCatalog.setID(linkedCatalogID);
					collection.getLinkedCatalogs().add(linkedCatalog);
				}catch(Throwable t) {
					log.warn("Unable to parse {} ",linkedCatalogFile,t);
				}			
			}
			toReturn.setSubCatalogs(collection);
		}
		return toReturn;
	}
	

	

	//**********************


			
}
