package org.gcube.dataanalysis.geo.connectors.wfs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.gcube.contentmanagement.graphtools.utils.HttpRequest;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.geo.meta.OGCFormatter;
import org.gcube.dataanalysis.geo.utils.JsonMapper;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.operation.overlay.snap.GeometrySnapper;

public class WFSDataExplorer {

	private static String callWFS(String geoServer, String layer, double x, double y) {

		float tolerance = 0.25f;
		String wfsURL = OGCFormatter.getWfsUrl(geoServer, layer, OGCFormatter.pointToBoundingBox(x, y, tolerance), 1, "json");
		AnalysisLogger.getLogger().debug("WFSDataExplorer-> Requesting URL: " + wfsURL);
		String returned = null;
		try {
			returned = HttpRequest.sendGetRequest(wfsURL, null);
		} catch (Exception e) {
			AnalysisLogger.getLogger().debug("WFSDataExplorer-> ERROR " + e.getLocalizedMessage());
		}
		if (returned != null)
			// AnalysisLogger.getLogger().debug("EnvDataExplorer-> Found Intersection: " + returned);
			AnalysisLogger.getLogger().debug("WFSDataExplorer-> Found Intersection");
		else
			AnalysisLogger.getLogger().debug("WFSDataExplorer-> Found Nothing!");

		return returned;
	}

	private static String callWFS(String geoServer, String layer, double xL, double yL, double xR, double yR) {

		// String wfsURL = OGCFormatter.getWfsUrl(geoServer, layer, OGCFormatter.buildBoundingBox(xL, yL, xR, yR), 0, "json");
		// there is a bug in WFS in the retrieval according to a bounding box: y must be in the range -180;180. then I preferred to take all the features
		//"51,-120,57,-106"
		String bbox = null;
//		bbox = "60,0,90,180";
		
		String wfsURL = OGCFormatter.getWfsUrl(geoServer, layer, bbox, 0, "json");
		AnalysisLogger.getLogger().debug("WFSDataExplorer-> Requesting URL: " + wfsURL);
		String returned = null;
		try {
			returned = HttpRequest.sendGetRequest(wfsURL, null);
		} catch (Exception e) {
			AnalysisLogger.getLogger().debug("WFSDataExplorer-> ERROR " + e.getLocalizedMessage());
		}
		if (returned != null)
			// AnalysisLogger.getLogger().debug("EnvDataExplorer-> Found Intersection: " + returned);
			AnalysisLogger.getLogger().debug("WFSDataExplorer-> Found Intersection");
		else
			AnalysisLogger.getLogger().debug("WFSDataExplorer-> Found Nothing!");

		return returned;
	}

	public static LinkedHashMap<String, Double> getFeatures(String geoserver, String layer, double x, double y) {
		try {
			AnalysisLogger.getLogger().debug("Calling WFS towards Geoserver:" + geoserver + " and layer:" + layer);
			String jsonString = callWFS(geoserver, layer, x, y);
			LinkedHashMap<String, Object> map = JsonMapper.parse(jsonString);
			LinkedHashMap<String, String> mapout = (LinkedHashMap<String, String>) ((HashMap<String, Object>) map.get("features")).get("properties");
			LinkedHashMap<String, Double> values = new LinkedHashMap<String, Double>();
			for (String key : mapout.keySet()) {
				values.put(key, Double.parseDouble(mapout.get(key)));
			}
			return values;
		} catch (Exception e) {
			AnalysisLogger.getLogger().debug("WFSDataExplorer-> Error in getting properties");
			return null;
		}
	}

