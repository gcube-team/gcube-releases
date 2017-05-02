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

import java.util.Map;


/**
 * Similar interface to the {@link net.sf.csv4j.CSVLineProcessor} interface
 * for processing CSV lines
 * except that the lines are processed as a {@link java.util.Map} of field name and values.
 * @author Shawn Boyce
 * @since 1.0
 */
public interface CSVFieldMapProcessor
{
    /**
     * Process a CSV data line.
     * @param linenumber line number in the file
     * @param fields map of CSV fields names/values
     */
    void processDataLine( int linenumber, Map<String,String> fields );

    /**
     * Indicates if the line processing should continue.
     * @return true if continue to process lines; false if processing should stop.
     */
    boolean continueProcessing();
}
