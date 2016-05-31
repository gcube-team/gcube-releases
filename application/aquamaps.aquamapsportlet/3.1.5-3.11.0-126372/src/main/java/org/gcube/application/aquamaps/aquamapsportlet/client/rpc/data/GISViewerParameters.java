package org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GISViewerParameters extends Msg implements IsSerializable {

	private List<String> layers=new ArrayList<String>();
	
	public GISViewerParameters() {
		// TODO Auto-generated constructor stub
	}
	
	public GISViewerParameters(List<String> layers,boolean status,String msg) {
		super(status, msg);
		setLayers(layers);
	}
	public List<String> getLayers() {
		return layers;
	}
	public void setLayers(List<String> layers) {
		this.layers.clear();
		this.layers.addAll(layers);
	}
	
}
