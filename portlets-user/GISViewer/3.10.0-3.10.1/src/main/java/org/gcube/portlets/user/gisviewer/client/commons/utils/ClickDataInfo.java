package org.gcube.portlets.user.gisviewer.client.commons.utils;


/*
 * author: ceras
 */
public class ClickDataInfo {
	public static enum TYPE {POINT, BOX}

	private static final double THRESHOLD_AREA = 300;

	private int x, y, w, h;
	private String bbox;
//	private List<GeoserverItem> geoserverItems = new ArrayList<GeoserverItem>();
	private TYPE type=null;
	private double x1;
	private double y1;
	private double x2;
	private double y2; 
	
	//ADDED BY FRANCESCO M.
	private int limit = Integer.MAX_VALUE;
	
	public ClickDataInfo(int x, int y, int w, int h, String bbox) {
		super();
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.bbox = bbox;
		this.type = TYPE.POINT;
//		System.out.println("POINT SELECTED: x="+x+", y="+y+", w="+w+", h="+h+", bbox="+bbox);
	}
	
	/**
	 * @param bbox2
	 */
	public ClickDataInfo(double x1, double y1, double x2, double y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		
        // note: the bounding box is left,lower,right,upper
        this.bbox = y1 +","+ x1 +","+ y2 +","+ x2;
        
		this.type = TYPE.BOX;

		double area = (x2-x1) * (y2-y1);
//        System.out.println("BOX SELECTED: BBOX="+bbox + ", AREA="+area);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getW() {
		return w;
	}

	public void setW(int w) {
		this.w = w;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

	public String getBbox() {
		return bbox;
	}
	
	public void setBbox(String bbox) {
		this.bbox = bbox;
	}
	
	public boolean isPoint() {
		return this.type==TYPE.POINT;
	}

	public boolean isBox() {
		return this.type==TYPE.BOX;
	}
	
	public boolean isHardQuery() {
		if (this.isPoint())
			return false;
		else {
			double area = (x2-x1) * (y2-y1);
			return (area > THRESHOLD_AREA);
		}
	}
	
	/**
	 * @return the x1
	 */
	public double getX1() {
		return x1;
	}
	
	/**
	 * @return the x2
	 */
	public double getX2() {
		return x2;
	}
	
	/**
	 * @return the y1
	 */
	public double getY1() {
		return y1;
	}
	
	/**
	 * @return the y2
	 */
	public double getY2() {
		return y2;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
	
}
