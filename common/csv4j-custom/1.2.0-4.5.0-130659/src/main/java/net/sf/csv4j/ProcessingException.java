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

/**
 * This exception indicates an error occurred during processing of a CSV file.
 * @author Shawn Boyce
 * @since 1.0
 */
public class ProcessingException extends Exception
{
    /**
     * The line number that the processing error occurred on.
     * @serial
     */
    private final int linenumber;


    /**
     * Constructor.
     * @param cause root cause of the exception
     * @param linenumber line number of file where exception occurred
     */
    public ProcessingException( final Throwable cause, final int linenumber )
    {
        super( cause );
        this.linenumber = linenumber;
    }


    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "line #" + linenumber + ": " + super.toString();
    }
}
