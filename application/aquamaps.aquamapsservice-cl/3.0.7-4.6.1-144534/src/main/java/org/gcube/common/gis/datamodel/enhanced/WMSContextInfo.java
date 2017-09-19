package org.gcube.common.gis.datamodel.enhanced;

import java.util.ArrayList;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.StringArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.gis.WMSContextInfoType;

public class WMSContextInfo {

	private int width;
	private int height;
	private String displayProjection;
	private BoundsInfo maxExtent;
	private BoundsInfo minExtent;
	private int numZoomLevels;
	private int zoomTo;
	private double lon_center;
	private double lat_center;
	private String units;
	private String title;
	private String name;
	private double maxResolution;
	private ArrayList<String> layers;
	private ArrayList<String> keywords;
	private String _abstract;
	private String logoFormat;
	private int logoWidth;
	private int logoHeight;
	private String logoUrl;
	private String contactInformation;
	
	
	public WMSContextInfo(WMSContextInfoType toLoad) {
		super();
		
		
		this.setWidth(toLoad.width());
		this.setHeight(toLoad.height());
		this.setDisplayProjection(toLoad.displayProjection());
		if(toLoad.maxExtent() != null) this.setMaxExtent(new BoundsInfo(toLoad.maxExtent()));
		if(toLoad.minExtent() != null) this.setMinExtent(new BoundsInfo(toLoad.minExtent()));
		this.setNumZoomLevels(toLoad.numZoomLevels());
		this.setZoomTo(toLoad.zoomTo());
		this.setLon_center(toLoad.lon_center());
		this.setLat_center(toLoad.lat_center());
		this.setUnits(toLoad.units());
		this.setTitle(toLoad.title());
		this.setName(toLoad.name());
		this.setMaxResolution(toLoad.maxResolution());
		if (toLoad.layers() != null) 
			for(String layer:toLoad.layers().items())layers.add(layer);
		if (toLoad.keywords() != null) this.setKeywords(new ArrayList<String>(toLoad.keywords().items()));
		this.set_abstract(toLoad.abstractField());
		this.setLogoFormat(toLoad.logoFormat());
		this.setLogoHeight(toLoad.logoHeight());
		this.setLogoWidth(toLoad.logoWidth());
		this.setLogoUrl(toLoad.logoUrl());
		this.setContactInformation(toLoad.contactInformation());
	}
	
	public WMSContextInfoType toStubsVersion() {
		WMSContextInfoType res = new WMSContextInfoType();
		
	
		res.width(this.getWidth());
		res.height(this.getHeight());
		res.displayProjection(this.getDisplayProjection());
		if (this.getMaxExtent() != null) res.maxExtent(this.getMaxExtent().toStubsVersion());
		if (this.getMinExtent() != null) res.minExtent(this.getMinExtent().toStubsVersion());
		res.numZoomLevels(this.getNumZoomLevels());
		res.zoomTo(this.getZoomTo());
		res.lon_center(this.getLon_center());
		res.lat_center(this.getLat_center());
		res.units(this.getUnits());
		res.title(this.getTitle());
		res.name(this.getName());
		res.maxResolution(this.getMaxResolution());
		if (this.getLayers() != null) res.layers(new StringArray(getLayers()));
		if (this.getKeywords() != null) res.keywords(new StringArray(this.getKeywords()));
		res.abstractField(this.get_abstract());
		res.contactInformation(this.getContactInformation());
		res.logoFormat(this.getLogoFormat());
		res.logoHeight(this.getLogoHeight());
		res.logoUrl(this.getLogoUrl());
		res.logoWidth(this.getLogoWidth());
				
		return res;
	}
	
	

	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String get_abstract() {
		return _abstract;
	}

	public void set_abstract(String _abstract) {
		this._abstract = _abstract;
	}

	public String getDisplayProjection() {
		return displayProjection;
	}

	public void setDisplayProjection(String displayProjection) {
		this.displayProjection = displayProjection;
	}

	public int getNumZoomLevels() {
		return numZoomLevels;
	}

	public void setNumZoomLevels(int numZoomLevels) {
		this.numZoomLevels = numZoomLevels;
	}

	public int getZoomTo() {
		return zoomTo;
	}

	public void setZoomTo(int zoomTo) {
		this.zoomTo = zoomTo;
	}

	public double getLon_center() {
		return lon_center;
	}

	public void setLon_center(double lon_center) {
		this.lon_center = lon_center;
	}

	public double getLat_center() {
		return lat_center;
	}

	public void setLat_center(double lat_center) {
		this.lat_center = lat_center;
	}

	public double getMaxResolution() {
		return maxResolution;
	}

	public void setMaxResolution(double maxResolution) {
		this.maxResolution = maxResolution;
	}

	public ArrayList<String> getLayers() {
		return layers;
	}
	public void setLayers(ArrayList<String> layers) {
		this.layers = layers;
	}

	public BoundsInfo getMaxExtent() {
		return maxExtent;
	}

	public void setMaxExtent(BoundsInfo maxExtent) {
		this.maxExtent = maxExtent;
	}

	public BoundsInfo getMinExtent() {
		return minExtent;
	}

	public void setMinExtent(BoundsInfo minExtent) {
		this.minExtent = minExtent;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public WMSContextInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public ArrayList<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(ArrayList<String> keywords) {
		this.keywords = keywords;
	}

	public String getLogoFormat() {
		return logoFormat;
	}

	public void setLogoFormat(String logoFormat) {
		this.logoFormat = logoFormat;
	}

	public int getLogoWidth() {
		return logoWidth;
	}

	public void setLogoWidth(int logoWidth) {
		this.logoWidth = logoWidth;
	}

	public int getLogoHeight() {
		return logoHeight;
	}

	public void setLogoHeight(int logoHeight) {
		this.logoHeight = logoHeight;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getContactInformation() {
		return contactInformation;
	}

	public void setContactInformation(String contactInformation) {
		this.contactInformation = contactInformation;
	}
}