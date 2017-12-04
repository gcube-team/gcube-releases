package org.gcube.portlets.user.dataminermanager.shared.data.computations;

import java.util.ArrayList;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ComputationValueFileList extends ComputationValue {
	private static final long serialVersionUID = -5845606225432949795L;

	private ArrayList<ComputationValue> fileList;
	private String separator;

	public ComputationValueFileList() {
		super(ComputationValueType.FileList);
	}

	public ComputationValueFileList(ArrayList<ComputationValue> fileList,
			String separator) {
		super(ComputationValueType.FileList);
		this.fileList = fileList;
		this.separator = separator;
		value = new String();
		for (ComputationValue file : fileList) {
			value = value + file.getValue();
		}
	}

	public ArrayList<ComputationValue> getFileList() {
		return fileList;
	}

	public void setFileList(ArrayList<ComputationValue> fileList) {
		this.fileList = fileList;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	@Override
	public String toString() {
		return "ComputationValueFileList [fileList=" + fileList
				+ ", separator=" + separator + ", type=" + type + ", value="
				+ value + "]";
	}

}
