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

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * An adapter from {@link CSVLineProcessor} to {@link CSVFieldMapProcessor}.
 * @author Shawn Boyce
 * @since 1.0
 */
class LineToSortedFieldMapAdapter extends AbstractLineToFieldMapAdapter implements CSVLineProcessor
{
    private final CSVSortedFieldMapProcessor processor;
    private Comparator<String> comparator;


    /**
     * Constructor.
     * @param processor processor to use
     */
    public LineToSortedFieldMapAdapter( final CSVSortedFieldMapProcessor processor )
    {
        this.processor = processor;
    }


    /** {@inheritDoc} */
    @Override
    public void processHeaderLine( final int linenumber, final List<String> fieldNames )
    {
        super.processHeaderLine( linenumber, fieldNames );
        this.comparator = new LineToSortedFieldMapAdapter.FieldOrderComparator( this.header );
    }


    /** {@inheritDoc} */
    public boolean continueProcessing()
    {
        return processor.continueProcessing();
    }

    protected Map<String, String> createMap()
    {
        return new TreeMap<String,String>( this.comparator );
    }


    protected void processDataLine( final int lineNumber, final Map<String, String> fields )
    {
        this.processor.processDataLine( lineNumber, (SortedMap<String,String>) fields );
    }


    /**
     * Compares two field names based on a given positional ordering.
     */
    static class FieldOrderComparator implements Comparator<String>
    {
        private final Map<String,Integer> fieldMap;

        public FieldOrderComparator( final List<String> fields )
        {
            this.fieldMap = new HashMap<String,Integer>( fields.size() );
            int pos = 0;
            for ( String field : fields )
            {
                this.fieldMap.put( field, pos );
                pos++;
            }
        }


        public int compare( String fieldName1, String fieldName2 )
        {
            if ( fieldName1.equals( fieldName2 ) )
                return 0;

            final Integer pos1 = this.fieldMap.get( fieldName1 );
            final Integer pos2 = this.fieldMap.get( fieldName2 );

            // handle unknown fields
            if ( pos1 == pos2 )
                return 0;
            else if ( pos1 == null )
                return +1;  // unknown field goes last
            else if ( pos2 == null )
                return -1;  // unknown field goes last

            if ( pos1 < pos2 )
                return -1;
            else if ( pos1 > pos2 )
                return +1;
            else
                return 0;
        }
    }
}
