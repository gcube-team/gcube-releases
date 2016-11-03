package org.gcube.datatransformation.datatransformationlibrary.imanagers;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.imanagers.queries.DescriptionQueryObject;
import org.gcube.datatransformation.datatransformationlibrary.imanagers.queries.QueryObject;
import org.gcube.datatransformation.datatransformationlibrary.imanagers.queries.QueryParser;
import org.gcube.datatransformation.datatransformationlibrary.model.TransformationUnit;
import org.gcube.datatransformation.datatransformationlibrary.model.TransformationProgram;
import org.gcube.datatransformation.datatransformationlibrary.model.XMLDefinitions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Dimitris Katris, NKUA
 * 
 * Implementation of <tt>IManager</tt> which uses as registry a local <tt>xml</tt> document.
 */
public class LocalInfoManager implements IManager{

	private static Logger log = LoggerFactory.getLogger(LocalInfoManager.class);
	
	private String programsFile;
	
	private Document programsDoc;
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.imanagers.IManager#getTransformationProgram(java.lang.String)
	 * @param transformationProgramID The id of the <tt>Transformation Program</tt>.
	 * @return The instance of the <tt>Transformation Program</tt>.
	 * @throws Exception If the IManager did not manage to fetch the <tt>Transformation Program</tt> from the registry.
	 */
	public TransformationProgram getTransformationProgram(String transformationProgramID) throws Exception {
		parseDocument();
		
		Element resource=null;
		NodeList resources = programsDoc.getElementsByTagName(XMLDefinitions.ELEMENT_resource);
		for (int i = 0; i < resources.getLength(); i++) {
			if (((Element) resources.item(i)).getElementsByTagName(XMLDefinitions.ELEMENT_id).item(0).getTextContent().equals(transformationProgramID)) {
				resource = (Element) resources.item(i);
			}
		}
		if(resource==null){
			log.error("Resource (TP) with id "+transformationProgramID+" does not exist.");
			return null;
		}
		
		TransformationProgram programinst = new TransformationProgram();
		programinst.fromDOM(resource);

		return programinst;
	}
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.imanagers.IManager#getTransformationUnit(java.lang.String, java.lang.String)
	 * @param transformationProgramID The id of the <tt>Transformation Program</tt> in which the <tt>Transformation Unit</tt> belongs to.
	 * @param transformationUnitID The id of the <tt>Transformation Unit</tt>.
	 * @return The instance of the <tt>Transformation Unit</tt>.
	 * @throws Exception If the IManager did not manage to fetch the <tt>Transformation Unit</tt> from the registry.
	 */
	public TransformationUnit getTransformationUnit(String transformationProgramID, String transformationUnitID) throws Exception {
		TransformationProgram transformationProgram = getTransformationProgram(transformationProgramID);
		if(transformationProgram.getTransformationUnits()==null || transformationProgram.getTransformationUnits().size()==0){
			log.error("Transformation program with id "+transformationProgramID+" does not contain any transformations");
			throw new Exception("Transformation program with id "+transformationProgramID+" does not contain any transformations");
		}
		for(TransformationUnit transformation: transformationProgram.getTransformationUnits()){
			if(transformation.getId().equals(transformationUnitID)){
				return transformation;
			}
		}
		log.error("Did not manage to find transformationUnit with id "+transformationUnitID+" in transformationUnit program with id "+transformationProgramID);
		throw new Exception("Did not manage to find transformationUnit with id "+transformationUnitID+" in transformationUnit program with id "+transformationProgramID);
	}

