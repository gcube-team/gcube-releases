package org.gcube.portlets.admin.software_upload_wizard.shared.filetypes;

import java.util.ArrayList;

public class MiscFileType extends FileType {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1218187145175877917L;

	public final static String NAME = "Misc file";
	private static final String DESCRIPTION = "";
	public final static ArrayList<String> EXTENSIONS = new ArrayList<String>();
	public final static boolean ALLOW_MULTI = true;
	public final static String CONTENT_TYPE = ""; // TODO recover content type
	
	private boolean mandatory;
	
	public MiscFileType() {
		this(false);
	}

	public MiscFileType(boolean isMandatory) {
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
		return ALLOW_MULTI;
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
