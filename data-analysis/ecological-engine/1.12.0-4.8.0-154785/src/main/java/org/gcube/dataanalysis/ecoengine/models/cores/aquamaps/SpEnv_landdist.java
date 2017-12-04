package org.gcube.dataanalysis.ecoengine.models.cores.aquamaps;

import java.util.List;

public class SpEnv_landdist extends AquamapsEnvelope {

	/*
$strSQL="SELECT DISTINCT $oc_var.CsquareCode, $oc_var.SpeciesID, HCAF.LandDist
FROM $oc_var INNER JOIN HCAF ON $oc_var.CsquareCode = HCAF.CsquareCode
WHERE $oc_var.SpeciesID = '" . $row['SpeciesID'] . "' 
AND HCAF.LandDist <> -9999
AND HCAF.LandDist is not null
AND HCAF.OceanArea > 0
AND $oc_var.inc = 'y' 
ORDER BY HCAF.LandDist";
	*/
	
	public void calcEnvelope(List<Object> speciesOccurrences){
		calculatePercentiles(speciesOccurrences, $LandUpper, $LandLower);
		
		//check if envelope is as broad as pre-defined minimum
		if (PMax - PMin < 2)
		{
		    double $ParaMid = (PMin + PMax) / Double.valueOf(2);                
		    double $PMinTmp = $ParaMid - 1;
		    double $PMaxTmp = $ParaMid + 1;                
		    
		  //enforce a minimum preferred range as long as it doesn't extrapolate outer limits
			if ($PMinTmp < Min)	{//preferred Min value as is 
			}
		    else	{PMin = $PMinTmp;}
		             
		    if ($PMaxTmp > Max)	{ //preferred Max value as is
			}
		    else	{PMax = $PMaxTmp;}
		}
					
//		/check difference between min/max and pref. min/max
		if (PMin - Min < 1)
		{
		    double $MinTmp = PMin - 1;
		    if ($MinTmp > $LandLower) 	{Min = $MinTmp;}
		    else						{Min = $LandLower;}
		}
		            
		if (Max - PMax < 1)
		{
		    double $MaxTmp = PMax + 1;
		    if ($MaxTmp < $LandUpper) 	{Max = $MaxTmp;}
		    else						{Max = $LandUpper;}
		}
	}
	
	
	public String toString(){
		String exitString = "landdistmin='"+Min+"'," + 
			"landdistprefmin='"+PMin+"'," + 
			"landdistprefmax='"+PMax+"'," + 
			"landdistmax='"+Max+"'"; 
			
		return exitString;
	}
	
}
