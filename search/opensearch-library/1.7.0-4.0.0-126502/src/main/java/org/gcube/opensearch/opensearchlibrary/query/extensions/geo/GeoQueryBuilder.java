package org.gcube.opensearch.opensearchlibrary.query.extensions.geo;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gcube.opensearch.opensearchlibrary.GeoConstants;
import org.gcube.opensearch.opensearchlibrary.query.BasicQueryBuilder;
import org.gcube.opensearch.opensearchlibrary.query.BasicURLTemplate;
import org.gcube.opensearch.opensearchlibrary.query.IncompleteQueryException;
import org.gcube.opensearch.opensearchlibrary.query.MalformedQueryException;
import org.gcube.opensearch.opensearchlibrary.query.NonExistentParameterException;
import org.gcube.opensearch.opensearchlibrary.query.QueryBuilder;
import org.gcube.opensearch.opensearchlibrary.query.QueryBuilderDecorator;
import org.gcube.opensearch.opensearchlibrary.query.URLTemplate;
import org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement;

public class GeoQueryBuilder extends QueryBuilderDecorator {

	public GeoQueryBuilder(QueryBuilder qb) {
		super(qb);
	}
	
	@Override
	public QueryBuilder clone() {
		QueryBuilder qb;
		try {
			qb = new GeoQueryBuilder(this.qb);
		}catch(Exception e) {
			return null;
		}
		return qb;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#setParameter(String, String)
	 */
	public GeoQueryBuilder setParameter(String name, String value) throws NonExistentParameterException, Exception  {
		
		if(name.compareTo(GeoConstants.boxQname) == 0 || name.compareTo(GeoConstants.polygonQname) == 0)
			qb.setParameter(name, URLDecoder.decode(value, "UTF-8"));
		else if(name.compareTo(GeoConstants.geometryQname) == 0)
			qb.setParameter(name, URLDecoder.decode(value, "UTF-8").replaceAll(" ", "\\+"));
		else
			qb.setParameter(name, value);
		
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#setParameters(List, List)
	 */
	public GeoQueryBuilder setParameters(List<String> names, List<Object> values) throws NonExistentParameterException, Exception {
		
		if(names.size() != values.size())
			throw new Exception("List size mismatch");
		
		int index;
		if((index = names.indexOf(GeoConstants.boxQname)) != 1) {
			if(values.get(index) instanceof String)
				setParameter(GeoConstants.boxQname, (String)values.get(index));
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
		if((index = names.indexOf(GeoConstants.polygonQname)) != 1) {
			if(values.get(index) instanceof String)
				setParameter(GeoConstants.polygonQname, (String)values.get(index));
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
		if((index = names.indexOf(GeoConstants.lonQname)) != -1) {
			if(values.get(index) instanceof String)
				setParameter(GeoConstants.lonQname, (String)values.get(index));
			else if(values.get(index) instanceof Float)
				setParameter(GeoConstants.lonQname, ((Float)values.get(index)).toString());
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
		if((index = names.indexOf(GeoConstants.latQname)) != -1) {
			if(values.get(index) instanceof String)
				setParameter(GeoConstants.latQname, (String)values.get(index));
			else if(values.get(index) instanceof Float)
				setParameter(GeoConstants.latQname, ((Float)values.get(index)).toString());
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
		if((index = names.indexOf(GeoConstants.locationStringQname)) != -1) {
			if(values.get(index) instanceof String)
				setParameter(GeoConstants.lonQname, (String)values.get(index));
			else
				throw new ClassCastException();
		}
		if((index = names.indexOf(GeoConstants.radiusQname)) != -1) {
			if(values.get(index) instanceof String)
				setParameter(GeoConstants.radiusQname, (String)values.get(index));
			else if(values.get(index) instanceof Integer)
				setParameter(GeoConstants.latQname, ((Integer)values.get(index)).toString());
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
	
		qb.setParameters(names, values);
		
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#isQueryComplete()
	 */
	public boolean isQueryComplete() {
		if(isParameterSet(GeoConstants.lonQname) && !isParameterSet(GeoConstants.latQname))
				return false;
		if(isParameterSet(GeoConstants.latQname) && !isParameterSet(GeoConstants.lonQname))
			return false;
		return qb.isQueryComplete();
	}

	/**
	 * Checks if the parameter values are in consistent form
	 * @throws MalformedQueryException If the query contains mutually exclusive filters of some parameter value is not of the correct syntax
	 * @throws IncompleteQueryException If the query does not contain all the parameters which must be present in order for the query to be valid
	 * @throws Exception In case of other error
	 */
	private void validateQuery() throws IncompleteQueryException, MalformedQueryException, Exception {
		
		boolean multipleFilters = false;
		if(isParameterSet(GeoConstants.boxQname)) {
			if(isParameterSet(GeoConstants.polygonQname) || isParameterSet(GeoConstants.geometryQname) || 
					isParameterSet(GeoConstants.latQname) || isParameterSet(GeoConstants.lonQname) || isParameterSet(GeoConstants.radiusQname))
				multipleFilters = true;
		}else if(isParameterSet(GeoConstants.polygonQname)) {
			if(isParameterSet(GeoConstants.boxQname) || isParameterSet(GeoConstants.geometryQname) || 
					isParameterSet(GeoConstants.lonQname) || isParameterSet(GeoConstants.latQname) || isParameterSet(GeoConstants.radiusQname))
				multipleFilters = true;
		}else if(isParameterSet(GeoConstants.geometryQname)) {
			if(isParameterSet(GeoConstants.boxQname) || isParameterSet(GeoConstants.polygonQname) || 
					isParameterSet(GeoConstants.lonQname) || isParameterSet(GeoConstants.latQname) || isParameterSet(GeoConstants.radiusQname))
				multipleFilters = true;
		}else if(isParameterSet(GeoConstants.latQname) || isParameterSet(GeoConstants.lonQname) || isParameterSet(GeoConstants.radiusQname)) {
			if(isParameterSet(GeoConstants.boxQname) || isParameterSet(GeoConstants.polygonQname) || isParameterSet(GeoConstants.geometryQname))
				multipleFilters = true;
			else {
				if(!isParameterSet(GeoConstants.latQname) || !isParameterSet(GeoConstants.lonQname))
					throw new IncompleteQueryException("Could not constitute filter triple. One of geo:lat, geo:lon is missing");
			}
		}
		
		if(multipleFilters == true)
			throw new MalformedQueryException("More than one of geo:box, geo:polygon, geo:geometry and (geo:lon,geo:lat,geo:radius) mutually exclusive filters were found");
		
		for(String param: Arrays.asList(GeoConstants.boxQname, GeoConstants.polygonQname, GeoConstants.geometryQname, GeoConstants.latQname, GeoConstants.lonQname, GeoConstants.radiusQname)) {
			
			if(!isParameterSet(param))
				continue;
			
			String value = null;
			try {
				value = getParameterValue(param);
			}catch(Exception e) {
				throw new MalformedQueryException(e);
			}
			
			if(param.compareTo(GeoConstants.boxQname) == 0) {
				validateBoxValue(param, value);
				continue;
			}
			
			if(param.compareTo(GeoConstants.polygonQname) == 0) {
				validatePolygonValue(param, value);
				continue;
			}
			
			if(param.compareTo(GeoConstants.geometryQname) == 0) {
				validateGeometryValue(param, value);
				continue;
			}
			
			if(param.compareTo(GeoConstants.latQname) == 0) {
				float coordValue = 0.0f;
				try{
					coordValue = Float.parseFloat(value);
				}catch(Exception e) {
					throw new MalformedQueryException("Malformed geo:lat parameter", param);
				}
				if(coordValue < -90.0f || coordValue > 90.0f) {
					throw new MalformedQueryException("Malformed geo:lat parameter: value out of bounds", param);
				}
				continue;
			}
			
			if(param.compareTo(GeoConstants.lonQname) == 0) {
				float coordValue = 0.0f;
				try{
					coordValue = Float.parseFloat(value);
				}catch(Exception e) {
					throw new MalformedQueryException("Malformed geo:lon parameter", param);
				}
				if(coordValue < -180.0f || coordValue > 180.0f) {
					throw new MalformedQueryException("Malformed geo:lon parameter: value out of bounds", param);
				}
				continue;
			}
			
			if(param.compareTo(GeoConstants.radiusQname) == 0) {
				try {
					Integer.parseInt(value);
				}catch(Exception e) {
					throw new MalformedQueryException("Malformed geo:radius parameter");
				}
			}
		}
	}
	
	/**
	 * Checks if the value of the Geo bounding box (geo:box) parameter is in consistent form
	 * @param param The qualified name of the parameter
	 * @param value The value of the parameter
	 * @throws MalformedQueryException If the parameter value is not of the correct syntax
	 */
	private void validateBoxValue(String param, String value) throws MalformedQueryException {
		String[] boxCoords = value.split(",");
		if(boxCoords.length != 4)
			throw new MalformedQueryException("Wrong number of geo:box coordinates");
		int i = 0;
		for(String boxCoord : boxCoords) {
			float coordValue = 0.0f;
			try {
				coordValue = Float.parseFloat(boxCoord);
			}catch(Exception e) {
				throw new MalformedQueryException("Malformed geo:box coordinate", param);				
			}
			if(i%2 == 0) {
				if(coordValue < -90.0f || coordValue > 90.0f)
					throw new MalformedQueryException("Malformed geo:box coordinate: latitude out of bounds");	
			}else {
				if(coordValue < -180.0f || coordValue > 180.0f)
					throw new MalformedQueryException("Malformed geo:box coordinate: longtitude out of bounds");	
			}
			i++;
		}
	}
	
	/**
	 * Checks if the value of the Geo polygon (geo:polygon) parameter is in consistent form.
	 * More specifically, the polygon described is considered valid if it consists of a sequence of (lat,lon) coordinate pairs
	 * and the last coordinate is equal to the first, indicating that the polygon is closed.
	 * @param param The qualified name of the parameter
	 * @param value The value of the parameter
	 * @throws MalformedQueryException If the parameter value is not of the correct syntax
	 */
	private void validatePolygonValue(String param, String value) throws MalformedQueryException {
		String[] polyCoords = value.split(",");
		if(polyCoords.length < 8 || polyCoords.length%2 != 0)
			throw new MalformedQueryException("Wrong number of geo:polygon coordinates");
		int i = 0;
		for(String polyCoord : polyCoords) {
			float coordValue = 0.0f;
			try {
				coordValue = Float.parseFloat(polyCoord);
			}catch(Exception e) {
				throw new MalformedQueryException("Malformed geo:polygon coordinate", param);
			}
			if(i%2 == 0) {
				if(coordValue < -90.0f || coordValue > 90.0f)
					throw new MalformedQueryException("Malformed geo:polygon coordinate: latitude out of bounds");	
			}else {
				if(coordValue < -180.0f || coordValue > 180.0f)
					throw new MalformedQueryException("Malformed geo:polygon coordinate: longtitude out of bounds");	
			}
			i++;
		}
		if(!polyCoords[0].equals(polyCoords[polyCoords.length-1]) || !polyCoords[1].equals(polyCoords[polyCoords.length-2]))
			throw new MalformedQueryException("geo:polygon parameter value does not define a closed polygon");
	}
	
	/**
	 * Checks if the value of the Geo geometry (geo:geometry) parameter is in consistent form, in accordance to the Well Known Text (WKT) standard.
	 * All coordinates should be 2D (lon,lat), as stated in the Geo Extensions specification.
	 * @param param The qualified name of the parameter
	 * @param value The value of the parameter
	 * @throws MalformedQueryException If the parameter value is not of the correct syntax
	 */
	private void validateGeometryValue(String param, String value) throws MalformedQueryException, Exception {
		value = URLDecoder.decode(value, "UTF-8");
		int index;
		if((index = value.indexOf('(')) == -1)
			throw new MalformedQueryException("Malformed geo:geometry value: Could not find geometric object");
		String geometry = value.substring(0, index);
		String geometricObject = value.substring(index);
		if(geometricObject.charAt(0) != '(' && geometricObject.charAt(geometricObject.length()-1) != ')')
			throw new MalformedQueryException("Malformed geo:geometry value: Malformed geometric object");
		geometricObject = geometricObject.substring(1, geometricObject.length()-1);
		
		if(geometry.equals("POINT"))
			validateGeometryPoint(param, geometricObject);
		else if(geometry.equals("LINESTRING") || geometry.equals("MULTIPOINT"))
			validateGeometryPointCollection(param, geometricObject);
		else if(geometry.equals("POLYGON"))
			validateGeometryPolygon(param, geometricObject);
		else if(geometry.equals("MULTILINESTRING"))
			validateGeometryMultiLineString(param, geometricObject);
		else if(geometry.equals("MULTIPOLYGON"))
			validateGeometryMultiPolygon(param, geometricObject);
		else
			throw new MalformedQueryException("Malformed geo:geometry value: Unrecognized geometric  object");
	}
	
	private List<String> validateGeometryPoint(String param, String geometricObject) throws MalformedQueryException {
		Pattern p = Pattern.compile("^([\\d\\.\\+-]+) ([\\d\\.\\+-]+)$");
		Matcher m = p.matcher(geometricObject);
		List<String> coords = new ArrayList<String>();
		String coord = null;
		while(m.find()) {
			if((coord = m.group(1)) != null)
				coords.add(coord);
			if((coord = m.group(2)) != null)
				coords.add(coord);
		}
		
		if(coords.size() != 2)
			throw new MalformedQueryException("Malformed geo:geometry value", param);
		
		int i = 0;
		for(String coordinate : coords) {
			float coordValue = 0.0f;
			try {
				coordValue = Float.parseFloat(coordinate);
			}catch(Exception e) {
				throw new MalformedQueryException("Malformed geo:geometry value: Malformed coordinate value", param);
			}
			if(i%2 == 0) {
				if(coordValue < -180.0f || coordValue > 180.0f )
					throw new MalformedQueryException("Malformed geo:geometry value: Longtitude out of range");
			}else {
				if(coordValue < -90.0f || coordValue > 90.0f)
					throw new MalformedQueryException("Malformed geo:geometry value: Latitude out of range");
			}
			i++;
		}
		
		return coords;
	}
	
	private List<String> validateGeometryPointCollection(String param, String geometricObject) throws MalformedQueryException {
		Pattern p = Pattern.compile("^([\\d\\.\\+-]+ [\\d\\.\\+-]+(?:,\\s*)?)+$");
		Matcher m = p.matcher(geometricObject);
		List<String> coords = new ArrayList<String>();
		if(!m.find())
			throw new MalformedQueryException("Malformed geo:geometry value");
		String[] points = m.group().split(",");
		for(String point: points) {
			if(!point.equals(""))
				coords.addAll(validateGeometryPoint(param, point.trim()));
		}
		if(m.find() || coords.size() == 0)
			throw new MalformedQueryException("Malformed geo:geometry value");
		return coords;
	}
	
	private List<String> validateGeometryMultiLineString(String param, String geometricObject) throws MalformedQueryException {
		Pattern p = Pattern.compile("^(\\(([\\d\\.\\+-]+ [\\d\\.\\+-]+(?:,[\\s]*)?)+\\)(?:,[\\s]*)?)+$");
		Matcher m = p.matcher(geometricObject);
		List<String> coords = new ArrayList<String>();
		if(!m.find())
			throw new MalformedQueryException("Malformed geo:geometry value");
		String[] lines = m.group().split("\\),");
		for(int i = 0; i <  lines.length; i++) {
			String line = lines[i].trim();
			if(line.equals(""))
				continue;
			if(i == lines.length-1)
				line = line.substring(1, line.length()-1);
			else
				line = line.substring(1);
			coords.addAll(validateGeometryPointCollection(param, line));
		}
		if(m.find() || coords.size() == 0)
			throw new MalformedQueryException("Malformed geo:geometry value");
			
		return coords;
	}
	
	private List<String> validateGeometryPolygon(String param, String geometricObject) throws MalformedQueryException {
		Pattern p = Pattern.compile("^((\\((([\\d\\.\\+-]+) ([\\d\\.\\+-]+)(,[\\s]*)?)+\\))(,[\\s]*)?)+$");
		Matcher m = p.matcher(geometricObject);
		List<String> coords = new ArrayList<String>();
		List<String> polyCoords = null;
		if(!m.find())
			throw new MalformedQueryException("Malformed geo:geometry value");
		String[] lines = m.group().split("\\),");
		if(m.find())
			throw new MalformedQueryException("Malformed geo:geometry value");
		for(int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();
			if(line.equals(""))
				continue;
			if(i == lines.length-1)
				line = line.substring(1, line.length()-1);
			else
				line = line.substring(1);
		
			polyCoords = validateGeometryPointCollection(param, line);
			coords.addAll(polyCoords);

			if(polyCoords.size() < 8 || polyCoords.size()%2 != 0)
				throw new MalformedQueryException("Malformed geo:geometry value: Wrong number of coordinates");
			if(!polyCoords.get(0).equals(polyCoords.get(polyCoords.size()-2)) || !polyCoords.get(1).equals(polyCoords.get(polyCoords.size()-1)))
				throw new MalformedQueryException("Malformed geo:geometry value: Polygon is not closed");
	
		}
		return coords;
	}
	
	private List<String> validateGeometryMultiPolygon(String param, String geometricObject) throws MalformedQueryException {
		Pattern p = Pattern.compile("^(\\((\\(([\\d\\.\\+-]+ [\\d\\.\\+-]+(,[\\s]*)?)+\\)(,[\\s]*)?)+\\)(,[\\s]*)?)+$");
		Matcher m = p.matcher(geometricObject);
		List<String> coords = new ArrayList<String>();
		if(!m.find())
			throw new MalformedQueryException("Malformed geo:geometry value");
		String[] polygons = m.group().split("\\)\\),");
		if(m.find())
			throw new MalformedQueryException("Malformed geo:geometry value");
		for(int i = 0; i < polygons.length; i++) {
			String polygon = polygons[i].trim();
			if(polygon.equals(""))
				continue;
			if(i == polygons.length-1)
				polygon = polygon.substring(1, polygon.length()-1);
			else
				polygon = polygon.substring(1) + ')';
			coords.addAll(validateGeometryPolygon(param, polygon));
		}
		return coords;
	}

	
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#getQuery()
	 */
	public String getQuery() throws IncompleteQueryException, MalformedQueryException, Exception {

		validateQuery();
		return qb.getQuery();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#setParameters(QueryElement)
	 */
	public QueryBuilder setParameters(QueryElement queryEl) throws NonExistentParameterException, Exception    {

		Map<String, String> m = queryEl.getQueryParameters();
		for(Map.Entry<String, String> e : m.entrySet())
			setParameter(e.getKey(), e.getValue());

		return this;
	}
	
	public static void main(String[] args) throws Exception {
		Map<String, String> nsPrefixes = new HashMap<String, String>();
		nsPrefixes.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#","rdf");
		nsPrefixes.put("http://www.genesi-dr.eu/spec/opensearch/extensions/eop/1.0/","eop");
		nsPrefixes.put("http://a9.com/-/opensearch/extensions/time/1.0/","time");
		nsPrefixes.put("http://a9.com/-/opensearch/extensions/geo/1.0/","geo"); 
		nsPrefixes.put("http://earth.esa.int/sar","sar"); 
	    nsPrefixes.put("http://purl.org/dc/elements/1.1/","dc");
		nsPrefixes.put("http://purl.org/dc/terms/","dct");
		nsPrefixes.put("http://xmlns.com/2008/dclite4g#","dclite4g"); 
		nsPrefixes.put("http://www.w3.org/2002/12/cal/ical#","ical");
		nsPrefixes.put("http://www.w3.org/2005/Atom","atom");
		nsPrefixes.put("http://www.example.com/schemas/envisat.rdf#","envisat");
		nsPrefixes.put("http://www.w3.org/2002/07/owl#","owl"); 
		nsPrefixes.put("http://downlode.org/Code/RDF/file-properties/","fp"); 
		nsPrefixes.put("http://dclite4g.xmlns.com/ws.rdf#", "ws"); 
		nsPrefixes.put("http://a9.com/-/spec/opensearch/1.1/", "os");
		nsPrefixes.put("http://www.eorc.jaxa.jp/JERS-1/en/", "jers"); 
		nsPrefixes.put("http://a9.com/-/opensearch/extensions/sru/2.0/", "sru"); 
		
		//String geometricObject = "POLYGON((0.582 40.496, 0.231 40.737, 0.736 42.869, 3.351 42.386, 3.263 41.814, 2.164 41.265, 0.978 40.957, 0.802 40.781, 0.978 40.649, 0.582 40.496), (1 2,3 4, 5 6, 1 2))";
		String geometricObject = "MULTIPOLYGON(((0.582 40.496, -0.231 40.737, 0.736 42.869, -3.351 42.386, 3.263 41.814, 2.164 41.265, 0.978 40.957, 0.802 40.781, 0.978 40.649, 0.582 40.496), (1 2,3 4, 5 6, 1 2)), ((3 4, 5 6, 7 8, 3 4), (1 1, 2 2, 3 3, 4 4, 5 5, 6 6, 7 7, 1 1)))";
		//String geometricObject = "POINT(0.582 40.496)";
		//String geometricObject = "LINESTRING(0.582 40.496, 167.878 85.767)";
		//String geometricObject = "MULTIPOINT(3.5 5.6, 4.8 10.5)";
		//String geometricObject = "MULTILINESTRING((3 4,10 50,20 25),(-5 -8,-10 -8,-15 -4))";
		long start = Calendar.getInstance().getTimeInMillis();
		URLTemplate template = new BasicURLTemplate("http://www.testGeoExt.com/rdf/?count={count?}&startPage={startPage?}&startIndex={startIndex?}&sort={sru:sortKeys?}&ce={ws:ce?}&protocol={ws:protocol?}&resourcetype={ws:type?}&q={searchTerms?}&start={time:start?}&stop={time:end?}&ingested={dct:modified?}&bbox={geo:box?}&geometry={geo:geometry?}&uid={geo:uid?}&processingCenter={eop:processingCenter?}&acquisitionStation={eop:acquisitionStation?}&size={eop:size?}&orbitNumber={eop:orbitNumber?}&trackNumber={eop:trackNumber?}&lat={geo:lat?}&lon={geo:lon?}&radius={geo:radius?}", nsPrefixes);
		for(int i = 0; i < 1000; i++) {
			GeoQueryBuilder qb = new GeoQueryBuilder(new BasicQueryBuilder(template, "1", "1"));
			qb.setParameter(GeoConstants.geometryQname, java.net.URLEncoder.encode(geometricObject, "UTF-8"));
		//	qb.setParameter(GeoConstants.boxQname, "0.582,40.245,0.231,40.737");
		//	qb.setParameter(GeoConstants.lonQname, "-179.0");
		//	qb.setParameter(GeoConstants.latQname, "84.0");
		//	qb.setParameter(GeoConstants.radiusQname, "100");
			String q = qb.getQuery();
			//System.out.println(qb.getQuery());
			if(i % 100 == 0)
				System.out.println(i*100 + "th record in " + (Calendar.getInstance().getTimeInMillis() - start) + " millis");
		}
	}
}
