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

import java.util.List;


/**
 * Interface for processing CSV lines for the optional header
 * and the data lines. 
 * @author Shawn Boyce
 * @since 1.0
 */
public interface CSVLineProcessor
{
    /**
     * Process the CSV header line.
     * Guaranteed to be called once before any calls to processDataLine.
     * @param linenumber line number in the file
     * @param fieldNames CSV field names (name Strings are never null)
     */
    void processHeaderLine( int linenumber, List<String> fieldNames );

    /**
     * Process a CSV data line.
     * @param linenumber line number in the file
     * @param fieldValues CSV field values (value Strings are never null)
     */
    void processDataLine( int linenumber, List<String> fieldValues );


    /**
     * Indicates if the line processing should continue.
     * @return true if continue to process lines; false if processing should stop.
     */
    boolean continueProcessing();
}
