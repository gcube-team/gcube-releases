package org.gcube.portlets.admin.software_upload_wizard.shared.filetypes;

import java.util.ArrayList;

public class SoftwareTarballFileType extends FileType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1147790164853990518L;

	public final static String NAME = "Software Tarball";
	public final static String DESCRIPTION = "A tar.gz file containing several files";
	public final static ArrayList<String> EXTENSIONS = new ArrayList<String>();
	public final static boolean ALLOW_MULTI = false;
	public final static String CONTENT_TYPE = ""; // TODO recover content type
	public final static boolean IS_MANDATORY = true;
	
	static {
		EXTENSIONS.add("tar.gz");
	}

	public SoftwareTarballFileType() {
	
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
		return IS_MANDATORY;
	}

}
