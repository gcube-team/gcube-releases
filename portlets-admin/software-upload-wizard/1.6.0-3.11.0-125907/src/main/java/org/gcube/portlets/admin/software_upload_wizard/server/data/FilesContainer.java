package org.gcube.portlets.admin.software_upload_wizard.server.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gcube.portlets.admin.software_upload_wizard.shared.SoftwareFileDetail;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

public class FilesContainer {

	ArrayList<SoftwareFile> files = new ArrayList<SoftwareFile>();

	public ArrayList<SoftwareFile> getFiles() {
		return files;
	}

	public SoftwareFile createNewFile() {
		SoftwareFile archive = new SoftwareFile();
		getFiles().add(archive);
		return archive;
	}

	public boolean hasFileWithFileType(String fileTypeName) {
		for (SoftwareFile file : getFiles()) {
			if (file.getTypeName().equals(fileTypeName))
				return true;
		}
		return false;
	}

	public List<SoftwareFile> getFilesWithFileType(String fileTypeName){
		Collection<SoftwareFile> result = Collections2.filter(getFiles(), new isFileTypePredicate(fileTypeName));
		return new ArrayList<SoftwareFile>(result);
	}

	public void deleteFile(String filename) {
		Iterables.removeIf(getFiles(), new HasFilenamePredicate(filename));
	}

	public boolean containsFile(String filename) {
		return Iterables.any(getFiles(), new HasFilenamePredicate(filename));
	}

	public ArrayList<SoftwareFileDetail> getSoftwareFileDetails() {
		Collection<SoftwareFileDetail> collection = Collections2.transform(
				getFiles(), new Function<SoftwareFile, SoftwareFileDetail>() {

					@Override
					public SoftwareFileDetail apply(SoftwareFile file) {
						return new SoftwareFileDetail(file.getFilename(), file
								.getTypeName());
					}
				});
		return new ArrayList<SoftwareFileDetail>(collection);
	}

	private class HasFilenamePredicate implements Predicate<SoftwareFile> {

		private String filename;

		public HasFilenamePredicate(String filename) {
			this.filename = filename;
		}

		@Override
		public boolean apply(SoftwareFile file) {
			if (file.getFilename().equals(filename))
				return true;
			else
				return false;
		}
	}

	private class isFileTypePredicate implements Predicate<SoftwareFile> {

		private String typeName;

		public isFileTypePredicate(String typeName) {
			super();
			this.typeName = typeName;
		}

		@Override
		public boolean apply(SoftwareFile file) {
			if (file.getTypeName().equals(typeName))
				return true;
			return false;
		}

	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String NEW_LINE = "\n";
		result.append("Total files #: " + getFiles().size() + NEW_LINE);

		for (int i = 0; i < getFiles().size(); i++) {
			SoftwareFile file = getFiles().get(i);
			result.append("File #" + i + NEW_LINE);
			result.append("\tFilename:\t" + file.getFilename() + NEW_LINE);
			result.append("\tType:\t" + file.getTypeName() + NEW_LINE);
		}

		return result.toString();
	}

}
