package org.gcube.portlets.admin.software_upload_wizard.shared.filetypes;

import java.util.ArrayList;

public class JarFileType extends FileType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6845891471519572140L;
	
	public final static String NAME = "Jar archive";
	public final static String DESCRIPTION = "Java Archive";
	public final static ArrayList<String> EXTENSIONS = new ArrayList<String>();
	public final static String CONTENT_TYPE = "application/x-java-archive";

	private boolean allowMulti;
	private boolean mandatory;

	static {
		EXTENSIONS.add("jar");
	}

	public JarFileType() {
		this(false, true);
	}

	public JarFileType(boolean allowMulti, boolean isMandatory) {
		this.allowMulti = allowMulti;
		this.mandatory = isMandatory;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	@Override
	public boolean allowsMulti() {
		return allowMulti;
	}

	@Override
	public ArrayList<String> getAllowedExtensions() {
		return EXTENSIONS;

	}

	@Override
	public String getContentType() {
		return CONTENT_TYPE;
	}

	@Override
	public boolean isMandatory() {
		return mandatory;
	}

}
