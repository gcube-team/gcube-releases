package org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.user.client.rpc.IsSerializable;

public class ImageItem extends BaseModelData implements IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5445347504803383871L;
	public static String PATH="PATH";
	public static String LABEL="LABEL";
	
	public ImageItem(){
		
	}
	
	public ImageItem (String url){
		url=url.trim();
		set(PATH,url);
		int start=url.lastIndexOf('/')+1;
		int end=url.lastIndexOf('.');
		set(LABEL,url.substring(start,(end>start?end:url.length())));
	}
	
}
