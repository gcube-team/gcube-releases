package org.gcube.data.analysis.dataminermanagercl.shared.data.computations;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ComputationValueFile extends ComputationValue {
	private static final long serialVersionUID = -5845606225432949795L;

	private String fileName;
	private String mimeType;

	public ComputationValueFile() {
		super(ComputationValueType.File);
	}

	public ComputationValueFile(String url, String fileName, String mimeType) {
		super(ComputationValueType.File, url);
		this.fileName = fileName;
		this.mimeType = mimeType;
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

	@Override
	public String toString() {
		return "ComputationValueFile [fileName=" + fileName + ", mimeType="
				+ mimeType + ", type=" + type + ", value=" + value + "]";
	}

	

}
