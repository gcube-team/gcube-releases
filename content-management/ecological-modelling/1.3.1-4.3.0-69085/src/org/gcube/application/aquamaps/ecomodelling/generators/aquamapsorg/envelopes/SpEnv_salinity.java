package org.gcube.application.aquamaps.ecomodelling.generators.aquamapsorg.envelopes;

import java.util.List;

public class SpEnv_salinity extends AquamapsEnvelope{

	/*
	$strSQL="SELECT DISTINCT $oc_var.CsquareCode, $oc_var.SpeciesID, HCAF.$fld
FROM $oc_var INNER JOIN HCAF ON $oc_var.CsquareCode = HCAF.CsquareCode
WHERE $oc_var.SpeciesID = '" . $row['SpeciesID'] . "' 
AND HCAF.$fld <> -9999
AND HCAF.$fld is not null
AND HCAF.OceanArea > 0
AND $oc_var.inc = 'y' 
ORDER BY HCAF.$fld";
	*/
	
	public void calcEnvelope(List<Object> speciesOccurrences, String $layer){

		double $SalinUp;
		double $SalinLow;
		if ($layer.equals("s"))
		{
			$SalinUp = $SalinUpper;
		    $SalinLow = $SalinLower;
		}
		else if ($layer.equals("b"))
		{
		    $SalinUp = $SalinBUpper; 	//reset absolute min and max for bottom
		    $SalinLow = $SalinBLower;
		}
		else
		{
			$SalinUp = $SalinUpper;
		    $SalinLow = $SalinLower;
		}
		
		calculatePercentiles(speciesOccurrences, $SalinUp, $SalinLow);
		
		//check if envelope is as broad as pre-defined minimum
		if (PMax - PMin < 1)
		{
		    double $ParaMid = (PMin + PMax) / Double.valueOf(2);               
		    double $PMinTmp = $ParaMid - 0.5;
		    double $PMaxTmp = $ParaMid + 0.5;
		    
			//enforce a minimum preferred range as long as it doesn't extrapolate outer limits
		    if ($PMinTmp < Min)	{
		    	// preferred Min value as is
			}
		    else {PMin = $PMinTmp;}             
		    
			if ($PMaxTmp > Max)	{//preferred Max value as is
			}
		    else {PMax = $PMaxTmp;}	   			   
		}

		//check difference between min/max and pref. min/max
		if (PMin - Min < 0.5)
		{     
			double $MinTmp = PMin - 0.5;
		    if ($MinTmp > $SalinLower)	{Min = $MinTmp;}
		    else						{Min = $SalinLower;}
		}
		            
		if (Max - PMax < 0.5)
		{     
			double $MaxTmp = PMax + 0.5;
		    if ($MaxTmp < $SalinUpper)	{Max = $MaxTmp;}
		    else						{Max = $SalinUpper;}
		}
		
	}
	
	
	public String toString(){
		String exitString = "salinitymin='"+Min+"'," + 
			"salinityprefmin='"+PMin+"'," + 
			"salinityprefmax='"+PMax+"'," + 
			"salinitymax='"+Max+"'"; 
			
		return exitString;
	}
	
}
