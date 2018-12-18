package gr.cite.geoanalytics.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileSearch {

	private String fileNameToSearch;
	private List<String> result = new ArrayList<String>();

	public String getFileNameToSearch() {
		return fileNameToSearch;
	}

	public void setFileNameToSearch(String fileNameToSearch) {
		this.fileNameToSearch = fileNameToSearch;
	}

	public List<String> getResult() {
		return result;
	}


	public void searchDirectory(File directory, String fileNameToSearch) {

		setFileNameToSearch(fileNameToSearch);

		if (directory.isDirectory()) {
			search(directory);
		} else {
			System.out.println(directory.getAbsoluteFile() + " is not a directory!");
		}

	}

	private void search(File file) {

		if (file.isDirectory()) {
			// do you have permission to read this directory?
			if (file.canRead()) {
				for (File temp : file.listFiles()) {
					if (temp.isDirectory()) {
						search(temp);
					} else {
						if (getFileNameToSearch().equals(temp.getName().toLowerCase())) {
							result.add(temp.getAbsoluteFile().toString());
						}

					}
				}

			} else {
				System.out.println(file.getAbsoluteFile() + "Permission Denied");
			}
		}

	}

}