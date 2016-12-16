package org.gcube.portlets.user.td.chartswidget.client.store;

import java.io.Serializable;
import java.util.ArrayList;




/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ValueOperationStore implements Serializable {

	private static final long serialVersionUID = 8878584108478017984L;

	protected static ArrayList<ValueOperationElement> store;
	
	protected static ValueOperationElement AVGElement=new ValueOperationElement(1,ValueOperationType.AVG);
	protected static ValueOperationElement MAXElement=new ValueOperationElement(2,ValueOperationType.MAX);
	protected static ValueOperationElement MINElement=new ValueOperationElement(3,ValueOperationType.MIN);
	protected static ValueOperationElement SUMElement=new ValueOperationElement(4,ValueOperationType.SUM);
	

	public static ArrayList<ValueOperationElement> getValuesOperationType(){
		store=new ArrayList<ValueOperationElement>();
		store.add(AVGElement);
		store.add(MAXElement);
		store.add(MINElement);
		store.add(SUMElement);
		
		
		return store;
	}
	
	public static int selectedPosition(String selected){
		int position=0;
		if(selected.compareTo(ValueOperationType.AVG.toString())==0){
			position=1;
		} else {
			if(selected.compareTo(ValueOperationType.MAX.toString())==0){
				position=2;
			} else {
				if(selected.compareTo(ValueOperationType.MIN.toString())==0){
					position=3;
				} else {
					if(selected.compareTo(ValueOperationType.SUM.toString())==0){
						position=4;
					} else {
					
					}
				}
			}
		}
		return position;
	}
	
	
	public static ValueOperationType selectedValueOperation(String selected){
		if(selected.compareTo(ValueOperationType.AVG.toString())==0){
			return ValueOperationType.AVG;
		} else {
			if(selected.compareTo(ValueOperationType.MAX.toString())==0){
				return ValueOperationType.MAX;
			} else {
				if(selected.compareTo(ValueOperationType.MIN.toString())==0){
					return ValueOperationType.MIN;
				} else {
					if(selected.compareTo(ValueOperationType.SUM.toString())==0){
						return ValueOperationType.SUM;
					} else {
						return null;
					}
				}
			}
		}
	}
	
	public static ValueOperationElement selectedValueOperationElement(String selected){
		if(selected.compareTo(ValueOperationType.AVG.toString())==0){
			return AVGElement;
		} else {
			if(selected.compareTo(ValueOperationType.MAX.toString())==0){
				return MAXElement;
			} else {
				if(selected.compareTo(ValueOperationType.MIN.toString())==0){
					return MINElement;
				} else {
					if(selected.compareTo(ValueOperationType.SUM.toString())==0){
						return SUMElement;
					} else {
						return null;
					}
				}
			}
		}
	}

	
}
