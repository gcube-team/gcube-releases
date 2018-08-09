package org.gcube.portlets.user.td.resourceswidget.client.store;

import java.io.Serializable;
import java.util.ArrayList;




/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class ZoomLevelStore implements Serializable {
	private static final long serialVersionUID = -7118270169570196724L;

	protected static ArrayList<ZoomLevelElement> store;
	
	protected static ZoomLevelElement fitElement=new ZoomLevelElement(1,ZoomLevelType.Fit);
	protected static ZoomLevelElement maxZoomElement=new ZoomLevelElement(2,ZoomLevelType.MaxZoom);
	protected static ZoomLevelElement p50Element=new ZoomLevelElement(3,ZoomLevelType.P50);
	protected static ZoomLevelElement p75Element=new ZoomLevelElement(4,ZoomLevelType.P75);
	protected static ZoomLevelElement p100Element=new ZoomLevelElement(5,ZoomLevelType.P100);
	protected static ZoomLevelElement p200Element=new ZoomLevelElement(6,ZoomLevelType.P200);
	

	public static ArrayList<ZoomLevelElement> getZoomLevelTypes(){
		store=new ArrayList<ZoomLevelElement>();
		store.add(fitElement);
		store.add(maxZoomElement);
		store.add(p50Element);
		store.add(p75Element);
		store.add(p100Element);
		store.add(p200Element);
		return store;
	}
	
	public static int selectedZoomLevelPosition(String selected){
		int position=0;
		if(selected.compareTo(ZoomLevelType.Fit.toString())==0){
			position=1;
		} else {
			if(selected.compareTo(ZoomLevelType.MaxZoom.toString())==0){
				position=2;
			} else {
				if(selected.compareTo(ZoomLevelType.P50.toString())==0){
					position=3;
				} else {
					if(selected.compareTo(ZoomLevelType.P75.toString())==0){
						position=4;
					} else {
						if(selected.compareTo(ZoomLevelType.P100.toString())==0){
							position=5;
						} else {
							if(selected.compareTo(ZoomLevelType.P200.toString())==0){
								position=6;
							} else {
							
							}
						}
					}
				}
			}
		}
		return position;
	}
	
	
	public static ZoomLevelType selectedZoomLevel(String selected){
		if(selected.compareTo(ZoomLevelType.Fit.toString())==0){
			return ZoomLevelType.Fit;
		} else {
			if(selected.compareTo(ZoomLevelType.MaxZoom.toString())==0){
				return ZoomLevelType.MaxZoom;
			} else {
				if(selected.compareTo(ZoomLevelType.P50.toString())==0){
					return ZoomLevelType.P50;
				} else {
					if(selected.compareTo(ZoomLevelType.P75.toString())==0){
						return ZoomLevelType.P75;
					} else {
						if(selected.compareTo(ZoomLevelType.P100.toString())==0){
							return ZoomLevelType.P100;
						} else {
							if(selected.compareTo(ZoomLevelType.P200.toString())==0){
								return ZoomLevelType.P200;
							} else {
								return null;
							}
						}
					}
				}
			}
		}
	}
	
	public static ZoomLevelElement selectedZoomLevelElement(String selected){
		if(selected.compareTo(ZoomLevelType.Fit.toString())==0){
			return fitElement;
		} else {
			if(selected.compareTo(ZoomLevelType.MaxZoom.toString())==0){
				return maxZoomElement;
			} else {
				if(selected.compareTo(ZoomLevelType.P50.toString())==0){
					return p50Element;
				} else {
					if(selected.compareTo(ZoomLevelType.P75.toString())==0){
						return p75Element;
					} else {
						if(selected.compareTo(ZoomLevelType.P100.toString())==0){
							return p100Element;
						} else {
							if(selected.compareTo(ZoomLevelType.P200.toString())==0){
								return p200Element;
							} else {
								return null;
							}
						}
					}
				}
			}
		}
	}

	
}
