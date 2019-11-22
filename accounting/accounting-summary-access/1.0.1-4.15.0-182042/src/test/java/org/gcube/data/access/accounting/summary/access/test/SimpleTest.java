package org.gcube.data.access.accounting.summary.access.test;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.GregorianCalendar;

import org.gcube.accounting.accounting.summary.access.AccountingDao;
import org.gcube.accounting.accounting.summary.access.ParameterException;
import org.gcube.accounting.accounting.summary.access.model.MeasureResolution;
import org.gcube.accounting.accounting.summary.access.model.ScopeDescriptor;

public class SimpleTest {
	
	private static final SimpleDateFormat monthFormatter=new SimpleDateFormat("yyyy-MM-dd-HH:mm ZZZ");

	
	public static void main(String[] args) throws ParameterException, Exception {
		TokenSetter.set("/gcube/devNext");
//		AccountingDao dao=AccountingDao.get();
		AccountingDao dao=AccountingDao.get(new DummyContextTreeProvider());
		
		ScopeDescriptor desc=dao.getTree(null);
		
		System.out.println(desc);
		
//		System.out.println("1527811200000 "+monthFormatter.format(new Date(1527811200000l)));
//		System.out.println("1527804000000 "+monthFormatter.format(new Date(1527804000000l)));
		
//		Date from=new Date(1514764800000l);
		
		Instant from=Instant.parse("2018-07-03T10:15:30.00Z");
		
//		System.out.println("Time: " +from.toEpochMilli()+" -> "+monthFormatter.format(from));
		
//		Date to=new GregorianCalendar(2018,6,1).getTime();
		
		Instant to=Instant.parse("2018-12-03T10:15:30.00Z");
		
		scan(desc,from,to,dao);
		
		
	
		
	}

	private static void scan(ScopeDescriptor desc, Instant from, Instant to, AccountingDao dao) throws ParameterException, SQLException {
		System.out.println("**********************************************************************************************************");
		System.out.println(desc);
		System.out.println(dao.getReportByScope(desc, from, to, MeasureResolution.MONTHLY));
//		if(desc.hasChildren()) {
//			for(ScopeDescriptor child:desc.getChildren())
//				scan(child,from,to,dao);
//		}
	}
	
	
}
