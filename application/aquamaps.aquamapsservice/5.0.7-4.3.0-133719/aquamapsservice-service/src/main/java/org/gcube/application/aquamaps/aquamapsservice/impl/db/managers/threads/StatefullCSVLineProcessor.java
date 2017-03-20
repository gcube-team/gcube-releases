package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.threads;

import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import net.sf.csv4j.CSVLineProcessor;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatefullCSVLineProcessor implements CSVLineProcessor {
	
	final static Logger logger= LoggerFactory.getLogger(StatefullCSVLineProcessor.class);
	boolean continueProcess=true;
	private PreparedStatement ps=null;
	private DBSession session=null;
	private Long count=0l;
	private ResourceStatus status=ResourceStatus.Completed;
	private List<Field> model;
	private int[] modelCSVFieldsMapping;	
	private Long totalCount;
	private boolean[] fieldsMask;
	private long updateStep;
	private Resource toFillResource; 
	
	
	public StatefullCSVLineProcessor(List<Field> model,Resource tofillResource,Long totalCount,boolean[] fieldsmask) {
		 this.model=model;
		 modelCSVFieldsMapping= new int[model.size()];
		 this.totalCount=totalCount;		
		 this.toFillResource=tofillResource;
		 this.fieldsMask=fieldsmask;
		 updateStep=totalCount>1000?1000:totalCount/5;
		 logger.info("Instatiated Line Processor");
		 logger.info("csv parsed row count : "+totalCount);
		 logger.info("to Fill Resource : "+toFillResource);
	}
	
	
	@Override
	public boolean continueProcessing() {return continueProcess;}
	
	@Override
	public void processDataLine(int arg0, List<String> arg1) {
		List<Field> line= new ArrayList<Field>();
		try{
		for(int i=0;i<model.size();i++){
			Field modelField=model.get(i);
			line.add(new Field(modelField.name(),arg1.get(modelCSVFieldsMapping[i]),modelField.type()));
		}
		count+=(session.fillParameters(line, 0, ps)).executeUpdate();
		if(count % updateStep==0) {
			logger.debug("Updateing "+count+" / "+totalCount);
			SourceManager.setCountRow(toFillResource.getSearchId(), count);
		}
		}catch(Exception e){
			logger.error("Unable to insert row",e);
			try{
			ParameterMetaData meta=ps.getParameterMetaData();
			logger.error("Parameters :");
			for(int i=0;i<meta.getParameterCount();i++){
				Field f=line.get(i);
				logger.error(f.name()+" FIELD TYPE : "+f.type()+" SQL TYPE : "+meta.getParameterType(i+1)+" VALUE :"+f.value());
			}
			}catch(Exception e1){
				logger.error("Unable to read parameter metadata ",e1);
			}
			status=ResourceStatus.Error;
		}
	}
	
	@Override
	public void processHeaderLine(int arg0, List<String> arg1) {
		try{
			logger.trace("Processing Header..");
			continueProcess=arg1.size()==model.size();
			if(continueProcess){
				for(int i=0;i<arg1.size();i++){
					if(fieldsMask[i]){
						boolean found=false;
						for(int j=0;j<model.size();j++)
							if(arg1.get(i).equalsIgnoreCase(model.get(j).name())){
								modelCSVFieldsMapping[j]=i;
								found=true;
								break;
							}
						if(!found) throw new Exception("Found field "+arg1.get(i)+" has no match in table");
					}
				}
				logger.trace("Matched "+arg1.size()+" fields : ");
				for(int i=0;i<model.size();i++){
					Field modelField=model.get(i);
					logger.debug(modelField.name()+" , " +arg1.get(modelCSVFieldsMapping[i])+" , "+modelField.type());
				}
				session=DBSession.getInternalDBSession();
				session.disableAutoCommit();
				ps=session.getPreparedStatementForInsert(model, toFillResource.getTableName());
			}else throw new Exception("Selected Type and csv fields count are not compatible");
		}catch(Exception e){
			logger.error("Unable to initialize reading",e);
			continueProcess=false;
			status=ResourceStatus.Error;
		}
	}
	
	public void close(){
		if (session!=null){
			try{
				logger.info("finalizing csv process...");								
				session.commit();
				session.close();
			}catch(Exception e){
				logger.warn("Unable to close session", e);
			}
		}
		try{
			toFillResource.setRowCount(count);
			toFillResource.setStatus(status);
			logger.info("Updateing resource "+toFillResource);
			SourceManager.update(toFillResource);
		}catch(Exception e){
			logger.warn("Unable to update resource meta ",e);
		}
	}
}
