package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.gis;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.gisTypesNS;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.StringArray;

@XmlRootElement(namespace=gisTypesNS)
public class LayerInfoType {
	
	@XmlElement(namespace=gisTypesNS)
	private String name;
	@XmlElement(namespace=gisTypesNS)
	private String title;
	@XmlElement(namespace=gisTypesNS,name="abstract")
	private String abstractField;
	@XmlElement(namespace=gisTypesNS)
	private String url;
	@XmlElement(namespace=gisTypesNS)
	private String serverProtocol;
	@XmlElement(namespace=gisTypesNS)
	private String serverPassword;
	@XmlElement(namespace=gisTypesNS)
	private String serverLogin;
	@XmlElement(namespace=gisTypesNS)
	private String serverType;
	@XmlElement(namespace=gisTypesNS)
	private String srs;
	@XmlElement(namespace=gisTypesNS)
	private LayerType type;
	@XmlElement(namespace=gisTypesNS)
	private boolean transparent;
	@XmlElement(namespace=gisTypesNS)
	private boolean baseLayer;
	@XmlElement(namespace=gisTypesNS)
	private int buffer;
	@XmlElement(namespace=gisTypesNS)
	private boolean hasLegend;
	@XmlElement(namespace=gisTypesNS)
	private boolean visible;
	@XmlElement(namespace=gisTypesNS)
	private boolean selected;
	@XmlElement(namespace=gisTypesNS)
	private boolean queryable;
	@XmlElement(namespace=gisTypesNS)
	private BoundsInfoType maxExtent;
	@XmlElement(namespace=gisTypesNS)
	private BoundsInfoType minExtent;
	@XmlElement(namespace=gisTypesNS)
	private String defaultStyle;
	@XmlElement(namespace=gisTypesNS)
	private double opacity;
	@XmlElement(namespace=gisTypesNS)
	private StringArray styles;
	@XmlElement(namespace=gisTypesNS)
	private TransectInfoType transect;
	
	public LayerInfoType() {
		// TODO Auto-generated constructor stub
	}

	

