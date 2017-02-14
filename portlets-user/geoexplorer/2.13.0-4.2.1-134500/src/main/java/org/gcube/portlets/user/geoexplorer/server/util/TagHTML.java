package org.gcube.portlets.user.geoexplorer.server.util;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 26, 2013
 * 
 */
public class TagHTML {

	public static final String DOCTYPE = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">";
	
	public static final String HTML = "<HTML>";
	public static final String HTMLCLOSE = "</HTML>";
	
	public static final String HEAD = "<HEAD>";
	public static final String HEADCLOSE = "</HEAD>";
	
	public static final String TITLE = "<TITLE>";
	public static final String TITLECLOSE = "</TITLE>"; 
	
	public static final String BODY = "<BODY>";
	public static final String BODYCLOSE = "</BODY>";
	
	public static final String BR = "<br/>";
	public static final String HR = "<hr>";

	public static final String DIV = "<div>";
	public static final String DIVCLOSE = "</div>";
	
	public static final String IMG = "<img>";
	public static final String IMGCLOSE = "</img>";

	/**
	 * 
	 * @param i
	 * @return
	 */
	public static final String HI(int i) {
		return "<h" + i + ">";

	}

	/**
	 * 
	 * @param i
	 * @return
	 */
	public static final String HIClose(int i) {
		return "</h" + i + ">";

	}
	
	/**
	 * 
	 * @param id
	 * @param styleClassName
	 * @return
	 */
	public static final String DivWithIdAndStyle(String id, String styleClassName){
		
		String divIdStyle = "<div";

		if(id!=null && !id.isEmpty())
			divIdStyle+= " id="+id;
		
		if(styleClassName!=null && !styleClassName.isEmpty())
			divIdStyle+= " class="+styleClassName;
		
		return divIdStyle+=">";
	}
	
	/**
	 * 
	 * @param id
	 * @param source
	 * @return
	 */
	public static final String ImgWithIdAndSource(String id, String source){
		
		String divIdStyle = "<img";

		if(id!=null && !id.isEmpty())
			divIdStyle+= " id="+id;
		
		if(source!=null && !source.isEmpty())
			divIdStyle+= " src="+source;
		
		return divIdStyle+=">";
	}
}
