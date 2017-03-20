package org.gcube.portlets.user.td.tablewidget.client.geospatial;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ResolutionStore implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6520001942214730827L;

	private static ArrayList<Resolution> storeCSquareResolution;
	private static ArrayList<Resolution> storeOcenaAreaResolution;
	
	private static Resolution resolutionCSquare1=new Resolution(1, 0.1d,"0.1");
	private static Resolution resolutionCSquare2=new Resolution(2, 0.5d,"0.5");
	private static Resolution resolutionCSquare3=new Resolution(3, 1.0d,"1");
	private static Resolution resolutionCSquare4=new Resolution(4, 5.0d,"5");
	private static Resolution resolutionCSquare5=new Resolution(5, 10.0d,"10");
	
	private static Resolution resolutionOceanArea1=new Resolution(1, 1.0d,"1");
	private static Resolution resolutionOceanArea2=new Resolution(2, 5.0d,"5");
	private static Resolution resolutionOceanArea3=new Resolution(3, 10.0d,"10");
	
	public static ArrayList<Resolution> getStoreCSquareResolution() {
		storeCSquareResolution=new ArrayList<Resolution>();
		storeCSquareResolution.add(resolutionCSquare1);
		storeCSquareResolution.add(resolutionCSquare2);
		storeCSquareResolution.add(resolutionCSquare3);
		storeCSquareResolution.add(resolutionCSquare4);
		storeCSquareResolution.add(resolutionCSquare5);
		return storeCSquareResolution;
	}
	
	public static Resolution getStoreCSquareResolutionDefault(){
		return resolutionCSquare2;
	}
	
	public static ArrayList<Resolution> getStoreOceanAreaResolution() {
		storeOcenaAreaResolution=new ArrayList<Resolution>();
		storeOcenaAreaResolution.add(resolutionOceanArea1);
		storeOcenaAreaResolution.add(resolutionOceanArea2);
		storeOcenaAreaResolution.add(resolutionOceanArea3);	
		return storeOcenaAreaResolution;
	}
	
	public static Resolution getStoreOceanAreaResolutionDefault(){
		return resolutionOceanArea2;
	}
	
}
