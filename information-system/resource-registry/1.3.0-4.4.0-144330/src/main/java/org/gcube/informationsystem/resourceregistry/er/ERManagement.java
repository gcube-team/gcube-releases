/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.er;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.gcube.informationsystem.model.AccessType;
import org.gcube.informationsystem.model.ER;
import org.gcube.informationsystem.model.ISManageable;
import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsRelatedTo;
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.EntityNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.RelationNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.gcube.informationsystem.resourceregistry.context.ContextUtility;
import org.gcube.informationsystem.resourceregistry.context.SecurityContextMapper;
import org.gcube.informationsystem.resourceregistry.context.SecurityContextMapper.PermissionMode;
import org.gcube.informationsystem.resourceregistry.dbinitialization.DatabaseIntializator;
import org.gcube.informationsystem.resourceregistry.er.entity.EntityManagement;
import org.gcube.informationsystem.resourceregistry.er.entity.FacetManagement;
import org.gcube.informationsystem.resourceregistry.er.entity.ResourceManagement;
import org.gcube.informationsystem.resourceregistry.er.relation.ConsistsOfManagement;
import org.gcube.informationsystem.resourceregistry.er.relation.IsRelatedToManagement;
import org.gcube.informationsystem.resourceregistry.er.relation.RelationManagement;
import org.gcube.informationsystem.resourceregistry.schema.SchemaManagementImpl;
import org.gcube.informationsystem.resourceregistry.utils.HeaderOrient;
import org.gcube.informationsystem.resourceregistry.utils.HeaderUtility;
import org.gcube.informationsystem.resourceregistry.utils.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.orientechnologies.orient.core.metadata.OMetadata;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.util.ODateHelper;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientElement;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.util.StringFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public abstract class ERManagement<ERType extends ER, El extends Element> {

	private static Logger logger = LoggerFactory.getLogger(ERManagement.class);

	public final String AT = "@";
	public final String UNDERSCORE = "_";

	protected final Set<String> ignoreKeys;
	protected final Set<String> ignoreStartWithKeys;

	protected Class<ERType> erTypeClass;
	protected String baseType;

	protected OrientGraph orientGraph;

	protected UUID uuid;
	protected JsonNode jsonNode;
	protected String erType;

	protected Class<El> elementClass;
	protected El element;

	@SuppressWarnings("rawtypes")
	public static ERManagement getERManagement(AccessType querableType)
			throws ResourceRegistryException {
		switch (querableType) {
		case FACET:
			return new FacetManagement();

		case RESOURCE:
			return new ResourceManagement();

		case IS_RELATED_TO:
			return new IsRelatedToManagement();

		case CONSISTS_OF:
			return new ConsistsOfManagement();

		default:
			throw new ResourceRegistryException(String.format(
					"%s is not querable", querableType.toString()));
		}
	}

	@SuppressWarnings("rawtypes")
	public static ERManagement getERManagement(String type)
			throws ResourceRegistryException {

		OClass oClass = SchemaManagementImpl.getTypeSchema(type, null);
		ERManagement erManagement = null;

		if (oClass.isSubClassOf(Resource.NAME)) {
			erManagement = new ResourceManagement();
		} else if (oClass.isSubClassOf(Facet.NAME)) {
			erManagement = new FacetManagement();
		} else if (oClass.isSubClassOf(ConsistsOf.NAME)) {
			erManagement = new ConsistsOfManagement();
		} else if (oClass.isSubClassOf(IsRelatedTo.NAME)) {
			erManagement = new IsRelatedToManagement();
		}

		if (erManagement == null) {
			throw new ResourceRegistryException(String.format(
					"%s is not querable", type.toString()));
		}

		erManagement.setElementType(type);
		return erManagement;
	}

	@SuppressWarnings("rawtypes")
	public static ERManagement getERManagement(OrientGraph orientGraph,
			Element element) throws ResourceRegistryException {
		if (element instanceof Vertex) {
			return EntityManagement.getEntityManagement(orientGraph,
					(Vertex) element);
		} else if (element instanceof Edge) {
			return RelationManagement.getRelationManagement(orientGraph,
					(Edge) element);
		}
		throw new ResourceRegistryException(String.format(
				"%s is not a %s nor a %s", element.getClass().getSimpleName(),
				Entity.NAME, Relation.NAME));
	}

	@SuppressWarnings("rawtypes")
	public static ERManagement getERManagementFromUUID(OrientGraph orientGraph,
			UUID uuid) throws ResourceRegistryException {
		Element element;

		try {
			element = Utility.getElementByUUID(orientGraph, null, uuid,
					Vertex.class);
		} catch (Exception e) {
			try {
				element = Utility.getElementByUUID(orientGraph, null, uuid,
						Edge.class);
			} catch (Exception ex) {
				throw new ResourceRegistryException(String.format(
						"%s does not belong to an %s nor to a %s",
						uuid.toString(), Entity.NAME, Relation.NAME));
			}
		}

		return getERManagement(orientGraph, element);

	}

	protected ERManagement(Class<ERType> erClass) {
		this.erTypeClass = erClass;

		this.ignoreKeys = new HashSet<String>();

		this.ignoreStartWithKeys = new HashSet<String>();

		this.ignoreStartWithKeys.add(AT);
		this.ignoreStartWithKeys.add(UNDERSCORE);

	}

	protected ERManagement(Class<ERType> erClass, OrientGraph orientGraph) {
		this(erClass);
		this.orientGraph = orientGraph;
	}

	public void setUUID(UUID uuid) throws ResourceRegistryException {
		this.uuid = uuid;
		if (jsonNode != null) {
			checkUUIDMatch();
		}
	}

	public void setJSON(JsonNode jsonNode) throws ResourceRegistryException {
		this.jsonNode = jsonNode;
		checkJSON();
	}

	public void setJSON(String jsonRepresentation)
			throws ResourceRegistryException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			this.jsonNode = mapper.readTree(jsonRepresentation);
		} catch (IOException e) {
			throw new ResourceRegistryException(e);
		}
		checkJSON();
	}

	public void setElementType(String erType) throws ResourceRegistryException {
		this.erType = erType;
		if (erType == null || erType.compareTo("") == 0) {
			if (Facet.class.isAssignableFrom(erTypeClass)) {
				this.erType = Facet.NAME;
			}
			if (Resource.class.isAssignableFrom(erTypeClass)) {
				this.erType = Resource.NAME;
			}
			if (ConsistsOf.class.isAssignableFrom(erTypeClass)) {
				this.erType = ConsistsOf.NAME;
			}
			if (IsRelatedTo.class.isAssignableFrom(erTypeClass)) {
				this.erType = IsRelatedTo.NAME;
			}
			throw new ResourceRegistryException("Invalid type " + erType
					+ " provided");
		}
		
		if (jsonNode != null) {
			checkERMatch();
		}
	}

	protected void checkJSON() throws ResourceRegistryException {
		if (uuid == null) {
			try {
				uuid = org.gcube.informationsystem.impl.utils.Utility
						.getUUIDFromJsonNode(jsonNode);
			} catch (Exception e) {

			}
		} else {
			checkUUIDMatch();
		}

		if (this.erType == null) {
			this.erType = getClassProperty(jsonNode);
		} else {
			checkERMatch();
		}
	}

	protected void checkERMatch() throws ResourceRegistryException {
		if(jsonNode!=null){
			String type = getClassProperty(jsonNode);
			if (type != null && type.compareTo(erType) != 0) {
				String error = String
						.format("Declared resourceType does not match with json representation %s!=%s",
								erType, type);
				logger.trace(error);
				throw new ResourceRegistryException(error);
			}
		}

		try {
			SchemaManagementImpl.getTypeSchema(erType, baseType);
		} catch (SchemaNotFoundException e) {
			throw e;
		}
	}

	protected void checkUUIDMatch() throws ResourceRegistryException {
		Header header = null;
		try {
			header = HeaderUtility.getHeader(jsonNode, false);
		} catch (Exception e) {
			throw new ResourceRegistryException(e);
		}

		if (header != null) {
			UUID resourceUUID = header.getUUID();
			if (resourceUUID.compareTo(uuid) != 0) {
				String error = String
						.format("UUID provided in header (%s) differs from the one (%s) used to identify the %s instance",
								resourceUUID.toString(), uuid.toString(),
								erType);
				throw new ResourceRegistryException(error);

			}
		}
	}

	public JSONObject serializeSelfOnly() throws ResourceRegistryException {
		try {
			return toJSONObject();
			//return Utility.toJsonObject((OrientElement) getElement(), false);
		}catch(Exception e){
			throw new ResourceRegistryException(e);
		}
	}
	
	public abstract String serialize() throws ResourceRegistryException;

	public abstract JSONObject serializeAsJson()
			throws ResourceRegistryException;

	public abstract El reallyUpdate() throws EntityNotFoundException,
			ResourceRegistryException;

	public abstract boolean reallyDelete() throws EntityNotFoundException,
			ResourceRegistryException;

	public abstract boolean reallyAddToContext() throws ContextException,
			ResourceRegistryException;

	public abstract boolean reallyRemoveFromContext() throws ContextException,
			ResourceRegistryException;

	public void setElement(El element) throws ResourceRegistryException {
		if (element == null) {
			throw new ResourceRegistryException("Trying to set null "
					+ elementClass.getSimpleName() + " in " + this);
		}
		this.element = element;
		this.uuid = HeaderUtility.getHeader(element).getUUID();
	}

	protected void throwElementNotFoundException(ResourceRegistryException e)
			throws EntityNotFoundException, RelationNotFoundException,
			ResourceRegistryException {

		if (Resource.class.isAssignableFrom(erTypeClass)) {
			throw new ResourceNotFoundException(e);
		} else if (Facet.class.isAssignableFrom(erTypeClass)) {
			throw new FacetNotFoundException(e);
		} else if (Relation.class.isAssignableFrom(erTypeClass)) {
			throw new RelationNotFoundException(e);
		}

		throw e;

	}

	public El getElement() throws ResourceRegistryException {
		try {
			if (element == null) {
				element = Utility.getElementByUUID(orientGraph,
						erType == null ? baseType : erType, uuid, elementClass);
			}
		} catch (ResourceRegistryException e) {
			throwElementNotFoundException(e);
		}
		return element;
	}

	public abstract String reallyGetAll(boolean polymorphic)
			throws ResourceRegistryException;

	public String all(boolean polymorphic) throws ResourceRegistryException {
		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.READER);

			return reallyGetAll(polymorphic);
		} catch (ResourceRegistryException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceRegistryException(e);
		} finally {
			if (orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}

	public String read() throws EntityNotFoundException,
			ResourceRegistryException {
		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.READER);

			getElement();

			return serialize();
		} catch (ResourceRegistryException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceRegistryException(e);
		} finally {
			if (orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}

	public String update() throws RelationNotFoundException,
			ResourceRegistryException {
		try {

			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.WRITER);

			element = reallyUpdate();

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

	public boolean delete() throws FacetNotFoundException,
			ResourceRegistryException {
		logger.debug("Going to delete {} with UUID {}", baseType, uuid);

		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.WRITER);

			boolean deleted = reallyDelete();

			orientGraph.commit();

			logger.info("{} with UUID {} was successfully deleted.", baseType,
					uuid);

			return deleted;

		} catch (ResourceRegistryException e) {
			logger.error("Unable to delete {} with UUID {}", baseType, uuid, e);
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw e;
		} catch (Exception e) {
			logger.error("Unable to delete {} with UUID {}", baseType, uuid, e);
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

	public boolean addToContext() throws ContextException {
		logger.debug("Going to add {} with UUID {} to actual Context",
				baseType, uuid);

		try {
			orientGraph = SecurityContextMapper.getSecurityContextFactory(
					SecurityContextMapper.ADMIN_SECURITY_CONTEXT_UUID,
					PermissionMode.WRITER).getTx();

			boolean added = reallyAddToContext();

			orientGraph.commit();
			logger.info("{} with UUID {} successfully added to actual Context",
					baseType, uuid);

			return added;
		} catch (Exception e) {
			logger.error("Unable to add {} with UUID {} to actual Context",
					baseType, uuid, e);
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw new ContextException(e);
		} finally {
			if (orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}

	public boolean removeFromContext() throws ContextException {
		logger.debug("Going to remove {} with UUID {} from actual Context",
				baseType, uuid);

		try {
			orientGraph = SecurityContextMapper.getSecurityContextFactory(
					SecurityContextMapper.ADMIN_SECURITY_CONTEXT_UUID,
					PermissionMode.WRITER).getTx();

			boolean removed = reallyRemoveFromContext();

			orientGraph.commit();
			logger.info(
					"{} with UUID {} successfully removed from actual Context",
					baseType, uuid);

			return removed;
		} catch (Exception e) {
			logger.error(
					"Unable to remove {} with UUID {} from actual Context",
					baseType, uuid, e);
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw new ContextException(e);
		} finally {
			if (orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}

	public static String getClassProperty(JsonNode jsonNode) {
		if (jsonNode.has(ISManageable.CLASS_PROPERTY)) {
			return jsonNode.get(ISManageable.CLASS_PROPERTY).asText();
		}
		return null;
	}

	public static Object getObjectFromElement(JsonNode value)
			throws ResourceRegistryException {
		JsonNodeType jsonNodeType = value.getNodeType();

		switch (jsonNodeType) {
			case OBJECT:
				return EmbeddedMangement.getEmbeddedType(value);
	
			case ARRAY:
				List<Object> array = new ArrayList<>();
				Iterator<JsonNode> arrayElement = value.elements();
				while (arrayElement.hasNext()) {
					JsonNode arrayNode = arrayElement.next();
					Object objectNode = getObjectFromElement(arrayNode);
					if (objectNode != null) {
						array.add(objectNode);
					}
				}
				return array;
	
			case BINARY:
				break;
	
			case BOOLEAN:
				return value.asBoolean();
	
			case NULL:
				break;
	
			case NUMBER:
				if (value.isDouble() || value.isFloat()) {
					return value.asDouble();
				}
				if (value.isBigInteger() || value.isShort() || value.isInt()) {
					return value.asInt();
				}
	
				if (value.isLong()) {
					return value.asLong();
				}
				break;
	
			case STRING:
				return value.asText();
	
			case MISSING:
				break;
	
			case POJO:
				break;
	
			default:
				break;
		}

		return null;
	}

	public static Map<String, Object> getPropertyMap(JsonNode jsonNode,
			Set<String> ignoreKeys, Set<String> ignoreStartWith)
			throws JsonProcessingException, IOException {

		Map<String, Object> map = new HashMap<>();

		if (ignoreKeys == null) {
			ignoreKeys = new HashSet<>();
		}

		if (ignoreStartWith == null) {
			ignoreStartWith = new HashSet<>();
		}

		Iterator<Entry<String, JsonNode>> fields = jsonNode.fields();

		OUTER_WHILE: while (fields.hasNext()) {
			Entry<String, JsonNode> entry = fields.next();

			String key = entry.getKey();

			if (ignoreKeys.contains(key)) {
				continue;
			}

			for (String prefix : ignoreStartWith) {
				if (key.startsWith(prefix)) {
					continue OUTER_WHILE;
				}
			}

			JsonNode value = entry.getValue();
			Object object = null;
			try {
				object = getObjectFromElement(value);
				if (object != null) {
					map.put(key, object);
				}
			} catch (ResourceRegistryException e) {
				logger.warn("An invalidy property has been provided. It will be ignored.");
			}

		}

		return map;
	}

	public static Element updateProperties(Element element, JsonNode jsonNode,
			Set<String> ignoreKeys, Set<String> ignoreStartWithKeys)
			throws ResourceRegistryException {

		Set<String> oldKeys = element.getPropertyKeys();

		Map<String, Object> properties;
		if (element instanceof Vertex || element instanceof Edge) {
			try {
				properties = getPropertyMap(jsonNode, ignoreKeys,
						ignoreStartWithKeys);
			} catch (IOException e) {
				throw new ResourceRegistryException(e);
			}
		} else {
			String error = String.format("Error while updating %s properties",
					element.toString());
			throw new ResourceRegistryException(error);
		}

		oldKeys.removeAll(properties.keySet());

		for (String key : properties.keySet()) {
			try {
				element.setProperty(key, properties.get(key));
			} catch (Exception e) {
				String error = String.format(
						"Error while setting property %s : %s", key, properties
								.get(key).toString());
				logger.error(error);
				throw new ResourceRegistryException(error, e);
			}
		}

		OUTER_FOR: for (String key : oldKeys) {

			if (ignoreKeys.contains(key)) {
				continue;
			}

			for (String prefix : ignoreStartWithKeys) {
				if (key.startsWith(prefix)) {
					continue OUTER_FOR;
				}
			}

			element.removeProperty(key);
		}

		((OrientElement) element).save();

		return element;
	}
	
	
	public JSONObject toJSONObject() throws ResourceRegistryException {
		try {
			OrientElement orientElement = (OrientElement) element;
			Map<String, Object> properties = orientElement.getProperties();
			
			
			String type = orientElement.getRecord().getClassName();
			OMetadata oMetadata = orientElement.getGraph().getRawGraph().getMetadata();
			OSchema oSchema = oMetadata.getSchema();
			OClass oClass = oSchema.getClass(type);
			
			
			OUTER_FOR: for(String key : orientElement.getPropertyKeys()){
				
				Object object = properties.get(key);
				
				if(key.compareTo(ER.HEADER_PROPERTY)==0){
					// Keep the header
					HeaderOrient headerOrient = HeaderUtility.getHeaderOrient((ODocument) object);
					JSONObject headerObject = new JSONObject(headerOrient.toJSON("class"));
					properties.put(ER.HEADER_PROPERTY, headerObject);
					continue;
				}
				
				if (ignoreKeys.contains(key)) {
					properties.remove(key);
					continue OUTER_FOR;
				}

				for (String prefix : ignoreStartWithKeys) {
					if (key.startsWith(prefix)) {
						properties.remove(key);
						continue OUTER_FOR;
					}
				}
				
				if(object instanceof ODocument){
					String json = ((ODocument) object).toJSON("class");
					JSONObject jsonObject = new JSONObject(json);
					properties.put(key, jsonObject);
				}
				
				if(object instanceof Date){
					OProperty oProperty = oClass.getProperty(key);
					OType oType = oProperty.getType();
					DateFormat dateFormat = ODateHelper.getDateTimeFormatInstance();
					switch (oType) {
						case DATE:
							dateFormat = ODateHelper.getDateFormatInstance();
							break;
	
						case DATETIME:
							dateFormat = ODateHelper.getDateTimeFormatInstance();
							break;
							
						default:
							break;
					}
					
					properties.put(key, dateFormat.format((Date) object));
				}
				
			}
			
			
			JSONObject jsonObject = new JSONObject(properties);
			jsonObject.put(ISManageable.CLASS_PROPERTY, type);
			
			
			
			//Collection<String> superClasses = oClass.getSuperClassesNames();
			
			Collection<OClass> allSuperClasses = oClass.getAllSuperClasses();
			Collection<String> superClasses = new HashSet<>();
			for(OClass oSuperClass : allSuperClasses){
				String name = oSuperClass.getName();
				if(name.compareTo(StringFactory.V.toUpperCase())==0 || 
					name.compareTo(StringFactory.E.toUpperCase())==0 || 
					name.compareTo(DatabaseIntializator.O_RESTRICTED_CLASS)==0){
					continue;
				}
				superClasses.add(name);
			}
			
			JSONArray jsonArray = new JSONArray(superClasses);
			jsonObject.put(ISManageable.SUPERCLASSES_PROPERTY, jsonArray);
			
			return jsonObject;
			
		}catch(Exception e){
			throw new ResourceRegistryException("Error while serializing " + element.toString(), e);
		}
	}

}
