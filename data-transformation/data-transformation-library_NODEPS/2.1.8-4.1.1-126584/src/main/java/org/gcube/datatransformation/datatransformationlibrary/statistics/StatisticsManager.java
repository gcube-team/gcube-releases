package org.gcube.datatransformation.datatransformationlibrary.statistics;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * @author Dimitris Katris, NKUA
 * 
 * <tt>StatisticsManager</tt> is responsible to maintain statistics for each operation performed by the <tt>Data Transformation Service</tt>.
 */
public class StatisticsManager {

	/**
	 * @author Dimitris Katris, NKUA
	 * 
	 * <p>There are four distinguishable types of metrics.</p>
	 * <li>DTS - Type of metric for internal operations of <tt>DTS</tt></li>
	 * <li>SOURCE - Type of metric used by <tt>Data Sources</tt></li>
	 * <li>TRANSFORMER - Type of metric used by <tt>Programs</tt></li>
	 * <li>SINK - Type of metric used by <tt>Data Sinks</tt></li> 
	 */
	public enum MetricType{
		/**
		 * The types of metrics.
		 */
		DTS, SOURCE, TRANSFORMER, SINK
	}
	
	private static HashMap<MetricType, HashMap<String, Metric>> metricsByType = new HashMap<MetricType, HashMap<String,Metric>>();
	private static HashMap<String, Metric> dtsSourceMetrics = new HashMap<String, Metric>();
	private static HashMap<String, Metric> dataSourceMetrics = new HashMap<String, Metric>();
	private static HashMap<String, Metric> transformationMetrics = new HashMap<String, Metric>();
	private static HashMap<String, Metric> dataSinkMetrics = new HashMap<String, Metric>();
	
	static {
		metricsByType.put(MetricType.DTS, dtsSourceMetrics);
		metricsByType.put(MetricType.SOURCE, dataSourceMetrics);
		metricsByType.put(MetricType.TRANSFORMER, transformationMetrics);
		metricsByType.put(MetricType.SINK, dataSinkMetrics);
	}
	
	/**
	 * Returns the <tt>Metric</tt> by its name and <tt>MetricType</tt>.
	 * 
	 * @param name The name of the <tt>Metric</tt>. 
	 * @param mType The type of the <tt>Metric</tt>.
	 * @return The <tt>Metric</tt>.
	 */
	public static Metric getMetric(String name, MetricType mType){
		return metricsByType.get(mType).get(name);
	}
	
	/**
	 * <p>Creates a new <tt>Metric</tt>.</p>
	 * <p>Synchronization is applicable only for the iterators which are read in {@link StatisticsManager#toXML()}.</p>
	 * 
	 * @param name The name of the new <tt>Metric</tt>.
	 * @param description A description of the new <tt>Metric</tt>.
	 * @param mType The type of the new <tt>Metric</tt>.
	 * @return The new <tt>Metric</tt>.
	 */
	synchronized public static Metric createMetric(String name, String description, MetricType mType){
		Metric metric = new Metric(name, description);
		metricsByType.get(mType).put(name, metric);
		return metric;
	}
	
	/**
	 * <p>Creates a new <tt>Metric</tt> with a specific maximum number of measures.</p>
	 * <p>Synchronization is applicable only for the iterators which are read in {@link StatisticsManager#toXML()}.</p>
	 * 
	 * @param name The name of the new <tt>Metric</tt>.
	 * @param description A description of the new <tt>Metric</tt>.
	 * @param mType The type of the new <tt>Metric</tt>.
	 * @param maxNumMeasures The maximum number of measures that will be taken for this <tt>Metric</tt>.
	 * @return The new <tt>Metric</tt>.
	 */
	synchronized public static Metric createMetric(String name, String description, MetricType mType, int maxNumMeasures){
		Metric metric = new Metric(name, description, maxNumMeasures);
		metricsByType.get(mType).put(name, metric);
		return metric;
	}
	
	/**
	 * <p>Returns all metrics of one {@link MetricType}.</p>
	 * <p>Accesses to the returned {@link HashMap} shall be synchronized on lock of the <tt>StatisticsManager</tt> instance.</p> 
	 * 
	 * @param mType The type of the metrics.
	 * @return The metrics.
	 */
	public static HashMap<String, Metric> getAllMetricsOfType(MetricType mType){
		return metricsByType.get(mType);
	}
	
