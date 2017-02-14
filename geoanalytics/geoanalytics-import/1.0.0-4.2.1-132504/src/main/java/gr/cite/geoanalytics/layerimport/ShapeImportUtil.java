package gr.cite.geoanalytics.layerimport;

import gr.cite.gaap.utilities.TypeUtils;
import gr.cite.geoanalytics.common.ShapeAttributeDataType;
import gr.cite.geoanalytics.dataaccess.dao.UUIDGenerator;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDao;
import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeImport;
import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeImportDao;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.vividsolutions.jts.geom.Geometry;

@Component
public class ShapeImportUtil {

	private static Logger log = LoggerFactory.getLogger(ShapeImportUtil.class);
	
	private static boolean inferTypes = false;
	private static Map<String, ShapeAttributeDataType> featureTypes = new HashMap<String, ShapeAttributeDataType>();
	
	public static final String DefaultCharset = "UTF-8";
	public static final boolean DefaultAxisInvert = false;
	
	private static PrincipalDao principalDao;
	private static ShapeImportDao shapeImportDao;
	
	@Inject
	public void setPrincipalDao(PrincipalDao principalDao) {
		ShapeImportUtil.principalDao = principalDao;
	}
	
	@Inject
	public void setShapeImportDao(ShapeImportDao shapeImportDao) {
		ShapeImportUtil.shapeImportDao = shapeImportDao;
	}
	
	
	public static void setTypeInference(boolean val) {
		inferTypes = val;
	}
	
	public static void addFeatureTypes(Map<String, ShapeAttributeDataType> types) {
		featureTypes.putAll(types);
	}
	
	public static void clearFeatureTypes() {
		featureTypes.clear();
	}
	
	public static UUID fromShapeFile(String filename, int srid) throws Exception {
		return ShapeImportUtil.fromShapefile(filename, srid, DefaultCharset, DefaultAxisInvert, principalDao.systemPrincipal());	
	}

	public static UUID fromShapeFile(String filename, int srid, String charset) throws Exception {
		return ShapeImportUtil.fromShapefile(filename, srid, charset, DefaultAxisInvert, principalDao.systemPrincipal());	
	}
	
	public static UUID fromShapeFile(String filename, int srid, String charset, boolean axisInvert) throws Exception {
		return ShapeImportUtil.fromShapefile(filename, srid, charset, axisInvert, principalDao.systemPrincipal());
	}

