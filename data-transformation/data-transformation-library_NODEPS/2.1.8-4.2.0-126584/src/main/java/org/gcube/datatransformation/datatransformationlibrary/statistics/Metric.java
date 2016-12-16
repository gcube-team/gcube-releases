package org.gcube.datatransformation.datatransformationlibrary.statistics;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.gcube.datatransformation.datatransformationlibrary.PropertiesManager;

/**
 * @author Dimitris Katris, NKUA
 * 
 * <tt>Metric</tt> class maintains measures for a specific operation performed by the <tt>DTS</tt>.
 */
public class Metric {
	private String name;
	private String description;

	/** The maximum number of measures to take into account */
	private int maxNumMeasures = PropertiesManager.getIntPropertyValue("statistics.maxnummeasures", "100");
	
	/** The list of measures for a metric */
	private Queue<Long> measures = new LinkedList<Long>();
	
	/** The minimum measured value for a metric */
	private long minMeasure = Long.MAX_VALUE;
	
	/** The maximum measured value for a metric */
	private long maxMeasure = Long.MIN_VALUE;
	
	/** The sum of different measured values for a metric */
	private long sumOfMeasures = 0;
	
	/** The number of different measured values for a metric */ 
	private long numMeasures = 0;
	
	/**
	 * Instantiates a new <tt>Metric</tt>.
	 * 
	 * @param name The name of the <tt>Metric</tt>.
	 * @param description The description of the <tt>Metric</tt>.
	 */
	public Metric(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	/**
	 * Instantiates a new <tt>Metric</tt>.
	 * 
	 * @param name The name of the <tt>Metric</tt>.
	 * @param description The description of the <tt>Metric</tt>.
	 * @param maxNumMeasures The maximum number of measures that will be taken for this <tt>Metric</tt>.
	 */
	public Metric(String name, String description, int maxNumMeasures) {
		this.name = name;
		this.description = description;
		this.maxNumMeasures=maxNumMeasures;
	}

	/**
	 * Returns the description of the <tt>Metric</tt>.
	 * 
	 * @return The description of the <tt>Metric</tt>.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of the <tt>Metric</tt>.
	 * 
	 * @param description The description of the <tt>Metric</tt>.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the name of the <tt>Metric</tt>..
	 * @return The name of the <tt>Metric</tt>.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the <tt>Metric</tt>.
	 * @param name The name of the <tt>Metric</tt>.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Adds a measure to this <tt>Metric</tt>.
	 * 
	 * @param measuredVal The value of the measure.
	 */
	public void addMeasure(Long measuredVal){
		if (measuredVal < minMeasure) {
			minMeasure = measuredVal;
		} 
		if (measuredVal > maxMeasure) {
			maxMeasure = measuredVal;
		}

		if (numMeasures < maxNumMeasures) {
			sumOfMeasures += measuredVal;
			numMeasures++;
		} else {
			sumOfMeasures = sumOfMeasures - measures.poll() + measuredVal;
		}
		measures.add(measuredVal);
	}
	
	
	/**
	 * Returns the minimum measured value for the metric
	 * 
	 * @return the min value
	 */
	public long getMinMeasure() {
		if (numMeasures == 0)
			return 0;
		return minMeasure;
	}
	
	/**
	 * Returns the maximum measured value for the metric
	 * 
	 * @return the max value
	 */
	public long getMaxMeasure() {
		if (numMeasures == 0)
			return 0;
		return maxMeasure;
	}

	/**
	 * Returns the mean value of all the measured values
	 *  
	 * @return the mean value
	 */
	public long getMeanOfMeasures() {
		if (numMeasures == 0)
			return 0;
		else
			return (sumOfMeasures / numMeasures);
	}
	
	/**
	 * Resets the counting of the statistics of this <tt>Metric</tt>.
	 */
	public void reset(){
		this.measures.clear();
		this.minMeasure = Long.MAX_VALUE;
		this.maxMeasure = Long.MIN_VALUE;
		this.sumOfMeasures = 0;
		this.numMeasures = 0;
	}

	/**
	 * Returns the mean value of a <tt>Collection</tt> of <tt>Metrics</tt>.
	 * 
	 * @param metrics The <tt>Collection</tt> of <tt>Metrics</tt>.
	 * @return The mean value of these <tt>Metrics</tt>.
	 */
	public static long getMeanValueOfMetrics(Collection<Metric> metrics){
		long totalNumOfMeasures = 0;
		long totalMeanValue=0;
		Iterator<Metric> it = metrics.iterator();
		while(it.hasNext()){
			Metric metric = it.next();
			totalNumOfMeasures += metric.numMeasures;
		}
		if(totalNumOfMeasures==0){
			return 0;
		}
		Iterator<Metric> it2 = metrics.iterator();
		while(it2.hasNext()){
			Metric metric = it2.next();
			totalMeanValue += metric.getMeanOfMeasures()*metric.numMeasures/totalNumOfMeasures;
		}
		return totalMeanValue;
	}
}
