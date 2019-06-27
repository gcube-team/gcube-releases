package gr.cite.gaap.servicelayer;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gr.cite.gaap.utilities.TypeUtils;
import gr.cite.geoanalytics.common.ShapeAttributeDataType;

public class Toolbox {
	
	
	private static final String NoMappingKey = "\t\t\t__NoVal__\t\t\t";
	private static final String NoValueKey = "";
	
	private static final  Logger log = LoggerFactory.getLogger(Toolbox.class);
	
	private Map<String, ShapeAttributeDataType> featureTypes;
	
	public static final String DefaultCharset = "UTF-8";
	public static final boolean DefaultForceLonLat = false;
	
	public Toolbox(){
		this.featureTypes = new HashMap<String, ShapeAttributeDataType>();
	}
	
	public Toolbox(Map<String, ShapeAttributeDataType> featureTypes) { 
		this.featureTypes = featureTypes;
	}
	
	
	public void addFeatureTypes(Map<String, ShapeAttributeDataType> types)
	{
		featureTypes.putAll(types);
	}
	
	public void clearFeatureTypes()
	{
		featureTypes.clear();
	}
	

	private String inferType(AttributeType t, Object value, boolean inferTypes) throws Exception
	{
		if(t.getBinding().getName().equals(String.class.getName()))
		{
			String val = (String)value;
			if(featureTypes.containsKey(t.getName().toString()))
			{
				if(featureTypes.get(t.getName().toString()) != ShapeAttributeDataType.STRING)
				{
					switch(featureTypes.get(t.getName().toString()))
					{
					case SHORT:
						Short.parseShort(val);
						return "short";
					case INTEGER:
						Integer.parseInt(val);
						return "integer";
					case LONG:
						Long.parseLong(val);
						return "long";
					case FLOAT:
						Float.parseFloat(val);
						return "float";
					case DOUBLE:
						Double.parseDouble(val);
						return "double";
					case DATE:
						return "date";
					case STRING:
					case LONGSTRING:
						return "string";
					}
				}else return "string";
			}else if(inferTypes)
			{
				if(TypeUtils.tryParseShort(val) != null) return "short";
				else if(TypeUtils.tryParseInteger(val) != null) return "integer";
				else if(TypeUtils.tryParseLong(val) != null) return "long";
				else if(TypeUtils.tryParseFloat(val) != null) return "float";
				else if(TypeUtils.tryParseDouble(val) != null) return "double";
				else if(TypeUtils.tryParseDate(val, "MMM dd yyyy HH:mm:ss") != null) return "date";
				return "string";
			}else return "string";
		}else if(t.getBinding().getName().equals(Integer.class.getName()))
		{
			Integer val = (Integer)value;
			if(featureTypes.containsKey(t.getName().toString()))
			{
				if(featureTypes.get(t.getName().toString()) != ShapeAttributeDataType.INTEGER)
				{
					switch(featureTypes.get(t.getName().toString()))
					{
					case FLOAT:
						return "float";
					case DOUBLE:
						return "double";
					case LONG:
						return "long";
					case SHORT:
						return "integer"; //narrowing not allowed
					case STRING:
					case LONGSTRING:
						return "string";
					}
					return "integer"; //integer to date not supported
				}else return "integer";
			}
			return "integer"; //no reason to promote numeric types
		}else if(t.getBinding().getName().equals(Long.class.getName()))
		{
			Long val = (Long)value;
			if(featureTypes.containsKey(t.getName().toString()))
			{
				if(featureTypes.get(t.getName().toString()) != ShapeAttributeDataType.LONG)
				{
					switch(featureTypes.get(t.getName().toString()))
					{
					case STRING:
					case LONGSTRING:
						return "string";
					case FLOAT:
						return "float";
					case DOUBLE:
						return "double";
					case INTEGER:
					case SHORT:
						return "long"; //narrowing not allowed
					}
					return "long"; //long to date not supported
				}
			}
			return "long"; //no reason to promote numeric types
		}else if(t.getBinding().getName().equals(Short.class.getName()))
		{
			Short val = (Short)value;
			if(featureTypes.containsKey(t.getName().toString()))
			{
				if(featureTypes.get(t.getName().toString()) != ShapeAttributeDataType.SHORT)
				{
					switch(featureTypes.get(t.getName().toString()))
					{
					case STRING:
					case LONGSTRING:
						return "string";
					case FLOAT:
						return "float";
					case DOUBLE:
						return "double";
					case INTEGER:
						return "integer";
					case LONG:
						return "long";
					}
					return "short"; //short to date not supported
					}
			}
			return "short"; //no reason to promote numeric types
		}else if(t.getBinding().getName().equals(Float.class.getName()))
		{
			Float val = (Float)value;
			if(featureTypes.containsKey(t.getName().toString()))
			{
				if(featureTypes.get(t.getName().toString()) != ShapeAttributeDataType.FLOAT)
				{
					switch(featureTypes.get(t.getName().toString()))
					{
					case STRING:
					case LONGSTRING:
						return "string";
					case SHORT:
					case INTEGER:
					case LONG:
						return "float";
					case DOUBLE:
						return "double";
					}
					return "float"; //float to date not supported
					}
			}
			return "float"; //no reason to promote numeric types
		}else if(t.getBinding().getName().equals(Double.class.getName()))
		{
			Double val = (Double)value;
			if(featureTypes.containsKey(t.getName().toString()))
			{
				if(featureTypes.get(t.getName().toString()) != ShapeAttributeDataType.DOUBLE)
				{
					switch(featureTypes.get(t.getName().toString()))
					{
					case STRING:
					case LONGSTRING:
						return "string";
					case SHORT:
					case INTEGER:
					case LONG:
						return "double";
					case FLOAT:
						return "double"; //narrowing not allowed
					}
					return "double"; //double to date not supported
					}
			}
			return "double"; //no reason to promote numeric types
		}else if(t.getBinding().getName().equals(Date.class.getName()))
		{
			Date val = (Date)value;
			if(featureTypes.containsKey(t.getName().toString()))
			{
				if(featureTypes.get(t.getName().toString()) != ShapeAttributeDataType.DATE)
				{
					switch(featureTypes.get(t.getName().toString()))
					{
					case STRING:
					case LONGSTRING:
						return "string";
					case SHORT:
					case INTEGER:
					case LONG:
						return "long";
					}
					return "date";
				}
			}
			return "date";
		}
		
		throw new Exception("Unrecognized data type: " + t.getBinding().getName());
	}
	
