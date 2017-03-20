package org.gcube.spatial.data.geoutility.bean;


/**
 *
 * Param	Mandatory
 * service 	Yes 		Service name. Value is WMS.
 * version 	Yes 		Service version. Value is one of 1.0.0, 1.1.0, 1.1.1, 1.3.
 * request 	Yes 		Operation name. Value is GetMap.
 * layers 	Yes 		Layers to display on map. Value is a comma-separated list of layer names.
 * styles 	Yes 		Styles in which layers are to be rendered. Value is a comma-separated list of style names, or empty if default styling is required. Style names may be empty in the list, to use default layer styling.
 * srs or crs 	Yes 	Spatial Reference System for map output. Value is in form EPSG:nnn. crs is the parameter key used in WMS 1.3.0.
 * bbox 	Yes 		Bounding box for map extent. Value is minx,miny,maxx,maxy in units of the SRS.
 * width 	Yes 		Width of map output, in pixels.
 * height 	Yes 		Height of map output, in pixels.
 * format 	Yes 		Format for the map output. See WMS output formats for supported values.
 * transparent 	No 		Whether the map background should be transparent. Values are true or false. Default is false
 * bgcolor 	No 			Background color for the map image. Value is in the form RRGGBB. Default is FFFFFF (white).
 * exceptions 	No 		Format in which to report exceptions. Default value is application/vnd.ogc.se_xml.
 * time 	No 			Time value or range for map data. See Time Support in Geoserver WMS for more information.
 * sld 		No 			A URL referencing a StyledLayerDescriptor XML file which controls or enhances map layers and styling
 * sld_body 	No 		A URL-encoded StyledLayerDescriptor XML document which controls or enhances map layers and styling
 *
 */
/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 26, 2013
 *
 */
public enum WmsParameters {

	SERVICE("SERVICE", "WMS"),
	VERSION("VERSION", "1.1.0"),
	REQUEST("REQUEST", "GetMap"),
	LAYERS("LAYERS", ""),
	STYLES("STYLES",""),
	BBOX("BBOX","-180,-90,180,90"),
	WIDTH("WIDTH","676"),
	HEIGHT("HEIGHT","230"),
	SRS("SRS","EPSG:4326"),
	CRS("CRS","EPSG:4326"), //WMS 1.3.0 COMPLIANT
	FORMAT("FORMAT","image/png"),
	TRANSPARENT("TRANSPARENT","true");

	private String parameter;
	private String value;

	WmsParameters(String parameter, String value){
		this.parameter = parameter;
		this.value = value;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
