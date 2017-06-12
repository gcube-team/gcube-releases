package org.gcube.application.aquamaps.aquamapsservice.impl.engine.analysis;

import java.util.ArrayList;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.AnalysisTableManager;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Analysis;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.AnalysisFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnalysisUpdaterThread extends Thread{
	final static Logger logger= LoggerFactory.getLogger(AnalysisUpdaterThread.class);


	private long millis;


	public AnalysisUpdaterThread(long millis) {
		super("Analysis status updater");
		this.millis=millis;
	}
	@Override
	public void run() {
		while(true){
			try{
				//Updating data generation percent...
				ArrayList<Field> filter=new ArrayList<Field>();
				filter.add(new Field(AnalysisFields.status+"",SubmittedStatus.Generating+"",FieldType.STRING));
				for(Analysis reference : AnalysisTableManager.getList(filter)){
					try{
						Double percent=((double)reference.getPerformedAnalysis().size()/reference.getType().size())*100;
						StringBuilder logBuilder=new StringBuilder(" performed Size = "+reference.getPerformedAnalysis().size());
						logBuilder.append("to perform size ="+reference.getType().size());
						for(Integer reportId:reference.getReportID()){
							Double reportStatus=(AnalyzerFactory.getReport(reportId,false).getPercent());
							percent=percent+(reportStatus/reference.getType().size());
							logBuilder.append("reportId "+reportId+" status "+reportStatus);
						}
						logger.debug("Updateing reference : "+reference.getId()+", percent "+percent+", forumla details "+logBuilder);
						AnalysisTableManager.setPhasePercent(percent, reference.getId());
					}catch(Exception e){logger.warn("Skipping percent update for analysis id "+reference.getId()+", report id was "+reference.getReportID(),e);}
				}				
			}catch(Exception e){
				logger.error("Unexpected exception ",e);
			}finally{
				try{					
					Thread.sleep(millis);
				}catch(InterruptedException e){
					//Woken up
				}
			}
		}
	}
}
