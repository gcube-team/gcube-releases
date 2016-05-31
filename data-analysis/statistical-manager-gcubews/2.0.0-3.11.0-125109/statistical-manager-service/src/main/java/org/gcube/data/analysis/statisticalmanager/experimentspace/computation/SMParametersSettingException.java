package org.gcube.data.analysis.statisticalmanager.experimentspace.computation;

import org.gcube.data.analysis.statisticalmanager.exception.StatisticalManagerException;

public class SMParametersSettingException extends StatisticalManagerException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 364945176259290972L;
	private final static String message = "Settings service parameters error : %s";
	
	public SMParametersSettingException(String parameter){
		super(String.format(message, parameter));
	}
}
