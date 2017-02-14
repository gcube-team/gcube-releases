package org.gcube.application.aquamaps.aquamapsservice.impl.monitor;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.common.core.contexts.GHNContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class StatusMonitorThread extends Thread {

	final static Logger logger= LoggerFactory.getLogger(StatusMonitorThread.class);

	private long interval;
	private long freeSpaceThreshold;

	private static String valueDisk="freeDiskSpace";



	public StatusMonitorThread(long waitTime,long freeSpaceThreshold) {
		super("Machine status monitor");
		this.interval=waitTime;
		this.freeSpaceThreshold=freeSpaceThreshold;
	}


	@Override
	public void run() {
		while(true){
			try{
//				logger.trace("Retrieving free space..");
				long currentDisk=GHNContext.getContext().getFreeSpace(GHNContext.getContext().getLocation());
				if(currentDisk<freeSpaceThreshold){
					logger.trace("Observed "+currentDisk+" / "+freeSpaceThreshold);
					logger.trace("Storing value..");
					HSQLDB.insertReportItem(valueDisk, currentDisk);
					logger.trace("Forming report..");
					ReportItem ri=HSQLDB.getReport(valueDisk);
					ri.setTime(ServiceUtils.getDate());
					ri.setActualValue(currentDisk);
					ri.setThreshold(freeSpaceThreshold);
					ri.setValueName(valueDisk);
					XStream xstream = new XStream();
					xstream.alias("ReportItem", org.gcube.application.aquamaps.aquamapsservice.impl.monitor.ReportItem.class);
					String toPublish=xstream.toXML(ri);
					logger.trace("publishing "+toPublish);
					ServiceContext.getContext().getInstance().setSpecificData(toPublish);
				}				
				
			}catch(Exception e){
				logger.error("Unexpected error ", e);
			}finally{
				try{
//					logger.trace("Monitoring routine completed, gonna execute again in : "+interval);
				Thread.sleep(interval);
				}catch(InterruptedException eI){
					logger.trace("Awaken monitoring thread");
				}
			}
		}
	}

}
