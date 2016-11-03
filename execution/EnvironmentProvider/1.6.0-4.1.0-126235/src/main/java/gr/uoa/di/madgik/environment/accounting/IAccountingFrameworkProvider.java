package gr.uoa.di.madgik.environment.accounting;

import gr.uoa.di.madgik.environment.IEnvironmentProvider;
import gr.uoa.di.madgik.environment.accounting.record.ExecutionUsageRecord;
import gr.uoa.di.madgik.environment.accounting.record.JobUsageRecord;

public interface IAccountingFrameworkProvider extends IEnvironmentProvider {

	public void SessionInit() throws Exception;

	public void Send(ExecutionUsageRecord record) throws Exception;
}
