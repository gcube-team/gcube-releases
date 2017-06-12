package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.gis;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.gisTypesNS;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace=gisTypesNS)
public class BoundsInfoType {
	@XmlElement(namespace=gisTypesNS)
	private double minx;
	@XmlElement(namespace=gisTypesNS)
	private double maxx;
	@XmlElement(namespace=gisTypesNS)
	private double miny;
	@XmlElement(namespace=gisTypesNS)
	private double maxy;
	@XmlElement(namespace=gisTypesNS)
	private String crs;
	
	
	public BoundsInfoType() {
		// TODO Auto-generated constructor stub
	}


	public BoundsInfoType(double minx, double maxx, double miny, double maxy,
			String crs) {
		super();
		this.minx = minx;
		this.maxx = maxx;
		this.miny = miny;
		this.maxy = maxy;
		this.crs = crs;
	}


	/**
	 * @return the minx
	 */
	public double minx() {
		return minx;
	}


	/**
	 * @param minx the minx to set
	 */
	public void minx(double minx) {
		this.minx = minx;
	}


	/**
	 * @return the maxx
	 */
	public double maxx() {
		return maxx;
	}


	/**
	 * @param maxx the maxx to set
	 */
	public void maxx(double maxx) {
		this.maxx = maxx;
	}


	/**
	 * @return the miny
	 */
	public double miny() {
		return miny;
	}


	/**
	 * @param miny the miny to set
	 */
	public void miny(double miny) {
		this.miny = miny;
	}


	/**
	 * @return the maxy
	 */
	public double maxy() {
		return maxy;
	}


	/**
	 * @param maxy the maxy to set
	 */
	public void maxy(double maxy) {
		this.maxy = maxy;
	}


	/**
	 * @return the crs
	 */
	public String crs() {
		return crs;
	}


	/**
	 * @param crs the crs to set
	 */
	public void crs(String crs) {
		this.crs = crs;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BoundsInfoType [minx=");
		builder.append(minx);
		builder.append(", maxx=");
		builder.append(maxx);
		builder.append(", miny=");
		builder.append(miny);
		builder.append(", maxy=");
		builder.append(maxy);
		builder.append(", crs=");
		builder.append(crs);
		builder.append("]");
		return builder.toString();
	}
	
	
}
