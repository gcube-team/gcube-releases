package org.gcube.dataanalysis.dataminer.poolmanager.service.exceptions;

import java.util.Collection;

import org.gcube.dataanalysis.dataminer.poolmanager.util.exception.DMPMException;

public class UndefinedDependenciesException extends DMPMException {

	private String message;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4504593796352609191L;

	public UndefinedDependenciesException(Collection<String> undefinedDependencies) {
		super ("Some dependencies are not defined");
		this.message = "Following dependencies are not defined:\n";
		for (String n : undefinedDependencies) {
			message += "\n" + n +"\n";
		}
	}
	
	@Override
	public String getErrorMessage() {
		return this.message;
	}

}
