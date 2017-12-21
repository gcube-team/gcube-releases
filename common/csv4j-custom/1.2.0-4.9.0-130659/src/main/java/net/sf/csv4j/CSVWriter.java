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

import java.io.Closeable;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;


/**
 * Writes output lines in CSV format.
 * @author Shawn Boyce
 * @since 1.0
 */
public class CSVWriter implements Closeable
{
    private final Writer writer;
    private final char commentChar;
    private final char delimiter;
    private final String lineSeparator;


    /**
     * Constructor.
     * Uses default comment and delimiter characters.
     * @param writer writer to output to
     */
    public CSVWriter( Writer writer )
    {
        this( writer, CSVConstants.DEFAULT_DELIMITER, CSVConstants.DEFAULT_COMMENT );
    }


    /**
     * Constructor.
     * @param writer writer to output to
     * @param delimiter field delimiter character to use
     */
    public CSVWriter( Writer writer, char delimiter )
    {
        this( writer, delimiter, CSVConstants.DEFAULT_COMMENT );
    }

    /**
     * Constructor.
     * @param writer writer to output to
     * @param delimiter field delimiter character to use
     * @param comment comment character to use
     */
    public CSVWriter( Writer writer, char delimiter, char comment )
    {
        this.writer = writer;
        this.delimiter = delimiter;
        this.commentChar = comment;
        this.lineSeparator = System.getProperty( "line.separator" );
    }
    /**
     * Output a CSV comment line.
     * Comment lines start with the comment character.
     * <p>
     * Example:
     * <p>
     * <code>
     * writer.writeCommentLine( "this is a comment" );
     * </code>
     * <p>
     * results in
     * <pre>
     * # this is a comment line
     * </pre>
     * @param comment comment text to output. Embedded newline/carriage returns are handled correctly.
     * @throws IOException if an error occurs when writing
     */
    public void writeCommentLine( final String comment ) throws IOException
    {
        // newlines output without modification in the comment will result in an invalid CSV line
        // ensure that all comment lines are commented
        final String[] lines = comment.split( "[\r\n]" );
        for ( String line : lines )
        {
            writer.write( this.commentChar );
            writer.write( ' ' );
            writer.write( line );
            writer.write( this.lineSeparator );
        }
    }

    // todo better way to support writing of header line vs data lines (padding of commas ?)
    // todo when outputting maps -- could  avoid header and data line methods by keeping track of whether header line output 

    /**
     * Output the CSV header line which contains the names of the fields.
     * This method should only be called once per file.
     * @param fields fields to output
     * @throws IOException if an error occurs when writing
     */
    public void writeHeaderLine( final SortedMap<String,String> fields ) throws IOException
    {
        writeLine( fields.keySet() );
    }

    /**
     * Output the CSV data line which contains the field values
     * @param fields fields to output
     * @throws IOException if an error occurs when writing
     */
    public void writeDataLine( final SortedMap<String,String> fields ) throws IOException
    {
        writeLine( fields.values() );
    }


    /**
     * Output a line of CSV fields. This can be field names or values.
     * Each field is separated by the field delimiter.
     * @param values strings to output
     * @throws IOException if an error occurs while writing
     */
    public void writeLine( final List<String> values  ) throws IOException
    {
        writeLine( (Collection<String>) values );
    }

    /**
     * Output a line of CSV fields. This can be field names or values.
     * Each field is separated by the field delimiter.
     * @param values strings to output
     * @throws IOException if an error occurs while writing
     */
    public void writeLine( final String[] values ) throws IOException
    {
        for ( int ii = 0; ii < values.length; ii++ )
        {
            escapeCSV( writer, values[ii], this.delimiter );
            if ( (ii+1) < values.length )
                writer.append( this.delimiter );
        }
        writer.write( this.lineSeparator );
    }


    /**
     * Outputs a list of strings.
     * @param values strings to output
     * @throws IOException if an I/O error occurs
     */
    private void writeLine( final Collection<String> values ) throws IOException
    {
        int ii = 0;
        for ( String value : values )
        {
            escapeCSV( writer, value, this.delimiter );
            if ( ++ii < values.size() )
                writer.append( this.delimiter );
        }
        writer.write( this.lineSeparator );
    }

    /**
     * Escapes a text string for CSV output.
     * @param text text string to escape
     * @return appropriately escaped CSV text string
     */
    public static String escapeCSV( final String text )
    {
        return escapeCSV( text, CSVConstants.DEFAULT_DELIMITER );
    }

    /**
     * Escapes a text string for CSV output.
     * @param text text string to escape
     * @param delimiter field delimiter
     * @return appropriately escaped CSV text string 
     */
    public static String escapeCSV( final String text, final char delimiter )
    {
        try
        {
            final StringWriter writer = new StringWriter();
            escapeCSV( writer, text, delimiter );
            return writer.toString();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "escapeCSV error", e ); // should not happen with a StringWriter
        }
    }

    /**
     * Escapes a text string for CSV output.
     * @param text text string to escape
     * @param writer writer to send the CSV output to
     * @param delimiter field delimiter to use
     * @throws IOException if an IO error occurs
     */
    public static void escapeCSV( final Writer writer, final String text, char delimiter ) throws IOException
    {
        final StringBuilder sbuf = new StringBuilder();
        boolean isBuffering = true;
        
        // scan for special characters; if none, just output value
        for ( int ii = 0; ii < text.length(); ii++ )
        {
            final char ch = text.charAt( ii );
            switch ( ch )
            {
            case CSVConstants.DOUBLE_QUOTE:
                if ( isBuffering )
                {
                    isBuffering = false;
                    writer.write( CSVConstants.DOUBLE_QUOTE );
                    if ( sbuf.length() != 0 )
                        writer.write( sbuf.toString() );
                }

                // double quote is escaped with a second double quote
                writer.write( CSVConstants.DOUBLE_QUOTE );
                writer.write( CSVConstants.DOUBLE_QUOTE );
                break;

            // line feeds need to be escaped
            case CSVConstants.CARRIAGE_RETURN:
            case CSVConstants.NEWLINE:
                if ( isBuffering )
                {
                    isBuffering = false;
                    writer.write( CSVConstants.DOUBLE_QUOTE );
                    if ( sbuf.length() != 0 )
                        writer.write( sbuf.toString() );
                }

                writer.write( ch );
                break;

            default:
                if ( ch == delimiter )
                {
                    if ( isBuffering )
                    {
                        isBuffering = false;
                        writer.write( CSVConstants.DOUBLE_QUOTE );
                        if ( sbuf.length() != 0 )
                            writer.write( sbuf.toString() );
                    }

                    writer.write( ch );
                }
                else if ( isBuffering )
                {
                    sbuf.append( ch );
                }
                else
                {
                    writer.write( ch );
                }
                break;
            }
        }

        if ( isBuffering ) // no special chars encountered
        {
            writer.write( text ); // no need for sbuf contents
        }
        else // output the ending double quote
        {
            writer.write( CSVConstants.DOUBLE_QUOTE );
        }
    }


	@Override
	public void close() throws IOException {
		writer.close();		
	}

}
