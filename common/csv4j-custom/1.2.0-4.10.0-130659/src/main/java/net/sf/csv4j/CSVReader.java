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

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.List;


/**
 * Reads and parses CSV lines from an input source.
 * Correctly handles quoted fields which contain line terminators.
 * The CSVReader is designed to be similar to {@link Reader} classes and in particular
 * is similar to the {@link LineNumberReader} in terms of its API.
 * @author Shawn Boyce
 * @since 1.0
 */
public class CSVReader
{
    enum States
    {
        Appending,         // appending characters

        InsideDoubleQuote, // inside a quoted string
        EndingDoubleQuote, // last character was a double quote

        CarriageReturn,    // last character was a carriage return

        InsideComment,     // inside comment line

        EOF,               // at EOF
    }

    private final Reader reader;
    private final CSVParser parser;
    private final String comment;
    private final char commentChar;
    private int lineNumber = 0;
    private String currentLine;
    private final char buf[] = new char[100];
    private int bufLen = 0;
    private int bufPos = 0;
    private States state = States.Appending;


    /**
     * Constructs a CSV reader with the default options.
     * @param reader input to read from.
     */
    public CSVReader( final Reader reader )
    {
        this( reader, CSVConstants.DEFAULT_COMMENT );
    }

    /**
     * Constructs a CSV reader with the specified options.
     * @param reader input to read from. A {@link java.io.BufferedReader} is recommended for better performance.
     * Note that a {@link java.io.LineNumberReader} should not be used since it will not
     * be able to track the line numbers correctly (newlines can be escaped in a CSV file).
     * @param comment character indicating line is a comment and should be ignored
     */
    public CSVReader( final Reader reader, final char comment )
    {
        this( reader, CSVConstants.DEFAULT_DELIMITER, comment );
    }

    /**
     * Constructs a CSV reader with the specified options.
     * @param reader input to read from. A {@link java.io.BufferedReader} is recommended for better performance.
     * Note that a {@link java.io.LineNumberReader} should not be used since it will not
     * be able to track the line numbers correctly (newlines can be escaped in a CSV file).
     * @param comment character indicating line is a comment and should be ignored
     * @param delimiter field delimiter character
     */
    public CSVReader( final Reader reader, final char delimiter, final char comment )
    {
        this.reader = reader;
        this.parser = new CSVParser( delimiter, true );
        this.comment = comment + "";
        this.commentChar = comment;
    }


    /**
     * Returns the current line number.
     * Number only changes after readLine() is invoked. 
     * @return current line number
     */
    public int getLineNumber()
    {
        return this.lineNumber;
    }
    

    /**
	 * @return the currentLine
	 */
	public String getCurrentLine() {
		return currentLine;
	}

	/**
     * Reads and parses the next CSV line from the input.
     * Comment lines will be ignored.
     * @return <code>null</code> if EOF reached
     * @throws IOException if an error occurs reading the input
     * @throws ParseException if an error occurs during CSV parsing
     */
    public List<String> readLine() throws IOException, ParseException
    {
        return readLine(false);
    }
    
	/**
     * Reads and parses the next CSV line from the input.
     * Comment lines will be ignored.
     * @param includeComment <code>true</code> to include lines starting with comment char
     * @return <code>null</code> if EOF reached
     * @throws IOException if an error occurs reading the input
     * @throws ParseException if an error occurs during CSV parsing
     */
    public List<String> readLine(boolean includeComment) throws IOException, ParseException
    {
        while ( true )
        {
        	currentLine = readCSVLine();
            if (currentLine == null )
                return null;
            	
           if (currentLine.startsWith( this.comment ) || currentLine.trim().startsWith( this.comment )){
        	   if (includeComment) {
                   String line = currentLine.substring(this.comment.length());
                   return this.parser.tokenize(line);
        	   } else continue;
           }

            // return type mismatch - List vs String array
            return this.parser.tokenize(currentLine);
        }
    }
    
    
    
