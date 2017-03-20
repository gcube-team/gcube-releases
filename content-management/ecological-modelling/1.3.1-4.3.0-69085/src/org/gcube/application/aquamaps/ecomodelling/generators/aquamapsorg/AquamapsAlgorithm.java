package org.gcube.application.aquamaps.ecomodelling.generators.aquamapsorg;

import java.util.HashMap;

import org.gcube.application.aquamaps.ecomodelling.generators.abstracts.AbstractGenerationAlgorithm;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

public class AquamapsAlgorithm extends AbstractGenerationAlgorithm{

	
	
	
	//get BB for a Species
	public HashMap<String,String> getBoundingBoxInfo (String $paramData_NMostLat,String $paramData_SMostLat,String $paramData_WMostLong, String $paramData_EMostLong, Object[] maxMinLat,String $type){
		
		// START  N-S Bounding Box adjustment values - copied from procBB_2050_next.php
		String $southern_hemisphere_adjusted = "n";
		String $northern_hemisphere_adjusted = "n";

		String $pass_NS = "n";
		String $pass_N  = "n";
		String $pass_S  = "n";
		
		HashMap<String,String> boundingInfo = new HashMap<String, String>();
		
		// START E-W Bounding Box adjustment values if 2050 map data - copied from procBB_2050.php
		if (($type!=null) && $type.equals("2050")) {

			/*this will extend 10 degrees E-W direction if bounding box exists (N,S,W,E values available)*/
						
			double $Wmost = ($paramData_WMostLong==null)? -9999: Double.parseDouble($paramData_WMostLong);
			double $Emost = ($paramData_EMostLong==null)? -9999: Double.parseDouble($paramData_EMostLong);
			double $n = 0;
			if 	(	(($paramData_NMostLat!=null)&&($paramData_NMostLat.length()>0)) &&
					(($paramData_SMostLat!=null)&&($paramData_SMostLat.length()>0)) 	&&
					(($paramData_WMostLong!=null)&&($paramData_WMostLong.length()>0)) 	&&
					(($paramData_EMostLong!=null)&&($paramData_EMostLong.length()>0))
				)
			{
				
				$paramData_NMostLat = ""+(Double.parseDouble($paramData_NMostLat) + 10);		if(Double.parseDouble($paramData_NMostLat) > 90)	{$paramData_NMostLat = "90";}
				$paramData_SMostLat = ""+(Double.parseDouble($paramData_SMostLat) - 10);		if(Double.parseDouble($paramData_SMostLat) < -90)	{$paramData_SMostLat = "-90";}

				if	(	$paramData_WMostLong.equals("-180") && 
						$paramData_EMostLong.equals("180")
					)  	//case is circumglobal
				{	}
				else
				{
					
					$paramData_WMostLong = ""+(Double.parseDouble($paramData_WMostLong) - 10);
					if(Double.parseDouble($paramData_WMostLong) < 0)
					{	
						if(Double.parseDouble($paramData_WMostLong) < -180)	{$n = (Double.parseDouble($paramData_WMostLong) + 180) * (-1); $paramData_WMostLong = ""+(180 - $n);}	
					}
					else
					{	
						if(Double.parseDouble($paramData_WMostLong) > 180)	{$n = Double.parseDouble($paramData_WMostLong) - 180; 	$paramData_WMostLong = ""+(-180 + $n);}		
					}

					$paramData_EMostLong = ""+(Double.parseDouble($paramData_EMostLong) + 10);
					if(Double.parseDouble($paramData_EMostLong) < 0)
					{	
						if(Double.parseDouble($paramData_EMostLong) < -180)	{
							$n = (Double.parseDouble($paramData_EMostLong) + 180) * (-1); $paramData_EMostLong = ""+(180 - $n);
						}	
					}
				else
				{
					if(Double.parseDouble($paramData_EMostLong) > 180) {
						$n = Double.parseDouble($paramData_EMostLong) - 180; $paramData_EMostLong = ""+(-180 + $n);
					}		
				}
			}
			
			//start kathy's routine {to check if overlapping longitude}
			double $diff = Double.parseDouble($paramData_WMostLong) - Double.parseDouble($paramData_EMostLong);	//used by case 1 & 2

			//case 1
			if	(	($Wmost < 0) && ($Emost < 0) && ($Wmost >= $Emost) && (($diff <= 0)	&& ($diff >= -20))	) { 
				$paramData_WMostLong = "-180"; $paramData_EMostLong = "180"; 
			}

			//case 2
			if	(	($Wmost > 0) && ($Emost > 0) && ($Wmost >= $Emost) && (($diff <= 0) && ($diff >= -20)) ) {	
				$paramData_WMostLong = "-180"; $paramData_EMostLong = "180"; 
			}

			//case 3
			if	(	($Wmost <= -170) && ($Emost >= 170)) {
				$paramData_WMostLong = "-180"; $paramData_EMostLong = "180"; }

			//case 4
			if	(	($Wmost >= 0 && $Wmost <= 10) && ($Emost <= 0 && $Emost >= -10)) { 
				$paramData_WMostLong = "-180"; $paramData_EMostLong = "180"; 
			}
			//end kathy's routine	

			}//if all bb are filled
			else {
				//no bounding box
			}
		}
		// END E-W Bounding Box adjustment values if 2050 map data
		
		
		if 	(	(($paramData_NMostLat==null)||($paramData_NMostLat.length()==0)) ||
				(($paramData_SMostLat==null)||($paramData_SMostLat.length()==0)) 	||
				(($paramData_WMostLong==null)||($paramData_WMostLong.length()==0)) 	||
				(($paramData_EMostLong==null)||($paramData_EMostLong.length()==0))
			)
		{

			if		(($paramData_NMostLat!=null)&&($paramData_NMostLat.length()>0) && ($paramData_SMostLat!=null) && ($paramData_SMostLat.length()>0)){$pass_NS = "y";}
			else if	(($paramData_NMostLat!=null)&& ($paramData_NMostLat.length()>0)){$pass_N = "y";}
			else if	(($paramData_SMostLat!=null) && ($paramData_SMostLat.length()>0)){$pass_S = "y";}
			
			else{
				//String $qry="SELECT DISTINCT Max(hcaf.CenterLat) AS maxCLat, Min(hcaf.CenterLat) AS minCLat FROM $oc_var INNER JOIN HCAF ON $oc_var.CsquareCode = HCAF.CsquareCode WHERE (((hcaf.OceanArea > 0))) AND $oc_var.SpeciesID = '$SpeciesID'	AND $oc_var.GoodCell <> 0";
				double $maxCLat = 0;
				double $minCLat = 0;
				try{
					$maxCLat = Double.parseDouble(""+maxMinLat[0]); 
					$minCLat = Double.parseDouble(""+maxMinLat[1]);
				}catch(Exception ex ){}
				
				if ($minCLat > 10) {	
					$paramData_SMostLat="0";
					$southern_hemisphere_adjusted = "y";
				}
				else if	($maxCLat < -10) {	
					$paramData_NMostLat="0";
					$northern_hemisphere_adjusted = "y";
				}
			
			}
		}
		// END  Bounding Box adjustment values
		
		boundingInfo.put("$southern_hemisphere_adjusted", $southern_hemisphere_adjusted);
		boundingInfo.put("$northern_hemisphere_adjusted", $northern_hemisphere_adjusted);
		boundingInfo.put("$pass_NS", $pass_NS);
		boundingInfo.put("$pass_N", $pass_N);
		boundingInfo.put("$pass_S", $pass_S);
		boundingInfo.put("$paramData_NMostLat", $paramData_NMostLat);
		boundingInfo.put("$paramData_SMostLat", $paramData_SMostLat);
		boundingInfo.put("$paramData_WMostLong", $paramData_WMostLong);
		boundingInfo.put("$paramData_EMostLong", $paramData_EMostLong);
		
		return boundingInfo;
	}
	//calculate BB and FAOAreas flags for (species,csquare)
	public HashMap<String,Integer> calculateBoundingBox(String csquarecode,String $pass_NS,String $pass_N,String $pass_S,
			String $CenterLat,String $CenterLong,String $FAOAreaM,
			String $paramData_NMostLat, String $paramData_SMostLat,String $paramData_WMostLong,String $paramData_EMostLong,String $paramData_FAOAreas,
			String $northern_hemisphere_adjusted, String $southern_hemisphere_adjusted){
		
		
//		if (csquarecode.equals("7112:123:4"))
//			System.out.println();
		
		
		// Get	values for 	$InFAO and $InBox these will be used as FILTERS
		int $InFAO=0;
		int $InBox=0;
		int $InLong;
		
		String $tmpstr="";
		HashMap<String,Integer> AreaInfo = new HashMap<String, Integer>();

		// START $InBox

		//start adjustment on N or S or NS limit exists
		if($pass_NS.equals("y"))
		{	if	(Double.parseDouble($CenterLat) >= Double.parseDouble($paramData_SMostLat)
				&&
				Double.parseDouble($CenterLat) <= Double.parseDouble($paramData_NMostLat))	{$InBox = 1; }	
		}else if($pass_N.equals("y")){	
			if	(Double.parseDouble($CenterLat) <= Double.parseDouble($paramData_NMostLat))	{$InBox = 1;}	
		}else if($pass_S.equals("y")){
			if	(Double.parseDouble($CenterLat) >= Double.parseDouble($paramData_SMostLat))	{$InBox = 1; }	
		}else{
			//start hemispheres using good cells
			if($southern_hemisphere_adjusted.equals("y")){
				if(Double.parseDouble($CenterLat) > 0)	
					{	$InBox = 1; 	}
			}else if($northern_hemisphere_adjusted.equals("y")){
				if(Double.parseDouble($CenterLat) < 0)	{$InBox = 1;}
			}else{
				$InBox = 0;
			}
			//end hemispheres using good cells
		}

		
			if 	(	($paramData_NMostLat!= null && $paramData_NMostLat.length()>0) 	&& 
					($paramData_SMostLat!= null && $paramData_SMostLat.length()>0)  &&
					($paramData_WMostLong!= null && $paramData_WMostLong.length()>0)  &&
					($paramData_EMostLong!= null && $paramData_EMostLong.length()>0)  
				)
				
			{		
			
				//'handle longitude crossing the date line
			    if (Double.parseDouble($paramData_WMostLong) > Double.parseDouble($paramData_EMostLong))
				{
					
					
					if	(
							(Double.parseDouble($CenterLong) >= Double.parseDouble($paramData_EMostLong))	&&
							(Double.parseDouble($CenterLong) <= Double.parseDouble($paramData_WMostLong))
						)
					{$InLong = 0;}
					else
					{$InLong = 1;}
				}
			    else
				{
					if	(
							(Double.parseDouble($CenterLong) >= Double.parseDouble($paramData_WMostLong))	&&
							(Double.parseDouble($CenterLong) <= Double.parseDouble($paramData_EMostLong))
						)
					{$InLong = 1;}
					else
					{$InLong = 0;}
				}
				
			    if	(
						(Double.parseDouble($CenterLat) >= Double.parseDouble($paramData_SMostLat))	&&
						(Double.parseDouble($CenterLat) <= Double.parseDouble($paramData_NMostLat))	&&
						$InLong == 1
					)
				{
					$InBox = 1;
			    }
				else
				{
					$InBox = 0;
			    }
			
			}
			//end new from skit nov 2006	
		
		
			
			//START $InFAO

		    //'check FAO area
			if 	( $FAOAreaM == null || $FAOAreaM.length() == 0	)
			{
				$InFAO = 0;

			}
	        else
			{
	            $tmpstr = $FAOAreaM; 			
										
				if ( ($paramData_FAOAreas!=null) && $paramData_FAOAreas.contains($tmpstr))
				{
					$InFAO = 1;
				}			
	            else 
				{
					$InFAO = 0;
				}
	        }
			
			AreaInfo.put("$InBox",$InBox);
			AreaInfo.put("$InFAO",$InFAO);
			
			
			
			
			
			return AreaInfo;
	}

