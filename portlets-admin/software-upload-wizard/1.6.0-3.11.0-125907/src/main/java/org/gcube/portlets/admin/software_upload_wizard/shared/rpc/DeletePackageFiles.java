package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import java.util.ArrayList;

import net.customware.gwt.dispatch.shared.Action;

public class DeletePackageFiles implements Action<DeletePackageFilesResult> {

	private String packageId;
	private ArrayList<String> filenames;
	
	@SuppressWarnings("unused")
	private DeletePackageFiles() {
		// Serialization only
	}
	
	public DeletePackageFiles(String packageId, ArrayList<String> filenames) {
		super();
		this.packageId = packageId;
		this.filenames = filenames;
	}
	
	public String getPackageId() {
		return packageId;
	}
	
	public ArrayList<String> getFilenames() {
		return filenames;
	}
}
