package org.gcube.informationsystem.resourceregistry.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.gcube.informationsystem.impl.utils.ISMapper;
import org.gcube.informationsystem.model.embedded.Embedded;
import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERNotFoundException;
import org.gcube.informationsystem.resourceregistry.context.ContextUtility;
import org.gcube.informationsystem.resourceregistry.context.security.AdminSecurityContext;
import org.gcube.informationsystem.resourceregistry.context.security.SecurityContext.PermissionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientElement;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.util.io.graphson.GraphSONMode;
import com.tinkerpop.blueprints.util.io.graphson.GraphSONUtility;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class Utility {
	
	private static final Logger logger = LoggerFactory.getLogger(Utility.class);
	
	public static final String SHOULD_NOT_OCCUR_ERROR_MESSAGE = "This is really strange and should not occur. Please contact the system administrator.";
	
	public static JSONObject toJsonObject(OrientElement element, boolean raw) throws ResourceRegistryException {
		try {
			return new JSONObject(toJsonString(element, raw));
		} catch(Exception e) {
			throw new ResourceRegistryException(e);
		}
	}
	
	public static String toJsonString(OrientElement element, boolean raw) {
		ORecord oRecord = element.getRecord();
		return toJsonString(oRecord, raw);
	}
	
	public static String toJsonString(ORecord oRecord, boolean raw) {
		if(raw) {
			return oRecord.toJSON();
		}
		return oRecord.toJSON("class");
	}
	
	public static JSONObject toJsonObject(Element element, boolean raw) throws JSONException {
		if(raw) {
			return GraphSONUtility.jsonFromElement(element, element.getPropertyKeys(), GraphSONMode.EXTENDED);
		} else {
			Set<String> keys = new HashSet<>(element.getPropertyKeys());
			for(String key : element.getPropertyKeys()) {
				if(key.startsWith("_")) {
					keys.remove(key);
				}
			}
			return GraphSONUtility.jsonFromElement(element, keys, GraphSONMode.EXTENDED);
		}
	}
	
	public static String toJsonString(Element element, boolean raw) {
		try {
			return toJsonObject(element, true).toString();
		} catch(Exception e) {
			return String.valueOf(element);
		}
	}
	
	public static <El extends Element> El getElementByUUIDAsAdmin(String elementType, UUID uuid,
			Class<? extends El> clz) throws ERNotFoundException, ResourceRegistryException {
		OrientGraphNoTx orientGraphNoTx = null;
		try {
			AdminSecurityContext adminSecurityContext = ContextUtility.getAdminSecurityContext();
			orientGraphNoTx = adminSecurityContext.getGraphNoTx(PermissionMode.READER);
			return Utility.getElementByUUID(orientGraphNoTx, elementType, uuid, clz);
		} finally {
			if(orientGraphNoTx != null) {
				orientGraphNoTx.shutdown();
			}
		}
	}
	
	public static <El extends Element> El getElementByUUID(Graph graph, String elementType, UUID uuid,
			Class<? extends El> clz) throws ERNotFoundException, ResourceRegistryException {
		
		if(elementType == null || elementType.compareTo("") == 0) {
			if(Vertex.class.isAssignableFrom(clz)) {
				elementType = Entity.NAME;
			}
			if(Edge.class.isAssignableFrom(clz)) {
				elementType = Relation.NAME;
			}
		}
		
		// TODO Rewrite using Gremlin
		String select = "SELECT FROM " + elementType + " WHERE " + Relation.HEADER_PROPERTY + "." + Header.UUID_PROPERTY
				+ " = \"" + uuid.toString() + "\"";
		
		OSQLSynchQuery<El> osqlSynchQuery = new OSQLSynchQuery<>(select);
		
		Iterable<El> elements = ((OrientBaseGraph) graph).command(osqlSynchQuery).execute();
		if(elements == null || !elements.iterator().hasNext()) {
			String error = String.format("No %s with UUID %s was found", elementType, uuid.toString());
			logger.info(error);
			throw new ERNotFoundException(error);
		}
		
		Iterator<El> iterator = elements.iterator();
		El element = iterator.next();
		
		logger.trace("{} with {} is : {}", elementType, uuid.toString(), Utility.toJsonString(element, true));
		
		if(iterator.hasNext()) {
			throw new ResourceRegistryException("Found more than one " + elementType + " with uuid " + uuid.toString()
					+ ". This is a fatal error please contact Admnistrator");
		}
		
		return element;
	}
	
	public static <E extends Embedded> E getEmbedded(Class<E> clz, Element element, String property)
			throws ResourceRegistryException {
		try {
			ODocument oDocument = element.getProperty(property);
			E e = ISMapper.unmarshal(clz, oDocument.toJSON());
			return e;
		} catch(Exception ex) {
			String error = String.format("Error while getting %s from %s", property, toJsonString(element, true));
			throw new ResourceRegistryException(error, ex);
		}
	}
	
	public static UUID getUUID(Element element) throws ResourceRegistryException {
		/*
		 * ODocument header = element.getProperty(Entity.HEADER_PROPERTY); String
		 * contextID = header.field(Header.UUID_PROPERTY); return
		 * UUID.fromString(contextID);
		 */
		Header header = HeaderUtility.getHeader(element);
		return header.getUUID();
	}
	
}