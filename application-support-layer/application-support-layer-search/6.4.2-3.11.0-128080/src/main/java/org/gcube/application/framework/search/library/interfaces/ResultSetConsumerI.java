package org.gcube.application.framework.search.library.interfaces;



import gr.uoa.di.madgik.grs.buffer.GRS2BufferException;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderInvalidArgumentException;
import gr.uoa.di.madgik.grs.record.GRS2RecordDefinitionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.gcube.application.framework.contentmanagement.content.impl.DigitalObject;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.search.library.exception.InitialBridgingNotCompleteException;
import org.gcube.application.framework.search.library.exception.InternalErrorException;
import org.gcube.application.framework.search.library.exception.gRS2AvailableRecordsRetrievalException;
import org.gcube.application.framework.search.library.exception.gRS2BufferException;
import org.gcube.application.framework.search.library.exception.gRS2NoRecordReadWithinTimeIntervalException;
import org.gcube.application.framework.search.library.exception.gRS2ReaderException;
import org.gcube.application.framework.search.library.exception.gRS2RecordDefinitionException;
import org.gcube.application.framework.search.library.util.DisableButtons;

public interface ResultSetConsumerI {
	
	
	public List<Properties> getFirstRaw(int n, DisableButtons dis, ASLSession session) throws gRS2NoRecordReadWithinTimeIntervalException, gRS2ReaderException, gRS2AvailableRecordsRetrievalException, GRS2RecordDefinitionException, GRS2BufferException;
	
	/**
	 * 
	 * @param n
	 * @param dis
	 * @param session
	 * @return
	 * @throws gRS2NoRecordReadWithinTimeIntervalException
	 * @throws gRS2RecordDefinitionException
	 * @throws gRS2ReaderException
	 * @throws gRS2AvailableRecordsRetrievalException
	 * @throws InitialBridgingNotCompleteException 
	 * @throws InternalErrorException 
	 */
	public List<DigitalObject> getFirst(int n, DisableButtons dis, ASLSession session) throws gRS2NoRecordReadWithinTimeIntervalException, gRS2RecordDefinitionException, gRS2ReaderException, gRS2AvailableRecordsRetrievalException, InitialBridgingNotCompleteException, InternalErrorException;

	
	public List<Properties> getNextRaw(int n, DisableButtons dis, ASLSession session) throws gRS2NoRecordReadWithinTimeIntervalException, GRS2RecordDefinitionException, GRS2BufferException;
	
	/**
	 * 
	 * @param n
	 * @param dis
	 * @param session
	 * @return
	 * @throws gRS2NoRecordReadWithinTimeIntervalException
	 * @throws gRS2RecordDefinitionException
	 * @throws gRS2ReaderException
	 * @throws gRS2AvailableRecordsRetrievalException
	 * @throws InitialBridgingNotCompleteException 
	 * @throws InternalErrorException 
	 */
	public List<DigitalObject> getNext(int n, DisableButtons dis, ASLSession session) throws gRS2NoRecordReadWithinTimeIntervalException, gRS2RecordDefinitionException, gRS2ReaderException, gRS2AvailableRecordsRetrievalException, InitialBridgingNotCompleteException, InternalErrorException;

	/**
	 * 
	 * @param n
	 * @param dis
	 * @param session
	 * @return
	 * @throws gRS2NoRecordReadWithinTimeIntervalException
	 * @throws gRS2RecordDefinitionException
	 * @throws gRS2ReaderException
	 * @throws gRS2AvailableRecordsRetrievalException
	 * @throws InitialBridgingNotCompleteException 
	 * @throws InternalErrorException 
	 */
	public List<DigitalObject> getPrevious(int n, DisableButtons dis, ASLSession session) throws gRS2NoRecordReadWithinTimeIntervalException, gRS2RecordDefinitionException, gRS2ReaderException, gRS2AvailableRecordsRetrievalException, InitialBridgingNotCompleteException, InternalErrorException;
	
	
	/**
	 * 
	 * @param n
	 * @param offset
	 * @param session
	 * @return
	 * @throws gRS2ReaderException
	 * @throws gRS2RecordDefinitionException
	 * @throws gRS2BufferException
	 */
	public List<String> getResultsToText (int n, int offset, ASLSession session) throws gRS2ReaderException, gRS2RecordDefinitionException, gRS2BufferException;
	
	/**
	 * 
	 * @param gst
	 */
	public abstract void setGenericSearchType (String gst);
	
	public int getNumOfResultsRead ();
	
	public boolean getTotalRead();
	
	public ArrayList<DigitalObject> getAllResultIds(ASLSession session);
	
	public void setOnlyPresentables();
	
	public void setWindowSize(int size) throws GRS2ReaderInvalidArgumentException;

	public void setSearchStartTime(long timeMillis);
	
	public void setOnlyTitleSnippet (boolean onlyTS);
	
	public boolean advanceReaderBy(int numOfResults);

}
