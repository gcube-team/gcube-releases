package org.gcube.portlets.admin.software_upload_wizard.shared.filetypes;

import java.util.ArrayList;

public class UninstallScriptFileType extends FileType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1218187145175877917L;
	
	public final static String NAME = "Uninstall script";
	private static final String DESCRIPTION = "";
	public final static ArrayList<String> EXTENSIONS = new ArrayList<String>();
	public final static String CONTENT_TYPE = "application/x-shellscript";
	
	private boolean allowMulti = true;
	private boolean mandatory;
	
	static{
		EXTENSIONS.add("sh");
	}
	
	public UninstallScriptFileType(){
		this(false);
	}
	
	public UninstallScriptFileType(boolean isMandatory) {
		this.mandatory= isMandatory;
	}
	
	public UninstallScriptFileType(boolean isMandatory, boolean allowMulti) {
		this.mandatory= isMandatory;
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