	private static String inferType(AttributeType t, Object value) throws Exception {
		if(t.getBinding().getName().equals(String.class.getName())) {
			String val = (String)value;
			if(featureTypes.containsKey(t.getName().toString())) {
				ShapeAttributeDataType type = featureTypes.get(t.getName().toString());

				switch(featureTypes.get(t.getName().toString()))
				{
				case SHORT:
					Short.parseShort(val);
					break;
				case INTEGER:
					Integer.parseInt(val);
					break;
				case LONG:
					Long.parseLong(val);
					break;
				case FLOAT:
					Float.parseFloat(val);
					break;
				case DOUBLE:
					Double.parseDouble(val);
					break;
				case DATE:
					break;
				}
				return type.getXmlType();
			}else if(inferTypes) {
				if(TypeUtils.tryParseShort(val) != null) return ShapeAttributeDataType.SHORT.getXmlType();
				else if(TypeUtils.tryParseInteger(val) != null) return ShapeAttributeDataType.INTEGER.getXmlType();
				else if(TypeUtils.tryParseLong(val) != null) return ShapeAttributeDataType.LONG.getXmlType();
				else if(TypeUtils.tryParseFloat(val) != null) return ShapeAttributeDataType.FLOAT.getXmlType();
				else if(TypeUtils.tryParseDouble(val) != null) return ShapeAttributeDataType.DOUBLE.getXmlType();
				else if(TypeUtils.tryParseDate(val, "MMM dd yyyy HH:mm:ss") != null) return ShapeAttributeDataType.DATE.getXmlType();
				return ShapeAttributeDataType.STRING.getXmlType();
			}else return ShapeAttributeDataType.STRING.getXmlType();
		}else if(t.getBinding().getName().equals(Integer.class.getName())) {
			Integer val = (Integer)value;
			if(featureTypes.containsKey(t.getName().toString())) {
				if(featureTypes.get(t.getName().toString()) != ShapeAttributeDataType.INTEGER) {
					switch(featureTypes.get(t.getName().toString())) {
					case FLOAT:
						return ShapeAttributeDataType.FLOAT.getXmlType();
					case DOUBLE:
						return ShapeAttributeDataType.DOUBLE.getXmlType();
					case LONG:
						return ShapeAttributeDataType.LONG.getXmlType();
					case SHORT:
						return ShapeAttributeDataType.INTEGER.getXmlType(); //narrowing not allowed
					case STRING:
					case LONGSTRING:
						return ShapeAttributeDataType.STRING.getXmlType();
					}
					return ShapeAttributeDataType.INTEGER.getXmlType(); //integer to date not supported
				}else return ShapeAttributeDataType.INTEGER.getXmlType();
			}
			return ShapeAttributeDataType.INTEGER.getXmlType(); //no reason to promote numeric types
		}else if(t.getBinding().getName().equals(Long.class.getName())) {
			Long val = (Long)value;
			if(featureTypes.containsKey(t.getName().toString())) {
				if(featureTypes.get(t.getName().toString()) != ShapeAttributeDataType.LONG) {
					switch(featureTypes.get(t.getName().toString()))
					{
					case STRING:
					case LONGSTRING:
						return ShapeAttributeDataType.STRING.getXmlType();
					case FLOAT:
						return ShapeAttributeDataType.FLOAT.getXmlType();
					case DOUBLE:
						return ShapeAttributeDataType.DOUBLE.getXmlType();
					case INTEGER:
					case SHORT:
						return ShapeAttributeDataType.LONG.getXmlType(); //narrowing not allowed
					}
					return ShapeAttributeDataType.LONG.getXmlType(); //long to date not supported
				}
			}
			return ShapeAttributeDataType.LONG.getXmlType(); //no reason to promote numeric types
		}else if(t.getBinding().getName().equals(Short.class.getName())) {
			Short val = (Short)value;
			if(featureTypes.containsKey(t.getName().toString())) {
				if(featureTypes.get(t.getName().toString()) != ShapeAttributeDataType.SHORT) {
					switch(featureTypes.get(t.getName().toString())) {
					case STRING:
					case LONGSTRING:
						return ShapeAttributeDataType.STRING.getXmlType();
					case FLOAT:
						return ShapeAttributeDataType.FLOAT.getXmlType();
					case DOUBLE:
						return ShapeAttributeDataType.DOUBLE.getXmlType();
					case INTEGER:
						return ShapeAttributeDataType.INTEGER.getXmlType();
					case LONG:
						return ShapeAttributeDataType.LONG.getXmlType();
					}
					return ShapeAttributeDataType.SHORT.getXmlType(); //short to date not supported
					}
			}
			return "short"; //no reason to promote numeric types
		}else if(t.getBinding().getName().equals(Float.class.getName())) {
			Float val = (Float)value;
			if(featureTypes.containsKey(t.getName().toString())) {
				if(featureTypes.get(t.getName().toString()) != ShapeAttributeDataType.FLOAT) {
					switch(featureTypes.get(t.getName().toString())) {
					case STRING:
					case LONGSTRING:
						return ShapeAttributeDataType.STRING.getXmlType();
					case SHORT:
					case INTEGER:
					case LONG:
						return ShapeAttributeDataType.FLOAT.getXmlType();
					case DOUBLE:
						return ShapeAttributeDataType.DOUBLE.getXmlType();
					}
					return ShapeAttributeDataType.FLOAT.getXmlType(); //float to date not supported
					}
			}
			return ShapeAttributeDataType.FLOAT.getXmlType(); //no reason to promote numeric types
		}else if(t.getBinding().getName().equals(Double.class.getName())) {
			Double val = (Double)value;
			if(featureTypes.containsKey(t.getName().toString())) {
				if(featureTypes.get(t.getName().toString()) != ShapeAttributeDataType.DOUBLE) {
					switch(featureTypes.get(t.getName().toString())) {
					case STRING:
					case LONGSTRING:
						return ShapeAttributeDataType.STRING.getXmlType();
					case SHORT:
					case INTEGER:
					case LONG:
						return ShapeAttributeDataType.DOUBLE.getXmlType();
					case FLOAT:
						return ShapeAttributeDataType.DOUBLE.getXmlType(); //narrowing not allowed
					}
					return ShapeAttributeDataType.DOUBLE.getXmlType(); //double to date not supported
					}
			}
			return ShapeAttributeDataType.DOUBLE.getXmlType(); //no reason to promote numeric types
		}
		
		throw new Exception("Unrecognized data type: " + t.getBinding().getName());
	}
	
