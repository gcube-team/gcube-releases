/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: GenericResourceManager.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.managers.resources;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.GenericResource.Profile;
import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.resourcemanagement.support.server.exceptions.AbstractResourceException;
import org.gcube.resourcemanagement.support.server.exceptions.ResourceAccessException;
import org.gcube.resourcemanagement.support.server.exceptions.ResourceParameterException;
import org.gcube.resourcemanagement.support.server.types.AllowedResourceTypes;
import org.gcube.resourcemanagement.support.server.utils.Assertion;
import org.gcube.resourcemanagement.support.server.utils.ServerConsole;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * @author Massimiliano Assante (ISTI-CNR)
 * @author Daniele Strollo (ISTI-CNR)
 */
public class GenericResourceManager extends AbstractResourceManager {
	// Used internally to require static functionalities (e.g. deploy).
	private static GenericResourceManager singleton = null;
	private static final String LOG_PREFIX = "[GenericResource-MGR]";

	static {
		if (GenericResourceManager.singleton == null) {
			try {
				GenericResourceManager.singleton = new GenericResourceManager("dummyservice", "dummyservice");
			} catch (Exception e) {
				ServerConsole.error(LOG_PREFIX, e);
			}
		}
	}

	/**
	 * @deprecated discouraged use. With no ID some operations cannot be accessed.
	 */
	public GenericResourceManager()
	throws ResourceParameterException,
	ResourceAccessException {
		super(AllowedResourceTypes.GenericResource);
	}

	public GenericResourceManager(final String id)
	throws ResourceParameterException,
	ResourceAccessException {
		super(id, AllowedResourceTypes.GenericResource);
	}

	/**
	 * @param id
	 * @param name
	 * @param type
	 * @throws ResourceParameterException
	 * @throws ResourceAccessException
	 */
	public GenericResourceManager(final String id, final String name)
	throws ResourceParameterException,
	ResourceAccessException {
		super(id, name, AllowedResourceTypes.GenericResource);
	}

	/**
	 * @param id
	 * @param name
	 * @param type
	 * @param subtype
	 * @throws ResourceParameterException
	 * @throws ResourceAccessException
	 */
	public GenericResourceManager(final String id, final String name, final String subtype)
	throws ResourceParameterException, ResourceAccessException {
		super(id, name, AllowedResourceTypes.GenericResource, subtype);
	}

	/**
	 * Updates the resource.
	 * @param name (Mandatory) the name to assign to the resource
	 * @param description (optional) if null it will not be changed
	 * @param body (optional) if null it will not be changed
	 * @param subType (optional) if null it will not be changed
	 * @param scope (optional) if null it will not be changed
	 * @throws AbstractResourceException
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws IOException 
	 */
	public final void update(final String name,	final String description, final String body, final String subType, final ScopeBean scope) throws Exception {
		
		Assertion<AbstractResourceException> checker = new Assertion<AbstractResourceException>();
		checker.validate(name != null && name.trim().length() != 0, new ResourceParameterException("Invalid field name. Null or empty value not allowed"));

		ServerConsole.trace(LOG_PREFIX, "[RES-UPDATE] updating resource " + this.getType() + " " + this.getID());

		GenericResource resource = getResourceToEditById(this.getID(), scope);
		resource.profile().name(name.trim());
		
		if (description != null) {
			resource.profile().description(description.trim());
		}
		if (body != null) {
			appendXmlFragment(resource.profile(), body);
		}
		if (subType != null)
			resource.profile().type(subType.trim());
	
		ScopeProvider.instance.set(scope.toString());		
		RegistryPublisher publisher = getRegistryPublisher();		
		String id  = publisher.update(resource).id();
		
		if (id == null || id.length() == 0) {
			throw new Exception("The GenericResource has not been updated");
		}
		ServerConsole.info(LOG_PREFIX, "Resource Updated with ID: " + id);
	}

	private GenericResource getResourceToEditById(String id, ScopeBean scope) throws Exception {
		
		ScopeProvider.instance.set(scope.toString());	
		
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/ID/text() eq '"+ id +"'");
		 
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		 
		List<GenericResource> r = client.submit(query);
		
		if (r == null || r.isEmpty())
			throw new Exception("Could not retrieve GenericResource profile with id " + id + " in scope + " +scope);
		else 
			return r.get(0);
	}
	/**
	 * Creates a Generic Resource and returns the ID given by the
	 * resource manager at creation phase.
	 * @return the id assigned to the newly created resource
	 */
	public static final synchronized String create(final String ID,	final ScopeBean scope, final String name, final String description,	final String body, final String subType)
	throws Exception {
		Assertion<AbstractResourceException> checker = new Assertion<AbstractResourceException>();
		checker.validate(name != null && name.trim().length() != 0, new ResourceParameterException("Invalid field name. Null or empty value not allowed"));
		checker.validate(subType != null && subType.trim().length() != 0, new ResourceParameterException("Invalid field subType. Null or empty value not allowed"));

		GenericResource resource = new GenericResource();
		resource.newProfile().name(name.trim());
		
		if (description != null) {
			resource.profile().description(description.trim());
		}
		if (body != null) {
			appendXmlFragment(resource.profile(), body);
		}
		resource.profile().type(subType.trim());
	

		GenericResourceManager gm = new GenericResourceManager();
		
	
		ScopeProvider.instance.set(scope.toString());		
		RegistryPublisher publisher = gm.getRegistryPublisher();		
		String id  = publisher.create(resource).id();
		
		if (id == null || id.length() == 0) {
			throw new Exception("The GenericResource has not been created");
		}
		ServerConsole.info(LOG_PREFIX, "Resource Created with ID: " + id);
		return id;
	}

	@Override
	protected final Resource buildResource(final String xmlRepresentation) throws AbstractResourceException {
		try {
			JAXBContext ctx = JAXBContext.newInstance(GenericResource.class);
			Unmarshaller unmarshaller = ctx.createUnmarshaller();
			StringReader reader = new StringReader(xmlRepresentation);
			GenericResource deserialised = (GenericResource) unmarshaller.unmarshal(reader);
			return deserialised;
		} catch (Exception e) {
			throw new ResourceAccessException("Cannot load the resource " + this.getType(), e);
		}
	}
	
	/**
	 * append a well formed xml string to the body
	 * @param parent
	 * @param fragment
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public static void appendXmlFragment(Profile profile, String fragment) throws IOException, ParserConfigurationException {
		
		ServerConsole.debug(LOG_PREFIX, "Appending to <Body> " + fragment);
		
		DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Element elem = profile.newBody();
		Node fragmentNode;
		try {
			fragmentNode = docBuilder.parse(new InputSource(new StringReader(fragment))).getDocumentElement();
			fragmentNode = elem.getOwnerDocument().importNode(fragmentNode, true);
			elem.appendChild(fragmentNode);
		} catch (SAXException e) {
			//in case no xml is entered, just text
			ServerConsole.warn("no valid xml appending this:" + fragment);
			profile.newBody(fragment);
		}

	}
}
