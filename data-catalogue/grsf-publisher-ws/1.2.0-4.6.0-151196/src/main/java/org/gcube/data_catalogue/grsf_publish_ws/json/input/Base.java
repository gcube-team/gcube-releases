package org.gcube.data_catalogue.grsf_publish_ws.json.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.CustomField;
import org.gcube.data_catalogue.grsf_publish_ws.utils.HelperMethods;
import org.gcube.datacatalogue.ckanutillibrary.shared.ResourceBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The base class contains basic information needed to publish something in the data catalogue.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@JsonIgnoreProperties(value = {"author", "author_contact"})
public class Base {

	private static Logger logger = LoggerFactory.getLogger(Base.class);
	public static final String UUID_KB_KEY = "UUID Knowledge Base";

	@JsonProperty("catalog_id") //used on patch/update product call
	private String catalogId;

	@JsonProperty("description")
	private String description;

	@JsonProperty("license_id")
	private String license;

	// filled automatically by the service
	@JsonProperty("author")
	private String author;

	// filled automatically by the service
	@JsonProperty("author_contact")
	private String authorContact;

	@JsonProperty("version")
	private Long version;

	@JsonProperty("maintainer")
	private String maintainer;

	@JsonProperty("maintainer_contact")
	private String maintainerContact;

	@JsonProperty("extras_fields")
	private Map<String, List<String>> extrasFields = new HashMap<>();

	@JsonProperty("extras_resources")
	private List<ResourceBean> extrasResources = new ArrayList<ResourceBean>();

	@JsonProperty("uuid_knowledge_base")
	@CustomField(key=UUID_KB_KEY)
	@NotNull(message="uuid_knowledge_base cannot be null")
	@Size(min=1, message="uuid_knowledge_base cannot be empty")
	private String uuid;

	public Base() {
		super();
	}

	/**
	 * @param id
	 * @param description
	 * @param license
	 * @param author
	 * @param authorContact
	 * @param version
	 * @param maintainer
	 * @param maintainerContact
	 * @param extrasFields
	 * @param extrasResources
	 * @param uuid
	 */
	public Base(String id, String description, String license, String author,
			String authorContact, Long version, String maintainer,
			String maintainerContact, Map<String, List<String>> extrasFields,
			List<ResourceBean> extrasResources, String uuid) {
		super();
		this.catalogId = id;
		this.description = description;
		this.license = license;
		this.author = author;
		this.authorContact = authorContact;
		this.version = version;
		this.maintainer = maintainer;
		this.maintainerContact = maintainerContact;
		this.extrasFields = extrasFields;
		this.extrasResources = extrasResources;
		this.uuid = uuid;
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

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * Use for generics object (unrecognized from Jackson) to be put into the map
	 * @param key
	 * @param value
	 */
	@JsonAnySetter
	private void genericSetter(String key, Object value){

		logger.info("Found extra property: [" + key + "," + value + "]");
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
		return "Base [catalogId=" + catalogId + ", description=" + description + ", license="
				+ license + ", author=" + author + ", authorContact="
				+ authorContact + ", version=" + version + ", maintainer="
				+ maintainer + ", maintainerContact=" + maintainerContact
				+ ", extrasFields=" + extrasFields + ", extrasResources="
				+ extrasResources + ", uuid=" + uuid + "]";
	}

}