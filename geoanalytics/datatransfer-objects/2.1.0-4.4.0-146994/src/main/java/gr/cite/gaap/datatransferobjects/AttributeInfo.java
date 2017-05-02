package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttributeInfo {
	
	private static Logger logger = LoggerFactory.getLogger(AttributeInfo.class);
	private String name = null;
	private String type = null;
	private String taxonomy = null;
	private String term = null;
	private String value = null;
	private boolean store = false;
	private boolean presentable = false;
	private String document = null;
	private boolean mapValue = false;

	private boolean autoValueMapping = false;
	private boolean autoDocumentMapping = false;
	private String termParentTaxonomy = null;
	private String linkVerb = null;

	public AttributeInfo() {
		logger.trace("Initialized default contructor for AttributeInfo");
	}

	public AttributeInfo(String name, String type, String taxonomy, String term, boolean store, boolean presentable) {
		logger.trace("Initializing AttributeInfo...");
		this.name = name;
		this.type = type;
		this.taxonomy = taxonomy;
		this.term = term;
		this.store = store;
		this.presentable = presentable;
		logger.trace("Initialized AttributeInfo");
	}

	public AttributeInfo(String name, String type, String taxonomy, String term, String value, boolean mapValue) {
		logger.trace("Initializing AttributeInfo...");
		this.name = name;
		this.type = type;
		this.taxonomy = taxonomy;
		this.term = term;
		this.value = value;
		this.mapValue = mapValue;
		logger.trace("Initialized AttributeInfo");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTaxonomy() {
		return taxonomy;
	}

	public void setTaxonomy(String taxonomy) {
		this.taxonomy = taxonomy;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isStore() {
		return store;
	}

	public void setStore(boolean store) {
		this.store = store;
	}

	public boolean isPresentable() {
		return presentable;
	}

	public void setPresentable(boolean presentable) {
		this.presentable = presentable;
	}

	public boolean isMapValue() {
		return mapValue;
	}

	public void setMapValue(boolean mapValue) {
		this.mapValue = mapValue;
	}

	public boolean isAutoValueMapping() {
		return autoValueMapping;
	}

	public void setAutoValueMapping(boolean autoDocumentMapping) {
		this.autoValueMapping = autoDocumentMapping;
	}

	public boolean isAutoDocumentMapping() {
		return autoDocumentMapping;
	}

	public void setAutoDocumentMapping(boolean autoDocumentMapping) {
		this.autoDocumentMapping = autoDocumentMapping;
	}

	public String getTermParentTaxonomy() {
		return termParentTaxonomy;
	}

	public void setTermParentTaxonomy(String termParentTaxonomy) {
		this.termParentTaxonomy = termParentTaxonomy;
	}

	public String getLinkVerb() {
		return linkVerb;
	}

	public void setLinkVerb(String linkVerb) {
		this.linkVerb = linkVerb;
	}
}
