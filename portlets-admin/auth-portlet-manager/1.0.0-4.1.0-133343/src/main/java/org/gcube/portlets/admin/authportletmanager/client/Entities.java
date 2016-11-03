package org.gcube.portlets.admin.authportletmanager.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.admin.authportletmanager.shared.Caller;

/**
 * 
 * @author "Alessandro Pieve " <a
 *         href="mailto:alessandro.pieve@isti.cnr.it">alessandro.pieve@isti.cnr.it</a>
 * 
 */

public class Entities {

	private static Entities istanza;

	private static ArrayList<Caller> callers;
	private static Map<String, List<String>> servicesMap;
	private static ArrayList<String> access;


	private Entities(){

	}
	public static Entities getInstance()
	{
		if (istanza == null)
		{
			istanza = new Entities();
		}

		return istanza; 
	}
	/**
	 * getCallers
	 * @return
	 */
	public static ArrayList<Caller> getCallers(){
		//order by type caller 
		Collections.sort(callers,new Comparator<Caller>() {
		    public int compare(Caller o1, Caller o2) {
		    	
		    	 int c;
		    	    c =o1.getTypecaller().compareTo(o2.getTypecaller());
		    	    if (c == 0)
		    	       c = o1.getCallerName().compareTo(o2.getCallerName());
		    	    return c;
		    	}		    
		});
		return callers;
	}
	/**
	 * callerList
	 * @param callersList
	 */
	public static void setCallers(ArrayList<Caller> callersList) {
		callers = callersList;
	}

	public static Caller getCallerByName(String callerName){
		for (Caller caller: callers){

			if (caller.getCallerName().equals(callerName)){
				return caller;
			}

		}
		return null;

	}


	public static Map<String, List<String>> getServicesMap() {
		return servicesMap;
	}
	public static void setServicesMap(Map<String, List<String>> servicesMapList) {
		servicesMap = servicesMapList;
	}


	public static ArrayList<String> getAccess() {
		return access;
	}
	public static void setAccess(ArrayList<String> access) {
		Entities.access = access;
	}



}
