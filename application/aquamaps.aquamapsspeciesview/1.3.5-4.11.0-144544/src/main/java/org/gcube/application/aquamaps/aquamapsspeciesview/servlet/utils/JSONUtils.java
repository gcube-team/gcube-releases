package org.gcube.application.aquamaps.aquamapsspeciesview.servlet.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Perturbation;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Species;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.PerturbationType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.EnvelopeFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.fields.SpeciesFields;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JSONUtils {

	public static List<Species> JSONToSpecies(String toParse) throws JSONException{
		JSONArray array= new JSONArray(toParse.substring(toParse.indexOf("[")));
		List<Species> toReturn=new ArrayList<Species>();
		for(int i = 0; i<array.length();i++){			
			JSONObject obj= array.getJSONObject(i);
			Species toAdd= new Species(obj.getString(SpeciesOccursumFields.speciesid+""));
			Iterator<String> iterator= obj.keys();
			while(iterator.hasNext()){
				String key=iterator.next();
				if(!key.equalsIgnoreCase(SpeciesOccursumFields.speciesid+"")){
					Field field= new Field();
					field.name(key);
					field.type(FieldType.STRING);
					field.value(obj.getString(key));
					toAdd.addField(field);
				}
			}
			toReturn.add(toAdd);
		}
		return toReturn;
	}

	
	public static List<String> JSONSpeciesIds(String toParse)throws Exception{
		List<String> toReturn=new ArrayList<String>();
		int index = 0;
		String toFind="\""+SpeciesOccursumFields.speciesid+"\":\"";
		while(index<toParse.length()){
			int start=toParse.indexOf(toFind, index);
			int end=toParse.indexOf('"', start+toFind.length());
			if(start>index){
				toReturn.add(toParse.substring(start+toFind.length(), end));
				index=end;
			}
			else index=toParse.length();			
		}
		return toReturn;
	}
	
	
	public static String pertMapToJSON(Map<SpeciesFields,Float> perturbations) throws JSONException{
		JSONArray array=new JSONArray();

		for(Entry<SpeciesFields,Float> entry:perturbations.entrySet()){
			JSONObject obj=new JSONObject();
			obj.put(entry.getKey()+"", entry.getValue());

			array.put(obj);

		}
		return array.toString();
	}

	public static Map<String,Perturbation> JSONtoPert(String JSONString) throws Exception{		
		Map<String, Perturbation> toReturn=new HashMap<String, Perturbation>();
		if(JSONString!=null)
		try{
			JSONArray array=new JSONArray(JSONString.substring(JSONString.indexOf("["),JSONString.lastIndexOf("]")+1));
			for(int i=0;i<array.length();i++){
				JSONObject obj= array.getJSONObject(i);
				String key=(String) obj.keys().next();
				toReturn.put(key, new Perturbation(PerturbationType.ASSIGN,obj.get(key).toString()));
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;}
		return toReturn;
	}
	
	public static String weitghtsToJSON(Map<String,Boolean> weights)throws JSONException{
		JSONArray array=new JSONArray();		
		for(Entry<String,Boolean> entry: weights.entrySet()){
			JSONObject obj=new JSONObject();
			obj.put(entry.getKey(), entry.getValue());
			array.put(obj);
		}
		return array.toString();
	}


	public static Map<EnvelopeFields,Field> JSONtoWeights(String JSONString) throws Exception{		
		Map<EnvelopeFields,Field> weights=new HashMap<EnvelopeFields, Field>();
		if(JSONString!=null)
		try{
			JSONArray array=new JSONArray(JSONString.substring(JSONString.indexOf("["),JSONString.lastIndexOf("]")+1));
			for(int i = 0 ; i<array.length();i++){
				JSONObject obj= array.getJSONObject(i);
				Field f=new Field();
				f.type(FieldType.BOOLEAN);
				String fName=obj.keys().next().toString();
				f.name(fName);
				f.value(obj.get(fName).toString());
				weights.put(EnvelopeFields.valueOf(f.name()),f);
			}
		}catch(Exception e){
			throw e;
//			logger.debug("Invalid or empty weigths selection JSON String");
			}
		return weights;
	}
//
//	
	
	
	
}
