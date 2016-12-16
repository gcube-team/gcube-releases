/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.resources.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gcube.informationsystem.model.embedded.Embedded;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsRelatedTo;
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.resourceregistry.api.SchemaManagement;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.gcube.informationsystem.resourceregistry.context.SecurityContextMapper;
import org.gcube.informationsystem.resourceregistry.context.SecurityContextMapper.PermissionMode;
import org.gcube.informationsystem.types.TypeBinder.Property;
import org.gcube.informationsystem.types.TypeBinder.TypeDefinition;
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
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientElementType;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 *         TODO Create an instance for each Registered Type in a management
 *         SecurityContext so that that management context can be used to see
 *         Entity and Relations as graph.
 */
public class SchemaManagementImpl implements SchemaManagement {

	private static Logger logger = LoggerFactory
			.getLogger(SchemaManagementImpl.class);

	protected static OClass getEntityOClass(OrientBaseGraph orientBaseGraph,
			String entityType) throws SchemaException {
		OMetadata oMetadata = orientBaseGraph.getRawGraph().getMetadata();
		OSchema oSchema = oMetadata.getSchema();
		return oSchema.getClass(entityType);
	}

	protected static OClass getTypeSchema(OrientBaseGraph orientBaseGraph,
			String type, String baseType) throws SchemaNotFoundException {
		try {
			OClass oClass = getEntityOClass(orientBaseGraph, type);
			if (baseType != null) {
				if (baseType.compareTo(Embedded.NAME) != 0
						&& !oClass.isSubClassOf(baseType)) {
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

	protected static OClass getTypeSchema(String type, String baseType)
			throws SchemaNotFoundException {
		OrientGraphFactory orientGraphFactory = SecurityContextMapper
				.getSecurityContextFactory(
						SecurityContextMapper.ADMIN_SECURITY_CONTEXT_UUID,
						PermissionMode.READER);

		OrientGraphNoTx orientGraphNoTx = null;
		try {
			logger.debug("Getting {} Type {} schema",
					baseType != null ? baseType : "", type);

			orientGraphNoTx = orientGraphFactory.getNoTx();

			return getTypeSchema(orientGraphNoTx, type, baseType);

		} finally {
			if (orientGraphNoTx != null) {
				orientGraphNoTx.shutdown();
			}
		}
	}

	protected static String serializeOClass(OClass oClass)
			throws SchemaException {
		ODocument oDocument = ((OClassImpl) oClass).toStream();
		return oDocument.toJSON();
	}

	protected List<OClass> getSuperclassesAndCheckCompliancy(
			OrientGraphNoTx orientGraphNoTx, TypeDefinition typeDefinition,
			String baseType) throws SchemaException {

		Set<String> superClasses = typeDefinition.getSuperclasses();
		if (baseType != null) {
			if (superClasses == null || superClasses.size() == 0) {
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
		try {
			OClass oClass = getTypeSchema(type, baseType);
			return serializeOClass(oClass);
		} catch (Exception e) {
			throw new SchemaNotFoundException(e);
		}
	}

	protected String registerTypeSchema(String jsonSchema, Class<?> baseType)
			throws SchemaException {

		OrientGraphFactory orientGraphFactory = SecurityContextMapper
				.getSecurityContextFactory(
						SecurityContextMapper.ADMIN_SECURITY_CONTEXT_UUID,
						PermissionMode.WRITER);

		OrientGraphNoTx orientGraphNoTx = null;
		try {
			logger.info("Trying to register {} {}", baseType.getSimpleName(),
					jsonSchema);

			ObjectMapper mapper = new ObjectMapper();
			TypeDefinition typeDefinition = mapper.readValue(jsonSchema,
					TypeDefinition.class);

			orientGraphNoTx = orientGraphFactory.getNoTx();

			OClass oClass = null;

			if (Entity.class.isAssignableFrom(baseType)) {
				oClass = orientGraphNoTx.createVertexType(typeDefinition
						.getName());
			} else if (Relation.class.isAssignableFrom(baseType)) {
				oClass = orientGraphNoTx.createEdgeType(typeDefinition
						.getName());
			} else if (Embedded.class.isAssignableFrom(baseType)) {
				ODatabaseDocumentTx oDatabaseDocumentTx = orientGraphNoTx
						.getRawGraph();
				OMetadata oMetadata = oDatabaseDocumentTx.getMetadata();
				OSchema oSchema = oMetadata.getSchema();
				oClass = oSchema.createClass(typeDefinition.getName());
			}

			oClass.setDescription(typeDefinition.getDescription());
			try {
				oClass.setAbstract(typeDefinition.isAbstractType());
			} catch (Exception e) {
				logger.error(
						"Unable to set the Vertex Type {} as abstract. This is an OrientDB <= 2.2.12 bug. The Type will be created as it is not abstarct.",
						typeDefinition.getName());
			}

			List<OClass> oSuperclasses = getSuperclassesAndCheckCompliancy(
					orientGraphNoTx,
					typeDefinition,
					baseType == Embedded.class ? null : baseType
							.getSimpleName());
			oClass.setSuperClasses(oSuperclasses);

			if (Resource.class.isAssignableFrom(baseType)) {
				Set<Property> properties = typeDefinition.getProperties();
				if (properties != null && properties.size() > 0) {
					throw new Exception(
							"A Resource cannot contains any properties.");
				}
			} else {
				for (Property property : typeDefinition.getProperties()) {
					OProperty op = oClass.createProperty(property.getName(),
							OType.getById(property.getType().byteValue()));
					op.setDescription(property.getDescription());

					/*
					 * Mandatory and notNull does not work in distributed mode:
					 * so that on Type declaration they are forced to false
					 * ovp.setMandatory(property.isMandatory());
					 * ovp.setNotNull(property.isNotnull());
					 */
					op.setMandatory(false);
					op.setNotNull(false);

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

						if (linkedClass.isEdgeType()
								|| linkedClass.isVertexType()) {
							throw new Exception(
									"An Embedded Field cannot be an Entity or a Relation");
						}

						op.setLinkedClass(linkedClass);
					} else if (property.getLinkedType() != null) {
						op.setLinkedType(OType.getById(property.getLinkedType()
								.byteValue()));
					}
				}
			}

			OClass toBeSerializedOClass = oClass;
			if (oClass instanceof OrientElementType) {
				toBeSerializedOClass = getEntityOClass(orientGraphNoTx,
						typeDefinition.getName());
			}

			String ret = serializeOClass(toBeSerializedOClass);
			logger.info("{} type registered successfully: {}",
					baseType.getSimpleName(), jsonSchema);
			return ret;

		} catch (Exception e) {
			throw new SchemaException(e);
		} finally {
			if (orientGraphNoTx != null) {
				orientGraphNoTx.shutdown();
			}
		}
	}

	@Override
	public String registerEntitySchema(String jsonSchema)
			throws SchemaException {
		return registerTypeSchema(jsonSchema, Entity.class);
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
		return registerTypeSchema(jsonSchema, Facet.class);
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
		return registerTypeSchema(jsonSchema, Resource.class);
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
		return registerTypeSchema(jsonSchema, Embedded.class);
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
		return registerTypeSchema(jsonSchema, Relation.class);
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
		return registerTypeSchema(jsonSchema, ConsistsOf.class);
	}

	@Override
	public String getConsistOfSchema(String consistOfType)
			throws SchemaNotFoundException {
		return getTypeSchemaAsString(consistOfType, ConsistsOf.NAME);
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
		return registerTypeSchema(jsonSchema, IsRelatedTo.class);
	}

	@Override
	public String getRelatedToSchema(String relatedToType)
			throws SchemaNotFoundException {
		return getTypeSchemaAsString(relatedToType, IsRelatedTo.NAME);
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
