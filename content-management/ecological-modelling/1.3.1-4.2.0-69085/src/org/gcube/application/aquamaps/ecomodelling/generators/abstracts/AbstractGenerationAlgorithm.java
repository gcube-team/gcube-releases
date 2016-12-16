package org.gcube.application.aquamaps.ecomodelling.generators.abstracts;

import java.util.HashMap;

import org.gcube.application.aquamaps.ecomodelling.generators.aquamapsorg.AquamapsAlgorithm;

public abstract class AbstractGenerationAlgorithm {
	
	
	//gets the initialization value for a string object
	public static String getElement(Object[] featuresVector,int index){
		if (featuresVector[index] != null) return ""+featuresVector[index];
		else return null;
	}
	
	//gets the initialization value for a numeric object
	public static double getNumber(Object[] featuresVector,int index){
		
		double number = -9999;
		try{
			number = Double.parseDouble(""+featuresVector[index]);
		}catch(Exception e){}
		
		return number;
	}
	
	//get BB for a Species
	//Input
	/*
	 * North  latitude
	 * South most
	 * West most latitude
	 * East most latitude
	 * Array of max min longitude for a species: maximum latitute, minimum latitude, speciesid (e.g fis-1234)
	 * layer information (s or b)
	 */
	//Output 
	/*
	 * Map of values like:
	 * $pass_NS
		$pass_N,
		$pass_S,
		$paramData_NMostLat,
		$paramData_SMostLat,
		$paramData_WMostLong,
		$paramData_EMostLong,
		$northern_hemisphere_adjusted,
		$southern_hemisphere_adjusted
	 */
	public abstract HashMap<String,String> getBoundingBoxInfo(String $paramData_NMostLat,String $paramData_SMostLat,String $paramData_WMostLong, String $paramData_EMostLong, Object[] maxMinLat,String $type);
	
	//calculate BB and FAOAreas flags for (species,csquare)
	//Input:
	/*
	 * parameters from the bounding box information
	 * csquarecode e.g. 1000:100:10:1
	 * center longitude of a square 
	 * center latitude of a square
	 */
	//Output:
	/*
	 * $InBox : 1 or 0
	 * $InFAO: 1 or 0
	 * 
	 */
	public abstract HashMap<String,Integer> calculateBoundingBox(String csquarecode,String $pass_NS,String $pass_N,String $pass_S,String $CenterLat,String $CenterLong,String $FAOAreaM,String $paramData_NMostLat, String $paramData_SMostLat,String $paramData_WMostLong,String $paramData_EMostLong,String $paramData_FAOAreas,String $northern_hemisphere_adjusted, String $southern_hemisphere_adjusted);

	//Probability calculation - initializes and calculates
	//INPUT:
	//one vector of features (numbers) for a species (hspen)
	//one vector of the same features associated to a single csquare (one hcaf row)
	//OUTPUT: a number representing a probability
	public abstract double getSpeciesProb(Object[] speciesFeatures,Object[] csquareFeatures);

	//accessory procedure
	//input : a list of FAO Areas separated by commas. e.g. 31,27,48
	//output: a list possibily enriched of FAO areas
	public abstract String procFAO_2050(String $temp);
}
