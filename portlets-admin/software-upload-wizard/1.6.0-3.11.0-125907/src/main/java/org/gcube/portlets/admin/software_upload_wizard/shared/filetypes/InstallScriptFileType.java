package org.gcube.portlets.admin.software_upload_wizard.shared.filetypes;

import java.util.ArrayList;

public class InstallScriptFileType extends FileType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1218187145175877917L;

	public final static String NAME = "Install script";
	public final static String DESCRIPTION = "Web Application Archive";
	public final static ArrayList<String> EXTENSIONS = new ArrayList<String>();
	public final static String CONTENT_TYPE = "application/x-shellscript";
	public boolean allowMulti = true;

	private boolean mandatory;

	static {
		EXTENSIONS.add("sh");
	}
	
	public InstallScriptFileType() {
		this(false);
	}

	public InstallScriptFileType(boolean isMandatory) {
		this.mandatory = isMandatory;
	}
	
	public InstallScriptFileType(boolean isMandatory, boolean allowMulti) {
		this.mandatory = isMandatory;
		this.allowMulti = allowMulti;
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
