package org.gcube.informationsystem.resourceregistry.er.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.gcube.informationsystem.model.reference.AccessType;
import org.gcube.informationsystem.model.reference.embedded.Header;
import org.gcube.informationsystem.model.reference.entity.Entity;
import org.gcube.informationsystem.model.reference.entity.Facet;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.informationsystem.model.reference.relation.ConsistsOf;
import org.gcube.informationsystem.model.reference.relation.Relation;
import org.gcube.informationsystem.resourceregistry.api.exceptions.AvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.NotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.EntityAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.query.InvalidQueryException;
import org.gcube.informationsystem.resourceregistry.context.security.SecurityContext;
import org.gcube.informationsystem.resourceregistry.context.security.SecurityContext.PermissionMode;
import org.gcube.informationsystem.resourceregistry.er.ERManagement;
import org.gcube.informationsystem.resourceregistry.er.ERManagementUtility;
import org.gcube.informationsystem.resourceregistry.er.relation.RelationManagement;
import org.gcube.informationsystem.resourceregistry.utils.Utility;

import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientElement;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public abstract class EntityManagement<E extends Entity> extends ERManagement<E,Vertex> {
	
	/**
	 * Provide a cache edge-internal-id -> RelationManagement 
	 * this avoid to recreate the relationManagement of already visited edges
	 */
	@SuppressWarnings("rawtypes")
	protected Map<String,RelationManagement> relationManagements;
	
	protected EntityManagement(AccessType accessType) {
		super(accessType);
		
		this.ignoreKeys.add(Entity.HEADER_PROPERTY);
		
		this.ignoreStartWithKeys.add(OrientVertex.CONNECTION_IN_PREFIX.toLowerCase());
		this.ignoreStartWithKeys.add(OrientVertex.CONNECTION_OUT_PREFIX.toLowerCase());
		this.ignoreStartWithKeys.add(OrientVertex.CONNECTION_IN_PREFIX.toUpperCase());
		this.ignoreStartWithKeys.add(OrientVertex.CONNECTION_OUT_PREFIX.toUpperCase());
		
		this.relationManagements = new HashMap<>();
		
	}
	
	protected EntityManagement(AccessType accessType, SecurityContext workingContext, OrientGraph orientGraph) {
		this(accessType);
		this.orientGraph = orientGraph;
		setWorkingContext(workingContext);
	}
	
	@SuppressWarnings("rawtypes")
	/*
	 * It works perfectly in case of any kind of update. In case of use from create
	 * the cache does not work by using the ID because until commit the edge has a
	 * fake id starting with - (minus) sign. This not imply any collateral effect
	 * but a better solution is a desiderata.
	 */
	protected RelationManagement getRelationManagement(Edge edge) throws ResourceRegistryException {
		String id = edge.getId().toString();
		RelationManagement relationManagement = relationManagements.get(id);
		if(relationManagement == null) {
			relationManagement = ERManagementUtility.getRelationManagement(getWorkingContext(), orientGraph, edge);
			relationManagements.put(id, relationManagement);
		}
		return relationManagement;
	}
	
	protected void addToRelationManagement(@SuppressWarnings("rawtypes") RelationManagement relationManagement)
			throws ResourceRegistryException {
		Element elem = relationManagement.getElement();
		String id = elem.getId().toString();
		if(relationManagements.get(id) != null && relationManagements.get(id) != relationManagement) {
			StringBuilder errorMessage = new StringBuilder();
			errorMessage.append("Two different instance of ");
			errorMessage.append(relationManagement.getClass().getSimpleName());
			errorMessage.append(" point to the same ");
			errorMessage.append(elem.getClass().getSimpleName());
			errorMessage.append(". ");
			errorMessage.append(Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
			throw new ResourceRegistryException(errorMessage.toString());
		}
		relationManagements.put(id, relationManagement);
	}
	
	protected static JSONObject addRelation(JSONObject sourceResource, JSONObject relation, String arrayKey)
			throws ResourceRegistryException {
		JSONArray relationArray = null;
		try {
			if(sourceResource.has(arrayKey)) {
				relationArray = sourceResource.getJSONArray(arrayKey);
			} else {
				relationArray = new JSONArray();
			}
			
			relationArray.put(relation);
			sourceResource.putOpt(arrayKey, relationArray);
		} catch(Exception e) {
			throw new ResourceRegistryException(e);
		}
		return sourceResource;
	}
	
	protected Vertex createVertex() throws EntityAlreadyPresentException, ResourceRegistryException {
		
		logger.trace("Going to create {} for {} ({}) using {}", Vertex.class.getSimpleName(), accessType.getName(),
				elementType, jsonNode);
		
		try {
			
			if(oClass.isAbstract()) {
				String error = String.format(
						"Trying to create an instance of %s of type %s which is abstract. The operation will be aborted.",
						accessType.getName(), elementType);
				throw new ResourceRegistryException(error);
			}
			
			Vertex vertexEntity = orientGraph.addVertex("class:" + elementType);
			
			try {
				if(uuid != null) {
					Vertex v = getElement();
					if(v != null) {
						String error = String.format("A %s with UUID %s already exist", elementType, uuid.toString());
						throw getSpecificERAlreadyPresentException(error);
					}
				}
				
			} catch(NotFoundException e) {
				try {
					Element el = ERManagementUtility.getAnyElementByUUID(uuid);
					String error = String.format("UUID %s is already used by another %s. This is not allowed.",
							uuid.toString(), (el instanceof Vertex) ? Entity.NAME : Relation.NAME);
					throw getSpecificERAvailableInAnotherContextException(error);
					
				} catch(NotFoundException e1) {
					// OK the UUID is not already used.
				}
			} catch(AvailableInAnotherContextException e) {
				throw e;
			}
			
			this.element = vertexEntity;
			
			if(accessType == AccessType.RESOURCE) {
				// Facet and relation are created in calling method
			} else {
				ERManagement.updateProperties(oClass, element, jsonNode, ignoreKeys, ignoreStartWithKeys);
			}
			
			logger.info("Created {} is {}", Vertex.class.getSimpleName(),
					Utility.toJsonString((OrientVertex) element, true));
			
			return element;
		} catch(ResourceRegistryException e) {
			throw e;
		} catch(Exception e) {
			logger.trace("Error while creating {} for {} ({}) using {}", Vertex.class.getSimpleName(),
					accessType.getName(), elementType, jsonNode, e);
			throw new ResourceRegistryException("Error Creating " + elementType + " with " + jsonNode, e.getCause());
		}
	}
	
	@Override
	protected boolean reallyAddToContext(SecurityContext targetSecurityContext)
			throws ContextException, ResourceRegistryException {
		
		targetSecurityContext.addElement(getElement(), orientGraph);
		
		Iterable<Edge> edges = getElement().getEdges(Direction.OUT);
		
		for(Edge edge : edges) {
			@SuppressWarnings("rawtypes")
			RelationManagement relationManagement = getRelationManagement(edge);
			relationManagement.internalAddToContext(targetSecurityContext);
		}
		
		return true;
	}
	
	@Override
	protected boolean reallyRemoveFromContext(SecurityContext targetSecurityContext)
			throws ContextException, ResourceRegistryException {
		
		Iterable<Edge> edges = getElement().getEdges(Direction.OUT);
		
		for(Edge edge : edges) {
			@SuppressWarnings("rawtypes")
			RelationManagement relationManagement = getRelationManagement(edge);
			relationManagement.internalRemoveFromContext(targetSecurityContext);
		}
		
		targetSecurityContext.removeElement(getElement(), orientGraph);
		
		return true;
	}
	
	@Override
	public String reallyGetAll(boolean polymorphic) throws ResourceRegistryException {
		JSONArray jsonArray = new JSONArray();
		Iterable<Vertex> iterable = orientGraph.getVerticesOfClass(elementType, polymorphic);
		for(Vertex vertex : iterable) {
			@SuppressWarnings("rawtypes")
			EntityManagement entityManagement = ERManagementUtility.getEntityManagement(getWorkingContext(),
					orientGraph, vertex);
			try {
				JSONObject jsonObject = entityManagement.serializeAsJson();
				jsonArray.put(jsonObject);
			} catch(ResourceRegistryException e) {
				logger.error("Unable to correctly serialize {}. It will be excluded from results. {}",
						vertex.toString(), Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
			}
		}
		return jsonArray.toString();
	}
	
	public String reallyQuery(String relationType, String referenceType, UUID referenceUUID, Direction direction,
			boolean polymorphic, Map<String,String> constraint) throws ResourceRegistryException {
		JSONArray jsonArray = new JSONArray();
		
		Iterable<Vertex> references = null;
		
		if(referenceUUID != null) {
			Element element = ERManagementUtility.getAnyElementByUUID(referenceUUID);
			if(element instanceof Vertex) {
				@SuppressWarnings("unchecked")
				EntityManagement<Entity> entityManagement = ERManagementUtility.getEntityManagement(getWorkingContext(),
						orientGraph, (Vertex) element);
				
				OrientVertexType orientVertexType = ((OrientVertex) element).getType();
				
				String elementType = entityManagement.getElementType();
				if(elementType.compareTo(referenceType) != 0) {
					if(polymorphic && orientVertexType.isSubClassOf(referenceType)) {
						// OK
					} else {
						String error = String.format("Referenced instace with UUID %s is not a %s", referenceUUID,
								referenceType);
						throw new InvalidQueryException(error);
					}
				}
				
				List<Vertex> vertexes = new ArrayList<>();
				vertexes.add((Vertex) element);
				references = vertexes;
				
			} else {
				String error = String.format("Referenced instace with UUID %s is not an %s", referenceUUID, Entity.NAME);
				throw new InvalidQueryException(error);
			}
			
		} else {
			references = orientGraph.getVerticesOfClass(referenceType, polymorphic);
		}
		
		for(Vertex v : references) {
			List<Direction> directions = new ArrayList<>();
			if(direction==Direction.BOTH) {
				directions.add(Direction.IN);
				directions.add(Direction.OUT);
			}else {
				directions.add(direction);
			}
			
			for(Direction d : directions) {
			
				Iterable<Edge> edges = v.getEdges(d.opposite(), relationType);
				for(Edge edge : edges) {
					Vertex vertex = ((OrientEdge) edge).getVertex(d);
					OrientVertex orientVertex = (OrientVertex) vertex;
					
					if(((OrientVertex) v).getIdentity().compareTo(orientVertex.getIdentity()) == 0) {
						continue;
					}
					
					if(elementType.compareTo(orientVertex.getLabel()) != 0) {
						OrientVertexType orientVertexType = orientVertex.getType();
						
						if(polymorphic && orientVertexType.isSubClassOf(elementType)) {
							// OK
						} else {
							// excluding from results
							continue;
						}
					}
					
					@SuppressWarnings("rawtypes")
					EntityManagement entityManagement = ERManagementUtility.getEntityManagement(getWorkingContext(),
							orientGraph, vertex);
					try {
						if(entityManagement.getUUID().compareTo(referenceUUID) == 0) {
							continue;
						}
						JSONObject jsonObject = entityManagement.serializeAsJson();
						jsonArray.put(jsonObject);
					} catch(ResourceRegistryException e) {
						logger.error("Unable to correctly serialize {}. It will be excluded from results. {}",
								vertex.toString(), Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
					}
				}
			}
		}
		return jsonArray.toString();
	}
	
	public String reallyQueryTraversal(String relationType, String referenceType, UUID referenceUUID,
			Direction direction, boolean polymorphic, Map<String,String> constraint) throws ResourceRegistryException {
		JSONArray jsonArray = new JSONArray();
		
		if(referenceUUID != null) {
			constraint.put(Entity.HEADER_PROPERTY + "." + Header.UUID_PROPERTY, referenceUUID.toString());
		}
		
		// TODO check types
		
		/*
		 * SELECT FROM (TRAVERSE inE('isIdentifiedBy'), outV('EService') FROM (SELECT
		 * FROM SoftwareFacet WHERE group='VREManagement' AND name='SmartExecutor'))
		 * 
		 * WHERE @class='EService' // Only is not polymorphic
		 */
		
		StringBuilder selectStringBuilder = new StringBuilder("SELECT FROM (TRAVERSE ");
		selectStringBuilder.append(direction.name().toLowerCase());
		selectStringBuilder.append("E('");
		selectStringBuilder.append(relationType);
		selectStringBuilder.append("'), ");
		selectStringBuilder.append(direction.opposite().name().toLowerCase());
		selectStringBuilder.append("V('");
		selectStringBuilder.append(elementType);
		selectStringBuilder.append("') FROM (SELECT FROM ");
		selectStringBuilder.append(referenceType);
		boolean first = true;
		for(String key : constraint.keySet()) {
			if(first) {
				selectStringBuilder.append(" WHERE ");
				first = false;
			} else {
				selectStringBuilder.append(" AND ");
			}
			selectStringBuilder.append(key);
			selectStringBuilder.append("=");
			String value = constraint.get(key).trim();
			selectStringBuilder.append("'");
			selectStringBuilder.append(value);
			selectStringBuilder.append("'");
		}
		selectStringBuilder.append(" ))");
		
		if(!polymorphic) {
			selectStringBuilder.append(" WHERE @class='");
			selectStringBuilder.append(elementType);
			selectStringBuilder.append("'");
		}
		
		String select = selectStringBuilder.toString();
		logger.trace(select);
		
		OSQLSynchQuery<Element> osqlSynchQuery = new OSQLSynchQuery<Element>(select);
		Iterable<Element> elements = orientGraph.command(osqlSynchQuery).execute();
		
		for(Element element : elements) {
			
			if(polymorphic) {
				OrientVertexType orientVertexType = null;
				try {
					OrientElement orientElement = ((OrientElement) element);
					if(orientElement instanceof OrientEdge) {
						continue;
					}
					orientVertexType = ((OrientVertex) orientElement).getType();
				} catch(Exception e) {
					String error = String.format("Unable to detect type of %s. %s", element.toString(),
							Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
					logger.error(error, e);
					throw new ResourceRegistryException(error);
				}
				
				if(orientVertexType.getName().compareTo(elementType) != 0) {
					if(!orientVertexType.isSubClassOf(elementType)) {
						continue;
					}
				}
				
			}
			
			Vertex vertex = (Vertex) element;
			
			@SuppressWarnings("rawtypes")
			EntityManagement entityManagement = ERManagementUtility.getEntityManagement(getWorkingContext(),
					orientGraph, vertex);
			try {
				if(constraint.containsKey(Entity.HEADER_PROPERTY + "." + Header.UUID_PROPERTY)) {
					String uuid = constraint.get(Entity.HEADER_PROPERTY + "." + Header.UUID_PROPERTY);
					if(entityManagement.getUUID().compareTo(UUID.fromString(uuid)) == 0) {
						continue;
					}
				}
				JSONObject jsonObject = entityManagement.serializeAsJson();
				jsonArray.put(jsonObject);
			} catch(ResourceRegistryException e) {
				logger.error("Unable to correctly serialize {}. It will be excluded from results. {}",
						vertex.toString(), Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
			}
		}
		
		return jsonArray.toString();
	}
	
	public String query(String relationType, String referenceType, UUID referenceUUID, Direction direction,
			boolean polymorphic, Map<String,String> constraint) throws ResourceRegistryException {
		try {
			orientGraph = getWorkingContext().getGraph(PermissionMode.READER);
			
			AccessType relationAccessType = ERManagementUtility.getBaseAccessType(relationType);
			if(relationAccessType != AccessType.IS_RELATED_TO && relationAccessType != AccessType.CONSISTS_OF) {
				String error = String.format("%s must be a relation type", relationType);
				throw new ResourceRegistryException(error);
			}
			
			AccessType referenceAccessType = ERManagementUtility.getBaseAccessType(referenceType);
			if(referenceAccessType != AccessType.RESOURCE && referenceAccessType != AccessType.FACET) {
				String error = String.format("%s must be a en entity type", referenceType);
				throw new ResourceRegistryException(error);
			}
			
			if(constraint == null) {
				constraint = new HashMap<>();
			}
			
			switch(accessType) {
				case RESOURCE:
					
					if(relationAccessType == AccessType.CONSISTS_OF) {
						
						if(direction != Direction.OUT) {
							String error = String.format("%s can only goes %s from %s.", relationType,
									Direction.OUT.name(), elementType);
							throw new InvalidQueryException(error);
						} else {
							if(referenceAccessType != AccessType.FACET) {
								String error = String.format("%s can only has as target a %s. Provided instead %s : %s",
										relationType, Facet.NAME, referenceAccessType, referenceType);
								throw new InvalidQueryException(error);
							}
						}
					}
					
					break;
				
				case FACET:
					if(relationAccessType != AccessType.CONSISTS_OF || direction != Direction.IN
							|| referenceAccessType != AccessType.RESOURCE) {
						String error = String.format("%s can only has %s %s from a %s.", elementType,
								Direction.IN.name(), ConsistsOf.NAME, Resource.NAME);
						throw new InvalidQueryException(error);
					}
					
					break;
				
				default:
					break;
			}
			
			return reallyQuery(relationType, referenceType, referenceUUID, direction, polymorphic, constraint);
			
		} catch(ResourceRegistryException e) {
			throw e;
		} catch(Exception e) {
			throw new ResourceRegistryException(e);
		} finally {
			if(orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}
	
}
