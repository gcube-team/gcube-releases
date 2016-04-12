package org.gcube.portlets.admin.software_upload_wizard.shared.filetypes;

import java.util.ArrayList;

public class PatchArchiveFileType extends FileType {

	public final static String NAME = "Patch archive";
	public final static String DESCRIPTION = "GCube Patch Archive";
	public final static ArrayList<String> EXTENSIONS = new ArrayList<String>();
	public final static String CONTENT_TYPE = ""; //TODO recover content type
	public final static boolean ALLOW_MULTI = false;
	public final static boolean MANDATORY = true;

	static {
		EXTENSIONS.add("tar.gz");
	}
	
	public PatchArchiveFileType() {
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
		return MANDATORY;
	}

}
