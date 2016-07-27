/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.resources.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.ConsistOf;
import org.gcube.informationsystem.model.relation.RelatedTo;
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.resourceregistry.api.SchemaManagement;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.gcube.informationsystem.resourceregistry.dbinitialization.SecurityContextMapper;
import org.gcube.informationsystem.resourceregistry.dbinitialization.SecurityContextMapper.PermissionMode;
import org.gcube.informationsystem.types.TypeBinder.Property;
import org.gcube.informationsystem.types.TypeBinder.TypeDefinition;
import org.jboss.weld.exceptions.UnsupportedOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.OMetadata;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OClassImpl;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType.OrientVertexProperty;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 *         TODO Create an instance for each Registered Type in a management
 *         SecurityContext so that that management context can be used to see
 *         Entity and Relations as graph.
 */
public class SchemaManagementImpl implements SchemaManagement {

	private static Logger logger = LoggerFactory
			.getLogger(SchemaManagementImpl.class);

	protected static OClass getEntityOClass(OrientGraphNoTx orientGraphNoTx, String entityType) throws SchemaException {
		OMetadata oMetadata = orientGraphNoTx.getRawGraph().getMetadata();
		OSchema oSchema = oMetadata.getSchema();
		return oSchema.getClass(entityType);
		
	}
	
	protected OClass getTypeSchema(String type, String baseType)
			throws SchemaNotFoundException {
		OrientGraphFactory orientGraphFactory = SecurityContextMapper
				.getSecurityContextFactory(null, PermissionMode.READER);

		OrientGraphNoTx orientGraphNoTx = null;
		try {
			logger.debug("Getting {} Type {} schema",
					baseType != null ? baseType : "", type);

			orientGraphNoTx = orientGraphFactory.getNoTx();

			OClass oClass = getEntityOClass(orientGraphNoTx, type);

			if (baseType != null) {
				if (!oClass.isSubClassOf(baseType)) {
					throw new SchemaException("The requested type is not a "
							+ baseType);
				}
			}
			return oClass;

		} catch (SchemaNotFoundException snfe) {
			throw snfe;
		} catch (Exception e) {
			throw new SchemaNotFoundException(e.getMessage());
		}
	}

	protected static String serializeOClass(OClass oClass) {
		ODocument oDocument = ((OClassImpl) oClass).toStream();
		String json = oDocument.toJSON();
		logger.trace("Requested type serialization is {}", json);
		return json;
	}

	protected List<OClass> getSuperclassesAndCheckCompliancy(
			OrientGraphNoTx orientGraphNoTx, TypeDefinition typeDefinition,
			String baseType) throws SchemaException {

		Set<String> superClasses = typeDefinition.getSuperclasses();
		if (baseType !=null){
			if(superClasses == null || superClasses.size() == 0){
				throw new RuntimeException(
						String.format(
								"No Superclass found in schema %s. The Type Definition must extend %s",
								typeDefinition, baseType));
			}
		}

		List<OClass> oSuperclasses = new ArrayList<>();
		for (String superClass : superClasses) {
			OClass oSuperClass = getEntityOClass(orientGraphNoTx, superClass);
			if (baseType != null) {
				if (typeDefinition.getName().compareTo(baseType) != 0) {
					if (!oSuperClass.isSubClassOf(baseType)) {
						throw new RuntimeException(superClass
								+ " is not a subsclass of " + baseType
								+ ". Each Superclass MUST be a subclass of "
								+ baseType);
					}
				}
			}
			oSuperclasses.add(oSuperClass);
		}

		return oSuperclasses;
	}

	protected String getTypeSchemaAsString(String type, String baseType)
			throws SchemaNotFoundException {
		OrientGraphFactory orientGraphFactory = SecurityContextMapper
				.getSecurityContextFactory(null, PermissionMode.READER);

		OrientGraphNoTx orientGraphNoTx = null;
		try {
			orientGraphNoTx = orientGraphFactory.getNoTx();
			OClass oClass = getTypeSchema(type, baseType);
			return serializeOClass(oClass);
		} catch (Exception e) {
			throw new SchemaNotFoundException(e);
		} finally {
			if (orientGraphNoTx != null) {
				orientGraphNoTx.shutdown();
			}
		}
	}