	/**
	 * Converts the statistics to a an <tt>XML</tt> format.
	 * 
	 * @return The <tt>XML</tt> representation of the statistics.
	 */
	synchronized public static String toXML() {
		StringBuilder buf = new StringBuilder();
		buf.append("<Statistics>");
		buf.append("<text>");
		buf.append("This section summarizes some statistical information about the service");
		buf.append("</text>");
		buf.append("<DTS>");
		for (Entry<String, Metric> metricEntry : dtsSourceMetrics.entrySet()) {
			String metricName = metricEntry.getKey();
			Metric metric = metricEntry.getValue();
			
			buf.append("<");
			buf.append(metricName);
			buf.append(">");
			buf.append("<description>");
			buf.append(metric.getDescription());
			buf.append("</description>");
			buf.append("<MinValue>");
			buf.append(metric.getMinMeasure());
			buf.append("</MinValue>");
			buf.append("<MaxValue>");
			buf.append(metric.getMaxMeasure());
			buf.append("</MaxValue>");
			buf.append("<MeanValue>");
			buf.append(metric.getMeanOfMeasures());
			buf.append("</MeanValue>");
			buf.append("</");
			buf.append(metricName);
			buf.append(">");			
		}
		buf.append("</DTS>");
		buf.append("<DataSources>");
		for (Entry<String, Metric> metricEntry : dataSourceMetrics.entrySet()) {
			String metricName = metricEntry.getKey();
			Metric metric = metricEntry.getValue();
			
			buf.append("<");
			buf.append(metricName);
			buf.append(">");
			buf.append("<description>");
			buf.append(metric.getDescription());
			buf.append("</description>");
			buf.append("<MinValue>");
			buf.append(metric.getMinMeasure());
			buf.append("</MinValue>");
			buf.append("<MaxValue>");
			buf.append(metric.getMaxMeasure());
			buf.append("</MaxValue>");
			buf.append("<MeanValue>");
			buf.append(metric.getMeanOfMeasures());
			buf.append("</MeanValue>");
			buf.append("</");
			buf.append(metricName);
			buf.append(">");			
		}
		buf.append("</DataSources>");
		buf.append("<Transformers>");
		for (Entry<String, Metric> metricEntry : transformationMetrics.entrySet()) {
			String metricName = metricEntry.getKey();
			Metric metric = metricEntry.getValue();
			
			buf.append("<");
			buf.append(metricName);
			buf.append(">");
			buf.append("<description>");
			buf.append(metric.getDescription());
			buf.append("</description>");
			buf.append("<MinValue>");
			buf.append(metric.getMinMeasure());
			buf.append("</MinValue>");
			buf.append("<MaxValue>");
			buf.append(metric.getMaxMeasure());
			buf.append("</MaxValue>");
			buf.append("<MeanValue>");
			buf.append(metric.getMeanOfMeasures());
			buf.append("</MeanValue>");
			buf.append("</");
			buf.append(metricName);
			buf.append(">");			
		}
		buf.append("</Transformers>");
		buf.append("<DataSinks>");
		for (Entry<String, Metric> metricEntry : dataSinkMetrics.entrySet()) {
			String metricName = metricEntry.getKey();
			Metric metric = metricEntry.getValue();
			
			buf.append("<");
			buf.append(metricName);
			buf.append(">");
			buf.append("<description>");
			buf.append(metric.getDescription());
			buf.append("</description>");
			buf.append("<MinValue>");
			buf.append(metric.getMinMeasure());
			buf.append("</MinValue>");
			buf.append("<MaxValue>");
			buf.append(metric.getMaxMeasure());
			buf.append("</MaxValue>");
			buf.append("<MeanValue>");
			buf.append(metric.getMeanOfMeasures());
			buf.append("</MeanValue>");
			buf.append("</");
			buf.append(metricName);
			buf.append(">");			
		}
		buf.append("</DataSinks>");
		buf.append("</Statistics>");
		
		return buf.toString();
	}
}