	//help functions
	private boolean inside(String searched,String containing){
		if (containing!=null)
			return containing.contains(searched);
		else 
			return false;
	}
	//FAO Areas extension
	public String procFAO_2050(String $temp){
		
			if	(	inside("41",$temp)	|| 
					inside("47",$temp)	)	{if(!inside("48",$temp)){$temp += ", 48";}}

			if	(	inside("51",$temp)	|| 
					inside("57",$temp)	)	{if(!inside("58",$temp)){$temp += ", 58";}}
					
			if	(	inside("81",$temp)	|| 
					inside("87",$temp)	)	{if(!inside("88",$temp)){$temp += ", 88";}		
					}	
					
			if	(	inside("67",$temp)	)	{if(!inside("18",$temp)){$temp += ", 18";}}

			if	(	inside("31",$temp)	)	{	
				if(!inside("21",$temp)){$temp += ", 21";}
				if(!inside("41",$temp)){$temp += ", 41";}
			}

			if	(	inside("34",$temp)	)	{	
				if(!inside("27",$temp)){$temp += ", 27";}
				if(!inside("47",$temp)){$temp += ", 47";}		
			}

			if	(	inside("71",$temp)	) {	
				if(!inside("61",$temp)){$temp += ", 61";}		
				if(!inside("81",$temp)){$temp += ", 81";}		
			}
			if	(	inside("77",$temp)	) {	
				if(!inside("67",$temp)){$temp += ", 67";}		
				if(!inside("87",$temp)){$temp += ", 87";}		
			}
			return $temp;
	}
	//Probability calculation - initializes and calculates
	public double getSpeciesProb(Object[] speciesResults,Object[] csquarecodeInfo){
		
		String depthmin = getElement(speciesResults,0);
		
		int depthmean = 0;
		try{
			depthmean = Integer.parseInt(""+speciesResults[1]);
		}catch(Exception e){
		}
		
		String depthprefmin = getElement(speciesResults,2);
		
		String pelagic = getElement(speciesResults,3);
		
		String depthprefmax = getElement(speciesResults,4);
		
		String depthmax = getElement(speciesResults,5);
		
		String tempmin = getElement(speciesResults,6);
		
		String layer = getElement(speciesResults,7);
		
		String tempprefmin = getElement(speciesResults,8);
		
		String tempprefmax = getElement(speciesResults,9);
		
		String tempmax = getElement(speciesResults,10);
		
		String salinitymin = getElement(speciesResults,11);
		
		String salinityprefmin = getElement(speciesResults,12);
		
		String salinityprefmax = getElement(speciesResults,13);
		
		String salinitymax = getElement(speciesResults,14);
		
		String primprodmin = getElement(speciesResults,15);
		
		String primprodprefmin = getElement(speciesResults,16);
		
		String primprodprefmax = getElement(speciesResults,17);
		
		String primprodmax = getElement(speciesResults,18);
		
		String iceconmin = getElement(speciesResults,19);
		
		String iceconprefmin = getElement(speciesResults,20);
		
		String iceconprefmax = getElement(speciesResults,21);
		
		String iceconmax = getElement(speciesResults,22);
		
		String landdistyn = getElement(speciesResults,23);
		
		String landdistmin = getElement(speciesResults,24);
		
		String landdistprefmin = getElement(speciesResults,25);
		
		String landdistprefmax = getElement(speciesResults,26);
		
		String landdistmax = getElement(speciesResults,27);
		
		String csquarecode = getElement(csquarecodeInfo,0);
		
		double depthmeancsquare = getNumber(csquarecodeInfo, 1);
		double depthmaxcsquare = getNumber(csquarecodeInfo, 2);
		double depthmincsquare = getNumber(csquarecodeInfo, 3);
		double sstanmeancsquare = getNumber(csquarecodeInfo, 4);
		double sbtanmeancsquare = getNumber(csquarecodeInfo, 5);
		double salinitymeancsquare = getNumber(csquarecodeInfo, 6);
		double salinitybmeancsquare = getNumber(csquarecodeInfo, 7);
		double primprodmeancsquare = getNumber(csquarecodeInfo, 8);
		
		String iceconanncsquare = csquarecodeInfo[9]==null? "":(""+csquarecodeInfo[9]);
		
		double landdist = getNumber(csquarecodeInfo, 10);
		
		String vprovider = "";
		if (depthmean == 1) {
			vprovider = "MM";
		} 
		else {
			vprovider = "suitable";
		}
		
		double prob = 0;
		try{
//			long t0 = System.currentTimeMillis();
			prob = calcProb(depthmin, depthmean, depthmeancsquare, depthmaxcsquare, depthmincsquare, depthprefmin, pelagic, vprovider, 
				depthprefmax, depthmax, tempmin, layer, sstanmeancsquare, sbtanmeancsquare, tempprefmin, tempprefmax, tempmax, 
				salinitymin, salinitymeancsquare, salinitybmeancsquare, salinityprefmin, salinityprefmax, salinitymax, 
				primprodmin, primprodmeancsquare, primprodprefmin, primprodprefmax, primprodmax, 
				iceconmin, iceconanncsquare, iceconprefmin, iceconprefmax, iceconmax, 
				landdistyn, landdist, landdistmin, landdistprefmin, landdistprefmax, landdistmax);
			
//			long t1 = System.currentTimeMillis();
//			System.out.println("Time "+ (t0-t1)+"ms");
//			avgTime = MathFunctions.incrementPerc(avgTime, t1-t0, totalcounter);
//			totalcounter++;
//			System.out.println("Average Time "+ avgTime +"ms");
		}catch (Exception e){
			AnalysisLogger.getLogger().debug("Impossible to calculate probability: inconsistent values in the hcaf or hspen");
		}
		return prob;
	}
	
