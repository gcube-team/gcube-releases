package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.gis;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.gisTypesNS;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.StringArray;

@XmlRootElement(namespace=gisTypesNS)
public class WMSContextInfoType {

	@XmlElement(namespace=gisTypesNS)
	private int width;
	@XmlElement(namespace=gisTypesNS)
	private int height;
	@XmlElement(namespace=gisTypesNS)
	private String displayProjection;
	@XmlElement(namespace=gisTypesNS)
	private BoundsInfoType maxExtent;
	@XmlElement(namespace=gisTypesNS)
	private BoundsInfoType minExtent;
	@XmlElement(namespace=gisTypesNS)
	private int numZoomLevels;
	@XmlElement(namespace=gisTypesNS)
	private int zoomTo;
	@XmlElement(namespace=gisTypesNS)
	private double lon_center;
	@XmlElement(namespace=gisTypesNS)
	private double lat_center;
	@XmlElement(namespace=gisTypesNS)
	private String units;
	@XmlElement(namespace=gisTypesNS)
	private String title;
	@XmlElement(namespace=gisTypesNS)
	private String name;
	@XmlElement(namespace=gisTypesNS)
	private double maxResolution;
	@XmlElement(namespace=gisTypesNS)
	private StringArray layers;
	@XmlElement(namespace=gisTypesNS)
	private StringArray keywords;	
	@XmlElement(namespace=gisTypesNS,name="abstract")
	private String abstractField;
	@XmlElement(namespace=gisTypesNS)
	private String logoFormat;
	@XmlElement(namespace=gisTypesNS)
	private int logoWidth;
	@XmlElement(namespace=gisTypesNS)
	private int logoHeight;
	@XmlElement(namespace=gisTypesNS)
	private String logoUrl;
	@XmlElement(namespace=gisTypesNS)
	private String contactInformation;
	
	public WMSContextInfoType() {
		// TODO Auto-generated constructor stub
	}

	

	public WMSContextInfoType(int width, int height, String displayProjection,
			BoundsInfoType maxExtent, BoundsInfoType minExtent,
			int numZoomLevels, int zoomTo, double lon_center,
			double lat_center, String units, String title, String name,
			double maxResolution, StringArray layers, StringArray keywords,
			String abstractField, String logoFormat, int logoWidth,
			int logoHeight, String logoUrl, String contactInformation) {
		super();
		this.width = width;
		this.height = height;
		this.displayProjection = displayProjection;
		this.maxExtent = maxExtent;
		this.minExtent = minExtent;
		this.numZoomLevels = numZoomLevels;
		this.zoomTo = zoomTo;
		this.lon_center = lon_center;
		this.lat_center = lat_center;
		this.units = units;
		this.title = title;
		this.name = name;
		this.maxResolution = maxResolution;
		this.layers = layers;
		this.keywords = keywords;
		this.abstractField = abstractField;
		this.logoFormat = logoFormat;
		this.logoWidth = logoWidth;
		this.logoHeight = logoHeight;
		this.logoUrl = logoUrl;
		this.contactInformation = contactInformation;
	}



	/**
	 * @return the width
	 */
	public int width() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void width(int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int height() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void height(int height) {
		this.height = height;
	}

	/**
	 * @return the displayProjection
	 */
	public String displayProjection() {
		return displayProjection;
	}

	/**
	 * @param displayProjection the displayProjection to set
	 */
	public void displayProjection(String displayProjection) {
		this.displayProjection = displayProjection;
	}

	/**
	 * @return the maxExtent
	 */
	public BoundsInfoType maxExtent() {
		return maxExtent;
	}

	/**
	 * @param maxExtent the maxExtent to set
	 */
	public void maxExtent(BoundsInfoType maxExtent) {
		this.maxExtent = maxExtent;
	}

	/**
	 * @return the minExtent
	 */
	public BoundsInfoType minExtent() {
		return minExtent;
	}

	/**
	 * @param minExtent the minExtent to set
	 */
	public void minExtent(BoundsInfoType minExtent) {
		this.minExtent = minExtent;
	}

	/**
	 * @return the numZoomLevels
	 */
	public int numZoomLevels() {
		return numZoomLevels;
	}

	/**
	 * @param numZoomLevels the numZoomLevels to set
	 */
	public void numZoomLevels(int numZoomLevels) {
		this.numZoomLevels = numZoomLevels;
	}

	/**
	 * @return the lon_center
	 */
	public double lon_center() {
		return lon_center;
	}

	/**
	 * @param lon_center the lon_center to set
	 */
	public void lon_center(double lon_center) {
		this.lon_center = lon_center;
	}

	/**
	 * @return the lat_center
	 */
	public double lat_center() {
		return lat_center;
	}

	/**
	 * @param lat_center the lat_center to set
	 */
	public void lat_center(double lat_center) {
		this.lat_center = lat_center;
	}

	/**
	 * @return the units
	 */
	public String units() {
		return units;
	}

	/**
	 * @param units the units to set
	 */
	public void units(String units) {
		this.units = units;
	}

	/**
	 * @return the title
	 */
	public String title() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void title(String title) {
		this.title = title;
	}

	/**
	 * @return the name
	 */
	public String name() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void name(String name) {
		this.name = name;
	}

	/**
	 * @return the maxResolution
	 */
	public double maxResolution() {
		return maxResolution;
	}

	/**
	 * @param maxResolution the maxResolution to set
	 */
	public void maxResolution(double maxResolution) {
		this.maxResolution = maxResolution;
	}

	/**
	 * @return the layers
	 */
	public StringArray layers() {
		return layers;
	}

	/**
	 * @param layers the layers to set
	 */
	public void layers(StringArray layers) {
		this.layers = layers;
	}

	/**
	 * @return the keywords
	 */
	public StringArray keywords() {
		return keywords;
	}

	/**
	 * @param keywords the keywords to set
	 */
	public void keywords(StringArray keywords) {
		this.keywords = keywords;
	}

	/**
	 * @return the abstractField
	 */
	public String abstractField() {
		return abstractField;
	}

	/**
	 * @param abstractField the abstractField to set
	 */
	public void abstractField(String abstractField) {
		this.abstractField = abstractField;
	}

	/**
	 * @return the logoFormat
	 */
	public String logoFormat() {
		return logoFormat;
	}

	/**
	 * @param logoFormat the logoFormat to set
	 */
	public void logoFormat(String logoFormat) {
		this.logoFormat = logoFormat;
	}

	/**
	 * @return the logoWidth
	 */
	public int logoWidth() {
		return logoWidth;
	}

	/**
	 * @param logoWidth the logoWidth to set
	 */
	public void logoWidth(int logoWidth) {
		this.logoWidth = logoWidth;
	}

	/**
	 * @return the logoHeight
	 */
	public int logoHeight() {
		return logoHeight;
	}

	/**
	 * @param logoHeight the logoHeight to set
	 */
	public void logoHeight(int logoHeight) {
		this.logoHeight = logoHeight;
	}

	/**
	 * @return the logoUrl
	 */
	public String logoUrl() {
		return logoUrl;
	}

	/**
	 * @param logoUrl the logoUrl to set
	 */
	public void logoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	/**
	 * @return the contactInformation
	 */
	public String contactInformation() {
		return contactInformation;
	}

	/**
	 * @param contactInformation the contactInformation to set
	 */
	public void contactInformation(String contactInformation) {
		this.contactInformation = contactInformation;
	}

	
	public int zoomTo(){
		return zoomTo;
	}
	
	public void zoomTo(int zoomTo){
		this.zoomTo=zoomTo;
	}
	
}
