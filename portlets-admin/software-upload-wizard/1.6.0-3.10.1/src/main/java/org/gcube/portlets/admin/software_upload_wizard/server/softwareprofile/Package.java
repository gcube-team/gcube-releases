package org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.gcube.portlets.admin.software_upload_wizard.server.data.FilesContainer;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.FileType;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.MavenCoordinates;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PackageData;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PackageData.PackageType;

public class Package {
	
	private UUID uuid = UUID.randomUUID();
	private PackageData packageData;
	private FilesContainer filesContainer = new FilesContainer();
	private ArrayList<FileType> allowedFileTypes;
	private Collection<MavenCoordinates> mavenDependencies = new ArrayList<MavenCoordinates>();
	private MavenCoordinates artifactCoordinates = null;

	public Package(PackageType packageType){
		this.packageData = new PackageData(packageType);
		this.allowedFileTypes = new ArrayList<FileType>();
	}
	
	public Package(PackageType packageType, ArrayList<FileType> allowedFileTypes) {
		this.packageData = new PackageData(packageType);
		this.allowedFileTypes = allowedFileTypes;
	}

	public UUID getUuid() {
		return uuid;
	}

	public PackageData getData() {
		return packageData;
	}
	
	public void setPackageData(PackageData data){
		this.packageData = data;
	}

	public FilesContainer getFilesContainer() {
		return filesContainer;
	}

	public ArrayList<FileType> getAllowedFileTypes() {
		return allowedFileTypes;
	}

	public Collection<MavenCoordinates> getMavenDependencies() {
		return mavenDependencies;
	}

	public MavenCoordinates getArtifactCoordinates() {
		return artifactCoordinates;
	}

	public void setArtifactCoordinates(MavenCoordinates artifactCoordinates) {
		this.artifactCoordinates = artifactCoordinates;
	}
}
