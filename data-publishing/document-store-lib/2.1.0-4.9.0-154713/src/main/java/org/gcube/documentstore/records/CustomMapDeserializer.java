package org.gcube.documentstore.records;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class CustomMapDeserializer extends StdDeserializer<Map<String, Serializable>>{

	private static final long serialVersionUID = 1L;

	protected CustomMapDeserializer() {
		super(Map.class);
	}

	@Override
	public Map<String, Serializable> deserialize(JsonParser jp,
			DeserializationContext ctxt) throws IOException,
			JsonProcessingException {
		JsonToken currentToken = null;
		Map<String, Serializable> toRetunMap = new HashMap<String, Serializable>();
		Stack<Map<String, Serializable>> mapsStack = new Stack<Map<String,Serializable>>();
		while ((currentToken = jp.nextValue()) != null) {
			switch (currentToken) {
			case END_ARRAY:
				break;
			case START_ARRAY:
				break;
			case START_OBJECT:
				mapsStack.push(new HashMap<String, Serializable>());
				break;
			case FIELD_NAME:
				if(mapsStack.isEmpty())
					toRetunMap.put(jp.getCurrentName(), jp.getText());
				else mapsStack.peek().put(jp.getCurrentName(), jp.getText());
			break;
			case VALUE_STRING:
				if(mapsStack.isEmpty())
					toRetunMap.put(jp.getCurrentName(), jp.getText());
				else mapsStack.peek().put(jp.getCurrentName(), jp.getText());
			break;
			case VALUE_NUMBER_FLOAT:
				if(mapsStack.isEmpty())
					toRetunMap.put(jp.getCurrentName(), jp.getText());
				else mapsStack.peek().put(jp.getCurrentName(), jp.getText());
			break;
			case VALUE_NUMBER_INT:
				if(mapsStack.isEmpty())
					toRetunMap.put(jp.getCurrentName(), jp.getText());
				else mapsStack.peek().put(jp.getCurrentName(), jp.getText());
			break;
			case VALUE_FALSE:
				if(mapsStack.isEmpty())
					toRetunMap.put(jp.getCurrentName(), false);
				else mapsStack.peek().put(jp.getCurrentName(), false);
			break;
			case VALUE_TRUE:
				if(mapsStack.isEmpty())
					toRetunMap.put(jp.getCurrentName(), true);
				else mapsStack.peek().put(jp.getCurrentName(), true);
			break;
			case END_OBJECT:	
				if (mapsStack.isEmpty()) break;
				if (mapsStack.size()==1)
					toRetunMap.put(jp.getCurrentName(), (Serializable)mapsStack.pop());
				else{
					Map<String, Serializable> tmpMap =  mapsStack.pop();
					mapsStack.peek().put(jp.getCurrentName(), (Serializable)tmpMap);
				}
				break;
			default:
				break;
			}
		}
		
		return toRetunMap;
	}

}