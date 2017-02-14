package org.gcube.datatransformation.datatransformationlibrary.imanagers;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.datatransformation.datatransformationlibrary.imanagers.queries.ContentTypeQueryObject;
import org.gcube.datatransformation.datatransformationlibrary.imanagers.queries.DescriptionQueryObject;
import org.gcube.datatransformation.datatransformationlibrary.imanagers.queries.ProgramParametersQueryObject;
import org.gcube.datatransformation.datatransformationlibrary.imanagers.queries.QueryObject;
import org.gcube.datatransformation.datatransformationlibrary.imanagers.queries.QueryParser;
import org.gcube.datatransformation.datatransformationlibrary.imanagers.queries.TransformationUnitQueryObject;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.model.TransformationProgram;
import org.gcube.datatransformation.datatransformationlibrary.model.TransformationUnit;
import org.gcube.rest.commons.resourceawareservice.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * @author Dimitris Katris, NKUA
 * 
 * Implementation of <tt>IManager</tt> which uses as registry the gCube IS.
 */
public class ISManager implements IManager{

	private static ISManager instance = null;

	public static ISManager getInstance() {
		if (instance == null) {
			instance = new ISManager();
		}
		return instance;
	}

	/**
	 * Logs operations performed by the <tt>ISManager</tt>.
	 */
	private static Logger log = LoggerFactory.getLogger(ISManager.class);
	
