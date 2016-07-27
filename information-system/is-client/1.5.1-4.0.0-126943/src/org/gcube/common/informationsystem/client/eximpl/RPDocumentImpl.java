package org.gcube.common.informationsystem.client.eximpl;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.informationsystem.client.RPDocument;
import org.gcube.common.core.informationsystem.client.XMLResult;
import org.gcube.common.core.informationsystem.client.ISClient.ISMalformedResultException;
import org.gcube.common.core.informationsystem.client.impl.AbstractXMLResult;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.state.GCUBEWSResourceKey;
import org.gcube.common.core.state.GCUBEWSResourcePropertySet;
import org.globus.wsrf.ResourceKey;
import org.globus.wsrf.impl.SimpleResourceKey;
import org.globus.wsrf.impl.SimpleResourcePropertyMetaData;
import org.globus.wsrf.utils.AddressingUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * A specialisation of {@link XMLResult} to Resource Property Document of WS-Resources.
 * @author Fabio Simeoni (University of Strathclyde)
 *
 */
public class RPDocumentImpl extends AbstractXMLResult implements RPDocument {


	
	EndpointReferenceType endpoint;

	GCUBEWSResourceKey key;

	/**
	 * Creates an instance from an RPD serialisation.
	 * @param result the serialisation.
	 */
	public RPDocumentImpl(String result) throws ISResultInitialisationException {
		super(result);
	}
	
	protected void parse(String result) throws ISMalformedResultException {
		
		//we parse endpoint and then reduce result to RPD only
		Matcher m = Pattern.compile("<Data>(.*?)</Data>",Pattern.DOTALL).matcher(result); 
		if (m.find()) super.parse(m.group(0));
		else throw new ISMalformedResultException(new IllegalArgumentException());

		//sadly, we need to reparse with namespace validation on to get endpoint...
		try {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
			Element dom = domFactory.newDocumentBuilder().parse(new InputSource(new StringReader(result))).getDocumentElement();
			String uri = dom.getElementsByTagName("Source").item(0).getTextContent();
			NodeList nodes = dom.getElementsByTagName("CompleteSourceKey").item(0).getChildNodes();
			for (int i=0; i<nodes.getLength();i++) {
				Node node = nodes.item(i);
				if (node instanceof Element) {
					ResourceKey key = new SimpleResourceKey(new QName(node.getNamespaceURI(),node.getLocalName()), node.getTextContent());
					this.key = new GCUBEWSResourceKey(key);
					this.endpoint= AddressingUtils.createEndpointReference(uri,key);
					return;
				}
			}
		}
		catch(Exception e) {throw new ISMalformedResultException(e);}
	}
	
	/** {@inheritDoc}*/
	public GCUBEWSResourceKey getKey() {
		try {
			if (this.dom==null) this.parse(this.result);
		}catch(Exception e) {return null;}
		return this.key;
	}
	
	@Override
	//** {@inheritDoc} */
	public List<String> evaluate(String xpath) throws ISResultEvaluationException {
		return super.evaluate("/Data"+xpath);
	}
	
	/**
	 * Returns the WS-Resource endpoint.
	 * @return the endpoint.
	 */
	public EndpointReferenceType getEndpoint() {
		try {
			if (this.dom==null) this.parse(this.result);
		}catch(Exception e) {return null;}
		return this.endpoint;
	}
	
	/**
	 * Returns the identifier of the service of the WS-Resource.
	 * @return the identifier.
	 */
	public String getServiceID() {
		try {return this.evaluate("/"+GCUBEWSResourcePropertySet.RP_SID_NAME+"/text()").get(0);}
		catch(Exception e) {return null;}
	}
	/**
	 * Returns the name of the service of origin of the WS-Resource.
	 * @return the name.
	 */
	public String getServiceName() {
		try {return this.evaluate("/"+GCUBEWSResourcePropertySet.RP_SNAME_NAME+"/text()").get(0);}
		catch(Exception e) {return null;}
	}
	/**
	 * Returns the class of the service of origin of the WS-Resource.
	 * @return the class.
	 */
	public String getServiceClass() {
		try {return this.evaluate("/"+GCUBEWSResourcePropertySet.RP_SCLASS_NAME+"/text()").get(0);}
		catch(Exception e) {return null;}
	}
	/**
	 * Returns the identifier of the running instance of origin of the WS-Resource.
	 * @return the identifier.
	 */
	public String getRIID() {
		try {return this.evaluate("/"+GCUBEWSResourcePropertySet.RP_RIID_NAME+"/text()").get(0);}
		catch(Exception e) {return null;}
	}
	/**
	 * Returns the identifier of the gHN of origin of the WS-Resource.
	 * @return the identifier.
	 */
	public String getGHNID() {
		try {return this.evaluate("/"+GCUBEWSResourcePropertySet.RP_GID_NAME+"/text()").get(0);}
		catch(Exception e) {return null;}
	}
	
	/**
	 * Returns the scopes of the WS-Resource.
	 * @return the scopes.
	 */
	public List<GCUBEScope> getScope() {
		try {
			List<GCUBEScope> scopes = new ArrayList<GCUBEScope>();
			for (String scope : this.evaluate("/"+GCUBEWSResourcePropertySet.RP_SCOPES_NAME+"/text()")) scopes.add(GCUBEScope.getScope(scope));
			return scopes;
		}
		catch(Exception e) {return null;}
		
		
	}
	/**
	 * Returns the termination time of the WS-Resource.
	 * @return the termination time.
	 */
	public Calendar getTerminationTime() {//TODO
		try{
			Calendar time = Calendar.getInstance();
			time.setTime(new SimpleDateFormat().parse(this.evaluate("/"+SimpleResourcePropertyMetaData.TERMINATION_TIME.getName().getLocalPart()+"/text()").get(0)));
			return time;
		} catch(Exception e){return null;}
	}
}
