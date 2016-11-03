//package org.gcube.datatransformation.datatransformationservice.clients;
//
//import java.io.ByteArrayInputStream;
//import java.io.StringWriter;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.transform.TransformerException;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
//
//import org.gcube.common.core.contexts.GHNContext;
//import org.gcube.common.core.informationsystem.client.AtomicCondition;
//import org.gcube.common.core.informationsystem.client.ISClient;
//import org.gcube.common.core.informationsystem.client.XMLResult;
//import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericQuery;
//import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericResourceQuery;
//import org.gcube.common.core.resources.GCUBEGenericResource;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.common.core.utils.logging.GCUBELog;
//import org.gcube.datatransformation.datatransformationlibrary.imanagers.queries.ContentTypeCondition;
//import org.gcube.datatransformation.datatransformationlibrary.imanagers.queries.ContentTypeQueryObject;
//import org.gcube.datatransformation.datatransformationlibrary.imanagers.queries.QueryParser;
//import org.gcube.datatransformation.datatransformationlibrary.imanagers.queries.TransformationUnitQueryObject;
//import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
//import org.gcube.datatransformation.datatransformationlibrary.model.XMLDefinitions;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.NodeList;
//import org.xml.sax.SAXException;
//
//public class ISQueryTest {
//	
//	static GCUBELog log = new GCUBELog(ISQueryTest.class);
//	
//	static GCUBEScope scope = GCUBEScope.getScope("/gcube/devsec");
//	
//	public static void main(String[] args) throws Exception {
//		
//		GCUBEGenericResource resource;
//		try {
//			ISClient client = GHNContext.getImplementation(ISClient.class);
//			GCUBEGenericResourceQuery query = client.getQuery(GCUBEGenericResourceQuery.class);
//			query.addAtomicConditions(new AtomicCondition("/ID",args[0]));
//			
//			List<GCUBEGenericResource> results = client.execute(query, scope);
//			
//			if(results==null || results.size()==0){
//				if(results==null){
//					log.error("NULLLLLLLLLLLLLLLLLL");
//				}
//					
//				log.error("Could not find transformation program with id "+args[0]);
//				throw new Exception("Could not find transformation program with id "+args[0]);
//			}
//			resource = results.get(0);
//			log.info(resource.getName());
//		} catch (Exception e) {
//			log.error("Could not invoke IS to find the transformation program with id "+args[0],e);
//			throw new Exception("Could not invoke IS to find the transformation program with id "+args[0]);
//		}
//		
////		String query = "GET SFORMAT " +
////			"WHERE MIMETYPE=image";
////		String query = "GET TRANSFORMATION " +
////				"WHERE SFORMAT.1.MIMETYPE=image " +
////				"AND SFORMAT.1.MIMESUBTYPE=png " +
////				"AND TFORMAT.1.MIMESUBTYPE=jpg ";
////		
////		QueryObject object = QueryParser.parse(query);
////		String result = null;
////		if(FormatQueryObject.class.isInstance(object)){
////			System.out.println("--Quering Formats--");
////			FormatQueryObject fobject = (FormatQueryObject)object;
////			result = queryContentFormat(fobject);
////		}else if(TransformationQueryObject.class.isInstance(object)){
////			System.out.println("--Quering transformations--");
////			TransformationQueryObject tobject = (TransformationQueryObject)object;
////			result = queryTransformation(tobject);
////		}
////		log.debug("Result: "+result);
//	}
//	
//	//TODO: Format Parameters(=Probably one more nested query in content formats of a transformation...)
//	public static String queryTransformation(TransformationUnitQueryObject object) throws Exception {
//		ISClient client = GHNContext.getImplementation(ISClient.class);
//		GCUBEGenericQuery query = client.getQuery("GCUBEResourceQuery");
//		boolean putAnd=false;
//		StringBuilder expression = new StringBuilder();
//		expression.append("declare namespace is = 'http://gcube-system.org/namespaces/informationsystem/registry'; ");
//		expression.append("declare namespace gc = 'http://gcube-system.org/namespaces/common/core/porttypes/GCUBEProvider'; ");
//		expression.append("for $result in collection('/db/Profiles/GenericResource')//Document/Data/is:Profile/Resource ");
//		expression.append("where starts-with($result/Profile/Name/string(), 'gDTSTP_') ");
//		if(object.transformationProgramID!=null && object.transformationProgramID.trim().length()>0)
//			expression.append("and $result/ID/string() eq '"+object.transformationProgramID+"'");
//		expression.append(" return ");
//		expression.append("for $resultin in $result/Profile/Body/gDTSTransformationProgram/Transformations/Transformation ");
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
//							expression.append("starts-with($resultin/Sources/Source/ContentFormat/Mimetype/string(), '"+fcondition.getMimetype()+"/') ");
//						}
//						if(fcondition.getMimesubtype()!=null){
//							expression.append("ends-with($resultin/Sources/Source/ContentFormat/Mimetype/string(), '/"+fcondition.getMimesubtype()+"') ");
//						}
//					}else{
//						
//						expression.append("$resultin/Sources/Source/ContentFormat/Mimetype/string() eq '"+fcondition.getMimetype()+"/"+fcondition.getMimesubtype()+"' ");
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
//							expression.append("starts-with($resultin/Target/ContentFormat/Mimetype/string(), '"+fcondition.getMimetype()+"/') ");
//						}
//						if(fcondition.getMimesubtype()!=null){
//							expression.append("ends-with($resultin/Target/ContentFormat/Mimetype/string(), '/"+fcondition.getMimesubtype()+"') ");
//						}
//					}else{
//						expression.append("$resultin/Target/ContentFormat/Mimetype/string() eq '"+fcondition.getMimetype()+"/"+fcondition.getMimesubtype()+"' ");
//					}
//				}
//			}
//		
//		}
//		
//		expression.append("return <Transformation><TransformationProgramID>{$result/ID/string()}</TransformationProgramID><TransformationID>{$resultin/string(@id)}</TransformationID></Transformation>");
//		
//		query.setExpression(expression.toString());
//		
//		List<XMLResult> results = client.execute(query, scope);
//		StringBuilder finalresult = new StringBuilder();
//		finalresult.append("<Result>");
//		for(XMLResult result: results){
//			log.debug("XMLRESULT\n"+result);
//			finalresult.append(result.toString());
//		}
//		finalresult.append("</Result>");
//		return finalresult.toString();
//	}
//	
//	public static String queryTransformation(String targetMimeType, String ... sourceMimeTypes) throws Exception {
//		ISClient client = GHNContext.getImplementation(ISClient.class);
//		GCUBEGenericQuery query = client.getQuery("GCUBEResourceQuery");
//		boolean putAnd=false;
//		StringBuilder expression = new StringBuilder();
//		expression.append("declare namespace is = 'http://gcube-system.org/namespaces/informationsystem/registry'; ");
//		expression.append("declare namespace gc = 'http://gcube-system.org/namespaces/common/core/porttypes/GCUBEProvider'; ");
//		expression.append("for $result in collection('/db/Profiles/GenericResource')//Document/Data/is:Profile/Resource ");
//		expression.append("where starts-with($result/Profile/Name/string(), 'gDTSTP_') ");
//		expression.append(" return ");
//		expression.append("for $resultin in $result/Profile/Body/gDTSTransformationProgram/Transformations/Transformation ");
//		if(targetMimeType!=null && targetMimeType.trim().length()>0){
//			expression.append("where starts-with($resultin/Target/ContentFormat/Mimetype/string(), '"+targetMimeType+"') ");
//			putAnd=true;
//		}
//		if(sourceMimeTypes!=null && sourceMimeTypes.length>0){
//			for(String srcMimetype: sourceMimeTypes){
//				if(srcMimetype!=null && srcMimetype.trim().length()>0){
//					if(putAnd){
//						expression.append(" and ");
//					}
//					expression.append("where starts-with($resultin/Sources/Source/ContentFormat/Mimetype/string(), '"+srcMimetype+"') ");
//				}
//			}
//		}
//		expression.append("return <Transformation><TransformationProgramID>{$result/ID/string()}</TransformationProgramID><TransformationID>{$resultin/string(@id)}</TransformationID></Transformation>");
//		
//		query.setExpression(expression.toString());
//		
//		List<XMLResult> results = client.execute(query, scope);
//		StringBuilder finalresult = new StringBuilder();
//		finalresult.append("<Result>");
//		for(XMLResult result: results){
//			log.debug("XMLRESULT\n"+result);
//			finalresult.append(result.toString());
//		}
//		finalresult.append("</Result>");
//		return finalresult.toString();
//	}
//	
//	public static String queryContentFormat(ContentTypeQueryObject object) throws Exception {
//		
//		StringBuilder expression = new StringBuilder();
//		expression.append("declare namespace is = 'http://gcube-system.org/namespaces/informationsystem/registry'; ");
//		expression.append("declare namespace gc = 'http://gcube-system.org/namespaces/common/core/porttypes/GCUBEProvider'; ");
//		expression.append("for $result in collection('/db/Profiles/GenericResource')//Document/Data/is:Profile/Resource ");
//		expression.append("where starts-with($result/Profile/Name/string(), 'gDTSTP_') ");
////		TP
//		if(object.transformationProgramID!=null && object.transformationProgramID.trim().length()>0)
//			expression.append("and $result/ID/string() eq '"+object.transformationProgramID+"'");
//		expression.append(" return ");
//		expression.append("for $resultin in $result/Profile/Body/gDTSTransformationProgram/Transformations/Transformation");
//		if(object.transformationUnitID!=null && object.transformationUnitID.trim().length()>0)
//			expression.append("[string(@id) eq '"+object.transformationUnitID+"']");
//		
//		if(object.getResultType().equals(QueryParser.QUERYSOURCECONTENTTYPE)){
//			expression.append("/Sources/Source ");
//		}else{
//			expression.append("/Target ");
//		}
//		System.out.println("Mimetype: "+object.contentTypeCondition.getMimetype());
//		System.out.println("Mimetype: "+object.contentTypeCondition.getMimesubtype());
//		if(object.contentTypeCondition.getMimetype()==null || object.contentTypeCondition.getMimesubtype()==null){
//			if(object.contentTypeCondition.getMimetype()!=null){
//				expression.append("where starts-with($resultin/ContentFormat/Mimetype/string(), '"+object.contentTypeCondition.getMimetype()+"/') ");
//			}
//			if(object.contentTypeCondition.getMimesubtype()!=null){
//				expression.append("where ends-with($resultin/ContentFormat/Mimetype/string(), '/"+object.contentTypeCondition.getMimesubtype()+"') ");
//			}
//		}else{
//			expression.append("where $resultin/ContentFormat/Mimetype/string() eq '"+object.contentTypeCondition.getMimetype()+"/"+object.contentTypeCondition.getMimesubtype()+"' ");
//		}
//		//TODO: Add format parameters...	
//		
//		expression.append("return $resultin/ContentFormat");
//		return queryContentFormatByExpression(expression.toString());
//	}
//	
//	public static String queryContentFormat(String transformationProgramID, String transformationID, String mimetype, boolean isSource) throws Exception {
//		StringBuilder expression = new StringBuilder();
//		expression.append("declare namespace is = 'http://gcube-system.org/namespaces/informationsystem/registry'; ");
//		expression.append("declare namespace gc = 'http://gcube-system.org/namespaces/common/core/porttypes/GCUBEProvider'; ");
//		expression.append("for $result in collection('/db/Profiles/GenericResource')//Document/Data/is:Profile/Resource ");
//		expression.append("where starts-with($result/Profile/Name/string(), 'gDTSTP_') ");
//		//TP
//		if(transformationProgramID!=null && transformationProgramID.trim().length()>0)
//			expression.append("and $result/ID/string() eq '"+transformationProgramID+"'");
//		expression.append(" return ");
//		expression.append("for $resultin in $result/Profile/Body/gDTSTransformationProgram/Transformations/Transformation");
//		if(transformationID!=null && transformationID.trim().length()>0)
//			expression.append("[string(@id) eq '"+transformationID+"']");
//		
//		if(isSource){
//			expression.append("/Sources/Source ");
//			if(mimetype!=null && mimetype.trim().length()>0)
//				expression.append("where starts-with($resultin/ContentFormat/Mimetype/string(), '"+mimetype+"') ");
//		}else{
//			expression.append("/Target ");
//			if(mimetype!=null && mimetype.trim().length()>0)
//				expression.append("where starts-with($resultin/ContentFormat/Mimetype/string(), '"+mimetype+"') ");
//		}
//		
//		expression.append("return $resultin/ContentFormat");
//		return queryContentFormatByExpression(expression.toString());
//	}
//	
//	public static String queryContentFormatByExpression(String expression) throws Exception {
//		//Client initialization...
//		ISClient client = GHNContext.getImplementation(ISClient.class);
//		GCUBEGenericQuery query = client.getQuery("GCUBEResourceQuery");
//		query.setExpression(expression);
//		
//		List<XMLResult> results = client.execute(query, scope);
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
//	}
//}
