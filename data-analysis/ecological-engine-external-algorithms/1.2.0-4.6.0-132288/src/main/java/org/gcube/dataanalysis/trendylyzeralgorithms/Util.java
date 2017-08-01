package org.gcube.dataanalysis.trendylyzeralgorithms;

public class Util {
	
	public static String formatAreaName(String area)
	{
		String temp= area.replace(".", "_");
		String temp2= temp.replace(" ", "_");
		temp= temp2.replace("-", "_");
		temp2= temp.replace(",", "_");
		temp= temp2.replace("/", "_");
		return temp;
	}

}
