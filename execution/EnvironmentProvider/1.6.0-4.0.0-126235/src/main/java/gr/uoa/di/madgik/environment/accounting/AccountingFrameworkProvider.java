package gr.uoa.di.madgik.environment.accounting;

import gr.uoa.di.madgik.environment.accounting.record.ExecutionUsageRecord;
import gr.uoa.di.madgik.environment.exception.EnvironmentReportingException;
import gr.uoa.di.madgik.environment.exception.EnvironmentValidationException;

public class AccountingFrameworkProvider implements IAccountingFrameworkProvider {

	private static IAccountingFrameworkProvider StaticProvider = null;
	private static final Object lockMe = new Object();

	private IAccountingFrameworkProvider Provider = null;

	public static IAccountingFrameworkProvider init(String ProviderName) throws EnvironmentValidationException {
		try {
			synchronized (AccountingFrameworkProvider.lockMe) {
				if (AccountingFrameworkProvider.StaticProvider == null) {
					Object o;
					if (ProviderName.equals(AccountingFrameworkProvider.class.getName()))
						throw new EnvironmentValidationException("Class " + AccountingFrameworkProvider.class.getName()
								+ " cannot be defined as accounting provider");
					if (ProviderName == null || ProviderName.trim().equals(""))
						throw new EnvironmentValidationException("Provider name has not been specified correctly: " + ProviderName);
					else {
						Class<?> c = Class.forName(ProviderName);
						o = c.newInstance();
					}
					if (!(o instanceof IAccountingFrameworkProvider))
						throw new EnvironmentValidationException("Class" + ProviderName + " is not an accounting provider");
					AccountingFrameworkProvider prov = new AccountingFrameworkProvider();
					prov.Provider = (IAccountingFrameworkProvider) o;
					prov.SessionInit();
					AccountingFrameworkProvider.StaticProvider = prov;
				}
			}
			return AccountingFrameworkProvider.StaticProvider;
		} catch (Exception ex) {
			throw new EnvironmentValidationException("Could not initialize Accounting Provider", ex);
		}
	}

	public static boolean isInit() {
		synchronized (AccountingFrameworkProvider.lockMe) {
			return (AccountingFrameworkProvider.StaticProvider != null);
		}
	}

	public void SessionInit() throws Exception {
		if (this.Provider == null)
			throw new EnvironmentReportingException("Accounting Provider not initialized");
		this.Provider.SessionInit();
	}

	public void Send(ExecutionUsageRecord record) throws Exception {
		if (this.Provider == null)
			throw new EnvironmentReportingException("Accounting Provider not initialized");
		this.Provider.Send(record);
	}
}
