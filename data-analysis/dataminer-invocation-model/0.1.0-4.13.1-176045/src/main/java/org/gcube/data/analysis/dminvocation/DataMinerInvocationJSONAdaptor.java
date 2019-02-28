/**
 *
 */

package org.gcube.data.analysis.dminvocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.gcube.data.analysis.dminvocation.model.DataMinerInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;



/**
 * The Class DataMinerInvocationJSONAdaptor.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Dec 20, 2018
 */
public class DataMinerInvocationJSONAdaptor implements JsonDeserializer<DataMinerInvocation>, JsonSerializer<DataMinerInvocation>{

	List<String> requiredFields = new ArrayList<String>();

	private static Logger log = LoggerFactory.getLogger(DataMinerInvocationJSONAdaptor.class);

	/**
	 * Register required field.
	 *
	 * @param fieldName the field name
	 */
	void registerRequiredField(String fieldName) {

		requiredFields.add(fieldName);
	}

	/* (non-Javadoc)
	 * @see com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement, java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
	 */
	@Override
	public DataMinerInvocation deserialize(
		JsonElement json, Type arg1, JsonDeserializationContext arg2)
		throws JsonParseException {

		JsonObject jsonObject = (JsonObject) json;
		for (String fieldName : requiredFields) {
			if (jsonObject.get(fieldName) == null) {
				throw new JsonParseException("Required Field Not Found: " +fieldName);
			}
		}
		return new Gson().fromJson(json, DataMinerInvocation.class);
	}

	/* (non-Javadoc)
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(DataMinerInvocation dmi, Type arg1, JsonSerializationContext arg2) throws JsonParseException{

		try{
			List<String> errors = new ArrayList<String>();
			checkRequiredFiels(dmi, errors);
			if(errors.isEmpty()){
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				return gson.toJsonTree(dmi);
			}

			throw new JsonParseException(errors.toString());

		}catch(Exception e){
			log.error("Error on check required fields: ", e);
			throw new JsonParseException("Error on validating the instance: "+dmi, e);
		}
	}


	private static void checkRequiredFiels(Object theInstance, List<String> errors) throws IllegalArgumentException, IllegalAccessException{

		log.info("Checking instance: "+theInstance+" of Class: "+theInstance.getClass());

		for(Field field : theInstance.getClass().getDeclaredFields()){
			  Class<?> type = field.getType();
			  String name = field.getName();
			  Annotation[] annotations = field.getDeclaredAnnotations();
			  log.debug("Field: "+name);
			  for (Annotation annotation : annotations) {
				  if(annotation instanceof XmlElement){
					 field.setAccessible(true);
					 XmlElement xmlEl = (XmlElement) annotation;
					 if(xmlEl.required()){
						 Object value = field.get(theInstance);
						 if(value==null){
							 String error = "Required field '"+xmlEl.name() + "' of "+type+" is null";
							 log.error("Required field  '"+xmlEl.name() + "' is null");
							 errors.add(error);
						 }else if (String.class.equals(type) && ((String) value).isEmpty()){
							 String error = "Required field  '"+xmlEl.name() + "'  of "+type+" is empty";
							 log.error("Required field '"+xmlEl.name() + "' is null");
							 errors.add(error);
						 }else if(!type.isPrimitive() && !String.class.equals(type)){
							 log.debug("The field "+xmlEl.name()+ " is not a primitive or a String\n\n");
							 checkRequiredFiels(value, errors);
						 }
					 }
				  }else
					  log.trace("Another annotation (not instance of XmlElement): "+annotation);
			}
		}
	}
}
