package gr.cite.gaap.datatransferobjects;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.mapping.SystemMappingConfig;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;

public class TaxonomyTermMessenger {
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

	public TaxonomyTermMessenger() {
	}

	public TaxonomyTermMessenger(TaxonomyTerm t) throws Exception {
		this.id = t.getId().toString();
		this.name = t.getName();
		this.order = t.getOrder();
		if (t.getParent() != null)
			this.parentTaxonomy = t.getParent().getTaxonomy().getName();
		if (t.getParent() != null)
			this.parentTerm = t.getParent().getName();
		this.taxonomy = t.getTaxonomy().getName();
		if (t.getTaxonomyTermClass() != null)
			this.classTaxonomy = t.getTaxonomyTermClass().getTaxonomy().getName();
		if (t.getTaxonomyTermClass() != null)
			this.classTerm = t.getTaxonomyTermClass().getName();
		this.active = t.getIsActive();
	}

	public TaxonomyTermMessenger(TaxonomyTerm t, SystemMappingConfig config) throws Exception {
		this(t);
		if (config != null) {
			JAXBContext ctx = JAXBContext.newInstance(SystemMappingConfig.class);
			Marshaller um = ctx.createMarshaller();
			StringWriter sw = new StringWriter();
			um.marshal(config, sw);
			this.configuration = sw.toString();
		}
	}

	public TaxonomyTermMessenger(TaxonomyTerm t, SystemMappingConfig config, String mappedValue) throws Exception {
		this(t, config);
		this.mappedValue = mappedValue;
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