	float avgTime = 0;
	int totalcounter = 0;
	
	public AquamapsAlgorithm(){
	}
	
	//calculates probability
	public double calcProb(String $paramData_DepthMin, int $paramData_MeanDepth, double $DepthMean, double $DepthMax, double $DepthMin, String $paramData_DepthPrefMin, 
			String $paramData_Pelagic, String $vprovider, String $paramData_DepthPrefMax, String $paramData_DepthMax, 
			String $paramData_SSTMin, String $paramData_layer, double $SSTAnMean, double $SBTAnMean, String $paramData_SSTPrefMin, String $paramData_SSTPrefMax, String $paramData_SSTMax, 
			String $paramData_SalinityMin, double $SalinityMean, double $SalinityBMean, String $paramData_SalinityPrefMin, String $paramData_SalinityPrefMax, String $paramData_SalinityMax, 
			String $paramData_PrimProdMin, double $PrimProdMean, String $paramData_PrimProdPrefMin, String $paramData_PrimProdPrefMax, String $paramData_PrimProdMax, 
			String $paramData_IceConMin, String $IceConAnn, String $paramData_IceConPrefMin, String $paramData_IceConPrefMax, String $paramData_IceConMax, 
			String $paramData_LandDistYN, double $LandDist, String $paramData_LandDistMin, String $paramData_LandDistPrefMin, String $paramData_LandDistPrefMax, String $paramData_LandDistMax ){
		
		
		Double dparamData_DepthMin = null;
		Double dparamData_DepthPrefMin = null;
		Double dparamData_DepthPrefMax = null;
		Double dparamData_DepthMax = null; 
		Double dparamData_SSTMin = null;
		Double dparamData_SSTPrefMin = null;
		Double dparamData_SSTMax = null;
		Double dparamData_SSTPrefMax = null;
		Double dparamData_SalinityMin = null;
		Double dparamData_SalinityPrefMin = null;
		Double dparamData_SalinityPrefMax = null;
		Double dparamData_SalinityMax = null;
		Double dparamData_PrimProdMax = null;
		Double dparamData_PrimProdPrefMax = null;
		Double dparamData_PrimProdPrefMin = null;
		Double dparamData_PrimProdMin = null;
		Double dparamData_IceConMax = null;
		Double dparamData_IceConPrefMax = null;
		Double dparamData_IceConPrefMin = null;
		Double dparamData_IceConMin = null;
		Double dIceConAnn = null;
		Double dparamData_LandDistMax = null;
		Double dparamData_LandDistPrefMax = null;
		Double dparamData_LandDistPrefMin = null;
		Double dparamData_LandDistMin = null;
		
		//pre parsing of some variables
		try{
		dparamData_DepthMin = Double.parseDouble($paramData_DepthMin);
		dparamData_DepthPrefMin = Double.parseDouble($paramData_DepthPrefMin);
		dparamData_DepthPrefMax = Double.parseDouble($paramData_DepthPrefMax);
		dparamData_DepthMax = Double.parseDouble($paramData_DepthMax);
		
		dparamData_SSTMin = Double.parseDouble($paramData_SSTMin);
		dparamData_SSTPrefMin = Double.parseDouble($paramData_SSTPrefMin);
		dparamData_SSTMax = Double.parseDouble($paramData_SSTMax);
		dparamData_SSTPrefMax = Double.parseDouble($paramData_SSTPrefMax);
		
		dparamData_SalinityMin = Double.parseDouble($paramData_SalinityMin);
		dparamData_SalinityPrefMin = Double.parseDouble($paramData_SalinityPrefMin);
		dparamData_SalinityPrefMax = Double.parseDouble($paramData_SalinityPrefMax);
		dparamData_SalinityMax = Double.parseDouble($paramData_SalinityMax);
		
		dparamData_PrimProdMax = Double.parseDouble($paramData_PrimProdMax);
		dparamData_PrimProdPrefMax = Double.parseDouble($paramData_PrimProdPrefMax);
		dparamData_PrimProdPrefMin = Double.parseDouble($paramData_PrimProdPrefMin );
		dparamData_PrimProdMin = Double.parseDouble($paramData_PrimProdMin);
		
		dparamData_IceConMax = Double.parseDouble($paramData_IceConMax);
		dparamData_IceConPrefMax = Double.parseDouble($paramData_IceConPrefMax);
		dparamData_IceConPrefMin = Double.parseDouble($paramData_IceConPrefMin);
		dparamData_IceConMin = Double.parseDouble($paramData_IceConMin);
		dIceConAnn = Double.parseDouble($IceConAnn);				

		dparamData_LandDistMax = Double.parseDouble($paramData_LandDistMax);
		dparamData_LandDistPrefMax = Double.parseDouble($paramData_LandDistPrefMax);
		dparamData_LandDistPrefMin = Double.parseDouble($paramData_LandDistPrefMin);
		dparamData_LandDistMin = Double.parseDouble($paramData_LandDistMin);
		}catch(Exception ex){}
		//end preparsing
		
		// STEP 3 start of COMPUTATIONS - testing fields from HSPEN matched againsts all HCAF records
		
		//initialize factors to compute pTotal (PROBABILITY) pDepth, pSST, pSalin, pIce, pLand
		double $pDepth=0; 
		double $pSST=0;
		double $pSalin=0; 
		double $pIce=0;
		double $pLand=0; // previously set to 1 since when computation was omitted per Skit; now handles expert-reviewed HSPEN cases where distance to land is included
				
		double $pPProd=0;
		double $pTotal=0;
		
		
		double $paramfld = 0;
		double $paramfld1 = 0;
	//##################################################################################################
	// DEPTH
	//##################################################################################################

	if ($paramData_DepthMin == null ) { 
		$pDepth = 1;
	} 
	else {
			
		if ($paramData_MeanDepth == 1){
			$paramfld = $DepthMean;
			$paramfld1 = $DepthMean;
		}
		else {
			$paramfld = $DepthMax;
			$paramfld1 = $DepthMin;
		}
			
		$pDepth = -1;
			
	    if ($paramfld == -9999 || $paramData_DepthMin.equals("") ){$pDepth = 1;}
	        else
			{
	            if ($paramData_DepthMin.equals("") ||($paramfld < dparamData_DepthMin))
				{$pDepth = 0;}
	            else
				{
	                if 	(
							($paramfld < dparamData_DepthPrefMin) &&
							($paramfld >= dparamData_DepthMin)
						)
					{
	                    $pDepth = ($paramfld - dparamData_DepthMin) / (dparamData_DepthPrefMin - dparamData_DepthMin);
	                }
					else
					{				
						if ((Integer.parseInt($paramData_Pelagic)!= 0) && (!$vprovider.equals("MM")))
						{$pDepth = 1;}
	                    else
						{
	                        if 	(
								($paramfld >= dparamData_DepthPrefMin) && 
								($paramfld1 <= dparamData_DepthPrefMax)
								)
								{$pDepth = 1;}
	                        else
							{																	

								if (!$paramData_DepthPrefMax.equals(""))							
								{
				

	                            if ($paramfld1 >= dparamData_DepthPrefMax)
								{

									//to correct div by zero								
									if 	(
											(	dparamData_DepthMax - dparamData_DepthPrefMax) != 0
										)
									{
										$pDepth = (dparamData_DepthMax - $paramfld1) / (dparamData_DepthMax - dparamData_DepthPrefMax);
									}
									else
									{
										$pDepth=0;
									}

									
									if 	(
											(dparamData_DepthMax - dparamData_DepthPrefMax) != 0
										)
									{
	                                	$pDepth = (dparamData_DepthMax - $paramfld1) / (dparamData_DepthMax - dparamData_DepthPrefMax);
									}
									else
									{
										$pDepth=0;								
									}
									
									
	                                if ($pDepth < 0){$pDepth = 0;}
									
									
									
	                            }
								else {$pDepth = 0;}
								

								}
								else {$pDepth = 0;}


								
	                        }
	                    }
	                }
	            }
	        }
	}	
//			print "<br>Depth = ".$pDepth;

	//##################################################################################################
	// SST
	//##################################################################################################

		if ($paramData_SSTMin == null)
		{ $pSST = 1;} else{

			if 		($paramData_layer.equals("s"))	{$paramfld = $SSTAnMean;}
			else if 	($paramData_layer.equals("b"))	{$paramfld = $SBTAnMean;}



	        if ($paramfld == -9999 || $paramData_SSTMin.equals("")){$pSST = 1;}
	        else
			{
	            if ($paramfld < dparamData_SSTMin){$pSST = 0;}
	            else
				{
	                if (	($paramfld >= dparamData_SSTMin) && 
							$paramfld < dparamData_SSTPrefMin)
					{
	                    $pSST = ($paramfld - dparamData_SSTMin) / (dparamData_SSTPrefMin - dparamData_SSTMin);
	                }
					else
					{
	                    if (($paramfld >= dparamData_SSTPrefMin)&&
							($paramfld <= dparamData_SSTPrefMax)){$pSST = 1;}
	                    else
						{
	                        if (($paramfld > dparamData_SSTPrefMax) && 
								($paramfld <= dparamData_SSTMax))
							{
	                            $pSST = (dparamData_SSTMax - $paramfld) / (dparamData_SSTMax - dparamData_SSTPrefMax);
	                        }
							else {$pSST = 0;}
	                    }
	                }
	            }
	        }
		}
//			print "<br>Temp = ".$pSST;


	//##################################################################################################
	// Salinity
	//##################################################################################################			
	if ($paramData_SalinityMin == null)
		{ $pSalin = 1;} else{
		
			if 		($paramData_layer.equals("s"))	{$paramfld = $SalinityMean;}
			else if 	($paramData_layer.equals("b"))	{$paramfld = $SalinityBMean;}		

	        if ($paramfld == -9999 || $paramData_SalinityMin.equals("") )
			{ 
				//'no data available
	            $pSalin = 1;
			}
	        else
			{
	            if ($paramfld < dparamData_SalinityMin){$pSalin = 0;}
	            else
				{
	                if ($paramfld >= dparamData_SalinityMin && 
						$paramfld < dparamData_SalinityPrefMin)
					{
	                    $pSalin = ($paramfld - dparamData_SalinityMin) / (dparamData_SalinityPrefMin - dparamData_SalinityMin);
	                }
					else
					{
	                    if ($paramfld >= dparamData_SalinityPrefMin && 
							$paramfld <= dparamData_SalinityPrefMax){$pSalin = 1;}
	                    else
						{
	                        if (($paramfld > dparamData_SalinityPrefMax) && 
								$paramfld <= dparamData_SalinityMax)
							{
	                            $pSalin = (dparamData_SalinityMax - $paramfld) / (dparamData_SalinityMax - dparamData_SalinityPrefMax);
	                        }
							else
							{$pSalin = 0;}
	                    }
	                }
	            }
	        }
	    }    
//			print "<br>Salinity = ".$pSalin;


	//##################################################################################################
	// Primary Production
	//##################################################################################################
	if ($paramData_PrimProdMin == null)
		{ $pPProd = 1;} else{
			//modification of 07 04 11
	        if ($PrimProdMean == -9999)
			{
				//Then 'no data available
	            $pPProd = 1;
	        }
			else
			{
	            if ($PrimProdMean < dparamData_PrimProdMin )
				{
	                $pPProd = 0;
	            }
				else
				{
	                if (($PrimProdMean >= dparamData_PrimProdMin) && ($PrimProdMean < dparamData_PrimProdPrefMin))
					{
	                    $pPProd = ($PrimProdMean - dparamData_PrimProdMin) / (dparamData_PrimProdPrefMin - dparamData_PrimProdMin);
	                }
					else
					{
	                    if (($PrimProdMean >= dparamData_PrimProdPrefMin) && ($PrimProdMean <= dparamData_PrimProdPrefMax))
						{
	                        $pPProd = 1;
	                    }
						else
						{
	                        if (($PrimProdMean >dparamData_PrimProdPrefMax) && ($PrimProdMean <= dparamData_PrimProdMax))
							{
	                            $pPProd = (dparamData_PrimProdMax - $PrimProdMean) / (dparamData_PrimProdMax - dparamData_PrimProdPrefMax);
	                        }
							else
							{
	                            $pPProd = 0;
	                        }
	                    }
	                }
	            }
	        }
		}
//			print "<br> Primary Prod = ".$pPProd;	


	//###################################################################################################################################    
	// Sea Ice Con
	//###################################################################################################################################		

	if (($paramData_IceConMin == null) || ($paramData_IceConMin.length() == 0)){$pIce = 1;}
	else{
	//modification of 07 04 11
	if (($IceConAnn == null) ||($IceConAnn.length() == 0 )) {$pIce = 1;}
	else{
		if(!$IceConAnn.equals(""))
		{

			int $flgIceProbMultiplicationAlgorithm;
			if (dIceConAnn < dparamData_IceConMin)
			{

				$pIce = 0;
				$flgIceProbMultiplicationAlgorithm = 0;
			} 
			else if ((dIceConAnn >= dparamData_IceConMin) 		
						&& 
						(dIceConAnn < dparamData_IceConPrefMin))
			{	
				$pIce = (dIceConAnn - dparamData_IceConMin) / (dparamData_IceConPrefMin - dparamData_IceConMin);
				$flgIceProbMultiplicationAlgorithm = 0;
			} 
			else if (	((dIceConAnn) >= (dparamData_IceConPrefMin)) 
						&&
						((dIceConAnn) <= (dparamData_IceConPrefMax)))
			{

				$pIce = 1;
				$flgIceProbMultiplicationAlgorithm = 1;
			} 
			else if (	((dIceConAnn) > (dparamData_IceConPrefMax)) 
					&&
						((dIceConAnn) <= (dparamData_IceConMax))	)
			{	

				$pIce = (((dparamData_IceConMax) - (dIceConAnn))) / (((dparamData_IceConMax) - (dparamData_IceConPrefMax)));
				$flgIceProbMultiplicationAlgorithm = 1;
			} 

			else if ((dIceConAnn) > (dparamData_IceConMax))
			{
				$pIce = 0;
				$flgIceProbMultiplicationAlgorithm = 1;
			}
		}
	}
	}
//	 		print" pIce =   $pIce <br> ";


	//###################################################################################################################################    
	// Distance to Land
	//###################################################################################################################################

	if (Integer.parseInt($paramData_LandDistYN) == 0) {
		$pLand = 1;	
	}
	else {
			
			$pLand = 0;
			if ($LandDist == -9999 || $paramData_LandDistMin.equals("") ) { 
				//no data available
	            $pLand = 1;
			}
	        else
			{
	            if ($LandDist < (dparamData_LandDistMin)){
					$pLand = 0;
				}
	            else
				{
	                if (($LandDist >= (dparamData_LandDistMin)) && ($LandDist < (dparamData_LandDistPrefMin))) {
	                    $pLand = ($LandDist - (dparamData_LandDistMin)) / ((dparamData_LandDistPrefMin) - (dparamData_LandDistMin));
	                }
					else
					{
	                    if ((dparamData_LandDistPrefMax) > 1000) {
							$pLand = 1;
						}
	                    else
						{
	                        if (($LandDist >= (dparamData_LandDistPrefMin)) && ($LandDist <= (dparamData_LandDistPrefMax))){$pLand = 1;}
							else
							{
	                            if (($LandDist > (dparamData_LandDistPrefMax)) && ($LandDist <= (dparamData_LandDistMax))) {
	                                $pLand = ((dparamData_LandDistMax) - $LandDist) / ((dparamData_LandDistMax) - (dparamData_LandDistPrefMax));
	                            }
								else {$pLand = 0;}
	                        }
	                    }
	                }
	            }
	        }
		}
	//print "<br> Distance to Land = ".$pLand;	
/*
	System.out.println("FACTORS ");
	System.out.println("$pSST: "+$pSST);
	System.out.println("$pDepth: "+$pDepth);
	System.out.println("$pSalin: "+$pSalin);
	System.out.println("$pLand: "+$pLand);
	System.out.println("$pPProd: "+$pPProd);
	System.out.println("$pIce: "+$pIce);
	System.out.println("//");
	*/
	// get output of $pTotal by multiplication 
	$pTotal = $pSST * $pDepth * $pSalin * $pLand * $pPProd * $pIce; 

	return $pTotal;
	}
	

	
}
