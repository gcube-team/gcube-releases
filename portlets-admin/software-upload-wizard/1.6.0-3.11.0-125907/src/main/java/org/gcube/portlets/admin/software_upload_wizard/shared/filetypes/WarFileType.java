package org.gcube.portlets.admin.software_upload_wizard.shared.filetypes;

import java.util.ArrayList;

public class WarFileType extends FileType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3333738198662362745L;

	public final static String NAME = "War archive";
	public final static String DESCRIPTION = "Web Application Archive";
	public final static ArrayList<String> EXTENSIONS = new ArrayList<String>();
	public final static String CONTENT_TYPE = "application/octet-stream";

	private boolean allowsMulti = false;
	private boolean mandatory = true;

	static {
		EXTENSIONS.add("war");
	}
	
	public WarFileType() {
		this(false,true);
	}

	public WarFileType(boolean allowsMulti, boolean isMandatory) {
		this.allowsMulti = allowsMulti;
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
		return allowsMulti;
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
