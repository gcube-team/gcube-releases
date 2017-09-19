/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view.utility;

import com.google.gwt.core.shared.GWT;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 21, 2015
 */
public class CoordinateCalculator{

	//POINT
	private Point pointTop;
	
	private int widthExt;
	private int heigthExt;
	
	private int widthInt;
	private int heigthInt;

	/**
	 * @param pointTop
	 * @param widthExt
	 * @param heigthExt
	 * @param widthInt
	 * @param heigthInt
	 */
	public CoordinateCalculator(Point pointTop, int widthExt, int heigthExt,
			int widthInt, int heigthInt) {
		this.pointTop = pointTop;
		this.widthExt = widthExt;
		this.heigthExt = heigthExt;
		this.widthInt = widthInt;
		this.heigthInt = heigthInt;
	}
	
	public Point getViewPoint(){
		
		int w = (int) widthExt/2;
		int h = (int )heigthExt/2;
		
		int centerX = w+pointTop.getX();
		int centerY = h+pointTop.getY();
		
		GWT.log("Center ["+centerX+","+centerY+"]");
//		GWT.log("widthInt ["+widthInt+"]");
//		GWT.log("heigthInt ["+heigthInt+"]");
//		int q1 = widthInt*widthInt;
//		int q2 = heigthInt*heigthInt;
//		
//		int diag = (int) Math.sqrt(q1+q2);
//	
//		GWT.log("Diag ["+diag+"]");
//		
//		int diag2 = (int) diag/2;
//		
//		GWT.log("Diag/2 ["+diag2+"]");
		
		int widthInt2 = (int) widthInt/2;
		int heigthInt2 = (int) heigthInt/2;
		
		int x = mod(centerX - widthInt2);
		int y = mod(centerY - heigthInt2);
		Point viewPoint = new Point(x, y);
		GWT.log(viewPoint.toString());
		return viewPoint;
	}
	
	private int mod(int a){
		 return a>0? a: -a;
	}
}
