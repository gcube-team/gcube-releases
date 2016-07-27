/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.resources.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.codehaus.jettison.json.JSONException;
import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.EntityException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.FacetNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import com.tinkerpop.blueprints.util.io.graphson.GraphSONMode;
import com.tinkerpop.blueprints.util.io.graphson.GraphSONUtility;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
public class Utility {

	private static final Logger logger = LoggerFactory.getLogger(Utility.class);

	public static String orientVertexToJsonString(OrientVertex orientVertex) {
		ORecord oRecord = orientVertex.getRecord();
		return oRecord.toJSON();
	}

	public static String vertexToJsonString(Vertex vertex, boolean raw)
			throws JSONException {
		if (raw) {
			return GraphSONUtility.jsonFromElement(vertex,
					vertex.getPropertyKeys(), GraphSONMode.EXTENDED).toString();
		} else {
			Set<String> keys = new HashSet<>(vertex.getPropertyKeys());
			for (String key : vertex.getPropertyKeys()) {
				if (key.startsWith("_")) {
					keys.remove(key);
				}
			}
			return GraphSONUtility.jsonFromElement(vertex, keys,
					GraphSONMode.COMPACT).toString();
		}
	}

	public static String vertexToJsonString(Vertex vertex) {
		try {
			return vertexToJsonString(vertex, true);
		} catch (Exception e) {
			return String.valueOf(vertex);
		}
	}

	public static String orientEdgeToJsonString(OrientEdge orientEdge) {
		ORecord oRecord = orientEdge.getRecord();
		return oRecord.toJSON();
	}

	public static String edgeToJsonString(Edge edge, boolean raw)
			throws JSONException {
		if (raw) {
			return GraphSONUtility.jsonFromElement(edge,
					edge.getPropertyKeys(), GraphSONMode.EXTENDED).toString();
		} else {
			Set<String> keys = new HashSet<>(edge.getPropertyKeys());
			for (String key : edge.getPropertyKeys()) {
				if (key.startsWith("_")) {
					keys.remove(key);
				}
			}
			return GraphSONUtility.jsonFromElement(edge, keys,
					GraphSONMode.COMPACT).toString();
		}
	}

	public static String edgeToJsonString(Edge edge) {
		try {
			return edgeToJsonString(edge, true);
		} catch (Exception e) {
			return String.valueOf(edge);
		}
	}

	public static Vertex getEntityByUUID(OrientGraph orientGraph,
			String entityType, String uuid) throws ResourceRegistryException {

		if (entityType == null) {
			entityType = Entity.NAME;
		}

		// TODO Rewrite using Gremlin
		String select = "SELECT FROM " + entityType + " WHERE "
				+ Entity.HEADER_PROPERTY + "." + Header.UUID_PROPERTY + " = \""
				+ uuid + "\"";

		OSQLSynchQuery<Vertex> osqlSynchQuery = new OSQLSynchQuery<Vertex>(
				select);

		Iterable<Vertex> vertexes = orientGraph.command(osqlSynchQuery)
				.execute();
		if (vertexes == null || !vertexes.iterator().hasNext()) {
			String error = String.format("No %s with UUID %s was found",
					entityType, uuid);
			logger.info(error);
			throw new EntityException(error);
		}

		Iterator<Vertex> iterator = vertexes.iterator();
		Vertex entity = iterator.next();
		
		logger.trace("{} with {} is : {}", entityType, uuid,
				Utility.vertexToJsonString(entity));
		
		if (iterator.hasNext()) {
			throw new ResourceRegistryException("Found more than one "
					+ entityType + " with uuid " + uuid
					+ ". This is a fatal error please contact Admnistrator");
		}

		return entity;
	}

	public static Edge getRelationByUUID(OrientGraph orientGraph,
			String relationType, String uuid) throws ResourceRegistryException {

		if (relationType == null) {
			relationType = Relation.class.getSimpleName();
		}

		// TODO Rewrite using Gremlin
		String select = "SELECT FROM " + relationType + " WHERE "
				+ Relation.HEADER_PROPERTY + "." + Header.UUID_PROPERTY
				+ " = \"" + uuid + "\"";

		OSQLSynchQuery<Edge> osqlSynchQuery = new OSQLSynchQuery<Edge>(select);

		Iterable<Edge> edges = orientGraph.command(osqlSynchQuery).execute();
		if (edges == null || !edges.iterator().hasNext()) {
			String error = String.format("No %s with UUID %s was found",
					relationType, uuid);
			logger.info(error);
			throw new FacetNotFoundException(error);
		}

		Iterator<Edge> iterator = edges.iterator();
		Edge relation = iterator.next();

		logger.trace("{} with {} is : {}", relationType, uuid, 
				Utility.edgeToJsonString(relation));

		if (iterator.hasNext()) {
			throw new ResourceRegistryException("Found more than one "
					+ relationType + " with uuid " + uuid
					+ ". This is a fatal error please contact Admnistrator");
		}

		return relation;
	}
}