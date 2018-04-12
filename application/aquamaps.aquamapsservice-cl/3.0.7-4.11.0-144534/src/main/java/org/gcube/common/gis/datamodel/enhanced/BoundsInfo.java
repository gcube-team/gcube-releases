package org.gcube.common.gis.datamodel.enhanced;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.gis.BoundsInfoType;

public class BoundsInfo {
	
	private double minx = 0.0;
	private double maxx=0.0;
	private double miny=0.0;
	private double maxy=0.0;
	private String crs="";
	
	public BoundsInfo(double minx, double maxx, double miny, double maxy,
			String crs) {
		super();
		this.minx = minx;
		this.maxx = maxx;
		this.miny = miny;
		this.maxy = maxy;
		this.crs = crs;
	}

	public double getMinx() {
		return minx;
	}

	public void setMinx(double minx) {
		this.minx = minx;
	}

	public double getMaxx() {
		return maxx;
	}

	public void setMaxx(double maxx) {
		this.maxx = maxx;
	}

	public double getMiny() {
		return miny;
	}

	public void setMiny(double miny) {
		this.miny = miny;
	}

	public double getMaxy() {
		return maxy;
	}

	public void setMaxy(double maxy) {
		this.maxy = maxy;
	}

	public String getCrs() {
		return crs;
	}

	public void setCrs(String crs) {
		this.crs = crs;
	}

	public BoundsInfo(BoundsInfoType toLoad){
		this.setCrs(toLoad.crs());
		this.setMaxx(toLoad.maxx());
		this.setMaxy(toLoad.maxy());
		this.setMinx(toLoad.minx());
		this.setMiny(toLoad.miny());
	}
	
	public BoundsInfo() {
		// TODO Auto-generated constructor stub
	}

	public BoundsInfoType toStubsVersion() {
		BoundsInfoType res = new BoundsInfoType();
		res.crs(this.getCrs());
		res.maxx(this.getMaxx());
		res.maxy(this.getMaxy());
		res.minx(this.getMinx());
		res.miny(this.getMiny());
		
		return res;
	}
}
