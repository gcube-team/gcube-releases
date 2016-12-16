package org.gcube.dataanalysis.ecoengine.models.cores.aquamaps;

import java.util.List;

public class SpEnv_temp extends AquamapsEnvelope {

	/*
	$strSQL="SELECT DISTINCT speciesoccursum.CsquareCode, speciesoccursum.SpeciesID, HCAF.$fld
		FROM speciesoccursum INNER JOIN HCAF ON speciesoccursum.CsquareCode = HCAF.CsquareCode
		WHERE speciesoccursum.SpeciesID = ' .. ' 
		AND HCAF.$fld <> -9999
		AND HCAF.$fld is not null
		AND HCAF.OceanArea > 0
		AND speciesoccursum.inc = 'y' 
		ORDER BY HCAF.$fld";
*/
	//###################################################################################
	//This file re-computes the temperature values (Min, PrefMin, Max, PrefMax based on 
	//area restriction parameters set by the user
	//###################################################################################
	public void calcEnvelope(String $layer, List<Object> speciesOccurrences){
	
	calculatePercentiles(speciesOccurrences, $TempUpper, $TempLower);
	
	double $spreadVal = 0;
	if (Max <= 5) //then polar and deepwater species
	{ $spreadVal = 0.25; }
	else	{ $spreadVal = 1; }


	if ((PMax - PMin) < $spreadVal)
	{				
	    double $ParaMid = (PMin + PMax) / 2f;                				
		double $PMinTmp = $ParaMid - ($spreadVal / 2f);				
		double $PMaxTmp = $ParaMid + ($spreadVal / 2f);

		//enforce a minimum preferred range as long as it doesn't extrapolate outer limits
	    if ($PMinTmp < Min)	
		{	
			//preferred Min value as is 
		}
	    else	
		{
	    	PMin = $PMinTmp;
		}             
	    
		if ($PMaxTmp > Max)	
		{	
			//preferred Max value as is
		}
	    else 
		{
	    	PMax = $PMaxTmp;
		}
	}               

	//check difference between min/max and pref. min/max
	if (PMin - Min < 0.5)
	{     
		double $MinTmp = PMin - 0.5;
	    if ($MinTmp > $TempLower){Min = $MinTmp;}
	    else {Min = $TempLower;}
	}          

	if (Max - PMax < 0.5)
	{     
		double $MaxTmp = PMax + 0.5;
	    if ($MaxTmp < $TempUpper){Max = $MaxTmp;}
	    else {Max = $TempUpper;}
	}
	//check if envelope is as broad as pre-defined minimum
	if (PMax >= 25)
	{
		Max = PMax + 4.2;				
	}
	
	}
	
	
	public String toString(){
		String exitString = "tempmin='"+Min+"'," + 
			"tempprefmin='"+PMin+"'," + 
			"tempprefmax='"+PMax+"'," + 
			"tempmax='"+Max+"'"; 
			
		return exitString;
	}
	
}