	public LayerInfoType(String name, String title, String abstractField,
			String url, String serverProtocol, String serverPassword,
			String serverLogin, String serverType, String srs, LayerType type,
			boolean transparent, boolean baseLayer, int buffer,
			boolean hasLegend, boolean visible, boolean selected,
			boolean queryable, BoundsInfoType maxExtent,
			BoundsInfoType minExtent, String defaultStyle, double opacity,
			StringArray styles, TransectInfoType transect) {
		super();
		this.name = name;
		this.title = title;
		this.abstractField = abstractField;
		this.url = url;
		this.serverProtocol = serverProtocol;
		this.serverPassword = serverPassword;
		this.serverLogin = serverLogin;
		this.serverType = serverType;
		this.srs = srs;
		this.type = type;
		this.transparent = transparent;
		this.baseLayer = baseLayer;
		this.buffer = buffer;
		this.hasLegend = hasLegend;
		this.visible = visible;
		this.selected = selected;
		this.queryable = queryable;
		this.maxExtent = maxExtent;
		this.minExtent = minExtent;
		this.defaultStyle = defaultStyle;
		this.opacity = opacity;
		this.styles = styles;
		this.transect = transect;
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
	 * @return the serverProtocol
	 */
	public String serverProtocol() {
		return serverProtocol;
	}

	/**
	 * @param serverProtocol the serverProtocol to set
	 */
	public void serverProtocol(String serverProtocol) {
		this.serverProtocol = serverProtocol;
	}

	/**
	 * @return the serverPassword
	 */
	public String serverPassword() {
		return serverPassword;
	}

	/**
	 * @param serverPassword the serverPassword to set
	 */
	public void serverPassword(String serverPassword) {
		this.serverPassword = serverPassword;
	}

	/**
	 * @return the serverLogin
	 */
	public String serverLogin() {
		return serverLogin;
	}

	/**
	 * @param serverLogin the serverLogin to set
	 */
	public void serverLogin(String serverLogin) {
		this.serverLogin = serverLogin;
	}

	/**
	 * @return the serverType
	 */
	public String serverType() {
		return serverType;
	}

	/**
	 * @param serverType the serverType to set
	 */
	public void serverType(String serverType) {
		this.serverType = serverType;
	}

	/**
	 * @return the srs
	 */
	public String srs() {
		return srs;
	}

	/**
	 * @param srs the srs to set
	 */
	public void srs(String srs) {
		this.srs = srs;
	}

	/**
	 * @return the type
	 */
	public LayerType type() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void type(LayerType type) {
		this.type = type;
	}

	/**
	 * @return the transparent
	 */
	public boolean transparent() {
		return transparent;
	}

	/**
	 * @param transparent the transparent to set
	 */
	public void transparent(boolean transparent) {
		this.transparent = transparent;
	}

	/**
	 * @return the baseLayer
	 */
	public boolean baseLayer() {
		return baseLayer;
	}

	/**
	 * @param baseLayer the baseLayer to set
	 */
	public void baseLayer(boolean baseLayer) {
		this.baseLayer = baseLayer;
	}

	/**
	 * @return the buffer
	 */
	public int buffer() {
		return buffer;
	}

	/**
	 * @param buffer the buffer to set
	 */
	public void buffer(int buffer) {
		this.buffer = buffer;
	}

	/**
	 * @return the hasLegend
	 */
	public boolean hasLegend() {
		return hasLegend;
	}

	/**
	 * @param hasLegend the hasLegend to set
	 */
	public void hasLegend(boolean hasLegend) {
		this.hasLegend = hasLegend;
	}

	/**
	 * @return the visible
	 */
	public boolean visible() {
		return visible;
	}

	/**
	 * @param visible the visible to set
	 */
	public void visible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * @return the selected
	 */
	public boolean selected() {
		return selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void selected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return the queryable
	 */
	public boolean queryable() {
		return queryable;
	}

	/**
	 * @param queryable the queryable to set
	 */
	public void queryable(boolean queryable) {
		this.queryable = queryable;
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
	 * @return the defaultStyle
	 */
	public String defaultStyle() {
		return defaultStyle;
	}

	/**
	 * @param defaultStyle the defaultStyle to set
	 */
	public void defaultStyle(String defaultStyle) {
		this.defaultStyle = defaultStyle;
	}

	/**
	 * @return the opacity
	 */
	public double opacity() {
		return opacity;
	}

	/**
	 * @param opacity the opacity to set
	 */
	public void opacity(double opacity) {
		this.opacity = opacity;
	}

	/**
	 * @return the styles
	 */
	public StringArray styles() {
		return styles;
	}

	/**
	 * @param styles the styles to set
	 */
	public void styles(StringArray styles) {
		this.styles = styles;
	}

	/**
	 * @return the transect
	 */
	public TransectInfoType transect() {
		return transect;
	}

	/**
	 * @param transect the transect to set
	 */
	public void transect(TransectInfoType transect) {
		this.transect = transect;
	}

	
	public String url(){
		return url;	
	}
	
	public void url(String toSetUrl){
		this.url=toSetUrl;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LayerInfoType [name=");
		builder.append(name);
		builder.append(", title=");
		builder.append(title);
		builder.append(", abstractField=");
		builder.append(abstractField);
		builder.append(", url=");
		builder.append(url);
		builder.append(", serverProtocol=");
		builder.append(serverProtocol);
		builder.append(", serverPassword=");
		builder.append(serverPassword);
		builder.append(", serverLogin=");
		builder.append(serverLogin);
		builder.append(", serverType=");
		builder.append(serverType);
		builder.append(", srs=");
		builder.append(srs);
		builder.append(", type=");
		builder.append(type);
		builder.append(", transparent=");
		builder.append(transparent);
		builder.append(", baseLayer=");
		builder.append(baseLayer);
		builder.append(", buffer=");
		builder.append(buffer);
		builder.append(", hasLegend=");
		builder.append(hasLegend);
		builder.append(", visible=");
		builder.append(visible);
		builder.append(", selected=");
		builder.append(selected);
		builder.append(", queryable=");
		builder.append(queryable);
		builder.append(", maxExtent=");
		builder.append(maxExtent);
		builder.append(", minExtent=");
		builder.append(minExtent);
		builder.append(", defaultStyle=");
		builder.append(defaultStyle);
		builder.append(", opacity=");
		builder.append(opacity);
		builder.append(", styles=");
		builder.append(styles);
		builder.append(", transect=");
		builder.append(transect);
		builder.append("]");
		return builder.toString();
	}
	
	
	
	
}
