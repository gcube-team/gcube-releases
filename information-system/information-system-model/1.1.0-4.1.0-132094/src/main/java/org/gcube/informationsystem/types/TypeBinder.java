package org.gcube.informationsystem.types;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
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
import com.fasterxml.jackson.databind.ObjectMapper;

public class TypeBinder {

	private static Logger logger = LoggerFactory.getLogger(TypeBinder.class);
	
	private static final String EDGE_CLASS_NAME   = "E";
	private static final String VERTEX_CLASS_NAME = "V";

	public final static String NAME = "NAME";
	public final static String DESCRIPTION = "DESCRIPTION";
	
	public final static String UUID_PATTERN = "^([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}){1}$";
	public final static String URI_PATTERN = "";
	public final static String URL_PATTERN = "";
	
	private static String getStaticStringFieldByName(Class<?> type, String fieldName, String defaultValue){
		Field field;
		try {
			field = type.getDeclaredField(fieldName);
			return (String) field.get(null);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			return defaultValue;
		}
	}
	
	public static String serializeType(Class<?> type) throws Exception{
		TypeDefinition def = createTypeDefinition(type);
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(def);
		return json;
	}

	public static TypeDefinition createTypeDefinition(Class<?> type) {
		TypeDefinition typeDefinition = new TypeDefinition();
		
		typeDefinition.name = getStaticStringFieldByName(type, NAME, type.getSimpleName());
		typeDefinition.description = getStaticStringFieldByName(type, DESCRIPTION, "");
		typeDefinition.abstractType = false;
		
		if(type.isAnnotationPresent(Abstract.class)){
			typeDefinition.abstractType = true;
		}
		
		if(Entity.class.isAssignableFrom(type)) {
			if(Resource.class.isAssignableFrom(type)){
				typeDefinition.superclasses = retrieveSuperClasses(type, Resource.class, Entity.class.getSimpleName());
			}else{
				if(Facet.class.isAssignableFrom(type)){
					typeDefinition.superclasses = retrieveSuperClasses(type, Facet.class, Entity.class.getSimpleName());
				} else {
					typeDefinition.superclasses = retrieveSuperClasses(type, Entity.class, VERTEX_CLASS_NAME);
				}
			}
		} else if(Relation.class.isAssignableFrom(type)){
			if(IsRelatedTo.class.isAssignableFrom(type)){
				typeDefinition.superclasses = retrieveSuperClasses(type, IsRelatedTo.class, Relation.class.getSimpleName());
			} else if(ConsistsOf.class.isAssignableFrom(type)) {
				typeDefinition.superclasses = retrieveSuperClasses(type, ConsistsOf.class, Relation.class.getSimpleName());
			} else {
				typeDefinition.superclasses = retrieveSuperClasses(type, Relation.class, EDGE_CLASS_NAME);
			}
		} else if(Embedded.class.isAssignableFrom(type)){
			typeDefinition.superclasses = retrieveSuperClasses(type, Embedded.class, null);
		} else {
			throw new RuntimeException("Serialization required");
		}
		
		if(!Resource.class.isAssignableFrom(type)){
			typeDefinition.properties = retrieveListOfProperties(type);
		}
		
		logger.trace("{} TypeDefinition {} ", type, typeDefinition);
		return typeDefinition;
	}
	
	protected static Set<Property> retrieveListOfProperties(Class<?> type){
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

	protected static String getPropertyNameFromMethodName(Method method){
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
	
	protected static Property getProperty(ISProperty propertyAnnotation, Method method){
		String name = propertyAnnotation.name().isEmpty()?getPropertyNameFromMethodName(method):propertyAnnotation.name();
		Property property = new Property();
		property.name = name;
		property.description = propertyAnnotation.description();
		property.mandatory= propertyAnnotation.mandatory();
		property.notnull = !propertyAnnotation.nullable();
		property.readonly = propertyAnnotation.readonly();
		if(propertyAnnotation.max()>0) property.max = propertyAnnotation.max();
		if(propertyAnnotation.max()>=propertyAnnotation.min() && propertyAnnotation.min()>0) property.min = propertyAnnotation.min();
		if(!propertyAnnotation.regexpr().isEmpty()) property.regexpr = propertyAnnotation.regexpr();
		
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
			
			if((property.regexpr==null || property.regexpr.compareTo("")==0 )&& property.type==OType.STRING.getIntValue()){
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
					property.regexpr = stringBuilder.toString();
				}
				if(UUID.class.isAssignableFrom(type)){
					property.regexpr = UUID_PATTERN;
				}
				if(URI.class.isAssignableFrom(type)){
					property.regexpr = URI_PATTERN;
				}
				if(URL.class.isAssignableFrom(type)){
					property.regexpr = URL_PATTERN;
				}
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
			if(interfaceClass==Embedded.class){
				continue;
			}
			
			if(!baseClass.isAssignableFrom(interfaceClass)){
				continue;
			}
			
			interfaceList.add(getStaticStringFieldByName(interfaceClass, NAME, interfaceClass.getSimpleName()));
		}

		return interfaceList;
	}


	@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
	public static class TypeDefinition{

		protected String name;
		protected String description;
		protected boolean abstractType;
		protected Set<String> superclasses;
		protected Set<Property> properties;

		@Override
		public String toString() {
			return "TypeDefinition [name=" + name +", description="
					+ description + ", superclasses="
					+ superclasses + ", properties=" + properties + "]";
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public boolean isAbstractType() {
			return abstractType;
		}

		public Set<String> getSuperclasses() {
			return superclasses;
		}

		public Set<Property> getProperties() {
			return properties;
		}

	}

	@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
	public static class Property {

		private String name= "";
		private String description= "";
		private boolean mandatory = false;
		private boolean readonly = false;
		private boolean notnull = false;
		private Integer max= null;
		private Integer min= null;
		private String regexpr= null;
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

		public String getRegexpr() {
			return regexpr;
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

		@Override
		public String toString() {
			return "Property [name=" + name + ", description=" + description
					+ ", mandatory=" + mandatory + ", readonly=" + readonly
					+ ", notnull=" + notnull + ", max=" + max + ", min="
					+ min + ", regexpr=" + regexpr + ", type = " + type
					+ ", linkedType = " + linkedType + ", linkedClass = "
					+ linkedClass + "]";
		}


	}


}
