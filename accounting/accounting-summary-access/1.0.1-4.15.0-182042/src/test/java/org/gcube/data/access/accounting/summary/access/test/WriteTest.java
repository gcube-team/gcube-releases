package org.gcube.data.access.accounting.summary.access.test;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.List;

import org.gcube.accounting.accounting.summary.access.AccountingDao;
import org.gcube.accounting.accounting.summary.access.ParameterException;
import org.gcube.accounting.accounting.summary.access.model.MeasureResolution;
import org.gcube.accounting.accounting.summary.access.model.Record;
import org.gcube.accounting.accounting.summary.access.model.Report;
import org.gcube.accounting.accounting.summary.access.model.ReportElement;
import org.gcube.accounting.accounting.summary.access.model.ScopeDescriptor;
import org.gcube.accounting.accounting.summary.access.model.Series;
import org.gcube.accounting.accounting.summary.access.model.internal.Dimension;
import org.gcube.accounting.accounting.summary.access.model.update.AccountingRecord;

public class WriteTest {

	public static void main(String[] args) throws SQLException, ParameterException {
		TokenSetter.set("/gcube/devNext");

		AccountingDao dao=AccountingDao.get();
		
		
		
		ScopeDescriptor devsec=new ScopeDescriptor("Devsec","testScope");
		Dimension dummyDimension=new Dimension("dummy_xyz","Dummy dimension",null,"Test Measures");
		
		Instant measureDate=Instant.parse("2018-01-03T10:15:30.00Z");
		
		AccountingRecord[] records=new AccountingRecord[] {
			new AccountingRecord(devsec, measureDate, dummyDimension, 123l)	
		};
		
		
		Instant from=Instant.parse("2018-01-03T10:15:30.00Z");
		Instant to=Instant.parse("2018-02-03T10:15:30.00Z");
		
		
//		Record[] originals=getMeasures(dao.getReportByScope(devsec, from, to, MeasureResolution.MONTHLY),dummyDimension.getLabel(),devsec.getName());
		
		System.out.println("ATTEMPT 1");
		System.out.println(dao.insertRecords(records));
		
		Record[] updated1=getMeasures(dao.getReportByScope(devsec, from, to, MeasureResolution.MONTHLY),dummyDimension.getLabel(),devsec.getName());
		
//		try {
//			check(originals, updated1);
//		}catch(RuntimeException e) {
//			System.out.println(" Difference : "+e.getMessage());
//		}
		
		System.out.println("ATTEMPT 2");
		System.out.println(dao.insertRecords(new AccountingRecord[] {
				new AccountingRecord(devsec, measureDate.plus(Duration.ofDays(2)), dummyDimension, 12l)	
			}));
		
		Record[] updated2=getMeasures(dao.getReportByScope(devsec, from, to, MeasureResolution.MONTHLY),dummyDimension.getLabel(),devsec.getName());
		
		check(updated1, updated2);
	}

	
	private static Record[] getMeasures(Report rep,String measure, String scope) {
		
		for(ReportElement el : rep.getElements()) 
			if(el.getYAxis().equals(measure)) 
				for(Series series:el.getSerieses())
					if(series.getLabel().equals(scope)) return series.getDataRow();
					
			
		throw new RuntimeException("NOT FOUND");
		
	}
	
	
	private static boolean check(Record[] original, Record[] updated) {
		if(original.length!=updated.length) throw new RuntimeException("Different size");
		for(int i=0; i<original.length;i++) {
			Record orig=original[i];
			Record upd=updated[i];			
			if(!orig.getX().equals(upd.getX())) throw new RuntimeException("Different X on "+i+" element");
			if(!orig.getY().equals(upd.getY())) throw new RuntimeException("Different Y on "+i+" element");
		}
		return true;
	}
}
