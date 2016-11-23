package org.gcube.portlets.user.td.expressionwidget.client.threshold;

import java.util.ArrayList;



/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ThresholdStore {
	
	
	public static ArrayList<Threshold> thresholdsLevenshtein = new ArrayList<Threshold>() {
		private static final long serialVersionUID = -6559885743626876431L;
	{
	    add(new Threshold(1,1,"1"));
	    add(new Threshold(2,2,"2"));
	    add(new Threshold(3,3,"3"));
	    add(new Threshold(4,4,"4"));
	    add(new Threshold(5,5,"5"));
	    add(new Threshold(6,6,"6"));
	    add(new Threshold(7,7,"7"));
	    add(new Threshold(8,8,"8"));
	    add(new Threshold(9,9,"9"));
	    add(new Threshold(10,10,"10"));
	}};
	
	public static Threshold defaultThresholdLevenshtein(){
		return new Threshold(2,2,"2");
	}
	
	
	public static ArrayList<Threshold> thresholdsSimilarity = new ArrayList<Threshold>() {
		private static final long serialVersionUID = -6559885743626876431L;
	{
		 
		    add(new Threshold(1,0.1f,"0.1"));
		    add(new Threshold(2,0.2f,"0.2"));
		    add(new Threshold(3,0.3f,"0.3"));
		    add(new Threshold(4,0.4f,"0.4"));
		    add(new Threshold(5,0.5f,"0.5"));
		    add(new Threshold(6,0.6f,"0.6"));
		    add(new Threshold(7,0.7f,"0.7"));
		    add(new Threshold(8,0.8f,"0.8"));
		    add(new Threshold(9,0.9f,"0.9"));
	}};
	
	public static Threshold defaultThresholdSimilarity(){
		return new Threshold(8,0.8f,"0.8");
	}
	
	
}
