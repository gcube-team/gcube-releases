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

import java.util.HashMap;
import java.util.Map;


/**
 * An adapter from {@link net.sf.csv4j.CSVLineProcessor} to {@link net.sf.csv4j.CSVFieldMapProcessor}.
 * @author Shawn Boyce
 * @since 1.0
 */
class LineToFieldMapAdapter extends AbstractLineToFieldMapAdapter implements CSVLineProcessor
{
    private final CSVFieldMapProcessor processor;


    /**
     * Constructor.
     * @param processor processor to use
     */
    public LineToFieldMapAdapter( final CSVFieldMapProcessor processor )
    {
        this.processor = processor;
    }


    protected Map<String, String> createMap()
    {
        return new HashMap<String,String>();
    }


    protected void processDataLine( final int lineNumber, final Map<String, String> fields )
    {
        this.processor.processDataLine( lineNumber, fields );
    }


    /** {@inheritDoc} */
    public boolean continueProcessing()
    {
        return processor.continueProcessing();
    }
}
