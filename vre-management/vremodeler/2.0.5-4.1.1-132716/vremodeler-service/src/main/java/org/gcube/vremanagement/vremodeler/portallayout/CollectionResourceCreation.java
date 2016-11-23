package org.gcube.vremanagement.vremodeler.portallayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericResourceQuery;
import org.gcube.common.core.informationsystem.publisher.ISPublisher;
import org.gcube.common.core.resources.GCUBEGenericResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.vremanagement.vremodeler.impl.ServiceContext;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CollectionResourceCreation {

	private String vreId;
	private String vreName;
	private String createdResourceId=null;
	
	public String getCreatedResourceId(){
		return this.createdResourceId;
	}
	
	public CollectionResourceCreation(String vreId, String vreName){
		this.vreId= vreId;
		this.vreName= vreName;
	}
	
	/**
	 * create and publish the GenericResource corresponding to the scenario collection 
	 * 
	 * @throws Exception
	 */
	public void createAndPublish() throws Exception{
		GCUBEGenericResource res= GHNContext.getImplementation(GCUBEGenericResource.class);
		res.load(new StringReader(this.transformCollectionResource()));
		ISPublisher pub= GHNContext.getImplementation(ISPublisher.class);
		res.setID("");
		res.removeScope(ServiceContext.getContext().getScope());
		res.load(new StringReader(pub.registerGCUBEResource(res, GCUBEScope.getScope(ServiceContext.getContext().getScope()+"/"+this.vreName), ServiceContext.getContext())));
		this.createdResourceId= res.getID();
	}
	
	
	private StringBuilder getCollections() throws Exception {
		/*
		ResultSet rs= DBInterface.queryDB("SELECT c.name FROM COLLECTION c, VRERELATEDCOLLECTION vc WHERE c.id=vc.COLLID AND vc.vreid='"+this.vreId+"';");
		StringBuilder collectionToMaintain=new StringBuilder();
		while (rs.next()){
			collectionToMaintain.append(" @name='"+rs.getString(1)+"' or");
		}
		return new StringBuilder(collectionToMaintain.subSequence(0, collectionToMaintain.length()-3));
		*/
		return null;
	}
	
	
	private String transformCollectionResource() throws Exception{
		ISClient client= GHNContext.getImplementation(ISClient.class);
		GCUBEGenericResourceQuery query= client.getQuery(GCUBEGenericResourceQuery.class);
		query.addAtomicConditions(new AtomicCondition("/Profile/Name","ScenarioCollectionInfo"), new AtomicCondition("/Profile/Body/VRE/@name",ServiceContext.getContext().getScope().toString()));
		List<GCUBEGenericResource> list= client.execute(query, ServiceContext.getContext().getScope()) ;
		if (list.size()==0) throw new Exception("GenericResource ScenarioCollectionInfo not found in IS");
		StringWriter sw= new StringWriter();
		list.get(0).store(sw);
		InputStream is=CollectionResourceCreation.class.getResourceAsStream("/org/gcube/vremanagement/vremodeler/portallayout/xslt/ScenarioCollectionInfoTransformer.xsl");
		StringBuffer out = new StringBuffer();
	    byte[] b = new byte[4096];
	    for (int n; (n = is.read(b)) != -1;) {
	        out.append(new String(b, 0, n));
	    }
	    String xsltCollectionTransformer=out.toString().replace("%%COLLECATIONCOMPARISON%%", this.getCollections()).replace("%%VRENAME%%", ServiceContext.getContext().getScope()+"/"+this.vreName );
	    return doXSLTrasformation(sw.toString(),xsltCollectionTransformer );
	}
	
	
	
	private String doXSLTrasformation(String profile, String xsl) {
		StreamSource stylesource = new StreamSource(new StringReader(xsl));
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = tFactory.newTransformer(stylesource);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			return "";
		}
		Document document = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return "";
		}
		StringReader reader = new StringReader(profile);

		InputSource inputSource = new InputSource(reader);
		try {
			document = builder.parse(inputSource);
		} catch (SAXException e) {
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		DOMSource source = new DOMSource(document);
		ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(resultStream);
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
			return "";
		}
		return resultStream.toString();
	}

}
