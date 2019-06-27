package org.gcube.data.access.storagehub.handlers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.util.Text;
import org.gcube.common.storagehub.model.Excludes;
import org.gcube.common.storagehub.model.annotations.Attribute;
import org.gcube.common.storagehub.model.annotations.AttributeRootNode;
import org.gcube.common.storagehub.model.annotations.ListNodes;
import org.gcube.common.storagehub.model.annotations.MapAttribute;
import org.gcube.common.storagehub.model.annotations.NodeAttribute;
import org.gcube.common.storagehub.model.exceptions.BackendGenericError;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.gcube.common.storagehub.model.items.TrashItem;
import org.gcube.data.access.storagehub.Utils;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class Node2ItemConverter {

	private static final Logger logger = LoggerFactory.getLogger(Node2ItemConverter.class);
	
	private static HashMap<Class, Map<String, Class>> typeToSubtypeMap = new HashMap<>();
	
	public <T extends Item> T getFilteredItem(Node node, List<String> excludes, Class<? extends Item> nodeTypeToInclude) throws RepositoryException, BackendGenericError{
		@SuppressWarnings("unchecked")
		Class<T> classToHandle = (Class<T>)ClassHandler.instance().get(node.getPrimaryNodeType().getName());
		if (nodeTypeToInclude!=null && !(nodeTypeToInclude.isAssignableFrom(classToHandle))) return null;
		else return retrieveItem(node, excludes, classToHandle);
	}

	public <T extends Item> T getItem(Node node, List<String> excludes) throws RepositoryException, BackendGenericError{
		@SuppressWarnings("unchecked")
		Class<T> classToHandle = (Class<T>)ClassHandler.instance().get(node.getPrimaryNodeType().getName());
		Node nodeToRetrieve= node;
		if (SharedFolder.class.isAssignableFrom(classToHandle)) {
			NodeIterator it= node.getSharedSet();
			while (it.hasNext()) {
				Node sharedNode = it.nextNode();
				if (sharedNode.getPath().startsWith(Utils.getWorkspacePath().toPath())) {
					nodeToRetrieve = sharedNode;
					break;
				}
					
			}
		}
		return retrieveItem(nodeToRetrieve, excludes, classToHandle);
	}


	private <T extends Item> T retrieveItem(Node node, List<String> excludes, Class<T> classToHandle) throws RepositoryException, BackendGenericError{
		T item;
		try {
			item = classToHandle.newInstance();
		}catch (Exception e) {
			throw new BackendGenericError(e);
		}
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
				parent = getItem(node.getParent(), Excludes.ALL);
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
					parent = getItem(node.getParent(), Excludes.ALL);
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
					logger.debug("retrieve item - added field {}",field.getName());
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

	private <T> T iterateNodeAttributeFields(Class<T> clazz, Node node) throws Exception{
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
					logger.warn("error setting value {}",e.getMessage());
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
							logger.warn("the property {}  is not mapped",prop.getName());
						} catch (Exception e ) {
							logger.warn("error setting value {}",e.getMessage());
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
	private Object getPropertyValue(Class returnType, Property prop) throws Exception{
		if (returnType.equals(String.class))  return prop.getString();
		if (returnType.isEnum())  return Enum.valueOf(returnType, prop.getString());
		if (returnType.equals(Calendar.class))  return prop.getDate();
		if (returnType.equals(Boolean.class) || returnType.equals(boolean.class))  return prop.getBoolean();
		if (returnType.equals(Long.class) || returnType.equals(long.class)) return prop.getLong();
		if (returnType.equals(Integer.class) || returnType.equals(int.class)) return prop.getLong();
		if (returnType.isArray()) {
			if (prop.getType()==PropertyType.BINARY) {
				byte[] bytes = IOUtils.toByteArray(prop.getBinary().getStream());
				return bytes;	
			} else {
				Object[] ret= getArrayValue(prop);
				return Arrays.copyOf(ret, ret.length, returnType);
			}
		} 
		throw new Exception(String.format("class %s not recognized",returnType.getName()));
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
	
	private Object getPropertyValue(Property prop) throws Exception{
		if (prop.isMultiple()){
			Object[] values = new Object[prop.getValues().length];
			int i = 0;
			for (Value value : prop.getValues())
				values[i++] = getSingleValue(value);
			return values;
		} else
			return getSingleValue(prop.getValue());

	}
	
	private Object getSingleValue(Value value) throws Exception{
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
	
	private Object[] getArrayValue(Property prop) throws Exception{
		Object[] values = new Object[prop.getValues().length];
		int i = 0;
		for (Value value : prop.getValues())
			values[i++] = getSingleValue(value);
		return values;
	}

	public boolean checkNodeType(Node node, Class<? extends Item> classToCompare) throws BackendGenericError{
		try {
			
			logger.info("class from nodetype is {} and class to compare is {}",ClassHandler.instance().get(node.getPrimaryNodeType().getName()), classToCompare);
			
			return classToCompare.isAssignableFrom(ClassHandler.instance().get(node.getPrimaryNodeType().getName()));
					
			//(node.isNodeType(ClassHandler.instance().getNodeType(classToCompare)));
		}catch (Throwable e) {
			throw new BackendGenericError(e);
		}
	}
	
}
