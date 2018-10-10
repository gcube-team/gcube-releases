package org.gcube.data.access.storagehub.handlers;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.value.BinaryValue;
import org.apache.jackrabbit.value.BooleanValue;
import org.apache.jackrabbit.value.DateValue;
import org.apache.jackrabbit.value.LongValue;
import org.apache.jackrabbit.value.StringValue;
import org.gcube.common.storagehub.model.NodeConstants;
import org.gcube.common.storagehub.model.annotations.Attribute;
import org.gcube.common.storagehub.model.annotations.AttributeRootNode;
import org.gcube.common.storagehub.model.annotations.ListNodes;
import org.gcube.common.storagehub.model.annotations.MapAttribute;
import org.gcube.common.storagehub.model.annotations.NodeAttribute;
import org.gcube.common.storagehub.model.annotations.RootNode;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.gcube.common.storagehub.model.items.TrashItem;
import org.gcube.common.storagehub.model.items.nodes.Content;
import org.gcube.common.storagehub.model.types.ItemAction;
import org.gcube.common.storagehub.model.types.NodeProperty;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemHandler {

	private static final Logger logger = LoggerFactory.getLogger(ItemHandler.class);

	private static ClassHandler classHandler = new ClassHandler();

	private static HashMap<Class, Map<String, Class>> typeToSubtypeMap = new HashMap<>();

	public static <T extends Item> T getItem(Node node, List<String> excludes) throws Exception {

		@SuppressWarnings("unchecked")
		Class<T> classToHandle = (Class<T>)classHandler.get(node.getPrimaryNodeType().getName());

		T item = classToHandle.newInstance();
		item.setId(node.getIdentifier());
		item.setName(Text.unescapeIllegalJcrChars(node.getName()));
		item.setPath(Text.unescapeIllegalJcrChars(node.getPath()));
		item.setLocked(node.isLocked());
		item.setPrimaryType(node.getPrimaryNodeType().getName());
		Item parent = null ;
		if (item instanceof SharedFolder) {
			logger.trace("I'm in a Shared Folder");
			item.setShared(true);
		}else {
			try {
				parent = ItemHandler.getItem(node.getParent(), Arrays.asList("hl:accounting","jcr:content"));
				item.setShared(parent.isShared());
			} catch(Exception e) {
				item.setShared(false);
			}
		}

		if (item instanceof TrashItem)
			item.setTrashed(true);
		else {
			try {
				if (parent==null)
					parent = ItemHandler.getItem(node.getParent(), Arrays.asList("hl:accounting","jcr:content"));
				item.setTrashed(parent.isTrashed());
			} catch(Exception e) {
				item.setTrashed(false);
			}
		}

		try{
			item.setParentId(node.getParent().getIdentifier());
			item.setParentPath(node.getParent().getPath());
		}catch (Throwable e) {
			logger.trace("Root node doesn't have a parent");
		}

		for (Field field : retrieveAllFields(classToHandle)){
			if (field.isAnnotationPresent(Attribute.class)){
				Attribute attribute = field.getAnnotation(Attribute.class);
				field.setAccessible(true);
				try{
					Class<?> returnType = field.getType();
					field.set(item, getPropertyValue(returnType, node.getProperty(attribute.value())));

				}catch(PathNotFoundException e){
					logger.trace("the current node dosn't contain {} property",attribute.value());
				} catch (Exception e ) {
					logger.warn("error setting value for property {} ",attribute.value());
				}
			} else if (field.isAnnotationPresent(NodeAttribute.class)){
				String fieldNodeName = field.getAnnotation(NodeAttribute.class).value();
				//for now it excludes only first level node
				if (excludes!=null && excludes.contains(fieldNodeName)) continue;
				logger.trace("retrieving field node "+field.getName());
				field.setAccessible(true);
				try{
					Node fieldNode = node.getNode(fieldNodeName); 
					logger.trace("looking in node {} searched with {}",fieldNode.getName(),fieldNodeName);
					field.set(item, iterateNodeAttributeFields(field.getType(), fieldNode));
				}catch(PathNotFoundException e){
					logger.trace("the current node dosn't contain {} node",fieldNodeName);
				} catch (Exception e ) {
					logger.warn("error setting value",e);
				}


			}
		}
		return item;
	}

	private static <T> T iterateNodeAttributeFields(Class<T> clazz, Node node) throws Exception{
		T obj = clazz.newInstance();
		for (Field field : retrieveAllFields(clazz)){
			if (field.isAnnotationPresent(Attribute.class)){
				Attribute attribute = field.getAnnotation(Attribute.class);
				field.setAccessible(true);
				try{
					@SuppressWarnings("rawtypes")
					Class returnType = field.getType();
					field.set(obj, getPropertyValue(returnType, node.getProperty(attribute.value())));
				}catch(PathNotFoundException e){
					logger.trace("the current node dosn't contain {} property",attribute.value());
				} catch (Exception e ) {
					logger.warn("error setting value",e);
				}
			} else if (field.isAnnotationPresent(MapAttribute.class)){
				logger.trace("found field {} of type annotated as MapAttribute in class {} and node name {}", field.getName(), clazz.getName(), node.getName());
				field.setAccessible(true);
				String exclude = field.getAnnotation(MapAttribute.class).excludeStartWith();
				Map<String, Object> mapToset = new HashMap<String, Object>();
				PropertyIterator iterator = node.getProperties();
				if (iterator!=null) {
					while (iterator.hasNext()){
						Property prop = iterator.nextProperty();
						if (!exclude.isEmpty() && prop.getName().startsWith(exclude)) continue;
						try{
							logger.trace("adding {} in the map",prop.getName());
							
							mapToset.put(prop.getName(), getPropertyValue(prop));
						}catch(PathNotFoundException e){
							logger.warn("the property {}  is not mapped",prop.getName(),e);
						} catch (Exception e ) {
							logger.warn("error setting value",e);
						}
					}
				}
				field.set(obj, mapToset);
			} else if (field.isAnnotationPresent(ListNodes.class)){
				logger.trace("found field {} of type annotated as ListNodes in class {} on node {}", field.getName(), clazz.getName(), node.getName());
				field.setAccessible(true);
				String exclude = field.getAnnotation(ListNodes.class).excludeTypeStartWith();
				String include = field.getAnnotation(ListNodes.class).includeTypeStartWith();

				Class listType = field.getAnnotation(ListNodes.class).listClass();

				Map<String, Class> subTypesMap = Collections.emptyMap();

				if (!typeToSubtypeMap.containsKey(listType)) {


					Configuration config = new ConfigurationBuilder().forPackages(listType.getPackage().getName());
					Reflections reflections = new Reflections(config);
					Set<Class> subTypes = reflections.getSubTypesOf(listType);


					if (subTypes.size()>0) {
						subTypesMap = new HashMap<>();
						for (Class subtype: subTypes) 
							if (subtype.isAnnotationPresent(AttributeRootNode.class)) {
								AttributeRootNode attributeRootNode = (AttributeRootNode)subtype.getAnnotation(AttributeRootNode.class);
								subTypesMap.put(attributeRootNode.value(), subtype);
							}
					} else logger.trace("no subtypes found for {}",listType.getName());

					typeToSubtypeMap.put(listType, subTypesMap);

				} else {
					logger.info("subtypes already found in cache");
					subTypesMap = typeToSubtypeMap.get(listType);
				}

				List<Object> toSetList = new ArrayList<>();

				NodeIterator iterator = node.getNodes();

				while (iterator.hasNext()){
					Node currentNode = iterator.nextNode();

					String primaryType = currentNode.getPrimaryNodeType().getName();

					logger.trace("the current node {} has a list",currentNode.getName());

					if (!include.isEmpty() && !primaryType.startsWith(include))
						continue;
					if (!exclude.isEmpty() && primaryType.startsWith(exclude))
						continue;
					if (subTypesMap.containsKey(primaryType))
						toSetList.add(iterateNodeAttributeFields(subTypesMap.get(primaryType), currentNode));
					else toSetList.add(iterateNodeAttributeFields(listType, currentNode));
				}
				if (toSetList.size()!=0) field.set(obj, toSetList);
			}
		}
		return obj;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object getPropertyValue(Class returnType, Property prop) throws Exception{
		if (returnType.equals(String.class))  return prop.getString();
		if (returnType.isEnum())  return Enum.valueOf(returnType, prop.getString());
		if (returnType.equals(Calendar.class))  return prop.getDate();
		if (returnType.equals(Boolean.class) || returnType.equals(boolean.class))  return prop.getBoolean();
		if (returnType.equals(Long.class) || returnType.equals(long.class)) return prop.getLong();
		if (returnType.equals(Integer.class) || returnType.equals(int.class)) return prop.getLong();
		if (returnType.isArray()) {
			if (prop.getType()==PropertyType.BINARY) {
				byte[] bytes = new byte[32000];

				try (InputStream stream = prop.getBinary().getStream()){
					stream.read(bytes);
				}
				return bytes;	
			} else {
				Object[] ret= getArrayValue(prop);
				return Arrays.copyOf(ret, ret.length, returnType);
			}
		} 
		throw new Exception(String.format("class %s not recognized",returnType.getName()));
	}



	@SuppressWarnings({ "rawtypes" })
	private static Value getObjectValue(Class returnType, Object value) throws Exception{
		if (returnType.equals(String.class))  return new StringValue((String) value);
		if (returnType.isEnum())  return new StringValue(((Enum) value).toString());
		if (returnType.equals(Calendar.class))  return new DateValue((Calendar) value);
		if (returnType.equals(Boolean.class) || returnType.equals(boolean.class))  return new BooleanValue((Boolean) value);
		if (returnType.equals(Long.class) || returnType.equals(long.class)) return new LongValue((Long) value);
		if (returnType.equals(Integer.class) || returnType.equals(int.class)) return new LongValue((Long) value);
		if (returnType.isArray()) {
			if (returnType.getComponentType().equals(Byte.class) 
					|| returnType.getComponentType().equals(byte.class)) 
				return new BinaryValue((byte[]) value);
		}
		throw new Exception(String.format("class %s not recognized",returnType.getName()));
	}


	private static Object[] getArrayValue(Property prop) throws Exception{
		Object[] values = new Object[prop.getValues().length];
		int i = 0;
		for (Value value : prop.getValues())
			values[i++] = getSingleValue(value);
		return values;
	}


	private static Object getPropertyValue(Property prop) throws Exception{
		if (prop.isMultiple()){
			Object[] values = new Object[prop.getValues().length];
			int i = 0;
			for (Value value : prop.getValues())
				values[i++] = getSingleValue(value);
			return values;
		} else
			return getSingleValue(prop.getValue());

	}

	private static Object getSingleValue(Value value) throws Exception{
		switch (value.getType()) {
		case PropertyType.DATE:
			return value.getDate();
		case PropertyType.BOOLEAN:
			return value.getBoolean();
		case PropertyType.LONG:
			return value.getDate();
		default:
			return value.getString();
		}
	}

	private static Set<Field> retrieveAllFields(Class<?> clazz){

		Set<Field> fields = new HashSet<Field>();
		Class<?> currentClass = clazz;
		do{
			List<Field> fieldsFound = Arrays.asList(currentClass.getDeclaredFields());
			fields.addAll(fieldsFound);
		}while ((currentClass =currentClass.getSuperclass())!=null);
		return fields;
	}

	public static <T extends Item> Node createNodeFromItem(Session session, Node parentNode, T item){
		try {

			//TODO: must understand this place is for name or title
			String primaryType= classHandler.getNodeType(item.getClass());
			Node newNode = parentNode.addNode(item.getTitle(), primaryType);
			//newNode.setPrimaryType(primaryType);
			for (Field field : retrieveAllFields(item.getClass())){
				if (field.isAnnotationPresent(Attribute.class)){
					Attribute attribute = field.getAnnotation(Attribute.class);
					if (attribute.isReadOnly()) continue;
					field.setAccessible(true);
					try{
						//Class<?> returnType = field.getType();
						newNode.setProperty(attribute.value(), getObjectValue(field.getType(), field.get(item)));

					} catch (Exception e ) {
						logger.warn("error setting value for attribute "+attribute.value(),e);
					}
				} else if (field.isAnnotationPresent(NodeAttribute.class)){
					NodeAttribute nodeAttribute = field.getAnnotation(NodeAttribute.class);
					if (nodeAttribute.isReadOnly()) continue;
					String nodeName = nodeAttribute.value();
					logger.debug("retrieving field node "+field.getName());
					field.setAccessible(true);
					try{
						iterateItemNodeAttributeFields(field.get(item), newNode, nodeName);
					} catch (Exception e ) {
						logger.warn("error setting value",e);
					}


				}
			}
			return newNode;
		} catch (RepositoryException e) {
			logger.error("error writing repository",e);
			throw new RuntimeException(e);
		}

	}

	private static void iterateItemNodeAttributeFields(Object object, Node parentNode, String nodeName) throws Exception{

		AttributeRootNode attributeRootNode = object.getClass().getAnnotation(AttributeRootNode.class);
		Node newNode = parentNode.addNode(nodeName, attributeRootNode.value());
		//newNode.setPrimaryType(attributeRootNode.value());
		for (Field field : retrieveAllFields(object.getClass())){
			if (field.isAnnotationPresent(Attribute.class)){
				Attribute attribute = field.getAnnotation(Attribute.class);
				if (attribute.isReadOnly()) continue;
				field.setAccessible(true);
				try{
					@SuppressWarnings("rawtypes")
					Class returnType = field.getType();
					newNode.setProperty(attribute.value(), getObjectValue(returnType, field.get(object)));
				} catch (Exception e ) {
					logger.warn("error setting value",e);
				}
			} else if (field.isAnnotationPresent(MapAttribute.class)){
				//logger.debug("found field {} of type annotated as MapAttribute in class {}", field.getName(), clazz.getName());
				field.setAccessible(true);
				Map<String, Object> mapToset = (Map<String, Object>)field.get(object);
				for (Entry<String, Object> entry : mapToset.entrySet())
					try{
						newNode.setProperty(entry.getKey(), getObjectValue(entry.getValue().getClass(), entry.getValue()));
					} catch (Exception e ) {
						logger.warn("error setting value",e);
					}

			} else if (field.isAnnotationPresent(ListNodes.class)){
				logger.debug("found field {} of type annotated as ListNodes in class {} on node {}", field.getName(), object.getClass().getName(), newNode.getName());
				field.setAccessible(true);
				List<Object> toSetList = (List<Object>) field.get(object);

				int i = 0;
				for (Object obj: toSetList){

					logger.debug("the current node {} has a list",newNode.getName());

					iterateItemNodeAttributeFields(obj,newNode, field.getName()+(i++));
				}
			}
		}
	}

	public static <F extends AbstractFileItem> void replaceContent(Session session, Node node, F item){
		try {

			node.setPrimaryType(item.getClass().getAnnotation(RootNode.class).value());			
			Node contentNode = node.getNode(NodeConstants.CONTENT_NAME);
			contentNode.setPrimaryType(item.getContent().getClass().getAnnotation(AttributeRootNode.class).value());

			node.setProperty(NodeProperty.LAST_MODIFIED.toString(), item.getLastModificationTime());
			node.setProperty(NodeProperty.LAST_MODIFIED_BY.toString(), item.getLastModifiedBy());
			node.setProperty(NodeProperty.LAST_ACTION.toString(), ItemAction.UPDATED.name());
			
			for (Field field : retrieveAllFields(item.getContent().getClass())){
				if (field.isAnnotationPresent(Attribute.class)){
					Attribute attribute = field.getAnnotation(Attribute.class);
					if (attribute.isReadOnly()) continue;
					field.setAccessible(true);
					try{
						//Class<?> returnType = field.getType();
						contentNode.setProperty(attribute.value(), getObjectValue(field.getType(), field.get(item.getContent())));

					} catch (Exception e ) {
						logger.warn("error setting value for attribute "+attribute.value(),e);
					}
				}			
			}

		} catch (RepositoryException e) {
			logger.error("error writing repository",e);
			throw new RuntimeException(e);
		}

	}

}
