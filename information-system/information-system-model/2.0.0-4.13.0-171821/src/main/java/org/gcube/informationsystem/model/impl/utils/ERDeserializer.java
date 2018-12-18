/**
 * 
 */
package org.gcube.informationsystem.model.impl.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gcube.informationsystem.model.reference.AccessType;
import org.gcube.informationsystem.model.reference.ISManageable;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class ERDeserializer<ISM extends ISManageable> extends StdDeserializer<ISM> {

	private static final long serialVersionUID = -2551569658316955137L;

	protected final ObjectMapper mapper;
	
	public ERDeserializer(Class<ISM> clz, ObjectMapper mapper) {
		super(clz);
		this.mapper = mapper;
	}

	@Override
	public Object deserializeWithType(JsonParser jp,
			DeserializationContext ctxt, TypeDeserializer typeDeserializer)
			throws IOException {
		
		TreeNode treeNode = jp.readValueAsTree();
		JsonFactory jsonFactory = mapper.getFactory();
		JsonParser clonedJP = jsonFactory.createParser(treeNode.toString());
		clonedJP.nextToken();
		try {
			return typeDeserializer.deserializeTypedFromAny(clonedJP, ctxt);
		} catch (Exception e) {

			Class<?> candidatedSuperClass = _valueClass;
			List<TextNode> toBeKeepSuperClasses = new ArrayList<>();

			ObjectNode objectNode = (ObjectNode) treeNode;

			try {
				JsonNode superClassesTreeNode = objectNode
						.get(ISManageable.SUPERCLASSES_PROPERTY);
				if (superClassesTreeNode != null
						&& superClassesTreeNode.isArray()) {
					ArrayNode arrayNode = (ArrayNode) superClassesTreeNode;
					for (int i = 0; i < arrayNode.size(); i++) {
						try {
							JsonNode jsonNode = arrayNode.get(i);
							JsonNodeType jsonNodeType = jsonNode.getNodeType();
							switch (jsonNodeType) {
							case STRING:
								String superClass = jsonNode.asText();
								try {
									Enum.valueOf(AccessType.class,
											superClass.toUpperCase());
									// It is one of the BaseType. Looking for
									// another type because the base one
									continue;
								} catch (Exception ex) {
									// can continue discovery
								}

								JavaType javaType = typeDeserializer
										.getTypeIdResolver().typeFromId(ctxt,
												superClass);
								if (javaType == null) {
									TextNode textNode = new TextNode(superClass);
									toBeKeepSuperClasses.add(textNode);
									continue;
								}
								Class<?> clz = javaType.getRawClass();
								if (candidatedSuperClass.isAssignableFrom(clz)) {
									// clz is a subClass so that is a candidate
									candidatedSuperClass = clz;
								}
								break;

							default:
								break;
							}
						} catch (Exception ex) {
							// Trying the next one
						}

					}
					arrayNode.removeAll();
					arrayNode.addAll(toBeKeepSuperClasses);
				}

			} catch (Exception ex) {
				// Using already known candidatedSuperClass
			}

			
			if(candidatedSuperClass == _valueClass){
				// No suitable class found Using Dummy Implementation
				candidatedSuperClass = AccessType.getAccessType(_valueClass).getDummyImplementationClass();
			}
			objectNode.set(ISManageable.CLASS_PROPERTY,
					new TextNode(candidatedSuperClass.getSimpleName()));
			JsonParser jsonParser = jsonFactory.createParser(objectNode
					.toString());
			jsonParser.nextToken();
			return typeDeserializer.deserializeTypedFromAny(jsonParser, ctxt);

		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public ISM deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		return (ISM) deserializeWithType(jp, ctxt, null);
	}

}
