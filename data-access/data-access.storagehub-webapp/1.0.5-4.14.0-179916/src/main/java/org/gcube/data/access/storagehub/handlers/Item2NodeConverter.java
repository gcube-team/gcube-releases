package org.gcube.data.access.storagehub.handlers;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Singleton;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.value.BinaryValue;
import org.apache.jackrabbit.value.BooleanValue;
import org.apache.jackrabbit.value.DateValue;
import org.apache.jackrabbit.value.LongValue;
import org.apache.jackrabbit.value.StringValue;
import org.gcube.common.storagehub.model.Metadata;
import org.gcube.common.storagehub.model.NodeConstants;
import org.gcube.common.storagehub.model.annotations.Attribute;
import org.gcube.common.storagehub.model.annotations.AttributeRootNode;
import org.gcube.common.storagehub.model.annotations.ListNodes;
import org.gcube.common.storagehub.model.annotations.MapAttribute;
import org.gcube.common.storagehub.model.annotations.NodeAttribute;
import org.gcube.common.storagehub.model.annotations.RootNode;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.types.ItemAction;
import org.gcube.common.storagehub.model.types.NodeProperty;
import org.gcube.data.access.storagehub.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class Item2NodeConverter {

	private static final Logger logger = LoggerFactory.getLogger(Item2NodeConverter.class);
	
	public <T extends Item> Node getNode(Node parentNode, T item){
		try {
			String primaryType= ClassHandler.instance().getNodeType(item.getClass());
			Node newNode = parentNode.addNode(Text.escapeIllegalJcrChars(item.getName()), primaryType);
			//newNode.setPrimaryType(primaryType);
			for (Field field : retrieveAllFields(item.getClass())){
				if (field.isAnnotationPresent(Attribute.class)){
					Attribute attribute = field.getAnnotation(Attribute.class);
					if (attribute.isReadOnly()) continue;
					field.setAccessible(true);
					try{
						//Class<?> returnType = field.getType();
						logger.debug("creating node - added field {}",field.getName());
						Values values = getObjectValue(field.getType(), field.get(item));
						if (values.isMulti()) newNode.setProperty(attribute.value(), values.getValues());
						else  newNode.setProperty(attribute.value(), values.getValue());
					} catch (Exception e ) {
						logger.warn("error setting value for attribute {}: {}",attribute.value(), e.getMessage());
					}
				} else if (field.isAnnotationPresent(NodeAttribute.class)){
					NodeAttribute nodeAttribute = field.getAnnotation(NodeAttribute.class);
					if (nodeAttribute.isReadOnly()) continue;
					String nodeName = nodeAttribute.value();
					logger.debug("retrieving field node "+field.getName());
					field.setAccessible(true);
					try{
						Object obj = field.get(item);
						if (obj!=null) 
							iterateItemNodeAttributeFields(obj, newNode, nodeName);
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

	private void iterateItemNodeAttributeFields(Object object, Node parentNode, String nodeName) throws Exception{

		AttributeRootNode attributeRootNode = object.getClass().getAnnotation(AttributeRootNode.class);

		Node newNode;
		try {
			if (attributeRootNode==null || attributeRootNode.value().isEmpty())
				newNode = parentNode.addNode(nodeName);
			else newNode = parentNode.addNode(nodeName, attributeRootNode.value());
		}catch(ItemExistsException iee) {
			newNode = parentNode.getNode(nodeName);
		}
		
		for (Field field : retrieveAllFields(object.getClass())){
			if (field.isAnnotationPresent(Attribute.class)){
				Attribute attribute = field.getAnnotation(Attribute.class);
				if (attribute.isReadOnly()) continue;
				field.setAccessible(true);
				try{
					@SuppressWarnings("rawtypes")
					Class returnType = field.getType();
					Values values = getObjectValue(returnType, field.get(object));
					if (values.isMulti()) newNode.setProperty(attribute.value(), values.getValues());
					else newNode.setProperty(attribute.value(), values.getValue());
				} catch (Exception e ) {
					logger.warn("error setting value",e);
				}
			} else if (field.isAnnotationPresent(MapAttribute.class)){
				//logger.debug("found field {} of type annotated as MapAttribute in class {}", field.getName(), clazz.getName());
				field.setAccessible(true);
				Map<String, Object> mapToset = (Map<String, Object>)field.get(object);
				for (Entry<String, Object> entry : mapToset.entrySet())
					try{
						Values values = getObjectValue(entry.getValue().getClass(), entry.getValue());
						if (values.isMulti()) newNode.setProperty(entry.getKey(), values.getValues());
						else newNode.setProperty(entry.getKey(), values.getValue());
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
	
	@SuppressWarnings({ "rawtypes" })
	private Values getObjectValue(Class returnType, Object value) throws Exception{
		
		if (returnType.equals(String.class))  return new Values(new StringValue((String) value));
		if (returnType.isEnum())  return new Values(new StringValue(((Enum) value).toString()));
		if (returnType.equals(Calendar.class))  return new Values(new DateValue((Calendar) value));
		if (returnType.equals(Boolean.class) || returnType.equals(boolean.class))  return new Values(new BooleanValue((Boolean) value));
		if (returnType.equals(Long.class) || returnType.equals(long.class)) return new Values(new LongValue((Long) value));
		if (returnType.equals(Integer.class) || returnType.equals(int.class)) return new Values(new LongValue((Long) value));
		if (returnType.isArray()) {
			if (returnType.getComponentType().equals(Byte.class) 
					|| returnType.getComponentType().equals(byte.class)) 
				return new Values(new BinaryValue((byte[]) value));
			else {
				Object[] arrayObj= (Object[])value;
				Value[] arrayValue = new Value[arrayObj.length];
				int i=0;
				for (Object val: arrayObj) 
					arrayValue[i++]=getObjectValue(returnType.getComponentType(), val).getValue();
				return new Values(arrayValue);
			}
		}
		throw new Exception(String.format("class %s not recognized",returnType.getName()));
	}

	private Set<Field> retrieveAllFields(Class<?> clazz){

		Set<Field> fields = new HashSet<Field>();
		Class<?> currentClass = clazz;
		do{
			List<Field> fieldsFound = Arrays.asList(currentClass.getDeclaredFields());
			fields.addAll(fieldsFound);
		}while ((currentClass =currentClass.getSuperclass())!=null);
		return fields;
	}

	public <F extends AbstractFileItem> void replaceContent(Node node, F item, ItemAction action){
		try {

			node.setPrimaryType(item.getClass().getAnnotation(RootNode.class).value());			
			Node contentNode = node.getNode(NodeConstants.CONTENT_NAME);
			contentNode.setPrimaryType(item.getContent().getClass().getAnnotation(AttributeRootNode.class).value());

			node.setProperty(NodeProperty.LAST_MODIFIED.toString(), item.getLastModificationTime());
			node.setProperty(NodeProperty.LAST_MODIFIED_BY.toString(), item.getLastModifiedBy());
			node.setProperty(NodeProperty.LAST_ACTION.toString(), action.name());

			for (Field field : retrieveAllFields(item.getContent().getClass())){
				if (field.isAnnotationPresent(Attribute.class)){
					Attribute attribute = field.getAnnotation(Attribute.class);
					if (attribute.isReadOnly()) continue;
					field.setAccessible(true);
					try{
						//Class<?> returnType = field.getType();
						Values values = getObjectValue(field.getType(), field.get(item.getContent()));
						if (values.isMulti()) contentNode.setProperty(attribute.value(), values.getValues() );
						else contentNode.setProperty(attribute.value(), values.getValue());

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

	public <I extends Item> void updateMetadataNode(Node node, Map<String, Object> meta, String login){
		try {

			//TODO: make a method to update item not only metadata, check if the new metadata has an intersection with the old one to remove properties not needed

			Utils.setPropertyOnChangeNode(node, login, ItemAction.UPDATED);

			Node metadataNode;
			try {
				metadataNode = node.getNode(NodeProperty.METADATA.toString());
			}catch (PathNotFoundException e) {
				metadataNode = node.addNode(NodeProperty.METADATA.toString());
			}

			for (Field field : retrieveAllFields(Metadata.class)){
				if (field.isAnnotationPresent(MapAttribute.class)){
					//logger.debug("found field {} of type annotated as MapAttribute in class {}", field.getName(), clazz.getName());
					field.setAccessible(true);
					for (Entry<String, Object> entry : meta.entrySet())
						try{
							Values values = getObjectValue(entry.getValue().getClass(), entry.getValue());
							if (values.isMulti()) metadataNode.setProperty(entry.getKey(), values.getValues());
							else metadataNode.setProperty(entry.getKey(), values.getValue());
						} catch (Exception e ) {
							logger.warn("error setting value",e);
						}

				}
			}

		} catch (RepositoryException e) {
			logger.error("error writing repository",e);
			throw new RuntimeException(e);
		}

	}
	
	public static class Values {
		private Value singleValue;
		private Value[] multivalues;

		boolean multi = false;

		public Values(Value singleValue) {
			super();
			this.singleValue = singleValue;
			this.multivalues = null;
			multi = false;
		}

		public Values(Value[] multivalues) {
			super();
			multi = true;
			this.multivalues = multivalues;
			this.singleValue = null;
		}

		public boolean isMulti() {
			return multi;
		}

		public Value getValue(){
			if (multi) throw new RuntimeException("must be accessed as multi values");
			return this.singleValue;
		}

		public Value[] getValues(){
			if (!multi) throw new RuntimeException("must be accessed as single value");
			return this.multivalues;
		}

	}
	
}
