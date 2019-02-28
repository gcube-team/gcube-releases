package org.gcube.data.access.accounting.summary.access.test;

import java.sql.SQLException;
import java.time.Instant;

import org.gcube.accounting.accounting.summary.access.AccountingDao;
import org.gcube.accounting.accounting.summary.access.model.ScopeDescriptor;
import org.gcube.accounting.accounting.summary.access.model.internal.Dimension;
import org.gcube.accounting.accounting.summary.access.model.update.AccountingRecord;

public class WriteTest {

	public static void main(String[] args) throws SQLException {
		TokenSetter.set("/gcube/devNext");

		AccountingDao dao=AccountingDao.get();
		
		
		ScopeDescriptor devsec=new ScopeDescriptor("Devsec","/gcube/devsec");
		Dimension dummyDimension=new Dimension("dummy_xyz","Dummy dimension",null,"Test Measures");
		
		Instant measureDate=Instant.parse("2018-01-03T10:15:30.00Z");
		
		AccountingRecord[] records=new AccountingRecord[] {
			new AccountingRecord(devsec, measureDate, dummyDimension, 123l)	
		};
		
		System.out.println("ATTEMPT 1");
		System.out.println(dao.insertRecords(records));
		
		System.out.println("ATTEMPT 2");
		System.out.println(dao.insertRecords(records));
		
	}

}
