/**
 *
 */
package org.gcube.common.workspacetaskexecutor.util;

import java.io.IOException;
import java.util.List;

import org.gcube.common.workspacetaskexecutor.util.JsonUtil.ActionResponse.Action;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


/**
 * The Class JsonUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 2, 2018
 */
public class JsonUtil {

	private ObjectMapper objectMapper = new ObjectMapper();
	private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);



	/**
	 * Instantiates a new json util.
	 */
	public JsonUtil() {
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	}


	/**
	 * To json.
	 *
	 * @param <T> the generic type
	 * @param obj the obj
	 * @return the JSON object
	 * @throws JsonProcessingException the json processing exception
	 * @throws JSONException the JSON exception
	 */
	public <T> JSONObject toJSON(T obj) throws JsonProcessingException, JSONException{

		// Convert object to JSON string
		String json = objectMapper.writeValueAsString(obj);
		return new JSONObject(json);
	}


	/**
	 * Read object.
	 *
	 * @param <T> the generic type
	 * @param json the json
	 * @param obj the obj
	 * @return the t
	 * @throws JsonParseException the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public <T> T readObject(String json, Class<T> obj) throws JsonParseException, JsonMappingException, IOException{
		// Convert JSON string from file to Object
		return objectMapper.readValue(json, obj);
	}


	/**
	 * Read list.
	 *
	 * @param <T> the generic type
	 * @param arrayToJson the array to json
	 * @param mapType the map type
	 * @return the list
	 * @throws JsonParseException the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public <T> List<T> readList(String arrayToJson, TypeReference<List<T>> mapType) throws JsonParseException, JsonMappingException, IOException{
		// Convert JSON string from file to Object
		return objectMapper.readValue(arrayToJson, mapType);
	}



	/**
	 * To json array.
	 *
	 * @param <T> the generic type
	 * @param obj the obj
	 * @return the JSON array
	 * @throws JsonProcessingException the json processing exception
	 * @throws JSONException the JSON exception
	 */
	public <T> JSONArray toJSONArray(List<T> obj) throws JsonProcessingException, JSONException{

		// Convert List<T> to JSON string
		String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);

		return new JSONArray(json);
	}


	/**
	 * Removes the json object with the (key,value) passed in input.
	 *
	 * @param jsonArray the json array
	 * @param key the key
	 * @param value the value
	 * @return the action response
	 * @throws JSONException the JSON exception
	 */
	public ActionResponse removeJsonObject(JSONArray jsonArray, String key, String value) throws JSONException{

		ActionResponse resp = new ActionResponse();
		logger.debug("Removing json object with ("+key+","+value+")");

		if (jsonArray != null) {
			JSONArray newArray = new JSONArray(); //THE NEW JSON ARRAY
			for (int i=0; i < jsonArray.length(); i++){
			    JSONObject itemArr;
				try {

					itemArr = (JSONObject) jsonArray.get(i);
					//IF JSON OBJECT DOES NOT CONTAIN THE PROPERTY
					//RETURNING IT IN THE NEW JSON ARRAY
					if(!itemArr.getString(key).equals(value)){
						newArray.put(itemArr);
					}else{
						resp.setActionPerformed(Action.REMOVE);
					}
				}
				catch (JSONException e) {
					logger.error("JSONException: ", e.getMessage());
					throw new JSONException(e.getMessage());
				}
			}
			resp.setNewArray(newArray);
			return resp;
		}

		return null;
	}


	/**
	 * Update the input json object.
	 * If the (key,value) is found in the jsonArray, it updates the object. Otherwise the jsonObject is added to jsonArray
	 *
	 * @param jsonArray the json array
	 * @param jsonObject the json object
	 * @param key the key
	 * @param value the value
	 * @return the JSON array
	 * @throws JSONException
	 */
	public ActionResponse updateJsonObject(JSONArray jsonArray, JSONObject jsonObject, String key, String value) throws JSONException{

		ActionResponse resp = new ActionResponse();

		if (jsonArray != null) {
			boolean found = false;
			JSONArray newArray = new JSONArray(); //THE NEW JSON ARRAY

			for (int i=0; i < jsonArray.length(); i++){
			    JSONObject itemArr;
				try {

					itemArr = (JSONObject) jsonArray.get(i);
					//IF THE I-MO JSON OBJECT CONTAINS THE INPUT (KEY,VALUE)
					//UPDATING IT WITH THE NEW INPUT OBJECT
					if(itemArr.getString(key).equals(value)){
						newArray.put(jsonObject);
						found = true;
						resp.setActionPerformed(Action.UPDATE);
						logger.debug("Updated json with key: "+key+" and value: "+value);
					}else
						newArray.put(itemArr);
				}
				catch (JSONException e) {
					logger.error("JSONException: ", e);
					throw new JSONException(e.getMessage());
				}
			}

			if(!found){
				newArray.put(jsonObject);
				logger.debug("Added json with key: "+key+" and value: "+value);
				resp.setActionPerformed(Action.ADD);
			}

			resp.setNewArray(newArray);
			return resp;
		}

		return null;
	}

	public static class ActionResponse{
		public static enum Action {ADD, REMOVE, UPDATE}
		JSONArray newArray = null;
		Action actionPerformed;

		/**
		 *
		 */
		public ActionResponse() {

			// TODO Auto-generated constructor stub
		}
		/**
		 * @param newArray
		 * @param actionPerformed
		 */
		public ActionResponse(JSONArray newArray, Action actionPerformed) {

			super();
			this.newArray = newArray;
			this.actionPerformed = actionPerformed;
		}

		/**
		 * @return the newArray
		 */
		public JSONArray getNewArray() {

			return newArray;
		}

		/**
		 * @return the actionPerformed
		 */
		public Action getActionPerformed() {

			return actionPerformed;
		}

		/**
		 * @param newArray the newArray to set
		 */
		public void setNewArray(JSONArray newArray) {

			this.newArray = newArray;
		}

		/**
		 * @param actionPerformed the actionPerformed to set
		 */
		public void setActionPerformed(Action actionPerformed) {

			this.actionPerformed = actionPerformed;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {

			StringBuilder builder = new StringBuilder();
			builder.append("ActionResponse [newArray=");
			builder.append(newArray);
			builder.append(", actionPerformed=");
			builder.append(actionPerformed);
			builder.append("]");
			return builder.toString();
		}




	}



}
