package gr.cite.geoanalytics.dataaccess.entities.taxonomy.definition;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "extraData")
public class TaxonomyData {
	
	private boolean geographic = false;
	private UUID parent = null;
	private List<UUID> alternatives = new ArrayList<>();

	@XmlAttribute(name = "geographic", required = false)
	public boolean isGeographic() {
		return geographic;
	}

	public void setGeographic(boolean geographic) {
		this.geographic = geographic;
	}

	@XmlAttribute(name = "parent", required = false)
	public UUID getParent() {
		return parent;
	}

	public void setParent(UUID parent) {
		this.parent = parent;
	}

	@XmlElementWrapper(name = "alternatives", required = false)
	@XmlElement(name = "alternative")
	public List<UUID> getAlternatives() {
		return alternatives;
	}

	public void setAlternatives(List<UUID> alternatives) {
		this.alternatives = alternatives;
	}
}