	protected String registerVertexTypeSchema(String jsonSchema,
			Class<?> baseType) throws SchemaException {

		OrientGraphFactory orientGraphFactory = SecurityContextMapper
				.getSecurityContextFactory(null, PermissionMode.WRITER);

		OrientGraphNoTx orientGraphNoTx = null;
		try {
			logger.trace("Trying to register {} {}", baseType, jsonSchema);

			ObjectMapper mapper = new ObjectMapper();
			TypeDefinition typeDefinition = mapper.readValue(jsonSchema,
					TypeDefinition.class);

			orientGraphNoTx = orientGraphFactory.getNoTx();

			OrientVertexType ovt = orientGraphNoTx
					.createVertexType(typeDefinition.getName());
			ovt.setDescription(typeDefinition.getDescription());
			ovt.setAbstract(typeDefinition.isAbstractType());

			List<OClass> oSuperclasses = getSuperclassesAndCheckCompliancy(
					orientGraphNoTx, typeDefinition, baseType.getSimpleName());

			ovt.setSuperClasses(oSuperclasses);

			if (Resource.class.isAssignableFrom(baseType)) {
				Set<Property> properties = typeDefinition.getProperties();
				if (properties != null && properties.size() > 0) {
					throw new Exception(
							"A Resource cannot contains any properties.");
				}
			} else {
				for (Property property : typeDefinition.getProperties()) {
					OrientVertexProperty ovp = ovt.createProperty(
							property.getName(),
							OType.getById(property.getType().byteValue()));
					ovp.setDescription(property.getDescription());
					ovp.setMandatory(property.isMandatory());
					ovp.setNotNull(property.isNotnull());
					ovp.setReadonly(property.isReadonly());
					ovp.setRegexp(property.getRegexpr());
					if (property.getLinkedClass() != null) {
						OClass linkedClass = getEntityOClass(orientGraphNoTx, property.getLinkedClass());
						if (linkedClass == null) {
							logger.trace("class {} not found in schema",
									property.getLinkedClass());
							throw new Exception("class "
									+ property.getLinkedClass()
									+ " not found in schema");
						}

						if (linkedClass.isEdgeType()
								|| linkedClass.isVertexType()) {
							throw new Exception(
									"An Embedded Field cannot be an Entity or a Relation");
						}

						ovp.setLinkedClass(linkedClass);
					} else if (property.getLinkedType() != null) {
						ovp.setLinkedType(OType.getById(property
								.getLinkedType().byteValue()));
					}
				}
			}

			orientGraphNoTx.commit();
			
			OClass oClass = getTypeSchema(typeDefinition.getName(), null);
			return serializeOClass(oClass);
			
		} catch (Exception e) {
			throw new SchemaException(e);
		} finally {
			if (orientGraphNoTx != null) {
				orientGraphNoTx.shutdown();
			}
		}
	}

	protected String registerEdgeTypeSchema(String jsonSchema, String baseType)
			throws SchemaException {
		OrientGraphFactory orientGraphFactory = SecurityContextMapper
				.getSecurityContextFactory(null, PermissionMode.WRITER);

		OrientGraphNoTx orientGraphNoTx = null;
		try {
			logger.trace("Trying to register {} {}", baseType, jsonSchema);

			ObjectMapper mapper = new ObjectMapper();
			TypeDefinition typeDefinition = mapper.readValue(jsonSchema,
					TypeDefinition.class);

			orientGraphNoTx = orientGraphFactory.getNoTx();

			OrientEdgeType oet = orientGraphNoTx.createEdgeType(typeDefinition
					.getName());
			oet.setDescription(typeDefinition.getDescription());
			oet.setAbstract(typeDefinition.isAbstractType());

			List<OClass> oSuperclasses = getSuperclassesAndCheckCompliancy(
					orientGraphNoTx, typeDefinition, baseType);
			oet.setSuperClasses(oSuperclasses);

			for (Property property : typeDefinition.getProperties()) {
				OProperty op = oet.createProperty(property.getName(),
						OType.getById(property.getType().byteValue()));
				op.setDescription(property.getDescription());
				op.setMandatory(property.isMandatory());
				op.setNotNull(property.isNotnull());
				op.setReadonly(property.isReadonly());
				op.setRegexp(property.getRegexpr());
				if (property.getLinkedClass() != null) {
					OClass linkedClass = getEntityOClass(orientGraphNoTx,
							property.getLinkedClass());
					if (linkedClass == null) {
						logger.trace("class {} not found in schema",
								property.getLinkedClass());
						throw new Exception("class "
								+ property.getLinkedClass()
								+ " not found in schema");
					}

					if (linkedClass.isEdgeType() || linkedClass.isVertexType()) {
						throw new Exception(
								"An Embedded Field cannot be an Entity or a Relation");
					}

					op.setLinkedClass(linkedClass);
				}
			}

			orientGraphNoTx.commit();
			
			OClass oClass = getTypeSchema(typeDefinition.getName(), null);
			return serializeOClass(oClass);
		} catch (Exception e) {
			throw new SchemaException(e);
		} finally {
			if (orientGraphNoTx != null) {
				orientGraphNoTx.shutdown();
			}
		}
	}

