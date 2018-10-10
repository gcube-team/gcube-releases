package org.gcube.dataanalysis.ecoengine.models.cores.aquamaps;

import java.util.List;

public class SpEnv_seaice extends AquamapsEnvelope{

	/*
$strSQL="SELECT DISTINCT $oc_var.CsquareCode, $oc_var.SpeciesID, HCAF.IceConAnn
FROM $oc_var INNER JOIN HCAF ON $oc_var.CsquareCode = HCAF.CsquareCode
WHERE $oc_var.SpeciesID = '" . $row['SpeciesID'] . "' 
AND HCAF.IceConAnn is not null
AND HCAF.OceanArea > 0
AND $oc_var.inc = 'y' 
ORDER BY HCAF.IceConAnn";
*/
	//###################################################################################
	//This file re-computes the temperature values (Min, PrefMin, Max, PrefMax based on 
	//area restriction parameters set by the user
	//###################################################################################
	public void calcEnvelope(List<Object> speciesOccurrences){
	
		calculatePercentiles(speciesOccurrences, null, null);
		
		//per KK and JR: extend IceMin -  avoid exclusion of species from all non-ice covered areas
		double $adjVal = -1; double $sumIce = 0; double $meanIce = 0;			
		//fix to -1 per KK (Me!AdjustIce value taken from form input)
		
		//Mods May 2010: treat values <.01 as zero; per KK; revised during comparison with D4S2 Proj
        if (Min < 0.01)
            Min = 0.00;
        
		if (Min == 0)
		{
//			$paramData = $conn->query($strSQL);				
		    $sumIce = 0;

		    int $reccount = speciesOccurrences.size();
			for (int i=0 ; i< $reccount ;i++){
		    	Object[] $row = (Object[])speciesOccurrences.get(i);
		    	double $IceConn = AquamapsEnvelopeAlgorithm.getNumber($row,2);	
				//ice concentration
		         $sumIce = $sumIce + $IceConn;
		    }

			if($reccount != 0)	{$meanIce = Double.valueOf($sumIce) / Double.valueOf($reccount);}
			else				{$meanIce = 0;}
		             
		    Min = $adjVal + $meanIce;
		}

	}
	
	
	public String toString(){
		String exitString = "iceconmin='"+Min+"'," + 
			"iceconprefmin='"+PMin+"'," + 
			"iceconprefmax='"+PMax+"'," + 
			"iceconmax='"+Max+"'"; 
			
		return exitString;
	}
	
}
