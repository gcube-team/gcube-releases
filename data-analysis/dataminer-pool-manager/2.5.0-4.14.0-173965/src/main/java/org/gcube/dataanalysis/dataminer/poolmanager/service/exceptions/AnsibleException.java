package org.gcube.dataanalysis.dataminer.poolmanager.service.exceptions;

import org.gcube.dataanalysis.dataminer.poolmanager.util.exception.DMPMException;

public class AnsibleException extends DMPMException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6772009633547404120L;
	private int returnCode;



	public AnsibleException(int returnCode) {
		super ("Ansible work failed");
		this.returnCode =returnCode;
	}
	
	@Override
	public String getErrorMessage() {
		return "Installation failed. Return code=" + this.returnCode;
	}

}
