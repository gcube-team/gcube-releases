package org.gcube.portlets.widgets.ckandatapublisherwidget.shared;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.view.client.ProvidesKey;


/**
 * A resource element bean
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ResourceElementBean implements Comparable<ResourceElementBean>, Serializable{

	private static final long serialVersionUID = -1230871392599580669L;
	private int identifierGWT;
	private String name;
	private String editableName;
	private boolean toBeAdded;
	private boolean isFolder;
	private String fullPath;
	private String originalIdInWorkspace;
	private String mimeType;
	private String url;
	private String description;
	private String organizationNameDatasetParent; // the organization name in which the parent dataset was created
	private ResourceElementBean parent;
	private List<ResourceElementBean> children;

	// to generate the identifiers
	private static int nextId = 0;

	/**
	 * The key provider that provides the unique ID of a bean.
	 */
	public static final ProvidesKey<ResourceElementBean> KEY_PROVIDER = new ProvidesKey<ResourceElementBean>() {
		@Override
		public Object getKey(ResourceElementBean item) {
			return item == null ? null : item.identifierGWT;
		}
	};
	
	/**
	 * Copy constructor
	 * @param another
	 */
	public ResourceElementBean(ResourceElementBean another) {
		this.name = another.name;
		this.toBeAdded = another.toBeAdded;
		this.fullPath = another.fullPath;
		this.editableName = another.editableName;
		this.originalIdInWorkspace = another.originalIdInWorkspace;
		this.mimeType = another.mimeType;
		this.url = another.url;
		this.description = another.description;
		this.organizationNameDatasetParent = another.organizationNameDatasetParent;
	  }

	/**
	 * Default constructor
	 */
	public ResourceElementBean(){
		super();
		this.identifierGWT = nextId;
		nextId++;
	}

	/**
	 * @param identifier
	 * @param parentFolder
	 * @param name
	 * @param movedToRight
	 * @param isFolder
	 */
	public ResourceElementBean(
			ResourceElementBean parent,
			String name, 
			boolean isFolder, 
			List<ResourceElementBean> children,
			String fullPath) {
		this.identifierGWT = nextId;
		nextId++;
		this.parent = parent;
		this.name = name;
		this.isFolder = isFolder;
		this.children = children;
		this.fullPath = fullPath;
	}

	/**
	 * @param name
	 * @param toBeAdded
	 * @param isFolder
	 * @param parent
	 * @param children
	 * @param fullPath
	 * @param originalIdInWorkspace
	 * @param mimeType
	 * @param url
	 * @param description
	 * @param organizationNameDatasetParent
	 */
	public ResourceElementBean(String name, boolean toBeAdded,
			boolean isFolder, ResourceElementBean parent,
			List<ResourceElementBean> children, String fullPath,
			String originalIdInWorkspace, String mimeType, String url,
			String description, String organizationNameDatasetParent) {
		super();
		this.identifierGWT = nextId;
		nextId++;
		this.name = name;
		this.toBeAdded = toBeAdded;
		this.isFolder = isFolder;
		this.parent = parent;
		this.children = children;
		this.fullPath = fullPath;
		this.originalIdInWorkspace = originalIdInWorkspace;
		this.mimeType = mimeType;
		this.url = url;
		this.description = description;
		this.organizationNameDatasetParent = organizationNameDatasetParent;
	}

	public ResourceElementBean getParent() {
		return parent;
	}

	public void setParent(ResourceElementBean parent) {
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isToBeAdded() {
		return toBeAdded;
	}

	public void setToBeAdded(boolean toBeAdded) {
		this.toBeAdded = toBeAdded;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOrganizationNameDatasetParent() {
		return organizationNameDatasetParent;
	}

	public void setOrganizationNameDatasetParent(
			String organizationNameDatasetParent) {
		this.organizationNameDatasetParent = organizationNameDatasetParent;
	}

	public boolean isFolder() {
		return isFolder;
	}

	public void setFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}

	public List<ResourceElementBean> getChildren() {
		return children;
	}

	public void setChildren(List<ResourceElementBean> children) {
		this.children = children;
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	public String getOriginalIdInWorkspace() {
		return originalIdInWorkspace;
	}

	public void setOriginalIdInWorkspace(String originalIdInWorkspace) {
		this.originalIdInWorkspace = originalIdInWorkspace;
	}

	public String getEditableName() {
		return editableName;
	}

	public void setEditableName(String newName) {
		this.editableName = newName;
	}

	@Override
	public boolean equals(Object o) {
		boolean toReturn = false;
		if (o instanceof ResourceElementBean) {
			toReturn =  identifierGWT == ((ResourceElementBean) o).identifierGWT;
		}

		return toReturn;
	}

	@Override
	public int compareTo(ResourceElementBean o) {
		int toReturn = (o == null || o.fullPath == null) ? -1 : -o.fullPath.compareTo(fullPath);
		return toReturn;
	}

	@Override
	public String toString() {
		return "ResourceElementBean [identifierGWT=" + identifierGWT
				+ ", name=" + name + ", editableName=" + editableName
				+ ", toBeAdded=" + toBeAdded + ", isFolder=" + isFolder
				+ ", fullPath=" + fullPath + ", originalIdInWorkspace="
				+ originalIdInWorkspace + ", mimeType=" + mimeType + ", url="
				+ url + ", description=" + description
				+ ", organizationNameDatasetParent="
				+ organizationNameDatasetParent + ", parent=" + parent
				+ ", children number=" + (children == null ? 0 : children.size()) + "]";
	}
}