	public static List<FeaturedPolygon> getFeatures(String geoserver, String layer, double xL, double yL, double xR, double yR) {
		try {
			AnalysisLogger.getLogger().debug("Calling WFS towards Geoserver:" + geoserver + " and layer:" + layer);
			String jsonString = callWFS(geoserver, layer, xL, yL, xR, yR);
			// System.out.println("JSON:"+jsonString);
			LinkedHashMap<String, Object> map = JsonMapper.parse(jsonString);
//			System.out.println(map);
			List<FeaturedPolygon> fpolygons = new ArrayList<FeaturedPolygon>();
			FeaturedPolygon poly = null;
			int polygonId = 0;
			for (String key : map.keySet()) {
				if (key.contains("features")) {
					HashMap<String, Object> propertiesMap = (HashMap<String, Object>) map.get(key);
					
					// cycle on all the properties
					for (String properties : propertiesMap.keySet()) {
						if (properties.contains("properties")) {
							polygonId++;
							if (poly == null)
								poly = new FeaturedPolygon();
							
							LinkedHashMap<String, Object> props = (LinkedHashMap<String, Object>) propertiesMap.get(properties);
							// fill the properties of the fpolygon
							for (String keyprop : props.keySet()) {
								try {
									// fulfill the FeaturedPolygon
									String value = (""+props.get(keyprop)).replace("{", "").replace("}", "");
									try {
										String lowcaseprop = keyprop.toLowerCase();
//										System.out.println(poly.p.getCentroid()+" -> "+value);
										//add the first double value to the polygon
										if ((poly.value == null) && !lowcaseprop.startsWith("id") && !lowcaseprop.endsWith("id")){
											poly.setValue((double)polygonId);
											poly.addFeature(keyprop, value);
										}
										else
											poly.addFeature(keyprop, value);
									} catch (Exception e2) {
										poly.addFeature(keyprop, value);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						} else if (properties.contains("geometry") && !properties.contains("geometry_")) {

							if (poly == null)
								poly = new FeaturedPolygon();
							else if (poly.p != null) {
								if (poly.value == null)
									poly.value = Double.NaN;
								fpolygons.add(poly);
								poly = new FeaturedPolygon();
							}

							LinkedHashMap<String, String> props = (LinkedHashMap<String, String>) propertiesMap.get(properties);
							List<double[]> coords = WFS2Coordinates(props.toString());
							Geometry p = buildGeometryFromCoordinates(coords);

							if (p != null) {
								poly.setPolygon(p);
//								AnalysisLogger.getLogger().trace("Setting polygon p");
								// AnalysisLogger.getLogger().trace(p);
							}
							/*
							 * GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326); Polygon p = null; if (coords != null) {
							 * 
							 * Coordinate[] coordarray = new Coordinate[coords.size()]; int i = 0; for (double[] pair : coords) { coordarray[i] = new Coordinate(pair[0], pair[1]);
							 * 
							 * i++; } // TODO: build a multipoly if the ring is not closed! CoordinateArraySequence coordseq = new CoordinateArraySequence(coordarray); LinearRing ring = new LinearRing(coordseq, factory); p = new Polygon(ring, new LinearRing[] {}, factory); } poly.setPolygon(p);
							 */
						}

					}
				}
			}// end for on all the wfs

			if (poly != null) {
				if (poly.value == null)
					poly.value = Double.NaN;
				fpolygons.add(poly);
			}
			return fpolygons;
		} catch (Exception e) {
			AnalysisLogger.getLogger().debug("WFSDataExplorer-> Error in getting properties");
			e.printStackTrace();
			return null;
		}
	}

	public static Geometry buildGeometryFromCoordinates(List<double[]> coords) {
		Geometry p = null;
		GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

		int idx = -1;
		List<Coordinate> coordinatesArray = new ArrayList<Coordinate>();
		List<Polygon> polys = new ArrayList<Polygon>();
		List<LinearRing> holespolys = new ArrayList<LinearRing>();
		int j = 1;
		int i = 1;

		for (double[] pair : coords) {
			Coordinate coordPair = new Coordinate(pair[0], pair[1]);
			if ((idx = coordinatesArray.indexOf(coordPair)) >= 0) {
				// System.out.println("List Contains: "+coordinatesArray.get(idx));
				coordinatesArray.add(coordPair);
				if (idx == 0) {
					// System.out.println("Switching polygon: "+j+" "+coordinatesArray.get(idx));
					j++;
					i = 1;
					Polygon pp = sequence2Polygon(factory, coordinatesArray.subList(idx, coordinatesArray.size()), holespolys);

					// System.out.println(pp);
					polys.add(pp);
					coordinatesArray = new ArrayList<Coordinate>();
				} else {
					// System.out.println("Linear Ring "+i + " "+coordinatesArray.get(idx));
					i++;
					LinearRing ring = sequence2Ring(factory, coordinatesArray.subList(idx, coordinatesArray.size()));
					holespolys.add(ring);
					coordinatesArray = coordinatesArray.subList(0, idx);
				}
			} else
				coordinatesArray.add(coordPair);

		}

		// build a multipoly if the ring is not closed!

		if (polys.size() > 0) {
			// cut the holes
			List<Polygon> polysnoholes = new ArrayList<Polygon>();
			for (Polygon pp : polys) {
				
				boolean found = false;
				int h = 0;
				while(h<polysnoholes.size()) {
					Polygon polnh  = polysnoholes.get(h);
					boolean covers = false;
					
					try{
						covers  = polnh.covers(pp);
					}catch(Exception e){
						AnalysisLogger.getLogger().debug("Error in calculating superpositions: Snapping the geometries");
						double snapTol = GeometrySnapper.computeOverlaySnapTolerance(polnh, pp);
						pp = (Polygon) selfSnap(pp,snapTol);
						polnh = (Polygon) selfSnap(polnh,snapTol);
						AnalysisLogger.getLogger().debug("Geometries have been snapped");
						covers  = polnh.covers(pp);
					}
					
					if (covers) {
						// System.out.println("found hole! "+pp+" vs "+polnh);
						h=addDifference(h, polysnoholes, polnh, pp);
						found = true;
					} else if (pp.covers(polnh)) {
//						polysnoholes.set(h, (Polygon) pp.difference(polnh));
						h=addDifference(h, polysnoholes, pp, polnh);
						found = true;
					}
					h++;
				}
				if (!found)
					polysnoholes.add(pp);
			}
			Polygon[] polyrawarray = polysnoholes.toArray(new Polygon[polysnoholes.size()]);
			p = new MultiPolygon(polyrawarray, factory);
		}

		return p;
	}

	
	private static Geometry selfSnap(Geometry g, double snapTolerance)
	{
		GeometrySnapper snapper = new GeometrySnapper(g);
		Geometry snapped = snapper.snapTo(g, snapTolerance);
		// need to "clean" snapped geometry - use buffer(0) as a simple way to do this
		Geometry fix = snapped.buffer(0);
		return fix;
	}
	
	
	private static int addDifference(int h , List<Polygon> polysnoholes, Polygon polnh, Polygon pp){
			
		Geometry mp = polnh.difference(pp);
		if (mp instanceof com.vividsolutions.jts.geom.Polygon)
			polysnoholes.set(h, (Polygon) mp);
		else {
			MultiPolygon mup = (MultiPolygon) mp;
			int innerpolygons = mup.getNumGeometries();
			for (int k = 0; k < innerpolygons; k++) {
				Polygon ip = (Polygon) mup.getGeometryN(k);
				if (k==0)
					polysnoholes.set(h, ip);
				else
					polysnoholes.add(h, ip);
				h++;
			}
			if (innerpolygons>0)
				h--;//set the cursor on the last element
		}
		return h;

	}
	
	
	private static LinearRing sequence2Ring(GeometryFactory factory, List<Coordinate> coordinatesArray) {
		// System.out.println(coordinatesArray);
		Coordinate[] coordrawarray = coordinatesArray.toArray(new Coordinate[coordinatesArray.size()]);
		CoordinateArraySequence coordseq = new CoordinateArraySequence(coordrawarray);
		LinearRing ring = new LinearRing(coordseq, factory);

		return ring;
	}

	private static Polygon sequence2Polygon(GeometryFactory factory, List<Coordinate> coordinatesArray, List<LinearRing> holespolys) {
		// System.out.println(coordinatesArray);
		Coordinate[] coordrawarray = coordinatesArray.toArray(new Coordinate[coordinatesArray.size()]);
		LinearRing[] holes = holespolys.toArray(new LinearRing[holespolys.size()]);

		CoordinateArraySequence coordseq = new CoordinateArraySequence(coordrawarray);
		LinearRing ring = new LinearRing(coordseq, factory);

		Polygon p = new Polygon(ring, holes, factory);
		return p;
	}

	public static List<double[]> WFS2Coordinates(String wfsgeometry) {

		// geometry935133b1-ba3c-493d-8e18-6fb496ced995={type=MultiPolygon, coordinates={966a275c-23aa-4a43-a943-7e1c7eaf5d65=[[[1.5,125.00000000000011],[1.5,124.5],[2.000000000000057,124.5],[2.000000000000057,125.00000000000011],[1.5,125.00000000000011]]]}},
		String[] coordinatePairs = null;
		List<double[]> dpairs = new ArrayList<double[]>();
		if (wfsgeometry.toLowerCase().contains("multipolygon")||wfsgeometry.toLowerCase().contains("polygon")) {
			String coordString = "coordinates=";
			String coordinates = wfsgeometry.substring(wfsgeometry.indexOf(coordString) + coordString.length());
			coordinates = coordinates.substring(coordinates.indexOf("=") + 1);
			if (coordinates.contains("=")) {
				coordinates = coordinates.replaceAll("([A-Za-z0-9]|-|_)+=", "");
				coordinates = coordinates.replaceAll("\\],( )+\\[", "],[");
			}
			coordinatePairs = coordinates.split("\\],\\[");
			for (String coord : coordinatePairs) {
				coord = coord.replaceAll("(\\[|\\]|\\}|\\{|)", "");
				String[] coordpair = coord.split(",");
				double[] dd = new double[2];
				// invert the coordinates as the final must be are long,lat
				
				dd[1] = Double.parseDouble(coordpair[0]);
				dd[0] = Double.parseDouble(coordpair[1]);
				
				/*
				dd[1] = Double.parseDouble(coordpair[1]);
				dd[0] = Double.parseDouble(coordpair[0]);
				*/
				dpairs.add(dd);
			}
		}
		return dpairs;
	}

	public static void main1(String[] args) {

		String geom = "{type=MultiPolygon, coordinates={cce4daf3-966e-4b5f-adea-f88ea2b93d03=[[[-16,-146.49999999999997],[-16,-146.99999999999994],[-15.5,-146.99999999999994],[-15.5,-146.49999999999997],[-16,-146.49999999999997]]]}}";
		List<double[]> coords = WFS2Coordinates(geom);

		GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
		// GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 0);
		/*
		 * CoordinateArraySequence coords = new CoordinateArraySequence(new Coordinate[] { new Coordinate(12.0, 34.23), new Coordinate(12.000, 54.555), new Coordinate(7, 8), new Coordinate(12.0, 34.23) }); LinearRing ring = new LinearRing(coords, factory); Polygon p = new Polygon(ring, null, factory); CoordinateArraySequence pcoords = new CoordinateArraySequence(new Coordinate[] { new Coordinate(12.0, 34.23),});
		 */
		// CoordinateArraySequence coords = new CoordinateArraySequence(new Coordinate[] { new Coordinate(1.5, 125.00000000000011), new Coordinate(1.5, 124.5), new Coordinate(2.000000000000057, 124.5), new Coordinate(2.000000000000057, 125.00000000000011), new Coordinate(1.5, 125.00000000000011) });

		if (coords != null) {
			Coordinate[] coordarray = new Coordinate[coords.size()];
			int i = 0;
			for (double[] pair : coords) {
				coordarray[i] = new Coordinate(pair[0], pair[1]);
				i++;
			}
			CoordinateArraySequence coordseq = new CoordinateArraySequence(coordarray);
			LinearRing ring = new LinearRing(coordseq, factory);
			Polygon p = new Polygon(ring, new LinearRing[] {}, factory);
			// CoordinateArraySequence pcoords = new CoordinateArraySequence(new Coordinate[] { new Coordinate(-16,-146.49999999999997), });
			CoordinateArraySequence pcoords = new CoordinateArraySequence(new Coordinate[] { new Coordinate(-150, -16), });
			Point po = new Point(pcoords, factory);
			// po = p.getCentroid();
			System.out.println("contains: " + p.contains(po) + " boundary: " + p.covers(po));
		}
	}

	
	
	public static void main(String[] args) {

		String q = "[[[-10.0011869534696,151.288335840039],[-10.0353384533966,151.27859643813],[-10.0228061679999,151.308700562],[-10.0011869534696,151.288335840039]]], e3c47901-3de5-45d2-a272-c6f7d5df1dec=[[[-8.54674625399991,150.53036499],[-8.83403205899992,150.287445068],[-9.20889866086486,150.195933942647],[-9.20555999999993,150.21039],[-9.20777999999995,150.23218],[-9.27360999999991,150.33095],[-9.38638999999995,150.37717],[-9.39873372345699,150.375441317138],[-9.37888717699991,150.41633606],[-9.64140796699991,150.411376953],[-9.68103313399996,150.684051514],[-9.79481071047286,150.758883440934],[-9.74832999999995,150.75027],[-9.73082999999991,150.74884],[-9.70784999999995,150.76262],[-9.7194399999999,150.78802],[-9.73138999999991,150.80304],[-9.74693999999994,150.82163],[-9.81916999999993,150.90026],[-9.85235999999992,150.93539],[-9.89360999999991,150.96274],[-9.98527999999993,151.03055],[-9.99693999999994,151.03943],[-10.0169399999999,151.05996],[-10.0244399999999,151.07303],[-10.0466,151.11809],[-10.0413899999999,151.13666],[-10.03014,151.14818],[-10.0194499999999,151.14875],[-10.0033999999999,151.13893],[-9.98916999999994,151.13637],[-9.94207999999991,151.18817],[-9.93666999999993,151.20053],[-9.93091343037411,151.222140060489],[-9.68598556499995,150.991424561],[-9.45813846599992,150.936889648],[-9.30954170199993,151.03604126],[-9.13122558599991,150.961669922],[-8.80926608999994,151.055862427],[-8.66848054747773,151.099704833311],[-8.63888999999995,151.10107],[-8.56673125859819,151.063276911059],[-8.52198028599992,150.922012329],[-8.54674625399991,150.53036499]],[[-9.43832999999995,150.66666],[-9.44124999999991,150.67997],[-9.42805999999996,150.73191],[-9.42055999999991,150.7462],[-9.40541999999993,150.7615],[-9.41471999999993,150.77777],[-9.43277999999992,150.80442],[-9.45638999999994,150.8283],[-9.52319999999992,150.88692],[-9.64471999999995,150.93219],[-9.65916999999996,150.93055],[-9.67082999999991,150.92163],[-9.68207999999993,150.90387],[-9.67221999999992,150.89177],[-9.67916999999994,150.87523],[-9.71805999999992,150.84692],[-9.68555999999995,150.84412],[-9.65860999999995,150.80163],[-9.66249999999991,150.76331],[-9.66332999999991,150.69135],[-9.66291999999993,150.65804],[-9.65388999999993,150.62274],[-9.62332999999995,150.51443],[-9.5836099999999,150.4905],[-9.44082999999995,150.42746],[-9.4313899999999,150.42331],[-9.41471999999993,150.41999],[-9.40110999999996,150.41999],[-9.38943999999992,150.4219],[-9.37666999999993,150.42609],[-9.35707999999994,150.43913],[-9.33770999999996,150.48044],[-9.34124999999994,150.5022],[-9.35166999999995,150.53028],[-9.37054999999992,150.57135],[-9.38499999999993,150.59802],[-9.40110999999996,150.62149],[-9.4233299999999,150.63734],[-9.43832999999995,150.66666]]], c905ab63-23c2-4587-bdd6-d6d37a56be51=[[[-8.58588343092737,151.123734225448],[-8.59127089890423,151.123748898655],[-8.58637142199996,151.125274658],[-8.58588343092737,151.123734225448]]], 8471299d-4904-4a10-ab00-c6cc5605bf3b=[[[-10.1228941076499,151.06827675758],[-10.1141699999999,151.02582],[-10.1108299999999,150.99831],[-10.1127799999999,150.98331],[-10.1127665622499,150.982996372512],[-10.1466360089999,151.011245728],[-10.1228941076499,151.06827675758]]], d0a0b923-b401-4cec-ac35-c3d8c837bffc=[[[-10.0506772730004,150.931209804608],[-10.0041699999999,150.91553],[-9.92666999999994,150.87774],[-9.83888999999993,150.8269],[-9.80718113528387,150.767019514441],[-10.0277585979999,150.912094116],[-10.0506772730004,150.931209804608]]]}}";
		q = q.replaceAll("([A-Za-z0-9]|-|_)+=", "");
		// q = q.replaceAll("\\], .*=\\[", "],[");
		System.out.println(q);
		q = q.replaceAll("\\],( )+\\[", "],[");
		System.out.println(q);
	}
}
