package org.gcube.common.searchservice.searchlibrary.GarbageCollector;


import java.io.File;
import java.util.Calendar;

import org.apache.log4j.Logger;

/**
 * Defines the policy of the Garbage collector. Desides whether a {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
 * should be reclaimed based on its properties
 * 
 * @author UoA
 */
public class GCPolicy {

	private static Logger log = Logger.getLogger(GCPolicy.class);
	/**
	 * Constant used
	 */
	private static final short TRUE=0;
	/**
	 * Constant used
	 */
	private static final short FALSE=1;
	/**
	 * Constant used
	 */
	private static final short UNDEF=2;
	/**
	 * Constant used
	 */
	private static final long defaultTime=3600000;
//	private static final long defaultTime=60000;
	
	/**
	 * Desides whether a {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * should be reclaimed based on its properties 
	 * 
	 * @param props The properties of the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * @return <code>true</code> if the resources must be reclaimed, <code>false</code> otherwise
	 */
	public static boolean reclaim(GCProperties props){
		long now=Calendar.getInstance().getTimeInMillis();
		short lsPolicy=GCPolicy.lifeSpanPolicy(now,props);
		short ssPolicy=GCPolicy.ssPolicy(props);
		short defPolicy=GCPolicy.defaultPolicy(now,props);
		log.debug("lsPolicy: "+lsPolicy+" ssPolicy: "+ssPolicy+" defPolicy: "+defPolicy);
		log.debug("last access : "+props.getLastAccessed());
		if (GCPolicy.prioritize(lsPolicy,ssPolicy,defPolicy)==GCPolicy.TRUE) return true;
		return false;
	}
	
	/**
	 * Orderes the various policies and makes a desicion
	 * 
	 * @param lifespanPolicy the lifespan policy
	 * @param ssPolicy the ss policy
	 * @param defaultPolicy the default policy
	 * @return the choise
	 */
	private static short prioritize(short lifespanPolicy, short ssPolicy, short defaultPolicy ){
		if(ssPolicy!=GCPolicy.UNDEF) return ssPolicy;
		if(lifespanPolicy!=GCPolicy.UNDEF) return lifespanPolicy;
		return defaultPolicy;
	}
	
	/**
	 * Desides wheteher a single file shoulf be replaced based on its last modification time
	 * 
	 * @param file The file to check
	 * @return <code>true</code> if the resource must be reclaimed, <code>false</code> otherwise
	 */
	public static boolean reclaim(File file){
		if(file.lastModified()+GCPolicy.defaultTime < Calendar.getInstance().getTimeInMillis()) return true;
		return false;
	}
	
	/**
	 * Implements the policy conserning lifespan
	 * 
	 * @param now The current time in millisecs
	 * @param props The properties of the RS beeing evaluated
	 * @return <code>{@link GCPolicy#TRUE}</code> if thew RS should be claimed, 
	 * <code>{@link GCPolicy#FALSE}</code> if not, <code>{@link GCPolicy#UNDEF}</code> 
	 * if a desicion cannot be made
	 */
	private static short lifeSpanPolicy(long now,GCProperties props){
		long timeSinceAccess=now-props.getLastAccessed();
		long timeSinceAuthor=now-props.getLastAuthored();
		long lifeSpan=props.getMaxLifeSpan();
		if(lifeSpan==0) return GCPolicy.UNDEF;
		if(timeSinceAccess>lifeSpan && timeSinceAuthor>lifeSpan) return GCPolicy.TRUE;
		return GCPolicy.FALSE;
	}
	
	/**
	 * Implements the policy conserning SS
	 * 
	 * @param props The properties of the RS beeing evaluated
	 * @return <code>{@link GCPolicy#TRUE}</code> if thew RS should be claimed, 
	 * <code>{@link GCPolicy#FALSE}</code> if not, <code>{@link GCPolicy#UNDEF}</code> 
	 * if a desicion cannot be made
	 */
	private static short ssPolicy(GCProperties props){
		return GCPolicy.UNDEF;
	}
	
	/**
	 * Implements the default policy
	 * 
	 * @param now The current time in millisecs
	 * @param props The properties of the RS beeing evaluated
	 * @return <code>{@link GCPolicy#TRUE}</code> if thew RS should be claimed, 
	 * <code>{@link GCPolicy#FALSE}</code> if not, <code>{@link GCPolicy#UNDEF}</code> 
	 * if a desicion cannot be made
	 */
	private static short defaultPolicy(long now,GCProperties props){
		long timeSinceAccess=now-props.getLastAccessed();
		long timeSinceAuthor=now-props.getLastAuthored();
		if(timeSinceAccess>GCPolicy.defaultTime && timeSinceAuthor>GCPolicy.defaultTime) return GCPolicy.TRUE;
		return GCPolicy.FALSE;
	}
}
