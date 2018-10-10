package org.gcube.portlets.admin.accountingmanager.shared.export;

import java.io.Serializable;
import java.nio.file.Path;

import org.gcube.portlets.admin.accountingmanager.server.export.model.AccountingDataModel;

/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ExportDescriptor implements Serializable {
	private static final long serialVersionUID = 4778932733041422948L;
	private Path path;
	private AccountingDataModel csvModel;
	private String fileExtension;

	public ExportDescriptor() {
		super();
	}

	public ExportDescriptor(Path path, AccountingDataModel csvModel, String fileExtension) {
		super();
		this.path = path;
		this.csvModel = csvModel;
		this.fileExtension=fileExtension;
		
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public AccountingDataModel getCsvModel() {
		return csvModel;
	}

	public void setCsvModel(AccountingDataModel csvModel) {
		this.csvModel = csvModel;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	@Override
	public String toString() {
		return "ExportDescriptor [path=" + path + ", csvModel=" + csvModel
				+ ", fileExtension=" + fileExtension + "]";
	}

	

	

	

}
