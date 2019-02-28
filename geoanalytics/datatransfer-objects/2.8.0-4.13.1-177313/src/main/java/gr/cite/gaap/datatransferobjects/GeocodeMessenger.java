package gr.cite.gaap.datatransferobjects;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.geoanalytics.dataaccess.entities.geocode.Geocode;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.mapping.SystemMappingConfig;

public class GeocodeMessenger {
	private static Logger logger = LoggerFactory.getLogger(GeocodeMessenger.class);

	private String id = null;
	private String name = null;
	private Integer order = null;
	private String parentTaxonomy = null;
	private String parentTerm = null;
	private String taxonomy = null;
	private String classTaxonomy = null;
	private String classTerm = null;
	private String configuration = null;
	private boolean active = false;
	private String mappedValue = null;

	private String originalName = null;
	private String originalTaxonomyName = null;

	public GeocodeMessenger() {
		logger.trace("Initialized default contructor for GeocodeMessenger");

	}

	public GeocodeMessenger(Geocode t) throws Exception {
		logger.trace("Initializing GeocodeMessenger...");

		this.id = t.getId().toString();
		this.name = t.getName();
		this.order = t.getOrder();
		if (t.getParent() != null)
			this.parentTaxonomy = t.getParent().getGeocodeSystem().getName();
		if (t.getParent() != null)
			this.parentTerm = t.getParent().getName();
		this.taxonomy = t.getGeocodeSystem().getName();
		if (t.getGeocodeClass() != null)
			this.classTaxonomy = t.getGeocodeClass().getGeocodeSystem().getName();
		if (t.getGeocodeClass() != null)
			this.classTerm = t.getGeocodeClass().getName();
		this.active = t.getIsActive();
		logger.trace("Initialized GeocodeMessenger");

	}

	public GeocodeMessenger(Geocode t, SystemMappingConfig config) throws Exception {
		this(t);
		logger.trace("Initializing GeocodeMessenger...");
		if (config != null) {
			JAXBContext ctx = JAXBContext.newInstance(SystemMappingConfig.class);
			Marshaller um = ctx.createMarshaller();
			StringWriter sw = new StringWriter();
			um.marshal(config, sw);
			this.configuration = sw.toString();
		}
		logger.trace("Initialized GeocodeMessenger");
	}

	public GeocodeMessenger(Geocode t, SystemMappingConfig config, String mappedValue) throws Exception {
		this(t, config);
		logger.trace("Initializing GeocodeMessenger...");
		this.mappedValue = mappedValue;
		logger.trace("Initialized GeocodeMessenger");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getParentTaxonomy() {
		return parentTaxonomy;
	}

	public void setParentTaxonomy(String parentTaxonomy) {
		this.parentTaxonomy = parentTaxonomy;
	}

	public String getParentTerm() {
		return parentTerm;
	}

	public void setParentTerm(String parentTerm) {
		this.parentTerm = parentTerm;
	}

	public String getTaxonomy() {
		return taxonomy;
	}

	public void setTaxonomy(String taxonomy) {
		this.taxonomy = taxonomy;
	}

	public String getClassTaxonomy() {
		return classTaxonomy;
	}

	public void setClassTaxonomy(String classTaxonomy) {
		this.classTaxonomy = classTaxonomy;
	}

	public String getClassTerm() {
		return classTerm;
	}

	public void setClassTerm(String classTerm) {
		this.classTerm = classTerm;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public String getOriginalTaxonomyName() {
		return originalTaxonomyName;
	}

	public void setOriginalTaxonomyName(String originalTaxonomyName) {
		this.originalTaxonomyName = originalTaxonomyName;
	}

	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public String getMappedValue() {
		return mappedValue;
	}

	public void setMappedVale(String mappedValue) {
		this.mappedValue = mappedValue;
	}
}
