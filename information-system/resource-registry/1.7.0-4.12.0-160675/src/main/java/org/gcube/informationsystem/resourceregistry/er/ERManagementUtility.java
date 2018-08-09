package org.gcube.informationsystem.resourceregistry.er;

import java.util.UUID;

import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsRelatedTo;
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERNotFoundException;
import org.gcube.informationsystem.resourceregistry.context.security.SecurityContext;
import org.gcube.informationsystem.resourceregistry.er.entity.EntityManagement;
import org.gcube.informationsystem.resourceregistry.er.entity.FacetManagement;
import org.gcube.informationsystem.resourceregistry.er.entity.ResourceManagement;
import org.gcube.informationsystem.resourceregistry.er.relation.ConsistsOfManagement;
import org.gcube.informationsystem.resourceregistry.er.relation.IsRelatedToManagement;
import org.gcube.informationsystem.resourceregistry.er.relation.RelationManagement;
import org.gcube.informationsystem.resourceregistry.schema.SchemaManagementImpl;
import org.gcube.informationsystem.resourceregistry.utils.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ERManagementUtility {
	
	private static Logger logger = LoggerFactory.getLogger(EntityManagement.class);
	
	@SuppressWarnings("rawtypes")
	public static ERManagement getERManagement(String type) throws ResourceRegistryException {
		
		OClass oClass = SchemaManagementImpl.getTypeSchema(type, null);
		ERManagement erManagement = null;
		
		if(oClass.isSubClassOf(Resource.NAME)) {
			erManagement = new ResourceManagement();
		} else if(oClass.isSubClassOf(Facet.NAME)) {
			erManagement = new FacetManagement();
		} else if(oClass.isSubClassOf(ConsistsOf.NAME)) {
			erManagement = new ConsistsOfManagement();
		} else if(oClass.isSubClassOf(IsRelatedTo.NAME)) {
			erManagement = new IsRelatedToManagement();
		}
		
		if(erManagement == null) {
			throw new ResourceRegistryException(String.format("%s is not querable", type.toString()));
		}
		
		erManagement.setElementType(type);
		return erManagement;
	}
	
	@SuppressWarnings("rawtypes")
	private static ERManagement getERManagement(SecurityContext workingContext, OrientGraph orientGraph,
			Element element) throws ResourceRegistryException {
		if(element instanceof Vertex) {
			return getEntityManagement(workingContext, orientGraph, (Vertex) element);
		} else if(element instanceof Edge) {
			return getRelationManagement(workingContext, orientGraph, (Edge) element);
		}
		throw new ResourceRegistryException(String.format("%s is not a %s nor a %s", element.getClass().getSimpleName(),
				Entity.NAME, Relation.NAME));
	}
	
	public static Element getAnyElementByUUID(UUID uuid) throws ERNotFoundException, ResourceRegistryException {
		try {
			return Utility.getElementByUUIDAsAdmin(null, uuid, Vertex.class);
		} catch(ERNotFoundException e) {
			return Utility.getElementByUUIDAsAdmin(null, uuid, Edge.class);
		} catch(ResourceRegistryException e) {
			throw e;
		} catch(Exception e) {
			throw new ResourceRegistryException(e);
		}
	}
	
	private static Element getAnyElementByUUID(OrientGraph orientGraph, UUID uuid)
			throws ERNotFoundException, ResourceRegistryException {
		try {
			return Utility.getElementByUUID(orientGraph, null, uuid, Vertex.class);
		} catch(ERNotFoundException e) {
			return Utility.getElementByUUID(orientGraph, null, uuid, Edge.class);
		} catch(ResourceRegistryException e) {
			throw e;
		} catch(Exception e) {
			throw new ResourceRegistryException(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static ERManagement getERManagementFromUUID(SecurityContext workingContext, OrientGraph orientGraph,
			UUID uuid) throws ResourceRegistryException {
		Element element;
		try {
			element = getAnyElementByUUID(orientGraph, uuid);
			return getERManagement(workingContext, orientGraph, element);
		} catch(Exception e) {
			throw new ResourceRegistryException(String.format("%s does not belong to an %s nor to a %s",
					uuid.toString(), Entity.NAME, Relation.NAME));
		}
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static EntityManagement getEntityManagement(SecurityContext workingContext, OrientGraph orientGraph,
			Vertex vertex) throws ResourceRegistryException {
		
		if(orientGraph == null) {
			throw new ResourceRegistryException(
					OrientGraph.class.getSimpleName() + "instance is null. " + Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
		}
		
		if(vertex == null) {
			throw new ResourceRegistryException(
					Vertex.class.getSimpleName() + "instance is null. " + Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
		}
		
		OrientVertexType orientVertexType = null;
		try {
			orientVertexType = ((OrientVertex) vertex).getType();
		} catch(Exception e) {
			String error = String.format("Unable to detect type of %s. %s", vertex.toString(),
					Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
			logger.error(error, e);
			throw new ResourceRegistryException(error);
		}
		
		EntityManagement entityManagement = null;
		if(orientVertexType.isSubClassOf(Resource.NAME)) {
			entityManagement = new ResourceManagement(workingContext, orientGraph);
		} else if(orientVertexType.isSubClassOf(Facet.NAME)) {
			entityManagement = new FacetManagement(workingContext, orientGraph);
		} else {
			String error = String.format("{%s is not a %s nor a %s. %s", vertex, Resource.NAME, Facet.NAME,
					Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
			throw new ResourceRegistryException(error);
		}
		entityManagement.setElement(vertex);
		return entityManagement;
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static RelationManagement getRelationManagement(SecurityContext workingContext, OrientGraph orientGraph,
			Edge edge) throws ResourceRegistryException {
		
		if(orientGraph == null) {
			throw new ResourceRegistryException(
					OrientGraph.class.getSimpleName() + "instance is null. " + Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
		}
		
		if(edge == null) {
			throw new ResourceRegistryException(
					Edge.class.getSimpleName() + "instance is null. " + Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
		}
		
		OrientEdgeType orientEdgeType = ((OrientEdge) edge).getType();
		RelationManagement relationManagement = null;
		if(orientEdgeType.isSubClassOf(ConsistsOf.NAME)) {
			relationManagement = new ConsistsOfManagement(workingContext, orientGraph);
		} else if(orientEdgeType.isSubClassOf(IsRelatedTo.NAME)) {
			relationManagement = new IsRelatedToManagement(workingContext, orientGraph);
		} else {
			String error = String.format("{%s is not a %s nor a %s. %s", edge, ConsistsOf.NAME, IsRelatedTo.NAME,
					Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
			throw new ResourceRegistryException(error);
		}
		relationManagement.setElement(edge);
		return relationManagement;
	}
	
}
