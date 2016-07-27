package org.gcube.portlets.user.gisviewer.client.commons.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * The Class LayerItem.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 10, 2016
 */
public class LayerItem implements IsSerializable, Cloneable {

	private String name;
	private String url;
	private String layer;
	private String style;
	private boolean isTrasparent=false;
	private boolean isBaseLayer=true;
	private boolean isBorderLayer=false;
	private boolean wrapDateLine=true;
	private int buffer = 0;
	private boolean hasLegend=false;
	private boolean isVisible=true;
	private boolean isOnMap=true;
	private boolean clickData=true;
	private BoundsMap maxExtent = null;
	private String defaultStyle = "";
	private double opacity = -1;
	private ArrayList<String> styles = new ArrayList<String>();
	private String title=null;

	private List<Property> properties = new ArrayList<Property>();
	private String dataStore = "";
	private String geoserverUrl;
	private String geoserverWmsUrl;
	private String cqlFilter = "";
	private long id;
	private long order;
	private boolean isExternal = false;
	public static final String FLOAT_TYPE = "xsd:float";
	public static final String INT_TYPE = "xsd:int";

	private Map<String, String> wmsNotStandardParams = null;

	//USED BY NCWMS
	private boolean isNcWms = false;

	public static long ID_COUNTER = 0;

	//ADDED BY FRANCESCO M.
	public String serverWmsRequest;

	private boolean cqlFilterAvailable = false;
	private String UUID;
	private ZAxis zAxis = null;
	private Double zAxisSelected = null;

	/**
	 * Instantiates a new layer item.
	 */
	public LayerItem() {
		super();
		this.id = ID_COUNTER++;
	}

	/**
	 * Instantiates a new layer item.
	 *
	 * @param isExternal the is external
	 */
	public LayerItem(boolean isExternal) {
		this();
		this.setExternal(isExternal);
	}

	/**
	 * Checks if is checks for legend.
	 *
	 * @return true, if is checks for legend
	 */
	public boolean isHasLegend() {
		return hasLegend;
	}

	/**
	 * Sets the checks for legend.
	 *
	 * @param hasLegend the new checks for legend
	 */
	public void setHasLegend(boolean hasLegend) {
		this.hasLegend = hasLegend;
	}

	/**
	 * Gets the max extent.
	 *
	 * @return the max extent
	 */
	public BoundsMap getMaxExtent() {
		return maxExtent;
	}

	/**
	 * Sets the max extent.
	 *
	 * @param maxExtent the new max extent
	 */
	public void setMaxExtent(BoundsMap maxExtent) {
		this.maxExtent = maxExtent;
	}

