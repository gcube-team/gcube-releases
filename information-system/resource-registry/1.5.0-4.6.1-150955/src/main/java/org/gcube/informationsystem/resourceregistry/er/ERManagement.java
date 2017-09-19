/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.er;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.activation.UnsupportedDataTypeException;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.gcube.informationsystem.model.AccessType;
import org.gcube.informationsystem.model.ER;
import org.gcube.informationsystem.model.ISManageable;
import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.entity.Context;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsRelatedTo;
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet.FacetAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet.FacetAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.RelationAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.RelationAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.RelationNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaException;
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
 */
public abstract class ERManagement<ERType extends ER, El extends Element> {

	private static Logger logger = LoggerFactory.getLogger(ERManagement.class);

	public final String AT = "@";
	public final String UNDERSCORE = "_";

	protected final Set<String> ignoreKeys;
	protected final Set<String> ignoreStartWithKeys;
	
	protected Class<El> elementClass;
	protected AccessType accessType;
	
	protected OrientGraph orientGraph;

	protected UUID uuid;
	protected JsonNode jsonNode;
	protected OClass oClass;
	protected String erType;

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

	public static Element getAnyElementByUUID(UUID uuid) throws ERNotFoundException, ResourceRegistryException {
		try{
			return Utility.getElementByUUIDAsAdmin(null, uuid, Vertex.class);
		}catch (ERNotFoundException e) {
			return Utility.getElementByUUIDAsAdmin(null, uuid, Edge.class);
		} catch (ResourceRegistryException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceRegistryException(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static ERManagement getERManagementFromUUID(OrientGraph orientGraph,
			UUID uuid) throws ResourceRegistryException {
		Element element;
		try {
			element = getAnyElementByUUID(uuid);
			return getERManagement(orientGraph, element);
		} catch (Exception e) {
			throw new ResourceRegistryException(String.format(
						"%s does not belong to an %s nor to a %s",
						uuid.toString(), Entity.NAME, Relation.NAME));
		}
	}

	protected ERManagement(AccessType accessType) {
		this.accessType = accessType;

		this.ignoreKeys = new HashSet<String>();

		this.ignoreStartWithKeys = new HashSet<String>();

		this.ignoreStartWithKeys.add(AT);
		this.ignoreStartWithKeys.add(UNDERSCORE);

	}

	protected ERManagement(AccessType accessType, OrientGraph orientGraph) {
		this(accessType);
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

	protected OClass getOClass() throws SchemaException {
		if(oClass==null){
			if(element!=null){
				OrientElement orientElement = (OrientElement) element;
				OMetadata oMetadata = orientElement.getGraph().getRawGraph().getMetadata();
				OSchema oSchema = oMetadata.getSchema();
				String type = orientElement.getRecord().getClassName();
				oClass = oSchema.getClass(type);
			}else{
				oClass = SchemaManagementImpl.getTypeSchema(erType, accessType);
			}
		}
		return oClass;
	}
	
	public void setElementType(String erType) throws ResourceRegistryException {
		this.erType = erType;
		if (erType == null || erType.compareTo("") == 0) {
			erType = accessType.getName();
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
			} catch (Exception e) {}
		} else {
			checkUUIDMatch();
		}

		if (this.erType == null) {
			this.erType = getClassProperty(jsonNode);
			getOClass();
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
		getOClass();
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
		}catch(Exception e){
			throw new ResourceRegistryException(e);
		}
	}
	
	public abstract String serialize() throws ResourceRegistryException;

	public abstract JSONObject serializeAsJson()
			throws ResourceRegistryException;

	public abstract El reallyCreate() throws ERAlreadyPresentException, 
			ResourceRegistryException;

	public abstract El reallyUpdate() throws ERNotFoundException,
			ResourceRegistryException;

	public El reallyCreateOrUdate() throws ResourceRegistryException {
		try {
			return reallyUpdate();
		}catch (ERNotFoundException e) {
			return reallyCreate();
		}
	}
	
	public abstract boolean reallyDelete() throws ERNotFoundException,
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

	protected ERNotFoundException getSpecificElementNotFoundException(ERNotFoundException e) {
		switch (accessType) {
			case RESOURCE:
				return new ResourceNotFoundException(e.getMessage(), e.getCause());
			case FACET:
				return new FacetNotFoundException(e.getMessage(), e.getCause());
			case IS_RELATED_TO:
				return new RelationNotFoundException(e.getMessage(), e.getCause());
			case CONSISTS_OF:
				return new RelationNotFoundException(e.getMessage(), e.getCause());
			default:
				return e;
		}
	}
	
	protected ERAvailableInAnotherContextException getSpecificERAvailableInAnotherContextException(String message){
		switch (accessType) {
			case RESOURCE:
				return new ResourceAvailableInAnotherContextException(message);
			case FACET:
				return new FacetAvailableInAnotherContextException(message);
			case IS_RELATED_TO:
				return new RelationAvailableInAnotherContextException(message);
			case CONSISTS_OF:
				return new RelationAvailableInAnotherContextException(message);
			default:
				return new ERAvailableInAnotherContextException(message);
		}
	}
	
	protected ERAlreadyPresentException getSpecificERAlreadyPresentException(String message){
		switch (accessType) {
			case RESOURCE:
				return new ResourceAlreadyPresentException(message);
			case FACET:
				return new FacetAlreadyPresentException(message);
			case IS_RELATED_TO:
				return new RelationAlreadyPresentException(message);
			case CONSISTS_OF:
				return new RelationAlreadyPresentException(message);
			default:
				return new ERAlreadyPresentException(message);
		}
	}
	
	
	public El getElement() throws ERNotFoundException, ERAvailableInAnotherContextException, ResourceRegistryException {
		if (element == null) {
			try {
				element = retrieveElement();
			}catch (ERNotFoundException e) {
				try {
					retrieveElementFromAnyContext();
					throw getSpecificERAvailableInAnotherContextException(erType == null ? accessType.getName() : erType + " with UUID " + uuid + " is available in another " + Context.class.getSimpleName());
				} catch (ERAvailableInAnotherContextException e1) {
					throw e1;
				}catch (Exception e1) {
					throw e;
				} 
			} catch (ResourceRegistryException e) {
				throw e;
			} catch (Exception e) {
				throw new ResourceRegistryException(e);
			}
			
		}
		return element;
	}
	
	public El retrieveElement() throws ERNotFoundException, ResourceRegistryException {
		try {
			if(uuid==null){
				throw new ERNotFoundException("null UUID does not allow to retrieve the Element");
			}
			return Utility.getElementByUUID(orientGraph,
						erType == null ? accessType.getName() : erType, uuid, elementClass);
		} catch (ERNotFoundException e) {
			throw getSpecificElementNotFoundException(e);
		} catch (ResourceRegistryException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceRegistryException(e);
		}
	}
	
	public El retrieveElementFromAnyContext() throws ERNotFoundException, ResourceRegistryException {
		try{
			return Utility.getElementByUUIDAsAdmin(erType == null ? accessType.getName() : erType, uuid, elementClass);
		}catch (ERNotFoundException e) {
			throw getSpecificElementNotFoundException(e);
		} catch (ResourceRegistryException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceRegistryException(e);
		}
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
	
	
	public boolean exists() throws ERNotFoundException, 
			ERAvailableInAnotherContextException, ResourceRegistryException {
		try {
			orientGraph = ContextUtility.getActualSecurityContextGraph(PermissionMode.READER);

			getElement();

			return true;
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

	public String create() throws ERAlreadyPresentException, ResourceRegistryException {

		try {
			orientGraph = ContextUtility.getActualSecurityContextGraph(PermissionMode.WRITER);

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

	public String read() throws ERNotFoundException,
			ERAvailableInAnotherContextException, ResourceRegistryException {
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

	public String update() throws ERNotFoundException,
			ERAvailableInAnotherContextException, ResourceRegistryException {
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

	public boolean delete() throws ERNotFoundException,
			ERAvailableInAnotherContextException, ResourceRegistryException {
		logger.debug("Going to delete {} with UUID {}", accessType.getName(), uuid);

		try {
			/*
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.WRITER);
			*/
			
			orientGraph = SecurityContextMapper.getSecurityContextFactory(
					SecurityContextMapper.ADMIN_SECURITY_CONTEXT_UUID,
					PermissionMode.WRITER).getTx();
			
			boolean deleted = reallyDelete();

			if(deleted){
				orientGraph.commit();
				logger.info("{} with UUID {} was successfully deleted.", accessType.getName(),
						uuid);
			}else{
				logger.info("{} with UUID {} was NOT deleted.", accessType.getName(),
						uuid);
				orientGraph.rollback();
			}
			
			return deleted;

		} catch (ResourceRegistryException e) {
			logger.error("Unable to delete {} with UUID {}", accessType.getName(), uuid, e);
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw e;
		} catch (Exception e) {
			logger.error("Unable to delete {} with UUID {}", accessType.getName(), uuid, e);
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

	public boolean addToContext() throws ERNotFoundException, ContextException {
		logger.debug("Going to add {} with UUID {} to actual Context",
				accessType.getName(), uuid);

		try {
			orientGraph = SecurityContextMapper.getSecurityContextFactory(
					SecurityContextMapper.ADMIN_SECURITY_CONTEXT_UUID,
					PermissionMode.WRITER).getTx();

			boolean added = reallyAddToContext();

			orientGraph.commit();
			logger.info("{} with UUID {} successfully added to actual Context",
					accessType.getName(), uuid);

			return added;
		} catch (Exception e) {
			logger.error("Unable to add {} with UUID {} to actual Context",
					accessType.getName(), uuid, e);
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

	public boolean removeFromContext() throws ERNotFoundException, ContextException {
		logger.debug("Going to remove {} with UUID {} from actual Context",
				accessType.getName(), uuid);

		try {
			orientGraph = SecurityContextMapper.getSecurityContextFactory(
					SecurityContextMapper.ADMIN_SECURITY_CONTEXT_UUID,
					PermissionMode.WRITER).getTx();

			boolean removed = reallyRemoveFromContext();

			orientGraph.commit();
			logger.info(
					"{} with UUID {} successfully removed from actual Context",
					accessType.getName(), uuid);

			return removed;
		} catch (Exception e) {
			logger.error(
					"Unable to remove {} with UUID {} from actual Context",
					accessType.getName(), uuid, e);
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
			throws UnsupportedDataTypeException, ResourceRegistryException{
		JsonNodeType jsonNodeType = value.getNodeType();

		switch (jsonNodeType) {
			case OBJECT:
				return EmbeddedMangement.getEmbeddedType(value);
	
			case ARRAY:
				/*
				List<Object> list = new ArrayList<Object>();
				Iterator<JsonNode> arrayElement = value.elements();
				while (arrayElement.hasNext()) {
					JsonNode arrayNode = arrayElement.next();
					Object objectNode = getObjectFromElement(arrayNode);
					if (objectNode != null) {
						list.add(objectNode);
					}
				}
				return list;
				*/
				throw new UnsupportedDataTypeException("List/Set support is currently disabled due to OrientDB bug see https://github.com/orientechnologies/orientdb/issues/7354");
				
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

	public static Element updateProperties(OClass oClass, Element element, JsonNode jsonNode,
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
				
				Object object = properties.get(key);
				if(!oClass.existsProperty(key)){
					
					boolean set = false;
					
					if(object instanceof ODocument){
						ODocument oDocument = (ODocument) object;
						((OrientElement) element).setProperty(key, oDocument, OType.EMBEDDED);
						set = true;
					}
					
					/*
					if(object instanceof Set){
						((OrientElement) element).setProperty(key, object, OType.EMBEDDEDSET);
						set = true;
					}
					
					if(object instanceof List){
						((OrientElement) element).setProperty(key, object, OType.EMBEDDEDLIST);
						set = true;
					}
					*/
					
					if(!set){
						element.setProperty(key, object);
					}
					
				} else{
					element.setProperty(key, object);
				}
				
			} catch (Exception e) {
				String error = String.format(
						"Error while setting property %s : %s (%s)", key, properties
								.get(key).toString(), e.getMessage());
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
	
	protected Object getPropertyForJson(String key, Object object) throws ResourceRegistryException {
		try {
			if(key.compareTo(ER.HEADER_PROPERTY)==0){
				// Keeping the header
				HeaderOrient headerOrient = HeaderUtility.getHeaderOrient((ODocument) object);
				JSONObject headerObject = new JSONObject(headerOrient.toJSON("class"));
				return headerObject;
			}
			
			if (ignoreKeys.contains(key)) {
				return null;
			}
	
			for (String prefix : ignoreStartWithKeys) {
				if (key.startsWith(prefix)) {
					return null;
				}
			}
			
			if(object instanceof ODocument){
				String json = ((ODocument) object).toJSON("class");
				JSONObject jsonObject = new JSONObject(json);
				return jsonObject;
			}
			
			if(object instanceof Date){
				OProperty oProperty = getOClass().getProperty(key);
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
				
				return dateFormat.format((Date) object);
			}

			if(object instanceof Collection){
				Collection<?> collection = (Collection<?>) object;
				JSONArray jsonArray = new JSONArray();
				for(Object o : collection){
					Object obj =  getPropertyForJson("PLACEHOLDER", o);
					jsonArray.put(obj);
				}
				
				return jsonArray;
			}
			
			
			return object.toString();
			
		}catch(Exception e){
			throw new ResourceRegistryException("Error while serializing " 
					+ key + "=" + object.toString() + " in " + getElement().toString(), e);
		}
	}
	
	
	protected Collection<String> getSuperclasses() throws SchemaException{
		Collection<OClass> allSuperClasses = getOClass().getAllSuperClasses();
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
		
		return superClasses;
	}
	
	
	public JSONObject toJSONObject() throws ResourceRegistryException {
		try {
			OrientElement orientElement = (OrientElement) getElement();
			
			Map<String, Object> properties = orientElement.getProperties();
			for(String key : orientElement.getPropertyKeys()){
				Object object = properties.get(key);
				object = getPropertyForJson(key, object);
				if(object!=null){
					properties.put(key, object);
				}else{
					properties.remove(key);
				}
			}
			
			JSONObject jsonObject = new JSONObject(properties);
			
			String type = orientElement.getRecord().getClassName();
			jsonObject.put(ISManageable.CLASS_PROPERTY, type);
			
			Collection<String> superClasses = getSuperclasses();
			JSONArray jsonArray = new JSONArray(superClasses);
			jsonObject.put(ISManageable.SUPERCLASSES_PROPERTY, jsonArray);
			
			return jsonObject;
		} catch (ResourceRegistryException e) {
			throw e;
		} catch(Exception e){
			throw new ResourceRegistryException("Error while serializing " + getElement().toString(), e);
		}
	}

}
