package org.gcube.portlets.admin.accountingmanager.client.maindata.charts.utils;

/**
 * Note Storage Data are in kB
 * 
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ByteUnitMeasure {
	public static final String KILOBYTE = "KiloByte";
	public static final String MEGABYTE = "MegaByte";
	public static final String GIGABYTE = "GigaByte";
	public static final String TERABYTE = "TeraByte";

	public static final String kB = "kB";
	public static final String MB = "MB";
	public static final String GB = "GB";
	public static final String TB = "TB";

	public static long getKiloByteDimForStorage() {
		return 1;
	}

	public static long getMegaByteDimForStorage() {
		return 1000;
	}

	public static long getGigaByteDimForStorage() {
		return 1000000;
	}

	public static long getTeraByteDimForStorage() {
		return 1000000000;

	}

}
