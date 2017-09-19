//package gr.cite.gaap.servicelayer;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Set;
//import java.util.UUID;
//
//import javax.inject.Inject;
//
//import org.geotools.data.DataStore;
//import org.geotools.data.DataStoreFinder;
//import org.geotools.data.simple.SimpleFeatureCollection;
//import org.geotools.data.simple.SimpleFeatureIterator;
//import org.geotools.data.simple.SimpleFeatureSource;
//import org.geotools.geometry.jts.JTS;
//import org.geotools.referencing.CRS;
//import org.opengis.feature.simple.SimpleFeature;
//import org.opengis.feature.simple.SimpleFeatureType;
//import org.opengis.feature.type.AttributeType;
//import org.opengis.referencing.crs.CoordinateReferenceSystem;
//import org.opengis.referencing.operation.MathTransform;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.vividsolutions.jts.geom.Envelope;
//import com.vividsolutions.jts.geom.Geometry;
//
//import gr.cite.gaap.datatransferobjects.AttributeInfo;
//import gr.cite.gaap.datatransferobjects.ShapeImportInfo;
//import gr.cite.gaap.datatransferobjects.ShapeImportInstance;
//import gr.cite.gaap.utilities.HtmlUtils;
//import gr.cite.gaap.utilities.TypeUtils;
//import gr.cite.geoanalytics.common.ShapeAttributeDataType;
//import gr.cite.geoanalytics.dataaccess.dao.UUIDGenerator;
//import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
//import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
//import gr.cite.geoanalytics.dataaccess.entities.layer.dao.LayerDao;
//import gr.cite.geoanalytics.dataaccess.entities.layer.dao.TaxonomyLayerDao;
//import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
//import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDao;
//import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeImport;
//import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeImportDao;
//import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.mapping.AttributeMappingConfig;
//import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;
//
//@Service
//public class ShapeImportManager {
//	
//	private PrincipalDao principalDao;
//	private GeocodeManager taxonomyManager;
//	private ShapeManager shapeManager;
//	private ConfigurationManager configurationManager;
//	
//	private ShapeImportDao shapeImportDao;
////	private TaxonomyTermDao taxonomyTermDao;
//	private LayerDao layerDao;
//	private TaxonomyLayerDao taxonomyLayerDao;
//	
//	private static final String NoMappingKey = "\t\t\t__NoVal__\t\t\t";
//	private static final String NoValueKey = "";
//	
//	private static final  Logger log = LoggerFactory.getLogger(ShapeImportManager.class);
//	
//	private boolean inferTypes = false;
//	private Map<String, ShapeAttributeDataType> featureTypes = new HashMap<String, ShapeAttributeDataType>();
//	
//	public static final String DefaultCharset = "UTF-8";
//	public static final boolean DefaultForceLonLat = false;
//	
//	public ShapeImportManager() { }
//	
//	@Inject
//	public ShapeImportManager(PrincipalDao principalDao, GeocodeManager taxonomyManager, ConfigurationManager configManager)
//	{
//		this.principalDao = principalDao;
//		this.taxonomyManager = taxonomyManager;
//		this.configurationManager = configManager;
//	}
//	
//	@Inject
//	public void setShapeManager(ShapeManager shapeManager) {
//		this.shapeManager = shapeManager;
//	}
//	
//	@Inject
//	public void setShapeImportDao(ShapeImportDao shapeImportDao) {
//		this.shapeImportDao = shapeImportDao;
//	}
//	
//	@Inject
//	public void setLayerDao(LayerDao layerDao) {
//		this.layerDao = layerDao;
//	}
//	
//	
////	@Inject
////	public void setTaxonomyTermDao(TaxonomyTermDao taxonomyTermDao) {
////		this.taxonomyTermDao = taxonomyTermDao;
////	}
//	
//	public void setTypeInference(boolean val)
//	{
//		inferTypes = val;
//	}
//	
//	public void addFeatureTypes(Map<String, ShapeAttributeDataType> types)
//	{
//		featureTypes.putAll(types);
//	}
//	
//	public void clearFeatureTypes()
//	{
//		featureTypes.clear();
//	}
//	
//	public ShapeImportInfo fromShapeFile(String filename, String termId, int srid) throws Exception {
//		return fromShapefile(filename, termId, srid, DefaultCharset, DefaultForceLonLat, null, principalDao.systemPrincipal(), true);	
//	}
//
//	public  ShapeImportInfo fromShapeFile(String filename, String termId, int srid, String charset) throws Exception {
//		return fromShapefile(filename, termId, srid, charset, DefaultForceLonLat, null, principalDao.systemPrincipal(), true);	
//	}
//	
//	public ShapeImportInfo fromShapeFile(String filename, String termId, int srid, String charset, boolean forceLonLat) throws Exception {
//		return fromShapefile(filename, termId, srid, charset, forceLonLat, null, principalDao.systemPrincipal(), true);
//	}
//
//	private String inferType(AttributeType t, Object value) throws Exception
//	{
//		if(t.getBinding().getName().equals(String.class.getName()))
//		{
//			String val = (String)value;
//			if(featureTypes.containsKey(t.getName().toString()))
//			{
//				if(featureTypes.get(t.getName().toString()) != ShapeAttributeDataType.STRING)
//				{
//					switch(featureTypes.get(t.getName().toString()))
//					{
//					case SHORT:
//						Short.parseShort(val);
//						return "short";
//					case INTEGER:
//						Integer.parseInt(val);
//						return "integer";
//					case LONG:
//						Long.parseLong(val);
//						return "long";
//					case FLOAT:
//						Float.parseFloat(val);
//						return "float";
//					case DOUBLE:
//						Double.parseDouble(val);
//						return "double";
//					case DATE:
//						return "date";
//					case STRING:
//					case LONGSTRING:
//						return "string";
//					}
//				}else return "string";
//			}else if(inferTypes)
//			{
//				if(TypeUtils.tryParseShort(val) != null) return "short";
//				else if(TypeUtils.tryParseInteger(val) != null) return "integer";
//				else if(TypeUtils.tryParseLong(val) != null) return "long";
//				else if(TypeUtils.tryParseFloat(val) != null) return "float";
//				else if(TypeUtils.tryParseDouble(val) != null) return "double";
//				else if(TypeUtils.tryParseDate(val, "MMM dd yyyy HH:mm:ss") != null) return "date";
//				return "string";
//			}else return "string";
//		}else if(t.getBinding().getName().equals(Integer.class.getName()))
//		{
//			Integer val = (Integer)value;
//			if(featureTypes.containsKey(t.getName().toString()))
//			{
//				if(featureTypes.get(t.getName().toString()) != ShapeAttributeDataType.INTEGER)
//				{
//					switch(featureTypes.get(t.getName().toString()))
//					{
//					case FLOAT:
//						return "float";
//					case DOUBLE:
//						return "double";
//					case LONG:
//						return "long";
//					case SHORT:
//						return "integer"; //narrowing not allowed
//					case STRING:
//					case LONGSTRING:
//						return "string";
//					}
//					return "integer"; //integer to date not supported
//				}else return "integer";
//			}
//			return "integer"; //no reason to promote numeric types
//		}else if(t.getBinding().getName().equals(Long.class.getName()))
//		{
//			Long val = (Long)value;
//			if(featureTypes.containsKey(t.getName().toString()))
//			{
//				if(featureTypes.get(t.getName().toString()) != ShapeAttributeDataType.LONG)
//				{
//					switch(featureTypes.get(t.getName().toString()))
//					{
//					case STRING:
//					case LONGSTRING:
//						return "string";
//					case FLOAT:
//						return "float";
//					case DOUBLE:
//						return "double";
//					case INTEGER:
//					case SHORT:
//						return "long"; //narrowing not allowed
//					}
//					return "long"; //long to date not supported
//				}
//			}
//			return "long"; //no reason to promote numeric types
//		}else if(t.getBinding().getName().equals(Short.class.getName()))
//		{
//			Short val = (Short)value;
//			if(featureTypes.containsKey(t.getName().toString()))
//			{
//				if(featureTypes.get(t.getName().toString()) != ShapeAttributeDataType.SHORT)
//				{
//					switch(featureTypes.get(t.getName().toString()))
//					{
//					case STRING:
//					case LONGSTRING:
//						return "string";
//					case FLOAT:
//						return "float";
//					case DOUBLE:
//						return "double";
//					case INTEGER:
//						return "integer";
//					case LONG:
//						return "long";
//					}
//					return "short"; //short to date not supported
//					}
//			}
//			return "short"; //no reason to promote numeric types
//		}else if(t.getBinding().getName().equals(Float.class.getName()))
//		{
//			Float val = (Float)value;
//			if(featureTypes.containsKey(t.getName().toString()))
//			{
//				if(featureTypes.get(t.getName().toString()) != ShapeAttributeDataType.FLOAT)
//				{
//					switch(featureTypes.get(t.getName().toString()))
//					{
//					case STRING:
//					case LONGSTRING:
//						return "string";
//					case SHORT:
//					case INTEGER:
//					case LONG:
//						return "float";
//					case DOUBLE:
//						return "double";
//					}
//					return "float"; //float to date not supported
//					}
//			}
//			return "float"; //no reason to promote numeric types
//		}else if(t.getBinding().getName().equals(Double.class.getName()))
//		{
//			Double val = (Double)value;
//			if(featureTypes.containsKey(t.getName().toString()))
//			{
//				if(featureTypes.get(t.getName().toString()) != ShapeAttributeDataType.DOUBLE)
//				{
//					switch(featureTypes.get(t.getName().toString()))
//					{
//					case STRING:
//					case LONGSTRING:
//						return "string";
//					case SHORT:
//					case INTEGER:
//					case LONG:
//						return "double";
//					case FLOAT:
//						return "double"; //narrowing not allowed
//					}
//					return "double"; //double to date not supported
//					}
//			}
//			return "double"; //no reason to promote numeric types
//		}else if(t.getBinding().getName().equals(Date.class.getName()))
//		{
//			Date val = (Date)value;
//			if(featureTypes.containsKey(t.getName().toString()))
//			{
//				if(featureTypes.get(t.getName().toString()) != ShapeAttributeDataType.DATE)
//				{
//					switch(featureTypes.get(t.getName().toString()))
//					{
//					case STRING:
//					case LONGSTRING:
//						return "string";
//					case SHORT:
//					case INTEGER:
//					case LONG:
//						return "long";
//					}
//					return "date";
//				}
//			}
//			return "date";
//		}
//		
//		throw new Exception("Unrecognized data type: " + t.getBinding().getName());
//	}
//	
//	private String insertBursaWolfToWKT(String wkt, double[] bursaWolf)
//	{
//		String[] defs = wkt.split("DATUM\\[");
//		if(defs.length != 2)
//		{
//			log.warn("Could not insert Bursa-Wolf Parameters to CRS WKT");
//			return wkt;
//		}
//		int bracketCount = 1;
//		int index = 0;
//		int prevClose = -1;
//		while(bracketCount != 0)
//		{
//			int close = defs[1].indexOf(']', index);
//			if(prevClose == -1) prevClose = close;
//			int open = defs[1].indexOf('[', index);
//			if(close == -1)
//			{
//				log.warn("Invalid wkt");
//				return null;
//			}
//			if(open < close)
//			{
//				bracketCount++;
//				index = open + 1;
//			}else
//			{
//				bracketCount--;
//				index = close + 1;
//			}
//			if(bracketCount != 0) prevClose = close;
//		}
//		
//		StringBuilder formattedBursaWolf = new StringBuilder();
//		for(int i=0; i<bursaWolf.length; i++)
//		{
//			formattedBursaWolf.append(String.format(Locale.US, "%.2f",  bursaWolf[i]));
//			if(i != bursaWolf.length-1) formattedBursaWolf.append(", ");
//		}
//		
//		String res = defs[0] + "DATUM[" +
//				 defs[1].substring(0, prevClose+1) +
//				", TOWGS84[" + formattedBursaWolf.toString() + "]" +
//				defs[1].substring(prevClose+1);
//		return res;
//	}
//	
//	private void addMappingConfig(AttributeMappingConfig mcfg, Map<String, Map<String, AttributeMappingConfig>> cfgCache) throws Exception
//	{
//		if(cfgCache == null)
//		{
//			configurationManager.updateMappingConfig(mcfg);
//			return;
//		}
//		String key = null;
//		if(mcfg.getAttributeName() == null) return;
//		if(mcfg.getTermId() == null && mcfg.getAttributeValue() == null)
//			key = NoMappingKey;
//		else if(mcfg.getAttributeValue() == null)
//			key = NoValueKey;
//		else
//			key = mcfg.getAttributeValue();
//
//		if(cfgCache.get(mcfg.getAttributeName()) == null)
//			cfgCache.put(mcfg.getAttributeName(), new HashMap<String, AttributeMappingConfig>());
//		if(cfgCache.get(mcfg.getAttributeName()).get(key) == null)
//		{
//			cfgCache.get(mcfg.getAttributeName()).put(key, mcfg);
//			configurationManager.updateMappingConfig(mcfg);
//		}
//	}
//	
//	//TODO move all MappingConfig creation logic to ShapeManager.generateShapesOfImport
//	private String createDataXML(SimpleFeature feature, Map<String, Map<String,AttributeInfo>> attrInfo, Map<String, GeocodeSystem> taxonomyCache, String layerTermId,
//			Map<String, Map<String, AttributeMappingConfig>> cfgCache, boolean forceOverwriteMappings) throws Exception
//	{
//		StringBuilder xml = new StringBuilder();
//		xml.append("<extraData>");
//		List <AttributeType> types = feature.getType().getTypes();
//		
//		for(AttributeType t : (List<AttributeType>) types) {
//			Object val = feature.getAttribute(t.getName());
//			if(val != null)
//			{
//				String type = null;
//				if(attrInfo == null) type = inferType(t, val);
//				else type = attrInfo.get(t.getName().toString()).get("").getType();
//				boolean setTaxonomy = false;
//				boolean setValue = false;
//				String taxonomyId = null;
//				String layerId = null;
//				String attrValue = null;
//				Boolean presentable = true;
//				Boolean mapValue = true;
//				if(attrInfo != null)
//				{
//					String taxonomy = null;
//					
//					AttributeInfo ai = attrInfo.get(t.getName().toString()).get(val);
//					if(!val.equals("") && attrInfo.get(t.getName().toString()).get(val.toString()) != null)
//					{
//						taxonomy = attrInfo.get(t.getName().toString()).get(val.toString()).getTaxonomy();
//						setValue = true;
//					}else
//					{
//						if(attrInfo.get(t.getName().toString()).get("").isStore() == false)
//							continue; //ignore attribute that is marked as non-storeable
//						taxonomy = attrInfo.get(t.getName().toString()).get("").getTaxonomy();
//						presentable = attrInfo.get(t.getName().toString()).get("").isPresentable();
//					}
//					if(taxonomy !=null)
//					{
//						GeocodeSystem tax = taxonomyCache.get(taxonomy);
//						if(tax == null) tax = taxonomyManager.findGeocodeSystemByName(taxonomy, false);
//						if(tax == null) throw new Exception("Could not find taxonomy " + taxonomy);
//						else taxonomyCache.put(taxonomy, tax);
//						setTaxonomy = true;
//						taxonomyId = tax.getId().toString();
//						
//						if(setValue)
//						{
//							AttributeInfo valueMappingInfo = attrInfo.get(t.getName().toString()).get(val.toString());
//							String term = valueMappingInfo.getTerm();
//							attrValue = valueMappingInfo.getValue();
//							if(term == null)
//							{
//								log.error("No taxonomy term is defined for attribute value mapping (" + t.getName() + "," + val + ")");
//								throw new Exception("No taxonomy term is defined for attribute value mapping (" + t.getName() + "," + val + ")");
//							}
//							
//							List<Layer> layers = null;
//							layers = taxonomyLayerDao.findLayerByNameAndGeocodeSystem(term, taxonomy);
//							if(layers == null) throw new Exception("Could not find any layer for " + term);
//							if(layers.size() > 1) throw new Exception("Found more than 1 layers for " + term);
//							layerId = layers.get(0).getId().toString();
//							mapValue = valueMappingInfo.isMapValue();
//						}
//					}
//				}
//				
//				AttributeMappingConfig mcfg = new AttributeMappingConfig();
//				mcfg.setAttributeName(t.getName().toString());
//				mcfg.setAttributeType(type);
//				mcfg.setLayerTermId(layerTermId);
//				mcfg.setPresentable(presentable);
//				
//				if(setTaxonomy || setValue)
//				{
//					if(setValue)
//					{
//						mcfg.setAttributeValue(attrValue);
//						mcfg.setMapValue(mapValue);
//					}
//					if(setTaxonomy || setValue) mcfg.setTermId(setValue ? layerId : taxonomyId);
//			
//				}/*else if(forceOverwriteMappings)
//				{
//					List<MappingConfig> mcfgs = configurationManager.getMappingConfig(t.getName().toString());
//					if(mcfgs != null && !mcfgs.isEmpty())
//						configurationManager.removeMappingConfig(t.getName().toString());
//				}*/
//				addMappingConfig(mcfg, cfgCache);
//				
//				//this case applies when there exist both a taxonomy mapping for the attribute and value mappings
//				Map<String, AttributeInfo> aim = attrInfo.get(t.getName().toString());
//				if(aim.size() > 1 && aim.get("") != null && aim.get("").getTaxonomy() != null)
//				{
//					mcfg = new AttributeMappingConfig();
//					mcfg.setAttributeName(t.getName().toString());
//					mcfg.setLayerTermId(layerTermId);
//					mcfg.setAttributeType(type);
//					mcfg.setPresentable(presentable);
//					mcfg.setTermId(taxonomyId);
//					
//					addMappingConfig(mcfg, cfgCache);
//				}
//				
//				String processedVal = HtmlUtils.htmlEscape(discardIllegalValues(type, feature.getAttribute(t.getName()).toString().trim()));
//				
//				xml.append("<"+t.getName() + " type=\"" + type + "\" " + 
//						(setTaxonomy ? "taxonomy=\""+taxonomyId+"\" " : "") + (setValue ? ("term=\""+layerId+"\""): "") + ">"); 
//				xml.append(processedVal);
//				xml.append("</"+t.getName()+">");
//			}
//		}
//		xml.append("</extraData>");
//		
//		return xml.toString();
//	}
//	
//	private String discardIllegalValues(String type, String value)
//	{
//		try
//		{
//			if(type.equals("short"))
//				Short.parseShort(value);
//			else if(type.equals("integer"))
//				Integer.parseInt(value);
//			else if(type.equals("long"))
//				Long.parseLong(value);
//			else if(type.equals("float"))
//				Float.parseFloat(value);
//			else if(type.equals("double"))
//				Double.parseDouble(value);
//		}catch(NumberFormatException e)
//		{
//			return "";
//		}
//		return value;
//	}
//	@Transactional
//	private ShapeImportInfo fromFeatureSource(DataStore dataStore, SimpleFeatureSource featureSource, 
//			String termId, int srid, boolean forceLonLat, Map<String, Map<String,AttributeInfo>> attrInfo, 
//			Principal principal, boolean forceOverwriteMappings) throws Exception {
//		
//		Map<String, GeocodeSystem> taxonomyCache = new HashMap<String, GeocodeSystem>();
//		SimpleFeatureCollection collection = featureSource.getFeatures();
//		SimpleFeatureType schema = featureSource.getSchema();
//		SimpleFeatureIterator iterator = collection.features();
//		
//		// EPSG:GGRS87 / Greek Grid - instead of  2100
//		// GCS_WGS_1984 / EPSG:2100
//		
//		String sourceCode = null, targetCode;
//		
//		if(srid != -1) sourceCode = "EPSG:" + new Integer(srid).toString();
//		targetCode = "EPSG:4326";
//		
//		CoordinateReferenceSystem sourceCRS = null;
//		if(schema.getCoordinateReferenceSystem() != null) sourceCRS = schema.getCoordinateReferenceSystem();
//		else if(sourceCode != null) sourceCRS = CRS.decode(sourceCode);
//		
//		if(sourceCRS == null) throw new Exception("No coordinate system provided nor found in shape file definition");
//		
//		CoordinateReferenceSystem targetCRS = CRS.decode(targetCode, forceLonLat);
//		
//        UUID importUUID = UUIDGenerator.randomUUID();
//        
//        boolean lenient = false;
//        String wkt = sourceCRS.toWKT();
//        if(!wkt.toLowerCase().contains("towgs"))
//        {
//        	if(CRS.lookupEpsgCode(sourceCRS, true) == 2100) //Greek Grid)
//        	{
//        		double[] bursaWolf = {-199.87, 74.79, 246.62, 0, 0, 0, 0};
//        		log.warn("No transformation parameters were found within source CRS data." + 
//        				 "Automatically applying: " + Arrays.toString(bursaWolf));
//        		wkt = insertBursaWolfToWKT(wkt, bursaWolf);
//        		sourceCRS = CRS.parseWKT(wkt);
//        	}
//        	else
//        	{
//        		log.warn("No transformation parameters were found within source CRS data. Transformation may contain errors");
//        		lenient = true;
//        	}
//        }
//        
//        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, lenient);
//        
//        Envelope b = featureSource.getBounds();
//        b = JTS.transform(b, transform);
//        Bounds bounds = new Bounds();
//        bounds.setCrs("EPSG:4326");
//        bounds.setMinx(b.getMinX());
//        bounds.setMiny(b.getMinY());
//        bounds.setMaxx(b.getMaxX());
//        bounds.setMaxy(b.getMaxY());
//        
//    	Geometry g = null;
//    	
//		try {
//		   //speed up updates by executing them only when needed.
//	       //assumes that mappings are not different among features (features can
//	       //contain different subset of attributes, but when a mapping is present, it
//	       //is assumed to be the same as the corresponding mapping of all other features)
//		   //this assumption is valid and does not pose any limitations to the mapping
//		   //configuration of the system, which only supports dataset-wide mappings as well
//			Map<String, Map<String, AttributeMappingConfig>> cfgCache = new HashMap<String, Map<String, AttributeMappingConfig>>(); 
//			 
//			while (iterator.hasNext()) 
//			{
//				// read a shape file feature
//				SimpleFeature feature = iterator.next();
//				
//				// get its geometry
//				g = (Geometry) feature.getDefaultGeometry();
//
//				g = JTS.transform( g, transform);
//				g.setSRID(4326);
//				
//
//				String data = createDataXML(feature, attrInfo, taxonomyCache, termId, cfgCache, forceOverwriteMappings);
//				
//				ShapeImport shape = new ShapeImport();
//
//				shape.setCreationDate(Calendar.getInstance().getTime());
//				shape.setCreatorID(principal.getId());
//
//				shape.setData(data);
//				shape.setId(UUIDGenerator.randomUUID());
//				shape.setLastUpdate(Calendar.getInstance().getTime());
//				shape.setShapeIdentity(termId);
//				shape.setShapeImport(importUUID);
//				shape.setGeography(g);
//				shapeImportDao.create(shape);
//				
//			}
//		} 
//		finally 
//		{
//			iterator.close();
//		}
//		
//		Map<String, Set<String>> valueMappingValues = new HashMap<String, Set<String>>();
//		for(Map<String, AttributeInfo> aie : attrInfo.values())
//		{
//			AttributeInfo ai = aie.get("");
//			if(ai != null /*&& ai.isAutoValueMapping()*/)
//			{
//				valueMappingValues.put(ai.getName(), getAttributeValuesFromFeatureSource(featureSource, ai.getName()));
//				continue;
//			}
//			for(AttributeInfo info : aie.values())
//			{
//				if(info.getValue() != null)
//				{
//					valueMappingValues.put(ai.getName(), getAttributeValuesFromFeatureSource(featureSource, info.getName()));
//					break;
//				}
//			}
//		}
//		
//		return new ShapeImportInfo(importUUID, bounds, valueMappingValues);
//	}
//	
//	@Transactional
//	public ShapeImportInfo fromShapefile(String pathName, String termId, int srid, String charset, boolean forceLonLat, 
//			Map<String, Map<String,AttributeInfo>> attrInfo, Principal principal, boolean forceOverwriteMappings) throws Exception {
//		
//		if(srid < 0 && srid != -1) throw new IllegalArgumentException("Illegal srid code");
//		if(principal == null) throw new IllegalArgumentException("Creator not provided");
//
//		Map<String, String> map = new HashMap<String, String>();
//
//		File file = new File(pathName);
//
//		map.put("url", file.toURI().toString());
//		map.put("charset", charset);
//		
//		//String name = file.getName();
//		//name = name.substring(0, name.indexOf("."));
//		
//		SimpleFeatureSource featureSource = null;
//		DataStore dataStore = null;
//		try 
//		{
//			dataStore = DataStoreFinder.getDataStore(map);
//			featureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
//		} 
//		catch (IOException e) 
//		{
//			log.error("Error while reading shape file", e);
//			throw e;
//		}
//		return fromFeatureSource(dataStore, featureSource, termId, srid, forceLonLat, attrInfo, principal, forceOverwriteMappings);
//
//	}
//	
//	public Map<String, String> analyzeAttributesOfFeatureSource(SimpleFeatureSource featureSource) throws Exception
//	{
//		SimpleFeatureCollection collection = featureSource.getFeatures();
//		SimpleFeatureIterator iterator = collection.features();
//
//    	Map<String, String> attributes = new HashMap<String, String>();
//    	
//		try {
//			while (iterator.hasNext()) {
//				SimpleFeature feature = iterator.next();
//
//				List <AttributeType> types = feature.getType().getTypes();
//				for(AttributeType t : (List<AttributeType>) types) 
//				{
//					Object val = feature.getAttribute(t.getName());
//					if(val != null)
//					{
//						String type = inferType(t, val);
//						attributes.put(t.getName().toString(),  type); 
//					}
//				}
//			}
//			return attributes;
//		} 
//		finally 
//		{
//			iterator.close();
//		}
//	}
//	
//	public Map<String, String> analyzeAttributesOfShapeFile(String filename, String charset) throws Exception
//	{
//		Map<String, String> map = new HashMap<String, String>();
//
//		File file = new File(filename);
//
//		map.put("url", file.toURI().toString());
//		map.put("charset", charset);
//		
//		SimpleFeatureSource featureSource = null;
//		DataStore dataStore = null;
//		try 
//		{
//			dataStore = DataStoreFinder.getDataStore(map);
//			featureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
//		} catch (IOException e) 
//		{
//			log.error("Error while reading shape file", e);
//			throw e;
//		}
//		
//		return analyzeAttributesOfFeatureSource(featureSource);
//	}
//	
////	public Set<String> getAttributeValuesFromFeatureSource(SimpleFeatureSource featureSource, String attribute) throws Exception
////	{
////		SimpleFeatureCollection collection = featureSource.getFeatures();
////		SimpleFeatureIterator iterator = collection.features();
////
////		Set<String> values = new HashSet<String>();
////		try 
////		{
////			while (iterator.hasNext()) {
////				SimpleFeature feature = iterator.next();
////
////				List <AttributeType> types = feature.getType().getTypes();
////				for(AttributeType t : (List<AttributeType>) types) 
////				{
////					if(t.getName().toString().equals(attribute))
////					{
////						Object val = feature.getAttribute(t.getName());
////						if(val != null)
////							values.add(val.toString());
////					}
////				}
////			}
////			return values;
////		} 
////		finally 
////		{
////			iterator.close();
////		}
////	}
//	
//	public Set<String> getAttributeValuesFromShapeFile(String pathname, String charset, String attribute) throws Exception
//	{
//		Map<String, String> map = new HashMap<String, String>();
//
//		File file = new File(pathname);
//
//		map.put("url", file.toURI().toString());
//		map.put("charset", charset);
//		
//		SimpleFeatureSource featureSource = null;
//		DataStore dataStore = null;
//		try 
//		{
//			dataStore = DataStoreFinder.getDataStore(map);
//			featureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
//		} catch (IOException e) 
//		{
//			log.error("Error while reading shape file", e);
//			throw e;
//		}
//		
//		return getAttributeValuesFromFeatureSource(featureSource, attribute);
//	}
//	
//	@Transactional(readOnly = true)
//	public List<ShapeImport> getImport(UUID importId) throws Exception
//	{
//		return shapeImportDao.getImport(importId);
//	}
//	
//	@Transactional(readOnly = true)
//	public List<ShapeImport> findByImportIdentity(String identity) throws Exception
//	{
//		return shapeImportDao.findByIdentity(identity);
//	}
//	
//	@Transactional(readOnly = true)
//	public List<ShapeImportInstance> getImportInstances(boolean nonEmpty) throws Exception
//	{
//		List<ShapeImportInstance> res = new ArrayList<ShapeImportInstance>();
//		
//		List<UUID> instances = shapeImportDao.listImports();
//		for(UUID instance : instances)
//		{
//			ShapeImport si = getImport(instance).get(0);
//			if(nonEmpty)
//			{
//				if(shapeManager.countShapesOfImport(instance) == 0)
//					continue;
//			}
//			ShapeImportInstance sii = new ShapeImportInstance();
//			sii.setImportId(instance);
//			if(si.getShapeIdentity() != null)
//			{
//				Layer layer = layerDao.read(UUID.fromString(si.getShapeIdentity()));
//				if(layer != null)
//				{
////					if(layer.getTaxonomyLayers().iterator().hasNext())
////						sii.setTermTaxonomy(layer.getTaxonomyLayers().iterator().next().getTaxonomy().getName());
//					sii.setTermTaxonomy(layer.getGeocodeSystem().getName());
//					sii.setTerm(layer.getName());
//				}
//				
//			}
//			sii.setTimestamp(si.getCreationDate().getTime());
//			res.add(sii);
//		}
//		return res;
//	}
//}
