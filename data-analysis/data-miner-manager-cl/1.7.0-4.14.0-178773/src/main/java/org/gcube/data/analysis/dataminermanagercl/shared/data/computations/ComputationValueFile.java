package org.gcube.data.analysis.dataminermanagercl.shared.data.computations;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ComputationValueFile extends ComputationValue {
	private static final long serialVersionUID = -5845606225432949795L;

	private String fileName;
	private String mimeType;
	private boolean netcdf;

	public ComputationValueFile() {
		super(ComputationValueType.File);
	}

	public ComputationValueFile(String url, String fileName, String mimeType, boolean netcdf) {
		super(ComputationValueType.File, url);
		this.fileName = fileName;
		this.mimeType = mimeType;
		this.netcdf = netcdf;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public boolean isNetcdf() {
		return netcdf;
	}

	public void setNetcdf(boolean netcdf) {
		this.netcdf = netcdf;
	}

	@Override
	public String toString() {
		return "ComputationValueFile [fileName=" + fileName + ", mimeType=" + mimeType + ", netcdf=" + netcdf + "]";
	}

}
