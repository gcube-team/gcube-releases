package org.gcube.portlets.admin.software_upload_wizard.shared.filetypes;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class FileType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7794084883802665647L;

	public abstract String getName();

	public abstract String getDescription();

	public abstract boolean allowsMulti();

	public abstract ArrayList<String> getAllowedExtensions();

	public abstract String getContentType();

	public abstract boolean isMandatory();

	public boolean isFilenameExtensionValid(String filename) {
		if (getAllowedExtensions().size() == 0)
			return true;
		if (filename.endsWith("."))
			return false;
		for (String extension : getAllowedExtensions()) {
//			int pointIndex = filename.lastIndexOf(".");
//			if (pointIndex == -1 && extension.equals(""))
//				return true;
//			if (filename.substring(pointIndex + 1).equals(extension))
//				return true;
			if (filename.endsWith("." + extension)) return true;
		}
		return false;
	}

	public boolean isValidContentType(String contentType) {
		if (this.getContentType().equals(contentType))
			return true;
		else
			return false;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String NEW_LINE = "\n";

		result.append(this.getClass().getName() + " Object {" + NEW_LINE);
		result.append(" Name: " + getName() + NEW_LINE);
		result.append(" Description: " + getDescription() + NEW_LINE);
		result.append(" allowsMulti: " + allowsMulti() + NEW_LINE);
		result.append(" isMandatory: " + isMandatory() + NEW_LINE);
		result.append("}" + NEW_LINE);

		return result.toString();
	}

}
