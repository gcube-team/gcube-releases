package org.gcube.data.spd.executor.jobs.layer;

import static org.gcube.data.streams.dsl.Streams.convert;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

import org.gcube.data.spd.executor.jobs.URLJob;
import org.gcube.data.spd.executor.jobs.csv.OccurrenceReaderByKey;
import org.gcube.data.spd.model.PointInfo;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.service.types.JobStatus;
import org.gcube.data.spd.model.service.types.MetadataDetails;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.readers.LocalReader;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.LocalWrapper;
import org.gcube.data.spd.utils.DynamicMap;
import org.gcube.data.spd.utils.MapUtils;
import org.gcube.data.streams.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class LayerCreatorJob extends URLJob{


	private static Logger logger = LoggerFactory.getLogger(LayerCreatorJob.class);

	private String resultURL = null;

	private String errorFileURL = null;

	private JobStatus status;

	private Calendar endDate, startDate;

	private String id;

	private int completedEntries = 0;

	private Map<String, AbstractPlugin> plugins;
	
	private MetadataDetails metadata ;
	
	public LayerCreatorJob(String metadataDetails, Map<String, AbstractPlugin> plugins) {
		this.id = UUID.randomUUID().toString();
		this.status = JobStatus.PENDING;
		this.plugins = plugins;
		this.metadata = (MetadataDetails) new XStream().fromXML(metadataDetails);
	}
	
	@Override
	public boolean isResubmitPermitted() {
		return false;
	}

	@Override
	public JobStatus getStatus() {
		return status;
	}

	@Override
	public void setStatus(JobStatus status) {
		this.status = status;
	}

	@Override
	public String getResultURL() {
		return resultURL;
	}

	@Override
	public String getErrorURL() {
		return errorFileURL;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean validateInput(String input) {
		try{
			MetadataDetails md = (MetadataDetails) new XStream().fromXML(input);
			if (md!=null)
				return true;
		}catch(Throwable t){}
		return false;
	}

	@Override
	public int getCompletedEntries() {
		return completedEntries;
	}

	@Override
	public Calendar getStartDate() {
		return startDate;
	}

	@Override
	public Calendar getEndDate() {
		return endDate;
	}

	@Override
	public void execute() {
		try{
			this.startDate = Calendar.getInstance();
			this.status = JobStatus.RUNNING;
			
			LocalWrapper<OccurrencePoint> localWrapper = new LocalWrapper<OccurrencePoint>(2000);
			localWrapper.forceOpen();
					
			Stream<String> ids =convert(DynamicMap.get(this.id));
			
			OccurrenceReaderByKey occurrenceReader = new OccurrenceReaderByKey(localWrapper, ids, plugins);
			
			new Thread(occurrenceReader).start();
			
			LocalReader<OccurrencePoint> ocReader= new LocalReader<OccurrencePoint>(localWrapper);
			
			ArrayList<PointInfo> points=new ArrayList<>();
			
			while (ocReader.hasNext()){
				OccurrencePoint op = ocReader.next();
				points.add(new PointInfo(op.getDecimalLongitude(), op.getDecimalLatitude()));
				completedEntries++;
			}
						
			org.gcube.data.spd.utils.MapUtils.Map map = MapUtils.publishLayerByCoords(this.metadata, points,false,true);
			this.resultURL = map.getLayerUUID(); 
			this.status = JobStatus.COMPLETED;
		}catch (Exception e) {
			logger.error("error executing Layer Job",e);
			this.status = JobStatus.FAILED;
			return;
		} finally{
			this.endDate = Calendar.getInstance();
			DynamicMap.remove(this.id);
		}

	}



}
