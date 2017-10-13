package gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import gr.cite.geoanalytics.dataaccess.entities.layer.DataSource;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.SysConfigData;

@XmlRootElement
@XmlSeeAlso(LayerBounds.class)
public class LayerConfig implements SysConfigData {

	private String name = null;
	private String layerId = null;
	private String style = null;
	
	@Enumerated(EnumType.STRING)
	private DataSource dataSource = null;

	private Integer minScale = null;
	private Integer maxScale = null;

	private LayerBounds boundingBox;

	public LayerConfig() {}

	public LayerConfig(LayerConfig other) {
		this.name = other.name;
		this.layerId = other.layerId;
		this.minScale = other.minScale;
		this.maxScale = other.maxScale;
		this.style = other.style;
		this.boundingBox = new LayerBounds(other.boundingBox);
		this.dataSource = other.dataSource;
	}

	public String getStyle() {
		return style;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	@XmlElement
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@XmlElement
	public void setStyle(String style) {
		this.style = style;
	}

	public String getName() {
		return name;
	}

	@XmlElement
	public void setName(String name) {
		this.name = name;
	}

	public Integer getMinScale() {
		return minScale;
	}

	@XmlElement
	public void setMinScale(Integer minScale) {
		this.minScale = minScale;
	}

	public Integer getMaxScale() {
		return maxScale;
	}

	@XmlElement
	public void setMaxScale(Integer maxScale) {
		this.maxScale = maxScale;
	}

	public LayerBounds getBoundingBox() {
		return boundingBox;
	}

	@XmlElement(name = "bounds")
	public void setBoundingBox(LayerBounds boundingBox) {
		this.boundingBox = boundingBox;
	}

	public String getLayerId() {
		return layerId;
	}

	@XmlElement
	public void setLayerId(String layerId) {
		this.layerId = layerId;
	}

	@Override
	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (other == null || other.getClass() != this.getClass())
			return false;

		return layerId.equals(((LayerConfig) other).getLayerId());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(name);
		if (minScale != null)
			sb.append(" minScale: " + minScale);
		if (maxScale != null)
			sb.append(" maxScale: " + maxScale);
		if (boundingBox != null)
			sb.append(" bbox: (" + boundingBox.toString() + ")");
		if (layerId != null)
			sb.append(" termId: " + layerId);
		if (style != null)
			sb.append(" style: " + style);
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
}
