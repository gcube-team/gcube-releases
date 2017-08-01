package org.gcube.informationsystem.types;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.gcube.informationsystem.model.annotations.Abstract;
import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.embedded.Embedded;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsRelatedTo;
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.types.Type.OType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class TypeBinder {

	private static Logger logger = LoggerFactory.getLogger(TypeBinder.class);
	
	private static final String EDGE_CLASS_NAME   = "E";
	private static final String VERTEX_CLASS_NAME = "V";

	private final static String NAME = "NAME";
	private final static String DESCRIPTION = "DESCRIPTION";
	
	public final static String UUID_PATTERN = "^([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}){1}$";
	public final static String URI_PATTERN = null;
	public final static String URL_PATTERN = null;
	
	private static String getStaticStringFieldByName(Class<?> type, String fieldName, String defaultValue){
		Field field;
		try {
			field = type.getDeclaredField(fieldName);
			return (String) field.get(null);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			return defaultValue;
		}
	}
	
	public static String serializeTypeDefinition(TypeDefinition typeDefinition) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(typeDefinition);
		return json;
	}
	
	public static String serializeType(Class<?> type) throws Exception{
		TypeDefinition typeDefinition = createTypeDefinition(type);
		return serializeTypeDefinition(typeDefinition);
	}

	public static TypeDefinition deserializeTypeDefinition(String json) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, TypeDefinition.class);
	}
	
	public static String serializeTypeDefinitions(List<TypeDefinition> typeDefinitions) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(typeDefinitions);
		return json;
	}
	
	public static List<TypeDefinition> deserializeTypeDefinitions(String json) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		JavaType type = mapper.getTypeFactory().constructCollectionType(ArrayList.class, TypeDefinition.class) ;
		return mapper.readValue(json, type);
	}
	
	private static Class<?> getGenericClass(java.lang.reflect.Type type){
		TypeVariable<?> typeVariable = (TypeVariable<?>) type;
		java.lang.reflect.Type[] bounds = typeVariable.getBounds();
		java.lang.reflect.Type t = bounds[0];
		return (Class<?>) t; 
	}
	
	public static TypeDefinition createTypeDefinition(Class<?> clz) {
		TypeDefinition typeDefinition = new TypeDefinition();
		
		typeDefinition.name = getStaticStringFieldByName(clz, NAME, clz.getSimpleName());
		typeDefinition.description = getStaticStringFieldByName(clz, DESCRIPTION, "");
		typeDefinition.abstractType = false;
		
		if(clz.isAnnotationPresent(Abstract.class)){
			typeDefinition.abstractType = true;
		}
		
		if(Entity.class.isAssignableFrom(clz)) {
			if(Resource.class.isAssignableFrom(clz)){
				typeDefinition.superClasses = retrieveSuperClasses(clz, Resource.class, Entity.NAME);
			}else{
				if(Facet.class.isAssignableFrom(clz)){
					typeDefinition.superClasses = retrieveSuperClasses(clz, Facet.class, Entity.NAME);
				} else {
					typeDefinition.superClasses = retrieveSuperClasses(clz, Entity.class, VERTEX_CLASS_NAME);
				}
			}
		} else if(Relation.class.isAssignableFrom(clz)){
			if(IsRelatedTo.class.isAssignableFrom(clz)){
				typeDefinition.superClasses = retrieveSuperClasses(clz, IsRelatedTo.class, Relation.NAME);
			} else if(ConsistsOf.class.isAssignableFrom(clz)) {
				typeDefinition.superClasses = retrieveSuperClasses(clz, ConsistsOf.class, Relation.NAME);
			} else {
				typeDefinition.superClasses = retrieveSuperClasses(clz, Relation.class, EDGE_CLASS_NAME);
			}
			
			java.lang.reflect.Type[] typeParameters = clz.getTypeParameters();
			Class<?> sourceClass = getGenericClass(typeParameters[0]);
			Class<?> targetClass = getGenericClass(typeParameters[1]);
			
			typeDefinition.sourceType = getStaticStringFieldByName(sourceClass, NAME, sourceClass.getSimpleName());
			typeDefinition.targetType = getStaticStringFieldByName(targetClass, NAME, targetClass.getSimpleName());
			
		} else if(Embedded.class.isAssignableFrom(clz)){
			typeDefinition.superClasses = retrieveSuperClasses(clz, Embedded.class, clz == Embedded.class ? null : Embedded.NAME);
		} else {
			throw new RuntimeException("Serialization required");
		}
		
		if(!Resource.class.isAssignableFrom(clz)){
			typeDefinition.properties = retrieveListOfProperties(clz);
		}
		
		logger.trace("{} : {} ", clz, typeDefinition);
		return typeDefinition;
	}
	
	private static Set<Property> retrieveListOfProperties(Class<?> type){
		Set<Property> properties = new HashSet<>();
		for (Method m : type.getDeclaredMethods()){
			m.setAccessible(true);
			if(m.isAnnotationPresent(ISProperty.class)){
				ISProperty propAnnotation = m.getAnnotation(ISProperty.class);
				Property prop = getProperty(propAnnotation, m);
				properties.add(prop);
				logger.trace("Property {} retrieved in type {} ", prop, type.getSimpleName());
			}  

		}
		return properties;
	}

	private static String getPropertyNameFromMethodName(Method method){
		String name = method.getName();
		if(name.startsWith("get")){
			name = name.replace("get", "");

		}
		if(name.startsWith("is")){
			name = name.replace("is", "");
		}
		
		if(name.length() > 0){
			name = Character.toLowerCase(name.charAt(0)) + (name.length() > 1 ? name.substring(1) : "");
		}
		
		return name;
	}
	
	private static Property getProperty(ISProperty propertyAnnotation, Method method){
		String name = propertyAnnotation.name().isEmpty()?getPropertyNameFromMethodName(method):propertyAnnotation.name();
		Property property = new Property();
		property.name = name;
		property.description = propertyAnnotation.description();
		property.mandatory= propertyAnnotation.mandatory();
		property.notnull = !propertyAnnotation.nullable();
		property.readonly = propertyAnnotation.readonly();
		if(propertyAnnotation.max()>0) property.max = propertyAnnotation.max();
		if(propertyAnnotation.max()>=propertyAnnotation.min() && propertyAnnotation.min()>0) property.min = propertyAnnotation.min();
		if(!propertyAnnotation.regexpr().isEmpty()) property.regexp = propertyAnnotation.regexpr();
		
		logger.trace("Looking for property type {}", method.getReturnType());
		Class<?> type = method.getReturnType();
		property.type = OType.EMBEDDED.getIntValue();
		
		if(Embedded.class.isAssignableFrom(type)){
			if(type != Embedded.class){
				property.linkedClass = getStaticStringFieldByName(type, NAME, type.getSimpleName());
			}
		}else if (Type.getTypeByClass(type)!=null) {
			property.type = Type.getTypeByClass(type).getIntValue();
			if(property.type > 9 && property.type <= 12){
				java.lang.reflect.Type genericReturnType = method.getGenericReturnType();
				logger.trace("Generic Return Type {} for method {}", genericReturnType, method);
				
				java.lang.reflect.Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
				
				java.lang.reflect.Type genericType = null;
				for(java.lang.reflect.Type t : actualTypeArguments){
					logger.trace("Generic Return Type {} for method {} - Actual Type Argument : {}", genericReturnType, method, t);
					genericType = t;
				}
				
				Class<?> genericClass = (Class<?>) genericType;
				OType linkedOType = Type.getTypeByClass(genericClass);
				if(linkedOType!=null){
					property.linkedType = linkedOType.getIntValue();
				}else{
					property.linkedClass = getStaticStringFieldByName(genericClass, NAME, genericClass.getSimpleName());
				}
				
			}
			
			if((property.regexp==null || property.regexp.compareTo("")==0 )&& property.type==OType.STRING.getIntValue()){
				if(Enum.class.isAssignableFrom(type)){
					Object[] constants = type.getEnumConstants();
					StringBuilder stringBuilder = new StringBuilder("^(");
					for(int i=0; i<constants.length; i++){
						stringBuilder.append(constants[i].toString());
						if(i<constants.length-1){
							stringBuilder.append("|");
						}
					}
					stringBuilder.append(")$");
					property.regexp = stringBuilder.toString();
				}
				if(UUID.class.isAssignableFrom(type)){
					property.regexp = UUID_PATTERN;
				}
				if(URI.class.isAssignableFrom(type)){
					property.regexp = URI_PATTERN;
				}
				if(URL.class.isAssignableFrom(type)){
					property.regexp = URL_PATTERN;
				}
			}
			
			if(property.regexp!=null && property.regexp.compareTo("")==0){
				property.regexp = null;
			}
			
		} else {
			throw new RuntimeException("Type " + type.getSimpleName() + " not reconized");
		}
		
		return property;
	}

	private static Set<String> retrieveSuperClasses(Class<?> type, Class<?> baseClass, String topSuperClass){
		Set<String> interfaceList = new HashSet<>();
		
		if(type==baseClass){
			interfaceList.add(topSuperClass);
			return interfaceList;
		}
		
		Class<?>[] interfaces = type.getInterfaces();
		
		for (Class<?> interfaceClass : interfaces) {
			
			if(!baseClass.isAssignableFrom(interfaceClass)){
				continue;
			}
			
			interfaceList.add(getStaticStringFieldByName(interfaceClass, NAME, interfaceClass.getSimpleName()));
		}

		return interfaceList;
	}

	@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
	@JsonIgnoreProperties(ignoreUnknown=true)
	public static class TypeDefinition {

		protected String name;
		protected String description;
		@JsonProperty(value="abstract")
		protected boolean abstractType;
		protected Set<String> superClasses;
		protected Set<Property> properties;
		
		@JsonInclude(JsonInclude.Include.NON_NULL) 
		protected String sourceType;
		@JsonInclude(JsonInclude.Include.NON_NULL) 
		protected String targetType;
		
		@Override
		public String toString() {
			return "TypeDefinition ["
					+ "name=" + name
					+ (sourceType==null ? "" : "(" + sourceType + "->" + targetType + ")")
					+ ", description=" + description
					+ ", abstract=" + abstractType
					+ ", superClasses=" + superClasses 
					+ ", properties=" + properties
					+ "]";
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public boolean isAbstract() {
			return abstractType;
		}

		public Set<String> getSuperClasses() {
			return superClasses;
		}

		public Set<Property> getProperties() {
			return properties;
		}

		public String getSourceType() {
			return sourceType;
		}
		
		public String getTargetType() {
			return targetType;
		}
		
	}

	@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
	@JsonIgnoreProperties(ignoreUnknown=true)
	public static class Property {

		private String name= "";
		private String description= "";
		private boolean mandatory = false;
		private boolean readonly = false;
		private boolean notnull = false;
		private Integer max= null;
		private Integer min= null;
		private String regexp= null;
		private Integer linkedType = null;
		private String linkedClass = null;
		private Integer type=null;

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public boolean isMandatory() {
			return mandatory;
		}

		public boolean isReadonly() {
			return readonly;
		}

		public boolean isNotnull() {
			return notnull;
		}

		public Integer getMax() {
			return max;
		}

		public Integer getMin() {
			return min;
		}

		public String getRegexp() {
			return regexp;
		}
		
		public Integer getLinkedType() {
			return linkedType;
		}
		
		public String getLinkedClass() {
			return linkedClass;
		}

		public Integer getType() {
			return type;
		}
		
		@JsonIgnore
		public String getTypeStringValue() {
			if(type==null){
				return null;
			}
			return OType.values()[type].getStringValue();
		}
		
		@Override
		public String toString() {
			return "Property [name=" + name + ", description=" + description
					+ ", mandatory=" + mandatory + ", readonly=" + readonly
					+ ", notnull=" + notnull + ", max=" + max + ", min="
					+ min + ", regexpr=" + regexp + ", type = " + type
					+ " (" + getTypeStringValue() + "), linkedType = " + linkedType + ", linkedClass = "
					+ linkedClass + "]";
		}


	}

}
