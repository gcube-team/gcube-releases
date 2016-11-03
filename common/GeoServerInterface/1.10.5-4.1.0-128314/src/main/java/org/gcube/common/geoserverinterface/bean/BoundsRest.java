package org.gcube.common.geoserverinterface.bean;

import java.io.Serializable;

public class BoundsRest implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4828601642585472060L;
	/**
	 * @uml.property  name="minx"
	 */
	private double minx = 0.0;
	/**
	 * @uml.property  name="maxx"
	 */
	private double maxx = 0.0;
	/**
	 * @uml.property  name="miny"
	 */
	private double miny = 0.0;
	/**
	 * @uml.property  name="maxy"
	 */
	private double maxy = 0.0;
	/**
	 * @uml.property  name="crs"
	 */
	private String crs = "";
	public BoundsRest() {
		super();
		// TODO Auto-generated constructor stub
	}
	public BoundsRest(double minx, double maxx, double miny, double maxy, String crs) {
		super();
		this.minx = minx;
		this.maxx = maxx;
		this.miny = miny;
		this.maxy = maxy;
		this.crs = crs;
	}
	/**
	 * @return
	 * @uml.property  name="minx"
	 */
	public double getMinx() {
		return minx;
	}
	/**
	 * @param minx
	 * @uml.property  name="minx"
	 */
	public void setMinx(double minx) {
		this.minx = minx;
	}
	/**
	 * @return
	 * @uml.property  name="maxx"
	 */
	public double getMaxx() {
		return maxx;
	}
	/**
	 * @param maxx
	 * @uml.property  name="maxx"
	 */
	public void setMaxx(double maxx) {
		this.maxx = maxx;
	}
	/**
	 * @return
	 * @uml.property  name="miny"
	 */
	public double getMiny() {
		return miny;
	}
	/**
	 * @param miny
	 * @uml.property  name="miny"
	 */
	public void setMiny(double miny) {
		this.miny = miny;
	}
	/**
	 * @return
	 * @uml.property  name="maxy"
	 */
	public double getMaxy() {
		return maxy;
	}
	/**
	 * @param maxy
	 * @uml.property  name="maxy"
	 */
	public void setMaxy(double maxy) {
		this.maxy = maxy;
	}
	/**
	 * @return
	 * @uml.property  name="crs"
	 */
	public String getCrs() {
		return crs;
	}
	/**
	 * @param crs
	 * @uml.property  name="crs"
	 */
	public void setCrs(String crs) {
		this.crs = crs;
	}
}
