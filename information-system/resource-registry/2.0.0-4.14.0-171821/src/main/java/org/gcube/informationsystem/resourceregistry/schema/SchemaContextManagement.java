package org.gcube.informationsystem.resourceregistry.schema;

import java.util.Iterator;

import org.gcube.informationsystem.model.reference.AccessType;
import org.gcube.informationsystem.model.reference.embedded.Embedded;
import org.gcube.informationsystem.model.reference.entity.Entity;
import org.gcube.informationsystem.model.reference.relation.Relation;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.gcube.informationsystem.resourceregistry.context.ContextUtility;
import org.gcube.informationsystem.resourceregistry.context.security.AdminSecurityContext;
import org.gcube.informationsystem.resourceregistry.context.security.SecurityContext.PermissionMode;
import org.gcube.informationsystem.types.TypeBinder.TypeDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class SchemaContextManagement implements SchemaManagement {
	
	private static Logger logger = LoggerFactory.getLogger(SchemaContextManagement.class);
	
	public static final String SCHEMA = "__SCHEMA";
	
	protected Vertex getVertex(OrientGraph orientGraph, String vertexType) throws Exception {
		Iterable<Vertex> iterable = orientGraph.getVerticesOfClass(vertexType, false);
		Iterator<Vertex> iterator = iterable.iterator();
		
		Vertex vertex = null;
		if(iterator.hasNext()) {
			vertex = iterator.next();
		} else {
			String error = String.format("%s is not a registered type", vertexType);
			logger.trace(error);
			throw new Exception(error);
		}
		
		if(iterator.hasNext()) {
			String error = String.format(
					"More than one instance of %s found in Management Context. This MUST not happen. Please contact system administrator.",
					vertexType);
			logger.error(error);
			throw new Exception(error);
		}
		
		return vertex;
	}
	
	@Override
	public String create(String json, AccessType baseType) throws SchemaException {
		
		OrientGraph orientGraph = null;
		
		try {
			AdminSecurityContext adminSecurityContext = ContextUtility.getAdminSecurityContext();
			orientGraph = adminSecurityContext.getGraph(PermissionMode.WRITER);
			
			ObjectMapper mapper = new ObjectMapper();
			TypeDefinition typeDefinition = mapper.readValue(json, TypeDefinition.class);
			
			if(Entity.class.isAssignableFrom(baseType.getTypeClass())) {
				OrientVertex orientVertex = orientGraph.addVertex("class:" + typeDefinition.getName());
				orientVertex.setProperty(SCHEMA, json);
				orientVertex.save();
			} else if(Relation.class.isAssignableFrom(baseType.getTypeClass())) {
				String sourceClass = typeDefinition.getSourceType();
				Vertex source = getVertex(orientGraph, sourceClass);
				
				String targetClass = typeDefinition.getTargetType();
				Vertex target = getVertex(orientGraph, targetClass);
				
				OrientEdge orientEdge = orientGraph.addEdge(null, source, target, typeDefinition.getName());
				orientEdge.setProperty(SCHEMA, json);
				orientEdge.save();
				
			} else if(Embedded.class.isAssignableFrom(baseType.getTypeClass())) {
				ODocument doc = new ODocument(typeDefinition.getName());
				doc.field(SCHEMA, json);
				doc.save();
			}
			
			orientGraph.commit();
			return json;
			
		} catch(Exception e) {
			if(orientGraph != null) {
				orientGraph.rollback();
			}
			throw new SchemaException(e);
		} finally {
			if(orientGraph != null) {
				orientGraph.shutdown();
			}
		}
		
	}
	
	@Override
	public String read(String type, boolean includeSubtypes) throws SchemaNotFoundException, SchemaException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String update(String type, AccessType accessType, String json)
			throws SchemaNotFoundException, SchemaException {
		throw new UnsupportedOperationException("Not Yet implemented");
	}
	
	@Override
	public String delete(String type, AccessType accessType) throws SchemaNotFoundException {
		throw new UnsupportedOperationException("Not Yet implemented");
	}
	
}
