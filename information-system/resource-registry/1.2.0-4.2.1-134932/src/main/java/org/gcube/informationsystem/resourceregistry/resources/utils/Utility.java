/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.resources.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientElement;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.util.io.graphson.GraphSONMode;
import com.tinkerpop.blueprints.util.io.graphson.GraphSONUtility;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class Utility {

	private static final Logger logger = LoggerFactory.getLogger(Utility.class);

	public static JSONObject toJsonObject(OrientElement element, boolean raw) throws JSONException {
		return new JSONObject(toJsonString(element, raw));
	}

	public static String toJsonString(OrientElement element, boolean raw) {
		ORecord oRecord = element.getRecord();
		return toJsonString(oRecord, raw);
	}

	public static String toJsonString(ORecord oRecord, boolean raw) {
		if (raw) {
			return oRecord.toJSON();
		}
		return oRecord.toJSON("class");
	}
	
	public static JSONObject toJsonObject(Element element, boolean raw) throws JSONException {
		if(raw){
			return GraphSONUtility.jsonFromElement(element,
					element.getPropertyKeys(), GraphSONMode.EXTENDED);
		}else{
			Set<String> keys = new HashSet<>(element.getPropertyKeys());
			for (String key : element.getPropertyKeys()) {
				if (key.startsWith("_")) {
					keys.remove(key);
				}
			}
			return GraphSONUtility.jsonFromElement(element, keys,
					GraphSONMode.EXTENDED);
		}
	}

	public static String toJsonString(Element element, boolean raw) {
		try {
			return toJsonObject(element, true).toString();
		} catch (Exception e) {
			return String.valueOf(element);
		}
	}
	
	public static <El extends Element> El getElementByUUID(OrientGraph orientGraph,
			String elementType, UUID uuid, Class<? extends El> clz) throws ResourceRegistryException {

		if (elementType == null || elementType.compareTo("")==0) {
			if(Vertex.class.isAssignableFrom(clz)){
				elementType = Entity.NAME;
			}
			if(Edge.class.isAssignableFrom(clz)){
				elementType = Relation.NAME;
			}
		}

		// TODO Rewrite using Gremlin
		String select = "SELECT FROM " + elementType + " WHERE "
				+ Relation.HEADER_PROPERTY + "." + Header.UUID_PROPERTY
				+ " = \"" + uuid.toString() + "\"";

		OSQLSynchQuery<El> osqlSynchQuery = new OSQLSynchQuery<>(select);

		Iterable<El> elements = orientGraph.command(osqlSynchQuery).execute();
		if (elements == null || !elements.iterator().hasNext()) {
			String error = String.format("No %s with UUID %s was found",
					elementType, uuid.toString());
			logger.info(error);
			throw new ResourceRegistryException(error);
		}

		Iterator<El> iterator = elements.iterator();
		El element = iterator.next();

		logger.trace("{} with {} is : {}", elementType, uuid.toString(),
				Utility.toJsonString(element, true));

		if (iterator.hasNext()) {
			throw new ResourceRegistryException("Found more than one "
					+ elementType + " with uuid " + uuid.toString()
					+ ". This is a fatal error please contact Admnistrator");
		}

		return element;
	}
	
}