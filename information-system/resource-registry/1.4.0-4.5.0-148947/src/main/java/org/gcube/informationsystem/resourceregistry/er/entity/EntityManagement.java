/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.er.entity;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.gcube.informationsystem.model.AccessType;
import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.EntityAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERNotFoundException;
import org.gcube.informationsystem.resourceregistry.context.ContextUtility;
import org.gcube.informationsystem.resourceregistry.er.ERManagement;
import org.gcube.informationsystem.resourceregistry.er.relation.RelationManagement;
import org.gcube.informationsystem.resourceregistry.utils.HeaderUtility;
import org.gcube.informationsystem.resourceregistry.utils.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
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

	protected EntityManagement(AccessType accessType) {
		super(accessType);

		this.ignoreKeys.add(Entity.HEADER_PROPERTY);

		this.ignoreStartWithKeys.add(OrientVertex.CONNECTION_IN_PREFIX
				.toLowerCase());
		this.ignoreStartWithKeys.add(OrientVertex.CONNECTION_OUT_PREFIX
				.toLowerCase());
		this.ignoreStartWithKeys.add(OrientVertex.CONNECTION_IN_PREFIX
				.toUpperCase());
		this.ignoreStartWithKeys.add(OrientVertex.CONNECTION_OUT_PREFIX
				.toUpperCase());

	}

	protected EntityManagement(AccessType accessType, OrientGraph orientGraph) {
		this(accessType);
		this.orientGraph = orientGraph;
	}
	
	@Override
	public abstract Vertex reallyCreate() throws EntityAlreadyPresentException, 
		ResourceRegistryException;

	protected Vertex createVertex() throws EntityAlreadyPresentException,
			ResourceRegistryException {

		logger.trace("Going to create {} for {} ({}) using {}",
				Vertex.class.getSimpleName(), accessType.getName(), erType, jsonNode);

		try {

			if(oClass.isAbstract()){
				String error = String.format("Trying to create an instance of %s of type %s which is abstract. The operation will be aborted.", 
						accessType.getName(), erType);
				throw new ResourceRegistryException(error);
			}
			
			Vertex vertexEntity = orientGraph.addVertex("class:" + erType);

			try {
				if(uuid!=null){
					Vertex v = getElement();
					if (v != null) {
						String error = String.format(
								"A %s with UUID %s already exist", erType,
								uuid.toString());
						throw getSpecificERAlreadyPresentException(error);
					}
				}
				
			} catch (ERNotFoundException e) {
				try {
					Element el = getAnyElementByUUID(uuid);
					String error = String.format(
							"UUID %s is already used by another %s. This is not allowed.",
							uuid.toString(), (el instanceof Vertex) ? Entity.NAME : Relation.NAME);
					throw getSpecificERAvailableInAnotherContextException(error);
					
				}catch (ERNotFoundException e1) {
					// OK the UUID is not already used.
				}
			} catch (ERAvailableInAnotherContextException e) {
				throw e;
			} 
			
			this.element = vertexEntity;

			Header entityHeader = HeaderUtility.getHeader(jsonNode, true);
			if (entityHeader != null) {
				element.setProperty(Entity.HEADER_PROPERTY, entityHeader);
			} else {
				entityHeader = HeaderUtility.addHeader(element, null);
			}

			
			if (accessType.compareTo(AccessType.RESOURCE)==0) {
				// Facet and relation are created in calling method
			} else {
				ERManagement.updateProperties(oClass, element, jsonNode, ignoreKeys,
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
					Vertex.class.getSimpleName(), accessType.getName(), erType, jsonNode, e);
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
		
		if(orientGraph==null){
			throw new ResourceRegistryException(OrientGraph.class.getSimpleName() + "instance is null. This is really strage please contact the administrator.");
		}
		
		if(vertex==null){
			throw new ResourceRegistryException(Vertex.class.getSimpleName() + "instance is null. This is really strage please contact the administrator.");
		}
		
		OrientVertexType orientVertexType = null;
		try {
			orientVertexType = ((OrientVertex) vertex).getType();
		}catch (Exception e) {
			String error = String.format("Unable to detect type of %s. This is really strage please contact the administrator.", vertex.toString()); 
			logger.error(error, e);
			throw new ResourceRegistryException(error);
		}
		
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

	@Override
	public String reallyGetAll(boolean polymorphic) throws ResourceRegistryException {
		JSONArray jsonArray = new JSONArray();
		Iterable<Vertex> iterable = orientGraph.getVerticesOfClass(erType, polymorphic);
		for(Vertex vertex : iterable){
			@SuppressWarnings("rawtypes")
			EntityManagement entityManagement = getEntityManagement(orientGraph, vertex);
			try {
				JSONObject jsonObject =  entityManagement.serializeAsJson();
				jsonArray.put(jsonObject);
			}catch (ResourceRegistryException e) {
				logger.error("Unable to correctly serialize {}. It will be excluded from results. This is really strange and should not occur.", vertex.toString());
			}
		}
		return jsonArray.toString();
	}

}
