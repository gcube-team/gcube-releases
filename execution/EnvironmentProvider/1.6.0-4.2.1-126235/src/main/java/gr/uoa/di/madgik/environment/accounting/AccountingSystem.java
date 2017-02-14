package gr.uoa.di.madgik.environment.accounting;

import gr.uoa.di.madgik.environment.accounting.record.ExecutionUsageRecord;
import gr.uoa.di.madgik.environment.exception.EnvironmentValidationException;

public class AccountingSystem {
	private static IAccountingFrameworkProvider provider = null;
	private static Object lockMe = new Object();

	public static void init(String providerName) throws EnvironmentValidationException {
		synchronized (lockMe) {
			if (provider == null)
				provider = AccountingFrameworkProvider.init(providerName);
		}
	}

	public static void send(ExecutionUsageRecord record) throws Exception {
		synchronized (lockMe) {
			provider.Send(record);
		}
	}
	
	public static boolean isInitialised() {
		return provider != null;
	}
}
