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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;


/**
 * Provides an API similar to {@link java.util.StringTokenizer}.
 * In addition to StringTokenizer's two interfaces, this class also supports the
 * Iterator interface. 
 * It supports too many style of iterations as a result:
 * <ul>
 * <li>Tokenizer style</li>
 * <li>Iterator style</li>
 * <li>Enumerator style</li>
 * </ul>
 * @author Shawn Boyce
 * @since 1.0
 * @see <a href="http//en.wikipedia.org/wiki/Comma-separated_values">Comma Separated Values on Wikipedia</a>
 * @see java.util.StringTokenizer
 */
public class CSVTokenizer
        implements Tokenizer<String>, Enumeration<String>, Iterator<String>, Iterable<String>
{
    /**
     * List of tokens
     */
    private final List<String> tokens;

    /**
     * Current iteration index into tokens list.
     */
    private int iterIndex = 0;


    /**
     * Creates a tokenizer from the specified CSV line.
     * @param line string to tokenize
     * @throws ParseException if the line cannot be tokenized
     */
    public CSVTokenizer( final String line ) throws ParseException
    {
        this.tokens = new CSVParser().tokenize( line ); 
    }

    /** {@inheritDoc} */
    public String nextToken()
    {
        if ( this.iterIndex >= this.tokens.size() )
            throw new NoSuchElementException();
        
        return this.tokens.get( this.iterIndex++ );
    }

    /** {@inheritDoc} */
    public boolean hasMoreTokens()
    {
        return this.iterIndex < this.tokens.size();
    }

    /** {@inheritDoc} */
    public int countTokens()
    {
        return this.tokens.size() - this.iterIndex;
    }

    /** {@inheritDoc} */
    public boolean hasMoreElements()
    {
        return hasMoreTokens();
    }

    /** {@inheritDoc} */
    public String nextElement()
    {
        return nextToken();
    }

    /** {@inheritDoc} */
    public boolean hasNext()
    {
        return hasMoreTokens();
    }

    /** {@inheritDoc} */
    public String next()
    {
        return nextToken();
    }


    /**
     * Not supported.
     * @throws RuntimeException always thrown
     */
    public void remove()
    {
        throw new RuntimeException( "not supported" );
    }


    /**
     * Reset the iterator back to the beginning.
     */
    public void reset()
    {
        this.iterIndex = 0;
    }

    /** {@inheritDoc} */
    public Iterator<String> iterator()
    {
        return this;
    }
}
