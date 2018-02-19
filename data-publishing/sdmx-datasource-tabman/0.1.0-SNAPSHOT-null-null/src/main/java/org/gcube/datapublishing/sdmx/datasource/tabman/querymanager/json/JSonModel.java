/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.json;


import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ciro
 */
public abstract class JSonModel {
    	protected Map<String, JSonModel> dataMap;
	protected Map<String, List<Object>> arrayDataMap;
	protected Map<String, String> mainMap;
	private Logger logger;
	
	
	public enum KeyType {
		
				STRING (0b0001),
				STRING_ARRAY (0b0010),
				OBJECT (0b0100),
				OBJECT_ARRAY (0b1000);
		
				private int type;
				
		 KeyType (int type)
		{
					this.type = type;
		}
		
		 public int getValue ()
		 {
			 return this.type;
		 }
	}
	
	
	public JSonModel() 
	{
		this.logger = LoggerFactory.getLogger(JSonModel.class);
		this.dataMap = new HashMap<>();
		this.arrayDataMap= new HashMap<>();
		this.mainMap = new HashMap<>();
	}
	

	protected abstract int checkKey (String key);
	
	protected abstract JSonModel generateInternalObject (String key);
	
	protected abstract JSonModel generateInternalArrayObject (String key, int element);

	public void fromJson (byte [] json)  throws Exception
	{
        JsonFactory f = new JsonFactory();
        JsonParser jp = f.createJsonParser(json);
        jp.nextToken();
        fromJson(jp);
	}
	
	protected void fromJson (JsonParser jp)  throws Exception
	{

        JsonToken tolkein = jp.getCurrentToken();// START_OBJECT

        
        while ((tolkein = jp.nextToken()) != JsonToken.END_OBJECT) 
        {
            String key = jp.getCurrentName();
            tolkein = jp.nextToken();
            logger.debug("Parsing key "+key);
            int type = checkKey(key);
            logger.debug("Key type "+Integer.toBinaryString(type));
            
            if (tolkein == JsonToken.START_OBJECT)
            {
            	if (keyTypeComparison(type, KeyType.OBJECT.getValue()))
            	{
            		this.logger.debug("Valid object key "+key);
                	JSonModel internalModel = this.generateInternalObject(key);
                	internalModel.fromJson(jp);
                	this.dataMap.put(key, internalModel);
            	}

            }
            else if (tolkein == JsonToken.START_ARRAY)
            {

            	logger.debug("Array");
            	List<Object> modelArray = new ArrayList<> ();
            	int arrayElement = 0;
            	
            	while ((tolkein = jp.nextToken())!= JsonToken.END_ARRAY)
            	{
	        		//if (keyTypeComparison(type, KeyType.OBJECT_ARRAY.getValue()) && (tolkein == JsonToken.START_OBJECT))
	        		if (keyTypeComparison(type, KeyType.OBJECT_ARRAY.getValue()))
	        		
	        		{
	        			logger.debug("Array of objects");
	         			this.logger.debug("Valid array key "+key);
                    	JSonModel internalModel = generateInternalArrayObject(key,arrayElement);
                    	internalModel.fromJson(jp);
                    	modelArray.add(internalModel);
                    	arrayElement ++;
            		}
 
            		else if (keyTypeComparison(type, KeyType.STRING_ARRAY.getValue()))
            		{
            			logger.debug("Array of strings");
	        			this.logger.debug("Valid array key "+key);
	        			String value = jp.getText();
	        			this.logger.debug("Value "+value);
	        			modelArray.add(value);
	                	arrayElement ++;
           
            		}

                	if (!modelArray.isEmpty())this.arrayDataMap.put(key, modelArray);
            	}

            }
            else if (keyTypeComparison(type, KeyType.STRING.getValue()))
            {
            	this.logger.debug("Valid key "+key);
                String value = jp.getText();
                this.mainMap.put(key, value);
            }
            else this.logger.debug("Invalid key "+key);
        }
        
	}
	

	private boolean keyTypeComparison (int keyType, int keyMask)
	{
		return (keyType & keyMask) == keyMask;
	}
	
	
	public String toJson() throws Exception {
	
        StringWriter outBuffer = new StringWriter();
        JsonFactory f = new JsonFactory();
        JsonGenerator g = f.createJsonGenerator(outBuffer);
        g.useDefaultPrettyPrinter();
        g.writeStartObject();
		toJson(g);
        g.writeEndObject();
        g.flush();
        return outBuffer.toString();
	}
	
	protected void toJson(JsonGenerator g) throws Exception 
	{


		Iterator<String> mapKeys = this.mainMap.keySet().iterator();
		
		while (mapKeys.hasNext())
		{
			String key = mapKeys.next();
			String value = this.mainMap.get(key);
			g.writeStringField(key, value);
		}
        
		Iterator<String> dataMapKeys = this.dataMap.keySet().iterator();
		
		while (dataMapKeys.hasNext())
		{
			String key = dataMapKeys.next();
			JSonModel value = this.dataMap.get(key);
			g.writeObjectFieldStart(key);
			value.toJson(g);
			g.writeEndObject();
		}
		

		Iterator<String> dataMapArrayKeys = this.arrayDataMap.keySet().iterator();
		
		
		while (dataMapArrayKeys.hasNext())
		{
			String key = dataMapArrayKeys.next();
			List<Object> value = this.arrayDataMap.get(key);
		
			g.writeArrayFieldStart(key);
			
			logger.debug("Array element key "+key);
			
			for (Object model : value)
			{
				if (model instanceof JSonModel)
				{
					logger.debug("Object element");
					g.writeStartObject();
					((JSonModel) model).toJson(g);
					g.writeEndObject();
				}
				else if (model instanceof String)
				{
					logger.debug("String element");
					g.writeString((String)model); 
				}
				else logger.debug("Invalid element "+model.getClass().toString());
				

			}
			g.writeEndArray();
		}

    }
	
	
	public String toString ()
	{
		try {
			return this.toJson();
		} catch (Exception e) {
			logger.error("problem in converting data into JSon string "+e);
			return "";
		}
	}
}