	/**
	 * Sets the max extent.
	 *
	 * @param lowerLeftX the lower left x
	 * @param lowerLeftY the lower left y
	 * @param upperRightX the upper right x
	 * @param upperRightY the upper right y
	 * @param crs the crs
	 */
	public void setMaxExtent(double lowerLeftX,
								double lowerLeftY,
								double upperRightX,
								double upperRightY,
								String crs) {

		this.maxExtent = new BoundsMap(lowerLeftX, lowerLeftY, upperRightX, upperRightY, crs);
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the url.
	 *
	 * @param url the new url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Gets the layer.
	 *
	 * @return the layer
	 */
	public String getLayer() {
		return layer;
	}

	/**
	 * Sets the layer.
	 *
	 * @param layer the new layer
	 */
	public void setLayer(String layer) {
		this.layer = layer;
	}

	/**
	 * Gets the style.
	 *
	 * @return the style
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * Sets the style.
	 *
	 * @param style the new style
	 */
	public void setStyle(String style) {
		this.style = style;
	}

	/**
	 * Checks if is trasparent.
	 *
	 * @return true, if is trasparent
	 */
	public boolean isTrasparent() {
		return isTrasparent;
	}

	/**
	 * Sets the trasparent.
	 *
	 * @param isTrasparent the new trasparent
	 */
	public void setTrasparent(boolean isTrasparent) {
		this.isTrasparent = isTrasparent;
	}

	/**
	 * Checks if is base layer.
	 *
	 * @return true, if is base layer
	 */
	public boolean isBaseLayer() {
		return isBaseLayer;
	}

	/**
	 * Sets the base layer.
	 *
	 * @param isBaseLayer the new base layer
	 */
	public void setBaseLayer(boolean isBaseLayer) {
		this.isBaseLayer = isBaseLayer;
	}

	/**
	 * Checks if is wrap date line.
	 *
	 * @return true, if is wrap date line
	 */
	public boolean isWrapDateLine() {
		return wrapDateLine;
	}

	/**
	 * Sets the wrap date line.
	 *
	 * @param wrapDateLine the new wrap date line
	 */
	public void setWrapDateLine(boolean wrapDateLine) {
		this.wrapDateLine = wrapDateLine;
	}

	/**
	 * Gets the buffer.
	 *
	 * @return the buffer
	 */
	public int getBuffer() {
		return buffer;
	}

	/**
	 * Sets the buffer.
	 *
	 * @param buffer the new buffer
	 */
	public void setBuffer(int buffer) {
		this.buffer = buffer;
	}

	/**
	 * Checks if is visible.
	 *
	 * @return true, if is visible
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/**
	 * Sets the visible.
	 *
	 * @param isVisible the new visible
	 */
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	/**
	 * Checks if is on map.
	 *
	 * @return true, if is on map
	 */
	public boolean isOnMap() {
		return isOnMap;
	}

	/**
	 * Sets the on map.
	 *
	 * @param isOnMap the new on map
	 */
	public void setOnMap(boolean isOnMap) {
		this.isOnMap = isOnMap;
	}

	/**
	 * Checks if is click data.
	 *
	 * @return true, if is click data
	 */
	public boolean isClickData() {
		return clickData;
	}

	/**
	 * Sets the click data.
	 *
	 * @param clickData the new click data
	 */
	public void setClickData(boolean clickData) {
		this.clickData = clickData;
	}

	/**
	 * Gets the default style.
	 *
	 * @return the default style
	 */
	public String getDefaultStyle() {
		return defaultStyle;
	}

	/**
	 * Sets the default style.
	 *
	 * @param defaultStyle the new default style
	 */
	public void setDefaultStyle(String defaultStyle) {
		this.defaultStyle = defaultStyle;
	}

	/**
	 * Gets the styles.
	 *
	 * @return the styles
	 */
	public ArrayList<String> getStyles() {
		return styles;
	}

	/**
	 * Sets the styles.
	 *
	 * @param styles the new styles
	 */
	public void setStyles(ArrayList<String> styles) {
		this.styles = styles;
	}

	/**
	 * Gets the opacity.
	 *
	 * @return the opacity
	 */
	public double getOpacity() {
		return opacity;
	}

	/**
	 * Sets the opacity.
	 *
	 * @param opacity the new opacity
	 */
	public void setOpacity(double opacity) {
		this.opacity = opacity;
	}


//	@Override
	/* (non-Javadoc)
 * @see java.lang.Object#clone()
 */
public LayerItem clone() {
		LayerItem ele = new LayerItem();

		ele.setBaseLayer(this.isBaseLayer);
		ele.setClickData(this.clickData);
		ele.setBuffer(this.buffer);
		ele.setDefaultStyle(this.defaultStyle);
		ele.setHasLegend(this.hasLegend);
		ele.setLayer(this.layer);
		ele.setMaxExtent(maxExtent);
		ele.setName(this.name);
		ele.setOnMap(this.isOnMap);
		ele.setOpacity(this.opacity);
		ele.setStyle(this.style);
		ele.setStyles(this.styles);
		ele.setTrasparent(this.isTrasparent);
		ele.setUrl(this.url);
		ele.setVisible(this.isVisible);
		ele.setWrapDateLine(this.wrapDateLine);
		ele.setProperties(this.getProperties());
		ele.setWmsNotStandardParams(this.wmsNotStandardParams);
//		ele.setUUID(this.UUID);
		return ele;
	}

	/**
	 * Checks if is nc wms.
	 *
	 * @return the isNcWms
	 */
	public boolean isNcWms() {
		return isNcWms;
	}

	/**
	 * Sets the nc wms.
	 *
	 * @param isNcWms the isNcWms to set
	 */
	public void setNcWms(boolean isNcWms) {
		this.isNcWms = isNcWms;
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	public List<Property> getProperties() {
		return properties;
	}

	/**
	 * Sets the properties.
	 *
	 * @param properties the new properties
	 */
	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	// added by ceras
	// search a float property in the layer's feature type
	/**
	 * Contains property.
	 *
	 * @param propertyName the property name
	 * @return true, if successful
	 */
	public boolean containsProperty(String propertyName) {
		boolean contains = false;
		for (Property p : properties) {
			if (p.getName().equals(propertyName) && (p.getType().equals(FLOAT_TYPE) || p.getType().equals(INT_TYPE))) {
				contains=true;
			}
		}
		return contains;
	}

	// added by ceras
	// search a property in the layer's feature type, giving property name and property type
	/**
	 * Contains property.
	 *
	 * @param propertyName the property name
	 * @param propertyType the property type
	 * @return true, if successful
	 */
	public boolean containsProperty(String propertyName, String propertyType) {
		if (properties==null) {
			return false;
		}

		boolean contains = false;
		for (Property p : properties) {
			if (p.getName().equals(propertyName) && p.getType().equals(propertyType)) {
				contains=true;
			}
		}
		return contains;
	}

	// added by ceras
	// search a property in the layer's feature type, giving a property object
	/**
	 * Contains property.
	 *
	 * @param property the property
	 * @return true, if successful
	 */
	public boolean containsProperty(Property property) {
		if (properties==null) {
			return false;
		}

		boolean contains = false;
		for (Property p : properties) {
			if (p.getName().equals(property.getName()) && p.getType().equals(property.getType())) {
				contains=true;
			}
		}
		return contains;
	}

	// added by ceras
	// search for float Property objects belongs to the layer
	/**
	 * Gets the all float properties.
	 *
	 * @return the all float properties
	 */
	public List<Property> getAllFloatProperties() {
		if (properties==null) {
			return null;
		}

		List<Property> floatProperties = new ArrayList<Property>();
		for (Property p : properties) {
			if (p.getType().equals(FLOAT_TYPE)) {
				floatProperties.add(p);
			}
		}
		return floatProperties;
	}

	// added by ceras
	// search for float Property names belongs to the layer
	/**
	 * Gets the all float property names.
	 *
	 * @return the all float property names
	 */
	public List<String> getAllFloatPropertyNames() {
		if (properties==null) {
			return null;
		}

		List<String> floatPropertyNames = new ArrayList<String>();
		for (Property p : properties) {
			if (p.getType().equals(FLOAT_TYPE)) {
				floatPropertyNames.add(p.getName());
			}
		}

		return floatPropertyNames;
	}


	// added by ceras
	// get the first float Property names belongs to the layer
	/**
	 * Gets the first float property name.
	 *
	 * @return the first float property name
	 */
	public String getFirstFloatPropertyName() {
		if (properties==null) {
			return null;
		}

		for (Property p : properties) {
			if (p.getType().equals(FLOAT_TYPE)) {
				return p.getName();
			}
		}

		return null;
	}

	/**
	 * Sets the z axis selected.
	 *
	 * @param value the new z axis selected
	 */
	public void setZAxisSelected(Double value) {
		this.zAxisSelected = value;
	}

	/**
	 * Gets the z axis selected.
	 *
	 * @return the zAxisSelected
	 */
	public Double getZAxisSelected() {

		return zAxisSelected;
	}

	/**
	 * Sets the list z axis.
	 *
	 * @param zAxis the new list z axis
	 */
	public void setZAxis(ZAxis zAxis) {
		this.zAxis = zAxis;
	}

	/**
	 * Gets the list z axis.
	 *
	 * @return the zAxis
	 */
	public ZAxis getZAxis() {

		return zAxis;
	}

	// added by ceras
	/**
	 * Sets the data store.
	 *
	 * @param dataStore the new data store
	 */
	public void setDataStore(String dataStore) {
		this.dataStore  = dataStore;
	}

	// added by ceras
	/**
	 * Gets the data store.
	 *
	 * @return the data store
	 */
	public String getDataStore() {
		return dataStore;
	}

	// added by ceras
	/**
	 * Sets the border layer.
	 *
	 * @param isBorderLayer the new border layer
	 */
	public void setBorderLayer(boolean isBorderLayer) {
		this.isBorderLayer = isBorderLayer;
	}

	// added by ceras
	/**
	 * Checks if is border layer.
	 *
	 * @return true, if is border layer
	 */
	public boolean isBorderLayer() {
		return isBorderLayer;
	}

	// added by ceras
	/**
	 * Sets the geoserver url.
	 *
	 * @param geoserverUrl the new geoserver url
	 */
	public void setGeoserverUrl(String geoserverUrl) {
		this.geoserverUrl = geoserverUrl;
	}

	// added by ceras
	/**
	 * Gets the geoserver url.
	 *
	 * @return the geoserver url
	 */
	public String getGeoserverUrl() {
		return geoserverUrl;
	}

	// added by ceras
	/**
	 * Sets the geoserver wms url.
	 *
	 * @param geoserverWmsUrl the new geoserver wms url
	 */
	public void setGeoserverWmsUrl(String geoserverWmsUrl) {
		this.geoserverWmsUrl = geoserverWmsUrl;
	}

	// added by ceras
	/**
	 * Gets the geoserver wms url.
	 *
	 * @return the geoserver wms url
	 */
	public String getGeoserverWmsUrl() {
		return geoserverWmsUrl;
	}

	/**
	 * Sets the title.
	 *
	 * @param title the new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Sets the order.
	 *
	 * @param order the new order
	 */
	public void setOrder(long order) {
		this.order = order;
	}

	/**
	 * Gets the order.
	 *
	 * @return the order
	 */
	public long getOrder() {
		return order;
	}

	/**
	 * Gets the cql filter.
	 *
	 * @return the cql filter
	 */
	public String getCqlFilter() {
		return cqlFilter;
	}

	/**
	 * Sets the cql filter.
	 *
	 * @param cqlFilter the new cql filter
	 */
	public void setCqlFilter(String cqlFilter) {
		this.cqlFilter = cqlFilter;
	}

	/**
	 * Sets the uuid.
	 *
	 * @param uUID the new uuid
	 */
	public void setUUID(String uUID) {
		this.UUID = uUID;
	}


	/**
	 * Gets the uuid.
	 *
	 * @return the uUID
	 */
	public String getUUID() {
		return UUID;
	}

	/**
	 * Checks if is external.
	 *
	 * @return true, if is external
	 */
	public boolean isExternal() {
		return isExternal;
	}

	/**
	 * Sets the external.
	 *
	 * @param isExternal the isExternal to set
	 */
	public void setExternal(boolean isExternal) {
		this.isExternal = isExternal;
	}

	//ADDED BY Francesco M.
	/**
	 * Checks if is cql filter available.
	 *
	 * @return true, if is cql filter available
	 */
	public boolean isCqlFilterAvailable() {
		return cqlFilterAvailable;
	}

	/**
	 * Sets the cql filter available.
	 *
	 * @param cqlFilterAvailable the new cql filter available
	 */
	public void setCqlFilterAvailable(boolean cqlFilterAvailable) {
		this.cqlFilterAvailable = cqlFilterAvailable;
	}

	/**
	 * Gets the server wms request.
	 *
	 * @return the server wms request
	 */
	public String getServerWmsRequest() {
		return serverWmsRequest;
	}

	/**
	 * Sets the server wms request.
	 *
	 * @param serverWmsRequest the new server wms request
	 */
	public void setServerWmsRequest(String serverWmsRequest) {
		this.serverWmsRequest = serverWmsRequest;
	}

	/**
	 * Gets the wms not standard params.
	 *
	 * @return the wmsNotStandardParams
	 */
	public Map<String, String> getWmsNotStandardParams() {
		return wmsNotStandardParams;
	}

	/**
	 * Sets the wms not standard params.
	 *
	 * @param wmsNotStandardParams the wmsNotStandardParams to set
	 */
	public void setWmsNotStandardParams(Map<String, String> wmsNotStandardParams) {
		this.wmsNotStandardParams = wmsNotStandardParams;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("LayerItem [name=");
		builder.append(name);
		builder.append(", url=");
		builder.append(url);
		builder.append(", layer=");
		builder.append(layer);
		builder.append(", style=");
		builder.append(style);
		builder.append(", isTrasparent=");
		builder.append(isTrasparent);
		builder.append(", isBaseLayer=");
		builder.append(isBaseLayer);
		builder.append(", isBorderLayer=");
		builder.append(isBorderLayer);
		builder.append(", wrapDateLine=");
		builder.append(wrapDateLine);
		builder.append(", buffer=");
		builder.append(buffer);
		builder.append(", hasLegend=");
		builder.append(hasLegend);
		builder.append(", isVisible=");
		builder.append(isVisible);
		builder.append(", isOnMap=");
		builder.append(isOnMap);
		builder.append(", clickData=");
		builder.append(clickData);
		builder.append(", maxExtent=");
		builder.append(maxExtent);
		builder.append(", defaultStyle=");
		builder.append(defaultStyle);
		builder.append(", opacity=");
		builder.append(opacity);
		builder.append(", styles=");
		builder.append(styles);
		builder.append(", title=");
		builder.append(title);
		builder.append(", properties=");
		builder.append(properties);
		builder.append(", dataStore=");
		builder.append(dataStore);
		builder.append(", geoserverUrl=");
		builder.append(geoserverUrl);
		builder.append(", geoserverWmsUrl=");
		builder.append(geoserverWmsUrl);
		builder.append(", cqlFilter=");
		builder.append(cqlFilter);
		builder.append(", id=");
		builder.append(id);
		builder.append(", order=");
		builder.append(order);
		builder.append(", isExternal=");
		builder.append(isExternal);
		builder.append(", wmsNotStandardParams=");
		builder.append(wmsNotStandardParams);
		builder.append(", isNcWms=");
		builder.append(isNcWms);
		builder.append(", serverWmsRequest=");
		builder.append(serverWmsRequest);
		builder.append(", cqlFilterAvailable=");
		builder.append(cqlFilterAvailable);
		builder.append(", UUID=");
		builder.append(UUID);
		builder.append(", zAxis=");
		builder.append(zAxis);
		builder.append("]");
		return builder.toString();
	}
}
