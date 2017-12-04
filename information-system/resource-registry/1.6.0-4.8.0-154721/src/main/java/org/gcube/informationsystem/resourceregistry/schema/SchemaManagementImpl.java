/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.activation.UnsupportedDataTypeException;

import org.gcube.informationsystem.model.AccessType;
import org.gcube.informationsystem.model.embedded.Embedded;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.gcube.informationsystem.resourceregistry.context.SecurityContextMapper;
import org.gcube.informationsystem.resourceregistry.context.SecurityContextMapper.PermissionMode;
import org.gcube.informationsystem.types.TypeBinder;
import org.gcube.informationsystem.types.TypeBinder.Property;
import org.gcube.informationsystem.types.TypeBinder.TypeDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
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
 * 
 */
public class SchemaManagementImpl implements SchemaManagement {

	private static Logger logger = LoggerFactory
			.getLogger(SchemaManagementImpl.class);

	protected static OClass getOClass(OSchema oSchema, String type)
			throws SchemaException {
		return oSchema.getClass(type);
	}

	public static OClass getTypeSchema(OrientBaseGraph orientBaseGraph,
			String type, AccessType accessType) throws SchemaException {
		OMetadata oMetadata = orientBaseGraph.getRawGraph().getMetadata();
		OSchema oSchema = oMetadata.getSchema();
		return getTypeSchema(oSchema, type, accessType);
	}

	public static OClass getTypeSchema(OSchema oSchema, String type,
			AccessType accessType) throws SchemaException {
		try {
			OClass oClass = oSchema.getClass(type);
			if (oClass == null) {
				throw new SchemaNotFoundException(type + " was not registered");
			}
			if(accessType!=null && type.compareTo(accessType.getName())!= 0) {
				if (!oClass.isSubClassOf(accessType.getName())) {
					throw new SchemaException(type + " is not a " + accessType.getName());
				}
			}
			return oClass;
		} catch (SchemaNotFoundException snfe) {
			throw snfe;
		} catch (Exception e) {
			throw new SchemaException(e.getMessage());
		}
	}

	public static OClass getTypeSchema(String type, AccessType accessType)
			throws SchemaException {
		OrientGraphFactory orientGraphFactory = SecurityContextMapper
				.getSecurityContextFactory(
						SecurityContextMapper.ADMIN_SECURITY_CONTEXT_UUID,
						PermissionMode.READER);

		OrientGraphNoTx orientGraphNoTx = null;
		try {
			logger.debug("Getting {} Type {} schema",
					accessType != null ? accessType.getName() : "", type);

			orientGraphNoTx = orientGraphFactory.getNoTx();

			return getTypeSchema(orientGraphNoTx, type, accessType);

		} finally {
			if (orientGraphNoTx != null) {
				orientGraphNoTx.shutdown();
			}
		}
	}

	protected static TypeDefinition getTypeDefinition(OClass oClass)
			throws SchemaException {
		ODocument oDocument = ((OClassImpl) oClass).toStream();
		String json = oDocument.toJSON();
		try {
			return TypeBinder.deserializeTypeDefinition(json);
		} catch (Exception e) {
			throw new SchemaException(e);
		}
	}

	protected static String getTypeDefinitionAsString(OClass oClass)
			throws SchemaException {

		try {
			TypeDefinition typeDefinition = getTypeDefinition(oClass);
			return TypeBinder.serializeTypeDefinition(typeDefinition);
		} catch (Exception e) {
			throw new SchemaException(e);
		}
	}

