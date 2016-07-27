/*
 * Copyright 2007 Shawn Boyce.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.csv4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;


/**
 * Provides an easy to use mechanism to process CSV files.
 * The details of reading and parsing the CSV file along with its header
 * are managed.
 * <p>
 * The API provides a callback processor interface ({@link CSVLineProcessor}) 
 * which is called as the file is processed to receive the CSV header and data lines.
 * A simpler interface, {@link net.sf.csv4j.CSVFieldMapProcessor},
 * provides the field name and values for each line as an easy to access Map.
 * <p>
 * Example usage with CSVLineProcessor:
 * <pre>
 *      final CSVFileProcessor fp = new CSVFileProcessor();
 *      fp.processFile( "data.csv", new CSVLineProcessor() {
 *          public void processHeaderLine( int linenumber, List&lt;String&gt; fields )
 *          {
 *              // use / save the header
 *          }
 *
 *
 *          public void processDataLine( int linenumber, List&lt;String&gt; fields )
 *          {
 *              // use the data
 *          }
 *      } );
 * </pre>
 *
 * <p>
 * Example usage with CSVFieldMapProcessor:
 * <pre>
 *      final CSVFileProcessor fp = new CSVFileProcessor();
 *      fp.processFile( "data.csv", new CSVFieldMapProcessor() {
 *          public void processDataLine( int linenumber, Map&lt;String,String&gt; fields )
 *          {
 *              // use the data
 *          }
 *      } );
 * </pre>
 * @author Shawn Boyce
 * @since 1.0
 */
public class CSVFileReaderProcessor
{
    private final CSVReaderProcessor processor = new CSVReaderProcessor();

    /** Constructor. */
    public CSVFileReaderProcessor()
    {
    }


    /**
     * Returns the comment setting.
     * @return comment setting.
     */
    public char getComment()
    {
        return processor.getComment();
    }


    /**
     * Sets the comment string value.
     * @param comment the new comment string value
     */
    public void setComment( final char comment )
    {
        processor.setComment( comment );
    }
    
    /**
	 * @return the delimiter
	 */
	public char getDelimiter() {
		return processor.getDelimiter();
	}


	/**
	 * @param delimiter the delimiter to set
	 */
	public void setDelimiter(char delimiter) {
		this.processor.setDelimiter(delimiter);
	}


    /**
     * Indicates if the file has a header line.
     * @return true if header line expected; false otherwise.
     */
    public boolean isHasHeader()
    {
        return processor.isHasHeader();
    }

    /**
     * Sets the hasHeader value. 
     * @param hasHeader true if file has a header line; false if not
     */
    public void setHasHeader( final boolean hasHeader )
    {
        processor.setHasHeader( hasHeader );
    }


    /**
     * Processes the CSV file using the provided processor
     * @param filename name of CSV file
     * @param processor handler to process the CSV lines
     * @return number of lines processed
     * @throws FileNotFoundException if file not found
     * @throws IOException if a read error occurs
     * @throws ProcessingException if an exception is thrown by the processor
     * @throws ParseException if a error occurs parsing the CSV line
     */
    public int processFile( final String filename, Charset charset, final CSVLineProcessor processor )
            throws IOException, ProcessingException, ParseException
    {
        return this.processor.processStream( new InputStreamReader(new FileInputStream(filename), charset), processor );
    }


    /**
     * Processes the CSV file using the provided processor.
     * Notes:
     * <ol>
     * <li>CSV file must have a header line (hasHeader must be true)</li>
     * <li>if a data line has fewer columns than the header line, the missing
     * columns will be blanks
     * </ol>
     * @param filename name of CSV file
     * @param processor handler to process the CSV lines
     * @return number of lines processed
     * @throws FileNotFoundException if file not found
     * @throws IOException if a read error occurs
     * @throws ProcessingException if an exception is thrown by the processor
     * @throws ParseException if a error occurs parsing the CSV line
     */
    public int processFile( final String filename, Charset charset, final CSVFieldMapProcessor processor )
            throws IOException, ProcessingException, ParseException
    {
        return processFile( filename, charset,new LineToFieldMapAdapter( processor ) );
    }

    /**
     * Processes the CSV file using the provided processor.
     * Notes:
     * <ol>
     * <li>CSV file must have a header line (hasHeader must be true)</li>
     * <li>if a data line has fewer columns than the header line, the missing
     * columns will be blanks
     * </ol>
     * @param filename name of CSV file
     * @param processor handler to process the CSV lines
     * @return number of lines processed
     * @throws FileNotFoundException if file not found
     * @throws IOException if a read error occurs
     * @throws ProcessingException if an exception is thrown by the processor
     * @throws ParseException if a error occurs parsing the CSV line
     */
    public int processFile( final String filename, Charset charset,final CSVSortedFieldMapProcessor processor )
            throws IOException, ProcessingException, ParseException
    {
        return processFile( filename,charset, new LineToSortedFieldMapAdapter( processor ) );
    }

}
