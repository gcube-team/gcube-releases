package org.gcube.common.gis.datamodel.enhanced;

import java.util.ArrayList;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.StringArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.gis.LayerInfoType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.gis.LayerType;

public class LayerInfo {


	public LayerInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	private String name;
	private String title;
	private String _abstract;
	private String url;
	private String serverProtocol;
	private String serverPassword;
	private String serverLogin;
	private String serverType;
	private String srs;
	private LayerType type;
	private boolean trasparent=false;
	private boolean baseLayer=false;
	private int buffer = 0;
	private boolean hasLegend=false;
	private boolean visible=false;
	private boolean selected=false;
	private boolean queryable=false;
	private BoundsInfo maxExtent;
	private BoundsInfo minExtent;
	private String defaultStyle;
	private double opacity=0.0;
	private ArrayList<String> styles;
	private TransectInfo transect;
	
	public LayerInfo(LayerInfoType toLoad){
		this.setName(toLoad.name());
		this.setTitle(toLoad.title());
		this.set_abstract(toLoad.abstractField());
		this.setUrl(toLoad.url());
		this.setServerLogin(toLoad.serverLogin());
		this.setServerPassword(toLoad.serverPassword());
		this.setServerProtocol(toLoad.serverProtocol());
		this.setServerType(toLoad.serverType());
		this.setSrs(toLoad.srs());
		if(toLoad.type() != null ) this.setType(toLoad.type());
		this.setTrasparent(toLoad.transparent());
		this.setBaseLayer(toLoad.baseLayer());
		this.setBuffer(toLoad.buffer());
		this.setHasLegend(toLoad.hasLegend());
		this.setVisible(toLoad.visible());
		this.setSelected(toLoad.selected());
		this.setQueryable(toLoad.queryable());
		if(toLoad.maxExtent() != null) this.setMaxExtent(new BoundsInfo(toLoad.maxExtent()));
		if(toLoad.minExtent() != null) this.setMinExtent(new BoundsInfo(toLoad.minExtent()));
		this.setDefaultStyle(toLoad.defaultStyle());
		this.setOpacity(toLoad.opacity());
		if (toLoad.styles() != null) this.setStyles(new ArrayList<String>(toLoad.styles().items()));
		if (toLoad.transect() != null) this.setTransect(new TransectInfo(toLoad.transect()));
	}
	
	

	
	public LayerInfoType toStubsVersion(){
		LayerInfoType res = new LayerInfoType();
		res.name(this.getName());
		res.title(this.getTitle());
		res.abstractField(this.get_abstract());
		res.url(this.getUrl());
		res.serverLogin(this.getServerLogin());
		res.serverPassword(this.getServerPassword());
		res.serverProtocol(this.getServerProtocol());
		res.serverType(this.getServerType());
		res.srs(this.getSrs());
		if (this.getType() != null) res.type(this.getType());
		res.transparent(this.isTrasparent());
		res.baseLayer(this.isBaseLayer());
		res.buffer(this.getBuffer());
		res.hasLegend(this.isHasLegend());
		res.visible(this.isVisible());
		res.selected(this.isSelected());
		res.queryable(this.isQueryable());
		if (this.getMaxExtent() != null) res.maxExtent(this.getMaxExtent().toStubsVersion());
		if (this.getMinExtent() != null) res.minExtent(this.getMinExtent().toStubsVersion());
		res.defaultStyle(this.getDefaultStyle());
		res.opacity(this.getOpacity());
		if (this.getStyles() != null) res.styles(new StringArray(this.getStyles()));
		if (this.getTransect() != null) res.transect(this.getTransect().toStubsVersion());
			
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getServerProtocol() {
		return serverProtocol;
	}

	public void setServerProtocol(String serverProtocol) {
		this.serverProtocol = serverProtocol;
	}

	public String getServerPassword() {
		return serverPassword;
	}

	public void setServerPassword(String serverPassword) {
		this.serverPassword = serverPassword;
	}

	public String getServerLogin() {
		return serverLogin;
	}

	public void setServerLogin(String serverLogin) {
		this.serverLogin = serverLogin;
	}

	public String getServerType() {
		return serverType;
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
	}

	public String getSrs() {
		return srs;
	}

	public void setSrs(String srs) {
		this.srs = srs;
	}

	public LayerType getType() {
		return type;
	}

	public void setType(LayerType type) {
		this.type = type;
	}

	public boolean isTrasparent() {
		return trasparent;
	}

	public void setTrasparent(boolean trasparent) {
		this.trasparent = trasparent;
	}

	public boolean isBaseLayer() {
		return baseLayer;
	}

	public void setBaseLayer(boolean baseLayer) {
		this.baseLayer = baseLayer;
	}

	public int getBuffer() {
		return buffer;
	}

	public void setBuffer(int buffer) {
		this.buffer = buffer;
	}

	public boolean isHasLegend() {
		return hasLegend;
	}

	public void setHasLegend(boolean hasLegend) {
		this.hasLegend = hasLegend;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isQueryable() {
		return queryable;
	}

	public void setQueryable(boolean queryable) {
		this.queryable = queryable;
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

	public String getDefaultStyle() {
		return defaultStyle;
	}

	public void setDefaultStyle(String defaultStyle) {
		this.defaultStyle = defaultStyle;
	}

	public double getOpacity() {
		return opacity;
	}

	public void setOpacity(double opacity) {
		this.opacity = opacity;
	}

	public ArrayList<String> getStyles() {
		return styles;
	}

	public void setStyles(ArrayList<String> styles) {
		this.styles = styles;
	}

	public TransectInfo getTransect() {
		return transect;
	}

	public void setTransect(TransectInfo transect) {
		this.transect = transect;
	}
}
