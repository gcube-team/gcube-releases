package org.gcube.portlets.admin.software_upload_wizard.shared.filetypes;

import java.util.ArrayList;

public class RebootScriptFileType extends FileType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1218187145175877917L;

	public final static String NAME = "Reboot script";
	public final static String DESCRIPTION = "A shell script that restart the application";
	public final static ArrayList<String> EXTENSIONS = new ArrayList<String>();
	public final static String CONTENT_TYPE = "application/x-shellscript";
	
	private boolean allowMulti = true;
	private boolean mandatory;

	static {
		EXTENSIONS.add("sh");
	}

	public RebootScriptFileType() {
		this(false);
	}

	public RebootScriptFileType(boolean mandatory) {
		this.mandatory = mandatory;
	}
	
	public RebootScriptFileType(boolean mandatory, boolean allowMulti) {
		this.mandatory = mandatory;
		this.allowMulti=allowMulti;
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
