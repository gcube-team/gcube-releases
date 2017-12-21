package org.gcube.dataanalysis.ecoengine.signals;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.gcube.contentmanagement.graphtools.utils.DateGuesser;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.utils.Operations;
import org.gcube.dataanalysis.ecoengine.utils.Tuple;

public class TimeSeries {

	private double[] values;
	private Date[] times;
	private Date[] unsortedtimes;
	private String[] timeLabels;
	private long minimumtimegap = -1;
	private String timepattern;
	
	AlgorithmConfiguration config;

	public TimeSeries(int timeLength, AlgorithmConfiguration config) {
		values = new double[timeLength];
		times = new Date[timeLength];
		unsortedtimes = new Date[timeLength];
		timeLabels = new String[timeLength];
		this.config = config;
	}

	public TimeSeries(double[] values, Date[] time, String[] timeLabels, AlgorithmConfiguration config) {
		this.values = values;
		this.times = time;
		this.unsortedtimes = Arrays.copyOf(time, time.length);
		this.timeLabels = timeLabels;
		this.config = config;
	}

	public void setValues(double[] values) {
		this.values = values;
	}

	public void setTime(Date[] time) {
		this.times = time;
	}

	public void setTimeLabels(String[] timeLabels) {
		this.timeLabels = timeLabels;
	}

	public double[] getValues() {
		return values;
	}

	public String[] getLabels() {
		return timeLabels;
	}

	public Date[] getTime() {
		return times;
	}
	
	
	public Date[] extendTime(int furtherPointsinTime){
		Date[] time = new Date[times.length+furtherPointsinTime];
		for (int i=0;i<times.length;i++){
			time[i]=times[i];
		}
		
		long lastDate = times[times.length-1].getTime();
		
		for (int i=times.length;i<(times.length+furtherPointsinTime);i++){
			time[i]=new Date(lastDate+(i+1-times.length)*minimumtimegap);
		}
		
		return time;
	}

	public double[] getMillisecondsTimeline() {
		double[] secondstimes = new double[times.length];
		for (int i = 0; i < times.length; i++) {
			long t = times[i].getTime();
			secondstimes[i] = (double) t;
		}
		return secondstimes;
	}

	public long getMimimumTimeGapInMillisecs() {
		if (minimumtimegap > -1)
			return minimumtimegap;

		long mintime = Long.MAX_VALUE;
		for (int i = 1; i < times.length; i++) {
			long t0 = times[i - 1].getTime();
			long t1 = times[i].getTime();
			long timediff = Math.abs(t1 - t0);
			if (timediff < mintime && timediff > 0)
				mintime = timediff;
		}
		minimumtimegap = mintime;
		return mintime;
	}

	public void addElement(double value, Date date, String label, int index) {
		values[index] = value;
		times[index] = date;
		unsortedtimes[index] = date;
		timeLabels[index] = label;
	}

	public void sort() {
		Arrays.sort(times);
		double[] tempvalues = new double[values.length];
		String[] temptimeLabels = new String[timeLabels.length];
		List<Date> unsortedTimesList = Arrays.asList(unsortedtimes);
		int i = 0;
		for (Date time : times) {
			int index = unsortedTimesList.indexOf(time);
			tempvalues[i] = values[index];
			temptimeLabels[i] = timeLabels[index];
			i++;
		}
		values = null;
		timeLabels = null;
		values = tempvalues;
		timeLabels = temptimeLabels;
		unsortedtimes = Arrays.copyOf(times, times.length);
	}

	public double getValue(int index) {
		return values[index];
	}

	public Date getDate(int index) {
		return times[index];
	}

	public String getTimeLabel(int index) {
		return timeLabels[index];
	}

	// each element in the list is Time,Quantity
	public static TimeSeries buildFromSignal(List<Tuple<String>> lines, AlgorithmConfiguration config) throws Exception {
		TimeSeries ts = new TimeSeries(lines.size(), config);
		int counter = 0;
		String timepattern = null;
		SimpleDateFormat sdf = null;
		for (Tuple<String> line : lines) {

			String timel = line.getElements().get(0);
			timel = timel.replace("time:", "");
			Double quantity = Double.parseDouble(line.getElements().get(1));

			Date time = null;

			if (counter == 0) {
				timepattern = DateGuesser.getPattern(timel);
				ts.setTimepattern(timepattern);
				AnalysisLogger.getLogger().debug("Time pattern: " + timepattern);
				sdf = new SimpleDateFormat(timepattern, Locale.ENGLISH);
			}
			try{
				time = (Date) sdf.parse(timel);
			}catch(Exception e){
				AnalysisLogger.getLogger().debug("Error in parsing...adjusting "+timel);
				time = DateGuesser.convertDate(timel).getTime();
				AnalysisLogger.getLogger().debug("Error in parsing...adjusting "+timel+" in "+time);
			}
			
			if (counter == 0) {
				AnalysisLogger.getLogger().debug("Date detection: input " + timel + " output " + time);
			}

			ts.addElement(quantity, time, timel, counter);
			counter++;

		}
		
		ts.sort();
		
		return ts;
	}

	public void convertToUniformSignal(double samplingrate) throws Exception {
		if (samplingrate <= 0) {
			if (minimumtimegap < 0)
				getMimimumTimeGapInMillisecs();
			if (minimumtimegap > 0)
				samplingrate = 1d / (double) minimumtimegap;
		}

		AnalysisLogger.getLogger().debug("TimeSeries->Samplig rate: " + samplingrate + " minimum gap in time: " + minimumtimegap);
		if (samplingrate == 0)
			return;

		double[] timeline = getMillisecondsTimeline();
		AnalysisLogger.getLogger().debug("TimeSeries->filling gaps");
		double[] newvalues = SignalProcessing.fillTimeSeries(values, timeline, samplingrate, config);

		if (newvalues.length != values.length) {
			AnalysisLogger.getLogger().debug("TimeSeries->filling also time values");
			Date[] newtimeline = SignalProcessing.fillTimeLine(timeline, samplingrate, config);

			values = null;
			times = null;
			unsortedtimes = null;
			values = newvalues;
			times = newtimeline;
			unsortedtimes = newtimeline;
			timeLabels = new String[times.length];
		}

		AnalysisLogger.getLogger().debug("TimeSeries->Returning values");
		timeLabels = new String[times.length];
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ROOT);
		for (int i = 0; i < times.length; i++) {
			timeLabels[i] = sdf.format(times[i]);
		}

	}

	public void normalize() throws Exception {
		double max = Operations.getMax(values);
		for (int i = 0; i < values.length; i++) {
			values[i] = values[i] / max;
		}
	}

	public String getTimepattern() {
		return timepattern;
	}

	public void setTimepattern(String timepattern) {
		this.timepattern = timepattern;
	}
}
