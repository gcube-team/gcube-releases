package org.gcube.portlets.user.workspace.client.model;

import java.util.Date;

import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 *         May 12, 2014
 *
 */
public class FileTrashedModel extends FileGridModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2186023125020221821L;

	private String orginalPath;
	private Date deleteDate;
	private InfoContactModel deletedBy;

	public static enum STOREINFO {
		ORIGINALPATH, DELETEDATE, DELETEUSER
	}

	public FileTrashedModel() {
	}

	public FileTrashedModel(String identifier, String name, String path, Date creationDate, FileModel parent, long size,
			boolean isDirectory, boolean isShared) {
		super(identifier, name, path, creationDate, parent, size, isDirectory, isShared);
	}

	public FileTrashedModel(String orginalPath, Date deleteDate, InfoContactModel deleteUser) {
		super();
		this.orginalPath = orginalPath;
		this.deleteDate = deleteDate;
		this.deletedBy = deleteUser;
	}

	public FileTrashedModel(String identifier, String name, Date creationDate, FileModel parent, long size,
			boolean isDirectory, boolean isShared) {
		super(identifier, name, creationDate, parent, size, isDirectory, isShared);
	}

	public String getOrginalPath() {
		return orginalPath;
	}

	public Date getDeleteDate() {
		return deleteDate;
	}

	public InfoContactModel getDeleteUser() {
		return deletedBy;
	}

	public void setOrginalPath(String orginalPath) {
		set(STOREINFO.ORIGINALPATH.toString(), orginalPath);
		this.orginalPath = orginalPath;
	}

	public void setDeleteDate(Date deleteDate) {
		set(STOREINFO.DELETEDATE.toString(), deleteDate);
		this.deleteDate = deleteDate;
	}

	public void setDeleteUser(InfoContactModel deleteUser) {
		this.deletedBy = deleteUser;
		set(STOREINFO.DELETEUSER.toString(), deleteUser.getName());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FileTrashedModel [orginalPath=");
		builder.append(orginalPath);
		builder.append(", deleteDate=");
		builder.append(deleteDate);
		builder.append(", deletedBy=");
		builder.append(deletedBy);
		builder.append(", getIdentifier()=");
		builder.append(getIdentifier());
		builder.append(", getName()=");
		builder.append(getName());
		builder.append(", isDirectory()=");
		builder.append(isDirectory());
		builder.append("]");
		return builder.toString();
	}
}