	protected List<OClass> getSuperclassesAndCheckCompliancy(
			OrientGraphNoTx orientGraphNoTx, TypeDefinition typeDefinition,
			String baseType) throws SchemaException {

		Set<String> superClasses = typeDefinition.getSuperClasses();
		if (baseType != null) {
			if (superClasses == null || superClasses.size() == 0) {
				throw new RuntimeException(
						String.format(
								"No Superclass found in schema %s. The Type Definition must extend %s",
								typeDefinition, baseType));
			}
		}

		OMetadata oMetadata = orientGraphNoTx.getRawGraph().getMetadata();
		OSchema oSchema = oMetadata.getSchema();

		List<OClass> oSuperclasses = new ArrayList<>();
		for (String superClass : superClasses) {
			OClass oSuperClass = getOClass(oSchema, superClass);
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
	
	protected String registerTypeSchema(String jsonSchema, AccessType baseType)
			throws SchemaException {

		OrientGraphFactory orientGraphFactory = SecurityContextMapper
				.getSecurityContextFactory(
						SecurityContextMapper.ADMIN_SECURITY_CONTEXT_UUID,
						PermissionMode.WRITER);

		OrientGraphNoTx orientGraphNoTx = null;
		try {
			logger.info("Trying to register {} {}", baseType.getName(),
					jsonSchema);

			ObjectMapper mapper = new ObjectMapper();
			TypeDefinition typeDefinition = mapper.readValue(jsonSchema,
					TypeDefinition.class);

			orientGraphNoTx = orientGraphFactory.getNoTx();
			OMetadata oMetadata = orientGraphNoTx.getRawGraph().getMetadata();
			OSchema oSchema = oMetadata.getSchema();

			OClass oClass = null;

			if (Entity.class.isAssignableFrom(baseType.getTypeClass())) {
				oClass = orientGraphNoTx.createVertexType(typeDefinition
						.getName());
			} else if (Relation.class.isAssignableFrom(baseType.getTypeClass())) {
				oClass = orientGraphNoTx.createEdgeType(typeDefinition
						.getName());
				
				/*
				 * This information are persisted in Management Context
				 * 
				 * String outBaseType = typeDefinition.getOutBaseType();
				 * String inBaseType = typeDefinition.getInBaseType();
				 * 
				 */
				
			} else if (Embedded.class.isAssignableFrom(baseType.getTypeClass())) {
				oClass = oSchema.createClass(typeDefinition.getName());
			} else {
				String error = String
						.format("Allowed superclass are %s, %s, %s, or any subclasses of them.",
								Entity.NAME, Relation.NAME, Embedded.NAME);
				throw new ResourceRegistryException(error);
			}

			if (typeDefinition.getDescription() != null) {
				try {
					oClass.setDescription(typeDefinition.getDescription());
				}catch (Exception e) {
					logger.warn("Unable to set description. This is an orient bug. See https://github.com/orientechnologies/orientdb/issues/7065");
				}
			}

			try {
				// oClass.setAbstract(false); // Used to allow to persist Schema in Context Management
				oClass.setAbstract(typeDefinition.isAbstract());
			} catch (Exception e) {
				logger.error(
						"Unable to set the Vertex Type {} as abstract. This is an OrientDB <= 2.2.12 bug. The Type will be created as it is not abstract.",
						typeDefinition.getName());
			}

			if (typeDefinition.getName().compareTo(Embedded.NAME) != 0) {
				List<OClass> oSuperclasses = getSuperclassesAndCheckCompliancy(
						orientGraphNoTx, typeDefinition, baseType.getName());
				oClass.setSuperClasses(oSuperclasses);
			}

			if (Resource.class.isAssignableFrom(baseType.getTypeClass())) {
				Set<Property> properties = typeDefinition.getProperties();
				if (properties != null && properties.size() > 0) {
					throw new Exception(
							"A Resource cannot contains any properties.");
				}
			} else {
				for (Property property : typeDefinition.getProperties()) {
					
					OType oType = OType.getById(property.getType().byteValue());
					switch (oType) {
						case EMBEDDEDLIST:
							throw new UnsupportedDataTypeException(oType.name() + " support is currently disabled due to OrientDB bug see https://github.com/orientechnologies/orientdb/issues/7354");
						case EMBEDDEDSET:
							throw new UnsupportedDataTypeException(oType.name() + " support is currently disabled due to OrientDB bug see https://github.com/orientechnologies/orientdb/issues/7354");
						default:
							break;
					}
					
					OProperty op = oClass.createProperty(property.getName(), oType);
					op.setDescription(property.getDescription());

					/*
					 * Mandatory and notNull does not work in distributed mode:
					 * so that on Type declaration they are forced to false
					 * ovp.setMandatory(property.isMandatory());
					 * ovp.setNotNull(property.isNotnull());
					 * 
					 * This information are persisted in Management Context
					 */
					op.setMandatory(false);
					op.setNotNull(false);

					op.setReadonly(property.isReadonly());
					op.setRegexp(property.getRegexp());

					if (property.getLinkedClass() != null) {
						OClass linkedClass = getOClass(oSchema,
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
				toBeSerializedOClass = getOClass(oSchema,
						typeDefinition.getName());
			}
			
			/*
			SchemaContextManagement managementUtility = new SchemaContextManagement();
			String ret  = managementUtility.create(jsonSchema, baseType);
			*/
			
			// TODO Remove when the previous has been implemented
			String ret = getTypeDefinitionAsString(toBeSerializedOClass);
			
			logger.info("{} type registered successfully: {}",
					baseType.getName(), jsonSchema);
			return ret;

		} catch (Exception e) {
			throw new SchemaException(e);
		} finally {
			if (orientGraphNoTx != null) {
				orientGraphNoTx.shutdown();
			}
		}
	}

	protected String getSchema(String type, boolean includeSubtypes)
			throws SchemaNotFoundException, SchemaException {
		OrientGraphFactory orientGraphFactory = SecurityContextMapper
				.getSecurityContextFactory(
						SecurityContextMapper.ADMIN_SECURITY_CONTEXT_UUID,
						PermissionMode.WRITER);

		OrientGraphNoTx orientGraphNoTx = null;
		try {
			orientGraphNoTx = orientGraphFactory.getNoTx();

			OMetadata oMetadata = orientGraphNoTx.getRawGraph().getMetadata();
			OSchema oSchema = oMetadata.getSchema();
			OClass baseOClass = getTypeSchema(oSchema, type, null);

			List<TypeDefinition> typeDefinitions = new ArrayList<>();
			typeDefinitions.add(getTypeDefinition(baseOClass));
			
			if(includeSubtypes){
				Collection<OClass> subClasses = baseOClass.getAllSubclasses();
				for (OClass oClass : subClasses) {
					typeDefinitions.add(getTypeDefinition(oClass));
				}
			}
			
			/*
			Collection<OClass> oClasses = oSchema.getClasses();
			for (OClass oClass : oClasses) {
				if (oClass.isSubClassOf(baseOClass)) {
					typeDefinitions.add(getTypeDefinition(oClass));
				}
			}
			*/

			return TypeBinder.serializeTypeDefinitions(typeDefinitions);
		} catch (SchemaException e) {
			throw e;
		} catch (Exception e) {
			throw new SchemaException(e);
		} finally {
			if (orientGraphNoTx != null) {
				orientGraphNoTx.shutdown();
			}
		}

	}

	@Override
	public String create(String jsonSchema, AccessType accessType)
			throws SchemaException {
		return registerTypeSchema(jsonSchema, accessType);
	}

	@Override
	public String read(String entityType, boolean includeSubtypes)
			throws SchemaNotFoundException, SchemaException {
		return getSchema(entityType, includeSubtypes);
	}

	@Override
	public String update(String entityType, AccessType accessType,
			String jsonSchema) throws SchemaNotFoundException, SchemaException {
		throw new UnsupportedOperationException("Not Yet implemented");
	}

	@Override
	public String delete(String entityType, AccessType accessType)
			throws SchemaNotFoundException {
		throw new UnsupportedOperationException("Not Yet implemented");
	}

}