	//private static DataType processType(Object val)
	private static String createDataXML(SimpleFeature feature) throws Exception {
		StringBuilder xml = new StringBuilder();
		xml.append("<extraData>");
		List <AttributeType> types = feature.getType().getTypes();
		for(AttributeType t : (List<AttributeType>) types) {
			Object val = feature.getAttribute(t.getName());
			if(val != null) {
				String type = inferType(t, val);
				xml.append("<"+t.getName() + " type=\"" + type + "\">"); 
				xml.append(feature.getAttribute(t.getName()));
				xml.append("</"+t.getName()+">");
			}
		}
		xml.append("</extraData>");
		return xml.toString();
	}
	
	public static UUID fromShapefile(String pathName, int srid, String charset, boolean forceLonLat, Principal principal) throws Exception {
		
		if(srid < 0 && srid != -1) throw new IllegalArgumentException("Illegal srid code");
		if(principal == null) throw new IllegalArgumentException("Creator not provided");
		
		Geometry g = null;

		Map<String, String> map = new HashMap<String, String>();

		File file = new File(pathName);

		map.put("url", file.toURI().toString());
		//map.put("charset", charset);

        SimpleFeatureType schema = null;
		SimpleFeatureSource featureSource;
		
		SimpleFeatureCollection collection = null;
		try {
			DataStore dataStore = DataStoreFinder.getDataStore(map);
			featureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
			collection = featureSource.getFeatures();
			schema = featureSource.getSchema();
		} catch (IOException e) {
			log.error("Error while reading shape file", e);
			throw e;
		}

		SimpleFeatureIterator iterator = collection.features();
		
		// EPSG:GGRS87 / Greek Grid - instead of  2100
		// GCS_WGS_1984 / EPSG:2100
		
		String sourceCode, targetCode;
		
		sourceCode = "EPSG:" + new Integer(srid).toString();
		targetCode = "EPSG:4326";

		CoordinateReferenceSystem sourceCRS = null;
		if(schema.getCoordinateReferenceSystem() != null) sourceCRS = schema.getCoordinateReferenceSystem();
		else sourceCRS = CRS.decode(sourceCode);
		
		if(sourceCRS == null) throw new Exception("No coordinate system provided nor found in shape file definition");
		
		CoordinateReferenceSystem targetCRS = CRS.decode(targetCode, forceLonLat);
	
        UUID importUUID = UUIDGenerator.randomUUID();
        
        boolean lenient = false;
        if(!sourceCRS.toWKT().toLowerCase().contains("towgs")) {
        	log.warn("No transformation parameters were found within source CRS data. Transformation may contain errors");
        	lenient = true;
        }
		try {
			while (iterator.hasNext()) {
				// read a shape file feature
				SimpleFeature feature = iterator.next();
				
				// get its geometry
				g = (Geometry) feature.getDefaultGeometry();


				MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, lenient);
				g = JTS.transform( g, transform);
				g.setSRID(4326);
				
//				System.out.println("attribute count: " + feature.getAttributeCount());
//				for (int i = 0; i != 11; ++i) {
//					System.out.println("attribute count: " + feature.getAttribute(i));
//				}
				
//				System.out.println("feature to string: " + feature.toString());
//				System.out.println("feature descriptor local name: " + feature.getDescriptor().getLocalName());
//				System.out.println("feature: " + feature.getDescriptor().toString());
//				System.out.println("feature: get types" + feature.getType().getTypes().toString());
//				System.out.println("feature: get user data" + feature.getFeatureType().getUserData().toString());
//				System.out.println("feature: get PERM_POPUL: " + feature.getAttribute("PERM_POPUL"));
				
//				List <AttributeType> types = feature.getType().getTypes();
//				for(AttributeType t : (List<AttributeType>) types) {
//					System.out.println("attribute: " + t.getName().getClass().getName() + " has value: " + (t.getName()));
//					System.out.println("attribute: " + t.getName().getClass().getName() + " has value: " + feature.getAttribute(t.getName()));
//					System.out.println("attribute: " + t.getName() + " has value: " + (feature.getAttribute(t.getName())));
//					if (feature.getAttribute(t.getName()) != null) {
//						System.out.println((feature.getAttribute(t.getName()).getClass().getName()));
//					}
//					else {
//						System.out.println("null");
//					}
//				}

				String data = createDataXML(feature);
				
				ShapeImport shape = new ShapeImport();

				shape.setCreationDate(Calendar.getInstance().getTime());
				shape.setCreator(principal);

				shape.setData(data);
				shape.setId(UUIDGenerator.randomUUID());
				shape.setLastUpdate(Calendar.getInstance().getTime());
				//TODO: shape file name or description ?
				shape.setShapeIdentity("identity");
				shape.setShapeImport(importUUID);
				shape.setGeography(g);
				shapeImportDao.create(shape);
				
			}
		} finally {
			iterator.close();
		}
		
		return importUUID;
	}

}
