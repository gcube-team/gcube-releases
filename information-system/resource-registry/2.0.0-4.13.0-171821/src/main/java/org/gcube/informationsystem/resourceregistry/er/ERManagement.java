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
import org.gcube.informationsystem.model.reference.AccessType;
import org.gcube.informationsystem.model.reference.ER;
import org.gcube.informationsystem.model.reference.ISManageable;
import org.gcube.informationsystem.model.reference.embedded.Header;
import org.gcube.informationsystem.model.reference.entity.Context;
import org.gcube.informationsystem.resourceregistry.api.exceptions.AlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.AvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.NotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaException;
import org.gcube.informationsystem.resourceregistry.context.ContextUtility;
import org.gcube.informationsystem.resourceregistry.context.security.SecurityContext;
import org.gcube.informationsystem.resourceregistry.context.security.SecurityContext.PermissionMode;
import org.gcube.informationsystem.resourceregistry.dbinitialization.DatabaseEnvironment;
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
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static Logger staticLogger = LoggerFactory.getLogger(ERManagement.class);
	
	public final String AT = "@";
	public final String UNDERSCORE = "_";
	
	protected final Set<String> ignoreKeys;
	protected final Set<String> ignoreStartWithKeys;
	
	protected Class<El> elementClass;
	protected final AccessType accessType;
	
	protected OrientGraph orientGraph;
	
	protected UUID uuid;
	protected JsonNode jsonNode;
	protected OClass oClass;
	protected String elementType;
	
	protected El element;
	protected boolean reload;
	
	public UUID getUUID() {
		return uuid;
	}
	
	public boolean isReload() {
		return reload;
	}
	
	public void setReload(boolean reload) {
		this.reload = reload;
	}
	
	public AccessType getAccessType() {
		return accessType;
	}
	
	protected SecurityContext workingContext;
	
	protected SecurityContext getWorkingContext() throws ResourceRegistryException {
		if(workingContext == null) {
			workingContext = ContextUtility.getCurrentSecurityContext();
		}
		return workingContext;
	}
	
	public void setWorkingContext(SecurityContext workingContext) {
		this.workingContext = workingContext;
	}
	
	protected ERManagement(AccessType accessType) {
		this.accessType = accessType;
		
		this.ignoreKeys = new HashSet<String>();
		
		this.ignoreStartWithKeys = new HashSet<String>();
		
		this.ignoreStartWithKeys.add(AT);
		this.ignoreStartWithKeys.add(UNDERSCORE);
		
		this.reload = false;
		
	}
	
	public void setUUID(UUID uuid) throws ResourceRegistryException {
		this.uuid = uuid;
		if(jsonNode != null) {
			checkUUIDMatch();
		}
	}
	
	public void setJSON(JsonNode jsonNode) throws ResourceRegistryException {
		this.jsonNode = jsonNode;
		checkJSON();
	}
	
	public void setJSON(String jsonRepresentation) throws ResourceRegistryException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			this.jsonNode = mapper.readTree(jsonRepresentation);
		} catch(IOException e) {
			throw new ResourceRegistryException(e);
		}
		checkJSON();
	}
	
	protected OClass getOClass() throws SchemaException, ResourceRegistryException {
		if(oClass == null) {
			if(element != null) {
				OrientElement orientElement = (OrientElement) element;
				OMetadata oMetadata = orientElement.getGraph().getRawGraph().getMetadata();
				OSchema oSchema = oMetadata.getSchema();
				String type = orientElement.getRecord().getClassName();
				oClass = oSchema.getClass(type);
			} else {
				oClass = SchemaManagementImpl.getTypeSchema(elementType, accessType);
			}
		}
		return oClass;
	}
	
	public void setElementType(String erType) throws ResourceRegistryException {
		if(this.elementType == null) {
			if(erType == null || erType.compareTo("") == 0) {
				erType = accessType.getName();
			}
			this.elementType = erType;
		} else {
			if(elementType.compareTo(erType) != 0) {
				throw new ResourceRegistryException(
						"Provided type " + erType + " does not match with the one already known " + this.accessType);
			}
		}
		
		if(jsonNode != null) {
			checkERMatch();
		}
	}
	
	public String getElementType() {
		return elementType;
	}
	
	protected void checkJSON() throws ResourceRegistryException {
		if(uuid == null) {
			try {
				uuid = org.gcube.informationsystem.model.impl.utils.Utility.getUUIDFromJsonNode(jsonNode);
			} catch(Exception e) {
			}
		} else {
			checkUUIDMatch();
		}
		
		if(this.elementType == null) {
			this.elementType = getClassProperty(jsonNode);
			getOClass();
		} else {
			checkERMatch();
		}
	}
	
	protected void checkERMatch() throws ResourceRegistryException {
		if(jsonNode != null) {
			String type = getClassProperty(jsonNode);
			if(type != null && type.compareTo(elementType) != 0) {
				String error = String.format("Requested type does not match with json representation %s!=%s",
						elementType, type);
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
		} catch(Exception e) {
			throw new ResourceRegistryException(e);
		}
		
		if(header != null) {
			UUID resourceUUID = header.getUUID();
			if(resourceUUID.compareTo(uuid) != 0) {
				String error = String.format(
						"UUID provided in header (%s) differs from the one (%s) used to identify the %s instance",
						resourceUUID.toString(), uuid.toString(), elementType);
				throw new ResourceRegistryException(error);
				
			}
		}
	}
	
	public JSONObject serializeSelfOnly() throws ResourceRegistryException {
		try {
			return toJSONObject();
		} catch(Exception e) {
			throw new ResourceRegistryException(e);
		}
	}
	
	public abstract String serialize() throws ResourceRegistryException;
	
	public abstract JSONObject serializeAsJson() throws ResourceRegistryException;
	
	protected abstract El reallyCreate() throws AlreadyPresentException, ResourceRegistryException;
	
	public El internalCreate() throws AlreadyPresentException, ResourceRegistryException {
		try {
			reallyCreate();
			
			Header entityHeader = HeaderUtility.getHeader(jsonNode, true);
			if(entityHeader != null) {
				element.setProperty(ER.HEADER_PROPERTY, entityHeader);
			} else {
				entityHeader = HeaderUtility.addHeader(element, null);
			}
			
			getWorkingContext().addElement(element, orientGraph);
			
			((OrientElement) element).save();
			
			return element;
		} catch(ResourceRegistryException e) {
			throw e;
		} catch(Exception e) {
			throw new ResourceRegistryException("Error Creating " + elementType + " with " + jsonNode, e);
		}
	}
	
	protected abstract El reallyUpdate() throws NotFoundException, ResourceRegistryException;
	
	public El internalUpdate() throws NotFoundException, ResourceRegistryException {
		try {
			
			reallyUpdate();
			
			HeaderUtility.updateModifiedByAndLastUpdate(element);
			((OrientElement) element).save();
			
			return element;
		} catch(ResourceRegistryException e) {
			throw e;
		} catch(Exception e) {
			throw new ResourceRegistryException("Error Updating " + elementType + " with " + jsonNode, e);
		}
	}
	
	public El internalCreateOrUdate() throws ResourceRegistryException {
		try {
			return internalUpdate();
		} catch(NotFoundException e) {
			return internalCreate();
		}
	}
	
	protected abstract boolean reallyDelete() throws NotFoundException, ResourceRegistryException;
	
	public boolean internalDelete() throws NotFoundException, ResourceRegistryException {
		// Added for consistency with create and update addToContext removeFromContext.
		return reallyDelete();
	}
	
	protected abstract boolean reallyAddToContext(SecurityContext targetSecurityContext)
			throws ContextException, ResourceRegistryException;
	
	public boolean internalAddToContext(SecurityContext targetSecurityContext)
			throws ContextException, ResourceRegistryException {
		try {
			boolean ret = reallyAddToContext(targetSecurityContext);
			HeaderUtility.updateModifiedByAndLastUpdate(element);
			((OrientElement) element).save();
			return ret && true;
		} catch(ResourceRegistryException e) {
			throw e;
		} catch(Exception e) {
			throw new ResourceRegistryException(
					"Error Adding " + elementType + " to " + targetSecurityContext.toString(), e.getCause());
		}
	}
	
	protected abstract boolean reallyRemoveFromContext(SecurityContext targetSecurityContext)
			throws ContextException, ResourceRegistryException;
	
	public boolean internalRemoveFromContext(SecurityContext targetSecurityContext)
			throws ContextException, ResourceRegistryException {
		try {
			boolean ret = reallyRemoveFromContext(targetSecurityContext);
			HeaderUtility.updateModifiedByAndLastUpdate(element);
			((OrientElement) element).save();
			return ret && true;
		} catch(ResourceRegistryException e) {
			throw e;
		} catch(Exception e) {
			throw new ResourceRegistryException(
					"Error Removing " + elementType + " from " + targetSecurityContext.toString(), e.getCause());
		}
	}
	
	public void setElement(El element) throws ResourceRegistryException {
		if(element == null) {
			throw new ResourceRegistryException("Trying to set null " + elementClass.getSimpleName() + " in " + this);
		}
		this.element = element;
		this.uuid = HeaderUtility.getHeader(element).getUUID();
		this.elementType = ((OrientElement) element).getLabel();
	}
	
	protected abstract NotFoundException getSpecificElementNotFoundException(NotFoundException e);
	
	protected abstract AvailableInAnotherContextException getSpecificERAvailableInAnotherContextException(
			String message);
	
	protected abstract AlreadyPresentException getSpecificERAlreadyPresentException(String message);
	
	public El getElement() throws NotFoundException, AvailableInAnotherContextException, ResourceRegistryException {
		if(element == null) {
			try {
				element = retrieveElement();
			} catch(NotFoundException e) {
				try {
					retrieveElementFromAnyContext();
					throw getSpecificERAvailableInAnotherContextException(elementType == null ? accessType.getName()
							: elementType + " with UUID " + uuid + " is available in another "
									+ Context.class.getSimpleName());
				} catch(AvailableInAnotherContextException e1) {
					throw e1;
				} catch(Exception e1) {
					throw e;
				}
			} catch(ResourceRegistryException e) {
				throw e;
			} catch(Exception e) {
				throw new ResourceRegistryException(e);
			}
			
		} else {
			if(reload) {
				((OrientElement) element).reload();
			}
		}
		return element;
	}
	
	public El retrieveElement() throws NotFoundException, ResourceRegistryException {
		try {
			if(uuid == null) {
				throw new NotFoundException("null UUID does not allow to retrieve the Element");
			}
			return Utility.getElementByUUID(orientGraph, elementType == null ? accessType.getName() : elementType, uuid,
					elementClass);
		} catch(NotFoundException e) {
			throw getSpecificElementNotFoundException(e);
		} catch(ResourceRegistryException e) {
			throw e;
		} catch(Exception e) {
			throw new ResourceRegistryException(e);
		}
	}
	
	public El retrieveElementFromAnyContext() throws NotFoundException, ResourceRegistryException {
		try {
			return Utility.getElementByUUIDAsAdmin(elementType == null ? accessType.getName() : elementType, uuid,
					elementClass);
		} catch(NotFoundException e) {
			throw getSpecificElementNotFoundException(e);
		} catch(ResourceRegistryException e) {
			throw e;
		} catch(Exception e) {
			throw new ResourceRegistryException(e);
		}
	}
	
	public abstract String reallyGetAll(boolean polymorphic) throws ResourceRegistryException;
	
	public String all(boolean polymorphic) throws ResourceRegistryException {
		try {
			
			orientGraph = getWorkingContext().getGraph(PermissionMode.READER);
			
			return reallyGetAll(polymorphic);
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
	
	public boolean exists() throws NotFoundException, AvailableInAnotherContextException, ResourceRegistryException {
		try {
			orientGraph = getWorkingContext().getGraph(PermissionMode.READER);
			
			getElement();
			
			return true;
		} catch(ResourceRegistryException e) {
			logger.error("Unable to find {} with UUID {}", accessType.getName(), uuid);
			throw e;
		} catch(Exception e) {
			logger.error("Unable to find {} with UUID {}", accessType.getName(), uuid, e);
			throw new ResourceRegistryException(e);
		} finally {
			if(orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}
	
	public String createOrUpdate() throws NotFoundException, AvailableInAnotherContextException, ResourceRegistryException {
		try {
			orientGraph = getWorkingContext().getGraph(PermissionMode.WRITER);
			orientGraph.setAutoStartTx(false);
			orientGraph.begin();
			
			boolean update = false;
			try {
				getElement();
				update = true;
				element = internalUpdate();
			}catch (NotFoundException e) {
				element = internalCreate();
			}
			
			orientGraph.commit();
			
			if(update) {
				setReload(true);
			}
			
			// TODO Notify to subscriptionNotification
			
			return serialize();
			
		} catch(ResourceRegistryException e) {
			logger.error("Unable to update {} with UUID {}", accessType.getName(), uuid);
			if(orientGraph != null) {
				orientGraph.rollback();
			}
			throw e;
		} catch(Exception e) {
			logger.error("Unable to update {} with UUID {}", accessType.getName(), uuid, e);
			if(orientGraph != null) {
				orientGraph.rollback();
			}
			throw new ResourceRegistryException(e);
		} finally {
			if(orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}
	
	
	
	public String create() throws AlreadyPresentException, ResourceRegistryException {
		
		try {
			orientGraph = getWorkingContext().getGraph(PermissionMode.WRITER);
			orientGraph.setAutoStartTx(false);
			orientGraph.begin();
			
			element = internalCreate();
			
			orientGraph.commit();
			
			// TODO Notify to subscriptionNotification
			
			return serialize();
			
		} catch(ResourceRegistryException e) {
			logger.error("Unable to create {}", accessType.getName());
			if(orientGraph != null) {
				orientGraph.rollback();
			}
			throw e;
		} catch(Exception e) {
			logger.error("Unable to create {}", accessType.getName(), e);
			if(orientGraph != null) {
				orientGraph.rollback();
			}
			throw new ResourceRegistryException(e);
		} finally {
			if(orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}
	
	public String read() throws NotFoundException, AvailableInAnotherContextException, ResourceRegistryException {
		try {
			orientGraph = getWorkingContext().getGraph(PermissionMode.READER);
			
			getElement();
			
			return serialize();
		} catch(ResourceRegistryException e) {
			logger.error("Unable to read {} with UUID {}", accessType.getName(), uuid);
			throw e;
		} catch(Exception e) {
			logger.error("Unable to read {} with UUID {}", accessType.getName(), uuid, e);
			throw new ResourceRegistryException(e);
		} finally {
			if(orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}
	
	public String update() throws NotFoundException, AvailableInAnotherContextException, ResourceRegistryException {
		try {
			orientGraph = getWorkingContext().getGraph(PermissionMode.WRITER);
			orientGraph.setAutoStartTx(false);
			orientGraph.begin();
			
			element = internalUpdate();
			
			orientGraph.commit();
			
			setReload(true);
			
			// TODO Notify to subscriptionNotification
			
			return serialize();
			
		} catch(ResourceRegistryException e) {
			logger.error("Unable to update {} with UUID {}", accessType.getName(), uuid);
			if(orientGraph != null) {
				orientGraph.rollback();
			}
			throw e;
		} catch(Exception e) {
			logger.error("Unable to update {} with UUID {}", accessType.getName(), uuid, e);
			if(orientGraph != null) {
				orientGraph.rollback();
			}
			throw new ResourceRegistryException(e);
		} finally {
			if(orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}
	
	public boolean delete() throws NotFoundException, AvailableInAnotherContextException, ResourceRegistryException {
		logger.debug("Going to delete {} with UUID {}", accessType.getName(), uuid);
		
		try {
			
			orientGraph = ContextUtility.getAdminSecurityContext().getGraph(PermissionMode.WRITER);
			orientGraph.setAutoStartTx(false);
			orientGraph.begin();
			
			boolean deleted = reallyDelete();
			
			if(deleted) {
				orientGraph.commit();
				logger.info("{} with UUID {} was successfully deleted.", accessType.getName(), uuid);
			} else {
				logger.info("{} with UUID {} was NOT deleted.", accessType.getName(), uuid);
				orientGraph.rollback();
			}
			
			return deleted;
			
		} catch(ResourceRegistryException e) {
			logger.error("Unable to delete {} with UUID {}", accessType.getName(), uuid);
			if(orientGraph != null) {
				orientGraph.rollback();
			}
			throw e;
		} catch(Exception e) {
			logger.error("Unable to delete {} with UUID {}", accessType.getName(), uuid, e);
			if(orientGraph != null) {
				orientGraph.rollback();
			}
			throw new ResourceRegistryException(e);
		} finally {
			if(orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}
	
	public boolean addToContext(UUID contextUUID)
			throws NotFoundException, ContextException, ResourceRegistryException {
		logger.info("Going to add {} with UUID {} to Context with UUID {}", accessType.getName(), uuid, contextUUID);
		
		try {
			orientGraph = ContextUtility.getAdminSecurityContext().getGraph(PermissionMode.WRITER);
			orientGraph.setAutoStartTx(false);
			orientGraph.begin();
			
			SecurityContext targetSecurityContext = ContextUtility.getInstance().getSecurityContextByUUID(contextUUID);
			
			boolean added = internalAddToContext(targetSecurityContext);
			
			orientGraph.commit();
			logger.info("{} with UUID {} successfully added to Context with UUID {}", elementType, uuid, contextUUID);
			
			return added;
		} catch(ResourceRegistryException e) {
			logger.error("Unable to add {} with UUID {} to Context with UUID {}", elementType, uuid, contextUUID);
			if(orientGraph != null) {
				orientGraph.rollback();
			}
			throw e;
		} catch(Exception e) {
			logger.error("Unable to add {} with UUID {} to Context with UUID {}", elementType, uuid, contextUUID, e);
			if(orientGraph != null) {
				orientGraph.rollback();
			}
			throw new ContextException(e);
		} finally {
			if(orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}
	
	public boolean removeFromContext(UUID contextUUID)
			throws NotFoundException, ContextException, ResourceRegistryException {
		logger.debug("Going to remove {} with UUID {} from Context with UUID {}", elementType, uuid, contextUUID);
		
		try {
			
			orientGraph = ContextUtility.getAdminSecurityContext().getGraph(PermissionMode.WRITER);
			orientGraph.setAutoStartTx(false);
			orientGraph.begin();
			
			SecurityContext targetSecurityContext = ContextUtility.getInstance().getSecurityContextByUUID(contextUUID);
			
			boolean removed = internalRemoveFromContext(targetSecurityContext);
			
			orientGraph.commit();
			logger.info("{} with UUID {} successfully removed from Context with UUID {}", elementType, uuid,
					contextUUID);
			
			return removed;
		} catch(ResourceRegistryException e) {
			logger.error("Unable to remove {} with UUID {} from Context with UUID {}", elementType, uuid, contextUUID);
			if(orientGraph != null) {
				orientGraph.rollback();
			}
			throw e;
		} catch(Exception e) {
			logger.error("Unable to remove {} with UUID {} from Context with UUID {}", elementType, uuid, contextUUID,
					e);
			if(orientGraph != null) {
				orientGraph.rollback();
			}
			throw new ContextException(e);
		} finally {
			if(orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}
	
	public static String getClassProperty(JsonNode jsonNode) {
		if(jsonNode.has(ISManageable.CLASS_PROPERTY)) {
			return jsonNode.get(ISManageable.CLASS_PROPERTY).asText();
		}
		return null;
	}
	
	public static Object getObjectFromElement(JsonNode value)
			throws UnsupportedDataTypeException, ResourceRegistryException {
		JsonNodeType jsonNodeType = value.getNodeType();
		
		switch(jsonNodeType) {
			case OBJECT:
				return EmbeddedMangement.getEmbeddedType(value);
			
			case ARRAY:
				/*
				List<Object> list = new ArrayList<Object>();
				Iterator<JsonNode> arrayElement = value.elements();
				while(arrayElement.hasNext()) {
					JsonNode arrayNode = arrayElement.next();
					Object objectNode = getObjectFromElement(arrayNode);
					if(objectNode != null) {
						list.add(objectNode);
					}
				}
				return list;
				*/
				throw new UnsupportedDataTypeException(
						"List/Set support is currently disabled due to OrientDB bug see https://github.com/orientechnologies/orientdb/issues/7354");
				
			case BINARY:
				break;
			
			case BOOLEAN:
				return value.asBoolean();
			
			case NULL:
				break;
			
			case NUMBER:
				if(value.isDouble() || value.isFloat()) {
					return value.asDouble();
				}
				if(value.isBigInteger() || value.isShort() || value.isInt()) {
					return value.asInt();
				}
				
				if(value.isLong()) {
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
	
	public static Map<String,Object> getPropertyMap(JsonNode jsonNode, Set<String> ignoreKeys,
			Set<String> ignoreStartWith) throws JsonProcessingException, IOException {
		
		Map<String,Object> map = new HashMap<>();
		
		if(ignoreKeys == null) {
			ignoreKeys = new HashSet<>();
		}
		
		if(ignoreStartWith == null) {
			ignoreStartWith = new HashSet<>();
		}
		
		Iterator<Entry<String,JsonNode>> fields = jsonNode.fields();
		
		OUTER_WHILE: while(fields.hasNext()) {
			Entry<String,JsonNode> entry = fields.next();
			
			String key = entry.getKey();
			
			if(ignoreKeys.contains(key)) {
				continue;
			}
			
			for(String prefix : ignoreStartWith) {
				if(key.startsWith(prefix)) {
					continue OUTER_WHILE;
				}
			}
			
			JsonNode value = entry.getValue();
			Object object = null;
			try {
				object = getObjectFromElement(value);
				if(object != null) {
					map.put(key, object);
				}
			} catch(ResourceRegistryException e) {
				staticLogger.warn("An invalidy property has been provided. It will be ignored.");
			}
			
		}
		
		return map;
	}
	
	public static Element updateProperties(OClass oClass, Element element, JsonNode jsonNode, Set<String> ignoreKeys,
			Set<String> ignoreStartWithKeys) throws ResourceRegistryException {
		
		Set<String> oldKeys = element.getPropertyKeys();
		
		Map<String,Object> properties;
		if(element instanceof Vertex || element instanceof Edge) {
			try {
				properties = getPropertyMap(jsonNode, ignoreKeys, ignoreStartWithKeys);
			} catch(IOException e) {
				throw new ResourceRegistryException(e);
			}
		} else {
			String error = String.format("Error while updating %s properties", element.toString());
			throw new ResourceRegistryException(error);
		}
		
		oldKeys.removeAll(properties.keySet());
		
		for(String key : properties.keySet()) {
			try {
				
				Object object = properties.get(key);
				if(!oClass.existsProperty(key)) {
					
					boolean set = false;
					
					if(object instanceof ODocument) {
						ODocument oDocument = (ODocument) object;
						((OrientElement) element).setProperty(key, oDocument, OType.EMBEDDED);
						set = true;
					}
					
					/*
					 * if(object instanceof Set){ ((OrientElement) element).setProperty(key, object,
					 * OType.EMBEDDEDSET); set = true; } if(object instanceof List){
					 * ((OrientElement) element).setProperty(key, object, OType.EMBEDDEDLIST); set =
					 * true; }
					 */
					
					if(!set) {
						element.setProperty(key, object);
					}
					
				} else {
					element.setProperty(key, object);
				}
				
			} catch(Exception e) {
				String error = String.format("Error while setting property %s : %s (%s)", key,
						properties.get(key).toString(), e.getMessage());
				staticLogger.error(error);
				throw new ResourceRegistryException(error, e);
			}
		}
		
		OUTER_FOR: for(String key : oldKeys) {
			
			if(ignoreKeys.contains(key)) {
				continue;
			}
			
			for(String prefix : ignoreStartWithKeys) {
				if(key.startsWith(prefix)) {
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
			if(key.compareTo(ER.HEADER_PROPERTY) == 0) {
				// Keeping the header
				HeaderOrient headerOrient = HeaderUtility.getHeaderOrient((ODocument) object);
				JSONObject headerObject = new JSONObject(headerOrient.toJSON("class"));
				return headerObject;
			}
			
			if(ignoreKeys.contains(key)) {
				return null;
			}
			
			for(String prefix : ignoreStartWithKeys) {
				if(key.startsWith(prefix)) {
					return null;
				}
			}
			
			if(object instanceof ODocument) {
				String json = ((ODocument) object).toJSON("class");
				JSONObject jsonObject = new JSONObject(json);
				return jsonObject;
			}
			
			if(object instanceof Date) {
				OProperty oProperty = getOClass().getProperty(key);
				OType oType = oProperty.getType();
				DateFormat dateFormat = ODateHelper.getDateTimeFormatInstance();
				switch(oType) {
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
			
			if(object instanceof Collection) {
				Collection<?> collection = (Collection<?>) object;
				JSONArray jsonArray = new JSONArray();
				for(Object o : collection) {
					Object obj = getPropertyForJson("PLACEHOLDER", o);
					jsonArray.put(obj);
				}
				
				return jsonArray;
			}
			
			return object.toString();
			
		} catch(Exception e) {
			throw new ResourceRegistryException(
					"Error while serializing " + key + "=" + object.toString() + " in " + getElement().toString(), e);
		}
	}
	
	protected Collection<String> getSuperclasses() throws SchemaException, ResourceRegistryException {
		Collection<OClass> allSuperClasses = getOClass().getAllSuperClasses();
		Collection<String> superClasses = new HashSet<>();
		for(OClass oSuperClass : allSuperClasses) {
			String name = oSuperClass.getName();
			if(name.compareTo(StringFactory.V.toUpperCase()) == 0 || name.compareTo(StringFactory.E.toUpperCase()) == 0
					|| name.compareTo(DatabaseEnvironment.O_RESTRICTED_CLASS) == 0) {
				continue;
			}
			superClasses.add(name);
		}
		
		return superClasses;
	}
	
	public JSONObject toJSONObject() throws ResourceRegistryException {
		try {
			OrientElement orientElement = (OrientElement) getElement();
			
			Map<String,Object> properties = orientElement.getProperties();
			for(String key : orientElement.getPropertyKeys()) {
				Object object = properties.get(key);
				object = getPropertyForJson(key, object);
				if(object != null) {
					properties.put(key, object);
				} else {
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
		} catch(ResourceRegistryException e) {
			throw e;
		} catch(Exception e) {
			throw new ResourceRegistryException("Error while serializing " + getElement().toString(), e);
		}
	}
	
}
