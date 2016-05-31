package org.gcube.contentmanagement.timeseries.geotools.vti.connectors;

import java.util.Date;

import org.gcube.contentmanagement.graphtools.utils.MathFunctions;

public class FishingHoursCalculator {

	
	
	
	public static double[] calculateFishingHours(String [] vesselIDsField,Date[] timeStamps){
		int vNumber = vesselIDsField.length;
		double [] hours = new double[vNumber];
		Date previousDate = null;
		String previousVessel = null;
		for (int i=0;i<vNumber;i++){
			if ((previousVessel!= null)&&(!previousVessel.equals(vesselIDsField[i])))
				previousDate = null;
			
//			System.out.print(timeStamps[i]+" vs "+previousDate+" ");
			
			if (previousDate==null)
				hours [i] = 0.0f;
			else{
				long timediff = timeStamps[i].getTime()-previousDate.getTime();
//				System.out.print("time diff: "+timediff);
				//if time difference< 4 hours
//				if ((timediff>=0) && (timediff<=4*60*60*1000)){
				if ((timediff>=0) && (timediff<=14400000)){
					hours [i] = MathFunctions.roundDecimal((double)timediff/(double)(60*60*1000),2);
				}
				else
					hours [i] = 0.0f;
			}
			
//			System.out.println(" hours:"+hours[i]);
			
			previousDate = timeStamps[i];
			previousVessel = vesselIDsField[i]; 
		}
		
//		System.exit(0);
		return hours;
	}
	
	
}