	public String registerDocumentSchema(String jsonSchema)
			throws SchemaException {
		OrientGraphFactory orientGraphFactory = SecurityContextMapper
				.getSecurityContextFactory(null, PermissionMode.WRITER);

		OrientGraphNoTx orientGraphNoTx = null;
		try {
			logger.trace("Trying to register {}", jsonSchema);
			
			ObjectMapper mapper = new ObjectMapper();
			TypeDefinition typeDefinition = mapper.readValue(jsonSchema,
						TypeDefinition.class);
			
			orientGraphNoTx = orientGraphFactory.getNoTx();

			ODatabaseDocumentTx oDatabaseDocumentTx = orientGraphNoTx.getRawGraph();
			OMetadata oMetadata = oDatabaseDocumentTx.getMetadata();
			OSchema oSchema = oMetadata.getSchema();

			OClass oClass = oSchema.createClass(typeDefinition.getName());
			oClass.setDescription(typeDefinition.getDescription());
			oClass.setAbstract(typeDefinition.isAbstractType());

			List<OClass> oSuperclasses = getSuperclassesAndCheckCompliancy(
					orientGraphNoTx, typeDefinition, null);
			oClass.setSuperClasses(oSuperclasses);

			oDatabaseDocumentTx.commit();
			
			
			for (Property property : typeDefinition.getProperties()) {
				OProperty ovp = oClass.createProperty(property.getName(),
						OType.getById(property.getType().byteValue()));
				ovp.setDescription(property.getDescription());
				ovp.setMandatory(property.isMandatory());
				ovp.setNotNull(property.isNotnull());
				ovp.setReadonly(property.isReadonly());
				ovp.setRegexp(property.getRegexpr());
				if (property.getLinkedClass() != null) {
					OClass linkedClass = getEntityOClass(orientGraphNoTx,
							property.getLinkedClass());
					if (linkedClass == null) {
						logger.trace("class {} not found in schema",
								property.getLinkedClass());
						throw new Exception("class "
								+ property.getLinkedClass()
								+ " not found in schema");
					}

					if (linkedClass.isEdgeType() || linkedClass.isVertexType()) {
						throw new Exception(
								"An Embedded Field cannot be an Entity or a Relation");
					}

					ovp.setLinkedClass(linkedClass);
				}
			}
			
			orientGraphNoTx.commit();
			
			return serializeOClass(oClass);
			
		} catch (Exception e) {
			throw new SchemaException(e);
		} finally {
			if (orientGraphNoTx != null) {
				orientGraphNoTx.shutdown();
			}
		}
	}

	/*
	public void addDocumentProperties(TypeDefinition typeDefinition) throws SchemaException {
		OrientGraphFactory orientGraphFactory = SecurityContextMapper
				.getSecurityContextFactory(null, PermissionMode.WRITER);

		OrientGraphNoTx orientGraphNoTx = null;
		try {
			logger.trace("Adding properties to {}", typeDefinition);

			OClass oClass = getEntityOClass(orientGraphNoTx, typeDefinition.getName());
			
			orientGraphNoTx = orientGraphFactory.getNoTx();
			
			for (Property property : typeDefinition.getProperties()) {
				OProperty ovp = oClass.createProperty(property.getName(),
						OType.getById(property.getType().byteValue()));
				ovp.setDescription(property.getDescription());
				ovp.setMandatory(property.isMandatory());
				ovp.setNotNull(property.isNotnull());
				ovp.setReadonly(property.isReadonly());
				ovp.setRegexp(property.getRegexpr());
				if (property.getLinkedClass() != null) {
					OClass linkedClass = getEntityOClass(orientGraphNoTx,
							property.getLinkedClass());
					if (linkedClass == null) {
						logger.trace("class {} not found in schema",
								property.getLinkedClass());
						throw new Exception("class "
								+ property.getLinkedClass()
								+ " not found in schema");
					}

					if (linkedClass.isEdgeType() || linkedClass.isVertexType()) {
						throw new Exception(
								"An Embedded Field cannot be an Entity or a Relation");
					}

					ovp.setLinkedClass(linkedClass);
				}
			}
		} catch (Exception e) {
			throw new SchemaException(e);
		} finally {
			if (orientGraphNoTx != null) {
				orientGraphNoTx.shutdown();
			}
		}
	}
	*/
	
	@Override
	public String registerEntitySchema(String jsonSchema)
			throws SchemaException {
		return registerVertexTypeSchema(jsonSchema, Entity.class);
	}

	@Override
	public String getEntitySchema(String entityType)
			throws SchemaNotFoundException {
		return getTypeSchemaAsString(entityType, Entity.NAME);
	}

	@Override
	public String updateEntitySchema(String entityType, String jsonSchema)
			throws SchemaNotFoundException, SchemaException {
		throw new UnsupportedOperationException("Not Yet implemented");
	}

	@Override
	public String deleteEntitySchema(String entityType)
			throws SchemaNotFoundException {
		throw new UnsupportedOperationException("Not Yet implemented");
	}

