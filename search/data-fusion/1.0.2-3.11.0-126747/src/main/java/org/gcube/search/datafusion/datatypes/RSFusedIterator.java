package org.gcube.search.datafusion.datatypes;

import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderException;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.GenericRecord;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gcube.search.datafusion.helpers.RecordHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * Fuses multiple iterators into one. Since it operates over {@link GenericRecord}s sorting is performed during the fusion
 * based on their rank (if no rank is specified the record is consider as high-ranked)
 * 
 * @author Alex Antoniadis
 * 
 */
public class RSFusedIterator implements Iterator<PositionalRecordWrapper>, Serializable {
	private static final long serialVersionUID = 1L;
	private List<Iterator<GenericRecord>> iters;
	private GenericRecord vals[];
	private int ranked = 0;
	private long counts[];
	private final int n;
	private int prevMaxIdx = -1;
	private long recsProduced = 0;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RSFusedIterator.class);

	public static Iterator<GenericRecord> locatorToIterator(URI locator) throws GRS2ReaderException {
		IRecordReader<GenericRecord> reader = new ForwardReader<GenericRecord>(locator);
		LOGGER.info("getting iterator for locator : " + locator.toString());
		
		return reader.iterator();
	}

	/**
	 * Converts an array on locators into a list of iterators over those locators
	 * 
	 * @param locators
	 * @return list of iterators
	 * @throws GRS2ReaderException
	 */
	public static List<Iterator<GenericRecord>> locatorsToItersList(URI[] locators) throws GRS2ReaderException {
		LOGGER.info("getting iterators for locators : ");
		LOGGER.info("---------------------------------");
		List<Iterator<GenericRecord>> iters = new ArrayList<Iterator<GenericRecord>>();
		for (URI loc : locators) 
			iters.add(locatorToIterator(loc));
		
		LOGGER.info("---------------------------------");
		return iters;
	}

	public RSFusedIterator(URI[] locators) throws GRS2ReaderException {
		this(locators, -1);
	}

	public RSFusedIterator(URI[] locators, int n) throws GRS2ReaderException {
		this(locatorsToItersList(locators), n);
	}

	public RSFusedIterator(List<Iterator<GenericRecord>> streamIters) {
		this(streamIters, -1);
	}

	public RSFusedIterator(List<Iterator<GenericRecord>> streamIters, int n) {
		this.iters = streamIters;
		this.vals = new GenericRecord[streamIters.size()];
		this.counts = new long[streamIters.size()];
		this.n = n;
		
		LOGGER.info("Initializing Fused Iterator for : " + streamIters.size() + " iterators and n :" + n);

		for (int i = 0; i != streamIters.size(); ++i) {
			vals[i] = streamIters.get(i).hasNext() ? streamIters.get(i).next() : null;
			counts[i] = 0;
		}
	}


	public boolean hasNext() {
		if (this.ranked == this.n)
			return false;

		for (int i = 0; i != this.vals.length; ++i)
			if (i != this.prevMaxIdx && this.vals[i] != null)
				return true;

		//if there is no iterator available
		if (this.prevMaxIdx == -1)
			return false;
		
		return this.iters.get(this.prevMaxIdx).hasNext();
	}

	public PositionalRecordWrapper next() {
		if (this.prevMaxIdx != -1)
			this.vals[this.prevMaxIdx] = this.iters.get(this.prevMaxIdx).hasNext() ? this.iters.get(this.prevMaxIdx).next() : null;

		GenericRecord res = null;
		Float max = Float.valueOf(-1);
		int maxIdx = -1;

		for (int i = 0; i != this.vals.length; ++i) {
			LOGGER.info("checking : " + this.vals[i] + " i : " + i + " , vals length : " + vals.length);
			if (this.vals[i] != null) {
				Float rank = RecordHelper.getRank(this.vals[i]);
				LOGGER.info("rank : " + rank);
				//we consider null as the highest rank
				if (rank == null || (max != null && rank > max )) {
					max = rank;
					maxIdx = i;
					res = this.vals[i];
				}
			}
		}

		if (max != null)
			this.ranked++;

		// if (iters.get(maxIdx).hasNext())
		// vals[maxIdx] = iters.get(maxIdx).next();
		// else {
		// vals[maxIdx] = null;
		// iters.get(maxIdx).remove();
		// }

		// vals[maxIdx] = iters.get(maxIdx).hasNext() ? iters.get(maxIdx).next()
		// : null;
		this.counts[maxIdx]++;
		this.prevMaxIdx = maxIdx;

		PositionalRecordWrapper prw = new PositionalRecordWrapper(this.counts[maxIdx], res);

		this.recsProduced++;
		LOGGER.trace("records produced : " + this.recsProduced);
		LOGGER.trace("records ranked   : " + this.ranked);
		
		return prw;
	}

	public void remove() {
	}

}
