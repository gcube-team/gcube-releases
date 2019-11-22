package org.gcube.accounting.accounting.summary.access;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Set;

import org.gcube.accounting.accounting.summary.access.impl.AccountingDaoImpl;
import org.gcube.accounting.accounting.summary.access.impl.ContextTreeProvider;
import org.gcube.accounting.accounting.summary.access.model.MeasureResolution;
import org.gcube.accounting.accounting.summary.access.model.Report;
import org.gcube.accounting.accounting.summary.access.model.ScopeDescriptor;
import org.gcube.accounting.accounting.summary.access.model.internal.Dimension;
import org.gcube.accounting.accounting.summary.access.model.update.AccountingRecord;
import org.gcube.accounting.accounting.summary.access.model.update.UpdateReport;

public interface AccountingDao {

	public static AccountingDao get() {
		return new AccountingDaoImpl();
	}
	
	public static AccountingDao get(ContextTreeProvider provider) {
		AccountingDaoImpl toReturn= new  AccountingDaoImpl();
		toReturn.setTreeProvider(provider);
		return toReturn;
	}
	
	public ScopeDescriptor getTree(Object request)throws ParameterException, Exception;
	
	public Report getReportByScope(ScopeDescriptor desc, Instant from, Instant to, MeasureResolution resolution) throws ParameterException, SQLException;
	
	
	public Set<Dimension> getDimensions() throws SQLException;
	
	public Set<ScopeDescriptor> getContexts() throws SQLException;
	
	public UpdateReport insertRecords(AccountingRecord... toInsert) throws SQLException;
			
	
}
