package org.gcube.data_catalogue.grsf_publish_ws.json.input.record;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.data_catalogue.grsf_publish_ws.utils.HelperMethods;
import org.gcube.datacatalogue.ckanutillibrary.shared.ResourceBean;
import org.gcube.datacatalogue.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The base class contains basic information needed to publish something in the data catalogue.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Base {

	private static final List<String> FIELDS_TO_IGNORE = Arrays.asList(
			Constants.AUTHOR, Constants.AUTHOR_CONTACT,  Constants.SYSTEM_TYPE_CUSTOM_KEY, Constants.GRSF_TYPE_JSON_KEY,
			Constants.FISHERY_URI, Constants.STOCK_URI);

	private static Logger logger = LoggerFactory.getLogger(Base.class);

	@JsonProperty(Constants.CATALOG_ID) //used on patch/update product call
	private String catalogId;

	@JsonProperty(Constants.DESCRIPTION)
	private String description;

	@JsonProperty(Constants.LICENSE_ID)
	private String license;

	// filled automatically by the service
	@JsonProperty(Constants.AUTHOR)
	private String author;

	// filled automatically by the service
	@JsonProperty(Constants.AUTHOR_CONTACT)
	private String authorContact;

	@JsonProperty(Constants.VERSION)
	private Long version;

	@JsonProperty(Constants.MAINTAINER)
	private String maintainer;

	@JsonProperty(Constants.MAINTAINER_CONTACT)
	private String maintainerContact;

	@JsonProperty(Constants.EXTRAS_FIELD)
	private Map<String, List<String>> extrasFields = new HashMap<>();

	@JsonProperty(Constants.EXTRAS_RESOURCES)
	private List<ResourceBean> extrasResources = new ArrayList<ResourceBean>();

	public Base() {
		super();
	}

	/**
	 * @param catalogId
	 * @param description
	 * @param license
	 * @param author
	 * @param authorContact
	 * @param version
	 * @param maintainer
	 * @param maintainerContact
	 * @param extrasFields
	 * @param extrasResources
	 */
	public Base(String catalogId, String description, String license,
			String author, String authorContact, Long version,
			String maintainer, String maintainerContact,
			Map<String, List<String>> extrasFields,
			List<ResourceBean> extrasResources) {
		super();
		this.catalogId = catalogId;
		this.description = description;
		this.license = license;
		this.author = author;
		this.authorContact = authorContact;
		this.version = version;
		this.maintainer = maintainer;
		this.maintainerContact = maintainerContact;
		this.extrasFields = extrasFields;
		this.extrasResources = extrasResources;
	}

	public String getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getAuthorContact() {
		return authorContact;
	}

	public void setAuthorContact(String authorContact) {
		this.authorContact = authorContact;
	}

	public String getMaintainer() {
		return maintainer;
	}

	public void setMaintainer(String maintainer) {
		this.maintainer = maintainer;
	}

	public String getMaintainerContact() {
		return maintainerContact;
	}

	public void setMaintainerContact(String maintainerContact) {
		this.maintainerContact = maintainerContact;
	}

	public Map<String, List<String>> getExtrasFields() {
		return extrasFields;
	}

	public void setExtrasFields(Map<String, List<String>> extrasFields) {
		this.extrasFields = extrasFields;
	}

	public List<ResourceBean> getExtrasResources() {
		return extrasResources;
	}

	public void setExtrasResources(List<ResourceBean> extrasResources) {
		this.extrasResources = extrasResources;
	}

	/**
	 * Use for generics object (unrecognized from Jackson) to be put into the map
	 * @param key
	 * @param value
	 */
	@JsonAnySetter
	private void genericSetter(String key, Object value){

		logger.info("Found extra property: [" + key + "," + value + "]");

		if(FIELDS_TO_IGNORE.contains(key)){
			logger.debug("Ignoring field with key " + key);
			return;
		}

		List<String> values = new ArrayList<String>();
		if(extrasFields.containsKey(key))
			values = extrasFields.get(key);
		else
			values = new ArrayList<String>();

		values.add(HelperMethods.removeHTML(value.toString()));
		extrasFields.put(key, values);
	}

	@Override
	public String toString() {
		return "Base [catalogId=" + catalogId + ", description=" + description
				+ ", license=" + license + ", author=" + author
				+ ", authorContact=" + authorContact + ", version=" + version
				+ ", maintainer=" + maintainer + ", maintainerContact="
				+ maintainerContact + ", extrasFields=" + extrasFields
				+ ", extrasResources=" + extrasResources + "]";
	}

}