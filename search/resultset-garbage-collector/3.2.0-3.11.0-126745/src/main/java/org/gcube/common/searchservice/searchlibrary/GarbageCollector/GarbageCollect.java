package org.gcube.common.searchservice.searchlibrary.GarbageCollector;


import org.gcube.common.searchservice.resultsetservice.stubs.ReclaimNotificationMessageType;
import org.gcube.common.searchservice.resultsetservice.stubs.ReclaimNotificationMessageWrapperType;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSConstants;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSFileHelper;
import org.globus.wsrf.impl.SimpleTopic;

import java.io.File;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * @author UoA
 * 
 * This Library element scans the predefined result set store locations and
 * garbage collects the parts of the stored result sets, destroying the
 * respective WS-Resources
 */

public class GarbageCollect implements Runnable {
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(GarbageCollect.class);
	
	private static SimpleTopic reclaimingEprs;

	/**
	 * Default constructor that registers a topic to IS for notifying when Results Sets are reclaimed by the Collector
	 * @param topic the topic for notifying about garbage collected RS eprs
	 */
	 public GarbageCollect(SimpleTopic topic){
		reclaimingEprs = topic; 
	}
	
	/**
	 * Entry point for the procedure to start
	 * 
	 * @param args
	 *            no arguments expected
	 * @throws Exception
	 *             An unrecoverable for the operation error ocured
	 */
	public static void main(String[] args) throws Exception {
		File base = new File(RSConstants.baseDirectory);
		MessageDigest algorithm = null;
		try {
			algorithm = MessageDigest.getInstance("SHA-1");
		} catch (Exception e) {
			log.error("Could not initialize message digest for SHA-1. Throwing Exception",e);
			throw new Exception("Could not initialize message digest for SHA-1");
		}
		Set<String> checked = new HashSet<String>();
		while (true) {
			
			Vector<String> reclaimedEprs = new Vector<String>();
			
			try {
				if (!base.exists() || !base.isDirectory()) {
					
					log.warn("Base directory " + RSConstants.baseDirectory
							+ " is not a directory");
				} else {
					File[] dirs = base.listFiles();
					for (int i = 0; i < dirs.length; i++) {
						try {
							if (dirs[i].isDirectory()) {
								File[] files = dirs[i].listFiles();
								for (int q = 0; q < files.length; q++) {
									try {
										log.trace("checking for file " + files[q].toString());
										if (!files[q].exists()) {
											log.trace("doesn't exist aymore");
											continue;
										}
										if (GCHelper.alreadyChecked(files[q].toString(), algorithm,	checked))
											continue;
										if (files[q].isFile() && (files[q].toString().endsWith(RSConstants.pextention) 
												|| files[q].toString().endsWith(RSConstants.textention))) {
											if (GCPolicy.reclaim(files[q])) {
												Arnold.terminate(files[q]);
											}
										} else if (files[q].isFile() && files[q].toString().endsWith(RSConstants.cextention)
																&& !(new File(RSFileHelper.contentToHeader(files[q].toString())).exists())) {
											if (GCPolicy.reclaim(files[q])) {
												Arnold.terminate(files[q]);
											}
										} else if (files[q].isFile()&& files[q].toString().endsWith(RSConstants.hextention)) {
											GCProperties props = new GCProperties();
											props.addToChain(files[q].toString());
											String head = GCHelper.goToHead(files[q].toString(), props);
											String tail = GCHelper.goToLast(files[q].toString(), props);
											GCHelper.headPoperties(head, props);
											GCHelper.tailPoperties(tail, props);
											if (GCPolicy.reclaim(props)) {
												//store the eprs that are being garbage collected
												for(int j=0; j<props.getWSEPRs().size(); j++)
													reclaimedEprs.add(props.getWSEPRs().get(j));
													
												Arnold.terminate(props);
											} else {
												for (int chain = 0; chain < props.getChainOfFiles().size(); chain++) {
													GCHelper.alreadyChecked(props.getChainOfFiles().get(chain),	algorithm, checked);
												}
											}
										}
									} catch (Exception e) {
										log.error("Could not evaluate file. Continuing",e);
									}
								}
							}
						} catch (Exception e) {
							log.error("Could not evaluate directory. Continuing",e);
						}
					}
				}
			} catch (Exception e) {
				log.error("Could not complete iteration. Continuing", e);
			}
			
			try{
				//Construct a Notification Message with all the eprs that were garbage collected and sent a notification
				String[] eprArray = (String[])reclaimedEprs.toArray(new String[reclaimedEprs.size()]);
				if(eprArray != null && eprArray.length > 0){
					ReclaimNotificationMessageType msg = new ReclaimNotificationMessageType();
					msg.setReclaimedEpr(eprArray);
					ReclaimNotificationMessageWrapperType wrapper = new ReclaimNotificationMessageWrapperType(msg);
					reclaimingEprs.notify(wrapper);
					log.info("notification sent for " + eprArray.length + " eprs: ");
					for(int j=0; j<eprArray.length; j++)
						log.info(j + ": " + eprArray[j]);
				}
			} catch(Exception e){
				log.error("Could not send Notification. Continuing", e);
			}
			
			checked.clear();
			try {
				log.info("Garbadge collector sleep");
//				Thread.sleep(15000);
				Thread.sleep(300000);
			} catch (Exception e) {
				log.info("Garbadge collector wake up");
			}
		}
	}

	/**
	 * Run the main thread of the GC
	 */
	public void run() {
		try {
			log.info("Garbadge collector to start in 10");
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
			Thread.sleep(300000);
//			Thread.sleep(15000);
			log.info("Garbadge collector is enabled");
			main(null);
		} catch (Exception e) {
			log.error("Garbadge collector failed to start",e);
		}
	}
}