	@Override
	public String registerFacetSchema(String jsonSchema) throws SchemaException {
		return registerVertexTypeSchema(jsonSchema, Facet.class);
	}

	@Override
	public String getFacetSchema(String facetType)
			throws SchemaNotFoundException {
		return getTypeSchemaAsString(facetType, Facet.NAME);
	}

	@Override
	public String updateFacetSchema(String facetType, String jsonSchema)
			throws SchemaNotFoundException, SchemaException {
		throw new UnsupportedOperationException("Not Yet implemented");
	}

	@Override
	public String deleteFacetSchema(String facetType)
			throws SchemaNotFoundException {
		throw new UnsupportedOperationException("Not Yet implemented");
	}

	@Override
	public String registerResourceSchema(String jsonSchema)
			throws SchemaException {
		return registerVertexTypeSchema(jsonSchema, Resource.class);
	}

	@Override
	public String getResourceSchema(String resourceType)
			throws SchemaNotFoundException {
		return getTypeSchemaAsString(resourceType, Resource.NAME);
	}

	@Override
	public String updateResourceSchema(String resourceType, String jsonSchema)
			throws SchemaNotFoundException, SchemaException {
		throw new UnsupportedOperationException("Not Yet implemented");
	}

	@Override
	public String deleteResourceSchema(String resourceType)
			throws SchemaNotFoundException {
		throw new UnsupportedOperationException("Not Yet implemented");
	}

	@Override
	public String registerEmbeddedTypeSchema(String jsonSchema)
			throws SchemaException {
		
		return registerDocumentSchema(jsonSchema);
		/*
		ObjectMapper mapper = new ObjectMapper();
		TypeDefinition typeDefinition;
		try {
			typeDefinition = mapper.readValue(jsonSchema,
					TypeDefinition.class);
			registerDocumentSchema(typeDefinition);
			addDocumentProperties(typeDefinition);
			
			return jsonSchema;
		} catch (SchemaException e) {
			throw e;
		} catch (Exception e) {
			throw new SchemaException(e);
		}
		*/
	}

	@Override
	public String getEmbeddedTypeSchema(String embeddedType)
			throws SchemaNotFoundException {
		return getTypeSchemaAsString(embeddedType, null);
	}

	@Override
	public String updateEmbeddedTypeSchema(String embeddedType,
			String jsonSchema) throws SchemaNotFoundException, SchemaException {
		throw new UnsupportedOperationException("Not Yet implemented");
	}

	@Override
	public String deleteEmbeddedTypeSchema(String embeddedType)
			throws SchemaNotFoundException {
		throw new UnsupportedOperationException("Not Yet implemented");
	}

	@Override
	public String registerRelationSchema(String jsonSchema)
			throws SchemaException {
		return registerEdgeTypeSchema(jsonSchema, Relation.NAME);
	}

	@Override
	public String getRelationSchema(String relationType)
			throws SchemaNotFoundException {
		return getTypeSchemaAsString(relationType, Relation.NAME);
	}

	@Override
	public String updateRelationSchema(String relationType, String jsonSchema)
			throws SchemaNotFoundException, SchemaException {
		throw new UnsupportedOperationException("Not Yet implemented");
	}

	@Override
	public String deleteRelationSchema(String relationType)
			throws SchemaNotFoundException {
		throw new UnsupportedOperationException("Not Yet implemented");
	}

	@Override
	public String registerConsistOfSchema(String jsonSchema)
			throws SchemaException {
		return registerEdgeTypeSchema(jsonSchema, ConsistOf.NAME);
	}

	@Override
	public String getConsistOfSchema(String consistOfType)
			throws SchemaNotFoundException {
		return getTypeSchemaAsString(consistOfType, ConsistOf.NAME);
	}

	@Override
	public String updateConsistOfSchema(String consistOfType, String jsonSchema)
			throws SchemaNotFoundException, SchemaException {
		throw new UnsupportedOperationException("Not Yet implemented");
	}

	@Override
	public String deleteConsistOfSchema(String consistOfType)
			throws SchemaNotFoundException {
		throw new UnsupportedOperationException("Not Yet implemented");
	}

	@Override
	public String registerRelatedToSchema(String jsonSchema)
			throws SchemaException {
		return registerEdgeTypeSchema(jsonSchema, RelatedTo.NAME);
	}

	@Override
	public String getRelatedToSchema(String relatedToType)
			throws SchemaNotFoundException {
		return getTypeSchemaAsString(relatedToType, RelatedTo.NAME);
	}

	@Override
	public String updateRelatedToSchema(String relatedToType, String jsonSchema)
			throws SchemaNotFoundException, SchemaException {
		throw new UnsupportedOperationException("Not Yet implemented");
	}

	@Override
	public String deleteRelatedToSchema(String relatedToType)
			throws SchemaException {
		throw new UnsupportedOperationException("Not Yet implemented");
	}

}