	/**
	 * Sets the <tt>xml</tt> document from which the <tt>LocalInfoManager</tt> will read information for <tt>Transformation Programs</tt> 
	 * 
	 * @param programsFile The <tt>xml</tt> document from which the <tt>LocalInfoManager</tt> will read information for <tt>Transformation Programs</tt>
	 */
	public void setProgramsFile(String programsFile) {
		this.programsFile = programsFile;
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.imanagers.IManager#getAvailableTransformationProgramIDs()
	 * @return The available <tt>Transformation Program IDs</tt>.
	 * @throws Exception If the available <tt>Transformation Program IDs</tt> could not be fetched from the registry.
	 */
	public String[] getAvailableTransformationProgramIDs() throws Exception {
		parseDocument();
		
		ArrayList<String> ids = new ArrayList<String>();
			
		NodeList idels = programsDoc.getElementsByTagName(XMLDefinitions.ELEMENT_id);
		for (int i = 0; i < idels.getLength(); i++) {
			ids.add(((Element) idels.item(i)).getTextContent());
		}
		return (String[]) ids.toArray(new String[ids.size()]);

	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.imanagers.IManager#publishTransformationProgram(org.gcube.datatransformation.datatransformationlibrary.model.TransformationProgram)
	 * @param transformationProgram The <tt>Transformation Program</tt> instance which will be published.
	 * @throws Exception If the <tt>Transformation Program</tt> could not be published.
	 */
	public void publishTransformationProgram(TransformationProgram transformationProgram) throws Exception {
		try {
			String xml = transformationProgram.toXML();
			log.info("Going to publish the following transformationUnit program\n"+xml);
		} catch (Exception e) {
			//Catching the exception...
			//Even if the TP cannot be published the transformationUnit procedure shall be continued in this node...
			log.error("Could not publish transformationUnit program",e);
			throw new Exception("Could not publish transformationUnit program");
		}
	}
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.imanagers.IManager#queryTransformationPrograms(java.lang.String)
	 * @param query The query.
	 * @return The result of the query in <tt>xml</tt> format.
	 * @throws Exception If the query could not be performed.
	 */
	public String queryTransformationPrograms(String query) throws Exception {
		QueryObject object = QueryParser.parse(query);
		String result = null;
		if(DescriptionQueryObject.class.isInstance(object)){
			log.debug("Quering for Description...");
			DescriptionQueryObject tobject = (DescriptionQueryObject)object;
			result = queryDescription(tobject);
		}else{
			log.debug("Invalid query object type");
			throw new Exception("Invalid query object type");
		}
		log.debug("Result of query: "+query+" is \n"+result);
		return result;
	}
	
	private String queryDescription(DescriptionQueryObject object) throws Exception{
		log.debug("Going to get description for "+object.transformationProgramID+"/"+object.transformationUnitID);
		if(object.transformationProgramID==null || object.transformationProgramID.trim().length()==0){
			throw new Exception("Cannot query for description without setting transformationUnit program id");
		}
		TransformationProgram tp = this.getTransformationProgram(object.transformationProgramID);
		if(object.transformationUnitID!=null && object.transformationUnitID.trim().length()>0){
			TransformationUnit selected=null;
			for(TransformationUnit tr: tp.getTransformationUnits()){
				if(tr.getId().equals(object.transformationUnitID)){
					selected=tr;
				}
			}
			if(selected==null){
				throw new Exception("Could not find transformationUnit with id "+object.transformationUnitID+" in TransformationProgram "+object.transformationProgramID);
			}
			ArrayList<TransformationUnit> tUnits = new ArrayList<TransformationUnit>();
			tUnits.add(selected);
			tp.setTransformationUnits(tUnits);
		}
		
		return "<Result>"+tp.toXML()+"</Result>";
	}
	
	private void parseDocument() throws Exception {
		if(programsFile==null){
			log.error("Programs file not specified");
			throw new Exception("Programs file not specified");
		}
		
		if(programsDoc==null){
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			try {
				builder = factory.newDocumentBuilder();
				programsDoc = builder.parse(new File(programsFile));
			} catch (Exception e) {
				log.error("Could not parse the ProgramsFile: "+programsFile,e);
				throw new Exception("Could not parse the ProgramsFile: "+programsFile);
			}
		}
	}
}
