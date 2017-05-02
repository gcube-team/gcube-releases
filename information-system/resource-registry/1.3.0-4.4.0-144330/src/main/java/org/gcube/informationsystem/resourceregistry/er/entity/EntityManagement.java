/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.er.entity;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.EntityAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.context.ContextUtility;
import org.gcube.informationsystem.resourceregistry.context.SecurityContextMapper.PermissionMode;
import org.gcube.informationsystem.resourceregistry.er.ERManagement;
import org.gcube.informationsystem.resourceregistry.er.relation.RelationManagement;
import org.gcube.informationsystem.resourceregistry.utils.HeaderUtility;
import org.gcube.informationsystem.resourceregistry.utils.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public abstract class EntityManagement<E extends Entity> extends
		ERManagement<E, Vertex> {

	private static Logger logger = LoggerFactory
			.getLogger(EntityManagement.class);

	protected EntityManagement(Class<E> entityClass) {
		super(entityClass);

		this.ignoreKeys.add(Entity.HEADER_PROPERTY);

		this.ignoreStartWithKeys.add(OrientVertex.CONNECTION_IN_PREFIX
				.toLowerCase());
		this.ignoreStartWithKeys.add(OrientVertex.CONNECTION_OUT_PREFIX
				.toLowerCase());
		this.ignoreStartWithKeys.add(OrientVertex.CONNECTION_IN_PREFIX
				.toUpperCase());
		this.ignoreStartWithKeys.add(OrientVertex.CONNECTION_OUT_PREFIX
				.toUpperCase());

		if (Facet.class.isAssignableFrom(entityClass)) {
			this.baseType = Facet.NAME;
		} else if (Resource.class.isAssignableFrom(entityClass)) {
			this.baseType = Resource.NAME;
		} else {
			this.baseType = Entity.NAME;
		}
	}

	protected EntityManagement(Class<E> entityClass, OrientGraph orientGraph) {
		this(entityClass);
		this.orientGraph = orientGraph;
	}

	protected Vertex createVertex() throws EntityAlreadyPresentException,
			ResourceRegistryException {

		logger.trace("Going to create {} for {} ({}) using {}",
				Vertex.class.getSimpleName(), baseType, erType, jsonNode);

		try {

			Vertex vertexEntity = orientGraph.addVertex("class:" + erType);

			try {

				Vertex v = getElement();
				if (v != null) {
					String error = String.format(
							"A %s with UUID %s already exist", erType,
							uuid.toString());
					throw new EntityAlreadyPresentException(error);
				}

			} catch (EntityAlreadyPresentException e) {
				throw e;
			} catch (Exception e) {
				// no header or no header with uuid is provided and it is fine
			}

			this.element = vertexEntity;

			Header entityHeader = HeaderUtility.getHeader(jsonNode, true);
			if (entityHeader != null) {
				element.setProperty(Entity.HEADER_PROPERTY, entityHeader);
			} else {
				entityHeader = HeaderUtility.addHeader(element, null);
			}

			if (Resource.class.isAssignableFrom(erTypeClass)) {
				// Facet and relation are created in calling method
			} else {
				ERManagement.updateProperties(element, jsonNode, ignoreKeys,
						ignoreStartWithKeys);
			}

			ContextUtility.addToActualContext(orientGraph, element);

			((OrientVertex) element).save();

			logger.info("Created {} is {}", Vertex.class.getSimpleName(),
					Utility.toJsonString((OrientVertex) element, true));

			return element;
		} catch (ResourceRegistryException e) {
			throw e;
		} catch (Exception e) {
			logger.trace("Error while creating {} for {} ({}) using {}",
					Vertex.class.getSimpleName(), baseType, erType, jsonNode, e);
			throw new ResourceRegistryException("Error Creating " + erType
					+ " with " + jsonNode, e.getCause());
		}
	}

	@Override
	public boolean reallyAddToContext() throws ContextException,
			ResourceRegistryException {

		ContextUtility.addToActualContext(orientGraph, getElement());

		Iterable<Edge> edges = element.getEdges(Direction.OUT);

		for (Edge edge : edges) {
			@SuppressWarnings("rawtypes")
			RelationManagement relationManagement = RelationManagement
					.getRelationManagement(orientGraph, edge);
			relationManagement.reallyAddToContext();
		}

		return true;
	}

	@Override
	public boolean reallyRemoveFromContext() throws ContextException,
			ResourceRegistryException {

		ContextUtility.removeFromActualContext(orientGraph, getElement());

		Iterable<Edge> edges = element.getEdges(Direction.OUT);

		for (Edge edge : edges) {
			@SuppressWarnings("rawtypes")
			RelationManagement relationManagement = RelationManagement
					.getRelationManagement(orientGraph, edge);
			relationManagement.reallyRemoveFromContext();
		}

		return true;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static EntityManagement getEntityManagement(OrientGraph orientGraph,
			Vertex vertex) throws ResourceRegistryException {
		OrientVertexType orientVertexType = ((OrientVertex) vertex).getType();
		EntityManagement entityManagement = null;
		if (orientVertexType.isSubClassOf(Resource.NAME)) {
			entityManagement = new ResourceManagement(orientGraph);
		} else if (orientVertexType.isSubClassOf(Facet.NAME)) {
			entityManagement = new FacetManagement(orientGraph);
		} else {
			String error = String.format("{%s is not a %s nor a %s. "
					+ "This is really strange and should not occur. "
					+ "Please Investigate it.", vertex, Resource.NAME,
					Facet.NAME);
			throw new ResourceRegistryException(error);
		}
		entityManagement.setElement(vertex);
		return entityManagement;
	}

	public abstract Vertex reallyCreate() throws EntityAlreadyPresentException,
			ResourceRegistryException;

	public String create() throws EntityAlreadyPresentException,
			ResourceRegistryException {

		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.WRITER);

			element = reallyCreate();

			orientGraph.commit();

			return serialize();

		} catch (ResourceRegistryException e) {
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw e;
		} catch (Exception e) {
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw new ResourceRegistryException(e);
		} finally {
			if (orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}

	@Override
	public String reallyGetAll(boolean polymorphic) throws ResourceRegistryException {
		JSONArray jsonArray = new JSONArray();
		Iterable<Vertex> iterable = orientGraph.getVerticesOfClass(erType, polymorphic);
		for(Vertex vertex : iterable){
			@SuppressWarnings("rawtypes")
			EntityManagement entityManagement = getEntityManagement(orientGraph, vertex);
			JSONObject jsonObject =  entityManagement.serializeAsJson();
			jsonArray.put(jsonObject);
		}
		return jsonArray.toString();
	}

}