    /**
     * Reads and parses the next CSV line from the input.
     * Comment lines will be ignored.
     * @return the number of fields, -1 if EOF reached
     * @throws IOException if an error occurs reading the input
     * @throws ParseException if an error occurs during CSV parsing
     */
    public long countFields() throws IOException, ParseException
    {
        while ( true )
        {
        	currentLine = readCSVLine();
            if ( currentLine == null )
                return -1;

            if ( currentLine.startsWith( this.comment ) || currentLine.trim().startsWith( this.comment ) )
            {
                continue; // while -- ignore comment lines until read a data line
            }

            // return type mismatch - List vs String array
            return this.parser.countFields(currentLine);
        }
    }
    
    /**
     * Reads and parses the next CSV line from the input.
     * Comment lines will be ignored.
     * @return json line
     * @throws IOException if an error occurs reading the input
     * @throws ParseException if an error occurs during CSV parsing
     */
    public String readJSonLine() throws IOException, ParseException
    {
        while ( true )
        {
        	currentLine = readCSVLine();
            if (currentLine == null )
                return null;

            if (currentLine.startsWith( this.comment ) || currentLine.trim().startsWith( this.comment ) )
            {
                continue; // while -- ignore comment lines until read a data line
            }

            // return type mismatch - List vs String array
            return this.parser.toJSon(currentLine, getLineNumber());
        }
    }
   
    /**
     * Closes the input
     * @throws IOException if an I/O error occurs
     */
    public void close() throws IOException
    {
        this.reader.close();
    }

    /**
     * Read a line of CSV text.
     * A line is terminated by newline ('\n'), carriage return ('\r'),
     * or both ('\r\n')
     * <b>unless</b> they are enclosed within double quotes.
     * Line is also terminated by the EOF.
     * @return CSV line without line terminator characters; null returned if EOF reached
     * @throws IOException if an IO error occurs
     */
    public String readCSVLine() throws IOException
    {
        if ( state == States.EOF )
            return null;
        
        final StringBuilder line = new StringBuilder();

        while ( true )
        {
            if ( this.bufPos >= this.bufLen ) // fill the buffer
            {
                this.bufPos = 0;
                this.bufLen = this.reader.read( buf );
                if ( this.bufLen < 0 )
                {
                    state = States.EOF;
                    if ( line.length() != 0 )
                    {
                        lineNumber++;
                        return line.toString(); // return what's in the buffer. Next call will return null
                    }
                    else
                    {
                        return null; // eof
                    }
                }
            }

            for ( ; bufPos < this.bufLen; bufPos++ )
            {
                final char ch = this.buf[bufPos];

                switch ( state )
                {
                case Appending:
                    switch ( ch )
                    {
                    case CSVConstants.CARRIAGE_RETURN:
                        state = States.CarriageReturn;
                        break;

                    case CSVConstants.NEWLINE:
                        lineNumber++;
                        bufPos++;
                        state = States.Appending;
                        return line.toString();

                    case CSVConstants.DOUBLE_QUOTE:
                        state = States.InsideDoubleQuote;
                        line.append( ch );
                        break;

                    default:
                        if ( ch == this.commentChar )
                            state = States.InsideComment;

                        line.append( ch );
                        break;
                    }
                    break;

                case InsideDoubleQuote:
                    if ( ch == CSVConstants.DOUBLE_QUOTE )
                    {
                        state = States.EndingDoubleQuote; // possible ending double quote or escaped one; next char will tell
                    }

                    line.append( ch );
                    break;

                case EndingDoubleQuote:
                    if ( ch == CSVConstants.DOUBLE_QUOTE ) // a quoted double quote is two consecutive double quotes
                    {
                        state = States.InsideDoubleQuote;
                        line.append( ch );
                    }
                    else
                    {
                        state = States.Appending; // we exited the quoted string
                        bufPos--; // process this character again
                    }
                    break;

                case CarriageReturn:
                    if ( ch == CSVConstants.NEWLINE )
                    {
                        bufPos++; // munch the CR LF
                    }
                    // else process this character in the next readLine() call

                    lineNumber++;
                    state = States.Appending;
                    return line.toString();

                case InsideComment:
                    switch ( ch )
                    {
                    case CSVConstants.CARRIAGE_RETURN:
                    case CSVConstants.NEWLINE:
                        bufPos--; // stay where we are and just change state
                        state = States.Appending;
                        break;

                    default:
                        line.append( ch );
                        break;
                    }
                    break;
                
                case EOF:
                    return null;
                }
            }
        }
    }
}
