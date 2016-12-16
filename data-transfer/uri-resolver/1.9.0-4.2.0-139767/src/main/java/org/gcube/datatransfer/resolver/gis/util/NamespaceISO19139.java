/**
 *
 */
package org.gcube.datatransfer.resolver.gis.util;


public abstract class NamespaceISO19139 {


	//Namespaces
	private final String namespaceCSW = "http://www.opengis.net/cat/csw/2.0.2";
	private final String namespaceDC = "http://purl.org/dc/elements/1.1/";
	private final String namespaceOWS = "http://www.opengis.net/ows";
	private final String namespaceGMD = "http://www.isotc211.org/2005/gmd";
	private final String namespaceGCO = "http://www.isotc211.org/2005/gco";
	private final String namespaceXLINK = "http://www.w3.org/1999/xlink";
	private final String namespaceSRV = "http://www.isotc211.org/2005/srv";
	private final String namespaceXSI = "http://www.w3.org/2001/XMLSchema-instance";
	private final String namespaceGML = "http://www.opengis.net/gml";
	private final String namespaceGTS = "http://www.isotc211.org/2005/gts";
	private final String namespaceGEONET = "http://www.fao.org/geonetwork";
	private final String namespaceGMX = "http://www.isotc211.org/2005/gmx";
	private final String namespaceWMS = "http://www.opengis.net/wms";
	private final String namespaceDCT = "http://purl.org/dc/terms/";

	//Prefixs
	private final String prefixCSW = "csw";
	private final String prefixGMD = "gmd";
	private final String prefixOWS = "ows";
	private final String prefixDC = "dc";
	private final String prefixDCT = "dct";
	private final String prefixGCO = "gco";
	private final String prefixXLINK= "xlink";
	private final String prefixSRV = "srv";
	private final String prefixXSI = "xsi";
	private final String prefixGML = "gml";
	private final String prefixGTS = "gts";
	private final String prefixGEONET = "geonet";
	private final String prefixGMX = "gmx";
	private final String prefixWMS = "wms";


	public String getNamespaceCSW() {
		return namespaceCSW;
	}
	public String getNamespaceDC() {
		return namespaceDC;
	}
	public String getNamespaceOWS() {
		return namespaceOWS;
	}
	public String getNamespaceGMD() {
		return namespaceGMD;
	}
	public String getNamespaceGCO() {
		return namespaceGCO;
	}
	public String getNamespaceXLINK() {
		return namespaceXLINK;
	}
	public String getNamespaceSRV() {
		return namespaceSRV;
	}
	public String getNamespaceXSI() {
		return namespaceXSI;
	}
	public String getNamespaceGML() {
		return namespaceGML;
	}
	public String getNamespaceGTS() {
		return namespaceGTS;
	}
	public String getNamespaceGEONET() {
		return namespaceGEONET;
	}
	public String getPrefixCSW() {
		return prefixCSW;
	}
	public String getPrefixGMD() {
		return prefixGMD;
	}
	public String getPrefixOWS() {
		return prefixOWS;
	}
	public String getPrefixDC() {
		return prefixDC;
	}
	public String getPrefixGCO() {
		return prefixGCO;
	}
	public String getPrefixXLINK() {
		return prefixXLINK;
	}
	public String getPrefixSRV() {
		return prefixSRV;
	}
	public String getPrefixGML() {
		return prefixGML;
	}
	public String getPrefixGTS() {
		return prefixGTS;
	}
	public String getPrefixGEONET() {
		return prefixGEONET;
	}
	public String getPrefixXSI() {
		return prefixXSI;
	}
	public String getPrefixGMX() {
		return prefixGMX;
	}
	public String getNamespaceGMX() {
		return namespaceGMX;
	}
	public String getPrefixWMS() {
		return prefixWMS;
	}
	public String getNamespaceWMS() {
		return namespaceWMS;
	}
	public String getNamespaceDCT() {
		return namespaceDCT;
	}
	public String getPrefixDCT() {
		return prefixDCT;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("NamespaceISO19139 [namespaceCSW=");
		builder.append(namespaceCSW);
		builder.append(", namespaceDC=");
		builder.append(namespaceDC);
		builder.append(", namespaceOWS=");
		builder.append(namespaceOWS);
		builder.append(", namespaceGMD=");
		builder.append(namespaceGMD);
		builder.append(", namespaceGCO=");
		builder.append(namespaceGCO);
		builder.append(", namespaceXLINK=");
		builder.append(namespaceXLINK);
		builder.append(", namespaceSRV=");
		builder.append(namespaceSRV);
		builder.append(", namespaceXSI=");
		builder.append(namespaceXSI);
		builder.append(", namespaceGML=");
		builder.append(namespaceGML);
		builder.append(", namespaceGTS=");
		builder.append(namespaceGTS);
		builder.append(", namespaceGEONET=");
		builder.append(namespaceGEONET);
		builder.append(", namespaceGMX=");
		builder.append(namespaceGMX);
		builder.append(", namespaceWMS=");
		builder.append(namespaceWMS);
		builder.append(", namespaceDCT=");
		builder.append(namespaceDCT);
		builder.append(", prefixCSW=");
		builder.append(prefixCSW);
		builder.append(", prefixGMD=");
		builder.append(prefixGMD);
		builder.append(", prefixOWS=");
		builder.append(prefixOWS);
		builder.append(", prefixDC=");
		builder.append(prefixDC);
		builder.append(", prefixDCT=");
		builder.append(prefixDCT);
		builder.append(", prefixGCO=");
		builder.append(prefixGCO);
		builder.append(", prefixXLINK=");
		builder.append(prefixXLINK);
		builder.append(", prefixSRV=");
		builder.append(prefixSRV);
		builder.append(", prefixXSI=");
		builder.append(prefixXSI);
		builder.append(", prefixGML=");
		builder.append(prefixGML);
		builder.append(", prefixGTS=");
		builder.append(prefixGTS);
		builder.append(", prefixGEONET=");
		builder.append(prefixGEONET);
		builder.append(", prefixGMX=");
		builder.append(prefixGMX);
		builder.append(", prefixWMS=");
		builder.append(prefixWMS);
		builder.append("]");
		return builder.toString();
	}

}