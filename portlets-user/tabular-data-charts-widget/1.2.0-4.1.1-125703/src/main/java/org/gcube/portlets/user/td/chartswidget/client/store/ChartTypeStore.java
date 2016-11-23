package org.gcube.portlets.user.td.chartswidget.client.store;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.td.widgetcommonevent.shared.charts.ChartType;




/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ChartTypeStore implements Serializable {

	private static final long serialVersionUID = -1908324094430432681L;

	protected static ArrayList<ChartTypeElement> store;
	
	protected static ChartTypeElement topRatingElement=new ChartTypeElement(1,ChartType.TopRating);
	

	public static ArrayList<ChartTypeElement> getChartsType(){
		store=new ArrayList<ChartTypeElement>();
		store.add(topRatingElement);
		return store;
	}
	
	public static int selectedChartPosition(String selected){
		int position=0;
		if(selected.compareTo(ChartType.TopRating.toString())==0){
			position=1;
		} else {
		
		}
		return position;
	}
	
	
	public static ChartType selectedChart(String selected){
		if(selected.compareTo(ChartType.TopRating.toString())==0){
			return ChartType.TopRating;
		} else {
			return null;
		}
	}
	
	public static ChartTypeElement selectedChartElement(String selected){
		if(selected.compareTo(ChartType.TopRating.toString())==0){
			return topRatingElement;
		} else {
			return null;
		}
	}

	
}
