package org.gcube.portlets.admin.software_upload_wizard.shared.filetypes;

import java.util.ArrayList;

public class GarFileType extends FileType {
	
	public final static String NAME = "GAR archive";
	public final static String DESCRIPTION = "GAR Archive";
	public final static ArrayList<String> EXTENSIONS = new ArrayList<String>();
	private final static boolean ALLOW_MULTI = false;
	private final static boolean MANDATORY = true;
	public final static String CONTENT_TYPE = ""; //TODO Check content type

	static {
		EXTENSIONS.add("gar");
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