	private String insertBursaWolfToWKT(String wkt, double[] bursaWolf)
	{
		String[] defs = wkt.split("DATUM\\[");
		if(defs.length != 2)
		{
			log.warn("Could not insert Bursa-Wolf Parameters to CRS WKT");
			return wkt;
		}
		int bracketCount = 1;
		int index = 0;
		int prevClose = -1;
		while(bracketCount != 0)
		{
			int close = defs[1].indexOf(']', index);
			if(prevClose == -1) prevClose = close;
			int open = defs[1].indexOf('[', index);
			if(close == -1)
			{
				log.warn("Invalid wkt");
				return null;
			}
			if(open < close)
			{
				bracketCount++;
				index = open + 1;
			}else
			{
				bracketCount--;
				index = close + 1;
			}
			if(bracketCount != 0) prevClose = close;
		}
		
		StringBuilder formattedBursaWolf = new StringBuilder();
		for(int i=0; i<bursaWolf.length; i++)
		{
			formattedBursaWolf.append(String.format(Locale.US, "%.2f",  bursaWolf[i]));
			if(i != bursaWolf.length-1) formattedBursaWolf.append(", ");
		}
		
		String res = defs[0] + "DATUM[" +
				 defs[1].substring(0, prevClose+1) +
				", TOWGS84[" + formattedBursaWolf.toString() + "]" +
				defs[1].substring(prevClose+1);
		return res;
	}
	
	
	public Map<String, String> analyzeAttributesOfFeatureSource(SimpleFeatureSource featureSource, boolean inferTypes) throws Exception
	{
		SimpleFeatureCollection collection = featureSource.getFeatures();
		SimpleFeatureIterator iterator = collection.features();

    	Map<String, String> attributes = new HashMap<String, String>();
    	
		try {
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();

				List <AttributeType> types = feature.getType().getTypes();
				for(AttributeType t : (List<AttributeType>) types) 
				{
					Object val = feature.getAttribute(t.getName());
					if(val != null)
					{
						String type = inferType(t, val, inferTypes);
						attributes.put(t.getName().toString(),  type); 
					}
				}
			}
			return attributes;
		} 
		finally 
		{
			iterator.close();
		}
	}
	
	public Map<String, String> analyzeAttributesOfShapeFile(String filename, String charset, boolean inferTypes) throws Exception
	{
		Map<String, String> map = new HashMap<String, String>();

		File file = new File(filename);

		map.put("url", file.toURI().toString());
		map.put("charset", charset);
		
		SimpleFeatureSource featureSource = null;
		DataStore dataStore = null;
		try 
		{
			dataStore = DataStoreFinder.getDataStore(map);
			featureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
		} catch (IOException e) 
		{
			log.error("Error while reading shape file", e);
			throw e;
		}
		
		return analyzeAttributesOfFeatureSource(featureSource, inferTypes);
	}
	
	public Set<String> getAttributeValuesFromFeatureSource(SimpleFeatureSource featureSource, String attribute) throws Exception
	{
		SimpleFeatureCollection collection = featureSource.getFeatures();
		SimpleFeatureIterator iterator = collection.features();

		Set<String> values = new HashSet<String>();
		try 
		{
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();

				List <AttributeType> types = feature.getType().getTypes();
				for(AttributeType t : (List<AttributeType>) types) 
				{
					if(t.getName().toString().equals(attribute))
					{
						Object val = feature.getAttribute(t.getName());
						if(val != null)
							values.add(val.toString());
					}
				}
			}
			return values;
		} 
		finally 
		{
			iterator.close();
		}
	}
	
	public Set<String> getAttributeValuesFromShapeFile(String pathname, String charset, String attribute) throws Exception
	{
		Map<String, String> map = new HashMap<String, String>();

		File file = new File(pathname);

		map.put("url", file.toURI().toString());
		map.put("charset", charset);
		
		SimpleFeatureSource featureSource = null;
		DataStore dataStore = null;
		try 
		{
			dataStore = DataStoreFinder.getDataStore(map);
			featureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
		} catch (IOException e) 
		{
			log.error("Error while reading shape file", e);
			throw e;
		}
		
		return getAttributeValuesFromFeatureSource(featureSource, attribute);
	}
	
}

