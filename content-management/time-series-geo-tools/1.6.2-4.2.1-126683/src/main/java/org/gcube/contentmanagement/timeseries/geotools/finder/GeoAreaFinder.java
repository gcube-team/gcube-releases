package org.gcube.contentmanagement.timeseries.geotools.finder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.timeseries.geotools.databases.ConnectionsManager;

/**
 * 
 * This class has the duty to find the area associated to an input string
 * 
 * @author coro
 * 
 */
public class GeoAreaFinder {
	ConnectionsManager connManager;
	String ref_country;
	private static final String getAreaCodeQuery = "SELECT un_code FROM %ref_country% where iso_3_code = '%1$s' or iso_2_code = '%1$s' or name_en = '%1$s' or name_en = '%1$s' or name_fr = '%1$s' or name_es = '%1$s' or name_ar = '%1$s' or name_zh = '%1$s' or name_ru = '%1$s';";
	private static final String getCsquaresQuery = "select csquarecode from hcaf_s where ((countrymain = '%1$s' or countrysecond = '%1$s' or countrythird = '%1$s' or eezfirst = '%1$s' or eezsecond = '%1$s' or eezthird = '%1$s') and oceanarea>0);";
	private static final String getCsquaresfromAreasQuery = "select csquarecode from hcaf_s where faoaream='%1$s';";
	private static final String getGeometryQuery = "SELECT the_geom FROM new_all_world where csquarecode = '%1$s';";

	public GeoAreaFinder(ConnectionsManager connManager,String ref_country) {
		this.connManager = connManager;
		this.geometries = new HashMap<String, String>();
		this.csquarecodesMap = new HashMap<String, List<String>>();
		this.ref_country = ref_country;
	}

	// associations between places and csquarecodes
	Map<String, List<String>> csquarecodesMap;

	// gets the csquare code associated to the country name, by means of the universal code
	public List<String> findCSquareCodes(String inputArea,List<String> previousCsquareCodes) throws Exception {
		List<String> csquareCodes = csquarecodesMap.get(inputArea);
		if (csquareCodes == null) {
			csquareCodes = new ArrayList<String>();
		
				// get the numeric code for the inputArea from time series
				String getAreaQuery = String.format(getAreaCodeQuery.replace("%ref_country%",ref_country),inputArea.replace("\'", "\\\'"));
				AnalysisLogger.getLogger().trace("findCSquareCodes->Conversion Table Query: "+getAreaQuery);
				List<Object> ucode = connManager.AquamapsQuery(getAreaQuery);
				if ((ucode != null) && (ucode.size() > 0)) {
					
					String universal_code = "" + ucode.get(0);
					int unicodelen = universal_code.length();
					if (unicodelen==1)
						universal_code = "00"+universal_code;
					else if (unicodelen==2)
						universal_code = "0"+universal_code;
					
					// search for csquare codes on the aquamaps table
					AnalysisLogger.getLogger().trace("findCSquareCodes->CSquares of the place "+universal_code+" - "+inputArea+" getting with the query:"+String.format(getCsquaresQuery, universal_code));
					List<Object> aquamapscsquarecodes = connManager.AquamapsQuery(String.format(getCsquaresQuery, universal_code));
					if (aquamapscsquarecodes!=null){
						AnalysisLogger.getLogger().trace("findCSquareCodes->OK - FOUND "+aquamapscsquarecodes.size()+" ELEMENTS");
						for (Object csquare : aquamapscsquarecodes) {
							csquareCodes.add((String) csquare);
						}
					}
					else
					{
						AnalysisLogger.getLogger().trace("findCSquareCodes->NULL VALUE ON PLACE "+inputArea);
//						System.exit(0);
					}
				}
				csquarecodesMap.put(inputArea, csquareCodes);
		
		}
		
		csquareCodes = filterCsquares(previousCsquareCodes,csquareCodes);
		return csquareCodes;
	}

	private List<String> filterCsquares(List<String> filtercodes,List<String> codes){
		List<String> outList = new ArrayList<String>();
		if (filtercodes != null){
			for (String fcode:filtercodes){
				if (codes.contains(fcode))
					outList.add(fcode);
			}
			return outList;
		}else{
			return codes;
		}
	}
	
	// associations between csquarecode and geometries
	Map<String, String> geometries;

	// gets the geometries associated to a csquare Code
	public List<String> getGeometries(List<String> csquareCodes) throws Exception {
		List<String> geos = new ArrayList<String>();
		for (String square : csquareCodes) {
			String geometry = geometries.get(square);
			if (geometry == null) {
				List<Object> geom = connManager.GeoserverQuery(String.format(getGeometryQuery, square));
				geometry = geom.get(0).toString();
				geometries.put(square, geometry);
			}
			geos.add(geometry);
		}

		return csquareCodes;
	}
	
	
	// gets the csquare code associated to the country name, by means of the universal code
	public List<String> findDirectlyCSquareCodes(String FAOAreaCode,List<String> previousCsquareCodes) {
		List<String> csquareCodes = csquarecodesMap.get(FAOAreaCode);
		
		if (csquareCodes == null) {
			csquareCodes = new ArrayList<String>();
			try {
				// get the numeric code for the inputArea from time series
				AnalysisLogger.getLogger().trace("findCSquareCodes->Aquamaps Query: "+String.format(getCsquaresfromAreasQuery, FAOAreaCode));
				List<Object> aquamapscsquarecodes = connManager.AquamapsQuery(String.format(getCsquaresfromAreasQuery, FAOAreaCode));
				if (aquamapscsquarecodes!=null){
						for (Object csquare : aquamapscsquarecodes) {
							csquareCodes.add((String) csquare);
						}
				}
				else{
						AnalysisLogger.getLogger().trace("findCSquareCodes->NULL VALUE ON FAO-AREA "+FAOAreaCode);
//						System.exit(0);
					}
				
				csquarecodesMap.put(FAOAreaCode, csquareCodes);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		csquareCodes = filterCsquares(previousCsquareCodes,csquareCodes);
		return csquareCodes;
	}
	
}
