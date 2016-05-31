package it.eng.rdlab.soa3.assertion.manager;

import it.eng.rdlab.soa3.assertion.configuration.ConfigurationBean;
import it.eng.rdlab.soa3.assertion.configuration.ConfigurationInformationBean;
import it.eng.rdlab.soa3.assertion.validation.Assertionsvalidation;
import it.eng.rdlab.soa3.assertion.validation.IAssertionValidator;
import it.eng.rdlab.soa3.config.ConfigurationManager;



public class AssertionValidationFactory 
{
	private static IAssertionValidator instance;
	
	public static IAssertionValidator getCurrentInstance ()
	{
		if (instance == null) getNewInstance();
		
		return instance;
	}
	
	public static IAssertionValidator getNewInstance ()
	{
		instance = new Assertionsvalidation();
		String path = ConfigurationManager.getInstance().getCaCert();
	
		ConfigurationInformationBean configuration = new ConfigurationInformationBean();
		configuration.setProperty(ConfigurationBean.PUBLIC_KEY, path);
		configuration.setProperty(ConfigurationBean.SIGNATURE_VALIDATION_ENABLED, ConfigurationManager.getInstance().getAssertionSignatureValidation());
		configuration.setProperty(ConfigurationBean.TIME_VALIDATION_ENABLED, ConfigurationManager.getInstance().getAssertionTimeValidation());
		instance.configure(configuration);
		return instance;
	}

}
