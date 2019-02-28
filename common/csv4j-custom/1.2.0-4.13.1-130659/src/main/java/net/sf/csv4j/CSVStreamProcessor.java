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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


/**
 * Provides an easy to use mechanism to process CSV streams.
 * The details of reading and parsing the CSV stream along with its header
 * are managed.
 * <p>
 * The API provides a callback processor interface ({@link net.sf.csv4j.CSVLineProcessor})
 * which is called as the stream is processed to receive the CSV header and data lines.
 * A simpler interface, {@link CSVFieldMapProcessor},
 * provides the field name and values for each line as an easy to access Map.
 * <p>
 * Example usage with CSVLineProcessor:
 * <pre>
 *      final CSVStreamProcessor fp = new CSVStreamProcessor();
 *      fp.processStream( "data.csv", new CSVLineProcessor() {
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
 *      final CSVStreamProcessor fp = new CSVStreamProcessor();
 *      fp.processStream( "data.csv", new CSVFieldMapProcessor() {
 *          public void processDataLine( int linenumber, Map&lt;String,String&gt; fields )
 *          {
 *              // use the data
 *          }
 *      } );
 * </pre>
 * @author Shawn Boyce
 * @since 1.0
 */
public class CSVStreamProcessor
{
    private char comment = CSVConstants.DEFAULT_COMMENT;
    private boolean hasHeader = true;

    /** Constructor. */
    public CSVStreamProcessor()
    {
    }


    /**
     * Returns the comment setting.
     * @return comment setting.
     */
    public char getComment()
    {
        return comment;
    }


    /**
     * Sets the comment string value.
     * @param comment the new comment string value
     */
    public void setComment( final char comment )
    {
        this.comment = comment;
    }


    /**
     * Indicates if the file has a header line.
     * @return true if header line expected; false otherwise.
     */
    public boolean isHasHeader()
    {
        return hasHeader;
    }

    /**
     * Sets the hasHeader value.
     * @param hasHeader true if file has a header line; false if not
     */
    public void setHasHeader( final boolean hasHeader )
    {
        this.hasHeader = hasHeader;
    }


    /**
     * Processes the CSV file using the provided processor.
     * @param is stream to read CSV from
     * @param processor handler to process the CSV lines
     * @return number of lines processed
     * @throws java.io.FileNotFoundException if file not found
     * @throws java.io.IOException if a read error occurs
     * @throws net.sf.csv4j.ProcessingException if an exception is thrown by the processor
     * @throws net.sf.csv4j.ParseException if a error occurs parsing the CSV line
     */
    public int processStream( final InputStreamReader is, final CSVLineProcessor processor )
            throws IOException, ProcessingException, ParseException
    {
        final CSVReader reader = new CSVReader( new BufferedReader( is ),
                                                this.comment );

        try
        {
            int lineCnt = 0;

            while ( processor.continueProcessing() )
            {
                final List<String> fields = reader.readLine();
                if ( fields.size() == 0 )
                {
                    break; // while
                }

                try
                {
                    if ( hasHeader && lineCnt == 0 )
                        processor.processHeaderLine( reader.getLineNumber(), fields );
                    else
                        processor.processDataLine( reader.getLineNumber(), fields );
                }
                catch ( Exception e )
                {
                    throw new ProcessingException( e, reader.getLineNumber() );
                }

                lineCnt++;
            }

            return lineCnt; // reader.getLineNumber();
        }
        finally
        {
            reader.close();
        }
    }


    /**
     * Processes the CSV file using the provided processor.
     * Notes:
     * <ol>
     * <li>CSV file must have a header line (hasHeader must be true)</li>
     * <li>if a data line has fewer columns than the header line, the missing
     * columns will be blanks
     * </ol>
     * @param is stream to read CSV from
     * @param processor handler to process the CSV lines
     * @return number of lines processed
     * @throws java.io.FileNotFoundException if file not found
     * @throws java.io.IOException if a read error occurs
     * @throws net.sf.csv4j.ProcessingException if an exception is thrown by the processor
     * @throws net.sf.csv4j.ParseException if a error occurs parsing the CSV line
     */
    public int processStream( final InputStreamReader is, final CSVFieldMapProcessor processor )
            throws IOException, ProcessingException, ParseException
    {
        this.hasHeader = true; // must have a header
        return processStream( is, new LineToFieldMapAdapter( processor ) );
    }

    /**
     * Processes the CSV file using the provided processor.
     * Notes:
     * <ol>
     * <li>CSV file must have a header line (hasHeader must be true)</li>
     * <li>if a data line has fewer columns than the header line, the missing
     * columns will be blanks
     * </ol>
     * @param is stream to read CSV from
     * @param processor handler to process the CSV lines
     * @return number of lines processed
     * @throws java.io.FileNotFoundException if file not found
     * @throws java.io.IOException if a read error occurs
     * @throws net.sf.csv4j.ProcessingException if an exception is thrown by the processor
     * @throws net.sf.csv4j.ParseException if a error occurs parsing the CSV line
     */
    public int processStream( final InputStreamReader is, final CSVSortedFieldMapProcessor processor )
            throws IOException, ProcessingException, ParseException
    {
        this.hasHeader = true; // must have a header
        return processStream( is, new LineToSortedFieldMapAdapter( processor ) );
    }

}
