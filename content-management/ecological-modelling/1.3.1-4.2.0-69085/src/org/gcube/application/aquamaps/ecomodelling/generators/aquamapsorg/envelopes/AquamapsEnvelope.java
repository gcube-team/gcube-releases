package org.gcube.application.aquamaps.ecomodelling.generators.aquamapsorg.envelopes;

import java.util.List;

import org.gcube.application.aquamaps.ecomodelling.generators.aquamapsorg.AquamapsEnvelopeAlgorithm;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.EnvelopeName;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.Envelope;
import org.gcube.contentmanagement.graphtools.utils.MathFunctions;

public class AquamapsEnvelope {

	public Double Min;
	public Double PMin;
	public Double Max;
	public Double PMax;
	
	public static final double $TempUpper = 30;
	public static final double $TempLower = -2;

	public static final double $SalinUpper = 40.2;
	public static final double $SalinLower = 3.56;

	public static final double $ProdUpper = 6000;
	public static final double $ProdLower = 0;

	public static final double $LandUpper = 4200;
	public static final double $LandLower = 0;

	public static final double $SalinBUpper = 40.9;
	public static final double $SalinBLower = 3.5;
	
	private float toleranceThr = 0.015f; //tolerance on relative error
	
	public static int round(double $n){
		
		$n = Math.round($n * 100.00)/100.00;
		
		String $nstr = ""+$n;
		int $dec_pos = $nstr.indexOf('.');	
		int $final = 0;
		
		String $left_char = "";
		String $right_char = "";
		
		if ($dec_pos>0)
		{
			$left_char=$nstr.substring($dec_pos-1,$dec_pos);
			$right_char=$nstr.substring($dec_pos+1,$dec_pos+2);
		}
		if ($right_char.equals("5"))
		{
			if 	(
					$left_char.equals("0") ||
					$left_char.equals("2") ||
					$left_char.equals("4") ||
					$left_char.equals("6") ||
					$left_char.equals("8") 				
				)
			{
				$final = (int)Math.round($n)-1;
			}
			else
			{
				$final = (int)Math.round($n);
			}
			
		}
		else
		{
			$final = (int)Math.round($n);
		}
		
		
		return $final; 
		}
	
	public void calculatePercentiles(List<Object> speciesOccurrences, Double $Uppermost, Double $Lowermost){
		int position = 2;
		int $reccount = speciesOccurrences.size();
		//compute positions of percentiles: 25th, 75th, 10th and 90th				
		int $Rec25 = round(25f * ($reccount + 1f) / 100f) - 1; //25
		int $Rec75 = round(75f * ($reccount + 1f) / 100f) - 1; //75
		int $Rec10 = 0;
		int $Rec90 = 0;
		
		if ($reccount >= 10 && $reccount <= 13)
		{
		    $Rec10 = round(10f * ($reccount + 1f) / 100f);
		    $Rec90 = round(90f * ($reccount + 1f) / 100f) - 2;			
		}
		else
		{
		    $Rec10 = round(10f * ($reccount + 1f) / 100f) - 1;
		    $Rec90 = round(90f * ($reccount + 1f) / 100f) - 1;			
		}

		//get percentiles
//		$paramData->data_seek(0);
		Object[] $row2 = (Object[])speciesOccurrences.get(0);			
		double $Min = AquamapsEnvelopeAlgorithm.getNumber($row2,position);
		
//		$paramData->data_seek($reccount - 1);
		$row2 = (Object[])speciesOccurrences.get($reccount - 1);
		double $Max = AquamapsEnvelopeAlgorithm.getNumber($row2,position);
		            
//		$paramData->data_seek($Rec25);
		$row2 = (Object[])speciesOccurrences.get($Rec25);		
		double $25 = AquamapsEnvelopeAlgorithm.getNumber($row2,position);
		            
//		$paramData->data_seek($Rec75);
		$row2 = (Object[])speciesOccurrences.get($Rec75);
		double $75 = AquamapsEnvelopeAlgorithm.getNumber($row2,position);
		            
//		$paramData->data_seek($Rec10);
		$row2 = (Object[])speciesOccurrences.get($Rec10);
		double $PMin = AquamapsEnvelopeAlgorithm.getNumber($row2,position);
		            
//		$paramData->data_seek($Rec90);
		$row2 = (Object[])speciesOccurrences.get($Rec90);			
		double $PMax = AquamapsEnvelopeAlgorithm.getNumber($row2,position);
		
		
		if (($Uppermost!= null) && ($Lowermost != null)){
			//interquartile adjusting
			double $InterQuartile = Math.abs($25 - $75);
			double $ParaAdjMax = $75 + Double.valueOf(1.5) * $InterQuartile;
			double $ParaAdjMin = $25 - Double.valueOf(1.5) * $InterQuartile;
		
			if ($ParaAdjMax < $Uppermost && $ParaAdjMax > $Max)
			{
				$Max = $ParaAdjMax;
			}
			if ($ParaAdjMin > $Lowermost && $ParaAdjMin < $Min)
			{
				$Min = $ParaAdjMin;
			}
		}
		
		Min = $Min;
		Max = $Max;
		PMin = $PMin;
		PMax = $PMax;
	}
	
	private static double relativeError(double realvalue,double calculatedvalue){
		double absoluteError = Math.abs(realvalue-calculatedvalue);
		double relativeErr = 0;
		double denominator = 1;
		if (realvalue!=0)
			denominator = realvalue;
		
		if (!((realvalue ==0) && (absoluteError==0))) 
			relativeErr = absoluteError/denominator;
		
			
		
//		AnalysisLogger.getLogger().debug("relative error "+relativeErr+" "+realvalue+" vs "+calculatedvalue);
		return Math.abs(relativeErr);
	} 

	public boolean checkPrevious(Double prevMin,Double prevMax,Double prevPMin,Double prevPMax){
		try{
			if ((relativeError(prevMin,Min)<toleranceThr) && (relativeError(prevMax,Max)<toleranceThr) && (relativeError(prevPMin,PMin)<toleranceThr) && (relativeError(prevPMax,PMax)<toleranceThr)) 
				return true;
			else
				return false;
			
		}catch(Exception e){
			return false;
		}
	}
	
	public Envelope toEnvelope(EnvelopeName name){
		Min = (Min==null)?null:MathFunctions.roundDecimal(Min,2);
		PMin = (PMin==null)?null:MathFunctions.roundDecimal(PMin,2);
		PMax = (PMax==null)?null:MathFunctions.roundDecimal(PMax,2);
		Max = (Max==null)?null:MathFunctions.roundDecimal(Max,2);
		
		Envelope env = new Envelope(""+Min, ""+PMin,""+PMax, ""+Max);
		
		env.setName(name);
		return env;
	}
	
}
