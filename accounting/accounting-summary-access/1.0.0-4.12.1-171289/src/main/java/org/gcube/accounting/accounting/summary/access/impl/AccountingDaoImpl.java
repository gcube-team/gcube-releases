package org.gcube.accounting.accounting.summary.access.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.gcube.accounting.accounting.summary.access.AccountingDao;
import org.gcube.accounting.accounting.summary.access.ParameterException;
import org.gcube.accounting.accounting.summary.access.impl.DBStructure.CONTEXTS;
import org.gcube.accounting.accounting.summary.access.impl.DBStructure.DIMENSIONS;
import org.gcube.accounting.accounting.summary.access.impl.DBStructure.Measure;
import org.gcube.accounting.accounting.summary.access.model.MeasureResolution;
import org.gcube.accounting.accounting.summary.access.model.Record;
import org.gcube.accounting.accounting.summary.access.model.Report;
import org.gcube.accounting.accounting.summary.access.model.ReportElement;
import org.gcube.accounting.accounting.summary.access.model.ScopeDescriptor;
import org.gcube.accounting.accounting.summary.access.model.Series;
import org.gcube.accounting.accounting.summary.access.model.internal.Dimension;
import org.gcube.accounting.accounting.summary.access.model.update.AccountingRecord;
import org.gcube.accounting.accounting.summary.access.model.update.UpdateReport;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountingDaoImpl implements AccountingDao{

	private static final ZoneId UTC=ZoneId.of("UTC");
	
	
	private ContextTreeProvider treeProvider=null;
	private ConnectionManager connectionManager=null;

	public AccountingDaoImpl() {
		connectionManager=new BasicConnectionManager();
		treeProvider=new BasicContextTreeProvider();
	}

	public void setTreeProvider(ContextTreeProvider treeProvider) {
		this.treeProvider = treeProvider;
	}

	public AccountingDaoImpl(ContextTreeProvider treeProvider, ConnectionManager connectionManager) {
		super();
		this.treeProvider = treeProvider;
		this.connectionManager = connectionManager;
	}



	@Override
	public Report getReportByScope(ScopeDescriptor desc, Instant from, Instant to, MeasureResolution resolution) throws SQLException, ParameterException {

		DateTimeFormatter formatter=getFormatter(resolution);
	
		log.info("Loading report {} for {} between {} and {} ",resolution,desc.getId(),
				formatter.format(from.atZone(UTC).toLocalDateTime()),formatter.format(to.atZone(UTC).toLocalDateTime()));
		long startReportTime=System.currentTimeMillis();
		
		
		

		if(from.isAfter(to)) throw new ParameterException("Irregular time interval: \"From\" parameter cannot be after \"To\" parameter.");
		int timeSlices=(int)getRangeSize(from, to, resolution);
//		if(timeSlices==0) {
//			log.debug("Time slices is {}. Going to increment \"To\" parameter.",timeSlices);
//			toDate=increment(toDate,resolution,1);
//			timeSlices=getRangeSize(fromDate, toDate, resolution);
//			if(timeSlices==0) throw new RuntimeException("Unexpected Exception");
//		}


		Connection conn=connectionManager.getConnection();
		Queries queries=new Queries(conn);



		//load available dimensions in time slice
		ResultSet dimensionRS=queries.getAvailableDimensions(from, to, desc, resolution);
		LinkedList<Dimension> foundDimensions=new LinkedList<>();
		while(dimensionRS.next()){
			String id=dimensionRS.getString(DIMENSIONS.ID);
			String label=dimensionRS.getString(DIMENSIONS.LABEL);
			String group=dimensionRS.getString(DIMENSIONS.GROUP);
			String aggregatedDim=dimensionRS.getString(DIMENSIONS.AGGREGATED_MEASURE);

			foundDimensions.add(new Dimension(id,label,aggregatedDim,group));
		}
		log.debug("Found {} dimensions to load. ",foundDimensions.size());

		// Prepare reports for each Dimension
		LinkedList<ReportElement> reports=new LinkedList<>();
		for(Dimension entry: foundDimensions){
			String xLabel="Time";
			String yLabel=entry.getLabel();
			String category=entry.getGroup();

			// Report 1 series for selected Scope
			reports.add(new ReportElement(desc.getName()+" "+yLabel,category,
					xLabel,yLabel,new Series[]{getSeries(queries, from, to, entry, desc, resolution, timeSlices)}));

			// Report 2 series for each children
			if(desc.hasChildren()) {
				LinkedList<Series> childrenSeries=new LinkedList<>();
				for(ScopeDescriptor child:desc.getChildren()){
					childrenSeries.add(getSeries(queries, from, to, entry, child, resolution, timeSlices));
				}
				reports.add(new ReportElement(desc.getName()+" children "+yLabel,category,
						xLabel,yLabel,childrenSeries.toArray(new Series[childrenSeries.size()])));
			}


			//			PreparedStatement psMeasure=queries.prepareMeasuresByDimension(desc, resolution);
			//			
			//			LocalDateTime toDate=to.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			//			for(LocalDateTime toAsk=from.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(); 
			//					toAsk.isAfter(toDate);toAsk=increment(toAsk,resolution)){
			//			// Scan for time slice	
			//			}
		}

		log.info("Loaded {} report elements in {} ms",reports.size(),(System.currentTimeMillis()-startReportTime));


		return new Report(reports);

	}


//	private Instant fixLowerBound(Instant toFix,MeasureResolution resolution) throws ParameterException {
//		switch(resolution) {		
//		case MONTHLY : {
//			LocalDateTime local=LocalDateTime.ofInstant(toFix, ZoneId.of("UTC")).with(firstDayOfMonth());
//			return toFix.with(ChronoField.DAY,local.getDayOfMonth()).with(ChronoField.CLOCK_HOUR_OF_DAY,0).
//				with(ChronoField.MINUTE_OF_HOUR,0).with(ChronoField.SECOND_OF_MINUTE,0).with(ChronoField.NANO_OF_SECOND,0);
//		}
//		default : throw new ParameterException("Invalid resolution "+resolution);
//		}
//	}
//
//	private Instant fixUpperBound(Instant toFix,MeasureResolution resolution) throws ParameterException {
//		switch(resolution) {
//		case MONTHLY : {
//			LocalDateTime local=LocalDateTime.ofInstant(toFix, ZoneId.of("UTC")).with(lastDayOfMonth());
//			return toFix.with(ChronoField.DAY_OF_MONTH,local.getDayOfMonth()).with(ChronoField.CLOCK_HOUR_OF_DAY,23).
//				with(ChronoField.MINUTE_OF_HOUR,59).with(ChronoField.SECOND_OF_MINUTE,59).with(ChronoField.NANO_OF_SECOND,999999999);
//		}
//		default : throw new ParameterException("Invalid resolution "+resolution);
//		}
//	}
	

	@Override
	public ScopeDescriptor getTree(Object request) throws Exception {
		return treeProvider.getTree(request);
	}

	
	@Override
	public Set<Dimension> getDimensions() throws SQLException {
		Connection conn=connectionManager.getConnection();
		Queries q=new Queries(conn);
		ResultSet rs=q.listDimensions();
		HashSet<Dimension> toReturn=new HashSet<>();
		while(rs.next()) {
			String id=rs.getString(DIMENSIONS.ID);
			String label=rs.getString(DIMENSIONS.LABEL);
			String group=rs.getString(DIMENSIONS.GROUP);
			String aggregatedMeasure =rs.getString(DIMENSIONS.AGGREGATED_MEASURE);
			toReturn.add(new Dimension(id,label,aggregatedMeasure,group));
		}
		return toReturn;
	}

	@Override
	public Set<ScopeDescriptor> getContexts() throws SQLException {
		Connection conn=connectionManager.getConnection();
		Queries q=new Queries(conn);
		ResultSet rs=q.listContexts();
		HashSet<ScopeDescriptor> toReturn=new HashSet<>();
		while(rs.next()) {
			String id=rs.getString(CONTEXTS.ID);
			String label=rs.getString(CONTEXTS.LABEL);			
			toReturn.add(new ScopeDescriptor(label,id));
		}
		return toReturn;
	}

	@Override
	public UpdateReport insertRecords(AccountingRecord... toInsert) throws SQLException {		
		log.trace("Preapring to insert {} records.",toInsert.length);
		
		Set<Dimension> existingDimensions=getDimensions();
		Set<ScopeDescriptor> existingContexts=getContexts();
		Connection conn=connectionManager.getConnection();
		
		log.debug("Loaded {} existing dimensions and {} contexts ",existingDimensions.size(),existingContexts.size());
		Queries q=new Queries(conn);
		PreparedStatement psMeasure=q.getMeasureInsertionPreparedStatement();
		PreparedStatement psContexts=q.getContextInsertionPreparedStatement();
		PreparedStatement psDimensions=q.getDimensionInsertionPreparedStatement();
		
		long writeMeasureCounter=0l;
		Set<Dimension> insertedDimensions=new HashSet<>();
		Set<ScopeDescriptor> insertedContexts=new HashSet<>();
		
		long previousMeasureCount=q.getMeasureCount(Measure.TABLENAME);
		
		
		log.debug("Actually registering records..");
		for(AccountingRecord record:toInsert) {
			Dimension dim=record.getDimension();
			ScopeDescriptor context=record.getContext();
			
			
			if(!existingDimensions.contains(dim)) {
				log.debug("Registering {} ",dim);
				//ID,Label,Group,AGG
				psDimensions.setString(1, dim.getId());
				psDimensions.setString(2, dim.getLabel());
				psDimensions.setString(3, dim.getGroup());
				psDimensions.setString(4, dim.getAggregatedMeasure());
				if(psDimensions.executeUpdate()==0)throw new SQLException("Error registering Dimension : No inserted rows");
				insertedDimensions.add(dim);
				existingDimensions.add(dim);
			}
			
			if(!existingContexts.contains(context)) {
				log.debug("Registering {} ",context);
				//ID,Label
				psContexts.setString(1, context.getId());
				psContexts.setString(2, context.getName());
				if(psContexts.executeUpdate()==0)throw new SQLException("Error registering Context : No inserted rows");
				insertedContexts.add(context);
				existingContexts.add(context);
			}
			
			//Context, Dim,time,measure
			psMeasure.setString(1, context.getId());
			psMeasure.setString(2, dim.getId());
			psMeasure.setTimestamp(3, new Timestamp(record.getTime().toEpochMilli()));
			psMeasure.setLong(4, record.getMeasure());
			psMeasure.setLong(5, record.getMeasure());
			if(psMeasure.executeUpdate()==0) throw new SQLException("Error registering Measure : No inserted rows"); 
			writeMeasureCounter++;
		}
		
		conn.commit();
		long resultingMeasureCount=q.getMeasureCount(Measure.TABLENAME);
		log.trace("Done inserting {} rows. Registered {} dimensions and {} contexts.",writeMeasureCounter,insertedDimensions.size(),insertedContexts.size());
		
		return new UpdateReport(previousMeasureCount,resultingMeasureCount,writeMeasureCounter,insertedContexts,insertedDimensions);
	}




	private static final long getRangeSize(Instant from, Instant to, MeasureResolution resolution) throws ParameterException {
		log.debug("Evaluating time range between {} , {} [{}]",from,to,resolution);

		Period d=Period.between(LocalDateTime.ofInstant(from, UTC).toLocalDate(), LocalDateTime.ofInstant(to, UTC).toLocalDate());
		switch(resolution) {
		case MONTHLY : return d.get(ChronoUnit.MONTHS)+(d.get(ChronoUnit.YEARS)*12)+1; // +1 to include upper bound
		default : throw new ParameterException("Invalid resolution "+resolution);
		}

	}
	


	private static Instant increment(Instant toIncrement,MeasureResolution res,int offset){
		switch(res){
		case MONTHLY : return LocalDateTime.ofInstant(toIncrement, UTC).plus(offset,ChronoUnit.MONTHS).toInstant(ZoneOffset.UTC);
		default : throw new RuntimeException("Unexpected Resolution "+res);
		}
	}

	private static final DateTimeFormatter monthFormatter=DateTimeFormatter.ofPattern("yyyy-MM");


	private static DateTimeFormatter getFormatter(MeasureResolution res){
		switch(res){
		case MONTHLY : return monthFormatter;
		default : throw new RuntimeException("Unexpected Resolution "+res);
		}
	}

	

	private Series getSeries(Queries queries, Instant from, Instant to, Dimension dim, ScopeDescriptor scope, MeasureResolution res, int timeSlices) throws SQLException{

		Record[] records=new Record[timeSlices];
		PreparedStatement ps=queries.prepareMeasuresByDimension(scope, res);
		DateTimeFormatter formatter=getFormatter(res);
		

		Instant currentTimeSlice=from;
		for(int i=0;i<timeSlices;i++){
			currentTimeSlice=increment(from,res,i); // Increment Date
//			Instant toSetEndDate=increment(from,res,i+1);

			Timestamp t=new Timestamp(currentTimeSlice.toEpochMilli());
			ps.setTimestamp(1, t);
			ps.setTimestamp(2, t);
			ps.setString(3, dim.getId());

			ResultSet rs=ps.executeQuery();
			Record toSet=new Record(formatter.format(LocalDateTime.ofInstant(currentTimeSlice, UTC)),0l);
			if(rs.next()){
				toSet.setY(rs.getLong(Measure.MEASURE));
			}
			records[i]=toSet;
		}

		return new Series(scope.getName(),records);

	}
}
