package org.gcube.portlets.user.td.columnwidget.client.store;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.tr.batch.ShowOccurrencesType;




/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ShowOccurrencesTypeStore implements Serializable {

	private static final long serialVersionUID = -1908324094430432681L;
	
	protected static ArrayList<ShowOccurrencesTypeElement> store;
	
	public static ShowOccurrencesTypeElement onlyErrorsElement=new ShowOccurrencesTypeElement(1,ShowOccurrencesType.ONLYERRORS);
	public static ShowOccurrencesTypeElement allElement=new ShowOccurrencesTypeElement(2,ShowOccurrencesType.ALL);
	

	public static ArrayList<ShowOccurrencesTypeElement> getShowOccurrencesType(){
		store=new ArrayList<ShowOccurrencesTypeElement>();
		store.add(onlyErrorsElement);
		store.add(allElement);
		return store;
	}
	
	public static int selectedShowOccurrencesTypePosition(String selected){
		int position=0;
		if(selected.compareTo(ShowOccurrencesType.ONLYERRORS.toString())==0){
			position=1;
		} else {
			if(selected.compareTo(ShowOccurrencesType.ALL.toString())==0){
				position=2;
			} else {
			}
		}
		return position;
	}
	
	
	public static String selectedShowOccurrencesType(String selected){
		if(selected.compareTo(ShowOccurrencesType.ONLYERRORS.toString())==0){
			return ShowOccurrencesType.ONLYERRORS.toString();
		} else {
			if(selected.compareTo(ShowOccurrencesType.ALL.toString())==0){
				return ShowOccurrencesType.ALL.toString();
			} else {
				return null;
			}
		}
	}
	
	public static ShowOccurrencesTypeElement selectedShowOccurrencesTypeElement(String selected){
		if(selected.compareTo(ShowOccurrencesType.ONLYERRORS.toString())==0){
			return onlyErrorsElement;
		} else {
			if(selected.compareTo(ShowOccurrencesType.ALL.toString())==0){
				return allElement;
			} else {
				return null;
			}
		}
	}
	
	
	
	
}
