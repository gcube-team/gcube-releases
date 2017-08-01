/*
 * Copyright 2008 Shawn Boyce.
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

import java.util.List;
import java.util.Map;


/**
 * Abstract base class to adapt from {@link CSVLineProcessor} to {@link CSVFieldMapProcessor} or {@link CSVSortedFieldMapProcessor}.
 * @author Shawn Boyce
 * @since 1.0
 */
abstract class AbstractLineToFieldMapAdapter implements CSVLineProcessor
{
    protected List<String> header;


    /**
     * Constructor.
     */
    public AbstractLineToFieldMapAdapter()
    {
    }


    /** {@inheritDoc} */
    public void processHeaderLine( final int linenumber, final List<String> fieldNames )
    {
        if ( this.header != null )
            throw new ParseException( "second header line at line #" + linenumber );

        this.header = fieldNames;
    }


    /** {@inheritDoc} */
    public void processDataLine( final int linenumber, final List<String> fieldValues )
    {
        if ( this.header == null )
            throw new ParseException( "missing header line" );

        final Map<String,String> fields = createMap();

        if ( this.header.size() > fieldValues.size() )
        {
            // this data line has too few values, so we need to pad it
            padList( fieldValues, this.header.size(), "" );
        }
        else if ( this.header.size() < fieldValues.size() )
        {
            throw new ParseException( "Line #" + linenumber +
                    ": # of field values (" + this.header.size() + ") > " +
                    "header size (" + fieldValues.size() + ")" );
        }

        for ( int ii = 0; ii < this.header.size(); ii++ )
        {
            fields.put( this.header.get( ii ), fieldValues.get( ii ) );
        }

        processDataLine( linenumber, fields );
    }


    protected abstract Map<String, String> createMap();
    protected abstract void processDataLine( int lineNumber, Map<String,String> fields );

    /** {@inheritDoc} */
    public abstract boolean continueProcessing();


    /**
     * Pad the List so its length is of a minimum size.
     * @param list list to pad
     * @param desiredLength minimum desired length of the list
     * @param fill string to use for padding
     */
    private static void padList( final List<String> list, final int desiredLength, final String fill )
    {
        final int numToPad = desiredLength - list.size();
        for ( int ii = 0; ii < numToPad; ii++ )
        {
            list.add( fill );
        }
    }
}