	/**
	 * The scope on which the <tt>ISManager</tt> operates.
	 */
	private String scope;
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.imanagers.IManager#getAvailableTransformationProgramIDs()
	 * @return The available <tt>Transformation Program IDs</tt>.
	 * @throws Exception If the available <tt>Transformation Program IDs</tt> could not be fetched from the IS.
	 */
	public String[] getAvailableTransformationProgramIDs() throws Exception{
		try {
			List<String> results;
			results = ICollectorSigleton.getInstance().listGenericResourceIDsByType("DTSTransformationProgram", scope);
			
			if(results.size()==0){
				log.warn("Did not manage to find any available transformationUnit programs");
				return new String[0];
			}else{
				return results.toArray(new String[results.size()]);
			}
		} catch (Exception e) {
			log.error("Could not invoke IS to find the available transformationUnit program IDs",e);
			throw new Exception("Could not invoke IS to find the available transformationUnit program IDs");
		}
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.imanagers.IManager#getTransformationProgram(java.lang.String)
	 * @param transformationProgramID The id of the <tt>Transformation Program</tt>.
	 * @return The instance of the <tt>Transformation Program</tt>.
	 * @throws Exception If the IManager did not manage to fetch the <tt>Transformation Program</tt> from the IS.
	 */
	public TransformationProgram getTransformationProgram(String transformationProgramID) throws Exception {
		Resource resource;
		try {
			List<Resource> results;
			if (transformationProgramID.startsWith("$")) {
				String resourceName = transformationProgramID.substring(1);
				log.debug("Going to get resource from IS with name "+resourceName+" from scope "+scope.toString());
				results = ICollectorSigleton.getInstance().getGenericResourcesByName(resourceName, scope);
			} else {
				log.debug("Going to get resource from IS with ID "+transformationProgramID+" from scope "+scope.toString());
				results = ICollectorSigleton.getInstance().getGenericResourcesByID(transformationProgramID, scope);
			}
			
			if(results==null || results.size()==0){
				log.error("Could not find transformationUnit program with id "+transformationProgramID);
				throw new Exception("Could not find transformationUnit program with id "+transformationProgramID);
			}
			resource = results.get(0);
		} catch (Exception e) {
			log.error("Could not invoke IS to find the transformationUnit program with id "+transformationProgramID,e);
			throw new Exception("Could not invoke IS to find the transformationUnit program with id "+transformationProgramID);
		}
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			ByteArrayInputStream instream = new ByteArrayInputStream(resource.getBodyAsString().getBytes());
			builder = factory.newDocumentBuilder();
			Document resourceDoc = builder.parse(instream);
			instream.close();
			
			TransformationProgram programinst = new TransformationProgram();
			programinst.fromDOM(resourceDoc.getDocumentElement());
			programinst.setId(resource.getResourceID());
			programinst.setName(resource.getName());
			programinst.setDescription(resource.getDescription());
			return programinst;
			
		} catch (Exception e) {
			log.error("Could not parse the resource(Transformation Program)",e);
			throw new Exception("Could not parse the resource(Transformation Program)");
		}
	}
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.imanagers.IManager#getTransformationUnit(java.lang.String, java.lang.String)
	 * @param transformationProgramID The id of the <tt>Transformation Program</tt> in which the <tt>Transformation Unit</tt> belongs to.
	 * @param transformationUnitID The id of the <tt>Transformation Unit</tt>.
	 * @return The instance of the <tt>Transformation Unit</tt>.
	 * @throws Exception If the IManager did not manage to fetch the <tt>Transformation Unit</tt> from the IS.
	 */
	public TransformationUnit getTransformationUnit(String transformationProgramID, String transformationUnitID) throws Exception {
		TransformationProgram transformationProgram = getTransformationProgram(transformationProgramID);
		if(transformationProgram.getTransformationUnits()==null || transformationProgram.getTransformationUnits().size()==0){
			log.error("Transformation program with id "+transformationProgramID+" does not contain any transformationUnit Units");
			throw new Exception("Transformation program with id "+transformationProgramID+" does not contain any transformationUnit Units");
		}
		for(TransformationUnit transformationUnit: transformationProgram.getTransformationUnits()){
			if(transformationUnit.getId().equals(transformationUnitID)){
				return transformationUnit;
			}
		}
		log.error("Did not manage to find transformationUnit Unit with id "+transformationUnitID+" in transformationUnit program with id "+transformationProgramID);
		throw new Exception("Did not manage to find transformationUnit Unit with id "+transformationUnitID+" in transformationUnit program with id "+transformationProgramID);
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.imanagers.IManager#publishTransformationProgram(org.gcube.datatransformation.datatransformationlibrary.model.TransformationProgram)
	 * @param transformationProgram The <tt>Transformation Program</tt> instance which will be published.
	 * @throws Exception If the <tt>Transformation Program</tt> could not be published.
	 */
	public void publishTransformationProgram(TransformationProgram transformationProgram) throws Exception {
		throw new Exception("Not implemented yet");
//		
//		String transformationProgramToXML = transformationProgram.toXML();
//		log.debug("Publishing in IS Transformation Program\n"+transformationProgramToXML);
//		GCUBEGenericResource resource = GHNContext.getImplementation(GCUBEGenericResource.class);
//		resource.load(new StringReader(transformationProgramToXML));
//		
//		log.debug("TPID "+resource.getID());
//		log.debug("TPName "+resource.getName());
//		log.debug("TPType "+resource.getType());
//		log.debug("TPBody "+resource.getBody());
//		
//		resource.addScope(scopeOld);
//		
//		ISPublisher publisher  = GHNContext.getImplementation(ISPublisher.class);
//		publisher.registerGCUBEResource(resource, scopeOld, DTSSManager.getSecurityManager());
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
		if(ContentTypeQueryObject.class.isInstance(object)){
			log.debug("Quering for Content Types...");
			ContentTypeQueryObject ctobject = (ContentTypeQueryObject)object;
			result = queryContentType(ctobject);
		}else if(TransformationUnitQueryObject.class.isInstance(object)){
			log.debug("Quering for Transformations...");
			TransformationUnitQueryObject tuobject = (TransformationUnitQueryObject)object;
			result = queryTransformationUnit(tuobject);
		}else if(DescriptionQueryObject.class.isInstance(object)){
			log.debug("Quering for Description...");
			DescriptionQueryObject tobject = (DescriptionQueryObject)object;
			result = queryDescription(tobject);
		}else if(ProgramParametersQueryObject.class.isInstance(object)){
			log.debug("Quering for program parameters...");
			ProgramParametersQueryObject ppobject = (ProgramParametersQueryObject)object;
			result = queryProgramParameters(ppobject);
		}else{
			log.debug("Invalid query object type");
			throw new Exception("Invalid query object type");
		}
		log.debug("Result of query: "+query+" is \n"+result);
		return result;
	}
	
	private String queryProgramParameters(ProgramParametersQueryObject object) throws Exception {
		log.debug("Going to get program parameters for "+object.transformationProgramID);
		if(object.transformationProgramID==null || object.transformationProgramID.trim().length()==0){
			throw new Exception("Cannot query for description without setting transformationUnit program id");
		}
		
		TransformationProgram tp = this.getTransformationProgram(object.transformationProgramID);
		
		StringBuilder result=new StringBuilder();
		result.append("<Result>");
		if(tp.getTransformer().getGlobalProgramParams()!=null || tp.getTransformer().getGlobalProgramParams().size()>0){
			for(Parameter param: tp.getTransformer().getGlobalProgramParams()){
				result.append("<Parameter isOptional=\""+param.isOptional()+"\" name=\""+param.getName()+"\" value=\""+param.getValue()+"\"/>");
			}
		}
		if(object.transformationUnitID!=null && object.transformationUnitID.trim().length()>0){
			TransformationUnit selected=null;
			for(TransformationUnit tu: tp.getTransformationUnits()){
				if(tu.getId().equals(object.transformationUnitID)){
					selected=tu;break;
				}
			}
			if(selected==null){
				throw new Exception("Could not find transformationUnit with id "+object.transformationUnitID+" in TransformationProgram "+object.transformationProgramID);
			}
			for(Parameter param: selected.getProgramParameters()){
				result.append("<Parameter isOptional=\""+param.isOptional()+"\" name=\""+param.getName()+"\" value=\""+param.getValue()+"\"/>");
			}
		}
		result.append("</Result>");
		return result.toString();
	}

	private String queryDescription(DescriptionQueryObject object) throws Exception{
		log.debug("Going to get description for "+object.transformationProgramID+"/"+object.transformationUnitID);
		if(object.transformationProgramID==null || object.transformationProgramID.trim().length()==0){
			throw new Exception("Cannot query for description without setting transformationUnit program id");
		}
		TransformationProgram tp = this.getTransformationProgram(object.transformationProgramID);
		if(object.transformationUnitID!=null && object.transformationUnitID.trim().length()>0){
			TransformationUnit selected=null;
			for(TransformationUnit tu: tp.getTransformationUnits()){
				if(tu.getId().equals(object.transformationUnitID)){
					selected=tu;break;
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
	
	//TODO: Format Parameters(=Probably one more nested query in content formats of a transformationUnit...)
	private String queryTransformationUnit(TransformationUnitQueryObject object) throws Exception {
		throw new Exception("not implemented yet");
		
//		ISClient client = GHNContext.getImplementation(ISClient.class);
//		GCUBEGenericQuery query = client.getQuery("GCUBEResourceQuery");
//		boolean putAnd=false;
//		StringBuilder expression = new StringBuilder();
//		expression.append("declare namespace is = 'http://gcube-system.org/namespaces/informationsystem/registry'; ");
//		expression.append("declare namespace gc = 'http://gcube-system.org/namespaces/common/core/porttypes/GCUBEProvider'; ");
//		expression.append("for $result in collection('/db/Profiles/GenericResource')//Document/Data/is:Profile/Resource ");
//		expression.append("where $result/Profile/SecondaryType/string() eq 'DTSTransformationProgram' ");
//		if(object.transformationProgramID!=null && object.transformationProgramID.trim().length()>0)
//			expression.append("and $result/ID/string() eq '"+object.transformationProgramID+"'");
//		expression.append(" return ");
//		expression.append("for $resultin in $result/Profile/Body/gDTSTransformationProgram/TransformationUnits/TransformationUnit ");
//		if((object.sourceContentTypeConditions!=null && object.sourceContentTypeConditions.size()>0) || (object.targetContentTypeConditions!=null && object.targetContentTypeConditions.size()>0)){
//			expression.append("where ");
//			if(object.sourceContentTypeConditions!=null && object.sourceContentTypeConditions.size()>0){
//				for(ContentTypeCondition fcondition: object.sourceContentTypeConditions.values()){
//					if(putAnd){
//						expression.append(" and ");
//					}
//					putAnd=true;
//					if(fcondition.getMimetype()==null || fcondition.getMimesubtype()==null){
//						if(fcondition.getMimetype()!=null){
//							expression.append("starts-with($resultin/Sources/Source/ContentType/Mimetype/string(), '"+fcondition.getMimetype()+"/') ");
//						}
//						if(fcondition.getMimesubtype()!=null){
//							expression.append("ends-with($resultin/Sources/Source/ContentType/Mimetype/string(), '/"+fcondition.getMimesubtype()+"') ");
//						}
//					}else{
//						expression.append("$resultin/Sources/Source/ContentType/Mimetype/string() eq '"+fcondition.getMimetype()+"/"+fcondition.getMimesubtype()+"' ");
//					}
//				}
//			}
//			if(object.targetContentTypeConditions!=null && object.targetContentTypeConditions.size()>0){
//				for(ContentTypeCondition fcondition: object.targetContentTypeConditions.values()){
//					if(putAnd){
//						expression.append(" and ");
//					}
//					putAnd=true;
//					if(fcondition.getMimetype()==null || fcondition.getMimesubtype()==null){
//						if(fcondition.getMimetype()!=null){
//							expression.append("starts-with($resultin/Target/ContentType/Mimetype/string(), '"+fcondition.getMimetype()+"/') ");
//						}
//						if(fcondition.getMimesubtype()!=null){
//							expression.append("ends-with($resultin/Target/ContentType/Mimetype/string(), '/"+fcondition.getMimesubtype()+"') ");
//						}
//					}else{
//						expression.append("$resultin/Target/ContentType/Mimetype/string() eq '"+fcondition.getMimetype()+"/"+fcondition.getMimesubtype()+"' ");
//					}
//				}
//			}
//		}
//		
//		expression.append("return <TransformationUnit><TransformationProgramID>{$result/ID/string()}</TransformationProgramID><TransformationUnitID>{$resultin/string(@id)}</TransformationUnitID></TransformationUnit>");
//		
//		query.setExpression(expression.toString());
//		
//		List<XMLResult> results = client.execute(query, scopeOld);
//		StringBuilder finalresult = new StringBuilder();
//		finalresult.append("<Result>");
//		for(XMLResult result: results){
//			log.debug("XMLRESULT\n"+result);
//			finalresult.append(result.toString());
//		}
//		finalresult.append("</Result>");
//		return finalresult.toString();
	}
	
	private String queryContentType(ContentTypeQueryObject object) throws Exception {
		
		StringBuilder expression = new StringBuilder();
		expression.append("declare namespace is = 'http://gcube-system.org/namespaces/informationsystem/registry'; ");
		expression.append("declare namespace gc = 'http://gcube-system.org/namespaces/common/core/porttypes/GCUBEProvider'; ");
		expression.append("for $result in collection('/db/Profiles/GenericResource')//Document/Data/is:Profile/Resource ");
		expression.append("where $result/Profile/SecondaryType/string() eq 'DTSTransformationProgram' ");
//		TP
		if(object.transformationProgramID!=null && object.transformationProgramID.trim().length()>0)
			expression.append("and $result/ID/string() eq '"+object.transformationProgramID+"'");
		expression.append(" return ");
		expression.append("for $resultin in $result/Profile/Body/gDTSTransformationProgram/TransformationUnits/TransformationUnit");
		if(object.transformationUnitID!=null && object.transformationUnitID.trim().length()>0)
			expression.append("[string(@id) eq '"+object.transformationUnitID+"']");
		
		if(object.getResultType().equals(QueryParser.QUERYSOURCECONTENTTYPE)){
			expression.append("/Sources/Source ");
		}else{
			expression.append("/Target ");
		}
		System.out.println("Mimetype: "+object.contentTypeCondition.getMimetype());
		System.out.println("SubMimetype: "+object.contentTypeCondition.getMimesubtype());
		if(object.contentTypeCondition.getMimetype()==null || object.contentTypeCondition.getMimesubtype()==null){
			if(object.contentTypeCondition.getMimetype()!=null){
				expression.append("where starts-with($resultin/ContentType/Mimetype/string(), '"+object.contentTypeCondition.getMimetype()+"/') ");
			}
			if(object.contentTypeCondition.getMimesubtype()!=null){
				expression.append("where ends-with($resultin/ContentType/Mimetype/string(), '/"+object.contentTypeCondition.getMimesubtype()+"') ");
			}
		}else{
			expression.append("where $resultin/ContentType/Mimetype/string() eq '"+object.contentTypeCondition.getMimetype()+"/"+object.contentTypeCondition.getMimesubtype()+"' ");
		}
		//TODO: Add format parameters...	
		
		expression.append("return $resultin/ContentType");
		return queryContentFormatByExpression(expression.toString());
	}
	
	private String queryContentFormatByExpression(String expression) throws Exception {
		throw new Exception("Not implemented yet");
//		//Client initialization...
//		ISClient client = GHNContext.getImplementation(ISClient.class);
//		GCUBEGenericQuery query = client.getQuery("GCUBEResourceQuery");
//		query.setExpression(expression);
//		
//		List<XMLResult> results = client.execute(query, scopeOld);
//		if(results==null||results.size()==0) {
//			log.info("No results found");
//			return "<Result/>";
//		} else {
//			StringBuilder strbuilder = new StringBuilder();
//			strbuilder.append("<Result>");
//			for(XMLResult result: results){
//				log.debug("Adding to doc: "+result.toString());
//				strbuilder.append(result.toString());
//			}
//			strbuilder.append("</Result>");
//			
//			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//			DocumentBuilder builder;
//			try {
//				
//				HashSet<Integer> exist = new HashSet<Integer>();
//				ByteArrayInputStream instream = new ByteArrayInputStream(strbuilder.toString().getBytes());
//				builder = factory.newDocumentBuilder();
//				Document doc = builder.parse(instream);
//				Element cformat;
//				Element resultElement = (Element)doc.getElementsByTagName("Result").item(0);
//				NodeList nlist = doc.getElementsByTagName(XMLDefinitions.ELEMENT_contenttype);
//				log.debug("#Results(Dublicate): "+nlist.getLength());
//				ArrayList<Element> elmsToRemove = new ArrayList<Element>();
//				for(int i=0;i<nlist.getLength();i++){
//					cformat=(Element)nlist.item(i);
//					ContentType format = new ContentType();
//					format.fromDOM(cformat);
//					log.debug("ContentType: "+format.toString()+", hashCode: "+format.hashCode());
//					if(!exist.contains(format.hashCode())){
//						log.debug("Adding: "+format.toString());
//						exist.add(format.hashCode());
//					}else{
//						log.debug("Removing: "+format.toString());
//						elmsToRemove.add(cformat);
//					}
//				}
//				for(Element elm: elmsToRemove){
//					resultElement.removeChild(elm);
//				}
//				
//				//Creating the xml...
//				TransformerFactory tFactory = TransformerFactory.newInstance();
//				javax.xml.transform.Transformer transformer = tFactory.newTransformer();
//		        transformer.setOutputProperty("omit-xml-declaration", "yes");
//		        StringWriter sw = new StringWriter();
//		        StreamResult result = new StreamResult(sw);
//		        DOMSource source = new DOMSource(doc);
//		        transformer.transform(source, result);
//		        return sw.getBuffer().toString();
//		        
//			} catch (SAXException e) {
//				log.error("Could not parse results document "+strbuilder.toString(),e);
//				throw new Exception("Could not parse results document "+strbuilder.toString());
//			} catch (TransformerException e) {
//				log.error("Could not serialize document",e);
//				throw new Exception("Could not serialize document");
//			} catch (Exception e) {
//				log.error("Could not execute query",e);
//				throw new Exception("Could not execute query: "+e.getMessage());
//			}
//		}
	}

	/**
	 * Returns the <tt>scope</tt> in which the <tt>ISManager</tt> operates.
	 * @return The <tt>scope</tt> in which the <tt>ISManager</tt> operates.
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * Sets the <tt>scope</tt> in which the <tt>ISManager</tt> operates.
	 * @param scope the <tt>scope</tt> in which the <tt>ISManager</tt> operates.
	 */
	public void setScope(String scope) {
		if(scope==null){
			log.error("Cannot set null scope to the ISManager");
			return;
		}else{
			log.debug("Setting scope "+scope.toString()+" to ISManager");
		}
		this.scope = scope;
	}
}
