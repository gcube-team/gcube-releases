package org.gcube.portlets.widgets.ckandatapublisherwidget.shared;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * This bean will contain during ckan metadata creation the following information
 * (related to the workspace folder that represents a dataset)
 * <ul>
 * <li> id -> the id that will be assigned by ckan
 * <li> Title -> folder's name
 * <li> Description -> folders' description
 * <li> tags -> folder's custom fields keys' names
 * <li> visibility -> as chosen by the creator (visible = true, not visible = false)
 * <li> source -> url of the folder within the workspace
 * <li> version -> during creation it is going to be 1.0
 * <li> author, maintainer -> folder's owner
 * <li> custom fields -> gcube items <key, value> couple
 * <li> organizationsList -> list of organizations to which the user belong (and in which 
 * he wants to publish)
 * <li> list of metadata, that is custom fields per vre
 * <li> the name of the chosen profile used
 * </ul>
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@SuppressWarnings("serial")
public class DatasetMetadataBean implements Serializable {

	private String id;
	private String title;
	private String description;
	private String license; // chosen by the user
	private String source; // url of the folder in the workspace
	private String authorName; // author name
	private String authorSurname; // author surname
	private String authorFullName;
	private String authorEmail; // folder's email owner
	private String maintainer;
	private String maintainerEmail;
	private String ownerIdentifier; // owner of the folder into the workspace (e.g., andrea.rossi)
	private String chosenProfile; // the name of the MetaDataProfile chosen
	private String selectedOrganization;
	private long version; // version 1, 2 ...
	private boolean visibility; // Private (false) or Public(true)
	private List<OrganizationBean> organizationList; // list of organization in which the user is present and could create the dataset
	private List<ResourceElementBean> resources; // in case of workspace, this is the list of children of the folder
	private List<MetaDataProfileBean> metadataList;
	private List<String> tags; // on retrieve, they are the keys of the custom fields
	private Map<String, List<String>> customFields;
	private List<GroupBean> groups;

	public DatasetMetadataBean(){
		super();
	}

	/** Create a metadata bean object.
	 * @param id
	 * @param title
	 * @param description
	 * @param customFields
	 * @param tags
	 * @param license
	 * @param visibility
	 * @param source
	 * @param version
	 * @param author
	 * @param authorEmail
	 * @param maintainer
	 * @param maintainerEmail
	 * @param ownerIdentifier
	 * @param organizationList
	 * @param selectedOrganization
	 * @param resourcesIds
	 * @param addResources
	 * @param metadataList
	 */
	public DatasetMetadataBean(String id, String title, String description,
			Map<String, List<String>> customFields, List<String> tags,
			String license, boolean visibility, String source, long version,
			String authorName, String authorSurname, String authorEmail, String maintainer,
			String maintainerEmail, String ownerIdentifier,
			List<OrganizationBean> organizationList, String selectedOrganization,
			List<ResourceElementBean> resources,
			List<MetaDataProfileBean> metadataList, List<GroupBean> groups) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.customFields = customFields;
		this.tags = tags;
		this.license = license;
		this.visibility = visibility;
		this.source = source;
		this.version = version;
		this.authorName = authorName;
		this.authorSurname = authorSurname;
		this.authorEmail = authorEmail;
		this.maintainer = maintainer;
		this.maintainerEmail = maintainerEmail;
		this.ownerIdentifier = ownerIdentifier;
		this.organizationList = organizationList;
		this.selectedOrganization = selectedOrganization;
		this.resources = resources;
		this.metadataList = metadataList;
		this.groups = groups;
	}

	public List<MetaDataProfileBean> getMetadataList() {
		return metadataList;
	}

	public void setMetadataList(List<MetaDataProfileBean> metadataList) {
		this.metadataList = metadataList;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOwnerIdentifier() {
		return ownerIdentifier;
	}

	public void setOwnerIdentifier(String ownerIdentifier) {
		this.ownerIdentifier = ownerIdentifier;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, List<String>> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(Map<String, List<String>> customFields) {
		this.customFields = customFields;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public boolean getVisibility() {
		return visibility;
	}

	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getAuthorSurname() {
		return authorSurname;
	}

	public void setAuthorSurname(String authorSurname) {
		this.authorSurname = authorSurname;
	}

	public String getAuthorEmail() {
		return authorEmail;
	}

	public void setAuthorEmail(String authorEmail) {
		this.authorEmail = authorEmail;
	}

	public String getMaintainer() {
		return maintainer;
	}

	public void setMaintainer(String maintainer) {
		this.maintainer = maintainer;
	}

	public String getMaintainerEmail() {
		return maintainerEmail;
	}

	public void setMaintainerEmail(String maintainerEmail) {
		this.maintainerEmail = maintainerEmail;
	}

	public List<OrganizationBean> getOrganizationList() {
		return organizationList;
	}

	public void setOrganizationList(List<OrganizationBean> organizationList) {
		this.organizationList = organizationList;
	}

	public String getSelectedOrganization() {
		return selectedOrganization;
	}

	public void setSelectedOrganization(String selectedOrganization) {
		this.selectedOrganization = selectedOrganization;
	}

	public List<ResourceElementBean> getResources() {
		return resources;
	}

	public void setResources(List<ResourceElementBean> resources) {
		this.resources = resources;
	}

	public String getAuthorFullName() {
		return authorFullName;
	}

	public void setAuthorFullName(String authorFullName) {
		this.authorFullName = authorFullName;
	}

	public String getChosenProfile() {
		return chosenProfile;
	}

	public void setChosenProfile(String chosenProfile) {
		this.chosenProfile = chosenProfile;
	}

	public List<GroupBean> getGroups() {
		return groups;
	}

	public void setGroups(List<GroupBean> groups) {
		this.groups = groups;
	}

	@Override
	public String toString() {
		return "DatasetMetadataBean [id=" + id + ", title=" + title
				+ ", description=" + description + ", license=" + license
				+ ", source=" + source + ", authorName=" + authorName
				+ ", authorSurname=" + authorSurname + ", authorFullName="
				+ authorFullName + ", authorEmail=" + authorEmail
				+ ", maintainer=" + maintainer + ", maintainerEmail="
				+ maintainerEmail + ", ownerIdentifier=" + ownerIdentifier
				+ ", chosenProfile=" + chosenProfile
				+ ", selectedOrganization=" + selectedOrganization
				+ ", version=" + version + ", visibility=" + visibility
				+ ", organizationList=" + organizationList + ", resources="
				+ resources + ", metadataList=" + metadataList + ", tags="
				+ tags + ", customFields=" + customFields + ", groups="
				+ groups + "]";
	}
}
